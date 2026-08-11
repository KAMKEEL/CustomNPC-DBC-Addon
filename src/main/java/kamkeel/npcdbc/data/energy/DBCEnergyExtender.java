package kamkeel.npcdbc.data.energy;

import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.JRMCoreHDBC;
import cpw.mods.fml.common.FMLCommonHandler;
import kamkeel.npcdbc.data.ability.DBCAbilityStats;
import kamkeel.npcdbc.util.AbilityDamageSource;
import kamkeel.npcdbc.util.DBCUtils;
import kamkeel.npcs.controllers.data.energy.IEnergyExtender;
import kamkeel.npcs.entity.EntityEnergyAbility;
import kamkeel.npcs.util.AttributeAttackUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import noppes.npcs.NpcDamageSource;
import noppes.npcs.controllers.data.MagicData;
import noppes.npcs.entity.EntityNPCInterface;

/**
 * DBC Addon energy handler. Routes damage from script-created energy entities
 * (projectiles, zones, sweepers, panels) through the DBC damage pipeline
 * when the entity carries customDamageData with DBC stats.
 *
 * The damageData NBT is expected to contain a "DBCAbilityStats" sub-tag
 * using the same format as ability DBC stats.
 */
public class DBCEnergyExtender implements IEnergyExtender {

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
            source = new AbilityDamageSource(owner);
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

            // Apply magic pipeline using the energy entity's magic data
            MagicData entityMagic = null;
            if (energyEntity instanceof EntityEnergyAbility) {
                entityMagic = ((EntityEnergyAbility) energyEntity).getMagicData();
            }
            outDamage = AttributeAttackUtil.calculateAbilityDamage(owner, target, outDamage, entityMagic);
        } else if (owner instanceof EntityNPCInterface) {
            // NPC owner: apply magic pipeline (splits + interactions, no gear)
            MagicData entityMagic = null;
            if (energyEntity instanceof EntityEnergyAbility) {
                entityMagic = ((EntityEnergyAbility) energyEntity).getMagicData();
            }
            outDamage = AttributeAttackUtil.calculateAbilityDamage(owner, target, outDamage, entityMagic);
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
                DBCUtils.applyDBCDamageToPlayer((EntityPlayer) target, outDamage, stats, source, owner);
            } else {
                DBCUtils.applyDBCDamageToPlayerDefault((EntityPlayer) target, outDamage, stats, source, owner);
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

}
