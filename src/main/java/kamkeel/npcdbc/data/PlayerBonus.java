package kamkeel.npcdbc.data;

import kamkeel.npcdbc.api.effect.IPlayerBonus;
import kamkeel.npcdbc.constants.DBCAttribute;
import net.minecraft.nbt.NBTTagCompound;

public class PlayerBonus implements IPlayerBonus {

    public String name;
    public byte type; // Type 0 is Multi, Type 1 is Addition

    public float strength;
    public float dexterity;
    public float willpower;

    public float constituion = 0;
    public float spirit = 0;

    public PlayerBonus(String name, byte type, float strength, float dexterity, float willpower) {
        this.name = name;
        this.strength = strength;
        this.dexterity = dexterity;
        this.willpower = willpower;
        this.type = type;
    }

    public PlayerBonus(String name, byte type, float strength, float dexterity, float willpower, float con, float spirit) {
        this.name = name;
        this.strength = strength;
        this.dexterity = dexterity;
        this.willpower = willpower;
        this.spirit = spirit;
        this.constituion = con;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public byte getType() {
        return type;
    }

    @Override
    public void setType(byte type) {
        this.type = type;
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

    @Override
    public float getConstituion() {
        return constituion;
    }

    @Override
    public void setConstituion(float constituion) {
        this.constituion = constituion;
    }

    @Override
    public float getSpirit() {
        return spirit;
    }

    @Override
    public void setSpirit(float spirit) {
        this.spirit = spirit;
    }

    public static PlayerBonus readBonusData(NBTTagCompound nbt) {
        String name = nbt.getString("ID");
        byte type = nbt.getByte("Type");
        float strength = nbt.getFloat(String.valueOf(DBCAttribute.Strength));
        float dexterity = nbt.getFloat(String.valueOf(DBCAttribute.Dexterity));
        float willpower = nbt.getFloat(String.valueOf(DBCAttribute.Willpower));
        float con = nbt.getFloat(String.valueOf(DBCAttribute.Constitution));
        float spirit = nbt.getFloat(String.valueOf(DBCAttribute.Spirit));
        return new PlayerBonus(name, type, strength, dexterity, willpower, con, spirit);
    }

    public NBTTagCompound writeBonusData(NBTTagCompound nbt) {
        nbt.setString("ID", this.name);
        nbt.setByte("Type", this.type);
        nbt.setFloat(String.valueOf(DBCAttribute.Strength), strength);
        nbt.setFloat(String.valueOf(DBCAttribute.Dexterity), dexterity);
        nbt.setFloat(String.valueOf(DBCAttribute.Willpower), willpower);
        nbt.setFloat(String.valueOf(DBCAttribute.Constitution), constituion);
        nbt.setFloat(String.valueOf(DBCAttribute.Spirit), spirit);
        return nbt;
    }
}
