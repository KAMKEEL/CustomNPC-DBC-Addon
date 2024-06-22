package kamkeel.npcdbc.client.render;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.client.ClientProxy;
import kamkeel.npcdbc.client.shader.ShaderHelper;
import kamkeel.npcdbc.client.shader.ShaderResources;
import kamkeel.npcdbc.entity.EntityAura;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.client.renderer.ImageData;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

public class NewAura {
    public static final String AURA_DIR = CustomNpcPlusDBC.ID + ":textures/aura/";
    public static final int FRAMES = 4;
    public static int currentFrame;

    public static void renderAura(EntityAura aura, float partialTicks) {
        float speed = ClientProxy.getTimeSinceStart() * 10;
        currentFrame = (int) (speed % FRAMES);
        ImageData image = new ImageData(AURA_DIR + "enhanced_aura.png");
        if (!image.imageLoaded())
            return;

        image.bindTexture();
        int width = image.getTotalWidth(), height = image.getTotalHeight();
        int frameWidth = width / 4, frameStartU = currentFrame * frameWidth;

        Entity ren = Minecraft.getMinecraft().renderViewEntity;
        float yaw = ren.rotationYaw, pitch = ren.rotationPitch;
        int thirdPerson = Minecraft.getMinecraft().gameSettings.thirdPersonView;

        ShaderHelper.useShader(ShaderHelper.aura, () -> {
            ShaderHelper.uniformColor("color1", 0xffff00, 0.5f);
            ShaderHelper.uniformColor("color2", 0x555500, 0.45f);
            ShaderHelper.uniformColor("color3", 0xAAAA00, 0.25f);
            ShaderHelper.uniformColor("color4", 0xAAAA00, 0.155f);

            ShaderHelper.uniformTexture("noiseTexture", 2, ShaderResources.PERLIN_NOISE);
            ShaderHelper.uniformTexture("cross", 3, AURA_DIR + "enhanced_aura_crosssection.png");
            ShaderHelper.uniform1f("speed", speed);
            ShaderHelper.uniform1f("pitch", pitch);

        });
        glDisable(GL_LIGHTING);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);
        glDepthFunc(GL_NOTEQUAL);
        glDepthMask(false);

        float scale = 7.4f;
        glPushMatrix();
        glTranslatef(0, 0.2f, 0);
        glScalef(scale + 1, scale + 3, scale + 1);

        GL11.glRotatef(180.0F - yaw, 0, 1, 0);
        GL11.glRotatef(360f - pitch, 1, 0, 0);
        if (pitch > 60 && thirdPerson == 1 || pitch < -60 && thirdPerson == 2) {
            scale = (60 / Math.abs(pitch) * 1) * 1f;
            glScalef(scale, scale, scale);
            glTranslatef(0, 0.015f * scale, 0);
        }

        renderQuad(image, frameStartU, 0, frameStartU + frameWidth, height);
        glPopMatrix();

        ShaderHelper.releaseShader();
        glEnable(GL_LIGHTING);
        glDisable(GL_BLEND);
        glDepthFunc(GL_LEQUAL);
    }


    public static void renderQuad(ImageData imageData, int startU, int startV, int endU, int endV) {
        if (!imageData.imageLoaded())
            return;

        glPushMatrix();
        Tessellator tessellator = Tessellator.instance;
        int totalWidth = imageData.getTotalWidth();
        int totalHeight = imageData.getTotalHeight();
        float u1 = startU * (1f / totalWidth);//0.0F;
        float v1 = startV * (1f / totalHeight);

        float u2 = endU * (1f / totalWidth);
        float v2 = endV * (1f / totalHeight);

        float textureXScale = 1.0F, textureYScale = 1.0F;
        if (totalWidth > totalHeight) {
            textureYScale = (float) totalHeight / totalWidth;
            glScalef(1 / textureYScale / 2, 1 / textureYScale / 2, 1 / textureYScale / 2);
        } else if (totalHeight > totalWidth) {
            textureXScale = (float) totalWidth / totalHeight;
            glScalef(1 / textureXScale / 2, 1 / textureXScale / 2, 1 / textureXScale / 2);
        }

        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(textureXScale * -(u2 - u1) / 2, textureYScale * (v2 - v1) / 2, 0, u1, v1); //-1, 0.5   0 1
        tessellator.addVertexWithUV(textureXScale * (u2 - u1) / 2, textureYScale * (v2 - v1) / 2, 0, u2, v1); //1, 0.5    1 , 1
        tessellator.addVertexWithUV(textureXScale * (u2 - u1) / 2, textureYScale * -(v2 - v1) / 2, 0, u2, v2); //1, -0.5
        tessellator.addVertexWithUV(textureXScale * -(u2 - u1) / 2, textureYScale * -(v2 - v1) / 2, 0, u1, v2); //-1, -0.5
        tessellator.draw();

        glPopMatrix();
    }
}
