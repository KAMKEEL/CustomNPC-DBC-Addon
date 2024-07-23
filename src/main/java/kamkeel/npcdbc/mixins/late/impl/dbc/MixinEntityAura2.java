package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.DragonBC.common.Npcs.EntityAura2;
import JinRyuu.JRMCore.client.config.jrmc.JGConfigClientSettings;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.ParticleFormHandler;
import kamkeel.npcdbc.client.gui.global.auras.SubGuiAuraDisplay;
import kamkeel.npcdbc.client.sound.ClientSound;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes2D;
import kamkeel.npcdbc.data.IAuraData;
import kamkeel.npcdbc.data.SoundSource;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixins.late.IEntityAura;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.Entity;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = EntityAura2.class, remap = false)
public class MixinEntityAura2 implements IEntityAura {

    @Shadow
    private float state;
    @Shadow
    private int rendpass;
    @Unique
    private boolean hasLightning;
    @Unique
    private int lightningColor;
    @Unique
    private int lightningAlpha;
    @Unique
    private int lightningSpeed;
    @Unique
    private int lightningIntensity;
    @Unique
    private float getSize = 1;
    @Unique
    private EnumAuraTypes2D type2D = null;

    @Unique
    private EntityAura2 parent;
    @Unique
    private boolean isKaiokenAura;

    @Unique
    private boolean enhancedRendering;
    @Unique
    private boolean isGUIAura;
    @Unique
    private Entity entity;

    @Shadow
    private String mot;

    @Shadow
    private byte bol6;
    @Shadow
    private int color;
    @Unique
    private IAuraData data;


    @Override
    public boolean isEnhancedRendering() {
        return enhancedRendering;
    }


    @Inject(method = "<init>(Lnet/minecraft/world/World;Ljava/lang/String;IFFIZ)V", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        EntityAura2 aura = (EntityAura2) (Object) this;


    }

    @Inject(method = "onUpdate", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;getPlayerEntityByName(Ljava/lang/String;)Lnet/minecraft/entity/player/EntityPlayer;", shift = At.Shift.AFTER), remap = true)
    private void redirect(CallbackInfo ci, @Local(name = "other") LocalRef<Entity> player, @Local(name = "aura_type") LocalBooleanRef aura_type, @Local(name = "aura_type2") LocalBooleanRef aura_type2) {
        EntityAura2 aura = (EntityAura2) (Object) this;
        Entity entity = Utility.getEntityFromID(aura.worldObj, mot);
        if (entity != null) {
            player.set(entity);
        } else {
            player.set(this.entity);
            mot = this.entity.getCommandSenderName();
        }


        if (entity != null) {
            if (aura.getAge() < aura.getLightLivingTime() && hasLightning && aura.getAge() == 2)
                playSound(player.get(), aura);
        }
        if (isGUIAura) {
            aura_type.set(true);
           aura_type2.set(false);
        }
        if (bol6 == -2) {
            float height = getSize <= 0 ? this.entity.height : (getSize * 1.3f);
            ParticleFormHandler.spawnAura2D(type2D, color, this.entity, data, height);
        }
    }


    @ModifyArgs(method = "onUpdate", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/entity/EntityCusPar;<init>(Ljava/lang/String;Lnet/minecraft/world/World;FFDDDDDDDDDFIIIIZFZFILjava/lang/String;IIFFFIFFFFFFFFFIFFFFFZIZLnet/minecraft/entity/Entity;)V", ordinal = 0))
    private void setDamage(Args args) {
        args.set(48, entity);
    }


    @Redirect(method = "onUpdate", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/client/config/jrmc/JGConfigClientSettings;CLIENT_GR0:Z"))
    private boolean fixBuiltInParticles() {
        if (type2D != null) {
            if (type2D == EnumAuraTypes2D.None)
                return false;
        }

        return JGConfigClientSettings.CLIENT_GR0;

    }



    @Unique
    @Override
    public void setEntity(Entity entity) {
        this.entity = entity;
        EntityAura2 aura = (EntityAura2) (Object) this;
        if (this.entity instanceof EntityNPCInterface && SubGuiAuraDisplay.useGUIAura) {
            DBCDisplay display = ((INPCDisplay) ((EntityNPCInterface) this.entity).display).getDBCDisplay();
            if (display != null) {
                if (rendpass == 0)
                    display.dbcSecondaryAuraQueue.put(aura.getEntityId(), aura);
                else
                    display.dbcAuraQueue.put(aura.getEntityId(), aura);
                enhancedRendering = true;

            }
        }
    }

    @Unique
    @Override
    public Entity getEntity() {
        return this.entity;
    }

    @Unique
    @SideOnly(Side.CLIENT)
    public void playSound(Entity player, EntityAura2 aura){
        new ClientSound(new SoundSource("jinryuudragonbc:1610.spark", player)).setVolume(0.0375F).setPitch(0.85F + aura.getLightLivingTime() * 0.05F).play(false);
    }

    @Unique
    @Override
    public float getState() {
        return state;
    }

    @Unique
    @Override
    public void setState(float ok) {
        state = ok;
    }

    @Unique
    @Override
    public boolean hasLightning() {
        return hasLightning;
    }

    @Unique
    @Override
    public void setType2D(EnumAuraTypes2D types2D) {
        type2D = types2D;
    }

    @Unique
    @Override
    public EnumAuraTypes2D getType2D() {
        return type2D;
    }
    @Unique
    @Override
    public void setHasLightning(boolean hasLightning) {
        this.hasLightning = hasLightning;
    }

    @Unique
    @Override
    public int getLightningColor() {
        return lightningColor;
    }

    @Unique
    @Override
    public void setLightningColor(int lightningColor) {
        this.lightningColor = lightningColor;
    }

    @Unique
    @Override
    public int getLightningAlpha() {
        return lightningAlpha;
    }

    @Unique
    @Override
    public void setLightningAlpha(int lightningAlpha) {
        this.lightningAlpha = lightningAlpha;
    }

    @Unique
    @Override
    public int getLightningSpeed() {
        return lightningSpeed;
    }

    @Unique
    @Override
    public void setLightningSpeed(int lightningSpeed) {
        this.lightningSpeed = lightningSpeed;
    }

    @Unique
    @Override
    public int getLightningIntensity() {
        return lightningIntensity;
    }

    @Unique
    @Override
    public void setLightningIntensity(int lightningIntensity) {
        this.lightningIntensity = lightningIntensity;
    }

    @Unique
    @Override
    public float getSize() {
        return getSize;
    }

    @Unique
    @Override
    public void setSize(float getSize) {
        this.getSize = getSize;
    }

    @Unique
    @Override
    public EntityAura2 getParent() {
        return this.parent;
    }


    @Unique
    @Override
    public void setParent(EntityAura2 parent) {
        this.parent = parent;
    }

    @Unique
    @Override
    public boolean hasParent() {
        return this.parent != null;
    }

    @Unique
    @Override
    public void setIsKaioken(boolean is) {
        this.isKaiokenAura = is;
    }


    @Unique
    @Override
    public boolean isKaioken() {
        return isKaiokenAura;
    }


    @Unique
    @Override
    public void setAuraData(IAuraData data) {
        this.data = data;
    }


    @Unique
    @Override
    public IAuraData getAuraData() {
        return data;
    }

    @Unique
    @Override
    public int getRenderPass() {
        return rendpass;
    }

    @Unique
    @Override
    public void setGUIAura(boolean is) {
        this.isGUIAura = is;
    }


    @Unique
    @Override
    public boolean isGUIAura() {
        return isGUIAura;
    }

}
