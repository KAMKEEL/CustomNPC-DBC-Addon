package kamkeel.npcdbc.client.model.part;

import kamkeel.npcdbc.client.model.ModelDBCPartInterface;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixin.INPCDisplay;
import net.minecraft.client.model.ModelRenderer;
import noppes.npcs.client.model.ModelMPM;
import noppes.npcs.entity.data.ModelData;
import noppes.npcs.entity.data.ModelPartData;
import scala.util.Left;

public class DBCArms extends ModelDBCPartInterface {

    boolean rightSide;

    // Arm Spikes
    public ModelRenderer LeftArmSpike;
    public ModelRenderer RightArmSpike;
    public ModelRenderer rightArmSpikePart;
    public ModelRenderer leftArmSpikePart;

    // Shoulder Pads
    public ModelRenderer ArcoLeftShoulder;
    public ModelRenderer ArcoRightShoulder;

    public DBCArms(ModelMPM base, boolean rightSide) {
        super(base);
        textureHeight = 32;
        textureWidth = 64;
        this.rightSide = rightSide;

        if(rightSide){
            this.ArcoRightShoulder = new ModelRenderer(base, 38, 0);
            this.ArcoRightShoulder.addBox(-6.0F, -3.0F, -3.0F, 7, 4, 6);
            this.ArcoRightShoulder.setRotationPoint(-5.0F, 2.0F, 0.0F);
            this.ArcoRightShoulder.setTextureSize(128, 64);

            this.RightArmSpike = new ModelRenderer(base, 0, 0);
            this.RightArmSpike.addBox(-0.0F, -0.0F, -0.0F, 0, 0, 0, 0.02F);
            this.RightArmSpike.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.rightArmSpikePart = new ModelRenderer(base, 0, 6);
            this.rightArmSpikePart.addBox(-6.0F, 1.0F, -1.0F, 1, 5, 2);
            this.rightArmSpikePart.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.setRotation(this.rightArmSpikePart, 0.0F, 0.0F, (float) (-Math.PI / 6));
            this.RightArmSpike.addChild(rightArmSpikePart);

            this.addChild(ArcoRightShoulder);
            this.addChild(RightArmSpike);
        } else {
            this.ArcoLeftShoulder = new ModelRenderer(base, 38, 0);
            this.ArcoLeftShoulder.mirror = true;
            this.ArcoLeftShoulder.addBox(-1.0F, -3.0F, -3.0F, 7, 4, 6);
            this.ArcoLeftShoulder.setRotationPoint(5.0F, 2.0F, 0.0F);
            this.ArcoLeftShoulder.setTextureSize(128, 64);

            this.LeftArmSpike = new ModelRenderer(base, 0, 0);
            this.LeftArmSpike.addBox(-0.0F, -0.0F, -0.0F, 0, 0, 0, 0.02F);
            this.LeftArmSpike.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.leftArmSpikePart = new ModelRenderer(base, 0, 6);
            this.leftArmSpikePart.addBox(5.0F, 1.0F, -1.0F, 1, 5, 2);
            this.leftArmSpikePart.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.LeftArmSpike.addChild(leftArmSpikePart);

            this.addChild(LeftArmSpike);
            this.addChild(ArcoLeftShoulder);
        }
    }

    @Override
    public void render(float par1) {
        DBCDisplay display = ((INPCDisplay) entity.display).getDBCDisplay();
        if(display.useSkin){
            this.useColor = 0;
            this.bodyCM = display.getCurrentBodyColor("cm");
            super.render(par1);
        } else {
            super.render(par1);
        }
    }

    @Override
    public void initData(ModelData modelData) {
        ModelPartData config = data.getPartData("dbcArms");
        if(config == null)
        {
            isHidden = true;
            return;
        }
        bodyCM = config.color;
        isHidden = false;

        if(this.rightSide){
            RightArmSpike.isHidden = config.type != 1;
            ArcoRightShoulder.isHidden = config.type != 2;
        }
        else {
            LeftArmSpike.isHidden = config.type != 1;
            ArcoLeftShoulder.isHidden = config.type != 2;
        }

        if(!config.playerTexture){
            location = config.getResource();
        }
        else
            location = null;
    }
}
