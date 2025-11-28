package kamkeel.npcdbc.mixins.late.impl.npc.client;

import kamkeel.npcdbc.client.gui.global.auras.SubGuiAuraDisplay;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.client.gui.select.GuiSoundSelection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GuiSoundSelection.class, remap = false)
public abstract class MixinGuiSoundSelection {

    @Redirect(method = "close", at = @At(value = "INVOKE", target = "Lnoppes/npcs/client/controllers/MusicController;stopAllSounds()V"))
    public void fixSound(MusicController instance) {
        if (SubGuiAuraDisplay.aura != null) {

        } else
            instance.stopAllSounds();
    }

    @Redirect(method = "actionPerformed", at = @At(value = "INVOKE", target = "Lnoppes/npcs/client/controllers/MusicController;stopMusic()V", remap=false), remap = true)
    public void fixSound2(MusicController instance) {
        if (SubGuiAuraDisplay.aura != null) {

        } else
            instance.stopMusic();
    }

}
