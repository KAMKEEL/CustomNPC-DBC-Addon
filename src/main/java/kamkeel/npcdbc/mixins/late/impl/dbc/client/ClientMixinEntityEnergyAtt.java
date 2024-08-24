package kamkeel.npcdbc.mixins.late.impl.dbc.client;

import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.entity.EntityEnAttacks;
import JinRyuu.JRMCore.entity.EntityEnergyAtt;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityEnergyAtt.class)
public abstract class ClientMixinEntityEnergyAtt extends EntityEnAttacks {
    public ClientMixinEntityEnergyAtt(World par1World) {
        super(par1World);
    }


    @Redirect(method = "setDead", at=@At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;isShtng:Z", opcode = Opcodes.PUTSTATIC), remap = true)
    private void fixKiChargeReset_setDead(boolean value){
        if(this.shootingEntity == Minecraft.getMinecraft().thePlayer){
            JRMCoreH.isShtng = value;
        }
    }

    @Redirect(method = "onUpdate", at=@At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;isShtng:Z", opcode = Opcodes.PUTSTATIC), remap = true)
    private void fixKiChargeReset_onUpdate(boolean value){
        if(this.shootingEntity == Minecraft.getMinecraft().thePlayer){
            JRMCoreH.isShtng = value;
        }
    }
}
