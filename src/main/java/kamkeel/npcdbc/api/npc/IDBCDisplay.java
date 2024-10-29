package kamkeel.npcdbc.api.npc;

import kamkeel.npcdbc.api.aura.IAura;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes2D;

public interface IDBCDisplay {
    void setColor(String type, int color);

    int getColor(String type);

    boolean isEnabled();

    void setEnabled(boolean enabled);

    EnumAuraTypes2D getFormAuraTypes();

    void setFormAuraTypes(String type);

    String getHairCode();

    void setHairCode(String hairCode);

    /**
     * @param arm 0 is right arm, 1 is left arm
     * @return Ki Weapon Data for the given arm.
     */
    IKiWeaponData getKiWeapon(int arm);

    byte getRace();

    void setRace(byte race);

    int setBodyType();

    void getBodyType(int bodyType);

    byte getTailState();

    void setTailState(byte tail);

    /**
     * @param type Legal types: "base", "raditz", "ssj", "ssj2", "ssj3", "ssj4", "oozaru", "" for no type
     *             Only saiyans and half-saiyans are eligible for ssj4 and oozaru
     */
    void setHairType(String type);

    String getHairType();

    boolean hasCoolerMask();

    void hasCoolerMask(boolean has);

    boolean hasEyebrows();

    void hasEyebrows(boolean has);

    boolean hasAura();

    IAura getAura();

    void setAura(IAura aura);

    void setAura(int auraID);

    boolean isAuraToggled();

    void toggleAura(boolean toggle);

    boolean isInAura(IAura aura);

    /**
     * @param id Transforms to the form with this ID
     */
    void transform(int id);

    /**
     * @param form Transforms to this form
     */
    void transform(IForm form);

    /**
     * if NPC is currently undergoing transformation, cancel it
     */
    void cancelTransformation();

    /**
     * @param id descends from current form to the form with this ID
     *           Set ID to -1 if to base
     */
    void descend(int id);

    /**
     * @param form descends from current form to this form
     *             set form to null for descending to base
     */
    void descend(IForm form);
}
