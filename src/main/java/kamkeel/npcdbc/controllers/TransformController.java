package kamkeel.npcdbc.controllers;

import JinRyuu.DragonBC.common.DBCKiTech;
import JinRyuu.JRMCore.JRMCoreH;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.constants.enums.EnumNBTType;
import kamkeel.npcdbc.data.CustomForm;
import kamkeel.npcdbc.data.DBCData;
import kamkeel.npcdbc.data.PlayerCustomFormData;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.DBCSetValue;
import kamkeel.npcdbc.network.packets.TransformPacket;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;

public class TransformController {

    public static int s, id, time, releaseTime, soundTime;
    public static boolean ascending, cantTransform, transformed;
    public static String ascendSound, descendSound;
    public static float rage, rageValue;
    public static DBCData dbcData;

    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    // Client  side handling

    //WIP, only 85% done, but is functional and won't break
    @SideOnly(Side.CLIENT)
    public static void Ascend(CustomForm form) {
        if (cantTransform || (rage > 0 && transformed) || (Utility.getSelfData() != null && Utility.getSelfData().getCurrentForm() != null && Utility.getSelfData().getCurrentForm().getID() == form.id))
            return;
        dbcData = DBCData.get(Minecraft.getMinecraft().thePlayer);
        if (dbcData == null)
            return;

        time++;
        releaseTime++;
        soundTime++;
        TransformController.setAscending(true);
        rageValue = getRageMeterIncrementation();
        rage += rageValue;
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
        }

        JRMCoreH.TransSaiCurRg = (byte) rage;
        PacketHandler.Instance.sendToServer(new DBCSetValue(CustomNpcPlusDBC.proxy.getClientPlayer(), EnumNBTType.INT, "jrmcSaiRg", (int) rage).generatePacket());
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
            else
                rage = 0;
            if (rage <= 50 && JRMCoreH.StusEfctsMe(1))
                JRMCoreH.Skll((byte) 5, (byte) 1, (byte) 1);
        }
        if (rage == 0 && (JRMCoreH.StusEfctsMe(1) || cantTransform || transformed || ascending))
            setAscending(false);

        JRMCoreH.TransSaiCurRg = (byte) rage;
        dbcData.getRawCompound().setInteger("jrmcSaiRg", (int) rage);
    }

    @SideOnly(Side.CLIENT)
    public static void setAscending(boolean bo) {
        ascending = bo;
        JRMCoreH.Skll((byte) 5, bo ? (byte) 0 : 1, (byte) 1);
        if (!bo) {
            cantTransform = false;
            transformed = false;
        }
    }

    @SideOnly(Side.CLIENT)
    public static void resetTimers() {
        time = 0;
        releaseTime = 0;
        soundTime = 0;
    }

    @SideOnly(Side.CLIENT)
    public static float getRageMeterIncrementation() {
        double fm = 6;//getFormMasteryValue(k);
        double maxfm = 100;//getMaxFormMasteryValue(k);

        if (Utility.percentBetween(fm, maxfm, 0, 5))
            return 3;
        else if (Utility.percentBetween(fm, maxfm, 5, 10))
            return 6f;
        else if (Utility.percentBetween(fm, maxfm, 10, 20))
            return 10;
        else if (Utility.percentBetween(fm, maxfm, 20, 30))
            return 15;
        else if (Utility.percentBetween(fm, maxfm, 30, 40))
            return 20;
        else if (Utility.percentBetween(fm, maxfm, 40, 50))
            return 25;
        else if (Utility.percentBetween(fm, maxfm, 50, 60))
            return 30;
        else if (Utility.percentBetween(fm, maxfm, 60, 70))
            return 35;
        else if (Utility.percentBetween(fm, maxfm, 70, 80))
            return 40;
        else if (Utility.percentBetween(fm, maxfm, 80, 90))
            return 45;
        else if (Utility.percentBetween(fm, maxfm, 90, 100))
            return 50;
        else if (fm >= maxfm)
            return 100;
        return 0;
    }

    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    // Server side handling

    public static void handleCustomFormAscend(EntityPlayerMP p, int formID) {
        PlayerCustomFormData formData = Utility.getFormData(p);
        if (formData.currentForm != formID) {
            DBCData dbcData = DBCData.get(p);
            formData.currentForm = formID;
            if (dbcData.State > 0)
                dbcData.State = 0;
            formData.updateClient();
            Utility.sendMessage(p, "§aTransformed to§r " + formData.getCurrentForm().getMenuName());
            dbcData.saveNBTData(null);
        }
    }

    public static void handleCustomFormDescend(EntityPlayerMP p) {
        PlayerCustomFormData formData = Utility.getFormData(p);
        if (formData.isInCustomForm()) {
            Utility.sendMessage(p, "§cDescended from§r " + formData.getCurrentForm().getMenuName());
            formData.currentForm = -1;
            formData.updateClient();
            JRMCoreH.setByte(0, p, "jrmcSaiRg");
            DBCData.get(p).saveNBTData(null);
        }
    }


}
