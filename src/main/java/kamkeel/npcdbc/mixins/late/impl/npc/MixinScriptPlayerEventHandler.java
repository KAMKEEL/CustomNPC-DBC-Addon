package kamkeel.npcdbc.mixins.late.impl.npc;

import com.llamalad7.mixinextras.sugar.Local;
import kamkeel.npcdbc.data.DBCDamageCalc;
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
    private DBCDamageCalc attackedEventDamage;

    @Unique
    private DBCDamageCalc attackEventDamage;

    @Redirect(method = "invoke(Lnet/minecraftforge/event/entity/living/LivingAttackEvent;)V", at = @At(value = "FIELD", target = "Lnet/minecraftforge/event/entity/living/LivingAttackEvent;ammount:F", opcode = Opcodes.GETFIELD, remap = true, ordinal = 0))
    public float attackedEvent(LivingAttackEvent instance) {
        if (instance.source.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) instance.source.getEntity();
            DBCData data = DBCData.get(player);
            if (dbcAltered = data.Powertype == 1) {
                float attackStat = DBCUtils.calculateAttackStat(player, instance.ammount, instance.source);
                if (instance.entityLiving instanceof EntityPlayer){
                    this.attackedEventDamage =  DBCUtils.calculateDBCDamageFromSource(instance.entityLiving, attackStat, instance.source);
                    return attackedEventDamage.getDamage();
                }
                else
                    return attackStat;
            }
        }
        return instance.ammount;
    }

    @Redirect(method = "invoke(Lnet/minecraftforge/event/entity/living/LivingAttackEvent;)V", at = @At(value = "FIELD", target = "Lnet/minecraftforge/event/entity/living/LivingAttackEvent;ammount:F", opcode = Opcodes.GETFIELD, remap = true, ordinal = 1))
    public float attackEvent(LivingAttackEvent instance) {
        if (instance.source.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) instance.source.getEntity();
            DBCData data = DBCData.get(player);
            if (dbcAltered = data.Powertype == 1) {
                float attackStat = DBCUtils.calculateAttackStat(player, instance.ammount, instance.source);
                if (instance.entityLiving instanceof EntityPlayer){
                    attackEventDamage = DBCUtils.calculateDBCDamageFromSource(instance.entityLiving, attackStat, instance.source);
                    return attackEventDamage.getDamage();
                }
                else
                    return attackStat;
            }
        }
        return instance.ammount;
    }

    @Inject(method = "invoke(Lnet/minecraftforge/event/entity/living/LivingAttackEvent;)V", at = @At(value = "INVOKE", target = "Lnoppes/npcs/EventHooks;onPlayerAttacked(Lnoppes/npcs/controllers/data/PlayerDataScript;Lnoppes/npcs/scripted/event/PlayerEvent$AttackedEvent;)Z", shift = At.Shift.AFTER))
    public void fixPlayerAttackedEvent(LivingAttackEvent event, CallbackInfo ci, @Local(name = "pevent") PlayerEvent.AttackedEvent ev) {
        if (dbcAltered && attackedEventDamage != null && !event.isCanceled()) {
            if(attackedEventDamage.getDamage() != ev.getDamage())
                DBCUtils.scriptingLastSetDamage = (int) ev.getDamage();
            //attackedEventDamage.processExtras();
        }

        dbcAltered = false;
        attackedEventDamage = null;
    }

    @Inject(method = "invoke(Lnet/minecraftforge/event/entity/living/LivingAttackEvent;)V", at = @At(value = "INVOKE", target = "Lnoppes/npcs/EventHooks;onPlayerAttack(Lnoppes/npcs/controllers/data/PlayerDataScript;Lnoppes/npcs/scripted/event/PlayerEvent$AttackEvent;)Z", shift = At.Shift.AFTER))
    public void fixPlayerAttackEvent(LivingAttackEvent event, CallbackInfo ci, @Local(name = "pevent1") PlayerEvent.AttackEvent ev) {
        if (dbcAltered && attackEventDamage != null && attackEventDamage.getDamage() != ev.getDamage() && !event.isCanceled()) {
            if (event.entityLiving instanceof EntityNPCInterface && DBCUtils.npcLastSetDamage == null)
                DBCUtils.npcLastSetDamage = (int) ev.getDamage();
            else if (event.entityLiving instanceof EntityPlayer && DBCUtils.scriptingLastSetDamage == null)
                DBCUtils.scriptingLastSetDamage = (int) ev.getDamage();
        }

        dbcAltered = false;
        attackEventDamage = null;
    }
}
