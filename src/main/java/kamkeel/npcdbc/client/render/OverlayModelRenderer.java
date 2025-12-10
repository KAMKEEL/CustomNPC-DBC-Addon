package kamkeel.npcdbc.client.render;

import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.entity.ModelBipedBody;
import kamkeel.npcdbc.data.overlay.Overlay;
import kamkeel.npcdbc.data.overlay.Overlay.Type;
import net.minecraft.util.MathHelper;
import noppes.npcs.client.model.ModelMPM;
import org.lwjgl.opengl.GL11;

import java.util.EnumMap;
import java.util.Map;

public final class OverlayModelRenderer {

    public static final float SCALE = 0.0625f;

    private static final Map<Type, RenderFunction> PLAYER_MAP = new EnumMap<>(Type.class);
    private static final Map<Type, RenderFunctionMPM> NPC_MAP = new EnumMap<>(Type.class);

    /* ─────────────────────────────
     * HEAD
     * ───────────────────────────── */
    private static void renderHead(Context ctx) {
        float a = ctx.age;
        float scaleXZ = (0.5F + 0.5F / a) * (ctx.female() ? 0.85F : 1.0F);

        float adjust;
        if (a >= 1.5F && a <= 2.0F) {
            adjust = (2.0F - a) / 2.5F;
        } else if (a < 1.5F && a >= 1.0F) {
            adjust = (a * 2.0F - 2.0F) * 0.2F;
        } else {
            adjust = 0.0F;
        }
        float translateY = (a - 1.0F) / a * (2.0F - adjust);

        GL11.glPushMatrix();
        GL11.glScalef(scaleXZ, 0.5F + 0.5F / a, scaleXZ);
        GL11.glTranslatef(0.0F, translateY, 0.0F);
        ctx.model.bipedHead.render(SCALE);
        GL11.glPopMatrix();
    }

    /* ─────────────────────────────
     * ARMS
     * ───────────────────────────── */
    private static void renderArm(Context ctx, boolean right) {
        float scaleXZ = ctx.invAge() * (ctx.female() ? 0.7F : 1.0F);

        GL11.glPushMatrix();
        GL11.glScalef(scaleXZ, ctx.invAge(), scaleXZ);
        GL11.glTranslatef(0.0F, (ctx.age - 1.0F) * 1.5F, 0.0F);

        if (ctx.female()) {
            if (right)
                ctx.model.Brightarm.render(SCALE);
            else
                ctx.model.Bleftarm.render(SCALE);
        } else {
            if (right)
                ctx.model.bipedRightArm.render(SCALE);
            else
                ctx.model.bipedLeftArm.render(SCALE);
        }

        GL11.glPopMatrix();
    }

    /* ─────────────────────────────
     * LEGS
     * ───────────────────────────── */
    private static void renderLeg(Context ctx, boolean right) {
        float scaleX = ctx.invAge() * (ctx.female() ? 0.85F : 1.0F);
        float scaleZ = ctx.invAge() * (ctx.female() ? 0.775F : 1.0F);
        float translateX = ctx.female() ? right ? -0.015F : 0.015F : 0;
        float translateY = ctx.female() ? ctx.model.isSneak ? 0 : -0.015F : 0;

        GL11.glPushMatrix();
        GL11.glScalef(scaleX, ctx.invAge(), scaleZ);
        GL11.glTranslatef(translateX, (ctx.age - 1.0F) * 1.5F, translateY);

        if (ctx.female()) {
            if (right)
                ctx.model.rightleg.render(SCALE);
            else
                ctx.model.leftleg.render(SCALE);
        } else {
            if (right)
                ctx.model.bipedRightLeg.render(SCALE);
            else
                ctx.model.bipedLeftLeg.render(SCALE);
        }

        GL11.glPopMatrix();
    }

    /* ─────────────────────────────
     * BODY
     * ───────────────────────────── */
    private static void renderBody(Context ctx) {
        if (!ctx.female())
            renderMaleBody(ctx);
        else
            renderFemaleBody(ctx);
    }

    private static void renderMaleBody(Context ctx) {
        GL11.glPushMatrix();
        GL11.glScalef(ctx.invAge(), ctx.invAge(), ctx.invAge());
        GL11.glTranslatef(0.0F, (ctx.age - 1.0F) * 1.5F, 0.0F);
        ctx.model.bipedBody.render(SCALE);
        GL11.glPopMatrix();
    }

