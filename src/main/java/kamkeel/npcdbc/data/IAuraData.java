package kamkeel.npcdbc.data;

import kamkeel.npcdbc.entity.EntityAura;

public interface IAuraData {
    public EntityAura getAuraEntity();
    public void setAuraEntity(EntityAura aura);
    public int getAuraColor();
    public boolean isTransforming();
    public boolean isInKaioken();
    public boolean isCharging();

    public byte getRace();

    public int getFormID();

    // Aura Color
    // Aura Size
    // Aura State
    // Aura State
    // Aura Race




}
