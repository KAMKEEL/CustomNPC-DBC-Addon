package kamkeel.npcdbc.client.render;

import JinRyuu.JBRA.RenderPlayerJBRA;
import JinRyuu.JRMCore.entity.EntityCusPar;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.client.shader.PostProcessing;
import kamkeel.npcdbc.client.shader.ShaderHelper;
import kamkeel.npcdbc.client.shader.ShaderResources;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.entity.EntityAura;
import kamkeel.npcdbc.mixins.early.IEntityMC;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import kamkeel.npcdbc.mixins.late.IRenderCusPar;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import noppes.npcs.client.renderer.ImageData;
import noppes.npcs.client.renderer.RenderCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.glu.Sphere;

import java.nio.FloatBuffer;
import java.util.Iterator;

import static kamkeel.npcdbc.client.shader.PostProcessing.processBloom;
import static kamkeel.npcdbc.client.shader.ShaderHelper.*;
import static org.lwjgl.opengl.GL11.*;

public class RenderEventHandler {
    public static FloatBuffer PRE_RENDER_MODELVIEW = BufferUtils.createFloatBuffer(16);

    @SubscribeEvent
    public void enablePlayerStencil(RenderPlayerEvent.Pre e) {
        glClear(GL_STENCIL_BUFFER_BIT); //TODO: needs to be put somewhere else i.e RenderWorldLastEvent, but for some reason doesn't work when put there
        glEnable(GL_STENCIL_TEST);
        enableStencilWriting(e.entity.getEntityId());
    }

    @SubscribeEvent
    public void enableHandStencil(DBCPlayerEvent.RenderArmEvent.Pre e) {
        glClear(GL_STENCIL_BUFFER_BIT); //TODO: needs to be put somewhere else i.e RenderWorldLastEvent, but for some reason doesn't work when put there
        glEnable(GL_STENCIL_TEST);
        enableStencilWriting(e.entity.getEntityId());
    }

    @SubscribeEvent
    public void enableEntityStencil(RenderLivingEvent.Pre e) {
        if ((e.entity instanceof EntityPlayer)) {
            //IMPORTANT, SAVES THE MODEL VIEW MATRIX PRE ENTITYLIVING TRANSFORMATIONS
            glGetFloat(GL_MODELVIEW_MATRIX, PRE_RENDER_MODELVIEW);
        } else if ((e.entity instanceof EntityNPCInterface)) {
            glGetFloat(GL_MODELVIEW_MATRIX, PRE_RENDER_MODELVIEW);
            glClear(GL_STENCIL_BUFFER_BIT); //TODO: needs to be put somewhere else i.e RenderWorldLastEvent, but for some reason doesn't work when put there
            glEnable(GL_STENCIL_TEST);
            enableStencilWriting(e.entity.getEntityId());
            Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
        }
        glDepthMask(true); //fixes a native MC RP1 entity bug in which the depth test is disabled
    }

    @SubscribeEvent
    public void renderNPC(RenderLivingEvent.Post e) {
        if (!(e.entity instanceof EntityNPCInterface))
            return;

        EntityNPCInterface entity = (EntityNPCInterface) e.entity;
        RenderCustomNpc r = (RenderCustomNpc) e.renderer;
        float partialTicks = Minecraft.getMinecraft().timer.renderPartialTicks;

        disableStencilWriting(entity.getEntityId(), false);
        Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
        DBCDisplay display = ((INPCDisplay) entity.display).getDBCDisplay();


        ////////////////////////////////////////
        ////////////////////////////////////////
        //Aura
        EntityAura aura = display.auraEntity;
        if (aura != null && aura.shouldRender()) {
            glPushMatrix();
            glLoadMatrix(PRE_RENDER_MODELVIEW); //RESETS TRANSFORMATIONS DONE TO CURRENT MATRIX TO PRE-ENTITY RENDERING STATE
            AuraRenderer.Instance.renderAura(aura, partialTicks);
            glPopMatrix();
        }


        ////////////////////////////////////////
        ////////////////////////////////////////
        //Custom Particles
        glPushMatrix();
        glLoadMatrix(PRE_RENDER_MODELVIEW); //IMPORTANT, PARTICLES WONT ROTATE PROPERLY WITHOUT THIS
        IRenderCusPar particleRender = null;
        for (Iterator<EntityCusPar> iter = display.particleRenderQueue.iterator(); iter.hasNext(); ) {
            EntityCusPar particle = iter.next();
            if (particleRender == null)
                particleRender = (IRenderCusPar) RenderManager.instance.getEntityRenderObject(particle);

            particleRender.renderParticle(particle, partialTicks);
            if (particle.isDead)
                iter.remove();
        }
        glPopMatrix();


        ////////////////////////////////////////
        ////////////////////////////////////////
        //Outline

        ////////////////////////////////////////
        ////////////////////////////////////////
        Minecraft.getMinecraft().entityRenderer.enableLightmap(0);
        enableStencilWriting(e.entity.getEntityId());
        // postStencilRendering();//LETS YOU DRAW TO THE COLOR BUFFER AGAIN
        glClear(GL_STENCIL_BUFFER_BIT); //TODO: needs to be put somewhere else i.e RenderWorldLastEvent, but for some reason doesn't work when put there
        glDisable(GL_STENCIL_TEST);

    }


