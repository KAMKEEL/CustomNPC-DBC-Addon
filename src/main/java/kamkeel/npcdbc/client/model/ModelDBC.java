package kamkeel.npcdbc.client.model;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.client.ColorMode;
import kamkeel.npcdbc.client.model.part.*;
import kamkeel.npcdbc.client.model.part.hair.DBCHair;
import kamkeel.npcdbc.client.utils.Color;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormDisplay;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.model.ModelMPM;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.data.ModelScalePart;
import org.lwjgl.opengl.GL11;

public class ModelDBC extends ModelBase {

    private final ModelMPM parent;
    public static boolean isHurt = false;
    public float rot1;
    public float rot2;
    public float rot3;
    public float rot4;
    public float rot5;
    public float rot6;

    // DBC Parts
    public DBCHair DBCHair;
    public DBCHorns DBCHorns;
    public DBCEars DBCEars;
    public DBCBody DBCBody;
    public DBCRightArms DBCRightArms;
    public DBCLeftArms DBCLeftArms;

    // Face
    public ModelRenderer nose;
    public ModelRenderer mouth;
    public ModelRenderer eyeleft;
    public ModelRenderer eyeright;
    public ModelRenderer eyebase;
    public ModelRenderer eyebrow;

    private String SDDir = CustomNpcPlusDBC.ID + ":textures/sd/";
    private String HDDir = CustomNpcPlusDBC.ID + ":textures/hd/";
    public DBCDisplay display;

    public ModelDBC(ModelMPM mpm) {
        this.parent = mpm;
        this.textureHeight = mpm.textureHeight;
        this.textureWidth = mpm.textureWidth;

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


        this.parent.bipedHead.addChild(DBCHair = new DBCHair(mpm));
        this.parent.bipedHead.addChild(DBCHorns = new DBCHorns(mpm));
        this.parent.bipedHead.addChild(DBCEars = new DBCEars(mpm));
        this.parent.bipedBody.addChild(DBCBody = new DBCBody(mpm));

        this.parent.bipedRightArm.addChild(this.DBCRightArms = new DBCRightArms(mpm));
        this.parent.bipedLeftArm.addChild(this.DBCLeftArms = new DBCLeftArms(mpm));
    }

    public void setPlayerData(EntityCustomNpc entity) {
        display = ((INPCDisplay) entity.display).getDBCDisplay();
        this.DBCHair.setData(entity.modelData, entity, display);
        this.DBCHorns.setData(entity.modelData, entity, display);
        this.DBCEars.setData(entity.modelData, entity, display);
        this.DBCBody.setData(entity.modelData, entity, display);
        this.DBCRightArms.setData(entity.modelData, entity, display);
        this.DBCLeftArms.setData(entity.modelData, entity, display);

    }

