package kamkeel.npcdbc.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.controllers.TransformController;
import kamkeel.npcdbc.data.CustomForm;
import kamkeel.npcdbc.data.DBCData;
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
        if (formData != null && formData.hasSelectedForm()) {
            if (formData.isInCustomForm()) {
                CustomForm form = formData.getCurrentForm();
                if (form.hasChild() && formData.hasUnlocked(form.getChildID()))
                    TransformController.Ascend(form.getC());
            } else
                TransformController.Ascend(formData.getSelectedForm());

        }
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent e) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen == null && KeyHandler.AscendKey.isPressed()) {
            PlayerCustomFormData formData = Utility.getSelfData();
            if (formData != null) {
                if (formData.selectedForm == -1)
                    Utility.sendMessage(mc.thePlayer, "§cYou have not selected a custom form!");
                else if (formData.isInCustomForm()) {
                    if (TransformController.rage > 0 && TransformController.transformed) {
                        Utility.sendMessage(mc.thePlayer, "§cYou need to cool down!");
                        return;
                    }
                    CustomForm form = formData.getCurrentForm();
                    if (form.hasChild() && !formData.hasUnlocked(form.getChildID()))
                        Utility.sendMessage(mc.thePlayer, "§cYou do not have the next transformation unlocked!");
                }
            }
        }
    }

    @SubscribeEvent
    public void logoutEvent(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        ClientCache.clientDataCache.clear();
    }

}
