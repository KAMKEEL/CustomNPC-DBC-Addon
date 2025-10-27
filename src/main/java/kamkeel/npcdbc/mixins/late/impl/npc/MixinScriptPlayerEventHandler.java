package kamkeel.npcdbc.mixins.late.impl.npc;

import kamkeel.npcdbc.data.DBCDamageCalc;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.util.DBCUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import noppes.npcs.ScriptPlayerEventHandler;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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
        EntityPlayer player = npcdbc$getAttackingPlayer(instance.source);
        if (player != null) {
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
        EntityPlayer player = npcdbc$getAttackingPlayer(instance.source);
        if (player != null) {
            DBCData data = DBCData.get(player);
            boolean isNPC = instance.entityLiving instanceof EntityNPCInterface;
            dbcAltered = data.Powertype == 1;
            if (dbcAltered && !isNPC) {
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

    @Unique
    private EntityPlayer npcdbc$getAttackingPlayer(DamageSource source) {
        if (!(source instanceof EntityDamageSource) || source.isFireDamage() || source.isMagicDamage()) {
            return null;
        }

        Entity direct = source.getEntity();
        if (direct instanceof EntityPlayer) {
            return (EntityPlayer) direct;
        }

        Entity owner = source.getSourceOfDamage();
        return owner instanceof EntityPlayer ? (EntityPlayer) owner : null;
    }

    // COMMENTED FOR NOW
    // You should not be allowed to ALTER Attack or Attacked Damage Values

//    @Inject(method = "invoke(Lnet/minecraftforge/event/entity/living/LivingAttackEvent;)V", at = @At(value = "INVOKE", target = "Lnoppes/npcs/EventHooks;onPlayerAttacked(Lnoppes/npcs/controllers/data/PlayerDataScript;Lnoppes/npcs/scripted/event/PlayerEvent$AttackedEvent;)Z", shift = At.Shift.AFTER))
//    public void fixPlayerAttackedEvent(LivingAttackEvent event, CallbackInfo ci, @Local(name = "pevent") PlayerEvent.AttackedEvent ev) {
//        if (dbcAltered && attackedEventDamage != null && !event.isCanceled()) {
//            if(attackedEventDamage.getDamage() != ev.getDamage())
//                DBCUtils.scriptingLastSetDamage = (int) ev.getDamage();
//            //attackedEventDamage.processExtras();
//        }
//
//        dbcAltered = false;
//        attackedEventDamage = null;
//    }
//
//    @Inject(method = "invoke(Lnet/minecraftforge/event/entity/living/LivingAttackEvent;)V", at = @At(value = "INVOKE", target = "Lnoppes/npcs/EventHooks;onPlayerAttack(Lnoppes/npcs/controllers/data/PlayerDataScript;Lnoppes/npcs/scripted/event/PlayerEvent$AttackEvent;)Z", shift = At.Shift.AFTER))
//    public void fixPlayerAttackEvent(LivingAttackEvent event, CallbackInfo ci, @Local(name = "pevent1") PlayerEvent.AttackEvent ev) {
//        if (dbcAltered && attackEventDamage != null && attackEventDamage.getDamage() != ev.getDamage() && !event.isCanceled()) {
//            if (event.entityLiving instanceof EntityNPCInterface && DBCUtils.npcLastSetDamage == null)
//                DBCUtils.npcLastSetDamage = (int) ev.getDamage();
//            else if (event.entityLiving instanceof EntityPlayer && DBCUtils.scriptingLastSetDamage == null)
//                DBCUtils.scriptingLastSetDamage = (int) ev.getDamage();
//        }
//
//        dbcAltered = false;
//        attackEventDamage = null;
//    }
}
