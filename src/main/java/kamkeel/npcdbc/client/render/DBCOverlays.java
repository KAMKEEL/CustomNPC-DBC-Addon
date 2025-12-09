package kamkeel.npcdbc.client.render;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.data.form.OverlayManager;

import static kamkeel.npcdbc.data.form.OverlayManager.Type.*;

public class DBCOverlays {

    public static OverlayManager Savior = new OverlayManager();

    static {
        Savior.add(Face).add(ALL).add(Chest);

    }

    public static OverlayManager.Overlay SSJ3_FACE = new OverlayManager.Overlay().applyTexture((texture, data, overlay) -> {

        return "/male/face_" + data.eyeType() + "/" + data.mouthType + "/" + data.noseType + "/";
    }).applyColor((color, alpha, data, overlay) -> {


        return null;
    });

    // public static OverlayManager.Overlay SSJ4_Fur = .............
}
