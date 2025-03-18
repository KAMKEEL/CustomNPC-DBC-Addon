package kamkeel.npcdbc.data.attribute.requirements;

import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcs.controllers.data.attribute.requirement.IRequirementChecker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import static JinRyuu.JRMCore.JRMCoreH.Races;

public class DBCRaceRequirement implements IRequirementChecker {

    @Override
    public String getKey() {
        return "dbc_race";
    }

    @Override
    public String getTranslation() {
        return "jinryuujrmcore.Race";
    }

    @Override
    public String getTooltipValue(NBTTagCompound nbt) {
        if (nbt.hasKey(getKey())) {
            int race = nbt.getInteger(getKey());
            try {
                return Races[race];
            } catch (Exception ignored) {
            }
        }
        return "null";
    }

    @Override
    public void apply(NBTTagCompound nbt, Object value) {
        if (value instanceof Integer) {
            nbt.setInteger(getKey(), (Integer) value);
        }
    }

    @Override
    public Object getValue(NBTTagCompound nbtTagCompound) {
        if (nbtTagCompound.hasKey(getKey())) {
            return nbtTagCompound.getInteger(getKey());
        }
        return null;
    }

    @Override
    public boolean check(EntityPlayer player, NBTTagCompound nbt) {
        if (nbt.hasKey(getKey())) {
            int race = nbt.getInteger(getKey());
            DBCData dbcData = DBCData.get(player);
            return dbcData.Race == race;
        }
        return true;
    }
}
