package kamkeel.npcdbc.data.overlay;

import kamkeel.npcdbc.api.client.overlay.IOverlayModelScript;
import noppes.npcs.janino.JaninoScript;

/**
 * Janino script type for scripted overlay models.
 * <p>
 * Provides default imports so script authors can use GL11, OverlayContext,
 * IOverlayContext, Color, and ModelRenderer without explicit import statements.
 */
public class OverlayModelScript extends JaninoScript<IOverlayModelScript.Functions> implements IOverlayModelScript {

    /**
     * Default imports available to overlay model scripts without explicit import statements.
     */
    public static final String[] DEFAULT_IMPORTS = {
            "kamkeel.npcdbc.data.overlay.OverlayContext",
            "kamkeel.npcdbc.api.client.overlay.IOverlayContext",
            "kamkeel.npcdbc.api.Color",
            "net.minecraft.client.model.ModelRenderer",
            "net.minecraft.client.model.ModelBiped",
            "org.lwjgl.opengl.GL11",
    };

    public OverlayModelScript() {
        super(Functions.class, DEFAULT_IMPORTS);
    }
}
