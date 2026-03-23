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

@SideOnly(Side.CLIENT)
public class AndroidRaceRenderer implements IRaceRenderer {

    // ── Normal tail chain (6 segments, parent-child) ──
    private ModelRenderer bioTailRoot;
    private ModelRenderer btailS1, btailS2, btailS3, btailS4, btailS5, btailS6;

    // ── Max tail chain (6 segments, parent-child) ──
    private ModelRenderer bioTailMaxRoot;
    private ModelRenderer btailS1M, btailS2M, btailS3M, btailS4M, btailS5M, btailS6M;

    // ── Head crest (parent with 2 children) ──
    private ModelRenderer bioheadRoot;
    private ModelRenderer biohead1, biohead2;

    // ── Wings ──
    private ModelRenderer wing, wing2;

    private boolean partsInitialized;
    private ModelBipedDBC cachedModel;
    private Boolean assetsAvailable;
    private boolean loggedMissingAssets;

    private String textureDir() {
        return ConfigDBCClient.EnableHDTextures
            ? CustomNpcPlusDBC.ID + ":textures/sd/android/"
            : CustomNpcPlusDBC.ID + ":textures/sd/android/";
    }

    // ── IRaceRenderer ──

    @Override
    public boolean render(RaceRenderContext ctx) {
        if (!hasRequiredAssets()) {
            if (!loggedMissingAssets) {
                LogWriter.info("Skipping Android custom race renderer: required Android textures not present.");
                loggedMissingAssets = true;
            }
            return false;
        }

        ensurePartsInitialized(ctx.model);

        int state = ctx.state;
        switch (state) {
            case 1:
                renderSemiPerfect(ctx);
                break;
            case 2:
                renderPerfect(ctx);
                break;
            case 3:
                renderPerfectMax(ctx);
                break;
            case 4:
                renderUncontrolledMax(ctx);
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
                renderArmSemiPerfect(ctx, model, id);
                break;
            case 2:
                renderArmPerfect(ctx, model, id);
                break;
            case 3:
                renderArmPerfectMax(ctx, model, id);
                break;
            case 4:
                renderArmUncontrolledMax(ctx, model, id);
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

        btailS6 = new ModelRenderer(model, 44, 16);
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
        btailS6M.cubeList.add(new ModelBox(btailS6M, 20, 16, -3.5F, -3.5F, 6, 7, 7, 6, 0));

        // Chain: root -> s1M -> s2M -> s3M -> s4M -> s5M -> s6M
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
        biohead1.addBox(-2.5F, -14, -3.5F, 3, 7, 7);
        biohead1.setRotationPoint(0, 0, 0);
        setRotation(biohead1, 0, 0, -0.2094395F);

        biohead2 = new ModelRenderer(model, 0, 0);
        biohead2.mirror = true;
        biohead2.addBox(-0.5F, -14, -3.5F, 3, 7, 7);
        biohead2.setRotationPoint(0, 0, 0);
        setRotation(biohead2, 0, 0, 0.2094395F);

        bioheadRoot.addChild(biohead1);
        bioheadRoot.addChild(biohead2);
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

    private boolean hasRequiredAssets() {
        if (assetsAvailable != null) return assetsAvailable;

        String[] required = {"bio3.png", "bio1.png", "bio2.png", "bioeyesbase.png", "bioeyeleft.png", "bioeyeright.png"};
        for (String name : required) {
            try {
                Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(textureDir() + name));
            } catch (IOException ignored) {
                assetsAvailable = false;
                return false;
            }
        }
        assetsAvailable = true;
        return true;
    }

    // ── State renderers (third person) ──

    private void renderBase(RaceRenderContext ctx) {
        ModelBipedDBC model = ctx.model;

        // Tail
        bindAndColor(ctx, "biotail2.png", 0xFFFFFF);
        renderTailStaticWithBodyTransform(ctx, bioTailRoot);

        bindAndColor(ctx, "biotail.png", ctx.bodyCM);
        renderTailWithBodyTransform(ctx, bioTailRoot, true);

        // Wings
        bindAndColor(ctx, "biowings.png", ctx.bodyCM);
        renderWingsWithBodyTransform(ctx);

        // Body layers
        bindAndColor(ctx, "bio3.png", ctx.bodyCM);
        model.renderBody(0.0625F);

        bindAndColor(ctx, "bio1.png", ctx.bodyC1);
        model.renderBody(0.0625F);

        bindAndColor(ctx, "bio2.png", ctx.bodyC2);
        model.renderBody(0.0625F);

        // Eyes
        renderEyes(ctx, "bioeyesbase.png", "bioeyeleft.png", "bioeyeright.png");
    }

    private void renderSemiPerfect(RaceRenderContext ctx) {
        ModelBipedDBC model = ctx.model;

        // Body layers
        bindAndColor(ctx, "bio3S.png", ctx.bodyCM);
        model.renderBody(0.0625F);

        // Head crest
        bindAndColor(ctx, "bioskinsemi.png", 0xFFFFFF);
        renderHeadCrestWithHeadTransform(ctx);

        bindAndColor(ctx, "bio1S.png", ctx.bodyC1);
        model.renderBody(0.0625F);

        // Tail
        whiteColor();
        ctx.bindTexture(new ResourceLocation(textureDir() + "biotail2.png"));
        renderTailStaticWithBodyTransform(ctx, bioTailRoot);

        bindAndColor(ctx, "biotailS.png", 0xFFFFFF);
        renderTailWithBodyTransform(ctx, bioTailRoot, true);

        bindAndColor(ctx, "bio2S.png", ctx.bodyC2);
        model.renderBody(0.0625F);

        bindAndColor(ctx, "bio4S.png", 0xFFFFFF);
        model.renderBody(0.0625F);

        // Eyes
        renderEyes(ctx, "bioeyesbaseS.png", "bioeyeleftS.png", "bioeyerightS.png");
    }

    private void renderPerfect(RaceRenderContext ctx) {
        ModelBipedDBC model = ctx.model;

        // Body layers
        bindAndColor(ctx, "bio2P.png", ctx.bodyCM);
        model.renderBody(0.0625F);

        // Head crest
        bindAndColor(ctx, "bioskin.png", 0xFFFFFF);
        renderHeadCrestWithHeadTransform(ctx);

        // Wings
        bindAndColor(ctx, "biowingsP.png", 0xFFFFFF);
        renderWingsWithBodyTransform(ctx);

        bindAndColor(ctx, "bio1P.png", 0xFFFFFF);
        model.renderBody(0.0625F);

        // Eyes
        renderEyes(ctx, "bioeyesbaseS.png", "bioeyeleftS.png", "bioeyerightS.png");
    }

    private void renderPerfectMax(RaceRenderContext ctx) {
        ModelBipedDBC model = ctx.model;

        // Max tail overlay (white)
        whiteColor();
        ctx.bindTexture(new ResourceLocation(textureDir() + "biotailmax2.png"));
        renderTailStaticWithBodyTransform(ctx, bioTailMaxRoot);

        // Body
        bindAndColor(ctx, "bio2M.png", ctx.bodyCM);
        model.renderBody(0.0625F);

        // Head crest
        bindAndColor(ctx, "bioheadM.png", 0xFFFFFF);
        renderHeadCrestWithHeadTransform(ctx);

        // Animated max tail
        bindAndColor(ctx, "biotailmax.png", 0xFFFFFF);
        renderTailWithBodyTransform(ctx, bioTailMaxRoot, true);

        // White body overlay
        whiteColor();
        ctx.bindTexture(new ResourceLocation(textureDir() + "bio1M.png"));
        model.renderBody(0.0625F);

        // Wings
        bindAndColor(ctx, "biowingsP.png", 0xFFFFFF);
        renderWingsWithBodyTransform(ctx);

        // Eyes
        renderEyes(ctx, "bioeyesbaseS.png", "bioeyeleftS.png", "bioeyerightS.png");
    }

    private void renderUncontrolledMax(RaceRenderContext ctx) {
        ModelBipedDBC model = ctx.model;

        // Base body
        RenderPlayerJBRA.glColor3f(ctx.bodyCM);
        model.renderBody(0.0625F);

        bindAndColor(ctx, "bio2UM.png", 0xFFFFFF);
        model.renderBody(0.0625F);

        // Head crest
        bindAndColor(ctx, "bioheadUM.png", 0xFFFFFF);
        renderHeadCrestWithHeadTransform(ctx);

        // Animated max tail
        bindAndColor(ctx, "biotailUM.png", 0xFFFFFF);
        renderTailWithBodyTransform(ctx, bioTailMaxRoot, true);

        // Max tail overlay
        whiteColor();
        ctx.bindTexture(new ResourceLocation(textureDir() + "biotailmax2.png"));
        renderTailStaticWithBodyTransform(ctx, bioTailMaxRoot);

        bindAndColor(ctx, "bio1UM.png", 0xFFFFFF);
        model.renderBody(0.0625F);

        // Wings
        bindAndColor(ctx, "biowingsP.png", 0xFFFFFF);
        renderWingsWithBodyTransform(ctx);

        // Eyes
        renderEyes(ctx, "bioeyesbaseUM.png", "bioeyeleftUM.png", "bioeyerightUM.png");
    }

    // ── Eye rendering ──

    private void renderEyes(RaceRenderContext ctx, String baseTexture, String leftTexture, String rightTexture) {
        ModelBipedDBC model = ctx.model;

        whiteColor();
        ctx.bindTexture(new ResourceLocation(textureDir() + baseTexture));
        model.renderHairs(0.0625F, "EYEBASE");

        RenderPlayerJBRA.glColor3f(ctx.eyeC1);
        ctx.bindTexture(new ResourceLocation(textureDir() + leftTexture));
        model.renderHairs(0.0625F, "EYELEFT");

        RenderPlayerJBRA.glColor3f(ctx.eyeC2);
        ctx.bindTexture(new ResourceLocation(textureDir() + rightTexture));
        model.renderHairs(0.0625F, "EYERIGHT");
    }

    // ── First-person arm rendering ──

    private void renderArmBase(RaceRenderContext ctx, ModelBipedDBC model, int id) {
        bindAndColor(ctx, "bio1.png", ctx.bodyC1);
        renderArmPiece(model, id, ctx.entity instanceof EntityPlayer ? (EntityPlayer) ctx.entity : null);

        bindAndColor(ctx, "bio2.png", ctx.bodyC2);
        renderArmPiece(model, id, ctx.entity instanceof EntityPlayer ? (EntityPlayer) ctx.entity : null);

        bindAndColor(ctx, "bio3.png", ctx.bodyCM);
        renderArmPiece(model, id, ctx.entity instanceof EntityPlayer ? (EntityPlayer) ctx.entity : null);
    }

    private void renderArmSemiPerfect(RaceRenderContext ctx, ModelBipedDBC model, int id) {
        EntityPlayer player = ctx.entity instanceof EntityPlayer ? (EntityPlayer) ctx.entity : null;

        bindAndColor(ctx, "bio3S.png", ctx.bodyCM);
        renderArmPiece(model, id, player);

        bindAndColor(ctx, "bio1S.png", ctx.bodyC1);
        renderArmPiece(model, id, player);

        bindAndColor(ctx, "bio2S.png", ctx.bodyC2);
        renderArmPiece(model, id, player);

        whiteColor();
        ctx.bindTexture(new ResourceLocation(textureDir() + "bio4S.png"));
        renderArmPiece(model, id, player);
    }

    private void renderArmPerfect(RaceRenderContext ctx, ModelBipedDBC model, int id) {
        EntityPlayer player = ctx.entity instanceof EntityPlayer ? (EntityPlayer) ctx.entity : null;

        bindAndColor(ctx, "bio2P.png", ctx.bodyCM);
        renderArmPiece(model, id, player);

        whiteColor();
        ctx.bindTexture(new ResourceLocation(textureDir() + "bio1P.png"));
        renderArmPiece(model, id, player);
    }

    private void renderArmPerfectMax(RaceRenderContext ctx, ModelBipedDBC model, int id) {
        EntityPlayer player = ctx.entity instanceof EntityPlayer ? (EntityPlayer) ctx.entity : null;

        bindAndColor(ctx, "bio2M.png", ctx.bodyCM);
        renderArmPiece(model, id, player);

        whiteColor();
        ctx.bindTexture(new ResourceLocation(textureDir() + "bio1M.png"));
        renderArmPiece(model, id, player);
    }

    private void renderArmUncontrolledMax(RaceRenderContext ctx, ModelBipedDBC model, int id) {
        EntityPlayer player = ctx.entity instanceof EntityPlayer ? (EntityPlayer) ctx.entity : null;

        RenderPlayerJBRA.glColor3f(ctx.bodyCM);
        ctx.bindTexture(new ResourceLocation(textureDir() + "bio2UM.png"));
        renderArmPiece(model, id, player);

        whiteColor();
        ctx.bindTexture(new ResourceLocation(textureDir() + "bio1UM.png"));
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
