package kamkeel.npcdbc.data.npc;

import kamkeel.npcdbc.api.npc.IDBCStats;
import net.minecraft.nbt.NBTTagCompound;

public class DBCStats implements IDBCStats {
    public boolean enabled = false;
    public boolean friendlyFist = false, ignoreDex = false, ignoreBlock = false, ignoreEndurance = false, ignoreKiProtection = false, ignoreFormReduction = false, hasDefensePenetration = false;

    public int defensePenetration = 10;

    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setBoolean("DBCStatsEnabled", enabled);
        if (enabled) {
            NBTTagCompound dbcStats = new NBTTagCompound();

            dbcStats.setBoolean("DBCFriendlyFist", friendlyFist);
            dbcStats.setBoolean("DBCIgnoreDex", ignoreDex);
            dbcStats.setBoolean("DBCIgnoreBlock", ignoreBlock);
            dbcStats.setBoolean("DBCIgnoreEndurance", ignoreEndurance);
            dbcStats.setBoolean("DBCIgnoreKiProtection", ignoreKiProtection);
            dbcStats.setBoolean("DBCIgnoreFormReduction", ignoreFormReduction);
            dbcStats.setBoolean("DBCIsDefensePen", hasDefensePenetration);
            dbcStats.setInteger("DBCDefensePen", defensePenetration);

            nbttagcompound.setTag("DBCStats", dbcStats);
        } else {
            nbttagcompound.removeTag("DBCStats");
        }
        return nbttagcompound;
    }

    public void readFromNBT(NBTTagCompound nbttagcompound) {
        enabled = nbttagcompound.getBoolean("DBCStatsEnabled");
        if (enabled) {
            NBTTagCompound dbcStats = nbttagcompound.getCompoundTag("DBCStats");

            friendlyFist = dbcStats.getBoolean("DBCFriendlyFist");
            ignoreDex = dbcStats.getBoolean("DBCIgnoreDex");
            ignoreBlock = dbcStats.getBoolean("DBCIgnoreBlock");
            ignoreEndurance = dbcStats.getBoolean("DBCIgnoreEndurance");
            ignoreKiProtection = dbcStats.getBoolean("DBCIgnoreKiProtection");
            ignoreFormReduction = dbcStats.getBoolean("DBCIgnoreFormReduction");
            hasDefensePenetration = dbcStats.getBoolean("DBCIsDefensePen");
            defensePenetration = dbcStats.getInteger("DBCDefensePen");
        } else {
            nbttagcompound.removeTag("DBCStats");
        }
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
}