    public void renderPlayer(EntityPlayer player, Render renderer, float partialTicks, boolean isArm) {
        ShaderHelper.releaseShader();
        RenderPlayerJBRA render = (RenderPlayerJBRA) renderer;
        DBCData data = DBCData.get(player);

        disableStencilWriting(player.getEntityId(), false);
        Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
        EntityAura aura = data.auraEntity;


        ////////////////////////////////////////
        ////////////////////////////////////////
        //Aura
        if (aura != null && aura.shouldRender()) {
            glPushMatrix();
            glLoadMatrix(PRE_RENDER_MODELVIEW); //RESETS TRANSFORMATIONS DONE TO CURRENT MATRIX TO PRE-ENTITY RENDERING STATE
            AuraRenderer.Instance.renderAura(aura, partialTicks);
            glPopMatrix();
        }


        ////////////////////////////////////////
        ////////////////////////////////////////
        //Custom Particles
        glPushMatrix();
        glLoadMatrix(PRE_RENDER_MODELVIEW); //IMPORTANT, PARTICLES WONT ROTATE PROPERLY WITHOUT THIS
        IRenderCusPar particleRender = null;
        for (Iterator<EntityCusPar> iter = data.particleRenderQueue.iterator(); iter.hasNext(); ) {
            EntityCusPar particle = iter.next();
            if (particleRender == null)
                particleRender = (IRenderCusPar) RenderManager.instance.getEntityRenderObject(particle);

            particleRender.renderParticle(particle, partialTicks);
            if (particle.isDead)
                iter.remove();
        }
        glPopMatrix();


        ////////////////////////////////////////
        ////////////////////////////////////////
        //Outline
        data.outline = new PlayerOutline(0x00ffff, 0xffffff);
        //  data.outline = null;
        if (data.outline != null) {
            if (ConfigDBCClient.EnableBloom) {
                PostProcessing.drawToBuffers(0, 2);
                processBloom = true;
            }

            glPushMatrix();
            useShader(ShaderHelper.outline, () -> {
                uniformTexture("noiseTexture", 2, ShaderResources.PERLIN_NOISE);
                uniformTextureResolution("texRes", ShaderResources.PERLIN_NOISE);
                uniformColor("innerColor", 0x00ffff, 1);
                uniformColor("outerColor", 0xffffff, 1);
                uniform1f("noiseSize", 1f);
                uniform1f("range", 0.21f);
                uniform1f("threshold", 0.55f);
                uniform1f("noiseSpeed", 1);
                uniform1f("throbSpeed", 0f);

                float[] blurKernel = new float[]{1.f, 2.f, 1.f, 2.f, 4.f, 2.f, 1.f, 2.f, 1.f};
                uniformArray("blurKernel", blurKernel);
                uniform1f("blurIntensity", 1f);
            });
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            Sphere s = new Sphere();
            int sphereTrans = 10;
            glTranslatef(0, 0, sphereTrans);
            // s.draw(6, 36, 18);
            glTranslatef(0, 0, -sphereTrans);
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

            PlayerOutline.renderOutline(render, player, partialTicks, isArm);
            releaseShader();
            glPopMatrix();

        } else if (aura == null && ((IEntityMC) player).getRenderPassTampered()) {
            ((IEntityMC) player).setRenderPass(0);
        }


        ////////////////////////////////////////
        ////////////////////////////////////////
        if (processBloom)
            PostProcessing.resetDrawBuffer();
        Minecraft.getMinecraft().entityRenderer.enableLightmap(0);
        //  postStencilRendering();//LETS YOU DRAW TO THE COLOR BUFFER AGAIN
        enableStencilWriting(player.getEntityId());

    }

    @SubscribeEvent
    public void renderPlayer(DBCPlayerEvent.RenderEvent.Pre e) {
        EntityAura aura = DBCData.get(e.entityPlayer).auraEntity;
        if ((aura != null && aura.shouldRender()) || DBCData.get(e.entityPlayer).outline != null)
            Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
    }

    @SubscribeEvent
    public void renderPlayer(DBCPlayerEvent.RenderEvent.Post e) {
        renderPlayer(e.entityPlayer, e.renderer, e.partialRenderTick, false);
    }

    @SubscribeEvent
    public void renderHand(DBCPlayerEvent.RenderArmEvent.Post e) {
        renderPlayer(e.entityPlayer, e.renderer, e.partialRenderTick, true);
    }

    public void newerAuraTemp(EntityAura aura, float partialTicks) {
        double interPosX = (aura.lastTickPosX + (aura.posX - aura.lastTickPosX) * (double) partialTicks) - RenderManager.renderPosX;
        double interPosY = (aura.lastTickPosY + (aura.posY - aura.lastTickPosY) * (double) partialTicks) - RenderManager.renderPosY;
        double interPosZ = (aura.lastTickPosZ + (aura.posZ - aura.lastTickPosZ) * (double) partialTicks) - RenderManager.renderPosZ;
        ImageData tex = new ImageData(CustomNpcPlusDBC.ID + ":textures/aura/aura.png");
        float scale = 2.00f;

        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_LIGHTING);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glAlphaFunc(GL_GREATER, 0.003921569F);
        Minecraft.getMinecraft().entityRenderer.disableLightmap(0);

        glScalef(scale, scale, scale);
        glTranslated(interPosX, interPosY - 0.65f, interPosZ - 0.025f);
        glTranslatef(0f, 0, -0.35f);
        glRotatef(180, 0, 0, 1);
        glRotatef(315, 1, 0, 0);

        for (float j = 1; j < 2; j += 1) {
            glPushMatrix();
            glRotatef(360 * j, 0F, 0F, 1F);
            renderImage(tex, aura.color1, 0.2f);
            glPopMatrix();
        }

        // Reset OpenGL states
        glAlphaFunc(GL_GREATER, 0.1F);
        glDisable(GL_BLEND);
        glEnable(GL_LIGHTING);
        glPopMatrix();
    }

    public static void renderImage(ImageData imageData, int color, float alpha) {
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
