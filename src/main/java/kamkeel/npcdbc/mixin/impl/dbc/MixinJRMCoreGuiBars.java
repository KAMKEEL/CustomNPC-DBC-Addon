package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JRMCore.JRMCoreGuiBars;
import JinRyuu.JRMCore.client.config.jrmc.JGConfigClientSettings;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.statuseffect.PlayerEffect;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;


@Mixin(value = JRMCoreGuiBars.class, remap = false)
public abstract class MixinJRMCoreGuiBars extends Gui {

    @Inject(method = "showSE", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;displayWidth:I", shift = At.Shift.BEFORE))
    private void renderStatusEffectIcon(int var51, int var61, int var71, int var81, CallbackInfo ci, @Local(name = "i") LocalIntRef i, @Local(name = "j") LocalIntRef j) {
        DBCData dbcData = DBCData.getClient();
        if (dbcData == null)
            return;
        HashMap<Integer, PlayerEffect> current = dbcData.stats.getPlayerEffects();
        for (int id : current.keySet()) {
            StatusEffect effect = StatusEffectController.Instance.get(id);
            if (effect == null)
                continue;
            if (effect.icon.length() > 3) {
                drawIcon(var51 + i.get(), var61 + j.get(), effect.icon, effect.iconX, effect.iconY);
                if (var71 == 0) {
                    i.set(i.get() + 18);
                } else
                    j.set(j.get() + 18);
            }
        }
    }

    @Unique
    private void drawIcon(int x, int y, String iconDir, int iconX, int iconY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(iconDir));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float w = 100f;
        int w2 = (int) (0.16F * (100.0F - w));
        this.drawTexturedModalRect(x + 2 + (JGConfigClientSettings.CLIENT_hud0 > 1 ? 50 : 0), y + w2 + 2, iconX, iconY, 16, 16);
    }

}
