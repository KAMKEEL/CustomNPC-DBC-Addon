package kamkeel.npcdbc.data.race.display;

import net.minecraft.util.ResourceLocation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextureSlot {
    public static final String BODY = "body";
    public static final String EYEBROW = "eyebrow";
    public static final String EYEWHITE = "eyewhite";
    public static final String EYE_RIGHT = "eye_right";
    public static final String EYE_LEFT = "eye_left";
    public static final String MOUTH = "mouth";
    public static final String NOSE = "nose";

    public final String id;
    private final List<ResourceLocation> variations = new ArrayList<>();

    public TextureSlot(String id) {
        this.id = id;
    }

    public TextureSlot add(ResourceLocation texture) {
        variations.add(texture);
        return this;
    }

    public ResourceLocation get(int index) {
        if (index < 0 || index >= variations.size())
            return null;
        return variations.get(index);
    }

    public int getCount() {
        return variations.size();
    }

    public List<ResourceLocation> getVariations() {
        return Collections.unmodifiableList(variations);
    }
}
