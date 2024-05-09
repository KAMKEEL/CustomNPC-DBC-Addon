package kamkeel.npcdbc.client.model.part;

import kamkeel.npcdbc.client.model.ModelDBCPartInterface;
import kamkeel.npcdbc.client.model.ModelNPCDBC;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixin.INPCDisplay;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.model.ModelMPM;
import noppes.npcs.entity.data.ModelData;
import noppes.npcs.entity.data.ModelPartData;

public class DBCRightArms extends ModelDBCPartInterface {

    // Arm Spikes
    public ModelRenderer RightArmSpike;
    public ModelRenderer rightArmSpikePart;

    // Shoulder Pads
    public ModelRenderer ArcoRightShoulder;

    public DBCRightArms(ModelMPM base) {
        super(base);
        textureWidth = 64;
        textureHeight = 32;

        this.ArcoRightShoulder = new ModelRenderer(base, 38, 0);
        this.ArcoRightShoulder.addBox(-6.0F, -3.0F, -3.0F, 7, 4, 6);
        this.ArcoRightShoulder.setRotationPoint(0, 0F, 0.0F);
        this.ArcoRightShoulder.setTextureSize(128, 64);
        this.addChild(ArcoRightShoulder);


        this.RightArmSpike = new ModelRenderer(base, 0, 0);
        this.RightArmSpike.addBox(-0.0F, -0.0F, -0.0F, 0, 0, 0, 0.02F);
        this.RightArmSpike.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rightArmSpikePart = new ModelRenderer(base, 0, 6);
        this.rightArmSpikePart.addBox(-6.0F, 1.0F, -1.0F, 1, 5, 2);
        this.rightArmSpikePart.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.rightArmSpikePart, 0.0F, 0.0F, (float) (-Math.PI / 6));
        this.RightArmSpike.addChild(rightArmSpikePart);
        this.addChild(RightArmSpike);
    }

    @Override
    public void render(float par1) {
        DBCDisplay display = ((INPCDisplay) entity.display).getDBCDisplay();
        this.ArcoRightShoulder.rotateAngleY = base.bipedRightArm.rotateAngleY;
        this.ArcoRightShoulder.rotateAngleX = base.bipedRightArm.rotateAngleX;
        this.ArcoRightShoulder.rotateAngleZ = base.bipedRightArm.rotateAngleZ;

        if (display.useSkin) {
            this.useColor = 0;
            location = new ResourceLocation("jinryuudragonbc:cc/arc/m/0B20.png");
            useColor = 0;
            bodyCM = ModelNPCDBC.getBodyColor(display, "bodycm", display.race);
            super.render(par1);

            location = new ResourceLocation("jinryuudragonbc:cc/arc/m/2B20.png");
            useColor = 2;
            bodyC2 = ModelNPCDBC.getBodyColor(display, "bodyc2", display.race);
            super.render(par1);

            useColor = 0;
        } else {
            super.render(par1);
        }
    }

    @Override
    public void initData(ModelData modelData) {
        ModelPartData config = data.getPartData("dbcArms");
        if (config == null) {
            isHidden = true;
            return;
        }
        useColor = 0;
        bodyCM = config.color;
        isHidden = false;

        RightArmSpike.isHidden = config.type != 1;
        ArcoRightShoulder.isHidden = config.type != 2;
        if (!config.playerTexture) {
            location = config.getResource();
        } else
            location = null;
    }
}
