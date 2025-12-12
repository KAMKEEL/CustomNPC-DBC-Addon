package kamkeel.npcdbc.client.render;

import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.entity.ModelBipedBody;
import kamkeel.npcdbc.data.overlay.Overlay.Type;
import kamkeel.npcdbc.data.overlay.OverlayContext;
import net.minecraft.util.MathHelper;
import noppes.npcs.constants.EnumAnimation;
import org.lwjgl.opengl.GL11;

import java.util.EnumMap;
import java.util.Map;

public final class OverlayModelRenderer {

    private static final Map<Type, RenderFunction> MODEL_MAP = new EnumMap<>(Type.class);

    public static final float SCALE = 0.0625f;
    /* ─────────────────────────────
     * HEAD
     * ───────────────────────────── */
    private static void renderHead(OverlayContext ctx) {
        GL11.glPushMatrix();
        float faceScale = 1.005f;
        GL11.glScalef(faceScale, faceScale, faceScale);

        if (ctx.isNPC) {
            ctx.mpm().renderHead(ctx.npc, SCALE);
        } else {
            float a = ctx.age();
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
        GL11.glPopMatrix();
    }

    /* ─────────────────────────────
     * ARMS
     * ───────────────────────────── */
    private static void renderArm(OverlayContext ctx, boolean right) {
        if (ctx.isNPC) {
            GL11.glPushMatrix();
            if (ctx.npc.currentAnimation == EnumAnimation.DANCING) {
                float dancing = (float) ctx.npc.ticksExisted / 4.0F;
                GL11.glTranslatef((float) Math.sin(dancing) * 0.025F, (float) Math.abs(Math.cos(dancing)) * 0.125F - 0.02F, 0.0F);
            }

            if (right)
                ctx.mpm().bipedRightArm.render(SCALE);
            else
                ctx.mpm().bipedLeftArm.render(SCALE);
            GL11.glPopMatrix();
        } else {
            float scaleXZ = ctx.invAge() * (ctx.female() ? 0.7F : 1.0F);

            GL11.glPushMatrix();
            GL11.glScalef(scaleXZ, ctx.invAge(), scaleXZ);
            GL11.glTranslatef(0.0F, (ctx.age() - 1.0F) * 1.5F, 0.0F);

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
    }

    /* ─────────────────────────────
     * LEGS
     * ───────────────────────────── */
    private static void renderLeg(OverlayContext ctx, boolean right) {
        if (ctx.isNPC) {
            if (right)
                ctx.mpm().bipedRightLeg.render(SCALE);
            else
                ctx.mpm().bipedLeftLeg.render(SCALE);
        } else {
            float scaleX = ctx.invAge() * (ctx.female() ? 0.85F : 1.0F);
            float scaleZ = ctx.invAge() * (ctx.female() ? 0.775F : 1.0F);
            float translateX = ctx.female() ? right ? -0.015F : 0.015F : 0;
            float translateY = ctx.female() ? ctx.model.isSneak ? 0 : -0.015F : 0;

            GL11.glPushMatrix();
            GL11.glScalef(scaleX, ctx.invAge(), scaleZ);
            GL11.glTranslatef(translateX, (ctx.age() - 1.0F) * 1.5F, translateY);

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
    }

    /* ─────────────────────────────
     * BODY
     * ───────────────────────────── */
    private static void renderBody(OverlayContext ctx) {
        if (ctx.isNPC) {
            ctx.mpm().renderBody(ctx.npc, SCALE);
        } else {
            if (!ctx.female())
                renderMaleBody(ctx);
            else
                renderFemaleBody(ctx);
        }
    }

    private static void renderMaleBody(OverlayContext ctx) {
        GL11.glPushMatrix();
        GL11.glScalef(ctx.invAge(), ctx.invAge(), ctx.invAge());
        GL11.glTranslatef(0.0F, (ctx.age() - 1.0F) * 1.5F, 0.0F);
        ctx.model.bipedBody.render(SCALE);
        GL11.glPopMatrix();
    }

    private static void renderFemaleBody(OverlayContext ctx) {
        ModelBipedBody model = ctx.model;

        float a = ctx.age();
        int g = ctx.gender();
        int p = ctx.pregnant();

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
     * Registry
     * ───────────────────────────── */
    static {
        /* ───────── Overlay Type -> Render Functions ───────── */
        MODEL_MAP.put(Type.Eyebrows, ctx -> renderHead(ctx));
        MODEL_MAP.put(Type.EyeWhite, ctx -> renderHead(ctx));
        MODEL_MAP.put(Type.LeftEye, ctx -> renderHead(ctx));
        MODEL_MAP.put(Type.RightEye, ctx -> renderHead(ctx));
        MODEL_MAP.put(Type.Nose, ctx -> renderHead(ctx));
        MODEL_MAP.put(Type.Mouth, ctx -> renderHead(ctx));
        MODEL_MAP.put(Type.Face, ctx -> {
            render(Type.Eyebrows, ctx);
            render(Type.EyeWhite, ctx);
            render(Type.LeftEye, ctx);
            render(Type.RightEye, ctx);
            render(Type.Nose, ctx);
            render(Type.Mouth, ctx);
        });

        MODEL_MAP.put(Type.RightArm, ctx -> renderArm(ctx, true));
        MODEL_MAP.put(Type.LeftArm, ctx -> renderArm(ctx, false));
        MODEL_MAP.put(Type.Arms, ctx -> {
            render(Type.RightArm, ctx);
            render(Type.LeftArm, ctx);

        });

        MODEL_MAP.put(Type.RightLeg, ctx -> renderLeg(ctx, true));
        MODEL_MAP.put(Type.LeftLeg, ctx -> renderLeg(ctx, false));
        MODEL_MAP.put(Type.Legs, ctx -> {
            render(Type.RightLeg, ctx);
            render(Type.LeftLeg, ctx);
        });

        MODEL_MAP.put(Type.Chest, ctx1 -> renderBody(ctx1));

        MODEL_MAP.put(Type.ALL, ctx -> {
            render(Type.Face, ctx);
            render(Type.Arms, ctx);
            render(Type.Legs, ctx);
            render(Type.Chest, ctx);
        });
    }

    public static void render(Type type, OverlayContext ctx) {
        if (ctx.typeDisabled(type))
            return;

        RenderFunction fn = MODEL_MAP.get(type);
        if (fn != null)
            fn.render(ctx);
    }

    @FunctionalInterface
    public interface RenderFunction {
        void render(OverlayContext ctx);
    }

}
