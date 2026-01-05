package kamkeel.npcdbc.api;

import kamkeel.npcdbc.api.aura.IAura;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.api.outline.IOutline;

/**
 * Primarily intended to give DBC information in client scripts,
 * as the IPlayer and subsequent interfaces do not work on the client.
 */
public interface ISimpleDBCData {

    int getRace();
    byte getRelease();
    int getBody();
    int getHP();
    int getStamina();
    int getKi();
    byte getForm();
    byte getForm2();
    int getPowerPoints();
    String getJRMCSE();
    byte getDBCClass();
    int getPowerType();
    String getFusionString();

    boolean isTurboOn();
    int getMaxHP();
    int getMaxBody();
    float getBodyPercentage();
    int getMaxKi();
    int getMaxStamina();
    int[] getAllAttributes();
    int getFullAttribute(int id);
    int[] getAllFullAttributes();
    String getRaceName();
    String getCurrentDBCFormName();

    boolean isChargingKiAttack();
    int getMaxStat(int id);
    int getCurrentStat(int id);
    int getMajinAbsorptionRace();
    int getMajinAbsorptionPower();
    boolean isKO();
    boolean isUI();
    boolean isMUI();
    boolean isMystic();
    boolean isGOD();
    boolean isLegendary();
    boolean isDivine();
    boolean isMajin();

    boolean isKaioken();

    boolean isFlying();

    boolean isInCustomForm();

    boolean isInCustomForm(int id);
    boolean isInCustomForm(IForm form);
    IForm getCurrentForm();
    IAura getAura();
    boolean isInAura();
    boolean isInAura(IAura aura);
    boolean isInAura(String auraName);
    boolean isInAura(int auraID);
    IOutline getOutline();

    boolean isReleasing();
    boolean isMeditating();
    boolean isSuperRegen();
    boolean isSwooping();
    boolean isInMedicalLiquid();

}
