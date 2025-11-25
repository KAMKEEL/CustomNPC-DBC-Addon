package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.JRMCorePacHanS;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.scripted.NpcAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JRMCorePacHanS.class, remap = false)
public abstract class MixinJRMCorePacHanS {


    @Inject(method = "handleStats3", at = @At(value = "INVOKE", target = "Ljava/lang/String;substring(II)Ljava/lang/String;", ordinal = 0), cancellable = true, remap = false)
    private void injectSkillUpgradeEvent(byte b, byte b2, byte b3, EntityPlayer p, CallbackInfo ci, @Local(name = "skillLvl") int skillLvl, @Local(name = "tpCost") LocalIntRef tpCost, @Local(name = "currentTP") int currentTP) {
        if (b != 3)
            return;

        IPlayer player = (IPlayer) NpcAPI.Instance().getIEntity(p);
        DBCPlayerEvent.SkillEvent.Upgrade event = new DBCPlayerEvent.SkillEvent.Upgrade(player, b2 == 100 ? 0 : 1, b2, tpCost.get(), skillLvl);
        if (DBCEventHooks.onSkillEvent(event)) {
            ci.cancel();
            return;
        }
        if (currentTP < event.getCost()) {
            ci.cancel();
            return;
        }
        tpCost.set(event.getCost());

    }

    @Inject(method = "handleStats3", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setString(Ljava/lang/String;Ljava/lang/String;)V", ordinal = 0, remap = true, shift = At.Shift.BEFORE), cancellable = true, remap = false)
    private void injectSkillLearnEvent(byte b, byte b2, byte b3, EntityPlayer p, CallbackInfo ci, @Local(name = "tpCost") LocalIntRef tpCost, @Local(name = "currentTP") int currentTP) {
        if (b != 1)
            return;

        IPlayer player = (IPlayer) NpcAPI.Instance().getIEntity(p);
        DBCPlayerEvent.SkillEvent.Learn event = new DBCPlayerEvent.SkillEvent.Learn(player, 1, b2, tpCost.get());
        if (DBCEventHooks.onSkillEvent(event)) {
            ci.cancel();
            return;
        }
        if (currentTP < event.getCost()) {
            ci.cancel();
            return;
        }
        tpCost.set(event.getCost());

    }

    @Inject(method = "handleStats3", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectSkillUnlearnEvent(byte b, byte b2, byte b3, EntityPlayer p, CallbackInfo ci) {
        if (b != 2)
            return;

        IPlayer player = (IPlayer) NpcAPI.Instance().getIEntity(p);
        DBCPlayerEvent.SkillEvent.Unlearn event = new DBCPlayerEvent.SkillEvent.Unlearn(player, 1, b2);
        if (DBCEventHooks.onSkillEvent(event)) {
            ci.cancel();
        }
    }

}
