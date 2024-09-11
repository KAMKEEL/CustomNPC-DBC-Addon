package kamkeel.npcdbc.constants;

import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.server.config.dbc.JGConfigUltraInstinct;
import kamkeel.npcdbc.util.DBCUtils;
import noppes.npcs.util.ValueUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;

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
    public static final int Mystic = 21;
    public static final int GodOfDestruction = 24;
    public static final int Legendary = 25;
    public static final int Divine = 26;
    public static final int Majin = 27;

    public static final int Kaioken = 31;
    public static final int Kaioken2 = 32;
    public static final int Kaioken3 = 33;
    public static final int Kaioken4 = 34;
    public static final int Kaioken5 = 35;
    public static final int Kaioken6 = 36;

    public static final int MasteredUltraInstinct = 40;
    public static final int UltraInstinct = 41;


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

    public static String getMenuName(int race, int form, boolean isDivine) {
        String name = null;
        if (race == DBCRace.HUMAN) {
            if (form == HumanBuffed)
                name = "§3Buffed";
            else if (form == HumanFullRelease)
                name = "§4Full Release";
            else if (form == HumanGod)
                name = "§cGod";
        } else if (race == DBCRace.SAIYAN || race == DBCRace.HALFSAIYAN) {
            if (form == SuperSaiyan)
                name = "§eSuper Saiyan";
            else if (form == SuperSaiyanG2)
                name = "§eSuper Saiyan G2";
            else if (form == SuperSaiyanG3)
                name = "§eSuper Saiyan G3";
            else if (form == MasteredSuperSaiyan)
                name = "§eSuper Saiyan (FP)";
            else if (form == SuperSaiyan2)
                name = "§eSuper Saiyan 2";
            else if (form == SuperSaiyan3)
                name = "§eSuper Saiyan 3";
            else if (form == SuperSaiyan4)
                name = "§4Super Saiyan 4";
            else if (form == GreatApe)
                name = "§6Great Ape";
            else if (form == SuperGreatApe)
                name = "§6Super Great Ape";
            else if (form == SuperSaiyanGod)
                name = "§cSuper Saiyan God";
            else if (form == SuperSaiyanBlue)
                name = !isDivine ? "§bSuper Saiyan Blue" :  "§5Super Saiyan Rosé";
            else if (form == BlueEvo)
                name = !isDivine ? "§1Super Saiyan Blue Evo" : "§dSuper Saiyan Rosé Evo";


        } else if (race == DBCRace.NAMEKIAN) {
            if (form == NamekGiant)
                name = "§2Giant";
            else if (form == NamekFullRelease)
                name = "§aFull Release";
            else if (form == NamekGod)
                name = "§cGod";
        } else if (race == DBCRace.ARCOSIAN) {
            if (form == Minimal)
                name = "§5Minimal";
            else if (form == FirstForm)
                name = "§5First Form";
            else if (form == SecondForm)
                name = "§5Second Form";
            else if (form == ThirdForm)
                name = "§5Third Form";
            else if (form == FinalForm)
                name = "§5Final Form";
            else if (form == SuperForm)
                name = "§5Super Form";
            else if (form == UltimateForm)
                name = "§6Ultimate Form";
            else if (form == ArcoGod)
                name = "§cGod";
        } else if (race == DBCRace.MAJIN) {
            if (form == MajinEvil)
                name = "§8Evil";
            else if (form == MajinFullPower)
                name = "§5Full Power";
            else if (form == MajinPure)
                name = "§dPure";
            else if (form == MajinGod)
                name = "§cGod";

        }

        if (form == Mystic)
            name = "Mystic";

        for (int i = 0; i < 6; i++) {
            if (form == Kaioken + i) {
                name = "§cKaioken " + JRMCoreH.TransKaiNms[i + 1];
                break;
            }
        }

        for (int i = 0; i < JGConfigUltraInstinct.CONFIG_UI_LEVELS; i++) {
            if (form == UltraInstinct + i) {
                name = "§7" + DBCUtils.CONFIG_UI_NAME[i];
                break;
            }
        }

        if (form == GodOfDestruction)
            name = "§dGod of Destruction";

        return name;
    }

    public static HashMap<Integer, String> getFormsMap(int race) {
        HashMap<Integer, String> forms = new LinkedHashMap<>();
        forms.put(Base, "§3Base");
        if (race == DBCRace.HUMAN) {
            forms.put(HumanBuffed, "§3Buffed");
            forms.put(HumanFullRelease, "§4Full Release");
            forms.put(HumanGod, "§cGod");
        } else if (race == DBCRace.SAIYAN || race == DBCRace.HALFSAIYAN) {
            forms.put(SuperSaiyan, "§eSuper Saiyan");
            forms.put(SuperSaiyanG2, "§eSuper Saiyan G2");
            forms.put(SuperSaiyanG3, "§eSuper Saiyan G3");
            forms.put(MasteredSuperSaiyan, "§eMastered Super Saiyan");
            forms.put(SuperSaiyan2, "§eSuper Saiyan 2");
            forms.put(SuperSaiyan3, "§eSuper Saiyan 3");
            forms.put(SuperSaiyan4, "§4Super Saiyan 4");
            forms.put(GreatApe, "§6Great Ape");
            forms.put(SuperGreatApe, "§6Super Great Ape");
            forms.put(SuperSaiyanGod, "§cSuper Saiyan God");
            forms.put(SuperSaiyanBlue, "§bSuper Saiyan Blue");
            forms.put(BlueEvo, "§1Super Saiyan Blue Evo");
        } else if (race == DBCRace.NAMEKIAN) {
            forms.put(NamekGiant, "§2Giant");
            forms.put(NamekFullRelease, "§aFull Release");
            forms.put(NamekGod, "§cGod");
        } else if (race == DBCRace.ARCOSIAN) {
            forms.put(Minimal, "§5Minimal");
            forms.put(FirstForm, "§5First Form");
            forms.put(SecondForm, "§5Second Form");
            forms.put(ThirdForm, "§5Third Form");
            forms.put(FinalForm, "§5Final Form");
            forms.put(SuperForm, "§5Super Form");
            forms.put(UltimateForm, "§6Ultimate Form");
            forms.put(ArcoGod, "§cGod");
        } else if (race == DBCRace.MAJIN) {
            forms.put(MajinEvil, "§8Evil");
            forms.put(MajinFullPower, "§5Full Power");
            forms.put(MajinPure, "§dPure");
            forms.put(MajinGod, "§cGod");
        }
        return forms;
    }


    public static String getJRMCName(int formID, int race) {
        if(JRMCoreH.trans.length > race && formID < JRMCoreH.trans[race].length)
            return JRMCoreH.trans[race][formID];

        switch(formID){
            case Kaioken:
            case Kaioken2:
            case Kaioken3:
            case Kaioken4:
            case Kaioken5:
            case Kaioken6:
                return JRMCoreH.transNonRacial[0];
            case Mystic:
                return JRMCoreH.transNonRacial[1];
            case UltraInstinct:
            case MasteredUltraInstinct:
                return JRMCoreH.transNonRacial[2];
            case GodOfDestruction:
                return JRMCoreH.transNonRacial[3];
            default:
                return null;
        }
    }

    public static int getJRMCFormID(int formID, int race){

        boolean isMysticOn = formID == Mystic;
        boolean isGoDOn = formID == GodOfDestruction;
        boolean isKaiokenOn = formID >= Kaioken && formID <= Kaioken6;
        boolean isUltraInstinctOn = formID == MasteredUltraInstinct || formID == UltraInstinct;
        int currentFormID;

        if (isMysticOn) {
            currentFormID = JRMCoreH.trans[race].length + 1;
        } else if (isGoDOn) {
            currentFormID = JRMCoreH.trans[race].length + 3;
        } else if (isKaiokenOn) {
            currentFormID = JRMCoreH.trans[race].length;
        } else if (isUltraInstinctOn) {
            currentFormID = JRMCoreH.trans[race].length + 2;
        } else {
            if(formID < 0 || formID > JRMCoreH.trans[race].length-1)
                formID = -1;
            currentFormID = formID;
        }

        return currentFormID;
    }

    public static double getJRMCMaxFormLevel(int formID, int race) {
        int properFormID = DBCForm.getJRMCFormID(formID, race);
        if(properFormID < 0)
            return -1;
        return DBCUtils.getMaxFormMasteryLvl(properFormID, race);
    }
}
