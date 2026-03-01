package kamkeel.npcdbc.data.ability.conditions;

import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcs.controllers.data.ability.enums.UserType;
import kamkeel.npcs.controllers.data.ability.conditions.ConditionThreshold;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.entity.EntityNPCInterface;

public class ConditionKiThreshold extends ConditionThreshold {

    public ConditionKiThreshold() {
        this.typeId = "condition.npcdbc.ki_threshold";
        this.name = "condition.npcdbc.ki_threshold";
        this.userType = UserType.PLAYER_ONLY;
    }

    @Override
    protected float getEntityValue(EntityLivingBase entity) {
        if (!(entity instanceof EntityPlayer)) return 0;
        return DBCData.get((EntityPlayer) entity).Ki;
    }

    @Override
    protected float getEntityMaxValue(EntityLivingBase entity) {
        if (!(entity instanceof EntityPlayer)) return 1;
        return DBCData.get((EntityPlayer) entity).stats.getMaxKi();
    }

    @Override
    protected boolean checkEntity(EntityLivingBase entity) {
        if (!(entity instanceof EntityPlayer)) return false;
        return super.checkEntity(entity);
    }

    @Override
    protected String getStatName() {
        return "Ki";
    }
}
