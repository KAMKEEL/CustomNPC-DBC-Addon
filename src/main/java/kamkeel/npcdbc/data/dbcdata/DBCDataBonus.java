package kamkeel.npcdbc.data.dbcdata;

import kamkeel.npcdbc.data.PlayerBonus;
import kamkeel.npcdbc.data.statuseffect.PlayerEffect;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DBCDataBonus {
    private final DBCData data;

    public DBCDataBonus(DBCData dbcData) {
        this.data = dbcData;
    }

    public float[] getMultiBonus() {
        float[] bonuses = new float[3];
        for(PlayerBonus playerBonus : data.currentBonuses.values()){
            if(playerBonus.type == 0){
                bonuses[0] += playerBonus.strength;
                bonuses[1] += playerBonus.dexterity;
                bonuses[2] += playerBonus.willpower;
            }
        }
        return bonuses;
    }

    public float[] getFlatBonus() {
        float[] bonuses = new float[5];
        for(PlayerBonus playerBonus : data.currentBonuses.values()){
            if(playerBonus.type == 1){
                bonuses[0] += playerBonus.strength;
                bonuses[1] += playerBonus.dexterity;
                bonuses[2] += playerBonus.willpower;

                bonuses[3] += playerBonus.constituion;
                bonuses[4] += playerBonus.spirit;
            }
        }
        return bonuses;
    }

    public void setCurrentBonuses(HashMap<String, PlayerBonus> setVals) {
        NBTTagCompound raw = data.getRawCompound();
        data.currentBonuses = updateBonuses(setVals);
        saveBonusNBT(raw);
    }

    public HashMap<String, PlayerBonus> updateBonuses(HashMap<String, PlayerBonus> setVals) {
        HashMap<String, PlayerBonus> createdMap = new HashMap<>();
        for (PlayerBonus playerBonus : setVals.values()) {
            PlayerBonus newBonus;
            if (data.currentBonuses.containsKey(playerBonus.name)) {
                newBonus = data.currentBonuses.get(playerBonus.name);
                newBonus.type = playerBonus.type;
                newBonus.strength = playerBonus.strength;
                newBonus.dexterity = playerBonus.dexterity;
                newBonus.willpower = playerBonus.willpower;

                newBonus.constituion = playerBonus.constituion;
                newBonus.spirit = playerBonus.spirit;
                createdMap.put(playerBonus.name, newBonus);
            } else {
                createdMap.put(playerBonus.name, new PlayerBonus(playerBonus.name, playerBonus.type, playerBonus.strength,
                    playerBonus.dexterity, playerBonus.willpower, playerBonus.constituion, playerBonus.spirit));
            }
        }
        return createdMap;
    }

    public void saveBonusNBT(NBTTagCompound nbt) {
        NBTTagList nbttaglist = new NBTTagList();

        // Copy the collection to avoid ConcurrentModificationException
        List<PlayerBonus> bonusCopy = new ArrayList<>(data.currentBonuses.values());
        for (PlayerBonus bonus : bonusCopy) {
            nbttaglist.appendTag(bonus.writeBonusData(new NBTTagCompound()));
        }
        nbt.setTag("addonBonus", nbttaglist);
    }

}
