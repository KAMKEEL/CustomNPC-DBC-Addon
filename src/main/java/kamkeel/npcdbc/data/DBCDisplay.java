package kamkeel.npcdbc.data;

import kamkeel.npcdbc.api.IDBCDisplay;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes;
import net.minecraft.nbt.NBTTagCompound;

public class DBCDisplay implements IDBCDisplay {

    public boolean enabled = false;
    private EnumAuraTypes enumAuraTypes = EnumAuraTypes.None;

    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setBoolean("DBCDisplayEnabled", enabled);
        if(enabled){
            nbttagcompound.setInteger("DBCDisplayAura", enumAuraTypes.ordinal());
        }
        return nbttagcompound;
    }

    public void readFromNBT(NBTTagCompound nbttagcompound) {
        enabled = nbttagcompound.getBoolean("DBCDisplayEnabled");
        if(enabled) {
            enumAuraTypes = EnumAuraTypes.values()[nbttagcompound.getInteger("DBCDisplayAura") % EnumAuraTypes.values().length];
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public EnumAuraTypes getFormAuraTypes() {
        return enumAuraTypes;
    }

    public void setFormAuraTypes(EnumAuraTypes enumAuraTypes) {
        this.enumAuraTypes = enumAuraTypes;
    }

    @Override
    public void setFormAuraTypes(String type) {
        this.enumAuraTypes = EnumAuraTypes.valueOf(type);
    }
}
