package kamkeel.npcdbc.client.render;

import JinRyuu.DragonBC.common.DBCClient;
import JinRyuu.DragonBC.common.Npcs.RenderDBC;
import JinRyuu.JRMCore.client.config.jrmc.JGConfigClientSettings;
import kamkeel.npcdbc.client.model.ModelAura;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.entity.EntityAura;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Random;

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
            if (System.currentTimeMillis() - animationStartTime > 200 / 2 / pulseMax) {
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
        int age = Math.max(1, aura.ticksExisted % speed);
        float alphaConfig = (float) JGConfigClientSettings.CLIENT_DA21 / 10.0F;
        float alpha = aura.alpha;
        alpha = (isFirstPerson ? aura.isInner ? 0.0075f : 0.0125f : alpha) * alphaConfig;

        int state = aura.dbcData.State;
        int race = aura.dbcData.Race;

        pulseMax = 0;
        if (pulseMax > 0)
            animatePulsing();
        else
            pulseAnimation = 0;
        Random rand = new Random();



        ResourceLocation t1 = aura.tex1.length() > 3 ? new ResourceLocation(aura.tex1) : null;
        ResourceLocation t2 = aura.tex2.length() > 3 ? new ResourceLocation(aura.tex2) : null;
        ResourceLocation t3 = aura.tex3.length() > 3 ? new ResourceLocation(aura.tex3) : null;
        
        GL11.glPushMatrix();
        aura.size = 1f;
        GL11.glTranslated(posX, posY + aura.getYOffset(), posZ);

        GL11.glPushMatrix();
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glDisable(2896);
        GL11.glBlendFunc(770, 771);
        GL11.glAlphaFunc(516, 0.003921569F);

        float sizeStateReleaseFactor = getStateSizeFactor(state, race) + aura.dbcData.Release * 0.03f;
        float pulsingSize = pulseAnimation * 0.03f;
        GL11.glScalef(aura.size + 0.1F * sizeStateReleaseFactor + pulsingSize, aura.size + 0.07F * sizeStateReleaseFactor, aura.size + 0.1F * sizeStateReleaseFactor + pulsingSize);
      
        GL11.glTranslatef(0.0F, -0.3F - 0.07F * sizeStateReleaseFactor, 0.0F);
        GL11.glRotatef(age * 4, 0.0F, 1.0F, 0.0F);

        float modelRotX = 0.75f;
        int maxLayers = 5;

        for (float i = 1; i < maxLayers + 1; ++i) {
            float layerPercent = i / maxLayers;
            float layerTemp = layerPercent * 20f;
            
            for (float j = 1; j < 2; j += 0.05) {

                Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
                model.auraModel.offsetY = -(i / maxLayers) * 20 * 0.11f;
                model.auraModel.offsetZ = layerTemp < 7F ? 0.2F - 1 * 0.075F : 0.35F + (1 - 7.0F) * 0.055F;
                model.auraModel.rotateAngleX = (0.9926646F - layerTemp * 0.01F - 0 * modelRotX) * (1 - i / maxLayers) * (1 - ((float) Math.pow(i / maxLayers, 2)));
                if (layerPercent > 0.99) //makes aura close in at top
                    model.auraModel.rotateAngleX = 100;

                model.auraModel.rotationPointY = 55.0F + (i / maxLayers) * 20;
                float r = rand.nextInt(200);
                if (layerTemp > 3) //aura intensity
                    model.auraModel.offsetY += -r * 0.0015f * getStateIntensity(state, race);
                
                
                GL11.glPushMatrix();
                GL11.glRotatef(360 * j, 0.0F, 1.0F, 0.0F);
                if (layerPercent < 0.21) {
                    glColor4f(aura.color1, alpha);
                    this.renderManager.renderEngine.bindTexture(t1);
                    model.auraModel.render(0.0625f);

                    if (t2 != null) {
                        this.renderManager.renderEngine.bindTexture(t2);
                        glColor4f(aura.color2, alpha);
                        model.auraModel.render(0.0625f);
                    }
                }
                GL11.glPopMatrix();

                GL11.glPushMatrix();
                GL11.glRotatef(360 * j + 45, 0F, 1F, 0F);
                this.renderManager.renderEngine.bindTexture(t1);
                if (aura.color3 > -1 && i == 1)
                    cf(aura.color1, aura.color3, alpha);
                else
                    glColor4f(aura.color1, alpha);
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
        Minecraft.getMinecraft().entityRenderer.enableLightmap(0);

    }

    public static float getStateIntensity(int state, int race) {
        float intensityFactor = 0.75f; //the higher, the more intensely the aura moves in Y axis
        if (race == DBCRace.SAIYAN || race == DBCRace.HALFSAIYAN) {
            if (state > DBCForm.Base && state < DBCForm.SuperSaiyan2)
                intensityFactor = 0.4f;
            else if (state == DBCForm.SuperSaiyan2)
                intensityFactor = 0.325f;
            else if (state == DBCForm.SuperSaiyan3)
                intensityFactor = 0.35f;
            else if (state == DBCForm.SuperSaiyan4)
                intensityFactor = 0.1f;

            else if (state == DBCForm.SuperSaiyanGod)
                return intensityFactor * 0.5f;
            else if (state == DBCForm.SuperSaiyanBlue)
                intensityFactor = 0.1f;
            else if (state == DBCForm.BlueEvo)
                intensityFactor = 0.1f;
        }

        if (state < 1)
            state = 1;

        return state * intensityFactor;
    }

    public static float getStateSizeFactor(int state, int race) {
        int sizeFactor = state; //responsible for correctly scaling aura sizes
        if (race == DBCRace.SAIYAN || race == DBCRace.HALFSAIYAN) {
            if (state == DBCForm.Base)
                sizeFactor = 1;
            else if (state > DBCForm.Base && state < DBCForm.SuperSaiyan2)
                sizeFactor = 10;
            else if (state == DBCForm.SuperSaiyan2)
                sizeFactor = 15;
            else if (state == DBCForm.SuperSaiyan3)
                sizeFactor = 30;
            else if (state == DBCForm.SuperSaiyan4)
                sizeFactor = 35;

            else if (state == DBCForm.SuperSaiyanGod)
                sizeFactor = 8;
            else if (state == DBCForm.SuperSaiyanBlue)
                sizeFactor = 8;
            else if (state == DBCForm.BlueEvo)
                sizeFactor = 12;

        }
        return sizeFactor * 0.5f;

    }

    public void doRender(Entity aura, double posX, double posY, double posZ, float yaw, float partialTickTime) {
        this.renderAura((EntityAura) aura, posX, posY, posZ);
    }
}
