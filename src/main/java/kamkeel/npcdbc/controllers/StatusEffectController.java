package kamkeel.npcdbc.controllers;

import kamkeel.npcdbc.api.effect.ICustomEffect;
import kamkeel.npcdbc.api.effect.IStatusEffect;
import kamkeel.npcdbc.api.effect.IStatusEffectHandler;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.DBCSyncType;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.statuseffect.custom.CustomEffect;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.statuseffect.*;
import kamkeel.npcdbc.data.statuseffect.PlayerEffect;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import kamkeel.npcdbc.data.statuseffect.custom.EffectScriptHandler;
import kamkeel.npcdbc.data.statuseffect.types.*;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.get.DBCInfoSyncPacket;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcdbc.util.Utility;
import kamkeel.npcs.network.enums.EnumSyncAction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.CustomNpcs;
import noppes.npcs.LogWriter;
import noppes.npcs.api.entity.IPlayer;

import noppes.npcs.util.NBTJsonUtil;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;

public class StatusEffectController implements IStatusEffectHandler {

    public static StatusEffectController Instance = new StatusEffectController();

    public HashMap<Integer, StatusEffect> standardEffects = new HashMap<>();
    // TODO: I will implement later - Kam
    //      ...
    //      Fucking Liar - Hussar (respectfully)
    public HashMap<Integer, CustomEffect> customEffectsSync = new HashMap<>();
    public HashMap<Integer, CustomEffect> customEffects = new HashMap<>();
    public HashMap<Integer, EffectScriptHandler> customEffectScriptHandlers = new HashMap<>();
    private HashMap<Integer, String> bootOrder;
    private int lastUsedID = Effects.CUSTOM_EFFECT;

    public ConcurrentHashMap<UUID, Map<Integer, PlayerEffect>> playerEffects = new ConcurrentHashMap<>();

    // Maps for Status Effects
    private final ConcurrentHashMap<UUID, DamageTracker> damageTrackers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, SenzuConsumptionData> playerSenzuConsumption = new ConcurrentHashMap<>();
    public StatusEffectController() {

    }

    public static StatusEffectController getInstance() {
        return Instance;
    }

    public void load() {
        playerEffects.clear();

        // Global Registration for Effects
        standardEffects.put(Effects.REGEN_HEALTH, new RegenHealth());
        standardEffects.put(Effects.REGEN_KI, new RegenKi());
        standardEffects.put(Effects.REGEN_STAMINA, new RegenStamina());
        standardEffects.put(Effects.NAMEK_REGEN, new NamekRegen());
        standardEffects.put(Effects.FRUIT_OF_MIGHT, new FruitOfMight());
        standardEffects.put(Effects.BLOATED, new Bloated());        // TODO: Finish it
        standardEffects.put(Effects.MEDITATION, new Meditation());
        standardEffects.put(Effects.OVERPOWER, new Overpower());
        standardEffects.put(Effects.CHOCOLATED, new Chocolated());  // TODO: Finish it
        standardEffects.put(Effects.DARKNESS, new Darkness());      // TODO: Finish it
        standardEffects.put(Effects.ZENKAI, new Zenkai());
        standardEffects.put(Effects.POTARA, new PotaraFusion());
        standardEffects.put(Effects.EXHAUSTED, new Exhausted());

        standardEffects.put(Effects.HUMAN_SPIRIT, new HumanSpirit());
        standardEffects.put(Effects.COLD_BLOODED, new Exhausted()); // TODO: Finish it
        standardEffects.put(Effects.KI_DEFENSE, new Exhausted()); // TODO: Finish it

        bootOrder = new HashMap<>();
        LogWriter.info("Loading custom effects...");
        readCustomEffectMap();
        loadCustomEffects();
        LogWriter.info("Done loading custom effects.");
    }


    public void runEffects(EntityPlayer player) {
        Map<Integer, PlayerEffect> current = getPlayerEffects(player);
        for (int active : current.keySet()) {
            StatusEffect effect = get(active);
            if (effect != null) {
                effect.runEffect(player, current.get(active));
            }
        }
    }

