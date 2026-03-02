package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.entity.EntityEnergyAtt;
import JinRyuu.JRMCore.entity.EntityEnAttacks;
import kamkeel.npcdbc.network.packets.player.KiAttackColorSyncPacket;
import kamkeel.npcdbc.util.DBCColorUtil;
import kamkeel.npcs.entity.EntityEnergyBarrier;
import kamkeel.npcs.entity.EntityEnergyBarrier.ProjectileHitOutcome;
import kamkeel.npcs.entity.EntityEnergyBarrier.ProjectileHitResult;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Mixin to make DBC ki attacks interact with CNPC+ energy barriers.
 * Supports full barrier interaction: absorb, reflect, pause, and pass-through on break.
 * Injects barrier collision and pause tick checks at the start of EntityEnergyAtt.onUpdate().
 */
@Mixin(value = EntityEnergyAtt.class, remap = false)
public abstract class MixinEntityEnergyAttBarrier extends EntityEnAttacks {

    public MixinEntityEnergyAttBarrier(World world) {
        super(world);
    }

    // ==================== SHADOWS ====================

    @Shadow public abstract byte getType();
    @Shadow public abstract float getSize();
    @Shadow public boolean shooterHolds;
    @Shadow private Entity target;

    // Raw damage field — DBC uses this directly for damage dealing ((int)this.damage).
    // getDamage() applies a GoD multiplier on top, which is only for clash comparisons.
    // We use the raw field to avoid double-application of the GoD multiplier.
    @Shadow private double damage;

    // ==================== PAUSE / REFLECT STATE ====================

    @Unique private static final int BARRIER_IMPACT_PAUSE_TICKS = 10;

    @Unique private int npcdbc$barrierPauseTicks = 0;
    @Unique private boolean npcdbc$destroyOnResume = false;
    @Unique private double npcdbc$pausedMotionX;
    @Unique private double npcdbc$pausedMotionY;
    @Unique private double npcdbc$pausedMotionZ;
    @Unique private boolean npcdbc$reflected = false;

