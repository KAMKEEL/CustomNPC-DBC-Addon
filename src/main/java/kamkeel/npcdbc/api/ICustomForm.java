package kamkeel.npcdbc.api;

import net.minecraft.entity.player.EntityPlayer;

public interface ICustomForm {

    String getName();

    void setName(String name);

    String getMenuName();

    void setMenuName(String name);

    int getRace();

    void setRace(int race);

    String getHairCode();

    /**
     * @param hairCode The hair code to set transformation's hair to, usually gotten from the JinGames Hair Salon
     */
    void setHairCode(String hairCode);

    /**
     * @param type Legal types: aura, hair, eye, bodyMain, body1, body2, body3
     * @return Decimal color of type
     */
    int getColor(String type);

    /**
     * @param type  Legal types: aura, hair, eye, bodyMain, body1, body2, body3
     * @param color Decimal color to set type as
     */
    void setColor(String type, int color);

    /**
     * @param type Legal types: "base", "ssj", "ssj2", "ssj3", "ssj4", "oozaru", "" for no type
     */
    void setHairType(String type);

    /**
     * @param type Legal types: "base", "ssj", "ssj2", "ssj3", "ssj4", "oozaru", "" for no type
     */
    String getHairType(String type);

    /**
     * @param type Legal types: aura, hair, eye, bodyMain, body1, body2, body3
     * @return True if form has the "hasTypeColor" field to true,
     * useful for Namekians and Arcosians multi body color setting
     */
    boolean hasColor(String type);

    /**
     * @param type Legal types: aura, hair, eye, bodyMain, body1, body2, body3
     * @param has  True if form should have color of type
     */
    void setHasColor(String type, boolean has);

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
     * @return form's size, default is 1.0f of player's current size
     */
    float getSize();

    /**
     * @param size size to set form to. 2.0f sets the player to 2x their normal size. Max: 50.0
     */
    void setSize(float size);

    boolean hasSize();

    /**
     * Must be set to true in order for formSize to be enabled
     *
     * @param hasSize
     */
    void setHasSize(boolean hasSize);

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

    int getAuraColor();

    void setAuraColor(int auraColor);

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

    public IFormMastery getFormMastery();

    ICustomForm save();

}
