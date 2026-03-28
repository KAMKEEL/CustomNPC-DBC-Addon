package kamkeel.npcdbc.data.race.display;

import kamkeel.npcdbc.api.Color;
import kamkeel.npcdbc.data.race.serial.DataCompound;
import kamkeel.npcdbc.data.race.serial.DataSerializable;

import java.util.*;

public class RaceDisplay implements DataSerializable {

    // ── Built-in component IDs ─────────────────────────────────────────────────
    public static final String COMPONENT_BODY = "body";
    public static final String COMPONENT_FACE = "face";
    public static final String COMPONENT_EYES = "eyes";
    public static final String COMPONENT_HAIR = "hair";

    // ── Built-in layer IDs ─────────────────────────────────────────────────────
    public static final String LAYER_BODY_CM = "bodycm";
    public static final String LAYER_BODY_C1 = "bodyc1";
    public static final String LAYER_BODY_C2 = "bodyc2";
    public static final String LAYER_BODY_C3 = "bodyc3";
    public static final String LAYER_EYE = "eye";
    public static final String LAYER_EYEBROWS = "eyebrows";
    public static final String LAYER_EYEBASE = "eyebase";
    public static final String LAYER_LEFT_EYE = "lefteye";
    public static final String LAYER_RIGHT_EYE = "righteye";
    public static final String LAYER_NOSE = "nose";
    public static final String LAYER_MOUTH = "mouth";
    public static final String LAYER_HAIR = "hair";
    public static final String LAYER_FUR = "fur";

    /** Canonical body layer order for DBC metadata. */
    private static final String[] BODY_LAYER_ORDER = {
        LAYER_BODY_CM, LAYER_BODY_C1, LAYER_BODY_C2, LAYER_BODY_C3
    };

    // ── Components ─────────────────────────────────────────────────────────────
    /**
     * Top-level display components, keyed by {@link DisplayComponent#id}.
     * Every {@code RaceDisplay} starts with two built-in components:
     * {@link #COMPONENT_BODY} and {@link #COMPONENT_FACE}
     * (face has {@link #COMPONENT_EYES} as its sub-component).
     */
    private final Map<String, DisplayComponent> components = new LinkedHashMap<>();

    // ── Creator metadata ───────────────────────────────────────────────────────
    /**
     * Opaque key used by the client-side race renderer registry to look up
     * the {@code IRaceRenderer} implementation for this race.
     * May be {@code null} if the race has no custom client renderer.
     */
    public String rendererKey;

    /**
     * Skin customization limits for the DBC creator GUI.
     * Indices: [0]=bodyType, [1]=colorSlots, [2]=nose, [3]=mouth, [4]=eyes, [5]=eyeColorSlots
     * Default matches Human: {1, 1, 5, 5, 6, 2}
     */
    private int[] skinLimits = {1, 1, 5, 5, 6, 2};

    /** Number of genders: 1 = male only, 2 = male + female. Default: 2 */
    private int genderCount = 2;

    /**
     * Hair type string for DBC's RaceCanHaveHair array.
     * "H" = human hair, "A" = antenna, "R" = arcosian ridges, "X" = none.
     */
    private String hairType = "H";

    /**
     * Number of body color presets. Auto-derived by {@link #syncCreatorMetadata()}.
     * Defaults to 1.
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
     * Fixed hair color int, or -1 for player-choosable.
     * Derived from the hair layer's color override.
     */
    public int fixedHairColor = -1;

    /**
     * Permission string for DBC's RaceAllow array.
     * "DBC" = requires DBC, "All" = always available, "HHC" = requires HHC.
     */
    private String raceAllow = "DBC";

    // ── Constructor ────────────────────────────────────────────────────────────

    public RaceDisplay() {
        // Built-in body component: bodycm always present
        DisplayComponent bodyComponent = new DisplayComponent(COMPONENT_BODY, "Body");
        bodyComponent.addLayer(new DisplayLayer(LAYER_BODY_CM, "Body Main"));
        addComponent(bodyComponent);

        // Built-in eyes sub-component
        DisplayComponent eyesComponent = new DisplayComponent(COMPONENT_EYES, "Eyes");
        eyesComponent.addLayer(new DisplayLayer(LAYER_LEFT_EYE, "Left Eye"));
        eyesComponent.addLayer(new DisplayLayer(LAYER_RIGHT_EYE, "Right Eye"));
        eyesComponent.addLayer(new DisplayLayer(LAYER_EYE, "Eyes"));

        // Built-in face component; eyes is its sub-component
        DisplayComponent faceComponent = new DisplayComponent(COMPONENT_FACE, "Face");
        faceComponent.addLayer(new DisplayLayer(LAYER_EYEBASE, "Eye Base"));
        faceComponent.addLayer(new DisplayLayer(LAYER_EYEBROWS, "Eyebrows"));
        faceComponent.setSubComponent(eyesComponent);
        faceComponent.addLayer(new DisplayLayer(LAYER_NOSE, "Nose"));
        faceComponent.addLayer(new DisplayLayer(LAYER_MOUTH, "Mouth"));
        addComponent(faceComponent);

        // Built-in hair component: hair always present, visibility defined by renderer
        DisplayComponent hairComponent = new DisplayComponent(COMPONENT_HAIR, "Hair");
        hairComponent.addLayer(new DisplayLayer(LAYER_HAIR, "Hair"));
        addComponent(hairComponent);
    }

