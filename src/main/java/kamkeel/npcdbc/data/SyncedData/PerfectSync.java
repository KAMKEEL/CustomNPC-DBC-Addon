package kamkeel.npcdbc.data.SyncedData;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.network.PacketRegistry;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Automatically syncs any NBT compound of choice to client every SaveEveryXTicks,
 * so the latest version of said compound is always available on client.
 * <p>
 * Usage: extend this class, set DATA_NAME to name of compound you want to sync, then create fields equivalent
 * to tags in compound inside new class, or can skip this step and use the tags from compound directly.
 * Finally, add the save/load NBT data methods.
 * Check DBCData for a concrete implementation
 */
public abstract class PerfectSync<T extends PerfectSync<T>> {
    //only entities that are eligible for subclasses of PerfectSync get registed
    public static HashMap<UUID, List<PerfectSync>> data = new HashMap<>();

    public static int SaveEveryXTicks = 60;
    public String DATA_NAME;
    public Entity entity;
    public EntityPlayer player;
    public World world;
    public NBTTagCompound nbt;

    public PerfectSync(Entity entity) {
        this.entity = entity;
        this.world = entity.worldObj;
    }

    public void init(Entity entity, World world) {
    }


    public void syncClient() {
        NBTTagCompound data = compound();
        String s = DATA_NAME.equals(DBCData.dn) ? "DBCData" : "";
        PacketRegistry.syncData(entity, "update" + s + ":" + Utility.getUUID(entity), data);
    }

    /**
     * Loads fields from server NBT compound
     */
    public void loadFromNBT(boolean syncClient) {
        if (Utility.isServer()) {
            loadNBTData(null);

            if (syncClient)
                syncClient();
        }
    }


    /**
     * Saves field values to server NBT compound
     *
     * @param syncClient
     */
    public void saveToNBT(boolean syncClient) {
        if (Utility.isServer()) {
            saveNBTData(null);

            if (syncClient)
                syncClient();
        }
    }


    public void saveServer(String tag, String type) {// saves to server from client

        Object o = null;
        if (type.equals("Int"))
            o = nbt.getInteger(tag);
        else if (type.equals("Float"))
            o = nbt.getFloat(tag);
        else if (type.equals("Double"))
            o = nbt.getDouble(tag);
        else if (type.equals("Long"))
            o = nbt.getLong(tag);
        else if (type.equals("Byte"))
            o = nbt.getByte(tag);
        else if (type.equals("Boolean"))
            o = nbt.getBoolean(tag);
        else if (type.equals("String"))
            o = nbt.getString(tag);

        String d = ";" + DATA_NAME + ";" + tag + ";" + type + ";" + o;

        if (entity instanceof EntityPlayer)
            PacketRegistry.syncData((EntityPlayer) entity, "updateServer" + d, null); // updateServer;DBCData;jrmcStrI;Int;100
        else
            PacketRegistry.syncData(null, "updateServerEntity" + d + ";" + Utility.getEntityID(entity), null);// updateServerEntity;DBCData;jrmcStrI;Int;100;56,false

    }

    /**
     * @return The entire compound
     */

    public NBTTagCompound compound() {
        NBTTagCompound nbt = null;
        if (Utility.isServer()) {
            if (!entity.getEntityData().hasKey(DATA_NAME)) {
                nbt = new NBTTagCompound();
                entity.getEntityData().setTag(DATA_NAME, nbt);
            }
            nbt = entity.getEntityData().getCompoundTag(DATA_NAME);
        } else {
            if (this.nbt == null)
                this.nbt = new NBTTagCompound();
            nbt = this.nbt;
        }
        return nbt;
    }

    public int getInt(String string) {
        return compound().getInteger(string);
    }

    public float getFloat(String string) {
        return compound().getFloat(string);
    }

    public double getDouble(String string) {
        return compound().getDouble(string);
    }

    public long getLong(String string) {
        return compound().getLong(string);
    }

    public byte getByte(String string) {
        return compound().getByte(string);
    }

