package kamkeel.npcdbc.data.race.races.android;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.api.client.overlay.IOverlay.ColorType;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.data.overlay.DisplayChain;
import kamkeel.npcdbc.data.overlay.DisplayChainGroup;

import static kamkeel.npcdbc.api.client.overlay.IOverlay.Type;
import static kamkeel.npcdbc.constants.BodyLayer.*;

public class AndroidLayers {

    // ── Body Types ─────────────────────────────────────────

    public static final String BASE = "base";

    // ════════════════════════════════════════════════════════════════
    // Display Chains
    // ════════════════════════════════════════════════════════════════

    // ── Base (Imperfect) ─────────────────────────────────────────

    public static final DisplayChain BASE_FACE = DisplayChain.create(BASE)
        .textureDir(() ->
            CustomNpcPlusDBC.ID + ":textures/" +
                (ConfigDBCClient.EnableHDTextures ? "hd" : "sd") + "/android/")
        .add(Type.EyeWhite, EYEBASE, "Eye Base")
            .texture("base/face/eye_base_%e_%g.png")
            .colorType(ColorType.Custom).defaultColor(0xFFFFFF).fixedColor(true).and()
        .add(Type.Eyebrows, EYEBROWS, "Eyebrows")
            .texture("base/face/eyebrow_%e_%g.png")
            .colorType(ColorType.Hair).and()
        .add(Type.Eyebrows, "eyeshade", "Eye Shade")
            .texture("base/face/eye_shade_%e_%g.png")
            .colorType(ColorType.BodyCM).defaultColor(0x0).and()
        .add(Type.LeftEye, EYE_LEFT, "Left Eye")
            .texture("base/face/eye_left_%e_%g.png")
            .colorType(ColorType.Eye).defaultColor(0x84c8e9).and()
        .add(Type.RightEye, EYE_RIGHT, "Right Eye")
            .texture("base/face/eye_right_%e_%g.png")
            .colorType(ColorType.Eye).defaultColor(0x84c8e9).and()
        .add(Type.Nose, NOSE, "Nose")
            .texture("base/face/nose_%n_%g.png")
            .colorType(ColorType.BodyCM).and()
        .add(Type.Mouth, MOUTH, "Mouth")
            .texture("base/face/mouth_%m_%g.png")
            .colorType(ColorType.BodyCM).and();


    public static final DisplayChain BASE_BODY = DisplayChain.create(BASE)
        .textureDir(() ->
            CustomNpcPlusDBC.ID + ":textures/" +
                (ConfigDBCClient.EnableHDTextures ? "hd" : "sd") + "/android/")
        .add(Type.ALL, BODY_CM, "Body Main")
            .texture("base/android_0_%g.png")
            .colorType(ColorType.BodyCM).defaultColor(0xecd2b4)
        .and();

    public static final DisplayChainGroup BASE_GROUP = DisplayChainGroup.of(BASE_BODY, BASE_FACE);
}
