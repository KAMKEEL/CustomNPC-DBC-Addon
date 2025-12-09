package kamkeel.npcdbc.client.render;

import kamkeel.npcdbc.data.form.OverlayManager;

import static kamkeel.npcdbc.client.model.ModelDBC.HD;
import static kamkeel.npcdbc.client.model.ModelDBC.PATH;
import static kamkeel.npcdbc.data.form.OverlayManager.ColorType.Fur;
import static kamkeel.npcdbc.data.form.OverlayManager.ColorType.Hair;
import static kamkeel.npcdbc.data.form.OverlayManager.Type.*;

public class DBCOverlays {

    public static OverlayManager Savior = new OverlayManager();
    public static OverlayManager SSJ4_FUR = new OverlayManager();

    static {
        Savior.add(Face).texture(PATH + "savior/savioreyes.png").colorType(Fur);
        Savior.add(Face).texture(PATH + "savior/saviormouth.png").color(0Xffffff);
        Savior.add(Chest).texture(PATH + "savior/saviorchest.png").colorType(Hair);


        SSJ4_FUR.add(ALL).texture((tex, data, o) -> HD ? PATH + "ssj4/ss4b" + data.furType() + ".png" : "jinryuudragonbc:cc/ss4b").colorType(Fur);
    }
}
