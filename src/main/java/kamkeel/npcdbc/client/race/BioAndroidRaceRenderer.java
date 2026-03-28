package kamkeel.npcdbc.client.race;

import JinRyuu.JBRA.ModelBipedDBC;
import JinRyuu.JBRA.RenderPlayerJBRA;
import JinRyuu.JBRA.mod_JBRA;
import JinRyuu.JRMCore.client.config.jrmc.JGConfigClientSettings;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.config.ConfigDBCClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.LogWriter;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class BioAndroidRaceRenderer implements IRaceRenderer {

    // ── Normal tail chain (6 segments, parent-child) ──
    private ModelRenderer bioTailRoot;
    private ModelRenderer btailS1, btailS2, btailS3, btailS4, btailS5, btailS6;

    // ── Max tail chain (6 segments, parent-child) ──
    private ModelRenderer bioTailMaxRoot;
    private ModelRenderer btailS1M, btailS2M, btailS3M, btailS4M, btailS5M, btailS6M, btailS7M;

    // ── Head crest (parent with 2 children) ──
    private ModelRenderer bioheadRoot;
    private ModelRenderer biohead1, biohead2, biohead1I, biohead2I;

    // ── Wings ──
    private ModelRenderer wing, wing2;

    private boolean partsInitialized;
    private ModelBipedDBC cachedModel;
    private Boolean assetsAvailable;
    private boolean loggedMissingAssets;

    private String textureDir() {
        return ConfigDBCClient.EnableHDTextures
            ? CustomNpcPlusDBC.ID + ":textures/sd/bio_android/"
            : CustomNpcPlusDBC.ID + ":textures/sd/bio_android/";
    }

    // ── IRaceRenderer ──

    @Override
    public boolean render(RaceRenderContext ctx) {
        if (!hasRequiredAssets()) {
            if (!loggedMissingAssets) {
                LogWriter.info("Skipping Bio-Android custom race renderer: required Bio-Android textures not present.");
                loggedMissingAssets = true;
            }
            return false;
        }

        ensurePartsInitialized(ctx.model);

        int state = ctx.state;
        switch (state) {
            case 1:
                renderPerfect(ctx);
                break;
            case 2:
                renderSemiPerfect(ctx);
                break;
            case 3:
                renderMax(ctx);
                break;
            default:
                renderBase(ctx);
                break;
        }
        return true;
    }

    @Override
    public boolean renderArm(RaceRenderContext ctx) {
        if (!hasRequiredAssets())
            return false;

        ensurePartsInitialized(ctx.model);

        int state = ctx.state;
        ModelBipedDBC model = ctx.model;
        int id = ctx.armAnimationId;

        switch (state) {
            case 1:
                renderArmPerfect(ctx, model, id);
                break;
            case 2:
                renderArmSemiPerfect(ctx, model, id);
                break;
            case 3:
                renderArmMax(ctx, model, id);
                break;
            default:
                renderArmBase(ctx, model, id);
                break;
        }
        return true;
    }

    // ── Part initialisation ──

    private void ensurePartsInitialized(ModelBipedDBC model) {
        if (partsInitialized && cachedModel == model) return;

        initNormalTail(model);
        initMaxTail(model);
        initHeadCrest(model);
        initWings(model);

        cachedModel = model;
        partsInitialized = true;
    }

    private void initNormalTail(ModelBipedDBC model) {
        bioTailRoot = new ModelRenderer(model);
        bioTailRoot.addBox(0, 0, 0, 0, 12, 0, 0.02F);
        bioTailRoot.setRotationPoint(0, 0, 0);

        btailS1 = new ModelRenderer(model);
        btailS1.addBox(-2, -2, 0, 4, 4, 6);
        btailS1.setRotationPoint(-2, -2, 0);
        setRotation(btailS1, -(float)(Math.PI / 6), 0, 0);

        btailS2 = new ModelRenderer(model);
        btailS2.addBox(-2, -2, 0, 4, 4, 6);
        btailS2.setRotationPoint(0, 0, 5);
        setRotation(btailS2, (float)(Math.PI / 6), 8.727E-4F, 0);

        btailS3 = new ModelRenderer(model);
        btailS3.addBox(-2, -2, 0, 4, 4, 6);
        btailS3.setRotationPoint(0, 0, 5);

        btailS4 = new ModelRenderer(model);
        btailS4.addBox(-2, -2, 0, 4, 4, 6);
        btailS4.setRotationPoint(0, 0, 5);

        btailS5 = new ModelRenderer(model);
        btailS5.addBox(-2, -2, 0, 4, 4, 6);
        btailS5.setRotationPoint(0, 0, 5);

        btailS6 = new ModelRenderer(model, 0, 10);
        btailS6.addBox(-1, -1.5F, -0.5F, 2, 2, 6);
        btailS6.setRotationPoint(0, 0, 4);

        // Build chain: root -> s1 -> s2 -> s3 -> s4 -> s5 -> s6
        btailS5.addChild(btailS6);
        btailS4.addChild(btailS5);
        btailS3.addChild(btailS4);
        btailS2.addChild(btailS3);
        btailS1.addChild(btailS2);
        bioTailRoot.addChild(btailS1);

        bioTailRoot.rotationPointX = 2;
        bioTailRoot.rotationPointY = 10;
        bioTailRoot.rotationPointZ = 2;
    }

    private void initMaxTail(ModelBipedDBC model) {
        bioTailMaxRoot = new ModelRenderer(model);
        bioTailMaxRoot.setRotationPoint(0, 5, 2);

        btailS1M = new ModelRenderer(model);
        btailS1M.setRotationPoint(0, 0, 0);
        btailS1M.cubeList.add(new ModelBox(btailS1M, 0, 0, -2, -2, 0, 4, 4, 6, 0));

        btailS2M = new ModelRenderer(model);
        btailS2M.setRotationPoint(0, 0, 5);
        btailS2M.cubeList.add(new ModelBox(btailS2M, 0, 0, -2, -2, 0, 4, 4, 6, 0));

        btailS3M = new ModelRenderer(model);
        btailS3M.setRotationPoint(0, 0, 5);
        btailS3M.cubeList.add(new ModelBox(btailS3M, 0, 0, -2, -2, 0, 4, 4, 6, 0));

        btailS4M = new ModelRenderer(model);
        btailS4M.setRotationPoint(0, 0, 5);
        btailS4M.cubeList.add(new ModelBox(btailS4M, 0, 0, -2, -2, 0, 4, 4, 6, 0));

        btailS5M = new ModelRenderer(model);
        btailS5M.setRotationPoint(0, 0, 5);
        btailS5M.cubeList.add(new ModelBox(btailS5M, 0, 0, -2, -2, 0, 4, 4, 6, 0));

        btailS6M = new ModelRenderer(model, 0, 0);
        btailS6M.setRotationPoint(0, 0, 5);
        btailS6M.cubeList.add(new ModelBox(btailS6M, 0, 0, -2, -2, 0, 4, 4, 6, 0));

        btailS7M = new ModelRenderer(model, 0, 0);
        btailS7M.setRotationPoint(0, 0, 5);
        btailS7M.cubeList.add(new ModelBox(btailS7M, 0, 10, -3.5f, -3.5f, 0, 7, 7, 7, 0));

        // Chain: root -> s1M -> s2M -> s3M -> s4M -> s5M -> s6M -> s7M
        btailS6M.addChild(btailS7M);
        btailS5M.addChild(btailS6M);
        btailS4M.addChild(btailS5M);
        btailS3M.addChild(btailS4M);
        btailS2M.addChild(btailS3M);
        btailS1M.addChild(btailS2M);
        bioTailMaxRoot.addChild(btailS1M);
    }

    private void initHeadCrest(ModelBipedDBC model) {
        bioheadRoot = new ModelRenderer(model, 0, 0);
        bioheadRoot.addBox(0, 0, 0, 0, 0, 0, 0.02F);
        bioheadRoot.setRotationPoint(0, 0, 0);

        biohead1 = new ModelRenderer(model, 0, 0);
        biohead1.addBox(-4F, -14, -3.5F, 3, 7, 7);
        biohead1.setRotationPoint(0, 0, 0);
        setRotation(biohead1, 0, 0, 0);

        biohead2 = new ModelRenderer(model, 0, 0);
        biohead2.mirror = true;
        biohead2.addBox(1F, -14, -3.5F, 3, 7, 7);
        biohead2.setRotationPoint(0, 0, 0);
        setRotation(biohead2, 0, 0, 0);

        biohead1I = new ModelRenderer(model, 0, 0);
        biohead1I.addBox(-2.5F, -14, -3.5F, 3, 6, 5);
        biohead1I.setRotationPoint(0, 0, 0);
        setRotation(biohead1I, 0, 0, -0.2094395F);

        biohead2I = new ModelRenderer(model, 0, 0);
        biohead2I.mirror = true;
        biohead2I.addBox(-0.5F, -14, -3.5F, 3, 6, 5);
        biohead2I.setRotationPoint(0, 0, 0);
        setRotation(biohead2I, 0, 0, 0.2094395F);

        bioheadRoot.addChild(biohead1);
        bioheadRoot.addChild(biohead2);
        bioheadRoot.addChild(biohead1I);
        bioheadRoot.addChild(biohead2I);
    }

    private void initWings(ModelBipedDBC model) {
        wing = new ModelRenderer(model, 0, 0);
        wing.addBox(-1, 2, 2, 7, 20, 1);
        wing.setRotationPoint(0, 0, 0);
        setRotation(wing, 0.1570796F, 0.0349066F, -0.2792527F);

        wing2 = new ModelRenderer(model, 0, 0);
        wing2.mirror = true;
        wing2.addBox(-6, 2, 2, 7, 20, 1);
        wing2.setRotationPoint(0, 0, 0);
        setRotation(wing2, 0.1570796F, -0.0349066F, 0.2792527F);
    }

    private void setRotation(ModelRenderer m, float x, float y, float z) {
        m.rotateAngleX = x;
        m.rotateAngleY = y;
        m.rotateAngleZ = z;
    }

    // ── Tail animation ──

    private void animateNormalTail(ModelBipedDBC model) {
        float rot3 = model.rot3;
        float r = MathHelper.sin(rot3 * 0.02F) * 0.1F;
        float r2 = MathHelper.cos(rot3 * 0.02F) * 0.1F;
        float r3 = MathHelper.cos(rot3 * 0.14F) * 0.1F;
        boolean anim = mod_JBRA.a6P9H9B;

        btailS1.rotateAngleY = 0.2F;
        if (anim) btailS1.rotateAngleY += MathHelper.cos(rot3 * 0.09F) * 0.2F - 0.2F + r;
        btailS1.rotateAngleX = -0.3F;

        btailS2.rotateAngleY = 0.2F;
        if (anim) btailS2.rotateAngleY += MathHelper.cos(rot3 * 0.09F) * 0.2F - 0.2F + r2 + r3;
        btailS2.rotateAngleX = 0.4F;

        btailS3.rotateAngleY = 0.1F;
        if (anim) btailS3.rotateAngleY += MathHelper.cos(rot3 * 0.09F) * 0.1F - 0.1F + r + r3;
        btailS3.rotateAngleX = 0.6F;
        if (anim) btailS3.rotateAngleX += MathHelper.sin(rot3 * 0.09F) * 0.4F + 0.3F;

        btailS4.rotateAngleY = 0.1F;
        if (anim) btailS4.rotateAngleY += MathHelper.cos(rot3 * 0.09F) * 0.4F - 0.1F + r2;
        btailS4.rotateAngleX = 0.3F;
        if (anim) btailS4.rotateAngleX += MathHelper.sin(rot3 * 0.09F) * 0.1F - 0.2F;

        btailS5.rotateAngleY = 0.2F;
        if (anim) btailS5.rotateAngleY += MathHelper.cos(rot3 * 0.09F) * 0.4F - 0.2F + r + r3;
        btailS5.rotateAngleX = -0.2F;
        if (anim) btailS5.rotateAngleX += MathHelper.sin(rot3 * 0.09F) * 0.1F - 0.3F;

        btailS6.rotateAngleY = 0.2F;
        if (anim) btailS6.rotateAngleY += MathHelper.cos(rot3 * 0.09F) * 0.4F - 0.2F + r + r3;
        btailS6.rotateAngleX = -0.2F;
        if (anim) btailS6.rotateAngleX += MathHelper.sin(rot3 * 0.09F) * 0.1F - 0.3F;
    }

    private void animateMaxTail(ModelBipedDBC model) {
        float rot3 = model.rot3;
        float r = MathHelper.sin(rot3 * 0.02F) * 0.1F;
        float r2 = MathHelper.cos(rot3 * 0.02F) * 0.1F;
        float r3 = MathHelper.cos(rot3 * 0.14F) * 0.1F;
        boolean anim = mod_JBRA.a6P9H9B;

        btailS1M.rotateAngleY = 0.2F;
        if (anim) btailS1M.rotateAngleY += MathHelper.cos(rot3 * 0.09F) * 0.2F - 0.2F + r;
        btailS1M.rotateAngleX = -0.3F;

        btailS2M.rotateAngleY = 0.2F;
        if (anim) btailS2M.rotateAngleY += MathHelper.cos(rot3 * 0.09F) * 0.2F - 0.2F + r2 + r3;
        btailS2M.rotateAngleX = 0.4F;

        btailS3M.rotateAngleY = 0.1F;
        if (anim) btailS3M.rotateAngleY += MathHelper.cos(rot3 * 0.09F) * 0.1F - 0.1F + r + r3;
        btailS3M.rotateAngleX = 0.6F;
        if (anim) btailS3M.rotateAngleX += MathHelper.sin(rot3 * 0.09F) * 0.4F + 0.3F;

        btailS4M.rotateAngleY = 0.1F;
        if (anim) btailS4M.rotateAngleY += MathHelper.cos(rot3 * 0.09F) * 0.4F - 0.1F + r2;
        btailS4M.rotateAngleX = 0.3F;
        if (anim) btailS4M.rotateAngleX += MathHelper.sin(rot3 * 0.09F) * 0.1F - 0.2F;

        btailS5M.rotateAngleY = 0.2F;
        if (anim) btailS5M.rotateAngleY += MathHelper.cos(rot3 * 0.09F) * 0.4F - 0.2F + r + r3;
        btailS5M.rotateAngleX = -0.2F;
        if (anim) btailS5M.rotateAngleX += MathHelper.sin(rot3 * 0.09F) * 0.1F - 0.3F;

        btailS6M.rotateAngleY = 0.2F;
        if (anim) btailS6M.rotateAngleY += MathHelper.cos(rot3 * 0.09F) * 0.4F - 0.2F + r + r3;
        btailS6M.rotateAngleX = -0.2F;
        if (anim) btailS6M.rotateAngleX += MathHelper.sin(rot3 * 0.09F) * 0.1F - 0.3F;

        btailS7M.rotateAngleY = btailS6M.rotateAngleY;
        btailS7M.rotateAngleX = btailS6M.rotateAngleX;

//        btailS7M.rotateAngleY = 0.2F;
//        if (anim) btailS7M.rotateAngleY += MathHelper.cos(rot3 * 0.09F) * 0.4F - 0.2F + r + r3;
//        btailS7M.rotateAngleX = -0.2F;
//        if (anim) btailS7M.rotateAngleX += MathHelper.sin(rot3 * 0.09F) * 0.1F - 0.3F;
    }

    // ── Body-part rendering ──

    private void renderTailWithBodyTransform(RaceRenderContext ctx, ModelRenderer tailRoot, boolean animate) {
        ModelBipedDBC model = ctx.model;
        float f = 0.0625F;

        GL11.glPushMatrix();
        float f6 = ModelBipedDBC.f;
        GL11.glScalef(1.0F / f6 * (ModelBipedDBC.g <= 1 ? 1.0F : 0.7F), 1.0F / f6, 1.0F / f6 * (ModelBipedDBC.g <= 1 ? 1.0F : 0.7F));
        GL11.glTranslatef(0, (f6 - 1.0F) * 1.5F, 0);
        transRot(f, model.B1);
        if (animate) {
            if (tailRoot == bioTailRoot)
                animateNormalTail(model);
            else
                animateMaxTail(model);
        }
        tailRoot.render(f);
        GL11.glPopMatrix();
    }

    private void renderTailStaticWithBodyTransform(RaceRenderContext ctx, ModelRenderer tailRoot) {
        ModelBipedDBC model = ctx.model;
        float f = 0.0625F;

        GL11.glPushMatrix();
        float f6 = ModelBipedDBC.f;
        GL11.glScalef(1.0F / f6 * (ModelBipedDBC.g <= 1 ? 1.0F : 0.7F), 1.0F / f6, 1.0F / f6 * (ModelBipedDBC.g <= 1 ? 1.0F : 0.7F));
        GL11.glTranslatef(0, (f6 - 1.0F) * 1.5F, 0);
        transRot(f, model.B1);
        tailRoot.render(f);
        GL11.glPopMatrix();
    }

    private void renderWingsWithBodyTransform(RaceRenderContext ctx) {
        ModelBipedDBC model = ctx.model;
        float f = 0.0625F;

        GL11.glPushMatrix();
        float f6 = ModelBipedDBC.f;
        GL11.glScalef(1.0F / f6 * (ModelBipedDBC.g <= 1 ? 1.0F : 0.7F), 1.0F / f6, 1.0F / f6 * (ModelBipedDBC.g <= 1 ? 1.0F : 0.7F));
        GL11.glTranslatef(0, (f6 - 1.0F) * 1.5F, 0);

        wing.rotateAngleY = Math.abs(model.bipedLeftArm.rotateAngleY / 7.0F) + model.bipedBody.rotateAngleY;
        wing.rotateAngleX = Math.abs(model.bipedLeftArm.rotateAngleX / 7.0F) + model.bipedBody.rotateAngleX;
        wing.rotationPointX = model.bipedBody.rotationPointX;
        wing.rotationPointY = model.bipedBody.rotationPointY;
        wing.render(f);

        wing2.rotateAngleY = Math.abs(model.bipedLeftArm.rotateAngleY / 7.0F) + model.bipedBody.rotateAngleY;
        wing2.rotateAngleX = Math.abs(model.bipedLeftArm.rotateAngleX / 7.0F) + model.bipedBody.rotateAngleX;
        wing2.rotationPointX = model.bipedBody.rotationPointX;
        wing2.rotationPointY = model.bipedBody.rotationPointY;
        wing2.render(f);

        GL11.glPopMatrix();
    }

    private void renderHeadCrestWithHeadTransform(RaceRenderContext ctx) {
        ModelBipedDBC model = ctx.model;
        float f = 0.0625F;

        GL11.glPushMatrix();
        float f6 = ModelBipedDBC.f;
        GL11.glScalef(1.0F / f6 * (ModelBipedDBC.g <= 1 ? 1.0F : 0.7F), 1.0F / f6, 1.0F / f6 * (ModelBipedDBC.g <= 1 ? 1.0F : 0.7F));
        GL11.glTranslatef(0, (f6 - 1.0F) * 1.5F, 0);

        bioheadRoot.rotateAngleY = model.bipedHead.rotateAngleY;
        bioheadRoot.rotateAngleX = model.bipedHead.rotateAngleX;
        bioheadRoot.rotationPointX = model.bipedHead.rotationPointX;
        bioheadRoot.rotationPointY = model.bipedHead.rotationPointY;

        biohead1I.isHidden = ctx.state != 0;
        biohead2I.isHidden = ctx.state != 0;

        biohead1.isHidden = ctx.state == 0;
        biohead2.isHidden = ctx.state == 0;

        bioheadRoot.render(f);

        GL11.glPopMatrix();
    }

    private void transRot(float f5, ModelRenderer m) {
        GL11.glTranslatef(m.rotationPointX * f5, m.rotationPointY * f5, m.rotationPointZ * f5);
        if (m.rotateAngleZ != 0)
            GL11.glRotatef(m.rotateAngleZ * (180.0F / (float) Math.PI), 0, 0, 1);
        if (m.rotateAngleY != 0)
            GL11.glRotatef(m.rotateAngleY * (180.0F / (float) Math.PI), 0, 1, 0);
        if (m.rotateAngleX != 0)
            GL11.glRotatef(m.rotateAngleX * (180.0F / (float) Math.PI), 1, 0, 0);
    }

    // ── Rendering helpers ──

    private void bindAndColor(RaceRenderContext ctx, String texture, int color) {
        ctx.bindTexture(new ResourceLocation(textureDir() + texture));
        RenderPlayerJBRA.glColor3f(color);
    }

    private void whiteColor() {
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
    }

    private String[] getRequiredAssets() {
        List<String> list = new ArrayList<>();

        String[] states = {"imperfect", "semiperfect", "perfect", "max"};
        String[] facial = {"eye_base.png", "eye_left.png", "eye_right.png", "nose.png", "mouth.png"};
        String[] tail = {"tail_0.png", "tail_1.png", "stinger.png"};

        for (String state : states) {
            for (int i = 0; i < 5; i++) {
                list.add(textureDir() + state + "/bio_" + state + "_" + i + ".png");
            }

            // face
            for (String f : facial) {
                list.add(textureDir() + state + "/face/" + f);
            }

            list.add(textureDir() + state + "/bio_" + state + "_crest.png");

            if (!state.equals("semiperfect")) {
                list.add(textureDir() + state + "/bio_" + state + "_wings.png");
            }

            if (!state.equals("perfect")) {
                for (String t : tail) {
                    list.add(textureDir() + state + "/bio_" + state + "_" + t);
                }
            }
        }

        return list.toArray(new String[0]);
    }

    private boolean hasRequiredAssets() {
        if (assetsAvailable != null) return assetsAvailable;

        String[] assets = getRequiredAssets();

        for (String asset : assets) {
            if (!resourceExists(asset)) {
                printMissingTexture(asset);
                return setAssets(false);
            }
        }

        return setAssets(true);
    }

    private boolean resourceExists(String path) {
        try {
            Minecraft.getMinecraft().getResourceManager()
                .getResource(new ResourceLocation(path));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean setAssets(boolean value) {
        assetsAvailable = value;
        return value;
    }

    private void printMissingTexture(String texture) {
        LogWriter.error("Missing Texture for Bio Android: " + texture);
    }

    // ── State renderers (third person) ──

    private void renderBase(RaceRenderContext ctx) {
        ModelBipedDBC model = ctx.model;

        // Head crest
        bindAndColor(ctx, "imperfect/bio_imperfect_crest.png", ctx.bodyCM);
        renderHeadCrestWithHeadTransform(ctx);

        // Tail
        bindAndColor(ctx, "imperfect/bio_imperfect_stinger.png", 0xFFFFFF);
        renderTailStaticWithBodyTransform(ctx, bioTailRoot);

        bindAndColor(ctx, "imperfect/bio_imperfect_tail_0.png", ctx.bodyCM);
        renderTailWithBodyTransform(ctx, bioTailRoot, true);

        bindAndColor(ctx, "imperfect/bio_imperfect_tail_1.png", 0xFFFFFF);
        renderTailWithBodyTransform(ctx, bioTailRoot, true);

        // Wings
        bindAndColor(ctx, "imperfect/bio_imperfect_wings.png", ctx.bodyCM);
        renderWingsWithBodyTransform(ctx);

        // Body layers
        bindAndColor(ctx, "imperfect/bio_imperfect_0.png", ctx.bodyCM);
        model.renderBody(0.0625F);

        bindAndColor(ctx, "imperfect/bio_imperfect_1.png", ctx.bodyC1);
        model.renderBody(0.0625F);

        bindAndColor(ctx, "imperfect/bio_imperfect_2.png", ctx.bodyC2);
        model.renderBody(0.0625F);

        bindAndColor(ctx, "imperfect/bio_imperfect_3.png", ctx.bodyC3);
        model.renderBody(0.0625F);

        bindAndColor(ctx, "imperfect/bio_imperfect_4.png", 0xFFFFFF);
        model.renderBody(0.0625F);

        // Eyes
        renderEyes(ctx, "imperfect/face/eye_base.png", "imperfect/face/eyebrow.png", "imperfect/face/eye_left.png", "imperfect/face/eye_right.png");
    }

    private void renderSemiPerfect(RaceRenderContext ctx) {
        ModelBipedDBC model = ctx.model;

        // Head crest
        bindAndColor(ctx, "semiperfect/bio_semiperfect_crest.png", ctx.bodyCM);
        renderHeadCrestWithHeadTransform(ctx);

        // Tail
        bindAndColor(ctx, "semiperfect/bio_semiperfect_stinger.png", 0xFFFFFF);
        renderTailStaticWithBodyTransform(ctx, bioTailRoot);

        bindAndColor(ctx, "semiperfect/bio_semiperfect_tail_0.png", ctx.bodyC2);
        renderTailWithBodyTransform(ctx, bioTailRoot, true);

        bindAndColor(ctx, "semiperfect/bio_semiperfect_tail_1.png", 0xFFFFFF);
        renderTailWithBodyTransform(ctx, bioTailRoot, true);

        // Body layers
        bindAndColor(ctx, "semiperfect/bio_semiperfect_0.png", ctx.bodyCM);
        model.renderBody(0.0625F);

        bindAndColor(ctx, "semiperfect/bio_semiperfect_1.png", ctx.bodyC1);
        model.renderBody(0.0625F);

        bindAndColor(ctx, "semiperfect/bio_semiperfect_2.png", ctx.bodyC2);
        model.renderBody(0.0625F);

        bindAndColor(ctx, "semiperfect/bio_semiperfect_3.png", ctx.bodyC3);
        model.renderBody(0.0625F);

        bindAndColor(ctx, "semiperfect/bio_semiperfect_4.png", 0xFFFFFF);
        model.renderBody(0.0625F);

        // Eyes
        renderEyes(ctx, "semiperfect/face/eye_base.png", "semiperfect/face/eyebrow.png", "semiperfect/face/eye_left.png", "semiperfect/face/eye_right.png");
        renderFacialFeatures(ctx, "semiperfect/face/nose.png", ctx.bodyC1, "semiperfect/face/mouth.png", 0xd7a4bb);
    }

    private void renderPerfect(RaceRenderContext ctx) {
        ModelBipedDBC model = ctx.model;

        // Body layers
        bindAndColor(ctx, "perfect/bio_perfect_0.png", ctx.bodyCM);
        model.renderBody(0.0625F);

        // Head crest
        bindAndColor(ctx, "perfect/bio_perfect_crest.png", ctx.bodyCM);
        renderHeadCrestWithHeadTransform(ctx);

        // Wings
        bindAndColor(ctx, "perfect/bio_perfect_wings.png", 0xFFFFFF);
        renderWingsWithBodyTransform(ctx);

        bindAndColor(ctx, "perfect/bio_perfect_1.png", ctx.bodyC1);
        model.renderBody(0.0625F);

        bindAndColor(ctx, "perfect/bio_perfect_2.png", ctx.bodyC2);
        model.renderBody(0.0625F);

        bindAndColor(ctx, "perfect/bio_perfect_3.png", ctx.bodyC3);
        model.renderBody(0.0625F);

        bindAndColor(ctx, "perfect/bio_perfect_4.png", 0xFFFFFF);
        model.renderBody(0.0625F);

        // Eyes
        renderEyes(ctx, "perfect/face/eye_base.png", "perfect/face/eyebrow.png", "perfect/face/eye_left.png", "perfect/face/eye_right.png");
        renderFacialFeatures(ctx, "perfect/face/nose.png", ctx.bodyC1, "perfect/face/mouth.png", ctx.bodyC1);
    }

    private void renderMax(RaceRenderContext ctx) {
        ModelBipedDBC model = ctx.model;

        // Base body
        bindAndColor(ctx, "max/bio_max_0.png", ctx.bodyCM);
        model.renderBody(0.0625F);

        // Head crest
        bindAndColor(ctx, "max/bio_max_crest.png", ctx.bodyCM);
        renderHeadCrestWithHeadTransform(ctx);

        // Tail
        bindAndColor(ctx, "max/bio_max_stinger.png", 0xFFFFFF);
        renderTailStaticWithBodyTransform(ctx, bioTailMaxRoot);

        bindAndColor(ctx, "max/bio_max_tail_0.png", ctx.bodyCM);
        renderTailWithBodyTransform(ctx, bioTailMaxRoot, true);

        bindAndColor(ctx, "max/bio_max_tail_1.png", ctx.bodyC1);
        renderTailWithBodyTransform(ctx, bioTailMaxRoot, true);

        // Wings
        bindAndColor(ctx, "max/bio_max_wings.png", 0xFFFFFF);
        renderWingsWithBodyTransform(ctx);

        bindAndColor(ctx, "max/bio_max_1.png", ctx.bodyC1);
        model.renderBody(0.0625F);

        bindAndColor(ctx, "max/bio_max_2.png", ctx.bodyC2);
        model.renderBody(0.0625F);

        bindAndColor(ctx, "max/bio_max_3.png", ctx.bodyC3);
        model.renderBody(0.0625F);

        bindAndColor(ctx, "max/bio_max_4.png", 0xFFFFFF);
        model.renderBody(0.0625F);

        // Eyes
        renderEyes(ctx, "max/face/eye_base.png", "max/face/eyebrow.png", "max/face/eye_left.png", "max/face/eye_right.png");
        renderFacialFeatures(ctx, "max/face/nose.png", 0xFFFFFF, "max/face/mouth.png", 0xFFE0FA);
    }

    // ── Eye rendering ──

    private void renderEyes(RaceRenderContext ctx, String baseTexture, String eyebrowTexture, String leftTexture, String rightTexture) {
        ModelBipedDBC model = ctx.model;

        boolean semiPerfect = ctx.state == 2;

        whiteColor();
        ctx.bindTexture(new ResourceLocation(textureDir() + baseTexture));
        model.renderHairs(0.0625F, "EYEBASE");

        if (!eyebrowTexture.isEmpty()) {
            RenderPlayerJBRA.glColor3f(ctx.bodyC1);
            ctx.bindTexture(new ResourceLocation(textureDir() + eyebrowTexture));
            model.renderHairs(0.0625F, "EYEBROW");
        }

        RenderPlayerJBRA.glColor3f(semiPerfect ? 0xafddff : ctx.eyeC1);
        ctx.bindTexture(new ResourceLocation(textureDir() + leftTexture));
        model.renderHairs(0.0625F, "EYELEFT");

        RenderPlayerJBRA.glColor3f(semiPerfect ? 0xafddff : ctx.eyeC2);
        ctx.bindTexture(new ResourceLocation(textureDir() + rightTexture));
        model.renderHairs(0.0625F, "EYERIGHT");
    }

    private void renderFacialFeatures(RaceRenderContext ctx, String noseTexture, int noseColor, String mouthTexture, int mouthColor) {
        ModelBipedDBC model = ctx.model;

        RenderPlayerJBRA.glColor3f(noseColor);
        ctx.bindTexture(new ResourceLocation(textureDir() + noseTexture));
        model.renderHairs(0.0625F, "FACENOSE");

        RenderPlayerJBRA.glColor3f(mouthColor);
        ctx.bindTexture(new ResourceLocation(textureDir() + mouthTexture));
        model.renderHairs(0.0625F, "FACEMOUTH");
    }

    // ── First-person arm rendering ──

    private void renderArmBase(RaceRenderContext ctx, ModelBipedDBC model, int id) {
        bindAndColor(ctx, "imperfect/bio_imperfect_0.png", ctx.bodyCM);
        renderArmPiece(model, id, ctx.entity instanceof EntityPlayer ? (EntityPlayer) ctx.entity : null);

        bindAndColor(ctx, "imperfect/bio_imperfect_1.png", ctx.bodyC1);
        renderArmPiece(model, id, ctx.entity instanceof EntityPlayer ? (EntityPlayer) ctx.entity : null);

        bindAndColor(ctx, "imperfect/bio_imperfect_2.png", ctx.bodyC2);
        renderArmPiece(model, id, ctx.entity instanceof EntityPlayer ? (EntityPlayer) ctx.entity : null);

        bindAndColor(ctx, "imperfect/bio_imperfect_3.png", ctx.bodyC3);
        renderArmPiece(model, id, ctx.entity instanceof EntityPlayer ? (EntityPlayer) ctx.entity : null);

        bindAndColor(ctx, "imperfect/bio_imperfect_4.png", 0xFFFFFF);
        renderArmPiece(model, id, ctx.entity instanceof EntityPlayer ? (EntityPlayer) ctx.entity : null);
    }

    private void renderArmSemiPerfect(RaceRenderContext ctx, ModelBipedDBC model, int id) {
        EntityPlayer player = ctx.entity instanceof EntityPlayer ? (EntityPlayer) ctx.entity : null;

        bindAndColor(ctx, "semiperfect/bio_semiperfect_0.png", ctx.bodyCM);
        renderArmPiece(model, id, player);

        bindAndColor(ctx, "semiperfect/bio_semiperfect_1.png", ctx.bodyC1);
        renderArmPiece(model, id, player);

        bindAndColor(ctx, "semiperfect/bio_semiperfect_2.png", ctx.bodyC2);
        renderArmPiece(model, id, player);

        bindAndColor(ctx, "semiperfect/bio_semiperfect_3.png", ctx.bodyC3);
        renderArmPiece(model, id, player);

        bindAndColor(ctx, "semiperfect/bio_semiperfect_4.png", 0xFFFFFF);
        renderArmPiece(model, id, player);
    }

    private void renderArmPerfect(RaceRenderContext ctx, ModelBipedDBC model, int id) {
        EntityPlayer player = ctx.entity instanceof EntityPlayer ? (EntityPlayer) ctx.entity : null;

        bindAndColor(ctx, "perfect/bio_perfect_0.png", ctx.bodyCM);
        renderArmPiece(model, id, player);

        bindAndColor(ctx, "perfect/bio_perfect_1.png", ctx.bodyC1);
        renderArmPiece(model, id, player);

        bindAndColor(ctx, "perfect/bio_perfect_2.png", ctx.bodyC2);
        renderArmPiece(model, id, player);

        bindAndColor(ctx, "perfect/bio_perfect_3.png", ctx.bodyC3);
        renderArmPiece(model, id, player);

        bindAndColor(ctx, "perfect/bio_perfect_4.png", 0xFFFFFF);
        renderArmPiece(model, id, player);
    }

    private void renderArmMax(RaceRenderContext ctx, ModelBipedDBC model, int id) {
        EntityPlayer player = ctx.entity instanceof EntityPlayer ? (EntityPlayer) ctx.entity : null;

        bindAndColor(ctx, "max/bio_max_0.png", ctx.bodyCM);
        renderArmPiece(model, id, player);

        bindAndColor(ctx, "max/bio_max_1.png", ctx.bodyC1);
        renderArmPiece(model, id, player);

        bindAndColor(ctx, "max/bio_max_2.png", ctx.bodyC2);
        renderArmPiece(model, id, player);

        bindAndColor(ctx, "max/bio_max_3.png", ctx.bodyC3);
        renderArmPiece(model, id, player);

        bindAndColor(ctx, "max/bio_max_4.png", 0xFFFFFF);
        renderArmPiece(model, id, player);
    }

    private void renderArmPiece(ModelBipedDBC model, int id, EntityPlayer player) {
        if (player != null)
            model.setRotationAngles(0, 0, 0, 0, 0, 0.0625F, player);

        if (id == -1) {
            model.RA.render(0.0625F);
        } else {
            applyArmAnimation(model.RA, model.LA, id);
        }
    }

    private void applyArmAnimation(ModelRenderer ra, ModelRenderer la, int id) {
        if (id != 0 && id != 6) {
            if (id == 2 || id == 3) {
                GL11.glPushMatrix();
                GL11.glTranslatef(-0.2F, 0, -0.1F);
                GL11.glRotatef(10, -1, 0, 0);
                GL11.glRotatef(20, 0, 0, -1);
                ra.render(0.0625F);
                GL11.glPopMatrix();
            } else if (id == 4 || id == 5) {
                GL11.glPushMatrix();
                GL11.glTranslatef(-0.2F, 0.4F, -0.1F);
                GL11.glRotatef(10, -1, 0, 0);
                GL11.glRotatef(20, 0, 0, -1);
                GL11.glRotatef(40, 0, 0, 1);
                ra.render(0.0625F);
                GL11.glPopMatrix();
            }
        } else {
            boolean shouldRender = id == 0 ? JGConfigClientSettings.CLIENT_DA18 : JGConfigClientSettings.instantTransmissionFirstPerson;
            if (shouldRender) {
                GL11.glPushMatrix();
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
                GL11.glDepthMask(false);
                GL11.glTranslatef(-0.5F, -0.1F, -0.1F);
                GL11.glRotatef(40, 0, 0, -1);
                GL11.glRotatef(80, -1, 0, 0);
                GL11.glRotatef(id == 0 ? -20 : 30, 0, 0, 1);
            }

            ra.render(0.0625F);

            if (shouldRender) {
                GL11.glPopMatrix();
            }
        }

        applyArmBruiseAnimation(ra, la, id);
    }

    private void applyArmBruiseAnimation(ModelRenderer ra, ModelRenderer la, int id) {
        if (id == 0) {
            if (JGConfigClientSettings.CLIENT_DA18) {
                GL11.glPushMatrix();
                GL11.glTranslatef(-0.2F, -0.4F, -0.8F);
                GL11.glRotatef(50, 1, 0, 1);
                GL11.glRotatef(90, 0, 1, 0);
                GL11.glRotatef(20, 0, 0, 1);
                la.render(0.0625F);
                GL11.glPopMatrix();
            }
        } else if (id == 3) {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.1F, -0.2F, -0.5F);
            GL11.glTranslatef(-0.2F, 0, -0.1F);
            GL11.glRotatef(10, -1, 0, 0);
            GL11.glRotatef(20, 0, 0, -1);
            GL11.glRotatef(115, 0, 1, 0);
            la.render(0.0625F);
            GL11.glPopMatrix();
        } else if (id == 5) {
            GL11.glPushMatrix();
            GL11.glTranslatef(-0.2F, -0.4F, -0.8F);
            GL11.glTranslatef(-0.4F, 0.1F, -0.1F);
            GL11.glRotatef(42, -1, 0, 0);
            GL11.glRotatef(10, 0, 0, 1);
            GL11.glRotatef(115, 0, 1, 0);
            GL11.glTranslatef(-0.6F, 0.08F, 0.3F);
            la.render(0.0625F);
            GL11.glPopMatrix();
        }
    }
}
