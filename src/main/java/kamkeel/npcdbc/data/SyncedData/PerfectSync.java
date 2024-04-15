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
import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * Automatically syncs any NBT compound of choice to client every SaveEveryXTicks,
 * so the latest version of said compound is always available on client.
 * <p>
 * Usage: extend this class, set DATA_NAME to name of compound you want to sync, then create fields equivalent
 * to tags in compound inside new class, or can skip this step and use the tags from compound directly.
 * Finally, add the save/load NBT data methods.
 * Check DBCData for a concrete implementation
 */
public abstract class PerfectSync<T extends PerfectSync<T>> implements IExtendedEntityProperties {
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
            PerfectSync.register(e, DBCData.dn, true);
        }
    }

    // register all implementations individually here
    public static void register(Entity e, String dn, boolean registerClient) {
        if (dn.equals(DBCData.dn) && DBCData.eligibleForDBC(e) && !DBCData.has(e)) {
            e.registerExtendedProperties(DBCData.dn, new DBCData(e));
            DBCData.get(e).loadFromNBT(false); // initial loading of fields from server NBTs
            if (registerClient)
                registerClient(e, dn);
        }
    }

    public static void registerClient(Entity e, String dn) {
        PacketRegistry.syncData(e, "register" + ";" + dn, new NBTTagCompound()); // register;CMData

    }

    public static <T extends PerfectSync<T>> T get(Entity e, String s) {
        if (e == null)
            return null;
        return (T) e.getExtendedProperties(s);

    }

    //checks if entity has their own data registered
    public static boolean has(Entity e, String dn) {
        return get(e, dn) != null;
    }

    public static NBTTagCompound compound(Entity e, String dn) {
        return PerfectSync.get(e, dn).compound();
    }

    @SideOnly(Side.CLIENT)
    public static <T extends PerfectSync<T>> T getClient(String s) {
        return get(Minecraft.getMinecraft().thePlayer, s);
    }

    public void init(Entity entity, World world) {
    }


    public void saveNBTData(NBTTagCompound compound) {
    }

    /**
     * Loads fields from server NBT compound
     */
    public void loadFromNBT(boolean saveClient) {
        if (Utility.isServer()) {
            loadNBTData(null);
            if (!saveClient)
                return;
            NBTTagCompound data = compound();
            String s = DATA_NAME.equals(DBCData.dn) ? "DBCData" : "";
            if (entity instanceof EntityPlayer)
                PacketRegistry.syncData(entity, "update" + s, data);
            else
                PacketRegistry.syncData(entity, "updateEntity" + s + ":" + Utility.getEntityID(entity), data);

        }
    }

    public void loadNBTData(NBTTagCompound compound) {
    }

    /**
     * Saves field values to server NBT compound
     *
     * @param saveClient
     */
    public void saveToNBT(boolean saveClient) {
        if (Utility.isServer()) {
            saveNBTData(null);
            if (!saveClient)
                return;
            NBTTagCompound data = compound();
            String s = DATA_NAME.equals(DBCData.dn) ? "DBCData" : "";
            if (entity instanceof EntityPlayer)
                PacketRegistry.syncData(entity, "update" + s, data);
            else
                PacketRegistry.syncData(entity, "updateEntity" + s + ":" + Utility.getEntityID(entity), data);
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

    ////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////
    // Tag getters and setters

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

}
