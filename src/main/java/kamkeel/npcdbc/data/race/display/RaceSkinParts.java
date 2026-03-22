package kamkeel.npcdbc.data.race.display;

import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class RaceSkinParts {

    public enum PartType {
        BODY("body", new ResourceLocation("jinryuudragonbc", "textures/skin/body/default.png")),
        EYES("eyes", new ResourceLocation("jinryuudragonbc", "textures/skin/eyes/default.png")),
        NOSE("nose", new ResourceLocation("jinryuudragonbc", "textures/skin/nose/default.png")),
        MOUTH("mouth", new ResourceLocation("jinryuudragonbc", "textures/skin/mouth/default.png"));

        public final String id;
        public final ResourceLocation fallback;

        PartType(String id, ResourceLocation fallback) {
            this.id = id;
            this.fallback = fallback;
        }
    }

    private final Map<PartType, ResourceLocation[]> parts = new HashMap<>();

    public RaceSkinParts setParts(PartType type, ResourceLocation... textures) {
        parts.put(type, textures);
        return this;
    }

    public ResourceLocation[] getParts(PartType type) {
        return parts.getOrDefault(type, new ResourceLocation[]{type.fallback});
    }

    public ResourceLocation getPart(PartType type, int index) {
        ResourceLocation[] textures = getParts(type);
        if (index < 0 || index >= textures.length)
            return type.fallback;
        return textures[index];
    }

    public int getCount(PartType type) {
        ResourceLocation[] textures = parts.get(type);
        if (textures == null) return 1;
        return textures.length;
    }

    /**
     * Generates the skinLimits int[6] array expected by the DBC GUI system.
     * [0] = body types
     * [1] = unknown1 (default 1)
     * [2] = noses
     * [3] = mouths
     * [4] = eyes
     * [5] = unknown2 (default 2)
     */
    public int[] toSkinLimits() {
        return new int[]{
            getCount(PartType.BODY),
            1,
            getCount(PartType.NOSE),
            getCount(PartType.MOUTH),
            getCount(PartType.EYES),
            2
        };
    }
}
