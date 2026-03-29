package kamkeel.npcdbc.data.race.races.bioandroid;
import kamkeel.npcdbc.api.client.overlay.IOverlay.ColorType;
import kamkeel.npcdbc.data.overlay.DisplayChain;
import kamkeel.npcdbc.data.overlay.DisplayChainGroup;

import static kamkeel.npcdbc.constants.BodyLayer.*;
import static kamkeel.npcdbc.api.client.overlay.IOverlay.*;

public class BioAndroidLayers {

    // ── Body Types ─────────────────────────────────────────
    
    public static final String BASE = "base", SEMI_PERFECT = "semi_perfect", PERFECT = "perfect", MAX = "max";
    
    // ════════════════════════════════════════════════════════════════
    // Display Chains
    // ════════════════════════════════════════════════════════════════

    // ── Base (Imperfect) ─────────────────────────────────────────
    
    public static final DisplayChain BASE_FACE = DisplayChain.create(BASE)
        .add(Type.EyeWhite, EYEBASE, "Eye Base")
            .texture("imperfect/face/eye_base.png")
            .colorType(ColorType.Custom).defaultColor(0xFFFFFF).fixedColor(true).and()
        .add(Type.Eyebrows, EYEBROWS, "Eyebrows")
            .texture("imperfect/face/eyebrow.png")
            .colorType(ColorType.BodyC1).and()
        .add(Type.LeftEye, EYE_LEFT, "Left Eye")
            .texture("imperfect/face/eye_left.png")
            .colorType(ColorType.Eye).and()
        .add(Type.RightEye, EYE_RIGHT, "Right Eye")
            .texture("imperfect/face/eye_right.png")
            .colorType(ColorType.Eye).and();

    
    public static final DisplayChain BASE_BODY = DisplayChain.create(BASE)
        .add(Type.ALL, BODY_CM, "Body Main")
            .texture("imperfect/bio_imperfect_0.png")
            .colorType(ColorType.BodyCM).defaultColor(0x568D32).and()
        .add(Type.ALL, BODY_C1, "Body Layer 1")
            .texture("imperfect/bio_imperfect_1.png")
            .colorType(ColorType.BodyC1).defaultColor(0xB7C913).and()
        .add(Type.ALL, BODY_C2, "Body Layer 2")
            .texture("imperfect/bio_imperfect_2.png")
            .colorType(ColorType.BodyC2).defaultColor(0xFCB054).and()
        .add(Type.ALL, BODY_C3, "Body Layer 3")
            .texture("imperfect/bio_imperfect_3.png")
            .colorType(ColorType.BodyC3).defaultColor(0x909CC4).and()
        .add(Type.ALL, BODY_C4, "Body Layer 4")
            .texture("imperfect/bio_imperfect_4.png")
            .colorType(ColorType.Custom).defaultColor(0xFFFFFF).fixedColor(true).and()
       // ── Custom geometry - derives IRaceModelComponent ──────────────────────────────────────────────────
        .add(Type.ALL, "crest", "Head Crest")
            .texture("imperfect/bio_imperfect_crest.png")
            .colorType(ColorType.BodyCM).componentKey("npcdbc:bio_crest").and()
        .add(Type.ALL, "tail_static", "Tail Stinger")
            .texture("imperfect/bio_imperfect_stinger.png")
            .colorType(ColorType.Custom).defaultColor(0xFFFFFF).fixedColor(true)
            .componentKey("npcdbc:bio_tail").and()
        .add(Type.ALL, "tail", "Tail Layer 0")
            .texture("imperfect/bio_imperfect_tail_0.png")
            .colorType(ColorType.BodyCM)
            .componentKey("npcdbc:bio_tail").and()
        .add(Type.ALL, "tail_1", "Tail Layer 1")
            .texture("imperfect/bio_imperfect_tail_1.png")
            .colorType(ColorType.Custom).defaultColor(0xFFFFFF).fixedColor(true)
            .componentKey("npcdbc:bio_tail").slotId("tail").and()
        .add(Type.ALL, "wings", "Wings")
            .texture("imperfect/bio_imperfect_wings.png")
            .colorType(ColorType.BodyCM)
            .componentKey("npcdbc:bio_wings").and();

    public static final DisplayChainGroup BASE_GROUP = DisplayChainGroup.of(BASE_BODY, BASE_FACE);

    // ── Semi-Perfect ─────────────────────────────────────────────

