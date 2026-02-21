package kamkeel.npcdbc.data.ability.conditions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcs.controllers.data.ability.UserType;
import kamkeel.npcs.controllers.data.ability.conditions.AbilityCondition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.gui.builder.FieldDef;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.List;

import static JinRyuu.JRMCore.JRMCoreH.getPlayerLevel;

public class ConditionDBCLevel extends AbilityCondition {

    public enum CompareType {
        ABOVE {
            @Override
            public boolean test(int value, int threshold) {
                return value >= threshold;
            }
        },
        BELOW {
            @Override
            public boolean test(int value, int threshold) {
                return value <= threshold;
            }
        },
        EQUAL {
            @Override
            public boolean test(int value, int threshold) {
                return value == threshold;
            }
        };

        public abstract boolean test(int value, int threshold);

        public static CompareType fromOrdinal(int ordinal) {
            CompareType[] values = values();
            return (ordinal >= 0 && ordinal < values.length) ? values[ordinal] : ABOVE;
        }

        @Override
        public String toString() {
            return "condition." + name().toLowerCase();
        }
    }

    private int level = 1;
    private CompareType compareType = CompareType.ABOVE;

    public ConditionDBCLevel() {
        this.typeId = "condition.npcdbc.level";
        this.name = "condition.npcdbc.level";
        this.userType = UserType.PLAYER_ONLY;
    }

    @Override
    protected boolean checkEntity(EntityLivingBase entity) {
        if (entity instanceof EntityNPCInterface) return true;
        if (!(entity instanceof EntityPlayer)) return true;

        EntityPlayer player = (EntityPlayer) entity;
        DBCData data = DBCData.get(player);
        int playerLevel = getPlayerLevel(data.STR + data.DEX + data.CON + data.WIL + data.MND + data.SPI);
        return compareType.test(playerLevel, level);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getConditionDefinitions(List<FieldDef> defs) {
        defs.add(FieldDef.intField("condition.level", this::getLevel, this::setLevel).min(1));
        defs.add(FieldDef.enumField("condition.compare_type", CompareType.class, this::getCompareType, this::setCompareType));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getConditionSummary() {
        String filterLabel = StatCollector.translateToLocal("condition.filter." + getFilter().name().toLowerCase());
        String typeLabel = StatCollector.translateToLocal(compareType.toString());
        return "[" + filterLabel + "] Level " + typeLabel + " " + level;
    }

    @Override
    public void writeTypeNBT(NBTTagCompound nbt) {
        nbt.setInteger("level", level);
        nbt.setInteger("compareType", compareType.ordinal());
    }

    @Override
    public void readTypeNBT(NBTTagCompound nbt) {
        level = nbt.getInteger("level");
        compareType = CompareType.fromOrdinal(nbt.getInteger("compareType"));
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }

    public CompareType getCompareType() {
        return compareType;
    }

    public void setCompareType(CompareType compareType) {
        this.compareType = compareType;
    }
}
