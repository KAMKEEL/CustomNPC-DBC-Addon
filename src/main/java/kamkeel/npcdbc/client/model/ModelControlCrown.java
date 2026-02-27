package kamkeel.npcdbc.client.model;

import JinRyuu.JRMCore.entity.ModelBipedBody;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class ModelControlCrown extends ModelBipedBody {
    public static final ModelControlCrown CROWN = new ModelControlCrown();

    private final ModelRenderer Head;

    private ModelControlCrown() {
        textureWidth = 32;
        textureHeight = 16;

        Head = new ModelRenderer(this);
        Head.setRotationPoint(0.0F, 0.0F, 0.0F);
        Head.cubeList.add(new ModelBox(Head, 0, 0, -4.0F, -7.0F, -4.0F, 8, 8, 8, 0.1F));
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        copyRotationData(Head, bipedHead);
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
            this.Head.render(scale);
            GL11.glPopMatrix();
        } else {
            scalar = f;
            GL11.glPushMatrix();
            GL11.glScalef((0.5F + 0.5F / scalar) * (g <= 1 ? 1.0F : 0.85F), 0.5F + 0.5F / scalar, (0.5F + 0.5F / scalar) * (g <= 1 ? 1.0F : 0.85F));
            GL11.glTranslatef(0.0F, (scalar - 1.0F) / scalar * (2.0F - (scalar >= 1.5F && scalar <= 2.0F ? (2.0F - scalar) / 2.5F : (scalar < 1.5F && scalar >= 1.0F ? (scalar * 2.0F - 2.0F) * 0.2F : 0.0F))), 0.0F);
            this.Head.render(scale);
            GL11.glPopMatrix();
        }
    }
}
