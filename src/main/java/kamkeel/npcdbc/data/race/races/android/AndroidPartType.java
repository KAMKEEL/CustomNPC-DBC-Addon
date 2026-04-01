package kamkeel.npcdbc.data.race.races.android;

import kamkeel.npcdbc.CustomNpcPlusDBC;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class AndroidPartType {

    // ──────────────────── Registry ────────────────────
    private static final Map<String, AndroidPartType> REGISTRY = new LinkedHashMap<>();
    private static int nextOrdinal = 0;

    // ──────────────────── Built-in parts ────────────────────
    public static final AndroidPartType RED_RIBBON_CORE = register(AndroidParts.RED_RIBBON_CORE);

    // ──────────────────── Instance fields ────────────────────
    private final int ordinal;
    private final AndroidPart part;

    private AndroidPartType(AndroidPart part, int ordinal) {
        this.part = part;
        this.ordinal = ordinal;
    }

    // ──────────────────── Public API ────────────────────

    public String getId() {
        return part.getId();
    }

    public int ordinal() {
        return ordinal;
    }

    public String getName() {
        String id = part.getId();
        return id.contains(":") ? id.split(":")[1] : id;
    }

    public AndroidPart getPart() {
        return part;
    }

    public AndroidPartSlot getSlot() {
        return part.getSlot();
    }

    public boolean fitsSlot(AndroidPartSlot slot) {
        return part.fitsSlot(slot);
    }

    // ──────────────────── Registration ────────────────────

    public static AndroidPartType register(String namespace, AndroidPart part) {
        if (part == null)
            throw new IllegalArgumentException("AndroidPart must not be null");

        String id = namespace + ":" + part.getId();

        if (REGISTRY.containsKey(id))
            throw new IllegalStateException("Duplicate AndroidPartType registration: " + id);

        AndroidPartType type = new AndroidPartType(part, nextOrdinal++);
        REGISTRY.put(id, type);
        return type;
    }

    private static AndroidPartType register(AndroidPart part) {
        return register(CustomNpcPlusDBC.ID, part);
    }

    public static AndroidPartType byId(String id) {
        return id == null ? null : REGISTRY.get(id);
    }

    public static AndroidPartType byOrdinal(int ordinal) {
        if (ordinal < 0) return null;
        for (AndroidPartType type : REGISTRY.values())
            if (type.ordinal == ordinal) return type;
        return null;
    }

    public static Collection<AndroidPartType> values() {
        return Collections.unmodifiableCollection(REGISTRY.values());
    }

    public static int count() {
        return REGISTRY.size();
    }

    // ──────────────────── Object overrides ────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AndroidPartType)) return false;
        return getId().equals(((AndroidPartType) o).getId());
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
