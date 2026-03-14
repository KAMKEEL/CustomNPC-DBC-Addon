package kamkeel.npcdbc.data.ability.types;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcs.controllers.data.ability.AbilityVariant;
import kamkeel.npcs.controllers.data.ability.enums.LockMode;
import kamkeel.npcs.controllers.data.ability.enums.TargetingMode;
import kamkeel.npcs.controllers.data.ability.data.energy.EnergyCombatData;
import kamkeel.npcs.controllers.data.ability.data.energy.EnergyDisplayData;
import kamkeel.npcs.controllers.data.ability.data.energy.EnergyHomingData;
import kamkeel.npcs.controllers.data.ability.data.energy.EnergyLifespanData;
import kamkeel.npcs.controllers.data.ability.data.ProjectileData;
import kamkeel.npcs.controllers.data.ability.gui.AbilityFieldDefs;
import kamkeel.npcs.controllers.data.ability.type.energy.AbilityEnergyProjectile;
import kamkeel.npcs.controllers.data.telegraph.TelegraphType;
import kamkeel.npcs.entity.EntityAbilityOrb;
import kamkeel.npcs.util.AnchorPointHelper;
import kamkeel.npcs.util.RaycastUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import noppes.npcs.api.IPos;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IEntityLivingBase;
import noppes.npcs.client.gui.builder.FieldDef;
import noppes.npcs.controllers.data.MagicData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.scripted.NpcAPI;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * OrbSurround ability: Spawns multiple orbs that fly to random positions around
 * the target, freeze in place, then converge on the target dealing damage.
 *
 * Phases:
 *   1 - POSITIONING : orbs spawn and fly one-by-one toward scattered positions
 *   2 - STALLING    : orbs stop mid-air and hang for a moment
 *   3 - FIRING      : orbs re-target and converge on the target with full damage
 *   4 - WAITING     : ability waits for all orbs to die before completing
 *
 * Parent field usage:
 *   - entities[]          : live orb references (replaces activeOrbs[])
 *   - projectileSpawned[] : tracks which orbs have been spawned (via spawnProjectileEntity)
 *   - spawnedCount        : how many orbs have been spawned so far
 *   - projectileCount     : set to projectileAmount before initRuntimeState, drives entities[] size
 *   - fireDelay           : stagger delay between orbs in phase 1
 *   - cleanup()           : kills all entities[] and resets parent state
 *   - resetForBurst()     : clears entities[]/projectileSpawned[]/spawnedCount for next burst
 */
public class AbilityOrbSurround extends AbilityEnergyProjectile<EntityAbilityOrb> {

    // ==================== CONFIGURATION ====================

    private static final int MAX_AMOUNT = 50;

    private float offsetRangeX = 8.0f;
    private float offsetRangeY = 8.0f;
    private float offsetRangeZ = 8.0f;

    private float minPositioningSpeed = 0.5f;
    private float maxPositioningSpeed = 1.0f;

    /** How many ticks to wait after the last orb has been fired before freezing. */
    private int freezeDelay = 20;

    /** How many ticks orbs hang frozen before firing at the target. */
    private int freezeTicks = 20;

    /** Ticks between each orb being re-fired in phase 3. Independent from fireDelay (phase 1). */
    private int refireDelay = 0;
    private float refireSpeed = 1.5f;

    private float orbSize = 1.5f;
    private int projectileAmount = 25;

    // ==================== RUNTIME STATE ====================

    /** Scatter target positions, computed once per cast. One per orb. */
    private Vec3[] scatterPositions;

    /** Per-orb positioning speeds, randomised per cast. */
    private float[] positioningSpeeds;

    /** Caster/target snapshot captured at cast start, used when spawning staggered orbs. */
    private EntityLivingBase initCaster;
    private EntityLivingBase initTarget;

    private int phase = 0;
    private int phaseTick = 0;

    private static final Random RNG = new Random();

    // ==================== CONSTRUCTOR ====================

