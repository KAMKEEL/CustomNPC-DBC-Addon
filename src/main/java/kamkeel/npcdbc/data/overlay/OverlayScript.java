package kamkeel.npcdbc.data.overlay;

import io.github.somehussar.janinoloader.api.script.IScriptBodyBuilder;
import kamkeel.npcdbc.client.utils.Color;

public class OverlayScript extends JaninoScript<OverlayScript.Functions> {

    public OverlayScript() {
        super(Functions.class);
    }

    @Override
    protected void configure(IScriptBodyBuilder<Functions> builder) {
        builder.setDefaultImports(
                "kamkeel.npcdbc.client.utils.Color",
                "kamkeel.npcdbc.data.overlay.OverlayContext"
        );
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
