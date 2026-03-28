package kamkeel.npcdbc.data.race.stats;

import kamkeel.npcdbc.constants.enums.EnumDBCAttributes;
import kamkeel.npcdbc.constants.enums.EnumDBCClasses;
import kamkeel.npcdbc.constants.enums.EnumDBCStats;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.util.PlayerDataUtil;
import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.server.config.dbc.JGConfigDBCGoD;
import JinRyuu.JRMCore.server.config.dbc.JGConfigUltraInstinct;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Pure-computation helper for custom race stat injection into DBC's pipeline.
 *
 * <p>This class mirrors the formulas in {@code JRMCoreH.stat()} (section 3.2 of DBC_STAT_INJECTION.md)
 * and {@code JRMCoreH.attributeStart()} (section 7.1), translating DBC's 3D config arrays
 * ({@code CONFIG_RACES_STATS_MULTI}, {@code CONFIG_RACES_STAT_BONUS}, {@code CONFIG_RACES_ATTRIBUTE_MULTI},
 * {@code CONFIG_RACES_ATTRIBUTE_START}) into the addon's per-class {@link ClassStats} data model.
 *
 * <p>No runtime side effects — all methods are static, deterministic, and stateless.
 */
public class RaceStatCalculator {

    /**
     * Resolves the {@link ClassStats} for the given player's active custom race and DBC class.
     *
     * <p>Detection path: reads the player's addon-side {@link PlayerDBCInfo} to check
     * {@link PlayerDBCInfo#isCustomRace()}. Custom races use the Human carrier race
     * ({@code jrmcRace=0}) in DBC's own NBT, so we cannot detect custom race from
     * {@code jrmcRace} alone — the addon's own race tracking is authoritative.
     *
     * <p>The {@code powerType} guard is NOT applied here because different callers
     * (stat injection vs. attributeStart injection) have different guard needs. The
     * caller is responsible for checking {@code powerType == 1} before invoking this.
     *
     * @param player the player entity
     * @return the resolved ClassStats, or {@code null} if the player is not a custom race
     */
    public static ClassStats resolveClassStats(EntityPlayer player) {
        return resolveClassStats(player, null);
    }

    public static ClassStats resolveClassStats(EntityPlayer player, Integer classIDOverride) {
        PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(player);
        if (info == null)
            return null;

        // isCustomRace() checks that currentRaceKey is non-null and maps to a registered Race
        // in RaceController. Vanilla DBC races (Human/Saiyan/etc.) are NOT registered there.
        if (!info.isCustomRace())
            return null;

        Race race = info.getRace();
        if (race == null)
            return null;

        int classID;
        if (classIDOverride != null) {
            classID = classIDOverride;
        } else {
            NBTTagCompound nbt = player.getEntityData().getCompoundTag("PlayerPersisted");
            classID = nbt.getByte("jrmcClass");
        }
        EnumDBCClasses classEnum = EnumDBCClasses.fromOrdinal(classID);

        // RaceStats.getOrDefault returns ClassStats.defaults() if this class was never
        // explicitly configured, giving sane Human-like baseline values.
        return race.stats.getOrDefault(classEnum);
    }

