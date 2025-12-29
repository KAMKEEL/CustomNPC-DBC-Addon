package kamkeel.npcdbc.data.dbcdata;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.controllers.BonusController;
import kamkeel.npcdbc.constants.DBCAttribute;
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

    public BonusTotals calculateTotals() {
        float[] multi = new float[5];
        float[] flat = new float[5];

        for (PlayerBonus playerBonus : getCurrentBonuses().values()) {
            if (playerBonus.type == 0) {
                multi[0] += playerBonus.strength;
                multi[1] += playerBonus.dexterity;
                multi[2] += playerBonus.willpower;
                multi[3] += playerBonus.constituion;
                multi[4] += playerBonus.spirit;
            } else if (playerBonus.type == 1) {
                flat[0] += playerBonus.strength;
                flat[1] += playerBonus.dexterity;
                flat[2] += playerBonus.willpower;
                flat[3] += playerBonus.constituion;
                flat[4] += playerBonus.spirit;
            }
        }

        return new BonusTotals(multi, flat);
    }

    public float[] getMultiBonus() {
        return calculateTotals().copyMultipliers();
    }

    public float[] getFlatBonus() {
        return calculateTotals().copyFlatAdditions();
    }

    public float getMultiBonusForAttribute(int attributeID) {
        return calculateTotals().getMultiplier(attributeID);
    }

    public float getFlatBonusForAttribute(int attributeID) {
        return calculateTotals().getFlat(attributeID);
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

    public static final class BonusTotals {
        private final float[] multipliers;
        private final float[] flatAdditions;

        private BonusTotals(float[] multipliers, float[] flatAdditions) {
            this.multipliers = multipliers;
            this.flatAdditions = flatAdditions;
        }

        public float[] copyMultipliers() {
            return multipliers.clone();
        }

        public float[] copyFlatAdditions() {
            return flatAdditions.clone();
        }

        public float getMultiplier(int attributeID) {
            return getValue(attributeID, multipliers);
        }

        public float getFlat(int attributeID) {
            return getValue(attributeID, flatAdditions);
        }

        private float getValue(int attributeID, float[] values) {
            int index = toBonusIndex(attributeID);
            return index >= 0 ? values[index] : 0.0F;
        }
    }

    private static int toBonusIndex(int attributeID) {
        switch (attributeID) {
            case DBCAttribute.Strength:
                return 0;
            case DBCAttribute.Dexterity:
                return 1;
            case DBCAttribute.Willpower:
                return 2;
            case DBCAttribute.Constitution:
                return 3;
            case DBCAttribute.Spirit:
                return 4;
            default:
                return -1;
        }
    }

}
