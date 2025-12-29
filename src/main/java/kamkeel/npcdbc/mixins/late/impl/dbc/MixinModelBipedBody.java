package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.entity.ModelBipedBody;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ModelBipedBody.class, remap = false)
public abstract class MixinModelBipedBody extends ModelBiped {

    @Shadow
    public Entity Entity;
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
    public ModelRenderer breast;

    @Shadow
    public ModelRenderer breast2;

    @Shadow
    public ModelRenderer Bbreast;

    @Shadow
    public ModelRenderer hip;

    @Shadow
    public ModelRenderer waist;

    @Shadow
    public ModelRenderer bottom;

    @Shadow
    public ModelRenderer Bbreast2;

    @Shadow
    public float rot1;

    @Shadow
    public float rot2;

    /**
     * @author
     * @reason
     */
  //  @Overwrite
    public void render2Body(float par7) {
        int g =1;
        float f =1;
        int p = 0;

        if (g <= 1) {
            if (this.isChild) {
                float var8 = 2.0F;
                GL11.glPushMatrix();
                GL11.glScalef(1.5F / var8, 1.5F / var8, 1.5F / var8);
                GL11.glTranslatef(0.0F, 16.0F * par7, 0.0F);
                this.bipedHead.render(par7);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef(1.0F / var8, 1.0F / var8, 1.0F / var8);
                GL11.glTranslatef(0.0F, 24.0F * par7, 0.0F);
                this.bipedBody.render(par7);
                this.bipedRightArm.render(par7);
                this.bipedLeftArm.render(par7);
                this.bipedRightLeg.render(par7);
                this.bipedLeftLeg.render(par7);
                GL11.glPopMatrix();
            } else {
                float f6 = f;
                GL11.glPushMatrix();
                GL11.glScalef(0.5F + 0.5F / f6, 0.5F + 0.5F / f6, 0.5F + 0.5F / f6);
                GL11.glTranslatef(0.0F, (f6 - 1.0F) / f6 * (2.0F - (f6 >= 1.5F && f6 <= 2.0F ? (2.0F - f6) / 2.5F : (f6 < 1.5F && f6 >= 1.0F ? (f6 * 2.0F - 2.0F) * 0.2F : 0.0F))), 0.0F);
                this.bipedHead.render(par7);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
                GL11.glTranslatef(0.0F, (f6 - 1.0F) * 1.5F, 0.0F);
                this.bipedBody.render(par7);
                this.bipedRightArm.render(par7);
                this.bipedLeftArm.render(par7);
                this.bipedRightLeg.render(par7);
                this.bipedLeftLeg.render(par7);
                GL11.glPopMatrix();
            }
        } else {
            float f6 = f;
            GL11.glPushMatrix();
            GL11.glScalef((0.5F + 0.5F / f6) * (g <= 1 ? 1.0F : 0.85F), 0.5F + 0.5F / f6, (0.5F + 0.5F / f6) * (g <= 1 ? 1.0F : 0.85F));
            GL11.glTranslatef(0.0F, (f6 - 1.0F) / f6 * (2.0F - (f6 >= 1.5F && f6 <= 2.0F ? (2.0F - f6) / 2.5F : (f6 < 1.5F && f6 >= 1.0F ? (f6 * 2.0F - 2.0F) * 0.2F : 0.0F))), 0.0F);
            this.bipedHead.render(par7);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / f6 * (g <= 1 ? 1.0F : 0.7F), 1.0F / f6, 1.0F / f6 * (g <= 1 ? 1.0F : 0.7F));
            GL11.glTranslatef(0.0F, (f6 - 1.0F) * 1.5F, 0.0F);
            this.Brightarm.render(par7);
            this.Bleftarm.render(par7);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / f6 * (g <= 1 ? 1.0F : 0.85F), 1.0F / f6, 1.0F / f6 * (g <= 1 ? 1.0F : 0.775F));
            if (this.isSneak) {
                GL11.glTranslatef(-0.015F, (f6 - 1.0F) * 1.5F, -0.0F);
            } else {
                GL11.glTranslatef(-0.015F, (f6 - 1.0F) * 1.5F, -0.015F);
            }

