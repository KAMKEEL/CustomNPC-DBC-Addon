package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.DragonBC.common.Npcs.EntityAura2;
import JinRyuu.DragonBC.common.Npcs.RenderAura2;
import JinRyuu.JRMCore.JRMCoreHDBC;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.*;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.mixins.late.IEntityAura;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = RenderAura2.class, remap = false)
public class MixinRenderAura2 {

    @Shadow
    private int lightVertN;


    @Redirect(method = "lightning", at = @At(value = "INVOKE", target = "LJinRyuu/DragonBC/common/Npcs/EntityAura2;getState()F"))
    private float setHasLightning(EntityAura2 instance) {
        IEntityAura aura = (IEntityAura) instance;
        if (aura.hasLightning())
            return 5f;
        return instance.getState();
    }

    @Redirect(method = "lightning", at = @At(value = "INVOKE", target = "LJinRyuu/DragonBC/common/Npcs/EntityAura2;getLightLivingTime()I"))
    private int setLightningSpeed(EntityAura2 instance) {
        IEntityAura aura = (IEntityAura) instance;
        if (aura.hasLightning() && aura.getLightningSpeed() != -1)
            return aura.getLightningSpeed();

        return instance.getLightLivingTime();
    }

    @Inject(method = "lightning", at = @At(value = "FIELD", target = "LJinRyuu/DragonBC/common/Npcs/RenderAura2;lightVertN:I", ordinal = 0, shift = At.Shift.AFTER))
    private void setLightningIntensity(EntityAura2 e, double par2, double par4, double par6, float par9, float var20, float var13, boolean rot, CallbackInfo ci, @Local(name = "nu2") LocalIntRef nu2) {
        IEntityAura aura = (IEntityAura) e;
        if (!aura.hasLightning())
            return;

        int intensity = aura.getLightningIntensity();

        if (aura.getLightningIntensity() > -1) {
            lightVertN = intensity;
            nu2.set(intensity);
        }
        Minecraft.getMinecraft().entityRenderer.disableLightmap(0);

    }

    @Inject(method = "lightning", at = @At("TAIL"))
    private void enableLightMap(EntityAura2 e, double par2, double par4, double par6, float par9, float var20, float var13, boolean rot, CallbackInfo ci) {
        IEntityAura aura = (IEntityAura) e;
        if (!aura.hasLightning())
            return;

        Minecraft.getMinecraft().entityRenderer.enableLightmap(0);

    }

    @Inject(method = "lightning", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Tessellator;setColorRGBA_F(FFFF)V", ordinal = -1, shift = At.Shift.AFTER, remap = true))
    private void renderLightningColor(EntityAura2 e, double par2, double par4, double par6, float par9, float var20, float var13, boolean rot, CallbackInfo ci, @Local(name = "tessellator2") LocalRef<Tessellator> tessellator) {
        IEntityAura aura = (IEntityAura) e;
        if (!aura.hasLightning())
            return;

        int lightningColor = 0x25c9cf;
        int lightningAlpa = 255;

        if (aura.getLightningColor() > -1)
            lightningColor = aura.getLightningColor();
        if (aura.getLightningAlpha() > -1)
            lightningAlpa = aura.getLightningAlpha();

        if (lightningColor <= 1000)
            GL11.glDisable(GL11.GL_BLEND);

        tessellator.get().setColorRGBA_I(lightningColor, lightningAlpa);

    }

    @ModifyArgs(method = "func_tad(LJinRyuu/DragonBC/common/Npcs/EntityAura2;DDDFF)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glScalef(FFF)V", ordinal = 0))
    private void setAuraSize(Args args, @Local(ordinal = 0) LocalRef<EntityAura2> entityAura, @Local(name = "cl3b") LocalBooleanRef hasColor3) {
        EntityAura2 aur = entityAura.get();
        IEntityAura aura = (IEntityAura) aur;
        if (aura.getSize() != 1f) {
            float size = aura.getSize();
            float xSize = (float) args.get(0) * size;
            float ySize = (float) args.get(1) * size;
            float zSize = (float) args.get(2) * size;

            args.set(0, xSize);
            args.set(1, ySize);
            args.set(2, zSize);


        }
        hasColor3.set(aur.getColL3() > 0 && aur.getTexL3().length() > 2);
    }

    @ModifyArgs(method = "func_tad(LJinRyuu/DragonBC/common/Npcs/EntityAura2;DDDFF)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glTranslatef(FFF)V", ordinal = 0))
    private void fixAuraOffset(Args args, @Local(ordinal = 0) LocalRef<EntityAura2> entityAura, @Local(ordinal = 1) LocalDoubleRef parY) {
        IEntityAura aura = (IEntityAura) entityAura.get();
        if (aura.getSize() != 1f) {
            float fixedOffset = (float) (parY.get() + 3.0F * aura.getSize());
            args.set(1, fixedOffset);
        }
    }


    @Inject(method = "func_tad(LJinRyuu/DragonBC/common/Npcs/EntityAura2;DDDFF)V", at = @At(value = "INVOKE", target = "LJinRyuu/DragonBC/common/Npcs/EntityAura2;getState2()F", ordinal = 0, shift = At.Shift.BEFORE))
    private void fixAuraSize(EntityAura2 par1Entity, double parX, double parY, double parZ, float par8, float par9, CallbackInfo ci, @Local(name = "s1") LocalFloatRef s1, @Local(name = "s") LocalFloatRef s, @Local(name = "cr") LocalFloatRef cr) {
        EntityPlayer auraOwner = par1Entity.worldObj.getPlayerEntityByName(par1Entity.getmot());
        if (auraOwner instanceof EntityPlayer) {
            DBCData dbcData = DBCData.get(auraOwner);
            float size = JRMCoreHDBC.DBCsizeBasedOnRace2(dbcData.Race, dbcData.State);

            if (dbcData.addonFormID > -1)
                s1.set(8.0f * size);


         s.set(s.get() == 0 ? 1 : s.get() * Math.min(dbcData.Release, 100) * 0.01f);
            return;
        }
    }
}
