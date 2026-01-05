package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.JRMCorePacHanS;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import kamkeel.npcdbc.constants.DBCAbilities;
import kamkeel.npcdbc.controllers.AbilityController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.ability.AddonAbility;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.api.entity.IPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JRMCorePacHanS.class, remap = false)
public abstract class MixinJRMCorePacHanS {
    @Inject(method = "handleStats3", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setString(Ljava/lang/String;Ljava/lang/String;)V", ordinal = 0, remap = true, shift = At.Shift.BEFORE), cancellable = true, remap = false)
    private void injectDBCAbilities(byte b, byte b2, byte b3, EntityPlayer p, CallbackInfo ci, @Local(name = "tpCost") LocalIntRef tpCost, @Local(name = "currentTP") int currentTP) {
        if (b != 1)
            return;

        PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(p);
        info.updateClient();
    }

    @Inject(method = "handleStats3", at = @At(value = "INVOKE", target = "Ljava/lang/String;substring(II)Ljava/lang/String;", ordinal = 0), cancellable = true, remap = false)
    private void injectSkillUpgradeEvent(byte b, byte b2, byte b3, EntityPlayer p, CallbackInfo ci, @Local(name = "skillLvl") int skillLvl, @Local(name = "tpCost") LocalIntRef tpCost, @Local(name = "currentTP") int currentTP) {
        if (b != 3)
            return;

        PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(p);
        info.updateClient();
    }
}
