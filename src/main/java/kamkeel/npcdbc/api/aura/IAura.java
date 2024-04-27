package kamkeel.npcdbc.api.aura;

import net.minecraft.entity.player.EntityPlayer;

public interface IAura {

    String getName();

    /**
     * @param name Name of the aura. Must be unique to each aura
     */
    void setName(String name);

    String getMenuName();

    /**
     * @param name Name of aura to be displayed in all aura rendering, whether Aura Selection GUI or DBC stat sheet or chat. Minecraft Color codes are allowed "&4&l"
     */
    void setMenuName(String name);

    int getID();

    /**
     * Do not use this unless you know what you are changing. Dangerous to change.
     */
    void setID(int newID);

    /**
     * @param player Player to give this aura to
     */
    void assignToPlayer(EntityPlayer player);

    void removeFromPlayer(EntityPlayer player);

    void assignToPlayer(String playerName);

    void removeFromPlayer(String playerName);

    /**
     * @return An interface containing getters and setters for all the aura's rendering data
     */

    IAuraDisplay getDisplay();

    /**
     * @return Saves all Aura data. Always call this when changing any aura data
     */
    IAura save();


}
