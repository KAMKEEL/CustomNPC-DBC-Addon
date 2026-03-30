package kamkeel.npcdbc.controllers.sync;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Contract for a DBC addon synchronized data type. Mirrors
 * {@link kamkeel.npcs.controllers.sync.SyncHandler} but works
 * with {@code int}-based {@link kamkeel.npcdbc.constants.DBCSyncType}
 * constants instead of an enum.
 *
 * <p>Each handler is registered with {@link DBCSyncRegistry} and covers
 * all three sync actions: RELOAD (full), UPDATE (single entity),
 * and REMOVE (delete entity).</p>
 *
 * <p>Handlers that don't support UPDATE or REMOVE should use the
 * default no-op implementations.</p>
 */
public interface DBCSyncHandler {

    // ========== SERVER-SIDE ==========

    /**
     * Serialize ALL entities of this type into an NBTTagCompound.
     * Called during {@code syncPlayer()} to build the full RELOAD payload.
     *
     * @return the serialized compound with a "Data" NBTTagList
     */
    NBTTagCompound serializeAll();

    // ========== CLIENT-SIDE ==========

    /**
     * Handle a RELOAD action on the client. Deserialize the full
     * dataset and replace the client-side data.
     *
     * <p>Implementations should deserialize into a sync buffer,
     * atomically swap to the primary map, then reset the buffer.</p>
     *
     * @param compound the full serialized dataset from {@link #serializeAll()}
     */
    @SideOnly(Side.CLIENT)
    void clientHandleReload(NBTTagCompound compound);

    /**
     * Handle an UPDATE action on the client. Deserialize a single
     * entity and insert/replace it in the client-side data.
     *
     * <p>Default: no-op.</p>
     *
     * @param compound the serialized single entity
     */
    @SideOnly(Side.CLIENT)
    default void clientHandleUpdate(NBTTagCompound compound) {
    }

    /**
     * Handle a REMOVE action on the client. Remove the entity
     * with the given ID from client-side data.
     *
     * <p>Default: no-op.</p>
     *
     * @param id the ID of the entity to remove
     */
    @SideOnly(Side.CLIENT)
    default void clientHandleRemove(int id) {
    }

    /**
     * Handle a REMOVE action for string-keyed entities.
     * The key is read from the compound under the "Key" tag.
     * Int-keyed handlers should override {@link #clientHandleRemove(int)} instead.
     */
    @SideOnly(Side.CLIENT)
    default void clientHandleRemove(NBTTagCompound compound) {
    }

    /**
     * Whether this handler supports the UPDATE action.
     */
    default boolean supportsUpdate() {
        return false;
    }

    /**
     * Whether this handler supports the REMOVE action.
     */
    default boolean supportsRemove() {
        return false;
    }
}
