package kamkeel.npcdbc.data.race.display;

import kamkeel.npcdbc.api.Color;

import java.util.*;

public class RaceDisplay {
    private static final String[] BODY_COLOR_SLOT_ORDER = {
        ColorSlot.BODY_CM, ColorSlot.BODY_C1, ColorSlot.BODY_C2, ColorSlot.BODY_C3
    };
    
    private final Map<String, ColorSlot> colorSlots = new LinkedHashMap<>();
    private final List<ColorPreset> colorPresets = new ArrayList<>();
    private final Map<String, Color> colorOverrides = new HashMap<>();
    private final ColorPreset defaultColorPreset = new ColorPreset();
    private final Map<String, TextureSlot> textureSlots = new LinkedHashMap<>();

    /**
     * Opaque key used by the client-side race renderer registry to look up
     * the {@code IRaceRenderer} implementation for this race.
     * Common-safe: this is just a plain string, never a client class reference.
     * May be {@code null} if the race has no custom client renderer.
     */
    public String rendererKey;

    /**
     * Skin customization limits for the DBC creator GUI.
     * Indices: [0]=bodyType, [1]=colorSlots, [2]=nose, [3]=mouth, [4]=eyes, [5]=eyeColorSlots
     * Default matches Human: {1, 1, 5, 5, 6, 2}
     */
    public int[] skinLimits = {1, 1, 5, 5, 6, 2};

    /** Number of genders: 1 = male only, 2 = male + female. Default: 2 */
    public int genderCount = 2;

    /**
     * Hair type string for DBC's RaceCanHaveHair array.
     * "H" = human hair, "A" = antenna (Namekian), "R" = arcosian ridges, "X" = none.
     * Default: "H"
     */
    public String hairType = "H";

    /**
     * Number of body color presets (customSknLimitsBCP equivalent).
     * Auto-derived by {@link #syncCreatorMetadata()} from color presets / default colors.
     * Defaults to 1 for custom races unless explicit presets are declared.
     */
    public int bodyColorPresetCount = 1;

    /**
     * Allowed power types as a string of digits. e.g. "012" means types 0,1,2.
     * Matches JRMCoreH.RaceCanHavePwr format. Default: "012"
     */
    public String allowedPowerTypes = "012";

    /**
     * Custom skin mode for DBC's RaceCustomSkin array.
     * 0 = no custom skin, 1 = forced custom, 2 = optional. Default: 2
     */
    public int customSkinMode = 2;

    /**
     * Fixed hair color index, or -1 for player-choosable.
     * Matches JRMCoreH.RaceHairColor. Default: -1
     */
    public int fixedHairColor = -1;

    /**
     * Permission string for DBC's RaceAllow array.
     * "DBC" = requires DBC, "All" = always available, "HHC" = requires HHC.
     * Default: "DBC"
     */
    public String raceAllow = "DBC";
    
    public RaceDisplay() {
        addColorSlot(new ColorSlot(ColorSlot.EYES, "Eyes"));
        addColorSlot(new ColorSlot(ColorSlot.LEFT_EYE, "Left Eye"));
        addColorSlot(new ColorSlot(ColorSlot.RIGHT_EYE, "Right Eye"));
        addColorSlot(new ColorSlot(ColorSlot.BODY_CM, "Body Color Main"));

        addTextureSlot(new TextureSlot(TextureSlot.BODY));
        addTextureSlot(new TextureSlot(TextureSlot.EYEBROW));
        addTextureSlot(new TextureSlot(TextureSlot.EYEWHITE));
        addTextureSlot(new TextureSlot(TextureSlot.EYE_RIGHT));
        addTextureSlot(new TextureSlot(TextureSlot.EYE_LEFT));
        addTextureSlot(new TextureSlot(TextureSlot.MOUTH));
        addTextureSlot(new TextureSlot(TextureSlot.NOSE));
    }

    public void addColorSlot(ColorSlot slot) {
        colorSlots.put(slot.id, slot);
    }

    public void addColorPreset(ColorPreset preset) {
        colorPresets.add(preset);
    }

    public void addColorOverride(String slotId, Color color) {
        colorOverrides.put(slotId.toLowerCase(), color);
    }

    public void setDefaultColor(String slotId, int color) {
        defaultColorPreset.set(slotId, new Color(color));
    }

    public int getDefaultColor(String slotId) {
        Color c = defaultColorPreset.get(slotId);
        return c != null ? c.color : 0;
    }

