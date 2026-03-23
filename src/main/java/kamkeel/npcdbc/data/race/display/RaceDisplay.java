package kamkeel.npcdbc.data.race.display;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.api.Color;
import kamkeel.npcdbc.client.race.IRaceRenderer;

import java.util.*;

public class RaceDisplay {
    private final Map<String, ColorSlot> colorSlots = new LinkedHashMap<>();
    private final List<ColorPreset> colorPresets = new ArrayList<>();
    private final Map<String, Color> colorOverrides = new HashMap<>();
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
     * "H" = human hair, "A" = antenna (Namekian), "R" = arcosian ridges.
     * Default: "H"
     */
    public String hairType = "H";

    /** Number of body color presets (customSknLimitsBCP equivalent). Default: 7 */
    public int bodyColorPresetCount = 7;

    /**
     * Default eye colors per preset row. Each entry is one color int per preset.
     * Must have exactly as many entries as there are preset rows in defeyecols.
     * Default matches Human column from vanilla defeyecols: {1, 4896782, 14617612}
     */
    public int[] defaultEyeColors = {1, 4896782, 14617612};

    /**
     * Default body colors per preset row.
     * Outer dimension = preset count (matches defbodycols preset rows).
     * Inner dimension = body color components [CM, C1, C2?, C3?] — variable length.
     * Default matches Human column from vanilla defbodycols.
     */
    public int[][] defaultBodyColors = {
        {16297621, 6498048},
        {10112303, 6498048},
        {7225375, 6498048},
        {3677711, 6498048},
        {16297621, 6498048},
        {10112303, 6498048},
        {7225375, 6498048}
    };

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
}
