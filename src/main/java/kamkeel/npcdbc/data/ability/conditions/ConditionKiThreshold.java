package kamkeel.npcdbc.data.ability.conditions;

import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcs.controllers.data.ability.conditions.AbilityCondition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.gui.builder.FieldDef;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.ValueUtil;

import java.util.List;

public class ConditionKiThreshold extends AbilityCondition {
    private float thresholdPercent = 0.5f; // 50%
    private int threshold = 100;
    private boolean percent = true;
    private ThresholdType thresholdType = ThresholdType.ABOVE;

    public enum ThresholdType {
        ABOVE {
            @Override
            public boolean test(float value, float threshold) {
                return value >= threshold;
            }
        },
        BELOW {
            @Override
            public boolean test(float value, float threshold) {
                return value <= threshold;
            }
        },
        EQUAL {
            @Override
            public boolean test(float value, float threshold) {
                return value == threshold;
            }
        };

        public abstract boolean test(float value, float threshold);

        public static ThresholdType fromOrdinal(int ordinal) {
            ThresholdType[] values = values();
            if (ordinal >= 0 && ordinal < values.length) {
                return values[ordinal];
            }
            return ABOVE;
        }

        @Override
        public String toString() {
            switch (this) {
                case ABOVE:
                    return "condition.ki_above";
                case BELOW:
                    return "condition.ki_below";
                case EQUAL:
                    return "condition.ki_equal";
                default:
                    return name();
            }
        }
    }

    public ConditionKiThreshold() {
        this.typeId = "condition.npcdbc.ki_threshold";
        this.name = "condition.npcdbc.ki_threshold";
    }

    @Override
    public boolean checkEntity(EntityLivingBase entity) {
        if (entity instanceof EntityNPCInterface) {
            return false;
        }

        DBCData entityData = DBCData.get((EntityPlayer) entity);

        float casterKi = isPercent() ? (float) entityData.Ki / entityData.stats.getMaxKi() : entityData.Ki;
        return thresholdType.test(casterKi, isPercent() ? getThresholdPercent() : getThreshold());
    }

    @Override
    public void getConditionDefinitions(List<FieldDef> defs) {
        defs.add(FieldDef.intField("condition.threshold", this::getThreshold, this::setThreshold).min(0)
            .visibleWhen(() -> !isPercent()));
        defs.add(FieldDef.floatField("condition.thresholdPercent", this::getThresholdPercent, this::setThresholdPercent)
            .min(0).max(1)
            .visibleWhen(this::isPercent));
        defs.add(FieldDef.boolField("condition.percent", this::isPercent, this::setIsPercent));
        defs.add(FieldDef.enumField("condition.threshold_type", ThresholdType.class,
            this::getThresholdType, this::setThresholdType));
    }

    @Override
    public void writeTypeNBT(NBTTagCompound nbt) {
        nbt.setInteger("threshold", threshold);
        nbt.setFloat("thresholdPercent", thresholdPercent);
    }

    @Override
    public void readTypeNBT(NBTTagCompound nbt) {
        threshold = nbt.getInteger("threshold");
        thresholdPercent = nbt.getFloat("thresholdPercent");
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = Math.max(0, threshold);
    }

    public float getThresholdPercent() {
        return thresholdPercent;
    }

    public void setThresholdPercent(float thresholdPercent) {
        this.thresholdPercent = ValueUtil.clamp(thresholdPercent, 0, 1);
    }

    public ThresholdType getThresholdType() {
        return thresholdType;
    }

    public void setThresholdType(ThresholdType thresholdType) {
        this.thresholdType = thresholdType;
    }

    public boolean isPercent() {
        return percent;
    }

    public void setIsPercent(boolean percent) {
        this.percent = percent;
    }
}