    public boolean hasDefaultColor(String slotId) {
        return defaultColorPreset.has(slotId);
    }

    public ColorPreset getDefaultColorPreset() {
        return defaultColorPreset;
    }

    public void addTextureSlot(TextureSlot slot) {
        textureSlots.put(slot.id, slot);
    }

    public ColorSlot getColorSlot(String id) {
        return colorSlots.get(id.toLowerCase());
    }

    public boolean hasColorSlot(String id) {
        return colorSlots.containsKey(id.toLowerCase());
    }

    public Color getColorOverride(String slotId) {
        return colorOverrides.get(slotId.toLowerCase());
    }

    public boolean hasColorOverride(String slotId) {
        return colorOverrides.containsKey(slotId.toLowerCase());
    }

    public TextureSlot getTextureSlot(String id) {
        return textureSlots.get(id);
    }

    public boolean hasTextureSlot(String id) {
        return textureSlots.containsKey(id);
    }

    public Map<String, ColorSlot> getColorSlots() {
        return Collections.unmodifiableMap(colorSlots);
    }

    public List<ColorPreset> getColorPresets() {
        return Collections.unmodifiableList(colorPresets);
    }

    public Map<String, Color> getColorOverrides() {
        return Collections.unmodifiableMap(colorOverrides);
    }

    public Map<String, TextureSlot> getTextureSlots() {
        return Collections.unmodifiableMap(textureSlots);
    }

    // ══════════════════════════════════════════════════════════
    // Body color introspection
    // ══════════════════════════════════════════════════════════

    /**
     * Returns how many body color slots (bodycm, bodyc1, bodyc2, bodyc3) this
     * race has defined. The result is always between 1 and 4.
     * <p>
     * This drives {@code customSknLimits[race][1]} in the DBC creator.
     */
    public int getBodyColorSlotCount() {
        int count = 0;
        for (String id : BODY_COLOR_SLOT_ORDER) {
            if (colorSlots.containsKey(id)) count++;
        }
        return Math.max(count, 1);
    }

    /**
     * Returns an ordered list of the body color slot ids that are present,
     * following DBC's canonical order: bodycm → bodyc1 → bodyc2 → bodyc3.
     */
    public List<String> getBodyColorSlotIds() {
        List<String> ids = new ArrayList<>();
        for (String id : BODY_COLOR_SLOT_ORDER) {
            if (colorSlots.containsKey(id)) ids.add(id);
        }
        if (ids.isEmpty()) ids.add(ColorSlot.BODY_CM);
        return ids;
    }

    /**
     * Synchronises DBC creator metadata fields ({@link #skinLimits}[1] and
     * {@link #bodyColorPresetCount}) so they stay consistent with the
     * structural display model (color slots, presets, and default colors).
     * <p>
     * This should be called once, after build, before the expanded arrays
     * are generated. It is <em>idempotent</em>.
     */
    public void syncCreatorMetadata() {
        skinLimits[1] = getBodyColorSlotCount();

        bodyColorPresetCount = Math.max(1, colorPresets.size());
    }

    /**
     * Builds the body color component array for a single preset row,
     * using the canonical body-color slot order (CM, C1, C2, C3).
     * Only includes slots that are actually declared on this display.
     * Falls back to the default preset color, or 0 if none is set.
     */
    public int[] buildBodyColorRow(ColorPreset preset) {
        List<String> slotIds = getBodyColorSlotIds();
        int[] row = new int[slotIds.size()];
        for (int i = 0; i < slotIds.size(); i++) {
            String slotId = slotIds.get(i);
            if (preset != null && preset.has(slotId)) {
                row[i] = preset.get(slotId).color;
            } else {
                row[i] = getDefaultColor(slotId);
            }
        }
        return row;
    }

    /**
     * Builds a default body color row from the declared default preset.
     */
    public int[] buildDefaultBodyColorRow() {
        return buildBodyColorRow(defaultColorPreset);
    }

    public int[] buildEyeColorRows() {
        int genericEye = getDefaultColor(ColorSlot.EYES);
        return new int[]{
            genericEye,
            hasDefaultColor(ColorSlot.LEFT_EYE) ? getDefaultColor(ColorSlot.LEFT_EYE) : genericEye,
            hasDefaultColor(ColorSlot.RIGHT_EYE) ? getDefaultColor(ColorSlot.RIGHT_EYE) : genericEye
        };
    }
}
