package kamkeel.npcdbc.data.race.display;

import kamkeel.npcdbc.api.Color;

import java.util.*;

public class RaceDisplay {

    private static final String[] BODY_COLOR_SLOT_ORDER = {
        ColorSlot.BODY_CM, ColorSlot.BODY_C1, ColorSlot.BODY_C2, ColorSlot.BODY_C3
    };

    // ── Layers ─────────────────────────────────────────────────────────────────
    /**
     * Top-level color layers, keyed by {@link ColorLayer#id}.
     * Every {@code RaceDisplay} starts with three built-in layers:
     * {@link ColorLayer#BODY}, {@link ColorLayer#FACE}, and {@link ColorLayer#EYES}
     * (eyes is nested inside face as a sub-layer).
     */
    private final Map<String, ColorLayer> layers = new LinkedHashMap<>();

    // ── Presets / overrides / defaults ────────────────────────────────────────
    private final List<ColorPreset> colorPresets = new ArrayList<>();
    private final Map<String, Color> colorOverrides = new HashMap<>();
    private final ColorPreset defaultColorPreset = new ColorPreset();
    private final Map<String, TextureSlot> textureSlots = new LinkedHashMap<>();

    /**
     * Named body states that layer on top of this display when active.
     * The base display is always the initial state. Keyed by {@link BodyState#id}.
     */
    private final Map<String, BodyState> bodyStates = new LinkedHashMap<>();

    // ── Creator metadata ───────────────────────────────────────────────────────
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
    private int[]  skinLimits = {1, 1, 5, 5, 6, 2};

    /** Number of genders: 1 = male only, 2 = male + female. Default: 2 */
    private int genderCount = 2;

    /**
     * Hair type string for DBC's RaceCanHaveHair array.
     * "H" = human hair, "A" = antenna (Namekian), "R" = arcosian ridges, "X" = none.
     * Default: "H"
     */
    private String hairType = "H";

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
    private int customSkinMode = 2;

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
    private String raceAllow = "DBC";

    // ── Constructor ────────────────────────────────────────────────────────────

    public RaceDisplay() {
        // Built-in body layer: bodycm always present; further slots added via bodyColorSlots()
        ColorLayer bodyLayer = new ColorLayer(ColorLayer.BODY, "Body")
            .addSlot(ColorSlot.BODY_CM, "Body Color Main");
        addLayer(bodyLayer);

        // Built-in eyes sub-layer
        ColorLayer eyesLayer = new ColorLayer(ColorLayer.EYES, "Eyes")
            .addSlot(ColorSlot.EYES, "Eyes")
            .addSlot(ColorSlot.LEFT_EYE, "Left Eye")
            .addSlot(ColorSlot.RIGHT_EYE, "Right Eye");

        // Built-in face layer; eyes is a child
        ColorLayer faceLayer = new ColorLayer(ColorLayer.FACE, "Face");
        faceLayer.addSubLayer(eyesLayer);
        addLayer(faceLayer);

        // Default texture slots
        addTextureSlot(new TextureSlot(TextureSlot.BODY));
        addTextureSlot(new TextureSlot(TextureSlot.EYEBROW));
        addTextureSlot(new TextureSlot(TextureSlot.EYEWHITE));
        addTextureSlot(new TextureSlot(TextureSlot.EYE_RIGHT));
        addTextureSlot(new TextureSlot(TextureSlot.EYE_LEFT));
        addTextureSlot(new TextureSlot(TextureSlot.MOUTH));
        addTextureSlot(new TextureSlot(TextureSlot.NOSE));
    }

    // ── Layer management ───────────────────────────────────────────────────────

    /**
     * Registers a top-level {@link ColorLayer}. Use to add custom layers
     * beyond the three built-ins (body, face, face/eyes).
     */
    public void addLayer(ColorLayer layer) {
        layers.put(layer.id, layer);
    }

    /**
     * Returns a top-level layer by id, or {@code null} if not found.
     * Does NOT search sub-layers of children.
     */
    public ColorLayer getLayer(String id) {
        return layers.get(id.toLowerCase());
    }

    public boolean hasLayer(String id) {
        return layers.containsKey(id.toLowerCase());
    }

    public Map<String, ColorLayer> getLayers() {
        return Collections.unmodifiableMap(layers);
    }

    // ── Slot convenience (delegates to layers) ─────────────────────────────────

    /**
     * Adds a {@link ColorSlot} directly to the named top-level layer.
     * No-op if the layer does not exist.
     */
    public void addColorSlot(String layerId, ColorSlot slot) {
        ColorLayer layer = getLayer(layerId);
        if (layer != null) layer.addSlot(slot);
    }

    /**
     * Scans all top-level layers and their immediate sub-layers for a slot
     * with the given id. Returns the first match, or {@code null}.
     * Prefer going through a specific layer when the owner is known.
     */
    public ColorSlot getColorSlot(String slotId) {
        String key = slotId.toLowerCase();
        for (ColorLayer layer : layers.values()) {
            if (layer.hasSlot(key)) return layer.getSlot(key);
            for (ColorLayer sub : layer.getSubLayers().values()) {
                if (sub.hasSlot(key)) return sub.getSlot(key);
            }
        }
        return null;
    }

