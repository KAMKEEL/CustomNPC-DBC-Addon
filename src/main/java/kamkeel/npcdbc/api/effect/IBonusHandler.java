package kamkeel.npcdbc.api.effect;

import noppes.npcs.api.entity.IPlayer;

public interface IBonusHandler {

    void clearBonuses(IPlayer player);

    IPlayerBonus createBonus(String name, float str, float dex, float wil);

    IPlayerBonus createBonus(String name, float str, float dex, float wil, float con, float spi);

    /**
     * @param name Name of Bonus
     * @param type 0 - Multi, 1 is Added to Attributes
     * @param str  Strength Amount
     * @param dex  Dex Amount
     * @param wil  Willpower Amount
     * @param con  Constitution Amount
     * @param spi  Spirit Amount
     * @return The IPlayerBonus Object to apply
     */
    IPlayerBonus createBonus(String name, int type, float str, float dex, float wil, float con, float spi);

    boolean hasBonus(IPlayer player, String name);

    boolean hasBonus(IPlayer player, IPlayerBonus bonus);

    void applyBonus(IPlayer player, String name, float str, float dex, float wil);

    void applyBonus(IPlayer player, IPlayerBonus bonus);

    void removeBonus(IPlayer player, String name);

    void removeBonus(IPlayer player, IPlayerBonus bonus);
}
