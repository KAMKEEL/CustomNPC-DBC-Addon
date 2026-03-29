package kamkeel.npcdbc.client.race.bio;

import net.minecraft.client.model.ModelBiped;
import JinRyuu.JBRA.mod_JBRA;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.race.IOverlayModel;
import kamkeel.npcdbc.data.overlay.DisplayLayer;
import kamkeel.npcdbc.data.overlay.OverlayContext;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class BioAndroidMaxTailModel implements IOverlayModel {
    private boolean initialized;

    private ModelRenderer bioTailMaxRoot;
    private ModelRenderer btailS1M, btailS2M, btailS3M, btailS4M, btailS5M, btailS6M, btailS7M;

    @Override
    public void initialize(OverlayContext ctx) {
        if (initialized ) return;
        
        ModelBiped model = ctx.getComponentModel();

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

        btailS6M.addChild(btailS7M);
        btailS5M.addChild(btailS6M);
        btailS4M.addChild(btailS5M);
        btailS3M.addChild(btailS4M);
        btailS2M.addChild(btailS3M);
        btailS1M.addChild(btailS2M);
        bioTailMaxRoot.addChild(btailS1M);

        initialized = true;
    }

    @Override
    public void render(OverlayContext ctx, DisplayLayer layer) {
        float f = 0.0625F;
        boolean animate = !"tail_static".equals(layer.slotId);

        ctx.glColor(ctx.color);

        GL11.glPushMatrix();
        float f6 = ctx.age();
        GL11.glScalef(1.0F / f6 * (ctx.gender() <= 1 ? 1.0F : 0.7F), 1.0F / f6, 1.0F / f6 * (ctx.gender() <= 1 ? 1.0F : 0.7F));
        GL11.glTranslatef(0, (f6 - 1.0F) * 1.5F, 0);
        transRot(f, ctx.getBodyRenderer());
        if (animate) {
            animateMaxTail(ctx.getAnimationTick());
        }
        bioTailMaxRoot.render(f);
        GL11.glPopMatrix();
    }

    private void animateMaxTail(float tick) {
        float r = MathHelper.sin(tick * 0.02F) * 0.1F;
        float r2 = MathHelper.cos(tick * 0.02F) * 0.1F;
        float r3 = MathHelper.cos(tick * 0.14F) * 0.1F;
        boolean anim = mod_JBRA.a6P9H9B;

        btailS1M.rotateAngleY = 0.2F;
        if (anim) btailS1M.rotateAngleY += MathHelper.cos(tick * 0.09F) * 0.2F - 0.2F + r;
        btailS1M.rotateAngleX = -0.3F;

        btailS2M.rotateAngleY = 0.2F;
        if (anim) btailS2M.rotateAngleY += MathHelper.cos(tick * 0.09F) * 0.2F - 0.2F + r2 + r3;
        btailS2M.rotateAngleX = 0.4F;

        btailS3M.rotateAngleY = 0.1F;
        if (anim) btailS3M.rotateAngleY += MathHelper.cos(tick * 0.09F) * 0.1F - 0.1F + r + r3;
        btailS3M.rotateAngleX = 0.6F;
        if (anim) btailS3M.rotateAngleX += MathHelper.sin(tick * 0.09F) * 0.4F + 0.3F;

        btailS4M.rotateAngleY = 0.1F;
        if (anim) btailS4M.rotateAngleY += MathHelper.cos(tick * 0.09F) * 0.4F - 0.1F + r2;
        btailS4M.rotateAngleX = 0.3F;
        if (anim) btailS4M.rotateAngleX += MathHelper.sin(tick * 0.09F) * 0.1F - 0.2F;

        btailS5M.rotateAngleY = 0.2F;
        if (anim) btailS5M.rotateAngleY += MathHelper.cos(tick * 0.09F) * 0.4F - 0.2F + r + r3;
        btailS5M.rotateAngleX = -0.2F;
        if (anim) btailS5M.rotateAngleX += MathHelper.sin(tick * 0.09F) * 0.1F - 0.3F;

        btailS6M.rotateAngleY = 0.2F;
        if (anim) btailS6M.rotateAngleY += MathHelper.cos(tick * 0.09F) * 0.4F - 0.2F + r + r3;
        btailS6M.rotateAngleX = -0.2F;
        if (anim) btailS6M.rotateAngleX += MathHelper.sin(tick * 0.09F) * 0.1F - 0.3F;

        btailS7M.rotateAngleY = btailS6M.rotateAngleY;
        btailS7M.rotateAngleX = btailS6M.rotateAngleX;
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
}
