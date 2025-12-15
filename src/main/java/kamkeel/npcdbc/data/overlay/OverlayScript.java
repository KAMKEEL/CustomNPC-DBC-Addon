package kamkeel.npcdbc.data.overlay;

import kamkeel.npcdbc.client.utils.Color;

public class OverlayScript extends JaninoScript<OverlayScript.Functions> {

    public OverlayScript() {
        super(Functions.class, (builder) -> builder.setDefaultImports(
                "kamkeel.npcdbc.client.utils.Color",
                "kamkeel.npcdbc.data.overlay.OverlayContext"));
    }

    public static class Functions implements Overlay.TextureFunction, Overlay.ColorFunction {
        public Color getColor(OverlayContext ctx) {
            return null;
        }

        public String getTexture(OverlayContext ctx) {
            return null;
        }
    }
}
