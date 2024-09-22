package kamkeel.npcdbc.client.render;

import JinRyuu.JBRA.RenderPlayerJBRA;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.client.config.jrmc.JGConfigClientSettings;
import JinRyuu.JRMCore.i.ExtendedPlayer;
import kamkeel.npcdbc.client.ClientProxy;
import kamkeel.npcdbc.client.model.part.hair.DBCHair;
import kamkeel.npcdbc.client.shader.ShaderHelper;
import kamkeel.npcdbc.client.shader.ShaderResources;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.data.outline.Outline;
import kamkeel.npcdbc.util.DBCUtils;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import noppes.npcs.client.model.ModelMPM;
import noppes.npcs.entity.EntityCustomNpc;
import org.lwjgl.opengl.GL11;

import static kamkeel.npcdbc.client.render.RenderEventHandler.disableStencilWriting;
import static kamkeel.npcdbc.client.shader.ShaderHelper.*;
import static org.lwjgl.opengl.GL11.*;

public class OutlineRenderer {
    public static void renderOutline(RenderPlayerJBRA render, Outline outline, EntityPlayer player, float partialTicks, boolean isArm) {
        ClientProxy.renderingOutline = true;
        DBCData data = DBCData.get(player);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);
        glDepthMask(true);
        glPushMatrix();

        ///////////////////////////////////
        ///////////////////////////////////
        //Outer
        useShader(ShaderHelper.outline, () -> {
            uniformTexture("noiseTexture", 2, ShaderResources.PERLIN_NOISE);
            outline.innerColor.uniform("innerColor");
            outline.outerColor.uniform("outerColor");
            uniform1f("noiseSize", outline.noiseSize);
            uniform1f("range", outline.colorSmoothness);
            uniform1f("threshold", outline.colorInterpolation);
            uniform1f("noiseSpeed", outline.speed);
            uniform1f("throbSpeed", outline.pulsingSpeed);
        });
        float scale = 1.025f, yScale = 1.025f, outlineSize = isArm ? 1f : outline.size;
        ItemStack chestPlate = player.getEquipmentInSlot(3);
        if (chestPlate != null) {
            scale = yScale = 1.035f;
        }

        glPushMatrix();
        float size = scale * yScale * outlineSize;
        glScalef(size, 1.025f * yScale, size);

        glPushMatrix();
        glTranslatef(0, -0.020f, 0);
        if (isArm) {
            glTranslatef(0.018f, 0, 0);
            renderDBCArm(player, render);
        } else
            render.modelMain.renderBody(0.0625F);
        glPopMatrix();

        if (!isArm && !DBCForm.isMonke(data.Race, data.State)) {
            float hairSize = 1.015f;
            glScalef(hairSize, hairSize, hairSize);
            glTranslatef(0, 0.025f, 0);
            renderHair(player, render);
        }
        glPopMatrix();

        if (!isArm) {
            disableStencilWriting((player.getEntityId() + RenderEventHandler.TAIL_STENCIL_ID) % 256, false);
            glPushMatrix();
            glScalef(1.02f * 1.01f * outlineSize, 1, 1.02f * 1.02f * outlineSize);

            int race = data.Race;
            if (race == DBCRace.NAMEKIAN) {
                render.modelMain.renderHairs(0.0625F, "N");
            } else if (DBCRace.isSaiyan(race)) {
                byte ts = Byte.parseByte(JRMCoreH.dat19[data.stats.getJRMCPlayerID()].split(";")[0]);
                render.modelMain.renderHairs(0.0625F, ts != 0 && ts != -1 ? (ts == 1 ? "SJT2" : "") : "SJT1");
            } else if (race == DBCRace.ARCOSIAN) {
                byte ts = Byte.parseByte(JRMCoreH.dat19[data.stats.getJRMCPlayerID()].split(";")[0]);
                int state = data.State;
                Form form = data.getForm();
                if (form != null) {
                    if (form.display.bodyType.equals("firstform"))
                        state = 0;
                    else if (form.display.bodyType.equals("secondform"))
                        state = 2;
                    else if (form.display.bodyType.equals("thirdform"))
                        state = 3;
                    else if (form.display.bodyType.equals("finalform"))
                        state = 4;
                    else if (form.display.bodyType.equals("ultimatecooler"))
                        state = 5;
                    else if (form.display.bodyType.equals("golden"))
                        state = 6;
                }

                render.modelMain.renderHairs(0.0625F, (ts == 4 ? "n" : "") + "FR" + JRMCoreH.TransFrHrn[state]);
            }

            glPopMatrix();
            disableStencilWriting(player.getEntityId() % 256, false);
        }

