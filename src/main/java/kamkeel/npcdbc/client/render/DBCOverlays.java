package kamkeel.npcdbc.client.render;

import kamkeel.npcdbc.data.form.FacePartData.Part;
import kamkeel.npcdbc.data.overlay.OverlayChain;
import kamkeel.npcdbc.data.overlay.OverlayContext;

import static java.lang.String.format;
import static kamkeel.npcdbc.client.model.ModelDBC.*;
import static kamkeel.npcdbc.data.overlay.Overlay.ColorType.*;

import static kamkeel.npcdbc.data.overlay.Overlay.Type.*;

public class DBCOverlays {

    public static final OverlayChain SSJ4_FUR = OverlayChain.create("SSJ4 Fur");
    public static final OverlayChain OOZARU_FUR = OverlayChain.create("Oozaru Fur");

    public static final OverlayChain SSJ3_FACE = OverlayChain.create("SSJ3 Face");
    public static final OverlayChain SSJ4_FACE = OverlayChain.create("SSJ4 Face");

    public static final OverlayChain PUPILS = OverlayChain.create("Pupils");

    static {
        SSJ4_FUR.add(ALL, Fur, ctx -> path("ssj4/ss4b" + ctx.furType() + ".png", "jinryuudragonbc:cc/ss4b"));


        OOZARU_FUR.add(ALL, BodyCM, path("oozaru/oozaru1.png", "jinryuudragonbc:cc/oozaru1.png"));
        OOZARU_FUR.add(ALL, Fur, path("oozaru/oozaru2.png", "jinryuudragonbc:cc/oozaru2.png"));


        TexturePath SSJ4_PATH = (ctx, path) -> HDDir + format("%s/%s/face_%s/%s.png", ctx.furDir(), ctx.genderDir(), ctx.eyeType(), path);
        SSJ4_FACE.add(Face, BodyCM, ctx -> SSJ4_PATH.get(ctx, "ssj4shade"));
        SSJ4_FACE.add(EyeWhite, 0xFFFFFF, ctx -> SSJ4_PATH.get(ctx, "ssj4eyewhite"));
        SSJ4_FACE.add(Eyebrows, Fur, ctx -> SSJ4_PATH.get(ctx, "ssj4brows"));
        SSJ4_FACE.add(Eyebrows, Hair, ctx -> SSJ4_PATH.get(ctx, "ssj4brows2"));
        SSJ4_FACE.add(LeftEye, Eye, ctx -> SSJ4_PATH.get(ctx, "ssj4eyeleft"));
        SSJ4_FACE.add(RightEye, Eye, ctx -> SSJ4_PATH.get(ctx, "ssj4eyeright"));
        SSJ4_FACE.add(LeftEye, 0xFFFFFF, ctx -> SSJ4_PATH.get(ctx, "ssj4glowleft")).condition(OverlayContext::furDaima);
        SSJ4_FACE.add(RightEye, 0xFFFFFF, ctx -> SSJ4_PATH.get(ctx, "ssj4glowright")).condition(OverlayContext::furDaima);
        SSJ4_FACE.disable(Part.Eyebrows, Part.EyeWhite, Part.LeftEye, Part.RightEye);


        TexturePath SSJ3_PATH = (ctx, path) -> HDDir + format("ssj3/%s/face_%s/%s.png", ctx.genderDir(), ctx.eyeType(), path);
        SSJ3_FACE.add(Face, BodyCM, ctx -> SSJ3_PATH.get(ctx, "ssj3shade"));
        SSJ3_FACE.add(EyeWhite, 0xFFFFFF, ctx -> SSJ3_PATH.get(ctx, "ssj3eyewhite"));
        SSJ3_FACE.add(Eyebrows, Fur, ctx -> SSJ3_PATH.get(ctx, "ssj3brows"));
        SSJ3_FACE.add(LeftEye, Eye, ctx -> SSJ3_PATH.get(ctx, "ssj3eyeleft"));
        SSJ3_FACE.add(RightEye, Eye, ctx -> SSJ3_PATH.get(ctx, "ssj3eyeright"));
        SSJ3_FACE.disable(Part.Eyebrows, Part.EyeWhite, Part.LeftEye, Part.RightEye);


        TexturePath PUPILS_PATH = (ctx, path) -> OVERLAY_DIR + format("eyespupils/%s_%s_%s.png", path, ctx.eyeType(), ctx.female() ? "f" : "m");
        PUPILS.add(LeftEye, Eye, ctx -> PUPILS_PATH.get(ctx, "left/left_eye"));
        PUPILS.add(RightEye, Eye, ctx -> PUPILS_PATH.get(ctx, "right/right_eye"));
        PUPILS.disable(Part.LeftEye, Part.RightEye);
    }

    public interface TexturePath {
        String get(OverlayContext ctx, String path);
    }
}
/*
    Try to use String.format for textures that have a lot of ctx calls.
    Each %s in String.Format is replaced with the following arg respectively:

        Old: HD(ctx.furDaima() ? "ssj4d" : "ssj4" + ctx.genderDirectory() + "/face_" + ctx.eyeType() + "/ssj4shade.png")
        New: format(HD("%s/%s/face_%s/ssj4shade.png"), ctx.furDaima() ? "ssj4d" : "ssj4", ctx.genderDirectory(), ctx.eyeType())));
 */
