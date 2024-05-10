package kamkeel.npcdbc.client.model.part;

import kamkeel.npcdbc.client.model.ModelDBCPartInterface;
import kamkeel.npcdbc.client.model.ModelDBC;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixin.INPCDisplay;
import net.minecraft.client.model.ModelRenderer;
import noppes.npcs.client.model.ModelMPM;
import noppes.npcs.entity.data.ModelData;
import noppes.npcs.entity.data.ModelPartData;

public class DBCBody extends ModelDBCPartInterface {

    // Back Spikes
    public ModelRenderer BackSpikes;
    public ModelRenderer backSpike1;
    public ModelRenderer backSpike2;

    public DBCBody(ModelMPM base) {
        super(base);
        textureHeight = 32;
        textureWidth = 64;

        this.BackSpikes = new ModelRenderer(base, 0, 0);
        this.BackSpikes.addBox(-0.0F, -0.0F, -0.0F, 0, 0, 0, 0.02F);
        this.BackSpikes.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.backSpike1 = new ModelRenderer(base, 8, 38);
        this.backSpike1.addBox(2.0F, -4.0F, 3.0F, 2, 6, 2);
        this.backSpike1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.backSpike1, -0.9773844F, 0.0F, 0.2094395F);
        this.backSpike2 = new ModelRenderer(base, 8, 38);
        this.backSpike2.addBox(-4.0F, -4.0F, 3.0F, 2, 6, 2);
        this.backSpike2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.backSpike2, -0.9773844F, 0.0F, -0.2094395F);
        this.BackSpikes.addChild(this.backSpike1);
        this.BackSpikes.addChild(this.backSpike2);
        this.addChild(BackSpikes);
    }

    @Override
    public void render(float par1) {
        DBCDisplay display = ((INPCDisplay) entity.display).getDBCDisplay();
        if(display.useSkin){
            this.useColor = 0;
            this.bodyCM = display.bodyCM;
            super.render(par1);
        } else {
            super.render(par1);
        }
    }

    @Override
    public void initData(ModelData modelData) {
        ModelPartData config = data.getPartData("dbcBody");
        if(config == null)
        {
            isHidden = true;
            return;
        }
        bodyCM = config.color;
        isHidden = false;

        BackSpikes.isHidden = config.type != 1;

        if(!config.playerTexture){
            location = config.getResource();
        }
        else
            location = null;
    }
}
