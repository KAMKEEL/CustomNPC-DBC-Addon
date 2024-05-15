package kamkeel.npcdbc.client.render;

import JinRyuu.DragonBC.common.DBCClient;
import JinRyuu.DragonBC.common.Npcs.RenderDBC;
import JinRyuu.JRMCore.client.config.jrmc.JGConfigClientSettings;
import kamkeel.npcdbc.client.model.ModelAura;
import kamkeel.npcdbc.entity.EntityAura;
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
        if (aura.aura.display.kettleModeType == 1)
            return;

        boolean isFirstPerson = DBCClient.mc.thePlayer == aura.entity && DBCClient.mc.gameSettings.thirdPersonView == 0;


        int speed = aura.speed = 100;
        int age = Math.max(3, aura.ticksExisted % speed);
        float alphaConfig = (float) JGConfigClientSettings.CLIENT_DA21 / 10.0F;
        float alpha = aura.alpha;
        alpha = (isFirstPerson ? aura.isInner ? 0.0075f : 0.0125f : alpha) * alphaConfig;


        pulseMax = 5;
        if (pulseMax > 0)
            animatePulsing();
        else
            pulseAnimation = 0;


        ResourceLocation t1 = aura.tex1.length() > 3 ? new ResourceLocation(aura.tex1) : null;
        ResourceLocation t2 = aura.tex2.length() > 3 ? new ResourceLocation(aura.tex2) : null;
        ResourceLocation t3 = aura.tex3.length() > 3 ? new ResourceLocation(aura.tex3) : null;
        
        GL11.glPushMatrix();
        GL11.glTranslated(posX, posY + 2f * aura.size, posZ);

        GL11.glPushMatrix();
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glDisable(2896);
        GL11.glBlendFunc(770, 771);
        GL11.glAlphaFunc(516, 0.003921569F);

        float sizeStateReleaseFactor = aura.dbcData.Release * 0.03f;
        float pulsingSize = pulseAnimation * 0.03f;
        GL11.glScalef(aura.size + 0.1F * sizeStateReleaseFactor + pulsingSize, aura.size + 0.07F * sizeStateReleaseFactor, aura.size + 0.1F * sizeStateReleaseFactor + pulsingSize);
      
        GL11.glTranslatef(0.0F, -0.3F - 0.07F * sizeStateReleaseFactor, 0.0F);

      //  float spd2 = 18.0F / (speed * 0.05F);
       // float spin = aura.ticksExisted * spd2;
        GL11.glRotatef(aura.ticksExisted % 360 * 3, 0.0F, 1.0F, 0.0F);
        float modelRotX = 0.75f;

        int maxLayers = 15;
        for (float i = 1; i < maxLayers + 1; ++i) {
            for (float j = 1; j < 2; j += 0.05) {
                GL11.glPushMatrix();
//                GL11.glRotatef(360 * j, 0.0F, 1.0F, 0.0F);
//                if (age < 15.0F) {
//                    this.renderManager.renderEngine.bindTexture(t1);
//                    glColor4f(aura.color1, alpha);
//                    this.model.renderModel(aura, age, (float) i * modelRotX, speed);
//
//
//                    if (t2 != null) {
//                        this.renderManager.renderEngine.bindTexture(t2);
//                        glColor4f(aura.color2, alpha);
//                        model.auraModel.render(0.0625f);
//                    }
//                }
                GL11.glPopMatrix();

                GL11.glPushMatrix();
                GL11.glRotatef(360 * j + 45, 0F, 1F, 0F);
                this.renderManager.renderEngine.bindTexture(aura.color3 > -1 && i == 1 ? t3 : t1);
                if (aura.color3 > -1 && i == 1)
                    cf(aura.color1, aura.color3, alpha);
                else
                    glColor4f(aura.color1, alpha);


                float agePerc = (float) age / speed;
                float ageTemp = agePerc * 20f;
                float layerPerc =  i / maxLayers;
                float layerTemp = layerPerc * 20f;

                //  model.auraModel.offsetY = -(i / maxLayers) * 20 * 0.15f; // from 0 to -1.64 to -4(max)
                model.auraModel.offsetY = -(i / maxLayers) * 20 * 0.2f;
                //  model.auraModel.offsetZ = ageTemp < 8.0F ? 0.4F - ageTemp * 0.1F : -0.5F + (ageTemp - 7.0F) * 0.053F;   //from  -0.4  (widest)  to 0.4
                model.auraModel.offsetZ = layerTemp < 5F ? 0.1F - layerTemp * 0.075F : -0.3F + (layerTemp - 7.0F) * 0.035F;
                model.auraModel.rotateAngleX = (0.9926646F - layerTemp * 0.01F - 0 * modelRotX) * (1 - i / maxLayers) * (1 - ((float) Math.pow(i / maxLayers, 2)));
                if (layerPerc > 0.99)
                    model.auraModel.rotateAngleX = 100;

                model.auraModel.rotationPointY = 55.0F +  (i / maxLayers) *20;
                model.auraModel.render(0.0625f);


                if (t2 != null) {
                    this.renderManager.renderEngine.bindTexture(t2);
                    glColor4f(aura.color2, alpha);
                    model.auraModel.render(0.0625f);
                }

                GL11.glPopMatrix();

                if (aura.color3 > -1 && t3 != null) {
                    GL11.glPushMatrix();
                    GL11.glScalef(0.9F, 0.9F, 0.9F);
                    GL11.glTranslatef(0.0F, 0.5F, 0.0F);
                    GL11.glRotatef(360 * j + 45, 0.0F, 1.0F, 0.0F);

                    this.renderManager.renderEngine.bindTexture(t3);
                    glColor4f(aura.color3, alpha);
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
