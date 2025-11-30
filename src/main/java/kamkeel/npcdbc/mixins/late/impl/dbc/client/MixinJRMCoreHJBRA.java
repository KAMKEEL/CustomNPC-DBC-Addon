package kamkeel.npcdbc.mixins.late.impl.dbc.client;

import JinRyuu.JRMCore.JRMCoreHJBRA;
import JinRyuu.JRMCore.JRMCoreHSAC;
import JinRyuu.JRMCore.entity.ModelBipedBody;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import noppes.npcs.entity.EntityCustomNpc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = JRMCoreHJBRA.class, remap = false)
public abstract class MixinJRMCoreHJBRA {

    @Inject(method = "showModel", at = @At("HEAD"), remap = false)
    private static void injectRender(ModelBiped m, EntityLivingBase entityLiving, ItemStack is, int par2, CallbackInfoReturnable<ModelBiped> cir) {

    }

    @Inject(method = "modelHelper", at = @At("HEAD"), remap = false)
    private static void injectNPCModelData(EntityLivingBase entityLiving, ModelBipedBody mdl, CallbackInfo ci) {
        if (entityLiving instanceof EntityCustomNpc) {
            EntityCustomNpc npc = (EntityCustomNpc) entityLiving;
            DBCDisplay display = ((INPCDisplay) npc.display).getDBCDisplay();
            ModelBipedBody.g = display.isFemaleInternal() ? 2 : 1;
            ModelBipedBody.p = 0;
            ModelBipedBody.f = 1;
            mdl.b = display.breastSize;

            mdl.blk = false;
            mdl.instantTransmission = false;
            mdl.KiAttack = 0;
            mdl.heldItemRight = 0;
            mdl.aimedBow = false;

        }
    }
}
