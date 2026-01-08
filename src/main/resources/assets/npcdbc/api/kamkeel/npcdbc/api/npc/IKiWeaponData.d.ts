/**
 * Generated from Java file for CustomNPC+ Minecraft Mod 1.7.10
 * Package: kamkeel.npcdbc.api.npc
 */

export interface IKiWeaponData {
    isEnabled(): boolean;
    /**
     * Sets the weapon type
     *
     * @param type - 0 is off, 1 is Ki Blade, 2 is Ki Scythe
     */
    setWeaponType(type: number): void;
    /**
     * @return - 0 is off, 1 is Ki Blade, 2 is Ki Scythe
     */
    getWeaponType(): number;
    /**
     * Sets the weapon color.
     *
     * @param color If it's set to -1, it relies on current aura color.
     * @param alpha opacity of the color. 0 - Transparent, 1 - Opaque
     */
    setColor(color: number, alpha: number): void;
    getColor(): number;
    setXOffset(offset: number): void;
    setYOffset(offset: number): void;
    setZOffset(offset: number): void;
    getXOffset(): number;
    getYOffset(): number;
    getZOffset(): number;
    setXScale(scale: number): void;
    setYScale(scale: number): void;
    setZScale(scale: number): void;
    getXScale(): number;
    getYScale(): number;
    getZScale(): number;
}

