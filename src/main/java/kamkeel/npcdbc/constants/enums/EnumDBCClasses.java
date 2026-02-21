package kamkeel.npcdbc.constants.enums;

public enum EnumDBCClasses {
    MARTIAL_ARTIST, SPIRITUALIST, WARRIOR;

    @Override
    public String toString() {
        switch (this) {
            case MARTIAL_ARTIST:
                return "condition.class.martial_artist";
            case SPIRITUALIST:
                return "condition.class.spiritualist";
            case WARRIOR:
                return "condition.class.warrior";
            default:
                return name();
        }
    }

    public static EnumDBCClasses fromOrdinal(int ordinal) {
        EnumDBCClasses[] values = values();
        return (ordinal >= 0 && ordinal < values.length) ? values[ordinal] : MARTIAL_ARTIST;
    }
}
