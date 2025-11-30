package kamkeel.npcdbc.client.model.part;

import kamkeel.npcdbc.client.ClientConstants;
import kamkeel.npcdbc.client.model.ModelDBCPartInterface;
import kamkeel.npcdbc.client.render.RenderEventHandler;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormDisplay;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import noppes.npcs.client.model.ModelMPM;
import noppes.npcs.entity.data.ModelData;
import noppes.npcs.entity.data.ModelPartData;
import org.lwjgl.opengl.GL11;

import static kamkeel.npcdbc.client.render.RenderEventHandler.disableStencilWriting;

public class DBCFemaleBody extends ModelDBCPartInterface {

    // Back Spikes
    public ModelRenderer BackSpikes;
    public ModelRenderer backSpike1;
    public ModelRenderer backSpike2;

    public ModelRenderer body;
    public ModelRenderer breast;
    public ModelRenderer breast2;
    public ModelRenderer Bbreast;
    public ModelRenderer hip;
    public ModelRenderer waist;
    public ModelRenderer bottom;
    public ModelRenderer Bbreast2;

    public float rot1;
    public float rot4;
    public float rot3;
    public float rot2;
    public float rot5;
    public float rot6;

    int breastSize = 1;

    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {

        this.rot1 = par1;
        this.rot2 = par2;
        this.rot3 = par3;
        this.rot4 = par4;
        this.rot5 = par5;
        this.rot6 = par6;
        if (base.onGround > -9990.0F) {
            float f = base.onGround;

            float bodyTwist = MathHelper.sin(MathHelper.sqrt_float(f) * 3.1415927F * 2.0F) * 0.2F;
            this.Bbreast.rotateAngleY = bodyTwist;
            this.body.rotateAngleY = bodyTwist;
            this.hip.rotateAngleY = bodyTwist;
            this.waist.rotateAngleY = bodyTwist;
            this.bottom.rotateAngleY = bodyTwist;
            this.Bbreast2.rotateAngleY = bodyTwist;

            float f2 = 1.0F - f;
            f2 *= f2 * f2;
            f2 = 1.0F - f2;

            // small forward lean
            this.Bbreast.rotateAngleX -= MathHelper.sin(f2 * 3.1415927F) * 0.1F;
            this.body.rotateAngleX = this.Bbreast.rotateAngleX;
            this.hip.rotateAngleX = this.Bbreast.rotateAngleX;
            this.waist.rotateAngleX = this.Bbreast.rotateAngleX;
            this.bottom.rotateAngleX = this.Bbreast.rotateAngleX;
            this.Bbreast2.rotateAngleX = this.Bbreast.rotateAngleX;
        }

        if (base.isSneak) {

            this.Bbreast.rotateAngleX = 0.5F;
            this.body.rotateAngleX = 0.5F;
            this.hip.rotateAngleX = 0.5F;
            this.waist.rotateAngleX = 0.5F;
            this.bottom.rotateAngleX = 0.5F;
            this.Bbreast2.rotateAngleX = 0.5F;

        } else {

            this.Bbreast.rotateAngleX = 0.0F;
            this.body.rotateAngleX = 0.0F;
            this.hip.rotateAngleX = 0.0F;
            this.waist.rotateAngleX = 0.0F;
            this.bottom.rotateAngleX = 0.0F;
            this.Bbreast2.rotateAngleX = 0.0F;
        }
    }