    private static void renderFemaleBody(Context ctx) {
        ModelBipedBody model = ctx.model;

        float a = ctx.age;
        int g = ctx.gender;
        int p = ctx.pregnant;

        float invA = 1.0F / a;
        boolean female = g > 1;

        /* ───────── Breast offsets / physics ───────── */
        String[] s = JRMCoreH.data(model.Entity.getCommandSenderName(), 1, "0;0;0;0;0;0;0;0;0").split(";");

        int b = JRMCoreH.dnsBreast(s[1]);
        float scale = b * 0.03F;

        float br = 0.4235988F + scale;
        float bs = 0.8F + scale;
        float bsY = 0.85F + scale * 0.5F;
        float bt = 0.1F * scale;

        boolean bounce = model.Entity.onGround || model.Entity.isInWater();
        float speed = model.Entity.isSprinting() ? 1.5F : model.Entity.isSneaking() ? 0.5F : 1.0F;

        float bbY = bounce ? MathHelper.sin(model.rot1 * 0.6662F * speed * 1.5F + (float) Math.PI) * model.rot2 * 0.03F * b * 0.1119F : 0.0F;

        /* ───────── Breast rotations ───────── */
        model.breast.rotateAngleX = -br;
        model.breast.rotateAngleY = model.breast.rotateAngleZ = 0;

        model.breast2.rotateAngleX = br;
        model.breast2.rotateAngleY = 3.141593F;
        model.breast2.rotateAngleZ = 0;

        if (bounce) {
            float wave = MathHelper.cos(model.rot1 * 0.6662F * speed + (float) Math.PI) * model.rot2 * 0.05F * b * 0.1119F;

            model.breast.rotateAngleX += -wave;
            model.breast.rotateAngleY += wave * 0.4F;

            model.breast2.rotateAngleX += wave;
            model.breast2.rotateAngleY += wave * 0.4F;
        }

        /* ───────── Breast Up ───────── */
        GL11.glPushMatrix();
        GL11.glScalef(invA * (female ? 0.675F : 1.0F), invA, invA * (female ? 0.8F : 1.0F));
        GL11.glTranslatef(0.0F, (a - 1.0F) * 1.5F + bbY, 0.015F + bt);
        GL11.glScalef(1.0F, bsY, bs);
        model.Bbreast.render(SCALE);
        GL11.glPopMatrix();

        /* ───────── Breast Down ───────── */
        GL11.glPushMatrix();
        GL11.glScalef(invA * (female ? 0.675F : 1.0F) - 0.001F, invA, invA * (female ? 0.8F : 1.0F) - 0.001F);
        GL11.glTranslatef(0.0F, (a - 1.0F) * 1.5F + 0.001F + bbY, 0.015F + bt);
        GL11.glScalef(1.0F, bsY, bs);
        model.Bbreast2.render(SCALE);
        GL11.glPopMatrix();

        /* ───────── Core body ───────── */
        GL11.glPushMatrix();
        GL11.glScalef(invA * (female ? 0.7F : 1.0F), invA, invA * (female ? 0.7F : 1.0F));
        GL11.glTranslatef(0.0F, (a - 1.0F) * 1.5F, 0.0F);
        model.body.render(SCALE);
        GL11.glPopMatrix();

        /* ───────── Waist ───────── */
        GL11.glPushMatrix();
        GL11.glScalef(invA * (female ? 0.65F : 1.0F), invA, invA * (female ? 0.65F : 1.0F) * (1.0F + 0.001F * p));
        GL11.glTranslatef(0.0F, (a - 1.0F) * 1.5F, model.isSneak ? 0.0F : -0.04F - 1.0E-4F * p);
        model.waist.render(SCALE);
        GL11.glPopMatrix();

        /* ───────── Hip ───────── */
        GL11.glPushMatrix();
        GL11.glScalef(invA * (female ? 0.75F : 1.0F), invA, invA * (female ? 0.75F : 1.0F) * (1.0F + 0.005F * p));
        GL11.glTranslatef(0.0F, (a - 1.0F) * 1.5F, model.isSneak ? 0.0F : -0.02F - 5.0E-4F * p);
        model.hip.render(SCALE);
        GL11.glPopMatrix();

        /* ───────── Bottom ───────── */
        GL11.glPushMatrix();
        GL11.glScalef(invA * (female ? 0.85F : 1.0F), invA, invA * (female ? 0.85F : 1.0F) * (1.0F + 0.005F * p));
        GL11.glTranslatef(0.0F, (a - 1.0F) * 1.5F, -5.0E-4F * p);
        model.bottom.render(SCALE);
        GL11.glPopMatrix();
    }

