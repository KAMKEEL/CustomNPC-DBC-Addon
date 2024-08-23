package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.entity.EntityEnAttacks;
import JinRyuu.JRMCore.entity.EntityEnergyAtt;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityEnergyAtt.class, remap = false)
public abstract class MixinEntityEnergyAtt extends EntityEnAttacks {

    public MixinEntityEnergyAtt(World par1World) {
        super(par1World);
    }

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void target(Entity entity, CallbackInfo ci) {
        EntityEnergyAtt ki = (EntityEnergyAtt) (Object) this;
        if (ki.shootingEntity == entity && ki.shootingEntity instanceof EntityNPCInterface && ki.ticksExisted < 60)
            ci.cancel();
    }

    @Redirect(method = "setDead", at=@At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;isShtng:Z", opcode = Opcodes.PUTSTATIC), remap = true)
    private void fixKiChargeReset(boolean value){
        if(this.shootingEntity == Minecraft.getMinecraft().thePlayer){
            JRMCoreH.isShtng = value;
        }
    }
}
