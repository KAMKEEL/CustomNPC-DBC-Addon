package kamkeel.npcdbc.client;

import kamkeel.npcdbc.data.dbcdata.DBCData;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.config.ConfigClient;
import noppes.npcs.util.CacheHashMap;

import java.util.HashMap;

public class ClientCache {

    public static boolean fromRenderPlayerJBRA;
    public static boolean isChangePart;
    public static final CacheHashMap<String, CacheHashMap.CachedObject<DBCData>> clientDataCache = new CacheHashMap<>((long) ConfigClient.CacheLife * 60 * 1000);

    public static boolean allowTransformBypass = false;
    public static boolean hasChargingDex = false;
    public static HashMap<Integer, Float> chargingDexValues = new HashMap<>();

    public static DBCData getClientData(EntityPlayer player){
        synchronized (clientDataCache) {
            if (!clientDataCache.containsKey(player.getCommandSenderName()))
                clientDataCache.put(player.getCommandSenderName(), new CacheHashMap.CachedObject<>(new DBCData(player)));
            return clientDataCache.get(player.getCommandSenderName()).getObject();
        }
    }
}
