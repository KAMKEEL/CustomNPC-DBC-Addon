package kamkeel.npcdbc.mixin.impl;

import JinRyuu.JRMCore.JRMCoreGuiBars;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.JRMCoreHDBC;
import JinRyuu.JRMCore.client.config.jrmc.JGConfigClientSettings;
import kamkeel.npcdbc.skills.Transform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = JRMCoreGuiBars.class, remap = false)
public class MixinJRMCoreGuiBars extends Gui {
    @Shadow
    Minecraft mc;

    /**
     * For transformation rage bar
     * @author
     * @reason
     */
    @Overwrite
    public void renderRageBar() {
        ResourceLocation tx = new ResourceLocation("jinryuumodscore:icons.png");
        this.mc.renderEngine.bindTexture(tx);
        short stt = JRMCoreH.TransSaiTre[JRMCoreH.State];
        if (stt <= 4) {
            int var51 = JRMCoreHDBC.DBCgetConfigcwfb() + 10;
            int var61 = JRMCoreHDBC.DBCgetConfigchfb() + 20;
            short var21 = JGConfigClientSettings.get_hud_x();
            float maxperc = (float) var21 * 1.0F / 100.0F;
            float var22 = maxperc * (float) (Transform.rage > 0 ? Transform.rage : JRMCoreH.TransSaiCurRg);
            if (var22 > (float) var21) {
                var22 = (float) var21;
            }

            if (var22 > 0.0F) {
                GL11.glPushMatrix();
                GL11.glEnable(3042);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glBlendFunc(775, 769);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.drawTexturedModalRect(var51 + JGConfigClientSettings.CLIENT_hud0x + (JGConfigClientSettings.CLIENT_hud0 > 1 ? 51 : 0), var61 - 10 + JGConfigClientSettings.CLIENT_hud0y + (JGConfigClientSettings.CLIENT_hud0 > 1 ? 5 : 0), 0, 141, (int) var22, 2);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(3042);
                GL11.glPopMatrix();
            }
        }

    }
}
