package kamkeel.npcdbc.data.race.display;

import kamkeel.npcdbc.api.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A single visual layer within a {@link DisplayComponent}.
 * <p>
 * Each layer represents one color/texture channel of a component (e.g. "bodycm",
 * "bodyc1"). It holds:
 * <ul>
 *   <li><b>textureVariants</b> — ordered list of texture paths the player can cycle through.</li>
 *   <li><b>textureOverride</b> — optional fixed texture path, ignoring player choice.</li>
 *   <li><b>colorOverride</b> — optional fixed color, ignoring player choice.</li>
 *   <li><b>colorPresets</b> — optional list of pre-defined colors the player can pick.</li>
 * </ul>
 */
public class DisplayLayer {

    /** Unique identifier for this layer. Always lower-case. */
    public final String id;

    /** Human-readable display name shown in UI contexts. */
    public final String displayName;

    /** Ordered texture variants the player can choose from. */
    private final List<String> textureVariants = new ArrayList<>();

    /**
     * Optional texture override. When set, this texture is always used
     * regardless of the player's variant selection.
     */
    private String textureOverride = null;

    /**
     * Optional color override. When set, this color is always applied
     * to this layer regardless of the player's color choice.
     */
    private Color colorOverride = null;

    /**
     * Pre-defined colors the player can pick from. Empty means no presets —
     * the player uses the free color picker directly.
     */
    private final List<Color> colorPresets = new ArrayList<>();

    /**
     * Default color for this layer. Used when no player color has been set.
     * {@code null} means no default (falls back to 0).
     */
    private Color defaultColor = null;

    public DisplayLayer(String id, String displayName) {
        this.id          = id.toLowerCase();
        this.displayName = displayName;
    }

    // ── Texture variants ───────────────────────────────────────────────────────

    /**
     * Adds a texture variant path to the end of the variant list.
     *
     * @return {@code this} for chaining
     */
    public DisplayLayer addTextureVariant(String texturePath) {
        textureVariants.add(texturePath);
        return this;
    }

    /**
     * Returns the texture path at the given variant index,
     * or {@code null} if the index is out of bounds.
     */
    public String getTextureVariant(int index) {
        if (index < 0 || index >= textureVariants.size()) return null;
        return textureVariants.get(index);
    }

    /** Returns how many texture variants this layer declares. */
    public int getTextureVariantCount() {
        return textureVariants.size();
    }

    public List<String> getTextureVariants() {
        return Collections.unmodifiableList(textureVariants);
    }

    // ── Texture override ───────────────────────────────────────────────────────

    /**
     * Sets a fixed texture override. When present, this texture is always used
     * regardless of the player's variant index.
     *
     * @return {@code this} for chaining
     */
    public DisplayLayer setTextureOverride(String texturePath) {
        this.textureOverride = texturePath;
        return this;
    }

    /** Returns the texture override path, or {@code null} if none is set. */
    public String getTextureOverride() {
        return textureOverride;
    }

    public boolean hasTextureOverride() {
        return textureOverride != null;
    }

    /**
     * Resolves the effective texture path for the given variant index.
     * Returns the override if set, otherwise the variant at the given index.
     */
    public String resolveTexture(int variantIndex) {
        if (textureOverride != null) return textureOverride;
        return getTextureVariant(variantIndex);
    }

    // ── Color override ─────────────────────────────────────────────────────────

    /**
     * Sets a fixed color override for this layer. When present, the player
     * cannot change this layer's color.
     *
     * @return {@code this} for chaining
     */
    public DisplayLayer setColorOverride(Color color) {
        this.colorOverride = color;
        return this;
    }

    /** Convenience overload accepting a raw ARGB int. */
    public DisplayLayer setColorOverride(int color) {
        return setColorOverride(new Color(color));
    }

    /** Returns the color override, or {@code null} if none is set. */
    public Color getColorOverride() {
        return colorOverride;
    }

    public boolean hasColorOverride() {
        return colorOverride != null;
    }

    // ── Color presets ──────────────────────────────────────────────────────────

    /**
     * Adds a pre-defined color preset the player can choose from.
     *
     * @return {@code this} for chaining
     */
    public DisplayLayer addColorPreset(Color color) {
        colorPresets.add(color);
        return this;
    }

    /** Convenience overload accepting a raw ARGB int. */
    public DisplayLayer addColorPreset(int color) {
        return addColorPreset(new Color(color));
    }

    public List<Color> getColorPresets() {
        return Collections.unmodifiableList(colorPresets);
    }

    public int getColorPresetCount() {
        return colorPresets.size();
    }

    public boolean hasColorPresets() {
        return !colorPresets.isEmpty();
    }

    /**
     * Truncates the color preset list to {@code count} entries.
     * No-op if the list is already shorter.
     */
    public void truncateColorPresets(int count) {
        if (colorPresets.size() > count) {
            colorPresets.subList(count, colorPresets.size()).clear();
        }
    }

    // ── Default color ──────────────────────────────────────────────────────────

    /**
     * Sets the default color for this layer, used when no player color is set.
     *
     * @return {@code this} for chaining
     */
    public DisplayLayer setDefaultColor(Color color) {
        this.defaultColor = color;
        return this;
    }

    /** Convenience overload accepting a raw ARGB int. */
    public DisplayLayer setDefaultColor(int color) {
        return setDefaultColor(new Color(color));
    }

    /** Returns the default color int, or {@code 0} if none is set. */
    public int getDefaultColor() {
        return defaultColor != null ? defaultColor.color : 0;
    }

    public boolean hasDefaultColor() {
        return defaultColor != null;
    }

    /**
     * Resolves the effective color: override takes priority,
     * then default, then 0.
     */
    public int resolveColor(int playerColor) {
        if (colorOverride != null) return colorOverride.color;
        return playerColor != 0 ? playerColor : getDefaultColor();
    }
}
