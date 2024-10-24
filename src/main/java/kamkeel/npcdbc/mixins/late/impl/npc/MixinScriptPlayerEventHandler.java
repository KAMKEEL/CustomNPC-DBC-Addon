package kamkeel.npcdbc.mixins.late.impl.npc;

import com.llamalad7.mixinextras.sugar.Local;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.util.DBCUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import noppes.npcs.ScriptPlayerEventHandler;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.scripted.event.PlayerEvent;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ScriptPlayerEventHandler.class, remap = false)
public abstract class MixinScriptPlayerEventHandler {

    @Unique
    private boolean dbcAltered;
    @Unique
    private float dam1, dam2;

    @Redirect(method = "invoke(Lnet/minecraftforge/event/entity/living/LivingAttackEvent;)V", at = @At(value = "FIELD", target = "Lnet/minecraftforge/event/entity/living/LivingAttackEvent;ammount:F", opcode = Opcodes.GETFIELD, remap = true, ordinal = 0))
    public float test(LivingAttackEvent instance) {
        if (instance.source.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) instance.source.getEntity();
            DBCData data = DBCData.get(player);
            if (dbcAltered = data.Powertype == 1) {
                dam1 = DBCUtils.calculateAttackStat(player, instance.ammount, instance.source);
                if (instance.entityLiving instanceof EntityPlayer)
                    return DBCUtils.calculateDBCDamageFromSource(instance.entityLiving, dam1, instance.source);
                else
                    return dam1;
            }
        }
        return instance.ammount;
    }

    @Redirect(method = "invoke(Lnet/minecraftforge/event/entity/living/LivingAttackEvent;)V", at = @At(value = "FIELD", target = "Lnet/minecraftforge/event/entity/living/LivingAttackEvent;ammount:F", opcode = Opcodes.GETFIELD, remap = true, ordinal = 1))
    public float test2(LivingAttackEvent instance) {
        if (instance.source.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) instance.source.getEntity();
            DBCData data = DBCData.get(player);
            if (dbcAltered = data.Powertype == 1) {
                dam2 = DBCUtils.calculateAttackStat(player, instance.ammount, instance.source);
                if (instance.entityLiving instanceof EntityPlayer)
                    return DBCUtils.calculateDBCDamageFromSource(instance.entityLiving, dam1, instance.source);
                else
                    return dam2;
            }
        }
        return instance.ammount;
    }

    @Inject(method = "invoke(Lnet/minecraftforge/event/entity/living/LivingAttackEvent;)V", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/entity/living/LivingAttackEvent;setCanceled(Z)V", shift = At.Shift.AFTER, remap = true, ordinal = 0))
    public void attackedFixDamagedEventDBCDamage(LivingAttackEvent event, CallbackInfo ci, @Local(name = "pevent") PlayerEvent.AttackedEvent ev) {
        if (dbcAltered && dam1 != ev.getDamage() && !event.isCanceled()) {
            DBCUtils.lastSetDamage2 = (int) ev.getDamage();
            dbcAltered = false;
        }
        dam1 = 0;
    }

    @Inject(method = "invoke(Lnet/minecraftforge/event/entity/living/LivingAttackEvent;)V", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/entity/living/LivingAttackEvent;setCanceled(Z)V", shift = At.Shift.AFTER, remap = true, ordinal = 1))
    public void attackFixDamagedEventDBCDamage2(LivingAttackEvent event, CallbackInfo ci, @Local(name = "pevent1") PlayerEvent.AttackEvent ev) {
        if (dbcAltered && dam2 != ev.getDamage() && !event.isCanceled()) {
            if (event.entityLiving instanceof EntityNPCInterface)
                DBCUtils.npcLastSetDamage = (int) ev.getDamage();
            else if (event.entityLiving instanceof EntityPlayer)
                DBCUtils.lastSetDamage2 = (int) ev.getDamage();
            dbcAltered = false;
        }
        dam2 = 0;
    }
}
