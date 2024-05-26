package kamkeel.npcdbc.client.render;

import JinRyuu.DragonBC.common.DBCClient;
import JinRyuu.DragonBC.common.Npcs.RenderDBC;
import JinRyuu.JRMCore.JRMCoreClient;
import JinRyuu.JRMCore.JRMCoreHDBC;
import JinRyuu.JRMCore.client.config.jrmc.JGConfigClientSettings;
import kamkeel.npcdbc.client.model.ModelAura;
import kamkeel.npcdbc.client.sound.Sound;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes3D;
import kamkeel.npcdbc.data.IAuraData;
import kamkeel.npcdbc.entity.EntityAura;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import noppes.npcs.util.ValueUtil;
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
    private float[][] lightVertRotation;
    private int lightVertN;
    public AuraRenderer() {
        super(new ModelAura(), 0.5F);
        model = (ModelAura) this.mainModel;
        this.shadowSize = 0.0F;
        this.lightVertRotation = new float[10][7];
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
        if (aura.aura.display.kettleModeType == 1 || aura.aura.display.type == EnumAuraTypes3D.None || !JGConfigClientSettings.CLIENT_DA14)
            return;

        byte race = aura.auraData.getRace();
        byte state = aura.auraData.getState();
        int speed = aura.speed;
        int age = Math.max(1, aura.ticksExisted % speed);
        float release = Math.max(5, aura.auraData.getRelease());


        float alpha = aura.alpha;
        float alphaConfig = (float) JGConfigClientSettings.CLIENT_DA21 / 10.0F;
        boolean isFirstPerson = DBCClient.mc.thePlayer == aura.entity && DBCClient.mc.gameSettings.thirdPersonView == 0;
        alpha = (isFirstPerson ? aura.isInner ? 0.0075f : 0.005f : alpha) * alphaConfig;

        pulseMax = 5;
        if (pulseMax > 0)
            animatePulsing();
        else
            pulseAnimation = 0;

        Random rand = new Random();
        GL11.glPushMatrix();

        float pulsingSize = pulseAnimation * 0.03f;
        float stateSizeFactor = getStateSizeFactor(aura.auraData);
        float sizeStateReleaseFactor = stateSizeFactor + (release / 100) * Math.max(stateSizeFactor * 0.75f, 2.5f); //aura gets 1.75x bigger at 100% release
        float size = aura.size + 0.1f * sizeStateReleaseFactor;
        aura.effectiveSize = size;

        double yOffset = aura.getYOffset(size);
        if (stateSizeFactor < 4)  //fixes bug in which offset is not correct if size is too small
            yOffset -= 0.4 - (sizeStateReleaseFactor / 5) * 0.4;
        GL11.glTranslated(posX, posY +yOffset, posZ);

        GL11.glPushMatrix();
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);


        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glDisable(2896);
        GL11.glBlendFunc(770, 771);
        GL11.glAlphaFunc(516, 0.003921569F);


        GL11.glScalef(size + pulsingSize, size, size + pulsingSize);
        GL11.glRotatef(aura.ticksExisted % 360 * speed, 0.0F, 1.0F, 0.0F);

        int maxLayers = 5;
        for (float i = 1; i < maxLayers + 1; ++i) {
            float layerPercent = i / maxLayers;
            float layerTemp = layerPercent * 20f;

            for (float j = 1; j < 2; j += 0.05) {

                Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
                model.auraModel.offsetY = -(i / maxLayers) * aura.height;
                model.auraModel.offsetZ = layerTemp < 7F ? 0.2F - 1 * 0.075F : 0.35F + (1 - 7.0F) * 0.055F;
                model.auraModel.rotateAngleX = (0.9926646F - layerTemp * 0.01F) * (1 - i / maxLayers) * (1 - ((float) Math.pow(i / maxLayers, 2)));
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
                    this.renderManager.renderEngine.bindTexture(aura.text1);
                    model.auraModel.render(0.0625f);

                    if (aura.text2 != null) {
                        this.renderManager.renderEngine.bindTexture(aura.text2);
                        glColor4f(aura.color2, alpha);
                        model.auraModel.render(0.0625f);
                    }
                }
                GL11.glPopMatrix();

                GL11.glPushMatrix();
                GL11.glRotatef(360 * j + 45, 0F, 1F, 0F);
                this.renderManager.renderEngine.bindTexture(aura.text1);
                if (aura.color3 > -1 && j < 1)
                    cf(aura.color1, aura.color3, alpha);
                else
                    glColor4f(aura.color1, alpha);
                model.auraModel.render(0.0625f);

                if (aura.text2 != null) {
                    GL11.glTranslatef(0.0F, 3F, 0.0F);
                    GL11.glPushMatrix();
                    GL11.glScalef(0.8F, 0.4F, 0.8F);

                    this.renderManager.renderEngine.bindTexture(aura.text2);
                    glColor4f(aura.color2, alpha);
                    model.auraModel.render(0.0625f);
                    GL11.glPopMatrix();
                }

                GL11.glPopMatrix();

                if (aura.color3 > -1 && aura.text3 != null) {
                    GL11.glPushMatrix();
                    GL11.glScalef(0.9F, 0.9F, 0.9F);
                    GL11.glTranslatef(0.0F, 0.5F, 0.0F);
                    GL11.glRotatef(360 * j + 45, 0.0F, 1.0F, 0.0F);

                    this.renderManager.renderEngine.bindTexture(aura.text3);
                    glColor4f(aura.color3, alpha);
                    model.auraModel.render(0.0625f);

                    GL11.glPopMatrix();
                }
            }

        }
        float r = rand.nextInt(50);
        if (aura.hasLightning && r < 10 && age < 10)
            lightning(aura, posX, posY + aura.getYOffset(), posZ);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(1);
        tessellator.setColorRGBA_I(0xffffff, 255);
        tessellator.addVertex(0, 0, 0);
        tessellator.addVertex(0, 0, 1);
        //  GL11.glScalef(3, 3, 3);
        tessellator.draw();

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

    private void lightning(EntityAura aura, double par2, double par4, double par6) {
        Random rand = new Random();

        if (aura.ticksExisted % 100 > 0 && rand.nextLong() < 1)
            return;

        this.lightVertRotation = new float[10][7];
        GL11.glPushMatrix();
        Tessellator tessellator = Tessellator.instance;
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        GL11.glScalef(0.5f, 1f, 0.5f);

        double[] adouble = new double[8];
        double[] adouble1 = new double[8];
        double d3 = 0.0;
        double d4 = 0.0;
        GL11.glTranslated(par2, par4 +2.3, par6);
        int k1 = 0;
        int nu = (int) (Math.random() * 10.0) + 1;
        int nu2 = 3;
        if (!JRMCoreClient.mc.isGamePaused()) {
            this.lightVertN = (int) (rand.nextFloat() * 7.0);
        }

        // lightVertN = 3;
        for (int i = 0; i < this.lightVertN - 1; ++i) {
            if (!JRMCoreClient.mc.isGamePaused()) {
                this.lightVertRotation[i][0] = (float) (Math.random() * 1.0);
                this.lightVertRotation[i][1] = (float) (Math.random() * 1.0);
                this.lightVertRotation[i][2] = (float) (Math.random() * 1.0);
                this.lightVertRotation[i][3] = (float) (Math.random() * 1.2000000476837158) - 0.6F;
                this.lightVertRotation[i][4] = (float) (Math.random() * (double) aura.entity.height) - aura.entity.height / 2.0F;
                this.lightVertRotation[i][5] = (float) (Math.random() * 1.2000000476837158) - 0.6F;
                this.lightVertRotation[i][6] = (float) (Math.random() * 0.20000000298023224);
            }

            float sc = (0.05F + this.lightVertRotation[i][6]) * 0.75f;
            GL11.glRotatef(360.0F * this.lightVertRotation[i][0], 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(360.0F * this.lightVertRotation[i][1], 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(360.0F * this.lightVertRotation[i][2], 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(this.lightVertRotation[i][3], this.lightVertRotation[i][4], this.lightVertRotation[i][5]);


            for (int j = 0; j < nu2; ++j) {
                int k = 7;
                int l = 0;
                if (j > 0) {
                    k = 7 - j;
                }

                if (j > 0) {
                    l = k - 2;
                }

                double d5 = adouble[k] - d3;
                double d6 = adouble1[k] - d4;

                for (int i1 = k; i1 >= l; --i1) {
                    double d7 = d5;
                    double d8 = d6;
                    d5 += (double) (rand.nextInt(31) - 15) * 0.07000000029802322;
                    d6 += (double) (rand.nextInt(31) - 15) * 0.07000000029802322;
                    tessellator.startDrawing(5);
                    float f2 = 0.5F;
                    tessellator.setColorRGBA_I(aura.lightningColor, aura.lightningAlpha);


                    double d9 = 0.1 + (double) k1 * 0.2;
                    double d10 = 0.1 + (double) k1 * 0.2;

                    for (int j1 = 0; j1 < 5; ++j1) {
                        double d11 = 0.0 - d9;
                        double d12 = 0.0 - d9;
                        if (j1 == 1 || j1 == 2) {
                            d11 += d9 * 2.0 * (double) sc;
                        }

                        if (j1 == 2 || j1 == 3) {
                            d12 += d9 * 2.0 * (double) sc;
                        }

                        double d13 = 0.0 - d10;
                        double d14 = 0.0 - d10;
                        if (j1 == 1 || j1 == 2) {
                            d13 += d10 * 2.0 * (double) sc;
                        }

                        if (j1 == 2 || j1 == 3) {
                            d14 += d10 * 2.0 * (double) sc;
                        }

                        if (i1 < 8) {
                            tessellator.addVertex(d13 + d5 * (double) sc, -((double) (i1 * 1 - 7)) * (double) sc, d14 + d6 * (double) sc);
                            tessellator.addVertex(d11 + d7 * (double) sc, -((double) ((i1 + 1) * 1 - 7)) * (double) sc, d12 + d8 * (double) sc);
                        }
                    }

                    tessellator.draw();
                }
            }
        }
        if (rand.nextInt(15) < 2 && aura.ticksExisted % 5 == 0)
            new Sound("jinryuudragonbc:1610.spark", aura.entity).setVolume(0.1f).setPitch(0.90f + rand.nextInt(3) * 0.05f).play(false);
        GL11.glDisable(3042);
        GL11.glEnable(2896);
        GL11.glEnable(3553);
        GL11.glPopMatrix();

    }
    public static float getStateIntensity(int state, int race) {
        float intensityFactor = 150f; //the higher, the more intensely the aura moves in Y axis
        if (race == DBCRace.SAIYAN || race == DBCRace.HALFSAIYAN) {
            if (state > DBCForm.Base && state < DBCForm.SuperSaiyan2)
                intensityFactor = 40f;
            else if (state == DBCForm.SuperSaiyan2)
                intensityFactor = 32.5f;
            else if (state == DBCForm.SuperSaiyan3)
                intensityFactor = 35f;
            else if (state == DBCForm.SuperSaiyan4)
                intensityFactor = 10f;

            else if (state == DBCForm.SuperSaiyanGod)
                intensityFactor = 10f;
            else if (state == DBCForm.SuperSaiyanBlue)
                intensityFactor = 15f;
            else if (state == DBCForm.BlueEvo)
                intensityFactor = 15f;
        }

        if (state < 1)
            state = 1;

        return state * intensityFactor / 100;
    }

    public static float getStateSizeFactor(IAuraData data) {
        int state = data.getState();
        int race = data.getRace();


        float sizeFactor = state; //responsible for correctly scaling aura sizes
        if (race == DBCRace.SAIYAN || race == DBCRace.HALFSAIYAN) {
            if (state == DBCForm.Base)
                sizeFactor = 0.5f;
            else if (state > DBCForm.Base && state < DBCForm.SuperSaiyan2)
                sizeFactor = 4;
            else if (state == DBCForm.SuperSaiyan2)
                sizeFactor = 6f;
            else if (state == DBCForm.SuperSaiyan3)
                sizeFactor = 10;
            else if (state == DBCForm.SuperSaiyan4)
                sizeFactor = 12.5f;

            else if (state == DBCForm.SuperSaiyanGod)
                sizeFactor = 1;
            else if (state == DBCForm.SuperSaiyanBlue)
                sizeFactor = 4;
            else if (state == DBCForm.BlueEvo)
                sizeFactor = 6;

        }

        if (data.getFormID() > -1) {
            int release = data.getRelease();
            float size = JRMCoreHDBC.DBCsizeBasedOnRace2(race, state);
            float effectiveSize = size * ValueUtil.clamp(release, 15, 25) * 0.015f;
            float factor = effectiveSize / size * 10;
            return size * factor;
        }

        return sizeFactor;

    }

    public void doRender(Entity aura, double posX, double posY, double posZ, float yaw, float partialTickTime) {
        this.renderAura((EntityAura) aura, posX, posY, posZ);
    }
}
