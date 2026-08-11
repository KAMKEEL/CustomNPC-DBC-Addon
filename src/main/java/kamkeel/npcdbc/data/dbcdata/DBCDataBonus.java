package kamkeel.npcdbc.data.dbcdata;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.constants.DBCAttribute;
import kamkeel.npcdbc.controllers.BonusController;
import kamkeel.npcdbc.data.PlayerBonus;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DBCDataBonus {
    private static final int ATTRIBUTE_COUNT = 5;
    private static final int STAT_COUNT = PlayerBonus.STAT_COUNT;
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
        float[] multiplicative = new float[ATTRIBUTE_COUNT];
        Arrays.fill(multiplicative, 1.0f);

        float[] statPercentage = new float[STAT_COUNT];
        float[] statFlat = new float[STAT_COUNT];
        float[] statMultiplicative = new float[STAT_COUNT];
        Arrays.fill(statMultiplicative, 1.0f);

        for (PlayerBonus playerBonus : getCurrentBonuses().values()) {
            if (playerBonus == null)
                continue;

            float[] values = playerBonus.getValues();
            float[] statValues = playerBonus.getStatValues();
            switch (playerBonus.type) {
                case 0: // Percentage (additive stacking)
                    for (int i = 0; i < ATTRIBUTE_COUNT; i++)
                        percentage[i] += values[i];
                    for (int i = 0; i < STAT_COUNT; i++)
                        statPercentage[i] += statValues[i];
                    break;
                case 1: // Flat
                    for (int i = 0; i < ATTRIBUTE_COUNT; i++)
                        flat[i] += values[i];
                    for (int i = 0; i < STAT_COUNT; i++)
                        statFlat[i] += statValues[i];
                    break;
                case 2: // Multiplicative (true percentage multiplication)
                    for (int i = 0; i < ATTRIBUTE_COUNT; i++)
                        multiplicative[i] *= Math.max(0.0f, 1.0f + values[i] / 100.0f);
                    for (int i = 0; i < STAT_COUNT; i++)
                        statMultiplicative[i] *= Math.max(0.0f, 1.0f + statValues[i] / 100.0f);
                    break;
            }
        }

        // Clamp percentage totals to -1.0 (can't reduce more than 100%)
        for (int i = 0; i < ATTRIBUTE_COUNT; i++) {
            if (percentage[i] < -1.0f) percentage[i] = -1.0f;
        }
        for (int i = 0; i < STAT_COUNT; i++) {
            if (statPercentage[i] < -1.0f) statPercentage[i] = -1.0f;
        }

        return new BonusTotals(percentage, flat, multiplicative, statPercentage, statFlat, statMultiplicative);
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

    public float[] getStatMultiBonus() {
        return calculateTotals().copyStatPercentage();
    }

    public float[] getStatFlatBonus() {
        return calculateTotals().copyStatFlat();
    }

    public float getMultiBonusForStat(int statID) {
        return calculateTotals().getStatPercentage(statID);
    }

    public float getFlatBonusForStat(int statID) {
        return calculateTotals().getStatFlat(statID);
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

        private final float[] statPercentage;
        private final float[] statFlatAdditions;
        private final float[] statMultiplicative;

        private BonusTotals(float[] percentage, float[] flatAdditions, float[] multiplicative,
                            float[] statPercentage, float[] statFlatAdditions, float[] statMultiplicative) {
            this.percentage = percentage;
            this.flatAdditions = flatAdditions;
            this.multiplicative = multiplicative;
            this.statPercentage = statPercentage;
            this.statFlatAdditions = statFlatAdditions;
            this.statMultiplicative = statMultiplicative;
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

        public float[] copyStatPercentage() {
            return statPercentage.clone();
        }

        public float[] copyStatFlat() {
            return statFlatAdditions.clone();
        }

        public float[] copyStatMultiplicative() {
            return statMultiplicative.clone();
        }

        public float getStatPercentage(int statID) {
            return isStat(statID) ? statPercentage[statID] : 0.0F;
        }

        public float getStatFlat(int statID) {
            return isStat(statID) ? statFlatAdditions[statID] : 0.0F;
        }

        public float getStatMultiplicative(int statID) {
            return isStat(statID) ? statMultiplicative[statID] : 1.0F;
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

            return applyStack(currentValue, baseAttribute,
                multiplicative[index], percentage[index], flatAdditions[index]);
        }

        /**
         * Applies all bonus types to a statistic value in the same order as {@link #applyAll(int, int, int)}.
         * Statistics outside the DBC stat pipeline are returned unchanged.
         *
         * @param statID       The {@link kamkeel.npcdbc.constants.DBCStatistics} ID
         * @param baseStat     The stat value before any form or bonus modification (used for percentage calc)
         * @param currentValue The current computed stat value
         * @return The modified stat value, minimum 1
         */
        public int applyAllStats(int statID, int baseStat, int currentValue) {
            if (!isStat(statID))
                return currentValue;

            return applyStack(currentValue, baseStat,
                statMultiplicative[statID], statPercentage[statID], statFlatAdditions[statID]);
        }

        /**
         * Runs the multiplicative, percentage and flat stack in double precision so large
         * DBC values are not truncated by float's 24-bit mantissa, then rounds and clamps once.
         */
        private static int applyStack(int currentValue, int base, float multi, float percent, float flat) {
            double result = currentValue;

            if (multi != 1.0F)
                result *= multi;

            if (percent != 0.0F)
                result += (double) base * percent;

            if (flat != 0.0F)
                result += flat;

            if (Double.isNaN(result))
                return 1;

            long rounded = Math.round(result);
            if (rounded < 1L)
                return 1;
            if (rounded > Integer.MAX_VALUE)
                return Integer.MAX_VALUE;
            return (int) rounded;
        }

        private float getValue(int attributeID, float[] values) {
            int index = toBonusIndex(attributeID);
            return index >= 0 ? values[index] : 0.0F;
        }
    }

    private static boolean isStat(int statID) {
        return statID >= 0 && statID < STAT_COUNT;
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
