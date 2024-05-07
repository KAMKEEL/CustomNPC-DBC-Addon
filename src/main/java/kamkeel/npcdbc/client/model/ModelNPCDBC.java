package kamkeel.npcdbc.client.model;

import JinRyuu.JBRA.ModelRendererJBRA;
import JinRyuu.JBRA.RenderPlayerJBRA;
import JinRyuu.JBRA.mod_JBRA;
import JinRyuu.JRMCore.JRMCoreClient;
import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixin.INPCDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.model.ModelMPM;
import noppes.npcs.entity.EntityNPCInterface;
import org.lwjgl.opengl.GL11;

public class ModelNPCDBC extends ModelBase {

    private final ModelMPM parent;
    public ModelRendererJBRA[] hairall;
    public float rot1;
    public float rot2;
    public float rot3;
    public float rot4;
    public float rot5;
    public float rot6;

    public ModelRenderer nose;
    public ModelRenderer mouth;
    public ModelRenderer eyeleft;
    public ModelRenderer eyeright;
    public ModelRenderer eyebase;
    public ModelRenderer eyebrow;

    public TextureManager tex;

    public int stateChange = 0;

    public ModelNPCDBC(ModelMPM mpm) {
        this.parent = mpm;
        this.textureHeight = 32;
        this.textureWidth = 64;
        tex = Minecraft.getMinecraft().renderEngine;

        // Init Hair
        this.hairall = new ModelRendererJBRA[224];
        int hossz;
        int face;
        for (hossz = 0; hossz < 4; ++hossz) {
            for (face = 0; face < 56; ++face) {
                if (this.hairall[hossz + face * 4] == null) {
                    this.hairall[hossz + face * 4] = new ModelRendererJBRA(this, 32, 0);
                    this.hairall[hossz + face * 4].addBox(-1.0F, hossz == 0 ? -1.0F : 0.0F, -1.0F, 2, 3, 2);
                    this.hairall[hossz + face * 4].setRotationPoint(0.0F, 0.0F, 0.0F);
                    this.setRotation(this.hairall[hossz + face * 4], 0.0F, 0.0F, 0.0F);
                }
            }
        }
        for (hossz = 0; hossz < 4; ++hossz) {
            for (face = 0; face < 56; ++face) {
                if (hossz != 3) {
                    this.hairall[hossz + face * 4].addChild(this.hairall[hossz + 1 + face * 4]);
                }
            }
        }

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
    }

    public static int dnsHair1(String s, int n) {
        int a = 0;
        try {
            a = Integer.parseInt(s.charAt(n) + "");
        } catch (NumberFormatException var3) {

        }
        return s.length() > n ? a : 0;
    }

    public static int dnsHair2(String s, int n) {
        int a = 0;
        try {
            a = Integer.parseInt(s.charAt(n) + s.charAt(n + 1) + "");
        } catch (NumberFormatException var3) {

        }
        return s.length() > n ? a : 0;
    }


