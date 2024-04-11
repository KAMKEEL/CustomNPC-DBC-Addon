package kamkeel.npcdbc.api;

public interface IDBCStats {
    boolean isEnabled();

    void setEnabled(boolean enabled);

    boolean isFriendlyFist();

    void setFriendlyFist(boolean friendlyFist);

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
