package kamkeel.npcdbc.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.entity.EntityAura;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import noppes.npcs.client.renderer.ImageData;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

public class RenderEventHandler {

    @SubscribeEvent
    public void renderPlayer(RenderPlayerEvent.Pre e) {
        EntityPlayer player = e.entityPlayer;
        if (DBCData.get(player) == null)
            return;

        EntityAura aura = DBCData.get(player).auraEntity;
        if (aura == null || !aura.shouldRender())
            return;

        ImageData tex = new ImageData("jinryuudragonbc:aura.png");

        float scale = 5f;
        glPushMatrix();
        GL11.glEnable(GL_BLEND);
        GL11.glDisable(GL_LIGHTING);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GL11.glAlphaFunc(GL_GREATER, 0.003921569F);
        Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
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

        glDepthMask(true);
        GL11.glAlphaFunc(GL_GREATER, 0.1F);
        GL11.glDisable(GL_BLEND);
        GL11.glEnable(GL_LIGHTING);
        glPopMatrix();


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
}
