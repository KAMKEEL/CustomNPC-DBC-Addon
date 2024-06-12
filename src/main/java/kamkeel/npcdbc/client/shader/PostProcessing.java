package kamkeel.npcdbc.client.shader;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import kamkeel.npcdbc.client.ClientProxy;
import kamkeel.npcdbc.client.render.RenderEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static kamkeel.npcdbc.client.shader.ShaderHelper.*;
import static org.lwjgl.opengl.GL11.*;

public class PostProcessing {
    public static int MAIN_BLOOM_TEXTURE, MAIN_BLUR_TEXTURE;
    public static int BLOOM_BUFFERS_LENGTH = 10;
    public static int[] bloomBuffers = new int[BLOOM_BUFFERS_LENGTH];
    public static int[] bloomTextures = new int[bloomBuffers.length];

    public static void init(int width, int height) {
        int previousBuffer = glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);

        for (int i = 0; i < bloomBuffers.length; i++) {

            int mipWidth = width >> (i + 1);
            int mipHeight = height >> (i + 1);
            if (mipWidth <= 0 || mipHeight <= 0)
                break;

            bloomBuffers[i] = OpenGlHelper.func_153165_e();
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, bloomBuffers[i]);

            bloomTextures[i] = TextureUtil.glGenTextures();
            glBindTexture(GL_TEXTURE_2D, bloomTextures[i]);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL30.GL_RGBA16F, mipWidth, mipHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);

            OpenGlHelper.func_153188_a(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, bloomTextures[i], 0);


            int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
            if (status != GL30.GL_FRAMEBUFFER_COMPLETE)
                throw new RuntimeException("Framebuffer " + i + " is not complete: " + status);

            glClearColor(0, 0, 0, 0);
            glClear(GL_COLOR_BUFFER_BIT);
        }
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, previousBuffer);

    }

    public static void deleteBuffers() {
        for (int i = 0; i < bloomBuffers.length; i++) {
            if (bloomTextures[i] > -1)
                TextureUtil.deleteTexture(bloomTextures[i]);
            if (bloomBuffers[i] > -1)
                OpenGlHelper.func_153174_h(bloomBuffers[i]);
        }
        bloomBuffers = new int[BLOOM_BUFFERS_LENGTH];
        bloomTextures = new int[bloomBuffers.length];
    }

    @SubscribeEvent
    public void preProcess(PostProcessing.Event.Pre e) {
        RenderEventHandler.tempPre(e);
    }

    @SubscribeEvent
    public void postProcess(PostProcessing.Event.Post e) {
        // PostProcessing.renderQuad(ClientProxy.rendering, 0, 0, buff.framebufferWidth, buff.framebufferHeight);
        // PostProcessing.renderQuad(ClientProxy.rendering,  buff.framebufferWidth * 0.55f, 0, buff.framebufferWidth, buff.framebufferHeight * 0.45f);
        RenderEventHandler.tempPost(e);
    }

    public static void blurFilter(int textureID, float blurIntensity, float startX, float startY, float width, float height) {

        useShader(blur, () -> {
            uniformVec2("u_resolution", width - startX, height - startY);
            uniform1i("horizontal", 0);
            uniform1f("blurIntensity", blurIntensity);
        });
        renderQuad(textureID, startX, startY, width, height); //vertical blur

        loadUniforms(() -> {
            uniform1i("horizontal", 1);
        });
        renderQuad(textureID, startX, startY, width, height); //horizontal blur

        releaseShader();
    }

    public static void renderQuad(int textureID, float startX, float startY, float width, float height) {
        Tessellator tessellator = Tessellator.instance;
        if (textureID != -1)
            glBindTexture(GL_TEXTURE_2D, textureID);

        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(startX, startY, 0, 0, 1);
        tessellator.addVertexWithUV(startX, height, 0, 0, 0);
        tessellator.addVertexWithUV(width, height, 0, 1, 0);
        tessellator.addVertexWithUV(width, startY, 0, 1, 1);
        tessellator.draw();

        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public static void createMainBloomTexture() {
        Framebuffer fb = getMainBuffer();
        int previousBuffer = glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);

        fb.bindFramebuffer(false);
        MAIN_BLOOM_TEXTURE = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, MAIN_BLOOM_TEXTURE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL30.GL_RGBA16F, fb.framebufferTextureWidth, fb.framebufferTextureHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        OpenGlHelper.func_153188_a(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT2, GL_TEXTURE_2D, MAIN_BLOOM_TEXTURE, 0);

        MAIN_BLUR_TEXTURE = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, MAIN_BLUR_TEXTURE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL30.GL_RGBA16F, fb.framebufferTextureWidth, fb.framebufferTextureHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        OpenGlHelper.func_153188_a(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT3, GL_TEXTURE_2D, MAIN_BLUR_TEXTURE, 0);

        int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
        if (status != GL30.GL_FRAMEBUFFER_COMPLETE)
            throw new RuntimeException("Framebuffer is not complete: " + status);


        ClientProxy.rendering = ClientProxy.defaultRendering = fb.framebufferTexture;
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, previousBuffer);

    }
    public static void drawToBuffer(int... colorBuffers) {
        IntBuffer buffer = BufferUtils.createIntBuffer(colorBuffers.length);
        for (int colorBuffer : colorBuffers) {
            if (colorBuffer > 15)
                continue;
            buffer.put(GL30.GL_COLOR_ATTACHMENT0 + colorBuffer);
        }
        buffer.flip();

        GL20.glDrawBuffers(buffer);
    }

    public static void resetDrawBuffer() {
        GL20.glDrawBuffers(GL30.GL_COLOR_ATTACHMENT0);
    }

    public static Framebuffer getMainBuffer() {
        return Minecraft.getMinecraft().getFramebuffer();
    }

    public static void bindMainBuffer() {
        getMainBuffer().bindFramebuffer(false);
    }

    public static class Event extends cpw.mods.fml.common.eventhandler.Event {
        public Framebuffer frameBuffer;
        public int textureID;

        public Event(Framebuffer frameBuffer, int textureID) {
            this.frameBuffer = frameBuffer;
            this.textureID = textureID;
        }


        @Cancelable
        public static class Pre extends Event {
            public Pre(Framebuffer frameBuffer, int textureID) {
                super(frameBuffer, textureID);
            }
        }

        public static class Post extends Event {
            public Post(Framebuffer frameBuffer, int textureID) {
                super(frameBuffer, textureID);
            }
        }
    }
}
