package kamkeel.npcdbc.constants.enums;

public enum EnumAbilityDamageType {
    DEFAULT,
    FLAT,
    MELEE,
    KI,
    CNPC;

    public static EnumAbilityDamageType fromOrdinal(int ordinal) {
        if (ordinal >= 0 && ordinal < values().length)
            return values()[ordinal];
        return DEFAULT;
    }
}
