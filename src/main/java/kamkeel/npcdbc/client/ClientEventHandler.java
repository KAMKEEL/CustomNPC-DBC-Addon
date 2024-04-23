package kamkeel.npcdbc.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.controllers.TransformController;
import kamkeel.npcdbc.data.DBCData;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.form.Form;
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
        PlayerDBCInfo formData = Utility.getSelfData();
        if (formData != null && formData.hasSelectedForm()) {
            if (formData.isInCustomForm()) {
                Form form = formData.getCurrentForm();
                if (form.hasChild() && formData.hasFormUnlocked(form.getChildID())){
                    IForm child = form.getChild();
                    if(verifyFormTransform((Form) child))
                        TransformController.Ascend((Form) child);
                }
            } else
                if(verifyFormTransform(formData.getSelectedForm()))
                    TransformController.Ascend(formData.getSelectedForm());
        }
    }

    private boolean verifyFormTransform(Form form){
        if(form == null)
            return false;

        Minecraft mc = Minecraft.getMinecraft();
        if(mc.thePlayer == null)
            return false;

        PlayerDBCInfo formData = Utility.getSelfData();
        if(formData == null)
            return false;

        DBCData dbcData = DBCData.getClient();
        // Check for in Required DBC Form before Transforming
        if(form.requiredForm.containsKey((int) dbcData.Race)){
            if(form.requiredForm.get((int) dbcData.Race) != dbcData.State){
                Utility.sendMessage(mc.thePlayer, "§cYou are not in the right DBC form");
                return false;
            }
        } else {
            // Must be in Parent Form to Transform
            if(form.isFromParentOnly() && form.parentID != -1 && form.parentID != formData.currentForm){
                Utility.sendMessage(mc.thePlayer, "§cYou are not in the correct parent form");
                return false;
            }
        }
        return true;
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent e) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen == null && KeyHandler.AscendKey.isPressed()) {
            PlayerDBCInfo formData = Utility.getSelfData();
            if (formData != null) {
                if (formData.selectedForm == -1)
                    Utility.sendMessage(mc.thePlayer, "§cYou have not selected a custom form!");
                else if (formData.isInCustomForm()) {
                    if (TransformController.rage > 0 && TransformController.transformed) {
                        Utility.sendMessage(mc.thePlayer, "§cYou need to cool down!");
                        return;
                    }
                    Form form = formData.getCurrentForm();
                    if (form.hasChild() && !formData.hasFormUnlocked(form.getChildID()))
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
