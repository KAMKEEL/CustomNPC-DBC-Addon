package kamkeel.npcdbc.client;

import JinRyuu.DragonBC.common.Npcs.EntityAura2;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.entity.EntityAura;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import noppes.npcs.client.renderer.ImageData;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

public class RenderEventHandler {

    @SubscribeEvent
    public void renderPlayer(RenderPlayerEvent.Pre e) {
        EntityPlayer player = e.entityPlayer;
        if (DBCData.get(player) == null)
            return;

        setStencilStart(1, true);

        EntityAura aura = DBCData.get(player).auraEntity;
        if (aura == null || !aura.shouldRender())
            return;

        ImageData tex = new ImageData("jinryuudragonbc:aura.png");

        float scale = 5f;
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
        Minecraft.getMinecraft().entityRenderer.disableLightmap(0);

        // Move aura rendering here
        glDepthMask(false);
        glScalef(scale, scale, scale);
        for (int i = 0; i < 2; i++) {
            glPushMatrix();
            if (i == 1) {
                glRotatef(180, 0, 0, 1);
                glRotatef(180, 0, 1, 0);
            }
            renderImage(tex, aura.color1, 1f);
            glPopMatrix();
        }

//        EntityAura2 aur = new EntityAura2(player.worldObj, player.getCommandSenderName(), 0xffffff, 0, 0, 100, false);
//        aur.setAlp(0.2F);
//        player.worldObj.spawnEntityInWorld(aur);

        // Reset depth mask after rendering aura
        glDepthMask(true);

        // Reset OpenGL states
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopAttrib();
        GL11.glPopMatrix();

        setStencilEnd(1, true);
    }

    @SubscribeEvent
    public void renderLast(RenderWorldLastEvent renderWorldLastEvent){
        GL11.glClear(GL_STENCIL_BUFFER_BIT);
    }

    @SubscribeEvent
    public void renderPlayer(RenderPlayerEvent.Post e) {
    }

    public static void renderImage(ImageData imageData, int color, float alpha) {
        if (!imageData.imageLoaded())
            return;

        glPushMatrix();
        float red = (color >> 16 & 255) / 255f;
        float green = (color >> 8 & 255) / 255f;
        float blue = (color & 255) / 255f;
        GL11.glColor4f(red, green, blue, alpha);

        for (int i = 0; i < 2; i++) {
            boolean front = i == 0;
            Tessellator tessellator = Tessellator.instance;
            if (front) {
                GL11.glRotated(180, 0.0, 0.0, 1.0);
            }

            imageData.bindTexture();

            int totalWidth = imageData.getTotalWidth();
            int totalHeight = imageData.getTotalHeight();
            float u1 = 0.0F;
            float u2 = 1.0F;
            float v1 = 0.0F;
            float v2 = 1.0F;

            float textureXScale = 1.0F, textureYScale = 1.0F;
            if (totalWidth > totalHeight) {
                textureYScale = (float) totalHeight / totalWidth;
                GL11.glScalef(1 / textureYScale / 2, 1 / textureYScale / 2, 1 / textureYScale / 2);
            } else if (totalHeight > totalWidth) {
                textureXScale = (float) totalWidth / totalHeight;
                GL11.glScalef(1 / textureXScale / 2, 1 / textureXScale / 2, 1 / textureXScale / 2);
            }

            tessellator.startDrawingQuads();
            tessellator.setBrightness(240);
            tessellator.setColorOpaque_F(1, 1, 1);
            tessellator.setColorRGBA_F(red, green, blue, alpha);
            tessellator.addVertexWithUV(textureXScale * (u2 - u1) / 2, 0, textureYScale * (v2 - v1) / 2, u2, v2);
            tessellator.addVertexWithUV(textureXScale * (u2 - u1) / 2, 0, textureYScale * -(v2 - v1) / 2, u2, v1);
            tessellator.addVertexWithUV(textureXScale * -(u2 - u1) / 2, 0, textureYScale * -(v2 - v1) / 2, u1, v1);
            tessellator.addVertexWithUV(textureXScale * -(u2 - u1) / 2, 0, textureYScale * (v2 - v1) / 2, u1, v2);
            tessellator.draw();
        }
        GL11.glPopMatrix();
    }

    public static void setStencilStart(int id, boolean doEnable) {

        if(doEnable) {

            GL11.glEnable(GL11.GL_STENCIL_TEST);
        }

        GL11.glStencilFunc(GL11.GL_ALWAYS, id, 0xFF);
        GL11.glStencilMask(0xFF);
    }

    public static void setStencilEnd(int id, boolean doEnable) {

        GL11.glStencilFunc(GL11.GL_ALWAYS, id, 0xFF);
        GL11.glStencilMask(0xFF);

        if(doEnable) {

            GL11.glDisable(GL11.GL_STENCIL_TEST);
        }
    }

    public static void checkStencilStart(int id, boolean invert, boolean doEnable) {

        if(doEnable) {

            GL11.glEnable(GL11.GL_STENCIL_TEST);
        }

        GL11.glStencilFunc(invert ? GL11.GL_EQUAL : GL11.GL_NOTEQUAL, id, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
        GL11.glStencilMask(0x00);
    }

    public static void checkStencilEnd(boolean doEnable) {

        GL11.glStencilMask(0xFF);

        if(doEnable) {

            GL11.glDisable(GL11.GL_STENCIL_TEST);
        }
    }
}
