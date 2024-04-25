package kamkeel.npcdbc.controllers;

import kamkeel.npcdbc.data.DBCData;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import kamkeel.npcdbc.data.statuseffect.types.RegenEffect;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.UUID;

public class StatusEffectController {

    public static StatusEffectController Instance = new StatusEffectController();
    public HashMap<Integer, StatusEffect> registeredEffects = new HashMap<>();
    public HashMap<UUID, HashMap<Integer, Integer>> activeEffects = new HashMap<>();

    public void load(){
        activeEffects.clear();

        registeredEffects.put(1, new RegenEffect(-1));
    }

    public void loadEffects(EntityPlayer player){
        DBCData dbcData = DBCData.get(player);
        activeEffects.put(Utility.getUUID(player), dbcData.activeEffects);
    }

    public void runEffects(EntityPlayer player){
        HashMap<Integer, Integer> current = getPlayerEffects(player);
        for(int active : current.keySet()){
            StatusEffect effect = registeredEffects.get(active);
            if(effect != null){
                effect.runEffect(player);
            }
        }
    }

    public HashMap<Integer, Integer> getPlayerEffects(EntityPlayer player){
        return activeEffects.get(Utility.getUUID(player));
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
