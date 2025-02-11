package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.JRMCoreGuiBars;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.JRMCoreHC;
import JinRyuu.JRMCore.client.config.jrmc.JGConfigClientSettings;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.LocalizationHelper;
import kamkeel.npcdbc.client.gui.dbc.AbstractJRMCGui;
import kamkeel.npcdbc.client.gui.dbc.JRMCoreLabel;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.ClientCacheHandler;
import noppes.npcs.client.renderer.ImageData;
import noppes.npcs.controllers.CustomEffectController;
import noppes.npcs.controllers.data.CustomEffect;
import noppes.npcs.controllers.data.EffectKey;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerEffect;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ConcurrentHashMap;


@Mixin(value = JRMCoreGuiBars.class, remap = false)
public abstract class MixinJRMCoreGuiBars extends Gui {

    @Shadow
    private Minecraft mc;

    @Redirect(method = "kiBarHelper", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreGuiBars;getSmoothReleaseLevel()I", ordinal = 0))
    private int release(JRMCoreGuiBars instance) {
        if (JRMCoreHC.smoothReleaseLevel > 100)
            return 100;
        return (int) JRMCoreHC.smoothReleaseLevel;
    }
    @Inject(method = "showSE", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;displayWidth:I", shift = At.Shift.BEFORE, remap = true))
    private void renderStatusEffectIcon(int var51, int var61, int var71, int var81, CallbackInfo ci, @Local(name = "i") LocalIntRef i, @Local(name = "j") LocalIntRef j) {
        DBCData dbcData = DBCData.getClient();
        if (dbcData == null)
            return;

        PlayerData playerData = PlayerData.get(dbcData.player);
        ConcurrentHashMap<EffectKey, PlayerEffect> current = playerData.effectData.getEffects();
        if(current == null)
            return;
        for (EffectKey key : current.keySet()) {
            CustomEffect effect = CustomEffectController.getInstance().get(key.getId(), key.getIndex());
            if (effect == null)
                continue;
            if (effect.icon.length() > 3) {
                drawIcon(var51 + i.get(), var61 + j.get(), effect);
                String text;
                if(effect instanceof StatusEffect)
                    text = StatCollector.translateToLocal(((StatusEffect) effect).getLangName());
                else
                    text = effect.getMenuName();

                text += JRMCoreH.cldgy;
                if(playerData.effectData.hasPlayerEffect(key.getId(), key.getIndex())){
                    PlayerEffect pe = playerData.effectData.getPlayerEffect(key.getId(), key.getIndex());
                    if(pe.getDuration() != -100)
                        text += "\nTime: " + pe.getDuration() + "s";
                    else
                        text += "\nTime: Infinite";

                    if(pe.level > 1){
                        text += "\nLevel: " + pe.level;
                    }
                }

                addHoverable(text, (var51 + i.get()+2), (var61 + j.get()+3));
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
        addHoverable(name, var51+2, var61+3);
    }

    @Unique
    private void addHoverable(String name, int i, int i1) {
        if (name == null)
            return;
        if (this.mc.currentScreen instanceof AbstractJRMCGui) {
            AbstractJRMCGui gui = (AbstractJRMCGui) this.mc.currentScreen;
            gui.horribleDBCDynamicLabels.put(i, new JRMCoreLabel(null, name, i, i1, 16, 16));
        }
    }

    @Unique
    private void drawIcon(int x, int y, CustomEffect effect) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float w = 100f;
        int w2 = (int) (0.16F * (100.0F - w));

        ImageData data = ClientCacheHandler.getImageData(effect.icon);

        if (data.imageLoaded()) {
            data.bindTexture();
            int iconX = effect.iconX;
            int iconY = effect.iconY;
            int iconWidth = effect.getWidth();
            int iconHeight = effect.getHeight();
            int width = data.getTotalWidth();
            int height = data.getTotalWidth();


            func_152125_a(x + 2 + (JGConfigClientSettings.CLIENT_hud0 > 1 ? 50 : 0), y + w2 + 2, iconX, iconY, iconWidth, iconHeight, 16, 16, width, height);
        } else {
            Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("customnpcs", "textures/marks/question.png"));
            func_146110_a(x + 2 + (JGConfigClientSettings.CLIENT_hud0 > 1 ? 50 : 0), y + w2 + 2, 0, 0, 16, 16, 16, 16);
        }

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(CustomNpcPlusDBC.ID + ":textures/gui/icons.png"));
        this.drawTexturedModalRect(x + 2 + (JGConfigClientSettings.CLIENT_hud0 > 1 ? 50 : 0), y + w2 + 2, 0, 240, 16, 16);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
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
