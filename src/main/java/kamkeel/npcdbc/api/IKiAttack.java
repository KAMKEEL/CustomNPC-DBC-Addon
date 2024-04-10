package kamkeel.npcdbc.api;

public interface IKiAttack {
    byte getType();
    void setType(byte type);

    byte getSpeed();
    void setSpeed(byte speed);

    int getDamage();
    void setDamage(int damage);

    boolean isHasEffect();
    void setHasEffect(boolean hasEffect);

    byte getColor();
    void setColor(byte color);

    byte getDensity();
    void setDensity(byte density);

    boolean isHasSound();
    void setHasSound(boolean hasSound);

    byte getChargePercent();
    void setChargePercent(byte chargePercent);
}

