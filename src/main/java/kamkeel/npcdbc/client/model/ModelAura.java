package kamkeel.npcdbc.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class ModelAura extends ModelBase {
    public ModelRenderer auraModel;

    public ModelAura() {
        textureWidth = 64;
        textureHeight = 32;
        auraModel = new ModelRenderer(this, 0, 0);
        auraModel.addBox(-10.0F, -17.0F, -8.0F, 20, 20, 0);
        auraModel.setRotationPoint(0.0F, 20.0F, 0.0F);
    }

    public void renderModel(Entity entity, float age, float rotX, float speed) {
        this.render(entity, speed, rotX, age, 0.0F, 0.0F, 0.0625f);
    }

    public void render(Entity entity, float speed, float rotX, float age, float f3, float f4, float f5) {
        GL11.glPushMatrix();
        this.setRotationAngles(speed, rotX, age);
        this.auraModel.render(f5);
        GL11.glPopMatrix();
    }

    public void setRotationAngles(float speed, float rotX, float age) {
        float ageTemp = age;
        if (age > speed) {
            ageTemp = age - speed;
        }

        float maxageperc = 100.0F / speed;
        float curperc = ageTemp * maxageperc * 0.01F; // perc from 0 to 1
        ageTemp = curperc * 20.0F; //1 to 20

        //  this.auraModel.rotationPointY = 55.0F + ageTemp;

        this.auraModel.offsetY = -ageTemp * 0.1F; // from 0 to -1.64 to -4(max)

        //from  -0.4  (widest)  to 0.4
        this.auraModel.offsetZ = ageTemp < 8.0F ? 0.3F - ageTemp * 0.1F : -0.5F + (ageTemp - 7.0F) * 0.053F;
        // if (curperc > 0.5)
        //GL11.glScalef(1 - curperc ,1, 1- curperc );
        this.auraModel.rotateAngleX = (0.8726646F - curperc * 0.01F - rotX) * (1 - ((float) Math.pow(curperc, 2)));
    }
}
