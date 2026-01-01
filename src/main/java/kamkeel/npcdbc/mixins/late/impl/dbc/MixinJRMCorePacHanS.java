package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.JRMCorePacHanS;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import kamkeel.npcdbc.constants.DBCAbilities;
import kamkeel.npcdbc.controllers.AbilityController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.ability.AddonAbility;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
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
        DBCData data = DBCData.get(p);
        PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(p);

        for (AddonAbility ability : AbilityController.Instance.addonAbilities.values()) {
            if (ability.skillId == b2) {
                info.dbcAbilityData.addAbility(ability.id);
            }
        }

        if (data.Skills.contains("KI") && data.Skills.contains("KF") && !info.dbcAbilityData.hasAbilityUnlocked(DBCAbilities.KiWeapon)) {
            info.dbcAbilityData.addAbility(DBCAbilities.KiWeapon);
        }

        info.updateClient();
    }
}
