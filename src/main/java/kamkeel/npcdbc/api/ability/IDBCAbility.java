package kamkeel.npcdbc.api.ability;

import kamkeel.npcdbc.api.npc.IDBCStats;

/**
 * DBC-specific settings for abilities.
 * Extends {@link IDBCStats} for NPC damage configuration
 * and adds player resource cost/drain fields.
 */
public interface IDBCAbility extends IDBCStats {

    int getKiCost();

    void setKiCost(int kiCost);

    int getKiDrain();

    void setKiDrain(int kiDrain);

    int getStaminaCost();

    void setStaminaCost(int staminaCost);

    int getStaminaDrain();

    void setStaminaDrain(int staminaDrain);
}
