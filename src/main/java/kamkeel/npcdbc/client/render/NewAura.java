package kamkeel.npcdbc.client.render;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.client.ClientProxy;
import kamkeel.npcdbc.client.shader.ShaderHelper;
import kamkeel.npcdbc.client.shader.ShaderResources;
import kamkeel.npcdbc.entity.EntityAura;
import net.minecraft.client.renderer.Tessellator;
import noppes.npcs.client.renderer.ImageData;

import static org.lwjgl.opengl.GL11.*;

public class NewAura {
    public static final String AURA_DIR = CustomNpcPlusDBC.ID + ":textures/aura/";
    public static final int FRAMES = 4;
    public static int currentFrame;

    public static void renderAura(EntityAura aura, float partialTicks) {
        float speed = ClientProxy.getTimeSinceStart() * 15;
        currentFrame = (int) (speed % FRAMES);
        ImageData image = new ImageData(AURA_DIR + "enhanced_aura.png");
        if (!image.imageLoaded())
            return;

        image.bindTexture();
        int width = image.getTotalWidth(), height = image.getTotalHeight();
        int frameWidth = width / 4, frameStartU = currentFrame * frameWidth;

        glDisable(GL_LIGHTING);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBlendFunc(GL_ONE, GL_ONE);
        glPushMatrix();
        float scale = 7.4f;
        glTranslatef(0, 0.4f, 0);
        glScalef(scale + 1, scale + 3, scale + 1);


        ShaderHelper.useShader(ShaderHelper.aura, () -> {
            ShaderHelper.uniformColor("color1", 0xffff00, 1f);
            ShaderHelper.uniformColor("color2", 0xffff00, 0.75f);
            ShaderHelper.uniformColor("color3", 0x00ffff, 0.5f);
            ShaderHelper.uniformColor("color4", 0xff00ff, 0.15f);

            ShaderHelper.uniformTexture("noiseTexture", 2, ShaderResources.PERLIN_NOISE);
            ShaderHelper.uniform1f("speed", speed);
        });
        renderQuad(image, frameStartU, 0, frameStartU + frameWidth, height);
        ShaderHelper.releaseShader();

        glPopMatrix();
        glEnable(GL_LIGHTING);
        glDisable(GL_BLEND);

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
