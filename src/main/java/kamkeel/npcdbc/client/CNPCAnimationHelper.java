package kamkeel.npcdbc.client;

import JinRyuu.JBRA.DBC_GiTurtleMdl;
import JinRyuu.JBRA.GiTurtleMdl;
import JinRyuu.JBRA.JBRAH;
import JinRyuu.JBRA.ModelBipedDBC;
import JinRyuu.JBRA.RenderPlayerJBRA;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.JRMCoreHDBC;
import JinRyuu.JRMCore.client.config.jrmc.JGConfigClientSettings;
import JinRyuu.JRMCore.i.ExtendedPlayer;
import JinRyuu.JRMCore.server.config.dbc.JGConfigRaces;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import noppes.npcs.client.ClientCacheHandler;
import noppes.npcs.client.ClientEventHandler;
import noppes.npcs.constants.EnumAnimationPart;
import noppes.npcs.controllers.data.Animation;
import noppes.npcs.controllers.data.AnimationData;
import noppes.npcs.controllers.data.Frame;
import noppes.npcs.controllers.data.FramePart;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON;

public class CNPCAnimationHelper {
    private static final HashSet<Object> parentParts = new HashSet<>();
    private static final HashSet<Object> childParts = new HashSet<>();
    private static final HashSet<Object> processedModels = new HashSet<>();

