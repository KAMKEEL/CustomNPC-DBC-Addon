package kamkeel.npcdbc.items;


import JinRyuu.JRMCore.entity.ModelBipedBody;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class PotaraModel extends ModelBipedBody {
    ModelRenderer earring = new ModelRenderer(this, "earring");

    public PotaraModel(){

        earring.setTextureOffset(0, 0);
        earring.setTextureSize(16, 16);
        earring.mirror = true;
        earring.addBox(3.75f, -4, 0, 1, 1, 1);
        //earring.addBox(0, 0, 0, 1, 2, 1);
        bipedHead.addChild(earring);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5){
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);

        //@TODO Add OpenGL stuff maybe idfk??? I suck at it
        //I need my OpenGL Tutorial arc to start tbh.
        this.earring.rotateAngleY = this.bipedHead.rotateAngleY;
        this.earring.rotateAngleX = this.bipedHead.rotateAngleX;
        this.earring.rotateAngleZ = this.bipedHead.rotateAngleZ;

        this.earring.render(f5);


    }
}
