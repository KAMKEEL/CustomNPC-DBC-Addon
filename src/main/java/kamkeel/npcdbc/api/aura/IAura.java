package kamkeel.npcdbc.api.aura;

import net.minecraft.entity.player.EntityPlayer;

public interface IAura {

    String getName();

    void setName(String name);

    String getMenuName();

    void setMenuName(String name);

    int getID();

    /**
     * Do not use this unless you know what you are changing. Dangerous to change.
     */
    void setID(int newID);

    void assignToPlayer(EntityPlayer p);

    void removeFromPlayer(EntityPlayer p);

    void assignToPlayer(String name);

    void removeFromPlayer(String name);

    IAuraDisplay getDisplay();

    IAura save();


}
