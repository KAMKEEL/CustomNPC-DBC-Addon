package kamkeel.npcdbc.data.race.display;

import kamkeel.npcdbc.api.Color;
import kamkeel.npcdbc.data.race.serial.DataCompound;
import kamkeel.npcdbc.data.race.serial.DataSerializable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A named grouping of {@link DisplayLayer}s representing one visual region
 * of a race model (e.g. "body", "face").
 * <p>
 * A component may have at most <b>one</b> sub-component (e.g. "eyes" inside "face").
 * Sub-components cannot nest further — the hierarchy is limited to two levels.
 * <p>
 * Layers within a component are ordered by insertion (LinkedHashMap).
 */
public class DisplayComponent implements DataSerializable {

    /** Unique identifier for this component. Always lower-case. */
    public final String id;

    /** Human-readable display name shown in UI contexts. */
    public final String displayName;

    /** Layers owned by this component, keyed by {@link DisplayLayer#id}. */
    private final Map<String, DisplayLayer> layers = new LinkedHashMap<>();

    /**
     * Optional single sub-component (e.g. "eyes" inside "face").
     * Cannot itself contain another sub-component.
     */
    private DisplayComponent subComponent = null;

    /**
     * The fixed number of color presets this component declares.
     * All layers will be normalized to this count when {@link #normalizeLayerPresets()} is called:
     * layers with fewer presets are padded with their {@link DisplayLayer#getDefaultColor()},
     * then {@code 0xFFFFFF} as final fallback; layers with more are truncated.
     * -1 means no preset count has been set.
     */
    private int presetCount = -1;

    public DisplayComponent(String id, String displayName) {
        this.id = id.toLowerCase();
        this.displayName = displayName;
    }

    // ── Preset count ───────────────────────────────────────────────────────────

    /**
     * Sets the fixed number of color presets for this component.
     * Call {@link #normalizeLayerPresets()} after all layers and their presets are declared.
     *
     * @return {@code this} for chaining
     */
    public DisplayComponent setPresetCount(int count) {
        this.presetCount = Math.max(0, count);
        return this;
    }

    public int getPresetCount() {
        return presetCount;
    }

    public boolean hasPresetCount() {
        return presetCount >= 0;
    }

    /**
     * Normalizes every layer's color preset list to exactly {@link #presetCount} entries.
     * <ul>
     *   <li>Layers with <b>fewer</b> presets: padded with the layer's
     *       {@link DisplayLayer#getDefaultColor()}, falling back to {@code 0xFFFFFF}.</li>
     *   <li>Layers with <b>more</b> presets: truncated to {@code presetCount}.</li>
     * </ul>
     * No-op if {@code presetCount} has not been set.
     *
     * @return {@code this} for chaining
     */
    public DisplayComponent normalizeLayerPresets() {
        if (presetCount < 0) return this;
        for (DisplayLayer layer : layers.values()) {
            List<Color> presets = layer.getColorPresets();
            int current = presets.size();
            if (current < presetCount) {
                int fallback = layer.hasDefaultColor() ? layer.getDefaultColor() : 0xFFFFFF;
                for (int i = current; i < presetCount; i++) {
                    layer.addColorPreset(fallback);
                }
            } else if (current > presetCount) {
                layer.truncateColorPresets(presetCount);
            }
        }
        return this;
    }

    // ── Layer management ───────────────────────────────────────────────────────

    /**
     * Registers a {@link DisplayLayer} on this component.
     *
     * @return {@code this} for chaining
     */
    public DisplayComponent addLayer(DisplayLayer layer) {
        layers.put(layer.id, layer);
        return this;
    }

    /**
     * Returns the layer with the given id, or {@code null} if not found.
     */
    public DisplayLayer getLayer(String id) {
        return layers.get(id.toLowerCase());
    }

    public boolean hasLayer(String id) {
        return layers.containsKey(id.toLowerCase());
    }

    public Map<String, DisplayLayer> getLayers() {
        return Collections.unmodifiableMap(layers);
    }

    public int getLayerCount() {
        return layers.size();
    }

    // ── Sub-component ──────────────────────────────────────────────────────────

    /**
     * Sets the single sub-component for this component.
     * The sub-component must not itself have a sub-component.
     * Silently ignored if {@code sub} already has a sub-component set.
     *
     * @return {@code this} for chaining
     */
    public DisplayComponent setSubComponent(DisplayComponent sub) {
        if (sub != null && sub.hasSubComponent()) {
            // No grandchildren allowed
            return this;
        }
        this.subComponent = sub;
        return this;
    }

    /** Returns the sub-component, or {@code null} if none is set. */
    public DisplayComponent getSubComponent() {
        return subComponent;
    }

    public boolean hasSubComponent() {
        return subComponent != null;
    }

    // ── Layer search (includes sub-component) ──────────────────────────────────

    /**
     * Searches this component and its sub-component for a layer with the given id.
     * Returns the first match, or {@code null}.
     */
    public DisplayLayer findLayer(String id) {
        String key = id.toLowerCase();
        DisplayLayer own = layers.get(key);
        if (own != null) return own;
        if (subComponent != null) return subComponent.getLayer(key);
        return null;
    }

    @Override
    public DataCompound serialize(DataCompound data) {
        if (hasPresetCount()) data.putInt("presetCount", presetCount);
        for (Map.Entry<String, DisplayLayer> entry : layers.entrySet())
            data.put(entry.getKey(), entry.getValue());
        if (subComponent != null)
            data.put(subComponent.id, subComponent);
        return data;
    }

    @Override
    public void deserialize(DataCompound data) {
        if (data.has("presetCount")) setPresetCount(data.getInt("presetCount", -1));
        for (Map.Entry<String, DisplayLayer> entry : layers.entrySet())
            data.deserialize(entry.getKey(), entry.getValue());
        if (subComponent != null)
            data.deserialize(subComponent.id, subComponent);
    }
}
