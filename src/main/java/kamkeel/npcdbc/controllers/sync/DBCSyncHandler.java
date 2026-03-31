package kamkeel.npcdbc.controllers.sync;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.constants.DBCSyncType;
import kamkeel.npcs.controllers.sync.SyncHandler;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Contract for a DBC addon synchronized data type. Mirrors
 * {@link SyncHandler} but works
 * with {@code int}-based {@link DBCSyncType}
 * constants instead of an enum.
 *
 * <p>Each handler is registered with {@link DBCSyncRegistry} and covers
 * all three sync actions: RELOAD (all objects), UPDATE (single object),
 * and REMOVE (single object).</p>
 *
 * <p>Handlers that don't support UPDATE or REMOVE should use the
 * default no-op implementations.</p>
 */
public interface DBCSyncHandler {

    // ========== SERVER-SIDE ==========

    /**
     * Serialize ALL objects of this sync type into an NBTTagCompound.
     * Called during {@code syncPlayer()} to build the full RELOAD payload.
     *
     * @return the serialized compound with a "Data" NBTTagList
     */
    NBTTagCompound serializeAll();

    // ========== CLIENT-SIDE ==========

    /**
     * Updates the full dataset for the client-side data.
     *
     * <p>Implementations should deserialize into a sync buffer,
     * atomically swap to the primary map, then reset the buffer.</p>
     *
     * @param compound the full serialized dataset from {@link #serializeAll()}
     */
    @SideOnly(Side.CLIENT)
    void clientHandleReload(NBTTagCompound compound);

    /**
     * Updates a single object in the client-side data.
     * 
     * @param compound the serialized single object
     */
    @SideOnly(Side.CLIENT)
    default void clientHandleUpdate(NBTTagCompound compound) {
    }

    /**
     * Removes a single object from client-side data.
     * Full object NBTTagCompound sent finer query control
     * Int-keyed handlers should override {@link #clientHandleRemove(int)} instead.
     */
    @SideOnly(Side.CLIENT)
    default void clientHandleRemove(NBTTagCompound compound) {
    }
    
    /**
     * Removes a single object with the given ID from client-side data.
     * 
     * @param id the ID of the object to remove
     */
    @SideOnly(Side.CLIENT)
    default void clientHandleRemove(int id) {
    }

    /**Whether this handler supports the UPDATE action.*/
    default boolean supportsUpdate() {
        return false;
    }

    /**Whether this handler supports the REMOVE action.*/
    default boolean supportsRemove() {
        return false;
    }
}
