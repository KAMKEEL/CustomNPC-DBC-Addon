package kamkeel.npcdbc.api.client.overlay;

import kamkeel.npcdbc.api.Color;

public interface IOverlayScript {

    abstract class Functions {
        public Color getColor(IOverlayContext ctx) {
            return null;
        }

        public String getTexture(IOverlayContext ctx) {
            return null;
        }
    }
}
