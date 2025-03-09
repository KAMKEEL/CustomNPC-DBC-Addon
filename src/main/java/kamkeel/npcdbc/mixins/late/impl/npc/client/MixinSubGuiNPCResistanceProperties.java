package kamkeel.npcdbc.mixins.late.impl.npc.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import kamkeel.npcdbc.mixins.late.IKiResistance;
import noppes.npcs.Resistances;
import noppes.npcs.client.gui.SubGuiNpcResistanceProperties;
import noppes.npcs.client.gui.util.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SubGuiNpcResistanceProperties.class, remap = false)
public abstract class MixinSubGuiNPCResistanceProperties extends SubGuiInterface implements ISliderListener {

    @Shadow
    private Resistances resistances;

    @Inject(method = "initGui", at = @At(value = "INVOKE", target = "Lnoppes/npcs/client/gui/SubGuiNpcResistanceProperties;getSlider(I)Lnoppes/npcs/client/gui/util/GuiNpcSlider;", shift = At.Shift.AFTER, ordinal = 2, remap = false))
    public void addKiResistanceSlider(CallbackInfo ci, @Local(name = "y") LocalIntRef y) {
        int yVal = y.get();

        yVal += 22;

        IKiResistance kiResistance = (IKiResistance) resistances;

        addLabel(new GuiNpcLabel(50,"stats.ki", guiLeft + 4, yVal + 5));
        addSlider(new GuiNpcSlider(this, 50, guiLeft + 94, yVal, (int)(kiResistance.getKiResistance() * 100 - 100)  + "%", kiResistance.getKiResistance() / 2));
        getSlider(50).enabled = !resistances.disableDamage;

        y.set(yVal);
    }

    @Inject(method = "mouseReleased", at = @At("TAIL"))
    public void mouseReleased(GuiNpcSlider slider, CallbackInfo ci) {
        IKiResistance kiResistance = (IKiResistance) resistances;
        if(slider.id == 50){
            kiResistance.setKiResistance(slider.sliderValue * 2);
        }
    }
}
