package kamkeel.npcdbc.api.outline;

import kamkeel.npcdbc.data.outline.Outline;

public interface IOutline {

    void setInnerColor(int color, float alpha);

    void setOuterColor(int color, float alpha);

    Outline setSize(float size);

    Outline setNoiseSize(float size);

    Outline setSpeed(float speed);

    Outline setPulsingSpeed(float speed);

    Outline setColorSmoothness(float smoothness);

    Outline setColorInterpolation(float interp);

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
