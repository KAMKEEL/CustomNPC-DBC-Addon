package kamkeel.npcdbc.controllers;

import JinRyuu.DragonBC.common.DBCKiTech;
import JinRyuu.JRMCore.JRMCoreH;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.constants.enums.EnumNBTType;
import kamkeel.npcdbc.data.DBCData;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.DBCSetValPacket;
import kamkeel.npcdbc.network.packets.TransformPacket;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import static JinRyuu.JRMCore.JRMCoreH.isInBaseForm;
import static JinRyuu.JRMCore.JRMCoreH.rc_arc;

public class TransformController {

    public static int s, id, time, releaseTime, soundTime;
    public static boolean ascending, cantTransform, transformed;
    public static String ascendSound, descendSound;
    public static float rage, rageValue;
    public static DBCData dbcData;
    public static Form transformedInto;

    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    // Client  side handling

    //WIP, only 85% done, but is functional and won't break
    @SideOnly(Side.CLIENT)
    public static void Ascend(Form form) {
        Form currentForm = Utility.getCurrentForm(Minecraft.getMinecraft().thePlayer);
        if (cantTransform || (rage > 0 && transformed) || currentForm != null && currentForm.getID() == form.id)
            return;
        dbcData = DBCData.get(Minecraft.getMinecraft().thePlayer);
        if (dbcData == null || JRMCoreH.curRelease <= 0 || JRMCoreH.curEnergy <= 0)
            return;

        time++;
        releaseTime++;
        soundTime++;
        TransformController.setAscending(true);
        rageValue = getRageMeterIncrementation(form);
        rage += rageValue;
        JRMCoreH.TransSaiCurRg = (byte) rage;
        PacketHandler.Instance.sendToServer(new DBCSetValPacket(CustomNpcPlusDBC.proxy.getClientPlayer(), EnumNBTType.INT, "jrmcSaiRg", (int) rage).generatePacket());

        if (soundTime == 1 || soundTime >= 33) { //plays aura sound every 33 ticks
            if (soundTime != 1)
                soundTime = 0;
            String ar = "jinryuudragonbc:DBC.aura";
            DBCKiTech.soundAsc(ar);
        }
        if (time >= 6) { //increments rage meter and drain ki cost every 6 ticks
            time = 0;
            int cost = JRMCoreH.maxEnergy / 20;
            if (JRMCoreH.curEnergy < cost)
                return;
            JRMCoreH.Cost(cost);

        }
        if (JRMCoreH.curRelease < 50 && releaseTime >= 10) { //if release is less than 50%, increment it until it is so
            float en = 100.0F / JRMCoreH.maxEnergy * JRMCoreH.curEnergy;
            float re = JRMCoreH.curRelease;
            en = en > 75.0F ? 75.0F : en;
            if (re >= 50.0F ? (re - 50.0F < (en - 50.0F) * 2.0F) : (re < ((en <= 10.0F) ? (en * 5.0F) : 50.0F)))
                JRMCoreH.Rls((byte) 1);
            releaseTime = 0;

        }
        if (rage >= 100) { //transform when rage meter reaches 100 (max)
            PacketHandler.Instance.sendToServer(new TransformPacket(Minecraft.getMinecraft().thePlayer, form.getID(), true).generatePacket());
            DBCKiTech.soundAsc(form.getAscendSound());
            resetTimers();
            cantTransform = true;
            transformed = true;
            transformedInto = form;
        }
    }

    @SideOnly(Side.CLIENT)
    public static void decrementRage() {
        dbcData = DBCData.get(Minecraft.getMinecraft().thePlayer);
        if (dbcData == null || rage == 0)
            return;

        if (rage > 0) {
            if (rage > 100)
                rage = 100;
            if (rage - (rageValue) >= 0)
                rage -= (rageValue);
            else {
                rage = 0;
                transformedInto = null;
            }
            if (rage <= 50 && JRMCoreH.StusEfctsMe(1))
                JRMCoreH.Skll((byte) 5, (byte) 1, (byte) 1);
        }
        if (rage == 0 && (JRMCoreH.StusEfctsMe(1) || cantTransform || transformed || ascending))
            setAscending(false);

        JRMCoreH.TransSaiCurRg = (byte) rage;
        PacketHandler.Instance.sendToServer(new DBCSetValPacket(CustomNpcPlusDBC.proxy.getClientPlayer(), EnumNBTType.INT, "jrmcSaiRg", (int) rage).generatePacket());
    }

