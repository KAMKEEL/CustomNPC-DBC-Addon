package kamkeel.npcdbc.api.effect;

import noppes.npcs.api.entity.IPlayer;

public interface IPlayerEffect {

    void kill();

    int getId();

    int getDuration();

    void setDuration(int duration);

    byte getLevel();

    void setLevel(byte level);

    String getName();

    void performEffect(IPlayer player);
}
