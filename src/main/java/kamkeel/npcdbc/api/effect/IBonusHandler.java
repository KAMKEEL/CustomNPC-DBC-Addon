package kamkeel.npcdbc.api.effect;

import noppes.npcs.api.entity.IPlayer;

public interface IBonusHandler {

    IPlayerBonus createBonus(String name, float str, float dex, float wil);

    IPlayerBonus createBonus(String name, float str, float dex, float wil, float con, float spi);

    boolean hasBonus(IPlayer player, String name);

    boolean hasBonus(IPlayer player, IPlayerBonus bonus);

    void applyBonus(IPlayer player, String name, float str, float dex, float wil);

    void applyBonus(IPlayer player, IPlayerBonus bonus);

    void removeBonus(IPlayer player, String name);

    void removeBonus(IPlayer player, IPlayerBonus bonus);
}