    // ==================== MAIN INJECTION ====================

    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true, remap = true)
    private void checkBarrierCollision(CallbackInfo ci) {
        if (this.worldObj.isRemote || this.isDead) return;

        // Skip shields (type 7) and explosions (type 8) — they are area effects, not projectiles
        byte type = this.getType();
        if (type == 7 || type == 8) return;

        // Grace period: let the ki attack fully spawn before checking
        if (this.ticksExisted < 2) return;

        // Tick pause state first — if paused, skip all other logic
        if (npcdbc$tickPause()) {
            ci.cancel();
            return;
        }

        // Predict next position for swept collision
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

            if (!barrier.isIncomingGenericProjectile(
                futureX, futureY, futureZ,
                this.motionX, this.motionY, this.motionZ,
                this.posX, this.posY, this.posZ,
                ownerEntityId))
            {
                continue;
            }

            // Use raw damage field — this is what DBC uses for actual damage dealing.
            // The barrier's own type multiplier is applied internally by the barrier.
            float rawDamage = (float) this.damage;
            ProjectileHitOutcome outcome = barrier.onGenericProjectileHitResolved(this, rawDamage, "dbc.ki_attack");

            if (npcdbc$handleHitOutcome(barrier, outcome, rawDamage)) {
                ci.cancel();
                return;
            }
        }
    }

    // ==================== HIT OUTCOME HANDLER ====================

    @Unique
    private boolean npcdbc$handleHitOutcome(EntityEnergyBarrier barrier, ProjectileHitOutcome outcome, float rawDamage) {
        if (outcome == null || outcome.result == ProjectileHitResult.PASS) {
            return false;
        }

        // BROKEN: barrier destroyed
        if (outcome.result == ProjectileHitResult.BROKEN) {
            if (outcome.remainingProjectileDamage <= 0.0f) {
                // Ki attack exhausted its damage breaking the barrier — pause and die
                npcdbc$beginPause(true, barrier);
                return true;
            }
            // Pass through: compute survival ratio and scale raw damage proportionally.
            // remainingProjectileDamage includes the barrier's type multiplier, so we use
            // the ratio to avoid baking the multiplier into the ki attack's raw damage.
            if (rawDamage > 0.0f) {
                float survivalRatio = outcome.remainingProjectileDamage / (rawDamage * barrier.getBarrierData().getMultiplier("dbc.ki_attack"));
                this.damage = this.damage * Math.max(0.0, Math.min(1.0, survivalRatio));
            }
            return false;
        }

        // BLOCKED
        if (outcome.shouldReflect()) {
            // Don't reflect continuous waves that the player is still holding
            if (this.shooterHolds) {
                npcdbc$beginPause(true, barrier);
                return true;
            }

            if (npcdbc$reflect(barrier, outcome.reflectStrengthPct)) {
                return true;
            }
            // Reflect failed — absorb instead
            npcdbc$beginPause(true, barrier);
            return true;
        }

        // Blocked, no reflect — pause and destroy
        npcdbc$beginPause(true, barrier);
        return true;
    }

    // ==================== REFLECTION ====================

    @Unique
    private boolean npcdbc$reflect(EntityEnergyBarrier barrier, float reflectStrengthPct) {
        if (barrier == null) return false;

        double vx = this.motionX;
        double vy = this.motionY;
        double vz = this.motionZ;
        double vLenSq = vx * vx + vy * vy + vz * vz;
        if (vLenSq < 1.0e-8) return false;

        // Get surface normal from the barrier
        double[] normal = barrier.getSurfaceNormal(this.posX, this.posY, this.posZ, vx, vy, vz);
        if (normal == null) return false;

        // Ensure normal faces against the velocity
        double dot = vx * normal[0] + vy * normal[1] + vz * normal[2];
        if (dot > 0.0) {
            normal[0] = -normal[0];
            normal[1] = -normal[1];
            normal[2] = -normal[2];
            dot = -dot;
        }

        // Reflect: R = V - 2(V.N)N
        double rx = vx - 2.0 * dot * normal[0];
        double ry = vy - 2.0 * dot * normal[1];
        double rz = vz - 2.0 * dot * normal[2];

        // Lift reflected motion to avoid ground-diving
        if (ry < -0.06) {
            double speed = Math.sqrt(rx * rx + ry * ry + rz * rz);
            ry = Math.max(0.08, Math.abs(ry) * 0.35);
            double newLen = Math.sqrt(rx * rx + ry * ry + rz * rz);
            if (newLen > 1.0e-8) {
                double scale = speed / newLen;
                rx *= scale;
                ry *= scale;
                rz *= scale;
            }
        }

        // Save reflected motion for resume after pause
        npcdbc$pausedMotionX = rx;
        npcdbc$pausedMotionY = ry;
        npcdbc$pausedMotionZ = rz;

        // Snap outside barrier surface
        float bias = Math.max(0.06f, Math.min(0.22f, this.getSize() * 0.18f + 0.03f));
        double[] snapPos = barrier.getOutsideSurfacePoint(
            this.posX, this.posY, this.posZ,
            vx, vy, vz, bias);
        this.setPosition(snapPos[0], snapPos[1], snapPos[2]);

        // Swap shootingEntity to barrier owner so reflected ki can damage original caster
        int originalOwnerId = this.shootingEntity != null ? this.shootingEntity.getEntityId() : -1;
        Entity barrierOwner = barrier.getOwnerEntity();
        if (barrierOwner != null) {
            this.shootingEntity = barrierOwner;
        }

        // If barrier is set to target the original owner, retarget the ki attack
        if (barrier.getBarrierData().isTargetOwner() && originalOwnerId >= 0) {
            Entity originalOwner = this.worldObj.getEntityByID(originalOwnerId);
            if (originalOwner != null) {
                this.target = originalOwner;
            }
        }

        // Reduce raw damage by reflect strength percentage
        float clampedStrength = Math.max(0.0f, Math.min(100.0f, reflectStrengthPct));
        this.damage = this.damage * (1.0 - clampedStrength / 100.0);
        if (this.damage < 0.0) this.damage = 0.0;

        npcdbc$reflected = true;

        // Color shift to barrier colors
        int newColor = DBCColorUtil.findClosestPaletteIndex(barrier.getInnerColor());
        int newColor2 = DBCColorUtil.findClosestPaletteIndex(barrier.getOuterColor());
        ((IEntityEnergyAttAccessor) (Object) this).npcdbc$setColor(newColor);
        ((IEntityEnergyAttAccessor) (Object) this).npcdbc$setColor2(newColor2);
        KiAttackColorSyncPacket.sendToTracking((Entity) (Object) this, newColor, newColor2);

        // Begin pause (will resume with reflected motion after 10 ticks)
        npcdbc$barrierPauseTicks = BARRIER_IMPACT_PAUSE_TICKS;
        npcdbc$destroyOnResume = false;
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
        this.velocityChanged = true;

        return true;
    }

    // ==================== PAUSE SYSTEM ====================

    @Unique
    private void npcdbc$beginPause(boolean destroyOnResume, EntityEnergyBarrier barrier) {
        if (npcdbc$barrierPauseTicks <= 0) {
            npcdbc$pausedMotionX = this.motionX;
            npcdbc$pausedMotionY = this.motionY;
            npcdbc$pausedMotionZ = this.motionZ;
        }

        npcdbc$destroyOnResume = destroyOnResume;
        npcdbc$barrierPauseTicks = BARRIER_IMPACT_PAUSE_TICKS;
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
        this.velocityChanged = true;

        // Snap outside barrier surface so the ki attack visually sits on the surface
        if (barrier != null) {
            float snapBias = Math.max(0.06f, Math.min(0.22f, this.getSize() * 0.18f + 0.03f));
            double[] snapPos = barrier.getOutsideSurfacePoint(
                this.posX, this.posY, this.posZ,
                npcdbc$pausedMotionX, npcdbc$pausedMotionY, npcdbc$pausedMotionZ,
                snapBias);
            this.setPosition(snapPos[0], snapPos[1], snapPos[2]);
        }
    }

    @Unique
    private boolean npcdbc$tickPause() {
        if (npcdbc$barrierPauseTicks <= 0) {
            return false;
        }

        // Maintain entity lifecycle that DBC's skipped onUpdate() would normally handle.
        // Entity.onUpdate() updates prevPos, increments ticksExisted, handles fire/portal.
        // We manually update the critical fields to prevent stale prevPos causing swept
        // collision issues and to keep ticksExisted incrementing.
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.ticksExisted++;

        // Zero motion every tick to prevent DBC or other systems from moving the ki attack
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
        npcdbc$barrierPauseTicks--;

        if (npcdbc$barrierPauseTicks <= 0) {
            if (npcdbc$destroyOnResume) {
                this.setDead();
            } else {
                // Resume with reflected motion
                this.motionX = npcdbc$pausedMotionX;
                this.motionY = npcdbc$pausedMotionY;
                this.motionZ = npcdbc$pausedMotionZ;
                this.velocityChanged = true;
            }
            npcdbc$destroyOnResume = false;
        }

        return true;
    }
}
