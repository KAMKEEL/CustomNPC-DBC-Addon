package kamkeel.npcdbc.mixin.impl;

import kamkeel.addon.DBCAddon;
import kamkeel.npcdbc.data.DBCStats;
import kamkeel.npcdbc.mixin.INPCDisplay;
import kamkeel.npcdbc.mixin.INPCStats;
import kamkeel.npcdbc.util.DBCUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static JinRyuu.JRMCore.JRMCoreH.getInt;

@Mixin(DBCAddon.class)
public class MixinDBCAddon {

    @Shadow(remap = false) public boolean supportEnabled;

    /**
     * @author Kamkeel
     * @reason Allow for Copying of Data for DBC Model information
     */
    @Overwrite(remap = false)
    public void dbcCopyData(EntityLivingBase copied, EntityLivingBase entity) {
        if(!supportEnabled)
            return;

        if (entity instanceof EntityNPCInterface receiverNPC && copied instanceof EntityNPCInterface npc) {
            INPCStats stats = (INPCStats) npc.stats;
            INPCStats receiverStats =  (INPCStats) receiverNPC.stats;

            INPCDisplay display = (INPCDisplay) npc.display;
            INPCDisplay receiverDisplay =  (INPCDisplay) receiverNPC.display;

            receiverStats.getDBCStats().setFriendlyFist(stats.getDBCStats().isFriendlyFist());
            receiverStats.getDBCStats().setIgnoreDex(stats.getDBCStats().isIgnoreDex());
            receiverStats.getDBCStats().setIgnoreBlock(stats.getDBCStats().isIgnoreBlock());
            receiverStats.getDBCStats().setIgnoreEndurance(stats.getDBCStats().isIgnoreEndurance());
            receiverStats.getDBCStats().setIgnoreKiProtection(stats.getDBCStats().isIgnoreKiProtection());
            receiverStats.getDBCStats().setIgnoreFormReduction(stats.getDBCStats().isIgnoreFormReduction());
            receiverStats.getDBCStats().setHasDefensePenetration(stats.getDBCStats().hasDefensePenetration());
            receiverStats.getDBCStats().setDefensePenetration(stats.getDBCStats().getDefensePenetration());
        }
    }

    /**
     * @author Kamkeel
     * @reason Checks if the DBC Attack event will be run
     */
    @Overwrite(remap = false)
    public boolean canDBCAttack(EntityNPCInterface npc, float attackStrength, Entity receiver) {
        if(!supportEnabled)
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
        if(npc.isRemote())
            return;

        if(attackStrength <= 0)
            return;

        if(!(receiver instanceof EntityPlayer player))
            return;

        if(npc.stats instanceof INPCStats){
            DBCStats dbcStats = ((INPCStats) npc.stats).getDBCStats();
            DBCUtils.doDBCDamage(player, (int) attackStrength, dbcStats);
        }
    }

    /**
     * @author Kamkeel
     * @reason Performs DBC Damage Calculations
     */
    @Overwrite(remap = false)
    public boolean isKO(EntityNPCInterface npc, EntityPlayer player) {
        if(npc.isRemote())
            return false;

        if(npc.stats instanceof INPCStats){
            DBCStats dbcStats = ((INPCStats) npc.stats).getDBCStats();
            if(dbcStats.enabled && dbcStats.isFriendlyFist()){
                int currentKO = getInt(player, "jrmcHar4va");
                return currentKO > 0;
            }
        }
        return false;
    }
}
