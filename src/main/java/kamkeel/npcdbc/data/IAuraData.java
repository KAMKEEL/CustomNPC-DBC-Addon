package kamkeel.npcdbc.data;

import kamkeel.npcdbc.entity.EntityAura;

public interface IAuraData {
    public EntityAura getAuraEntity();
    public void setAuraEntity(EntityAura aura);
    public int getAuraColor();
    public boolean isTransforming();

    public boolean isChargingKi();

    public byte getRace();

    public int getFormID();
    public byte getRelease();
    public byte getState();

    public boolean isForm(int form);

    public int getDBCColor();
    public boolean isAuraOn();
    
    // Aura Color
    // Aura Size
    // Aura State
    // Aura State
    // Aura Race




}
