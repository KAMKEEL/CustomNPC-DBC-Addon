package kamkeel.npcdbc.data.ability;

import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.JRMCoreHDBC;
import cpw.mods.fml.common.FMLCommonHandler;
import kamkeel.npcdbc.constants.DBCDamageSource;
import kamkeel.npcdbc.data.DBCDamageCalc;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.DBCUtils;
import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.controllers.data.ability.conditions.AbilityCondition;
import kamkeel.npcs.controllers.data.ability.conditions.ConditionHPThreshold;
import kamkeel.npcs.controllers.data.ability.conditions.ConditionThreshold;
import kamkeel.npcs.controllers.data.ability.enums.AbilityPhase;
import kamkeel.npcs.controllers.data.ability.extender.IAbilityExtender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import noppes.npcs.NpcDamageSource;
import noppes.npcs.entity.EntityNPCInterface;

/**
 * DBC Addon ability extender. Provides:
 * - DBC damage routing for all ability damage when DBC Addon is installed
 * - Lifecycle hooks for player resource costs (ki, stamina)
 */
public class DBCAbilityExtender implements IAbilityExtender {

    @Override
    public boolean onAbilityStart(Ability ability, EntityLivingBase caster, EntityLivingBase target) {
        if (!(caster instanceof EntityPlayer))
            return true;

        DBCAbilityStats stats = DBCAbilityStats.fromAbility(ability);
        DBCData data = DBCData.get((EntityPlayer) caster);

        // Calculate actual costs (flat or percent of max pool)
        int kiCost = stats.getKiCost();
        if (kiCost > 0 && stats.isKiCostPercent())
            kiCost = (int) (kiCost / 100.0 * data.stats.getMaxKi());

        int staminaCost = stats.getStaminaCost();
        if (staminaCost > 0 && stats.isStaminaCostPercent())
            staminaCost = (int) (staminaCost / 100.0 * data.stats.getMaxStamina());

        if (kiCost <= 0 && staminaCost <= 0)
            return true;

        if (kiCost > 0 && data.Ki < kiCost)
            return false;

        if (staminaCost > 0 && data.Stamina < staminaCost)
            return false;

        if (kiCost > 0)
            data.stats.restoreKiFlat(-kiCost);

        if (staminaCost > 0)
            data.stats.restoreStaminaFlat(-staminaCost);

        return true;
    }

    @Override
    public boolean onAbilityTick(Ability ability, EntityLivingBase caster, EntityLivingBase target,
                                 AbilityPhase phase, int tick) {
        // Drain only during ACTIVE phase — cost is handled on start, drain is per-tick while executing
        if (phase != AbilityPhase.ACTIVE)
            return true;

        if (!(caster instanceof EntityPlayer))
            return true;

        DBCAbilityStats stats = DBCAbilityStats.fromAbility(ability);
        int kiDrain = stats.getKiDrain();
        int staminaDrain = stats.getStaminaDrain();

        if (kiDrain <= 0 && staminaDrain <= 0)
            return true;

        DBCData data = DBCData.get((EntityPlayer) caster);

        int actualKiDrain = 0;
        int actualStaminaDrain = 0;

        if (kiDrain > 0) {
            actualKiDrain = stats.isKiDrainPercent()
                ? (int) (kiDrain / 100.0 * data.stats.getMaxKi()) : kiDrain;
            if (data.Ki < actualKiDrain)
                return false; // interrupt — not enough ki
        }

        if (staminaDrain > 0) {
            actualStaminaDrain = stats.isStaminaDrainPercent()
                ? (int) (staminaDrain / 100.0 * data.stats.getMaxStamina()) : staminaDrain;
            if (data.Stamina < actualStaminaDrain)
                return false; // interrupt — not enough stamina
        }

        // Deduct both after validation passes
        if (actualKiDrain > 0)
            data.stats.restoreKiFlat(-actualKiDrain);
        if (actualStaminaDrain > 0)
            data.stats.restoreStaminaFlat(-actualStaminaDrain);

        return true;
    }

    @Override
    public float modifyBarrierHealth(Ability ability, EntityLivingBase caster, float baseHealth) {
        if (!(caster instanceof EntityPlayer))
            return baseHealth; // NPCs always use flat health

        DBCAbilityStats stats = DBCAbilityStats.fromAbility(ability);
        if (!stats.barrierScalingEnabled)
            return baseHealth;

        float scaledHealth = DBCUtils.calculateBarrierHealth((EntityPlayer) caster, stats);
        return scaledHealth > 0 ? scaledHealth : baseHealth;
    }

