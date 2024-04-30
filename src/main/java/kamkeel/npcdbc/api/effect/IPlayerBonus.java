package kamkeel.npcdbc.api.effect;

public interface IPlayerBonus {

    String getName();

    float getStrength();

    void setStrength(byte strength);

    float getDexterity();

    void setDexterity(byte dexterity);

    float getWillpower();

    void setWillpower(byte willpower);
}
