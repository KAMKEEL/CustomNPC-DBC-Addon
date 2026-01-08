/**
 * Generated from Java file for CustomNPC+ Minecraft Mod 1.7.10
 * Package: kamkeel.npcdbc.api.aura
 */

export interface IAura {
    getName(): string;
    /**
     * @param name Name of the aura. Must be unique to each aura
     */
    setName(name: string): void;
    getMenuName(): string;
    /**
     * @param name Name of aura to be displayed in all aura rendering, whether Aura Selection GUI or DBC stat sheet or chat. Minecraft Color codes are allowed "&amp;4&amp;l"
     */
    setMenuName(name: string): void;
    getID(): number;
    /**
     * Do not use this unless you know what you are changing. Dangerous to change.
     *
     * @param newID new Aura ID
     */
    setID(newID: number): void;
    /**
     * @param player Player to give this aura to
     */
    assignToPlayer(player: IPlayer): void;
    removeFromPlayer(player: IPlayer): void;
    assignToPlayer(playerName: string): void;
    removeFromPlayer(playerName: string): void;
    /**
     * @return An interface containing getters and setters for all the aura's rendering data
     */
    getDisplay(): import('./IAuraDisplay').IAuraDisplay;
    getSecondaryAuraID(): number;
    getSecondaryAura(): import('./IAura').IAura;
    setSecondaryAura(id: number): void;
    setSecondaryAura(aura: import('./IAura').IAura): void;
    /**
     * @return clones this IAura object and returns a new IAura with the same exact properties
     */
    clone(): import('./IAura').IAura;
    /**
     * @return Saves all Aura data. Always call this when changing any aura data
     */
    save(): import('./IAura').IAura;
}

