package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.i.ExtendedPlayer;
import JinRyuu.JRMCore.server.config.dbc.JGConfigDBCGoD;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.IExtendedEntityProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ExtendedPlayer.class)
public abstract class MixinJRMCExtendedPlayer implements IExtendedEntityProperties {

    @Shadow
    @Final
    private EntityPlayer player;

    @Inject(method = "getGoDOn", at = @At("HEAD"), remap = false, cancellable = true)
    public final void setGoDAnimColor(CallbackInfoReturnable<Integer> cir){
        DBCData dbcData = DBCData.get(this.player);
        Form form = dbcData.getForm();

        if(form != null && form.mastery.destroyerEnabled && JGConfigDBCGoD.CONFIG_GOD_ENERGY_ENABLED && JGConfigDBCGoD.CONFIG_GOD_ENABLED){
            cir.setReturnValue(1);
        }
    }
}
