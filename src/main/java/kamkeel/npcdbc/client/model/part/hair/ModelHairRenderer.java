package kamkeel.npcdbc.client.model.part.hair;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import org.lwjgl.opengl.GL11;

public class ModelHairRenderer extends ModelRenderer {

    public float lengthY;
    public float sizeXZ;

    public ModelHairRenderer(ModelBase base, String name) {
        super(base, name);
        this.lengthY = 1.0F;
        this.sizeXZ = 1.0F;
    }

    public ModelHairRenderer(ModelBase base) {
        this(base, (String)null);
    }

    public ModelHairRenderer(ModelBase base, int p_i1174_2_, int p_i1174_3_) {
        this(base);
        this.setTextureOffset(p_i1174_2_, p_i1174_3_);
    }

    @SideOnly(Side.CLIENT)
    public void render(float p_78785_1_) {
        if (!this.isHidden && this.showModel) {
            if (!this.compiled) {
                this.compileDisplayList(p_78785_1_);
            }

            GL11.glTranslatef(this.offsetX, this.offsetY, this.offsetZ);
            int i;
            if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
                if (this.rotationPointX == 0.0F && this.rotationPointY == 0.0F && this.rotationPointZ == 0.0F) {
                    GL11.glCallList(this.displayList);
                    if (this.childModels != null) {
                        for(i = 0; i < this.childModels.size(); ++i) {
                            ((ModelHairRenderer)this.childModels.get(i)).render(p_78785_1_);
                        }
                    }
                } else {
                    GL11.glTranslatef(this.rotationPointX * p_78785_1_, this.rotationPointY * p_78785_1_, this.rotationPointZ * p_78785_1_);
                    GL11.glScalef(this.sizeXZ, this.lengthY, this.sizeXZ);
                    GL11.glCallList(this.displayList);
                    GL11.glScalef(1.0F / this.sizeXZ, 1.0F / this.lengthY, 1.0F / this.sizeXZ);
                    if (this.childModels != null) {
                        for(i = 0; i < this.childModels.size(); ++i) {
                            ((ModelHairRenderer)this.childModels.get(i)).render(p_78785_1_);
                        }
                    }

                    GL11.glTranslatef(-this.rotationPointX * p_78785_1_, -this.rotationPointY * p_78785_1_, -this.rotationPointZ * p_78785_1_);
                }
            } else {
                GL11.glPushMatrix();
                GL11.glTranslatef(this.rotationPointX * p_78785_1_, this.rotationPointY * p_78785_1_, this.rotationPointZ * p_78785_1_);
                if (this.rotateAngleZ != 0.0F) {
                    GL11.glRotatef(this.rotateAngleZ * 57.295776F, 0.0F, 0.0F, 1.0F);
                }

                if (this.rotateAngleY != 0.0F) {
                    GL11.glRotatef(this.rotateAngleY * 57.295776F, 0.0F, 1.0F, 0.0F);
                }

                if (this.rotateAngleX != 0.0F) {
                    GL11.glRotatef(this.rotateAngleX * 57.295776F, 1.0F, 0.0F, 0.0F);
                }

                GL11.glScalef(this.sizeXZ, this.lengthY, this.sizeXZ);
                GL11.glCallList(this.displayList);
                GL11.glScalef(1.0F / this.sizeXZ, 1.0F / this.lengthY, 1.0F / this.sizeXZ);
                GL11.glTranslatef(0.0F, this.lengthY * 0.15F - 0.15F, 0.0F);
                if (this.childModels != null) {
                    for(i = 0; i < this.childModels.size(); ++i) {
                        ((ModelHairRenderer)this.childModels.get(i)).render(p_78785_1_);
                    }
                }

                GL11.glPopMatrix();
            }

            GL11.glTranslatef(-this.offsetX, -this.offsetY, -this.offsetZ);
        }

    }
}