    /** Returns whether any layer (or immediate sub-layer) contains the given slot id. */
    public boolean hasColorSlot(String slotId) {
        return getColorSlot(slotId) != null;
    }

    /**
     * Returns all slots owned by a specific top-level layer.
     * Returns an empty map if the layer is not found.
     */
    public Map<String, ColorSlot> getColorSlots(String layerId) {
        ColorLayer layer = getLayer(layerId);
        return layer != null ? layer.getSlots() : Collections.emptyMap();
    }

    // ── Color presets ──────────────────────────────────────────────────────────

    public void addColorPreset(ColorPreset preset)  { colorPresets.add(preset); }
    public List<ColorPreset> getColorPresets() { return Collections.unmodifiableList(colorPresets); }

    // ── Color overrides ────────────────────────────────────────────────────────

    public void addColorOverride(String slotId, Color color) {
        colorOverrides.put(slotId.toLowerCase(), color);
    }

    public Color getColorOverride(String slotId) { return colorOverrides.get(slotId.toLowerCase()); }
    public boolean hasColorOverride(String slotId) { return colorOverrides.containsKey(slotId.toLowerCase()); }
    public Map<String, Color> getColorOverrides() { return Collections.unmodifiableMap(colorOverrides); }

    // ── Default colors ─────────────────────────────────────────────────────────

    public void setDefaultColor(String slotId, int color) {
        defaultColorPreset.set(slotId, new Color(color));
    }

    public int getDefaultColor(String slotId) {
        Color c = defaultColorPreset.get(slotId);
        return c != null ? c.color : 0;
    }

    public boolean hasDefaultColor(String slotId) { return defaultColorPreset.has(slotId); }
    public ColorPreset getDefaultColorPreset() { return defaultColorPreset; }

    // ── Texture slots ──────────────────────────────────────────────────────────

    public void addTextureSlot(TextureSlot slot) { textureSlots.put(slot.id, slot); }
    public TextureSlot getTextureSlot(String id) { return textureSlots.get(id); }
    public boolean hasTextureSlot(String id) { return textureSlots.containsKey(id); }
    public Map<String, TextureSlot> getTextureSlots() { return Collections.unmodifiableMap(textureSlots); }

    // ── Body states ────────────────────────────────────────────────────────────

    public void addBodyState(BodyState state) { bodyStates.put(state.id, state); }
    public BodyState getBodyState(String id) { return bodyStates.get(id.toLowerCase()); }
    public boolean hasBodyState(String id) { return bodyStates.containsKey(id.toLowerCase()); }
    public Map<String, BodyState> getBodyStates() { return Collections.unmodifiableMap(bodyStates); }

    // ── Properties ─────────────────────────────────────────────────────────────

    public int getGenderCount() {
        return genderCount;
    }

    public void setGenderCount(int genderCount) {
        this.genderCount = Math.max(1, Math.min(2, genderCount));
    }

    public String getHairType() {
        return hairType;
    }

    public void setHairType(String hairType) {
        if (!isHairTypeValid(hairType)) return;
        this.hairType = hairType;
    }

    private boolean isHairTypeValid(String h) {
        return h != null && (h.equals("H") || h.equals("A") || h.equals("R") || h.equals("X"));
    }

    public String getAllowedPowerTypes() {
        return allowedPowerTypes;
    }

    public void setAllowedPowerTypes(String allowedPowerTypes) {
        if (!isPowerTypeValid(allowedPowerTypes)) return;
        this.allowedPowerTypes = allowedPowerTypes;
    }

    private boolean isPowerTypeValid(String pwt) {
        if (pwt == null || pwt.isEmpty() || pwt.length() > 3) return false;

        for (int i = 0; i < pwt.length(); i++) {
            char c = pwt.charAt(i);
            if (c < '0' || c > '2') return false;
        }

        return true;
    }

    public int getCustomSkinMode() {
        return customSkinMode;
    }

    public void setCustomSkinMode(int customSkinMode) {
        this.customSkinMode = Math.max(0, Math.min(2, customSkinMode));
    }

    public String getRaceAllow() {
        return raceAllow;
    }

    public void setRaceAllow(String raceAllow) {
        if (!isRaceAllowValid(raceAllow)) return;
        this.raceAllow = raceAllow;
    }

    private boolean isRaceAllowValid(String ra) {
        return ra != null && (ra.equals("All") || ra.equals("DBC") || ra.equals("HHC"));
    }

    public int[] getSkinLimits() {
        return Arrays.copyOf(skinLimits, skinLimits.length);
    }

    public void setSkinLimits(int bodyType, int colorSlots, int nose, int mouth, int eyes, int eyeColorSlots) {
        skinLimits = new int[]{bodyType, colorSlots, nose, mouth, eyes, eyeColorSlots};
    }

