package kamkeel.npcdbc.controllers;

import kamkeel.npcdbc.api.effect.IStatusEffectHandler;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.DBCData;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import kamkeel.npcdbc.data.statuseffect.types.*;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.UUID;

public class StatusEffectController implements IStatusEffectHandler {

    public static StatusEffectController Instance = new StatusEffectController();
    public HashMap<Integer, StatusEffect> standardEffects = new HashMap<>();
    public HashMap<Integer, StatusEffect> customEffects = new HashMap<>(); // TODO: I will implement later - Kam

    public HashMap<UUID, HashMap<Integer, StatusEffect>> activeEffects = new HashMap<>();

    public static StatusEffectController getInstance() {
        return Instance;
    }

    public void load() {
        activeEffects.clear();

        standardEffects.put(Effects.REGEN, new Regen(-1));
        standardEffects.put(Effects.NAMEK_REGEN, new NamekRegen(-1));
        standardEffects.put(Effects.FRUIT_OF_MIGHT, new FruitOfMight(-1));
        standardEffects.put(Effects.INFLATION, new Inflation(-1));
        standardEffects.put(Effects.MEDITATION, new Meditation(-1));
        standardEffects.put(Effects.OVERPOWER, new Overpower(-1));
        standardEffects.put(Effects.CHOCOLATED, new Chocolated(-1));
    }

    public void loadEffects(EntityPlayer player) {
        DBCData dbcData = DBCData.get(player);
        activeEffects.put(Utility.getUUID(player), dbcData.activeEffects);
    }

    public void runEffects(EntityPlayer player) {
        HashMap<Integer, StatusEffect> current = getPlayerEffects(player);
        for (int active : current.keySet()) {
            StatusEffect effect = standardEffects.get(active);
            if (effect != null) {
                effect.runEffect(player);
            }
        }
    }

    public StatusEffect get(int id) {
        return standardEffects.get(id);
    }

    public HashMap<Integer, StatusEffect> getPlayerEffects(EntityPlayer player) {
        return activeEffects.get(Utility.getUUID(player));
    }

    public void applyEffect(EntityPlayer player, StatusEffect effect) {
        HashMap<Integer, StatusEffect> currentEffects = new HashMap<>();
        if (activeEffects.containsKey(player.getUniqueID()))
            currentEffects = activeEffects.get(Utility.getUUID(player));

        currentEffects.put(effect.id, effect);
        effect.init(player);
    }

    public void removeEffect(EntityPlayer player, StatusEffect effect) {
        HashMap<Integer, StatusEffect> currentEffects = new HashMap<>();
        if (activeEffects.containsKey(player.getUniqueID()))
            currentEffects = activeEffects.get(Utility.getUUID(player));

        currentEffects.remove(effect.id);
        effect.runout(player);
    }

    @Override
    public boolean hasEffectTime(EntityPlayer player, int id) {
        HashMap<Integer, StatusEffect> currentEffects = new HashMap<>();
        if (activeEffects.containsKey(player.getUniqueID()))
            currentEffects = activeEffects.get(Utility.getUUID(player));
        else
            return false;

        return currentEffects.containsKey(id);
    }

    @Override
    public int getEffectDuration(EntityPlayer player, int id) {
        HashMap<Integer, StatusEffect> currentEffects = new HashMap<>();
        if (activeEffects.containsKey(player.getUniqueID()))
            currentEffects = activeEffects.get(Utility.getUUID(player));

        if (currentEffects.containsKey(id))
            return currentEffects.get(id).duration;

        return -2;
    }

    @Override
    public void applyEffect(EntityPlayer player, int id, int duration, byte level) {
        HashMap<Integer, StatusEffect> currentEffects = new HashMap<>();
        if (activeEffects.containsKey(player.getUniqueID()))
            currentEffects = activeEffects.get(Utility.getUUID(player));

        StatusEffect statusEffect = new StatusEffect(duration);
        statusEffect.id = id;
        statusEffect.level = level;
        currentEffects.put(id, statusEffect);
        statusEffect.init(player);

    }

    @Override
    public void removeEffect(EntityPlayer player, int id) {
        HashMap<Integer, StatusEffect> currentEffects = new HashMap<>();
        if (activeEffects.containsKey(player.getUniqueID()))
            currentEffects = activeEffects.get(Utility.getUUID(player));

        currentEffects.remove(id);
        currentEffects.get(id).runout(player);

    }
}
