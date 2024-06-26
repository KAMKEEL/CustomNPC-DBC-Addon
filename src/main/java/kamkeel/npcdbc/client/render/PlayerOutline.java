package kamkeel.npcdbc.client.render;

import JinRyuu.JBRA.RenderPlayerJBRA;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.client.config.jrmc.JGConfigClientSettings;
import JinRyuu.JRMCore.i.ExtendedPlayer;
import kamkeel.npcdbc.client.ClientProxy;
import kamkeel.npcdbc.client.ColorMode;
import kamkeel.npcdbc.client.shader.ShaderHelper;
import kamkeel.npcdbc.client.shader.ShaderResources;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.mixins.early.IEntityMC;
import kamkeel.npcdbc.util.DBCUtils;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import static kamkeel.npcdbc.client.render.RenderEventHandler.disableStencilWriting;
import static kamkeel.npcdbc.client.shader.ShaderHelper.*;
import static org.lwjgl.opengl.GL11.*;

public class PlayerOutline {
    public int innerColor, outerColor;
    public float innerAlpha = 1f, outerAlpha = 1f, innerSize = 1f, outerSize = 1f;

    public PlayerOutline(int innerColor, int outerColor) {
        this.innerColor = innerColor;
        this.outerColor = outerColor;
    }

    public PlayerOutline setAlpha(float inner, float outer) {
        if (inner > 0)
            innerAlpha = Math.min(inner, 1f);
        if (outer > 0)
            outerAlpha = Math.min(outer, 1f);
        return this;
    }

    public PlayerOutline setSize(float inner, float outer) {
        if (inner > 0)
            innerSize = Math.min(inner, 5f);
        if (outer > 0)
            outerSize = Math.min(outer, 5f);
        return this;
    }

