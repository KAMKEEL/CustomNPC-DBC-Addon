package kamkeel.npcdbc.api.npc;

import kamkeel.npcdbc.api.aura.IAura;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.api.outline.IOutline;

public interface IDBCDisplay {

    /**
     * @param type  Legal types: hair, eye, bodycm, bodyc1, bodyc2, bodyc3, fur
     * @param color Decimal or hexadecimal color (i.e. 0xffffff for white) color to set type as
     */
    void setColor(String type, int color);

    int getColor(String type);

    boolean isEnabled();

    /**
     * @param enabled enabling this display allows everything in this interface to function
     */
    void setEnabled(boolean enabled);

    String getHairCode();

    void setHairCode(String hairCode);

    /**
     * @param arm 0 is right arm, 1 is left arm
     * @return Ki Weapon Data for the given arm.
     */
    IKiWeaponData getKiWeapon(int arm);

    byte getRace();

    /**
     * @param race 0 is Human, 1 Saiyan, 2 Half-Saiyan, 3 Namekian, 4 Arcosian, 5 Majin
     */

    void setRace(byte race);

    /**
     * Namekian/arcosian body types
     *
     * @param bodyType from 0 to 2.
     */
    void setBodyType(int bodyType);

    int getBodyType();

    byte getTailState();

    /**
     * @param tail 0 for straight, 1 for wrapped, 2 for arcosian tail (if race is arco), anything else for no tail
     */
    void setTailState(byte tail);

    /**
     * @param type Legal types: "base", "raditz", "ssj", "ssj2", "ssj3", "ssj4", "oozaru", "" for no type
     *             Only saiyans and half-saiyans are eligible for ssj4 and oozaru
     */
    void setHairType(String type);

    String getHairType();

    boolean hasCoolerMask();

    /**
     * @param has set
     */
    void setHasCoolerMask(boolean has);

    boolean hasEyebrows();

    void setHasEyebrows(boolean has);

    boolean hasBodyFur();

    void setHasBodyFur(boolean hasFur);

    boolean hasAura();

    IAura getAura();

    void setAura(IAura aura);

    void setAura(int auraID);

    boolean isAuraToggled();

    void toggleAura(boolean toggle);

    boolean isInAura(IAura aura);

    IOutline getOutline();

    void setOutline(int id);

    void setOutline(IOutline outline);

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
