package kamkeel.npcdbc.data.ability.conditions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcs.controllers.data.ability.conditions.ConditionCompare;
import kamkeel.npcs.controllers.data.ability.enums.UserType;
import kamkeel.npcs.controllers.data.ability.conditions.AbilityCondition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.gui.builder.FieldDef;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.List;

public class ConditionDBCLevel extends ConditionCompare {
    private int level = 1;

    public ConditionDBCLevel() {
        this.typeId = "condition.npcdbc.level";
        this.name = "condition.npcdbc.level";
        this.userType = UserType.PLAYER_ONLY;
    }

    @Override
    protected float getEntityValue(EntityLivingBase entity) {
        EntityPlayer player = (EntityPlayer) entity;
        return DBCData.get(player).getPlayerLevel();
    }

    @Override
    protected float getThreshold() {
        return getLevel();
    }

    @Override
    protected boolean checkEntity(EntityLivingBase entity) {
        if (entity instanceof EntityNPCInterface) return true;
        if (!(entity instanceof EntityPlayer)) return true;

        return super.checkEntity(entity);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getExtraDefinitions(List<FieldDef> defs) {
        defs.add(FieldDef.intField("condition.level", this::getLevel, this::setLevel).min(1));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getConditionSummary() {
        String filterLabel = StatCollector.translateToLocal("condition.filter." + getFilter().name().toLowerCase());
        String typeLabel = StatCollector.translateToLocal(compareType.toString());
        return "[" + filterLabel + "] Level " + typeLabel + " " + level;
    }

    @Override
    public void writeExtraNBT(NBTTagCompound nbt) {
        nbt.setInteger("level", level);
        nbt.setInteger("compareType", compareType.ordinal());
    }

    @Override
    public void readExtraNBT(NBTTagCompound nbt) {
        level = nbt.getInteger("level");
        compareType = CompareType.fromOrdinal(nbt.getInteger("compareType"));
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }
}
