package kamkeel.npcdbc.data.dbcdata;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.ClientCache;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.config.ConfigClient;
import noppes.npcs.util.CacheHashMap;

public class DBCDataUniversal {

    /**
     * Data is cleared after 10 minutes
     **/
    public static final CacheHashMap<String, CacheHashMap.CachedObject<DBCData>> dbcDataCache = new CacheHashMap<>((long) ConfigClient.CacheLife * 60 * 1000);


    /**
     * Static / Universal DBC Data
     * Functionality for Inheritance
     * Making Code a lot cleaner to read and process
     */
    public static DBCData getData(EntityPlayer player) {
        synchronized (dbcDataCache) {
            if (!dbcDataCache.containsKey(player.getCommandSenderName()))
                dbcDataCache.put(player.getCommandSenderName(), new CacheHashMap.CachedObject<>(new DBCData(player)));
            return dbcDataCache.get(player.getCommandSenderName()).getObject();
        }
    }

    public static DBCData get(EntityPlayer player) {
        DBCData data;
        if (player.worldObj.isRemote) {
            data = ClientCache.getClientData(player);
        } else {
            data = getData(player);
            data.loadNBTData(false);
        }
        data.player = player;
        return data;
    }

    @SideOnly(Side.CLIENT)
    public static DBCData getClient() {
        return get(Minecraft.getMinecraft().thePlayer);
    }

    public static Aura getAura(EntityPlayer player) {
        if (player == null)
            return null;

        DBCData dbcData = get(player);
        if (dbcData == null)//(dbcData.Release <= 0 || dbcData.Ki <= 0)
            return null;

        int form = dbcData.auraID;
        if (form == -1)
            return null;


        return (Aura) AuraController.getInstance().get(form);
    }

    public static Form getForm(EntityPlayer p) {
        if (Utility.isServer(p))
            return PlayerDataUtil.getDBCInfo(p) != null ? PlayerDataUtil.getDBCInfo(p).getCurrentForm() : null;
        else {
            DBCData dbcData = get(p);
            if (dbcData == null)//(dbcData.Release <= 0 || dbcData.Ki <= 0)
                return null;

            int form = dbcData.addonFormID;
            if (form == -1)
                return null;


            return (Form) FormController.getInstance().get(form);
        }
    }

    public static float getFormLevel(EntityPlayer player) {
        DBCData dbcData = get(player);
        if (dbcData == null)
            return 0f;

        return dbcData.addonFormLevel;
    }
}
