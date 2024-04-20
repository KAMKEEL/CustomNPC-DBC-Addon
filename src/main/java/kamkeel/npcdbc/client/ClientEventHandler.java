package kamkeel.npcdbc.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.controllers.TransformController;
import kamkeel.npcdbc.data.PlayerCustomFormData;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;


public class ClientEventHandler {

    @SubscribeEvent
    public void onSkill(TickEvent.PlayerTickEvent event) {
        if (event.side == Side.SERVER || event.player == null)
            return;

        if (event.phase == TickEvent.Phase.START) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.currentScreen == null && KeyHandler.AscendKey.getIsKeyPressed()) {
                performAscend();
            } else {
                TransformController.decrementRage();
            }
        }
    }

    private void performAscend() {
        PlayerCustomFormData formData = Utility.getSelfData();
        if (formData != null && formData.selectedForm > -1)
            TransformController.Ascend(Utility.getSelfData().getSelectedForm());

    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent e) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen == null && KeyHandler.AscendKey.getIsKeyPressed()) {
            PlayerCustomFormData formData = Utility.getSelfData();
            if (formData != null && formData.selectedForm == -1)
                Utility.sendMessage(mc.thePlayer, "§cYou have not selected a custom form!");
        }
    }
}
