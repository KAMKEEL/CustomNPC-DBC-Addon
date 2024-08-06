package kamkeel.npcdbc.data.npc;

import kamkeel.npcdbc.api.npc.IDBCStats;
import kamkeel.npcdbc.config.ConfigDBCGeneral;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.ValueUtil;

public class DBCStats implements IDBCStats {
    public boolean enabled = ConfigDBCGeneral.STATS_BY_DEFAULT;
    public boolean friendlyFist = false, ignoreDex = false, ignoreBlock = false, ignoreEndurance = false, ignoreKiProtection = false, ignoreFormReduction = false, hasDefensePenetration = false;

    public int defensePenetration = 10;
    public int friendlyFistTime = 6;
    public byte release = 100;

    private boolean canBeLockedOn = true;

    private EntityNPCInterface npc;

    public DBCStats(EntityNPCInterface npc) {
        this.npc = npc;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setBoolean("DBCStatsEnabled", enabled);
        nbttagcompound.setBoolean("DBCCanBeLockedOn", canBeLockedOn);
        if(friendlyFistTime <= 0)
            friendlyFistTime = 6;

        if (enabled) {
            NBTTagCompound dbcStats = new NBTTagCompound();

            dbcStats.setBoolean("DBCFriendlyFist", friendlyFist);
            dbcStats.setInteger("DBCFriendlyFistTime", friendlyFistTime);
            dbcStats.setBoolean("DBCIgnoreDex", ignoreDex);
            dbcStats.setBoolean("DBCIgnoreBlock", ignoreBlock);
            dbcStats.setBoolean("DBCIgnoreEndurance", ignoreEndurance);
            dbcStats.setBoolean("DBCIgnoreKiProtection", ignoreKiProtection);
            dbcStats.setBoolean("DBCIgnoreFormReduction", ignoreFormReduction);
            dbcStats.setBoolean("DBCIsDefensePen", hasDefensePenetration);
            dbcStats.setInteger("DBCDefensePen", defensePenetration);
            dbcStats.setByte("DBCRelease", release);

            nbttagcompound.setTag("DBCStats", dbcStats);
        } else {
            nbttagcompound.removeTag("DBCStats");
        }
        return nbttagcompound;
    }

    public void readFromNBT(NBTTagCompound nbttagcompound) {
        enabled = nbttagcompound.getBoolean("DBCStatsEnabled");

        if(nbttagcompound.hasKey("DBCCanBeLockedOn"))
            setLockOnState(nbttagcompound.getBoolean("DBCCanBeLockedOn"));
        else
            setLockOnState(true);

        if (enabled) {
            NBTTagCompound dbcStats = nbttagcompound.getCompoundTag("DBCStats");

            friendlyFist = dbcStats.getBoolean("DBCFriendlyFist");
            friendlyFistTime = dbcStats.getInteger("DBCFriendlyFistTime");
            ignoreDex = dbcStats.getBoolean("DBCIgnoreDex");
            ignoreBlock = dbcStats.getBoolean("DBCIgnoreBlock");
            ignoreEndurance = dbcStats.getBoolean("DBCIgnoreEndurance");
            ignoreKiProtection = dbcStats.getBoolean("DBCIgnoreKiProtection");
            ignoreFormReduction = dbcStats.getBoolean("DBCIgnoreFormReduction");
            hasDefensePenetration = dbcStats.getBoolean("DBCIsDefensePen");
            defensePenetration = dbcStats.getInteger("DBCDefensePen");
            release = dbcStats.getByte("DBCRelease");
        } else {
            nbttagcompound.removeTag("DBCStats");
        }

        if(friendlyFistTime <= 0)
            friendlyFistTime = 6;
    }

    @Override
    public void setRelease(byte release) {
        this.release = release;
    }

    @Override
    public byte getRelease() {
        return release;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isFriendlyFist() {
        return friendlyFist;
    }

    @Override
    public void setFriendlyFist(boolean friendlyFist) {
        this.friendlyFist = friendlyFist;
    }

    @Override
    public int getFriendlyFistAmount() {
        return friendlyFistTime;
    }

    @Override
    public void setFriendlyFistAmount(int seconds) {
        friendlyFistTime = ValueUtil.clamp(seconds, 1, 60);
    }

    @Override
    public boolean isIgnoreDex() {
        return ignoreDex;
    }

    @Override
    public void setIgnoreDex(boolean ignoreDex) {
        this.ignoreDex = ignoreDex;
    }

    @Override
    public boolean isIgnoreBlock() {
        return ignoreBlock;
    }

    @Override
    public void setIgnoreBlock(boolean ignoreBlock) {
        this.ignoreBlock = ignoreBlock;
    }

    @Override
    public boolean isIgnoreEndurance() {
        return ignoreEndurance;
    }

    @Override
    public void setIgnoreEndurance(boolean ignoreEndurance) {
        this.ignoreEndurance = ignoreEndurance;
    }

    @Override
    public boolean isIgnoreKiProtection() {
        return ignoreKiProtection;
    }

    @Override
    public void setIgnoreKiProtection(boolean ignoreKiProtection) {
        this.ignoreKiProtection = ignoreKiProtection;
    }

    @Override
    public boolean isIgnoreFormReduction() {
        return ignoreFormReduction;
    }

    @Override
    public void setIgnoreFormReduction(boolean ignoreFormReduction) {
        this.ignoreFormReduction = ignoreFormReduction;
    }

    @Override
    public boolean hasDefensePenetration() {
        return hasDefensePenetration;
    }

    @Override
    public void setHasDefensePenetration(boolean hasDefensePenetration) {
        this.hasDefensePenetration = hasDefensePenetration;
    }

    @Override
    public int getDefensePenetration() {
        return defensePenetration;
    }

    @Override
    public void setDefensePenetration(int defensePenetration) {
        this.defensePenetration = defensePenetration;
    }

    @Override
    public boolean canBeLockedOn() {
        if(npc != null)
            return npc.getDataWatcher().getWatchableObjectInt(31) == 1;

        return canBeLockedOn;
    }

    @Override
    public void setLockOnState(boolean canBeLockedOn) {
        this.canBeLockedOn = canBeLockedOn;
        if(npc != null)
            npc.getDataWatcher().updateObject(31, Integer.valueOf(canBeLockedOn ? 1 : 0));
    }
}
