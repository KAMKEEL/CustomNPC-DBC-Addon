package kamkeel.npcdbc.client.render;

import JinRyuu.JBRA.ModelBipedDBC;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.entity.ModelBipedBody;
import kamkeel.npcdbc.data.form.OverlayManager;
import kamkeel.npcdbc.data.form.OverlayManager.Type;
import net.minecraft.util.MathHelper;
import noppes.npcs.client.model.ModelMPM;
import org.lwjgl.opengl.GL11;

import java.util.EnumMap;
import java.util.Map;

public class OverlayModelRenderer {
    private static final Map<OverlayManager.Type, RenderFunction> PLAYER_MAP = new EnumMap<>(Type.class);
    private static final Map<OverlayManager.Type, RenderFunctionMPM> NPC_MAP = new EnumMap<>(Type.class);

    public static ModelBipedBody model;
    public static float par = 0.0625f;

    public static RenderFunction HEAD = (g, a, p) -> {
        float genderRatio = g <= 1 ? 1.0F : 0.85F;
        float scale = 0.5F + 0.5F / a;
        GL11.glPushMatrix();
        GL11.glScalef(scale * genderRatio, scale, scale * genderRatio);
        GL11.glTranslatef(0.0F, (a - 1.0F) / a * (2.0F - (a >= 1.5F && a <= 2.0F ? (2.0F - a) / 2.5F : (a < 1.5F && a >= 1.0F ? (a * 2.0F - 2.0F) * 0.2F : 0.0F))), 0.0F);
        model.bipedHead.render(par);
        GL11.glPopMatrix();
    };

    public static RenderFunction RIGHT_ARM = (g, a, p) -> {
        GL11.glPushMatrix();
        GL11.glScalef(1.0F / a * (g <= 1 ? 1.0F : 0.7F), 1.0F / a, 1.0F / a * (g <= 1 ? 1.0F : 0.7F));
        GL11.glTranslatef(0.0F, (a - 1.0F) * 1.5F, 0.0F);
        if (g <= 1)
            model.bipedRightArm.render(par);
        else
            model.Brightarm.render(par);
        GL11.glPopMatrix();
    };

    public static RenderFunction LEFT_ARM = (g, a, p) -> {
        GL11.glPushMatrix();
        GL11.glScalef(1.0F / a * (g <= 1 ? 1.0F : 0.7F), 1.0F / a, 1.0F / a * (g <= 1 ? 1.0F : 0.7F));
        GL11.glTranslatef(0.0F, (a - 1.0F) * 1.5F, 0.0F);
        if (g <= 1)
            model.bipedLeftArm.render(par);
        else
            model.Bleftarm.render(par);
        GL11.glPopMatrix();
    };

    public static RenderFunction ARMS = (g, a, p) -> {
        RIGHT_ARM.render(g, a, p);
        LEFT_ARM.render(g, a, p);
    };

    public static RenderFunction RIGHT_LEG = (g, a, p) -> {
        GL11.glPushMatrix();
        GL11.glScalef(1.0F / a * (g <= 1 ? 1.0F : 0.85F), 1.0F / a, 1.0F / a * (g <= 1 ? 1.0F : 0.775F));
        GL11.glTranslatef(g <= 1 ? 0.0F : -0.015F, (a - 1.0F) * 1.5F, g <= 1 ? 0 : model.isSneak ? -0.0F : -0.015F);

        if (g <= 1)
            model.bipedRightLeg.render(par);
        else
            model.rightleg.render(par);
        GL11.glPopMatrix();
    };

    public static RenderFunction LEFT_LEG = (g, a, p) -> {
        GL11.glPushMatrix();
        GL11.glScalef(1.0F / a * (g <= 1 ? 1.0F : 0.85F), 1.0F / a, 1.0F / a * (g <= 1 ? 1.0F : 0.775F));
        GL11.glTranslatef(g <= 1 ? 0.0F : 0.015f, (a - 1.0F) * 1.5F, g <= 1 ? 0 : model.isSneak ? -0.0F : -0.015F);

        if (g <= 1)
            model.bipedLeftLeg.render(par);
        else
            model.leftleg.render(par);

        GL11.glPopMatrix();
    };