    public AbilityOrbSurround() {
        super(
            new EnergyDisplayData(0xFFFFFF, 0xFFFF00, true, 0.4f, 1.5f, 10.0f),
            new EnergyCombatData(),
            new EnergyHomingData(),
            new EnergyLifespanData()
        );
        this.typeId = "ability.npcdbc.orb_surround";
        this.name = "Orb Surround";
        this.targetingMode = TargetingMode.AGGRO_TARGET;
        this.maxRange = 30.0f;
        this.minRange = 5.0f;
        this.cooldownTicks = 0;
        this.windUpTicks = 20;
        this.lockMovement = LockMode.WINDUP_AND_ACTIVE;
        this.telegraphType = TelegraphType.CIRCLE;
        this.showTelegraph = true;
        this.burstOverlap = true;
        this.windUpAnimationName = "Ability_OrbSurround_Windup";
        this.activeAnimationName = "Ability_OrbSurround_Active";

        this.defaultIconLayers = new DefaultIconLayer[]{
            new DefaultIconLayer("customnpcs:textures/gui/ability/orb.png",
                () -> isOuterColorEnabled() ? getOuterColor() : getInnerColor())
        };
    }

    // ==================== ABSTRACT IMPLEMENTATIONS ====================

    @Override
    protected EntityAbilityOrb createEntity(EntityLivingBase caster, EntityLivingBase target,
                                            Vec3 spawnPos, EnergyDisplayData resolved, int index) {
        return new EntityAbilityOrb(
            caster.worldObj, caster, target,
            spawnPos.xCoord, spawnPos.yCoord, spawnPos.zCoord, orbSize,
            resolved, combatData, homingData, lightningData, lifespanData);
    }

    @Override
    protected void fireEntity(EntityAbilityOrb orb, EntityLivingBase target) {
        // Motion is applied directly in spawnAndFireOrbToScatter / phase 3 loop.
    }

    protected EntityAbilityOrb createProjectileEntity(EntityLivingBase caster, EntityLivingBase target, int index) {
        int variantIndex = index % projectileCount;
        Vec3 spawnPos = getSpawnPosition(caster, variantIndex);
        EnergyDisplayData resolved = projectiles[variantIndex].resolveDisplay(displayData);
        EntityAbilityOrb entity = createEntity(caster, target, spawnPos, resolved, index);
        entity.setAnchorData(projectiles[variantIndex].anchor);
        entity.setEffects(this.effects);
        entity.setIgnoreIFrames(this.isIgnoreIFrames());
        entity.setSourceAbility(this);
        MagicData resolvedMagic = resolveMagicData(caster);
        if (resolvedMagic != null) {
            entity.setMagicData(resolvedMagic.copy());
        }
        return entity;
    }

    @Override
    protected void setupEntityCharging(EntityAbilityOrb orb, ProjectileData projData, int index) {
        orb.setupCharging(projData.anchor, windUpTicks);
    }

    @Override
    protected void setupEntityPreview(EntityAbilityOrb orb, EntityLivingBase caster,
                                      EnergyDisplayData resolved, ProjectileData projData, int index) {
        orb.setupPreview(caster, orbSize, resolved, lightningData, projData.anchor, windUpTicks);
    }

    @Override
    protected EntityAbilityOrb[] createEntityArray(int size) {
        return new EntityAbilityOrb[size];
    }

    /**
     * Spawn position for each orb: always at caster chest height.
     * Overrides parent so createProjectileEntity() uses this position.
     */
    @Override
    protected void initRuntimeState(EntityLivingBase caster) {
        entities = createEntityArray(projectileAmount);
        projectileSpawned = new boolean[projectileAmount];
        spawnedCount = 0;
        chargeVisualIds = new String[projectileAmount];
        chargeVisualCaster = caster;
    }

    @Override
    protected float getProjectileTelegraphRadius() {
        return orbSize * 1.5f;
    }

    protected Vec3 getSpawnPosition(EntityLivingBase caster, int index) {
        int variantIndex = index % projectileCount;
        return AnchorPointHelper.calculateAnchorPosition(caster, projectiles[variantIndex].anchor);
    }

    // ==================== PARENT LIFECYCLE OVERRIDES ====================

    @Override
    public void onExecute(EntityLivingBase caster, EntityLivingBase target) {
        // Intentionally empty — orb spawning happens in onActiveTick at tick == 0.
    }

    /**
     * Ready for burst once all phase-1 orbs have been fired.
     * The orbs continue through phases 2-4 in the world via burstOverlap.
     */
    @Override
    public boolean isReadyForBurstCompletion(int activeTick) {
        int lastPhase = isFreeOnCast() ? 3 : 4;
        boolean correctTick = refireDelay <= 0 || phaseTick >= refireDelay * (projectileAmount - 1);
        return phase == lastPhase && correctTick;
    }

