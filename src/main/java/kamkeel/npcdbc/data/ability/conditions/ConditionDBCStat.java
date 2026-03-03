package kamkeel.npcdbc.data.ability.conditions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcs.controllers.data.ability.enums.UserType;
import kamkeel.npcs.controllers.data.ability.conditions.ConditionThreshold;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.client.gui.builder.FieldDef;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.List;

public class ConditionDBCStat extends ConditionThreshold {

    public enum StatType {
        STR, DEX, CON, WIL, MND, SPI;

        public static StatType fromOrdinal(int ordinal) {
            StatType[] values = values();
            return (ordinal >= 0 && ordinal < values.length) ? values[ordinal] : STR;
        }

        @Override
        public String toString() {
            return "condition.stat." + name().toLowerCase();
        }
    }

    private StatType statType = StatType.STR;

    public ConditionDBCStat() {
        this.typeId = "condition.npcdbc.stat";
        this.name = "condition.npcdbc.stat";
        this.userType = UserType.PLAYER_ONLY;
        this.percent = false;
        this.thresholdFlat = 100f;
    }

    @Override
    protected float getEntityValue(EntityLivingBase entity) {
        if (!(entity instanceof EntityPlayer)) return 0;
        DBCData data = DBCData.get((EntityPlayer) entity);
        switch (statType) {
            case STR: return data.STR;
            case DEX: return data.DEX;
            case CON: return data.CON;
            case WIL: return data.WIL;
            case MND: return data.MND;
            case SPI: return data.SPI;
            default: return 0;
        }
    }

    @Override
    protected float getEntityMaxValue(EntityLivingBase entity) {
        // DBC stats don't have a meaningful max; percent mode is forced off
        return 1;
    }

    @Override
    protected boolean checkEntity(EntityLivingBase entity) {
        if (!(entity instanceof EntityPlayer)) return true;
        return super.checkEntity(entity);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getConditionDefinitions(List<FieldDef> defs) {
        defs.add(FieldDef.enumField("condition.stat_type", StatType.class, this::getStatType, this::setStatType));
        // Only show flat threshold -- DBC stats have no meaningful max for percent mode
        defs.add(FieldDef.floatField("condition.threshold_flat", this::getThresholdFlat, this::setThresholdFlat).min(0));
        defs.add(FieldDef.enumField("condition.threshold_type", ThresholdType.class,
            this::getThresholdType, this::setThresholdType));
    }

    @Override
    protected String getStatName() {
        return statType.name();
    }

    @Override
    public void writeTypeNBT(net.minecraft.nbt.NBTTagCompound nbt) {
        super.writeTypeNBT(nbt);
        nbt.setInteger("statType", statType.ordinal());
    }

    @Override
    public void readTypeNBT(net.minecraft.nbt.NBTTagCompound nbt) {
        super.readTypeNBT(nbt);
        statType = StatType.fromOrdinal(nbt.getInteger("statType"));
    }

    public StatType getStatType() {
        return statType;
    }

    public void setStatType(StatType statType) {
        this.statType = statType;
    }
}
