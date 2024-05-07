package kamkeel.npcdbc.api.effect;

public interface IPlayerBonus {

    String getName();

    byte getType();

    void setType(byte type);

    float getStrength();

    void setStrength(byte strength);

    float getDexterity();

    void setDexterity(byte dexterity);

    float getWillpower();

    void setWillpower(byte willpower);

    float getConstituion();

    void setConstituion(float constituion);

    float getSpirit();

    void setSpirit(float spirit);
}
