package kamkeel.npcdbc.data;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.api.IKiAttack;
import noppes.npcs.util.ValueUtil;

public class KiAttack implements IKiAttack {
    private byte type = 0;
    private byte speed = 0;
    private int damage = 0;
    private boolean hasEffect = false;
    private byte color = 0;
    private byte density = 100;
    private boolean hasSound;
    private byte chargePercent;
    private boolean forceDestroyerMode;
    private boolean respectFormDestroyerMode = true;

    public KiAttack() {}

    // Constructor
    public KiAttack(byte type, byte speed, int damage, boolean hasEffect, byte color, byte density, boolean hasSound, byte chargePercent) {
        setType(type);
        setSpeed(speed);
        setDamage(damage);
        setHasEffect(hasEffect);
        setColor(color);
        setDensity(density);
        setHasSound(hasSound);
        setChargePercent(chargePercent);
    }

    // Getters and setters
    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = ValueUtil.clamp(type, (byte) 0 , (byte) 8);
    }

    public byte getSpeed() {
        return speed;
    }

    public void setSpeed(byte speed) {
        this.speed = ValueUtil.clamp(speed, (byte) 0 , (byte) 100);
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = Math.max(damage, 0);
    }

    public boolean hasEffect() {
        return hasEffect;
    }

    public void setHasEffect(boolean hasEffect) {
        this.hasEffect = hasEffect;
    }

    public byte getColor() {
        return color;
    }

    public void setColor(byte color) {
        this.color = ValueUtil.clamp(color, (byte) 0, (byte) (JRMCoreH.techCol.length - 1));
    }

    public byte getDensity() {
        return density;
    }

    public void setDensity(byte density) {
        this.density = density < 0 ? 0 : density;
    }

    public boolean hasSound() {
        return hasSound;
    }

    public void setHasSound(boolean hasSound) {
        this.hasSound = hasSound;
    }

    public byte getChargePercent() {
        return chargePercent;
    }

    public void setChargePercent(byte chargePercent) {
        this.chargePercent = ValueUtil.clamp(chargePercent, (byte) 0, (byte) 100);
    }

    @Override
    public boolean respectFormDestoryerConfig() {
        return this.respectFormDestroyerMode;
    }

    @Override
    public void setRespectFormDestroyerConfig(boolean respectFormConfig) {
        this.respectFormDestroyerMode = respectFormConfig;
    }

    @Override
    public boolean isDestroyerAttack() {
        return this.forceDestroyerMode;
    }

    @Override
    public void setDestroyerAttack(boolean isDestroyer) {
        this.forceDestroyerMode = isDestroyer;
    }
}
