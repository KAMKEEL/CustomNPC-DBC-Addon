package kamkeel.npcdbc.data.ability.conditions;

import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcs.controllers.data.ability.conditions.AbilityCondition;
import kamkeel.npcs.controllers.data.ability.enums.UserType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.gui.builder.FieldDef;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.List;

public class ConditionFused extends AbilityCondition {

    public ConditionFused() {
        this.typeId = "condition.npcdbc.fused";
        this.name = "condition.npcdbc.fused";
        this.userType = UserType.PLAYER_ONLY;
    }

    @Override
    protected boolean checkEntity(EntityLivingBase entity) {
        if (entity instanceof EntityNPCInterface) return false;

        if (entity instanceof EntityPlayer) {
            DBCData data = DBCData.get((EntityPlayer) entity);

            return data.stats.isFused();
        }

        return false;
    }

    @Override
    public void getConditionDefinitions(List<FieldDef> defs) {

    }

    @Override
    public String getConditionSummary() {
        return StatCollector.translateToLocal(getFilter().toString());
    }

    @Override
    public void writeTypeNBT(NBTTagCompound nbt) {

    }

    @Override
    public void readTypeNBT(NBTTagCompound nbt) {

    }
}
