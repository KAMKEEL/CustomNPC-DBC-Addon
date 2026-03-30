package kamkeel.npcdbc.controllers.sync;

import kamkeel.npcdbc.constants.DBCSyncType;
import kamkeel.npcs.controllers.sync.SyncRegistry;
import noppes.npcs.LogWriter;

import java.util.HashMap;
import java.util.Map;

/**
 * Central registry for {@link DBCSyncHandler} instances used by the DBC addon.
 * Mirrors {@link SyncRegistry} but uses
 * {@code int}-based {@link DBCSyncType} constants.
 *
 * <p>Registration happens during {@code DBCSyncController.load()} and is
 * read-only after initialization completes.</p>
 */
public final class DBCSyncRegistry {

    private static final Map<Integer, DBCSyncHandler> handlers = new HashMap<>();

    private DBCSyncRegistry() {
    }

    /**
     * Register a handler for a DBC sync type.
     *
     * @param syncType the {@link DBCSyncType} constant
     * @param handler  the handler implementation
     * @throws IllegalStateException if the type is already registered
     */
    public static void register(int syncType, DBCSyncHandler handler) {
        if (handlers.containsKey(syncType)) {
            throw new IllegalStateException("DBCSyncHandler already registered for sync type " + syncType);
        }
        handlers.put(syncType, handler);
    }

    /**
     * Get the handler for a DBC sync type.
     *
     * @param syncType the sync type constant
     * @return the handler, or null if not registered
     */
    public static DBCSyncHandler getHandler(int syncType) {
        return handlers.get(syncType);
    }

    /**
     * Get all registered sync type IDs.
     *
     * @return an iterable of registered type IDs
     */
    public static Map<Integer, DBCSyncHandler> getAll() {
        return handlers;
    }

    /**
     * Clear all registrations. Called during {@code DBCSyncController.load()}
     * to allow re-registration on world reload.
     */
    public static void clear() {
        handlers.clear();
    }

    /**
     * Validate that all expected DBC sync types have registered handlers.
     * Logs a warning for any type without a handler.
     *
     * @param expectedTypes the sync type constants to check
     */
    public static void validateRegistrations(int... expectedTypes) {
        for (int type : expectedTypes) {
            if (!handlers.containsKey(type)) {
                LogWriter.info("[DBCSyncRegistry] No DBCSyncHandler registered for sync type " + type);
            }
        }
    }

    /**
     * Check whether a handler is registered for the given type.
     */
    public static boolean isRegistered(int syncType) {
        return handlers.containsKey(syncType);
    }
}
