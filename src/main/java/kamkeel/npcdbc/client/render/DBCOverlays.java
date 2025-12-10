package kamkeel.npcdbc.client.render;

import kamkeel.npcdbc.data.overlay.OverlayChain;

import static kamkeel.npcdbc.client.model.ModelDBC.*;
import static kamkeel.npcdbc.data.overlay.Overlay.ColorType.Fur;
import static kamkeel.npcdbc.data.overlay.Overlay.ColorType.Hair;
import static kamkeel.npcdbc.data.overlay.Overlay.Type.*;

public class DBCOverlays {

    public static OverlayChain Savior = new OverlayChain();
    public static OverlayChain SSJ4_FUR = new OverlayChain();

    static {
        Savior.add(Face, path("savior/savioreyes.png"), Fur);
        Savior.add(Face, path("savior/saviormouth.png"), 0XFFFFFF);
        Savior.add(Chest, path("savior/saviorchest.png"), Hair);


        SSJ4_FUR.add(ALL, Fur).texture((tex, data, o) -> path("ssj4/ss4b" + data.furType() + ".png", "jinryuudragonbc:cc/ss4b"));
    }
}
