package kamkeel.npcdbc.data.overlay;

import kamkeel.npcdbc.api.client.overlay.IOverlayScript;
import noppes.npcs.janino.JaninoScript;

public class OverlayScript extends JaninoScript<IOverlayScript.Functions> implements IOverlayScript {

    public OverlayScript() {
        super(Functions.class, (builder) -> builder.setDefaultImports(
                "kamkeel.npcdbc.api.Color",
                "kamkeel.npcdbc.api.client.overlay.IOverlayContext"));
    }
}
