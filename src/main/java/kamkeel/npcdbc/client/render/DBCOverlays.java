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
        Savior.add(Face).texture(path("savior/savioreyes.png")).colorType(Fur);
        Savior.add(Face).texture(path("savior/saviormouth.png")).color(0Xffffff);
        Savior.add(Chest).texture(path("savior/saviorchest.png")).colorType(Hair);


        SSJ4_FUR.add(ALL).texture((tex, data, o) -> path("ssj4/ss4b" + data.furType() + ".png", "jinryuudragonbc:cc/ss4b")).colorType(Fur);
    }
}
