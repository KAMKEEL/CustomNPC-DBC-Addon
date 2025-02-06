package kamkeel.npcdbc.controllers;

import kamkeel.npcdbc.constants.DBCSyncType;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.outline.Outline;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.DBCInfoSyncPacket;
import kamkeel.npcs.network.enums.EnumSyncAction;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.HashMap;

public class DBCSyncController {

    public static void syncPlayer(EntityPlayerMP player) {
        // Sync All Custom Forms
        NBTTagList list = new NBTTagList();
        for (Form customForm : FormController.getInstance().customForms.values()) {
            list.appendTag(customForm.writeToNBT());
        }
        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("Data", list);
        DBCPacketHandler.Instance.sendToPlayer(new DBCInfoSyncPacket(DBCSyncType.FORM, EnumSyncAction.RELOAD, -1, compound), player);

        // Sync All Custom Auras
        list = new NBTTagList();
        compound = new NBTTagCompound();
        for (Aura aura : AuraController.getInstance().customAuras.values()) {
            list.appendTag(aura.writeToNBT());
        }
        compound.setTag("Data", list);
        DBCPacketHandler.Instance.sendToPlayer(new DBCInfoSyncPacket(DBCSyncType.AURA, EnumSyncAction.RELOAD, -1, compound), player);

        // Sync All Custom Outlines
        list = new NBTTagList();
        compound = new NBTTagCompound();
        for (Outline outline : OutlineController.getInstance().customOutlines.values()) {
            list.appendTag(outline.writeToNBT());
        }
        compound.setTag("Data", list);
        DBCPacketHandler.Instance.sendToPlayer(new DBCInfoSyncPacket(DBCSyncType.OUTLINE, EnumSyncAction.RELOAD, -1, compound), player);
    }

    public static void clientSync(int synctype, NBTTagCompound compound) {
        if (synctype == DBCSyncType.FORM) {
            NBTTagList list = compound.getTagList("Data", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                Form form = new Form();
                form.readFromNBT(list.getCompoundTagAt(i));
                FormController.getInstance().customFormsSync.put(form.id, form);
            }

            FormController.getInstance().customForms = FormController.getInstance().customFormsSync;
            FormController.getInstance().customFormsSync = new HashMap<>();
        } else if (synctype == DBCSyncType.AURA) {
            NBTTagList list = compound.getTagList("Data", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                Aura aura = new Aura();
                aura.readFromNBT(list.getCompoundTagAt(i));
                AuraController.getInstance().customAurasSync.put(aura.id, aura);
            }

            AuraController.getInstance().customAuras = AuraController.getInstance().customAurasSync;
            AuraController.getInstance().customAurasSync = new HashMap<>();
        } else if (synctype == DBCSyncType.OUTLINE) {
            NBTTagList list = compound.getTagList("Data", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                Outline outline = new Outline();
                outline.readFromNBT(list.getCompoundTagAt(i));
                OutlineController.getInstance().customOutlinesSync.put(outline.id, outline);
            }

            OutlineController.getInstance().customOutlines = OutlineController.getInstance().customOutlinesSync;
            OutlineController.getInstance().customOutlinesSync = new HashMap<>();
        }
    }

    public static void clientSyncUpdate(int synctype, NBTTagCompound compound) {
        if (synctype == DBCSyncType.FORM) {
            Form form = new Form();
            form.readFromNBT(compound);
            FormController.getInstance().customForms.put(form.id, form);
        } else if (synctype == DBCSyncType.AURA) {
            Aura aura = new Aura();
            aura.readFromNBT(compound);
            AuraController.getInstance().customAuras.put(aura.id, aura);
        } else if (synctype == DBCSyncType.OUTLINE) {
            Outline outline = new Outline();
            outline.readFromNBT(compound);
            OutlineController.getInstance().customOutlines.put(outline.id, outline);
        }
    }

    public static void clientSyncRemove(int synctype, int id) {
        if (synctype == DBCSyncType.FORM) {
            Form form = FormController.Instance.customForms.remove(id);
        } else if (synctype == DBCSyncType.AURA) {
            Aura aura = AuraController.Instance.customAuras.remove(id);
        } else if (synctype == DBCSyncType.OUTLINE) {
            Outline outline = OutlineController.Instance.customOutlines.remove(id);
        }
    }
}
