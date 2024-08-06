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

    /**
     * Unused right now.
     * @param damage
     */
    void setDamage(float damage);

    /**
     * Unused right now.
     * @return damage the weapon adds to the NPCs total
     */
    float getDamage();
}