    /**
     * Between burst iterations: let the parent clear entities[]/projectileSpawned[]/spawnedCount,
     * then reset our own phase state. burstOverlap=true means the parent does NOT kill old orbs.
     */
    @Override
    public void resetForBurst() {
        super.resetForBurst(); // clears entities[], projectileSpawned[], spawnedCount (no kill because burstOverlap=true)
        scatterPositions = null;
        positioningSpeeds = null;
        initCaster = null;
        initTarget = null;
        phase = 0;
        phaseTick = 0;
    }

    /**
     * Fully overrides the parent active tick to implement 4-phase surround behaviour.
     *
     * entities[] is used as the live orb registry (replaces the old activeOrbs[]).
     * Orbs are spawned one-by-one via spawnAndFireOrbToScatter, which calls the
     * parent's spawnProjectileEntity to register them in entities[]/projectileSpawned[].
     */
    @Override
    public void onActiveTick(EntityLivingBase caster, EntityLivingBase target, int tick) {

        // tick == 0: set projectileCount = projectileAmount so initRuntimeState allocates
        // entities[] at the right size, then compute scatter data.
        if (tick == 0) {
            if (!preparePhase1(caster, target)) {
                signalCompletion();
                return;
            }
        }

        if (entities == null) {
            signalCompletion();
            return;
        }

        phaseTick++;

        // ── PHASE 1: POSITIONING ──────────────────────────────────────
        // Each orb is spawned + fired toward its scatter position on its own tick.
        // freezeDelay only begins counting after the last orb has been fired.
        if (phase == 1) {
            for (int i = 0; i < projectileAmount; i++) {
                if (phaseTick != i * fireDelay + 1) continue;
                spawnAndFireOrbToScatter(i);
            }

            int lastFirePhaseTick = (projectileAmount - 1) * fireDelay + 1;
            if (phaseTick >= lastFirePhaseTick + freezeDelay) {
                phase = 2;
                phaseTick = 0;
                freezeAllOrbs();
            }
        }

        // ── PHASE 2: STALLING ─────────────────────────────────────────
        if (phase == 2) {
            boolean allDead = true;
            for (EntityAbilityOrb orb : entities) {
                if (orb != null && orb.isEntityAlive()) {
                    allDead = false;
                    break;
                }
            }
            if (allDead) {
                cleanup();
                if (!isFreeOnCast()) {
                    signalCompletion();
                    return;
                }
            }


            if (phaseTick >= freezeTicks) {
                phase = 3;
                phaseTick = 0;
            }
        }

        // ── PHASE 3: FIRING ───────────────────────────────────────────
        // convergencePos is recalculated per orb to use the target's live position.
        if (phase == 3) {
            for (int i = 0; i < entities.length; i++) {
                if (phaseTick != i * refireDelay + 1) continue;
                if (entities[i] == null || !entities[i].isEntityAlive()) continue;

                EntityLivingBase liveTarget = resolveLiveTarget(caster, target);
                Vec3 convergencePos = getCenterPos(caster, liveTarget);
                if (convergencePos == null) continue;

                entities[i].setSpeed(refireSpeed);
                entities[i].setCombatDamage(combatData.damage);
                setOrbMotionToward(entities[i], convergencePos.xCoord, convergencePos.yCoord, convergencePos.zCoord, refireSpeed);
                entities[i].sendClientSync();
            }

            int lastRefirePhaseTick = (entities.length - 1) * refireDelay + 1;
            if (phaseTick > lastRefirePhaseTick) {
                phase = 4;
                phaseTick = 0;
                if (isFreeOnCast()) {
                    signalCompletion();
                    return;
                }
            }
        }

        // ── PHASE 4: WAITING ──────────────────────────────────────────
        if (phase == 4) {

            if (entities == null) {
                cleanup();
                signalCompletion();
                return;
            }

            boolean allDead = true;
            for (EntityAbilityOrb orb : entities) {
                if (orb != null && orb.isEntityAlive()) {
                    allDead = false;
                    break;
                }
            }
            if (allDead) {
                cleanup();
                if (!isFreeOnCast()) {
                    signalCompletion();
                }
            }
        }
    }

    // ==================== PHASE HELPERS ====================