    @Override
    public boolean onAbilityHeal(Ability ability, EntityLivingBase caster, EntityLivingBase target, float healAmount) {
        if (!(target instanceof EntityPlayer))
            return false; // Non-DBC entities use vanilla healing

        DBCData data = DBCData.get((EntityPlayer) target);
        if (data == null)
            return false;

        DBCAbilityStats stats = DBCAbilityStats.fromAbility(ability);

        // When heal scaling is enabled and caster is a player, use CNPC scaling sets
        // Scaled values are always applied as flat body HP (they produce DBC-scale numbers)
        if (stats.healScalingEnabled && caster instanceof EntityPlayer) {
            float scaled = DBCUtils.calculateHealingAmount((EntityPlayer) caster, stats);
            if (scaled > 0) {
                data.stats.restoreHealthFlat((int) scaled);
                return true;
            }
        }

        // No scaling — use ability's base heal amount with healing mode
        if (stats.healingMode == 0) {
            // FLAT: heal amount is direct body HP
            data.stats.restoreHealthFlat((int) healAmount);
        } else {
            // PERCENT: heal amount is treated as percentage of max body
            data.stats.restoreHealthPercent(healAmount);
        }

        return true; // Handled — skip vanilla entity.heal()
    }

    @Override
    public float modifyProjectileDamage(Ability ability, EntityLivingBase caster, float baseDamage) {
        if (!(caster instanceof EntityPlayer))
            return baseDamage;

        DBCAbilityStats stats = DBCAbilityStats.fromAbility(ability);
        float calcDamage = DBCUtils.calculateAbilityAttackDamage((EntityPlayer) caster, stats);
        return calcDamage > 0 ? calcDamage : baseDamage;
    }

    @Override
    public boolean onAbilityDamage(Ability ability, EntityLivingBase caster, EntityLivingBase target,
                                   float damage, float knockback, float knockbackUp,
                                   double knockbackDirX, double knockbackDirZ,
                                   float damageMultiplier) {
        DBCAbilityStats stats = DBCAbilityStats.fromAbility(ability);

        // Build the damage source based on caster type
        DamageSource source;
        if (caster instanceof EntityNPCInterface) {
            source = new NpcDamageSource("mob", (EntityNPCInterface) caster);
        } else if (caster instanceof EntityPlayer) {
            source = DamageSource.causePlayerDamage((EntityPlayer) caster);
        } else {
            source = DamageSource.causeMobDamage(caster);
        }

        // Calculate outgoing damage
        float outDamage = damage; // default: use ability's base damage
        if (caster instanceof EntityPlayer) {
            float calcDamage = DBCUtils.calculateAbilityAttackDamage((EntityPlayer) caster, stats);
            if (calcDamage > 0) {
                // DBC scaling replaces the base damage; re-apply any ability-internal modifiers
                // (e.g. Slam height scaling) so the ability's multiplier is preserved.
                // damageMultiplier accounts for barrier absorption (< 1.0 when projectile broke through a barrier).
                outDamage = calcDamage * ability.getDamageMultiplier() * damageMultiplier;
            }
        }

        // Route damage to target
        if (target instanceof EntityPlayer) {
            // Check No PVP dimension toggle for player-to-player ability damage
            if (caster instanceof EntityPlayer) {
                MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
                if (server != null) {
                    String pvpSetting = JRMCoreH.rwip(server, target.dimension + "");
                    if ("false".equalsIgnoreCase(pvpSetting)) {
                        return true; // No damage in No PVP dimension
                    }
                }
            }

            // Check JRMC safezone protection
            if (JRMCoreConfig.sfzns && JRMCoreHDBC.JRMCoreEHonLivingHurtSafeZone(target)) {
                return true; // No damage inside safezone
            }

            // Player target: flag-guarded attackEntityFrom for knockback/animation only
            DBCUtils.abilityDamageHandled = true;
            DBCUtils.abilityDamageAmount = outDamage;
            try {
                target.attackEntityFrom(source, 1.0f);
            } finally {
                DBCUtils.abilityDamageHandled = false;
                DBCUtils.abilityDamageAmount = null;
            }

            // Player DBC Stats: when enabled AND usePlayerSettings is false, the ability's
            // configured ignore flags (IgnoreDex, FriendlyFist, etc.) override the player's own settings.
            // When usePlayerSettings is true, the player's own DBC settings (Friendly Fist, etc.) are used.
            // For NPC casters, usePlayerSettings is irrelevant — always use ability stats when enabled.
            boolean useAbilityStats = stats.isEnabled();
            if (caster instanceof EntityPlayer && stats.getUsePlayerSettings()) {
                useAbilityStats = false;
            }

            if (useAbilityStats) {
                // Use ability's configured ignore flags for defender reduction
                applyDBCDamageToPlayer((EntityPlayer) target, outDamage, stats, source);
            } else {
                // Use player's own DBC combat settings (generic defender reduction)
                applyDBCDamageToPlayerDefault((EntityPlayer) target, outDamage, stats, source);
            }
        } else if (target instanceof EntityNPCInterface) {
            // NPC target: set npcLastSetDamage for the Mixin to pick up
            DBCUtils.npcLastSetDamage = outDamage;
            target.attackEntityFrom(source, outDamage);
        } else {
            // Other entities: direct damage
            target.attackEntityFrom(source, outDamage);
        }

        return true; // Always handled when DBC Addon is installed
    }