    public void renderFace(EntityNPCInterface npc, DBCDisplay display) {
        int bodyColor = 16297621;
        RenderPlayerJBRA.glColor3f(bodyColor);

        int noseType = 2;
        tex.bindTexture(new ResourceLocation("jinryuumodscore", "cc/humn" + noseType + ".png"));

        this.nose.rotateAngleY = parent.bipedHead.rotateAngleY;
        this.nose.rotateAngleX = parent.bipedHead.rotateAngleX;
        this.nose.rotationPointX = parent.bipedHead.rotationPointX;
        this.nose.rotationPointY = parent.bipedHead.rotationPointY;
        this.nose.render(0.0625F);


        int mouthType = 1;
        tex.bindTexture(new ResourceLocation("jinryuumodscore", "cc/humm" + mouthType + ".png"));
        this.mouth.rotateAngleY = parent.bipedHead.rotateAngleY;
        this.mouth.rotateAngleX = parent.bipedHead.rotateAngleX;
        this.mouth.rotationPointX = parent.bipedHead.rotationPointX;
        this.mouth.rotationPointY = parent.bipedHead.rotationPointY;
        this.mouth.render(0.0625F);

        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        int eyeType = 0;
        tex.bindTexture(new ResourceLocation("jinryuumodscore", "cc/humb" + eyeType + ".png"));
        this.eyebase.rotateAngleY = parent.bipedHead.rotateAngleY;
        this.eyebase.rotateAngleX = parent.bipedHead.rotateAngleX;
        this.eyebase.rotationPointX = parent.bipedHead.rotationPointX;
        this.eyebase.rotationPointY = parent.bipedHead.rotationPointY;
        this.eyebase.render(0.0625F);

        tex.bindTexture(new ResourceLocation("jinryuumodscore", "cc/humw" + eyeType + ".png"));
        RenderPlayerJBRA.glColor3f(display.getHairColor());
        this.eyebrow.rotateAngleY = parent.bipedHead.rotateAngleY;
        this.eyebrow.rotateAngleX = parent.bipedHead.rotateAngleX;
        this.eyebrow.rotationPointX = parent.bipedHead.rotationPointX;
        this.eyebrow.rotationPointY = parent.bipedHead.rotationPointY;
        this.eyebrow.render(0.0625F);

        RenderPlayerJBRA.glColor3f(display.getEyeColor());
        tex.bindTexture(new ResourceLocation("jinryuumodscore", "cc/huml" + eyeType + ".png"));
        this.eyeleft.rotateAngleY = parent.bipedHead.rotateAngleY;
        this.eyeleft.rotateAngleX = parent.bipedHead.rotateAngleX;
        this.eyeleft.rotationPointX = parent.bipedHead.rotationPointX;
        this.eyeleft.rotationPointY = parent.bipedHead.rotationPointY;
        this.eyeleft.render(0.0625F);

        tex.bindTexture(new ResourceLocation("jinryuumodscore", "cc/humr" + eyeType + ".png"));
        this.eyeright.rotateAngleY = parent.bipedHead.rotateAngleY;
        this.eyeright.rotateAngleX = parent.bipedHead.rotateAngleX;
        this.eyeright.rotationPointX = parent.bipedHead.rotationPointX;
        this.eyeright.rotationPointY = parent.bipedHead.rotationPointY;
        this.eyeright.render(0.0625F);

    }

    public void renderHead(EntityNPCInterface npc) {
        DBCDisplay display = ((INPCDisplay) npc.display).getDBCDisplay();
        if (display.enabled && !display.getHairCode().isEmpty()) {
            tex.bindTexture(new ResourceLocation("jinryuumodscore:gui/normall.png"));
            GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
            RenderPlayerJBRA.glColor3f(display.getHairColor());
            renderHairs(npc, 0.0625F, display.getHairCode(), 0, 0, null);

            renderFace(npc, display);
            GL11.glPopAttrib();
            parent.currentlyPlayerTexture = false;
        }
    }

    public void renderBody(EntityNPCInterface npc) {

    }

