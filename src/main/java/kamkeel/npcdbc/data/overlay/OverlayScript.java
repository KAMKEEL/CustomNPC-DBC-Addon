package kamkeel.npcdbc.data.overlay;

import kamkeel.npcdbc.api.client.overlay.IOverlayScript;
import noppes.npcs.janino.JaninoScript;

public class OverlayScript extends JaninoScript<IOverlayScript.Functions> implements IOverlayScript {

    /**
     * Default imports available to overlay scripts without explicit import statements.
     */
    public static final String[] DEFAULT_IMPORTS = {
        "kamkeel.npcdbc.api.Color",
        "kamkeel.npcdbc.api.client.overlay.IOverlayContext"
    };

    public OverlayScript() {
        super(Functions.class, DEFAULT_IMPORTS, true);
    }
}
