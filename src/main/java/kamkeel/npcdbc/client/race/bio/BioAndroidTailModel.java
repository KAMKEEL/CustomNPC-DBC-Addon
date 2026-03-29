package kamkeel.npcdbc.client.race.bio;

import JinRyuu.JBRA.mod_JBRA;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.race.IOverlayModel;
import kamkeel.npcdbc.data.overlay.OverlayContext;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class BioAndroidTailModel implements IOverlayModel {
    private boolean initialized;

    private ModelRenderer bioTailRoot;
    private ModelRenderer btailS1, btailS2, btailS3, btailS4, btailS5, btailS6;

    @Override
    public void initialize(OverlayContext ctx) {
        if (initialized)
            return;

        ModelBiped model = ctx.getComponentModel();

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

        btailS5.addChild(btailS6);
        btailS4.addChild(btailS5);
        btailS3.addChild(btailS4);
        btailS2.addChild(btailS3);
        btailS1.addChild(btailS2);
        bioTailRoot.addChild(btailS1);

        bioTailRoot.rotationPointX = 2;
        bioTailRoot.rotationPointY = 10;
        bioTailRoot.rotationPointZ = 2;

        initialized = true;
    }

    @Override
    public void render(OverlayContext ctx) {
        boolean animate = !"tail_static".equals(ctx.key());

        GL11.glPushMatrix();
        float f6 = ctx.age();
        GL11.glScalef(1.0F / f6 * (ctx.gender() <= 1 ? 1.0F : 0.7F), 1.0F / f6, 1.0F / f6 * (ctx.gender() <= 1 ? 1.0F : 0.7F));
        GL11.glTranslatef(0, (f6 - 1.0F) * 1.5F, 0);
        transRot(SCALE, ctx.getBodyRenderer());
        if (animate) 
            animateNormalTail(ctx.getAnimationTick());
        
        bioTailRoot.render(SCALE);
        GL11.glPopMatrix();
    }

    private void animateNormalTail(float tick) {
        float r = MathHelper.sin(tick * 0.02F) * 0.1F;
        float r2 = MathHelper.cos(tick * 0.02F) * 0.1F;
        float r3 = MathHelper.cos(tick * 0.14F) * 0.1F;
        boolean anim = mod_JBRA.a6P9H9B;

        btailS1.rotateAngleY = 0.2F;
        if (anim) btailS1.rotateAngleY += MathHelper.cos(tick * 0.09F) * 0.2F - 0.2F + r;
        btailS1.rotateAngleX = -0.3F;

        btailS2.rotateAngleY = 0.2F;
        if (anim) btailS2.rotateAngleY += MathHelper.cos(tick * 0.09F) * 0.2F - 0.2F + r2 + r3;
        btailS2.rotateAngleX = 0.4F;

        btailS3.rotateAngleY = 0.1F;
        if (anim) btailS3.rotateAngleY += MathHelper.cos(tick * 0.09F) * 0.1F - 0.1F + r + r3;
        btailS3.rotateAngleX = 0.6F;
        if (anim) btailS3.rotateAngleX += MathHelper.sin(tick * 0.09F) * 0.4F + 0.3F;

        btailS4.rotateAngleY = 0.1F;
        if (anim) btailS4.rotateAngleY += MathHelper.cos(tick * 0.09F) * 0.4F - 0.1F + r2;
        btailS4.rotateAngleX = 0.3F;
        if (anim) btailS4.rotateAngleX += MathHelper.sin(tick * 0.09F) * 0.1F - 0.2F;

        btailS5.rotateAngleY = 0.2F;
        if (anim) btailS5.rotateAngleY += MathHelper.cos(tick * 0.09F) * 0.4F - 0.2F + r + r3;
        btailS5.rotateAngleX = -0.2F;
        if (anim) btailS5.rotateAngleX += MathHelper.sin(tick * 0.09F) * 0.1F - 0.3F;

        btailS6.rotateAngleY = 0.2F;
        if (anim) btailS6.rotateAngleY += MathHelper.cos(tick * 0.09F) * 0.4F - 0.2F + r + r3;
        btailS6.rotateAngleX = -0.2F;
        if (anim) btailS6.rotateAngleX += MathHelper.sin(tick * 0.09F) * 0.1F - 0.3F;
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

    private void setRotation(ModelRenderer m, float x, float y, float z) {
        m.rotateAngleX = x;
        m.rotateAngleY = y;
        m.rotateAngleZ = z;
    }
}
