package kamkeel.npcdbc.data.dbcdata;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.controllers.BonusController;
import kamkeel.npcdbc.data.PlayerBonus;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.HashMap;
import java.util.Map;

public class DBCDataBonus {
    private final DBCData data;

    public DBCDataBonus(DBCData dbcData) {
        this.data = dbcData;
    }

    public Map<String, PlayerBonus> getCurrentBonuses() {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            if (data.currentBonuses == null)
                data.currentBonuses = new HashMap<>();
            return data.currentBonuses;
        }

        return BonusController.getInstance().getPlayerBonus(data.player);
    }

    public float[] getMultiBonus() {
        float[] bonuses = new float[5];
        for (PlayerBonus playerBonus : getCurrentBonuses().values()) {
            if (playerBonus.type == 0) {
                bonuses[0] += playerBonus.strength;
                bonuses[1] += playerBonus.dexterity;
                bonuses[2] += playerBonus.willpower;
                bonuses[3] += playerBonus.constituion;
                bonuses[4] += playerBonus.spirit;
            }
        }
        return bonuses;
    }

    public float[] getFlatBonus() {
        float[] bonuses = new float[5];
        for (PlayerBonus playerBonus : getCurrentBonuses().values()) {
            if (playerBonus.type == 1) {
                bonuses[0] += playerBonus.strength;
                bonuses[1] += playerBonus.dexterity;
                bonuses[2] += playerBonus.willpower;
                bonuses[3] += playerBonus.constituion;
                bonuses[4] += playerBonus.spirit;
            }
        }
        return bonuses;
    }

    public void saveBonusNBT(NBTTagCompound nbt) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
            return;

        NBTTagList nbttaglist = new NBTTagList();

        for (PlayerBonus bonus : getCurrentBonuses().values()) {
            nbttaglist.appendTag(bonus.writeBonusData(new NBTTagCompound()));
        }
        nbt.setTag("addonBonus", nbttaglist);
    }

}