    // ── Component management ───────────────────────────────────────────────────

    /**
     * Registers a top-level {@link DisplayComponent}.
     * Use to add custom components beyond the built-ins (body, face).
     */
    public void addComponent(DisplayComponent component) {
        components.put(component.id, component);
    }

    /** Returns a top-level component by id, or {@code null} if not found. */
    public DisplayComponent getComponent(String id) {
        return components.get(id.toLowerCase());
    }

    public boolean hasComponent(String id) {
        return components.containsKey(id.toLowerCase());
    }

    public Map<String, DisplayComponent> getComponents() {
        return Collections.unmodifiableMap(components);
    }

    // ── Layer convenience ──────────────────────────────────────────────────────

    /**
     * Adds a {@link DisplayLayer} directly to the named top-level component.
     * No-op if the component does not exist.
     */
    public void addLayer(String componentId, DisplayLayer layer) {
        DisplayComponent component = getComponent(componentId);
        if (component != null) component.addLayer(layer);
    }

    /**
     * Searches all top-level components and their sub-components for a layer
     * with the given id. Returns the first match, or {@code null}.
     */
    public DisplayLayer getLayer(String layerId) {
        String key = layerId.toLowerCase();
        for (DisplayComponent component : components.values()) {
            DisplayLayer found = component.findLayer(key);
            if (found != null) return found;
        }
        return null;
    }

    public boolean hasLayer(String layerId) {
        return getLayer(layerId) != null;
    }

    // ── Properties ─────────────────────────────────────────────────────────────

    public int getGenderCount() { return genderCount; }

    public void setGenderCount(int genderCount) {
        this.genderCount = Math.max(1, Math.min(2, genderCount));
    }

    public String getHairType() { return hairType; }

    public void setHairType(String hairType) {
        if (isHairTypeValid(hairType)) this.hairType = hairType;
    }

    private boolean isHairTypeValid(String h) {
        return h != null && (h.equals("H") || h.equals("A") || h.equals("R") || h.equals("X"));
    }

    public String getAllowedPowerTypes() { return allowedPowerTypes; }

    public void setAllowedPowerTypes(String allowedPowerTypes) {
        if (isPowerTypeValid(allowedPowerTypes)) this.allowedPowerTypes = allowedPowerTypes;
    }

    private boolean isPowerTypeValid(String pwt) {
        if (pwt == null || pwt.isEmpty() || pwt.length() > 3) return false;
        for (int i = 0; i < pwt.length(); i++) {
            char c = pwt.charAt(i);
            if (c < '0' || c > '2') return false;
        }
        return true;
    }

    public int getCustomSkinMode() { return customSkinMode; }

    public void setCustomSkinMode(int customSkinMode) {
        this.customSkinMode = Math.max(0, Math.min(2, customSkinMode));
    }

    public String getRaceAllow() { return raceAllow; }

