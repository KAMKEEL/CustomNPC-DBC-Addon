package kamkeel.npcdbc.client.modern;

import java.util.HashMap;
import java.util.Map;

import static kamkeel.npcdbc.client.modern.ModernGLHelper.createVAO;

public class ModernModels {
    public static Map<Integer, ModernModel> loadedModels = new HashMap();
    public static ModernModel quad;

    public static void loadModels() {
        float[] vertices = {
            // Position          // Normal            // Tex Coords  // Color
            -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,    // Bottom-left
            -0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,    // Top-left
            0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,     // Top-right
            0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f,     // Bottom-right
        };

        int[] indices = {0, 1, 2, 2, 3, 0};
        quad = createVAO(vertices, indices);
    }

    public static void delete() { //memory management, cleans up vaos and vbos from memory
        for (ModernModel model : loadedModels.values())
            model.destroy();
    }
}
