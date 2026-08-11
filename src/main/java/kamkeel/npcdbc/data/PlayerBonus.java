package kamkeel.npcdbc.data;

import kamkeel.npcdbc.api.effect.IPlayerBonus;
import kamkeel.npcdbc.constants.DBCAttribute;
import kamkeel.npcdbc.constants.DBCStatistics;
import net.minecraft.nbt.NBTTagCompound;

public class PlayerBonus implements IPlayerBonus {

    /**
     * Number of statistics that run through the DBC stat pipeline,
     * covering {@link DBCStatistics#Melee} through {@link DBCStatistics#MaxSkills}.
     */
    public static final int STAT_COUNT = 7;

    public String name;
    // Type 0 = Percentage (additive stacking, applied as % of base)
    // Type 1 = Flat (direct addition)
    // Type 2 = Multiplicative (true percentage multiplication, each bonus compounds)
    public byte type;

    public float strength = 0;
    public float dexterity = 0;
    public float willpower = 0;

    public float constituion = 0;
    public float spirit = 0;

    public final float[] stats = new float[STAT_COUNT];

    public PlayerBonus(String name, byte type) {
        this.name = name;
        this.type = type;
    }

    public PlayerBonus(String name, byte type, float strength, float dexterity, float willpower) {
        this.name = name;
        this.strength = sanitize(strength);
        this.dexterity = sanitize(dexterity);
        this.willpower = sanitize(willpower);
        this.type = type;
    }

    public PlayerBonus(String name, byte type, float strength, float dexterity, float willpower, float con, float spirit) {
        this.name = name;
        this.strength = sanitize(strength);
        this.dexterity = sanitize(dexterity);
        this.willpower = sanitize(willpower);
        this.spirit = sanitize(spirit);
        this.constituion = sanitize(con);
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
    public void setStrength(float strength) {
        this.strength = sanitize(strength);
    }

    @Override
    public float getDexterity() {
        return dexterity;
    }

    @Override
    public void setDexterity(float dexterity) {
        this.dexterity = sanitize(dexterity);
    }

    @Override
    public float getWillpower() {
        return willpower;
    }

    @Override
    public void setWillpower(float willpower) {
        this.willpower = sanitize(willpower);
    }

    @Override
    public float getConstitution() {
        return constituion;
    }

    @Override
    public void setConstitution(float constitution) {
        this.constituion = sanitize(constitution);
    }

    @Override
    public float getSpirit() {
        return spirit;
    }

    @Override
    public void setSpirit(float spirit) {
        this.spirit = sanitize(spirit);
    }

    @Override
    public float getStat(int statID) {
        if (statID < 0 || statID >= STAT_COUNT)
            return 0;
        return stats[statID];
    }

    @Override
    public void setStat(int statID, float value) {
        if (statID < 0 || statID >= STAT_COUNT)
            return;
        stats[statID] = sanitize(value);
    }

    @Override
    public float getMelee() {
        return stats[DBCStatistics.Melee];
    }

    @Override
    public void setMelee(float melee) {
        stats[DBCStatistics.Melee] = sanitize(melee);
    }

    @Override
    public float getDefense() {
        return stats[DBCStatistics.Defense];
    }

    @Override
    public void setDefense(float defense) {
        stats[DBCStatistics.Defense] = sanitize(defense);
    }

    @Override
    public float getBody() {
        return stats[DBCStatistics.Body];
    }

    @Override
    public void setBody(float body) {
        stats[DBCStatistics.Body] = sanitize(body);
    }

    @Override
    public float getStamina() {
        return stats[DBCStatistics.Stamina];
    }

    @Override
    public void setStamina(float stamina) {
        stats[DBCStatistics.Stamina] = sanitize(stamina);
    }

    @Override
    public float getEnergyPower() {
        return stats[DBCStatistics.EnergyPower];
    }

    @Override
    public void setEnergyPower(float energyPower) {
        stats[DBCStatistics.EnergyPower] = sanitize(energyPower);
    }

    @Override
    public float getEnergyPool() {
        return stats[DBCStatistics.EnergyPool];
    }

    @Override
    public void setEnergyPool(float energyPool) {
        stats[DBCStatistics.EnergyPool] = sanitize(energyPool);
    }

    @Override
    public float getMaxSkills() {
        return stats[DBCStatistics.MaxSkills];
    }

    @Override
    public void setMaxSkills(float maxSkills) {
        stats[DBCStatistics.MaxSkills] = sanitize(maxSkills);
    }

    public float[] getValues() {
        return new float[]{strength, dexterity, willpower, constituion, spirit};
    }

    public float[] getStatValues() {
        return stats.clone();
    }

    /**
     * NaN and infinity are rejected so a bad script value cannot poison the whole bonus stack.
     */
    private static float sanitize(float value) {
        return (Float.isNaN(value) || Float.isInfinite(value)) ? 0.0F : value;
    }

    public static PlayerBonus readBonusData(NBTTagCompound nbt) {
        String name = nbt.getString("ID");
        byte type = nbt.getByte("Type");
        float strength = nbt.getFloat(String.valueOf(DBCAttribute.Strength));
        float dexterity = nbt.getFloat(String.valueOf(DBCAttribute.Dexterity));
        float willpower = nbt.getFloat(String.valueOf(DBCAttribute.Willpower));
        float con = nbt.getFloat(String.valueOf(DBCAttribute.Constitution));
        float spirit = nbt.getFloat(String.valueOf(DBCAttribute.Spirit));
        PlayerBonus bonus = new PlayerBonus(name, type, strength, dexterity, willpower, con, spirit);

        if (nbt.hasKey("Stats", 10)) {
            NBTTagCompound statTag = nbt.getCompoundTag("Stats");
            for (int i = 0; i < STAT_COUNT; i++)
                bonus.stats[i] = sanitize(statTag.getFloat(String.valueOf(i)));
        }
        return bonus;
    }

    public NBTTagCompound writeBonusData(NBTTagCompound nbt) {
        nbt.setString("ID", this.name);
        nbt.setByte("Type", this.type);
        nbt.setFloat(String.valueOf(DBCAttribute.Strength), strength);
        nbt.setFloat(String.valueOf(DBCAttribute.Dexterity), dexterity);
        nbt.setFloat(String.valueOf(DBCAttribute.Willpower), willpower);
        nbt.setFloat(String.valueOf(DBCAttribute.Constitution), constituion);
        nbt.setFloat(String.valueOf(DBCAttribute.Spirit), spirit);

        NBTTagCompound statTag = new NBTTagCompound();
        for (int i = 0; i < STAT_COUNT; i++)
            statTag.setFloat(String.valueOf(i), stats[i]);
        nbt.setTag("Stats", statTag);
        return nbt;
    }
}