    // ────────────────────────────────────────────────────────────────────────
    // Stat → Attribute mapping table (mirrors DBC's call sites in Sd35MR and jrmcDam):
    //
    //   Stat (index)       Input Attribute    attributeID param
    //   ─────────────────  ─────────────────  ──────────────────
    //   Melee       (0)  ← STR  (0)           0
    //   Defense     (1)  ← DEX  (1)           1
    //   Body        (2)  ← CON  (2)           2
    //   Stamina     (3)  ← WIL  (3)           3
    //   EnergyPower (4)  ← WIL  (3)           3  ← NOT 4
    //   EnergyPool  (5)  ← SPI  (5)           5
    //   MaxSkills   (6)  ← MND  (4)           4
    //   Speed       (7)  ← DEX  (1)           1
    //
    // The attributeIndex parameter in computeStat controls which
    // CONFIG_RACES_ATTRIBUTE_MULTI column is applied as the final multiplier.
    // It does NOT always equal statIndex (e.g., EnergyPower uses stat=4 but attr=3).
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Computes a single stat value, 1:1 translation of DBC's {@code stat()} formula.
     *
     * <p>This replaces all three DBC config array lookups in one shot:
     * <ul>
     *   <li>{@code CONFIG_RACES_STATS_MULTI[race][class][stat]}
     *       → {@link ClassStats#getStatAttributeMultiplier}</li>
     *   <li>{@code CONFIG_RACES_STAT_BONUS[race][class][stat]}
     *       → {@link ClassStats#getStatBonus}</li>
     *   <li>{@code CONFIG_RACES_ATTRIBUTE_MULTI[race][class][attr]}
     *       → {@link ClassStats#getAttributeMultiplier}</li>
     * </ul>
     *
     * @param stats          the class-specific stat configuration for this custom race
     * @param statIndex      DBC stat index (0=Melee .. 11=FlySpeed), see {@link EnumDBCStats}
     * @param attributeIndex DBC attribute index for the final multiplier (0-5), or -1 to skip
     * @param attributeValue the MODIFIED attribute value (output of getPlayerAttribute)
     * @param skillBonus     skill bonus multiplier (e.g., SklLvl_KiBs for EnergyPool)
     * @return the computed stat value, mirroring what DBC's stat() would return
     */
    public static int computeStat(ClassStats stats, int statIndex, int attributeIndex, int attributeValue,
                                  float skillBonus) {
        // Clamp statIndex to valid enum range (mirrors DBC's bounds check:
        //   stat = attributes.length > stat ? stat : attributes.length - 1)
        EnumDBCStats[] allStats = EnumDBCStats.values();
        if (statIndex < 0)
            statIndex = 0;
        if (statIndex >= allStats.length)
            statIndex = allStats.length - 1;
        EnumDBCStats statEnum = allStats[statIndex];

        // Step 1: stat-attribute multiplier (mirrors CONFIG_RACES_STATS_MULTI[race][class][stat])
        //   statAttMulti = statAttributeMultiplier * attributeValue
        //   e.g., for Saiyan MA Melee: statAttMulti = 2.5 * modified_STR
        double statAttMulti = stats.getStatAttributeMultiplier(statEnum) * attributeValue;

        // Step 2: stat bonus (mirrors CONFIG_RACES_STAT_BONUS[race][class][stat])
        //   In DBC for powerType=1, the entire bonus (race+class combined) is stored in
        //   the config array. ClassStats.statBonuses follows the same convention — the
        //   combined race+class bonus. The class-only bonus (classBonusOnly=true) always
        //   returns 0 for DBC power type, since it's already baked in.
        double statBonus = stats.getStatBonus(statEnum);

        if(statEnum == EnumDBCStats.ENERGY_POOL){
           // statAttMulti = 5;
            //statBonus = 2;
            int x = 20;

        }
        // Step 3: combine (mirrors DBC's round(...) call)
        //   value = statAttMulti + (statBonus * 0.01 * statAttMulti) + (classBonus * 0.01 * statAttMulti) + (statAttMulti * skillBonus)
        //   For powerType=1, classBonus is always 0 (baked into statBonus), so we omit it.
        int value = (int) Math.round(statAttMulti + statBonus * 0.01 * statAttMulti + statAttMulti * skillBonus);

        // Step 4: attribute multiplier (mirrors CONFIG_RACES_ATTRIBUTE_MULTI[race][class][attr])
        //   Applied as a final multiplier on the entire stat value.
        //   attributeIndex may differ from statIndex — e.g., EnergyPower (stat=4) uses WIL (attr=3).
        //   If attributeIndex is -1 (no bonus lookup), this step is skipped entirely.
        if (attributeIndex >= 0 && attributeIndex < EnumDBCAttributes.values().length) {
            double attrMulti = stats.getAttributeMultiplier(EnumDBCAttributes.values()[attributeIndex]);
            value = (int) (value * attrMulti);
        }

        return value;
    }

    /**
     * Returns the initial (starting) attribute value for character creation.
     *
     * <p>Maps to {@code CONFIG_RACES_ATTRIBUTE_START[race][class][attr]}, read by
     * {@code JRMCoreH.attributeStart()} during character creation and stat resets.
     *
     * @param stats          the class-specific stat configuration for this custom race
     * @param attributeIndex DBC attribute index (0=STR .. 5=SPI)
     * @return the configured starting attribute value
     */
    public static int getInitialAttribute(ClassStats stats, int attributeIndex) {
        // Clamp to valid attribute range
        EnumDBCAttributes[] allAttrs = EnumDBCAttributes.values();
        if (attributeIndex < 0)
            attributeIndex = 0;
        if (attributeIndex >= allAttrs.length)
            attributeIndex = allAttrs.length - 1;

        return stats.getInitialAttribute(allAttrs[attributeIndex]);
    }

    // ────────────────────────────────────────────────────────────────────────
    // getPlayerAttribute parity for custom races
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Intermediate result from {@link #computePlayerAttribute}: the attribute
     * value after the inner getter and GoD/UI multipliers but before Kaioken,
     * Majin, and Legendary modifiers. Used by the Divine post-processing step.
     */
    public static double preKaiokenResult;

    /**
     * Inner race getter for custom races. 1:1 parity with
     * {@code getAttributeHuman}/{@code getAttributeSaiyan}/etc.
     * Uses {@link RaceAttributeConfig} instead of DBC's per-race arrays.
     */
    public static int computeInnerAttribute(
            RaceAttributeConfig config,
            EntityPlayer player, int[] curAtr, int atr, int st, int skl,
            boolean mystic, int mysticLvl, boolean isFused,
            boolean ultraInstinct, int powerType, boolean GoD
    ) {
        boolean isPowerTypeKi = powerType == 1;

        // DISABLE BASE FORM MASTERY MULTI FOR NOW
        double formMasteryMulti = !ultraInstinct && !GoD
                ? 1 //JRMCoreH.getFormMasteryAttributeMulti(player, st, 0, 0, false, mystic, ultraInstinct, GoD)
                : 1.0;

        boolean inMystic = mysticLvl > 0 && mystic;

        float percentMulti = isPowerTypeKi
                ? (inMystic ? config.getMysticFormMulti(atr) : config.getBaseFormMulti(atr))
                : 1.0f;
        int flatBonusVal = isPowerTypeKi
                ? (inMystic ? config.getMysticFormFlatBonus(atr) : config.getBaseFormFlatBonus(atr))
                : 0;

        double stateBonusPercentage = percentMulti * formMasteryMulti;
        double stateBonusFlat = flatBonusVal;

        // GoD / UI base form ignore — custom races are always in base form (st=0)
        if (GoD && JGConfigDBCGoD.CONFIG_GOD_IGNORE_BASE_CONFIG) {
            stateBonusPercentage = 1.0;
        } else if (ultraInstinct && JGConfigUltraInstinct.CONFIG_UI_IGNORE_BASE_CONFIG) {
            stateBonusPercentage = 1.0;
        } else if (inMystic && config.mysticDamMulti != -1.0f) {
            stateBonusPercentage = 1.0 * formMasteryMulti;
        }

        int atrLimit = JRMCoreH.checkLimit();
        atrLimit = atrLimit > 1000 ? 1000 : atrLimit;

        double secondaryMulti;
        if (isPowerTypeKi) {
            if (inMystic) {
                float racialSkillBonus = config.mysticAttrBonusPerSkillLevel * (float) skl;
                if (config.mysticDamMulti == -1.0f) {
                    if (config.mysticFormula == RaceAttributeConfig.MysticFormula.PERCENT_MULTI_TIMES_SKILL) {
                        secondaryMulti = percentMulti * (1.0f + racialSkillBonus);
                    } else {
                        secondaryMulti = 1.0f + racialSkillBonus;
                    }
                } else {
                    secondaryMulti = config.mysticDamMulti + racialSkillBonus;
                }
            } else {
                secondaryMulti = 1.0 + config.attrBonusPerSkillLevel * (float) skl;
            }
        } else {
            secondaryMulti = 1.0;
        }

        int per = (int) (stateBonusPercentage * curAtr[atr] * secondaryMulti);

        int flt;
        if (isPowerTypeKi && inMystic) {
            flt = (int) (curAtr[atr] + flatBonusVal
                    * (1.0f + config.mysticAttrBonusPerSkillLevel * (float) skl)
                    * atrLimit * 0.001f);
        } else {
            flt = (int) (curAtr[atr] + stateBonusFlat * atrLimit * 0.001);
        }

        flt = flt > JRMCoreH.checkLimit() ? JRMCoreH.checkLimit() : flt;

        return per <= flt && stateBonusFlat != 0.0 && atr != 4 ? flt : per;
    }

    /**
     * Full {@code getPlayerAttribute} for custom races without a custom form active.
     * 1:1 parity with DBC's outer {@code getPlayerAttribute} logic, using
     * {@link RaceAttributeConfig} for per-race values and {@code JRMCoreH} calls
     * for global configs.
     *
     * <p>Does NOT include: addon bonuses, Divine effect, or attribute limit.
     * Those are applied by the caller (MixinJRMCoreH).
     *
     * <p>Sets {@link #preKaiokenResult} for the Divine post-processing step.
     */
    public static int computePlayerAttribute(
            RaceAttributeConfig config,
            EntityPlayer player, int[] currAttributes, int attribute, int st, int st2,
            String SklX, int currRelease, int arcRel,
            boolean legendOn, boolean majinOn, boolean kaiokenOn, boolean mysticOn,
            boolean uiOn, boolean GoDOn, int powerType, String[] Skls, boolean isFused
    ) {
        preKaiokenResult = 0;
        int skillX = powerType == 1 ? JRMCoreH.SklLvlX(1, SklX) - 1 : 0;
        int mysticLvl = powerType == 1 ? JRMCoreH.SklLvl(10, 1, Skls) : 0;

        double result = computeInnerAttribute(
                config, player, currAttributes, attribute, st, skillX,
                mysticOn, mysticLvl, isFused, uiOn, powerType, GoDOn
        );

        // GoD multiplier
        if (powerType == 1) {
            if (GoDOn) {
                double formMasteryMulti = JRMCoreH.getFormMasteryAttributeMulti(
                        player, "GodOfDestruction", st, st2, 0, kaiokenOn, mysticOn, uiOn, GoDOn);
                result = (int) ( result
                        * JGConfigDBCGoD.CONFIG_GOD_ATTRIBUTE_MULTI * config.godAttrMultiRace * formMasteryMulti);
            }

            // UI multiplier
            if (uiOn) {
                if (JGConfigUltraInstinct.CONFIG_UI_LEVELS > 0) {
                    double formMasteryMulti = JRMCoreH.getFormMasteryAttributeMulti(
                            player, "UltraInstict", st, st2, 0, kaiokenOn, mysticOn, uiOn, GoDOn);
                    int uiLevel = JRMCoreH.state2UltraInstinct(false, (byte) st2);
                    float uiRaceMulti = config.getUiAttrMultiRace(uiLevel);
                    result = (int) (result
                            * JGConfigUltraInstinct.CONFIG_UI_ATTRIBUTE_MULTI[uiLevel]
                            * 0.01F * uiRaceMulti
                            * 1.0 * formMasteryMulti);
                }
            } else if (st2 < JRMCoreH.TransKaiDmg.length) {
                double formMasteryMulti = 1.0;
                if (st2 > 0 && kaiokenOn) {
                    formMasteryMulti = JRMCoreH.getFormMasteryAttributeMulti(
                            player, "Kaioken", st, st2, 0, kaiokenOn, mysticOn, uiOn, GoDOn);
                }

                preKaiokenResult = result;

                boolean legendaryApplies = config.legendaryAppliesInBase || mysticOn;

                result = (int) (
                        result * JRMCoreH.TransKaiDmg[st2] * 1.0 * formMasteryMulti
                                + (majinOn ? (result * JRMCoreConfig.mjn) * 0.01F : 0.0F)
                                + (!legendOn || !legendaryApplies ? 0.0F : (result * JRMCoreConfig.lgnd) * 0.01F)
                );
            }
        }

        if (preKaiokenResult == 0) {
            preKaiokenResult = result;
        }

        return (int) (result > Double.MAX_VALUE ? Double.MAX_VALUE : result);
    }
}