    // ── Body color introspection ───────────────────────────────────────────────

    /**
     * Returns how many body color slots (bodycm → bodyc3) the body layer declares.
     * Always between 1 and 4. Drives {@code skinLimits[1]}.
     */
    public int getBodyColorSlotCount() {
        ColorLayer bodyLayer = getLayer(ColorLayer.BODY);
        if (bodyLayer == null) return 1;
        int count = 0;
        for (String id : BODY_COLOR_SLOT_ORDER) {
            if (bodyLayer.hasSlot(id)) count++;
        }
        return Math.max(count, 1);
    }

    /**
     * Returns the ordered list of body color slot ids present in the body layer,
     * following DBC canonical order: bodycm → bodyc1 → bodyc2 → bodyc3.
     */
    public List<String> getBodyColorSlotIds() {
        ColorLayer bodyLayer = getLayer(ColorLayer.BODY);
        List<String> ids = new ArrayList<>();
        if (bodyLayer != null) {
            for (String id : BODY_COLOR_SLOT_ORDER) {
                if (bodyLayer.hasSlot(id)) ids.add(id);
            }
        }
        if (ids.isEmpty()) ids.add(ColorSlot.BODY_CM);
        return ids;
    }

    /**
     * Returns how many eye color slots the eyes sub-layer declares.
     * Checks LEFT_EYE / RIGHT_EYE / EYES (generic). Always 0–2.
     * Drives {@code skinLimits[5]}.
     */
    public int getEyeColorSlotCount() {
        ColorLayer faceLayer = getLayer(ColorLayer.FACE);
        if (faceLayer == null) return 0;
        ColorLayer eyesLayer = faceLayer.getSubLayer(ColorLayer.EYES);
        if (eyesLayer == null) return 0;

        boolean hasLeft  = eyesLayer.hasSlot(ColorSlot.LEFT_EYE)
            || eyesLayer.hasSlot(ColorSlot.EYES);
        boolean hasRight = eyesLayer.hasSlot(ColorSlot.RIGHT_EYE)
            || eyesLayer.hasSlot(ColorSlot.EYES);
        return (hasLeft ? 1 : 0) + (hasRight ? 1 : 0);
    }

    /**
     * Returns the number of texture variations for the given slot.
     * Returns 1 as safe minimum when absent or empty.
     * Drives {@code skinLimits[2..4]}.
     */
    public int getTextureVariationCount(String slotId) {
        TextureSlot slot = textureSlots.get(slotId);
        return slot != null && slot.getCount() > 0 ? slot.getCount() : 1;
    }

    public int getFixedHairColor() {
        Color override = getColorOverride(ColorSlot.HAIR);
        return override != null ? override.color : -1;
    }

    // ── Metadata sync ──────────────────────────────────────────────────────────

    /**
     * Synchronises {@link #skinLimits} and {@link #bodyColorPresetCount} from
     * the current layer/texture/preset declarations.
     * BodyStates are excluded — metadata always reflects the base display.
     * Idempotent; call once after build.
     */
    public void syncCreatorMetadata() {
        skinLimits[1] = getBodyColorSlotCount();
        skinLimits[2] = getTextureVariationCount(TextureSlot.NOSE);
        skinLimits[3] = getTextureVariationCount(TextureSlot.MOUTH);
        skinLimits[4] = getTextureVariationCount(TextureSlot.EYE_LEFT);
        skinLimits[5] = getEyeColorSlotCount();
        fixedHairColor = getFixedHairColor();
        bodyColorPresetCount = Math.max(1, colorPresets.size());
    }

    // ── Color / texture build helpers ──────────────────────────────────────────

    /**
     * Builds the body color array for one preset row (CM, C1, C2, C3 order).
     * Falls back to default color, then 0.
     */
    public int[] buildBodyColorRow(ColorPreset preset) {
        List<String> slotIds = getBodyColorSlotIds();
        int[] row = new int[slotIds.size()];
        for (int i = 0; i < slotIds.size(); i++) {
            String slotId = slotIds.get(i);
            row[i] = (preset != null && preset.has(slotId))
                ? preset.get(slotId).color
                : getDefaultColor(slotId);
        }
        return row;
    }

    public int[] buildDefaultBodyColorRow() {
        return buildBodyColorRow(defaultColorPreset);
    }

    /**
     * Builds an eye color row [generic, left, right] from declared defaults.
     */
    public int[] buildEyeColorRows() {
        int genericEye = getDefaultColor(ColorSlot.EYES);
        return new int[]{
            genericEye,
            hasDefaultColor(ColorSlot.LEFT_EYE)  ? getDefaultColor(ColorSlot.LEFT_EYE)  : genericEye,
            hasDefaultColor(ColorSlot.RIGHT_EYE) ? getDefaultColor(ColorSlot.RIGHT_EYE) : genericEye
        };
    }
}
