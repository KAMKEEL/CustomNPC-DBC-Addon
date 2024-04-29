package kamkeel.npcdbc.api.effect;

public interface IPlayerBonus {

    String getName();

    byte getStrength();

    void setStrength(byte strength);

    byte getDexterity();

    void setDexterity(byte dexterity);

    byte getWillpower();

    void setWillpower(byte willpower);
}
