package kamkeel.npcdbc.data.form;

import kamkeel.npcdbc.constants.DBCStatistics;
import kamkeel.npcdbc.api.form.IAdvancedFormStat;
import kamkeel.npcdbc.api.form.IFormAdvanced;
import net.minecraft.nbt.NBTTagCompound;

public class FormAdvanced implements IFormAdvanced {

    private final Form parent;

    private static final int NUM_STATS = 12;
    public static final String[] STAT_NAMES = {
        "Melee", "Defense", "Body", "Stamina",
        "EnergyPower", "EnergyPool", "MaxSkills", "Speed",
        "RegenRateBody", "RegenRateStamina", "RegenRateEnergy", "FlySpeed"
    };

    private final AdvancedFormStat[] formStats;

    public FormAdvanced(Form parent) {
        this.parent = parent;
        formStats = new AdvancedFormStat[NUM_STATS];
        for (int i = 0; i < NUM_STATS; i++) {
            formStats[i] = new AdvancedFormStat();
        }
    }

    @Override
    public AdvancedFormStat getStat(int id) {
        if (id < 0 || id >= NUM_STATS) return null;
        return formStats[id];
    }

    @Override
    public void setStatEnabled(int id, boolean enabled) {
        // never allow toggling MaxSkills
        if (id == DBCStatistics.MaxSkills) return;
        AdvancedFormStat stat = getStat(id);
        if (stat != null) {
            stat.setEnabled(enabled);
        }
    }

    @Override
    public boolean isStatEnabled(int id) {
        // MaxSkills is always disabled
        if (id == DBCStatistics.MaxSkills) return false;
        AdvancedFormStat stat = getStat(id);
        return stat != null && stat.isEnabled();
    }

    @Override
    public void setStatBonus(int id, int bonus) {
        // no bonus for MaxSkills
        if (id == DBCStatistics.MaxSkills) return;
        AdvancedFormStat stat = getStat(id);
        if (stat != null) {
            stat.setBonus(bonus);
        }
    }

    @Override
    public int getStatBonus(int id) {
        if (id == DBCStatistics.MaxSkills) return 0;
        AdvancedFormStat stat = getStat(id);
        return (stat != null) ? stat.getBonus() : 0;
    }

    @Override
    public void setStatMulti(int id, float multiplier) {
        // no multiplier for MaxSkills
        if (id == DBCStatistics.MaxSkills) return;
        AdvancedFormStat stat = getStat(id);
        if (stat != null) {
            stat.setMultiplier(multiplier);
        }
    }

    @Override
    public float getStatMulti(int id) {
        if (id == DBCStatistics.MaxSkills) return 1.0f;
        AdvancedFormStat stat = getStat(id);
        return (stat != null) ? stat.getMultiplier() : 1.0f;
    }

    @Override
    public IFormAdvanced save() {
        if (parent != null) {
            parent.save();
        }
        return this;
    }

    /**
     * Load enabled stats, bonuses, and multipliers—but skip MaxSkills.
     */
    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound tag = compound.getCompoundTag("formAdvanced");
        for (int i = 0; i < NUM_STATS; i++) {
            if (i == DBCStatistics.MaxSkills) continue;
            if (tag.hasKey(STAT_NAMES[i])) {
                NBTTagCompound statTag = tag.getCompoundTag(STAT_NAMES[i]);
                if (statTag.getBoolean("enabled")) {
                    formStats[i].setEnabled(true);
                    formStats[i].setBonus(statTag.hasKey("bonus")
                        ? statTag.getInteger("bonus")
                        : 0);
                    formStats[i].setMultiplier(statTag.hasKey("multiplier")
                        ? statTag.getFloat("multiplier")
                        : 1.0f);
                }
            }
        }
    }

    /**
     * Persist only enabled stats—but skip MaxSkills.
     */
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tag = new NBTTagCompound();
        for (int i = 0; i < NUM_STATS; i++) {
            if (i == DBCStatistics.MaxSkills) continue;
            if (formStats[i].isEnabled()) {
                NBTTagCompound statTag = new NBTTagCompound();
                statTag.setBoolean("enabled", true);
                statTag.setInteger("bonus", formStats[i].getBonus());
                statTag.setFloat("multiplier", formStats[i].getMultiplier());
                tag.setTag(STAT_NAMES[i], statTag);
            }
        }
        compound.setTag("formAdvanced", tag);
        return compound;
    }

    public static class AdvancedFormStat implements IAdvancedFormStat {
        private boolean enabled = false;
        private int bonus = 0;
        private float multiplier = 1.0f;

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
