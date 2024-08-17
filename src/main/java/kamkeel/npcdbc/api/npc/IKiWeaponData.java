package kamkeel.npcdbc.api.npc;

public interface IKiWeaponData {

    boolean isEnabled();


    /**
     * Sets the weapon type
     * @param type - 0 is off, 1 is Ki Blade, 2 is Ki Scythe
     */
    void setWeaponType(int type);

    /**
     * @return - 0 is off, 1 is Ki Blade, 2 is Ki Scythe
     */
    int getWeaponType();

    /**
     * Sets the weapon color.
     * @param color If it's set to -1, it relies on current aura color.
     * @param alpha opacity of the color. 0 - Transparent, 1 - Opaque
     */

    void setColor(int color, float alpha);

    int getColor();

    void setXOffset(float offset);

    void setYOffset(float offset);

    void setZOffset(float offset);

    float getXOffset();

    float getYOffset();

    float getZOffset();

    void setXScale(float scale);

    void setYScale(float scale);

    void setZScale(float scale);

    float getXScale();

    float getYScale();

    float getZScale();
}
