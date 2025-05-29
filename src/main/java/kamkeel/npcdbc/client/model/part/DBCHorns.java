package kamkeel.npcdbc.client.model.part;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.client.ClientConstants;
import kamkeel.npcdbc.client.model.ModelDBCPartInterface;
import kamkeel.npcdbc.client.render.RenderEventHandler;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormDisplay;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.model.ModelMPM;
import noppes.npcs.entity.data.ModelData;
import noppes.npcs.entity.data.ModelPartData;
import noppes.npcs.util.ValueUtil;
import org.lwjgl.opengl.GL11;

import static kamkeel.npcdbc.client.render.RenderEventHandler.disableStencilWriting;

public class DBCHorns extends ModelDBCPartInterface {
    // First Form
    public ModelRenderer FirstFormSpikes;
    public ModelRenderer spikePairOne;
    public ModelRenderer spikePairTwo;

    // Second Form
    public ModelRenderer SecondFormSpikes;
    public ModelRenderer spikePairEOne;
    public ModelRenderer spikePairETwo;

    public ModelRenderer ThirdFormBigHead;
    public ModelRenderer bigHead;
    public ModelRenderer bigHeadSpike;
    public ModelRenderer bigHeadSpike2;


    // Fifth Form
    public ModelRenderer CoolerHeadSpikes;
    public ModelRenderer CSpike1;
    public ModelRenderer CSpike2;
    public ModelRenderer CSpike3;
    public ModelRenderer CSpike4;
    public ModelRenderer CSpike5;

    // Namekian Horn
    public ModelRenderer NamekianAntennas;
    public ModelRenderer ant1;
    public ModelRenderer ant2;
    public ModelRenderer ant3;
    public ModelRenderer ant4;

