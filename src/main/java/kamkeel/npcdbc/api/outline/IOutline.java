package kamkeel.npcdbc.api.outline;

import kamkeel.npcdbc.data.outline.Outline;

public interface IOutline {

    void setInnerColor(int color, float alpha);

    void setOuterColor(int color, float alpha);

    IOutline setSize(float size);

    IOutline setNoiseSize(float size);

    IOutline setSpeed(float speed);

    IOutline setPulsingSpeed(float speed);

    IOutline setColorSmoothness(float smoothness);

    IOutline setColorInterpolation(float interp);

    String getName();

    /**
     * @param name Name of the aura. Must be unique to each aura
     */
    void setName(String name);

    String getMenuName();

    /**
     * @param name Name of aura to be displayed in all aura rendering, whether Aura Selection GUI or DBC stat sheet or chat. Minecraft Color codes are allowed "&amp;4&amp;l"
     */
    void setMenuName(String name);

    int getID();

    /**
     * Do not use this unless you know what you are changing. Dangerous to change.
     * @param newID new ID.
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
