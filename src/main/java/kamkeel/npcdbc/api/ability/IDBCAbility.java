package kamkeel.npcdbc.api.ability;

import kamkeel.npcdbc.api.npc.IDBCStats;

/**
 * DBC-specific settings for abilities.
 * Extends {@link IDBCStats} for universal damage configuration
 * and adds player resource cost/drain and damage scaling fields.
 */
public interface IDBCAbility extends IDBCStats {

    // Player resource costs
    int getKiCost();

    void setKiCost(int kiCost);

    boolean isKiCostPercent();

    void setKiCostPercent(boolean percent);

    int getKiDrain();

    void setKiDrain(int kiDrain);

    boolean isKiDrainPercent();

    void setKiDrainPercent(boolean percent);

    int getStaminaCost();

    void setStaminaCost(int staminaCost);

    boolean isStaminaCostPercent();

    void setStaminaCostPercent(boolean percent);

    int getStaminaDrain();

    void setStaminaDrain(int staminaDrain);

    boolean isStaminaDrainPercent();

    void setStaminaDrainPercent(boolean percent);

    // Player damage configuration
    int getPlayerDamageType();

    void setPlayerDamageType(int type);

    int getScalingAttribute();

    void setScalingAttribute(int attribute);

    float getScalingMultiplier();

    void setScalingMultiplier(float multiplier);

    int getFlatDamage();

    void setFlatDamage(int damage);

    boolean getUsePlayerSettings();

    void setUsePlayerSettings(boolean use);

    int getScalingSetCount();

    void setScalingSetCount(int count);
}
