package kamkeel.npcdbc.data;

import kamkeel.npcdbc.api.effect.IPlayerBonus;
import kamkeel.npcdbc.constants.DBCAttribute;
import net.minecraft.nbt.NBTTagCompound;

public class PlayerBonus implements IPlayerBonus {

    public String name;
    public byte strength;
    public byte dexterity;
    public byte willpower;

    public PlayerBonus(String name, byte strength, byte dexterity, byte willpower) {
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
    public byte getStrength() {
        return strength;
    }

    @Override
    public void setStrength(byte strength) {
        this.strength = strength;
    }

    @Override
    public byte getDexterity() {
        return dexterity;
    }

    @Override
    public void setDexterity(byte dexterity) {
        this.dexterity = dexterity;
    }

    @Override
    public byte getWillpower() {
        return willpower;
    }

    @Override
    public void setWillpower(byte willpower) {
        this.willpower = willpower;
    }

    public static PlayerBonus readBonusData(NBTTagCompound nbt) {
        String name = nbt.getString("ID");
        byte strength = nbt.getByte(String.valueOf(DBCAttribute.Strength));
        byte dexterity = nbt.getByte(String.valueOf(DBCAttribute.Dexterity));
        byte willpower = nbt.getByte(String.valueOf(DBCAttribute.Willpower));
        return new PlayerBonus(name, strength, dexterity, willpower);
    }

    public NBTTagCompound writeBonusData(NBTTagCompound nbt) {
        nbt.setString("ID", this.name);
        nbt.setByte(String.valueOf(DBCAttribute.Strength), strength);
        nbt.setByte(String.valueOf(DBCAttribute.Dexterity), dexterity);
        nbt.setByte(String.valueOf(DBCAttribute.Willpower), willpower);
        return nbt;
    }
}
