package kamkeel.npcdbc.client.render;

import kamkeel.npcdbc.data.overlay.OverlayChain;

import static kamkeel.npcdbc.client.model.ModelDBC.*;
import static kamkeel.npcdbc.data.overlay.Overlay.ColorType.*;

import static kamkeel.npcdbc.data.overlay.Overlay.Type.*;

public class DBCOverlays {

    public static OverlayChain Savior = OverlayChain.create("Savior");
    public static OverlayChain SSJ4_FUR = OverlayChain.create("SSJ4_Fur");
    public static OverlayChain OOZARU = OverlayChain.create("Oozaru");
    public static OverlayChain SSJ4_FACE = OverlayChain.create("SSJ4_Face");

    static {
        Savior.add(Face, Fur, path("savior/savioreyes.png"));
        Savior.add(Face, 0XFFFFFF, path("savior/saviormouth.png"));
        Savior.add(Chest, Hair, path("savior/saviorchest.png"));

        SSJ4_FUR.add(ALL, Fur, (ctx) -> path("ssj4/ss4b" + ctx.furType() + ".png", "jinryuudragonbc:cc/ss4b"));

        OOZARU.add(ALL, Body, path("oozaru/oozaru1.png", "jinryuudragonbc:cc/oozaru1.png"));
        OOZARU.add(ALL, Fur, path("oozaru/oozaru2.png", "jinryuudragonbc:cc/oozaru2.png"));

        SSJ4_FACE.add(Face, Body, (ctx) -> HD(ctx.furDaima() ? "ssj4d" : "ssj4" + ctx.genderDirectory() + "/face_" + ctx.eyeType() + "/ssj4shade.png"));
        SSJ4_FACE.add(Face, 0xFFFFFF, (ctx) -> HD(ctx.furDaima() ? "ssj4d" : "ssj4" + ctx.genderDirectory() + "/face_" + ctx.eyeType() + "/ssj4eyewhite.png"));
        SSJ4_FACE.add(Face, Fur, (ctx) -> HD(ctx.furDaima() ? "ssj4d" : "ssj4" + ctx.genderDirectory() + "/face_" + ctx.eyeType() + "/ssj4brows.png"));
        SSJ4_FACE.add(Face, Hair, (ctx) -> HD(ctx.furDaima() ? "ssj4d" : "ssj4" + ctx.genderDirectory() + "/face_" + ctx.eyeType() + "/ssj4brows2.png"));
        SSJ4_FACE.add(Face, Eye, (ctx) -> HD(ctx.furDaima() ? "ssj4d" : "ssj4" + ctx.genderDirectory() + "/face_" + ctx.eyeType() + "/ssj4eyeleft.png"));
        SSJ4_FACE.add(Face, Eye, (ctx) -> HD(ctx.furDaima() ? "ssj4d" : "ssj4" + ctx.genderDirectory() + "/face_" + ctx.eyeType() + "/ssj4eyeright.png"));
    }
}
