package kamkeel.npcdbc.client.shader;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import kamkeel.npcdbc.client.render.RenderEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

public class PostProcessing {
    public static int COLOR_BUFFER_2;

    @SubscribeEvent
    public void preProcess(PostProcessing.Event.Pre e) {
       // RenderEventHandler.tempPre(e);


    }

    @SubscribeEvent
    public void postProcess(PostProcessing.Event.Post e) {
    //    RenderEventHandler.tempPost(e);


    }


    public static void renderQuad(int textureID, float startX, float startY, float width, float height) {
        Tessellator tessellator = Tessellator.instance;
        glBindTexture(GL_TEXTURE_2D, textureID);

        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(startX, startY, 0, 0, 1);
        tessellator.addVertexWithUV(startX, height, 0, 0, 0);
        tessellator.addVertexWithUV(width, height, 0, 1, 0);
        tessellator.addVertexWithUV(width, startY, 0, 1, 1);
        tessellator.draw();

        glBindTexture(GL_TEXTURE_2D, 0);
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
