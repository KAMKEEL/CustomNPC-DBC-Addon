package kamkeel.npcdbc.client.race.bio;

import JinRyuu.JBRA.ModelBipedDBC;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.race.IRaceModelComponent;
import kamkeel.npcdbc.data.overlay.DisplayLayer;
import kamkeel.npcdbc.data.overlay.OverlayContext;
import net.minecraft.client.model.ModelRenderer;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class BioAndroidWingsComponent implements IRaceModelComponent {
    private boolean initialized;
    private ModelBipedDBC cachedModel;

    private ModelRenderer wing, wing2;

    @Override
    public void initialize(ModelBipedDBC model) {
        if (initialized && cachedModel == model) return;

        wing = new ModelRenderer(model, 0, 0);
        wing.addBox(-1, 2, 2, 7, 20, 1);
        wing.setRotationPoint(0, 0, 0);
        setRotation(wing, 0.1570796F, 0.0349066F, -0.2792527F);

        wing2 = new ModelRenderer(model, 0, 0);
        wing2.mirror = true;
        wing2.addBox(-6, 2, 2, 7, 20, 1);
        wing2.setRotationPoint(0, 0, 0);
        setRotation(wing2, 0.1570796F, -0.0349066F, 0.2792527F);

        cachedModel = model;
        initialized = true;
    }

    @Override
    public void render(OverlayContext ctx, DisplayLayer layer) {
        ModelBipedDBC model = ctx.model;
        float f = 0.0625F;

        ctx.glColor(ctx.color);

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

    private void setRotation(ModelRenderer m, float x, float y, float z) {
        m.rotateAngleX = x;
        m.rotateAngleY = y;
        m.rotateAngleZ = z;
    }
}
