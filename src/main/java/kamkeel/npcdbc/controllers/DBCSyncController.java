package kamkeel.npcdbc.controllers;

import kamkeel.npcdbc.constants.DBCSyncType;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.DBCInfoSync;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.constants.EnumPacketClient;

import java.util.HashMap;

public class DBCSyncController {

	public static void syncPlayer(EntityPlayerMP player){
        // Sync All Custom Forms
        NBTTagList list = new NBTTagList();
        NBTTagCompound compound = new NBTTagCompound();
        for (Form customForm : FormController.getInstance().customForms.values()) {
            list.appendTag(customForm.writeToNBT());
            if (list.tagCount() > 10) {
                compound = new NBTTagCompound();
                compound.setTag("Data", list);
                PacketHandler.Instance.sendToPlayer(new DBCInfoSync(DBCSyncType.FORM, EnumPacketClient.SYNC_ADD, compound, -1).generatePacket(), player);
                list = new NBTTagList();
            }
        }
        compound = new NBTTagCompound();
        compound.setTag("Data", list);
        PacketHandler.Instance.sendToPlayer(new DBCInfoSync(DBCSyncType.FORM, EnumPacketClient.SYNC_END, compound, -1).generatePacket(), player);

        // Sync All Custom Auras
        list = new NBTTagList();
        compound = new NBTTagCompound();
        for (Aura aura : AuraController.getInstance().customAuras.values()) {
            list.appendTag(aura.writeToNBT());
            if (list.tagCount() > 10) {
                compound = new NBTTagCompound();
                compound.setTag("Data", list);
                PacketHandler.Instance.sendToPlayer(new DBCInfoSync(DBCSyncType.AURA, EnumPacketClient.SYNC_ADD, compound, -1).generatePacket(), player);
                list = new NBTTagList();
            }
        }
        compound = new NBTTagCompound();
        compound.setTag("Data", list);
        PacketHandler.Instance.sendToPlayer(new DBCInfoSync(DBCSyncType.AURA, EnumPacketClient.SYNC_END, compound, -1).generatePacket(), player);
	}


	public static void clientSync(int synctype, NBTTagCompound compound, boolean syncEnd) {
        if(synctype == DBCSyncType.FORM){
            NBTTagList list = compound.getTagList("Data", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                Form form = new Form();
                form.readFromNBT(list.getCompoundTagAt(i));
                FormController.getInstance().customFormsSync.put(form.id, form);
            }
            if (syncEnd) {
                FormController.getInstance().customForms = FormController.getInstance().customFormsSync;
                FormController.getInstance().customFormsSync = new HashMap<Integer, Form>();
            }
        }
        else if(synctype == DBCSyncType.AURA){
            NBTTagList list = compound.getTagList("Data", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                Aura aura = new Aura();
                aura.readFromNBT(list.getCompoundTagAt(i));
                AuraController.getInstance().customAurasSync.put(aura.id, aura);
            }
            if (syncEnd) {
                AuraController.getInstance().customAuras = AuraController.getInstance().customAurasSync;
                AuraController.getInstance().customAurasSync = new HashMap<Integer, Aura>();
            }
        }
	}

	public static void clientSyncUpdate(int synctype, NBTTagCompound compound) {
        if(synctype == DBCSyncType.FORM){
            Form form = new Form();
            form.readFromNBT(compound);
            FormController.getInstance().customForms.put(form.id, form);
        }
        else if(synctype == DBCSyncType.AURA){
            Aura aura = new Aura();
            aura.readFromNBT(compound);
            AuraController.getInstance().customAuras.put(aura.id, aura);
        }
	}

	public static void clientSyncRemove(int synctype, int id) {
		if(synctype == DBCSyncType.FORM){
            Form form = FormController.Instance.customForms.remove(id);
        }
        else if(synctype == DBCSyncType.AURA){
            Aura aura = AuraController.Instance.customAuras.remove(id);
        }
	}
}
