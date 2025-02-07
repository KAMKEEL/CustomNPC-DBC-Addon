package kamkeel.npcdbc.api.form;

import kamkeel.npcdbc.scripted.ScriptDBCAddon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.api.entity.IPlayer;

public interface IForm {

    String getName();

    /**
     * @param name Name of the form. Must be unique to each form
     */
    void setName(String name);

    String getMenuName();

    /**
     * @param name Name of form to be displayed in all form rendering, whether Form Selection GUI or DBC stat sheet or chat. Minecraft Color codes are allowed "&amp;4&amp;l"
     */

    void setMenuName(String name);

    int getRace();

    /**
     * @param race The race ID of the form. Only player's of raceID can access this form. -1 for ALL RACES
     */
    void setRace(int race);


    /**
     * @return An array of str,dex,will multipliers. index 0 is strengthMulti,1 dexMulti, 2 willMulti
     */
    float[] getAllMulti();

    /**
     * @param allMulti Sets strength, dex and willpower multipliers to this value
     */

    void setAllMulti(float allMulti);

    boolean raceEligible(int race);
    boolean raceEligible(IPlayer player);

    /**
     * @param id    0 for Strength, 1 for Dex, 3 for Willpower
     * @param multi attribute multiplier for given stat
     */
    void setAttributeMulti(int id, float multi);

    /**
     * @param id 0 for Strength, 1 for Dex, 3 for Willpower
     * @return Attribute multiplier for given stat
     */
    float getAttributeMulti(int id);

    /**
     * @param player Player to give this form to
     */
    void assignToPlayer(IPlayer player);

    void removeFromPlayer(IPlayer player);

    void assignToPlayer(String playerName);

    void removeFromPlayer(String playerName);

    void removeFromPlayer(IPlayer ScriptDBCAddon, boolean removesMastery);
    void removeFromPlayer(String playerName, boolean removesMastery);

    String getAscendSound();

    /**
     * @param directory Sound effect to play on form ascension
     */
    void setAscendSound(String directory);

    String getDescendSound();

    /**
     * @param directory Sound effect to play on descending
     */
    void setDescendSound(String directory);

    int getID();

    /**
     * Do not use this unless you know what you are changing. Dangerous to change.
     * @param newID new ID of the form.
     */
    void setID(int newID);

    /**
     * @return ID of form next in chain of transformation to this i.e SSJ2 Red is child of SSJ Red
     */
    int getChildID();

    boolean hasChild();

    /**
     * @param formID form ID to of child to this form. i.e id of SSJ2 Red to link to SSJ Red
     */
    void linkChild(int formID);

    void linkChild(IForm form);

    boolean isFromParentOnly();

    void setFromParentOnly(boolean set);

    void addFormRequirement(int race, byte state);

    void removeFormRequirement(int race);

    int getFormRequirement(int race);

    boolean isChildOf(IForm parent);

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

    /**
     * @param timeInTicks Sets the form's timer. When this timer runs out, player reverts from form
     */
    void setTimer(int timeInTicks);

    boolean hasTimer();

    /**
     * removes the form's parent
     */
    void removeParentForm();

    /**
     * @return An interface containing getters and setters for all the form's mastery data
     */
    IFormMastery getMastery();

    /**
     * @return An interface containing getters and setters for all the form's rendering data
     */

    IFormDisplay getDisplay();

    /**
     * @return An interface containing getters and setters for all data on the form's interactions with vanilla DBC forms
     */
    IFormStackable getStackable();

    void setMindRequirement(int mind);

    int getMindRequirement();

    /**
     *
     * @return clones this IForm object and returns a new IForm with the same exact properties
     */
    IForm clone();
    IForm save();
}
