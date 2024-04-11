package kamkeel.npcdbc.api;

public interface IKiAttack {

    /**
     * @return Type of Ki Attack [0 - 8] "Wave", "Blast", "Disk", "Laser", "Spiral", "BigBlast", "Barrage", "Shield", "Explosion"
     **/
    byte getType();

    /**
     * @param type Type of Ki Attack [0 - 8] "Wave", "Blast", "Disk", "Laser", "Spiral", "BigBlast", "Barrage", "Shield", "Explosion"
     **/
    void setType(byte type);

    /**
     * @return Speed of Ki Attack
     **/
    byte getSpeed();

    /**
     * @param speed Speed of Ki Attack [0 - 100]
     **/
    void setSpeed(byte speed);

    int getDamage();
    void setDamage(int damage);

    boolean hasEffect();
    void setHasEffect(boolean hasEffect);

    /**
     * @return Color of Ki Attack [0 - 30] ->
     *              0: "AlignmentBased", "white", "blue", "purple", "red", "black", "green", "yellow", "orange", "pink", "magenta",
     *              11: "lightPink", "cyan", "darkCyan", "lightCyan", "darkGray", "gray", "darkBlue", "lightBlue", "darkPurple", "lightPurple",
     *              21: "darkRed", "lightRed", "darkGreen", "lime", "darkYellow", "lightYellow", "gold", "lightOrange", "darkBrown", "lightBrown"
     **/
    byte getColor();

    /**
     * @param color Color of Ki Attack [0 - 30] ->
     *              0: "AlignmentBased", "white", "blue", "purple", "red", "black", "green", "yellow", "orange", "pink", "magenta",
     *              11: "lightPink", "cyan", "darkCyan", "lightCyan", "darkGray", "gray", "darkBlue", "lightBlue", "darkPurple", "lightPurple",
     *              21: "darkRed", "lightRed", "darkGreen", "lime", "darkYellow", "lightYellow", "gold", "lightOrange", "darkBrown", "lightBrown"
     **/
    void setColor(byte color);

    byte getDensity();
    void setDensity(byte density);

    boolean hasSound();
    void setHasSound(boolean hasSound);

    byte getChargePercent();
    void setChargePercent(byte chargePercent);
}

