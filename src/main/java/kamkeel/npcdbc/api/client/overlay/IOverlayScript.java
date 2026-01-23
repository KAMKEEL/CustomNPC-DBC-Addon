package kamkeel.npcdbc.api.client.overlay;

import kamkeel.npcdbc.api.Color;
import noppes.npcs.janino.annotations.ParamName;

public interface IOverlayScript {

    abstract class Functions {
        public Color getColor(@ParamName("ctx") IOverlayContext ctx) {
            return null;
        }

        public String getTexture(@ParamName("ctx") IOverlayContext ctx) {
            return null;
        }
    }
}
