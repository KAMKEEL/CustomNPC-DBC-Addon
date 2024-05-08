package kamkeel.npcdbc.client.model;

import JinRyuu.JBRA.ModelRendererJBRA;
import JinRyuu.JBRA.RenderPlayerJBRA;
import JinRyuu.JBRA.mod_JBRA;
import JinRyuu.JRMCore.JRMCoreClient;
import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.client.model.part.ArcoHorns;
import kamkeel.npcdbc.client.model.part.RaceEars;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.model.ModelMPM;
import noppes.npcs.entity.EntityCustomNpc;
import org.lwjgl.opengl.GL11;

public class ModelNPCDBC extends ModelBase {

    private final ModelMPM parent;
    public ModelRendererJBRA[] hairall;
    public float rot1;
    public float rot2;
    public float rot3;
    public float rot4;
    public float rot5;
    public float rot6;

    public ArcoHorns arcoHorns;
    public RaceEars raceEars;

    public ModelRenderer nose;
    public ModelRenderer mouth;
    public ModelRenderer eyeleft;
    public ModelRenderer eyeright;
    public ModelRenderer eyebase;
    public ModelRenderer eyebrow;

    public ModelRenderer BackSpikes;
    public ModelRenderer LeftArmSpike;
    public ModelRenderer RightArmSpike;

    public ModelRenderer F5spike1;
    public ModelRenderer F5spike2;
    public ModelRenderer F5spike3;
    public ModelRenderer F5spike4;

    public ModelRenderer ArcoLeftShoulder;
    public ModelRenderer ArcoRightShoulder;

    public int tempState, stateChange, state2Change, auraTime, auraType, bendTime;

    public ModelNPCDBC(ModelMPM mpm) {
        this.parent = mpm;
        this.textureHeight = 32;
        this.textureWidth = 64;
        // Init Hair
        this.hairall = new ModelRendererJBRA[224];
        int hossz;
        int face;
        for (hossz = 0; hossz < 4; ++hossz) {
            for (face = 0; face < 56; ++face) {
                if (this.hairall[hossz + face * 4] == null) {
                    this.hairall[hossz + face * 4] = new ModelRendererJBRA(this, 32, 0);
                    this.hairall[hossz + face * 4].addBox(-1.0F, hossz == 0 ? -1.0F : 0.0F, -1.0F, 2, 3, 2);
                    this.hairall[hossz + face * 4].setRotationPoint(0.0F, 0.0F, 0.0F);
                    this.setRotation(this.hairall[hossz + face * 4], 0.0F, 0.0F, 0.0F);
                }
            }
        }
        for (hossz = 0; hossz < 4; ++hossz) {
            for (face = 0; face < 56; ++face) {
                if (hossz != 3) {
                    this.hairall[hossz + face * 4].addChild(this.hairall[hossz + 1 + face * 4]);
                }
            }
        }

        this.nose = new ModelRenderer(this, 0, 0);
        this.nose.addBox(-4.0F, -8.0F, -4.006F, 8, 8, 0);
        this.nose.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.nose, 0.0F, 0.0F, 0.0F);

