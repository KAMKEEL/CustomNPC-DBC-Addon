package kamkeel.npcdbc.data.SyncedData;


import kamkeel.npcdbc.network.PacketRegistry;
import kamkeel.npcdbc.util.u;
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
    public static int SaveEveryXTicks = 200;
    public String DATA_NAME;
    public Entity p;
    public World w;
    public NBTTagCompound cmpd;

    public PerfectSync(Entity p) {
        this.p = p;
        this.w = p.worldObj;
    }

    //saves all datas for entity, add datas here
    public static void saveAllDatas(Entity p, boolean saveClient) {
        if (p instanceof EntityPlayer) {
            if (DBCData.has(p))
                DBCData.get(p).save(true);
            if (CustomFormData.has(p))
                CustomFormData.get(p).save(true);
        }
    }

    // registers all datas for entity IF they are eligible for it (check DBCData.eligibleForDBC), add datas here
    public static void registerAllDatas(Entity p) {
        if (p instanceof EntityPlayer) {
            PerfectSync.register(p, DBCData.dn, true);
            PerfectSync.register(p, CustomFormData.dn, true);
        }

    }

    // register all implementations individually here
    public static void register(Entity p, String dn, boolean registerClient) {
        if (dn.equals(DBCData.dn) && DBCData.eligibleForDBC(p) && !DBCData.has(p)) {
            p.registerExtendedProperties(DBCData.dn, new DBCData(p));
            if (registerClient)
                registerClient(p, dn);
        } else if (dn.equals(CustomFormData.dn) && CustomFormData.eligibleForCustomForms(p) && !CustomFormData.has(p)) {
            p.registerExtendedProperties(CustomFormData.dn, new CustomFormData(p));
            if (registerClient)
                registerClient(p, dn);

        }

    }

    public static void registerClient(Entity p, String dn) {
        PacketRegistry.syncData(p, "register" + ";" + dn, new NBTTagCompound()); // register;CMData

    }

    public static <T extends PerfectSync<T>> T get(Entity player, String s) {
        if (player == null)
            return null;
        return (T) player.getExtendedProperties(s);

    }

    //checks if entity has their own data registered
    public static boolean has(Entity p, String dn) {
        return get(p, dn) != null;
    }

    public static NBTTagCompound compound(Entity p, String dn) {
        return PerfectSync.get(p, dn).compound();
    }

    @Override
    public void init(Entity entity, World world) {
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
    }

    public void save(boolean saveClient) { // saves to client from server
        if (w == null)
            w = p.worldObj;

        if (u.isServer()) {
            NBTTagCompound data = compound();
            loadNBTData(data);
          //  System.out.println(compound());
            if (!saveClient)
                return;

            String s = DATA_NAME.equals(DBCData.dn) ? "DBCData" : DATA_NAME.equals(CustomFormData.dn) ? CustomFormData.dn : "";
            if (p instanceof EntityPlayer)
                PacketRegistry.syncData(p, "update" + s, data);
            else
                PacketRegistry.syncData(p, "updateEntity" + s + ":" + u.getEntityID(p), data);

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

        if (p instanceof EntityPlayer)
            PacketRegistry.syncData((EntityPlayer) p, "updateServer" + d, null); // updateServer;DBCData;jrmcStrI;Int;100
        else
            PacketRegistry.syncData(null, "updateServerEntity" + d + ";" + u.getEntityID(p), null);// updateServerEntity;DBCData;jrmcStrI;Int;100;56,false

    }

    /**
     * @return The entire compound
     */

    public NBTTagCompound compound() {
        NBTTagCompound nbt = null;
        if (u.isServer()) {
            if (!p.getEntityData().hasKey(DATA_NAME)) {
                nbt = new NBTTagCompound();
                p.getEntityData().setTag(DATA_NAME, nbt);
            }
            nbt = p.getEntityData().getCompoundTag(DATA_NAME);

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
            save(true); //syncs updated tag to client
        else
            saveServer(string, "Int"); //if tag changed on client, syncs it to server

    }

    public void setFloat(String string, float s) {
        compound().setFloat(string, s);
        if (u.isServer())
            save(true);
        else
            saveServer(string, "Float");

    }

    public void setDouble(String string, double s) {
        compound().setDouble(string, s);
        if (u.isServer())
            save(true);
        else
            saveServer(string, "Double");

    }

    public void setLong(String string, long s) {
        compound().setLong(string, s);
        if (u.isServer())
            save(true);
        else
            saveServer(string, "Long");
    }

    public void setByte(String string, byte s) {
        compound().setByte(string, s);
        if (u.isServer())
            save(true);
        else
            saveServer(string, "Byte");

    }

    public void setBoolean(String string, boolean s) {
        compound().setBoolean(string, s);
        if (u.isServer())
            save(true);
        else
            saveServer(string, "Boolean");

    }

    public void setString(String string, String s) {
        compound().setString(string, s);
        if (u.isServer())
            save(true);
        else
            saveServer(string, "String");

    }

}
