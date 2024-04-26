package kamkeel.npcdbc.api.effect;

import noppes.npcs.api.entity.IPlayer;

public interface IStatusEffect {

    int getId();

    int getDuration();

    void setDuration(int duration);

    byte getLevel();

    void setLevel(byte level);

    String getName();

    void performEffect(IPlayer player);
}
