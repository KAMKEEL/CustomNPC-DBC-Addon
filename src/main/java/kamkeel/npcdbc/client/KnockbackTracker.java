package kamkeel.npcdbc.client;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

/**
 * Preserves external velocity (knockback, explosions, fishing hooks, etc.)
 * through DBC's turbo (DashKi) and flight (FloatKi), which overwrite
 * motionX/Z every tick.
 * <p>
 * How it works:
 * 1. Before DBC runs: isolate external velocity by subtracting DBC's
 * expected decayed output (lastDBCOutput * friction) from current motion.
 * 2. After DBC runs: save DBC's new output, then add external velocity back.
 * 3. Vanilla friction naturally decays the external velocity each tick.
 * <p>
 * No packet interception needed - handles ALL external velocity sources.
 */
public class KnockbackTracker {

    /**
     * DBC's own output from last tick (before external was added back)
     */
    private static double dbcOutputX, dbcOutputZ;

    /**
     * Computed external velocity for this tick
     */
    private static double externalX, externalZ;

    /**
     * Motion saved at HEAD before DBC modifies it
     */
    private static double savedMotionX, savedMotionZ;

    /**
     * Tick when DBC movement was last active (HEAD fired) — used for gap detection
     */
    private static long lastActiveTick = -2;

    /**
     * Tick when beforeDBCMovement last ran (to detect unpaired calls)
     */
    private static long beforeTick = -1;

    private static final double MIN_THRESHOLD = 0.001;
    private static final double MAX_EXTERNAL = 3.0;

    /**
     * Call at HEAD of DashKi/FloatKi before DBC overwrites motion.
     * Isolates external velocity by subtracting DBC's expected contribution.
     */
    public static void beforeDBCMovement(EntityPlayer player) {
        if (player == null || !ClientCache.turboKnockbackFix) return;

        savedMotionX = player.motionX;
        savedMotionZ = player.motionZ;

        long currentTick = player.worldObj.getTotalWorldTime();
        beforeTick = currentTick;

        if (currentTick - lastActiveTick > 1) {
            // Gap in DBC movement — no valid prior state
            externalX = 0;
            externalZ = 0;
        } else {
            float friction = getHorizontalFriction(player);

            // external = current motion - what DBC's output decayed to via friction
            externalX = player.motionX - dbcOutputX * friction;
            externalZ = player.motionZ - dbcOutputZ * friction;

            if (Math.abs(externalX) < MIN_THRESHOLD) externalX = 0;
            if (Math.abs(externalZ) < MIN_THRESHOLD) externalZ = 0;

            // Cap for teleport/position-reset artifacts
            double mag = Math.sqrt(externalX * externalX + externalZ * externalZ);
            if (mag > MAX_EXTERNAL) {
                externalX = 0;
                externalZ = 0;
            }
        }

        lastActiveTick = currentTick;
    }

    /**
     * Call at TAIL of DashKi/FloatKi after DBC has set its motion values.
     * Saves DBC's output and restores external velocity on top.
     */
    public static void afterDBCMovement(EntityPlayer player) {
        if (player == null || !ClientCache.turboKnockbackFix) return;

        long currentTick = player.worldObj.getTotalWorldTime();

        // If beforeDBCMovement wasn't called this tick (e.g. flight toggled on
        // mid-method), treat as first tick — save DBC output, skip external
        if (beforeTick != currentTick) {
            dbcOutputX = player.motionX;
            dbcOutputZ = player.motionZ;
            return;
        }

        // DBC didn't actually modify motion (conditions inside method not met)
        if (player.motionX == savedMotionX && player.motionZ == savedMotionZ) {
            return;
        }

        // Save what DBC calculated (before we add external)
        dbcOutputX = player.motionX;
        dbcOutputZ = player.motionZ;

        // Restore external velocity on top of DBC's movement
        player.motionX += externalX;
        player.motionZ += externalZ;
    }

    /**
     * Horizontal friction factor from vanilla's moveEntityWithHeading.
     * Air: 0.91, Ground: blockSlipperiness * 0.91 (0.546 for normal blocks).
     */
    private static float getHorizontalFriction(EntityPlayer player) {
        if (!player.onGround) return 0.91F;

        int x = MathHelper.floor_double(player.posX);
        int y = MathHelper.floor_double(player.boundingBox.minY) - 1;
        int z = MathHelper.floor_double(player.posZ);
        Block block = player.worldObj.getBlock(x, y, z);
        return Math.max(block.slipperiness * 0.91F, 0.01F);
    }

    public static void clear() {
        dbcOutputX = dbcOutputZ = 0;
        externalX = externalZ = 0;
        savedMotionX = savedMotionZ = 0;
        lastActiveTick = -2;
        beforeTick = -1;
    }
}
