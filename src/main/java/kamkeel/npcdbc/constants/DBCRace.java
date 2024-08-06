package kamkeel.npcdbc.constants;

public class DBCRace {
    public static final int ALL = -1;
    public static final int HUMAN = 0;
    public static final int ALL_SAIYANS = 12;
    public static final int SAIYAN = 1;
    public static final int HALFSAIYAN = 2;
    public static final int NAMEKIAN = 3;
    public static final int ARCOSIAN = 4;
    public static final int MAJIN = 5;

    public static boolean isSaiyan(int race) {
        return race == SAIYAN || race == HALFSAIYAN || race == ALL_SAIYANS;
    }
}
