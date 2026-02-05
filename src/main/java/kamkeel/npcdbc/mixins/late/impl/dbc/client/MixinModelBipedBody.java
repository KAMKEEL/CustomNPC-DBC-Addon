package kamkeel.npcdbc.mixins.late.impl.dbc.client;

import JinRyuu.JBRA.GiTurtleMdl;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.entity.ModelBipedBody;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import kamkeel.npcdbc.mixins.late.IScaleRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.util.MathHelper;
import noppes.npcs.client.model.ModelMPM;
import noppes.npcs.client.model.util.ModelScaleRenderer;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.data.ModelScalePart;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import static JinRyuu.JRMCore.entity.ModelBipedBody.*;

@Mixin(value = ModelBipedBody.class, remap = false)
public abstract class MixinModelBipedBody extends ModelBiped {

    @Shadow
    public ModelRenderer bipedHead;
    @Shadow
    public ModelRenderer bipedBody;
    @Shadow
    public ModelRenderer bipedRightArm;
    @Shadow
    public ModelRenderer bipedLeftArm;
    @Shadow
    public ModelRenderer bipedRightLeg;
    @Shadow
    public ModelRenderer bipedLeftLeg;
    @Shadow
    public ModelRenderer body;
    @Shadow
    public ModelRenderer Brightarm;
    @Shadow
    public ModelRenderer Bleftarm;
    @Shadow
    public ModelRenderer rightleg;
    @Shadow
    public ModelRenderer leftleg;

    @Shadow
    public ModelRenderer hip;
    @Shadow
    public ModelRenderer waist;
    @Shadow
    public ModelRenderer Bbreast;
    @Shadow
    public ModelRenderer breast;
    @Shadow
    public ModelRenderer bottom;

    @Shadow
    public ModelRenderer breast2;
    @Shadow
    public ModelRenderer Bbreast2;

    @Shadow
    public float rot1;
    @Shadow
    public float rot4;
    @Shadow
    public float rot3;
    @Shadow
    public float rot2;
    @Shadow
    public float rot5;
    @Shadow
    public float rot6;
    @Shadow
    public net.minecraft.entity.Entity Entity;

    @Shadow
    private void setRotation(ModelRenderer model, float x, float y, float z) {}


    @Shadow
    public ModelRenderer leftarm;

