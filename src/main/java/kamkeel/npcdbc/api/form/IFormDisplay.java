package kamkeel.npcdbc.api.form;

import kamkeel.npcdbc.api.aura.IAura;
import kamkeel.npcdbc.api.outline.IOutline;

/**
 * This interface is heavily based on how DBC calculates its form masteries. Please check any race's form_mastery.cfg config to
 * get a better understanding on how this interface functions
 */
public interface IFormDisplay {


    String getHairCode();

    /**
     * @param hairCode The hair code to set transformation's hair to, usually gotten from the JinGames Hair Salon,
     *                 "bald" for no hair
     */
    void setHairCode(String hairCode);



    /**
     * @param type Legal types: hud, aura, hair, eye, bodycm, bodyc1, bodyc2, bodyc3
     * @return Decimal color of type
     */
    int getColor(String type);

    /**
     * @param type  Legal types: hud, aura, hair, eye, bodycm, bodyc1, bodyc2, bodyc3, fur
     * @param color Decimal or hexadecimal color (i.e. 0xffffff for white) to set type as
     */
    void setColor(String type, int color);

    /**
     * @param type Legal types: "base", "raditz", "ssj", "ssj2", "ssj3", "ssj4", "oozaru", "" for no type
     *             Only saiyans and half-saiyans are eligible for ssj4 and oozaru
     */
    void setHairType(String type);


    String getHairType();


    /**
     * @param type Legal types: hud, aura, hair, eye, bodycm, body1, body2, body3
     * @return True if form has the "hasTypeColor" field to true,
     * useful for Namekians and Arcosians multi body color setting
     */
    boolean hasColor(String type);


    boolean isBerserk();

    void setBerserk(boolean isBerserk);

    boolean hasEyebrows();

    void hasEyebrows(boolean has);

    /**
     * @return form's size, default is 1.0f of player's current size
     */
    float getSize();

    /**
     * @param size size to set form to. 2.0f sets the player to 2x their normal size. Min: 0.2, Max: 3.0
     */
    void setSize(float size);

    boolean getKeepOriginalSize();

    /**
     * @param keepOriginalSize True if you want CustomForm to maintain vanilla DBC size when stacking forms.
     *                         <p>
     *                         i.e if Giant form has a size of 3.0x normal minecraft steve size, enabling this
     *                         and setting formSize to 2.0 will cause the effective size to be 6.0x steve size.
     *                         Disabling it overrides vanilla DBC sizes this way a formSize of 2.0 will always be 2.0x steve size
     */
    void setKeepOriginalSize(boolean keepOriginalSize);

    /**
     * @return form's width, default is 1.0f of player's current size
     */
    float getWidth();

    /**
     * @param width size to set form to. 2.0f sets the player to 2x their normal size. Min: 0.2, Max: 3.0
     */
    void setWidth(float width);

    boolean hasSize();

    String getBodyType();

    /**
     * So far this is only for arcosian race.
     *
     * @param type Legal: firstform, secondform, thirdform, finalform, ultimatecooler, ultimate
     */
    void setBodyType(String type);

    public boolean hasArcoMask();

    /**
     * @param hasMask True if you want the form to render with Ultimate Cooler's mask
     */

    void hasArcoMask(boolean hasMask);


    boolean hasBodyFur();

    /**
     * @param hasFur True if you want the form to render with fur on body
     */
    void hasBodyFur(boolean hasFur);

    boolean effectMajinHair();

    /**
     * @param effect True to allow majin CFs to have different hairType like SSJ,SSJ2 and SSJ3
     */
    void setEffectMajinHair(boolean effect);

    /**
     * @return True if form has a custom IAura object
     */
    boolean hasAura();

    IAura getAura();

    /**
     * @param aura Set the IAura object you want the form to use
     */
    void setAura(IAura aura);

    /**
     * @param auraID ID of IAura object
     */
    void setAura(int auraID);

    void setOutline(int id);

    void setOutline(IOutline outline);

    /**
     * Saves CustomForm with the New Form Display Modifications
     *
     * @return IFormDisplay self object
     */
    IFormDisplay save();

    /**
     * Allows the player to edit their custom form's appearance.
     * @param customizable true or false.
     */
    void setCustomizable(boolean customizable);

    /**
     * @return If the form colors are customizable by players.
     */
    boolean isCustomizable();
}
