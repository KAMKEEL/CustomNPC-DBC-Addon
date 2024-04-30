package kamkeel.npcdbc.mixin.impl.dbc.recolor;

import JinRyuu.JRMCore.JRMCoreGuiScreen;
import kamkeel.npcdbc.client.ColorMode;
import kamkeel.npcdbc.config.ConfigDBCClient;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = JRMCoreGuiScreen.class, remap = false)

public class MixinJRMCoreGuiScreenColor extends GuiScreen  {

    @Redirect(method = "drawScreen", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I"), remap = true)
    private int drawScreenFix(FontRenderer instance, String text, int x, int y, int color){
        if(ConfigDBCClient.EnhancedGui){
            return instance.drawString(ColorMode.skimColors(text), x, y, ColorMode.textColor(), true);
        }
        return instance.drawString(text, x, y, color);
    }

    @Redirect(method = "drawHUD_helpgmodeselect", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I", remap = true))
    private int HelpSelect(FontRenderer instance, String text, int x, int y, int color){
        if(ConfigDBCClient.EnhancedGui){
            return instance.drawString(ColorMode.skimColors(text), x, y, ColorMode.textColor(), true);
        }
        return instance.drawString(text, x, y, color);
    }

    @Redirect(method = "drawHUD_instantTransmissionPicker", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I", remap = true))
    private int instantTrans(FontRenderer instance, String text, int x, int y, int color){
        if(ConfigDBCClient.EnhancedGui){
            return instance.drawString(ColorMode.skimColors(text), x, y, ColorMode.textColor(), true);
        }
        return instance.drawString(text, x, y, color);
    }

    @Redirect(method = "drawHUD_clntsett", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I", remap = true))
    private int drawHUD_clntsett(FontRenderer instance, String text, int x, int y, int color){
        if(ConfigDBCClient.EnhancedGui){
            return instance.drawString(ColorMode.skimColors(text), x, y, ColorMode.textColor(), true);
        }
        return instance.drawString(text, x, y, color);
    }

    @Redirect(method = "drawDetails(Ljava/lang/String;Ljava/lang/String;IIIILnet/minecraft/client/gui/FontRenderer;)V", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I", remap = true))
    private static int drawDetails(FontRenderer instance, String text, int x, int y, int color){
        if(ConfigDBCClient.EnhancedGui){
            return instance.drawString(ColorMode.skimColors(text), x, y, ColorMode.textColor(), true);
        }
        return instance.drawString(text, x, y, color);
    }

    @Redirect(method = "current", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I", remap = true))
    private int current(FontRenderer instance, String text, int x, int y, int color){
        if(ConfigDBCClient.EnhancedGui){
            return instance.drawString(ColorMode.skimColors(text), x, y, ColorMode.textColor(), true);
        }
        return instance.drawString(text, x, y, color);
    }

    @Inject(method = "textLevel", at= @At(value = "HEAD"), cancellable = true)
    private void textLevel(int lvl, CallbackInfoReturnable<String> cir){
        if(ConfigDBCClient.EnhancedGui){
            if(ConfigDBCClient.DarkMode){
                cir.setReturnValue("§7(lvl: " + lvl + ")");
            }
        }
    }
}
