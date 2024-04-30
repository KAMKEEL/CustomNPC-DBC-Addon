package kamkeel.npcdbc.data.dbcdata;

import kamkeel.npcdbc.data.PlayerBonus;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.HashMap;
import java.util.Iterator;

import static JinRyuu.JRMCore.JRMCoreH.nbt;

public class DBCDataBonus {
    private final DBCData data;

    public DBCDataBonus(DBCData dbcData) {
        this.data = dbcData;
    }

    public float[] getCurrentBonuses() {
        float[] bonuses = new float[3];
        for(PlayerBonus playerBonus : data.currentBonuses.values()){
            bonuses[0] += playerBonus.strength;
            bonuses[1] += playerBonus.dexterity;
            bonuses[2] += playerBonus.willpower;
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
                newBonus.strength = playerBonus.strength;
                newBonus.dexterity = playerBonus.dexterity;
                newBonus.willpower = playerBonus.willpower;
                createdMap.put(playerBonus.name, newBonus);
            } else {
                createdMap.put(playerBonus.name, new PlayerBonus(playerBonus.name, playerBonus.strength, playerBonus.dexterity, playerBonus.willpower));
            }
        }
        return createdMap;
    }

    public void saveBonusNBT(NBTTagCompound nbt) {
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = data.currentBonuses.values().iterator();
        while (iterator.hasNext()) {
            PlayerBonus bonus = (PlayerBonus) iterator.next();
            nbttaglist.appendTag(bonus.writeBonusData(new NBTTagCompound()));
        }
        nbt.setTag("addonBonus", nbttaglist);
    }

}
