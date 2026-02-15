package kamkeel.npcdbc.constants.enums;

public enum AbilityDamageType {
    DEFAULT,
    FLAT,
    MELEE,
    KI,
    CNPC;

    public static AbilityDamageType fromOrdinal(int ordinal) {
        if (ordinal >= 0 && ordinal < values().length)
            return values()[ordinal];
        return DEFAULT;
    }
}
