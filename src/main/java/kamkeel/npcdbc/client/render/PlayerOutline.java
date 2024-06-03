package kamkeel.npcdbc.client.render;

import JinRyuu.JBRA.RenderPlayerJBRA;
import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.client.ClientProxy;
import kamkeel.npcdbc.client.ColorMode;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.mixins.early.IEntityMC;
import kamkeel.npcdbc.util.DBCUtils;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

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

    public static void renderOutline(RenderPlayerJBRA render, EntityPlayer player, float partialTicks) {
        PlayerOutline outline = DBCData.get(player).outline;
        ClientProxy.RenderingOutline = true;
        if (player.isInWater())
            ((IEntityMC) player).setRenderPass(0);
        else
            ((IEntityMC) player).setRenderPass(ClientProxy.MiddleRenderPass);

        glPushMatrix();
        GL11.glEnable(GL_BLEND);
        GL11.glDisable(GL_LIGHTING);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GL11.glAlphaFunc(GL_GREATER, 0.003921569F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        glDepthMask(false);
        
        ///////////////////////////////////
        ///////////////////////////////////
        //Outer
        float scale = 1.045f, factor = 1.045f;
        glPushMatrix();
        //   ShaderHelper.useShader(ShaderHelper.pylonGlow);

        ColorMode.glColorInt(outline.outerColor, outline.outerAlpha);
        float size = scale * factor * outline.innerSize;
        glScalef(size, 1.025f * factor, size);
        glPushMatrix();
        glTranslatef(0, -0.030f, 0);
        renderDBCPlayer(player, render);
        glPopMatrix();
        renderHair(player, render);
        glPopMatrix();

        glPushMatrix();
        glScalef(1.025f * 1.025f * outline.outerSize, 1.025f, 1.025f * 1.025f * outline.outerSize);
        renderTail(player, render);
        //    ShaderHelper.releaseShader();

        glPopMatrix();

        //glStencilMask(0x0);
        
        ///////////////////////////////////
        ///////////////////////////////////
        //Inner
        // glStencilMask(0xFF);

        glPushMatrix();
        ColorMode.glColorInt(outline.innerColor, outline.innerAlpha); //inner
        glScalef(scale * outline.innerSize, 1.025f, scale * outline.innerSize);

        glPushMatrix();
        glTranslatef(0, -0.015f, 0);
        renderDBCPlayer(player, render);
        glPopMatrix();
        renderHair(player, render);
        glPopMatrix();

        glPushMatrix();
        glScalef(1.025f, 1.025f, 1.025f);
        renderTail(player, render);

        glPopMatrix();
        ///////////////////////////////////
        ///////////////////////////////////
        // RenderEventHandler.postStencilRendering();

        GL11.glAlphaFunc(GL_GREATER, 0.1F);
        GL11.glDisable(GL_BLEND);
        GL11.glEnable(GL_LIGHTING);
        GL11.glEnable(GL_TEXTURE_2D);
        glDepthMask(true);
        glPopMatrix();
        ClientProxy.RenderingOutline = false;

    }

    public static void renderDBCPlayer(EntityPlayer player, RenderPlayerJBRA renderer) {
        DBCData data = DBCData.get(player);
        int race = data.Race;

        renderer.modelMain.renderBody(0.0625F);
        if (race == DBCRace.NAMEKIAN)
            renderer.modelMain.renderHairs(0.0625F, "N");
        
    }

    public static void renderTail(EntityPlayer player, RenderPlayerJBRA renderer) {
        DBCData data = DBCData.get(player);
        int race = data.Race;

        if (DBCRace.isSaiyan(race)) {
            byte ts = Byte.parseByte(JRMCoreH.dat19[data.stats.getJRMCPlayerID()].split(";")[0]);
            renderer.modelMain.renderHairs(0.0625F, ts != 0 && ts != -1 ? (ts == 1 ? "SJT2" : "") : "SJT1");
        } else if (race == DBCRace.ARCOSIAN) {
            byte ts = Byte.parseByte(JRMCoreH.dat19[data.stats.getJRMCPlayerID()].split(";")[0]);
            renderer.modelMain.renderHairs(0.0625F, (ts == 4 ? "n" : "") + "FR" + JRMCoreH.TransFrHrn[data.State]);
        }
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

        if (race == DBCRace.HUMAN || race == DBCRace.SAIYAN || race == DBCRace.HALFSAIYAN) {
            if (!renderHair) {
                if (hairPreset == 10) //bald LMAO
                    renderer.modelMain.renderHeadwear(0.0625F);
                return;
            }

            if (customHair) {
                if (st == 6)
                    renderer.modelMain.renderHairs(0.0625F, "" + JRMCoreH.HairsT[6] + JRMCoreH.Hairs[0]);
                else if (st == 14)
                    renderer.modelMain.renderHairsV2(0.0625F, "373852546750347428545480193462285654801934283647478050340147507467501848505072675018255250726750183760656580501822475071675018255050716750189730327158501802475071675018973225673850189765616160501820414547655019545654216550195754542165501920475027655019943669346576193161503065231900475030655019406534276538199465393460501997654138655019976345453950189760494941501897615252415018976354563850189763494736501897614949395018976152523950189763525234501897584749395018976150493850189760545234501897585250415018885445474550189754475041501897545250435018885454523950185143607861501897415874585018514369196150185147768078391865525680565018974356806150188843567861501868396374615018975056805650189750568056501885582374615018975823726150187149568054501877495680565018774950785650189163236961501820", 0.0F, 0, 0, pl, race, renderer, (AbstractClientPlayer) player);
                else
                    renderer.modelMain.renderHairsV2(0.0625F, dnsH, 0.0F, st, rg, pl, race, renderer, (AbstractClientPlayer) player);
            } else if (st == 14)
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
}
