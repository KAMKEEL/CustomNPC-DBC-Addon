package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JRMCore.JRMCoreEH;
import kamkeel.npcdbc.data.PlayerCustomFormData;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JRMCoreEH.class, remap = false)
public class MixinJRMCoreEH {

    /**
     * The events are reversed in DBC, LivingAttackEvent increases the damaged's FM,
     * while LivingHurtEvent increases attacker's FM
     */
    @Inject(method = "SdajrR", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/server/config/dbc/JGConfigDBCFormMastery;FM_Enabled:Z", ordinal = 0, shift = At.Shift.BEFORE), remap = false)
    public void onFMAttackGain(LivingAttackEvent event, CallbackInfo ci) {
        PlayerCustomFormData c = Utility.getFormData((EntityPlayer) event.entity);
        c.updateCurrentFormMastery("damaged");
        //this gets method is called twice, should be once only
    }

    @Inject(method = "Sd35MR", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/server/config/dbc/JGConfigDBCFormMastery;FM_Enabled:Z", shift = At.Shift.BEFORE), remap = false)
    public void onFMDamagedGain(LivingHurtEvent event, CallbackInfo ci) {
        PlayerCustomFormData c = Utility.getFormData((EntityPlayer) event.source.getEntity());
        c.updateCurrentFormMastery("attack");
    }
}
