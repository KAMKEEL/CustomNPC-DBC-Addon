package kamkeel.npcdbc.client.model.part;

import kamkeel.npcdbc.client.model.ModelDBCPartInterface;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormDisplay;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import net.minecraft.client.model.ModelRenderer;
import noppes.npcs.client.model.ModelMPM;
import noppes.npcs.entity.data.ModelData;
import noppes.npcs.entity.data.ModelPartData;

public class DBCEars extends ModelDBCPartInterface {

    public ModelRenderer ArcoEars;
    public ModelRenderer ear1;
    public ModelRenderer ear2;

    public DBCEars(ModelMPM base) {
        super(base);
        textureHeight = 32;
        textureWidth = 64;

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
    public void render(float par1) {
        DBCDisplay display = ((INPCDisplay) entity.display).getDBCDisplay();
        if(display.useSkin){
            this.useColor = 0;
            bodyCM = display.bodyCM;
            //////////////////////////////////////////////////////
            //////////////////////////////////////////////////////
            //Forms
            Form form = display.getForm();
            if (form != null) {
                FormDisplay d = form.display;
                if (d.hasColor("bodycm"))
                    bodyCM = d.bodyCM;
            }
            //////////////////////////////////////////////////////
            //////////////////////////////////////////////////////

            super.render(par1);
        } else {
            super.render(par1);
        }
    }

    @Override
    public void initData(ModelData modelData) {
        ModelPartData config = data.getPartData("dbcEars");
        if(config == null)
        {
            isHidden = true;
            return;
        }
        bodyCM = config.color;
        isHidden = false;

        ArcoEars.isHidden = config.type != 1;

        if(!config.playerTexture){
            location = config.getResource();
        }
        else
            location = null;
    }
}
