package kamkeel.npcdbc.data.ability.conditions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.constants.enums.EnumDBCClasses;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcs.controllers.data.ability.enums.UserType;
import kamkeel.npcs.controllers.data.ability.conditions.AbilityCondition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.gui.builder.FieldDef;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.List;

public class ConditionDBCClass extends AbilityCondition {
    private EnumDBCClasses dbcClass = EnumDBCClasses.MARTIAL_ARTIST;

    public ConditionDBCClass() {
        this.typeId = "condition.npcdbc.class";
        this.name = "condition.npcdbc.class";
        this.userType = UserType.PLAYER_ONLY;
    }

    @Override
    protected boolean checkEntity(EntityLivingBase entity) {
        if (entity instanceof EntityNPCInterface) return true;
        if (!(entity instanceof EntityPlayer)) return true;

        EntityPlayer player = (EntityPlayer) entity;
        DBCData data = DBCData.get(player);
        return data.Class == dbcClass.ordinal();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getConditionDefinitions(List<FieldDef> defs) {
        defs.add(FieldDef.enumField("condition.class_type", EnumDBCClasses.class, this::getDbcClass, this::setDbcClass));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getConditionSummary() {
        String filterLabel = StatCollector.translateToLocal(getFilter().toString());
        String className = StatCollector.translateToLocal(dbcClass.toString());
        return "[" + filterLabel + "] Class: " + className;
    }

    @Override
    public void writeTypeNBT(NBTTagCompound nbt) {
        nbt.setInteger("dbcClass", dbcClass.ordinal());
    }

    @Override
    public void readTypeNBT(NBTTagCompound nbt) {
        dbcClass = EnumDBCClasses.fromOrdinal(nbt.getInteger("dbcClass"));
    }

    public EnumDBCClasses getDbcClass() {
        return dbcClass;
    }

    public void setDbcClass(EnumDBCClasses dbcClass) {
        this.dbcClass = dbcClass;
    }
}
