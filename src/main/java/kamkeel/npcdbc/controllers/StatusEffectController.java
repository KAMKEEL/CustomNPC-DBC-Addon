package kamkeel.npcdbc.controllers;

import kamkeel.npcdbc.data.DBCData;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.UUID;

public class StatusEffectController {

    public static StatusEffectController Instance = new StatusEffectController();
    public HashMap<UUID, HashMap<Integer, Integer>> activeEffects = new HashMap<>();

    public void load(){
        activeEffects.clear();
    }

    public void loadEffects(EntityPlayer player){
        DBCData dbcData = DBCData.get(player);
        activeEffects.put(Utility.getUUID(player), dbcData.activeEffects);
    }

    public void applyEffect(EntityPlayer player, StatusEffect effect){
        HashMap<Integer, Integer> currentEffects = new HashMap<>();
        if(activeEffects.containsKey(player.getUniqueID()))
            currentEffects = activeEffects.get(Utility.getUUID(player));

        currentEffects.put(effect.id, effect.timer);
    }

    public void removeEffect(EntityPlayer player, StatusEffect effect){
        HashMap<Integer, Integer> currentEffects = new HashMap<>();
        if(activeEffects.containsKey(player.getUniqueID()))
            currentEffects = activeEffects.get(Utility.getUUID(player));

        currentEffects.remove(effect.id);
    }

    public static StatusEffectController getInstance() {
        return Instance;
    }
}
