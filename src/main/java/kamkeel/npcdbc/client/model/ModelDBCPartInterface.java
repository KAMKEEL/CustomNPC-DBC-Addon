package kamkeel.npcdbc.client.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.model.ModelMPM;
import noppes.npcs.controllers.data.TintData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.data.ModelData;
import noppes.npcs.entity.data.ModelPartData;
import org.lwjgl.opengl.GL11;

public abstract class ModelDBCPartInterface extends ModelRenderer {
    public ModelData data;
    private EntityCustomNpc entity;
    public float scale = 1.0F;
    protected ResourceLocation location;
    public int useColor = 0; // CM, C1, C2, C3
    public int bodyCM = 16777215;
    public int bodyC1 = 16777215;
    public int bodyC2 = 16777215;
    public int bodyC3 = 16777215;
    public ModelMPM base;

    public ModelDBCPartInterface(ModelMPM par1ModelBase) {
        super(par1ModelBase);
        this.base = par1ModelBase;
        this.setTextureSize(0, 0);
    }

    public void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {
    }

    public void setLivingAnimations(ModelPartData data, EntityLivingBase entityliving, float f, float f1, float f2) {
    }

    public void setData(ModelData data, EntityCustomNpc entity) {
        this.data = data;
        this.entity = entity;
        this.initData(data);
    }

    public void render(float par1) {
        if (!this.base.isArmor) {
            if (this.location != null) {
                ClientProxy.bindTexture(this.location);
                this.base.currentlyPlayerTexture = false;
            } else if (!this.base.currentlyPlayerTexture) {
                ClientProxy.bindTexture(this.entity.textureLocation);
                this.base.currentlyPlayerTexture = true;
            }
        }

        TintData tintData = this.entity.display.tintData;
        boolean showColor = !this.base.isArmor && tintData.processColor(this.entity.hurtTime > 0 || this.entity.deathTime > 0);
        if (showColor) {
            int color = this.bodyCM;;
            switch (useColor){
                case 1:
                    color = this.bodyC1;
                    break;
                case 2:
                    color = this.bodyC2;
                    break;
                case 3:
                    color = this.bodyC3;
                    break;
            }

            float red = (float)(color >> 16 & 255) / 255.0F;
            float green = (float)(color >> 8 & 255) / 255.0F;
            float blue = (float)(color & 255) / 255.0F;
            GL11.glColor4f(red, green, blue, this.base.alpha);
        }

        super.render(par1);
        if (showColor) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, this.base.alpha);
        }
    }

    public abstract void initData(ModelData var1);
}
