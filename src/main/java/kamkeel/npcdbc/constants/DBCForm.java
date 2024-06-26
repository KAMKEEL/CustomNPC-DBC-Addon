package kamkeel.npcdbc.constants;

public class DBCForm {
    public static final int Base = 0;

    ////////////////////////////////
    ////////////////////////////////
    // Human forms
    public static final int HumanBuffed = 1;
    public static final int HumanFullRelease = 2;
    public static final int HumanGod = 3;


    ////////////////////////////////
    ////////////////////////////////
    // Saiyan forms
    public static final int SuperSaiyan = 1;
    public static final int SuperSaiyanG2 = 2; //ssj grade 2
    public static final int SuperSaiyanG3 = 3;
    public static final int MasteredSuperSaiyan = 4;
    public static final int SuperSaiyan2 = 5;
    public static final int SuperSaiyan3 = 6;
    public static final int SuperSaiyan4 = 14;
    public static final int GreatApe = 7;
    public static final int SuperGreatApe = 8;
    public static final int SuperSaiyanGod = 9;
    public static final int SuperSaiyanBlue = 10;
    public static final int BlueEvo = 15; // SSJ Blue Evolved


    ////////////////////////////////
    ////////////////////////////////
    // Namekian forms
    public static final int NamekGiant = 1;
    public static final int NamekFullRelease = 2;
    public static final int NamekGod = 3;

    ////////////////////////////////
    ////////////////////////////////
    // Arco forms
    public static final int Minimal = 0;
    public static final int FirstForm = 1;
    public static final int SecondForm = 2;
    public static final int ThirdForm = 3;
    public static final int FinalForm = 4;
    public static final int SuperForm = 5; //Ultimate Cooler
    public static final int UltimateForm = 6; //Golden Frieza
    public static final int ArcoGod = 7;

    ////////////////////////////////
    ////////////////////////////////
    // Majin forms
    public static final int MajinEvil = 1;
    public static final int MajinFullPower = 2;
    public static final int MajinPure = 3;
    public static final int MajinGod = 4;

    ////////////////////////////////
    ////////////////////////////////
    // Non-Racial forms
    public static final int Kaioken = 20;
    public static final int Mystic = 21;
    public static final int UltraInstinct = 22;
    public static final int MasteredUltraInstinct = 23;
    public static final int GodOfDestruction = 24;
    public static final int Legendary = 25;
    public static final int Divine = 26;
    public static final int Majin = 27;


    public static boolean isSaiyanGod(int state) {
        return state == SuperSaiyanGod || state == SuperSaiyanBlue || state == BlueEvo;
    }

    public static boolean isGod(int race, int state) {
        if (race == DBCRace.HUMAN) {
            if (state == HumanGod)
                return true;
        } else if (race == DBCRace.SAIYAN || race == DBCRace.HALFSAIYAN) {
            if (state == SuperSaiyanGod)
                return true;
        } else if (race == DBCRace.NAMEKIAN) {
            if (state == NamekGod)
                return true;
        } else if (race == DBCRace.ARCOSIAN) {
            if (state == ArcoGod)
                return true;
        } else if (race == DBCRace.MAJIN) {
            if (state == MajinGod)
                return true;
        }
        return false;
    }
}
