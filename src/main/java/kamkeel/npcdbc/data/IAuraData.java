package kamkeel.npcdbc.data;

import JinRyuu.JRMCore.entity.EntityCusPar;
import kamkeel.npcdbc.entity.EntityAura;
import net.minecraft.entity.Entity;

import java.util.HashMap;
import java.util.List;

public interface IAuraData {
    public EntityAura getAuraEntity();
    public Entity getEntity();
    public void setAuraEntity(EntityAura aura);
    public int getAuraColor();
    public int getActiveAuraColor();
    public void setActiveAuraColor(int color);
    public boolean isTransforming();

    public boolean isChargingKi();
    public boolean isInKaioken();

    public byte getRace();

    public int getFormID();
    public byte getRelease();
    public byte getState();
    public byte getState2();

    public boolean isForm(int form);

    public int getDBCColor();
    public boolean isAuraOn();
    public boolean isFusionSpectator();

    public void useStencilBuffer(boolean use);
    public HashMap getDBCAuras(boolean secondary);
    public List<EntityCusPar> getParticles();

    // Aura Color
    // Aura Size
    // Aura State
    // Aura State
    // Aura Race




}
