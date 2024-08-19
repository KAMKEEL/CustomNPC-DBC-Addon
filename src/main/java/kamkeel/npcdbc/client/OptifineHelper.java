package kamkeel.npcdbc.client;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import static kamkeel.npcdbc.client.shader.PostProcessing.mc;

public class OptifineHelper {
    public static HashMap<Integer, Entity> POSTPROCESSING_ENTITY_MAP = new LinkedHashMap<>();
    public static boolean shaderPackLoaded = false;

    public static boolean isQueued(Entity entity) {
        return POSTPROCESSING_ENTITY_MAP.containsKey(entity.getEntityId());

    }

    public static void enqueue(Entity entity) {
        POSTPROCESSING_ENTITY_MAP.put(entity.getEntityId(), entity);

    }

    public static void dequeue(Entity entity) {
        POSTPROCESSING_ENTITY_MAP.remove(entity.getEntityId());

    }

    public static void process() {
        for (Iterator<Entity> iter = POSTPROCESSING_ENTITY_MAP.values().iterator(); iter.hasNext(); ) {
            Entity entity = iter.next();
            RenderManager.instance.renderEntitySimple(entity, mc.timer.renderPartialTicks);
            iter.remove();
        }
    }
}
