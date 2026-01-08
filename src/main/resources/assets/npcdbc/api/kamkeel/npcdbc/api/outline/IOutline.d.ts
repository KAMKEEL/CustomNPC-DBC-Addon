/**
 * Generated from Java file for CustomNPC+ Minecraft Mod 1.7.10
 * Package: kamkeel.npcdbc.api.outline
 */

export interface IOutline {
    setInnerColor(color: number, alpha: number): void;
    setOuterColor(color: number, alpha: number): void;
    setSize(size: number): import('./IOutline').IOutline;
    setNoiseSize(size: number): import('./IOutline').IOutline;
    setSpeed(speed: number): import('./IOutline').IOutline;
    setPulsingSpeed(speed: number): import('./IOutline').IOutline;
    setColorSmoothness(smoothness: number): import('./IOutline').IOutline;
    setColorInterpolation(interp: number): import('./IOutline').IOutline;
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
     * @param newID new ID.
     */
    setID(newID: number): void;
    /**
     * @return clones this IAura object and returns a new IAura with the same exact properties
     */
    clone(): import('./IOutline').IOutline;
    /**
     * @return Saves all Aura data. Always call this when changing any aura data
     */
    save(): import('./IOutline').IOutline;
}