    /**
     * Sets projectileCount = projectileAmount so initRuntimeState allocates entities[]
     * at the correct size, then computes scatter positions and speeds for all orbs.
     * No orbs are spawned here — spawning is staggered across phase 1 ticks.
     */
    private boolean preparePhase1(EntityLivingBase caster, EntityLivingBase target) {
        phase = 1;
        phaseTick = 0;

        initCaster = caster;
        initTarget = resolveLiveTarget(caster, target);

        Vec3 center = getCenterPos(caster, initTarget);
        if (center == null) return false;

        initRuntimeState(caster);

        boolean onGround = isNearGround(caster.worldObj, initTarget, center)
            || isNearGround(caster.worldObj, caster, center);

        scatterPositions = new Vec3[projectileAmount];
        positioningSpeeds = new float[projectileAmount];

        for (int i = 0; i < projectileAmount; i++) {
            double ox = (RNG.nextDouble() * 2 - 1) * offsetRangeX;
            double oy = onGround
                ? RNG.nextDouble() * offsetRangeY + 2
                : (RNG.nextDouble() * 2 - 1) * offsetRangeY;
            double oz = (RNG.nextDouble() * 2 - 1) * offsetRangeZ;

            scatterPositions[i] = Vec3.createVectorHelper(
                center.xCoord + ox,
                center.yCoord + oy,
                center.zCoord + oz
            );

            positioningSpeeds[i] = minPositioningSpeed
                + RNG.nextFloat() * (maxPositioningSpeed - minPositioningSpeed);
        }

        return true;
    }

    /**
     * Spawns orb[i] using the parent's createProjectileEntity + spawnProjectileEntity,
     * then immediately sets its motion toward the pre-computed scatter position.
     * Using the parent methods ensures entities[i], projectileSpawned[i], spawnedCount
     * are all updated correctly.
     */
    private void spawnAndFireOrbToScatter(int i) {
        EntityAbilityOrb orb = createProjectileEntity(initCaster, initTarget, i);

        orb.setCombatDamage(0);
        orb.setSpeed(positioningSpeeds[i]);
        orb.setHomingEnabled(false);

        Vec3 pos = scatterPositions[i];
        setOrbMotionToward(orb, pos.xCoord, pos.yCoord, pos.zCoord, positioningSpeeds[i]);

        spawnProjectileEntity(orb, i);
    }

    /** Stops all live orbs in place at the start of phase 2. */
    private void freezeAllOrbs() {
        for (EntityAbilityOrb orb : entities) {
            if (orb == null || !orb.isEntityAlive()) continue;
            orb.motionX = 0;
            orb.motionY = 0;
            orb.motionZ = 0;
            orb.setSpeed(0);
        }
    }

    private Vec3 getCenterPos(EntityLivingBase caster, EntityLivingBase target) {
        if (target != null) {
            return Vec3.createVectorHelper(target.posX, target.posY + 1.0, target.posZ);
        }

        Vec3 pos = RaycastUtil.getLookingAtPos(caster, (int) maxRange, true, false, true);
        if (pos == null) return null;
        return pos;
    }

    private boolean isNearGround(World world, EntityLivingBase entity, Vec3 center) {
        if (entity != null) {
            int bx = (int) Math.floor(entity.posX);
            int by = (int) Math.floor(entity.posY) - 1;
            int bz = (int) Math.floor(entity.posZ);
            return world.getBlock(bx, by, bz) != null
                && !world.getBlock(bx, by, bz).isAir(world, bx, by, bz);
        }
        int bx = (int) Math.floor(center.xCoord);
        int by = (int) Math.floor(center.yCoord) - 1;
        int bz = (int) Math.floor(center.zCoord);
        return world.getBlock(bx, by, bz) != null
            && !world.getBlock(bx, by, bz).isAir(world, bx, by, bz);
    }

