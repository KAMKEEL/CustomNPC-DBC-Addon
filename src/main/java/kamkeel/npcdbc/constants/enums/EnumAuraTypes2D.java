package kamkeel.npcdbc.constants.enums;

import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.data.IAuraData;

import java.util.ArrayList;
import java.util.List;

public enum EnumAuraTypes2D {
    None("none"),
    Default("default"),
    Base("base"),
    SaiyanGod("ssgod"),
    SaiyanBlue("ssb"),
    SaiyanBlueEvo("shinka"),
    SaiyanRose("ssrose"),
    SaiyanRoseEvo("ssroseevo"),
    UltimateArco("ultimate"),
    UI("ui"),
    MasteredUI("mui"),
    GoD("godofdestruction"),
    GoDToppo("godofdestructiontoppo"),
    Jiren("jiren");


    public final String name;

    EnumAuraTypes2D(String name) {
        this.name = name;
    }


    public static EnumAuraTypes2D getEnumFromName(String name) {
        for (EnumAuraTypes2D auraType : EnumAuraTypes2D.values())
            if (auraType.name.equalsIgnoreCase(name))
                return auraType;

        return null;
    }

    public static List<String> getAllNames() {
        List<String> names = new ArrayList<>();
        for (EnumAuraTypes2D auraType : EnumAuraTypes2D.values())
            names.add(auraType.name);

        return names;
    }

    public static EnumAuraTypes2D getType(IAuraData data) {
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
        return Base;
    }

    public static EnumAuraTypes2D getFrom3D(EnumAuraTypes3D type) {
        if (type == EnumAuraTypes3D.Base)
            return Default;
        else if (type == EnumAuraTypes3D.SaiyanGod)
            return SaiyanGod;
        else if (type == EnumAuraTypes3D.SaiyanBlue)
            return SaiyanBlue;
        else if (type == EnumAuraTypes3D.SaiyanBlueEvo)
            return SaiyanBlueEvo;

        else if (type == EnumAuraTypes3D.SaiyanRose)
            return SaiyanRose;
        else if (type == EnumAuraTypes3D.SaiyanRoseEvo)
            return SaiyanRoseEvo;

        else if (type == EnumAuraTypes3D.UI)
            return UI;
        else if (type == EnumAuraTypes3D.GoD)
            return GoD;
        else if (type == EnumAuraTypes3D.UltimateArco)
            return UltimateArco;

        return None;
    }

    public static int getParticleWidth(IAuraData data) {
        int race = data.getRace();
        int state = data.getState();

        if (race == DBCRace.SAIYAN || race == DBCRace.HALFSAIYAN) {
            if (state > 0 && state< DBCForm.SuperSaiyan2)
                return 7;
            if (state == DBCForm.SuperSaiyan2)
                return 10;
            else if (state == DBCForm.SuperSaiyan3)
                return 15;
            else if (state == DBCForm.SuperSaiyan4)
                return 20;
            else if (state == DBCForm.BlueEvo)
                return 5;
            else if (state == DBCForm.SuperSaiyanBlue)
                return 5;
            else if (state == DBCForm.BlueEvo)
                return 5;
        }
        return 5;
    }
}

