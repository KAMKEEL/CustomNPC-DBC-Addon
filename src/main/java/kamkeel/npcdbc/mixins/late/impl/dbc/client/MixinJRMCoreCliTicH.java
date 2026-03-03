package kamkeel.npcdbc.mixins.late.impl.dbc.client;

import JinRyuu.JRMCore.JRMCoreCliTicH;
import kamkeel.npcdbc.network.packets.player.DBCLockOn;
import org.spongepowered.asm.lib.Opcodes;
import kamkeel.npcdbc.network.DBCPacketHandler;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JRMCoreCliTicH.class)
public abstract class MixinJRMCoreCliTicH {
    @Inject(method = "onInputEvent", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreCliTicH;lockOn:Lnet/minecraft/entity/EntityLivingBase;", opcode = Opcodes.PUTSTATIC, remap = false, shift = At.Shift.AFTER), remap = false)
    private void onLockOnChanged_input(CallbackInfo ci) {
        sendPacket(JRMCoreCliTicH.lockOn);
    }

    @Inject(method = "onRenderTick", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreCliTicH;lockOn:Lnet/minecraft/entity/EntityLivingBase;", opcode = Opcodes.PUTSTATIC, remap = false, shift = At.Shift.AFTER), remap = false)
    private void onLockOnChanged_render(CallbackInfo ci) {
        sendPacket(JRMCoreCliTicH.lockOn);
    }

    @Unique
    private static void sendPacket(EntityLivingBase value) {
        DBCLockOn.Sync packet = value == null
            ? new DBCLockOn.Sync()
            : new DBCLockOn.Sync(value.getEntityId());
        DBCPacketHandler.Instance.sendToServer(packet);
    }
}
