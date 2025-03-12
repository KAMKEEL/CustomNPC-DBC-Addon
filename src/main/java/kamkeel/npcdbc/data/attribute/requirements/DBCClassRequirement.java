package kamkeel.npcdbc.data.attribute.requirements;

import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcs.controllers.data.attribute.requirement.IRequirementChecker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import static JinRyuu.JRMCore.JRMCoreH.ClassesDBC;

public class DBCClassRequirement implements IRequirementChecker {

    @Override
    public String getKey() {
        return "dbc_class";
    }

    @Override
    public String getTranslation() {
        return "jinryuujrmcore.Class";
    }

    @Override
    public String getTooltipValue(NBTTagCompound nbt) {
        if(nbt.hasKey(getKey())) {
            int classID = nbt.getInteger(getKey());
            try{
                return ClassesDBC[classID];
            } catch (Exception ignored) {}
        }
        return "null";
    }

    @Override
    public void apply(NBTTagCompound nbt, Object value) {
        if(value instanceof Integer) {
            nbt.setInteger(getKey(), (Integer) value);
        }
    }

    @Override
    public boolean check(EntityPlayer player, NBTTagCompound nbt) {
        if(nbt.hasKey(getKey())) {
            int classID = nbt.getInteger(getKey());
            DBCData dbcData = DBCData.get(player);
            return dbcData.Class == classID;
        }
        return true;
    }
}
