package kamkeel.npcdbc.client.model;

import JinRyuu.JBRA.ModelRendererJBRA;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import noppes.npcs.client.model.ModelMPM;

public class ModelNPCDBC extends ModelBase {

    private ModelMPM parent;

    public ModelRendererJBRA[] hairall;

    public ModelNPCDBC(ModelMPM mpm){
        this.parent = mpm;
        this.textureHeight = 32;
        this.textureWidth = 64;

        // Init Hair
        this.hairall = new ModelRendererJBRA[224];
        int hossz;
        int face;
        for(hossz = 0; hossz < 4; ++hossz) {
            for(face = 0; face < 56; ++face) {
                if (this.hairall[hossz + face * 4] == null) {
                    this.hairall[hossz + face * 4] = new ModelRendererJBRA(this, 32, 0);
                    this.hairall[hossz + face * 4].addBox(-1.0F, hossz == 0 ? -1.0F : 0.0F, -1.0F, 2, 3, 2);
                    this.hairall[hossz + face * 4].setRotationPoint(0.0F, 0.0F, 0.0F);
                    this.setRotation(this.hairall[hossz + face * 4], 0.0F, 0.0F, 0.0F);
                }
            }
        }
        for(hossz = 0; hossz < 4; ++hossz) {
            for(face = 0; face < 56; ++face) {
                if (hossz != 3) {
                    this.hairall[hossz + face * 4].addChild(this.hairall[hossz + 1 + face * 4]);
                }
            }
        }
    }

    // Render Hairs V2 into NPC


    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    private void setRotation(ModelRendererJBRA model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
