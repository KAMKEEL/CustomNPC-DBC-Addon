package kamkeel.npcdbc.data.race.races.android;

import net.minecraft.entity.player.EntityPlayer;

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
    public static final AndroidPartType KI_RING = register(AndroidParts.KI_RING);

    // ──────────────────── Instance fields ────────────────────
    private final int ordinal;
    private final AndroidPartData part;

    private AndroidPartType(AndroidPartData part, int ordinal) {
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
        return part.getName();
    }

    public String getUnlocalizedName() {
        return part.getUnlocalizedName();
    }

    public String getNamespace() {
        return part.getNamespace();
    }

    public String getTextureDir() {
        return part.getTextureDir();
    }

    public String resolveTextureDir() {
        return part.resolveTextureDir();
    }

    public AndroidPartData getData() {
        return part;
    }

    public AndroidPartSlot getSlot() {
        return part.getSlot();
    }

    public boolean fitsSlot(AndroidPartSlot slot) {
        return part.fitsSlot(slot);
    }

    public void onEquip(EntityPlayer player) {
        part.onEquip(player);
    }

    public void onUnequip(EntityPlayer player) {
        part.onUnequip(player);
    }

    public void onTick(EntityPlayer player) {
        part.onUnequip(player);
    }

    // ──────────────────── Registration ────────────────────

    public static AndroidPartType register(AndroidPartData part) {
        if (part == null)
            throw new IllegalArgumentException("AndroidPart must not be null");

        String id = part.getId();

        if (REGISTRY.containsKey(id))
            throw new IllegalStateException("Duplicate AndroidPartType registration: " + id);

        AndroidPartType type = new AndroidPartType(part, nextOrdinal++);
        REGISTRY.put(id, type);
        return type;
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