    /* ─────────────────────────────
     * Player Context
     * ───────────────────────────── */
    public static final class Context {
        public final ModelBipedBody model;
        public final int gender;
        public final float age;
        public final int pregnant;

        public Context(ModelBipedBody model, int gender, float age, int pregnant) {
            this.model = model;
            this.gender = gender;
            this.age = age;
            this.pregnant = pregnant;
        }

        public boolean female() {
            return gender > 1;
        }

        public float invAge() {
            return 1.0F / age;
        }
    }

    /* ─────────────────────────────
     * Registry
     * ───────────────────────────── */
    static {
        /* ───────── Player Functions ───────── */
        PLAYER_MAP.put(Overlay.Type.Face, OverlayModelRenderer::renderHead);

        PLAYER_MAP.put(Overlay.Type.RightArm, ctx -> renderArm(ctx, true));
        PLAYER_MAP.put(Overlay.Type.LeftArm, ctx -> renderArm(ctx, false));
        PLAYER_MAP.put(Overlay.Type.Arms, ctx -> {
            renderArm(ctx, true);
            renderArm(ctx, false);
        });

        PLAYER_MAP.put(Overlay.Type.RightLeg, ctx -> renderLeg(ctx, true));
        PLAYER_MAP.put(Overlay.Type.LeftLeg, ctx -> renderLeg(ctx, false));
        PLAYER_MAP.put(Overlay.Type.Legs, ctx -> {
            renderLeg(ctx, true);
            renderLeg(ctx, false);
        });

        PLAYER_MAP.put(Overlay.Type.Chest, OverlayModelRenderer::renderBody);

        PLAYER_MAP.put(Overlay.Type.ALL, ctx -> {
            render(Overlay.Type.Face, ctx);
            render(Overlay.Type.Arms, ctx);
            render(Overlay.Type.Chest, ctx);
            render(Overlay.Type.Legs, ctx);
        });


        /* ───────── NPC Functions ───────── */
        NPC_MAP.put(Overlay.Type.Face, (m) -> m.renderHead(m.npc, SCALE));

        NPC_MAP.put(Overlay.Type.RightArm, (m) -> m.bipedRightArm.render(SCALE));
        NPC_MAP.put(Overlay.Type.LeftArm, (m) -> m.bipedLeftArm.render(SCALE));
        NPC_MAP.put(Overlay.Type.Arms, (m) -> m.renderArms(m.npc, SCALE, false));

        NPC_MAP.put(Overlay.Type.RightLeg, (m) -> m.legs.leg1.render(SCALE));
        NPC_MAP.put(Overlay.Type.LeftLeg, (m) -> m.legs.leg2.render(SCALE));
        NPC_MAP.put(Overlay.Type.Legs, (m) -> m.renderLegs(m.npc, SCALE));

        NPC_MAP.put(Overlay.Type.Chest, (m) -> m.renderBody(m.npc, SCALE));

        NPC_MAP.put(Overlay.Type.ALL, (m) -> {
            render(Overlay.Type.Face, m);
            render(Overlay.Type.Arms, m);
            render(Overlay.Type.Legs, m);
            render(Overlay.Type.Chest, m);
        });
    }

    public static void render(Type type, Context ctx) {
        RenderFunction fn = PLAYER_MAP.get(type);
        if (fn != null)
            fn.render(ctx);
    }

    public static void render(Type type, ModelMPM model) {
        RenderFunctionMPM fn = NPC_MAP.get(type);
        if (fn != null)
            fn.render(model);
    }

    @FunctionalInterface
    public interface RenderFunction {
        void render(Context ctx);
    }

    @FunctionalInterface
    public interface RenderFunctionMPM {
        void render(ModelMPM model);
    }
}
