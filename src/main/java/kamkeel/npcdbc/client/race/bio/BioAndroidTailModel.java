package kamkeel.npcdbc.client.race.bio;

import JinRyuu.JBRA.mod_JBRA;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.race.IOverlayModel;
import kamkeel.npcdbc.data.overlay.OverlayContext;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class BioAndroidTailModel implements IOverlayModel {
    private boolean initialized;

    private ModelRenderer tailRoot;
    private final ModelRenderer[] seg = new ModelRenderer[6];

    private ModelRenderer tailRootMax;
    private final ModelRenderer[] segMax = new ModelRenderer[6];
    private ModelRenderer stingerMax;

    private static boolean isMax(String key) {
        return key != null && key.contains("max");
    }

    @Override
    public void initialize(OverlayContext ctx) {
        if (initialized)
            return;
        
        initMax(ctx.getComponentModel());
        initNormal(ctx.getComponentModel());
        initialized = true;
    }

    private void initNormal(ModelBiped model) {
        tailRoot = new ModelRenderer(model);
        tailRoot.addBox(0, 0, 0, 0, 12, 0, 0.02F);
        tailRoot.setRotationPoint(2, 10, 2);

        seg[0] = new ModelRenderer(model);
        seg[0].addBox(-2, -2, 0, 4, 4, 6);
        seg[0].setRotationPoint(-2, -2, 0);
        setRotation(seg[0], -(float) (Math.PI / 6), 0, 0);

        seg[1] = new ModelRenderer(model);
        seg[1].addBox(-2, -2, 0, 4, 4, 6);
        seg[1].setRotationPoint(0, 0, 5);
        setRotation(seg[1], (float) (Math.PI / 6), 8.727E-4F, 0);

        for (int i = 2; i <= 4; i++) {
            seg[i] = new ModelRenderer(model);
            seg[i].addBox(-2, -2, 0, 4, 4, 6);
            seg[i].setRotationPoint(0, 0, 5);
        }

        seg[5] = new ModelRenderer(model, 0, 10);
        seg[5].addBox(-1, -1.5F, -0.5F, 2, 2, 6);
        seg[5].setRotationPoint(0, 0, 4);

        chain(seg, tailRoot);
    }

    private void initMax(ModelBiped model) {
        tailRootMax = new ModelRenderer(model);
        tailRootMax.setRotationPoint(0, 5, 2);

        segMax[0] = new ModelRenderer(model);
        segMax[0].setRotationPoint(0, 0, 0);
        segMax[0].cubeList.add(new ModelBox(segMax[0], 0, 0, -2, -2, 0, 4, 4, 6, 0));

        for (int i = 1; i <= 5; i++) {
            segMax[i] = new ModelRenderer(model);
            segMax[i].setRotationPoint(0, 0, 5);
            segMax[i].cubeList.add(new ModelBox(segMax[i], 0, 0, -2, -2, 0, 4, 4, 6, 0));
        }

        stingerMax = new ModelRenderer(model, 0, 0);
        stingerMax.setRotationPoint(0, 0, 5);
        stingerMax.cubeList.add(new ModelBox(stingerMax, 0, 10, -3.5f, -3.5f, 0, 7, 7, 7, 0));

        segMax[5].addChild(stingerMax);
        chain(segMax, tailRootMax);
    }

    private static void chain(ModelRenderer[] seg, ModelRenderer root) {
        for (int i = seg.length - 2; i >= 0; i--)
            seg[i].addChild(seg[i + 1]);
        root.addChild(seg[0]);
    }

    @Override
    public void render(OverlayContext ctx) {
        String key = ctx.key();
        boolean max = isMax(key);
        boolean animate = !key.contains("static");

        GL11.glPushMatrix();
        float f6 = ctx.age();
        GL11.glScalef(1.0F / f6 * (ctx.gender() <= 1 ? 1.0F : 0.7F), 1.0F / f6, 1.0F / f6 * (ctx.gender() <= 1 ? 1.0F : 0.7F));
        GL11.glTranslatef(0, (f6 - 1.0F) * 1.5F, 0);
        transRot(SCALE, ctx.getBodyRenderer());

        if (animate) animate(ctx.getAnimationTick(), max);
        (max ? tailRootMax : tailRoot).render(SCALE);

        GL11.glPopMatrix();
    }

    private void animate(float tick, boolean max) {
        ModelRenderer[] seg = max ? segMax : this.seg;
        float r = MathHelper.sin(tick * 0.02F) * 0.1F;
        float r2 = MathHelper.cos(tick * 0.02F) * 0.1F;
        float r3 = MathHelper.cos(tick * 0.14F) * 0.1F;
        boolean anim = mod_JBRA.a6P9H9B;

        seg[0].rotateAngleY = 0.2F;
        if (anim) seg[0].rotateAngleY += MathHelper.cos(tick * 0.09F) * 0.2F - 0.2F + r;
        seg[0].rotateAngleX = -0.3F;

        seg[1].rotateAngleY = 0.2F;
        if (anim) seg[1].rotateAngleY += MathHelper.cos(tick * 0.09F) * 0.2F - 0.2F + r2 + r3;
        seg[1].rotateAngleX = 0.4F;

        seg[2].rotateAngleY = 0.1F;
        if (anim) seg[2].rotateAngleY += MathHelper.cos(tick * 0.09F) * 0.1F - 0.1F + r + r3;
        seg[2].rotateAngleX = 0.6F;
        if (anim) seg[2].rotateAngleX += MathHelper.sin(tick * 0.09F) * 0.4F + 0.3F;

        seg[3].rotateAngleY = 0.1F;
        if (anim) seg[3].rotateAngleY += MathHelper.cos(tick * 0.09F) * 0.4F - 0.1F + r2;
        seg[3].rotateAngleX = 0.3F;
        if (anim) seg[3].rotateAngleX += MathHelper.sin(tick * 0.09F) * 0.1F - 0.2F;

        seg[4].rotateAngleY = 0.2F;
        if (anim) seg[4].rotateAngleY += MathHelper.cos(tick * 0.09F) * 0.4F - 0.2F + r + r3;
        seg[4].rotateAngleX = -0.2F;
        if (anim) seg[4].rotateAngleX += MathHelper.sin(tick * 0.09F) * 0.1F - 0.3F;

        seg[5].rotateAngleY = 0.2F;
        if (anim) seg[5].rotateAngleY += MathHelper.cos(tick * 0.09F) * 0.4F - 0.2F + r + r3;
        seg[5].rotateAngleX = -0.2F;
        if (anim) seg[5].rotateAngleX += MathHelper.sin(tick * 0.09F) * 0.1F - 0.3F;

        if (max) {
            stingerMax.rotateAngleY = seg[5].rotateAngleY;
            stingerMax.rotateAngleX = seg[5].rotateAngleX;
        }
    }

    private void transRot(float f5, ModelRenderer m) {
        GL11.glTranslatef(m.rotationPointX * f5, m.rotationPointY * f5, m.rotationPointZ * f5);
        if (m.rotateAngleZ != 0) GL11.glRotatef(m.rotateAngleZ * (180.0F / (float) Math.PI), 0, 0, 1);
        if (m.rotateAngleY != 0) GL11.glRotatef(m.rotateAngleY * (180.0F / (float) Math.PI), 0, 1, 0);
        if (m.rotateAngleX != 0) GL11.glRotatef(m.rotateAngleX * (180.0F / (float) Math.PI), 1, 0, 0);
    }

    private void setRotation(ModelRenderer m, float x, float y, float z) {
        m.rotateAngleX = x;
        m.rotateAngleY = y;
        m.rotateAngleZ = z;
    }
}
