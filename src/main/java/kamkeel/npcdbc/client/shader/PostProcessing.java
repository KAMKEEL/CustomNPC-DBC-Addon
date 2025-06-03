package kamkeel.npcdbc.client.shader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.CommonProxy;
import kamkeel.npcdbc.client.ClientConstants;
import kamkeel.npcdbc.client.gui.hud.formWheel.HUDFormWheel;
import kamkeel.npcdbc.config.ConfigDBCClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import static kamkeel.npcdbc.client.shader.ShaderHelper.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

@SideOnly(Side.CLIENT)
public class PostProcessing {

    public static Framebuffer MAIN;
    public static int MAIN_BLOOM_BUFFER, MAIN_BLOOM_TEXTURE, DEPTH_TEXTURE;
    public static int MISC_POST_PROCESSING_BUFFER, BLUR_TEXTURE;
    public static int blankTexture;

    public static int BLOOM_BUFFERS_LENGTH = 10;
    public static int[] bloomBuffers = new int[BLOOM_BUFFERS_LENGTH];
    public static int[] bloomTextures = new int[bloomBuffers.length];
    public static int[] bloomTextures2 = new int[bloomBuffers.length];

    public static int auraBuffer;
    public static int[] auraTextures = new int[3];

    public static boolean processBloom;
    public static boolean bloomSupported = true; // ← new flag

    public static FloatBuffer DEFAULT_MODELVIEW = BufferUtils.createFloatBuffer(16);
    public static FloatBuffer DEFAULT_PROJECTION = BufferUtils.createFloatBuffer(16);
    public static Minecraft mc = Minecraft.getMinecraft();
    public static int PREVIOUS_BUFFER;
    public static int VIEWPORT_WIDTH, VIEWPORT_HEIGHT;

    public static boolean hasInitialized;

    private static boolean isScissorEnabled;

    public static void startBlooming(boolean clearBloomBuffer) {
        if (!bloomSupported || !ConfigDBCClient.EnableBloom || !ShaderHelper.shadersEnabled())
            return;

        PREVIOUS_BUFFER = glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        glBindFramebuffer(GL_FRAMEBUFFER, MAIN_BLOOM_BUFFER);
        if (clearBloomBuffer) {
            drawToBuffers(2);
            glClearColor(0, 0, 0, 1);
            glClear(GL_COLOR_BUFFER_BIT);
        }

        drawToBuffers(0, 2);
        processBloom = true;
    }

    public static void endBlooming() {
        if (processBloom && bloomSupported) {
            glBindFramebuffer(GL_FRAMEBUFFER, PREVIOUS_BUFFER);
        }
    }

    public static void postProcess() {
        // when bloomSupported is false, skip bloom entirely
        if (bloomSupported) {
            bloom(1.5f, false);
        }

        if (bloomSupported && ShaderHelper.shadersEnabled() &&
            mc.currentScreen instanceof HUDFormWheel && HUDFormWheel.BLUR_ENABLED) {
            Framebuffer buff = getMainBuffer();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0D, mc.displayWidth, mc.displayHeight, 0.0D, 0, 1);

            glBindFramebuffer(GL_FRAMEBUFFER, MISC_POST_PROCESSING_BUFFER);
            drawToBuffers(0);
            disableGLState();

            blurVertical(buff.framebufferTexture, HUDFormWheel.BLUR_INTENSITY, 0, 0, mc.displayWidth, mc.displayHeight);

            buff.bindFramebuffer(false);
            disableGLState();
            blurHorizontal(BLUR_TEXTURE, HUDFormWheel.BLUR_INTENSITY, 0, 0, mc.displayWidth, mc.displayHeight);
            releaseShader();

            glEnable(GL_DEPTH_TEST);
            glDepthMask(true);
            glEnable(GL_TEXTURE_2D);
            glEnable(GL_COLOR_MATERIAL);
        }

