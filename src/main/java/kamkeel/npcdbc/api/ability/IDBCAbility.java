package kamkeel.npcdbc.api.ability;

import kamkeel.npcdbc.api.npc.IDBCStats;

/**
 * DBC-specific settings for abilities.
 * Extends {@link IDBCStats} for universal damage configuration
 * and adds player resource cost/drain and damage scaling fields.
 */
public interface IDBCAbility extends IDBCStats {

    // ==================== PLAYER RESOURCE COSTS ====================

    /** @return Ki cost deducted when the ability starts. */
    int getKiCost();

    /** @param kiCost Ki cost on ability start. */
    void setKiCost(int kiCost);

    /** @return Whether the ki cost is a percentage of max ki (true) or flat (false). */
    boolean isKiCostPercent();

    /** @param percent Whether ki cost is percent-based. */
    void setKiCostPercent(boolean percent);

    /** @return Ki drained per tick during the ACTIVE phase. */
    int getKiDrain();

    /** @param kiDrain Per-tick ki drain during ACTIVE phase. */
    void setKiDrain(int kiDrain);

    /** @return Whether the ki drain is a percentage of max ki (true) or flat (false). */
    boolean isKiDrainPercent();

    /** @param percent Whether ki drain is percent-based. */
    void setKiDrainPercent(boolean percent);

    /** @return Stamina cost deducted when the ability starts. */
    int getStaminaCost();

    /** @param staminaCost Stamina cost on ability start. */
    void setStaminaCost(int staminaCost);

    /** @return Whether the stamina cost is a percentage of max stamina (true) or flat (false). */
    boolean isStaminaCostPercent();

    /** @param percent Whether stamina cost is percent-based. */
    void setStaminaCostPercent(boolean percent);

    /** @return Stamina drained per tick during the ACTIVE phase. */
    int getStaminaDrain();

    /** @param staminaDrain Per-tick stamina drain during ACTIVE phase. */
    void setStaminaDrain(int staminaDrain);

    /** @return Whether the stamina drain is a percentage of max stamina (true) or flat (false). */
    boolean isStaminaDrainPercent();

    /** @param percent Whether stamina drain is percent-based. */
    void setStaminaDrainPercent(boolean percent);

    // ==================== PLAYER DAMAGE CONFIGURATION ====================

    /** @return Player damage type ordinal (determines damage calculation formula). */
    int getPlayerDamageType();

    /** @param type Player damage type ordinal. */
    void setPlayerDamageType(int type);

    /** @return DBC attribute used for damage scaling (e.g. STR, DEX, KI). */
    int getScalingAttribute();

    /** @param attribute Scaling attribute ordinal. */
    void setScalingAttribute(int attribute);

    /** @return Multiplier applied to the scaling attribute value. */
    float getScalingMultiplier();

    /** @param multiplier Scaling multiplier. */
    void setScalingMultiplier(float multiplier);

    /** @return Flat damage added to the scaled damage output. */
    int getFlatDamage();

    /** @param damage Flat damage amount. */
    void setFlatDamage(int damage);

    /** @return Whether to use the player's own DBC combat settings instead of ability-specific ones. */
    boolean getUsePlayerSettings();

    /** @param use Whether to use player settings. */
    void setUsePlayerSettings(boolean use);

    /** @return Number of CNPC scaling sets configured for this ability. */
    int getScalingSetCount();

    /** @param count Number of scaling sets. */
    void setScalingSetCount(int count);
}