    public void setHurt(EntityCustomNpc entity) {
        DBCDisplay display = ((INPCDisplay) entity.display).getDBCDisplay();
        isHurt = false;
        if (display.isEnabled() && display.useSkin && (entity.hurtTime > 0 || entity.isKilled())) {
            isHurt = true;
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
    }

    public void clearHurt() {
        if (!isHurt)
            return;
        isHurt = false;
    }

    public void renderFace(EntityCustomNpc entity, DBCDisplay display) {
        if (display.useSkin) {
            float y = entity.modelData.getBodyY();
            ModelScalePart head = entity.modelData.modelScale.head;

            int eyeColor = display.eyeColor;
            int eyeBrowColor = display.race == DBCRace.NAMEKIAN ? display.bodyCM : display.hairColor;
            int bodyCM = display.bodyCM;

            boolean isSaiyan = DBCRace.isSaiyan(display.race);
            boolean hasArcoMask = display.hasArcoMask, isBerserk = false, hasEyebrows = display.hasEyebrows;
            boolean isSSJ4 = display.hairType.equals("ssj4"), isOozaru = display.hairType.equals("oozaru");

            boolean HD = ConfigDBCClient.EnableHDTextures;

            //////////////////////////////////////////////////////
            //////////////////////////////////////////////////////
            //Forms
            Form form = display.getForm();
            if (form != null) {
                FormDisplay d = form.display;

                if (d.hasColor("eye"))
                    eyeColor = d.eyeColor;
                if (d.hasColor("hair"))
                    eyeBrowColor = d.hairColor;
                if (d.hasColor("bodycm"))
                    bodyCM = d.bodyCM;

                if (d.hasArcoMask)
                    hasArcoMask = true;

                hasEyebrows = d.hasEyebrows;
                if (d.hairType.equals("ssj3"))
                    hasEyebrows = false;
                else if (d.hairType.equals("ssj4"))
                    isSSJ4 = true;
                else if (d.hairType.equals("oozaru")) {
                    isOozaru = true;
                    if (d.eyeColor == -1)
                        eyeColor = 0xFF0000;
                }

                isBerserk = d.isBerserk;
            }
            //////////////////////////////////////////////////////
            //////////////////////////////////////////////////////
            boolean renderSSJ4Face = isSSJ4 && HD && hasEyebrows && isSaiyan;
            if (isOozaru && isSaiyan) {
                ClientProxy.bindTexture(new ResourceLocation((HD ? HDDir : SDDir) + "oozaru/oozarueyes.png")); //eyes
                ColorMode.applyModelColor(eyeColor, isHurt);
                this.eyebase.rotateAngleY = parent.bipedHead.rotateAngleY;
                this.eyebase.rotateAngleX = parent.bipedHead.rotateAngleX;
                this.eyebase.rotationPointX = parent.bipedHead.rotationPointX;
                this.eyebase.rotationPointY = parent.bipedHead.rotationPointY;
                GL11.glPushMatrix();
                GL11.glTranslatef(0, y, 0);
                GL11.glScalef(head.scaleX, head.scaleY, head.scaleZ);
                this.eyebase.render(0.0625F);
                GL11.glPopMatrix();

                ColorMode.applyModelColor(bodyCM, isHurt);
                DBCBody.Oozaru.rotateAngleY = parent.bipedHead.rotateAngleY;
                DBCBody.Oozaru.rotateAngleX = parent.bipedHead.rotateAngleX;
                DBCBody.Oozaru.rotationPointX = parent.bipedHead.rotationPointX;
                DBCBody.Oozaru.rotationPointY = parent.bipedHead.rotationPointY;
                GL11.glPushMatrix();
                GL11.glTranslatef(0, y, 0);
                GL11.glScalef(head.scaleX, head.scaleY, head.scaleZ);
                DBCBody.Oozaru.render(0.0625f);
                GL11.glPopMatrix();

                return;
            }
            ColorMode.applyModelColor(bodyCM, isHurt);
            if (renderSSJ4Face)
                ClientProxy.bindTexture(new ResourceLocation((HD ? HDDir + "base/nose/" : "jinryuumodscore:cc/") + "humn" + display.noseType + ".png"));
            else
                ClientProxy.bindTexture(new ResourceLocation(getFaceTexture(display, "n" + display.noseType)));

            this.nose.rotateAngleY = parent.bipedHead.rotateAngleY;
            this.nose.rotateAngleX = parent.bipedHead.rotateAngleX;
            this.nose.rotationPointX = parent.bipedHead.rotationPointX;
            this.nose.rotationPointY = parent.bipedHead.rotationPointY;

            GL11.glPushMatrix();
            GL11.glTranslatef(0, y, 0);
            GL11.glScalef(head.scaleX, head.scaleY, head.scaleZ);
            this.nose.render(0.0625F);
            GL11.glPopMatrix();

            if (renderSSJ4Face)
                return;

            String mouthDir = "";
            if (display.race == 4 && hasArcoMask)
                mouthDir = "jinryuudragonbc:cc/arc/m/0A" + 2 + display.bodyType + "a.png";
            else
                mouthDir = getFaceTexture(display, "m" + display.mouthType);

            ClientProxy.bindTexture(new ResourceLocation(mouthDir));
            this.mouth.rotateAngleY = parent.bipedHead.rotateAngleY;
            this.mouth.rotateAngleX = parent.bipedHead.rotateAngleX;
            this.mouth.rotationPointX = parent.bipedHead.rotationPointX;
            this.mouth.rotationPointY = parent.bipedHead.rotationPointY;

            GL11.glPushMatrix();
            GL11.glTranslatef(0, y, 0);
            GL11.glScalef(head.scaleX, head.scaleY, head.scaleZ);
            this.mouth.render(0.0625F);
            GL11.glPopMatrix();

            GL11.glColor3f(1.0f, 1.0f, 1.0f);
            ClientProxy.bindTexture(new ResourceLocation(getFaceTexture(display, "b" + display.eyeType)));
            this.eyebase.rotateAngleY = parent.bipedHead.rotateAngleY;
            this.eyebase.rotateAngleX = parent.bipedHead.rotateAngleX;
            this.eyebase.rotationPointX = parent.bipedHead.rotationPointX;
            this.eyebase.rotationPointY = parent.bipedHead.rotationPointY;

            GL11.glPushMatrix();
            GL11.glTranslatef(0, y, 0);
            GL11.glScalef(head.scaleX, head.scaleY, head.scaleZ);
            this.eyebase.render(0.0625F);
            GL11.glPopMatrix();

            if (display.race < 4) {
                if(display.race != DBCRace.NAMEKIAN)
                    ColorMode.applyModelColor(eyeBrowColor, isHurt);
                else{
                    ColorMode.applyModelColor(bodyCM, isHurt);
                }
                if (!hasEyebrows && display.race != DBCRace.NAMEKIAN)
                    ClientProxy.bindTexture(new ResourceLocation("jinryuumodscore", "cc/ssj3eyebrow/" + "humw" + display.eyeType + ".png"));
                else
                    ClientProxy.bindTexture(new ResourceLocation(getFaceTexture(display, "w" + display.eyeType)));
                this.eyebrow.rotateAngleY = parent.bipedHead.rotateAngleY;
                this.eyebrow.rotateAngleX = parent.bipedHead.rotateAngleX;
                this.eyebrow.rotationPointX = parent.bipedHead.rotationPointX;
                this.eyebrow.rotationPointY = parent.bipedHead.rotationPointY;

                GL11.glPushMatrix();
                GL11.glTranslatef(0, y, 0);
                GL11.glScalef(head.scaleX, head.scaleY, head.scaleZ);
                this.eyebrow.render(0.0625F);
                GL11.glPopMatrix();
            }


            if (!isBerserk) {
                ColorMode.applyModelColor(eyeColor, isHurt);
                ClientProxy.bindTexture(new ResourceLocation(getFaceTexture(display, "l" + display.eyeType)));
                this.eyeleft.rotateAngleY = parent.bipedHead.rotateAngleY;
                this.eyeleft.rotateAngleX = parent.bipedHead.rotateAngleX;
                this.eyeleft.rotationPointX = parent.bipedHead.rotationPointX;
                this.eyeleft.rotationPointY = parent.bipedHead.rotationPointY;

                GL11.glPushMatrix();
                GL11.glTranslatef(0, y, 0);
                GL11.glScalef(head.scaleX, head.scaleY, head.scaleZ);
                this.eyeleft.render(0.0625F);
                GL11.glPopMatrix();

                ClientProxy.bindTexture(new ResourceLocation(getFaceTexture(display, "r" + display.eyeType)));
                this.eyeright.rotateAngleY = parent.bipedHead.rotateAngleY;
                this.eyeright.rotateAngleX = parent.bipedHead.rotateAngleX;
                this.eyeright.rotationPointX = parent.bipedHead.rotationPointX;
                this.eyeright.rotationPointY = parent.bipedHead.rotationPointY;
                GL11.glPushMatrix();
                GL11.glTranslatef(0, y, 0);
                GL11.glScalef(head.scaleX, head.scaleY, head.scaleZ);
                this.eyeright.render(0.0625F);
                GL11.glPopMatrix();
            }
        }
    }

    public void renderSSJ4Face(int eyeColor, int furColor, int hairColor, int bodyCM, boolean isBerserk, boolean hasEyebrows, int eyeType) {
        boolean isHidden = DBCHair.isHidden;
        DBCHair.isHidden = true;

        ColorMode.applyModelColor(0xffffff, isHurt);
        ClientProxy.bindTexture(new ResourceLocation(HDDir + "ssj4/ssj4eyewhite.png"));
        parent.bipedHead.render(1F / 16F);

        if (!isBerserk) {
            ColorMode.applyModelColor(eyeColor, isHurt);
            ClientProxy.bindTexture(new ResourceLocation(HDDir + "ssj4/ssj4pupils.png"));
            parent.bipedHead.render(0.0625F);
        }

        ColorMode.applyModelColor(furColor, isHurt);
        ClientProxy.bindTexture(new ResourceLocation(HDDir + "ssj4/ssj4brows.png"));
        parent.bipedHead.render(1F / 16F);

        ColorMode.applyModelColor(hairColor, isHurt);
        ClientProxy.bindTexture(new ResourceLocation(HDDir + "ssj4/ssj4brows2.png"));
        parent.bipedHead.render(1F / 16F);


        ColorMode.applyModelColor(bodyCM, isHurt);
        ClientProxy.bindTexture(new ResourceLocation(HDDir + "ssj4/ssj4mouth0.png"));
        parent.bipedHead.render(1F / 16F);

        ColorMode.applyModelColor(bodyCM, isHurt);
        ClientProxy.bindTexture(new ResourceLocation(HDDir + "ssj4/ssj4shade.png"));
        parent.bipedHead.render(1F / 16F);

        DBCHair.isHidden = isHidden;
    }

    public void renderBodySkin(DBCDisplay display, ModelRenderer model) {
        if (display.useSkin) {

            int eyeColor = display.eyeColor;
            int hairColor = display.hairColor;

            int bodyCM = display.bodyCM;
            int bodyC1 = display.bodyC1;
            int bodyC2 = display.bodyC2;
            int bodyC3 = display.bodyC3;
            int furColor = display.furColor;
            boolean hasFur = display.hasFur;
            boolean isSSJ4 = display.hairType.equals("ssj4"), isOozaru = display.hairType.equals("oozaru"), hasEyebrows = display.hasEyebrows, isBerserk = false;
            //  ModelPartData tailData = ((EntityCustomNpc) display.npc).modelData.getOrCreatePart("tail");

            boolean HD = ConfigDBCClient.EnableHDTextures;
            //////////////////////////////////////////////////////
            //////////////////////////////////////////////////////
            //Forms
            Form form = display.getForm();
            if (form != null) {
                FormDisplay d = form.display;
                if (d.hasColor("eye"))
                    eyeColor = d.eyeColor;
                if (d.hasColor("hair"))
                    hairColor = d.hairColor;
                if (d.hasColor("bodycm"))
                    bodyCM = d.bodyCM;
                if (d.hasColor("bodyc1"))
                    bodyC1 = d.bodyC1;
                if (d.hasColor("bodyc2"))
                    bodyC2 = d.bodyC2;
                if (d.hasColor("bodyc3"))
                    bodyC3 = d.bodyC3;
                if (d.hasColor("fur"))
                    furColor = d.furColor;

                hasFur = d.hasBodyFur;
                if (d.hairType.equals("ssj4")) {
                    isSSJ4 = true;
                    if (d.eyeColor == -1)
                        eyeColor = 0xF3C807;
                } else if (d.hairType.equals("oozaru")) {
                    isOozaru = true;
                }
                hasEyebrows = d.hasEyebrows;
                isBerserk = d.isBerserk;
            }
            //////////////////////////////////////////////////////
            //////////////////////////////////////////////////////

            int race = display.race;
            if (race == DBCRace.HUMAN || DBCRace.isSaiyan(race)) {
                ClientProxy.bindTexture(new ResourceLocation("jinryuumodscore:cc/hum.png"));
                ColorMode.applyModelColor(bodyCM, isHurt);

                if (DBCRace.isSaiyan(race)) {
                    if (hasFur || isSSJ4 || isOozaru) {
                        ClientProxy.bindTexture(new ResourceLocation((HD ? HDDir + "base/" : "jinryuumodscore:cc/") + "hum.png"));
                        model.render(0.0625F); //important
                        if (isSSJ4) {
                            if (furColor == -1)
                                furColor = 0xDA152C;
                            if (HD && hasEyebrows)
                                renderSSJ4Face(eyeColor, furColor, hairColor, bodyCM, isBerserk, hasEyebrows, display.eyeType);
                        }

                        if (isOozaru) {
                            if (furColor == -1)
                                furColor = 6498048;
                            ClientProxy.bindTexture(new ResourceLocation(HD ? HDDir + "oozaru/oozaru1.png" : "jinryuudragonbc:cc/oozaru1.png")); //oozaru hairless body
                            ColorMode.applyModelColor(bodyCM, isHurt);
                            model.render(0.0625F);

                            ClientProxy.bindTexture(new ResourceLocation(HD ? HDDir + "oozaru/oozaru2.png" : "jinryuudragonbc:cc/oozaru2.png"));  //the fur
                        } else {
                            ClientProxy.bindTexture(new ResourceLocation(HD ? HDDir + "ssj4/ss4b.png" : "jinryuudragonbc:cc/ss4b.png"));
                        }
                        ColorMode.applyModelColor(furColor, isHurt);
                    }
                }

            } else if (race == DBCRace.NAMEKIAN) {
                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/nam/0nam" + display.bodyType + ".png"));
                ColorMode.applyModelColor(bodyCM, isHurt);
                model.render(0.0625F);

                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/nam/1nam" + display.bodyType + ".png"));
                ColorMode.applyModelColor(bodyC1, isHurt);
                model.render(0.0625F);

                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/nam/2nam" + display.bodyType + ".png"));
                ColorMode.applyModelColor(bodyC2, isHurt);
                model.render(0.0625F);

                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/nam/3nam" + display.bodyType + ".png"));
                GL11.glColor3f(1f, 1f, 1f);
            } else if (race == DBCRace.ARCOSIAN) {
                int st = display.getCurrentArcoState();
                ColorMode.applyModelColor(bodyCM, isHurt);
                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/0A" + JRMCoreH.TransFrSkn[st] + display.bodyType + ".png"));
                model.render(0.0625F);

                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/1A" + JRMCoreH.TransFrSkn[st] + display.bodyType + ".png"));
                ColorMode.applyModelColor(bodyC1, isHurt);
                model.render(0.0625F);

                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/2A" + JRMCoreH.TransFrSkn[st] + display.bodyType + ".png"));
                ColorMode.applyModelColor(bodyC2, isHurt);
                model.render(0.0625F);

                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/3A" + JRMCoreH.TransFrSkn[st] + display.bodyType + ".png"));
                ColorMode.applyModelColor(bodyC3, isHurt);
                model.render(0.0625F);

                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/4A" + JRMCoreH.TransFrSkn[st] + display.bodyType + ".png"));
                GL11.glColor3f(1f, 1f, 1f);
            } else if (race == DBCRace.MAJIN) {
                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/majin/majin.png"));
                ColorMode.applyModelColor(bodyCM, isHurt);
            }
        }
    }


    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {
    }

    public String getFaceTexture(DBCDisplay display, String t) {
        int race = display.race;
        String tex = "";
        int arcoState = display.getArco();

        if (race == DBCRace.HUMAN || race == DBCRace.SAIYAN || race == DBCRace.HALFSAIYAN)
            tex = "jinryuumodscore:cc/hum" + t + ".png";
        else if (race == DBCRace.NAMEKIAN)
            tex = "jinryuudragonbc:cc/nam/4nam" + t + ".png";
        else if (race == DBCRace.ARCOSIAN)
            tex = "jinryuudragonbc:cc/arc/m/4A" + JRMCoreH.TransFrSkn[arcoState] + display.bodyType + t + ".png";
        else if (race == DBCRace.MAJIN)
            tex = "jinryuudragonbc:cc/majin/majin" + t + ".png";
        return tex;
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
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
