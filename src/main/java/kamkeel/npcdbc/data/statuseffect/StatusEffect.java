package kamkeel.npcdbc.data.statuseffect;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.api.effect.IStatusEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import static kamkeel.npcdbc.scripted.DBCPlayerEvent.EffectEvent.ExpirationType;

public class StatusEffect implements IStatusEffect {
    public static final ResourceLocation defaultTexture = new ResourceLocation(CustomNpcPlusDBC.ID + ":textures/gui/icons.png");
    public int id = -1;

    public String name = "";
    public boolean lossOnDeath = true;
    public int length = 30;

    // Must be a multiple of 10
    public int everyXTick = 20;

    public String icon = CustomNpcPlusDBC.ID + ":textures/gui/icons.png";
    public int iconX = 0, iconY = 0;
    public final boolean isCustom;

    public StatusEffect() {
        this(false);
    }

    protected StatusEffect(boolean isCustom) {
        this.isCustom = isCustom;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isCustom() {
        return isCustom;
    }

    public void runEffect(EntityPlayer player, PlayerEffect playerEffect) {
        if (player.ticksExisted % everyXTick == 0) {
            onTick(player, playerEffect);
        }
    }

    public void onAdded(EntityPlayer player, PlayerEffect playerEffect) {

    }

    public void onTick(EntityPlayer player, PlayerEffect playerEffect) {
    }

    public void onRemoved(EntityPlayer player, PlayerEffect playerEffect, ExpirationType naturallyExpired) {

    }

    /**
     * DO NOT OVERRIDE
     */
    public int getWidth() {
        return 16;
    }

    /**
     * DO NOT OVERRIDE
     */
    public int getHeight() {
        return 16;
    }
}