        public static final DisplayChain SEMI_PERFECT_FACE = DisplayChain.create(SEMI_PERFECT)
        .add(Type.EyeWhite, EYEBASE, "Eye Base")
            .texture("semiperfect/face/eye_base.png")
            .colorType(ColorType.Custom).defaultColor(0xFFFFFF).fixedColor(true).and()
        .add(Type.Eyebrows, EYEBROWS, "Eyebrows")
            .texture("semiperfect/face/eyebrow.png")
            .colorType(ColorType.BodyC1).and()
        .add(Type.LeftEye, EYE_LEFT, "Left Eye")
            .texture("semiperfect/face/eye_left.png")
            .colorType(ColorType.Custom).defaultColor(0xDFEEEE).fixedColor(true).and()
        .add(Type.RightEye, EYE_RIGHT, "Right Eye")
            .texture("semiperfect/face/eye_right.png")
            .colorType(ColorType.Custom).defaultColor(0xDFEEEE).fixedColor(true).and()
        .add(Type.Nose, NOSE, "Nose")
            .texture("semiperfect/face/nose.png")
            .colorType(ColorType.BodyC1).and()
        .add(Type.Mouth, MOUTH, "Mouth")
            .texture("semiperfect/face/mouth.png")
            .colorType(ColorType.Custom).defaultColor(0xd7a4bb).fixedColor(true).and();

        
    public static final DisplayChain SEMI_PERFECT_BODY = DisplayChain.create(SEMI_PERFECT)
        .add(Type.ALL, BODY_CM, "Body Main")
            .texture("semiperfect/bio_semiperfect_0.png")
            .colorType(ColorType.BodyCM).defaultColor(0x568D32).and()
        .add(Type.ALL, BODY_C1, "Body Layer 1")
            .texture("semiperfect/bio_semiperfect_1.png")
            .colorType(ColorType.BodyC1).defaultColor(0xB7C913).and()
        .add(Type.ALL, BODY_C2, "Body Layer 2")
            .texture("semiperfect/bio_semiperfect_2.png")
            .colorType(ColorType.BodyC2).defaultColor(0xFCB054).and()
        .add(Type.ALL, BODY_C3, "Body Layer 3")
            .texture("semiperfect/bio_semiperfect_3.png")
            .colorType(ColorType.BodyC3).defaultColor(0x909CC4).and()
        .add(Type.ALL, BODY_C4, "Body Layer 4")
            .texture("semiperfect/bio_semiperfect_4.png")
            .colorType(ColorType.Custom).defaultColor(0xFFFFFF).fixedColor(true).and() 
       // ── Custom geometry - derives IRaceModelComponent ──────────────────────────────────────────────────
        .add(Type.ALL, "crest", "Head Crest")
            .texture("semiperfect/bio_semiperfect_crest.png")
            .colorType(ColorType.BodyCM).componentKey("npcdbc:bio_crest").and()
        .add(Type.ALL, "tail_static", "Tail Stinger")
            .texture("semiperfect/bio_semiperfect_stinger.png")
            .colorType(ColorType.Custom).defaultColor(0xFFFFFF).fixedColor(true)
            .componentKey("npcdbc:bio_tail").and()
        .add(Type.ALL, "tail", "Tail Layer 0")
            .texture("semiperfect/bio_semiperfect_tail_0.png")
            .colorType(ColorType.BodyC2)
            .componentKey("npcdbc:bio_tail").and()
        .add(Type.ALL, "tail_1", "Tail Layer 1")
            .texture("semiperfect/bio_semiperfect_tail_1.png")
            .colorType(ColorType.Custom).defaultColor(0xFFFFFF).fixedColor(true)
            .componentKey("npcdbc:bio_tail").slotId("tail").and();

    public static final DisplayChainGroup SEMI_PERFECT_GROUP = DisplayChainGroup.of(SEMI_PERFECT_BODY, SEMI_PERFECT_FACE);

    // ── Perfect ──────────────────────────────────────────────────

        public static final DisplayChain PERFECT_FACE = DisplayChain.create(PERFECT)
        .add(Type.EyeWhite, EYEBASE, "Eye Base")
            .texture("perfect/face/eye_base.png")
            .colorType(ColorType.Custom).defaultColor(0xFFFFFF).fixedColor(true).and()
        .add(Type.Eyebrows, EYEBROWS, "Eyebrows")
            .texture("perfect/face/eyebrow.png")
            .colorType(ColorType.BodyC1).and()
        .add(Type.LeftEye, EYE_LEFT, "Left Eye")
            .texture("perfect/face/eye_left.png")
            .colorType(ColorType.Eye).defaultColor(0x9d0707).and()
        .add(Type.RightEye, EYE_RIGHT, "Right Eye")
            .texture("perfect/face/eye_right.png")
            .colorType(ColorType.Eye).defaultColor(0x9d0707).and()
        .add(Type.Face, NOSE, "Nose")
            .texture("perfect/face/nose.png")
            .colorType(ColorType.BodyC1).and()
        .add(Type.Mouth, MOUTH, "Mouth")
            .texture("perfect/face/mouth.png")
            .colorType(ColorType.BodyC1).and();
        
        
    public static final DisplayChain PERFECT_BODY = DisplayChain.create(PERFECT)
        .add(Type.ALL, BODY_CM, "Body Main")
            .texture("perfect/bio_perfect_0.png")
            .colorType(ColorType.BodyCM).defaultColor(0xd5dbd9).and()
        .add(Type.ALL, BODY_C1, "Body Layer 1")
            .texture("perfect/bio_perfect_1.png")
            .colorType(ColorType.BodyC1).defaultColor(0xd5dbd9).and()
        .add(Type.ALL, BODY_C2, "Body Layer 2")
            .texture("perfect/bio_perfect_2.png")
            .colorType(ColorType.BodyC2).defaultColor(0xd7c827).and()
        .add(Type.ALL, BODY_C3, "Body Layer 3")
            .texture("perfect/bio_perfect_3.png")
            .colorType(ColorType.BodyC3).defaultColor(0x6c2f7c).and()
        .add(Type.ALL, BODY_C4, "Body Layer 4")
            .texture("perfect/bio_perfect_4.png")
            .colorType(ColorType.Custom).defaultColor(0xFFFFFF).fixedColor(true).and()
       // ── Custom geometry - derives IRaceModelComponent ──────────────────────────────────────────────────
        .add(Type.ALL, "crest", "Head Crest")
            .texture("perfect/bio_perfect_crest.png")
            .colorType(ColorType.BodyCM).componentKey("npcdbc:bio_crest").and()
        .add(Type.ALL, "wings", "Wings")
            .texture("perfect/bio_perfect_wings.png")
            .colorType(ColorType.Custom).defaultColor(0xFFFFFF).fixedColor(true)
            .componentKey("npcdbc:bio_wings").and();

