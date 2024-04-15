package kamkeel.npcdbc.data.SyncedData;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.network.PacketRegistry;
import kamkeel.npcdbc.util.u;
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
    public Entity e;
    public EntityPlayer p;
    public World w;
    public NBTTagCompound cmpd;

    public PerfectSync(Entity e) {
        this.e = e;
        this.w = e.worldObj;
    }

    //saves all datas for entity, add datas here
    public static void saveAllDatas(Entity e, boolean saveClient) {
        if (e instanceof EntityPlayer) {
            if (DBCData.has(e))
                DBCData.get(e).loadFromNBT(true);
            if (CustomFormData.has(e))
                CustomFormData.get(e).loadFromNBT(true);
        }
    }

    // registers all datas for entity IF they are eligible for it (check DBCData.eligibleForDBC), add datas here
    public static void registerAllDatas(Entity e) {
        if (e instanceof EntityPlayer) {
            PerfectSync.register(e, DBCData.dn, true);
            PerfectSync.register(e, CustomFormData.dn, true);
        }

    }

    // register all implementations individually here
    public static void register(Entity e, String dn, boolean registerClient) {
        if (dn.equals(DBCData.dn) && DBCData.eligibleForDBC(e) && !DBCData.has(e)) {
            e.registerExtendedProperties(DBCData.dn, new DBCData(e));
            DBCData.get(e).loadFromNBT(false); // initial loading of fields from server NBTs
            if (registerClient)
                registerClient(e, dn);
        } else if (dn.equals(CustomFormData.dn) && CustomFormData.eligibleForCustomForms(e) && !CustomFormData.has(e)) {
            e.registerExtendedProperties(CustomFormData.dn, new CustomFormData(e));
            CustomFormData.get(e).loadFromNBT(false);
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
        if (u.isServer()) {
            loadNBTData(null);
            if (!saveClient)
                return;
            NBTTagCompound data = compound();
            String s = DATA_NAME.equals(DBCData.dn) ? "DBCData" : DATA_NAME.equals(CustomFormData.dn) ? CustomFormData.dn : "";
            if (e instanceof EntityPlayer)
                PacketRegistry.syncData(e, "update" + s, data);
            else
                PacketRegistry.syncData(e, "updateEntity" + s + ":" + u.getEntityID(e), data);

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

        if (u.isServer()) {
            saveNBTData(null);
            if (!saveClient)
                return;
            NBTTagCompound data = compound();
            String s = DATA_NAME.equals(DBCData.dn) ? "DBCData" : DATA_NAME.equals(CustomFormData.dn) ? CustomFormData.dn : "";
            if (e instanceof EntityPlayer)
                PacketRegistry.syncData(e, "update" + s, data);
            else
                PacketRegistry.syncData(e, "updateEntity" + s + ":" + u.getEntityID(e), data);

        }
    }

    public void saveServer(String tag, String type) {// saves to server from client

        Object o = null;
        if (type.equals("Int"))
            o = cmpd.getInteger(tag);
        else if (type.equals("Float"))
            o = cmpd.getFloat(tag);
        else if (type.equals("Double"))
            o = cmpd.getDouble(tag);
        else if (type.equals("Long"))
            o = cmpd.getLong(tag);
        else if (type.equals("Byte"))
            o = cmpd.getByte(tag);
        else if (type.equals("Boolean"))
            o = cmpd.getBoolean(tag);
        else if (type.equals("String"))
            o = cmpd.getString(tag);

        String d = ";" + DATA_NAME + ";" + tag + ";" + type + ";" + o;

        if (e instanceof EntityPlayer)
            PacketRegistry.syncData((EntityPlayer) e, "updateServer" + d, null); // updateServer;DBCData;jrmcStrI;Int;100
        else
            PacketRegistry.syncData(null, "updateServerEntity" + d + ";" + u.getEntityID(e), null);// updateServerEntity;DBCData;jrmcStrI;Int;100;56,false

    }

    /**
     * @return The entire compound
     */

    public NBTTagCompound compound() {
        NBTTagCompound nbt = null;
        if (u.isServer()) {
            if (!e.getEntityData().hasKey(DATA_NAME)) {
                nbt = new NBTTagCompound();
                e.getEntityData().setTag(DATA_NAME, nbt);
            }
            nbt = e.getEntityData().getCompoundTag(DATA_NAME);

        } else {
            if (cmpd == null)
                cmpd = new NBTTagCompound();
            nbt = cmpd;
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
        if (u.isServer())
            loadFromNBT(true); //syncs updated tag to client
        else
            saveServer(string, "Int"); //if tag changed on client, syncs it to server

    }

    public void setFloat(String string, float s) {
        compound().setFloat(string, s);
        if (u.isServer())
            loadFromNBT(true);
        else
            saveServer(string, "Float");

    }

    public void setDouble(String string, double s) {
        compound().setDouble(string, s);
        if (u.isServer())
            loadFromNBT(true);
        else
            saveServer(string, "Double");

    }

    public void setLong(String string, long s) {
        compound().setLong(string, s);
        if (u.isServer())
            loadFromNBT(true);
        else
            saveServer(string, "Long");
    }

    public void setByte(String string, byte s) {
        compound().setByte(string, s);
        if (u.isServer())
            loadFromNBT(true);
        else
            saveServer(string, "Byte");

    }

    public void setBoolean(String string, boolean s) {
        compound().setBoolean(string, s);
        if (u.isServer())
            loadFromNBT(true);
        else
            saveServer(string, "Boolean");

    }

    public void setString(String string, String s) {
        compound().setString(string, s);
        if (u.isServer())
            loadFromNBT(true);
        else
            saveServer(string, "String");

    }

}
