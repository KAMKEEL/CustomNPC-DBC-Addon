package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JRMCore.JRMCoreGuiBars;
import JinRyuu.JRMCore.client.config.jrmc.JGConfigClientSettings;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import kamkeel.npcdbc.LocalizationHelper;
import kamkeel.npcdbc.client.gui.dbc.AbstractJRMCGui;
import kamkeel.npcdbc.client.gui.dbc.JRMCoreLabel;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.statuseffect.PlayerEffect;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;


@Mixin(value = JRMCoreGuiBars.class, remap = false)
public abstract class MixinJRMCoreGuiBars extends Gui {

    @Shadow
    private Minecraft mc;

    @Inject(method = "showSE", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;displayWidth:I", shift = At.Shift.BEFORE, remap = true))
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
                String text = effect.getName();
                if(dbcData.currentEffects.containsKey(effect.getId())){
                    PlayerEffect pe = dbcData.currentEffects.get(effect.getId());
                    if(pe.getDuration() != -100)
                        text += "\nTime: " + pe.getDuration() + "s";
                    else
                        text += "\nTime: Infinite";
                }

                addHoverable(text, var51 + i.get(), var61 + j.get());
                if (var71 == 0) {
                    i.set(i.get() + 18);
                } else
                    j.set(j.get() + 18);
            }
        }
    }

    @Inject(method = "drawIcon(III)V", at = @At("HEAD"))
    private void onDrawIcon(int var51, int var61, int resourceID, CallbackInfo ci) {
        String name = LocalizationHelper.getLocalizedString(this.statusEffectFromResourceID(resourceID));
        addHoverable(name, var51, var61);
    }

    @Unique
    private void addHoverable(String name, int i, int i1) {
        if (name == null)
            return;
        if (this.mc.currentScreen instanceof AbstractJRMCGui) {
            AbstractJRMCGui gui = (AbstractJRMCGui) this.mc.currentScreen;
            gui.horribleDBCDynamicLabels.put(i, new JRMCoreLabel(null, name, i, i1, 15, 15));
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

    private String statusEffectFromResourceID(int resourceID) {

        switch (resourceID) {
            case 0:
                return "dbc.se.blocking";

            case 1:
                return "dbc.se.flying";
            case 2:
                return "dbc.se.charging";

            case 3:
                return "dbc.se.kaioken";
            case 4:
                return "dbc.se.swooping";

            case 6:
                return "dbc.se.strain";
            case 7:
                return "dbc.se.fullmoon";
            case 8:
                return "dbc.se.godpower";
            case 9:
                return "dbc.se.majin";
            case 10:
                return "dbc.se.legendary";

            case 15:
                return "dbc.se.divine";
            case 16:
                return "dbc.se.pain";
            case 17:
                return "dbc.se.ultrainstinct";
            case 18:
                return "dbc.se.transforming";
            case 19:
                return "dbc.se.mystic";
            case 20:
                return "dbc.se.ko";
            case 21:
                return "dbc.se.fusion";

            case 23:
                return "dbc.se.god";

            case 128:
                return "dbc.se.turbo";
            case 129:
                return "dbc.se.strainIn";
            case 130:
                return "dbc.se.fatigue";

            default:
                return "ResourceID " + resourceID;
        }
    }

}
