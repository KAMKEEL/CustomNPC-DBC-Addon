package kamkeel.npcdbc.data.race.display;

import kamkeel.npcdbc.api.Color;
import net.minecraft.util.ResourceLocation;

import java.util.*;

/**
 * Represents an alternate visual state for a race (e.g. a transformation or
 * special form) that layers on top of the base {@link RaceDisplay}.
 * <p>
 * A {@code BodyState} is <b>never</b> the initial state — the base display is
 * always read first. When a state is active, its layers and overrides are merged
 * on top of the parent: layers declared here replace the parent's equivalent;
 * anything not declared here falls through to the parent transparently.
 * <p>
 * BodyStates have no color presets, because character creation always uses the
 * base display for customisation. They are purely a runtime visual layer.
 */
public class BodyState {

    /** Unique identifier for this state (e.g. "ssj", "great_ape"). */
    public final String id;

    /** Human-readable name shown in UI contexts. */
    public final String displayName;

    private final Map<String, ColorLayer> layers = new LinkedHashMap<>();

    private final Map<String, Color> colorOverrides = new HashMap<>();

    private final ColorPreset defaultColors = new ColorPreset();

    // ── Texture slots ─────────────────────────────────────────────────────────
    private final Map<String, TextureSlot> textureSlots = new LinkedHashMap<>();

    private final Map<String, Integer> textureOverrides = new HashMap<>();

    public BodyState(String id, String displayName) {
        this.id          = id.toLowerCase();
        this.displayName = displayName;
    }

    // ── Layer management ───────────────────────────────────────────────────────

    /**
     * Declares a top-level {@link ColorLayer} on this state. When active, this
     * layer overrides the parent display's layer with the same id.
     *
     * @return {@code this} for chaining
     */
    public BodyState addLayer(ColorLayer layer) {
        layers.put(layer.id, layer);
        return this;
    }

    /**
     * Resolves a top-level layer: returns this state's layer if declared,
     * otherwise falls back to the parent display's layer.
     */
    public ColorLayer resolveLayer(String id, RaceDisplay parent) {
        ColorLayer own = layers.get(id.toLowerCase());
        return own != null ? own : parent.getLayer(id);
    }

    public ColorLayer getLayer(String id)  { return layers.get(id.toLowerCase()); }
    public boolean hasLayer(String id)     { return layers.containsKey(id.toLowerCase()); }
    public Map<String, ColorLayer> getLayers() { return Collections.unmodifiableMap(layers); }

    // ── Slot convenience ───────────────────────────────────────────────────────

    /**
     * Adds a {@link ColorSlot} to the named top-level layer on this state.
     * If the layer does not yet exist on the state, it is created with an
     * empty display name — add the layer explicitly first for a named one.
     */
    public BodyState addColorSlot(String layerId, ColorSlot slot) {
        layers.computeIfAbsent(layerId.toLowerCase(), k -> new ColorLayer(k, k))
            .addSlot(slot);
        return this;
    }

    // ── Color overrides ────────────────────────────────────────────────────────

    /**
     * Adds a hard color override for the given slot. When this state is active,
     * the slot's color is locked to this value — the player cannot change it.
     *
     * @return {@code this} for chaining
     */
    public BodyState addColorOverride(String slotId, Color color) {
        colorOverrides.put(slotId.toLowerCase(), color);
        return this;
    }

    /**
     * Resolves a color override: returns this state's override if present,
     * otherwise falls back to the parent display's override.
     * Returns {@code null} if neither has an override for the slot.
     */
    public Color resolveColorOverride(String slotId, RaceDisplay parent) {
        Color own = colorOverrides.get(slotId.toLowerCase());
        return own != null ? own : parent.getColorOverride(slotId);
    }

    public boolean hasColorOverride(String slotId) { return colorOverrides.containsKey(slotId.toLowerCase()); }
    public Map<String, Color> getColorOverrides()  { return Collections.unmodifiableMap(colorOverrides); }

    // ── Default colors ─────────────────────────────────────────────────────────

    /**
     * Sets the default color for a slot declared in this state's layers.
     *
     * @return {@code this} for chaining
     */
    public BodyState setDefaultColor(String slotId, int color) {
        defaultColors.set(slotId, new Color(color));
        return this;
    }

    /**
     * Resolves the default color for a slot: returns this state's default if set,
     * otherwise falls back to the parent display's default.
     */
    public int resolveDefaultColor(String slotId, RaceDisplay parent) {
        if (defaultColors.has(slotId)) return defaultColors.get(slotId).color;
        return parent.getDefaultColor(slotId);
    }

    public boolean hasDefaultColor(String slotId) { return defaultColors.has(slotId); }
    public ColorPreset getDefaultColors()          { return defaultColors; }

    // ── Texture slots ──────────────────────────────────────────────────────────

    /**
     * Declares a texture slot on this state, overriding the parent's slot with
     * the same id when this state is active.
     *
     * @return {@code this} for chaining
     */
    public BodyState addTextureVariation(String slotId, ResourceLocation texture) {
        textureSlots.computeIfAbsent(slotId.toLowerCase(), TextureSlot::new).add(texture);
        return this;
    }

    /**
     * Resolves a texture slot: returns this state's slot if declared,
     * otherwise falls back to the parent display's slot.
     */
    public TextureSlot resolveTextureSlot(String id, RaceDisplay parent) {
        TextureSlot own = textureSlots.get(id);
        return own != null ? own : parent.getTextureSlot(id);
    }

    public boolean hasTextureSlot(String id)          { return textureSlots.containsKey(id); }
    public Map<String, TextureSlot> getTextureSlots() { return Collections.unmodifiableMap(textureSlots); }

    // ── Texture overrides ──────────────────────────────────────────────────────

    /**
     * Forces a specific texture variation index for the given slot when this
     * state is active, regardless of the player's face/body choices.
     *
     * @param slotId         the texture slot id (e.g. {@link TextureSlot#NOSE})
     * @param variationIndex the index into the slot's variation list to force
     * @return {@code this} for chaining
     */
    public BodyState addTextureOverride(String slotId, int variationIndex) {
        textureOverrides.put(slotId, variationIndex);
        return this;
    }

    /**
     * Returns the forced texture variation index for the given slot,
     * or {@code -1} if this state does not override that slot.
     */
    public int getTextureOverride(String slotId) {
        return textureOverrides.getOrDefault(slotId, -1);
    }

    public boolean hasTextureOverride(String slotId)       { return textureOverrides.containsKey(slotId); }
    public Map<String, Integer> getTextureOverrides()      { return Collections.unmodifiableMap(textureOverrides); }
}
