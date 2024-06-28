package kamkeel.npcdbc.api.npc;

public interface IDBCStats {
    void setRelease(byte release);

    byte getRelease();

    boolean isEnabled();

    void setEnabled(boolean enabled);

    boolean isFriendlyFist();

    void setFriendlyFist(boolean friendlyFist);

    int getFriendlyFistAmount();

    void setFriendlyFistAmount(int seconds);

    boolean isIgnoreDex();

    void setIgnoreDex(boolean ignoreDex);

    boolean isIgnoreBlock();

    void setIgnoreBlock(boolean ignoreBlock);

    boolean isIgnoreEndurance();

    void setIgnoreEndurance(boolean ignoreEndurance);

    boolean isIgnoreKiProtection();

    void setIgnoreKiProtection(boolean ignoreKiProtection);

    boolean isIgnoreFormReduction();

    void setIgnoreFormReduction(boolean ignoreFormReduction);

    boolean hasDefensePenetration();

    void setHasDefensePenetration(boolean hasDefensePenetration);

    int getDefensePenetration();

    void setDefensePenetration(int defensePenetration);
}
