package kamkeel.npcdbc.data.overlay;

import io.github.somehussar.janinoloader.api.script.IScriptBodyBuilder;
import kamkeel.npcdbc.client.utils.Color;

public class OverlayScript extends JaninoScript<OverlayScript.OverlayFunctions> {

    public OverlayScript() {
        super(OverlayFunctions.class);
    }

    @Override
    protected void configure(IScriptBodyBuilder<OverlayFunctions> builder) {
        builder.setDefaultImports(
                "kamkeel.npcdbc.client.utils.Color",
                "kamkeel.npcdbc.data.overlay.OverlayContext"
        );
    }

    public static class OverlayFunctions implements Overlay.TextureFunction, Overlay.ColorFunction {
        public Color getColor(OverlayContext ctx) {
            return null;
        }

        public String getTexture(OverlayContext ctx) {
            return null;
        }
    }

    public enum ScriptType {
        OnEffectAdd("onEffectAdd"),
        OnEffectTick("onEffectTick"),
        OnEffectRemove("onEffectRemove");

        public final String function;

        ScriptType(String functionName) {
            this.function = functionName;
        }
    }
}
