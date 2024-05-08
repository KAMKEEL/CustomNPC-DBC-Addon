package kamkeel.npcdbc.data.npc;

import kamkeel.npcdbc.api.npc.IDBCStats;
import net.minecraft.nbt.NBTTagCompound;

public class DBCStats implements IDBCStats {
    public boolean enabled = false;
    public boolean friendlyFist = false, ignoreDex = false, ignoreBlock = false, ignoreEndurance = false, ignoreKiProtection = false, ignoreFormReduction = false, hasDefensePenetration = false;

    public int defensePenetration = 10;

    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setBoolean("DBCEnabled", enabled);
        if (enabled) {
            nbttagcompound.setBoolean("DBCFriendlyFist", friendlyFist);
            nbttagcompound.setBoolean("DBCIgnoreDex", ignoreDex);
            nbttagcompound.setBoolean("DBCIgnoreBlock", ignoreBlock);
            nbttagcompound.setBoolean("DBCIgnoreEndurance", ignoreEndurance);
            nbttagcompound.setBoolean("DBCIgnoreKiProtection", ignoreKiProtection);
            nbttagcompound.setBoolean("DBCIgnoreFormReduction", ignoreFormReduction);
            nbttagcompound.setBoolean("DBCIsDefensePen", hasDefensePenetration);
            nbttagcompound.setInteger("DBCDefensePen", defensePenetration);
        }
        return nbttagcompound;
    }

    public void readFromNBT(NBTTagCompound nbttagcompound) {
        enabled = nbttagcompound.getBoolean("DBCEnabled");
        if (enabled) {
            friendlyFist = nbttagcompound.getBoolean("DBCFriendlyFist");
            ignoreDex = nbttagcompound.getBoolean("DBCIgnoreDex");
            ignoreBlock = nbttagcompound.getBoolean("DBCIgnoreBlock");
            ignoreEndurance = nbttagcompound.getBoolean("DBCIgnoreEndurance");
            ignoreKiProtection = nbttagcompound.getBoolean("DBCIgnoreKiProtection");
            ignoreFormReduction = nbttagcompound.getBoolean("DBCIgnoreFormReduction");
            hasDefensePenetration = nbttagcompound.getBoolean("DBCIsDefensePen");
            defensePenetration = nbttagcompound.getInteger("DBCDefensePen");
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