        if (isScissorEnabled)
            GL11.glEnable(GL_SCISSOR_TEST);
    }

    public static void bloom(float lightExposure, boolean resetGLState) {
        if (!bloomSupported)
            return;

        isScissorEnabled = GL11.glIsEnabled(GL_SCISSOR_TEST);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        if (!processBloom)
            return;

        updateViewportDimensions();
        int width = VIEWPORT_WIDTH, height = VIEWPORT_HEIGHT;
        FloatBuffer prevModelView = getModelView();
        FloatBuffer prevProjection = getProjection();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, width, height, 0.0D, 0, 1);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glColorMask(true, true, true, false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        mc.entityRenderer.disableLightmap(0);

        // Down Sampling in a mip chain
        glBindFramebuffer(GL_FRAMEBUFFER, bloomBuffers[0]);
        glClearColor(0, 0, 0, 0);
        glClear(GL_COLOR_BUFFER_BIT);
        glViewport(0, 0, width >> 1, height >> 1);
        useShader(downsample13);
        renderQuad(MAIN_BLOOM_TEXTURE, 0, 0, width, height);
        blurFilter(bloomTextures[0], 2.5f, 0, 0, width, height);
        int downSamples = 0;
        for (int i = 0; i < bloomBuffers.length; i++) {
            if (bloomBuffers[i] <= 0 || i + 1 >= bloomBuffers.length)
                continue;
            int mipWidth = width >> (i + 2), mipHeight = height >> (i + 2);
            glBindFramebuffer(GL_FRAMEBUFFER, bloomBuffers[i + 1]);
            glViewport(0, 0, mipWidth, mipHeight);
            useShader(downsample13);
            renderQuad(bloomTextures[i], 0, 0, width, height);
            downSamples = i + 1;
        }

        // Up sampling the mip chain
        for (int i = downSamples; i > 0; i--) {
            int lower = bloomTextures[i];
            int mipWidth = width >> (i), mipHeight = height >> (i);
            glBindFramebuffer(GL_FRAMEBUFFER, bloomBuffers[i - 1]);

            drawToBuffers(2);
            glViewport(0, 0, mipWidth, mipHeight);
            blurFilter(lower, 1f, 0, 0, width, height);
            resetDrawBuffer();
            int lowerUpscaled = bloomTextures2[i - 1];

            glEnable(GL_BLEND);
            glBlendFunc(GL_ONE, GL_ONE);
            renderQuad(lowerUpscaled, 0, 0, width, height);
            glDisable(GL_BLEND);
        }

        // Combine into default buffer
        MAIN.bindFramebuffer(false);
        glViewport(0, 0, width, height);
        useShader(additiveCombine, () -> {
            uniformTexture("bloomTexture", 2, bloomTextures[0]);
            uniform1f("exposure", lightExposure);
        });
        renderQuad(MAIN.framebufferTexture, 0, 0, width, height);
        releaseShader();

        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        if (resetGLState) {
            mc.entityRenderer.enableLightmap(0);
            glMatrixMode(GL11.GL_PROJECTION);
            glLoadMatrix(prevProjection);
            glMatrixMode(GL11.GL_MODELVIEW);
            glLoadMatrix(prevModelView);
            glEnable(GL_LIGHTING);
            glEnable(GL_ALPHA_TEST);
            glColorMask(true, true, true, true);
            if (!ClientConstants.renderingGUI && !ClientConstants.renderingArm)
                glEnable(GL_FOG);
        }

        glBindFramebuffer(GL_FRAMEBUFFER, MAIN_BLOOM_BUFFER);
        drawToBuffers(2);
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);

        MAIN.bindFramebuffer(false);
        processBloom = false;
    }

    public static void captureSceneDepth() {
        Framebuffer buff = getMainBuffer();
        int width = mc.displayWidth, height = mc.displayHeight;
        ByteBuffer depthBuff = BufferUtils.createByteBuffer(width * height * 4);
        GL11.glReadPixels(0, 0, width, height, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, depthBuff);

        ByteBuffer rgbaBuffer = BufferUtils.createByteBuffer(width * height * 4);
        for (int i = 0; i < width * height; i++) {
            float depth = depthBuff.getFloat(i * Float.BYTES);
            float depth2 = depth == 1 ? 0 : 1;
            rgbaBuffer.put((byte) (depth * 255));
            rgbaBuffer.put((byte) (depth * 255));
            rgbaBuffer.put((byte) (depth * 255));
            rgbaBuffer.put((byte) (depth == 1 ? 0 : 255));
        }
        rgbaBuffer.flip();

        GL11.glBindTexture(GL_TEXTURE_2D, DEPTH_TEXTURE);
        GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, rgbaBuffer);
    }

    public static void disableGLState() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glColorMask(true, true, true, false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        mc.entityRenderer.disableLightmap(0);
    }

    public static void enableGLState() {
        mc.entityRenderer.enableLightmap(0);
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
        glEnable(GL_BLEND);
        glEnable(GL_ALPHA_TEST);
        glEnable(GL_LIGHTING);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_COLOR_MATERIAL);
        glColorMask(true, true, true, true);
    }

    public static void blurVertical(int textureID, float blurIntensity, float startX, float startY, float width, float height) {
        useShader(blur, () -> {
            uniformVec2("u_resolution", width - startX, height - startY);
            uniform1i("horizontal", 0);
            uniform1f("blurIntensity", blurIntensity);
        });
        renderQuad(textureID, startX, startY, width, height);
    }

    public static void blurHorizontal(int textureID, float blurIntensity, float startX, float startY, float width, float height) {
        useShader(blur, () -> {
            uniformVec2("u_resolution", width - startX, height - startY);
            uniform1i("horizontal", 1);
            uniform1f("blurIntensity", blurIntensity);
        });
        renderQuad(textureID, startX, startY, width, height);
    }

    public static void blurFilter(int textureID, float blurIntensity, float startX, float startY, float width, float height) {
        blurVertical(textureID, blurIntensity, startX, startY, width, height);
        blurHorizontal(textureID, blurIntensity, startX, startY, width, height);
        releaseShader();
    }

    public static void setupDepthAndStencil() {
        OpenGlHelper.func_153176_h(OpenGlHelper.field_153199_f, MAIN.depthBuffer);
        if (net.minecraftforge.client.MinecraftForgeClient.getStencilBits() == 0) {
            OpenGlHelper.func_153186_a(OpenGlHelper.field_153199_f, 33190,
                MAIN.framebufferTextureWidth, MAIN.framebufferTextureHeight);
            OpenGlHelper.func_153190_b(OpenGlHelper.field_153198_e,
                OpenGlHelper.field_153201_h, OpenGlHelper.field_153199_f, MAIN.depthBuffer);
        } else {
            OpenGlHelper.func_153186_a(OpenGlHelper.field_153199_f,
                org.lwjgl.opengl.EXTPackedDepthStencil.GL_DEPTH24_STENCIL8_EXT,
                MAIN.framebufferTextureWidth, MAIN.framebufferTextureHeight);
            OpenGlHelper.func_153190_b(OpenGlHelper.field_153198_e,
                org.lwjgl.opengl.EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT,
                OpenGlHelper.field_153199_f, MAIN.depthBuffer);
            OpenGlHelper.func_153190_b(OpenGlHelper.field_153198_e,
                org.lwjgl.opengl.EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT,
                OpenGlHelper.field_153199_f, MAIN.depthBuffer);
        }
    }

    public static void init(int width, int height) {
        hasInitialized = true;

        // Minimal check: if FBOs or shaders aren’t supported, disable bloom entirely
        if (!OpenGlHelper.framebufferSupported || !ShaderHelper.shadersEnabled()) {
            bloomSupported = false;
            return;
        }

        int previousBuffer = glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        MAIN = getMainBuffer();

        MISC_POST_PROCESSING_BUFFER = OpenGlHelper.func_153165_e();
        GL30.glBindFramebuffer(GL_FRAMEBUFFER, MISC_POST_PROCESSING_BUFFER);

        BLUR_TEXTURE = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, BLUR_TEXTURE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL30.GL_RGBA16F, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        OpenGlHelper.func_153188_a(GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, BLUR_TEXTURE, 0);

        MAIN_BLOOM_BUFFER = OpenGlHelper.func_153165_e();
        GL30.glBindFramebuffer(GL_FRAMEBUFFER, MAIN_BLOOM_BUFFER);

        OpenGlHelper.func_153188_a(GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, MAIN.framebufferTexture, 0);

        MAIN_BLOOM_TEXTURE = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, MAIN_BLOOM_TEXTURE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL30.GL_RGBA16F, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        OpenGlHelper.func_153188_a(GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT2, GL_TEXTURE_2D, MAIN_BLOOM_TEXTURE, 0);
        setupDepthAndStencil();

        drawToBuffers(0, 2);
        glClearColor(0, 0, 0, 1f);
        glClear(GL_COLOR_BUFFER_BIT);
        for (int i = 0; i < bloomBuffers.length; i++) {
            int mipWidth = width >> (i + 1);
            int mipHeight = height >> (i + 1);
            if (mipWidth < 15 || mipHeight < 7)
                break;

            bloomBuffers[i] = OpenGlHelper.func_153165_e();
            GL30.glBindFramebuffer(GL_FRAMEBUFFER, bloomBuffers[i]);

            bloomTextures[i] = TextureUtil.glGenTextures();
            glBindTexture(GL_TEXTURE_2D, bloomTextures[i]);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL30.GL_RGBA16F, mipWidth, mipHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
            OpenGlHelper.func_153188_a(GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, bloomTextures[i], 0);

            bloomTextures2[i] = TextureUtil.glGenTextures();
            glBindTexture(GL_TEXTURE_2D, bloomTextures2[i]);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL30.GL_RGBA16F, mipWidth, mipHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
            OpenGlHelper.func_153188_a(GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT2, GL_TEXTURE_2D, bloomTextures2[i], 0);

            int status = GL30.glCheckFramebufferStatus(GL_FRAMEBUFFER);
            if (status != GL_FRAMEBUFFER_COMPLETE)
                CommonProxy.LOGGER.error("Framebuffer " + i + " is not complete: " + status);

            glClearColor(0, 0, 0, 1f);
            glClear(GL_COLOR_BUFFER_BIT);
        }
        resetDrawBuffer();
        MAIN.bindFramebuffer(false);

        int status = GL30.glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (status != GL_FRAMEBUFFER_COMPLETE)
            CommonProxy.LOGGER.error("Framebuffer is not complete: " + status);

        DEPTH_TEXTURE = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, DEPTH_TEXTURE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL30.GL_DEPTH_COMPONENT32F, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);

        auraBuffer = OpenGlHelper.func_153165_e();
        GL30.glBindFramebuffer(GL_FRAMEBUFFER, auraBuffer);
        for (int i = 0; i < auraTextures.length; i++) {
            auraTextures[i] = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, auraTextures[i]);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL30.GL_RGBA16F, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
            OpenGlHelper.func_153188_a(GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0 + i, GL_TEXTURE_2D, auraTextures[i], 0);
        }
        setupDepthAndStencil();
        status = GL30.glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (status != GL_FRAMEBUFFER_COMPLETE)
            CommonProxy.LOGGER.error("Aura framebuffer is not complete: " + status);
        glClearColor(0, 0, 0, 1f);
        glClear(GL_COLOR_BUFFER_BIT);

        GL30.glBindFramebuffer(GL_FRAMEBUFFER, previousBuffer);

        blankTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, blankTexture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL30.GL_RGBA16F, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
    }

    public static void delete() {
        if (!hasInitialized)
            return;
        for (int i = 0; i < bloomBuffers.length; i++) {
            if (bloomTextures[i] > 0)
                TextureUtil.deleteTexture(bloomTextures[i]);
            if (bloomBuffers[i] > 0)
                OpenGlHelper.func_153174_h(bloomBuffers[i]);
        }

        OpenGlHelper.func_153174_h(auraBuffer);
        for (int i = 0; i < auraTextures.length; i++) {
            if (auraTextures[i] > 0)
                TextureUtil.deleteTexture(auraTextures[i]);
        }
        OpenGlHelper.func_153174_h(MISC_POST_PROCESSING_BUFFER);

        bloomBuffers = new int[BLOOM_BUFFERS_LENGTH];
        bloomTextures = new int[bloomBuffers.length];

        TextureUtil.deleteTexture(MAIN_BLOOM_TEXTURE);
        TextureUtil.deleteTexture(DEPTH_TEXTURE);
        TextureUtil.deleteTexture(BLUR_TEXTURE);
        TextureUtil.deleteTexture(blankTexture);
    }

    public static void copyBuffer(int copyFBO, int pasteFBO, int width, int height, int bufferBits) {
        int previousBuffer = glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        glBindFramebuffer(GL_READ_BUFFER, copyFBO);
        glBindFramebuffer(GL_DRAW_BUFFER, pasteFBO);
        GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, bufferBits, GL_NEAREST);

        int status = GL30.glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (status != GL_FRAMEBUFFER_COMPLETE)
            CommonProxy.LOGGER.error("Copying FBO " + pasteFBO + " is not complete: " + status);

        GL30.glBindFramebuffer(GL_FRAMEBUFFER, previousBuffer);
    }

    public static void saveTextureToPNG(int textureID) {
        if (Minecraft.getMinecraft().isGamePaused())
            return;

        glBindTexture(GL_TEXTURE_2D, textureID);
        int width = (int) GL11.glGetTexLevelParameterf(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int height = (int) GL11.glGetTexLevelParameterf(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

        ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4);
        glGetTexImage(GL_TEXTURE_2D, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = (x + (height - y - 1) * width) * 4;
                int r = buffer.get(index) & 0xFF;
                int g = buffer.get(index + 1) & 0xFF;
                int b = buffer.get(index + 2) & 0xFF;
                int a = buffer.get(index + 3) & 0xFF;
                int argb = (a << 24) | (r << 16) | (g << 8) | b;
                image.setRGB(x, y, argb);
            }
        }

        String desktopPath = System.getProperty("user.home") + "/Desktop/image/";
        String filename = desktopPath + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "_" + textureID + ".png";
        File file = new File(filename);

        try {
            ImageIO.write(image, "PNG", file);
            System.out.println("Texture saved to: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to write PNG file: " + e.getMessage());
        }
    }

    public static Framebuffer getMainBuffer() {
        return Minecraft.getMinecraft().getFramebuffer();
    }

    public static void updateViewportDimensions() {
        IntBuffer viewport = BufferUtils.createIntBuffer(16);
        glGetInteger(GL_VIEWPORT, viewport);
        VIEWPORT_WIDTH = viewport.get(2);
        VIEWPORT_HEIGHT = viewport.get(3);
    }

    public static void drawToBuffers(int... colorBuffers) {
        IntBuffer buffer = BufferUtils.createIntBuffer(colorBuffers.length);
        for (int colorBuffer : colorBuffers) {
            if (colorBuffer > 15) continue;
            buffer.put(GL30.GL_COLOR_ATTACHMENT0 + colorBuffer);
        }
        buffer.flip();
        GL20.glDrawBuffers(buffer);
    }

    public static void resetDrawBuffer() {
        GL20.glDrawBuffers(GL30.GL_COLOR_ATTACHMENT0);
    }

    public static void renderQuad(int textureID, float startX, float startY, float width, float height) {
        Tessellator tessellator = Tessellator.instance;
        if (textureID != -1) glBindTexture(GL_TEXTURE_2D, textureID);

        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(startX, startY, 0, 0, 1);
        tessellator.addVertexWithUV(startX, height, 0, 0, 0);
        tessellator.addVertexWithUV(width, height, 0, 1, 0);
        tessellator.addVertexWithUV(width, startY, 0, 1, 1);
        tessellator.draw();

        glBindTexture(GL_TEXTURE_2D, 0);
    }
}