        releaseShader();
        ///////////////////////////////////
        ///////////////////////////////////
        //Inner
//        glPushMatrix();
//        ColorMode.glColorInt(outline.innerColor, outline.innerAlpha); //inner
//        glScalef(scale * outline.innerSize, 1.025f, scale * outline.innerSize);
//
//        glPushMatrix();
//        glTranslatef(0, -0.015f, 0);
//
//        if (isArm)
//            renderDBCArm(player, render);
//        else
//            renderDBCPlayer(player, render);
//
//        glPopMatrix();
//
//        if (!isArm)
//            renderHair(player, render);
//
//        glPopMatrix();
//
//        glPushMatrix();
//        glScalef(1.025f, 1.025f, 1.025f);
//
//        if (!isArm)
//            renderMisc(player, render);
//
//        glPopMatrix();

        ///////////////////////////////////
        ///////////////////////////////////
        glPopMatrix();
        GL11.glEnable(GL_LIGHTING);
        GL11.glDisable(GL_BLEND);
        GL11.glEnable(GL_TEXTURE_2D);
        ClientProxy.renderingOutline = false;

    }

    public static void renderOutlineNPC(ModelMPM model, Outline outline, EntityCustomNpc npc, DBCDisplay display, float partialTicks) {

        ClientProxy.renderingOutline = true;
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);
        glPushMatrix();

        useShader(ShaderHelper.outline, () -> {
            uniformTexture("noiseTexture", 2, ShaderResources.PERLIN_NOISE);
            outline.innerColor.uniform("innerColor");
            outline.outerColor.uniform("outerColor");
            uniform1f("noiseSize", outline.noiseSize);
            uniform1f("range", outline.colorSmoothness);
            uniform1f("threshold", outline.colorInterpolation);
            uniform1f("noiseSpeed", outline.speed);
            uniform1f("throbSpeed", outline.pulsingSpeed);
        });
        ///////////////////////////////////
        ///////////////////////////////////
        //Outer
        float scale = 1.025f, yScale = 1.025f, outlineSize = outline.size;
        ItemStack chestPlate = npc.getEquipmentInSlot(3);
        if (chestPlate != null) {
            scale = yScale = 1.035f;
        }

        float size = scale * yScale * outlineSize;
        glScalef(size, size, size);

        glPushMatrix();
        glScaled(1.03, 1.03, 1.03);
        model.renderBody(npc, 0.0625f);
        glPopMatrix();

        glPushMatrix();
        float size2 = 1.03f;
        glScalef(size2, size2, size2);
        glTranslatef(0, -0.09f, 0);
        model.renderLegs(npc, 0.0625f);
        glPopMatrix();

        //Left
        byte hideArms = npc.modelData.hideArms;

        if (hideArms != 1 && hideArms != 3) {
            glPushMatrix();
            glTranslatef(-.0375f, -0.0275f, 0);
            glScaled(1.05, 1.03, 1.05);
            npc.modelData.hideArms = 2;
            model.renderArms(npc, 0.0625f, false);
            glPopMatrix();
            npc.modelData.hideArms = hideArms;
        }

        //Right
        if (hideArms != 1 && hideArms != 2) {
            glPushMatrix();
            glTranslatef(.0375f, -0.025f, 0);
            glScaled(1.05, 1.02, 1.02);
            npc.modelData.hideArms = 3;
            model.renderArms(npc, 0.0625f, false);
            glPopMatrix();
            npc.modelData.hideArms = hideArms;
        }

        boolean hideHeadWear = model.bipedHeadwear.isHidden;
        model.bipedHeadwear.isHidden = true;
        glPushMatrix();
        model.renderHead(npc, 0.0625f);
        glPopMatrix();
        model.bipedHeadwear.isHidden = hideHeadWear;


        ///////////////////////////////////
        ///////////////////////////////////
        glPopMatrix();
        releaseShader();
        GL11.glEnable(GL_LIGHTING);
        GL11.glDisable(GL_BLEND);
        GL11.glEnable(GL_TEXTURE_2D);
        ClientProxy.renderingOutline = false;

    }

    public static void renderHair(EntityPlayer player, RenderPlayerJBRA renderer) {
        DBCData data = DBCData.get(player);
        int race = data.Race;
        if (race > 2 && race < 5)
            return;

        int st = data.State, rg = data.Rage, pl = data.stats.getJRMCPlayerID();
        String dns = data.DNS, dnsH = data.DNSHair;
        int hairPreset = JRMCoreH.dnsHairB(dns);
        boolean renderHair = DBCUtils.shouldRenderHair(player, pl);
        boolean customHair = hairPreset == 12 && dnsH.length() > 3;

        Form form = data.getForm();
        if (race == DBCRace.HUMAN || race == DBCRace.SAIYAN || race == DBCRace.HALFSAIYAN) {
            if (!renderHair) {
                if (hairPreset == 10) //bald LMAO
                    renderer.modelMain.renderHeadwear(0.0625F);
                return;
            }

            if (customHair) {
                if (st == 6 || form != null && (form.display.hairType.equals("ssj3") || form.display.hairType.equals("raditz")))
                    renderer.modelMain.renderHairs(0.0625F, "" + JRMCoreH.HairsT[6] + JRMCoreH.Hairs[0]);
                else if (st == 14)
                    renderer.modelMain.renderHairsV2(0.0625F, DBCHair.SSJ4_HAIR, 0.0F, 0, 0, pl, race, renderer, (AbstractClientPlayer) player);
                else
                    renderer.modelMain.renderHairsV2(0.0625F, dnsH, 0.0F, st, rg, pl, race, renderer, (AbstractClientPlayer) player);
            } else if (st == 14 || form != null && form.display.hairType.equals("ssj4"))
                renderer.modelMain.renderHairsV2(0.0625F, DBCHair.SSJ4_HAIR, 0.0F, 0, 0, pl, race, renderer, (AbstractClientPlayer) player);
            else
                renderer.modelMain.renderHairs(0.0625F, "" + JRMCoreH.HairsT[st] + JRMCoreH.Hairs[hairPreset]);


        } else if (race == DBCRace.MAJIN) {
            if (!renderHair)
                return;

            if (customHair)
                renderer.modelMain.renderHairsV2(0.0625F, dnsH, 0.0F, st, rg, pl, race, renderer, (AbstractClientPlayer) player);
            else if (hairPreset == 10)
                renderer.modelMain.renderHairsV2(0.0625F, DBCHair.MAJIN_HAIR, 0.0F, 0, 0, pl, race, renderer, (AbstractClientPlayer) player);
            else if (hairPreset == 11)
                renderer.modelMain.renderHairsV2(0.0625F, "345052545050001250545650500023505041505000345056455050000150505250500001505052505000015050555050000150505450500001505052505000015050525050000150433450500001505055505000015050525050000154395050500001505045505000015050475050000150504750500001505047505000015043655050000150504750500001505047505000015050475050000150504750500001544545505000015250505050003450505050500034505050505000015250505050000150505050500001505050505000015050505050000150505050500001525050505000015050505050000150505050500001525050505000235250505050003450505050500034505050505000235250505050000180501850500034695050505000346950505050000180501950500001805019505000345850505050003463505050500001805018505000018050185050003476505050500034765050505000018050195050003480501850500034505050505000345050505050003480501950500020", 0.0F, 0, 0, pl, race, renderer, (AbstractClientPlayer) player);

        }
    }

    public static void renderDBCArm(EntityPlayer player, RenderPlayerJBRA renderer) {
        boolean instantTransmission = ExtendedPlayer.get(player).getBlocking() == 2;
        int[] an = new int[]{1, 0, 2, 0, 0, 3, 0, 1, 1};
        int id = ExtendedPlayer.get(player).getBlocking() != 0 ? (instantTransmission ? 6 : 0) : (ExtendedPlayer.get(player).getAnimKiShoot() != 0 ? an[ExtendedPlayer.get(player).getAnimKiShoot() - 1] + 2 : -1);
        renderer.modelMain.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);

        if (id == -1 || !JGConfigClientSettings.CLIENT_DA4)
            renderer.modelMain.RA.render(0.0625F);
        else {
            if (id == 0) {
                if (JGConfigClientSettings.CLIENT_DA18) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(-0.2F, -0.4F, -0.8F);
                    GL11.glRotatef(50.0F, 1.0F, 0.0F, 1.0F);
                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
                    renderer.modelMain.LA.render(0.0625F);
                    GL11.glPopMatrix();
                }
            } else if (id == 3) {
                GL11.glPushMatrix();
                GL11.glTranslatef(0.1F, -0.2F, -0.5F);
                GL11.glTranslatef(-0.25F, 0.0F, -0.045F);
                GL11.glRotatef(10.0F, -1.0F, 0.0F, 0.0F);
                GL11.glRotatef(20.0F, 0.0F, 0.0F, -1.0F);
                GL11.glRotatef(115.0F, 0.0F, 1.0F, 0.0F);
                renderer.modelMain.LA.render(0.0625F);
                GL11.glPopMatrix();
            } else if (id == 5) {
                GL11.glPushMatrix();
                GL11.glTranslatef(-0.2F, -0.4F, -0.8F);
                GL11.glTranslatef(-0.4F, 0.1F, -0.1F);

                GL11.glRotatef(42.0F, -1.0F, 0.0F, 0.0F);
                GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(115.0F, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(-0.631F, 0.02125F, 0.235F);
                renderer.modelMain.LA.render(0.0625F);
                GL11.glPopMatrix();
            }

            if (id != 0 && id != 6) {
                if (id != 2 && id != 3) {
                    if (id == 4 || id == 5) {
                        GL11.glPushMatrix();
                        GL11.glTranslatef(-0.2F, 0.4F, -0.105F);
                        GL11.glRotatef(10.0F, -1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(20.0F, 0.0F, 0.0F, -1.0F);
                        GL11.glRotatef(40.0F, 0.0F, 0.0F, 1.0F);
                        renderer.modelMain.RA.render(0.0625F);
                        GL11.glPopMatrix();
                    }
                } else {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(-0.2F, 0.0F, -0.1F);
                    GL11.glRotatef(10.0F, -1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(20.0F, 0.0F, 0.0F, -1.0F);
                    renderer.modelMain.RA.render(0.0625F);
                    GL11.glPopMatrix();
                }
            } else {
                label53:
                {
                    if (id == 0) {
                        if (!JGConfigClientSettings.CLIENT_DA18) {
                            break label53;
                        }
                    } else if (!JGConfigClientSettings.instantTransmissionFirstPerson) {
                        break label53;
                    }

                    GL11.glPushMatrix();
                    GL11.glTranslatef(-0.48F, -0.14F, -0.13515F);
                    GL11.glRotatef(40.0F, 0.0F, 0.0F, -1.0F);
                    GL11.glRotatef(80.0F, -1.0F, 0.0F, 0.0F);
                    GL11.glRotatef((float) (id == 0 ? -20 : 30), 0.0F, 0.0F, 1.0F);
                }

                renderer.modelMain.RA.render(0.0625F);
                if (id == 0) {
                    if (!JGConfigClientSettings.CLIENT_DA18) {
                        return;
                    }
                } else if (!JGConfigClientSettings.instantTransmissionFirstPerson) {
                    return;
                }

                GL11.glPopMatrix();
            }
        }
    }
}
