package kamkeel.npcdbc.client.model;

import JinRyuu.JBRA.ModelRendererJBRA;
import JinRyuu.JBRA.RenderPlayerJBRA;
import JinRyuu.JBRA.mod_JBRA;
import JinRyuu.JRMCore.JRMCoreClient;
import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.model.ModelMPM;
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

    public ModelRenderer nose;
    public ModelRenderer mouth;
    public ModelRenderer eyeleft;
    public ModelRenderer eyeright;
    public ModelRenderer eyebase;
    public ModelRenderer eyebrow;

    public ModelRenderer Fro;
    public ModelRenderer Fro0;
    public ModelRenderer Fro1;
    public ModelRenderer Fro2;
    public ModelRenderer Fro5;
    public ModelRenderer Fro5b;
    public ModelRenderer Fro5l;
    public ModelRenderer Fro5r;
    public ModelRenderer FroB;
    public ModelRenderer appule;
    public ModelRenderer Fhorn2;
    public ModelRenderer Fhorn1;
    public ModelRenderer Fhorn3;
    public ModelRenderer Fhorn4;
    public ModelRenderer F2horn1;
    public ModelRenderer F2horn2;
    public ModelRenderer F5horn1;
    public ModelRenderer F5horn2;
    public ModelRenderer F5horn3;
    public ModelRenderer F5horn4;
    public ModelRenderer F5horn5;
    public ModelRenderer F5spike1;
    public ModelRenderer F5spike2;
    public ModelRenderer F5spike3;
    public ModelRenderer F5spike4;
    public ModelRenderer ftail1;
    public ModelRenderer ftail2;
    public ModelRenderer fear1;
    public ModelRenderer fear2;
    public ModelRenderer leftarmshoulder;
    public ModelRenderer rightarmshoulder;
    public ModelRenderer ftailS1;
    public ModelRenderer ftailS2;
    public ModelRenderer ftailS3;
    public ModelRenderer ftailS4;
    public ModelRenderer ftailS5;
    public ModelRenderer ftailS6;


    public TextureManager tex;

    public int tempState, stateChange, state2Change, auraTime, auraType, bendTime;

    public ModelNPCDBC(ModelMPM mpm) {
        this.parent = mpm;
        this.textureHeight = 32;
        this.textureWidth = 64;
        tex = Minecraft.getMinecraft().renderEngine;

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
    }

    public void addArcoHorns() {
        this.Fro = new ModelRenderer(this, 0, 0);
        this.Fro.addBox(-0.0F, -0.0F, -0.0F, 0, 0, 0, 0.02F);
        this.Fro.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Fro0 = new ModelRenderer(this, 0, 0);
        this.Fro0.addBox(-0.0F, -0.0F, -0.0F, 0, 0, 0, 0.02F);
        this.Fro0.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Fro1 = new ModelRenderer(this, 0, 0);
        this.Fro1.addBox(-0.0F, -0.0F, -0.0F, 0, 0, 0, 0.02F);
        this.Fro1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Fro2 = new ModelRenderer(this, 0, 0);
        this.Fro2.addBox(-0.0F, -0.0F, -0.0F, 0, 0, 0, 0.02F);
        this.Fro2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Fro5 = new ModelRenderer(this, 0, 0);
        this.Fro5.addBox(-0.0F, -0.0F, -0.0F, 0, 0, 0, 0.02F);
        this.Fro5.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Fro5b = new ModelRenderer(this, 0, 0);
        this.Fro5b.addBox(-0.0F, -0.0F, -0.0F, 0, 0, 0, 0.02F);
        this.Fro5b.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Fro5r = new ModelRenderer(this, 0, 0);
        this.Fro5r.addBox(-0.0F, -0.0F, -0.0F, 0, 0, 0, 0.02F);
        this.Fro5r.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Fro5l = new ModelRenderer(this, 0, 0);
        this.Fro5l.addBox(-0.0F, -0.0F, -0.0F, 0, 0, 0, 0.02F);
        this.Fro5l.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.FroB = new ModelRenderer(this, 0, 0);
        this.FroB.addBox(-0.0F, -0.0F, -0.0F, 0, 12, 0, 0.02F);
        this.FroB.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.appule = new ModelRenderer(this, 0, 16);
        this.appule.addBox(-4.0F, -8.0F, 4.0F, 8, 8, 8);
        this.appule.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.appule, 0.0F, 0.0F, 0.0F);
        this.Fhorn2 = new ModelRenderer(this, 8, 6);
        this.Fhorn2.addBox(1.5F, -11.0F, -3.5F, 2, 4, 2);
        this.Fhorn2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.Fhorn2, 0.0F, 0.0F, (float) (-Math.PI / 4));
        this.Fhorn1 = new ModelRenderer(this, 8, 6);
        this.Fhorn1.addBox(-3.5F, -11.0F, -3.5F, 2, 4, 2);
        this.Fhorn1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.Fhorn1, 0.0F, 0.0F, (float) (Math.PI / 4));
        this.Fhorn3 = new ModelRenderer(this, 8, 6);
        this.Fhorn3.addBox(2.5F, -14.0F, -3.5F, 2, 4, 2);
        this.Fhorn3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.Fhorn3, 0.0F, 0.0F, 0.2094395F);
        this.Fhorn4 = new ModelRenderer(this, 8, 6);
        this.Fhorn4.addBox(-4.5F, -14.0F, -3.5F, 2, 4, 2);
        this.Fhorn4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.Fhorn4, 0.0F, 0.0F, -0.2094395F);
        this.F2horn1 = new ModelRenderer(this, 16, 6);
        this.F2horn1.addBox(-3.5F, -11.0F, 6.5F, 2, 4, 2);
        this.F2horn1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.F2horn1, 0.0F, 0.0F, (float) (Math.PI / 4));
        this.F2horn2 = new ModelRenderer(this, 16, 6);
        this.F2horn2.addBox(1.5F, -11.0F, 6.5F, 2, 4, 2);
        this.F2horn2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.F2horn2, 0.0F, 0.0F, (float) (-Math.PI / 4));
        this.ftail1 = new ModelRenderer(this, 32, 16);
        this.ftail1.addBox(-2.0F, 7.0F, 4.0F, 4, 4, 12);
        this.ftail1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.ftail1, -0.3490659F, 0.0F, 0.0F);
        this.ftail2 = new ModelRenderer(this, 32, 16);
        this.ftail2.addBox(-2.0F, 15.0F, 2.0F, 4, 4, 12);
        this.ftail2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.ftail2, (float) (Math.PI / 6), 1.33E-5F, 0.0F);
        this.F5horn1 = new ModelRenderer(this, 8, 6);
        this.F5horn1.addBox(-4.5F, -8.0F, -6.5F, 2, 6, 2);
        this.F5horn1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.F5horn1, (float) (-Math.PI * 2.0 / 9.0), 0.0F, 1.047198F);
        this.F5horn2 = new ModelRenderer(this, 8, 6);
        this.F5horn2.addBox(2.5F, -8.0F, -6.5F, 2, 6, 2);
        this.F5horn2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.F5horn2, (float) (-Math.PI * 2.0 / 9.0), 0.0F, -1.047198F);
        this.F5horn3 = new ModelRenderer(this, 8, 6);
        this.F5horn3.addBox(-0.5F, -10.0F, -8.0F, 2, 6, 2);
        this.F5horn3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.F5horn3, (float) (-Math.PI * 2.0 / 9.0), 0.0F, 0.2094395F);
        this.F5horn4 = new ModelRenderer(this, 8, 6);
        this.F5horn4.addBox(-1.5F, -10.0F, -8.0F, 2, 6, 2);
        this.F5horn4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.F5horn4, (float) (-Math.PI * 2.0 / 9.0), 0.0F, -0.2094395F);
        this.F5horn5 = new ModelRenderer(this, 8, 6);
        this.F5horn5.addBox(-2.5F, -7.0F, -7.2F, 5, 2, 2);
        this.F5horn5.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.F5horn5, (float) (-Math.PI / 6), 0.0F, 0.0F);
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
        this.ftailS1 = new ModelRenderer(this, 38, 54);
        this.ftailS1.addBox(-2.0F, -2.0F, 0.0F, 4, 4, 6);
        this.ftailS1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.ftailS1, (float) (-Math.PI / 6), 0.0F, 0.0F);
        this.ftailS2 = new ModelRenderer(this, 38, 54);
        this.ftailS2.addBox(-2.0F, -2.0F, 0.0F, 4, 4, 6);
        this.ftailS2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.ftailS2, (float) (Math.PI / 6), 8.727E-4F, 0.0F);
        this.ftailS3 = new ModelRenderer(this, 38, 54);
        this.ftailS3.addBox(-2.0F, -2.0F, 0.0F, 4, 4, 6);
        this.ftailS3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.ftailS3, 0.0F, 0.0F, 0.0F);
        this.ftailS4 = new ModelRenderer(this, 38, 54);
        this.ftailS4.addBox(-2.0F, -2.0F, 0.0F, 4, 4, 6);
        this.ftailS4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.ftailS4, 0.0F, 0.0F, 0.0F);
        this.ftailS5 = new ModelRenderer(this, 38, 54);
        this.ftailS5.addBox(-2.0F, -2.0F, 0.0F, 4, 4, 6);
        this.ftailS5.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.ftailS5, 0.0F, 0.0F, 0.0F);
        this.ftailS6 = new ModelRenderer(this, 38, 54);
        this.ftailS6.addBox(-2.0F, -2.0F, 0.0F, 4, 4, 6);
        this.ftailS6.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.ftailS6, 0.0F, 0.0F, 0.0F);
        this.ftailS5.addChild(this.ftailS6);
        this.ftailS4.addChild(this.ftailS5);
        this.ftailS3.addChild(this.ftailS4);
        this.ftailS2.addChild(this.ftailS3);
        this.ftailS1.addChild(this.ftailS2);
        this.FroB.addChild(this.ftailS1);
        this.FroB.rotationPointX = 2.0F;
        this.FroB.rotationPointY = 10.0F;
        this.FroB.rotationPointZ = 2.0F;
        this.ftailS1.rotationPointX = -2.0F;
        this.ftailS1.rotationPointY = -2.0F;
        this.ftailS1.rotationPointZ = 0.0F;
        this.ftailS2.rotationPointX = 0.0F;
        this.ftailS2.rotationPointY = 0.0F;
        this.ftailS2.rotationPointZ = 5.0F;
        this.ftailS3.rotationPointX = 0.0F;
        this.ftailS3.rotationPointY = 0.0F;
        this.ftailS3.rotationPointZ = 5.0F;
        this.ftailS4.rotationPointX = 0.0F;
        this.ftailS4.rotationPointY = 0.0F;
        this.ftailS4.rotationPointZ = 5.0F;
        this.ftailS5.rotationPointX = 0.0F;
        this.ftailS5.rotationPointY = 0.0F;
        this.ftailS5.rotationPointZ = 5.0F;
        this.ftailS6.rotationPointX = 0.0F;
        this.ftailS6.rotationPointY = 0.0F;
        this.ftailS6.rotationPointZ = 5.0F;
        this.fear1 = new ModelRenderer(this, 12, 0);
        this.fear1.addBox(-5.0F, -5.0F, -3.0F, 1, 3, 2);
        this.fear1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.fear1, -0.4014257F, 0.0F, 0.0F);
        this.fear2 = new ModelRenderer(this, 12, 0);
        this.fear2.mirror = true;
        this.fear2.addBox(4.0F, -5.0F, -3.0F, 1, 3, 2);
        this.fear2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.fear2, -0.4014257F, 0.0F, 0.0F);
        this.rightarmshoulder = new ModelRenderer(this, 38, 0);
        this.rightarmshoulder.addBox(-6.0F, -3.0F, -3.0F, 7, 4, 6);
        this.rightarmshoulder.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.rightarmshoulder.setTextureSize(128, 64);
        this.leftarmshoulder = new ModelRenderer(this, 38, 0);
        this.leftarmshoulder.mirror = true;
        this.leftarmshoulder.addBox(-1.0F, -3.0F, -3.0F, 7, 4, 6);
        this.leftarmshoulder.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.leftarmshoulder.setTextureSize(128, 64);
        this.Fro0.addChild(this.Fhorn2);
        this.Fro0.addChild(this.Fhorn1);
        this.Fro1.addChild(this.Fhorn3);
        this.Fro1.addChild(this.Fhorn4);
        this.Fro2.addChild(this.appule);
        this.Fro2.addChild(this.F2horn1);
        this.Fro2.addChild(this.F2horn2);
        this.Fro.addChild(this.fear1);
        this.Fro.addChild(this.fear2);
        this.Fro5.addChild(this.F5horn1);
        this.Fro5.addChild(this.F5horn2);
        this.Fro5.addChild(this.F5horn3);
        this.Fro5.addChild(this.F5horn4);
        this.Fro5.addChild(this.F5horn5);
        this.Fro5r.addChild(this.F5spike1);
        this.Fro5l.addChild(this.F5spike2);
        this.Fro5b.addChild(this.F5spike3);
        this.Fro5b.addChild(this.F5spike4);
    }

    public void renderFace(DBCDisplay display) {
        RenderPlayerJBRA.glColor3f(display.bodyCM == -1 ? getDefaultColor("bodyCM", display.race) : display.bodyCM);
        tex.bindTexture(new ResourceLocation(getFaceTexture(display, "n" + display.noseType)));

        this.nose.rotateAngleY = parent.bipedHead.rotateAngleY;
        this.nose.rotateAngleX = parent.bipedHead.rotateAngleX;
        this.nose.rotationPointX = parent.bipedHead.rotationPointX;
        this.nose.rotationPointY = parent.bipedHead.rotationPointY;
        this.nose.render(0.0625F);


        String mouthDir = "";
        if (display.race == 4 && display.hasCoolerMask)
            mouthDir = "jinryuudragonbc:cc/arc/m/0A" + JRMCoreH.TransFrSkn[display.arcoState] + display.bodyType + "a.png";
        else
            mouthDir = getFaceTexture(display, "m" + display.mouthType);
        tex.bindTexture(new ResourceLocation(mouthDir));
        this.mouth.rotateAngleY = parent.bipedHead.rotateAngleY;
        this.mouth.rotateAngleX = parent.bipedHead.rotateAngleX;
        this.mouth.rotationPointX = parent.bipedHead.rotationPointX;
        this.mouth.rotationPointY = parent.bipedHead.rotationPointY;
        this.mouth.render(0.0625F);

        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        tex.bindTexture(new ResourceLocation(getFaceTexture(display, "b" + display.eyeType)));
        this.eyebase.rotateAngleY = parent.bipedHead.rotateAngleY;
        this.eyebase.rotateAngleX = parent.bipedHead.rotateAngleX;
        this.eyebase.rotationPointX = parent.bipedHead.rotationPointX;
        this.eyebase.rotationPointY = parent.bipedHead.rotationPointY;
        this.eyebase.render(0.0625F);

        if (display.race < 4) {
            RenderPlayerJBRA.glColor3f(display.hairColor);
            tex.bindTexture(new ResourceLocation(getFaceTexture(display, "w" + display.eyeType)));
            this.eyebrow.rotateAngleY = parent.bipedHead.rotateAngleY;
            this.eyebrow.rotateAngleX = parent.bipedHead.rotateAngleX;
            this.eyebrow.rotationPointX = parent.bipedHead.rotationPointX;
            this.eyebrow.rotationPointY = parent.bipedHead.rotationPointY;
            this.eyebrow.render(0.0625F);
        }

        RenderPlayerJBRA.glColor3f(display.eyeColor);
        tex.bindTexture(new ResourceLocation(getFaceTexture(display, "l" + display.eyeType)));
        this.eyeleft.rotateAngleY = parent.bipedHead.rotateAngleY;
        this.eyeleft.rotateAngleX = parent.bipedHead.rotateAngleX;
        this.eyeleft.rotationPointX = parent.bipedHead.rotationPointX;
        this.eyeleft.rotationPointY = parent.bipedHead.rotationPointY;
        this.eyeleft.render(0.0625F);

        tex.bindTexture(new ResourceLocation(getFaceTexture(display, "r" + display.eyeType)));
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

        if (race == DBCRace.HUMAN || race == DBCRace.SAIYAN || race == DBCRace.HALFSAIYAN) {
            int bodyCM = display.bodyCM == -1 ? getDefaultColor("bodyCM", race) : display.bodyCM;
            tex.bindTexture(new ResourceLocation("jinryuumodscore:cc/hum.png"));
            RenderPlayerJBRA.glColor3f(bodyCM);

        } else if (race == DBCRace.NAMEKIAN) {
            tex.bindTexture(new ResourceLocation("jinryuudragonbc:cc/nam/0nam" + display.bodyType + ".png"));
            int bodyCM = display.bodyCM == -1 ? getDefaultColor("bodyCM", race) : display.bodyCM;
            RenderPlayerJBRA.glColor3f(bodyCM);
            model.render(0.0625F);

            tex.bindTexture(new ResourceLocation("jinryuudragonbc:cc/nam/1nam" + display.bodyType + ".png"));
            int bodyC1 = display.bodyC1 == -1 ? getDefaultColor("bodyC1", race) : display.bodyC1;
            RenderPlayerJBRA.glColor3f(bodyC1);
            model.render(0.0625F);

            tex.bindTexture(new ResourceLocation("jinryuudragonbc:cc/nam/2nam" + display.bodyType + ".png"));
            int bodyC2 = display.bodyC2 == -1 ? getDefaultColor("bodyC2", race) : display.bodyC2;
            RenderPlayerJBRA.glColor3f(bodyC2);
            model.render(0.0625F);


            tex.bindTexture(new ResourceLocation("jinryuudragonbc:cc/nam/3nam" + display.bodyType + ".png"));
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
            tex.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/0B" + JRMCoreH.TransFrSkn2[st] + display.bodyType + ".png"));
            RenderPlayerJBRA.glColor3f(bodyCM);
            renderMisc((ts == 4 ? "n" : "") + "FR" + JRMCoreH.TransFrHrn[st]);

            tex.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/1B" + JRMCoreH.TransFrSkn2[st] + display.bodyType + ".png"));
            RenderPlayerJBRA.glColor3f(bodyC1);
            renderMisc((ts == 4 ? "n" : "") + "FR" + JRMCoreH.TransFrHrn[st]);

            tex.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/2B" + JRMCoreH.TransFrSkn2[st] + display.bodyType + ".png"));
            RenderPlayerJBRA.glColor3f(bodyC2);
            renderMisc((ts == 4 ? "n" : "") + "FR" + JRMCoreH.TransFrHrn[st]);

            tex.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/3B" + JRMCoreH.TransFrSkn2[st] + display.bodyType + ".png"));
            RenderPlayerJBRA.glColor3f(bodyC3);
            renderMisc((ts == 4 ? "n" : "") + "FR" + JRMCoreH.TransFrHrn[st]);

            tex.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/4B" + JRMCoreH.TransFrSkn2[st] + display.bodyType + ".png"));
            GL11.glColor3f(1f, 1f, 1f);
            renderMisc((ts == 4 ? "n" : "") + "FR" + JRMCoreH.TransFrHrn[st]);

            //ACTUAL BODY
            RenderPlayerJBRA.glColor3f(bodyCM);
            tex.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/0A" + JRMCoreH.TransFrSkn[st] + display.bodyType + ".png"));
            model.render(0.0625F);

            tex.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/1A" + JRMCoreH.TransFrSkn[st] + display.bodyType + ".png"));
            RenderPlayerJBRA.glColor3f(bodyC1);
            model.render(0.0625F);

            tex.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/2A" + JRMCoreH.TransFrSkn[st] + display.bodyType + ".png"));
            RenderPlayerJBRA.glColor3f(bodyC2);
            model.render(0.0625F);

            tex.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/3A" + JRMCoreH.TransFrSkn[st] + display.bodyType + ".png"));
            RenderPlayerJBRA.glColor3f(bodyC3);
            model.render(0.0625F);


            tex.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/4A" + JRMCoreH.TransFrSkn[st] + display.bodyType + ".png"));
            GL11.glColor3f(1f, 1f, 1f);

        } else if (race == DBCRace.MAJIN) {
            int bodyCM = display.bodyCM == -1 ? getDefaultColor("bodyCM", race) : display.bodyCM;
            tex.bindTexture(new ResourceLocation("jinryuudragonbc:cc/majin/majin.png"));
            RenderPlayerJBRA.glColor3f(bodyCM);

            //  model.render(0.0625F);


        }
        // parent.currentlyPlayerTexture = false;
    }

    public void renderMisc(String hair) {
        float par1 = 0.0625f;
        if (hair.contains("FR")) {
            this.Fro.rotateAngleY = parent.bipedHead.rotateAngleY;
            this.Fro.rotateAngleX = parent.bipedHead.rotateAngleX;
            this.Fro.rotationPointX = parent.bipedHead.rotationPointX;
            this.Fro.rotationPointY = parent.bipedHead.rotationPointY;
            this.Fro.render(par1);

            if (hair.contains("2")) {
                this.leftarmshoulder.rotationPointZ = parent.bipedLeftArm.rotationPointZ;
                this.leftarmshoulder.rotationPointY = parent.bipedLeftArm.rotationPointY;
                this.leftarmshoulder.rotationPointX = parent.bipedLeftArm.rotationPointX;
                this.leftarmshoulder.rotateAngleY = parent.bipedLeftArm.rotateAngleY;
                this.leftarmshoulder.rotateAngleX = parent.bipedLeftArm.rotateAngleX;
                this.leftarmshoulder.rotateAngleZ = parent.bipedLeftArm.rotateAngleZ;
                this.leftarmshoulder.render(par1);
                this.rightarmshoulder.rotationPointZ = parent.bipedRightArm.rotationPointZ;
                this.rightarmshoulder.rotationPointY = parent.bipedRightArm.rotationPointY;
                this.rightarmshoulder.rotationPointX = parent.bipedRightArm.rotationPointX;
                this.rightarmshoulder.rotateAngleY = parent.bipedRightArm.rotateAngleY;
                this.rightarmshoulder.rotateAngleX = parent.bipedRightArm.rotateAngleX;
                this.rightarmshoulder.rotateAngleZ = parent.bipedRightArm.rotateAngleZ;
                this.rightarmshoulder.render(par1);
            }
            if (!hair.contains("nFR")) {
                ModelRenderer var10000;
                GL11.glPushMatrix();
                this.transRot(par1, parent.bipedBody);
                GL11.glScalef(1.0F, 1.0F, 1.0F);
                this.FroB.render(par1);
                float r = MathHelper.sin(this.rot3 * 0.02F) * 0.1F;
                float r2 = MathHelper.cos(this.rot3 * 0.02F) * 0.1F;
                float r3 = MathHelper.cos(this.rot3 * 0.14F) * 0.1F;
                this.ftailS1.rotateAngleY = 0.2F;
                if (mod_JBRA.a6P9H9B) {
                    var10000 = this.ftailS1;
                    var10000.rotateAngleY += MathHelper.cos(this.rot3 * 0.09F) * 0.2F - 0.2F + r2;
                }

                this.ftailS1.rotateAngleX = -0.3F;
                this.ftailS2.rotateAngleY = 0.2F;
                if (mod_JBRA.a6P9H9B) {
                    var10000 = this.ftailS2;
                    var10000.rotateAngleY += MathHelper.cos(this.rot3 * 0.09F) * 0.2F - 0.2F + r3 + r3;
                }

                this.ftailS2.rotateAngleX = 0.4F;
                this.ftailS3.rotateAngleY = 0.1F;
                if (mod_JBRA.a6P9H9B) {
                    var10000 = this.ftailS3;
                    var10000.rotateAngleY += MathHelper.cos(this.rot3 * 0.09F) * 0.1F - 0.1F + r2 + r3;
                }

                this.ftailS3.rotateAngleX = 0.6F;
                if (mod_JBRA.a6P9H9B) {
                    var10000 = this.ftailS3;
                    var10000.rotateAngleX += MathHelper.sin(this.rot3 * 0.09F) * 0.4F + 0.3F;
                }

                this.ftailS4.rotateAngleY = 0.1F;
                if (mod_JBRA.a6P9H9B) {
                    var10000 = this.ftailS4;
                    var10000.rotateAngleY += MathHelper.cos(this.rot3 * 0.09F) * 0.4F - 0.1F + r3;
                }

                this.ftailS4.rotateAngleX = 0.3F;
                if (mod_JBRA.a6P9H9B) {
                    var10000 = this.ftailS4;
                    var10000.rotateAngleX += MathHelper.sin(this.rot3 * 0.09F) * 0.1F - 0.2F;
                }

                this.ftailS5.rotateAngleY = 0.2F;
                if (mod_JBRA.a6P9H9B) {
                    var10000 = this.ftailS5;
                    var10000.rotateAngleY += MathHelper.cos(this.rot3 * 0.09F) * 0.4F - 0.2F + r2 + r3;
                }

                this.ftailS5.rotateAngleX = -0.2F;
                if (mod_JBRA.a6P9H9B) {
                    var10000 = this.ftailS5;
                    var10000.rotateAngleX += MathHelper.sin(this.rot3 * 0.09F) * 0.1F - 0.3F;
                }

                this.ftailS6.rotateAngleY = 0.2F;
                if (mod_JBRA.a6P9H9B) {
                    var10000 = this.ftailS6;
                    var10000.rotateAngleY += MathHelper.cos(this.rot3 * 0.09F) * 0.4F - 0.2F + r3 + r3;
                }

                this.ftailS6.rotateAngleX = -0.4F;
                if (mod_JBRA.a6P9H9B) {
                    var10000 = this.ftailS6;
                    var10000.rotateAngleX += MathHelper.sin(this.rot3 * 0.09F) * 0.4F - 0.4F;
                }

                GL11.glPopMatrix();
            }

            if (hair.contains("4")) {
                this.Fro5b.rotateAngleY = parent.bipedBody.rotateAngleY;
                this.Fro5b.rotateAngleX = parent.bipedBody.rotateAngleX;
                this.Fro5b.rotationPointX = parent.bipedBody.rotationPointX;
                this.Fro5b.rotationPointY = parent.bipedBody.rotationPointY;
                this.Fro5b.render(par1);
                this.Fro5r.rotationPointX = parent.bipedRightArm.rotationPointX;
                this.Fro5r.rotationPointY = parent.bipedRightArm.rotationPointY;
                this.Fro5r.rotationPointZ = parent.bipedRightArm.rotationPointZ;
                this.Fro5r.rotateAngleY = parent.bipedRightArm.rotateAngleY;
                this.Fro5r.rotateAngleX = parent.bipedRightArm.rotateAngleX;
                this.Fro5r.rotateAngleZ = parent.bipedRightArm.rotateAngleZ;
                this.Fro5r.render(par1);
                this.Fro5l.rotationPointX = parent.bipedLeftArm.rotationPointX;
                this.Fro5l.rotationPointY = parent.bipedLeftArm.rotationPointY;
                this.Fro5l.rotationPointZ = parent.bipedLeftArm.rotationPointZ;
                this.Fro5l.rotateAngleY = parent.bipedLeftArm.rotateAngleY;
                this.Fro5l.rotateAngleX = parent.bipedLeftArm.rotateAngleX;
                this.Fro5l.rotateAngleZ = parent.bipedLeftArm.rotateAngleZ;
                this.Fro5l.render(par1);
            }


            if (hair.contains("0") || hair.contains("2") || hair.contains("1")) {
                this.Fro0.rotateAngleY = parent.bipedHead.rotateAngleY;
                this.Fro0.rotateAngleX = parent.bipedHead.rotateAngleX;
                this.Fro0.rotationPointX = parent.bipedHead.rotationPointX;
                this.Fro0.rotationPointY = parent.bipedHead.rotationPointY;
                this.Fro0.render(par1);
            }

            if (hair.contains("1") || hair.contains("2")) {
                this.Fro1.rotateAngleY = parent.bipedHead.rotateAngleY;
                this.Fro1.rotateAngleX = parent.bipedHead.rotateAngleX;
                this.Fro1.rotationPointX = parent.bipedHead.rotationPointX;
                this.Fro1.rotationPointY = parent.bipedHead.rotationPointY;
                this.Fro1.render(par1);
            }

            if (hair.contains("2")) {
                this.Fro2.rotateAngleY = parent.bipedHead.rotateAngleY;
                this.Fro2.rotateAngleX = parent.bipedHead.rotateAngleX;
                this.Fro2.rotationPointX = parent.bipedHead.rotationPointX;
                this.Fro2.rotationPointY = parent.bipedHead.rotationPointY;
                this.Fro2.render(par1);
            }

            if (hair.contains("4")) {
                this.Fro5.rotateAngleY = parent.bipedHead.rotateAngleY;
                this.Fro5.rotateAngleX = parent.bipedHead.rotateAngleX;
                this.Fro5.rotationPointX = parent.bipedHead.rotationPointX;
                this.Fro5.rotationPointY = parent.bipedHead.rotationPointY;
                this.Fro5.render(par1);

            }
        }

    }

    public void renderHairs(DBCDisplay display) {
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
        boolean aura = true;//aura anim JRMCoreH.StusEfctsClient(4, pl);
        boolean trbo = true; //turbo anim JRMCoreH.StusEfctsClient(3, pl);
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

        tex.bindTexture(new ResourceLocation("jinryuumodscore:gui/normall.png"));

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
        } catch (NumberFormatException var3) {
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
