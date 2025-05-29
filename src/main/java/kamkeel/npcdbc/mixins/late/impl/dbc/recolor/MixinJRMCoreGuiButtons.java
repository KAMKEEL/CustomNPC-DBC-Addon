package kamkeel.npcdbc.mixins.late.impl.dbc.recolor;

import JinRyuu.JRMCore.JRMCoreGuiButtons00;
import kamkeel.npcdbc.client.ColorMode;
import kamkeel.npcdbc.config.ConfigDBCClient;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JRMCoreGuiButtons00.class, remap = false)

public abstract class MixinJRMCoreGuiButtons extends GuiButton {

    @Shadow
    public int col;

    public MixinJRMCoreGuiButtons(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, buttonText);
    }

    public MixinJRMCoreGuiButtons(int stateName, int id, int p_i1021_3_, int p_i1021_4_, int p_i1021_5_, String p_i1021_6_) {
        super(stateName, id, p_i1021_3_, p_i1021_4_, p_i1021_5_, p_i1021_6_);
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void onConstructorComplete(CallbackInfo info) {
        if (ConfigDBCClient.EnhancedGui) {
            if (col == 0)
                col = 16777215;
        }
    }

    @Redirect(method = "drawButton", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreGuiButtons00;drawCenteredString(Lnet/minecraft/client/gui/FontRenderer;Ljava/lang/String;III)V"), remap = true)
    public void changeButtonText(JRMCoreGuiButtons00 instance, FontRenderer fontRenderer, String s, int x, int y, int originalColor) {
        if (originalColor == 14737632 && ConfigDBCClient.EnhancedGui && !ConfigDBCClient.DarkMode) {
            fontRenderer.drawString(s, x - fontRenderer.getStringWidth(s) / 2, y, ColorMode.LIGHTMODE_TEXT_ALTERNATE, false);
        } else {
            if (ConfigDBCClient.EnhancedGui) {
                fontRenderer.drawString(s, x - fontRenderer.getStringWidth(s) / 2, y, originalColor, false);
            } else {
                fontRenderer.drawString(s, x - fontRenderer.getStringWidth(s) / 2, y, originalColor, true);
            }
        }
    }
}