    /**
     * Apply damage to a player through the DBC damage system with ability's universal settings.
     * Uses calculateDBCStatDamage which respects the ability's ignore flags.
     */
    private void applyDBCDamageToPlayer(EntityPlayer player, float damage, DBCAbilityStats stats, DamageSource source) {
        DBCDamageCalc damageCalc = DBCUtils.calculateDBCStatDamage(player, (int) damage, stats, source);

        DBCPlayerEvent.DamagedEvent damagedEvent = new DBCPlayerEvent.DamagedEvent(
            player, damageCalc, source, DBCDamageSource.NPC
        );
        if (DBCEventHooks.onDBCDamageEvent(damagedEvent)) {
            return;
        }

        damageCalc.damage = damagedEvent.damage;
        damageCalc.stamina = damagedEvent.getStaminaReduced();
        damageCalc.ki = damagedEvent.getKiReduced();
        damageCalc.ko = damagedEvent.getFinalKO();

        DBCUtils.lastSetDamage = damageCalc;
        damageCalc.processExtras();

        DBCUtils.doDBCDamage(player, damageCalc.damage, stats, source);
    }

    /**
     * Apply damage to a player through the DBC damage system with default defender reduction.
     * Uses calculateDBCDamageFromSource which applies generic DEX/blocking/ki protection.
     * Friendly fist is handled by calculateDBCDamageFromSource (from the attacker's own toggles),
     * so we pass null for stats to avoid the ability's friendly fist overriding the player's settings.
     */
    private void applyDBCDamageToPlayerDefault(EntityPlayer player, float damage, DBCAbilityStats stats, DamageSource source) {
        DBCDamageCalc damageCalc = DBCUtils.calculateDBCDamageFromSource(player, damage, source);

        DBCPlayerEvent.DamagedEvent damagedEvent = new DBCPlayerEvent.DamagedEvent(
            player, damageCalc, source, DBCDamageSource.PLAYER
        );
        if (DBCEventHooks.onDBCDamageEvent(damagedEvent)) {
            return;
        }

        damageCalc.damage = damagedEvent.damage;
        damageCalc.stamina = damagedEvent.getStaminaReduced();
        damageCalc.ki = damagedEvent.getKiReduced();
        damageCalc.ko = damagedEvent.getFinalKO();

        DBCUtils.lastSetDamage = damageCalc;
        damageCalc.processExtras();

        // Pass null for stats — player's own settings are used (friendly fist already handled above)
        DBCUtils.doDBCDamage(player, damageCalc.damage, null, source);
    }

    @Override
    public Boolean onCheckConditionForPlayer(AbilityCondition condition, EntityLivingBase player) {
        if (condition instanceof ConditionHPThreshold) {
            return handleConditionHPThreshold((ConditionHPThreshold) condition, (EntityPlayer) player);
        }

        return null;
    }

    public boolean handleConditionHPThreshold(ConditionHPThreshold condition, EntityPlayer player) {
        boolean isPercent = condition.isPercent();
        ConditionThreshold.ThresholdType thresholdType = condition.getThresholdType();
        DBCData dbcData = DBCData.get(player);

        float value;
        float threshold;
        float thresholdPercent = condition.getThresholdPercent();
        float thresholdFlat = condition.getThresholdFlat();
        if (isPercent) {
            float max = dbcData.simplifiedDBCData.getMaxHP();
            value = max > 0 ? dbcData.simplifiedDBCData.getHP() / max : 0;
            threshold = thresholdPercent;
        } else {
            value = dbcData.simplifiedDBCData.getHP();
            threshold = thresholdFlat;
        }
        return thresholdType.test(value, threshold);
    }
}
