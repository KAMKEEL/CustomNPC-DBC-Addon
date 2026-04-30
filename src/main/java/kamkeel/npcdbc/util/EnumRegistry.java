package kamkeel.npcdbc.util;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class EnumRegistry {

    // ──────────────────── Global Registry ────────────────────
    private static final Map<Class<?>, Map<String, EnumRegistry>> REGISTRIES = new LinkedHashMap<>();
    private static final Map<Class<?>, int[]> ORDINAL_COUNTERS = new LinkedHashMap<>();

    // ──────────────────── Instance fields ────────────────────
    private final int ordinal;

    protected EnumRegistry() {
        Class<?> cls = getClass();
        REGISTRIES.computeIfAbsent(cls, k -> new LinkedHashMap<>());
        ORDINAL_COUNTERS.computeIfAbsent(cls, k -> new int[]{0});
        this.ordinal = ORDINAL_COUNTERS.get(cls)[0]++;
    }

    // ──────────────────── Abstract ────────────────────

    public abstract String getId();

    // ──────────────────── Registration ────────────────────

    protected final <T extends EnumRegistry> T register() {
        Class<?> cls = getClass();
        Map<String, EnumRegistry> registry = REGISTRIES.get(cls);
        String id = getId();

        if (registry.containsKey(id))
            throw new IllegalStateException("Duplicate registration: " + id);

        registry.put(id, this);

        return (T) this;
    }

    // ──────────────────── Lookup helpers ────────────────────

    public static <T extends EnumRegistry> T byId(Class<T> cls, String id) {
        if (id == null) return null;
        Map<String, EnumRegistry> registry = REGISTRIES.get(cls);
        return registry == null ? null : (T) registry.get(id);
    }

    public static <T extends EnumRegistry> T byOrdinal(Class<T> cls, int ordinal) {
        if (ordinal < 0) return null;
        Map<String, EnumRegistry> registry = REGISTRIES.get(cls);
        if (registry == null) return null;
        for (EnumRegistry entry : registry.values())
            if (entry.ordinal == ordinal) return (T) entry;
        return null;
    }

    public static <T extends EnumRegistry> Collection<T> values(Class<T> cls) {
        Map<String, EnumRegistry> registry = REGISTRIES.get(cls);
        if (registry == null) return Collections.emptyList();
        return (Collection<T>) Collections.unmodifiableCollection(registry.values());
    }

    public static int count(Class<?> cls) {
        Map<String, EnumRegistry> registry = REGISTRIES.get(cls);
        return registry == null ? 0 : registry.size();
    }

    public int ordinal() {
        return ordinal;
    }

    // ──────────────────── Object overrides ────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EnumRegistry)) return false;
        return getId().equals(((EnumRegistry) o).getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public String toString() {
        return getId();
    }
}