            this.rightleg.render(par7);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / f6 * (g <= 1 ? 1.0F : 0.85F), 1.0F / f6, 1.0F / f6 * (g <= 1 ? 1.0F : 0.775F));
            if (this.isSneak) {
                GL11.glTranslatef(0.015F, (f6 - 1.0F) * 1.5F, -0.0F);
            } else {
                GL11.glTranslatef(0.015F, (f6 - 1.0F) * 1.5F, -0.015F);
            }

            this.leftleg.render(par7);
            GL11.glPopMatrix();


            GL11.glPushMatrix();
            GL11.glScalef(1.0F / f6 * (g <= 1 ? 1.0F : 0.675F), 1.0F / f6, 1.0F / f6 * (g <= 1 ? 1.0F : 0.8F));
            int b = 6;
            String[] s = JRMCoreH.data(this.Entity.getCommandSenderName(), 1, "0;0;0;0;0;0;0;0;0").split(";");
            String dns = s[1];
            b = JRMCoreH.dnsBreast(dns);
            float scale = (float) b * 0.03F;
            float br = 0.4235988F + scale;
            float bs = 0.8F + scale;
            float bsY = 0.85F + scale * 0.5F;
            float bt = 0.1F * scale;
            boolean bounce = this.Entity.onGround || this.Entity.isInWater();
            float bspeed = this.Entity.isSprinting() ? 1.5F : (this.Entity.isSneaking() ? 0.5F : 1.0F);
            float bbY = (bounce ? MathHelper.sin(this.rot1 * 0.6662F * bspeed * 1.5F + (float) Math.PI) * this.rot2 * 0.03F : 0.0F) * (float) b * 0.1119F;
            GL11.glTranslatef(0.0F, (f6 - 1.0F) * 1.5F + bbY, 0.015F + bt);
            GL11.glScalef(1.0F, bsY, bs);
            this.setRotation(this.breast, -br, 0.0F, 0.0F);
            this.setRotation(this.breast2, br, 3.141593F, 0.0F);
            if (bounce) {
                ModelRenderer var10000 = this.breast;
                var10000.rotateAngleX += -MathHelper.cos(this.rot1 * 0.6662F * bspeed + (float) Math.PI) * this.rot2 * 0.05F * (float) b * 0.1119F;
                var10000 = this.breast;
                var10000.rotateAngleY += MathHelper.cos(this.rot1 * 0.6662F * bspeed + (float) Math.PI) * this.rot2 * 0.02F * (float) b * 0.1119F;
                var10000 = this.breast2;
                var10000.rotateAngleX += MathHelper.cos(this.rot1 * 0.6662F * bspeed + (float) Math.PI) * this.rot2 * 0.05F * (float) b * 0.1119F;
                var10000 = this.breast2;
                var10000.rotateAngleY += MathHelper.cos(this.rot1 * 0.6662F * bspeed + (float) Math.PI) * this.rot2 * 0.02F * (float) b * 0.1119F;
            }

            this.Bbreast.render(par7); // BODY
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / f6 * (g <= 1 ? 1.0F : 0.7F), 1.0F / f6, 1.0F / f6 * (g <= 1 ? 1.0F : 0.7F));
            GL11.glTranslatef(0.0F, (f6 - 1.0F) * 1.5F, 0.0F);
            this.body.render(par7);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / f6 * (g <= 1 ? 1.0F : 0.75F), 1.0F / f6, 1.0F / f6 * (g <= 1 ? 1.0F : 0.75F) * (1.0F + 0.005F * (float) p));
            if (this.isSneak) {
                GL11.glTranslatef(0.0F, (f6 - 1.0F) * 1.5F, 0.0F);
            } else {
                GL11.glTranslatef(0.0F, (f6 - 1.0F) * 1.5F, -0.02F - 5.0E-4F * (float) p);
            }

            this.hip.render(par7); // BODY
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / f6 * (g <= 1 ? 1.0F : 0.65F), 1.0F / f6, 1.0F / f6 * (g <= 1 ? 1.0F : 0.65F) * (1.0F + 0.001F * (float) p));
            if (this.isSneak) {
                GL11.glTranslatef(0.0F, (f6 - 1.0F) * 1.5F, 0.0F);
            } else {
                GL11.glTranslatef(0.0F, (f6 - 1.0F) * 1.5F, -0.04F - 1.0E-4F * (float) p);
            }

            this.waist.render(par7); // BODY
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / f6 * (g <= 1 ? 1.0F : 0.85F), 1.0F / f6, 1.0F / f6 * (g <= 1 ? 1.0F : 0.85F) * (1.0F + 0.005F * (float) p));
            GL11.glTranslatef(0.0F, (f6 - 1.0F) * 1.5F, 0.0F - 5.0E-4F * (float) p);
            this.bottom.render(par7); // BODY
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / f6 * (g <= 1 ? 1.0F : 0.675F) - 0.001F, 1.0F / f6, 1.0F / f6 * (g <= 1 ? 1.0F : 0.8F) - 0.001F);
            GL11.glTranslatef(0.0F, (f6 - 1.0F) * 1.5F + 0.001F + bbY, 0.015F + bt);
            GL11.glScalef(1.0F, bsY, bs);
            this.Bbreast2.render(par7); // BODY
            GL11.glPopMatrix();
        }
    }

    @Shadow
    protected abstract void setRotation(ModelRenderer model, float x, float y, float z);
}