        this.mouth = new ModelRenderer(this, 0, 0);
        this.mouth.addBox(-4.0F, -8.0F, -4.007F, 8, 8, 0);
        this.mouth.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.mouth, 0.0F, 0.0F, 0.0F);

        this.eyebase = new ModelRenderer(this, 0, 0);
        this.eyebase.addBox(-4.0F, -8.0F, -4.008F, 8, 8, 0);
        this.eyebase.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.eyebase, 0.0F, 0.0F, 0.0F);

        this.eyeleft = new ModelRenderer(this, 0, 0);
        this.eyeleft.addBox(-4.0F, -8.0F, -4.009F, 8, 8, 0);
        this.eyeleft.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.eyeleft, 0.0F, 0.0F, 0.0F);

        this.eyeright = new ModelRenderer(this, 0, 0);
        this.eyeright.addBox(-4.0F, -8.0F, -4.01F, 8, 8, 0);
        this.eyeright.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.eyeright, 0.0F, 0.0F, 0.0F);

        this.eyebrow = new ModelRenderer(this, 0, 0);
        this.eyebrow.addBox(-4.0F, -8.0F, -4.01F, 8, 8, 0);
        this.eyebrow.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.eyebrow, 0.0F, 0.0F, 0.0F);

        addArcoHorns();

        this.parent.bipedHead.addChild(arcoHorns = new ArcoHorns(mpm));
        this.parent.bipedHead.addChild(raceEars = new RaceEars(mpm));
    }

    public void setPlayerData(EntityCustomNpc entity) {
        this.arcoHorns.setData(entity.modelData, entity);
        this.raceEars.setData(entity.modelData, entity);
    }

    public void addArcoHorns() {
        this.BackSpikes = new ModelRenderer(this, 0, 0);
        this.BackSpikes.addBox(-0.0F, -0.0F, -0.0F, 0, 0, 0, 0.02F);
        this.BackSpikes.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.RightArmSpike = new ModelRenderer(this, 0, 0);
        this.RightArmSpike.addBox(-0.0F, -0.0F, -0.0F, 0, 0, 0, 0.02F);
        this.RightArmSpike.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.LeftArmSpike = new ModelRenderer(this, 0, 0);
        this.LeftArmSpike.addBox(-0.0F, -0.0F, -0.0F, 0, 0, 0, 0.02F);
        this.LeftArmSpike.setRotationPoint(0.0F, 0.0F, 0.0F);


        this.F5spike1 = new ModelRenderer(this, 0, 6);
        this.F5spike1.addBox(-6.0F, 1.0F, -1.0F, 1, 5, 2);
        this.F5spike1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.F5spike1, 0.0F, 0.0F, (float) (-Math.PI / 6));
        this.F5spike2 = new ModelRenderer(this, 0, 6);
        this.F5spike2.addBox(5.0F, 1.0F, -1.0F, 1, 5, 2);
        this.F5spike2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.F5spike2, 0.0F, 0.0F, (float) (Math.PI / 6));
        this.F5spike3 = new ModelRenderer(this, 8, 38);
        this.F5spike3.addBox(2.0F, -4.0F, 3.0F, 2, 6, 2);
        this.F5spike3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.F5spike3, -0.9773844F, 0.0F, 0.2094395F);
        this.F5spike4 = new ModelRenderer(this, 8, 38);
        this.F5spike4.addBox(-4.0F, -4.0F, 3.0F, 2, 6, 2);
        this.F5spike4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.F5spike4, -0.9773844F, 0.0F, -0.2094395F);


        this.ArcoRightShoulder = new ModelRenderer(this, 38, 0);
        this.ArcoRightShoulder.addBox(-6.0F, -3.0F, -3.0F, 7, 4, 6);
        this.ArcoRightShoulder.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.ArcoRightShoulder.setTextureSize(128, 64);
        this.ArcoLeftShoulder = new ModelRenderer(this, 38, 0);
        this.ArcoLeftShoulder.mirror = true;
        this.ArcoLeftShoulder.addBox(-1.0F, -3.0F, -3.0F, 7, 4, 6);
        this.ArcoLeftShoulder.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.ArcoLeftShoulder.setTextureSize(128, 64);

        this.RightArmSpike.addChild(this.F5spike1);
        this.LeftArmSpike.addChild(this.F5spike2);
        this.BackSpikes.addChild(this.F5spike3);
        this.BackSpikes.addChild(this.F5spike4);
    }

    public void renderFace(DBCDisplay display) {
        TextureManager textureManager = Minecraft.getMinecraft().renderEngine;
        RenderPlayerJBRA.glColor3f(display.bodyCM == -1 ? getDefaultColor("bodyCM", display.race) : display.bodyCM);
        textureManager.bindTexture(new ResourceLocation(getFaceTexture(display, "n" + display.noseType)));

        this.nose.rotateAngleY = parent.bipedHead.rotateAngleY;
        this.nose.rotateAngleX = parent.bipedHead.rotateAngleX;
        this.nose.rotationPointX = parent.bipedHead.rotationPointX;
        this.nose.rotationPointY = parent.bipedHead.rotationPointY;
        this.nose.render(0.0625F);

        String mouthDir = "";
        if (display.race == 4 && display.hasArcoMask)
            mouthDir = "jinryuudragonbc:cc/arc/m/0A" + JRMCoreH.TransFrSkn[display.arcoState] + display.bodyType + "a.png";
        else
            mouthDir = getFaceTexture(display, "m" + display.mouthType);
        textureManager.bindTexture(new ResourceLocation(mouthDir));
        this.mouth.rotateAngleY = parent.bipedHead.rotateAngleY;
        this.mouth.rotateAngleX = parent.bipedHead.rotateAngleX;
        this.mouth.rotationPointX = parent.bipedHead.rotationPointX;
        this.mouth.rotationPointY = parent.bipedHead.rotationPointY;
        this.mouth.render(0.0625F);

        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        textureManager.bindTexture(new ResourceLocation(getFaceTexture(display, "b" + display.eyeType)));
        this.eyebase.rotateAngleY = parent.bipedHead.rotateAngleY;
        this.eyebase.rotateAngleX = parent.bipedHead.rotateAngleX;
        this.eyebase.rotationPointX = parent.bipedHead.rotationPointX;
        this.eyebase.rotationPointY = parent.bipedHead.rotationPointY;
        this.eyebase.render(0.0625F);

        if (display.race < 4) {
            RenderPlayerJBRA.glColor3f(display.hairColor);
            textureManager.bindTexture(new ResourceLocation(getFaceTexture(display, "w" + display.eyeType)));
            this.eyebrow.rotateAngleY = parent.bipedHead.rotateAngleY;
            this.eyebrow.rotateAngleX = parent.bipedHead.rotateAngleX;
            this.eyebrow.rotationPointX = parent.bipedHead.rotationPointX;
            this.eyebrow.rotationPointY = parent.bipedHead.rotationPointY;
            this.eyebrow.render(0.0625F);
        }

        RenderPlayerJBRA.glColor3f(display.eyeColor);
        textureManager.bindTexture(new ResourceLocation(getFaceTexture(display, "l" + display.eyeType)));
        this.eyeleft.rotateAngleY = parent.bipedHead.rotateAngleY;
        this.eyeleft.rotateAngleX = parent.bipedHead.rotateAngleX;
        this.eyeleft.rotationPointX = parent.bipedHead.rotationPointX;
        this.eyeleft.rotationPointY = parent.bipedHead.rotationPointY;
        this.eyeleft.render(0.0625F);

        textureManager.bindTexture(new ResourceLocation(getFaceTexture(display, "r" + display.eyeType)));
        this.eyeright.rotateAngleY = parent.bipedHead.rotateAngleY;
        this.eyeright.rotateAngleX = parent.bipedHead.rotateAngleX;
        this.eyeright.rotationPointX = parent.bipedHead.rotationPointX;
        this.eyeright.rotationPointY = parent.bipedHead.rotationPointY;
        this.eyeright.render(0.0625F);
    }

    public void renderHead(DBCDisplay display) {
        display.arcoState = 5;
        GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
        renderHairs(display);

        renderFace(display);
        GL11.glPopAttrib();
    }

    public void renderAllBody(DBCDisplay display, ModelRenderer model) {
        int race = display.race;
        TextureManager textureManager = Minecraft.getMinecraft().renderEngine;

        if (race == DBCRace.HUMAN || race == DBCRace.SAIYAN || race == DBCRace.HALFSAIYAN) {
            int bodyCM = display.bodyCM == -1 ? getDefaultColor("bodyCM", race) : display.bodyCM;
            textureManager.bindTexture(new ResourceLocation("jinryuumodscore:cc/hum.png"));
            RenderPlayerJBRA.glColor3f(bodyCM);

        } else if (race == DBCRace.NAMEKIAN) {
            textureManager.bindTexture(new ResourceLocation("jinryuudragonbc:cc/nam/0nam" + display.bodyType + ".png"));
            int bodyCM = display.bodyCM == -1 ? getDefaultColor("bodyCM", race) : display.bodyCM;
            RenderPlayerJBRA.glColor3f(bodyCM);
            model.render(0.0625F);

            textureManager.bindTexture(new ResourceLocation("jinryuudragonbc:cc/nam/1nam" + display.bodyType + ".png"));
            int bodyC1 = display.bodyC1 == -1 ? getDefaultColor("bodyC1", race) : display.bodyC1;
            RenderPlayerJBRA.glColor3f(bodyC1);
            model.render(0.0625F);

            textureManager.bindTexture(new ResourceLocation("jinryuudragonbc:cc/nam/2nam" + display.bodyType + ".png"));
            int bodyC2 = display.bodyC2 == -1 ? getDefaultColor("bodyC2", race) : display.bodyC2;
            RenderPlayerJBRA.glColor3f(bodyC2);
            model.render(0.0625F);


            textureManager.bindTexture(new ResourceLocation("jinryuudragonbc:cc/nam/3nam" + display.bodyType + ".png"));
            GL11.glColor3f(1f, 1f, 1f);
            //model.render(0.0625F);

        } else if (race == DBCRace.ARCOSIAN) {
            int st = display.arcoState;
            int ts = 0;
            int bodyCM = display.bodyCM == -1 ? getDefaultColor("bodyCM", race) : display.bodyCM;
            int bodyC1 = display.bodyC1 == -1 ? getDefaultColor("bodyC1", race) : display.bodyC1;
            int bodyC2 = display.bodyC2 == -1 ? getDefaultColor("bodyC2", race) : display.bodyC2;
            int bodyC3 = display.bodyC3 == -1 ? getDefaultColor("bodyC3", race) : display.bodyC3;

            //HORNS
            textureManager.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/0B" + JRMCoreH.TransFrSkn2[st] + display.bodyType + ".png"));
            RenderPlayerJBRA.glColor3f(bodyCM);
            renderMisc("FR" + JRMCoreH.TransFrHrn[st]);

            textureManager.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/1B" + JRMCoreH.TransFrSkn2[st] + display.bodyType + ".png"));
            RenderPlayerJBRA.glColor3f(bodyC1);
            renderMisc("FR" + JRMCoreH.TransFrHrn[st]);

            textureManager.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/2B" + JRMCoreH.TransFrSkn2[st] + display.bodyType + ".png"));
            RenderPlayerJBRA.glColor3f(bodyC2);
            renderMisc("FR" + JRMCoreH.TransFrHrn[st]);

            textureManager.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/3B" + JRMCoreH.TransFrSkn2[st] + display.bodyType + ".png"));
            RenderPlayerJBRA.glColor3f(bodyC3);
            renderMisc("FR" + JRMCoreH.TransFrHrn[st]);

            textureManager.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/4B" + JRMCoreH.TransFrSkn2[st] + display.bodyType + ".png"));
            GL11.glColor3f(1f, 1f, 1f);
            renderMisc("FR" + JRMCoreH.TransFrHrn[st]);

            //ACTUAL BODY
            RenderPlayerJBRA.glColor3f(bodyCM);
            textureManager.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/0A" + JRMCoreH.TransFrSkn[st] + display.bodyType + ".png"));
            model.render(0.0625F);

            textureManager.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/1A" + JRMCoreH.TransFrSkn[st] + display.bodyType + ".png"));
            RenderPlayerJBRA.glColor3f(bodyC1);
            model.render(0.0625F);

            textureManager.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/2A" + JRMCoreH.TransFrSkn[st] + display.bodyType + ".png"));
            RenderPlayerJBRA.glColor3f(bodyC2);
            model.render(0.0625F);

            textureManager.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/3A" + JRMCoreH.TransFrSkn[st] + display.bodyType + ".png"));
            RenderPlayerJBRA.glColor3f(bodyC3);
            model.render(0.0625F);

            textureManager.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/4A" + JRMCoreH.TransFrSkn[st] + display.bodyType + ".png"));
            GL11.glColor3f(1f, 1f, 1f);

        } else if (race == DBCRace.MAJIN) {
            int bodyCM = display.bodyCM == -1 ? getDefaultColor("bodyCM", race) : display.bodyCM;
            textureManager.bindTexture(new ResourceLocation("jinryuudragonbc:cc/majin/majin.png"));
            RenderPlayerJBRA.glColor3f(bodyCM);

            //  model.render(0.0625F);


        }
        // parent.currentlyPlayerTexture = false;
    }

    public void renderMisc(String hair) {
        float par1 = 0.0625f;
        if (hair.contains("FR")) {
            if (hair.contains("2")) {
                this.ArcoLeftShoulder.rotationPointZ = parent.bipedLeftArm.rotationPointZ;
                this.ArcoLeftShoulder.rotationPointY = parent.bipedLeftArm.rotationPointY;
                this.ArcoLeftShoulder.rotationPointX = parent.bipedLeftArm.rotationPointX;
                this.ArcoLeftShoulder.rotateAngleY = parent.bipedLeftArm.rotateAngleY;
                this.ArcoLeftShoulder.rotateAngleX = parent.bipedLeftArm.rotateAngleX;
                this.ArcoLeftShoulder.rotateAngleZ = parent.bipedLeftArm.rotateAngleZ;
                this.ArcoLeftShoulder.render(par1);

                this.ArcoRightShoulder.rotationPointZ = parent.bipedRightArm.rotationPointZ;
                this.ArcoRightShoulder.rotationPointY = parent.bipedRightArm.rotationPointY;
                this.ArcoRightShoulder.rotationPointX = parent.bipedRightArm.rotationPointX;
                this.ArcoRightShoulder.rotateAngleY = parent.bipedRightArm.rotateAngleY;
                this.ArcoRightShoulder.rotateAngleX = parent.bipedRightArm.rotateAngleX;
                this.ArcoRightShoulder.rotateAngleZ = parent.bipedRightArm.rotateAngleZ;
                this.ArcoRightShoulder.render(par1);
            }
            if (hair.contains("4")) {
                // Back Spikes
                this.BackSpikes.rotateAngleY = parent.bipedBody.rotateAngleY;
                this.BackSpikes.rotateAngleX = parent.bipedBody.rotateAngleX;
                this.BackSpikes.rotationPointX = parent.bipedBody.rotationPointX;
                this.BackSpikes.rotationPointY = parent.bipedBody.rotationPointY;
                this.BackSpikes.render(par1);

                // Right Arm Spikes
                this.RightArmSpike.rotationPointX = parent.bipedRightArm.rotationPointX;
                this.RightArmSpike.rotationPointY = parent.bipedRightArm.rotationPointY;
                this.RightArmSpike.rotationPointZ = parent.bipedRightArm.rotationPointZ;
                this.RightArmSpike.rotateAngleY = parent.bipedRightArm.rotateAngleY;
                this.RightArmSpike.rotateAngleX = parent.bipedRightArm.rotateAngleX;
                this.RightArmSpike.rotateAngleZ = parent.bipedRightArm.rotateAngleZ;
                this.RightArmSpike.render(par1);

                // Left Arm Spikes
                this.LeftArmSpike.rotationPointX = parent.bipedLeftArm.rotationPointX;
                this.LeftArmSpike.rotationPointY = parent.bipedLeftArm.rotationPointY;
                this.LeftArmSpike.rotationPointZ = parent.bipedLeftArm.rotationPointZ;
                this.LeftArmSpike.rotateAngleY = parent.bipedLeftArm.rotateAngleY;
                this.LeftArmSpike.rotateAngleX = parent.bipedLeftArm.rotateAngleX;
                this.LeftArmSpike.rotateAngleZ = parent.bipedLeftArm.rotateAngleZ;
                this.LeftArmSpike.render(par1);
            }


            if (hair.contains("0") || hair.contains("2") || hair.contains("1")) {
                // Head Spike Pair Bottom
//                this.SpikePair.rotateAngleY = parent.bipedHead.rotateAngleY;
//                this.SpikePair.rotateAngleX = parent.bipedHead.rotateAngleX;
//                this.SpikePair.rotationPointX = parent.bipedHead.rotationPointX;
//                this.SpikePair.rotationPointY = parent.bipedHead.rotationPointY;
//                this.SpikePair.render(par1);
            }

            if (hair.contains("1") || hair.contains("2")) {
                // Head Spike Pair Extension
//                this.SpikePairExtended.rotateAngleY = parent.bipedHead.rotateAngleY;
//                this.SpikePairExtended.rotateAngleX = parent.bipedHead.rotateAngleX;
//                this.SpikePairExtended.rotationPointX = parent.bipedHead.rotationPointX;
//                this.SpikePairExtended.rotationPointY = parent.bipedHead.rotationPointY;
//                this.SpikePairExtended.render(par1);
            }

            if (hair.contains("2")) {
                // Form 3 Long Head
//                this.ThirdFormBigHead.rotateAngleY = parent.bipedHead.rotateAngleY;
//                this.ThirdFormBigHead.rotateAngleX = parent.bipedHead.rotateAngleX;
//                this.ThirdFormBigHead.rotationPointX = parent.bipedHead.rotationPointX;
//                this.ThirdFormBigHead.rotationPointY = parent.bipedHead.rotationPointY;
//                this.ThirdFormBigHead.render(par1);
            }

            if (hair.contains("4")) {
                // 5th Form Head Spikes
//                this.CoolerHeadSpikes.rotateAngleY = parent.bipedHead.rotateAngleY;
//                this.CoolerHeadSpikes.rotateAngleX = parent.bipedHead.rotateAngleX;
//                this.CoolerHeadSpikes.rotationPointX = parent.bipedHead.rotationPointX;
//                this.CoolerHeadSpikes.rotationPointY = parent.bipedHead.rotationPointY;
//                this.CoolerHeadSpikes.render(par1);
            }
        }

    }

    public void renderHairs(DBCDisplay display) {
        TextureManager textureManager = Minecraft.getMinecraft().renderEngine;
        if (display.hairCode.length() < 5) {
            if (display.hairCode.equalsIgnoreCase("bald"))
                return;
            if (display.race == 5)
                display.hairCode = "005050555050000050505550500000505055505000005050455050000050505250500000505052505000005050555050000050505450500000505052505000005050525050000150433450500000505055505000005050525050000054395050500000505045505000005050475050000050504750500000505047505000015043655050000050504750500000505047505000005050475050000050504750500000544545505000005250505050000052505050500000525050505000005250505050000050505050500000505050505000005050505050000052505050500000525050505000005250505050000052505050500000525050505000005245505050000054505050500000525050505000005252505050000070505050500000705050505000007050505050000070505050500000705050505000347050505050003470505050500000705050505000007050505050000069505050500000695050505000007050505050000070505050500000705050505000007050505050000070505050500020";
            if (display.hairType.equals("ssj4"))
                display.hairCode = "373852546750347428545480193462285654801934283647478050340147507467501848505072675018255250726750183760656580501822475071675018255050716750189730327158501802475071675018973225673850189765616160501820414547655019545654216550195754542165501920475027655019943669346576193161503065231900475030655019406534276538199465393460501997654138655019976345453950189760494941501897615252415018976354563850189763494736501897614949395018976152523950189763525234501897584749395018976150493850189760545234501897585250415018885445474550189754475041501897545250435018885454523950185143607861501897415874585018514369196150185147768078391865525680565018974356806150188843567861501868396374615018975056805650189750568056501885582374615018975823726150187149568054501877495680565018774950785650189163236961501820";

            if (display.hairCode.length() < 5)
                return;
        }
        boolean canUse = mod_JBRA.a6P9H9B;
        boolean pstrty = false;//super form selected;JRMCoreH.plyrSttngsClient(1, pl);
        boolean aura = display.auraOn;//aura anim JRMCoreH.StusEfctsClient(4, pl);
        boolean trbo = false; //turbo anim JRMCoreH.StusEfctsClient(3, pl);
        boolean kken = false;//kaioken anim JRMCoreH.StusEfctsClient(5, pl);
        boolean trty = false; //transforming anim JRMCoreH.StusEfctsClient(1, pl);

        String hairCode = display.hairCode;
        int state = 0;
        int rage = display.rage;

        if (display.hairType.equals("ssj"))
            state = 1;
        else if (display.hairType.equals("ssj2"))
            state = 5;
        else if (display.hairType.equals("ssj4"))
            hairCode = "373852546750347428545480193462285654801934283647478050340147507467501848505072675018255250726750183760656580501822475071675018255050716750189730327158501802475071675018973225673850189765616160501820414547655019545654216550195754542165501920475027655019943669346576193161503065231900475030655019406534276538199465393460501997654138655019976345453950189760494941501897615252415018976354563850189763494736501897614949395018976152523950189763525234501897584749395018976150493850189760545234501897585250415018885445474550189754475041501897545250435018885454523950185143607861501897415874585018514369196150185147768078391865525680565018974356806150188843567861501868396374615018975056805650189750568056501885582374615018975823726150187149568054501877495680565018774950785650189163236961501820";

        int hairColor = display.hairColor;
        if (display.hairColor == -1 && display.race == 5)
            hairColor = getDefaultColor("bodycm", 5);
        RenderPlayerJBRA.glColor3f(hairColor);

        textureManager.bindTexture(new ResourceLocation("jinryuumodscore:gui/normall.png"));

        boolean hasHairAnimations = true;
        int trTime = canUse ? 2 : 200;
        int arTime = canUse ? 2 : 200;
        if (hasHairAnimations) {
            if (JRMCoreH.HairsT(state, "B") && stateChange < 200) {
                stateChange += trTime;
            }

            if (JRMCoreH.HairsT(state, "C")) {
                if (stateChange < 200) {
                    stateChange += trTime;
                }

                if (state2Change < 200) {
                    state2Change += trTime;
                }
            }

            if (JRMCoreH.HairsT(tempState, "A") && !JRMCoreH.HairsT(state, "A")) {
                if (!JRMCoreH.HairsT(tempState, state) && stateChange < 200) {
                    stateChange += trTime;
                }

                if (stateChange >= 200) {
                    stateChange = 200;
                    tempState = state;
                }
            } else if (!JRMCoreH.HairsT(tempState, "A") && JRMCoreH.HairsT(state, "A")) {
                if ((!JRMCoreH.HairsT(tempState, state) || rage == 0) && stateChange > 0) {
                    stateChange -= trTime;
                }

                if (stateChange <= 0) {
                    stateChange = 0;
                    tempState = state;
                }
            } else if (!JRMCoreH.HairsT(tempState, state) && JRMCoreH.HairsT(tempState, "B") && JRMCoreH.HairsT(state, "B")) {
                tempState = state;
            } else if (JRMCoreH.HairsT(tempState, "A")) {
                if (!canUse && JRMCoreH.HairsT(tempState, state) && rage > 90) {
                    stateChange += trTime;
                    if (stateChange > 200) {
                        stateChange = 200;
                    }
                } else if (canUse && JRMCoreH.HairsT(tempState, state) && rage > 0 && stateChange < rage * 2) {
                    stateChange += trTime;
                } else if (JRMCoreH.HairsT(tempState, state)) {
                    if (stateChange > 0) {
                        stateChange -= trTime;
                    } else {
                        stateChange = 0;
                    }

                    if (state2Change > 0) {
                        state2Change -= trTime;
                    } else {
                        state2Change = 0;
                    }
                }
            } else if ((!JRMCoreH.HairsT(state, "B") || !pstrty) && !JRMCoreH.HairsT(state, "B")) {
                if (!JRMCoreH.HairsT(tempState, state) && JRMCoreH.HairsT(state, "C")) {
                    if (state2Change < 200) {
                        state2Change += trTime;
                    }

                    if (state2Change >= 200) {
                        state2Change = 200;
                        tempState = state;
                    }
                }
            } else if (!canUse && JRMCoreH.HairsT(tempState, state) && rage > 90) {
                state2Change += trTime;
                if (state2Change > 200) {
                    state2Change = 200;
                }
            } else if (canUse && JRMCoreH.HairsT(tempState, state) && rage > 0 && state2Change < rage * 2) {
                state2Change += trTime;
            } else if (state2Change > 200) {
                state2Change = 200;
                tempState = state;
            } else if (state2Change > 0) {
                state2Change -= trTime;
            } else if (state2Change != 0) {
                state2Change = 0;
            }
        }

        if (canUse && (aura || trty || kken || trbo)) { //turbo/kaioken/charging hair animation
            if (JRMCoreH.HairsT(tempState, state) && auraTime < 50) {
                if (auraTime < 50 && auraType == 0) {
                    auraTime += arTime;
                }

                if (auraTime >= 50) {
                    auraType = 1;
                }

                if (auraTime < 20 && auraType == 1) {
                    auraType = 0;
                }

                if (auraTime > 0 && auraType == 1) {
                    auraTime -= arTime;
                }
            } else if (JRMCoreH.HairsT(tempState, state) && !JRMCoreH.HairsT(state, "A")) {
                if (auraType < 2) {
                    auraType = 2;
                }

                if (bendTime < 50 && auraType == 2) {
                    bendTime += arTime;
                }

                if (bendTime >= 50) {
                    auraType = 3;
                }

                if (bendTime < 20 && auraType == 3) {
                    auraType = 2;
                }

                if (bendTime > 0 && auraType == 3) {
                    bendTime -= arTime;
                }
            }
        } else {
            if (auraType > 0) {
                auraType = 0;
            }

            if (bendTime > 0) {
                bendTime -= 1;
            }

            if (auraTime > 0) {
                auraTime -= 1;
                ;
            }
        }

        GL11.glPushMatrix();
        GL11.glScalef((0.5F + 0.5F / 1.0F) * 1.0F, 0.5F + 0.5F / 1.0F, (0.5F + 0.5F / 1.0F) * 1.0F);
        GL11.glTranslatef(0.0F, (1.0F - 1.0F) / 1.0F * (2.0F - (1.0F >= 1.5F && 1.0F <= 2.0F ? (2.0F - 1.0F) / 2.5F : (1.0F < 1.5F && 1.0F >= 1.0F ? (1.0F * 2.0F - 2.0F) * 0.2F : 0.0F))), 0.0F);
        float[] var10000 = new float[]{0.6F, 0.5F, 0.4F, -0.5F};
        var10000 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
        int[] hairRightPosZ = new int[]{3, 2, 1, 0, 3, 2, 1, 3, 2, 3};
        int[] hairRightPosY = new int[]{0, 0, 0, 0, 1, 1, 1, 2, 2, 3};
        int[] hairLeftPosZ = new int[]{0, 1, 2, 3, 1, 2, 3, 2, 3, 3};
        int[] hairLeftPosY = new int[]{0, 0, 0, 0, 1, 1, 1, 2, 2, 3};
        int[] hairBackPosX = new int[]{0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3};
        int[] hairBackPosY = new int[]{0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3};
        int[] hairTopPosX = new int[]{0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3};
        int[] hairTopPosZ = new int[]{0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3};
        int[] hairPos = new int[]{0, 4, 14, 24, 40, 56};
        String hairdns = hairCode;

        for (int face = 0; face < 56; ++face) {
            int l = dnsHair2(hairdns, face * 14);
            if (l != 0) {
                int X = dnsHair2(hairdns, face * 14 + 2);
                int Y = dnsHair2(hairdns, face * 14 + 4);
                int Z = dnsHair2(hairdns, face * 14 + 6);
                int B = dnsHair2(hairdns, face * 14 + 8);
                int P = dnsHair2(hairdns, face * 14 + 10);
                int T = dnsHair2(hairdns, face * 14 + 12);
                X = X > 82 ? 82 : (X < 18 ? 18 : X);
                Y = Y > 82 ? 82 : (Y < 18 ? 18 : Y);
                Z = Z > 82 ? 82 : (Z < 18 ? 18 : Z);
                B = B > 82 ? 82 : (B < 18 ? 18 : B);
                P = P > 82 ? 82 : (P < 18 ? 18 : P);
                T = T > 82 ? 82 : (T < 18 ? 18 : T);
                float x = (float) (X - 50) * 0.1F;
                float y = (float) (Y - 50) * 0.1F;
                float z = (float) (Z - 50) * 0.1F;
                float b = (float) (B - 50) * 0.1F;
                float p = (float) (P - 50) * 0.1F;
                int t = (int) ((float) (T - 18) * 1.62F);
                float Int = (float) t * 0.01F;
                boolean hpFront = face >= hairPos[0] && face < hairPos[1];
                boolean hpTop = face >= hairPos[4] && face < hairPos[5];
                boolean hpRight = face >= hairPos[1] && face < hairPos[2];
                boolean hpLeft = face >= hairPos[2] && face < hairPos[3];
                boolean hpBack = face >= hairPos[3] && face < hairPos[4];
                if (stateChange > 0 && l > 0) {
                    if (y > -1.0F && y < 1.0F && z > -1.0F && z < 1.0F && hpBack) {
                        x += (float) stateChange * Int * (x < 0.0F ? -0.01F : 0.01F) * (float) l * 0.01F;
                        x = x > 3.0F ? 3.0F : x;
                        x = x < -3.0F ? -3.0F : x;
                    }

                    if (y > -1.0F && y < 1.0F && x > -1.0F && x < 1.0F && !hpBack) {
                        z += (float) stateChange * Int * (z < 0.0F ? -0.01F : 0.01F);
                        z = z > 3.2F ? 3.2F : z;
                        z = z < -3.2F ? -3.2F : z;
                        if (!hpFront || x < 0.0F) {
                            x += (float) stateChange * Int * 0.01F;
                            x = x > 0.4F ? 0.4F : x;
                            x = x < -0.4F ? -0.4F : x;
                        }

                        if (z > 0.0F) {
                            boolean add = hpTop ? hairTopPosZ[face - hairPos[4]] == 0 || hairTopPosZ[face - hairPos[4]] == 2 : false;
                            boolean add2 = hpTop ? face % 4 == 0 || face % 4 == 3 : false;
                            b += (float) stateChange * Int * -0.02F;
                            b = b < (add && add2 ? 0.0F : -0.2F) ? (add && add2 ? 0.0F : -0.2F) : b;
                        } else if (z < 0.0F) {
                            boolean add = hpTop ? hairTopPosZ[face - hairPos[4]] == 0 || hairTopPosZ[face - hairPos[4]] == 2 : false;
                            boolean add2 = hpTop ? face % 4 == 0 || face % 4 == 3 : false;
                            b += (float) stateChange * Int * 0.02F;
                            b = b > (add && add2 ? 0.0F : 0.2F) ? (add && add2 ? 0.0F : 0.2F) : b;
                        }
                    } else if (y > -1.0F && y < 1.0F) {
                        x += (float) stateChange * Int * (x < 0.0F ? -0.01F : 0.01F);
                        x = x > 2.8F ? 2.8F : x;
                        x = x < -2.8F ? -2.8F : x;
                        if (b > 1.5F) {
                            x = x > 1.5F ? 1.5F : x;
                            x = x < -1.5F ? -1.5F : x;
                            b += (float) stateChange * Int * (b < 0.0F ? 0.03F : -0.03F);
                            b = b > 2.8F ? 2.8F : b;
                            b = b < -2.8F ? -2.8F : b;
                        }
                    } else if (x > -1.0F && x < 1.0F) {
                        z += (float) stateChange * Int * (z < 0.0F ? -0.01F : 0.01F);
                        z = z > 2.8F ? 2.8F : z;
                        z = z < -2.8F ? -2.8F : z;
                        if (b > 0.0F && z > 0.0F && y < 1.6F) {
                            z = z > 2.2F ? 2.2F : z;
                            z = z < -2.2F ? -2.2F : z;
                            float var91 = b + (float) stateChange * Int * -0.02F;
                            float var92 = var91 > b ? b : var91;
                            b = var92 < -b ? -b : var92;
                        } else if (b > 0.0F && z < 0.0F && y > 0.0F) {
                            z = z > 2.2F ? 2.2F : z;
                            z = z < -2.2F ? -2.2F : z;
                            float var89 = b + (float) stateChange * Int * -0.02F;
                            float var90 = var89 > b ? b : var89;
                            b = var90 < -b ? -b : var90;
                        } else if (y < -1.3F && b > 0.0F) {
                            z = z > 2.2F ? 2.2F : z;
                            z = z < -2.2F ? -2.2F : z;
                            b += (float) stateChange * Int * -0.02F;
                            b = b < 0.5F ? 0.5F : b;
                        }
                    }
                }

                if (state2Change > 0) {
                    if (y > -1.0F && y < 1.0F && x > -1.0F && x < 1.0F && hpFront) {
                        float Int2 = Int > 0.02F ? 0.6F : Int;
                        x += (float) state2Change * Int2 * 0.01F;
                        x = x > 0.2F ? 0.2F : x;
                        x = x < -0.2F ? -0.2F : x;
                        z += (float) state2Change * Int2 * (z < 0.0F ? -0.02F : 0.02F);
                        z = z > 2.8F ? 2.8F : z;
                        z = z < -2.8F ? -2.8F : z;
                    }

                    l = (int) ((float) l + (float) state2Change * 0.1F);
                    if (b < 0.0F) {
                        b += (float) state2Change * 5.0E-4F;
                        b = b >= 0.0F ? 0.2F : b;
                    }

                    if (b > 0.0F) {
                        b += (float) state2Change * -5.0E-4F;
                        b = b <= 0.0F ? -0.2F : b;
                    }
                }

                if (bendTime > 0) {
                    z += (float) bendTime * (z < 0.0F ? -0.0025F : 0.0025F);
                    b += (float) bendTime * (b > 0.0F ? -0.005F : 0.005F);
                    z = z > 3.2F ? 3.2F : z;
                    z = z < -3.2F ? -3.2F : z;
                }

                if (auraTime > 0) {
                    z += (float) auraTime * (z < 0.0F ? -0.0025F : 0.0025F);
                    b += (float) auraTime * (b > 0.0F ? -0.005F : 0.005F);
                    z = z > 3.2F ? 3.2F : z;
                    z = z < -3.2F ? -3.2F : z;
                }

                int lng = 0;
                if (!JRMCoreClient.mc.isGamePaused()) {
                    this.setRotation(this.hairall[lng + face * 4], x, y, z);
                    this.hairall[lng + face * 4].rotationPointX = -2.999F + (float) (face < 4 ? face * 2 : (face >= 14 && face < 24 ? 7 : (face >= 24 && face < 40 ? hairBackPosX[face - 4 - 10 - 10] * 2 : (face >= 40 && face < 56 ? hairTopPosX[face - 4 - 10 - 10 - 16] * 2 : -1))));
                    this.hairall[lng + face * 4].rotationPointZ = -3.999F + (face >= 4 && face < 14 ? (float) (hairRightPosZ[face - 4] * 2 + 1) : (face >= 14 && face < 24 ? (float) (hairLeftPosZ[face - 4 - 10] * 2 + 1) : (face >= 24 && face < 40 ? 8.0F : (face >= 40 && face < 56 ? (float) (hairTopPosZ[face - 4 - 10 - 10 - 16] * 2) + 0.9F : 0.0F))));
                    this.hairall[lng + face * 4].rotationPointY = -7.0F + (face >= 4 && face < 14 ? (float) (hairRightPosY[face - 4] * 2) : (face >= 14 && face < 24 ? (float) (hairLeftPosY[face - 4 - 10] * 2) : (face >= 24 && face < 40 ? (float) (hairBackPosY[face - 4 - 10 - 10] * 2) : -0.5F)));
                    float f = 1.57F;
                    float r = MathHelper.sin(this.rot3 * 0.02F) * 0.1F;
                    float r2 = MathHelper.cos(this.rot3 * 0.02F) * 0.1F;
                    float r3 = MathHelper.cos(this.rot3 * 0.14F) * 0.1F;
                    this.hairall[1 + face * 4].rotateAngleY = 0.0F;
                    this.hairall[1 + face * 4].rotateAngleX = -0.0F;
                    this.hairall[2 + face * 4].rotateAngleY = 0.0F;
                    this.hairall[2 + face * 4].rotateAngleX = 0.0F;
                    this.hairall[3 + face * 4].rotateAngleY = 0.0F;
                    this.hairall[3 + face * 4].rotateAngleX = 0.0F;
                    if (!hpTop && !hpRight && !hpLeft) {
                        this.hairall[1 + face * 4].rotateAngleX = b * 0.3F * (p > 0.5F ? 1.0F - p * 0.3F : (p < -0.5F ? 1.0F + -p * 0.1F : 1.0F));
                        this.hairall[2 + face * 4].rotateAngleX = b * 0.3F;
                        this.hairall[3 + face * 4].rotateAngleX = b * 0.3F * (p > 0.5F ? 1.0F + p * 0.1F : (p < -0.5F ? 1.0F - -p * 0.3F : 1.0F));
                    } else {
                        int min = hpLeft ? 1 : -1;
                        this.hairall[1 + face * 4].rotateAngleZ = (float) min * b * 0.3F * (p > 0.5F ? 1.0F - p * 0.3F : (p < -0.5F ? 1.0F + -p * 0.1F : 1.0F));
                        this.hairall[2 + face * 4].rotateAngleZ = (float) min * b * 0.3F;
                        this.hairall[3 + face * 4].rotateAngleZ = (float) min * b * 0.3F * (p > 0.5F ? 1.0F + p * 0.1F : (p < -0.5F ? 1.0F - -p * 0.3F : 1.0F));
                    }
                }

                this.hairall[1 + face * 4].rotationPointX = 0.0F;
                this.hairall[1 + face * 4].rotationPointZ = 0.0F;
                this.hairall[1 + face * 4].rotationPointY = 1.5F;
                this.hairall[2 + face * 4].rotationPointX = 0.0F;
                this.hairall[2 + face * 4].rotationPointZ = 0.0F;
                this.hairall[2 + face * 4].rotationPointY = 2.5F;
                this.hairall[3 + face * 4].rotationPointX = 0.0F;
                this.hairall[3 + face * 4].rotationPointZ = 0.0F;
                this.hairall[3 + face * 4].rotationPointY = 2.5F;
                GL11.glPushMatrix();
                GL11.glTranslatef(parent.bipedHead.rotationPointX * 0.0625f, parent.bipedHead.rotationPointY * 0.0625f, parent.bipedHead.rotationPointZ * 0.0625f);
                if (parent.bipedHead.rotateAngleZ != 0.0F) {
                    GL11.glRotatef(parent.bipedHead.rotateAngleZ * (180.0F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
                }

                if (parent.bipedHead.rotateAngleY != 0.0F) {
                    GL11.glRotatef(parent.bipedHead.rotateAngleY * (180.0F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
                }

                if (parent.bipedHead.rotateAngleX != 0.0F) {
                    GL11.glRotatef(parent.bipedHead.rotateAngleX * (180.0F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
                }

                GL11.glPushMatrix();
                var10000 = new float[]{4.0F, 2.0F, 1.5F, 1.0F, 1.0F};
                boolean[] var107 = new boolean[]{false, true, true, true, true};
                boolean[] var108 = new boolean[]{false, false, true, true, true};
                boolean[] var109 = new boolean[]{false, false, false, true, true};
                float tincs1 = (float) l < 33.0F ? (float) l / 33.0F : 1.0F;
                float tincs2 = (float) l > 33.0F && (float) l < 66.0F ? ((float) l - 33.0F) / 33.0F : ((float) l < 33.0F ? 0.0F : 1.0F);
                float tincs3 = (float) l > 66.0F ? ((float) l - 66.0F) / 33.0F : ((float) l < 66.0F ? 0.0F : 1.0F);
                this.hairall[lng + face * 4].lengthY = 1.0F;
                this.hairall[1 + face * 4].lengthY = tincs1;
                this.hairall[2 + face * 4].lengthY = tincs2;
                this.hairall[3 + face * 4].lengthY = tincs3;
                this.hairall[0 + face * 4].sizeXZ = 1.1F;
                this.hairall[1 + face * 4].sizeXZ = 1.0F;
                this.hairall[2 + face * 4].sizeXZ = 0.9F;
                this.hairall[3 + face * 4].sizeXZ = 0.8F;
                this.hairall[1 + face * 4].showModel = (float) l > 0.0F;
                this.hairall[2 + face * 4].showModel = (float) l > 33.0F;
                this.hairall[3 + face * 4].showModel = (float) l > 66.0F;
                this.hairall[lng + face * 4].render(0.0625f);
                GL11.glPopMatrix();
                GL11.glPopMatrix();
            }
        }

        GL11.glScalef(1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    public int getDefaultColor(String type, int race) {
        if (race < 3) {
            if (type.equalsIgnoreCase("bodycm"))
                return 16297621;
        } else if (race == 3) {
            if (type.equalsIgnoreCase("bodycm"))
                return 5095183;
            else if (type.equalsIgnoreCase("bodyc1"))
                return 13796998;
            else if (type.equalsIgnoreCase("bodyc2"))
                return 12854822;
        } else if (race == 4) {
            if (type.equalsIgnoreCase("bodycm"))
                return 15460342;
            else if (type.equalsIgnoreCase("bodyc1"))
                return 16111595;
            else if (type.equalsIgnoreCase("bodyc2"))
                return 8533141;
            else if (type.equalsIgnoreCase("bodyc3"))
                return 16550015;
        } else if (race == 5)
            if (type.equalsIgnoreCase("bodycm"))
                return 16757199;
        return 0;
    }

    public String getFaceTexture(DBCDisplay display, String t) {
        int race = display.race;
        String tex = "";
        int st = display.arcoState;

        if (race == DBCRace.HUMAN || race == DBCRace.SAIYAN || race == DBCRace.HALFSAIYAN)
            tex = "jinryuumodscore:cc/hum" + t + ".png";
        else if (race == DBCRace.NAMEKIAN)
            tex = "jinryuudragonbc:cc/nam/4nam" + t + ".png";
        else if (race == DBCRace.ARCOSIAN)
            tex = "jinryuudragonbc:cc/arc/m/4A" + JRMCoreH.TransFrSkn[st] + display.bodyType + t + ".png";
        else if (race == DBCRace.MAJIN)
            tex = "jinryuudragonbc:cc/majin/majin" + t + ".png";
        return tex;
    }

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

    public int dnsHair2(String s, int n) {
        int a = 0;
        try {
            a = Integer.parseInt(s.charAt(n) + "" + s.charAt(n + 1) + "");
        } catch (NumberFormatException ignored) {
        }
        return s.length() > n ? a : 0;
    }

    private void transRot(float f5, ModelRenderer m) {

        GL11.glTranslatef(m.rotationPointX * f5, m.rotationPointY * f5, m.rotationPointZ * f5);
        if (m.rotateAngleZ != 0.0F) {
            GL11.glRotatef(m.rotateAngleZ * (180.0F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
        }

        if (m.rotateAngleY != 0.0F) {
            GL11.glRotatef(m.rotateAngleY * (180.0F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
        }

        if (m.rotateAngleX != 0.0F) {
            GL11.glRotatef(m.rotateAngleX * (180.0F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
        }
    }
}
