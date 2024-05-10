package kamkeel.npcdbc.client.model;

import JinRyuu.JBRA.RenderPlayerJBRA;
import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.client.model.part.*;
import kamkeel.npcdbc.client.model.part.hair.DBCHair;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormDisplay;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.model.ModelMPM;
import noppes.npcs.entity.EntityCustomNpc;
import org.lwjgl.opengl.GL11;

public class ModelDBC extends ModelBase {

    private final ModelMPM parent;
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
        this.DBCHair.setData(entity.modelData, entity);
        this.DBCHorns.setData(entity.modelData, entity);
        this.DBCEars.setData(entity.modelData, entity);
        this.DBCBody.setData(entity.modelData, entity);
        this.DBCRightArms.setData(entity.modelData, entity);
        this.DBCLeftArms.setData(entity.modelData, entity);
    }

    public void renderFace(DBCDisplay display) {
        if (display.useSkin) {
            int eyeColor = display.eyeColor;
            int eyeBrowColor = display.hairColor;
            boolean hasArcoMask = display.hasArcoMask;

            //////////////////////////////////////////////////////
            //////////////////////////////////////////////////////
            //Forms
            Form form = display.getCurrentForm();
            if (form != null) {
                FormDisplay d = form.display;

                if (d.hasColor("eye"))
                    eyeColor = d.eyeColor;
                if (d.hasColor("hair"))
                    eyeBrowColor = d.hairColor;

                if (d.hasArcoMask)
                    hasArcoMask = true;

            }
            //////////////////////////////////////////////////////
            //////////////////////////////////////////////////////
            RenderPlayerJBRA.glColor3f(display.bodyCM);
            ClientProxy.bindTexture(new ResourceLocation(getFaceTexture(display, "n" + display.noseType)));

            this.nose.rotateAngleY = parent.bipedHead.rotateAngleY;
            this.nose.rotateAngleX = parent.bipedHead.rotateAngleX;
            this.nose.rotationPointX = parent.bipedHead.rotationPointX;
            this.nose.rotationPointY = parent.bipedHead.rotationPointY;
            this.nose.render(0.0625F);

            String mouthDir = "";
            if (display.race == 4 && hasArcoMask)
                mouthDir = "jinryuudragonbc:cc/arc/m/0A" + JRMCoreH.TransFrSkn[display.arcoState] + display.bodyType + "a.png";
            else
                mouthDir = getFaceTexture(display, "m" + display.mouthType);
            ClientProxy.bindTexture(new ResourceLocation(mouthDir));
            this.mouth.rotateAngleY = parent.bipedHead.rotateAngleY;
            this.mouth.rotateAngleX = parent.bipedHead.rotateAngleX;
            this.mouth.rotationPointX = parent.bipedHead.rotationPointX;
            this.mouth.rotationPointY = parent.bipedHead.rotationPointY;
            this.mouth.render(0.0625F);

            GL11.glColor3f(1.0f, 1.0f, 1.0f);
            ClientProxy.bindTexture(new ResourceLocation(getFaceTexture(display, "b" + display.eyeType)));
            this.eyebase.rotateAngleY = parent.bipedHead.rotateAngleY;
            this.eyebase.rotateAngleX = parent.bipedHead.rotateAngleX;
            this.eyebase.rotationPointX = parent.bipedHead.rotationPointX;
            this.eyebase.rotationPointY = parent.bipedHead.rotationPointY;
            this.eyebase.render(0.0625F);

            if (display.race < 4) {
                RenderPlayerJBRA.glColor3f(eyeBrowColor);
                ClientProxy.bindTexture(new ResourceLocation(getFaceTexture(display, "w" + display.eyeType)));
                this.eyebrow.rotateAngleY = parent.bipedHead.rotateAngleY;
                this.eyebrow.rotateAngleX = parent.bipedHead.rotateAngleX;
                this.eyebrow.rotationPointX = parent.bipedHead.rotationPointX;
                this.eyebrow.rotationPointY = parent.bipedHead.rotationPointY;
                this.eyebrow.render(0.0625F);
            }


            RenderPlayerJBRA.glColor3f(eyeColor);
            ClientProxy.bindTexture(new ResourceLocation(getFaceTexture(display, "l" + display.eyeType)));
            this.eyeleft.rotateAngleY = parent.bipedHead.rotateAngleY;
            this.eyeleft.rotateAngleX = parent.bipedHead.rotateAngleX;
            this.eyeleft.rotationPointX = parent.bipedHead.rotationPointX;
            this.eyeleft.rotationPointY = parent.bipedHead.rotationPointY;
            this.eyeleft.render(0.0625F);

            ClientProxy.bindTexture(new ResourceLocation(getFaceTexture(display, "r" + display.eyeType)));
            this.eyeright.rotateAngleY = parent.bipedHead.rotateAngleY;
            this.eyeright.rotateAngleX = parent.bipedHead.rotateAngleX;
            this.eyeright.rotationPointX = parent.bipedHead.rotationPointX;
            this.eyeright.rotationPointY = parent.bipedHead.rotationPointY;
            this.eyeright.render(0.0625F);
        }
    }

    public void renderHead(DBCDisplay display) {
        GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
        // renderHairs(display);
        renderFace(display);
        GL11.glPopAttrib();
    }

    public void renderAllBody(DBCDisplay display, ModelRenderer model) {
        if (display.useSkin) {
            int race = display.race;
            if (race == DBCRace.HUMAN || race == DBCRace.SAIYAN || race == DBCRace.HALFSAIYAN) {
                ClientProxy.bindTexture(new ResourceLocation("jinryuumodscore:cc/hum.png"));
                RenderPlayerJBRA.glColor3f(display.bodyCM);
            } else if (race == DBCRace.NAMEKIAN) {
                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/nam/0nam" + display.bodyType + ".png"));
                RenderPlayerJBRA.glColor3f(display.bodyCM);
                model.render(0.0625F);

                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/nam/1nam" + display.bodyType + ".png"));
                RenderPlayerJBRA.glColor3f(display.bodyC1);
                model.render(0.0625F);

                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/nam/2nam" + display.bodyType + ".png"));
                RenderPlayerJBRA.glColor3f(display.bodyC2);
                model.render(0.0625F);

                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/nam/3nam" + display.bodyType + ".png"));
                GL11.glColor3f(1f, 1f, 1f);
            } else if (race == DBCRace.ARCOSIAN) {
                int st = display.getCurrentArcoState();
                RenderPlayerJBRA.glColor3f(display.bodyCM);
                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/0A" + JRMCoreH.TransFrSkn[st] + display.bodyType + ".png"));
                model.render(0.0625F);

                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/1A" + JRMCoreH.TransFrSkn[st] + display.bodyType + ".png"));
                RenderPlayerJBRA.glColor3f(display.bodyC1);
                model.render(0.0625F);

                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/2A" + JRMCoreH.TransFrSkn[st] + display.bodyType + ".png"));
                RenderPlayerJBRA.glColor3f(display.bodyC2);
                model.render(0.0625F);

                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/3A" + JRMCoreH.TransFrSkn[st] + display.bodyType + ".png"));
                RenderPlayerJBRA.glColor3f(display.bodyC3);
                model.render(0.0625F);

                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/4A" + JRMCoreH.TransFrSkn[st] + display.bodyType + ".png"));
                GL11.glColor3f(1f, 1f, 1f);
            } else if (race == DBCRace.MAJIN) {
                ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/majin/majin.png"));
                RenderPlayerJBRA.glColor3f(display.bodyCM);
            }
        }
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
