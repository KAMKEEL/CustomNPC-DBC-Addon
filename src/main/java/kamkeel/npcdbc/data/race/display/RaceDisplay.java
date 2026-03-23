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
