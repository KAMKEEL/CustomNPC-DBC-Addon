package kamkeel.npcdbc.mixins.late.impl.npc;

import kamkeel.npcdbc.constants.DBCDamageSource;
import kamkeel.npcdbc.data.DBCDamageCalc;
import kamkeel.npcdbc.data.npc.DBCStats;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import kamkeel.npcdbc.mixins.late.INPCStats;
import kamkeel.npcdbc.mixins.late.IPlayerDBCInfo;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.DBCUtils;
import kamkeel.npcs.addon.DBCAddon;
import kamkeel.npcs.controllers.data.ability.type.AbilityDefend;
import kamkeel.npcs.controllers.data.ability.type.AbilityGuard;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import noppes.npcs.NpcDamageSource;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static JinRyuu.JRMCore.JRMCoreH.getInt;

@Mixin(DBCAddon.class)
public class MixinDBCAddon {

    @Shadow(remap = false)
    public boolean supportEnabled;

    /**
     * @author Kamkeel
     * @reason Allow for Copying of Data for DBC Model information
     */
    @Overwrite(remap = false)
    public void dbcCopyData(EntityLivingBase copied, EntityLivingBase entity) {
        if (!supportEnabled)
            return;

        if (entity instanceof EntityNPCInterface && copied instanceof EntityNPCInterface) {
            EntityNPCInterface receiverNPC = (EntityNPCInterface) entity;
            EntityNPCInterface npc = (EntityNPCInterface) copied;
            INPCStats stats = (INPCStats) npc.stats;
            INPCStats receiverStats = (INPCStats) receiverNPC.stats;

            INPCDisplay display = (INPCDisplay) npc.display;
            INPCDisplay receiverDisplay = (INPCDisplay) receiverNPC.display;

            NBTTagCompound dbcStats = new NBTTagCompound();
            stats.getDBCStats().writeToNBT(dbcStats);
            receiverStats.getDBCStats().readFromNBT(dbcStats);

            receiverDisplay.getDBCDisplay().setEnabled(display.getDBCDisplay().isEnabled());
        }
    }

    /**
     * @author Kamkeel
     * @reason Checks if the DBC Attack event will be run
     */
    @Overwrite(remap = false)
    public boolean canDBCAttack(EntityNPCInterface npc, float attackStrength, Entity receiver) {
        if (!supportEnabled)
            return false;
        DBCStats dbcStats = ((INPCStats) npc.stats).getDBCStats();
        return dbcStats.enabled && receiver instanceof EntityPlayer && attackStrength > 0;
    }

    /**
     * @author Kamkeel
     * @reason Performs DBC Damage Calculations
     */
    @Overwrite(remap = false)
    public void doDBCDamage(EntityNPCInterface npc, float attackStrength, Entity receiver) {
        if (npc.isRemote())
            return;

        if (attackStrength <= 0)
            return;

        if (!(receiver instanceof EntityPlayer))
            return;

        if (npc.stats instanceof INPCStats) {
            EntityPlayer player = (EntityPlayer) receiver;
            DBCStats dbcStats = ((INPCStats) npc.stats).getDBCStats();

            // Calculate DBC Damage
            DamageSource damageSource = new NpcDamageSource("mob", npc);
            DBCDamageCalc damageCalc = DBCUtils.calculateDBCStatDamage(player, (int) attackStrength, dbcStats, damageSource);
            damageCalc.damage = applyGuardReduction(player, npc, damageSource, damageCalc.damage);

            DBCPlayerEvent.DamagedEvent damagedEvent = new DBCPlayerEvent.DamagedEvent(player, damageCalc, damageSource, DBCDamageSource.NPC);
            if (DBCEventHooks.onDBCDamageEvent(damagedEvent))
                return;

            damageCalc.damage = damagedEvent.damage;
            damageCalc.stamina = damagedEvent.getStaminaReduced();
            damageCalc.ki = damagedEvent.getKiReduced();
            damageCalc.ko = damagedEvent.getFinalKO();
            DBCUtils.lastSetDamage = damageCalc;
            damageCalc.processExtras();

            DBCUtils.doDBCDamage(player, damageCalc.damage, dbcStats, damageSource);
        }
    }

    private float applyGuardReduction(EntityPlayer player, EntityLivingBase attacker, DamageSource source, float damage) {
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

    /**
     * @author Kamkeel
     * @reason Returns pre-calculated DBC attack damage of the current attacker
     */
    @Overwrite(remap = false)
    public float getAttackerDBCDamage(float vanillaDamage) {
        if (DBCUtils.preCalculatedAttackerDamage != null) {
            return DBCUtils.preCalculatedAttackerDamage;
        }
        return vanillaDamage;
    }

    /**
     * @author Kamkeel
     * @reason Performs DBC Damage Calculations
     */
    @Overwrite(remap = false)
    public boolean isKO(EntityNPCInterface npc, EntityPlayer player) {
        if (npc.isRemote())
            return false;

        if (npc.stats instanceof INPCStats) {
            DBCStats dbcStats = ((INPCStats) npc.stats).getDBCStats();
            if (dbcStats.enabled && dbcStats.isFriendlyFist()) {
                int currentKO = getInt(player, "jrmcHar4va");
                return currentKO > 0;
            }
        }
        return false;
    }


    /**
     * @author Kamkeel
     * @reason Writes Custom Form Player Data
     */
    @Overwrite(remap = false)
    public void writeToNBT(PlayerData playerData, NBTTagCompound nbtTagCompound) {
        ((IPlayerDBCInfo) playerData).getPlayerDBCInfo().saveNBTData(nbtTagCompound);
    }

    /**
     * @author Kamkeel
     * @reason Reads Custom Form Player Data
     */
    @Overwrite(remap = false)
    public void readFromNBT(PlayerData playerData, NBTTagCompound nbtTagCompound) {
        ((IPlayerDBCInfo) playerData).getPlayerDBCInfo().loadNBTData(nbtTagCompound);
    }
}
