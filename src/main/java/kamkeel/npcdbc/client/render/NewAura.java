package kamkeel.npcdbc.client.render;

import com.sun.org.apache.xpath.internal.operations.Bool;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.client.ClientProxy;
import kamkeel.npcdbc.client.shader.PostProcessing;
import kamkeel.npcdbc.client.shader.ShaderHelper;
import kamkeel.npcdbc.client.shader.ShaderResources;
import kamkeel.npcdbc.entity.EntityAura;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChatComponentText;
import noppes.npcs.client.renderer.ImageData;
import noppes.npcs.scripted.NpcAPI;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.opengl.GL11.*;

public class NewAura {
    public static final String AURA_DIR = CustomNpcPlusDBC.ID + ":textures/aura/";
    public static final int FRAMES = 6;
    public static int currentFrame;

    public static ImageData verticalAura = new ImageData(AURA_DIR + "new_aura_2.png");
    public static ImageData horizontalAura = new ImageData(AURA_DIR + "enhanced_aura_crosssection.png");

    public static void renderAura(EntityAura aura, float partialTicks) {
        float speed = ClientProxy.getTimeSinceStart() * 15;
        currentFrame = (int) (speed % FRAMES);
        ImageData image = verticalAura;
        if (!image.imageLoaded())
            return;

        if(((boolean) NpcAPI.Instance().getEngineObjects().getOrDefault("isLoaded", false))){
            NpcAPI.Instance().getEngineObjects().put("isLoaded", false);
            PostProcessing.saveTextureToPNG(PostProcessing.auraTextures[1]);
            PostProcessing.saveTextureToPNG(PostProcessing.auraTextures[0]);
        }


        image.bindTexture();
        int width = image.getTotalWidth(), height = image.getTotalHeight();
        int frameWidth = width / FRAMES, frameStartU = currentFrame * frameWidth;

        float load = Math.min(aura.ticksExisted / 15f, 1);
        float loadSize = Math.min(aura.ticksExisted / 7f, 1);





        glDisable(GL_LIGHTING);
        glEnable(GL_BLEND);
        glDisable(GL_CULL_FACE);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER,0);
        glDepthMask(false);
        glStencilMask(0x00);
        float scale = 10*loadSize, yScale = scale + 2;
        glPushMatrix();
        glTranslatef(0, -1.75f, 0);
        glTranslatef(0, 0.20f * yScale, 0);
        glScalef(scale, yScale, scale);


        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, PostProcessing.auraBuffer);
        glClearColor(0, 0, 0, 0);
        PostProcessing.drawToBuffers(0,1,2);
        glClear(GL_COLOR_BUFFER_BIT);



        glPushMatrix();
        PostProcessing.drawToBuffers(0);
//        glDisable(GL_STENCIL_TEST);
//        glDisable(GL_DEPTH_TEST);

        glTranslatef(0, -0.045f, 0);
        glScalef(0.95f, 0.95f, 0.95f);
        glColor4f((float) 0x67 /0xFF, (float) 0x3a /0xFF, (float) 0xb7 /0xFF, 1f);
        drawAuraModel(5, frameStartU, frameWidth, height);

//        if(!!(boolean) NpcAPI.Instance().getEngineObjects().getOrDefault("isLoaded", false)){
//            NpcAPI.Instance().getEngineObjects().put("isLoaded", true);
//        }
//        PostProcessing.saveTextureToPNG(PostProcessing.auraTextures[0]);
//        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(""+NpcAPI.Instance().getEngineObjects().getOrDefault("isLoaded", false)));
//
//        PostProcessing.drawToBuffers(1);
//
//        glScalef(0.8f, 0.95f, 0.8f);
//        glTranslatef(0, -0.025f, 0);
//        glColor4f(0, 0, 1, 1f);
//        drawAuraModel(5, frameStartU, frameWidth, height);
        glPopMatrix();

        PostProcessing.getMainBuffer().bindFramebuffer(false);
        for(int i = 0; i < PostProcessing.auraTextures.length; i++){
            OpenGlHelper.setActiveTexture(GL13.GL_TEXTURE0 + i+1);
            glBindTexture(GL_TEXTURE_2D, PostProcessing.auraTextures[i]);
        }

        glEnable(GL_STENCIL_TEST);
//        glEnable(GL_DEPTH_TEST);

        ShaderHelper.useShader(ShaderHelper.aura, () -> {
            Framebuffer main = PostProcessing.getMainBuffer();
            ShaderHelper.uniformColor("color1", 0x120C15, 1f);
            ShaderHelper.uniformColor("color2", 0x120C15, 0.9f);
            ShaderHelper.uniformColor("color3", 0x360248, 0.6f);
            ShaderHelper.uniformColor("color4", 0x3F0458, 0.6f);
            ShaderHelper.uniformVec2("resolution", main.framebufferWidth, main.framebufferHeight);

            ShaderHelper.uniform1f("speed", speed);

        });

        glPushMatrix();
        glColor4f(1, 1, 1, 1f*load);
        for(int i = 0; i < 5; i++) {
            glRotatef((float) 180 / 5, 0, 1, 0);
            renderQuad(image, frameStartU, 0, frameStartU + frameWidth, height);
        }
        glPopMatrix();

//        PostProcessing.saveTextureToPNG(PostProcessing.auraTextures[0]);

        for(int i = 0; i < PostProcessing.auraTextures.length; i++){
            OpenGlHelper.setActiveTexture(GL13.GL_TEXTURE0 + i+1);
            glBindTexture(GL_TEXTURE_2D, 0);
        }
        OpenGlHelper.setActiveTexture(GL13.GL_TEXTURE0);

        ShaderHelper.releaseShader();

        glPopMatrix();

        glEnable(GL_LIGHTING);
        glDisable(GL_BLEND);
        glEnable(GL_CULL_FACE);
    }

    private static void drawAuraModel(int amountOfFaces, int start, int width, int height){
        for(int i = 0; i < amountOfFaces; i++) {
            glRotatef((float) 180 / amountOfFaces, 0, 1, 0);
            renderQuad(verticalAura, start, 0, start + width, height);
        }
    }


    public static void renderQuad(ImageData imageData, int startU, int startV, int endU, int endV) {
        if (!imageData.imageLoaded())
            return;

        Tessellator tessellator = Tessellator.instance;
        int totalWidth = imageData.getTotalWidth();
        int totalHeight = imageData.getTotalHeight();
        float textureXScale = 1.0F, textureYScale = 1.0F;
        float u1 = startU * (1f / totalWidth);//0.0F;
        float v1 = startV * (1f / totalHeight);

        float u2 = endU * (1f / totalWidth);
        float v2 = endV * (1f / totalHeight);

        glPushMatrix();
        GL11.glRotated(180-( Minecraft.getMinecraft().thePlayer.rotationYaw), 0.0, 1.0, 0.0);

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
