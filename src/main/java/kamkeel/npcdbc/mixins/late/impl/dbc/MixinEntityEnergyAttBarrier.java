package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.entity.EntityEnergyAtt;
import JinRyuu.JRMCore.entity.EntityEnAttacks;
import kamkeel.npcs.entity.EntityEnergyBarrier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Mixin to make DBC ki attacks interact with CNPC+ energy barriers.
 * Injects barrier collision checks at the start of EntityEnergyAtt.onUpdate().
 */
@Mixin(value = EntityEnergyAtt.class, remap = false)
public abstract class MixinEntityEnergyAttBarrier extends EntityEnAttacks {

    public MixinEntityEnergyAttBarrier(World world) {
        super(world);
    }

    @Shadow
    public abstract double getDamage();

    @Shadow
    public abstract byte getType();

    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true)
    private void checkBarrierCollision(CallbackInfo ci) {
        if (this.worldObj.isRemote || this.isDead) return;

        // Skip shields (type 7) and explosions (type 8) — they are area effects, not projectiles
        byte type = this.getType();
        if (type == 7 || type == 8) return;

        // Grace period: let the ki attack fully spawn before checking
        if (this.ticksExisted < 2) return;

        // At HEAD of onUpdate: posX is current position, motionX is pending velocity.
        // Predict next position for swept collision (where the ki will be after this tick).
        double futureX = this.posX + this.motionX;
        double futureY = this.posY + this.motionY;
        double futureZ = this.posZ + this.motionZ;

        int ownerEntityId = this.shootingEntity != null ? this.shootingEntity.getEntityId() : -1;

        List<EntityEnergyBarrier> barriers = EntityEnergyBarrier.getActiveBarriers(this.worldObj);
        for (EntityEnergyBarrier barrier : barriers) {
            if (barrier.isDead) continue;

            // Quick distance pre-filter
            double dx = barrier.posX - this.posX;
            double dy = barrier.posY - this.posY;
            double dz = barrier.posZ - this.posZ;
            double distSq = dx * dx + dy * dy + dz * dz;
            double maxRange = barrier.getMaxExtent() + 10.0;
            if (distSq > maxRange * maxRange) continue;

            if (barrier.isIncomingGenericProjectile(
                futureX, futureY, futureZ,
                this.motionX, this.motionY, this.motionZ,
                this.posX, this.posY, this.posZ,
                ownerEntityId))
            {
                float damage = (float) this.getDamage();
                if (barrier.onGenericProjectileHit(this, damage, "dbc.ki_attack")) {
                    this.setDead();
                    ci.cancel();
                    return;
                }
            }
        }
    }
}
