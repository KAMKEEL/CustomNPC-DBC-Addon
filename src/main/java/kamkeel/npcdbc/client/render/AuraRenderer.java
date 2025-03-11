package kamkeel.npcdbc.client.render;

import JinRyuu.DragonBC.common.DBCClient;
import JinRyuu.DragonBC.common.Npcs.RenderDBC;
import JinRyuu.JRMCore.JRMCoreClient;
import JinRyuu.JRMCore.JRMCoreHDBC;
import JinRyuu.JRMCore.client.config.jrmc.JGConfigClientSettings;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.client.ClientConstants;
import kamkeel.npcdbc.client.model.ModelAura;
import kamkeel.npcdbc.client.sound.ClientSound;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes3D;
import kamkeel.npcdbc.data.IAuraData;
import kamkeel.npcdbc.data.SoundSource;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.entity.EntityAura;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.ValueUtil;

import java.util.Random;

import static JinRyuu.DragonBC.common.Npcs.RenderAura2.cf;
import static JinRyuu.DragonBC.common.Npcs.RenderAura2.glColor4f;
import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class AuraRenderer extends RenderDBC {
    public static AuraRenderer Instance;
    String auraDir = "jinryuudragonbc:";
    int pulseAnimation;
    int pulseMax = 8;
    long animationStartTime;
    boolean throbOut;
    private ModelAura model;
    private float[][] lightVertRotation;
    private int lightVertN;


    public AuraRenderer() {
        super(new ModelAura(), 0.5F);
        model = (ModelAura) this.mainModel;
        this.shadowSize = 0.0F;
        this.lightVertRotation = new float[10][7];
        Instance = this;
    }

    public void animatePulsing() {
        if (!DBCClient.mc.isGamePaused()) {
            if (System.currentTimeMillis() - animationStartTime > 400 / 2 / pulseMax) {
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

    public void renderAura(EntityAura aura, float partialTicks) {

        double interPosX = aura.lastTickPosX + (aura.posX - aura.lastTickPosX) * (double) partialTicks - RenderManager.renderPosX;
        double interPosY = aura.lastTickPosY + (aura.posY - aura.lastTickPosY) * (double) partialTicks - RenderManager.renderPosY;
        double interPosZ = aura.lastTickPosZ + (aura.posZ - aura.lastTickPosZ) * (double) partialTicks - RenderManager.renderPosZ;
        if (ClientConstants.renderingGUI)
            interPosX = interPosY = interPosZ = 0;

        int speed = aura.speed;
        int age = Math.max(1, aura.ticksExisted % speed);
        Random rand = new Random();



        if (aura.type3D == EnumAuraTypes3D.None)
            return;

        float release = Math.max(5, aura.auraData.getRelease());
        float alpha = aura.alpha;
        float alphaConfig = (float) JGConfigClientSettings.CLIENT_DA21 / 10.0F;
        boolean isFirstPerson = DBCClient.mc.thePlayer == aura.entity && DBCClient.mc.gameSettings.thirdPersonView == 0;
        alpha = (isFirstPerson ? aura.isKaioken ? 0.015f : 0.0125f : alpha) * alphaConfig * (isFirstPerson ? (float) ConfigDBCClient.FirstPerson3DAuraOpacity / 100 : 1.0f);
        // alpha = 1f;
        aura.setTexture(1, CustomNpcPlusDBC.ID + ":textures/aura/auraalpha.png");

        pulseMax = 4;
        if (pulseMax > 0)
            animatePulsing();
        else
            pulseAnimation = 0;

        float pulsingSize = pulseAnimation * 0.05f;
        float kaiokenSize = 0;

        boolean isKaioken = aura.isKaioken || aura.aura.display.overrideDBCAura && aura.isInKaioken;
        if (isKaioken)
            kaiokenSize = 1f * aura.auraData.getState2();

        float stateSizeFactor = getStateSizeFactor(aura.auraData) + kaiokenSize;

        float sizeStateReleaseFactor = stateSizeFactor + (release / 100) * Math.max(stateSizeFactor * 0.75f, 2.5f); //aura gets 1.75x bigger at 100% release
        float size = aura.size + 0.1f * sizeStateReleaseFactor;
        aura.effectiveSize = size;


        double yOffset = aura.getYOffset(size);
        if (stateSizeFactor < 4)  //fixes bug in which offset is not// correct if size is too small
            yOffset -= 0.4 - (sizeStateReleaseFactor / 5) * 0.4;
        glDisable(GL_LIGHTING);
        glEnable(GL_BLEND);
        //   if (aura.isKaioken)
        //   glBlendFunc(GL_SRC_ALPHA, GL_ONE);
        glAlphaFunc(GL_GREATER, 0);
        glDepthMask(false);
        glPushMatrix();

        glStencilFunc(GL_ALWAYS, aura.entity.getEntityId() % 256, 0xFF);
        glStencilMask(0xFF);
        float r = rand.nextInt(50);
        if (aura.hasLightning && r < 5 && age < 10)
            lightning(aura, interPosX, interPosY + aura.getYOffset(), interPosZ);
        glStencilFunc(GL_GREATER, aura.entity.getEntityId() % 256, 0xFF);
        glStencilMask(0x0);


        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glPushMatrix();
        glTranslated(interPosX, interPosY + yOffset, interPosZ);
        glRotatef(180, 0, 0, 1);
        glScalef((size + pulsingSize), size, (size + pulsingSize));
        glRotatef(aura.ticksExisted % 360 * speed, 0.0F, 1.0F, 0.0F);
        renderAura(aura, aura.color1, alpha, 1f);
        glPopMatrix();


        ////////////////////////////////////////
        ////////////////////////////////////////
        //Shader stuff
        // this.renderManager.renderEngine.bindTexture(new ResourceLocation("jinryuudragonbc:aurag.png"));
//        IShaderUniform uniforms = () -> {
//            ShaderHelper.uniformColor("rgba", aura.color1, 1f);
//            ShaderHelper.uniformVec3("center", (float) aura.entity.posX, (float) aura.entity.posY, (float) aura.entity.posZ);
//            ShaderHelper.uniformTexture("noiseTexture", 2, ShaderResources.PERLIN_NOISE);
//        };

        // ShaderHelper.useShader(ShaderHelper.aura, uniforms);

        //      ShaderHelper.releaseShader();


        ////////////////////////////////////////
        ////////////////////////////////////////


        glPopMatrix();
        glDepthMask(true);
        glDisable(GL_BLEND);
        glAlphaFunc(GL_GREATER, 0.5f);
        glEnable(GL_LIGHTING);

    }

    public void renderAura(EntityAura aura, int color, float alpha, float intensity) {
        byte race = aura.auraData.getRace();
        byte state = aura.auraData.getState();

        int maxLayers = 5;
        for (float i = 1; i < maxLayers + 1; ++i) {
            float layerPercent = i / maxLayers;
            float layerTemp = layerPercent * 20f;

            for (float j = 1; j < 2; j += 0.05f) {
                this.renderManager.renderEngine.bindTexture(aura.text1);

                model.auraModel.offsetY = -(i / maxLayers) * aura.height;
                model.auraModel.offsetZ = layerTemp < 7F ? 0.2F - 1 * 0.075F : 0.35F + (1 - 7.0F) * 0.055F;
                model.auraModel.rotateAngleX = (0.9926646F - layerTemp * 0.01F) * (1 - i / maxLayers) * (1 - ((float) Math.pow(i / maxLayers, 2)));
                if (layerPercent > 0.99) //makes aura close in at top
                    model.auraModel.rotateAngleX = 100;

                model.auraModel.rotationPointY = 55.0F + (i / maxLayers) * 20;
                float r = new Random().nextInt(200);
                if (layerTemp > 3) //aura intensity
                    model.auraModel.offsetY += -r * 0.0015f * intensity * getStateIntensity(state, race);

                glPushMatrix();
                glRotatef(360 * j, 0.0F, 1.0F, 0.0F);
                if (layerPercent < 0.21) {
                    glColor4f(color, alpha);
                    this.renderManager.renderEngine.bindTexture(aura.text1);
                    model.auraModel.render(0.0625f);

                }
                glPopMatrix();

                glPushMatrix();
                glRotatef(360 * j + 45, 0F, 1F, 0F);
                if (aura.color3 > -1 && j < 1)
                    cf(aura.color1, aura.color3, alpha);
                else
                    glColor4f(color, alpha);
                model.auraModel.render(0.0625f);
                glPopMatrix();
            }

        }


    }

    private void lightning(EntityAura aura, double par2, double par4, double par6) {
        if(!JGConfigClientSettings.CLIENT_DA12)
            return;

        Random rand = new Random();

        if (aura.ticksExisted % 100 > 0 && rand.nextLong() < 1)
            return;

        Tessellator tessellator = Tessellator.instance;
        this.lightVertRotation = new float[10][7];

        glBlendFunc(GL_SRC_ALPHA, GL_ONE);
        glDisable(GL_TEXTURE_2D);
        glPushMatrix();

        boolean client = Minecraft.getMinecraft().thePlayer == aura.entity;
        float clientOffset = !client ? 1.62f : 0;
        glTranslated(par2, par4 + clientOffset, par6);
        glScalef(0.85f, 1f, 0.85f);

        double[] adouble = new double[8];
        double[] adouble1 = new double[8];
        double d3 = 0.0;
        double d4 = 0.0;
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
            glRotatef(360.0F * this.lightVertRotation[i][0], 1.0F, 0.0F, 0.0F);
            glRotatef(360.0F * this.lightVertRotation[i][1], 0.0F, 1.0F, 0.0F);
            glRotatef(360.0F * this.lightVertRotation[i][2], 0.0F, 0.0F, 1.0F);
            glTranslatef(this.lightVertRotation[i][3], this.lightVertRotation[i][4], this.lightVertRotation[i][5]);


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
        if (rand.nextInt(100) < 5) {
            if (aura.isGUIAura)
                Minecraft.getMinecraft().thePlayer.playSound("jinryuudragonbc:1610.spark", 0.0375F, 00.90f + rand.nextInt(3) * 0.05f);
            else
                new ClientSound(new SoundSource("jinryuudragonbc:1610.spark", aura.entity)).setVolume(0.1f).setPitch(0.90f + rand.nextInt(3) * 0.05f).play(false);
        }
        glPopMatrix();
        glEnable(GL_TEXTURE_2D);

    }

    public void doRender(Entity aura, double posX, double posY, double posZ, float yaw, float partialTickTime) {
        // this.renderAura((EntityAura) aura,partialTickTime);
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
                intensityFactor = 15f;
            else if (state == DBCForm.SuperSaiyanBlue)
                intensityFactor = 15f;
            else if (state == DBCForm.BlueEvo)
                intensityFactor = 15f;
        } else if (race == DBCRace.HUMAN) {
            if (state == DBCForm.HumanBuffed)
                intensityFactor = 150;
            if (state == DBCForm.HumanFullRelease)
                intensityFactor = 150f;
        } else if (race == DBCRace.NAMEKIAN) {
            if (state == DBCForm.NamekFullRelease)
                intensityFactor = 60f;
        } else if (race == DBCRace.ARCOSIAN) {
            if (state == DBCForm.Base)
                intensityFactor = 150;
            else if (state == DBCForm.FirstForm)
                intensityFactor = 120;
            else if (state == DBCForm.SecondForm)
                intensityFactor = 100;
            else if (state == DBCForm.ThirdForm)
                intensityFactor = 90;
            else if (state == DBCForm.FinalForm)
                intensityFactor = 60;
            else if (state == DBCForm.SuperForm)
                intensityFactor = 40;
            else if (state == DBCForm.UltimateForm)
                intensityFactor = 50f;

        }
        if (DBCForm.isGod(race, state))
            intensityFactor = 20f;

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

        } else if (race == DBCRace.HUMAN) {
            if (state == DBCForm.HumanBuffed)
                sizeFactor = 4;
            if (state == DBCForm.HumanFullRelease)
                sizeFactor = 4;
        } else if (race == DBCRace.NAMEKIAN) {
            if (state == DBCForm.NamekFullRelease)
                sizeFactor = 4;
            else if (state == DBCForm.NamekGod)
                sizeFactor = 1f;
        } else if (race == DBCRace.ARCOSIAN) {
            if (state == DBCForm.Base)
                sizeFactor = 0.5f;
            else if (state == DBCForm.FirstForm)
                sizeFactor = 1;
            else if (state == DBCForm.SecondForm)
                sizeFactor = 1.5f;
            else if (state == DBCForm.ThirdForm)
                sizeFactor = 2;
            else if (state == DBCForm.FinalForm)
                sizeFactor = 3;
            else if (state == DBCForm.SuperForm)
                sizeFactor = 4;
            else if (state == DBCForm.UltimateForm)
                sizeFactor = 4;
            else if (state == DBCForm.ArcoGod)
                sizeFactor = 1;
        }

        if (DBCForm.isGod(race, state))
            sizeFactor = 1f;


        if (data.getAuraEntity().entity instanceof EntityNPCInterface) {
            EntityNPCInterface npc = (EntityNPCInterface) data.getAuraEntity().entity;
//            int release = 100;
//            float size = (float) ValueUtil.clamp(npc.display.modelSize, 1, 20) / 5;
//            float effectiveSize = size * release* 0.025f;//(float) (size * ValueUtil.clamp(release, 15, 25) *(1) );
            //   float factor = effectiveSize / size * 10;
            //   return effectiveSize ;
            Form form = PlayerDataUtil.getForm(data.getAuraEntity().entity);
            if (form != null) {
                switch (form.display.hairType) {
                    case "ssj":
                        sizeFactor = 1;
                        break;
                    case "ssj2":
                        sizeFactor = 1.5f;
                        break;
                    case "ssj3":
                        sizeFactor = 2;
                        break;
                    case "ssj4":
                        sizeFactor = 2.5f;
                        break;
                }
            }
            int release = data.getRelease();
            float size = (float) ValueUtil.clamp(npc.display.modelSize, 1, 20) / 5;

            float effectiveSize = size * ValueUtil.clamp(release, 15, 25) * 0.025f;
            float factor = effectiveSize / size * 10;
            // return size * factor;
            return sizeFactor * (float) ValueUtil.clamp(npc.display.modelSize, 1, 20) * (1f + (float) ValueUtil.clamp(npc.display.modelSize, 1, 20) / 15);
        }

        if (data.getFormID() > -1 || race == DBCRace.NAMEKIAN && state == DBCForm.NamekGiant) {
            int release = data.getRelease();
            float size = JRMCoreHDBC.DBCsizeBasedOnRace2(race, state);
            float effectiveSize = size * ValueUtil.clamp(release, 15, 25) * 0.025f;
            float factor = effectiveSize / size * 10;
            return size * factor;
        }

        return sizeFactor;

    }
}
