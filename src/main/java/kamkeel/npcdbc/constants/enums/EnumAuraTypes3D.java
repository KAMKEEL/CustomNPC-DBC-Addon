package kamkeel.npcdbc.constants.enums;

import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.data.IAuraData;

import java.util.ArrayList;
import java.util.List;

public enum EnumAuraTypes3D {
    None(""),
    Default("default"),
    SaiyanGod("ssgod"),
    SaiyanBlue("ssb"),
    SaiyanBlueEvo("shinka"),
    SaiyanRose("ssrose"),
    SaiyanRoseEvo("ssroseevo"),
    UltimateArco("ultimate"),
    UI("ui"),
    GoD("godofdestruction");

    private final String name;

    private EnumAuraTypes3D(String name) {
        this.name = name;
    }

    public static EnumAuraTypes3D getEnumFromName(String name) {
        for (EnumAuraTypes3D auraType : EnumAuraTypes3D.values()) {
            if (auraType.getName().equalsIgnoreCase(name)) {
                return auraType;
            }
        }
        return null;
    }

    public static List<String> getAllNames() {
        List<String> names = new ArrayList<>();
        for (EnumAuraTypes3D auraType : EnumAuraTypes3D.values()) {
            names.add(auraType.getName());
        }
        return names;
    }

    public static EnumAuraTypes3D getType(IAuraData data) {
        int race = data.getRace();
        int state = data.getState();
        boolean divine = data.isForm(DBCForm.Divine);
        boolean ui = data.isForm(DBCForm.UltraInstinct);
        boolean god = data.isForm(DBCForm.GodOfDestruction);

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
        return Default;
    }

    public static int getManualAuraColor(EnumAuraTypes3D playerAuraTypes, boolean revampedAura) {
        int col = -1;
        if (playerAuraTypes == EnumAuraTypes3D.SaiyanGod) {
            if (revampedAura)
                col = 0xe62727;
            else
                col = 16761125;
        } else if (playerAuraTypes == EnumAuraTypes3D.SaiyanBlue) {
            if (revampedAura)
                col = 0x48b6fa;//0x00ffff;
            else
                col = 2805230;
        } else if (playerAuraTypes == EnumAuraTypes3D.SaiyanBlueEvo) {
            if (revampedAura)
                col = 0x0c02cc;
            else
                col = 32767;
        } else if (playerAuraTypes == EnumAuraTypes3D.SaiyanRose) {
            col = 7536661;
        } else if (playerAuraTypes == EnumAuraTypes3D.SaiyanRoseEvo) {
            col = 14030412;
        } else if (playerAuraTypes == EnumAuraTypes3D.UI) {
            col = 15790320;
        } else if (playerAuraTypes == EnumAuraTypes3D.GoD) {
            col = 12464847;
        } else if (playerAuraTypes == EnumAuraTypes3D.UltimateArco) {
            col = 16430355;
        }
        return col;
    }


    public static boolean isBlue(EnumAuraTypes3D type) {
        if (type == SaiyanBlue || type == SaiyanBlueEvo || type == SaiyanRose || type == SaiyanRoseEvo)
            return true;
        return false;
    }


    public String getName() {
        return this.name;
    }

}