    public static RenderFunction LEGS = (g, a, p) -> {
        RIGHT_LEG.render(g, a, p);
        LEFT_LEG.render(g, a, p);
    };
    public static RenderFunction BODY = (g, a, p) -> {
        if (g <= 1) {
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / a, 1.0F / a, 1.0F / a);
            GL11.glTranslatef(0.0F, (a - 1.0F) * 1.5F, 0.0F);
            model.bipedBody.render(par);
            GL11.glPopMatrix();
            return;
        }

        String[] s = JRMCoreH.data(model.Entity.getCommandSenderName(), 1, "0;0;0;0;0;0;0;0;0").split(";");
        String dns = s[1];
        int b = JRMCoreH.dnsBreast(dns);
        float scale = (float) b * 0.03F;
        float br = 0.4235988F + scale;
        float bs = 0.8F + scale;
        float bsY = 0.85F + scale * 0.5F;
        float bt = 0.1F * scale;
        boolean bounce = model.Entity.onGround || model.Entity.isInWater();
        float bspeed = model.Entity.isSprinting() ? 1.5F : (model.Entity.isSneaking() ? 0.5F : 1.0F);
        float bbY = (bounce ? MathHelper.sin(model.rot1 * 0.6662F * bspeed * 1.5F + (float) Math.PI) * model.rot2 * 0.03F : 0.0F) * (float) b * 0.1119F;

        model.breast.rotateAngleX = -br;
        model.breast.rotateAngleY = model.breast.rotateAngleZ = 0;

        model.breast2.rotateAngleX = br;
        model.breast2.rotateAngleY = 3.141593F;
        model.breast2.rotateAngleZ = 0;

        if (bounce) {
            model.breast.rotateAngleX += -MathHelper.cos(model.rot1 * 0.6662F * bspeed + (float) Math.PI) * model.rot2 * 0.05F * (float) b * 0.1119F;
            model.breast.rotateAngleY += MathHelper.cos(model.rot1 * 0.6662F * bspeed + (float) Math.PI) * model.rot2 * 0.02F * (float) b * 0.1119F;
            model.breast2.rotateAngleX += MathHelper.cos(model.rot1 * 0.6662F * bspeed + (float) Math.PI) * model.rot2 * 0.05F * (float) b * 0.1119F;
            model.breast2.rotateAngleY += MathHelper.cos(model.rot1 * 0.6662F * bspeed + (float) Math.PI) * model.rot2 * 0.02F * (float) b * 0.1119F;
        }

