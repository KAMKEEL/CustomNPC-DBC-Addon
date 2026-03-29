package kamkeel.npcdbc.client.race.bio;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.race.IRaceModelComponent;
import kamkeel.npcdbc.data.overlay.DisplayLayer;
import kamkeel.npcdbc.data.overlay.OverlayContext;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class BioAndroidWingsComponent implements IRaceModelComponent {
    private boolean initialized;

    private ModelRenderer wing, wing2;

    @Override
    public void initialize(OverlayContext ctx) {
        if (initialized)
            return;
        
        ModelBiped model = ctx.getComponentModel();

        wing = new ModelRenderer(model, 0, 0);
        wing.addBox(-1, 2, 2, 7, 20, 1);
        wing.setRotationPoint(0, 0, 0);
        setRotation(wing, 0.1570796F, 0.0349066F, -0.2792527F);

        wing2 = new ModelRenderer(model, 0, 0);
        wing2.mirror = true;
        wing2.addBox(-6, 2, 2, 7, 20, 1);
        wing2.setRotationPoint(0, 0, 0);
        setRotation(wing2, 0.1570796F, -0.0349066F, 0.2792527F);

        initialized = true;
    }

    @Override
    public void render(OverlayContext ctx, DisplayLayer layer) {
        float f = 0.0625F;

        ctx.glColor(ctx.color);

        GL11.glPushMatrix();
        float f6 = ctx.age();
        GL11.glScalef(1.0F / f6 * (ctx.gender() <= 1 ? 1.0F : 0.7F), 1.0F / f6,
                1.0F / f6 * (ctx.gender() <= 1 ? 1.0F : 0.7F));
        GL11.glTranslatef(0, (f6 - 1.0F) * 1.5F, 0);
        
        ModelRenderer body = ctx.getBipedBody();
        ModelRenderer leftArm = ctx.getBipedLeftArm();
        
        wing.rotateAngleY = Math.abs(leftArm.rotateAngleY / 7.0F) + body.rotateAngleY;
        wing.rotateAngleX = Math.abs(leftArm.rotateAngleX / 7.0F) + body.rotateAngleX;
        wing.rotationPointX = body.rotationPointX;
        wing.rotationPointY = body.rotationPointY;
        wing.render(f);

        wing2.rotateAngleY = Math.abs(leftArm.rotateAngleY / 7.0F) + body.rotateAngleY;
        wing2.rotateAngleX = Math.abs(leftArm.rotateAngleX / 7.0F) + body.rotateAngleX;
        wing2.rotationPointX = body.rotationPointX;
        wing2.rotationPointY = body.rotationPointY;
        wing2.render(f);

        GL11.glPopMatrix();
    }

    private void setRotation(ModelRenderer m, float x, float y, float z) {
        m.rotateAngleX = x;
        m.rotateAngleY = y;
        m.rotateAngleZ = z;
    }
}
