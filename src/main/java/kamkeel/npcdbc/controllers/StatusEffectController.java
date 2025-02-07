package kamkeel.npcdbc.controllers;

import kamkeel.npcdbc.api.effect.IStatusEffect;
import kamkeel.npcdbc.api.effect.IStatusEffectHandler;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.statuseffect.*;
import kamkeel.npcdbc.data.statuseffect.CustomEffect;
import kamkeel.npcdbc.data.statuseffect.PlayerEffect;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import kamkeel.npcdbc.data.statuseffect.types.*;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.api.entity.IPlayer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static noppes.npcs.NoppesStringUtils.translate;

public class StatusEffectController implements IStatusEffectHandler {

    public static StatusEffectController Instance = new StatusEffectController();

    public HashMap<Integer, StatusEffect> standardEffects = new HashMap<>();
    // TODO: I will implement later - Kam
    //      ...
    //      Fucking Liar - Hussar (respectfully)
    public HashMap<Integer, CustomEffect> customEffectsSync = new HashMap<>();
    public HashMap<Integer, CustomEffect> customEffects = new HashMap<>();
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
        if (effect != null)
            customEffects.remove(effect.getId());
    }

    public void delete(int id) {
        IStatusEffect effect = get(id);
        if (effect != null)
            customEffects.remove(effect.getId());
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
        StatusEffect statusEffect = standardEffects.get(id);
        if (statusEffect == null)
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
        return hasEffect(player, effect.getId());
    }

    @Override
    public int getEffectDuration(IPlayer player, int id) {
        if (player == null || player.getMCEntity() == null)
            return -1;
        return getEffectDuration((EntityPlayer) player.getMCEntity(), id);
    }

    @Override
    public int getEffectDuration(IPlayer player, IStatusEffect effect) {
        return getEffectDuration(player, effect.getId());
    }

    @Override
    public void applyEffect(IPlayer player, int id, int duration, byte level) {
        if (player == null || player.getMCEntity() == null)
            return;
        applyEffect((EntityPlayer) player.getMCEntity(), id, duration, level);
    }

    @Override
    public void applyEffect(IPlayer player, IStatusEffect effect, int duration, byte level) {
        applyEffect(player, effect.getId(), duration, level);
    }

    @Override
    public void removeEffect(IPlayer player, int id) {
        if (player == null || player.getMCEntity() == null)
            return;
        removeEffect((EntityPlayer) player.getMCEntity(), id);
    }

    @Override
    public void removeEffect(IPlayer player, IStatusEffect effect) {
        removeEffect(player, effect.getId());
    }

    @Override
    public void clearEffects(IPlayer player) {
        if (player == null || player.getMCEntity() == null)
            return;
        clearEffects(player.getMCEntity());
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
}
