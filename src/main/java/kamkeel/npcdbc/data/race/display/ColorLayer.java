package kamkeel.npcdbc.data.race.display;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A named grouping of {@link ColorSlot}s that represents a distinct visual
 * region of a race model (e.g. "body", "face", "eyes").
 * <p>
 * Layers form a tree: a layer may contain child layers via {@link #addSubLayer}.
 * Sub-layers are completely independent — no color inheritance occurs between
 * parent and child. The hierarchy is purely organisational.
 * <p>
 * {@link ColorSlot} remains the internal leaf concept: a slot is a single
 * named color channel within a layer (e.g. "bodycm" inside the "body" layer).
 */
public class ColorLayer {

    // ── Built-in layer IDs ─────────────────────────────────────────────────────
    /** The body layer — holds bodycm, bodyc1, bodyc2, bodyc3. */
    public static final String BODY  = "body";
    /** The face layer — parent of the eyes sub-layer; holds nose/mouth (non-colorable). */
    public static final String FACE  = "face";
    /** The eyes sub-layer — holds lefteye, righteye, and the combined eye slot. */
    public static final String EYES  = "eyes";

    // ──────────────────────────────────────────────────────────────────────────

    /** Unique identifier for this layer. Always lower-case. */
    public final String id;

    /** Human-readable display name shown in UI contexts. */
    public final String displayName;

    /** Named color slots owned by this layer. */
    private final Map<String, ColorSlot> slots = new LinkedHashMap<>();

    /** Child layers nested under this layer. Completely independent — no inheritance. */
    private final Map<String, ColorLayer> subLayers = new LinkedHashMap<>();

    public ColorLayer(String id, String displayName) {
        this.id          = id.toLowerCase();
        this.displayName = displayName;
    }

    // ── Slot management ────────────────────────────────────────────────────────

    /**
     * Declares a {@link ColorSlot} on this layer.
     *
     * @param slot the slot to register
     * @return {@code this} for chaining
     */
    public ColorLayer addSlot(ColorSlot slot) {
        slots.put(slot.id, slot);
        return this;
    }

    /**
     * Convenience overload — creates and registers a slot from raw id + name.
     *
     * @param slotId      the slot identifier (e.g. {@link ColorSlot#BODY_CM})
     * @param displayName human-readable name
     * @return {@code this} for chaining
     */
    public ColorLayer addSlot(String slotId, String displayName) {
        return addSlot(new ColorSlot(slotId, displayName));
    }

    public ColorSlot getSlot(String slotId) {
        return slots.get(slotId.toLowerCase());
    }

    public boolean hasSlot(String slotId) {
        return slots.containsKey(slotId.toLowerCase());
    }

    public Map<String, ColorSlot> getSlots() {
        return Collections.unmodifiableMap(slots);
    }

    // ── Sub-layer management ───────────────────────────────────────────────────

    /**
     * Nests a child layer under this layer. Sub-layers are completely independent
     * — no color data is inherited from the parent.
     *
     * @param layer the child layer to register
     * @return {@code this} for chaining
     */
    public ColorLayer addSubLayer(ColorLayer layer) {
        subLayers.put(layer.id, layer);
        return this;
    }

    /**
     * Returns a direct child layer by id, or {@code null} if none is registered.
     * Does NOT search recursively.
     */
    public ColorLayer getSubLayer(String id) {
        return subLayers.get(id.toLowerCase());
    }

    public boolean hasSubLayer(String id) {
        return subLayers.containsKey(id.toLowerCase());
    }

    public Map<String, ColorLayer> getSubLayers() {
        return Collections.unmodifiableMap(subLayers);
    }

    // ── Slot count helpers ─────────────────────────────────────────────────────

    /** Returns the number of {@link ColorSlot}s directly owned by this layer. */
    public int getSlotCount() {
        return slots.size();
    }

    /**
     * Returns whether this layer declares any slot with the given id,
     * without descending into sub-layers.
     */
    public boolean ownsSlot(String slotId) {
        return slots.containsKey(slotId.toLowerCase());
    }
}
