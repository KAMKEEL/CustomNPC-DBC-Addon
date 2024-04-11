package kamkeel.npcdbc.api;

import noppes.npcs.api.entity.IDBCPlayer;

public interface IDBCAddon extends IDBCPlayer {
    int[] getAllFullStats();

    void setFlight(boolean bo);

    int getMaxBody();

    int getMaxHP();

    float getBodyPercentage();

    int getMaxKi();

    int getMaxStamina();

    void changeDBCAnim(int i);

    int[] getAllAttributes();

    void modifyAllAttributes(int[] Stats, boolean multiplyaddedStats, double multiValue);

    void modifyAllAttributes(int Num, boolean setStatsToNum);

    void modifyAllAttributes(int[] stats, boolean setStats);

    // 0 for strength, 1 dex, 2 constitution, 3 willpower, 4 mind, 5 spirit
    void multiplyAttribute(int statid, double multi);

    void multiplyAllAttributes(double multi);

    // str 0, dex 1, con 2, wil 3, mnd 4, spi 5
    int getFullStat(int statid);

    String getRaceName(int race);

    String getRaceName();

    String getFormName(int race, int form);

    String getCurrentFormName();

    void changeFormMastery(String formName, double amount, boolean add);

    double getFormMasteryValue(String formName);

    String getAllFormMasteries();

    String[] getAllFormMasteryData(int race, int formId);

    int getAllFormsLength(int race, boolean nonRacial);

    String[] getAllForms(int race, boolean nonRacial);

    boolean isDBCFusionSpectator();

    int getSkillLevel(String skillname);

    int getMaxStat(int attribute);

    int getCurrentStat(int attribute);

    double getCurrentFormMultiplier();

    boolean isMUI();
}
