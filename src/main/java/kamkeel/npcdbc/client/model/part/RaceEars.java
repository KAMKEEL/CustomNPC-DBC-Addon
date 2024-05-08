package kamkeel.npcdbc.client.model.part;

import net.minecraft.client.model.ModelRenderer;
import noppes.npcs.client.model.ModelMPM;
import noppes.npcs.client.model.util.ModelPartInterface;
import noppes.npcs.entity.data.ModelData;
import noppes.npcs.entity.data.ModelPartData;

public class RaceEars extends ModelPartInterface {

    public ModelRenderer ArcoEars;
    public ModelRenderer ear1;
    public ModelRenderer ear2;

    public RaceEars(ModelMPM base) {
        super(base);

        this.ArcoEars = new ModelRenderer(base, 0, 0);
        this.ArcoEars.addBox(-0.0F, -0.0F, -0.0F, 0, 0, 0, 0.02F);
        this.ArcoEars.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.ear1 = new ModelRenderer(base, 12, 0);
        this.ear1.addBox(-5.0F, -5.0F, -3.0F, 1, 3, 2);
        this.ear1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.ear1, -0.4014257F, 0.0F, 0.0F);
        this.ear2 = new ModelRenderer(base, 12, 0);
        this.ear2.mirror = true;
        this.ear2.addBox(4.0F, -5.0F, -3.0F, 1, 3, 2);
        this.ear2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.ear2, -0.4014257F, 0.0F, 0.0F);
        this.ArcoEars.addChild(this.ear1);
        this.ArcoEars.addChild(this.ear2);

        this.addChild(ArcoEars);
    }

    @Override
    public void initData(ModelData modelData) {
        ModelPartData config = data.getPartData("dbcEars");
        if(config == null)
        {
            isHidden = true;
            return;
        }
        color = config.color;
        isHidden = false;

        ArcoEars.isHidden = config.type != 1;

        if(!config.playerTexture){
            location = config.getResource();
        }
        else
            location = null;
    }
}
