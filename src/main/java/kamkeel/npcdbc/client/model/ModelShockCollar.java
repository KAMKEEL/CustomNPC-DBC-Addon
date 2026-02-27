package kamkeel.npcdbc.client.model;

import JinRyuu.JRMCore.entity.ModelBipedBody;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class ModelShockCollar extends ModelBipedBody {
    public static final ModelShockCollar COLLAR = new ModelShockCollar();

	private final ModelRenderer collar;

	public ModelShockCollar() {
		textureWidth = 32;
		textureHeight = 8;

		collar = new ModelRenderer(this);
		collar.setRotationPoint(0.0F, 0.0F, 0.0F);
		collar.cubeList.add(new ModelBox(collar, 0, 0, -4.0F, 0.0F, -2.0F, 8, 4, 4, 0.1F));
	}

	@Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        copyRotationData(collar, bipedBody);
        this.isSneak = entity.isSneaking();

        renderHeadpiece(f5);
    }

    private void copyRotationData(ModelRenderer model, ModelRenderer from) {
        model.rotationPointX = from.rotationPointX;
        model.rotationPointY = from.rotationPointY;
        model.rotationPointZ = from.rotationPointZ;

        rot(model, from);
    }

    private void renderHeadpiece(float scale) {
        float scalar;

        if (g <= 1) {
            scalar = f;
            GL11.glPushMatrix();
            GL11.glScalef(0.5F + 0.5F / scalar, 0.5F + 0.5F / scalar, 0.5F + 0.5F / scalar);
            GL11.glTranslatef(0.0F, (scalar - 1.0F) / scalar * (2.0F - (scalar >= 1.5F && scalar <= 2.0F ? (2.0F - scalar) / 2.5F : (scalar < 1.5F && scalar >= 1.0F ? (scalar * 2.0F - 2.0F) * 0.2F : 0.0F))), 0.0F);
            this.collar.render(scale);
            GL11.glPopMatrix();
        } else {
            scalar = f;
            GL11.glPushMatrix();
            GL11.glScalef((0.5F + 0.5F / scalar) * (g <= 1 ? 1.0F : 0.85F), 0.5F + 0.5F / scalar, (0.5F + 0.5F / scalar) * (g <= 1 ? 1.0F : 0.85F));
            GL11.glTranslatef(0.0F, (scalar - 1.0F) / scalar * (2.0F - (scalar >= 1.5F && scalar <= 2.0F ? (2.0F - scalar) / 2.5F : (scalar < 1.5F && scalar >= 1.0F ? (scalar * 2.0F - 2.0F) * 0.2F : 0.0F))), 0.0F);
            this.collar.render(scale);
            GL11.glPopMatrix();
        }
    }
}
