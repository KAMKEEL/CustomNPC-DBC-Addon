package kamkeel.npcdbc.data.outline;

import kamkeel.npcdbc.api.aura.IAuraDisplay;
import kamkeel.npcdbc.data.aura.Aura;
import net.minecraft.entity.player.EntityPlayer;

public interface IOutline {

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
     *
     * @return clones this IAura object and returns a new IAura with the same exact properties
     */
    IOutline clone();

    /**
     * @return Saves all Aura data. Always call this when changing any aura data
     */
    IOutline save();


}
