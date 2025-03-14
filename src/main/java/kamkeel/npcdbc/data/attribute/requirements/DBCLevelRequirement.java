package kamkeel.npcdbc.data.attribute.requirements;

import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcs.controllers.data.attribute.requirement.IRequirementChecker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import static JinRyuu.JRMCore.JRMCoreH.getPlayerLevel;

public class DBCLevelRequirement implements IRequirementChecker {

    @Override
    public String getKey() {
        return "dbc_level";
    }

    @Override
    public String getTranslation() {
        return "jinryuujrmcore.Level";
    }

    @Override
    public String getTooltipValue(NBTTagCompound nbt) {
        if(nbt.hasKey(getKey())) {
            int levelRequirement = nbt.getInteger(getKey());
            return levelRequirement + "";
        }
        return "null";
    }

    @Override
    public Object getValue(NBTTagCompound nbtTagCompound) {
        if(nbtTagCompound.hasKey(getKey())) {
            return nbtTagCompound.getInteger(getKey());
        }
        return null;
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
            int level = nbt.getInteger(getKey());
            DBCData dbcData = DBCData.get(player);
            int calculatedLevel = getPlayerLevel(dbcData.STR + dbcData.DEX + dbcData.CON + dbcData.WIL + dbcData.MND + dbcData.SPI);
            return calculatedLevel >= level;
        }
        return true;
    }
}