    public static final DisplayChainGroup PERFECT_GROUP = DisplayChainGroup.of(PERFECT_BODY, PERFECT_FACE);

    // ── Max ──────────────────────────────────────────────────────
    
    public static final DisplayChain MAX_FACE = DisplayChain.create(MAX)
        .add(Type.EyeWhite, EYEBASE, "Eye Base")
            .texture("max/face/eye_base.png")
            .colorType(ColorType.Custom).defaultColor(0xFFFFFF).fixedColor(true).and()
        .add(Type.Eyebrows, EYEBROWS, "Eyebrows")
            .texture("max/face/eyebrow.png")
            .colorType(ColorType.BodyC1).and()
        .add(Type.LeftEye, EYE_LEFT, "Left Eye")
            .texture("max/face/eye_left.png")
            .colorType(ColorType.Eye).and()
        .add(Type.RightEye, EYE_RIGHT, "Right Eye")
            .texture("max/face/eye_right.png")
            .colorType(ColorType.Eye).and()
        .add(Type.Nose, NOSE, "Nose")
            .texture("max/face/nose.png")
            .colorType(ColorType.Custom).defaultColor(0xFFFFFF).fixedColor(true).and()
        .add(Type.Mouth, MOUTH, "Mouth")
            .texture("max/face/mouth.png")
            .colorType(ColorType.Custom).defaultColor(0xFFE0FA).fixedColor(true).and();
    
    
    public static final DisplayChain MAX_BODY = DisplayChain.create(MAX)
        .add(Type.ALL, BODY_CM, "Body Main")
            .texture("max/bio_max_0.png")
            .colorType(ColorType.BodyCM).defaultColor(0xc22023).and()
        .add(Type.ALL, BODY_C1, "Body Layer 1")
            .texture("max/bio_max_1.png")
            .colorType(ColorType.BodyC1).defaultColor(0x9abe35).and()
        .add(Type.ALL, BODY_C2, "Body Layer 2")
            .texture("max/bio_max_2.png")
            .colorType(ColorType.BodyC2).defaultColor(0xc22023).and()
        .add(Type.ALL, BODY_C3, "Body Layer 3")
            .texture("max/bio_max_3.png")
            .colorType(ColorType.BodyC3).defaultColor(0x701b58).and()
        .add(Type.ALL, BODY_C4, "Body Layer 4")
            .texture("max/bio_max_4.png")
            .colorType(ColorType.Custom).defaultColor(0xFFFFFF).fixedColor(true).and()
       // ── Custom geometry - derives IRaceModelComponent ──────────────────────────────────────────────────
        .add(Type.ALL, "crest", "Head Crest")
            .texture("max/bio_max_crest.png")
            .colorType(ColorType.BodyCM).componentKey("npcdbc:bio_crest").and()
        .add(Type.ALL, "tail_static", "Tail Stinger")
            .texture("max/bio_max_stinger.png")
            .colorType(ColorType.Custom).defaultColor(0xFFFFFF).fixedColor(true)
            .componentKey("npcdbc:bio_tail_max").and()
        .add(Type.ALL, "tail", "Tail Layer 0")
            .texture("max/bio_max_tail_0.png")
            .colorType(ColorType.BodyCM)
            .componentKey("npcdbc:bio_tail_max").and()
        .add(Type.ALL, "tail_1", "Tail Layer 1")
            .texture("max/bio_max_tail_1.png")
            .colorType(ColorType.BodyC1)
            .componentKey("npcdbc:bio_tail_max").slotId("tail").and()
        .add(Type.ALL, "wings", "Wings")
            .texture("max/bio_max_wings.png")
            .colorType(ColorType.Custom).defaultColor(0xFFFFFF).fixedColor(true)
            .componentKey("npcdbc:bio_wings").and();

    public static final DisplayChainGroup MAX_GROUP = DisplayChainGroup.of(MAX_BODY, MAX_FACE);
    
}
