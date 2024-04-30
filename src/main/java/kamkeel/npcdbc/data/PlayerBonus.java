package kamkeel.npcdbc.data;

import kamkeel.npcdbc.api.effect.IPlayerBonus;
import kamkeel.npcdbc.constants.DBCAttribute;
import net.minecraft.nbt.NBTTagCompound;

public class PlayerBonus implements IPlayerBonus {

    public String name;
    public float strength;
    public float dexterity;
    public float willpower;

    public PlayerBonus(String name, float strength, float dexterity, float willpower) {
        this.name = name;
        this.strength = strength;
        this.dexterity = dexterity;
        this.willpower = willpower;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getStrength() {
        return strength;
    }

    @Override
    public void setStrength(byte strength) {
        this.strength = strength;
    }

    @Override
    public float getDexterity() {
        return dexterity;
    }

    @Override
    public void setDexterity(byte dexterity) {
        this.dexterity = dexterity;
    }

    @Override
    public float getWillpower() {
        return willpower;
    }

    @Override
    public void setWillpower(byte willpower) {
        this.willpower = willpower;
    }

    public static PlayerBonus readBonusData(NBTTagCompound nbt) {
        String name = nbt.getString("ID");
        float strength = nbt.getFloat(String.valueOf(DBCAttribute.Strength));
        float dexterity = nbt.getFloat(String.valueOf(DBCAttribute.Dexterity));
        float willpower = nbt.getFloat(String.valueOf(DBCAttribute.Willpower));
        return new PlayerBonus(name, strength, dexterity, willpower);
    }

    public NBTTagCompound writeBonusData(NBTTagCompound nbt) {
        nbt.setString("ID", this.name);
        nbt.setFloat(String.valueOf(DBCAttribute.Strength), strength);
        nbt.setFloat(String.valueOf(DBCAttribute.Dexterity), dexterity);
        nbt.setFloat(String.valueOf(DBCAttribute.Willpower), willpower);
        return nbt;
    }
}