    private void setOrbMotionToward(EntityAbilityOrb orb, double x, double y, double z, float speed) {
        double dx = x - orb.posX;
        double dy = y - orb.posY;
        double dz = z - orb.posZ;
        double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len > 0) {
            orb.motionX = (dx / len) * speed;
            orb.motionY = (dy / len) * speed;
            orb.motionZ = (dz / len) * speed;
        }
    }

    private EntityLivingBase resolveLiveTarget(EntityLivingBase caster, EntityLivingBase fallback) {
        if (caster instanceof EntityNPCInterface) {
            return ((EntityNPCInterface) caster).getAttackTarget();
        }

        if (caster instanceof EntityPlayer) {
            Entity[] foundEntities = RaycastUtil.getLookingAtEntities(
                caster, new Entity[]{caster}, (int) maxRange, 0, 1, true, false, true);

            if (foundEntities.length > 0 && foundEntities[0] instanceof EntityLivingBase)
                return (EntityLivingBase) foundEntities[0];
        }
        return fallback;
    }

    // ==================== CLEANUP ====================

    @Override
    public void cleanup() {
        // super.cleanup() iterates entities[] and kills all live orbs, then nulls everything.
        super.cleanup();
        scatterPositions = null;
        positioningSpeeds = null;
        initCaster = null;
        initTarget = null;
        phase = 0;
        phaseTick = 0;
    }

    // ==================== VARIANTS ====================

    @Override
    public List<AbilityVariant> getVariants() {
        return Arrays.asList(
            new AbilityVariant("ability.variant.surround", a -> {
                a.setName("Orb Surround");
            }),
            new AbilityVariant("ability.variant.surround_dense", a -> {
                AbilityOrbSurround s = (AbilityOrbSurround) a;
                a.setName("Dense Orb Surround");
                s.setProjectileAmount(25);
                s.setOrbSize(1.0f);
                s.setOffsetRangeX(5.0f);
                s.setOffsetRangeY(5.0f);
                s.setOffsetRangeZ(5.0f);
                s.setFreezeTicks(15);
                s.setRefireSpeed(2.0f);
            }),
            new AbilityVariant("ability.variant.surround_wide", a -> {
                AbilityOrbSurround s = (AbilityOrbSurround) a;
                a.setName("Wide Orb Surround");
                s.setProjectileAmount(25);
                s.setOrbSize(2.0f);
                s.setOffsetRangeX(14.0f);
                s.setOffsetRangeY(10.0f);
                s.setOffsetRangeZ(14.0f);
                s.setFreezeDelay(30);
                s.setFreezeTicks(30);
                s.setRefireSpeed(1.2f);
            }),
            new AbilityVariant("ability.variant.surround_burst", a -> {
                AbilityOrbSurround s = (AbilityOrbSurround) a;
                a.setName("Burst Orb Surround");
                s.setProjectileAmount(10);
                s.setOrbSize(1.0f);
                s.setOffsetRangeX(6.0f);
                s.setOffsetRangeY(6.0f);
                s.setOffsetRangeZ(6.0f);
                s.setFreezeDelay(10);
                s.setFreezeTicks(10);
                s.setRefireSpeed(2.0f);
                a.setBurstEnabled(true);
                a.setBurstAmount(3);
                a.setBurstDelay(20);
                a.setBurstReplayAnimations(false);
                a.setBurstOverlap(true);
            })
        );
    }

    // ==================== NBT ====================

    @Override
    protected void writeTypeSpecificNBT(NBTTagCompound nbt) {
        nbt.setFloat("orbSize", orbSize);
        nbt.setFloat("offsetRangeX", offsetRangeX);
        nbt.setFloat("offsetRangeY", offsetRangeY);
        nbt.setFloat("offsetRangeZ", offsetRangeZ);
        nbt.setFloat("minPositioningSpeed", minPositioningSpeed);
        nbt.setFloat("maxPositioningSpeed", maxPositioningSpeed);
        nbt.setInteger("freezeDelay", freezeDelay);
        nbt.setInteger("freezeTicks", freezeTicks);
        nbt.setInteger("projectileAmount", projectileAmount);
        nbt.setFloat("refireSpeed", refireSpeed);
        nbt.setInteger("refireDelay", refireDelay);
    }

    @Override
    protected void readTypeSpecificNBT(NBTTagCompound nbt) {
        this.orbSize = nbt.getFloat("orbSize");
        this.offsetRangeX = nbt.getFloat("offsetRangeX");
        this.offsetRangeY = nbt.getFloat("offsetRangeY");
        this.offsetRangeZ = nbt.getFloat("offsetRangeZ");
        this.minPositioningSpeed = nbt.getFloat("minPositioningSpeed");
        this.maxPositioningSpeed = nbt.getFloat("maxPositioningSpeed");
        this.freezeDelay = nbt.getInteger("freezeDelay");
        this.freezeTicks = nbt.getInteger("freezeTicks");
        this.projectileAmount = nbt.getInteger("projectileAmount");
        this.refireSpeed = nbt.getFloat("refireSpeed");
        this.refireDelay = nbt.getInteger("refireDelay");
    }

    // ==================== GETTERS / SETTERS ====================

    public int getProjectileAmount() { return projectileAmount; }
    public void setProjectileAmount(int amount) { projectileAmount = Math.max(1, Math.min(amount, MAX_AMOUNT)); }

    public float getOrbSize() { return orbSize; }
    public void  setOrbSize(float v) { this.orbSize = v; }

    public float getOffsetRangeX() { return offsetRangeX; }
    public void  setOffsetRangeX(float v) { this.offsetRangeX = v; }

    public float getOffsetRangeY() { return offsetRangeY; }
    public void  setOffsetRangeY(float v) { this.offsetRangeY = v; }

    public float getOffsetRangeZ() { return offsetRangeZ; }
    public void  setOffsetRangeZ(float v) { this.offsetRangeZ = v; }

    public float getMinPositioningSpeed() { return minPositioningSpeed; }
    public void  setMinPositioningSpeed(float v) { this.minPositioningSpeed = v; }

    public float getMaxPositioningSpeed() { return maxPositioningSpeed; }
    public void  setMaxPositioningSpeed(float v) { this.maxPositioningSpeed = v; }

    public int   getFreezeDelay() { return freezeDelay; }
    public void  setFreezeDelay(int v) { this.freezeDelay = v; }

    public int   getFreezeTicks() { return freezeTicks; }
    public void  setFreezeTicks(int v) { this.freezeTicks = v; }

    public float getRefireSpeed() { return refireSpeed; }
    public void  setRefireSpeed(float v) { this.refireSpeed = v; }

    public int   getRefireDelay() { return refireDelay; }
    public void  setRefireDelay(int v) { this.refireDelay = Math.max(0, v); }

    // ==================== GUI ====================

    @SideOnly(Side.CLIENT)
    @Override
    protected void addTypeDefinitions(List<FieldDef> defs) {
        defs.add(FieldDef.intField("ability.uniqueOrbs", this::getProjectileCount, this::setProjectileCount));

        defs.add(FieldDef.row(
            FieldDef.intField("ability.projectileAmount", this::getProjectileAmount, this::setProjectileAmount)
                .range(1, MAX_AMOUNT),
            FieldDef.intField("ability.fireDelay", this::getFireDelay, this::setFireDelay)
                .range(0, 200)
        ));

        defs.add(FieldDef.row(
            FieldDef.floatField("stats.size", this::getOrbSize, this::setOrbSize)
                .range(0.1f, 10.0f),
            FieldDef.floatField("enchantment.damage", this::getDamage, this::setDamage)
        ));

        defs.add(FieldDef.row(
            FieldDef.floatField("ability.knockback", this::getKnockback, this::setKnockback),
            FieldDef.intField("ability.lifetime", this::getMaxLifetime, this::setMaxLifetime).range(1, 1200)
        ));

        defs.add(FieldDef.section("ability.section.scatter"));
        defs.add(FieldDef.row(
            FieldDef.floatField("ability.offsetRangeX", this::getOffsetRangeX, this::setOffsetRangeX).range(1.0f, 50.0f),
            FieldDef.floatField("ability.offsetRangeY", this::getOffsetRangeY, this::setOffsetRangeY).range(1.0f, 50.0f)
        ));
        defs.add(FieldDef.floatField("ability.offsetRangeZ", this::getOffsetRangeZ, this::setOffsetRangeZ).range(1.0f, 50.0f));

        defs.add(FieldDef.section("ability.section.positioning"));
        defs.add(FieldDef.row(
            FieldDef.floatField("stats.minSpeed", this::getMinPositioningSpeed, this::setMinPositioningSpeed).range(0.1f, 5.0f),
            FieldDef.floatField("stats.maxSpeed", this::getMaxPositioningSpeed, this::setMaxPositioningSpeed).range(0.1f, 5.0f)
        ));

        defs.add(FieldDef.section("ability.section.freeze"));
        defs.add(FieldDef.row(
            FieldDef.intField("ability.freezeDelay", this::getFreezeDelay, this::setFreezeDelay).range(0, 100),
            FieldDef.intField("ability.freezeTicks", this::getFreezeTicks, this::setFreezeTicks).range(1, 200)
        ));

        defs.add(FieldDef.section("ability.section.refire"));
        defs.add(FieldDef.row(
            FieldDef.floatField("stats.refireSpeed", this::getRefireSpeed, this::setRefireSpeed).range(0.1f, 10.0f),
            FieldDef.intField("ability.refireDelay", this::getRefireDelay, this::setRefireDelay).range(0, 200)
        ));

        defs.add(FieldDef.section("ability.section.explosive"));
        defs.add(FieldDef.boolField("gui.enabled", this::isExplosive, this::setExplosive)
            .hover("ability.hover.explosive"));
        defs.add(FieldDef.floatField("gui.radius", this::getExplosionRadius, this::setExplosionRadius)
            .range(0.0f, EnergyCombatData.MAX_EXPLOSION_RADIUS)
            .visibleWhen(this::isExplosive));

        defs.add(AbilityFieldDefs.effectsListField("ability.effects", this::getEffects, this::setEffects));
    }
}