    public void killEffects(EntityPlayer player) {
        Map<Integer, PlayerEffect> current = getPlayerEffects(player);
        Iterator<Map.Entry<Integer, PlayerEffect>> iterator = current.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, PlayerEffect> entry = iterator.next();
            StatusEffect effect = get(entry.getKey());
            if (effect != null) {
                if (effect.lossOnDeath) {
                    effect.onRemoved(player, entry.getValue());
                    iterator.remove();
                }
            } else {
                iterator.remove(); // Use iterator to remove the current element
            }
        }
    }

    public boolean has(String name) {
        return get(name) != null;
    }

    public boolean has(int id) {
        return get(id) != null;
    }

    @Override
    public IStatusEffect createEffect(String name) {
        if (has(name))
            return get(name);

        CustomEffect effect = new CustomEffect();
        effect.name = name;

        if (effect.id == -1) {
            int  id = getUnusedId();
            while (customEffects.containsKey(id)) {
                id = getUnusedId();
            }
            effect.id = id;
        }
        customEffects.put(effect.id, effect);
        return effect;
    }

    @Override
    public IStatusEffect getEffect(String name) {
        return get(name);
    }

    @Override
    public void deleteEffect(String name) {
        IStatusEffect effect = getEffect(name);
        if (effect != null && effect.isCustom()) {
            CustomEffect foundEffect = customEffects.remove(effect.getID());
            customEffectScriptHandlers.remove(effect.getID());
            if (foundEffect != null && foundEffect.name != null) {
                File dir = this.getDir();
                for (File file : dir.listFiles()) {
                    if (!file.isFile() || !file.getName().endsWith(".json"))
                        continue;
                    if (file.getName().equals(foundEffect.name + ".json")) {
                        file.delete();
                        DBCPacketHandler.Instance.sendToAll(new DBCInfoSyncPacket(DBCSyncType.CUSTOM_EFFECT, EnumSyncAction.REMOVE, foundEffect.getID(), new NBTTagCompound()));
                        break;
                    }
                }
                saveEffectLoadMap();
            }
        }
    }

    public void delete(int id) {
        IStatusEffect effect = get(id);
        if (effect != null && effect.isCustom()) {
            CustomEffect foundEffect = customEffects.remove(effect.getID());
            customEffectScriptHandlers.remove(effect.getID());
            if (foundEffect != null && foundEffect.name != null) {
                File dir = this.getDir();
                for (File file : dir.listFiles()) {
                    if (!file.isFile() || !file.getName().endsWith(".json"))
                        continue;
                    if (file.getName().equals(foundEffect.name + ".json")) {
                        file.delete();
                        DBCPacketHandler.Instance.sendToAll(new DBCInfoSyncPacket(DBCSyncType.CUSTOM_EFFECT, EnumSyncAction.REMOVE, foundEffect.getID(), new NBTTagCompound()));
                        break;
                    }
                }
                saveEffectLoadMap();
            }
        }
    }


    public int getUnusedId() {
        for (int catid : customEffects.keySet()) {
            if (catid > lastUsedID)
                lastUsedID = catid;
        }
        lastUsedID++;
        return lastUsedID;
    }

    public StatusEffect get(String name) {
        for (StatusEffect effect : standardEffects.values()) {
            if (effect.getName().equalsIgnoreCase(name)) {
                return effect;
            }
        }

        for (CustomEffect effect : customEffects.values()) {
            if (effect.getName().equalsIgnoreCase(name)) {
                return effect;
            }
        }

        return null;
    }

    public StatusEffect get(int id) {
        StatusEffect statusEffect;
        if (id < 200)
            statusEffect = standardEffects.get(id);
        else
            statusEffect = customEffects.get(id);

        return statusEffect;
    }

    public Map<Integer, PlayerEffect> getPlayerEffects(EntityPlayer player) {
        UUID playerId = Utility.getUUID(player);
        if (!playerEffects.containsKey(playerId))
            playerEffects.put(playerId, new ConcurrentHashMap<>());
        return playerEffects.get(Utility.getUUID(player));
    }

    public void applyEffect(EntityPlayer player, int id) {
        StatusEffect parent = get(id);
        if (parent != null) {
            PlayerEffect playerEffect = new PlayerEffect(id, parent.length, (byte) 1);
            applyEffect(player, playerEffect);
        }
    }

    public void applyEffect(EntityPlayer player, int id, int duration) {
        StatusEffect parent = get(id);
        if (parent != null) {
            PlayerEffect playerEffect = new PlayerEffect(id, duration, (byte) 1);
            applyEffect(player, playerEffect);
        }
    }

    public void applyEffect(EntityPlayer player, PlayerEffect effect) {
        if (effect == null)
            return;

        StatusEffect parent = get(effect.id);
        if (parent != null) {
            Map<Integer, PlayerEffect> currentEffects = new ConcurrentHashMap<>();
            UUID uuid = Utility.getUUID(player);
            if (playerEffects.containsKey(uuid))
                currentEffects = playerEffects.get(Utility.getUUID(player));
            else
                playerEffects.put(uuid, currentEffects);

            currentEffects.put(effect.id, effect);
            parent.onAdded(player, effect);
        }
    }

    public void removeEffect(EntityPlayer player, PlayerEffect effect) {
        if (effect == null)
            return;

        Map<Integer, PlayerEffect> currentEffects = new ConcurrentHashMap<>();
        UUID uuid = Utility.getUUID(player);
        if (playerEffects.containsKey(uuid))
            currentEffects = playerEffects.get(Utility.getUUID(player));
        else
            playerEffects.put(uuid, currentEffects);

        if (currentEffects.containsKey(effect.id)) {
            StatusEffect parent = get(effect.id);
            if (parent != null) {
                parent.onRemoved(player, effect);
            }
            currentEffects.remove(effect.id);
        }
    }

    @Override
    public boolean hasEffect(IPlayer player, int id) {
        if (player == null || player.getMCEntity() == null)
            return false;
        return hasEffect((EntityPlayer) player.getMCEntity(), id);
    }

    @Override
    public boolean hasEffect(IPlayer player, IStatusEffect effect) {
        return hasEffect(player, effect.getID());
    }

    @Override
    public int getEffectDuration(IPlayer player, int id) {
        if (player == null || player.getMCEntity() == null)
            return -1;
        return getEffectDuration((EntityPlayer) player.getMCEntity(), id);
    }

    @Override
    public int getEffectDuration(IPlayer player, IStatusEffect effect) {
        return getEffectDuration(player, effect.getID());
    }

    @Override
    public void applyEffect(IPlayer player, int id, int duration, byte level) {
        if (player == null || player.getMCEntity() == null)
            return;
        applyEffect((EntityPlayer) player.getMCEntity(), id, duration, level);
    }

    @Override
    public void applyEffect(IPlayer player, IStatusEffect effect, int duration, byte level) {
        applyEffect(player, effect.getID(), duration, level);
    }

    @Override
    public void removeEffect(IPlayer player, int id) {
        if (player == null || player.getMCEntity() == null)
            return;
        removeEffect((EntityPlayer) player.getMCEntity(), id);
    }

    @Override
    public void removeEffect(IPlayer player, IStatusEffect effect) {
        removeEffect(player, effect.getID());
    }

    @Override
    public void clearEffects(IPlayer player) {
        if (player == null || player.getMCEntity() == null)
            return;
        clearEffects(player.getMCEntity());
    }

    @Override
    public ICustomEffect saveEffect(ICustomEffect customEffect) {
        if (customEffect.getID() < 0) {
            customEffect.setID(getUnusedId());
            while (has(customEffect.getName()))
                customEffect.setName(customEffect.getName() + "_");
        } else {
            CustomEffect existing = customEffects.get(customEffect.getID());
            if (existing != null && !existing.name.equals(customEffect.getName()))
                while (has(customEffect.getName()))
                    customEffect.setName(customEffect.getName() + "_");
        }

        customEffects.remove(customEffect.getID());
        customEffects.put(customEffect.getID(), (CustomEffect) customEffect);

        saveEffectLoadMap();

        File dir = this.getDir();
        if (!dir.exists())
            dir.mkdirs();

        File file = new File(dir, customEffect.getName() + ".json_new");
        File file2 = new File(dir, customEffect.getName() + ".json");

        try {
            NBTTagCompound nbtTagCompound = ((CustomEffect) customEffect).writeToNBT(true);
            NBTJsonUtil.SaveFile(file, nbtTagCompound);
            if (file2.exists())
                file2.delete();
            file.renameTo(file2);
            nbtTagCompound = ((CustomEffect) customEffect).writeToNBT(true);
            DBCPacketHandler.Instance.sendToAll(new DBCInfoSyncPacket(DBCSyncType.CUSTOM_EFFECT, EnumSyncAction.UPDATE, customEffect.getID(), new NBTTagCompound()));
        } catch (Exception e) {
            LogWriter.except(e);
        }
        return customEffects.get(customEffect.getID());
    }

    public void clearEffects(Entity player) {
        Map<Integer, PlayerEffect> effects = playerEffects.get(Utility.getUUID(player));
        if (effects != null) {
            effects.clear();
        }
    }

    public boolean hasEffect(EntityPlayer player, int id) {
        Map<Integer, PlayerEffect> currentEffects;
        UUID uuid = Utility.getUUID(player);
        if (playerEffects.containsKey(uuid))
            currentEffects = playerEffects.get(uuid);
        else
            return false;

        return currentEffects.containsKey(id);
    }

    public int getEffectDuration(EntityPlayer player, int id) {
        Map<Integer, PlayerEffect> currentEffects = new ConcurrentHashMap<>();
        UUID uuid = Utility.getUUID(player);
        if (playerEffects.containsKey(uuid))
            currentEffects = playerEffects.get(uuid);
        else
            playerEffects.put(uuid, currentEffects);

        if (currentEffects.containsKey(id))
            return currentEffects.get(id).duration;

        return -1;
    }

    public void applyEffect(EntityPlayer player, int id, int duration, byte level) {
        if (player == null || id <= 0)
            return;

        Map<Integer, PlayerEffect> currentEffects = new ConcurrentHashMap<>();
        UUID uuid = Utility.getUUID(player);
        if (playerEffects.containsKey(uuid))
            currentEffects = playerEffects.get(Utility.getUUID(player));
        else
            playerEffects.put(uuid, currentEffects);

        StatusEffect parent = get(id);
        if (parent != null) {
            PlayerEffect playerEffect = new PlayerEffect(id, duration, level);
            currentEffects.put(id, playerEffect);
            parent.onAdded(player, playerEffect);
        }
    }

    public void removeEffect(EntityPlayer player, int id) {
        if (player == null || id <= 0)
            return;

        Map<Integer, PlayerEffect> currentEffects = new ConcurrentHashMap<>();
        UUID uuid = Utility.getUUID(player);
        if (playerEffects.containsKey(uuid))
            currentEffects = playerEffects.get(Utility.getUUID(player));
        else
            playerEffects.put(uuid, currentEffects);

        if (currentEffects.containsKey(id)) {
            PlayerEffect current = currentEffects.get(id);
            StatusEffect parent = get(current.id);
            if (parent != null) {
                parent.onRemoved(player, current);
            }

            currentEffects.remove(id);
        }
    }

    public void decrementEffects(EntityPlayer player) {

        Iterator<PlayerEffect> iterator = getPlayerEffects(player).values().iterator();

        IPlayer iPlayer = PlayerDataUtil.getIPlayer(player);
        while (iterator.hasNext()) {
            PlayerEffect effect = iterator.next();

            if (effect == null) {
                iterator.remove();
                continue;
            }

            if (effect.duration == -100)
                continue;

            if (effect.duration <= 0) {
                StatusEffect parent = StatusEffectController.Instance.get(effect.id);
                if (parent != null) {
                    parent.onRemoved(player, effect);
                }
                iterator.remove();
                continue;
            }
            effect.duration--;
        }
    }

    /**
     * Checks if the player is allowed to consume Senzus based on current consumption rate.
     * If the consumption exceeds the rate, apply the "Bloated" effect.
     */
    public boolean allowSenzuConsumption(EntityPlayer player) {
        if (hasEffect(player, Effects.BLOATED)) {
            return false; // Player already has the Bloated effect, cannot consume more.
        }

        if (!ConfigDBCEffects.AUTO_BLOATED) {
            return true; // Automatic bloated status effect is disabled.
        }

        UUID playerId = Utility.getUUID(player);
        SenzuConsumptionData consumptionData = getPlayerSenzuData(player);
        long currentTime = System.currentTimeMillis();

        // Clean old consumption entries based on current time and defined decrease time.
        consumptionData.cleanOldConsumption(currentTime, ConfigDBCEffects.DECREASE_TIME);
        consumptionData.addConsumption(currentTime);

        // Calculate the excess consumption based on the defined threshold.
        int excessConsumption = consumptionData.getExcessConsumption(ConfigDBCEffects.BLOATED_THRESHOLD);

        // Check for the warning threshold.
        if (excessConsumption > 0 && excessConsumption < ConfigDBCEffects.MAX_THRESHOLD_EXCEED) {
            NetworkUtility.sendServerMessage(player, "§c", "npcdbc.full");
        }

        if (excessConsumption >= ConfigDBCEffects.MAX_THRESHOLD_EXCEED) {
            // Player has consumed too many Senzus above the rate, apply the "Bloated" effect.
            applyEffect(player, Effects.BLOATED, ConfigDBCEffects.BLOATED_TIME);
            NetworkUtility.sendServerMessage(player, "§4", "npcdbc.bloated");
            return false; // Deny further consumption.
        }

        playerSenzuConsumption.put(playerId, consumptionData);
        return true; // Allow consumption.
    }


    /**
     * Decreases the consumption count for each player in the map at the specified rate.
     * This should be called every DECREASE_TIME ticks in the game.
     */
    public void decreaseSenzuConsumption(EntityPlayer player) {
        SenzuConsumptionData data = getPlayerSenzuData(player);
        data.decreaseConsumption(ConfigDBCEffects.DECREASE_TIME);
    }
    private SenzuConsumptionData getPlayerSenzuData(EntityPlayer player) {
        UUID playerId = Utility.getUUID(player);
        return playerSenzuConsumption.computeIfAbsent(playerId, k -> new SenzuConsumptionData());
    }

    /**
     * Human Spirit
     */
    public void recordDamage(EntityPlayer player, double damageAmount) {
        UUID playerId = Utility.getUUID(player);
        damageTrackers.computeIfAbsent(playerId, k -> new DamageTracker(playerId)).recordDamage(damageAmount);
    }

    public void checkHumanSpirit(EntityPlayer player) {
        UUID playerId = Utility.getUUID(player);
        if (hasEffect(player, Effects.HUMAN_SPIRIT)) {
            return;
        }

        if (ConfigDBCEffects.EXHAUST_HUMANSPIRIT && hasEffect(player, Effects.EXHAUSTED)) {
            return;
        }

        DamageTracker tracker = damageTrackers.get(playerId);
        DBCData dbcData = DBCData.get(player);
        int maxHealth = dbcData.stats.getMaxBody();
        if (tracker != null && tracker.checkForHumanSpirit(maxHealth)) {
            applyEffect(player, Effects.HUMAN_SPIRIT);
            NetworkUtility.sendServerMessage(player, "§d", "npcdbc.humanSpiritMessage");
            tracker.cleanOldEntries(System.currentTimeMillis());
        }
    }

    public File getMapDir() {
        File dir = CustomNpcs.getWorldSaveDirectory();
        if (!dir.exists())
            dir.mkdir();
        return dir;
    }

    private void loadCustomEffects() {
        customEffects.clear();

        File dir = getDir();
        if (!dir.exists()) {
            dir.mkdir();
        } else {
            for (File file : dir.listFiles()) {
                if (!file.isFile() || !file.getName().endsWith(".json"))
                    continue;
                try {
                    CustomEffect effect = new CustomEffect();
                    effect.readFromNBT(NBTJsonUtil.LoadFile(file));
                    effect.name = file.getName().substring(0, file.getName().length() - 5);

                    if (effect.id == -1) {
                        effect.id = getUnusedId();
                    }

                    int originalID = effect.id;
                    int setID = effect.id;
                    while (bootOrder.containsKey(setID) || customEffects.containsKey(setID)) {
                        if (bootOrder.containsKey(setID))
                            if (bootOrder.get(setID).equals(effect.name))
                                break;

                        setID++;
                    }

                    effect.id = setID;
                    if (originalID != setID) {
                        LogWriter.info("Found Custom Effect ID Mismatch: " + effect.name + ", New ID: " + setID);
                        effect.save();
                    }

                    customEffects.put(effect.id, effect);
                } catch (Exception e) {
                    LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
                }
            }
        }
        saveEffectLoadMap();
    }

    private File getDir() {
        return new File(CustomNpcs.getWorldSaveDirectory(), "customeffects");
    }

    private void saveEffectLoadMap() {
        try {
            File saveDir = getMapDir();
            File file = new File(saveDir, "customeffects.dat_new");
            File file1 = new File(saveDir, "customeffects.dat_old");
            File file2 = new File(saveDir, "customeffects.dat");
            CompressedStreamTools.writeCompressed(this.writeMapNBT(), new FileOutputStream(file));
            if (file1.exists()) {
                file1.delete();
            }
            file2.renameTo(file1);
            if (file2.exists()) {
                file2.delete();
            }
            file.renameTo(file2);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            LogWriter.except(e);
        }
    }

    private NBTTagCompound writeMapNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList customEffects = new NBTTagList();
        for (Integer key : this.customEffects.keySet()) {
            CustomEffect customEffect = this.customEffects.get(key);
            if (!customEffect.getName().isEmpty()) {
                NBTTagCompound effectCompound = new NBTTagCompound();
                effectCompound.setString("Name", customEffect.getName());
                effectCompound.setInteger("ID", key);

                customEffects.appendTag(effectCompound);
            }
        }
        nbt.setTag("CustomEffects", customEffects);
        nbt.setInteger("lastID", lastUsedID);
        return nbt;
    }

    private void readCustomEffectMap() {
        bootOrder.clear();

        try {
            File file = new File(getMapDir(), "customeffects.dat");
            if (file.exists()) {
                loadCustomEffectMap(file);
            }
        } catch (Exception e) {
            try {
                File file = new File(getMapDir(), "customeffects.dat_old");
                if (file.exists()) {
                    loadCustomEffectMap(file);
                }
            } catch (Exception ignored) {
            }
        }
    }

    private void loadCustomEffectMap(File file) throws IOException {
        DataInputStream var1 = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(file))));
        readCustomEffectMap(var1);
        var1.close();
    }

    private void readCustomEffectMap(DataInputStream stream) throws IOException {
        NBTTagCompound nbtCompound = CompressedStreamTools.read(stream);
        this.readMapNBT(nbtCompound);
    }

    private void readMapNBT(NBTTagCompound compound) {
        lastUsedID = compound.getInteger("lastID");
        NBTTagList list = compound.getTagList("CustomEffects", 10);
        if (list != null) {
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
                String effectName = nbttagcompound.getString("Name");
                Integer key = nbttagcompound.getInteger("ID");
                bootOrder.put(key, effectName);
            }
        }
    }

    public void deleteEffectFile(String prevName) {
        File dir = this.getDir();
        if (!dir.exists())
            dir.mkdirs();
        File file2 = new File(dir, prevName + ".json");
        if (file2.exists())
            file2.delete();
    }
}
