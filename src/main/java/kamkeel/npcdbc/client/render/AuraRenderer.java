package kamkeel.npcdbc.client.render;

import JinRyuu.DragonBC.common.DBCClient;
import JinRyuu.DragonBC.common.Npcs.RenderDBC;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.client.config.jrmc.JGConfigClientSettings;
import kamkeel.npcdbc.client.model.ModelAura;
import kamkeel.npcdbc.constants.enums.EnumPlayerAuraTypes;
import kamkeel.npcdbc.data.aura.AuraDisplay;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.entity.EntityAura;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static JinRyuu.DragonBC.common.Npcs.RenderAura2.cf;
import static JinRyuu.DragonBC.common.Npcs.RenderAura2.glColor4f;

public class AuraRenderer extends RenderDBC {
    private ModelAura model;
    String auraDir = "jinryuudragonbc:";
    int pulseAnimation;
    int pulseMax = 8;
    long animationStartTime;
    boolean throbOut;

    public AuraRenderer() {
        super(new ModelAura(), 0.5F);
        model = (ModelAura) this.mainModel;
        this.shadowSize = 0.0F;

    }

    public void animatePulsing() {
        if (!DBCClient.mc.isGamePaused()) {
            if (System.currentTimeMillis() - animationStartTime > 300 / 2 / pulseMax) {
                if (this.throbOut) {
                    if (pulseAnimation >= pulseMax)
                        this.throbOut = false;
                    else
                        ++pulseAnimation;

                } else if (pulseAnimation <= 0)
                    this.throbOut = true;
                else
                    --pulseAnimation;


                animationStartTime = System.currentTimeMillis();
            }
        }

    }

