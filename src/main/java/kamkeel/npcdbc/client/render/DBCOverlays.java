package kamkeel.npcdbc.client.render;

import kamkeel.npcdbc.data.form.OverlayManager;

import static kamkeel.npcdbc.client.model.ModelDBC.*;
import static kamkeel.npcdbc.data.form.OverlayManager.ColorType.Fur;
import static kamkeel.npcdbc.data.form.OverlayManager.ColorType.Hair;
import static kamkeel.npcdbc.data.form.OverlayManager.Type.*;

public class DBCOverlays {

    public static OverlayManager Savior = new OverlayManager();
    public static OverlayManager SSJ4_FUR = new OverlayManager();

    static {
        Savior.add(Face, path("savior/savioreyes.png"), Fur);
        Savior.add(Face, path("savior/saviormouth.png"), 0XFFFFFF);
        Savior.add(Chest, path("savior/saviorchest.png"), Hair);


        SSJ4_FUR.add(ALL, Fur).texture((tex, data, o) -> path("ssj4/ss4b" + data.furType() + ".png", "jinryuudragonbc:cc/ss4b"));
    }
}