    public DBCFemaleBody(ModelMPM base) {
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


        this.body = new ModelRenderer(base, 16, 16);
        this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 4, 4, 0);
        this.body.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.body, 0.0F, 0.0F, 0.0F);
        this.hip = new ModelRenderer(base, 16, 23);
        this.hip.addBox(-4.0F, 7.0F, -2.0F, 8, 2, 4, 0);
        this.hip.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.hip, 0.0F, 0.0F, 0.0F);
        this.waist = new ModelRenderer(base, 16, 20);
        this.waist.addBox(-4.0F, 4.0F, -2.0F, 8, 3, 4, 0);
        this.waist.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.waist, 0.0F, 0.0F, 0.0F);
        this.Bbreast = new ModelRenderer(base, 0, 0);
        this.Bbreast.addBox(-4.0F, 2.266667F, -1.0F, 0, 0, 0, 0);
        this.Bbreast.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.breast = new ModelRenderer(base, 17, 18);
        this.breast.addBox(-4.0F, 2.266667F, -1.0F, 8, 3, 3, 0);
        this.breast.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.breast, -0.5235988F, 0.0F, 0.0F);
        this.Bbreast2 = new ModelRenderer(base, 0, 0);
        this.Bbreast2.addBox(-4.0F, 2.266667F, -1.0F, 0, 0, 0, 0);
        this.Bbreast2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.breast2 = new ModelRenderer(base, 9, 23);
        this.breast2.mirror = true;
        this.breast2.addBox(-4.0F, 2.266667F, -2.0F, 8, 3, 3, 0);
        this.breast2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.breast2, 0.5235988F, 3.141593F, 0.0F);
        this.bottom = new ModelRenderer(base, 16, 25);
        this.bottom.addBox(-4.0F, 9.0F, -2.0F, 8, 3, 4, 0);
        this.bottom.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.setRotation(this.bottom, 0.0F, 0.0F, 0.0F);
        this.Bbreast.addChild(this.breast);
        this.Bbreast2.addChild(this.breast2);

    }

    @Override
    public void render(float par1) {
        DBCDisplay display = ((INPCDisplay) entity.display).getDBCDisplay();
        if (!display.enabled)
            return;



// -----------------------------
// Outline / stencil setup
// -----------------------------
        if (!ClientConstants.renderingOutline && display.outlineID != -1)
            RenderEventHandler.enableStencilWriting((entity.getEntityId() + RenderEventHandler.TAIL_STENCIL_ID) % 256);

        GL11.glPushMatrix();  // Pushing matrix to start transformations

        float spike1RotX = 0;
        if (ClientConstants.renderingOutline) {
            spike1RotX = backSpike1.rotationPointX;
            GL11.glTranslatef(0.015f, -0.02f, 0);
            GL11.glScaled(1.02, 1.02, 0.95);
            backSpike1.rotationPointX = -1.4f;
            disableStencilWriting((entity.getEntityId() + RenderEventHandler.TAIL_STENCIL_ID) % 256, false);
        }

// -----------------------------
// Skin / color / form color logic
// -----------------------------
        if (display.useSkin) {
            this.useColor = 0;
            bodyCM = display.bodyCM;

            // Forms + form display color overrides
            Form form = display.getForm();
            if (form != null) {
                FormDisplay d = form.display;
                FormDisplay.BodyColor customClr = display.formColor;

                if (customClr.hasAnyColor(d, "bodycm"))
                    bodyCM = customClr.getProperColor(d, "bodycm");
            }
        }

// ------------------------------------------------
// BEGIN — Your optimized body part rendering
// ------------------------------------------------

// BREAST LOGIC PRECALC
        int breastSize = display.breastSize;  // replace with your actual getter if needed

        float scale = breastSize * 0.03F;
        float br = 0.4235988F + scale;
        float bs = 0.8F + scale;
        float bsY = 0.85F + scale * 0.5F;
        float bt = 0.1F * scale;

        boolean bounce = base.onGround != 0;
        float bspeed = false ? 1.5F : (base.isSneak ? 0.5F : 1.0F);

        float bbY = bounce
            ? MathHelper.sin(this.rot1 * 0.6662F * bspeed * 1.5F + (float)Math.PI)
            * this.rot2 * 0.03F * breastSize * 0.1119F
            : 0.0F;

// ---------------------------
// Bbreast BLOCK
// ---------------------------
        GL11.glPushMatrix();  // Push matrix for the Bbreast block

        GL11.glScalef(0.675F, 1.0F, 0.8F);

        GL11.glTranslatef(0.0F, bbY, 0.015F + bt);
        GL11.glScalef(1.0F, bsY, bs);

        this.setRotation(this.breast, -br, 0.0F, 0.0F);
        this.setRotation(this.breast2,  br, (float)Math.PI, 0.0F);

        if (bounce) {
            float xBounce = this.rot2 * 0.05F * breastSize * 0.1119F;
            float yBounce = this.rot2 * 0.02F * breastSize * 0.1119F;
            float c = MathHelper.cos(this.rot1 * 0.6662F * bspeed + (float)Math.PI);

            this.breast.rotateAngleX  += -c * xBounce;
            this.breast.rotateAngleY  +=  c * yBounce;
            this.breast2.rotateAngleX +=  c * xBounce;
            this.breast2.rotateAngleY +=  c * yBounce;
        }

        this.Bbreast.render(par1);

        GL11.glPopMatrix();  // Pop matrix after Bbreast block

// ---------------------------
// Bbreast2 BLOCK
// ---------------------------
        GL11.glPushMatrix();  // Push matrix for the Bbreast2 block

        GL11.glScalef(0.674F, 1.0F, 0.799F);

        GL11.glTranslatef(0.0F, 0.001F + bbY, 0.015F + bt);
        GL11.glScalef(1.0F, bsY, bs);

        this.Bbreast2.render(par1);

        GL11.glPopMatrix();  // Pop matrix after Bbreast2 block


// ---------------------------
// BODY BLOCK
// ---------------------------
        GL11.glPushMatrix();  // Push matrix for the body block

        GL11.glScalef(0.7F, 1.0F, 0.7F);
        this.body.render(par1);

        GL11.glPopMatrix();  // Pop matrix after body block


// ---------------------------
// HIP BLOCK
// ---------------------------
        GL11.glPushMatrix();  // Push matrix for the hip block

        GL11.glScalef(0.75F, 1.0F, 0.75F);

        float hipZ = base.isSneak ? 0.0F : (-0.02F);
        GL11.glTranslatef(0.0F, 0.0F, hipZ);

        this.hip.render(par1);

        GL11.glPopMatrix();  // Pop matrix after hip block


// ---------------------------
// WAIST BLOCK
// ---------------------------
        GL11.glPushMatrix();  // Push matrix for the waist block

        GL11.glScalef(0.65F, 1.0F, 0.65F);

        float waistZ = base.isSneak ? 0.0F : (-0.04F);
        GL11.glTranslatef(0.0F, 0.0F, waistZ);

        this.waist.render(par1);

        GL11.glPopMatrix();  // Pop matrix after waist block


// ---------------------------
// BOTTOM BLOCK
// ---------------------------
        GL11.glPushMatrix();  // Push matrix for the bottom block

        GL11.glScalef(0.85F, 1.0F, 0.85F);

        GL11.glTranslatef(0.0F, 0.0F, 0);
        this.bottom.render(par1);

        GL11.glPopMatrix();  // Pop matrix after bottom block

// ------------------------------------------------
// END — Your part rendering
// ------------------------------------------------

        GL11.glPopMatrix();  // Final Pop matrix (end of transformations)

// ------------------------------------------------
// Outline cleanup
// ------------------------------------------------
        if (!ClientConstants.renderingOutline && display.outlineID != -1)
            RenderEventHandler.enableStencilWriting(entity.getEntityId() % 256);

        if (ClientConstants.renderingOutline) {
            disableStencilWriting((entity.getEntityId()) % 256, false);
            backSpike1.rotationPointX = spike1RotX;
        }

    }


    @Override
    public void initData(ModelData modelData, DBCDisplay display) {
        ModelPartData config = data.getPartData("dbcBody");
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
                if (form.display.bodyType.equals("firstform")) {
                    config.type = 0;
                } else if (form.display.bodyType.equals("secondform")) {
                    config.type = 0;
                } else if (form.display.bodyType.equals("thirdform")) {
                    config.type = 0;
                } else if (form.display.bodyType.equals("finalform") || form.display.bodyType.equals("golden")) {
                    config.type = 0;
                } else if (form.display.bodyType.equals("ultimatecooler")) {
                    config.type = 1;
                }
            }
        }
        //////////////////////////////////////////////////////
        //////////////////////////////////////////////////////

        BackSpikes.isHidden = config.type != 1;

        if (!config.playerTexture) {
            location = config.getResource();
        } else
            location = null;
    }
}
