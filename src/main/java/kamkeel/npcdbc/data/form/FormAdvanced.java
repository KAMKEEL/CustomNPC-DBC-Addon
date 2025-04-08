package kamkeel.npcdbc.data.form;

import kamkeel.npcdbc.api.form.IAdvancedFormStat;
import kamkeel.npcdbc.api.form.IFormAdvanced;
import net.minecraft.nbt.NBTTagCompound;

public class FormAdvanced implements IFormAdvanced {

    private final Form parent;

    private static final int NUM_STATS = 12;
    private final AdvancedFormStat[] formStats;
    public static final String[] STAT_NAMES = {
        "Melee", "Defense", "Body", "Stamina",
        "EnergyPower", "EnergyPool", "MaxSkills", "Speed",
        "RegenRateBody", "RegenRateStamina", "RegenRateEnergy", "FlySpeed"
    };

    public FormAdvanced(Form parent) {
        this.parent = parent;
        formStats = new AdvancedFormStat[NUM_STATS];
        for (int i = 0; i < NUM_STATS; i++) {
            formStats[i] = new AdvancedFormStat();
        }
    }

    @Override
    public AdvancedFormStat getStat(int id) {
        if (id < 0 || id >= NUM_STATS) {
            return null;
        }
        return formStats[id];
    }

    @Override
    public void setStatEnabled(int id, boolean enabled) {
        AdvancedFormStat stat = getStat(id);
        if (stat != null) {
            stat.setEnabled(enabled);
        }
    }

    @Override
    public boolean isStatEnabled(int id) {
        AdvancedFormStat stat = getStat(id);
        return stat != null && stat.isEnabled();
    }

    @Override
    public void setStatBonus(int id, int bonus) {
        AdvancedFormStat stat = getStat(id);
        if (stat != null) {
            stat.setBonus(bonus);
        }
    }

    @Override
    public int getStatBonus(int id) {
        AdvancedFormStat stat = getStat(id);
        return (stat != null) ? stat.getBonus() : 0;
    }

    @Override
    public void setStatMulti(int id, float multiplier) {
        AdvancedFormStat stat = getStat(id);
        if (stat != null) {
            stat.setMultiplier(multiplier);
        }
    }

    @Override
    public float getStatMulti(int id) {
        AdvancedFormStat stat = getStat(id);
        return (stat != null) ? stat.getMultiplier() : 1.0f;
    }

    @Override
    public IFormAdvanced save() {
        if (parent != null)
            parent.save();
        return this;
    }

    // Reads the DBC stats from NBT.
    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound formStats = compound.getCompoundTag("formAdvanced");
        for (int i = 0; i < NUM_STATS; i++) {
            if (formStats.hasKey(STAT_NAMES[i])) {
                NBTTagCompound statCompound = formStats.getCompoundTag(STAT_NAMES[i]);
                // Only read if the stat section is enabled.
                boolean enabled = statCompound.getBoolean("enabled");
                if (enabled) {
                    this.formStats[i].setEnabled(true);
                    this.formStats[i].setBonus(statCompound.hasKey("bonus") ? statCompound.getInteger("bonus") : 0);
                    this.formStats[i].setMultiplier(statCompound.hasKey("multiplier") ? statCompound.getFloat("multiplier") : 1.0f);
                }
            }
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound formStats = new NBTTagCompound();
        for (int i = 0; i < NUM_STATS; i++) {
            if (this.formStats[i].isEnabled()) {
                NBTTagCompound statCompound = new NBTTagCompound();
                statCompound.setBoolean("enabled", true);
                statCompound.setInteger("bonus", this.formStats[i].getBonus());
                statCompound.setFloat("multiplier", this.formStats[i].getMultiplier());
                formStats.setTag(STAT_NAMES[i], statCompound);
            }
        }
        compound.setTag("formAdvanced", formStats);
        return compound;
    }

    public static class AdvancedFormStat implements IAdvancedFormStat {
        private boolean enabled = false;
        private int bonus = 0;
        private float multiplier = 1.0f;

        public AdvancedFormStat() {}

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        @Override
        public int getBonus() {
            return bonus;
        }

        @Override
        public void setBonus(int bonus) {
            this.bonus = bonus;
        }

        @Override
        public float getMultiplier() {
            return multiplier;
        }

        @Override
        public void setMultiplier(float multiplier) {
            this.multiplier = multiplier;
        }
    }
}
