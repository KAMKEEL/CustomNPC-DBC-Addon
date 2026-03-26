package kamkeel.npcdbc.mixins.late.impl.dbc.client;

import JinRyuu.JRMCore.JRMCoreCliTicH;
import JinRyuu.JRMCore.JRMCoreGui;
import kamkeel.npcdbc.client.gui.dbc.action.ActionMenuActionGateway;
import kamkeel.npcdbc.client.gui.dbc.action.ActionMenuScreen;
import kamkeel.npcdbc.client.gui.dbc.action.ActionMenuState;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.player.DBCLockOn;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JRMCoreCliTicH.class)
public abstract class MixinJRMCoreCliTicH {

    @Shadow(remap = false) static int actionSelectID;
    @Shadow(remap = false) static int actionNPA;
    @Shadow(remap = false) static boolean actionNBO;

    @Unique
    private static final ActionMenuState npcdbc$state = new ActionMenuState();
    @Unique
    private static final ActionMenuActionGateway npcdbc$gateway = new ActionMenuActionGateway();
    @Unique
    private static final ActionMenuScreen npcdbc$screen = new ActionMenuScreen(npcdbc$state, npcdbc$gateway);

    @Redirect(method = "onRenderTick", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreGui;renderActionMenu()V"), remap = false)
    private void npcdbc$redirectActionMenuRender(JRMCoreGui instance) {
        npcdbc$state.setPageIndex(actionNPA);
        npcdbc$state.setCenterDebounce(actionNBO);

        npcdbc$screen.render();

        actionSelectID = npcdbc$state.getActionSelectID();
        actionNPA = npcdbc$state.getPageIndex();
        actionNBO = npcdbc$state.isCenterDebounce();
    }

    @Inject(method = "onInputEvent", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreCliTicH;lockOn:Lnet/minecraft/entity/EntityLivingBase;", opcode = Opcodes.PUTSTATIC, remap = false, shift = At.Shift.AFTER), remap = false)
    private void onLockOnChanged_input(CallbackInfo ci) {
        npcdbc$sendPacket(JRMCoreCliTicH.lockOn);
    }

    @Inject(method = "onRenderTick", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreCliTicH;lockOn:Lnet/minecraft/entity/EntityLivingBase;", opcode = Opcodes.PUTSTATIC, remap = false, shift = At.Shift.AFTER), remap = false)
    private void onLockOnChanged_render(CallbackInfo ci) {
        npcdbc$sendPacket(JRMCoreCliTicH.lockOn);
    }

    @Unique
    private static void npcdbc$sendPacket(EntityLivingBase value) {
        DBCLockOn.Sync packet = value == null
            ? new DBCLockOn.Sync()
            : new DBCLockOn.Sync(value.getEntityId());
        DBCPacketHandler.Instance.sendToServer(packet);
    }
}
