package kamkeel.npcdbc.client.model;

import JinRyuu.JRMCore.JRMCoreClient;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.JRMCoreHJBRA;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.client.ColorMode;
import kamkeel.npcdbc.client.model.part.*;
import kamkeel.npcdbc.client.model.part.hair.DBCHair;
import kamkeel.npcdbc.client.render.OverlayModelRenderer;
import kamkeel.npcdbc.client.utils.Color;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.aura.AuraDisplay;
import kamkeel.npcdbc.data.form.FacePartData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormDisplay;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.data.npc.KiWeaponData;
import kamkeel.npcdbc.data.overlay.Overlay;
import kamkeel.npcdbc.data.overlay.OverlayChain;
import kamkeel.npcdbc.data.overlay.OverlayContext;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.model.ModelMPM;
import noppes.npcs.constants.EnumAnimation;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.data.ModelScalePart;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static kamkeel.npcdbc.client.render.DBCOverlays.*;
import static kamkeel.npcdbc.data.form.FacePartData.Part;
import static kamkeel.npcdbc.data.overlay.Overlay.ColorType.*;
import static kamkeel.npcdbc.data.overlay.Overlay.Type;
import static kamkeel.npcdbc.data.overlay.Overlay.Type.*;

public class ModelDBC extends ModelBase {

    public final ModelMPM parent;
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

    public static final String SDDir = CustomNpcPlusDBC.ID + ":textures/sd/";
    public static final String HDDir = CustomNpcPlusDBC.ID + ":textures/hd/";
    public static final String OVERLAY_DIR = CustomNpcPlusDBC.ID + ":textures/overlays/";

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