    public static void setOriginalValues(ModelBase mainModel) {
        if (mainModel == null)
            return;

        if (!processedModels.contains(mainModel)) {
            processedModels.add(mainModel);
        } else {
            return;
        }

        Class<?> clazz = mainModel.getClass();
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                fields.add(field);
            }
            clazz = clazz.getSuperclass();
        }

        for (Field field : fields) {
            if (field.getType().isAssignableFrom(ModelRenderer.class)) {
                try {
                    ModelRenderer modelRenderer = (ModelRenderer) field.get(mainModel);
                    if (!noppes.npcs.client.ClientEventHandler.originalValues.containsKey(modelRenderer)) {
                        FramePart part = new FramePart();
                        part.pivot = new float[]{modelRenderer.rotationPointX, modelRenderer.rotationPointY, modelRenderer.rotationPointZ};
                        part.rotation = new float[]{modelRenderer.rotateAngleX, modelRenderer.rotateAngleY, modelRenderer.rotateAngleZ};
                        noppes.npcs.client.ClientEventHandler.originalValues.put(modelRenderer, part);
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    public static boolean applyValues(ModelRenderer modelRenderer) {
        if (ClientEventHandler.renderingPlayer == null && ClientEventHandler.renderingNpc == null) {
            return false;
        }

        AnimationData animData = null;
        if (ClientEventHandler.renderingPlayer != null) {
            ClientEventHandler.playerModel = modelRenderer.baseModel;
            if (ClientCacheHandler.playerAnimations.containsKey(ClientEventHandler.renderingPlayer.getUniqueID())) {
                animData = ClientCacheHandler.playerAnimations.get(ClientEventHandler.renderingPlayer.getUniqueID());
            }
        } else if (ClientEventHandler.renderingNpc.display instanceof INPCDisplay) {
            return false;
        }

        ModelBase model = modelRenderer.baseModel;
        if (model != null && animData != null && animData.animation != null && animData.isActive()) {
            if (!parentParts.contains(modelRenderer) && modelRenderer.childModels != null && !modelRenderer.childModels.isEmpty()) {
                childParts.addAll(modelRenderer.childModels);
                parentParts.add(modelRenderer);
            }
            if (childParts.contains(modelRenderer) || isPartIgnored(model, modelRenderer)) {
                return false;
            }

            EnumAnimationPart partType = getDBCPartType(model, modelRenderer);
            if (partType == null) {
                partType = getPlayerPartType(modelRenderer);
                if (partType == null) {
                    partType = pivotEqualPart(modelRenderer);
                }
            }

            if (partType != null) {
                if (!noppes.npcs.client.ClientEventHandler.originalValues.containsKey(modelRenderer)) {
                    FramePart part = new FramePart();
                    part.pivot = new float[]{modelRenderer.rotationPointX, modelRenderer.rotationPointY, modelRenderer.rotationPointZ};
                    part.rotation = new float[]{modelRenderer.rotateAngleX, modelRenderer.rotateAngleY, modelRenderer.rotateAngleZ};
                    noppes.npcs.client.ClientEventHandler.originalValues.put(modelRenderer, part);
                }
                FramePart originalPart = noppes.npcs.client.ClientEventHandler.originalValues.get(modelRenderer);
                Frame frame = (Frame) animData.animation.currentFrame();
                if (frame != null && frame.frameParts.containsKey(partType)) {
                    FramePart part = frame.frameParts.get(partType);
                    part.interpolateAngles();
                    part.interpolateOffset();
                    modelRenderer.rotateAngleX = part.prevRotations[0];
                    modelRenderer.rotateAngleY = part.prevRotations[1];
                    modelRenderer.rotateAngleZ = part.prevRotations[2];
                    modelRenderer.rotationPointX = originalPart.pivot[0] + part.prevPivots[0];
                    modelRenderer.rotationPointY = originalPart.pivot[1] + part.prevPivots[1];
                    modelRenderer.rotationPointZ = originalPart.pivot[2] + part.prevPivots[2];
                }
            }
        }
        return false;
    }

    private static EnumAnimationPart getPlayerPartType(ModelRenderer renderer) {
        if (renderer.baseModel instanceof ModelBiped) {
            if (renderer == ((ModelBiped) renderer.baseModel).bipedHead
                || renderer == ((ModelBiped) renderer.baseModel).bipedHeadwear) {
                return EnumAnimationPart.HEAD;
            }
            if (renderer == ((ModelBiped) renderer.baseModel).bipedBody) {
                return EnumAnimationPart.BODY;
            }
            if (renderer == ((ModelBiped) renderer.baseModel).bipedRightArm) {
                return EnumAnimationPart.RIGHT_ARM;
            }
            if (renderer == ((ModelBiped) renderer.baseModel).bipedLeftArm) {
                return EnumAnimationPart.LEFT_ARM;
            }
            if (renderer == ((ModelBiped) renderer.baseModel).bipedRightLeg) {
                return EnumAnimationPart.RIGHT_LEG;
            }
            if (renderer == ((ModelBiped) renderer.baseModel).bipedLeftLeg) {
                return EnumAnimationPart.LEFT_LEG;
            }
        }
        return getPartType(renderer);
    }

    private static EnumAnimationPart pivotEqualPart(ModelRenderer renderer) {
        if (renderer.baseModel instanceof ModelBiped) {
            ModelRenderer head = ((ModelBiped) renderer.baseModel).bipedHead;
            ModelRenderer body = ((ModelBiped) renderer.baseModel).bipedBody;
            ModelRenderer larm = ((ModelBiped) renderer.baseModel).bipedLeftArm;
            ModelRenderer rarm = ((ModelBiped) renderer.baseModel).bipedRightArm;
            ModelRenderer lleg = ((ModelBiped) renderer.baseModel).bipedLeftLeg;
            ModelRenderer rleg = ((ModelBiped) renderer.baseModel).bipedRightLeg;

            if (pivotsEqual(renderer, head)) {
                return EnumAnimationPart.HEAD;
            }
            if (pivotsEqual(renderer, body)) {
                return EnumAnimationPart.BODY;
            }
            if (pivotsEqual(renderer, rarm)) {
                return EnumAnimationPart.RIGHT_ARM;
            }
            if (pivotsEqual(renderer, larm)) {
                return EnumAnimationPart.LEFT_ARM;
            }
            if (pivotsEqual(renderer, rleg)) {
                return EnumAnimationPart.RIGHT_LEG;
            }
            if (pivotsEqual(renderer, lleg)) {
                return EnumAnimationPart.LEFT_LEG;
            }
        }

        return null;
    }

    private static boolean pivotsEqual(ModelRenderer m1, ModelRenderer m2) {
        return m1.rotationPointX == m2.rotationPointX && m1.rotationPointY == m2.rotationPointY && m1.rotationPointZ == m2.rotationPointZ;
    }

    private static boolean isPartIgnored(ModelBase model, ModelRenderer modelRenderer) {
        if (model instanceof ModelBipedDBC) {
            ModelBipedDBC modelBipedDBC = (ModelBipedDBC) model;
            return modelRenderer == modelBipedDBC.Fro5r || modelRenderer == modelBipedDBC.Fro5l;
        }
        return false;
    }

    private static EnumAnimationPart getDBCPartType(ModelBase model, ModelRenderer renderer) {
        if (model instanceof ModelBipedDBC) {
            ModelBipedDBC modelBipedDBC = (ModelBipedDBC) model;
            if (renderer == modelBipedDBC.face1 || renderer == modelBipedDBC.face2
                || renderer == modelBipedDBC.face3 || renderer == modelBipedDBC.face4
                || renderer == modelBipedDBC.face5 || renderer == modelBipedDBC.face6
                //
                || renderer == modelBipedDBC.SaiO
                //
                || renderer == modelBipedDBC.Nam
                //
                || renderer == modelBipedDBC.Fro5 || renderer == modelBipedDBC.Fro
                || renderer == modelBipedDBC.Fro0 || renderer == modelBipedDBC.Fro1 || renderer == modelBipedDBC.Fro2) {
                return EnumAnimationPart.HEAD;
            }
            if (renderer == modelBipedDBC.Fro5b || renderer == modelBipedDBC.WShell) {
                return EnumAnimationPart.BODY;
            }
        }
        if (model instanceof GiTurtleMdl) {
            GiTurtleMdl giTurtleMdl = (GiTurtleMdl) model;
            try {
                Field field = GiTurtleMdl.class.getDeclaredField("cape");
                field.setAccessible(true);
                if (renderer == field.get(giTurtleMdl)) {
                    return EnumAnimationPart.BODY;
                }
            } catch (Exception ignored) {
            }
        }
        if (model instanceof DBC_GiTurtleMdl) {
            DBC_GiTurtleMdl dbcGiTurtleMdl = (DBC_GiTurtleMdl) model;
            if (renderer == dbcGiTurtleMdl.NeckRing) {
                return EnumAnimationPart.BODY;
            }
        }
        return null;
    }

    private static EnumAnimationPart getPartType(ModelRenderer renderer) {
        String rendererName = getModelRendererName(renderer);
        Set<Map.Entry<EnumAnimationPart, String[]>> entrySet = noppes.npcs.client.ClientEventHandler.partNames.entrySet();
        for (Map.Entry<EnumAnimationPart, String[]> entry : entrySet) {
            String[] names = entry.getValue();
            for (String partName : names) {
                if (partName.equals(rendererName)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    private static String getModelRendererName(ModelRenderer renderer) {
        Class<?> RenderClass = renderer.baseModel.getClass();
        Object model = renderer.baseModel;

        while (RenderClass != Object.class) {
            Field[] declared;
            if (noppes.npcs.client.ClientEventHandler.declaredFieldCache.containsKey(RenderClass)) {
                declared = noppes.npcs.client.ClientEventHandler.declaredFieldCache.get(RenderClass);
            } else {
                declared = RenderClass.getDeclaredFields();
                noppes.npcs.client.ClientEventHandler.declaredFieldCache.put(RenderClass, declared);
            }
            for (Field f : declared) {
                f.setAccessible(true);
                try {
                    if (renderer == f.get(model)) {
                        return f.getName();
                    }
                } catch (IllegalAccessException ignored) {
                }
            }
            RenderClass = RenderClass.getSuperclass();
        }
        return null;
    }

    public static void playerFullModel_head(Entity p_78088_1_, CallbackInfo callbackInfo) {
        if (ClientCacheHandler.playerAnimations.containsKey(p_78088_1_.getUniqueID())) {
            AnimationData animData = ClientCacheHandler.playerAnimations.get(p_78088_1_.getUniqueID());
            if (animData != null && animData.isActive()) {
                Frame frame = (Frame) animData.animation.currentFrame();
                if (frame.frameParts.containsKey(EnumAnimationPart.FULL_MODEL)) {
                    FramePart part = frame.frameParts.get(EnumAnimationPart.FULL_MODEL);
                    part.interpolateOffset();
                    part.interpolateAngles();
                    float pi = 180 / (float) Math.PI;
                    GL11.glTranslatef(part.prevPivots[0], -part.prevPivots[1], part.prevPivots[2]);
                    GL11.glRotatef(part.prevRotations[0] * pi, 1, 0, 0);
                    GL11.glRotatef(part.prevRotations[1] * pi, 0, 1, 0);
                    GL11.glRotatef(part.prevRotations[2] * pi, 0, 0, 1);
                }
            }
        }
    }

    public static boolean mixin_renderFirstPersonAnimation(float partialRenderTick, EntityPlayer player, ModelBiped model, RenderBlocks renderBlocksIr, ResourceLocation resItemGlint) {
        AnimationData animationData = ClientCacheHandler.playerAnimations.get(player.getUniqueID());
        if (animationData != null && animationData.isActive()) {
            Frame frame = (Frame) animationData.animation.currentFrame();
            if (frame.frameParts.containsKey(EnumAnimationPart.FULL_MODEL)) {
                FramePart part = frame.frameParts.get(EnumAnimationPart.FULL_MODEL);
                part.interpolateOffset();
                part.interpolateAngles();
            }
        }

        ModelRenderer[] parts = new ModelRenderer[]{model.bipedRightArm, model.bipedLeftArm, model.bipedRightLeg, model.bipedLeftLeg};
        EnumAnimationPart[] enumParts = new EnumAnimationPart[]{EnumAnimationPart.RIGHT_ARM, EnumAnimationPart.LEFT_ARM, EnumAnimationPart.RIGHT_LEG, EnumAnimationPart.LEFT_LEG};
        Frame frame;

        if (animationData != null && animationData.isActive()) {
            Animation animation = (Animation) animationData.getAnimation();
            frame = (Frame) animation.currentFrame();
            for (int i = 0; i < parts.length; i++) {
                ModelRenderer part = parts[i];
                EnumAnimationPart enumPart = enumParts[i];
                FramePart originalPart = noppes.npcs.client.ClientEventHandler.originalValues.get(part);
                if (originalPart != null && frame.frameParts.containsKey(enumPart)) {
                    FramePart animatedPart = frame.frameParts.get(enumPart);
                    part.rotationPointX = originalPart.pivot[0] + animatedPart.prevPivots[0];
                    part.rotationPointY = originalPart.pivot[1] + animatedPart.prevPivots[1];
                    part.rotationPointZ = originalPart.pivot[2] + animatedPart.prevPivots[2];
                    part.rotateAngleX = animatedPart.prevRotations[0];
                    part.rotateAngleY = animatedPart.prevRotations[1];
                    part.rotateAngleZ = animatedPart.prevRotations[2];
                }
            }
        } else {
            return false;
        }

        if (animationData.isActive()) {
            if (frame.frameParts.containsKey(EnumAnimationPart.FULL_MODEL)) {
                FramePart part = frame.frameParts.get(EnumAnimationPart.FULL_MODEL);
                float pi = 180 / (float) Math.PI;
                GL11.glRotatef(-part.prevRotations[1] * pi, 0, 1, 0);
            }
        }

        EntityClientPlayerMP entityclientplayermp = Minecraft.getMinecraft().thePlayer;
        RenderHelper.enableStandardItemLighting();
        int i = Minecraft.getMinecraft().theWorld.getLightBrightnessForSkyBlocks(MathHelper.floor_double(entityclientplayermp.posX), MathHelper.floor_double(entityclientplayermp.posY), MathHelper.floor_double(entityclientplayermp.posZ), 0);
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);

        float f3 = entityclientplayermp.prevRenderArmPitch + (entityclientplayermp.renderArmPitch - entityclientplayermp.prevRenderArmPitch) * partialRenderTick;
        float f4 = entityclientplayermp.prevRenderArmYaw + (entityclientplayermp.renderArmYaw - entityclientplayermp.prevRenderArmYaw) * partialRenderTick;
        GL11.glRotatef((entityclientplayermp.rotationPitch - f3) * 0.1F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef((entityclientplayermp.rotationYaw - f4) * 0.1F, 0.0F, 1.0F, 0.0F);

        try {
            dbcRender(player);
        } catch (Exception e) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getMinecraft().getTextureManager().bindTexture(entityclientplayermp.getLocationSkin());
            renderLimbs();
        }

        ItemRenderer itemRenderer = Minecraft.getMinecraft().entityRenderer.itemRenderer;
        ItemStack itemstack = itemRenderer.itemToRender;
        if (frame.frameParts.containsKey(EnumAnimationPart.RIGHT_ARM) && itemstack != null) {
            float f11, f12;
            GL11.glPushMatrix();

            if (player.fishEntity != null) {
                itemstack = new ItemStack(Items.stick);
            }

            float p_78785_1_ = 0.0625F;
            GL11.glTranslatef(0.1F, 0.4F + -0.75F * 0.8F, -0.3F);
            GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);

            GL11.glTranslatef(model.bipedRightArm.rotationPointX * p_78785_1_, model.bipedRightArm.rotationPointY * p_78785_1_, model.bipedRightArm.rotationPointZ * p_78785_1_);
            GL11.glRotatef((float) Math.toDegrees(model.bipedRightArm.rotateAngleZ), 0, 0, 1);
            GL11.glRotatef((float) Math.toDegrees(model.bipedRightArm.rotateAngleY), 0, 1, 0);
            GL11.glRotatef((float) Math.toDegrees(model.bipedRightArm.rotateAngleX), 1, 0, 0);
            GL11.glTranslatef(-0.1F, 0.6F, 0.0F);

            GL11.glRotatef(255, 0, 1, 0);
            GL11.glRotatef(45, 1, 0, 0);
            GL11.glRotatef(80, 0, 0, 1);

            float f6 = 1 / 1.5F;
            GL11.glScalef(f6, f6, f6);

            if (itemstack.getItem().requiresMultipleRenderPasses()) {
                for (k = 0; k < itemstack.getItem().getRenderPasses(itemstack.getItemDamage()); ++k) {
                    i = itemstack.getItem().getColorFromItemStack(itemstack, k);
                    f12 = (float) (i >> 16 & 255) / 255.0F;
                    f3 = (float) (i >> 8 & 255) / 255.0F;
                    f4 = (float) (i & 255) / 255.0F;
                    GL11.glColor4f(f12, f3, f4, 1.0F);
                    animationRenderItem(player, itemstack, k, EQUIPPED_FIRST_PERSON, renderBlocksIr, resItemGlint);
                }
            } else {
                k = itemstack.getItem().getColorFromItemStack(itemstack, 0);
                f11 = (float) (k >> 16 & 255) / 255.0F;
                f12 = (float) (k >> 8 & 255) / 255.0F;
                f3 = (float) (k & 255) / 255.0F;
                GL11.glColor4f(f11, f12, f3, 1.0F);
                animationRenderItem(player, itemstack, 0, EQUIPPED_FIRST_PERSON, renderBlocksIr, resItemGlint);
            }

            GL11.glPopMatrix();
        }

        for (int p = 0; p < parts.length; p++) {
            ModelRenderer part = parts[p];
            EnumAnimationPart enumPart = enumParts[p];
            FramePart originalPart = noppes.npcs.client.ClientEventHandler.originalValues.get(part);
            if (originalPart != null && frame.frameParts.containsKey(enumPart)) {
                part.rotationPointX = originalPart.pivot[0];
                part.rotationPointY = originalPart.pivot[1];
                part.rotationPointZ = originalPart.pivot[2];
                part.rotateAngleX = originalPart.rotation[0];
                part.rotateAngleY = originalPart.rotation[1];
                part.rotateAngleZ = originalPart.rotation[2];
            }
        }

        return true;
    }

    private static void renderLimbs() {
        AnimationData animationData = ClientCacheHandler.playerAnimations.get(Minecraft.getMinecraft().thePlayer.getUniqueID());
        if (animationData != null && animationData.isActive() && animationData.getAnimation() != null && animationData.getAnimation().currentFrame() != null) {
            Animation animation = (Animation) animationData.getAnimation();
            Frame frame = (Frame) animation.currentFrame();

            ModelBiped model = noppes.npcs.client.ClientEventHandler.firstPersonModel;

            if (frame.frameParts.containsKey(EnumAnimationPart.RIGHT_ARM)) {
                GL11.glPushMatrix();
                GL11.glTranslatef(0.1F, 0.4F + -0.75F * 0.8F, -0.3F);
                GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(0.0F, 0.0F, 0.0F, 1.0F);
                model.bipedRightArm.render(0.0625F);
                GL11.glPopMatrix();
            }


            if (frame.frameParts.containsKey(EnumAnimationPart.LEFT_ARM)) {
                GL11.glPushMatrix();
                GL11.glTranslatef(-0.1F, 0.4F + -0.75F * 0.8F, -0.3F);
                GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(0.0F, 0.0F, 0.0F, 1.0F);
                model.bipedLeftArm.render(0.0625F);
                GL11.glPopMatrix();
            }


            if (frame.frameParts.containsKey(EnumAnimationPart.RIGHT_LEG)) {
                GL11.glPushMatrix();
                GL11.glTranslatef(0.025F, 0.6F + -0.75F * 0.8F, -0.4F);
                GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(0.0F, 0.0F, 0.0F, 1.0F);
                model.bipedRightLeg.render(0.0625F);
                GL11.glPopMatrix();
            }

            if (frame.frameParts.containsKey(EnumAnimationPart.LEFT_LEG)) {
                GL11.glPushMatrix();
                GL11.glTranslatef(-0.025F, 0.6F + -0.75F * 0.8F, -0.4F);
                GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(0.0F, 0.0F, 0.0F, 1.0F);
                model.bipedLeftLeg.render(0.0625F);
                GL11.glPopMatrix();
            }
        }
    }

    /**
     * Renders the player's custom model with DBC mod-specific textures and animations.
     *
     * @param player The player entity to render.
     */
    private static void dbcRender(EntityPlayer player) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Minecraft mc = Minecraft.getMinecraft();
        EntityClientPlayerMP clientPlayer = mc.thePlayer;

        // Check if JHDS and DBC features are enabled
        boolean isJHDS = JBRAH.JHDS();
        boolean isDBC = JRMCoreH.DBC();

        // Get the custom render player instance and its main model
        RenderPlayerJBRA renderPlayer = (RenderPlayerJBRA) RenderManager.instance.getEntityRenderObject(player);
        ModelBiped modelMain = renderPlayer.modelMain;

        // Retrieve skin data if JHDS is enabled
        Object skinData = isJHDS ? JBRAH.skinData(clientPlayer) : null;

        // Set initial color with RGB offsets from RenderPlayerJBRA
        float baseBrightness = 1.0F;
        GL11.glColor3f(baseBrightness + getR(), baseBrightness + getG(), baseBrightness + getB());
        GL11.glPushMatrix();

        // Apply default rotation angles to the model
        modelMain.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);

        // Retrieve player data from JRMCoreH
        String dns = JRMCoreH.dns;
        if (dns.length() > 3) {
            int state = JRMCoreH.State;
            int race = JRMCoreH.dnsRace(dns);
            boolean isSaiOozar = JRMCoreH.rSai(race) && (state == 7 || state == 8);
            int gender = JRMCoreH.dnsGender(dns);
            int skinType = JRMCoreH.dnsSkinT(dns);
            boolean isLegendary = JRMCoreH.lgndb(player, race, state);
            boolean isArcosianUltimate = JRMCoreH.rc_arc(race) && state == 6;
            String dnsau = JRMCoreH.data(16, "");
            dnsau = dnsau.contains(";") ? dnsau.substring(1) : (player.getCommandSenderName().equals(mc.thePlayer.getCommandSenderName()) ? dnsau : "");

            // Calculate body attributes based on skin type and state
            int bodyType = skinType == 0 ? JRMCoreH.dnsBodyC1_0(dns) : JRMCoreH.dnsBodyT(dns);
            int bodyColorMain = skinType == 0 ? 0 : (isArcosianUltimate ? JRMCoreH.dnsauCM(dns) : JRMCoreH.dnsBodyCM(dns));
            int bodyColor1 = skinType == 0 ? 0 : (isArcosianUltimate ? JRMCoreH.dnsauC1(dns) : JRMCoreH.dnsBodyC1(dns));
            int bodyColor2 = skinType == 0 ? 0 : (isArcosianUltimate ? JRMCoreH.dnsauC2(dns) : JRMCoreH.dnsBodyC2(dns));
            int bodyColor3 = skinType == 0 ? 0 : (isArcosianUltimate ? JRMCoreH.dnsauC3(dns) : JRMCoreH.dnsBodyC3(dns));

            int[] raceCustomSkin = JRMCoreH.RaceCustomSkin;
            int[] specials = JRMCoreH.Specials;
            int playerSpecial = skinType == 0 || raceCustomSkin[race] == 0 ? 0 : (bodyType >= specials[race] ? specials[race] - 1 : bodyType);

            // Animation state logic
            ExtendedPlayer extendedPlayer = ExtendedPlayer.get(clientPlayer);
            int animKiShoot = extendedPlayer.getAnimKiShoot();
            int blocking = extendedPlayer.getBlocking();
            boolean instantTransmission = blocking == 2;
            int[] animationIndices = {1, 0, 2, 0, 0, 3, 0, 1, 1};
            int animationId = blocking != 0 ? (instantTransmission ? 6 : 0) : (animKiShoot != 0 ? animationIndices[animKiShoot - 1] + 2 : -1);
            if (!JGConfigClientSettings.CLIENT_DA4) {
                animationId = -1;
            }

            // Handle tail rendering for Saiyans
            if (isDBC) {
                String[] playerData = JRMCoreH.data(clientPlayer.getCommandSenderName(), 1, "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0").split(";");
                int tailColor = Integer.parseInt(playerData[2]);
                if (tailColor == 1) {
                    String[] playerSkills = JRMCoreH.PlyrSkills(clientPlayer);
                    int skillLevel12 = JRMCoreH.SklLvl(12, playerSkills);
                    int skillLevel15 = JRMCoreH.SklLvl(15, playerSkills);
                    String ss = playerData[17];
                    boolean hasSkill = isDBC && !ss.equals("-1");
                    GL11.glPushMatrix();
                    if (hasSkill && (skillLevel12 > 0 || skillLevel15 > 0)) {
                        if (animationId > -1) {
                            dbcFunctionAnimation(animationId, false, true);
                        }
                        GL11.glRotatef(6.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.29F, 0.15F, 0.0F);
                        RenderPlayerJBRA.kss(clientPlayer, false, Integer.parseInt(ss), skillLevel12, skillLevel15);
                    }
                    GL11.glPopMatrix();
                }
            }

            // Render race-specific skins
            ResourceLocation bodySkin;
            if (race == 5 && isDBC) { // Majin
                boolean isBaseMajin = state == 1;
                boolean isPureMajin = state == 3 && JGConfigRaces.CONFIG_MAJIN_PURE_PINK_SKIN;
                bodyColorMain = isBaseMajin ? 12561588 : (isPureMajin ? 16757199 : bodyColorMain);
                bodySkin = new ResourceLocation("jinryuudragonbc:cc/majin/" + (gender == 1 ? "f" : "") + "majin.png");
                renderBody(mc, modelMain, bodySkin, bodyColorMain, player);

                String[] absorptionData = JRMCoreH.data(player.getCommandSenderName(), 13, "0;0;0;0,0,0+0").split(";");
                String[] absorptionVisuals = absorptionData.length > 3 ? absorptionData[3].split(",")[1].split("\\+") : new String[]{"0"};
                int absorbedRace = Integer.parseInt(absorptionVisuals[0]);
                if (JRMCoreH.isRaceArcosian(absorbedRace) || JRMCoreH.isRaceNamekian(absorbedRace)) {
                    bodySkin = new ResourceLocation("jinryuudragonbc:cc/majin/" + (gender == 1 ? "f" : "") + "majin_" + (JRMCoreH.isRaceArcosian(absorbedRace) ? "arco" : "namek") + ".png");
                    renderBody(mc, modelMain, bodySkin, bodyColorMain, player);
                }
                renderDefaultSkin(mc, clientPlayer, modelMain, skinData, isJHDS, skinType, isSaiOozar);
            } else if (race == 3 && isDBC) { // Namekian
                renderNamekian(mc, modelMain, player, state, playerSpecial, bodyColorMain, bodyColor1, bodyColor2);
            } else if (race == 4 && isDBC) { // Arcosian
                renderArcosian(mc, modelMain, player, state, gender, playerSpecial, bodyColorMain, bodyColor1, bodyColor2, bodyColor3);
            } else {
                renderDefaultOrSaiyan(mc, clientPlayer, modelMain, player, skinData, isJHDS, skinType, isSaiOozar, gender, bodyType, state, race, bodyColorMain, bodyColor1, isLegendary);
            }

            // Render bruises if damage indicators are enabled
            renderBruises(mc, modelMain, player, isDBC, race);

            // Render armor
            renderArmor(mc, renderPlayer, modelMain, player, animationId);
        }

        GL11.glPopMatrix();
    }

    /**
     * Renders the player's body with the specified texture and color.
     *
     * @param mc      Minecraft instance.
     * @param model   The model to render.
     * @param texture The texture to apply.
     * @param color   The color to apply.
     * @param player  The player entity.
     */
    private static void renderBody(Minecraft mc, ModelBiped model, ResourceLocation texture, int color, EntityPlayer player) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        mc.getTextureManager().bindTexture(texture);
        glColor3f(color);
        model.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);
        renderLimbs();
    }

    /**
     * Renders the default player skin if applicable.
     *
     * @param mc         Minecraft instance.
     * @param player     The client player.
     * @param model      The model to render.
     * @param skinData   Skin data from JHDS.
     * @param isJHDS     Whether JHDS is enabled.
     * @param skinType   The skin type.
     * @param isSaiOozar Whether the player is in Oozaru form.
     */
    private static void renderDefaultSkin(Minecraft mc, EntityClientPlayerMP player, ModelBiped model, Object skinData, boolean isJHDS, int skinType, boolean isSaiOozar) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (!isSaiOozar && skinType == 0) {
            ResourceLocation skin = player.getLocationSkin().equals(ClientEventHandler.steveTextures) ? ClientEventHandler.steveTextures : player.getLocationSkin();
            if (isJHDS && JBRAH.getSkinHas(skinData)) {
                skin = JBRAH.getSkinLoc(skinData);
            }
            mc.getTextureManager().bindTexture(skin);
            GL11.glColor3f(1.0F + getR(), 1.0F + getG(), 1.0F + getB());
            model.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);
            renderLimbs();
        }
    }

    /**
     * Renders a Namekian player's body parts.
     *
     * @param mc            Minecraft instance.
     * @param model         The model to render.
     * @param player        The player entity.
     * @param state         The player's state.
     * @param playerSpecial Special skin variant.
     * @param bodyCM        Main body color.
     * @param bodyC1        Secondary color 1.
     * @param bodyC2        Secondary color 2.
     */
    private static void renderNamekian(Minecraft mc, ModelBiped model, EntityPlayer player, int state, int playerSpecial, int bodyCM, int bodyC1, int bodyC2) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        boolean hasGodKi = JRMCoreH.StusEfctsMe(17) && JRMCoreHDBC.godKiUserBase(3, state);
        if (hasGodKi) {
            bodyCM = 16744999;
            bodyC1 = 15524763;
            bodyC2 = 12854822;
        }
        renderBody(mc, model, new ResourceLocation("jinryuudragonbc:cc/nam/0nam" + playerSpecial + ".png"), bodyCM, player);
        renderBody(mc, model, new ResourceLocation("jinryuudragonbc:cc/nam/1nam" + playerSpecial + ".png"), bodyC1, player);
        renderBody(mc, model, new ResourceLocation("jinryuudragonbc:cc/nam/2nam" + playerSpecial + ".png"), bodyC2, player);
        renderBody(mc, model, new ResourceLocation("jinryuudragonbc:cc/nam/3nam" + playerSpecial + ".png"), getDefaultColor(), player);
    }

    /**
     * Renders an Arcosian player's body parts.
     *
     * @param mc            Minecraft instance.
     * @param model         The model to render.
     * @param player        The player entity.
     * @param state         The player's state.
     * @param gender        The player's gender.
     * @param playerSpecial Special skin variant.
     * @param bodyCM        Main body color.
     * @param bodyC1        Secondary color 1.
     * @param bodyC2        Secondary color 2.
     * @param bodyC3        Secondary color 3.
     */
    private static void renderArcosian(Minecraft mc, ModelBiped model, EntityPlayer player, int state, int gender, int playerSpecial, int bodyCM, int bodyC1, int bodyC2, int bodyC3) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        boolean hasGodKi = JRMCoreH.StusEfctsMe(17) && JRMCoreHDBC.godKiUserBase(4, state);
        if (hasGodKi) {
            state = 6;
            bodyCM = 5526612;
            bodyC1 = 12829635;
            bodyC3 = 1513239;
        }
        short[] transFrSkn = JRMCoreH.TransFrSkn;
        String genderPrefix = gender == 1 ? "f" : "m";
        renderBody(mc, model, new ResourceLocation("jinryuudragonbc:cc/arc/" + genderPrefix + "/0A" + transFrSkn[state] + playerSpecial + ".png"), bodyCM, player);
        renderBody(mc, model, new ResourceLocation("jinryuudragonbc:cc/arc/" + genderPrefix + "/1A" + transFrSkn[state] + playerSpecial + ".png"), bodyC1, player);
        renderBody(mc, model, new ResourceLocation("jinryuudragonbc:cc/arc/" + genderPrefix + "/2A" + transFrSkn[state] + playerSpecial + ".png"), bodyC2, player);
        renderBody(mc, model, new ResourceLocation("jinryuudragonbc:cc/arc/" + genderPrefix + "/3A" + transFrSkn[state] + playerSpecial + ".png"), bodyC3, player);
        renderBody(mc, model, new ResourceLocation("jinryuudragonbc:cc/arc/" + genderPrefix + "/4A" + transFrSkn[state] + playerSpecial + ".png"), getDefaultColor(), player);
    }

    /**
     * Renders default human or Saiyan Oozaru forms.
     *
     * @param mc           Minecraft instance.
     * @param clientPlayer The client player.
     * @param model        The model to render.
     * @param player       The player entity.
     * @param skinData     Skin data from JHDS.
     * @param isJHDS       Whether JHDS is enabled.
     * @param skinType     The skin type.
     * @param isSaiOozar   Whether the player is in Oozaru form.
     * @param gender       The player's gender.
     * @param bodyType     The body type.
     * @param state        The player's state.
     * @param race         The player's race.
     * @param bodyCM       Main body color.
     * @param bodyC1       Secondary color 1.
     * @param isLegendary  Whether the player is in a legendary state.
     */
    private static void renderDefaultOrSaiyan(Minecraft mc, EntityClientPlayerMP clientPlayer, ModelBiped model, EntityPlayer player, Object skinData, boolean isJHDS, int skinType, boolean isSaiOozar, int gender, int bodyType, int state, int race, int bodyCM, int bodyC1, boolean isLegendary) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (isSaiOozar) {
            renderBody(mc, model, new ResourceLocation("jinryuudragonbc:cc/oozaru1.png"), skinType != 0 ? bodyCM : 11374471, player);
            int tailColor = race != 2 && bodyType == 0 ? 6498048 : bodyType;
            int oozaruColor = state != 0 && state != 7 ? (isLegendary ? 10092390 : 16574610) : (skinType == 1 ? bodyC1 : tailColor);
            renderBody(mc, model, new ResourceLocation("jinryuudragonbc:cc/oozaru2.png"), oozaruColor, player);
        } else if (skinType != 0) {
            renderBody(mc, model, new ResourceLocation("jinryuumodscore:cc/" + (gender == 1 ? "f" : "") + "hum.png"), bodyCM, player);
        }
        renderDefaultSkin(mc, clientPlayer, model, skinData, isJHDS, skinType, isSaiOozar);
        if (state == 14) {
            int tailColor = race != 2 && bodyType == 0 ? 6498048 : bodyType;
            tailColor = JRMCoreH.isAprilFoolsModeOn() ? 13292516 : tailColor;
            int finalColor = skinType == 1 ? bodyC1 : tailColor;
            if (JRMCoreH.rSai(race) && finalColor == 6498048) {
                finalColor = JRMCoreH.isAprilFoolsModeOn() ? 13292516 : 14292268;
            }
            renderBody(mc, model, new ResourceLocation("jinryuudragonbc:cc/ss4" + (skinType == 0 ? "a" : "b") + ".png"), finalColor, player);
        }
    }

    /**
     * Renders bruise overlays based on player health percentage.
     *
     * @param mc     Minecraft instance.
     * @param model  The model to render.
     * @param player The player entity.
     * @param isDBC  Whether DBC is enabled.
     */
    private static void renderBruises(Minecraft mc, ModelBiped model, EntityPlayer player, boolean isDBC, int race) {
        if (JGConfigClientSettings.CLIENT_DA19 && (isDBC || JRMCoreH.NC())) {
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
            GL11.glDepthMask(false);


            int maxHealth = JRMCoreH.stat(player, 2, JRMCoreH.Pwrtyp, 2, JRMCoreH.PlyrAttrbts[2], race, JRMCoreH.Class, 0.0F);
            int currentHealth = Integer.parseInt(JRMCoreH.data(player.getCommandSenderName(), 8, "200"));
            int healthPercentage = (int) ((float) currentHealth / (maxHealth / 100.0F));

            String[] bruiseTextures = {
                "jinryuumodscore:cc/bruises1.png",
                "jinryuumodscore:cc/bruises2.png",
                "jinryuumodscore:cc/bruises3.png",
                "jinryuumodscore:cc/bruises4.png"
            };
            int[] thresholds = {70, 55, 35, 20};
            for (int i = 0; i < bruiseTextures.length; i++) {
                if (healthPercentage < thresholds[i]) {
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    mc.getTextureManager().bindTexture(new ResourceLocation(bruiseTextures[i]));
                    renderLimbs();
                }
            }

            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
        }
    }

    /**
     * Renders the player's armor.
     *
     * @param mc           Minecraft instance.
     * @param renderPlayer The custom render player instance.
     * @param model        The model to render.
     * @param player       The player entity.
     * @param animationId  The current animation ID.
     */
    private static void renderArmor(Minecraft mc, RenderPlayerJBRA renderPlayer, ModelBiped model, EntityPlayer player, int animationId) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ItemStack armorStack = player.inventory.armorItemInSlot(2);
        if (armorStack != null && armorStack.getItem() instanceof ItemArmor) {
            ItemArmor armorItem = (ItemArmor) armorStack.getItem();
            GL11.glPushMatrix();
            String dbcArmorTexture = armorItem.getArmorTexture(armorStack, player, 2, null);
            ResourceLocation armorTexture = dbcArmorTexture != null ? new ResourceLocation(dbcArmorTexture.replace("jbra", "").replace("_dam", "")) : RenderBiped.getArmorResource(player, armorStack, 1, null);
            mc.getTextureManager().bindTexture(armorTexture);

            if (animationId > -1) {
                dbcFunctionAnimation(animationId, false, true);
            }
            GL11.glColor3f(1.0F + getR(), 1.0F + getG(), 1.0F + getB());
            GL11.glScalef(1.0001F, 1.0001F, 1.0001F);
            if (dbcArmorTexture != null) {
                model.textureHeight = 64;
                model.textureWidth = 128;
            }
            model.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);
            renderLimbs();
            GL11.glPopMatrix();
        }
    }

    private static void dbcFunctionAnimation(int id, boolean s, boolean fp) {
        if (s) {
            if (id != 0 && id != 6) {
                if (id == 1) {
                    GL11.glTranslatef(-0.2F, -0.4F, -0.8F);
                    GL11.glRotatef(50.0F, 1.0F, 0.0F, 1.0F);
                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
                } else if (id != 2 && id != 3) {
                    if (id == 4 || id == 5) {
                        GL11.glTranslatef(-0.2F, 0.4F, -0.1F);
                        GL11.glRotatef(10.0F, -1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(20.0F, 0.0F, 0.0F, -1.0F);
                        GL11.glRotatef(40.0F, 0.0F, 0.0F, 1.0F);
                    }
                } else {
                    GL11.glTranslatef(-0.2F, 0.0F, -0.1F);
                    GL11.glRotatef(10.0F, -1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(20.0F, 0.0F, 0.0F, -1.0F);
                }
            } else {
                if (id == 0) {
                    if (!JGConfigClientSettings.CLIENT_DA18) {
                        return;
                    }
                } else if (!JGConfigClientSettings.instantTransmissionFirstPerson) {
                    return;
                }

                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                GL11.glAlphaFunc(516, 0.003921569F);
                GL11.glDepthMask(false);
                GL11.glTranslatef(-0.5F, -0.1F, -0.1F);
                GL11.glRotatef(40.0F, 0.0F, 0.0F, -1.0F);
                GL11.glRotatef(80.0F, -1.0F, 0.0F, 0.0F);
                GL11.glRotatef((float) (id == 0 ? -20 : 30), 0.0F, 0.0F, 1.0F);
            }
        } else if (id == 0) {
            if (JGConfigClientSettings.CLIENT_DA18) {
                GL11.glTranslatef(-0.2F, -0.4F, -0.8F);
                GL11.glRotatef(50.0F, 1.0F, 0.0F, 1.0F);
                GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
            }
        } else if (id == 3) {
            GL11.glTranslatef(0.1F, -0.2F, -0.5F);
            GL11.glTranslatef(-0.2F, 0.0F, -0.1F);
            GL11.glRotatef(10.0F, -1.0F, 0.0F, 0.0F);
            GL11.glRotatef(20.0F, 0.0F, 0.0F, -1.0F);
            GL11.glRotatef(115.0F, 0.0F, 1.0F, 0.0F);
        } else if (id == 5) {
            GL11.glTranslatef(-0.2F, -0.4F, -0.8F);
            GL11.glTranslatef(-0.4F, 0.1F, -0.1F);
            GL11.glRotatef(42.0F, -1.0F, 0.0F, 0.0F);
            GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(115.0F, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(-0.6F, 0.08F, 0.3F);
        }
    }

    /**
     * Returns the default RGB color adjusted by RenderPlayerJBRA offsets.
     *
     * @return The default color as an integer.
     */
    private static int getDefaultColor() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        float r = 1.0F + getR();
        float g = 1.0F + getG();
        float b = 1.0F + getB();
        return (int) (r * 255) << 16 | (int) (g * 255) << 8 | (int) (b * 255);
    }

    private static float getR() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> RenderPlayerJBRA = Class.forName("JinRyuu.JBRA.RenderPlayerJBRA");
        Method method = RenderPlayerJBRA.getDeclaredMethod("getR");
        method.setAccessible(true);
        return (float) method.invoke(null);
    }

    private static float getB() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> RenderPlayerJBRA = Class.forName("JinRyuu.JBRA.RenderPlayerJBRA");
        Method method = RenderPlayerJBRA.getDeclaredMethod("getB");
        method.setAccessible(true);
        return (float) method.invoke(null);
    }

    private static float getG() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> RenderPlayerJBRA = Class.forName("JinRyuu.JBRA.RenderPlayerJBRA");
        Method method = RenderPlayerJBRA.getDeclaredMethod("getG");
        method.setAccessible(true);
        return (float) method.invoke(null);
    }

    private static void glColor3f(int c) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        float h2 = (float) (c >> 16 & 255) / 255.0F;
        float h3 = (float) (c >> 8 & 255) / 255.0F;
        float h4 = (float) (c & 255) / 255.0F;
        float h1 = 1.0F;
        float r = h1 * h2;
        float g = h1 * h3;
        float b = h1 * h4;
        GL11.glColor3f(r + getR(), g + getG(), b + getB());
    }

    private static void animationRenderItem(EntityLivingBase p_78443_1_, ItemStack p_78443_2_, int p_78443_3_, IItemRenderer.ItemRenderType type, RenderBlocks renderBlocksIr, ResourceLocation resItemGlint) {
        GL11.glPushMatrix();
        TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
        Item item = p_78443_2_.getItem();
        Block block = Block.getBlockFromItem(item);

        if (p_78443_2_ != null && block != null && block.getRenderBlockPass() != 0) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_CULL_FACE);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        }
        IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(p_78443_2_, type);
        if (customRenderer != null) {
            texturemanager.bindTexture(texturemanager.getResourceLocation(p_78443_2_.getItemSpriteNumber()));
            ForgeHooksClient.renderEquippedItem(type, customRenderer, renderBlocksIr, p_78443_1_, p_78443_2_);
        } else if (p_78443_2_.getItemSpriteNumber() == 0 && item instanceof ItemBlock && RenderBlocks.renderItemIn3d(block.getRenderType())) {
            texturemanager.bindTexture(texturemanager.getResourceLocation(0));

            GL11.glTranslatef(0.0F, 0.2F, -0.2F);
            if (p_78443_2_ != null && block != null && block.getRenderBlockPass() != 0) {
                GL11.glDepthMask(false);
                renderBlocksIr.renderBlockAsItem(block, p_78443_2_.getItemDamage(), 1.0F);
                GL11.glDepthMask(true);
            } else {
                renderBlocksIr.renderBlockAsItem(block, p_78443_2_.getItemDamage(), 1.0F);
            }
        } else {
            IIcon iicon = p_78443_1_.getItemIcon(p_78443_2_, p_78443_3_);

            if (iicon == null) {
                GL11.glPopMatrix();
                return;
            }

            texturemanager.bindTexture(texturemanager.getResourceLocation(p_78443_2_.getItemSpriteNumber()));
            TextureUtil.func_152777_a(false, false, 1.0F);
            Tessellator tessellator = Tessellator.instance;
            float f = iicon.getMinU();
            float f1 = iicon.getMaxU();
            float f2 = iicon.getMinV();
            float f3 = iicon.getMaxV();
            float f4 = 0.0F;
            float f5 = 0.3F;
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glTranslatef(-f4, -f5, 0.0F);
            float f6 = 1.5F;
            GL11.glScalef(f6, f6, f6);
            GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
            ItemRenderer.renderItemIn2D(tessellator, f1, f2, f, f3, iicon.getIconWidth(), iicon.getIconHeight(), 0.0625F);

            if (p_78443_2_.hasEffect(p_78443_3_)) {
                GL11.glDepthFunc(GL11.GL_EQUAL);
                GL11.glDisable(GL11.GL_LIGHTING);
                texturemanager.bindTexture(resItemGlint);
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(768, 1, 1, 0);
                float f7 = 0.76F;
                GL11.glColor4f(0.5F * f7, 0.25F * f7, 0.8F * f7, 1.0F);
                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GL11.glPushMatrix();
                float f8 = 0.125F;
                GL11.glScalef(f8, f8, f8);
                float f9 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                GL11.glTranslatef(f9, 0.0F, 0.0F);
                GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef(f8, f8, f8);
                f9 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                GL11.glTranslatef(-f9, 0.0F, 0.0F);
                GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
                GL11.glPopMatrix();
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDepthFunc(GL11.GL_LEQUAL);
            }

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            texturemanager.bindTexture(texturemanager.getResourceLocation(p_78443_2_.getItemSpriteNumber()));
            TextureUtil.func_147945_b();
        }

        GL11.glPopMatrix();
    }

}
