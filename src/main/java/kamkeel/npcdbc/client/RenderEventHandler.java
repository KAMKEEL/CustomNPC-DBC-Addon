package kamkeel.npcdbc.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.entity.EntityAura;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import noppes.npcs.client.renderer.ImageData;

import static org.lwjgl.opengl.GL11.*;

public class RenderEventHandler {
    @SubscribeEvent
    public void renderPlayerPre(RenderPlayerEvent.Pre event){
        glEnable(GL_STENCIL_TEST);
        glClear(GL_STENCIL_BUFFER_BIT);

        glStencilFunc(GL_ALWAYS, 1, 0xFF);
        glStencilMask(0xFF);
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);

    }

    @SubscribeEvent
    public void renderPlayerPost(RenderPlayerEvent.Post event){

        glStencilFunc(GL_NOTEQUAL, 1, 0xFF);
        glStencilMask(0x00);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE);
        glEnable(GL_ALPHA_TEST);

        renderAura(event);

        glStencilMask(0xFF);
        glDisable(GL_BLEND);
        glStencilFunc(GL_ALWAYS, 0, 0xFF);
    }

    private void renderAura(RenderPlayerEvent.Post e){
        ImageData tex = new ImageData("jinryuudragonbc" + ":aura.png");

        EntityPlayer player = e.entityPlayer;
        float partialTicks = e.partialRenderTick;
        if (DBCData.get(player) == null)
            return;

        EntityAura aura = DBCData.get(player).auraEntity;
        if(aura == null)
            return;

        double posX = (aura.lastTickPosX + (aura.posX - aura.lastTickPosX) * (double) partialTicks) - RenderManager.renderPosX;
        double posY = (aura.lastTickPosY + (aura.posY - aura.lastTickPosY) * (double) partialTicks) - RenderManager.renderPosY;
        double posZ = (aura.lastTickPosZ + (aura.posZ - aura.lastTickPosZ) * (double) partialTicks) - RenderManager.renderPosZ;

        int scale = 9;
        glPushMatrix();

        glTranslated(posX, posY - 0.65f, posZ - 0.025f);
        glScalef(scale, scale, scale);
        glRotatef(90, 1, 0, 0);
        renderImage(tex, 0x880000, 0.5f);

        glPopMatrix();
    }

    public void renderImage(ImageData imageData, int color, float alpha) {
        if (!imageData.imageLoaded())
            return;

        glPushMatrix();
        float red = (color >> 16 & 255) / 255f;
        float green = (color >> 8 & 255) / 255f;
        float blue = (color & 255) / 255f;
        glColor4f(red, green, blue, alpha);

        for (int i = 0; i < 2; i++) {
            boolean front = i == 1;
            Tessellator tessellator = Tessellator.instance;
            if (front) {
                glRotatef(180, 0, 0, 1.0f);
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
                glScalef(1 / textureYScale / 2, 1 / textureYScale / 2, 1 / textureYScale / 2);
            } else if (totalHeight > totalWidth) {
                textureXScale = (float) totalWidth / totalHeight;
                glScalef(1 / textureXScale / 2, 1 / textureXScale / 2, 1 / textureXScale / 2);
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
        glPopMatrix();
    }
}
