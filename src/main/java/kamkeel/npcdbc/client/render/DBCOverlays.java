package kamkeel.npcdbc.client.render;

import kamkeel.npcdbc.data.overlay.OverlayChain;

import static kamkeel.npcdbc.client.model.ModelDBC.*;
import static kamkeel.npcdbc.data.overlay.Overlay.ColorType.*;

import static kamkeel.npcdbc.data.overlay.Overlay.Type.*;

public class DBCOverlays {

    public static OverlayChain Savior = OverlayChain.create("Savior");
    public static OverlayChain SSJ4_FUR = OverlayChain.create("SSJ4_Fur");
    public static OverlayChain SSJ4_FACE = OverlayChain.create("SSJ4_Face");

    static {
        Savior.add(Face, path("savior/savioreyes.png"), Fur);
        Savior.add(Face, path("savior/saviormouth.png"), 0XFFFFFF);
        Savior.add(Chest, path("savior/saviorchest.png"), Hair);


        SSJ4_FUR.add(ALL, Fur).texture((tex, ctx) -> path("ssj4/ss4b" + ctx.furType() + ".png", "jinryuudragonbc:cc/ss4b"));

        SSJ4_FACE.add(Face, Body).texture(((tex, ctx1) -> HD(ctx1.furDaima() ? "ssj4d" : "ssj4" + ctx1.genderDirectory() + "/face_" + ctx1.eyeType() + "/ssj4shade.png")));
        SSJ4_FACE.add(Face, 0xFFFFFF).texture(((tex, ctx1) -> HD(ctx1.furDaima() ? "ssj4d" : "ssj4" + ctx1.genderDirectory() + "/face_" + ctx1.eyeType() + "/ssj4eyewhite.png")));
        SSJ4_FACE.add(Face, Fur).texture(((tex, ctx1) -> HD(ctx1.furDaima() ? "ssj4d" : "ssj4" + ctx1.genderDirectory() + "/face_" + ctx1.eyeType() + "/ssj4brows.png")));
        SSJ4_FACE.add(Face, Hair).texture(((tex, ctx1) -> HD(ctx1.furDaima() ? "ssj4d" : "ssj4" + ctx1.genderDirectory() + "/face_" + ctx1.eyeType() + "/ssj4brows2.png")));
        SSJ4_FACE.add(Face, Eye).texture(((tex, ctx1) -> HD(ctx1.furDaima() ? "ssj4d" : "ssj4" + ctx1.genderDirectory() + "/face_" + ctx1.eyeType() + "/ssj4eyeleft.png")));
        SSJ4_FACE.add(Face, Eye).texture(((tex, ctx1) -> HD(ctx1.furDaima() ? "ssj4d" : "ssj4" + ctx1.genderDirectory() + "/face_" + ctx1.eyeType() + "/ssj4eyeright.png")));
    }
}
