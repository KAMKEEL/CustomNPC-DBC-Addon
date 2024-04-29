package kamkeel.npcdbc.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.controllers.TransformController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.util.PlayerDataUtil;
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
        PlayerDBCInfo formData = PlayerDataUtil.getClientDBCInfo();
        if (formData != null && formData.hasSelectedForm()) {
            Form form = formData.getSelectedForm();
            DBCData dbcData = DBCData.getClient();
            if (dbcData.stats.isFusionSpectator())
                return;
            float healthReq = (form.mastery.healthRequirement >= 100f || form.mastery.healthRequirement <= 0f) ?
                150 : form.mastery.healthRequirement * form.mastery.calculateMulti("healthRequirement", formData.getFormLevel(form.id));
            if (dbcData.stats.getCurrentBodyPercentage() > healthReq)
                return;
            if (form.mastery.hasHeat() && dbcData.Pain > 0)
                return;

            if (formData.isInCustomForm()) {
                form = formData.getCurrentForm();
                if (form.hasChild() && formData.hasFormUnlocked(form.getChildID())) {
                    IForm child = form.getChild();
                    if (verifyFormTransform((Form) child))
                        TransformController.Ascend((Form) child);
                }
            } else if (verifyFormTransform(formData.getSelectedForm()))
                TransformController.Ascend(formData.getSelectedForm());
        }
    }

    private boolean verifyFormTransform(Form form) {
        if (form == null)
            return false;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null)
            return false;

        PlayerDBCInfo formData = PlayerDataUtil.getClientDBCInfo();
        if (formData == null)
            return false;

        DBCData dbcData = DBCData.getClient();
        // Check for in Required DBC Form before Transforming
        if (!form.mastery.canInstantTransform(formData.getFormLevel(form.id)) && form.requiredForm.containsKey((int) dbcData.Race)) {
            if (form.requiredForm.get((int) dbcData.Race) != dbcData.State) {
                Utility.sendMessage(mc.thePlayer, "§cYou are not in the right DBC form");
                return false;
            }
        } else {
            // Must be in Parent Form to Transform
            if (!form.mastery.canInstantTransform(formData.getFormLevel(form.id)) && form.isFromParentOnly() && form.parentID != -1 && form.parentID != formData.currentForm) {
                Utility.sendMessage(mc.thePlayer, "§cYou are not in the correct parent form");
                return false;
            }
        }
        return true;
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent e) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen == null) {
            PlayerDBCInfo formData = PlayerDataUtil.getClientDBCInfo();
            if (formData != null) {
                if (KeyHandler.AscendKey.isPressed()) {
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
                    } else {
                        Form form = formData.getSelectedForm();
                        if (form != null) {
                            if (DBCData.getClient().stats.isFusionSpectator()){
                                Utility.sendMessage(mc.thePlayer, "§cYou cannot transform as a spectator");
                                return;
                            }
                            float healthReq = (form.mastery.healthRequirement >= 100f || form.mastery.healthRequirement <= 0f) ?
                                150 : form.mastery.healthRequirement * form.mastery.calculateMulti("healthRequirement", formData.getFormLevel(form.id));

                            if (DBCData.getClient().stats.getCurrentBodyPercentage() > healthReq) {
                                Utility.sendMessage(mc.thePlayer, "§cYou need to be below " + (int) healthReq + "% health to access the selected form!");
                                return;
                            }

                            if (form.mastery.hasHeat() && DBCData.getClient().Pain > 0){
                                Utility.sendMessage(mc.thePlayer, "§cFailed to transform, you are in pain!");
                                return;
                            }
                        }
                    }
                }
            }
        }

    }

    @SubscribeEvent
    public void logoutEvent(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        ClientCache.clientDataCache.clear();
    }

}
