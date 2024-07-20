package kamkeel.npcdbc.constants;

public class DBCRace {
    public static int ALL = -1;
    public static int HUMAN = 0;
    public static int ALL_SAIYANS = 12;
    public static int SAIYAN = 1;
    public static int HALFSAIYAN = 2;
    public static int NAMEKIAN = 3;
    public static int ARCOSIAN = 4;
    public static int MAJIN = 5;

    public static boolean isSaiyan(int race) {
        return race == SAIYAN || race == HALFSAIYAN || race ==  ALL_SAIYANS;
    }
}
