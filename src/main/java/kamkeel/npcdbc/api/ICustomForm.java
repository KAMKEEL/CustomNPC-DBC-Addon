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


    boolean isKaiokenStackable();

    boolean isUIStackable();

    void stackKaioken(boolean canStackKaioken);

    void stackUI(boolean canStackUI);

    float getKaiokenMulti();

    void setKaiokenMulti(float kaiokenMulti);

    float getUiMulti();

    void setUiMulti(float uiMulti);

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