    public boolean getBoolean(String string) {
        return compound().getBoolean(string);
    }

    public String getString(String string) {
        return compound().getString(string);
    }

    public void setInt(String string, int s) {
        compound().setInteger(string, s);
        if (Utility.isServer())
            loadFromNBT(true); //syncs updated tag to client
        else
            saveServer(string, "Int"); //if tag changed on client, syncs it to server

    }

    public void setFloat(String string, float s) {
        compound().setFloat(string, s);
        if (Utility.isServer())
            loadFromNBT(true);
        else
            saveServer(string, "Float");

    }

    ////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////
    // Tag getters and setters

    public void setDouble(String string, double s) {
        compound().setDouble(string, s);
        if (Utility.isServer())
            loadFromNBT(true);
        else
            saveServer(string, "Double");

    }

    public void setLong(String string, long s) {
        compound().setLong(string, s);
        if (Utility.isServer())
            loadFromNBT(true);
        else
            saveServer(string, "Long");
    }

    public void setByte(String string, byte s) {
        compound().setByte(string, s);
        if (Utility.isServer())
            loadFromNBT(true);
        else
            saveServer(string, "Byte");

    }

    public void setBoolean(String string, boolean s) {
        compound().setBoolean(string, s);
        if (Utility.isServer())
            loadFromNBT(true);
        else
            saveServer(string, "Boolean");

    }

    public void setString(String string, String s) {
        compound().setString(string, s);
        if (Utility.isServer())
            loadFromNBT(true);
        else
            saveServer(string, "String");


    }

    public void saveNBTData(NBTTagCompound compound) {
    }

    public void loadNBTData(NBTTagCompound compound) {
    }

    //saves all datas for entity, add datas here
    public static void saveAllDatas(Entity e, boolean saveClient) {
        if (e instanceof EntityPlayer) {
            if (DBCData.has(e))
                DBCData.get(e).loadFromNBT(true);
        }
    }

    // registers all datas for entity IF they are eligible for it (check DBCData.eligibleForDBC), add datas here
    public static void registerAllDatas(Entity e) {
        if (e instanceof EntityPlayer) {
            PerfectSync.register(e, DBCData.dn);
        }
    }

    // register all implementations individually here
    public static void register(Entity e, String dn) {
        UUID id = e instanceof EntityPlayer ? ((EntityPlayer) e).getGameProfile().getId() : e.getUniqueID();

        if (dn.equals(DBCData.dn) && DBCData.eligibleForDBC(e) && !DBCData.has(e)) {
            registerToMap(id, new DBCData(e));
            DBCData.get(e).loadFromNBT(false); // initial loading of fields from server NBTs
        }
    }

    public static void registerToMap(UUID id, PerfectSync dat) {
        if (!data.containsKey(id))
            data.put(id, new ArrayList<>());

        data.get(id).add(dat);
    }


    public static <T extends PerfectSync<T>> T get(Entity e, String s) {
        if (e == null)
            return null;

        UUID id = e instanceof EntityPlayer ? ((EntityPlayer) e).getGameProfile().getId() : e.getUniqueID();

        PerfectSync search = get(id, s);
        return search != null ? (T) search : null;
    }

    public static <T extends PerfectSync<T>> T get(UUID id, String s) {
        if (data.containsKey(id))
            for (PerfectSync d : data.get(id))
                if (d.DATA_NAME.equals(s))
                    return (T) d;

        return null;
    }

    //checks if entity has their own data registered
    public static boolean has(Entity e, String dn) {
        return get(e, dn) != null;
    }

    public static boolean has(UUID id, String dn) {
        return get(id, dn) != null;
    }

    public static NBTTagCompound compound(Entity e, String dn) {
        return PerfectSync.get(e, dn).compound();
    }

    @SideOnly(Side.CLIENT)
    public static <T extends PerfectSync<T>> T getClient(String s) {
        return get(Minecraft.getMinecraft().thePlayer, s);
    }

}
