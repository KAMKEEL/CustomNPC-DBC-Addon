package kamkeel.npcdbc.client.model;

import JinRyuu.JBRA.RenderPlayerJBRA;
import JinRyuu.JRMCore.JRMCoreClient;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.JRMCoreHJBRA;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.client.ColorMode;
import kamkeel.npcdbc.client.model.part.*;
import kamkeel.npcdbc.client.model.part.hair.DBCHair;
import kamkeel.npcdbc.client.utils.Color;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.aura.AuraDisplay;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormDisplay;
import kamkeel.npcdbc.data.form.FormOverlay;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.data.npc.KiWeaponData;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.ClientCacheHandler;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.model.ModelMPM;
import noppes.npcs.client.renderer.ImageData;
import noppes.npcs.constants.EnumAnimation;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.data.ModelScalePart;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashSet;

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

    public final boolean alexArms;

    public ModelDBC(ModelMPM mpm, boolean alexArms) {
        this.parent = mpm;
        this.textureHeight = mpm.textureHeight;
        this.textureWidth = mpm.textureWidth;
        this.alexArms = alexArms;

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

    public void renderFace(EntityCustomNpc entity, DBCDisplay display, ModelRenderer bipeadHead) {
        if (display.useSkin) {
            float y = entity.modelData.getBodyY();
            ModelScalePart head = entity.modelData.modelScale.head;

            int eyeColor = display.eyeColor;
            int eyeBrowColor = display.race == DBCRace.NAMEKIAN ? display.bodyCM : display.hairColor;
            int bodyCM = display.bodyCM;
            FormDisplay.FaceData faceData = display.faceData;

            boolean isSaiyan = DBCRace.isSaiyan(display.race);
            boolean hasArcoMask = display.hasArcoMask, isBerserk = false, hasEyebrows = display.hasEyebrows;
            boolean isSSJ4 = display.hairType.equals("ssj4"), isOozaru = display.hairType.equals("oozaru");
            boolean isSfH = isSaiyan && display.hasFur && display.furType == 2;

            boolean HD = ConfigDBCClient.EnableHDTextures;

            //////////////////////////////////////////////////////
            //////////////////////////////////////////////////////
            //Forms
            Form form = display.getForm();
            if (form != null) {
                FormDisplay d = form.display;
                FormDisplay.BodyColor customClr = display.formColor;

                if (customClr.hasAnyColor(d, "eye"))
                    eyeColor = customClr.getProperColor(d, "eye");
                if (customClr.hasAnyColor(d, "hair"))
                    eyeBrowColor = customClr.getProperColor(d, "hair");
                if (customClr.hasAnyColor(d, "bodycm"))
                    bodyCM = customClr.getProperColor(d, "bodyCM");

                if (d.hasBodyFur && d.furType == 2)
                    isSfH = true;

                if (d.hasArcoMask)
                    hasArcoMask = true;

                hasEyebrows = d.hasEyebrows;
                if (d.hairType.equals("ssj3"))
                    hasEyebrows = false;
                else if (d.hairType.equals("ssj4"))
                    isSSJ4 = true;
                else if (d.hairType.equals("oozaru")) {
                    isOozaru = true;
                    if (customClr.getProperColor(d, "eye") == -1)
                        eyeColor = 0xFF0000;
                }

                isBerserk = d.isBerserk;
                faceData = d.faceData;
            }
            //////////////////////////////////////////////////////
            //////////////////////////////////////////////////////
            boolean renderSSJ4Face = isSSJ4 && HD && hasEyebrows && isSaiyan;
            if (isOozaru && isSaiyan) {
                ClientProxy.bindTexture(new ResourceLocation((HD ? HDDir : SDDir) + "oozaru/oozarueyes.png")); //eyes
                ColorMode.applyModelColor(eyeColor, this.parent.alpha, isHurt);
                this.eyebase.rotateAngleY = parent.bipedHead.rotateAngleY;
                this.eyebase.rotateAngleX = parent.bipedHead.rotateAngleX;
                this.eyebase.rotateAngleZ = parent.bipedHead.rotateAngleZ;
                this.eyebase.rotationPointX = parent.bipedHead.rotationPointX;
                this.eyebase.rotationPointY = parent.bipedHead.rotationPointY;
                this.eyebase.rotationPointZ = parent.bipedHead.rotationPointZ;
                GL11.glPushMatrix();
                GL11.glTranslatef(0, y, 0);
                GL11.glScalef(head.scaleX, head.scaleY, head.scaleZ);
                this.eyebase.render(0.0625F);
                GL11.glPopMatrix();

                ColorMode.applyModelColor(bodyCM, this.parent.alpha, isHurt);
                DBCBody.Oozaru.rotateAngleY = parent.bipedHead.rotateAngleY;
                DBCBody.Oozaru.rotateAngleX = parent.bipedHead.rotateAngleX;
                DBCBody.Oozaru.rotateAngleZ = parent.bipedHead.rotateAngleZ;
                DBCBody.Oozaru.rotationPointX = parent.bipedHead.rotationPointX;
                DBCBody.Oozaru.rotationPointY = parent.bipedHead.rotationPointY;
                DBCBody.Oozaru.rotationPointZ = parent.bipedHead.rotationPointZ;
                GL11.glPushMatrix();
                GL11.glTranslatef(0, y, 0);
                GL11.glScalef(head.scaleX, head.scaleY, head.scaleZ);
                DBCBody.Oozaru.render(0.0625f);
                GL11.glPopMatrix();

                return;
            }
            ColorMode.applyModelColor(bodyCM, this.parent.alpha, isHurt);

            if (!isSfH) {
                if (!faceData.hasNose()) {
                    ClientProxy.bindTexture(new ResourceLocation(getFaceTexture(display, "n" + display.noseType)));

                    this.nose.rotateAngleY = parent.bipedHead.rotateAngleY;
                    this.nose.rotateAngleX = parent.bipedHead.rotateAngleX;
                    this.nose.rotateAngleZ = parent.bipedHead.rotateAngleZ;
                    this.nose.rotationPointX = parent.bipedHead.rotationPointX;
                    this.nose.rotationPointY = parent.bipedHead.rotationPointY;
                    this.nose.rotationPointZ = parent.bipedHead.rotationPointZ;

                    GL11.glPushMatrix();
                    GL11.glTranslatef(0, y, 0);
                    GL11.glScalef(head.scaleX, head.scaleY, head.scaleZ);
                    this.nose.render(0.0625F);
                    GL11.glPopMatrix();
                }

                if (!faceData.hasMouth()) {
                    String mouthDir = "";
                    if (display.race == 4 && hasArcoMask)
                        mouthDir = "jinryuudragonbc:cc/arc/m/0A" + 2 + display.bodyType + "a.png";
                    else
                        mouthDir = getFaceTexture(display, "m" + display.mouthType);

                    ClientProxy.bindTexture(new ResourceLocation(mouthDir));
                    this.mouth.rotateAngleY = parent.bipedHead.rotateAngleY;
                    this.mouth.rotateAngleX = parent.bipedHead.rotateAngleX;
                    this.mouth.rotateAngleZ = parent.bipedHead.rotateAngleZ;
                    this.mouth.rotationPointX = parent.bipedHead.rotationPointX;
                    this.mouth.rotationPointY = parent.bipedHead.rotationPointY;
                    this.mouth.rotationPointZ = parent.bipedHead.rotationPointZ;

                    GL11.glPushMatrix();
                    GL11.glTranslatef(0, y, 0);
                    GL11.glScalef(head.scaleX, head.scaleY, head.scaleZ);
                    this.mouth.render(0.0625F);
                    GL11.glPopMatrix();
                }

                if (!renderSSJ4Face) {
                    if (!faceData.hasWhite()) {
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, this.parent.alpha);
                        ClientProxy.bindTexture(new ResourceLocation(getFaceTexture(display, "b" + display.eyeType)));
                        this.eyebase.rotateAngleY = parent.bipedHead.rotateAngleY;
                        this.eyebase.rotateAngleX = parent.bipedHead.rotateAngleX;
                        this.eyebase.rotateAngleZ = parent.bipedHead.rotateAngleZ;
                        this.eyebase.rotationPointX = parent.bipedHead.rotationPointX;
                        this.eyebase.rotationPointY = parent.bipedHead.rotationPointY;
                        this.eyebase.rotationPointZ = parent.bipedHead.rotationPointZ;

                        GL11.glPushMatrix();
                        GL11.glTranslatef(0, y, 0);
                        GL11.glScalef(head.scaleX, head.scaleY, head.scaleZ);
                        this.eyebase.render(0.0625F);
                        GL11.glPopMatrix();
                    }

                    if (display.race < 4 && !faceData.hasEyebrows()) {
                        if (display.race != DBCRace.NAMEKIAN)
                            ColorMode.applyModelColor(eyeBrowColor, this.parent.alpha, isHurt);
                        else {
                            ColorMode.applyModelColor(bodyCM, this.parent.alpha, isHurt);
                        }
                        if (!hasEyebrows && display.race != DBCRace.NAMEKIAN)
                            ClientProxy.bindTexture(new ResourceLocation("jinryuumodscore", "cc/ssj3eyebrow/" + "humw" + display.eyeType + ".png"));
                        else
                            ClientProxy.bindTexture(new ResourceLocation(getFaceTexture(display, "w" + display.eyeType)));
                        this.eyebrow.rotateAngleY = parent.bipedHead.rotateAngleY;
                        this.eyebrow.rotateAngleX = parent.bipedHead.rotateAngleX;
                        this.eyebrow.rotateAngleZ = parent.bipedHead.rotateAngleZ;
                        this.eyebrow.rotationPointX = parent.bipedHead.rotationPointX;
                        this.eyebrow.rotationPointY = parent.bipedHead.rotationPointY;
                        this.eyebrow.rotationPointZ = parent.bipedHead.rotationPointZ;

                        GL11.glPushMatrix();
                        GL11.glTranslatef(0, y, 0);
                        GL11.glScalef(head.scaleX, head.scaleY, head.scaleZ);
                        this.eyebrow.render(0.0625F);
                        GL11.glPopMatrix();
                    }


                    if (!isBerserk) {
                        if (!faceData.hasLeftEye()) {
                            ColorMode.applyModelColor(eyeColor, this.parent.alpha, isHurt);
                            ClientProxy.bindTexture(new ResourceLocation(getFaceTexture(display, "l" + display.eyeType)));
                            this.eyeleft.rotateAngleY = parent.bipedHead.rotateAngleY;
                            this.eyeleft.rotateAngleX = parent.bipedHead.rotateAngleX;
                            this.eyeleft.rotateAngleZ = parent.bipedHead.rotateAngleZ;
                            this.eyeleft.rotationPointX = parent.bipedHead.rotationPointX;
                            this.eyeleft.rotationPointY = parent.bipedHead.rotationPointY;
                            this.eyeleft.rotationPointZ = parent.bipedHead.rotationPointZ;

                            GL11.glPushMatrix();
                            GL11.glTranslatef(0, y, 0);
                            GL11.glScalef(head.scaleX, head.scaleY, head.scaleZ);
                            this.eyeleft.render(0.0625F);
                            GL11.glPopMatrix();
                        }

                        if (!faceData.hasRightEye()) {
                            ColorMode.applyModelColor(eyeColor, this.parent.alpha, isHurt);
                            ClientProxy.bindTexture(new ResourceLocation(getFaceTexture(display, "r" + display.eyeType)));
                            this.eyeright.rotateAngleY = parent.bipedHead.rotateAngleY;
                            this.eyeright.rotateAngleX = parent.bipedHead.rotateAngleX;
                            this.eyeright.rotateAngleZ = parent.bipedHead.rotateAngleZ;
                            this.eyeright.rotationPointX = parent.bipedHead.rotationPointX;
                            this.eyeright.rotationPointY = parent.bipedHead.rotationPointY;
                            this.eyeright.rotationPointZ = parent.bipedHead.rotationPointZ;
                            GL11.glPushMatrix();
                            GL11.glTranslatef(0, y, 0);
                            GL11.glScalef(head.scaleX, head.scaleY, head.scaleZ);
                            this.eyeright.render(0.0625F);
                            GL11.glPopMatrix();
                        }
                    }
                }
            }

            if (form != null &&form.display.overlays.hasFaceOverlays) {
                renderFaceOverlays(form, display, bipeadHead);
            }
        }
    }

    private void renderFaceOverlays(Form form, DBCDisplay display, ModelRenderer bipeadHead) {
        FormOverlay overlayData = form.display.overlays;

        for (FormOverlay.Face faceData : overlayData.getFaces().toArray(new FormOverlay.Face[0])) {
            if (faceData.isEnabled()) {
                String faceTexture = faceData.getTexture(display.eyeType);
                ImageData imageData = ClientCacheHandler.getImageData(faceTexture);
                if (imageData == null || !imageData.imageLoaded())
                    continue;

                int color = getProperColor(form.display, display, faceData.getColor(), faceData.colorType);

                bipeadHead.render(0.0625F); //render MAIN skin (by this point its texture is only bound, not rendered yet)

                if (!bindImageDataTexture(imageData, color, faceData.getAlpha()))
                    continue;

                GL11.glPushMatrix();
                float scale = 1.002511f;
                GL11.glScalef(scale, scale, scale);
                bipeadHead.render(0.0625F);
                GL11.glPopMatrix();
            }
        }
    }

    private static int getProperColor(FormDisplay display, DBCDisplay npcDisplay, int customColor, FormOverlay.ColorType colorType) {
        int furColor = display.bodyColors.furColor;
        int hairColor = display.bodyColors.hairColor;
        int eyeColor = display.bodyColors.eyeColor;

        return colorType == FormOverlay.ColorType.Body ?
            npcDisplay.bodyCM : colorType == FormOverlay.ColorType.Eye ?
            eyeColor : colorType == FormOverlay.ColorType.Hair ?
            hairColor : colorType == FormOverlay.ColorType.Fur ?
            furColor : customColor;
    }

    private boolean bindImageDataTexture(ImageData data, int color, float alpha) {
        ResourceLocation location = data.getLocation();
        if (location != null && !data.invalid()) {
            try {
                ColorMode.glColorInt(color, alpha);
                ClientProxy.bindTexture(location);
                return true;
            } catch (Exception exception) {
                return false;
            }
        }

        return false;
    }

    public void renderSSJ4Face(int eyeColor, int furColor, int hairColor, int bodyCM, boolean isBerserk, boolean hasEyebrows, int eyeType) {
        boolean isHidden = DBCHair.isHidden;
        DBCHair.isHidden = true;

        String eyeDir = "ssj4/face_" + eyeType + "/";
        ColorMode.applyModelColor(0xffffff, this.parent.alpha, isHurt);
        ClientProxy.bindTexture(new ResourceLocation(HDDir + eyeDir + "ssj4eyewhite.png"));
        parent.bipedHead.render(1F / 16F);

        if (!isBerserk) {
            ColorMode.applyModelColor(eyeColor, this.parent.alpha, isHurt);
            ClientProxy.bindTexture(new ResourceLocation(HDDir + eyeDir + "ssj4pupils.png"));
            parent.bipedHead.render(0.0625F);
        }

        ColorMode.applyModelColor(furColor, this.parent.alpha, isHurt);
        ClientProxy.bindTexture(new ResourceLocation(HDDir + eyeDir + "ssj4brows.png"));
        parent.bipedHead.render(1F / 16F);

        ColorMode.applyModelColor(hairColor, this.parent.alpha, isHurt);
        ClientProxy.bindTexture(new ResourceLocation(HDDir + eyeDir + "ssj4brows2.png"));
        parent.bipedHead.render(1F / 16F);

        ColorMode.applyModelColor(bodyCM, this.parent.alpha, isHurt);
        ClientProxy.bindTexture(new ResourceLocation(HDDir + eyeDir + "ssj4shade.png"));
        parent.bipedHead.render(1F / 16F);

        DBCHair.isHidden = isHidden;
    }

    public void renderSaviorFace(int eyeColor) {
        boolean isHidden = DBCHair.isHidden;
        DBCHair.isHidden = true;

        String eyeDir = "savior/";
        ColorMode.applyModelColor(0xffffff, this.parent.alpha, isHurt);
        ClientProxy.bindTexture(new ResourceLocation((ConfigDBCClient.EnableHDTextures ? HDDir : SDDir) + eyeDir + "saviormouth.png"));
        parent.bipedHead.render(1F / 16F);

        ColorMode.applyModelColor(eyeColor, this.parent.alpha, isHurt);
        ClientProxy.bindTexture(new ResourceLocation((ConfigDBCClient.EnableHDTextures ? HDDir : SDDir) + eyeDir + "savioreyes.png"));
        parent.bipedHead.render(0.0625F);

        ColorMode.applyModelColor(eyeColor, this.parent.alpha, isHurt);
        ClientProxy.bindTexture(new ResourceLocation((ConfigDBCClient.EnableHDTextures ? HDDir : SDDir) + eyeDir + "saviorchest.png"));
        parent.bipedBody.render(0.0625F);

        DBCHair.isHidden = isHidden;
    }

    public void renderSSJ3Face(int eyeColor, int hairColor, int bodyCM, boolean isBerserk, int eyeType) {
        boolean isHidden = DBCHair.isHidden;
        DBCHair.isHidden = true;

        String eyeDir = "ssj3/face_" + eyeType + "/";
        ColorMode.applyModelColor(0xffffff, this.parent.alpha, isHurt);
        ClientProxy.bindTexture(new ResourceLocation(HDDir + eyeDir + "ssj3eyewhite.png"));
        parent.bipedHead.render(1F / 16F);

        if (!isBerserk) {
            ColorMode.applyModelColor(eyeColor, this.parent.alpha, isHurt);
            ClientProxy.bindTexture(new ResourceLocation(HDDir + eyeDir + "ssj3pupils.png"));
            parent.bipedHead.render(0.0625F);
        }

        ColorMode.applyModelColor(hairColor, this.parent.alpha, isHurt);
        ClientProxy.bindTexture(new ResourceLocation(HDDir + eyeDir + "ssj3brows.png"));
        parent.bipedHead.render(1F / 16F);

        ColorMode.applyModelColor(bodyCM, this.parent.alpha, isHurt);
        ClientProxy.bindTexture(new ResourceLocation(HDDir + eyeDir + "ssj3shade.png"));
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
            int furType = display.furType;
            boolean hasFur = display.hasFur;
            boolean isSSJ4 = display.hairType.equals("ssj4"), isOozaru = display.hairType.equals("oozaru"), isSSJ3 = display.hairType.equals("ssj3"), hasEyebrows = display.hasEyebrows, isBerserk = false;
            //  ModelPartData tailData = ((EntityCustomNpc) display.npc).modelData.getOrCreatePart("tail");

            boolean HD = ConfigDBCClient.EnableHDTextures;
            //////////////////////////////////////////////////////
            //////////////////////////////////////////////////////
            //Forms
            Form form = display.getForm();
            if (form != null) {
                FormDisplay d = form.display;
                FormDisplay.BodyColor customClr = display.formColor;

                if (customClr.hasAnyColor(d, "eye"))
                    eyeColor = customClr.getProperColor(d, "eye");
                if (customClr.hasAnyColor(d, "hair"))
                    hairColor = customClr.getProperColor(d, "hair");
                if (customClr.hasAnyColor(d, "bodycm"))
                    bodyCM = customClr.getProperColor(d, "bodycm");
                if (customClr.hasAnyColor(d, "bodyc1"))
                    bodyC1 = customClr.getProperColor(d, "bodyc1");
                if (customClr.hasAnyColor(d, "bodyc2"))
                    bodyC2 = customClr.getProperColor(d, "bodyc2");
                if (customClr.hasAnyColor(d, "bodyc3"))
                    bodyC3 = customClr.getProperColor(d, "bodyc3");
                if (customClr.hasAnyColor(d, "fur"))
                    furColor = customClr.getProperColor(d, "fur");

                hasFur = d.hasBodyFur;
                furType = d.furType;
                if (d.hairType.equals("ssj4")) {
                    isSSJ4 = true;
                    if (customClr.getProperColor(d, "eye") == -1)
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
                ColorMode.applyModelColor(bodyCM, this.parent.alpha, isHurt);

                if (DBCRace.isSaiyan(race)) {
                    if (hasFur || isSSJ4 || isOozaru) {
                        ClientProxy.bindTexture(new ResourceLocation((HD ? HDDir + "base/" : "jinryuumodscore:cc/") + "hum.png"));
                        model.render(0.0625F); //important
                        if (isSSJ4) {
                            if (furColor == -1)
                                furColor = 0xDA152C;
                            if (HD && hasEyebrows && display.furType != 2)
                                renderSSJ4Face(eyeColor, furColor, hairColor, bodyCM, isBerserk, hasEyebrows, display.eyeType);
                        }

                        if (!isOozaru && furType == 2) {
                            renderSaviorFace(eyeColor);
                        }

                        if (isOozaru) {
                            if (furColor == -1)
                                furColor = 6498048;
                            ClientProxy.bindTexture(new ResourceLocation(HD ? HDDir + "oozaru/oozaru1.png" : "jinryuudragonbc:cc/oozaru1.png")); //oozaru hairless body
                            ColorMode.applyModelColor(bodyCM, this.parent.alpha, isHurt);
                            model.render(0.0625F);

                            ClientProxy.bindTexture(new ResourceLocation(HD ? HDDir + "oozaru/oozaru2.png" : "jinryuudragonbc:cc/oozaru2.png"));  //the fur
                            ColorMode.applyModelColor(furColor, this.parent.alpha, isHurt);
                        } else {
                            ClientProxy.bindTexture(new ResourceLocation(HD ? HDDir + "ssj4/ss4b" + furType + ".png" : "jinryuudragonbc:cc/ss4b"));
                        }
                        ColorMode.applyModelColor(furColor, this.parent.alpha, isHurt);
                    }
                }
            } else if (race == DBCRace.NAMEKIAN) {
                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/nam/0nam" + display.bodyType + ".png"));
                ColorMode.applyModelColor(bodyCM, this.parent.alpha, isHurt);
                model.render(0.0625F);

                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/nam/1nam" + display.bodyType + ".png"));
                ColorMode.applyModelColor(bodyC1, this.parent.alpha, isHurt);
                model.render(0.0625F);

                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/nam/2nam" + display.bodyType + ".png"));
                ColorMode.applyModelColor(bodyC2, this.parent.alpha, isHurt);
                model.render(0.0625F);

                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/nam/3nam" + display.bodyType + ".png"));
                GL11.glColor4f(1f, 1f, 1f, this.parent.alpha);
            } else if (race == DBCRace.ARCOSIAN) {
                int st = display.getCurrentArcoState();
                ColorMode.applyModelColor(bodyCM, this.parent.alpha, isHurt);
                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/0A" + JRMCoreH.TransFrSkn[st] + display.bodyType + ".png"));
                model.render(0.0625F);

                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/1A" + JRMCoreH.TransFrSkn[st] + display.bodyType + ".png"));
                ColorMode.applyModelColor(bodyC1, this.parent.alpha, isHurt);
                model.render(0.0625F);

                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/2A" + JRMCoreH.TransFrSkn[st] + display.bodyType + ".png"));
                ColorMode.applyModelColor(bodyC2, this.parent.alpha, isHurt);
                model.render(0.0625F);

                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/3A" + JRMCoreH.TransFrSkn[st] + display.bodyType + ".png"));
                ColorMode.applyModelColor(bodyC3, this.parent.alpha, isHurt);
                model.render(0.0625F);

                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/4A" + JRMCoreH.TransFrSkn[st] + display.bodyType + ".png"));
                GL11.glColor4f(1f, 1f, 1f, this.parent.alpha);
            } else if (race == DBCRace.MAJIN) {
                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/majin/majin.png"));
                ColorMode.applyModelColor(bodyCM, this.parent.alpha, isHurt);
            }

            if (form != null && form.display.overlays.hasBodyOverlays) {
                renderBodyOverlays(form, display, model);
            }
        }
    }

    @Unique
    private void renderBodyOverlays(Form form, DBCDisplay display, ModelRenderer model) {
        FormOverlay overlayData = form.display.overlays;

        for (FormOverlay.Body bodyData : overlayData.getBodies().toArray(new FormOverlay.Body[0])) {
            if (bodyData.isEnabled()) {
                String bodyTexture = bodyData.getTexture();
                ImageData imageData = ClientCacheHandler.getImageData(bodyTexture);
                if (imageData == null || !imageData.imageLoaded())
                    continue;

                int color = getProperColor(form.display, display, bodyData.getColor(), bodyData.colorType);

                model.render(0.0625F); //render MAIN skin (by this point its texture is only bound, not rendered yet)
                bindImageDataTexture(imageData, color, bodyData.getAlpha());

                //   model.render(0.0625F); //Don't need to render this as the main method after the mixins renders the body anyway
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

    public void renderEnabledKiWeapons(float partialTicks) {
        EntityCustomNpc entity = (EntityCustomNpc) display.npc;
        ModelScalePart arms = entity.modelData.modelScale.arms;

        float x = (1.0F - entity.modelData.modelScale.body.scaleX) * 0.25F + (1.0F - arms.scaleX) * 0.075F;
        float y = entity.modelData.getBodyY() + (1.0F - arms.scaleY) * -0.1F - 0.025f;
        float z = 0.0F;

        GL11.glPushMatrix();

        if (entity.currentAnimation == EnumAnimation.DANCING) {
            float dancing = (float) entity.ticksExisted / 4.0F;
            GL11.glTranslatef((float) Math.sin((double) dancing) * 0.025F, (float) Math.abs(Math.cos((double) dancing)) * 0.125F - 0.02F, 0.0F);
        }

        //        ((ModelScaleRenderer)this.bipedLeftArm).setConfig(arms, -x, y, z);
        byte hideArms = entity.modelData.hideArms;

        KiWeaponData leftArm = display.kiWeaponLeft;
        boolean isLeftHidden = hideArms == 3 || hideArms == 1;
        if (!isLeftHidden && leftArm.isEnabled()) {
            GL11.glPushMatrix();
            GL11.glTranslatef(-x + (0.5f * 0.25f * (alexArms ? 0.75f : 1)), y, z);
            if (arms != null) {
                GL11.glTranslatef(0.0F, 0.0F, 0.0F);
            }

            boolean test = parent.bipedLeftArm.isHidden;
            parent.bipedLeftArm.isHidden = false;
            parent.bipedLeftArm.postRender(partialTicks);
            parent.bipedLeftArm.isHidden = test;
            if (arms != null) {
                GL11.glScalef(arms.scaleX, arms.scaleY, arms.scaleZ);
            }
            renderKiWeapon(entity, leftArm);
            GL11.glPopMatrix();
        }

        KiWeaponData rightArm = display.kiWeaponRight;
        boolean isRightHidden = hideArms == 2 || hideArms == 1;
        if (!isRightHidden && rightArm.isEnabled()) {
            GL11.glPushMatrix();
            GL11.glTranslatef(x, y, z);
            if (arms != null) {
                GL11.glTranslatef(0.0F, 0.0F, 0.0F);
            }

            boolean test = parent.bipedRightArm.isHidden;
            parent.bipedRightArm.isHidden = false;
            parent.bipedRightArm.postRender(partialTicks);
            parent.bipedRightArm.isHidden = test;
            if (arms != null) {
                GL11.glScalef(arms.scaleX, arms.scaleY, arms.scaleZ);
            }
            renderKiWeapon(entity, rightArm);
            GL11.glPopMatrix();
        }

        GL11.glPopMatrix();
    }

    private void renderKiWeapon(Entity entity, KiWeaponData weaponData) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
        GL11.glDepthMask(true);
        Minecraft.getMinecraft().entityRenderer.disableLightmap(0);

        Color col = weaponData.color.clone();
        if (col.color == -1) {
            col.color = getKiWeaponColor();
        }


        //        if (weaponData.syncAuraColor) {
        //            Aura aura = display.getToggledAura();
        //            if (aura != null)
        //                col.color = display.activeAuraColor;
        //        }


        GL11.glTranslatef(-0.06F, -0.05F, 0.0F);
        JRMCoreClient.mc.renderEngine.bindTexture(new ResourceLocation(JRMCoreH.tjjrmc + ":allw.png"));

        if (weaponData.weaponType == 1) {
            // float scl = (float)kiFistLevel * 0.02F + (float)kiInfuseLevel * 0.02F;
            float scl = weaponData.scaleY - 1;
            GL11.glTranslatef(weaponData.offsetX, -scl * 0.75f + weaponData.offsetY, weaponData.offsetZ);
            GL11.glScalef(weaponData.scaleX, 1.0f + scl, weaponData.scaleZ);
            float ex = (float) entity.ticksExisted;
            float r4 = (MathHelper.cos(ex / 2.0F) / 3.0F - 0.2F) / 8.0F;

            col.glColor();
            GL11.glTranslatef(0.0F, -r4, 0.0F);
            GL11.glRotatef(ex * 25.0F, 0.0F, 1.0F, 0.0F);
            JRMCoreHJBRA.model2.render(0.0625F, 0);

            col.multiply(0.8f).glColor();
            GL11.glTranslatef(0.0F, -0.12F, 0.0F);
            GL11.glScalef(1.3F, 1.18F, 1.3F);
            JRMCoreHJBRA.model2.render(0.0625F, 0);
        }

        if (weaponData.weaponType == 2) {
            GL11.glTranslatef(weaponData.offsetX, 0.6F + weaponData.offsetY, weaponData.offsetZ);
            col.glColor();
            GL11.glRotatef(-3.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(5.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(weaponData.scaleX, weaponData.scaleY, weaponData.scaleZ);
            JRMCoreHJBRA.model2.render(0.0625F, 1);
        }

        Minecraft.getMinecraft().entityRenderer.enableLightmap(0);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }

    private int getKiWeaponColor() {
        Form fakeForm = display.getForm();
        if (fakeForm != null) {
            if (fakeForm.display.auraColor > -1) {
                return fakeForm.display.auraColor;
            } else if (fakeForm.display.auraID != -1) {
                Aura aura = (Aura) AuraController.getInstance().get(fakeForm.display.auraID);
                AuraDisplay auraDisplay = (aura != null ? aura.display : null);
                return KiWeaponData.getColorByAuraType(auraDisplay);
            }
        }
        if (display.auraID != -1) {
            Aura aura = (Aura) AuraController.getInstance().get(display.auraID);
            AuraDisplay auraDisplay = (aura != null ? aura.display : null);
            return KiWeaponData.getColorByAuraType(auraDisplay);
        } else {
            return KiWeaponData.getColorByAuraTypeName("");
        }
    }
}