    public void renderAura(EntityAura aura, double posX, double posY, double posZ) {
        AuraDisplay display = aura.aura.display;

        if (display.kettleModeType == 1)
            return;


        int color1 = -1, color2 = -1, color3 = -1, speed = 20;
        String tex1 = auraDir + "aura.png", tex2 = "", tex3 = "";
        float alpha = 0.2f, size = 1f;

        boolean isInner = aura.isKaioken, isPlayer = aura.isPlayer;
        boolean isFirstPerson = DBCClient.mc.thePlayer == aura.entity && DBCClient.mc.gameSettings.thirdPersonView == 0;

        color1 = isPlayer ? aura.dbcData.AuraColor > 0 ? aura.dbcData.AuraColor : JRMCoreH.Algnmnt_rc(aura.dbcData.Alignment) : 11075583; //alignment color

        int mimicColor = EnumPlayerAuraTypes.getManualAuraColor(display.type);
        if (mimicColor != -1)
            color1 = mimicColor;

        if (display.type == EnumPlayerAuraTypes.SaiyanGod) {
            alpha = 0.2f;
            tex1 = auraDir + "aurai.png";
            tex2 = auraDir + "auraj.png";
            color2 = 16747301;
        } else if (display.type == EnumPlayerAuraTypes.SaiyanBlue) {
            speed = 40;
            alpha = 0.5F;
            tex1 = auraDir + "aurag.png";
            tex3 = auraDir + "auragb.png";
            color3 = 15727354;
        } else if (display.type == EnumPlayerAuraTypes.SaiyanBlueEvo) {
            speed = 40;
            alpha = 0.5F;
            tex1 = auraDir + "aurag.png";
            tex3 = auraDir + "auragb.png";
            color3 = 12310271;
        } else if (display.type == EnumPlayerAuraTypes.SaiyanRose) {
            speed = 30;
            alpha = 0.2F;
            tex1 = auraDir + "aurai.png";
            tex2 = auraDir + "auraj.png";
            color2 = 7872713;
        } else if (display.type == EnumPlayerAuraTypes.SaiyanRoseEvo) {
            speed = 30;
            alpha = 0.2F;
            tex1 = auraDir + "aurai.png";
            tex2 = auraDir + "auraj.png";
            color2 = 8592109;
        } else if (display.type == EnumPlayerAuraTypes.UI) {
            speed = 100;
            alpha = 0.15F;
            color1 = 15790320;
            tex1 = auraDir + "auras.png";
            color3 = 4746495;
            tex3 = auraDir + "auragb.png";
        } else if (display.type == EnumPlayerAuraTypes.GoD) {
            speed = 100;
            alpha = 0.2F;
            tex1 = auraDir + "aurag.png";
            tex3 = auraDir + "auragb.png";
            color2 = 12464847;
        } else if (display.type == EnumPlayerAuraTypes.UltimateArco) {
            alpha = 0.5F;
            tex1 = auraDir + "aurau.png";
            tex2 = auraDir + "aurau2.png";
            color2 = 16776724;
        }

        if (isPlayer && aura.dbcData.State > 0)//vanilla DBC form colors
            color1 = aura.dbcData.getDBCColor();

        if (display.hasColor("color1")) //IAura color
            color1 = display.color1;

        Form form = PlayerDataUtil.getForm(aura.entity);
        if (form != null && form.display.hasColor("aura")) //IForm color
            color1 = form.display.auraColor;


        if (display.hasColor("color2"))
            color2 = display.color2;
        if (display.hasColor("color3"))
            color3 = display.color3;


        if (display.hasAlpha("aura"))
            alpha = (float) display.alpha / 255;

        if (display.hasSpeed())
            speed = (int) display.speed;


        aura.speed = speed = 100;
        int age = aura.ticksExisted % speed;
        float alphaConfig = (float) JGConfigClientSettings.CLIENT_DA21 / 10.0F;
        alpha = 0.1f;
        alpha = (isFirstPerson ? isInner ? 0.025f : 0.05f : alpha) * alphaConfig;

        float spd2 = 18.0F / (speed * 0.05F);
        float spin = age * spd2;

        float sizeStateReleaseFactor = 1 + aura.dbcData.Release * 0.03f;
        float pulsingSize = pulseAnimation * 0.03f;

        pulseMax = 0;
        if (pulseMax > 0)
            animatePulsing();
        else
            pulseAnimation = 0;


        ResourceLocation t1 = tex1.length() > 3 ? new ResourceLocation(tex1) : null;
        ResourceLocation t2 = tex2.length() > 3 ? new ResourceLocation(tex2) : null;
        ResourceLocation t3 = tex3.length() > 3 ? new ResourceLocation(tex3) : null;

        GL11.glPushMatrix();
        GL11.glTranslated(posX, posY + 2.25f * size, posZ);

        GL11.glPushMatrix();
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glDisable(2896);
        GL11.glBlendFunc(770, 771);
        GL11.glAlphaFunc(516, 0.003921569F);


        GL11.glScalef(size + 0.1F * sizeStateReleaseFactor + pulsingSize, size + 0.07F * sizeStateReleaseFactor, size + 0.1F * sizeStateReleaseFactor + pulsingSize);
        GL11.glTranslatef(0.0F, -0.3F - 0.07F * sizeStateReleaseFactor, 0.0F);
        // GL11.glRotatef(spin, 0.0F, 1.0F, 0.0F);
        float modelRotX = 0.75f;

        int maxLayers = 10;
        for (float i = 0; i < maxLayers; ++i) {
            for (float j = 0; j < 1; j += 0.025) {
                GL11.glPushMatrix();
//                GL11.glRotatef(360 * j, 0.0F, 1.0F, 0.0F);
//                if (age < 15.0F) {
//                    this.renderManager.renderEngine.bindTexture(t1);
//                    glColor4f(color1, alpha);
//                    this.model.renderModel(aura, age, (float) i * modelRotX, speed);
//
//
//                    if (tex2.length() > 2) {
//                        this.renderManager.renderEngine.bindTexture(t2);
//                        glColor4f(color2, alpha);
//                        model.auraModel.render(0.0625f);
//                    }
//                }
                GL11.glPopMatrix();

                GL11.glPushMatrix();
                GL11.glRotatef(360 * j + 45, 0F, 1F, 0F);

                this.renderManager.renderEngine.bindTexture(color3 > -1 && i == 1 ? t3 : t1);
                if (color3 > -1 && i == 1)
                    cf(color1, color3, alpha);
                else
                    glColor4f(color1, alpha);


                float agePerc = (float) age / speed;
                float ageTemp = agePerc * 20f;
                float layerPerc =  i / maxLayers;

                model.auraModel.offsetY = -(i / maxLayers) * 20 * 0.15f; // from 0 to -1.64 to -4(max)
                //  model.auraModel.offsetZ = ageTemp < 8.0F ? 0.4F - ageTemp * 0.1F : -0.5F + (ageTemp - 7.0F) * 0.053F;   //from  -0.4  (widest)  to 0.4
                model.auraModel.offsetZ = ageTemp < 8.0F ? 0.4F - ageTemp * 0.1F : -0.3F + (ageTemp - 7.0F) * 0.035F;
                model.auraModel.rotateAngleX = (0.8726646F -  ageTemp * 0.01F - 0 * modelRotX) *(1 - i / maxLayers)* (1 -  ((float) Math.pow(i / maxLayers, 2)));
                model.auraModel.rotationPointY = 55.0F +  (i / maxLayers) *20;
                model.auraModel.render(0.0625f);


                if (tex2.length() > 2) {
                    this.renderManager.renderEngine.bindTexture(t2);
                    glColor4f(color2, alpha);
                    model.auraModel.render(0.0625f);
                }

                GL11.glPopMatrix();
                if (color3 > -1 && tex3.length() > 3) {
                    GL11.glPushMatrix();
                    GL11.glScalef(0.7F, 0.7F, 0.7F);
                    GL11.glTranslatef(0.0F, 1F, 0.0F);
                    GL11.glRotatef(360 * j + 45, 0.0F, 1.0F, 0.0F);

                    this.renderManager.renderEngine.bindTexture(t3);
                    glColor4f(color3, alpha);
                    model.auraModel.render(0.0625f);

                    GL11.glPopMatrix();
                }
            }

        }
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glAlphaFunc(516, 0.1F);
        GL11.glDisable(3042);
        GL11.glEnable(2896);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glPopMatrix();


        //Transparency stuff
        //  GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
        //  GL11.glDisable(GL11.GL_LIGHTING);
        //  Minecraft.getMinecraft().entityRenderer.disableLightmap((double) 0);
        //  GL11.glEnable(GL11.GL_BLEND);
        //  GL11.glDisable(GL11.GL_ALPHA_TEST);

        //Minecraft.getMinecraft().entityRenderer.enableLightmap((double) 0);

    }

    public void doRender(Entity aura, double posX, double posY, double posZ, float yaw, float partialTickTime) {
        this.renderAura((EntityAura) aura, posX, posY, posZ);
    }
}
