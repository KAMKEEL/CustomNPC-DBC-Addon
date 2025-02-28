package kamkeel.npcdbc.api.effect;

public interface IPlayerBonus {

    String getName();

    byte getType();

    void setType(byte type);

    float getStrength();

    void setStrength(float strength);

    float getDexterity();

    void setDexterity(float dexterity);

    float getWillpower();

    void setWillpower(float willpower);

    float getConstitution();

    void setConstitution(float constitution);

    float getSpirit();

    void setSpirit(float spirit);
}
