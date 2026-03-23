package kamkeel.npcdbc.data.form;

import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.controllers.RaceController;

public class FormRace {
    public static final FormRace ALL = new FormRace(DBCRace.ALL);
    public static final FormRace ALL_SAIYANS = new FormRace(DBCRace.ALL_SAIYANS);
    public static final FormRace HUMAN = new FormRace(DBCRace.HUMAN);
    public static final FormRace SAIYAN = new FormRace(DBCRace.SAIYAN);
    public static final FormRace HALFSAIYAN = new FormRace(DBCRace.HALFSAIYAN);
    public static final FormRace NAMEKIAN = new FormRace(DBCRace.NAMEKIAN);
    public static final FormRace ARCOSIAN = new FormRace(DBCRace.ARCOSIAN);
    public static final FormRace MAJIN = new FormRace(DBCRace.MAJIN);

    private final int raceId;

    public FormRace(int raceId) {
        this.raceId = raceId;
    }

    public int getRaceId() {
        return raceId;
    }

    public boolean isEligible(int playerRace) {
        if (raceId == DBCRace.ALL)
            return true;

        if (raceId == DBCRace.ALL_SAIYANS)
            return DBCRace.isSaiyan(playerRace);

        return raceId == playerRace;
    }

    public boolean isCustomRace() {
        return RaceController.Instance.isCustomRace(raceId);
    }

    public static FormRace of(int raceId) {
        return new FormRace(raceId);
    }

    public int toNBT() {
        return raceId;
    }

    public static FormRace fromNBT(int value) {
        return new FormRace(value);
    }
}