    public void setRaceAllow(String raceAllow) {
        if (isRaceAllowValid(raceAllow)) this.raceAllow = raceAllow;
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
     * Returns how many body color layers (bodycm → bodyc3) the body component declares.
     * Always between 1 and 4. Drives {@code skinLimits[1]}.
     */
    public int getBodyColorLayerCount() {
        DisplayComponent bodyComponent = getComponent(COMPONENT_BODY);
        if (bodyComponent == null) return 1;
        int count = 0;
        for (String id : BODY_LAYER_ORDER) {
            if (bodyComponent.hasLayer(id)) count++;
        }
        return Math.max(count, 1);
    }

    /**
     * Returns the ordered list of body color layer ids present in the body component,
     * following DBC canonical order: bodycm → bodyc1 → bodyc2 → bodyc3.
     */
    public List<String> getBodyColorLayerIds() {
        DisplayComponent bodyComponent = getComponent(COMPONENT_BODY);
        List<String> ids = new ArrayList<>();
        if (bodyComponent != null) {
            for (String id : BODY_LAYER_ORDER) {
                if (bodyComponent.hasLayer(id)) ids.add(id);
            }
        }
        if (ids.isEmpty()) ids.add(LAYER_BODY_CM);
        return ids;
    }

    /**
     * Returns how many eye color layers the eyes sub-component declares.
     * Checks LEFT_EYE / RIGHT_EYE / EYE (generic). Always 0–2.
     * Drives {@code skinLimits[5]}.
     */
    public int getEyeColorLayerCount() {
        DisplayComponent faceComponent = getComponent(COMPONENT_FACE);
        if (faceComponent == null) return 0;
        DisplayComponent eyesComponent = faceComponent.getSubComponent();
        if (eyesComponent == null) return 0;

        boolean hasLeft = eyesComponent.hasLayer(LAYER_LEFT_EYE)  || eyesComponent.hasLayer(LAYER_EYE);
        boolean hasRight = eyesComponent.hasLayer(LAYER_RIGHT_EYE) || eyesComponent.hasLayer(LAYER_EYE);
        return (hasLeft ? 1 : 0) + (hasRight ? 1 : 0);
    }

    /**
     * Returns the number of texture variants for the given layer id.
     * Returns 1 as safe minimum when absent or empty.
     * Drives {@code skinLimits[2..4]}.
     */
    public int getTextureVariantCount(String layerId) {
        DisplayLayer layer = getLayer(layerId);
        return (layer != null && layer.getTextureVariantCount() > 0)
            ? layer.getTextureVariantCount()
            : 1;
    }

    /**
     * Returns the fixed hair color from the hair layer's color override,
     * or -1 if the player can choose freely.
     */
    public int getFixedHairColor() {
        DisplayLayer hairLayer = getLayer(LAYER_HAIR);
        if (hairLayer != null && hairLayer.isFixedColor()) {
            return hairLayer.getDefaultColor();
        }
        return -1;
    }

    // ── Metadata sync ──────────────────────────────────────────────────────────

    /**
     * Synchronises {@link #skinLimits}, {@link #bodyColorPresetCount}, and
     * {@link #fixedHairColor} from the current component/layer declarations.
     * BodyStates are excluded — metadata always reflects the base display.
     * Idempotent; call once after build.
     */
    public void syncCreatorMetadata() {
        skinLimits[1] = getBodyColorLayerCount();
        skinLimits[2] = getTextureVariantCount("nose");
        skinLimits[3] = getTextureVariantCount("mouth");
        skinLimits[4] = getTextureVariantCount("eye_left");
        skinLimits[5] = getEyeColorLayerCount();
        fixedHairColor = getFixedHairColor();

        // bodyColorPresetCount: from component presetCount if set, else max across layers
        DisplayComponent bodyComponent = getComponent(COMPONENT_BODY);
        if (bodyComponent != null && bodyComponent.hasPresetCount()) {
            bodyColorPresetCount = Math.max(1, bodyComponent.getPresetCount());
        } else if (bodyComponent != null) {
            int maxPresets = 0;
            for (DisplayLayer layer : bodyComponent.getLayers().values()) {
                maxPresets = Math.max(maxPresets, layer.getColorPresetCount());
            }
            bodyColorPresetCount = Math.max(1, maxPresets);
        } else {
            bodyColorPresetCount = 1;
        }
    }

    // ── Color / texture build helpers ──────────────────────────────────────────

    /**
     * Builds the body color array in DBC canonical order (CM, C1, C2, C3).
     * For each layer, uses the color override if set, otherwise the player's
     * color (passed in via {@code playerColors} by index), otherwise the
     * layer's default color.
     *
     * @param playerColors player-chosen colors indexed by body layer order;
     *                     may be {@code null} or shorter than the layer count
     */
    //    public int[] buildBodyColorRow(int[] playerColors) {
    //        List<String> layerIds = getBodyColorLayerIds();
    //        DisplayComponent bodyComponent = getComponent(COMPONENT_BODY);
    //        int[] row = new int[layerIds.size()];
    //
    //        for (int i = 0; i < layerIds.size(); i++) {
    //            int playerColor = (playerColors != null && i < playerColors.length) ? playerColors[i] : 0;
    //            if (bodyComponent != null) {
    //                DisplayLayer layer = bodyComponent.getLayer(layerIds.get(i));
    //                if (layer != null) {
    //                    row[i] = layer.resolveColor(playerColor);
    //                    continue;
    //                }
    //            }
    //            row[i] = playerColor;
    //        }
    //        return row;
    //    }

    /** Builds the default body color row using each layer's default color. */
    public int[] buildDefaultBodyColorRow() {
        List<String> layerIds = getBodyColorLayerIds();
        DisplayComponent bodyComponent = getComponent(COMPONENT_BODY);
        int[] row = new int[layerIds.size()];

        for (int i = 0; i < layerIds.size(); i++) {
            if (bodyComponent != null) {
                DisplayLayer layer = bodyComponent.getLayer(layerIds.get(i));
                if (layer != null) {
                    row[i] = layer.getDefaultColor();
                    continue;
                }
            }
            row[i] = 0;
        }
        return row;
    }

    /**
     * Builds the body color array for a specific preset index.
     * For each layer, reads {@code colorPresets.get(presetIndex)}.
     * Falls back to {@link DisplayLayer#getDefaultColor()} if the preset at that index
     * is absent (list was not yet normalized), then to {@code 0}.
     *
     * @param presetIndex zero-based index into each layer's color preset list
     */
    public int[] buildBodyColorRowForPreset(int presetIndex) {
        List<String> layerIds = getBodyColorLayerIds();
        DisplayComponent bodyComponent = getComponent(COMPONENT_BODY);
        int[] row = new int[layerIds.size()];

        for (int i = 0; i < layerIds.size(); i++) {
            if (bodyComponent != null) {
                DisplayLayer layer = bodyComponent.getLayer(layerIds.get(i));
                if (layer != null) {
                    List<Color> presets = layer.getColorPresets();
                    if (presetIndex < presets.size()) {
                        row[i] = presets.get(presetIndex).color;
                    } else {
                        row[i] = layer.getDefaultColor();
                    }
                    continue;
                }
            }
            row[i] = 0;
        }
        return row;
    }

    /**
     * Builds an eye color row [generic, left, right] from the eyes layers' defaults.
     */
    public int[] buildEyeColorRows() {
        DisplayComponent faceComponent = getComponent(COMPONENT_FACE);
        DisplayComponent eyesComponent = faceComponent != null ? faceComponent.getSubComponent() : null;

        int genericEye = 0;
        int leftEye = 0;
        int rightEye = 0;

        if (eyesComponent != null) {
            DisplayLayer eyeLayer = eyesComponent.getLayer(LAYER_EYE);
            DisplayLayer leftLayer = eyesComponent.getLayer(LAYER_LEFT_EYE);
            DisplayLayer rightLayer = eyesComponent.getLayer(LAYER_RIGHT_EYE);

            if (eyeLayer  != null) genericEye = eyeLayer.getDefaultColor();
            leftEye = (leftLayer  != null && leftLayer.hasDefaultColor())  ? leftLayer.getDefaultColor()  : genericEye;
            rightEye = (rightLayer != null && rightLayer.hasDefaultColor()) ? rightLayer.getDefaultColor() : genericEye;
        }

        return new int[]{genericEye, leftEye, rightEye};
    }

    @Override
    public DataCompound serialize(DataCompound data) {
        data.comment("Display config. Components keyed by component id.");
        data.putInt("genderCount", genderCount);
        data.putString("hairType", hairType);
        data.putString("allowedPowerTypes", allowedPowerTypes);
        data.putInt("customSkinMode", customSkinMode);
        data.putString("raceAllow", raceAllow);
        data.putIntArray("skinLimits", skinLimits);
        for (Map.Entry<String, DisplayComponent> entry : components.entrySet())
            data.put(entry.getKey(), entry.getValue());
        return data;
    }

    @Override
    public void deserialize(DataCompound data) {
        if (data.has("genderCount"))      setGenderCount(data.getInt("genderCount", genderCount));
        if (data.has("hairType"))         setHairType(data.getString("hairType", hairType));
        if (data.has("allowedPowerTypes")) setAllowedPowerTypes(data.getString("allowedPowerTypes", allowedPowerTypes));
        if (data.has("customSkinMode"))   setCustomSkinMode(data.getInt("customSkinMode", customSkinMode));
        if (data.has("raceAllow"))        setRaceAllow(data.getString("raceAllow", raceAllow));
        if (data.has("skinLimits")) {
            int[] limits = data.getIntArray("skinLimits", skinLimits);
            if (limits.length == 6) {
                setSkinLimits(limits[0], limits[1], limits[2], limits[3], limits[4], limits[5]);
            }
        }
        for (Map.Entry<String, DisplayComponent> entry : components.entrySet())
            data.deserialize(entry.getKey(), entry.getValue());
        syncCreatorMetadata();
    }
}
