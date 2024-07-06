package kamkeel.npcdbc.constants;

import java.util.HashMap;

public class DBCForm {
    public static final int Base = 0;

    ////////////////////////////////
    ////////////////////////////////
    // Human forms
    public static final int HumanFullRelease = 1;
    public static final int HumanBuffed = 2;
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
    public static final int NamekFullRelease = 1;
    public static final int NamekGiant = 2;
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


    public static boolean isMonke(int race, int state) {
        return DBCRace.isSaiyan(race) && (state == GreatApe || state == SuperGreatApe);
    }

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

    public static HashMap<Integer, String> getFormsMap(int race) {
        HashMap<Integer, String> forms = new HashMap<>();
        forms.put(Base, "Base");
        if (race == DBCRace.HUMAN) {
            forms.put(HumanBuffed, "HumanBuffed");
            forms.put(HumanFullRelease, "HumanFullRelease");
            forms.put(HumanGod, "HumanGod");
        } else if (race == DBCRace.SAIYAN || race == DBCRace.HALFSAIYAN) {
            forms.put(SuperSaiyan, "SuperSaiyan");
            forms.put(SuperSaiyanG2, "SuperSaiyanG2");
            forms.put(SuperSaiyanG3, "SuperSaiyanG3");
            forms.put(MasteredSuperSaiyan, "MasteredSuperSaiyan");
            forms.put(SuperSaiyan2, "SuperSaiyan2");
            forms.put(SuperSaiyan3, "SuperSaiyan3");
            forms.put(SuperSaiyan4, "SuperSaiyan4");
            forms.put(GreatApe, "GreatApe");
            forms.put(SuperGreatApe, "SuperGreatApe");
            forms.put(SuperSaiyanGod, "SuperSaiyanGod");
            forms.put(SuperSaiyanBlue, "SuperSaiyanBlue");
            forms.put(BlueEvo, "BlueEvo");
        } else if (race == DBCRace.NAMEKIAN) {
            forms.put(NamekGiant, "NamekGiant");
            forms.put(NamekFullRelease, "NamekFullRelease");
            forms.put(NamekGod, "NamekGod");
        } else if (race == DBCRace.ARCOSIAN) {
            forms.put(Minimal, "Minimal");
            forms.put(FirstForm, "FirstForm");
            forms.put(SecondForm, "SecondForm");
            forms.put(ThirdForm, "ThirdForm");
            forms.put(FinalForm, "FinalForm");
            forms.put(SuperForm, "SuperForm");
            forms.put(UltimateForm, "UltimateForm");
            forms.put(ArcoGod, "ArcoGod");
        } else if (race == DBCRace.MAJIN) {
            forms.put(MajinEvil, "MajinEvil");
            forms.put(MajinFullPower, "MajinFullPower");
            forms.put(MajinPure, "MajinPure");
            forms.put(MajinGod, "MajinGod");
        }
        return forms;
    }

}
