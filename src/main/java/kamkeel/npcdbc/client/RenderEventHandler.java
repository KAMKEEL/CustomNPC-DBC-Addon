package kamkeel.npcdbc.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.entity.EntityAura;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import noppes.npcs.client.renderer.ImageData;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

public class RenderEventHandler {
    /**
     * ID used when cutting out the player from stencil buffer
     */
    public final static int PLAYER_STENCIL_ID = 1; 

    @SubscribeEvent
    public void renderPlayer(RenderPlayerEvent.Pre e) {
        glClear(GL_STENCIL_BUFFER_BIT); //TODO: needs to be put somewhere else i.e RenderWorldLastEvent, but for some reason doesn't work when put there
        glEnable(GL11.GL_STENCIL_TEST);
        enableStencilWriting(PLAYER_STENCIL_ID);
    }


    @SubscribeEvent
    public void renderPlayer(RenderPlayerEvent.Post e) {
        EntityPlayer player = e.entityPlayer;
        float partialTicks = e.partialRenderTick;
        if (DBCData.get(player) == null)
            return;

        EntityAura aura = DBCData.get(player).auraEntity;
        if (aura == null || aura != null || !aura.shouldRender())
            return;

        double interPosX = (aura.lastTickPosX + (aura.posX - aura.lastTickPosX) * (double) partialTicks) - RenderManager.renderPosX;
        double interPosY = (aura.lastTickPosY + (aura.posY - aura.lastTickPosY) * (double) partialTicks) - RenderManager.renderPosY;
        double interPosZ = (aura.lastTickPosZ + (aura.posZ - aura.lastTickPosZ) * (double) partialTicks) - RenderManager.renderPosZ;
        ImageData tex = new ImageData(CustomNpcPlusDBC.ID + ":textures/aura/aura.png");

        float scale = 2.00f;

        disableStencilWriting(1, false);
        glPushMatrix();


        GL11.glEnable(GL_BLEND);
        GL11.glDisable(GL_LIGHTING);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GL11.glAlphaFunc(GL_GREATER, 0.003921569F);
        Minecraft.getMinecraft().entityRenderer.disableLightmap(0);

        glScalef(scale, scale, scale);
        GL11.glTranslated(interPosX, interPosY - 0.65f, interPosZ-0.025f);
        glTranslatef(0f, 0, -0.35f);
        glRotatef(180, 0, 0, 1);
        glRotatef(315,1,0,0);

        for (float j = 1; j < 2; j += 1) {
            glPushMatrix();
            GL11.glRotatef(360 * j, 0F, 0F, 1F);
            renderImage(tex, aura.color1, 0.2f);
            glPopMatrix();
        }

        // Reset OpenGL states
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
        postStencilRendering();
        glStencilFunc(GL_ALWAYS, 0, 0xFF);  //LETS YOU DRAW TO THE COLOR BUFFER AGAIN
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
            boolean front = i == 1;
            Tessellator tessellator = Tessellator.instance;
            if (front) {
                GL11.glRotatef(180, 0, 0, 1.0f);
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

    public static void enableStencilWriting(int id) {
        glStencilFunc(GL_ALWAYS, id, 0xFF);  // Always draw to the color buffer & pass the stencil test
        glStencilMask(0xFF);  // Write to stencil buffer
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);  // Keep stencil value
    }

    public static void disableStencilWriting(int id, boolean invert) {
        glStencilFunc(invert ? GL_EQUAL : GL_NOTEQUAL, id, 0xFF);  // Test stencil value
        glStencilMask(0x00);  // Do not write to stencil buffer
    }

    public static void postStencilRendering() {
        glStencilFunc(GL_ALWAYS, 0, 0xFF);
    }
}
