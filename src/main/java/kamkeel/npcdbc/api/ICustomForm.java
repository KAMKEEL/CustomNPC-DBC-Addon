package kamkeel.npcdbc.api;

import net.minecraft.entity.player.EntityPlayer;

public interface ICustomForm {

    String getName();

    void setName(String name);

    int getRace();

    void setRace(int race);

    float getAllMulti();

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


    int getID();

    /**
     * Do not use this unless you know what you are changing. Dangerous to change.
     */
    void setID(int newID);


    ICustomForm save();

}
