package kamkeel.npcdbc.data.energy;

import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.JRMCoreHDBC;
import cpw.mods.fml.common.FMLCommonHandler;
import kamkeel.npcdbc.constants.DBCDamageSource;
import kamkeel.npcdbc.data.DBCDamageCalc;
import kamkeel.npcdbc.data.ability.DBCAbilityStats;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.DBCUtils;
import kamkeel.npcs.controllers.data.ability.type.AbilityDefend;
import kamkeel.npcs.controllers.data.ability.type.AbilityGuard;
import kamkeel.npcs.controllers.data.energy.IEnergyHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import noppes.npcs.NpcDamageSource;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;

/**
 * DBC Addon energy handler. Routes damage from script-created energy entities
 * (projectiles, zones, sweepers, panels) through the DBC damage pipeline
 * when the entity carries customDamageData with DBC stats.
 *
 * The damageData NBT is expected to contain a "DBCAbilityStats" sub-tag
 * using the same format as ability DBC stats.
 */
public class DBCEnergyHandler implements IEnergyHandler {

    @Override
    public boolean onEnergyDamage(Entity energyEntity, EntityLivingBase owner,
                                   EntityLivingBase target, float damage,
                                   float knockback, float knockbackUp,
                                   double kbDirX, double kbDirZ,
                                   float damageMultiplier,
                                   NBTTagCompound damageData) {
        DBCAbilityStats stats = DBCAbilityStats.fromNBT(damageData);
        if (!stats.isEnabled()) {
            return false;
        }

        // Build the damage source based on owner type
        DamageSource source;
        if (owner instanceof EntityNPCInterface) {
            source = new NpcDamageSource("mob", (EntityNPCInterface) owner);
        } else if (owner instanceof EntityPlayer) {
            source = DamageSource.causePlayerDamage((EntityPlayer) owner);
        } else {
            source = DamageSource.causeMobDamage(owner);
        }

        // Calculate outgoing damage
        float outDamage = damage * damageMultiplier;
        if (owner instanceof EntityPlayer) {
            float calcDamage = DBCUtils.calculateAbilityAttackDamage((EntityPlayer) owner, stats);
            if (calcDamage > 0) {
                outDamage = calcDamage * damageMultiplier;
            }
        }

        // Route damage to target
        if (target instanceof EntityPlayer) {
            // Check No PVP dimension toggle for player-to-player damage
            if (owner instanceof EntityPlayer) {
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

            // Use energy's DBC stats unless usePlayerSettings is true
            boolean useAbilityStats = true;
            if (owner instanceof EntityPlayer && stats.getUsePlayerSettings()) {
                useAbilityStats = false;
            }

            if (useAbilityStats) {
                applyDBCDamageToPlayer((EntityPlayer) target, outDamage, stats, source, owner);
            } else {
                applyDBCDamageToPlayerDefault((EntityPlayer) target, outDamage, stats, source, owner);
            }
        } else if (target instanceof EntityNPCInterface) {
            // NPC target: set npcLastSetDamage for the Mixin to pick up
            DBCUtils.npcLastSetDamage = outDamage;
            try {
                target.attackEntityFrom(source, outDamage);
            } finally {
                DBCUtils.npcLastSetDamage = null;
            }
        } else {
            // Other entities: direct damage
            target.attackEntityFrom(source, outDamage);
        }

        return true;
    }

    @Override
    public float modifyEnergyDamage(Entity energyEntity, EntityLivingBase owner,
                                     float baseDamage, NBTTagCompound damageData) {
        if (!(owner instanceof EntityPlayer)) {
            return baseDamage;
        }

        DBCAbilityStats stats = DBCAbilityStats.fromNBT(damageData);
        if (!stats.isEnabled()) {
            return baseDamage;
        }

        float calcDamage = DBCUtils.calculateAbilityAttackDamage((EntityPlayer) owner, stats);
        return calcDamage > 0 ? calcDamage : baseDamage;
    }

    /** Apply damage using the energy entity's DBC ignore flags. */
    private void applyDBCDamageToPlayer(EntityPlayer player, float damage, DBCAbilityStats stats,
                                         DamageSource source, EntityLivingBase attacker) {
        DBCDamageCalc damageCalc = DBCUtils.calculateDBCStatDamage(player, (int) damage, stats, source);
        damageCalc.damage = applyGuardReduction(player, attacker, source, damageCalc.damage);

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

    /** Apply damage using default DBC defender reduction (player's own settings). */
    private void applyDBCDamageToPlayerDefault(EntityPlayer player, float damage, DBCAbilityStats stats,
                                                DamageSource source, EntityLivingBase attacker) {
        DBCDamageCalc damageCalc = DBCUtils.calculateDBCDamageFromSource(player, damage, source);
        damageCalc.damage = applyGuardReduction(player, attacker, source, damageCalc.damage);

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

        // null stats = use player's own DBC settings
        DBCUtils.doDBCDamage(player, damageCalc.damage, null, source);
    }

    private float applyGuardReduction(EntityPlayer player, EntityLivingBase attacker,
                                       DamageSource source, float damage) {
        if (attacker == null) {
            return damage;
        }

        PlayerData pData = PlayerData.get(player);
        if (pData == null || pData.abilityData == null) {
            return damage;
        }

        AbilityDefend defend = pData.abilityData.getActiveDefend();
        if (defend instanceof AbilityGuard) {
            return defend.onDefend(attacker, source, damage);
        }
        return damage;
    }
}
