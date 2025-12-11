package kamkeel.npcdbc.client.render;

import kamkeel.npcdbc.data.overlay.OverlayChain;

import static java.lang.String.format;
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

        OOZARU.add(ALL, BodyCM, path("oozaru/oozaru1.png", "jinryuudragonbc:cc/oozaru1.png"));
        OOZARU.add(ALL, Fur, path("oozaru/oozaru2.png", "jinryuudragonbc:cc/oozaru2.png"));

        SSJ4_FACE.add(Face, BodyCM, (ctx) -> format(HD("%s/%s/face_%s/ssj4shade.png"), ctx.furDir(), ctx.genderDir(), ctx.eyeType()));
        SSJ4_FACE.add(Face, 0xFFFFFF, (ctx) -> format(HD("%s/%s/face_%s/ssj4eyewhite.png"), ctx.furDir(), ctx.genderDir(), ctx.eyeType()));
        SSJ4_FACE.add(Face, Fur, (ctx) -> format(HD("%s/%s/face_%s/ssj4brows.png"), ctx.furDir(), ctx.genderDir(), ctx.eyeType()));
        SSJ4_FACE.add(Face, Hair, (ctx) -> format(HD("%s/%s/face_%s/ssj4brows2.png"), ctx.furDir(), ctx.genderDir(), ctx.eyeType()));
        SSJ4_FACE.add(Face, Eye, (ctx) -> format(HD("%s/%s/face_%s/ssj4eyeleft.png"), ctx.furDir(), ctx.genderDir(), ctx.eyeType()));
        SSJ4_FACE.add(Face, Eye, (ctx) -> format(HD("%s/%s/face_%s/ssj4eyeright.png"), ctx.furDir(), ctx.genderDir(), ctx.eyeType()));
    }
}
/*
    Try to use String.format for textures that have a lot of ctx calls.
    Each %s in String.Format is replaced with the following arg respectively:

        Old: HD(ctx.furDaima() ? "ssj4d" : "ssj4" + ctx.genderDirectory() + "/face_" + ctx.eyeType() + "/ssj4shade.png")
        New: format(HD("%s/%s/face_%s/ssj4shade.png"), ctx.furDaima() ? "ssj4d" : "ssj4", ctx.genderDirectory(), ctx.eyeType())));
 */
