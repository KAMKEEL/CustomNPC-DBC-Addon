package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.DragonBC.common.Npcs.EntityAura2;
import JinRyuu.DragonBC.common.Npcs.RenderAura2;
import JinRyuu.JRMCore.JRMCoreHDBC;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.mixin.IEntityAura;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.entity.EntityCustomNpc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.awt.*;

@Mixin(value = RenderAura2.class, remap = false)
public class MixinRenderAura2 {

    @Inject(method = "lightning", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Tessellator;setColorRGBA_F(FFFF)V", ordinal = -1, shift = At.Shift.AFTER, remap = true))
    private void renderLightning(EntityAura2 e, double par2, double par4, double par6, float par9, float var20, float var13, boolean rot, CallbackInfo ci, @Local(name = "tessellator2") LocalRef<Tessellator> tessellator) {
        IEntityAura aura = (IEntityAura) e;
        if (aura.isHasLightning() && aura.getLightningColor() != 0) {
            Color col = Color.decode(aura.getLightningColor() + "");
            tessellator.get().setColorRGBA(col.getRed(), col.getGreen(), col.getBlue(), aura.getLightningAlpha());
        }
    }

    @ModifyArgs(method = "func_tad(LJinRyuu/DragonBC/common/Npcs/EntityAura2;DDDFF)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glScalef(FFF)V", ordinal = 0))
    private void auraSize(Args args, @Local(ordinal = 0) LocalRef<EntityAura2> entityAura) {
        IEntityAura aura = (IEntityAura) entityAura.get();
        if (aura.getSize() != 1f) {
            float xSize = (float) args.get(0) * aura.getSize();
            float ySize = (float) args.get(1) * aura.getSize();
            float zSize = (float) args.get(2) * aura.getSize();

            args.set(0, xSize);
            args.set(1, ySize);
            args.set(2, zSize);

        }
    }

    @Inject(method = "func_tad(LJinRyuu/DragonBC/common/Npcs/EntityAura2;DDDFF)V", at = @At(value = "INVOKE", target = "LJinRyuu/DragonBC/common/Npcs/EntityAura2;getState2()F", ordinal = 0, shift = At.Shift.BEFORE))
    private void fixAuraSize(EntityAura2 par1Entity, double parX, double parY, double parZ, float par8, float par9, CallbackInfo ci, @Local(name = "s1") LocalFloatRef s1, @Local(name = "s") LocalFloatRef s, @Local(name = "cr") LocalFloatRef cr) {
        EntityPlayer auraOwner = par1Entity.worldObj.getPlayerEntityByName(par1Entity.getmot());
        if (auraOwner instanceof EntityPlayer) {
            DBCData dbcData = DBCData.get(auraOwner);
            if (dbcData.addonFormID > -1) {
                float size = JRMCoreHDBC.DBCsizeBasedOnRace2(dbcData.Race, dbcData.State);
                s1.set(8.0f * size);
            }
            s.set(s1.get() * Math.min(dbcData.Release, 50) / 45);
        }
        Entity entity = Utility.getEntityFromID(par1Entity.worldObj, par1Entity.getmot());
        if (entity instanceof EntityCustomNpc) {
            EntityCustomNpc npc = (EntityCustomNpc) entity;
            s1.set(8.0f * 0.2f * npc.display.modelSize);
            s.set(s1.get() * Math.min(par1Entity.getCRel(), 70) / 45);
        }
    }
}
