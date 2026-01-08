/**
 * Generated from Java file for CustomNPC+ Minecraft Mod 1.7.10
 * Package: kamkeel.npcdbc.api.effect
 */

export interface IBonusHandler {
    clearBonuses(player: IPlayer): void;
    createBonus(name: string, str: number, dex: number, wil: number): import('./IPlayerBonus').IPlayerBonus;
    createBonus(name: string, str: number, dex: number, wil: number, con: number, spi: number): import('./IPlayerBonus').IPlayerBonus;
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
    createBonus(name: string, type: number, str: number, dex: number, wil: number, con: number, spi: number): import('./IPlayerBonus').IPlayerBonus;
    hasBonus(player: IPlayer, name: string): boolean;
    hasBonus(player: IPlayer, bonus: import('./IPlayerBonus').IPlayerBonus): boolean;
    applyBonus(player: IPlayer, name: string, str: number, dex: number, wil: number): void;
    applyBonus(player: IPlayer, bonus: import('./IPlayerBonus').IPlayerBonus): void;
    removeBonus(player: IPlayer, name: string): void;
    removeBonus(player: IPlayer, bonus: import('./IPlayerBonus').IPlayerBonus): void;
}

