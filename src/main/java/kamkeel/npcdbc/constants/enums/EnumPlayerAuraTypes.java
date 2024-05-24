package kamkeel.npcdbc.constants.enums;

import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.DBCRace;

import java.util.ArrayList;
import java.util.List;

public enum EnumPlayerAuraTypes {
    None(""),
    SaiyanGod("ssgod"),
    SaiyanBlue("ssb"),
    SaiyanBlueEvo("shinka"),
    SaiyanRose("ssrose"),
    SaiyanRoseEvo("ssroseevo"),
    UltimateArco("ultimate"),
    UI("ui"),
    GoD("godofdestruction");

    private final String name;

    private EnumPlayerAuraTypes(String name) {
        this.name = name;
    }

    public static EnumPlayerAuraTypes getEnumFromName(String name) {
        for (EnumPlayerAuraTypes auraType : EnumPlayerAuraTypes.values()) {
            if (auraType.getName().equalsIgnoreCase(name)) {
                return auraType;
            }
        }
        return null;
    }

    public static List<String> getAllNames() {
        List<String> names = new ArrayList<>();
        for (EnumPlayerAuraTypes auraType : EnumPlayerAuraTypes.values()) {
            names.add(auraType.getName());
        }
        return names;
    }

    public static EnumPlayerAuraTypes getType(int race, int state, boolean divine, boolean ui, boolean god) {
        if (ui)
            return UI;
        else if (god)
            return GoD;
        else if (DBCForm.isGod(race, state))
            return SaiyanGod;

        if (race == DBCRace.SAIYAN || race == DBCRace.HALFSAIYAN) {
            if (state == DBCForm.SuperSaiyanBlue)
                if (divine)
                    return SaiyanRose;
                else
                    return SaiyanBlue;
            else if (state == DBCForm.BlueEvo)
                if (divine)
                    return SaiyanRoseEvo;
                else
                    return SaiyanBlueEvo;

        }
        return None;
    }

    public static int getManualAuraColor(EnumPlayerAuraTypes playerAuraTypes) {
        int clr = -1;
        if (playerAuraTypes == EnumPlayerAuraTypes.GoD) {
            clr = 12464847;
        } else if (playerAuraTypes == EnumPlayerAuraTypes.UI) {
            clr = 15790320;
        } else if (playerAuraTypes == EnumPlayerAuraTypes.SaiyanRose) {
            clr = 7536661;
        } else if (playerAuraTypes == EnumPlayerAuraTypes.SaiyanRoseEvo) {
            clr = 14030412;
        } else if (playerAuraTypes == EnumPlayerAuraTypes.SaiyanBlue) {
            clr = 2805230;
        } else if (playerAuraTypes == EnumPlayerAuraTypes.SaiyanBlueEvo) {
            clr = 32767;
        } else if (playerAuraTypes == EnumPlayerAuraTypes.SaiyanGod) {
            clr = 16761125;
        } else if (playerAuraTypes == EnumPlayerAuraTypes.UltimateArco) {
            clr = 16430355;
        }
        return clr;
    }

    public static int getStateColor(int race, int state, boolean divine) {

        if (DBCForm.isGod(race, state))
            return 0xe62727;

        if (race == DBCRace.SAIYAN || race == DBCRace.HALFSAIYAN) {
            if (state == DBCForm.SuperSaiyanBlue)
                if (divine)
                    return 0;
                else
                    return 0x00ffff;
            else if (state == DBCForm.BlueEvo)
                if (divine)
                    return 0;
                else
                    return 0x1315ba;

        }
        return -1;
    }

    public static boolean isBlue(EnumPlayerAuraTypes type) {
        if (type == SaiyanBlue || type == SaiyanBlueEvo || type == SaiyanRose || type == SaiyanRoseEvo)
            return true;
        return false;
    }


    public String getName() {
        return this.name;
    }

}
