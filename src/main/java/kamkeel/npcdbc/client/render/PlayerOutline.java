package kamkeel.npcdbc.client.render;

import JinRyuu.JBRA.RenderPlayerJBRA;
import kamkeel.npcdbc.client.ClientProxy;
import kamkeel.npcdbc.client.ColorMode;
import kamkeel.npcdbc.client.RenderEventHandler;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.entity.EntityAura;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.Render;
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

    public static void renderOutline(RenderPlayerJBRA render, EntityPlayer p) {
        //Uncomment line below
        EntityAura aura = DBCData.get(p).auraEntity;
       // if (aura == null || aura != null)
         //   return;
        
        DBCData.get(p).outline = new PlayerOutline(0xCfffff, 0x0d2dba);
        if (DBCData.get(p).outline == null)
            return;

        PlayerOutline outline = DBCData.get(p).outline;
        ClientProxy.RenderingOutline = true;

        RenderEventHandler.enableStencilWriting(RenderEventHandler.PLAYER_STENCIL_ID);
        glPushMatrix();
        GL11.glEnable(GL_BLEND);
        GL11.glDisable(GL_LIGHTING);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GL11.glAlphaFunc(GL_GREATER, 0.003921569F);
        Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
        glDisable(GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        float scale = 1.045f;
        ///////////////////////////////////
        ///////////////////////////////////
        //Outer
        float factor = 1.045f;
        glPushMatrix();
        ColorMode.glColorInt(outline.outerColor, outline.outerAlpha);
        float size = scale * factor * outline.outerSize;
        glScalef(size, 1.025f * factor, size);
        glPushMatrix();
        glTranslatef(0, -0.050f, 0);
        render.modelMain.renderBody(0.0625F);
        glPopMatrix();

        renderHair(p, render);
        glPopMatrix();

        ///////////////////////////////////
        ///////////////////////////////////
        //Inner
        glPushMatrix();
        ColorMode.glColorInt(outline.innerColor, outline.innerAlpha); //inner
        glScalef(scale * outline.innerSize, 1.025f, scale * outline.innerSize);

        glPushMatrix();
        glTranslatef(0, -0.015f, 0);
        render.modelMain.renderBody(0.0625F);
        glPopMatrix();

        renderHair(p, render);
        glPopMatrix();


        GL11.glAlphaFunc(GL_GREATER, 0.1F);
        GL11.glDisable(GL_BLEND);
        GL11.glEnable(GL_LIGHTING);
        GL11.glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glPopMatrix();
        Minecraft.getMinecraft().entityRenderer.enableLightmap(0);
        ClientProxy.RenderingOutline = false;
        RenderEventHandler.postStencilRendering();
    }

    public static void renderHair(EntityPlayer p, RenderPlayerJBRA render) {
        DBCData d = DBCData.get(p);
        render.modelMain.renderHairsV2(0.0625f, d.DNSHair, 0f, (int) d.State, d.Rage, d.stats.getJRMCPlayerID(), d.Race, render, (AbstractClientPlayer) p);
    }
}
