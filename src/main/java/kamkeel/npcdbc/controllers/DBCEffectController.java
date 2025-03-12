package kamkeel.npcdbc.controllers;

import kamkeel.npcdbc.api.effect.IDBCEffectHandler;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.effects.AddonEffect;
import kamkeel.npcdbc.data.effects.DamageTracker;
import kamkeel.npcdbc.data.effects.SenzuConsumptionData;
import kamkeel.npcdbc.data.effects.types.*;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.controllers.CustomEffectController;
import noppes.npcs.controllers.data.EffectKey;
import noppes.npcs.controllers.data.PlayerEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DBCEffectController implements IDBCEffectHandler {

    public static int DBC_EFFECT_INDEX = 1;
    public static DBCEffectController Instance = new DBCEffectController();

    public HashMap<Integer, AddonEffect> standardEffects = new HashMap<>();

    // Maps for Status Effects
    private final ConcurrentHashMap<UUID, DamageTracker> damageTrackers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, SenzuConsumptionData> playerSenzuConsumption = new ConcurrentHashMap<>();

    public DBCEffectController() {}

    public static DBCEffectController getInstance() {
        return Instance;
    }

    public void load() {
        // Global Registration for Effects
        standardEffects.put(Effects.REGEN_HEALTH, new RegenHealth());
        standardEffects.put(Effects.REGEN_KI, new RegenKi());
        standardEffects.put(Effects.REGEN_STAMINA, new RegenStamina());
        standardEffects.put(Effects.NAMEK_REGEN, new NamekRegen());
        standardEffects.put(Effects.FRUIT_OF_MIGHT, new FruitOfMight());
        standardEffects.put(Effects.OVERPOWER, new Overpower());
        standardEffects.put(Effects.CHOCOLATED, new Chocolated());
        standardEffects.put(Effects.MEDITATION, new Meditation());
        standardEffects.put(Effects.BLOATED, new Bloated());
        standardEffects.put(Effects.DARKNESS, new Darkness());
        standardEffects.put(Effects.ZENKAI, new Zenkai());
        standardEffects.put(Effects.POTARA, new PotaraFusion());
        // standardEffects.put(Effects.COLD_BLOODED, new Exhausted()); 192
        // standardEffects.put(Effects.KI_DEFENSE, new Exhausted()); 208
        standardEffects.put(Effects.HUMAN_SPIRIT, new HumanSpirit());
        standardEffects.put(Effects.EXHAUSTED, new Exhausted());

        CustomEffectController.getInstance().registerEffectMap(DBC_EFFECT_INDEX, standardEffects);
    }

    public boolean hasEffect(EntityPlayer player, int id){
        return CustomEffectController.getInstance().hasEffect(player, id, DBC_EFFECT_INDEX);
    }

    public void applyEffect(EntityPlayer player, int id){
        AddonEffect addonEffect = standardEffects.get(id);
        if(addonEffect == null)
            return;

        CustomEffectController.getInstance().applyEffect(player, id, addonEffect.length, (byte) 1, DBC_EFFECT_INDEX);
    }

    public AddonEffect get(int id){
        return standardEffects.get(id);
    }

    public void applyEffect(EntityPlayer player, int id, int duration){
        CustomEffectController.getInstance().applyEffect(player, id, duration, (byte) 1, DBC_EFFECT_INDEX);
    }

    public void applyEffect(EntityPlayer player, int id, int duration, byte level){
        CustomEffectController.getInstance().applyEffect(player, id, duration, level, DBC_EFFECT_INDEX);
    }

    public void clearDBCEffects(EntityPlayer player) {
        Map<EffectKey, PlayerEffect> map = CustomEffectController.getInstance().getPlayerEffects(player);
        map.keySet().removeIf(key -> key.getIndex() == DBC_EFFECT_INDEX);
    }

    public void removeEffect(EntityPlayer player, int id) {
        Map<EffectKey, PlayerEffect> map = CustomEffectController.getInstance().getPlayerEffects(player);
        map.keySet().removeIf(key -> key.getIndex() == DBC_EFFECT_INDEX && key.getId() == id);
    }


    public int getEffectDuration(EntityPlayer player, int id) {
        return CustomEffectController.getInstance().getEffectDuration(player, id, DBC_EFFECT_INDEX);
    }

    public int getEffectLevel(EntityPlayer player, int id) {
        PlayerEffect effect = CustomEffectController.getInstance().getPlayerEffects(player)
            .get(new EffectKey(id, DBC_EFFECT_INDEX));
        return effect != null ? effect.level : -1;
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


    // API VERSIONS
    @Override
    public boolean hasEffect(IPlayer player, int id){
        if(player == null || !(player.getMCEntity() instanceof EntityPlayer))
            return false;

        return hasEffect((EntityPlayer) player.getMCEntity(), id);
    }

    @Override
    public void applyEffect(IPlayer player, int id){
        if(player == null || !(player.getMCEntity() instanceof EntityPlayer))
            return;

        EntityPlayer entityPlayer = (EntityPlayer) player.getMCEntity();
        applyEffect(entityPlayer, id);
    }

    @Override
    public void applyEffect(IPlayer player, int id, int duration){
        if(player == null || !(player.getMCEntity() instanceof EntityPlayer))
            return;

        EntityPlayer entityPlayer = (EntityPlayer) player.getMCEntity();
        applyEffect(entityPlayer, id, duration);
    }

    @Override
    public void applyEffect(IPlayer player, int id, int duration, byte level){
        if(player == null || !(player.getMCEntity() instanceof EntityPlayer))
            return;

        EntityPlayer entityPlayer = (EntityPlayer) player.getMCEntity();
        applyEffect(entityPlayer, id, duration, level);
    }

    @Override
    public void clearDBCEffects(IPlayer player){
        if(player == null || !(player.getMCEntity() instanceof EntityPlayer))
            return;

        EntityPlayer entityPlayer = (EntityPlayer) player.getMCEntity();
        clearDBCEffects(entityPlayer);
    }

    @Override
    public void removeEffect(IPlayer player, int id) {
        if(player == null || !(player.getMCEntity() instanceof EntityPlayer))
            return;

        EntityPlayer entityPlayer = (EntityPlayer) player.getMCEntity();
        removeEffect(entityPlayer, id);
    }

    @Override
    public int getEffectDuration(IPlayer player, int id) {
        if (player == null || !(player.getMCEntity() instanceof EntityPlayer))
            return -1;
        return getEffectDuration((EntityPlayer) player.getMCEntity(), id);
    }

    @Override
    public int getEffectLevel(IPlayer player, int id) {
        if (player == null || !(player.getMCEntity() instanceof EntityPlayer))
            return -1;
        return getEffectLevel((EntityPlayer) player.getMCEntity(), id);
    }
}