    public DBCHorns(ModelMPM base) {
        super(base);
        textureHeight = 32;
        textureWidth = 64;

        // First Form
        this.FirstFormSpikes = new ModelRenderer(base, 0, 0);
        this.FirstFormSpikes.addBox(-0.0F, -0.0F, -0.0F, 0, 0, 0, 0.02F);
        this.FirstFormSpikes.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.spikePairOne = new ModelRenderer(base, 8, 6);
        this.spikePairOne.addBox(1.5F, -11.0F, -3.5F, 2, 4, 2);
        this.spikePairOne.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.spikePairOne, 0.0F, 0.0F, (float) (-Math.PI / 4));
        this.spikePairTwo = new ModelRenderer(base, 8, 6);
        this.spikePairTwo.addBox(-3.5F, -11.0F, -3.5F, 2, 4, 2);
        this.spikePairTwo.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.spikePairTwo, 0.0F, 0.0F, (float) (Math.PI / 4));
        this.FirstFormSpikes.addChild(this.spikePairOne);
        this.FirstFormSpikes.addChild(this.spikePairTwo);

        // Second Form = First Form + Spike Pair Extended
        this.SecondFormSpikes = new ModelRenderer(base, 0, 0);
        this.SecondFormSpikes.addBox(-0.0F, -0.0F, -0.0F, 0, 0, 0, 0.02F);
        this.SecondFormSpikes.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.spikePairEOne = new ModelRenderer(base, 8, 6);
        this.spikePairEOne.addBox(2.5F, -14.0F, -3.5F, 2, 4, 2);
        this.spikePairEOne.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.spikePairEOne, 0.0F, 0.0F, 0.2094395F);
        this.spikePairETwo = new ModelRenderer(base, 8, 6);
        this.spikePairETwo.addBox(-4.5F, -14.0F, -3.5F, 2, 4, 2);
        this.spikePairETwo.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.spikePairETwo, 0.0F, 0.0F, -0.2094395F);
        this.SecondFormSpikes.addChild(this.spikePairEOne);
        this.SecondFormSpikes.addChild(this.spikePairETwo);

        // Big Head Spikes
        this.ThirdFormBigHead = new ModelRenderer(base, 0, 0);
        this.ThirdFormBigHead.addBox(-0.0F, -0.0F, -0.0F, 0, 0, 0, 0.02F);
        this.ThirdFormBigHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bigHead = new ModelRenderer(base, 0, 16);
        this.bigHead.addBox(-4.0F, -8.0F, 4.0F, 8, 8, 8);
        this.bigHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.bigHead, 0.0F, 0.0F, 0.0F);
        this.bigHeadSpike = new ModelRenderer(base, 16, 6);
        this.bigHeadSpike.addBox(-3.5F, -11.0F, 6.5F, 2, 4, 2);
        this.bigHeadSpike.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.bigHeadSpike, 0.0F, 0.0F, (float) (Math.PI / 4));
        this.bigHeadSpike2 = new ModelRenderer(base, 16, 6);
        this.bigHeadSpike2.addBox(1.5F, -11.0F, 6.5F, 2, 4, 2);
        this.bigHeadSpike2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.bigHeadSpike2, 0.0F, 0.0F, (float) (-Math.PI / 4));
        this.ThirdFormBigHead.addChild(this.bigHead);
        this.ThirdFormBigHead.addChild(this.bigHeadSpike);
        this.ThirdFormBigHead.addChild(this.bigHeadSpike2);


        // Fifth Form Cooler Head Spikes
        this.CoolerHeadSpikes = new ModelRenderer(base, 0, 0);
        this.CoolerHeadSpikes.addBox(-0.0F, -0.0F, -0.0F, 0, 0, 0, 0.02F);
        this.CoolerHeadSpikes.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.CSpike1 = new ModelRenderer(base, 8, 6);
        this.CSpike1.addBox(-4.5F, -8.0F, -6.5F, 2, 6, 2);
        this.CSpike1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.CSpike1, (float) (-Math.PI * 2.0 / 9.0), 0.0F, 1.047198F);
        this.CSpike2 = new ModelRenderer(base, 8, 6);
        this.CSpike2.addBox(2.5F, -8.0F, -6.5F, 2, 6, 2);
        this.CSpike2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.CSpike2, (float) (-Math.PI * 2.0 / 9.0), 0.0F, -1.047198F);
        this.CSpike3 = new ModelRenderer(base, 8, 6);
        this.CSpike3.addBox(-0.5F, -10.0F, -8.0F, 2, 6, 2);
        this.CSpike3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.CSpike3, (float) (-Math.PI * 2.0 / 9.0), 0.0F, 0.2094395F);
        this.CSpike4 = new ModelRenderer(base, 8, 6);
        this.CSpike4.addBox(-1.5F, -10.0F, -8.0F, 2, 6, 2);
        this.CSpike4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.CSpike4, (float) (-Math.PI * 2.0 / 9.0), 0.0F, -0.2094395F);
        this.CSpike5 = new ModelRenderer(base, 8, 6);
        this.CSpike5.addBox(-2.5F, -7.0F, -7.2F, 5, 2, 2);
        this.CSpike5.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.CSpike5, (float) (-Math.PI / 6), 0.0F, 0.0F);
        this.CoolerHeadSpikes.addChild(this.CSpike1);
        this.CoolerHeadSpikes.addChild(this.CSpike2);
        this.CoolerHeadSpikes.addChild(this.CSpike3);
        this.CoolerHeadSpikes.addChild(this.CSpike4);
        this.CoolerHeadSpikes.addChild(this.CSpike5);


        // Namekian Antenna
        (this.NamekianAntennas = new ModelRenderer(base, 0, 0)).addBox(-0.0f, -0.0f, -0.0f, 0, 0, 0, 0.02f);
        this.NamekianAntennas.setRotationPoint(0.0f, 0.0f, 0.0f);
        (this.ant1 = new ModelRenderer(base, 24, 4)).addBox(0.0f, -5.0f, -8.0f, 1, 1, 2);
        this.ant1.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.setRotation(this.ant1, -0.3490659f, -0.4363323f, 0.0f);
        (this.ant2 = new ModelRenderer(base, 24, 4)).addBox(0.0f, -8.533334f, -6.2f, 1, 1, 2);
        this.ant2.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.setRotation(this.ant2, 0.2094395f, -0.4364196f, 0.0f);
        (this.ant3 = new ModelRenderer(base, 24, 4)).addBox(-1.0f, -5.0f, -8.0f, 1, 1, 2);
        this.ant3.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.setRotation(this.ant3, -0.3490659f, 0.4363323f, 0.0f);
        (this.ant4 = new ModelRenderer(base, 24, 4)).addBox(-1.0f, -8.533334f, -6.2f, 1, 1, 2);
        this.ant4.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.setRotation(this.ant4, 0.2094395f, 0.4364196f, 0.0f);
        this.NamekianAntennas.addChild(this.ant1);
        this.NamekianAntennas.addChild(this.ant2);
        this.NamekianAntennas.addChild(this.ant3);
        this.NamekianAntennas.addChild(this.ant4);

        this.addChild(FirstFormSpikes);
        this.addChild(SecondFormSpikes);
        this.addChild(ThirdFormBigHead);
        this.addChild(CoolerHeadSpikes);
        this.addChild(NamekianAntennas);
    }

    @Override
    public void render(float par1) {
        DBCDisplay display = ((INPCDisplay) entity.display).getDBCDisplay();
        if (!display.enabled)
            return;

        if (!ClientConstants.renderingOutline && display.outlineID != -1) {
            int id = !ThirdFormBigHead.isHidden ? 0 : RenderEventHandler.TAIL_STENCIL_ID;
            RenderEventHandler.enableStencilWriting((entity.getEntityId() + id) % 256);
        }

        boolean isArco = display.race == DBCRace.ARCOSIAN;
        GL11.glPushMatrix();
        if (ClientConstants.renderingOutline) {
            if (!NamekianAntennas.isHidden) {
                GL11.glTranslatef(0.00f, 0.07f, 0.023f);
                GL11.glScaled(0.96, 1.12, 1.02);
            } else {
                GL11.glTranslatef(0.00f, 0.01f, 0.015f);
                GL11.glScaled(1.03, 0.99, 1.03);
            }
            int id = !ThirdFormBigHead.isHidden ? 0 : RenderEventHandler.TAIL_STENCIL_ID;
            disableStencilWriting((entity.getEntityId() + id) % 256, false);
        }
        if (display.useSkin) {
            int state = ValueUtil.clamp(display.getCurrentArcoState(), 0, 7);
            if (!isArco)
                state = 3;


            bodyCM = display.bodyCM;
            bodyC1 = display.bodyC1;
            bodyC2 = display.bodyC2;
            bodyC3 = display.bodyC3;
            //////////////////////////////////////////////////////
            //////////////////////////////////////////////////////
            //Forms
            Form form = display.getForm();
            if (form != null) {
                FormDisplay d = form.display;
                if (d.hasColor("bodycm"))
                    bodyCM = d.bodyColors.bodyCM;
                if (d.hasColor("bodyc1"))
                    bodyC1 = d.bodyColors.bodyC1;
                if (d.hasColor("bodyc2"))
                    bodyC2 = d.bodyColors.bodyC2;
                if (d.hasColor("bodyc3"))
                    bodyC3 = d.bodyColors.bodyC3;

                if (isArco) {
                    if (form.display.bodyType.equals("firstform")) {
                        state = 0;
                    } else if (form.display.bodyType.equals("secondform")) {
                        state = 2;
                    } else if (form.display.bodyType.equals("thirdform")) {
                        state = 3;
                    } else if (form.display.bodyType.equals("finalform") || form.display.bodyType.equals("golden")) {
                        state = 4;
                    } else if (form.display.bodyType.equals("ultimatecooler")) {
                        state = 5;
                    }
                }
            }
            //////////////////////////////////////////////////////
            //////////////////////////////////////////////////////
            if (!NamekianAntennas.isHidden) {
                useColor = 0;
                super.render(par1);
            } else {
                useColor = 0;
                location = new ResourceLocation("jinryuudragonbc:cc/arc/m/0B" + JRMCoreH.TransFrSkn2[state] + display.bodyType + ".png");
                super.render(par1);

                useColor = 1;
                location = new ResourceLocation("jinryuudragonbc:cc/arc/m/1B" + JRMCoreH.TransFrSkn2[state] + display.bodyType + ".png");
                super.render(par1);

                useColor = 2;
                location = new ResourceLocation("jinryuudragonbc:cc/arc/m/2B" + JRMCoreH.TransFrSkn2[state] + display.bodyType + ".png");
                super.render(par1);

                useColor = 3;
                location = new ResourceLocation("jinryuudragonbc:cc/arc/m/3B" + JRMCoreH.TransFrSkn2[state] + display.bodyType + ".png");
                super.render(par1);

                useColor = 0;
                location = new ResourceLocation("jinryuudragonbc:cc/arc/m/4B" + JRMCoreH.TransFrSkn2[state] + display.bodyType + ".png");
                super.render(par1);

            }
            if (!ClientConstants.renderingOutline && display.outlineID != -1)
                RenderEventHandler.enableStencilWriting(entity.getEntityId() % 256);

            if (ClientConstants.renderingOutline) {
                disableStencilWriting((entity.getEntityId()) % 256, false);
            }
        } else {
            super.render(par1);
        }
        GL11.glPopMatrix();
    }

    @Override
    public void initData(ModelData modelData, DBCDisplay display) {
        ModelPartData config = data.getPartData("dbcHorn");
        if (config == null) {
            isHidden = true;
            return;
        }
        bodyCM = config.color;
        isHidden = false;

        //////////////////////////////////////////////////////
        //////////////////////////////////////////////////////
        //Forms
        Form form = display.getForm();
        if (form != null) {
            if (display.race == DBCRace.ARCOSIAN) {
                switch (form.display.bodyType) {
                    case "firstform":
                        config.type = 2;
                        break;
                    case "secondform":
                        config.type = 3;
                        break;
                    case "thirdform":
                        config.type = 4;
                        break;
                    case "finalform":
                    case "golden":
                        config.type = 0;
                        break;
                    case "ultimatecooler":
                        config.type = 5;
                        break;
                }
            }
        }
        //////////////////////////////////////////////////////
        //////////////////////////////////////////////////////

        NamekianAntennas.isHidden = config.type != 1;
        FirstFormSpikes.isHidden = config.type != 2 && config.type != 3 && config.type != 4;
        SecondFormSpikes.isHidden = config.type != 3 && config.type != 4;
        ThirdFormBigHead.isHidden = config.type != 4;
        CoolerHeadSpikes.isHidden = config.type != 5;

        if (!config.playerTexture) {
            location = config.getResource();
        } else
            location = null;
    }
}
