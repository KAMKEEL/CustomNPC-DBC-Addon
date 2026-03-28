package kamkeel.npcdbc.data.race.display;

import kamkeel.npcdbc.api.Color;
import kamkeel.npcdbc.client.race.RaceRenderContext;
import kamkeel.npcdbc.data.race.serial.DataCompound;
import kamkeel.npcdbc.data.race.serial.DataSerializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * A single visual layer within a {@link DisplayComponent}.
 * <p>
 * Each layer represents one color/texture channel of a component (e.g. "bodycm",
 * "bodyc1"). It holds:
 * <ul>
 *   <li><b>textureVariants</b> — ordered list of texture paths the player can cycle through.</li>
 *   <li><b>fixedTexture</b> — if true, default texture path is used, ignoring player choice.</li>
 *   <li><b>fixedColor</b> — if true, default color is used, ignoring player choice.</li>
 *   <li><b>colorPresets</b> — optional list of pre-defined colors the player can pick.</li>
 * </ul>
 */
public class DisplayLayer implements DataSerializable {

    /** Unique identifier for this layer. Always lower-case. */
    public final String id;

    /** Human-readable display name shown in UI contexts. */
    public final String displayName;

    /** Ordered texture variants the player can choose from. */
    private final List<String> textureVariants = new ArrayList<>();

    /**
     * Default texture for this layer. Used when no texture has been set.
     * falls back to white texture.
     */
    private String defaultTexture = "jinryuumodscore:cc/hum.png";

    private Function<RaceRenderContext, String> textureFunction = null;

    /**
     * When true, default texture is always applied to this
     * layer regardless of the variant.
     */
    private boolean fixedTexture = false;

    /**
     * When true, default color is always applied to this
     * layer regardless of the player's color choice.
     */
    private boolean fixedColor = false;

    /**
     * Pre-defined colors the player can pick from. Empty means no presets —
     * the player uses the free color picker directly.
     */
    private final List<Color> colorPresets = new ArrayList<>();

    /**
     * Default color for this layer. Used when no player color has been set.
     * falls back to white.
     */
    private Color defaultColor = new Color(0xFFFFFF);

    private Function<RaceRenderContext, Color> colorFunction = null;

    public DisplayLayer(String id, String displayName) {
        this.id = id.toLowerCase();
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

    public void clearTextureVariants() {
        textureVariants.clear();
    }

    // ── Texture override ───────────────────────────────────────────────────────

    public boolean isFixedTexture() {
        return fixedTexture;
    }

    public DisplayLayer setFixedTexture(boolean fixedTexture) {
        this.fixedTexture = fixedTexture;
        return this;
    }

    // ── Default texture ────────────────────────────────────────────────────────

    /**
     * Sets a fixed texture override. When present, this texture is always used
     * regardless of the player's variant index.
     *
     * @return {@code this} for chaining
     */
    public DisplayLayer setDefaultTexture(String texturePath) {
        this.defaultTexture = texturePath;
        return this;
    }

    /** Returns the texture override path, or {@code null} if none is set. */
    public String getDefaultTexture() {
        return defaultTexture;
    }

    public boolean hasTextureOverride() {
        return defaultTexture != null;
    }

    // ── Texture function ────────────────────────────────────────────────────────

    public DisplayLayer setTextureFunction(Function<RaceRenderContext, String> textureFunction) {
        this.textureFunction = textureFunction;
        return this;
    }

    public Function<RaceRenderContext, String> getTextureFunction() {
        return textureFunction;
    }

    public boolean hasTextureFunction() {
        return textureFunction != null;
    }

    /**
     * Resolves the effective texture path for the given variant index.
     * Returns the default texture if fixedTexture is true, otherwise the variant at the given index.
     */
    public String resolveTexture(RaceRenderContext ctx, int variantIndex) {
        if (fixedTexture) return getDefaultTexture();
        if (hasTextureFunction()) return textureFunction.apply(ctx);
        return getTextureVariant(variantIndex);
    }

    // ── Color override ─────────────────────────────────────────────────────────

    public DisplayLayer setFixedColor(boolean fixedColor) {
        this.fixedColor = fixedColor;
        return this;
    }

    public boolean isFixedColor() {
        return this.fixedColor;
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

    public void clearColorPresets() {
        colorPresets.clear();
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

    // ── Color function ─────────────────────────────────────────────────────────

    public DisplayLayer setColorFunction(Function<RaceRenderContext, Color> colorFunction) {
        this.colorFunction = colorFunction;
        return this;
    }

    public Function<RaceRenderContext, Color> getColorFunction() {
        return colorFunction;
    }

    public boolean hasColorFunction() {
        return colorFunction != null;
    }

    /**
     * Resolves the effective color: override takes priority,
     * then default, then 0.
     */
    public int resolveColor(RaceRenderContext ctx) {
        if (fixedColor) return getDefaultColor();
        if (colorFunction != null && colorFunction.apply(ctx) != null) return colorFunction.apply(ctx).color;
        return getDefaultColor();
    }

    @Override
    public DataCompound serialize(DataCompound data) {
        data.comment("Colors as hex RRGGBB strings.");
        if (!textureVariants.isEmpty()) {
            data.putStringList("textureVariants", new ArrayList<String>(textureVariants));
        }

        data.putString("defaultTexture", defaultTexture);
        data.putBoolean("fixedTexture", fixedTexture);
        data.putString("defaultColor", String.format("%06X", getDefaultColor()));
        data.putBoolean("fixedColor", fixedColor);

        if (!colorPresets.isEmpty()) {
            List<String> presets = new ArrayList<String>();
            for (Color c : colorPresets) presets.add(String.format("%06X", c.color));
            data.putStringList("colorPresets", presets);
        }
        return data;
    }

    @Override
    public void deserialize(DataCompound data) {
        if (data.has("textureVariants")) {
            clearTextureVariants();
            for (String tv : data.getStringList("textureVariants")) addTextureVariant(tv);
        }

        if (data.has("defaultTexture")) {
            setDefaultTexture(data.getString("defaultTexture", "jinryuumodscore:cc/hum.png"));
        }
        if (data.has("defaultColor")) {
            try { setDefaultColor(Integer.parseInt(data.getString("defaultColor", "FFFFFF"), 16)); } catch (NumberFormatException ignored) {}
        }

        if (data.has("fixedTexture")) {
            setFixedTexture(data.getBoolean("fixedTexture", false));
        }
        if (data.has("fixedColor")) {
            setFixedColor(data.getBoolean("fixedColor", false));
        }

        if (data.has("colorPresets")) {
            clearColorPresets();
            for (String cp : data.getStringList("colorPresets")) {
                try { addColorPreset(Integer.parseInt(cp, 16)); } catch (NumberFormatException ignored) {}
            }
        }
    }
}