            boolean isSaiyan = DBCRace.isSaiyan(display.race);
            boolean hasArcoMask = display.hasArcoMask, isBerserk = false, hasEyebrows = display.hasEyebrows, hasPupils = display.hasPupils;
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
                hasPupils = d.hasPupils;
            }
            //////////////////////////////////////////////////////
            //////////////////////////////////////////////////////
            boolean renderSSJ4Face = isSSJ4 && HD && hasEyebrows && isSaiyan;
            if (isOozaru) {
                ClientProxy.bindTexture(new ResourceLocation((HD ? HDDir : SDDir) + "oozaru/oozarueyes.png")); //eyes

                ColorMode.applyModelColor(eyeColor, this.parent.alpha, isHurt);
                applyHeadRotations(eyebase);
                renderOnHead(eyebase, y);

                ColorMode.applyModelColor(bodyCM, this.parent.alpha, isHurt);
                applyHeadRotations(DBCBody.Oozaru);
                renderOnHead(DBCBody.Oozaru, y);
                return;
            }
            ColorMode.applyModelColor(bodyCM, this.parent.alpha, isHurt);

            Set<FacePartData.Part> disabledParts = display.getDisabledFaceParts();

            if (isBerserk || hasPupils)
                disabledParts.addAll(EnumSet.of(Part.LeftEye, Part.RightEye));

            if (display.race >= DBCRace.ARCOSIAN) //Arcosians and majins have no brows
                disabledParts.add(Part.Eyebrows);

            //  if (!faceData.disabled(Part.Nose, display.eyeType)) {
            if (!disabledParts.contains(Part.Nose)) {
                ClientProxy.bindTexture(new ResourceLocation(getFaceTexture(display, "n" + display.noseType)));

                renderOnHead(nose, y);
            }

            if (!disabledParts.contains(Part.Mouth)) {
                String mouthDir = "";
                if (display.race == 4 && hasArcoMask)
                    mouthDir = "jinryuudragonbc:cc/arc/m/0A" + 2 + display.bodyType + "a.png";
                else
                    mouthDir = getFaceTexture(display, "m" + display.mouthType);
                ClientProxy.bindTexture(new ResourceLocation(mouthDir));

                renderOnHead(mouth, y);
            }

            if (!disabledParts.contains(Part.EyeWhite)) {
                ClientProxy.bindTexture(new ResourceLocation(getFaceTexture(display, "b" + display.eyeType)));

                GL11.glColor4f(1.0f, 1.0f, 1.0f, this.parent.alpha);
                renderOnHead(eyebase, y);
            }

            if (!disabledParts.contains(Part.Eyebrows)) {
                ClientProxy.bindTexture(new ResourceLocation(getFaceTexture(display, "w" + display.eyeType)));

                ColorMode.applyModelColor(display.race == DBCRace.NAMEKIAN ? bodyCM : eyeBrowColor, this.parent.alpha, isHurt);
                renderOnHead(eyebrow, y);
            }

            //TODO FOR GOATEE: DBC EYE LEFT IS RIGHT AND RIGHT IS LEFT, GOTTA SWITCH THEM FOR OVERLAYS TOO
            if (!disabledParts.contains(Part.LeftEye)) {
                String texture = getFaceTexture(display, "l" + display.eyeType);
                ClientProxy.bindTexture(new ResourceLocation(texture));

                ColorMode.applyModelColor(eyeColor, this.parent.alpha, isHurt);
                renderOnHead(eyeleft, y);
            }

            if (!disabledParts.contains(Part.RightEye)) {
                String texture = getFaceTexture(display, "r" + display.eyeType);
                ClientProxy.bindTexture(new ResourceLocation(texture));

                ColorMode.applyModelColor(eyeColor, this.parent.alpha, isHurt);
                renderOnHead(eyeright, y);
            }
        }
    }

    public void renderOnHead(ModelRenderer model) {
        renderOnHead(model, parent.npc.modelData.getBodyY());
    }

    public void renderOnHead(ModelRenderer model, float bodyY) {
        ModelScalePart head = parent.npc.modelData.modelScale.head;
        applyHeadRotations(model);

        GL11.glPushMatrix();
        GL11.glTranslatef(0, bodyY, 0);
        GL11.glScalef(head.scaleX, head.scaleY, head.scaleZ);
        model.render(0.0625F);
        GL11.glPopMatrix();
    }

    public void applyHeadRotations(ModelRenderer model) {
        model.rotateAngleY = parent.bipedHead.rotateAngleY;
        model.rotateAngleX = parent.bipedHead.rotateAngleX;
        model.rotateAngleZ = parent.bipedHead.rotateAngleZ;
        model.rotationPointX = parent.bipedHead.rotationPointX;
        model.rotationPointY = parent.bipedHead.rotationPointY;
        model.rotationPointZ = parent.bipedHead.rotationPointZ;
    }

    public static boolean bindTexture(String texture) {
        if (texture == null || texture.isEmpty())
            return false;

        try {
            Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(texture));
            return true;
        } catch (Exception exception) {
            return false;
        }
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
            boolean isSSJ4 = display.hairType.equals("ssj4"), isOozaru = display.hairType.equals("oozaru"), isSSJ3 = display.hairType.equals("ssj3"), hasEyebrows = display.hasEyebrows, isBerserk = false, hasPupils = false;
            FacePartData faceData = display.faceData;
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
                hasPupils = d.hasPupils;
                faceData = d.faceData;
            }
            //////////////////////////////////////////////////////
            //////////////////////////////////////////////////////

            int race = display.race;
            if (race == DBCRace.HUMAN || DBCRace.isSaiyan(race)) {
                ClientProxy.bindTexture(new ResourceLocation("jinryuumodscore:cc/hum.png"));
                ColorMode.applyModelColor(bodyCM, this.parent.alpha, isHurt);
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


        }
    }

    /*
         Applies for both players and NPCs!

         This should ONLY be called once per entity's render cycle.
         To retrieve the fully calculated list below, refer to
         player/NPC's cachedOverlays
     */
    public static List<OverlayChain> applyOverlayChains(List<OverlayChain> uniqueChains, OverlayContext ctx) {
        ArrayList<OverlayChain> chains = new ArrayList<>();

        /*
            uniqueChains contains all entity-unique overlays.
            Whatever you add before the below addAll gets rendered below them all.

            Usually scars or whatever player unique customizations,
            so a lot of the built-in chains in DBCOverlays will
            probably be applied before.

            i.e SSJ4_Fur goes here, as you usually want it below most other overlays, as it's basically the skin

         */


         /*
           Create the overlays here to test them (easy to hotswap here), then when finished drop them in DBCOverlays

        i.e.:
        OverlayChain SSJ4_FUR = OverlayChain.create("SSJ4_Fur");
        SSJ4_FUR.add(ALL, Fur, (ctx1) -> path("ssj4/ss4b" + ctx1.furType() + ".png", "jinryuudragonbc:cc/ss4b"));
        chains.add(SSJ4_FUR);
        */


        boolean ssj3 = ctx.hairType("ssj3");
        boolean ssj4 = ctx.hairType("ssj4");
        boolean savior = ctx.furSavior();
        boolean oozaru = ctx.hairType("oozaru");
        boolean pupils = ctx.pupils();
        boolean eyebrows = ctx.eyebrows();

        /* ───────── Fur / Body Overlays ───────── */
        if (oozaru)
            chains.add(OOZARU_FUR);

        if (ssj4 || ctx.hasFur()) {
            chains.add(SSJ4_FUR);

            if (savior) {
                chains.add(SAVIOR);
            }
        }

        /* ───────── Face Overlays ───────── */
        if (HD() && !oozaru && pupils) {
            if (ssj3 || !eyebrows) //removed (!eyebrows && pupils) as old no eyebrows code no more
                chains.add(SSJ3_FACE);
            else if (ssj4 && !savior)
                chains.add(SSJ4_FACE);
            else  //SSJ3 and SSJ4 have their own pupils
                chains.add(PUPILS);
        }

        if (!eyebrows && !pupils)
            chains.add(NO_EYEBROWS);

        /* ───────── Main Entity Overlays ───────── */
        if (uniqueChains != null)
            chains.addAll(uniqueChains);

         /*
            Whatever you add after gets rendered on top of the above.
         */

        /**
         * You usually want form overlays on top of everything else.
         * They take precedence like form colors do.
         * So add them at the very end, unless something else goes on top.
         */
        if (ctx.form() != null && ctx.form.display.overlays.enabled) {
            Set<Type> disabledTypes = ctx.form.display.disabledOverlayTypes;
            if (!disabledTypes.isEmpty())
                ctx.disabledTypes = disabledTypes;

            chains.add(ctx.exceptFor = ctx.form.display.overlays);
        }

        return chains;
    }

    public static void renderOverlays(OverlayContext ctx) {
        List<OverlayChain> chains = applyOverlayChains(ctx.getOverlayChains(), ctx);
        ctx.cacheOverlays(chains);

        for (OverlayChain chain : chains) {
            ctx.chain = chain;
            if (!chain.isEnabled() || chain.condition != null && !chain.checkCondition(ctx))
                continue;


            for (Overlay overlay : chain.overlays) {
                ctx.overlay = overlay;
                overlay.chain = chain;

                if (!overlay.isEnabled() || overlay.condition != null && !overlay.checkCondition(ctx))
                    continue;

                Type type = overlay.getType();

                /* ───────── Texture ───────── */
                    ctx.texture = overlay.getTexture();

                String textureFunction = overlay.applyTexture(ctx);
                if (textureFunction != null)
                    ctx.texture = textureFunction;

                if (!bindTexture(ctx.texture))
                    continue;

                /* ───────── Colors ───────── */
                ctx.color = ctx.color(overlay.colorType);

                Color colorFunction = overlay.applyColor(ctx);
                if (colorFunction != null)
                    ctx.color = colorFunction;

                /* ───────── Glow ───────── */
                boolean glow = overlay.isGlow();
                if (glow) {
                    GL11.glDisable(GL11.GL_LIGHTING);
                    Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
                }

                /* ───────── Pre-Render ───────── */
                boolean oldArmor = false, oldHairHidden = false;
                if (ctx.isNPC) {
                      /*
                        NPCs bind their skin texture in the ModelBox.render,
                        like in model.bipedBody.render(), so that screws up
                        the overlay texture.

                        isArmor = false disables that binding.
                     */
                    oldArmor = ctx.mpm().isArmor;
                    ctx.mpm().isArmor = true;

                    //Hair renders by default with head, not needed here
                    if (type == Face) {
                        oldHairHidden = ctx.modelNpc.DBCHair.isHidden;
                        ctx.modelNpc.DBCHair.isHidden = true;
                    }
                }

                /* ───────── Rendering ───────── */
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.001f);

                ctx.glColor(ctx.color);
                OverlayModelRenderer.render(type, ctx);

                /* ───────── Post-Rendering ───────── */
                if (ctx.isNPC) {

                    //Set DBCHair.isHidden to original value
                    if (type == Face)
                        ctx.modelNpc.DBCHair.isHidden = oldHairHidden;

                    //Set isArmor to original value
                    ctx.mpm().isArmor = oldArmor;
                }

                /* ───────── Disable Glow ───────── */
                if (glow) {
                    GL11.glEnable(GL11.GL_LIGHTING);
                    Minecraft.getMinecraft().entityRenderer.enableLightmap(0);
                }
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

    /*
       apply HD/SD paths
     */
    public static String path(String tex) {
        return HD() ? HDDir + tex : SDDir + tex;
    }

    public static String path(String texHD, String texSD) {
        return HD() ? HDDir + texHD : texSD; //for SD textures outside of "textures/sd/"
    }

    public static String HD(String texHD) {
        return HDDir + texHD;
    }

    public static boolean HD() {
        return ConfigDBCClient.EnableHDTextures;
    }
}