    @Shadow
    public boolean isSneak;
//
//    /**
//     * @author
//     * @reason
//     */
//    @Overwrite
//    public void renderBody(float par7) {
//        ModelMPM modelMPM = null;
//        ModelScaleRenderer scaleRenderer = null;
//
//        // SAFETY: avoid calling the renderer/main model through RenderManager without checking
//        // This prevents possible recursive re-entry (which can cause StackOverflowError).
//        try {
//            if (Entity instanceof EntityCustomNpc && (Object) this instanceof GiTurtleMdl) {
//                Object renderObj = RenderManager.instance.getEntityRenderObject(Entity);
//                if (renderObj instanceof RendererLivingEntity) {
//                    RendererLivingEntity renderer = (RendererLivingEntity) renderObj;
//                    ModelBase main = renderer.mainModel;
//                    // only use the main model if it's a ModelMPM and not this model instance
//                    if (main instanceof ModelMPM && main != this) {
//                        modelMPM = (ModelMPM) main;
//                        this.isSneak = modelMPM.isSneak;
//                    }
//                }
//            }
//        } catch (Throwable t) {
//            // Defensive: if anything unexpected happens when querying render manager,
//            // fall back to modelMPM == null and continue rendering.
//            modelMPM = null;
//        }
//
//        float f5 = par7;
//        float f6;
//
//        if (g <= 1) {
//            if (this.isChild) {
//                // child head block
//                f6 = 2.0F;
//                GL11.glPushMatrix();
//                GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
//                GL11.glTranslatef(0.0F, 16.0F * par7, 0.0F);
//                GL11.glPushMatrix();
//                if (modelMPM != null) {
//                    scaleRenderer = (ModelScaleRenderer) modelMPM.bipedHead;
//                    customNPC_DBC_Addon$copyNpcRotationAngles(scaleRenderer, bipedHead);
//                    customNPC_DBC_Addon$copyScale(scaleRenderer);
//                }
//                this.bipedHead.render(par7);
//                GL11.glPopMatrix();
//                GL11.glPopMatrix();
//
//                // child body block
//                GL11.glPushMatrix();
//                GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
//                GL11.glTranslatef(0.0F, 24.0F * par7, 0.0F);
//                GL11.glPushMatrix();
//                if (modelMPM != null) {
//                    scaleRenderer = (ModelScaleRenderer) modelMPM.bipedBody;
//                    customNPC_DBC_Addon$copyNpcRotationAngles(scaleRenderer, bipedBody);
//                    customNPC_DBC_Addon$copyScale(scaleRenderer);
//                }
//                this.bipedBody.render(par7);
//                GL11.glPopMatrix();
//
//                // right arm
//                GL11.glPushMatrix();
//                if (modelMPM != null) {
//                    scaleRenderer = (ModelScaleRenderer) modelMPM.bipedRightArm;
//                    customNPC_DBC_Addon$copyNpcRotationAngles(scaleRenderer, bipedRightArm);
//                    customNPC_DBC_Addon$copyScale(scaleRenderer);
//                }
//                this.bipedRightArm.render(par7);
//                GL11.glPopMatrix();
//
//                // left arm
//                GL11.glPushMatrix();
//                if (modelMPM != null) {
//                    scaleRenderer = (ModelScaleRenderer) modelMPM.bipedLeftArm;
//                    customNPC_DBC_Addon$copyNpcRotationAngles(scaleRenderer, bipedLeftArm);
//                    customNPC_DBC_Addon$copyScale(scaleRenderer);
//                }
//                this.bipedLeftArm.render(par7);
//                GL11.glPopMatrix();
//
//                // right leg
//                GL11.glPushMatrix();
//                if (modelMPM != null) {
//                    scaleRenderer = (ModelScaleRenderer) modelMPM.bipedRightLeg;
//                    customNPC_DBC_Addon$copyNpcRotationAngles(scaleRenderer, bipedRightLeg);
//                    customNPC_DBC_Addon$copyScale(scaleRenderer);
//                }
//                this.bipedRightLeg.render(par7);
//                GL11.glPopMatrix();
//
//                // left leg
//                GL11.glPushMatrix();
//                if (modelMPM != null) {
//                    scaleRenderer = (ModelScaleRenderer) modelMPM.bipedLeftLeg;
//                    customNPC_DBC_Addon$copyNpcRotationAngles(scaleRenderer, bipedLeftLeg);
//                    customNPC_DBC_Addon$copyScale(scaleRenderer);
//                }
//                this.bipedLeftLeg.render(par7);
//                GL11.glPopMatrix();
//                GL11.glPopMatrix();
//            } else {
//                // adult, g <= 1 path
//                f6 = f;
//                // outer head scale
//                GL11.glPushMatrix();
//                GL11.glScalef(0.5F + 0.5F / f6, 0.5F + 0.5F / f6, 0.5F + 0.5F / f6);
//                GL11.glTranslatef(0.0F, (f6 - 1.0F) / f6 * (2.0F - (f6 >= 1.5F && f6 <= 2.0F ? (2.0F - f6) / 2.5F : (f6 < 1.5F && f6 >= 1.0F ? (f6 * 2.0F - 2.0F) * 0.2F : 0.0F))), 0.0F);
//
//                // head (with optional MPM scaling)
//                GL11.glPushMatrix();
//                if (modelMPM != null) {
//                    scaleRenderer = (ModelScaleRenderer) modelMPM.bipedHead;
//                    customNPC_DBC_Addon$copyNpcRotationAngles(scaleRenderer, bipedHead);
//                    customNPC_DBC_Addon$copyScale(scaleRenderer);
//                }
//                this.bipedHead.render(par7);
//                GL11.glPopMatrix();
//
//                GL11.glPopMatrix(); // pop outer head scale
//
//                // body + limbs group scale
//                GL11.glPushMatrix();
//                GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
//                GL11.glTranslatef(0.0F, (f6 - 1.0F) * 1.5F, 0.0F);
//
//                // body
//                GL11.glPushMatrix();
//                if (modelMPM != null) {
//                    scaleRenderer = (ModelScaleRenderer) modelMPM.bipedBody;
//                    customNPC_DBC_Addon$copyNpcRotationAngles(scaleRenderer, bipedBody);
//                    customNPC_DBC_Addon$copyScale(scaleRenderer);
//                }
//                this.bipedBody.render(par7);
//                GL11.glPopMatrix();
//
//                // right arm
//                GL11.glPushMatrix();
//                if (modelMPM != null) {
//                    scaleRenderer = (ModelScaleRenderer) modelMPM.bipedRightArm;
//                    customNPC_DBC_Addon$copyNpcRotationAngles(scaleRenderer, bipedRightArm);
//                    customNPC_DBC_Addon$copyScale(scaleRenderer);
//                }
//                this.bipedRightArm.render(par7);
//                GL11.glPopMatrix();
//
//                // left arm
//                GL11.glPushMatrix();
//                if (modelMPM != null) {
//                    scaleRenderer = (ModelScaleRenderer) modelMPM.bipedLeftArm;
//                    customNPC_DBC_Addon$copyNpcRotationAngles(scaleRenderer, bipedLeftArm);
//                    customNPC_DBC_Addon$copyScale(scaleRenderer);
//                }
//                this.bipedLeftArm.render(par7);
//                GL11.glPopMatrix();
//
//                // right leg
//                GL11.glPushMatrix();
//                if (modelMPM != null) {
//                    scaleRenderer = (ModelScaleRenderer) modelMPM.bipedRightLeg;
//                    customNPC_DBC_Addon$copyNpcRotationAngles(scaleRenderer, bipedRightLeg);
//                    customNPC_DBC_Addon$copyScale(scaleRenderer);
//                }
//                this.bipedRightLeg.render(par7);
//                GL11.glPopMatrix();
//
//                // left leg
//                GL11.glPushMatrix();
//                if (modelMPM != null) {
//                    scaleRenderer = (ModelScaleRenderer) modelMPM.bipedLeftLeg;
//                    customNPC_DBC_Addon$copyNpcRotationAngles(scaleRenderer, bipedLeftLeg);
//                    customNPC_DBC_Addon$copyScale(scaleRenderer);
//                }
//                this.bipedLeftLeg.render(par7);
//                GL11.glPopMatrix();
//
//                GL11.glPopMatrix(); // end body + limbs group
//            }
//        } else {
//            // g > 1 path
//            f6 = f;
//
//            // head outer scale
//            GL11.glPushMatrix();
//            GL11.glScalef((0.5F + 0.5F / f6) * 0.85F, 0.5F + 0.5F / f6, (0.5F + 0.5F / f6) * 0.85F);
//            GL11.glTranslatef(0.0F, (f6 - 1.0F) / f6 * (2.0F - (f6 >= 1.5F && f6 <= 2.0F ? (2.0F - f6) / 2.5F : (f6 < 1.5F && f6 >= 1.0F ? (f6 * 2.0F - 2.0F) * 0.2F : 0.0F))), 0.0F);
//
//            // head
//            GL11.glPushMatrix();
//            if (modelMPM != null) {
//                scaleRenderer = (ModelScaleRenderer) modelMPM.bipedHead;
//                customNPC_DBC_Addon$copyNpcRotationAngles(scaleRenderer, bipedHead);
//                customNPC_DBC_Addon$copyScale(scaleRenderer);
//            }
//            this.bipedHead.render(f5);
//            GL11.glPopMatrix();
//
//            GL11.glPopMatrix(); // pop head outer
//
//            // arms group
//            GL11.glPushMatrix();
//            GL11.glScalef(1.0F / f6 * 0.7F, 1.0F / f6, 1.0F / f6 * 0.7F);
//            GL11.glTranslatef(0.0F, (f6 - 1.0F) * 1.5F, 0.0F);
//
//            // Brightarm
//            GL11.glPushMatrix();
//            if (modelMPM != null) {
//                scaleRenderer = (ModelScaleRenderer) modelMPM.bipedRightArm;
//                customNPC_DBC_Addon$copyNpcRotationAngles(scaleRenderer, Brightarm);
//                customNPC_DBC_Addon$copyScale(scaleRenderer);
//            }
//            this.Brightarm.render(f5);
//            GL11.glPopMatrix();
//
//            // Bleftarm
//            GL11.glPushMatrix();
//            if (modelMPM != null) {
//                scaleRenderer = (ModelScaleRenderer) modelMPM.bipedLeftArm;
//                customNPC_DBC_Addon$copyNpcRotationAngles(scaleRenderer, Bleftarm);
//                customNPC_DBC_Addon$copyScale(scaleRenderer);
//            }
//            this.Bleftarm.render(f5);
//            GL11.glPopMatrix();
//
//            GL11.glPopMatrix(); // end arms group
//
//            // right leg group
//            GL11.glPushMatrix();
//            GL11.glScalef(1.0F / f6 * 0.85F, 1.0F / f6, 1.0F / f6 * 0.775F);
//            if (this.isSneak) {
//                GL11.glTranslatef(-0.015F, (f6 - 1.0F) * 1.5F, -0.0F);
//            } else {
//                GL11.glTranslatef(-0.015F, (f6 - 1.0F) * 1.5F, -0.015F);
//            }
//
//            GL11.glPushMatrix();
//            if (modelMPM != null) {
//                scaleRenderer = (ModelScaleRenderer) modelMPM.bipedRightLeg;
//                customNPC_DBC_Addon$copyNpcRotationAngles(scaleRenderer, rightleg);
//                customNPC_DBC_Addon$copyScale(scaleRenderer);
//            }
//            this.rightleg.render(f5);
//            GL11.glPopMatrix();
//
//            GL11.glPopMatrix(); // end right leg group
//
//            // left leg group
//            GL11.glPushMatrix();
//            GL11.glScalef(1.0F / f6 * 0.85F, 1.0F / f6, 1.0F / f6 * 0.775F);
//            if (this.isSneak) {
//                GL11.glTranslatef(0.015F, (f6 - 1.0F) * 1.5F, -0.0F);
//            } else {
//                GL11.glTranslatef(0.015F, (f6 - 1.0F) * 1.5F, -0.015F);
//            }
//
//            GL11.glPushMatrix();
//            if (modelMPM != null) {
//                scaleRenderer = (ModelScaleRenderer) modelMPM.bipedLeftLeg;
//                customNPC_DBC_Addon$copyNpcRotationAngles(scaleRenderer, leftleg);
//                customNPC_DBC_Addon$copyScale(scaleRenderer);
//            }
//            this.leftleg.render(f5);
//            GL11.glPopMatrix();
//
//            GL11.glPopMatrix(); // end left leg group
//
//            // BODY & BREAST & TORSO GROUP - ensure single push/pop for group
//            GL11.glPushMatrix();
//
//            if (modelMPM != null) {
//                ModelScaleRenderer scaledBody = (ModelScaleRenderer) modelMPM.bipedBody;
//                scaledBody.rotateAngleX -= (this.isSneak ? 0.5f : 0f);
//                GL11.glTranslatef(scaledBody.x, scaledBody.y, scaledBody.z);
//                customNPC_DBC_Addon$transRot(1, scaledBody);
////                copyScale(scaleRenderer); // note: scaleRenderer currently refers to last used renderer
//                scaledBody.rotateAngleX += (this.isSneak ? 0.5f : 0f);
//            }
//
//            GL11.glScalef(1.0F / f6 * 0.675F, 1.0F / f6, 1.0F / f6 * 0.8F);
//
//
//            // breast parameters
//            int b = 0;
//            if (Entity instanceof EntityCustomNpc) {
//                b = ((INPCDisplay) ((EntityCustomNpc) Entity).display).getDBCDisplay().breastSize;
//            } else {
//                String[] s = JRMCoreH.data(this.Entity.getCommandSenderName(), 1, "0;0;0;0;0;0;0;0;0").split(";");
//                String dns = s.length > 1 ? s[1] : "0";
//                b = JRMCoreH.dnsBreast(dns);
//            }
//            float scale = (float) b * 0.03F;
//            float br = 0.4235988F + scale;
//            float bs = 0.8F + scale;
//            float bsY = 0.85F + scale * 0.5F;
//            float bt = 0.1F * scale;
//            boolean bounce = this.Entity.onGround || this.Entity.isInWater();
//            float bspeed = this.Entity.isSprinting() ? 1.5F : (this.Entity.isSneaking() ? 0.5F : 1.0F);
//            float bbY = (bounce ? MathHelper.sin(this.rot1 * 0.6662F * bspeed * 1.5F + 3.1415927F) * this.rot2 * 0.03F : 0.0F) * (float) b * 0.1119F;
//
//            GL11.glTranslatef(0.0F, (f6 - 1.0F) * 1.5F + bbY, 0.015F + bt);
//            GL11.glScalef(1.0F, bsY, bs);
//            this.setRotation(this.breast, -br, 0.0F, 0.0F);
//            this.setRotation(this.breast2, br, 3.141593F, 0.0F);
//            if (bounce) {
//                ModelRenderer var10000 = this.breast;
//                var10000.rotateAngleX += -MathHelper.cos(this.rot1 * 0.6662F * bspeed + 3.1415927F) * this.rot2 * 0.05F * (float) b * 0.1119F;
//                var10000 = this.breast;
//                var10000.rotateAngleY += MathHelper.cos(this.rot1 * 0.6662F * bspeed + 3.1415927F) * this.rot2 * 0.02F * (float) b * 0.1119F;
//                var10000 = this.breast2;
//                var10000.rotateAngleX += MathHelper.cos(this.rot1 * 0.6662F * bspeed + 3.1415927F) * this.rot2 * 0.05F * (float) b * 0.1119F;
//                var10000 = this.breast2;
//                var10000.rotateAngleY += MathHelper.cos(this.rot1 * 0.6662F * bspeed + 3.1415927F) * this.rot2 * 0.02F * (float) b * 0.1119F;
//            }
//
//            this.Bbreast.render(f5);
//
//            // body
//            GL11.glPushMatrix();
//            GL11.glScalef(1.0F / f6 * 0.7F, 1.0F / f6, 1.0F / f6 * 0.7F);
//            GL11.glTranslatef(0.0F, (f6 - 1.0F) * 1.5F, 0.0F);
//            this.body.render(f5);
//            GL11.glPopMatrix();
//
//            // hip
//            GL11.glPushMatrix();
//            GL11.glScalef(1.0F / f6 * 0.75F, 1.0F / f6, 1.0F / f6 * 0.75F * (1.0F + 0.005F * (float) p));
//            if (this.isSneak) {
//                GL11.glTranslatef(0.0F, (f6 - 1.0F) * 1.5F, 0.0F);
//            } else {
//                GL11.glTranslatef(0.0F, (f6 - 1.0F) * 1.5F, -0.02F - 5.0E-4F * (float) p);
//            }
//            this.hip.render(f5);
//            GL11.glPopMatrix();
//
//            // waist
//            GL11.glPushMatrix();
//            GL11.glScalef(1.0F / f6 * 0.65F, 1.0F / f6, 1.0F / f6 * 0.65F * (1.0F + 0.001F * (float) p));
//            if (this.isSneak) {
//                GL11.glTranslatef(0.0F, (f6 - 1.0F) * 1.5F, 0.0F);
//            } else {
//                GL11.glTranslatef(0.0F, (f6 - 1.0F) * 1.5F, -0.04F - 1.0E-4F * (float) p);
//            }
//            this.waist.render(f5);
//            GL11.glPopMatrix();
//
//            // bottom
//            GL11.glPushMatrix();
//            GL11.glScalef(1.0F / f6 * 0.85F, 1.0F / f6, 1.0F / f6 * 0.85F * (1.0F + 0.005F * (float) p));
//            GL11.glTranslatef(0.0F, (f6 - 1.0F) * 1.5F, 0.0F - 5.0E-4F * (float) p);
//            this.bottom.render(f5);
//            GL11.glPopMatrix();
//
//            // Bbreast2
//            GL11.glPushMatrix();
//            GL11.glScalef(1.0F / f6 * 0.675F - 0.001F, 1.0F / f6, 1.0F / f6 * 0.8F - 0.001F);
//            GL11.glTranslatef(0.0F, (f6 - 1.0F) * 1.5F + 0.001F + bbY, 0.015F + bt);
//            GL11.glScalef(1.0F, bsY, bs);
//            this.Bbreast2.render(f5);
//            GL11.glPopMatrix();
//
//            GL11.glPopMatrix(); // end BODY & BREAST & TORSO GROUP
//        }
//
//    }
//
//    @Unique
//    private void customNPC_DBC_Addon$copyScale(ModelScaleRenderer scaleRenderer) {
//        ModelScalePart config = ((IScaleRenderer) scaleRenderer).config();
//        GL11.glTranslatef(scaleRenderer.x, scaleRenderer.y, scaleRenderer.z);
//        if (config != null) {
//            GL11.glScalef(config.scaleX, config.scaleY, config.scaleZ);
//        }
//
//    }
//
//    @Unique
//    private void customNPC_DBC_Addon$copyNpcRotationAngles(ModelScaleRenderer renderer, ModelRenderer dbc) {
//        if (renderer == null)
//            return;
//        dbc.rotateAngleX = renderer.rotateAngleX;
//        dbc.rotateAngleY = renderer.rotateAngleY;
//        dbc.rotateAngleZ = renderer.rotateAngleZ;
//        dbc.rotationPointX = renderer.rotationPointX;
//        dbc.rotationPointY = renderer.rotationPointY;
//        dbc.rotationPointZ = renderer.rotationPointZ;
//        GL11.glTranslatef(renderer.x, renderer.y, renderer.z);
//    }
//    @Unique
//    private void customNPC_DBC_Addon$transRot(float f5, ModelRenderer m) {
//        if (m == null)
//            return;
//        GL11.glTranslatef(m.rotationPointX * f5, m.rotationPointY * f5, m.rotationPointZ * f5);
//        if (m.rotateAngleZ != 0.0F) {
//            GL11.glRotatef(m.rotateAngleZ * (180.0F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
//        }
//
//        if (m.rotateAngleY != 0.0F) {
//            GL11.glRotatef(m.rotateAngleY * (180.0F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
//        }
//
//        if (m.rotateAngleX != 0.0F) {
//            GL11.glRotatef(m.rotateAngleX * (180.0F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
//        }
//    }


//    @Redirect(method = "renderBody", at = @At(value = "INVOKE", target="Lorg/lwjgl/opengl/GL11;glPushMatrix()V", ordinal =8), remap = false)
//    private void femaleMainBodyPushMatrix() {
//        GL11.glPushMatrix();
//        GL11.glPushMatrix();
//
//        if (Entity instanceof EntityCustomNpc) {
//            NPCRendererHelper.getMainModel(this.renderEntity);
//        }
//
//    }
//
//    @Redirect(method = "renderBody", at = @At(value = "INVOKE", target="Lorg/lwjgl/opengl/GL11;glPushMatrix()V", ordinal =13), remap = false)
//    private void femaleMainBodyPopMatrix() {
//        GL11.glPopMatrix();
//        GL11.glPopMatrix();
//
//    }

}
