package kamkeel.npcdbc.api.form;

import net.minecraft.entity.player.EntityPlayer;

public interface IForm {

    String getName();

    void setName(String name);

    String getMenuName();

    void setMenuName(String name);

    int getRace();

    void setRace(int race);


    void setState2Factor(int dbcForm, float factor);

    /**
     * @param dbcForm Legal args: 20 Kaioken, 21 UltraInstinct
     * @return how higher a stackable form's multi gets as you go up in state2
     * i.e. If KK form multi is 10x and State2Factor is 1x, very first KK form multi will be 10x, second KK form multi is 13.3x, final KK form will be 20x
     * if State2Factor is 2x, first is 10x, second is 16.6, final KK form will be 30 and so on.
     * This value scales off as a factor of the form's multiplier
     */
    float getState2Factor(int dbcForm);


    /**
     * @return index 0 is strengthMulti,1 dexMulti, 2 willMulti
     */
    float[] getAllMulti();

    void setAllMulti(float allMulti);

    /**
     * @param id    0 for Strength, 1 for Dex, 3 for Willpower
     * @param multi
     */
    void setAttributeMulti(int id, float multi);

    /**
     * @param id 0 for Strength, 1 for Dex, 3 for Willpower
     */
    float getAttributeMulti(int id);

    /**
     * @param DBCForm Non-racial DBC Forms, 20 Kaioken, 21 Mystic, 22 UI, 23 GoD
     * @return True if can stack DBCForm on Custom Form
     */
    boolean isFormStackable(int DBCForm);

    /**
     * @param DBCForm Non-racial DBC Forms, 20 Kaioken, 21 Mystic, 22 UI, 23 GoD
     */
    void stackForm(int DBCForm, boolean stackForm);

    /**
     * @param DBCForm Non-racial DBC Forms, 20 Kaioken, 21 Mystic, 22 UI, 23 GoD
     */
    void setFormMulti(int DBCForm, float multi);

    /**
     * @param DBCForm Non-racial DBC Forms, 20 Kaioken, 21 Mystic, 22 UI, 23 GoD
     * @return DBCForm's multi
     */
    float getFormMulti(int DBCForm);

    void assignToPlayer(EntityPlayer p);

    void removeFromPlayer(EntityPlayer p);

    void assignToPlayer(String name);

    void removeFromPlayer(String name);

    String getAscendSound();

    void setAscendSound(String directory);

    String getDescendSound();

    void setDescendSound(String directory);

    int getID();

    /**
     * Do not use this unless you know what you are changing. Dangerous to change.
     */
    void setID(int newID);

    /**
     * @return ID of form linked to this
     */
    int getChildID();

    boolean hasChild();

    /**
     * @param formID form ID to of child to this form. i.e linking SSJ2 Red to SSJ Red
     */
    void linkChild(int formID);

    void linkChild(IForm form);

    /**
     * @return the child of this form i.e if SSJ2 Red is child of SSJ Red(this), returns SSJ2 Red
     */
    IForm getChild();


    /**
     * removes the child of this form
     */
    void removeChildForm();

    /**
     * @return ID of form's parent i.e SSJ Red is parent of SSJ2 Red
     */
    int getParentID();

    boolean hasParent();

    /**
     * @param formID ID of parent of to link to this form.
     */
    void linkParent(int formID);

    void linkParent(IForm form);

    /**
     * @return the parent of this form i.e if SSJ2 Red is Parent of SSJ Red(this), returns SSJ2 Red
     */
    IForm getParent();


    int getTimer();

    void setTimer(int timeInTicks);

    boolean hasTimer();

    /**
     * removes the form's parent
     */
    void removeParentForm();

    public IFormMastery getMastery();

    IFormDisplay getDisplay();

    IForm save();
}
