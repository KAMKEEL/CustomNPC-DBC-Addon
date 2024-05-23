package kamkeel.npcdbc.constants.enums;

import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.DBCRace;

public enum EnumAuraTypes {
    None,
    Base,
    GoD,
    GoDToppo,
    UI,
    Golden,
    Legendary,
    SaiyanSuper,
    SaiyanGod,
    SaiyanBlue,
    SaiyanBlueEvo,
    SaiyanRose,
    SaiyanRoseEvo,
    Jiren;

    public static EnumAuraTypes getType(int race, int state, boolean divine, boolean ui, boolean god) {
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
}