        GL11.glPushMatrix();
        GL11.glScalef(1.0F / a * (g <= 1 ? 1.0F : 0.675F), 1.0F / a, 1.0F / a * (g <= 1 ? 1.0F : 0.8F));
        GL11.glTranslatef(0.0F, (a - 1.0F) * 1.5F + bbY, 0.015F + bt);
        GL11.glScalef(1.0F, bsY, bs);
        model.Bbreast.render(par); // BODY
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glScalef(1.0F / a * (g <= 1 ? 1.0F : 0.675F) - 0.001F, 1.0F / a, 1.0F / a * (g <= 1 ? 1.0F : 0.8F) - 0.001F);
        GL11.glTranslatef(0.0F, (a - 1.0F) * 1.5F + 0.001F + bbY, 0.015F + bt);
        GL11.glScalef(1.0F, bsY, bs);
        model.Bbreast2.render(par); // BODY
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glScalef(1.0F / a * (g <= 1 ? 1.0F : 0.7F), 1.0F / a, 1.0F / a * (g <= 1 ? 1.0F : 0.7F));
        GL11.glTranslatef(0.0F, (a - 1.0F) * 1.5F, 0.0F);
        model.body.render(par);
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glScalef(1.0F / a * (g <= 1 ? 1.0F : 0.65F), 1.0F / a, 1.0F / a * (g <= 1 ? 1.0F : 0.65F) * (1.0F + 0.001F * (float) p));
        GL11.glTranslatef(0.0F, (a - 1.0F) * 1.5F, model.isSneak ? 0.0F : -0.04F - 1.0E-4F * (float) p);
        model.waist.render(par); // BODY
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glScalef(1.0F / a * (g <= 1 ? 1.0F : 0.75F), 1.0F / a, 1.0F / a * (g <= 1 ? 1.0F : 0.75F) * (1.0F + 0.005F * (float) p));
        GL11.glTranslatef(0.0F, (a - 1.0F) * 1.5F, model.isSneak ? 0.0F : -0.02F - 5.0E-4F * (float) p);
        model.hip.render(par); // BODY
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glScalef(1.0F / a * (g <= 1 ? 1.0F : 0.85F), 1.0F / a, 1.0F / a * (g <= 1 ? 1.0F : 0.85F) * (1.0F + 0.005F * (float) p));
        GL11.glTranslatef(0.0F, (a - 1.0F) * 1.5F, 0.0F - 5.0E-4F * (float) p);
        model.bottom.render(par); // BODY
        GL11.glPopMatrix();
    };

    public static RenderFunction ALL = (g, a, p) -> {
        HEAD.render(g, a, p);
        ARMS.render(g, a, p);
        BODY.render(g, a, p);
        LEGS.render(g, a, p);
    };

    static {
        PLAYER_MAP.put(Type.ALL, ALL);
        PLAYER_MAP.put(Type.Face, HEAD);
        PLAYER_MAP.put(Type.Arms, ARMS);
        PLAYER_MAP.put(Type.RightArm, RIGHT_ARM);
        PLAYER_MAP.put(Type.LeftArm, LEFT_ARM);
        PLAYER_MAP.put(Type.Chest, BODY);
        PLAYER_MAP.put(Type.Legs, LEGS);
        PLAYER_MAP.put(Type.RightLeg, RIGHT_LEG);
        PLAYER_MAP.put(Type.LeftLeg, LEFT_LEG);


        NPC_MAP.put(Type.Face, (m) -> m.renderHead(m.npc, par));
        NPC_MAP.put(Type.Arms, (m) -> m.renderArms(m.npc, par, false));
        NPC_MAP.put(Type.RightArm, (m) -> m.bipedRightArm.render(par));
        NPC_MAP.put(Type.LeftArm, (m) -> m.bipedLeftArm.render(par));
        NPC_MAP.put(Type.Chest, (m) -> m.renderBody(m.npc, par));
        NPC_MAP.put(Type.Legs, (m) -> m.renderLegs(m.npc, par));
        NPC_MAP.put(Type.RightLeg, (m) -> m.legs.leg1.render(par));
        NPC_MAP.put(Type.LeftLeg, (m) -> m.legs.leg2.render(par));


        NPC_MAP.put(Type.ALL, (m) -> {
            render(Type.Face, m);
            render(Type.Arms, m);
            render(Type.Legs, m);
            render(Type.Chest, m);
        });

    }

    public static void render(Type type) {
        PLAYER_MAP.get(type).render(gender(), age(), pregnant());
    }

    public static void render(Type type, ModelMPM model) {
        NPC_MAP.get(type).render(model);
    }

    //THESE CRASH HOT SWAPPING: beware of static ModelBipedDBC calls
    public static int gender() {
        return ModelBipedDBC.g;
    }

    public static float age() {
        return ModelBipedDBC.f;
    }

    public static int pregnant() {
        return ModelBipedDBC.p;
    }

    public interface RenderFunction {
        void render(int gender, float age, int pregnant);
    }

    public interface RenderFunctionMPM {
        void render(ModelMPM model);
    }
}
