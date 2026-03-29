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
public class BioAndroidCrestComponent implements IRaceModelComponent {
    private boolean initialized;
    private ModelBipedDBC cachedModel;

    private ModelRenderer bioheadRoot;
    private ModelRenderer biohead1, biohead2, biohead1I, biohead2I;

    @Override
    public void initialize(ModelBipedDBC model) {
        if (initialized && cachedModel == model) return;

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

        cachedModel = model;
        initialized = true;
    }

    @Override
    public void render(OverlayContext ctx, DisplayLayer layer) {
        ModelBipedDBC model = ctx.model;
        float f = 0.0625F;

        ctx.glColor(ctx.color);

        boolean baseForm = ctx.form() == null;
        biohead1I.isHidden = !baseForm;
        biohead2I.isHidden = !baseForm;
        biohead1.isHidden = baseForm;
        biohead2.isHidden = baseForm;

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

    private void setRotation(ModelRenderer m, float x, float y, float z) {
        m.rotateAngleX = x;
        m.rotateAngleY = y;
        m.rotateAngleZ = z;
    }
}
