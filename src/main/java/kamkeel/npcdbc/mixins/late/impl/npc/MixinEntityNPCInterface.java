package kamkeel.npcdbc.mixins.late.impl.npc;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.util.DBCUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityNPCInterface.class)
public abstract class MixinEntityNPCInterface extends EntityCreature implements IEntityAdditionalSpawnData, ICommandSender, IRangedAttackMob, IBossDisplayData {

    private MixinEntityNPCInterface(World p_i1602_1_) {
        super(p_i1602_1_);
    }

    @Inject(method = "entityInit", at = @At("RETURN"))
    private void addWatchableObjects(CallbackInfo ci) {
        this.dataWatcher.addObject(31, Integer.valueOf(1));
    }

    @Inject(method = "attackEntityFrom", at = @At(value = "FIELD", target = "Lnoppes/npcs/entity/EntityNPCInterface;wrappedNPC:Lnoppes/npcs/api/entity/ICustomNpc;", shift = At.Shift.BEFORE))
    public void fixDamagedEventDBCDamage(DamageSource damagesource, float amount, CallbackInfoReturnable<Boolean> cir, @Local(name = "i") LocalFloatRef dam) {
        Entity attackerEntity = NoppesUtilServer.GetDamageSource(damagesource);

        if (attackerEntity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) attackerEntity;
            DBCData data = DBCData.get(player);
            if (data.Powertype == 1) {
                dam.set(DBCUtils.calculateDBCDamageFromSource(player, DBCUtils.calculateAttackStat(player, dam.get(), damagesource), damagesource));
            }
        }
    }
}
