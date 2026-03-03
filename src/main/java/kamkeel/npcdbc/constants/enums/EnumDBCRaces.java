package kamkeel.npcdbc.constants.enums;

public enum EnumDBCRaces {
    HUMAN, SAIYAN, HALFSAIYAN, NAMEKIAN, ARCOSIAN, MAJIN;

    @Override
    public String toString() {
        switch (this) {
            case HUMAN:
                return "display.human";
            case SAIYAN:
                return "display.saiyan";
            case HALFSAIYAN:
                return "display.halfsaiyan";
            case NAMEKIAN:
                return "display.namekian";
            case ARCOSIAN:
                return "display.arcosian";
            case MAJIN:
                return "display.majin";
            default:
                return name();
        }
    }

    public static EnumDBCRaces fromOrdinal(int ordinal) {
        EnumDBCRaces[] values = values();
        return (ordinal >= 0 && ordinal < values.length) ? values[ordinal] : HUMAN;
    }
}
