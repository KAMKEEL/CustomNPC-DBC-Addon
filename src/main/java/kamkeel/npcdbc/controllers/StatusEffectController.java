package kamkeel.npcdbc.controllers;

import kamkeel.npcdbc.api.effect.IStatusEffectHandler;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.statuseffect.CustomEffect;
import kamkeel.npcdbc.data.statuseffect.PlayerEffect;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import kamkeel.npcdbc.data.statuseffect.types.*;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.api.entity.IPlayer;

import java.util.HashMap;
import java.util.UUID;

public class StatusEffectController implements IStatusEffectHandler {

    public static StatusEffectController Instance = new StatusEffectController();

    public HashMap<Integer, StatusEffect> standardEffects = new HashMap<>();
    public HashMap<Integer, CustomEffect> customEffects = new HashMap<>(); // TODO: I will implement later - Kam

    public HashMap<UUID, HashMap<Integer, PlayerEffect>> playerEffects = new HashMap<>();

    public StatusEffectController(){

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
        standardEffects.put(Effects.BLOATED, new Bloated());    // TODO: Finish it
        standardEffects.put(Effects.MEDITATION, new Meditation());  // TODO: Finish it
        standardEffects.put(Effects.OVERPOWER, new Overpower());    // TODO: Finish it
        standardEffects.put(Effects.CHOCOLATED, new Chocolated());  // TODO: Finish it
        standardEffects.put(Effects.DARKNESS, new Darkness());      // TODO: Finish it
        standardEffects.put(Effects.ZENKAI, new Zenkai());          // TODO: Finish it
    }

    /**
     * Loads Effects from Players Player Persisted NBT
     * @param player Player Logging in
     */
    public void loadEffects(EntityPlayer player) {
        DBCData dbcData = DBCData.get(player);
        HashMap<Integer, PlayerEffect> playerEffectHashMap = new HashMap<>();
        for(PlayerEffect val : dbcData.currentEffects.values()){
            PlayerEffect playerEffect = new PlayerEffect(val.id, val.duration, val.level);
            playerEffectHashMap.put(playerEffect.id, playerEffect);
        }
        playerEffects.put(Utility.getUUID(player), playerEffectHashMap);
    }

    public void runEffects(EntityPlayer player) {
        HashMap<Integer, PlayerEffect> current = getPlayerEffects(player);
        for (int active : current.keySet()) {
            StatusEffect effect = get(active);
            if (effect != null) {
                effect.runEffect(player, current.get(active));
            }
        }
    }

    public void killEffects(EntityPlayer player) {
        HashMap<Integer, PlayerEffect> current = getPlayerEffects(player);
        for (int active : current.keySet()) {
            StatusEffect effect = get(active);
            if (effect != null) {
                if(effect.lossOnDeath)
                    current.remove(active);
            } else {
                current.remove(active);
            }
        }
    }

    public StatusEffect get(int id) {
        StatusEffect statusEffect = standardEffects.get(id);
        if(statusEffect == null)
            statusEffect = customEffects.get(id);

        return statusEffect;
    }

    public HashMap<Integer, PlayerEffect> getPlayerEffects(EntityPlayer player) {
        return playerEffects.get(Utility.getUUID(player));
    }

    public void applyEffect(EntityPlayer player, int id) {
        StatusEffect parent = get(id);
        if(parent != null){
            PlayerEffect playerEffect = new PlayerEffect(id, parent.length, (byte) 1);
            applyEffect(player, playerEffect);
        }
    }

    public void applyEffect(EntityPlayer player, int id, int duration) {
        StatusEffect parent = get(id);
        if(parent != null){
            PlayerEffect playerEffect = new PlayerEffect(id, duration, (byte) 1);
            applyEffect(player, playerEffect);
        }
    }

    public void applyEffect(EntityPlayer player, PlayerEffect effect) {
        if(effect == null)
            return;

        StatusEffect parent = get(effect.id);
        if(parent != null){
            HashMap<Integer, PlayerEffect> currentEffects = new HashMap<>();
            UUID uuid = Utility.getUUID(player);
            if (playerEffects.containsKey(uuid))
                currentEffects = playerEffects.get(Utility.getUUID(player));
            else
                playerEffects.put(uuid, currentEffects);

            currentEffects.put(effect.id, effect);
            parent.init(player, effect);
        }
    }

    public void removeEffect(EntityPlayer player, PlayerEffect effect) {
        if(effect == null)
            return;

        HashMap<Integer, PlayerEffect> currentEffects = new HashMap<>();
        UUID uuid = Utility.getUUID(player);
        if (playerEffects.containsKey(uuid))
            currentEffects = playerEffects.get(Utility.getUUID(player));
        else
            playerEffects.put(uuid, currentEffects);

        if(currentEffects.containsKey(effect.id)){
            StatusEffect parent = get(effect.id);
            if(parent != null){
                parent.runout(player, effect);
            }
            currentEffects.remove(effect.id);
        }
    }

    @Override
    public boolean hasEffect(IPlayer player, int id) {
        if(player == null || player.getMCEntity() == null)
            return false;
        return hasEffect((EntityPlayer) player.getMCEntity(), id);
    }

    @Override
    public int getEffectDuration(IPlayer player, int id) {
        if(player == null || player.getMCEntity() == null)
            return -1;
        return getEffectDuration((EntityPlayer) player.getMCEntity(), id);
    }

    @Override
    public void applyEffect(IPlayer player, int id, int duration, byte level) {
        if(player == null || player.getMCEntity() == null)
            return;
        applyEffect((EntityPlayer) player.getMCEntity(), id, duration, level);
    }

    @Override
    public void removeEffect(IPlayer player, int id) {
        if(player == null || player.getMCEntity() == null)
            return;
        removeEffect((EntityPlayer) player.getMCEntity(), id);
    }

    public boolean hasEffect(EntityPlayer player, int id) {
        HashMap<Integer, PlayerEffect> currentEffects;
        UUID uuid = Utility.getUUID(player);
        if (playerEffects.containsKey(uuid))
            currentEffects = playerEffects.get(uuid);
        else
            return false;

        return currentEffects.containsKey(id);
    }

    public int getEffectDuration(EntityPlayer player, int id) {
        HashMap<Integer, PlayerEffect> currentEffects = new HashMap<>();
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
        if(player == null || id <= 0)
            return;

        HashMap<Integer, PlayerEffect> currentEffects = new HashMap<>();
        UUID uuid = Utility.getUUID(player);
        if (playerEffects.containsKey(uuid))
            currentEffects = playerEffects.get(Utility.getUUID(player));
        else
            playerEffects.put(uuid, currentEffects);

        StatusEffect parent = get(id);
        if(parent != null){
            PlayerEffect playerEffect = new PlayerEffect(id, duration, level);
            currentEffects.put(id, playerEffect);
            parent.init(player, playerEffect);
        }
    }

    public void removeEffect(EntityPlayer player, int id) {
        if(player == null || id <= 0)
            return;

        HashMap<Integer, PlayerEffect> currentEffects = new HashMap<>();
        UUID uuid = Utility.getUUID(player);
        if (playerEffects.containsKey(uuid))
            currentEffects = playerEffects.get(Utility.getUUID(player));
        else
            playerEffects.put(uuid, currentEffects);

        if(currentEffects.containsKey(id)){
            PlayerEffect current = currentEffects.get(id);
            StatusEffect parent = get(current.id);
            if(parent != null)
                parent.runout(player, current);

            currentEffects.remove(id);
        }
    }
}
