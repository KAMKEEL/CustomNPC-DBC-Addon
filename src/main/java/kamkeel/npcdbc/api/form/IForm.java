package kamkeel.npcdbc.api.form;

import net.minecraft.entity.player.EntityPlayer;

public interface IForm {

    String getName();

    void setName(String name);

    String getMenuName();

    void setMenuName(String name);

    int getRace();

    void setRace(int race);


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

    boolean isFromParentOnly();

    void setFromParentOnly(boolean set);

    void addFormRequirement(int race, byte state);

    int getFormRequirement(int race);

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

    IFormMastery getMastery();

    IFormDisplay getDisplay();

    IFormStackable getStackable();


    IForm save();
}