    public void renderHairs(EntityNPCInterface npc, float par1, String h, int state, int rage, RenderPlayerJBRA rp) {
        String playerName = "Kam";//JRMCoreH.plyrs[pl];
        boolean canUse = mod_JBRA.a6P9H9B;
        boolean pstrty = false;//super form selected;JRMCoreH.plyrSttngsClient(1, pl);
        boolean aura = false;//aura anim JRMCoreH.StusEfctsClient(4, pl);
        boolean trbo = false; //turbo anim JRMCoreH.StusEfctsClient(3, pl);
        boolean kken = false;//kaioken anim JRMCoreH.StusEfctsClient(5, pl);
        boolean trty = false; //transforming anim JRMCoreH.StusEfctsClient(1, pl);

        boolean hasHairAnimations = true;
        int trTime = canUse ? 2 : 200;
        int arTime = canUse ? 2 : 200;
//        if (hasHairAnimations) {
//            if (JRMCoreH.HairsT(state, "B") && rp.getStateChange(playerName) < 200) {
//                rp.setStateChange(rp.getStateChange(playerName) + trTime, playerName);
//            }
//
//            if (JRMCoreH.HairsT(state, "C")) {
//                if (rp.getStateChange(playerName) < 200) {
//                    rp.setStateChange(rp.getStateChange(playerName) + trTime, playerName);
//                }
//
//                if (rp.getState2Change(playerName) < 200) {
//                    rp.setState2Change(rp.getState2Change(playerName) + trTime, playerName);
//                }
//            }
//
//            if (JRMCoreH.HairsT(rp.getState(playerName), "A") && !JRMCoreH.HairsT(state, "A")) {
//                if (!JRMCoreH.HairsT(rp.getState(playerName), state) && rp.getStateChange(playerName) < 200) {
//                    rp.setStateChange(rp.getStateChange(playerName) + trTime, playerName);
//                }
//
//                if (rp.getStateChange(playerName) >= 200) {
//                    rp.setStateChange(200, playerName);
//                    rp.setState(state, playerName);
//                }
//            } else if (!JRMCoreH.HairsT(rp.getState(playerName), "A") && JRMCoreH.HairsT(state, "A")) {
//                if ((!JRMCoreH.HairsT(rp.getState(playerName), state) || rage == 0) && rp.getStateChange(playerName) > 0) {
//                    rp.setStateChange(rp.getStateChange(playerName) - trTime, playerName);
//                }
//
//                if (rp.getStateChange(playerName) <= 0) {
//                    rp.setStateChange(0, playerName);
//                    rp.setState(state, playerName);
//                }
//            } else if (!JRMCoreH.HairsT(rp.getState(playerName), state) && JRMCoreH.HairsT(rp.getState(playerName), "B") && JRMCoreH.HairsT(state, "B")) {
//                rp.setState(state, playerName);
//            } else if (JRMCoreH.HairsT(rp.getState(playerName), "A")) {
//                if (!canUse && JRMCoreH.HairsT(rp.getState(playerName), state) && rage > 90) {
//                    rp.setStateChange(rp.getStateChange(playerName) + trTime, playerName);
//                    if (rp.getStateChange(playerName) > 200) {
//                        rp.setStateChange(200, playerName);
//                    }
//                } else if (canUse && JRMCoreH.HairsT(rp.getState(playerName), state) && rage > 0 && rp.getStateChange(playerName) < rage * 2) {
//                    rp.setStateChange(rp.getStateChange(playerName) + trTime, playerName);
//                } else if (JRMCoreH.HairsT(rp.getState(playerName), state)) {
//                    if (rp.getStateChange(playerName) > 0) {
//                        rp.setStateChange(rp.getStateChange(playerName) - trTime, playerName);
//                    } else {
//                        rp.setStateChange(0, playerName);
//                    }
//
//                    if (rp.getState2Change(playerName) > 0) {
//                        rp.setState2Change(rp.getState2Change(playerName) - trTime, playerName);
//                    } else {
//                        rp.setState2Change(0, playerName);
//                    }
//                }
//            } else if ((!JRMCoreH.HairsT(state, "B") || !pstrty) && !JRMCoreH.HairsT(state, "B")) {
//                if (!JRMCoreH.HairsT(rp.getState(playerName), state) && JRMCoreH.HairsT(state, "C")) {
//                    if (rp.getState2Change(playerName) < 200) {
//                        rp.setState2Change(rp.getState2Change(playerName) + trTime, playerName);
//                    }
//
//                    if (rp.getState2Change(playerName) >= 200) {
//                        rp.setState2Change(200, playerName);
//                        rp.setState(state, playerName);
//                    }
//                }
//            } else if (!canUse && JRMCoreH.HairsT(rp.getState(playerName), state) && rage > 90) {
//                rp.setState2Change(rp.getState2Change(playerName) + trTime, playerName);
//                if (rp.getState2Change(playerName) > 200) {
//                    rp.setState2Change(200, playerName);
//                }
//            } else if (canUse && JRMCoreH.HairsT(rp.getState(playerName), state) && rage > 0 && rp.getState2Change(playerName) < rage * 2) {
//                rp.setState2Change(rp.getState2Change(playerName) + trTime, playerName);
//            } else if (rp.getState2Change(playerName) > 200) {
//                rp.setState2Change(200, playerName);
//                rp.setState(state, playerName);
//            } else if (rp.getState2Change(playerName) > 0) {
//                rp.setState2Change(rp.getState2Change(playerName) - trTime, playerName);
//            } else if (rp.getState2Change(playerName) != 0) {
//                rp.setState2Change(0, playerName);
//            }
//        }
//
//        if (canUse && (aura || trty || kken || trbo)) { //turbo/kaioken/charging hair animation
//            if (JRMCoreH.HairsT(rp.getState(playerName), state) && rp.getAuratime(playerName) < 50) {
//                if (rp.getAuratime(playerName) < 50 && rp.getAuratype(playerName) == 0) {
//                    rp.setAuratime(rp.getAuratime(playerName) + arTime, playerName);
//                }
//
//                if (rp.getAuratime(playerName) >= 50) {
//                    rp.setAuratype(1, playerName);
//                }
//
//                if (rp.getAuratime(playerName) < 20 && rp.getAuratype(playerName) == 1) {
//                    rp.setAuratype(0, playerName);
//                }
//
//                if (rp.getAuratime(playerName) > 0 && rp.getAuratype(playerName) == 1) {
//                    rp.setAuratime(rp.getAuratime(playerName) - arTime, playerName);
//                }
//            } else if (JRMCoreH.HairsT(rp.getState(playerName), state) && !JRMCoreH.HairsT(state, "A")) {
//                if (rp.getAuratype(playerName) < 2) {
//                    rp.setAuratype(2, playerName);
//                }
//
//                if (rp.getBendtime(playerName) < 50 && rp.getAuratype(playerName) == 2) {
//                    rp.setBendtime(rp.getBendtime(playerName) + arTime, playerName);
//                }
//
//                if (rp.getBendtime(playerName) >= 50) {
//                    rp.setAuratype(3, playerName);
//                }
//
//                if (rp.getBendtime(playerName) < 20 && rp.getAuratype(playerName) == 3) {
//                    rp.setAuratype(2, playerName);
//                }
//
//                if (rp.getBendtime(playerName) > 0 && rp.getAuratype(playerName) == 3) {
//                    rp.setBendtime(rp.getBendtime(playerName) - arTime, playerName);
//                }
//            }
//        } else {
//            if (rp.getAuratype(playerName) > 0) {
//                rp.setAuratype(0, playerName);
//            }
//
//            if (rp.getBendtime(playerName) > 0) {
//                rp.setBendtime(rp.getBendtime(playerName) - 1, playerName);
//            }
//
//            if (rp.getAuratime(playerName) > 0) {
//                rp.setAuratime(rp.getAuratime(playerName) - 1, playerName);
//            }
//        }

        GL11.glPushMatrix();
        GL11.glScalef((0.5F + 0.5F / 1.0F) * 1.0F, 0.5F + 0.5F / 1.0F, (0.5F + 0.5F / 1.0F) * 1.0F);
        GL11.glTranslatef(0.0F, (1.0F - 1.0F) / 1.0F * (2.0F - (1.0F >= 1.5F && 1.0F <= 2.0F ? (2.0F - 1.0F) / 2.5F : (1.0F < 1.5F && 1.0F >= 1.0F ? (1.0F * 2.0F - 2.0F) * 0.2F : 0.0F))), 0.0F);
        float[] var10000 = new float[]{0.6F, 0.5F, 0.4F, -0.5F};
        var10000 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
        int[] hairRightPosZ = new int[]{3, 2, 1, 0, 3, 2, 1, 3, 2, 3};
        int[] hairRightPosY = new int[]{0, 0, 0, 0, 1, 1, 1, 2, 2, 3};
        int[] hairLeftPosZ = new int[]{0, 1, 2, 3, 1, 2, 3, 2, 3, 3};
        int[] hairLeftPosY = new int[]{0, 0, 0, 0, 1, 1, 1, 2, 2, 3};
        int[] hairBackPosX = new int[]{0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3};
        int[] hairBackPosY = new int[]{0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3};
        int[] hairTopPosX = new int[]{0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3};
        int[] hairTopPosZ = new int[]{0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3};
        int[] hairPos = new int[]{0, 4, 14, 24, 40, 56};
        String hairdns = h;

        for (int face = 0; face < 56; ++face) {
            int l = dnsHair2(hairdns, face * 14);
            if (l != 0) {
                int X = dnsHair2(hairdns, face * 14 + 2);
                int Y = dnsHair2(hairdns, face * 14 + 4);
                int Z = dnsHair2(hairdns, face * 14 + 6);
                int B = dnsHair2(hairdns, face * 14 + 8);
                int P = dnsHair2(hairdns, face * 14 + 10);
                int T = dnsHair2(hairdns, face * 14 + 12);
                X = X > 82 ? 82 : (X < 18 ? 18 : X);
                Y = Y > 82 ? 82 : (Y < 18 ? 18 : Y);
                Z = Z > 82 ? 82 : (Z < 18 ? 18 : Z);
                B = B > 82 ? 82 : (B < 18 ? 18 : B);
                P = P > 82 ? 82 : (P < 18 ? 18 : P);
                T = T > 82 ? 82 : (T < 18 ? 18 : T);
                float x = (float) (X - 50) * 0.1F;
                float y = (float) (Y - 50) * 0.1F;
                float z = (float) (Z - 50) * 0.1F;
                float b = (float) (B - 50) * 0.1F;
                float p = (float) (P - 50) * 0.1F;
                int t = (int) ((float) (T - 18) * 1.62F);
                float Int = (float) t * 0.01F;
                boolean hpFront = face >= hairPos[0] && face < hairPos[1];
                boolean hpTop = face >= hairPos[4] && face < hairPos[5];
                boolean hpRight = face >= hairPos[1] && face < hairPos[2];
                boolean hpLeft = face >= hairPos[2] && face < hairPos[3];
                boolean hpBack = face >= hairPos[3] && face < hairPos[4];
//                if (rp.getStateChange(playerName) > 0 && l > 0) {
//                    if (y > -1.0F && y < 1.0F && z > -1.0F && z < 1.0F && hpBack) {
//                        x += (float) rp.getStateChange(playerName) * Int * (x < 0.0F ? -0.01F : 0.01F) * (float) l * 0.01F;
//                        x = x > 3.0F ? 3.0F : x;
//                        x = x < -3.0F ? -3.0F : x;
//                    }
//
//                    if (y > -1.0F && y < 1.0F && x > -1.0F && x < 1.0F && !hpBack) {
//                        z += (float) rp.getStateChange(playerName) * Int * (z < 0.0F ? -0.01F : 0.01F);
//                        z = z > 3.2F ? 3.2F : z;
//                        z = z < -3.2F ? -3.2F : z;
//                        if (!hpFront || x < 0.0F) {
//                            x += (float) rp.getStateChange(playerName) * Int * 0.01F;
//                            x = x > 0.4F ? 0.4F : x;
//                            x = x < -0.4F ? -0.4F : x;
//                        }
//
//                        if (z > 0.0F) {
//                            boolean add = hpTop ? hairTopPosZ[face - hairPos[4]] == 0 || hairTopPosZ[face - hairPos[4]] == 2 : false;
//                            boolean add2 = hpTop ? face % 4 == 0 || face % 4 == 3 : false;
//                            b += (float) rp.getStateChange(playerName) * Int * -0.02F;
//                            b = b < (add && add2 ? 0.0F : -0.2F) ? (add && add2 ? 0.0F : -0.2F) : b;
//                        } else if (z < 0.0F) {
//                            boolean add = hpTop ? hairTopPosZ[face - hairPos[4]] == 0 || hairTopPosZ[face - hairPos[4]] == 2 : false;
//                            boolean add2 = hpTop ? face % 4 == 0 || face % 4 == 3 : false;
//                            b += (float) rp.getStateChange(playerName) * Int * 0.02F;
//                            b = b > (add && add2 ? 0.0F : 0.2F) ? (add && add2 ? 0.0F : 0.2F) : b;
//                        }
//                    } else if (y > -1.0F && y < 1.0F) {
//                        x += (float) rp.getStateChange(playerName) * Int * (x < 0.0F ? -0.01F : 0.01F);
//                        x = x > 2.8F ? 2.8F : x;
//                        x = x < -2.8F ? -2.8F : x;
//                        if (b > 1.5F) {
//                            x = x > 1.5F ? 1.5F : x;
//                            x = x < -1.5F ? -1.5F : x;
//                            b += (float) rp.getStateChange(playerName) * Int * (b < 0.0F ? 0.03F : -0.03F);
//                            b = b > 2.8F ? 2.8F : b;
//                            b = b < -2.8F ? -2.8F : b;
//                        }
//                    } else if (x > -1.0F && x < 1.0F) {
//                        z += (float) rp.getStateChange(playerName) * Int * (z < 0.0F ? -0.01F : 0.01F);
//                        z = z > 2.8F ? 2.8F : z;
//                        z = z < -2.8F ? -2.8F : z;
//                        if (b > 0.0F && z > 0.0F && y < 1.6F) {
//                            z = z > 2.2F ? 2.2F : z;
//                            z = z < -2.2F ? -2.2F : z;
//                            float var91 = b + (float) rp.getStateChange(playerName) * Int * -0.02F;
//                            float var92 = var91 > b ? b : var91;
//                            b = var92 < -b ? -b : var92;
//                        } else if (b > 0.0F && z < 0.0F && y > 0.0F) {
//                            z = z > 2.2F ? 2.2F : z;
//                            z = z < -2.2F ? -2.2F : z;
//                            float var89 = b + (float) rp.getStateChange(playerName) * Int * -0.02F;
//                            float var90 = var89 > b ? b : var89;
//                            b = var90 < -b ? -b : var90;
//                        } else if (y < -1.3F && b > 0.0F) {
//                            z = z > 2.2F ? 2.2F : z;
//                            z = z < -2.2F ? -2.2F : z;
//                            b += (float) rp.getStateChange(playerName) * Int * -0.02F;
//                            b = b < 0.5F ? 0.5F : b;
//                        }
//                    }
//                }
//
//                if (rp.getState2Change(playerName) > 0) {
//                    if (y > -1.0F && y < 1.0F && x > -1.0F && x < 1.0F && hpFront) {
//                        float Int2 = Int > 0.02F ? 0.6F : Int;
//                        x += (float) rp.getState2Change(playerName) * Int2 * 0.01F;
//                        x = x > 0.2F ? 0.2F : x;
//                        x = x < -0.2F ? -0.2F : x;
//                        z += (float) rp.getState2Change(playerName) * Int2 * (z < 0.0F ? -0.02F : 0.02F);
//                        z = z > 2.8F ? 2.8F : z;
//                        z = z < -2.8F ? -2.8F : z;
//                    }
//
//                    l = (int) ((float) l + (float) rp.getState2Change(playerName) * 0.1F);
//                    if (b < 0.0F) {
//                        b += (float) rp.getState2Change(playerName) * 5.0E-4F;
//                        b = b >= 0.0F ? 0.2F : b;
//                    }
//
//                    if (b > 0.0F) {
//                        b += (float) rp.getState2Change(playerName) * -5.0E-4F;
//                        b = b <= 0.0F ? -0.2F : b;
//                    }
//                }
//
//                if (rp.getBendtime(playerName) > 0) {
//                    z += (float) rp.getBendtime(playerName) * (z < 0.0F ? -0.0025F : 0.0025F);
//                    b += (float) rp.getBendtime(playerName) * (b > 0.0F ? -0.005F : 0.005F);
//                    z = z > 3.2F ? 3.2F : z;
//                    z = z < -3.2F ? -3.2F : z;
//                }
//
//                if (rp.getAuratime(playerName) > 0) {
//                    z += (float) rp.getAuratime(playerName) * (z < 0.0F ? -0.0025F : 0.0025F);
//                    b += (float) rp.getAuratime(playerName) * (b > 0.0F ? -0.005F : 0.005F);
//                    z = z > 3.2F ? 3.2F : z;
//                    z = z < -3.2F ? -3.2F : z;
//                }

                int lng = 0;
                if (!JRMCoreClient.mc.isGamePaused()) {
                    this.setRotation(this.hairall[lng + face * 4], x, y, z);
                    this.hairall[lng + face * 4].rotationPointX = -2.999F + (float) (face < 4 ? face * 2 : (face >= 14 && face < 24 ? 7 : (face >= 24 && face < 40 ? hairBackPosX[face - 4 - 10 - 10] * 2 : (face >= 40 && face < 56 ? hairTopPosX[face - 4 - 10 - 10 - 16] * 2 : -1))));
                    this.hairall[lng + face * 4].rotationPointZ = -3.999F + (face >= 4 && face < 14 ? (float) (hairRightPosZ[face - 4] * 2 + 1) : (face >= 14 && face < 24 ? (float) (hairLeftPosZ[face - 4 - 10] * 2 + 1) : (face >= 24 && face < 40 ? 8.0F : (face >= 40 && face < 56 ? (float) (hairTopPosZ[face - 4 - 10 - 10 - 16] * 2) + 0.9F : 0.0F))));
                    this.hairall[lng + face * 4].rotationPointY = -7.0F + (face >= 4 && face < 14 ? (float) (hairRightPosY[face - 4] * 2) : (face >= 14 && face < 24 ? (float) (hairLeftPosY[face - 4 - 10] * 2) : (face >= 24 && face < 40 ? (float) (hairBackPosY[face - 4 - 10 - 10] * 2) : -0.5F)));
                    float f = 1.57F;
                    float r = MathHelper.sin(this.rot3 * 0.02F) * 0.1F;
                    float r2 = MathHelper.cos(this.rot3 * 0.02F) * 0.1F;
                    float r3 = MathHelper.cos(this.rot3 * 0.14F) * 0.1F;
                    this.hairall[1 + face * 4].rotateAngleY = 0.0F;
                    this.hairall[1 + face * 4].rotateAngleX = -0.0F;
                    this.hairall[2 + face * 4].rotateAngleY = 0.0F;
                    this.hairall[2 + face * 4].rotateAngleX = 0.0F;
                    this.hairall[3 + face * 4].rotateAngleY = 0.0F;
                    this.hairall[3 + face * 4].rotateAngleX = 0.0F;
                    if (!hpTop && !hpRight && !hpLeft) {
                        this.hairall[1 + face * 4].rotateAngleX = b * 0.3F * (p > 0.5F ? 1.0F - p * 0.3F : (p < -0.5F ? 1.0F + -p * 0.1F : 1.0F));
                        this.hairall[2 + face * 4].rotateAngleX = b * 0.3F;
                        this.hairall[3 + face * 4].rotateAngleX = b * 0.3F * (p > 0.5F ? 1.0F + p * 0.1F : (p < -0.5F ? 1.0F - -p * 0.3F : 1.0F));
                    } else {
                        int min = hpLeft ? 1 : -1;
                        this.hairall[1 + face * 4].rotateAngleZ = (float) min * b * 0.3F * (p > 0.5F ? 1.0F - p * 0.3F : (p < -0.5F ? 1.0F + -p * 0.1F : 1.0F));
                        this.hairall[2 + face * 4].rotateAngleZ = (float) min * b * 0.3F;
                        this.hairall[3 + face * 4].rotateAngleZ = (float) min * b * 0.3F * (p > 0.5F ? 1.0F + p * 0.1F : (p < -0.5F ? 1.0F - -p * 0.3F : 1.0F));
                    }
                }

                this.hairall[1 + face * 4].rotationPointX = 0.0F;
                this.hairall[1 + face * 4].rotationPointZ = 0.0F;
                this.hairall[1 + face * 4].rotationPointY = 1.5F;
                this.hairall[2 + face * 4].rotationPointX = 0.0F;
                this.hairall[2 + face * 4].rotationPointZ = 0.0F;
                this.hairall[2 + face * 4].rotationPointY = 2.5F;
                this.hairall[3 + face * 4].rotationPointX = 0.0F;
                this.hairall[3 + face * 4].rotationPointZ = 0.0F;
                this.hairall[3 + face * 4].rotationPointY = 2.5F;
                GL11.glPushMatrix();
                GL11.glTranslatef(parent.bipedHead.rotationPointX * par1, parent.bipedHead.rotationPointY * par1, parent.bipedHead.rotationPointZ * par1);
                if (parent.bipedHead.rotateAngleZ != 0.0F) {
                    GL11.glRotatef(parent.bipedHead.rotateAngleZ * (180.0F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
                }

                if (parent.bipedHead.rotateAngleY != 0.0F) {
                    GL11.glRotatef(parent.bipedHead.rotateAngleY * (180.0F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
                }

                if (parent.bipedHead.rotateAngleX != 0.0F) {
                    GL11.glRotatef(parent.bipedHead.rotateAngleX * (180.0F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
                }

                GL11.glPushMatrix();
                var10000 = new float[]{4.0F, 2.0F, 1.5F, 1.0F, 1.0F};
                boolean[] var107 = new boolean[]{false, true, true, true, true};
                boolean[] var108 = new boolean[]{false, false, true, true, true};
                boolean[] var109 = new boolean[]{false, false, false, true, true};
                float tincs1 = (float) l < 33.0F ? (float) l / 33.0F : 1.0F;
                float tincs2 = (float) l > 33.0F && (float) l < 66.0F ? ((float) l - 33.0F) / 33.0F : ((float) l < 33.0F ? 0.0F : 1.0F);
                float tincs3 = (float) l > 66.0F ? ((float) l - 66.0F) / 33.0F : ((float) l < 66.0F ? 0.0F : 1.0F);
                this.hairall[lng + face * 4].lengthY = 1.0F;
                this.hairall[1 + face * 4].lengthY = tincs1;
                this.hairall[2 + face * 4].lengthY = tincs2;
                this.hairall[3 + face * 4].lengthY = tincs3;
                this.hairall[0 + face * 4].sizeXZ = 1.1F;
                this.hairall[1 + face * 4].sizeXZ = 1.0F;
                this.hairall[2 + face * 4].sizeXZ = 0.9F;
                this.hairall[3 + face * 4].sizeXZ = 0.8F;
                this.hairall[1 + face * 4].showModel = (float) l > 0.0F;
                this.hairall[2 + face * 4].showModel = (float) l > 33.0F;
                this.hairall[3 + face * 4].showModel = (float) l > 66.0F;
                this.hairall[lng + face * 4].render(par1);
                GL11.glPopMatrix();
                GL11.glPopMatrix();
            }
        }

        GL11.glScalef(1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    private void setRotation(ModelRendererJBRA model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
