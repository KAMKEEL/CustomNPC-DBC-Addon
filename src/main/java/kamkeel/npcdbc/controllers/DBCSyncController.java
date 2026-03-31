package kamkeel.npcdbc.controllers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.constants.DBCSyncType;
import kamkeel.npcdbc.controllers.sync.DBCSyncHandler;
import kamkeel.npcdbc.controllers.sync.DBCSyncRegistry;
import kamkeel.npcdbc.controllers.sync.handlers.*;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.get.DBCInfoSyncPacket;
import kamkeel.npcs.network.enums.EnumSyncAction;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.LogWriter;

import java.util.Map;

public class DBCSyncController {

    public static void load() {
        DBCSyncRegistry.clear();

        DBCSyncRegistry.register(DBCSyncType.FORM, new FormSyncHandler());
        DBCSyncRegistry.register(DBCSyncType.AURA, new AuraSyncHandler());
        DBCSyncRegistry.register(DBCSyncType.OUTLINE, new OutlineSyncHandler());
        DBCSyncRegistry.register(DBCSyncType.SKILL, new SkillSyncHandler());

        DBCSyncRegistry.validateRegistrations(
            DBCSyncType.FORM,
            DBCSyncType.AURA,
            DBCSyncType.OUTLINE,
            DBCSyncType.SKILL 
        );
    }

    /**
     * (Server side) Fires on Player login, sync all registered handlers
     * @param player
     */
    public static void syncPlayer(EntityPlayerMP player) {
        for(Map.Entry<Integer, DBCSyncHandler> handlers : DBCSyncRegistry.getAll().entrySet())
            syncType(handlers.getKey(), handlers.getValue(), player);
    }

    private static void syncType(int syncType, DBCSyncHandler handler, EntityPlayerMP player) {
        if (handler == null) return;
        try {
            NBTTagCompound compound = handler.serializeAll();
            if (compound != null) 
                DBCPacketHandler.Instance.sendToPlayer(new DBCInfoSyncPacket(syncType, EnumSyncAction.RELOAD, -1, compound), player);
            
        } catch (Exception e) {
            LogWriter.error("Failed to serialize DBC sync type " + syncType + " for " + player.getCommandSenderName(), e);
        }
    }

    /** Reloads all objects for the given syncType*/
    @SideOnly(Side.CLIENT)
    public static void clientSyncReload(int syncType, NBTTagCompound compound) {
        DBCSyncHandler handler = DBCSyncRegistry.getHandler(syncType);
        if (handler == null) return;
        try {
            handler.clientHandleReload(compound);
        } catch (Exception e) {
            LogWriter.error("Failed to handle DBC RELOAD for sync type " + syncType, e);
        }
    }

    /** Updates a single object for the given syncType*/
    @SideOnly(Side.CLIENT)
    public static void clientSyncUpdate(int syncType, NBTTagCompound compound) {
        DBCSyncHandler handler = DBCSyncRegistry.getHandler(syncType);
        if (handler == null) return;
        try {
            handler.clientHandleUpdate(compound);
        } catch (Exception e) {
            LogWriter.error("Failed to handle DBC UPDATE for sync type " + syncType, e);
        }
    }

    /** Removes a single object for the given syncType*/
    @SideOnly(Side.CLIENT)
    public static void clientSyncRemove(int syncType, int id, NBTTagCompound compound) {
        DBCSyncHandler handler = DBCSyncRegistry.getHandler(syncType);
        if (handler == null) return;
        try {
            handler.clientHandleRemove(id);
            handler.clientHandleRemove(compound); // For key based handlers
        } catch (Exception e) {
            LogWriter.error("Failed to handle DBC REMOVE for sync type " + syncType, e);
        }
    }
}
