package kamkeel.npcdbc.data.race.display;

import java.util.*;

public class RaceDisplay {
    private final Map<String, ColorSlot> colorSlots = new LinkedHashMap<>();
    private final List<ColorPreset> colorPresets = new ArrayList<>();
    private final Map<String, TextureSlot> textureSlots = new LinkedHashMap<>();

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

    public void addTextureSlot(TextureSlot slot) {
        textureSlots.put(slot.id, slot);
    }

    public ColorSlot getColorSlot(String id) {
        return colorSlots.get(id.toLowerCase());
    }

    public boolean hasColorSlot(String id) {
        return colorSlots.containsKey(id.toLowerCase());
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

    public Map<String, TextureSlot> getTextureSlots() {
        return Collections.unmodifiableMap(textureSlots);
    }
}