    @SideOnly(Side.CLIENT)
    public static void setAscending(boolean bo) {
        ascending = bo;
        if (time == 1 || !bo)
            JRMCoreH.Skll((byte) 5, bo ? (byte) 0 : 1, (byte) 1);
        if (!bo) {
            cantTransform = false;
            transformed = false;
            transformedInto = null;
        }
    }

    @SideOnly(Side.CLIENT)
    public static void resetTimers() {
        time = 0;
        releaseTime = 0;
        soundTime = 0;
    }

    @SideOnly(Side.CLIENT)
    public static float getRageMeterIncrementation(Form form) {
        PlayerDBCInfo formData = Utility.getSelfData();
        float curLevel = formData.getFormLevel(form.id);
        float maxLevel = form.getMastery().getMaxLevel();
        float ratio = curLevel / maxLevel;

        if (form.getMastery().hasInstantTransformationUnlockLevel())
            if (curLevel >= form.getMastery().getInstantTransformationUnlockLevel())
                return 15;

        if (Utility.percentBetween(curLevel, maxLevel, 0, 5))
            return 1;
        else if (Utility.percentBetween(curLevel, maxLevel, 5, 101))
            return 15 * ratio;
        return 0;
    }

    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    // Server side handling

    public static void handleFormAscend(EntityPlayer player, int formID) {
        Form form = (Form) FormController.getInstance().get(formID);
        if(form == null)
            return;

        PlayerDBCInfo formData = Utility.getData(player);
        if (formData.currentForm != formID) {
            DBCData dbcData = DBCData.get(player);
            // Check for in Required DBC Form before Transforming
            if(form.requiredForm.containsKey((int) dbcData.Race)){
                if(form.requiredForm.get((int) dbcData.Race) != dbcData.State)
                    return;
            } else {
                // Must be in Parent Form to Transform
                if(form.isFromParentOnly() && form.parentID != -1 && form.parentID != formData.currentForm)
                    return;
            }

            int prevID = formData.currentForm != 1 ? formData.currentForm : dbcData.State;
            if (DBCEventHooks.onFormChangeEvent(new DBCPlayerEvent.FormChangeEvent(Utility.getIPlayer(player), formData.currentForm != 1, prevID, true, formID)))
                return;

            if (!isInBaseForm(dbcData.Race, dbcData.State)) {
                if (!form.stackable.vanillaStackable){
                    if (rc_arc(dbcData.Race) && dbcData.State >= 4)
                        dbcData.State = 4;
                    else
                        dbcData.State = 0;
                }
            }

            formData.currentForm = formID;
            if (formData.getForm(formID).hasTimer())
                formData.addTimer(formID, formData.getForm(id).getTimer());

            formData.updateClient();
            Utility.sendMessage(player, "§aTransformed to§r " + formData.getCurrentForm().getMenuName());
            dbcData.saveNBTData();
        }
    }

    public static void handleFormDescend(EntityPlayer player) {
        PlayerDBCInfo formData = Utility.getData(player);
        if (formData.isInCustomForm()) {
            Form form = formData.getCurrentForm();
            DBCData dbcData = DBCData.get(player);

            Form parent = FormController.getInstance().customForms.get(form.getParentID());
            boolean intoParent = parent != null && formData.hasFormUnlocked(form.getParentID());

            int prevID = formData.currentForm != 1 ? formData.currentForm : dbcData.State;
            if (DBCEventHooks.onFormChangeEvent(new DBCPlayerEvent.FormChangeEvent(Utility.getIPlayer(player), formData.currentForm != 1, prevID, true, intoParent ? form.getParentID() : -1)))
                return;

            if(form.requiredForm.containsKey((int) dbcData.Race)){
                formData.currentForm = -1;
                Utility.sendMessage(player, "§cDescended from§r " + form.getMenuName());
                dbcData.State = form.requiredForm.get((int) dbcData.Race);
            }
            else {
                if (intoParent) {
                    Utility.sendMessage(player, "§cDescended into§r " + form.getParent().getMenuName());
                    formData.currentForm = form.getParentID();
                } else if (formData.getTimer(form.id) == 0) {
                    Utility.sendMessage(player, "§cTimer for§r " + form.getMenuName() + "§c has ran out!");
                } else {
                    Utility.sendMessage(player, "§cDescended from§r " + form.getMenuName());
                    formData.currentForm = -1;
                }
            }

            formData.updateClient();
            JRMCoreH.setByte(0, player, "jrmcSaiRg");
            dbcData.saveNBTData();
        }
    }
}