    public static void renderOutline(RenderPlayerJBRA render, EntityPlayer player, float partialTicks, boolean isArm) {
        PlayerOutline outline = DBCData.get(player).outline;
        ClientProxy.renderingOutline = true;
        if (player.isInWater())
            ((IEntityMC) player).setRenderPass(0);
        else
            ((IEntityMC) player).setRenderPass(ClientProxy.MiddleRenderPass);


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
            uniformColor("innerColor", outline.innerColor, 1);
            uniformColor("outerColor", outline.outerColor, 1);
            uniform1f("noiseSize", 1f);
            uniform1f("range", 0.21f);
            uniform1f("threshold", 0.55f);
            uniform1f("noiseSpeed", 1);
            uniform1f("throbSpeed", 0f);
        });

        float scale = 1.025f, factor = 1.025f;
        glPushMatrix();
        ColorMode.glColorInt(outline.outerColor, outline.outerAlpha);
        float size = scale * factor * outline.innerSize;
        glScalef(size, 1.025f * factor, size);

        glPushMatrix();
        glTranslatef(0, -0.020f, 0);
        if (isArm) {
            glTranslatef(0.018f, 0, 0);
            renderDBCArm(player, render);
        }
        else
            render.modelMain.renderBody(0.0625F);
        glPopMatrix();

        if (!isArm)
            renderHair(player, render);
        glPopMatrix();

        if (!isArm) {
            disableStencilWriting(player.getEntityId() + RenderEventHandler.TAIL_STENCIL_ID, false);
            glPushMatrix();
            glScalef(1.02f * 1.01f * outline.outerSize, 1, 1.02f * 1.02f * outline.outerSize);

            DBCData data = DBCData.get(player);
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
                }

                render.modelMain.renderHairs(0.0625F, (ts == 4 ? "n" : "") + "FR" + JRMCoreH.TransFrHrn[state]);
            }

            glPopMatrix();
            disableStencilWriting(player.getEntityId(), false);
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
                if (st == 6 || form != null && form.display.hairType.equals("ssj3"))
                    renderer.modelMain.renderHairs(0.0625F, "" + JRMCoreH.HairsT[6] + JRMCoreH.Hairs[0]);
                else if (st == 14)
                    renderer.modelMain.renderHairsV2(0.0625F, "373852546750347428545480193462285654801934283647478050340147507467501848505072675018255250726750183760656580501822475071675018255050716750189730327158501802475071675018973225673850189765616160501820414547655019545654216550195754542165501920475027655019943669346576193161503065231900475030655019406534276538199465393460501997654138655019976345453950189760494941501897615252415018976354563850189763494736501897614949395018976152523950189763525234501897584749395018976150493850189760545234501897585250415018885445474550189754475041501897545250435018885454523950185143607861501897415874585018514369196150185147768078391865525680565018974356806150188843567861501868396374615018975056805650189750568056501885582374615018975823726150187149568054501877495680565018774950785650189163236961501820", 0.0F, 0, 0, pl, race, renderer, (AbstractClientPlayer) player);
                else
                    renderer.modelMain.renderHairsV2(0.0625F, dnsH, 0.0F, st, rg, pl, race, renderer, (AbstractClientPlayer) player);
            } else if (st == 14 || form != null && form.display.hairType.equals("ssj4"))
                renderer.modelMain.renderHairsV2(0.0625F, "373852546750347428545480193462285654801934283647478050340147507467501848505072675018255250726750183760656580501822475071675018255050716750189730327158501802475071675018973225673850189765616160501820414547655019545654216550195754542165501920475027655019943669346576193161503065231900475030655019406534276538199465393460501997654138655019976345453950189760494941501897615252415018976354563850189763494736501897614949395018976152523950189763525234501897584749395018976150493850189760545234501897585250415018885445474550189754475041501897545250435018885454523950185143607861501897415874585018514369196150185147768078391865525680565018974356806150188843567861501868396374615018975056805650189750568056501885582374615018975823726150187149568054501877495680565018774950785650189163236961501820", 0.0F, 0, 0, pl, race, renderer, (AbstractClientPlayer) player);
            else
                renderer.modelMain.renderHairs(0.0625F, "" + JRMCoreH.HairsT[st] + JRMCoreH.Hairs[hairPreset]);


        } else if (race == DBCRace.MAJIN) {
            if (!renderHair)
                return;

            if (customHair)
                renderer.modelMain.renderHairsV2(0.0625F, dnsH, 0.0F, st, rg, pl, race, renderer, (AbstractClientPlayer) player);
            else if (hairPreset == 10)
                renderer.modelMain.renderHairsV2(0.0625F, "005050555050000050505550500000505055505000005050455050000050505250500000505052505000005050555050000050505450500000505052505000005050525050000150433450500000505055505000005050525050000054395050500000505045505000005050475050000050504750500000505047505000015043655050000050504750500000505047505000005050475050000050504750500000544545505000005250505050000052505050500000525050505000005250505050000050505050500000505050505000005050505050000052505050500000525050505000005250505050000052505050500000525050505000005245505050000054505050500000525050505000005252505050000070505050500000705050505000007050505050000070505050500000705050505000347050505050003470505050500000705050505000007050505050000069505050500000695050505000007050505050000070505050500000705050505000007050505050000070505050500020", 0.0F, 0, 0, pl, race, renderer, (AbstractClientPlayer) player);
            else if (hairPreset == 11)
                renderer.modelMain.renderHairsV2(0.0625F, "345052545050001250545650500023505041505000345056455050000150505250500001505052505000015050555050000150505450500001505052505000015050525050000150433450500001505055505000015050525050000154395050500001505045505000015050475050000150504750500001505047505000015043655050000150504750500001505047505000015050475050000150504750500001544545505000015250505050003450505050500034505050505000015250505050000150505050500001505050505000015050505050000150505050500001525050505000015050505050000150505050500001525050505000235250505050003450505050500034505050505000235250505050000180501850500034695050505000346950505050000180501950500001805019505000345850505050003463505050500001805018505000018050185050003476505050500034765050505000018050195050003480501850500034505050505000345050505050003480501950500020", 0.0F, 0, 0, pl, race, renderer, (AbstractClientPlayer) player);

        }
    }

    public static void renderDBCArm(EntityPlayer player, RenderPlayerJBRA renderer) {
        boolean instantTransmission = ExtendedPlayer.get(player).getBlocking() == 2;
        int[] an = new int[]{1, 0, 2, 0, 0, 3, 0, 1, 1};
        int id = ExtendedPlayer.get(player).getBlocking() != 0 ? (instantTransmission ? 6 : 0) : (ExtendedPlayer.get(player).getAnimKiShoot() != 0 ? an[ExtendedPlayer.get(player).getAnimKiShoot() - 1] + 2 : -1);
        renderer.modelMain.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);

        if (id == -1)
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
                GL11.glTranslatef(-0.2F, 0.0F, -0.1F);
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
                GL11.glTranslatef(-0.6F, 0.08F, 0.3F);
                renderer.modelMain.LA.render(0.0625F);
                GL11.glPopMatrix();
            }

            if (id != 0 && id != 6) {
                if (id != 2 && id != 3) {
                    if (id == 4 || id == 5) {
                        GL11.glPushMatrix();
                        GL11.glTranslatef(-0.2F, 0.4F, -0.1F);
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
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(770, 771);
                    GL11.glAlphaFunc(516, 0.003921569F);
                    GL11.glDepthMask(false);
                    GL11.glTranslatef(-0.5F, -0.1F, -0.1F);
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
