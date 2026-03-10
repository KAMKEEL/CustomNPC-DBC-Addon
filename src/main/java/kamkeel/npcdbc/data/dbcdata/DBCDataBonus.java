package kamkeel.npcdbc.data.dbcdata;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.constants.DBCAttribute;
import kamkeel.npcdbc.controllers.BonusController;
import kamkeel.npcdbc.data.PlayerBonus;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.HashMap;
import java.util.Map;

public class DBCDataBonus {
    private static final int ATTRIBUTE_COUNT = 5;
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
        float[] percentage = new float[ATTRIBUTE_COUNT];
        float[] flat = new float[ATTRIBUTE_COUNT];
        float[] multiplicative = {1.0f, 1.0f, 1.0f, 1.0f, 1.0f};

        for (PlayerBonus playerBonus : getCurrentBonuses().values()) {
            float[] values = playerBonus.getValues();
            switch (playerBonus.type) {
                case 0: // Percentage (additive stacking)
                    for (int i = 0; i < ATTRIBUTE_COUNT; i++)
                        percentage[i] += values[i];
                    break;
                case 1: // Flat
                    for (int i = 0; i < ATTRIBUTE_COUNT; i++)
                        flat[i] += values[i];
                    break;
                case 2: // Multiplicative (true percentage multiplication)
                    for (int i = 0; i < ATTRIBUTE_COUNT; i++)
                        multiplicative[i] *= Math.max(0.0f, 1.0f + values[i] / 100.0f);
                    break;
            }
        }

        // Clamp percentage totals to -1.0 (can't reduce more than 100%)
        for (int i = 0; i < ATTRIBUTE_COUNT; i++) {
            if (percentage[i] < -1.0f) percentage[i] = -1.0f;
        }

        return new BonusTotals(percentage, flat, multiplicative);
    }

    public float[] getMultiBonus() {
        return calculateTotals().copyPercentage();
    }

    public float[] getFlatBonus() {
        return calculateTotals().copyFlat();
    }

    public float getMultiBonusForAttribute(int attributeID) {
        return calculateTotals().getPercentage(attributeID);
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
        private final float[] percentage;
        private final float[] flatAdditions;
        private final float[] multiplicative;

        private BonusTotals(float[] percentage, float[] flatAdditions, float[] multiplicative) {
            this.percentage = percentage;
            this.flatAdditions = flatAdditions;
            this.multiplicative = multiplicative;
        }

        public float[] copyPercentage() {
            return percentage.clone();
        }

        /**
         * @deprecated Use {@link #copyPercentage()} instead
         */
        @Deprecated
        public float[] copyMultipliers() {
            return copyPercentage();
        }

        public float[] copyFlat() {
            return flatAdditions.clone();
        }

        /**
         * @deprecated Use {@link #copyFlat()} instead
         */
        @Deprecated
        public float[] copyFlatAdditions() {
            return copyFlat();
        }

        public float[] copyMultiplicative() {
            return multiplicative.clone();
        }

        public float getPercentage(int attributeID) {
            return getValue(attributeID, percentage);
        }

        /**
         * @deprecated Use {@link #getPercentage(int)} instead
         */
        @Deprecated
        public float getMultiplier(int attributeID) {
            return getPercentage(attributeID);
        }

        public float getFlat(int attributeID) {
            return getValue(attributeID, flatAdditions);
        }

        public float getMultiplicative(int attributeID) {
            int index = toBonusIndex(attributeID);
            return index >= 0 ? multiplicative[index] : 1.0f;
        }

        /**
         * Applies all bonus types to an attribute value in the correct order:
         * <ol>
         *   <li>Multiplicative (Type 2) - true percentage multiplication</li>
         *   <li>Percentage (Type 0) - additive percentage of base attribute</li>
         *   <li>Flat (Type 1) - direct addition</li>
         * </ol>
         * Result is floored to 1 to prevent division-by-zero in downstream calculations.
         *
         * @param attributeID  The DBC attribute ID
         * @param baseAttribute The original base attribute value (used for percentage calc)
         * @param currentValue  The current computed attribute value
         * @return The modified attribute value, minimum 1
         */
        public int applyAll(int attributeID, int baseAttribute, int currentValue) {
            int index = toBonusIndex(attributeID);
            if (index < 0)
                return currentValue;

            // Type 2: Multiplicative (applied first)
            if (multiplicative[index] != 1.0f)
                currentValue = Math.round(currentValue * multiplicative[index]);

            // Type 0: Percentage (additive stacking)
            if (percentage[index] != 0.0f)
                currentValue += Math.round(baseAttribute * percentage[index]);

            // Type 1: Flat
            if (flatAdditions[index] != 0.0f)
                currentValue += Math.round(flatAdditions[index]);

            return Math.max(currentValue, 1);
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
