/**
 * Generated from Java file for CustomNPC+ Minecraft Mod 1.7.10
 * Package: kamkeel.npcdbc.api.aura
 */

export interface IAuraDisplay {
    getKettleModeEnabled(): boolean;
    /**
     * @param set True if kettle mode is always on regardless of charging/kettleModeCharging
     */
    setKettleModeEnabled(set: boolean): void;
    getKettleModeCharging(): boolean;
    /**
     * @param set True if kettle mode is only when charging ki/transforming;
     */
    setKettleModeCharging(set: boolean): void;
    getOverrideDBCAura(): boolean;
    setOverrideDBCAura(override: boolean): Aura;
    toggleKaioken(toggle: boolean): void;
    getKaiokenSize(): number;
    setKaiokenSize(size: number): void;
    getKaiokenSound(): string;
    /**
     * @param soundDirectory "customnpcs:auras.kaioken_aura"
     *                       Set to "nosound" to disable kaioken aura sound,
     *                       Default is "", which plays "jinryuudragonbc:1610.aurabk".
     */
    setKaiokenSound(soundDirectory: string): void;
    hasSound(): boolean;
    getAuraSound(): string;
    /**
     * @param soundDirectory resource location of sound i.e "customnpcs:auras.majin_aura"
     *                       Setting this to "default" allows the sound to dynamically change according to the aura's getType()
     */
    setAuraSound(soundDirectory: string): void;
    isKaiokenToggled(): boolean;
    /**
     * @return legal types: ssgod, godofdestruction, ui, ssb, ssrose, ssroseevo, ssbkk, shinka
     */
    getType(): string;
    /**
     * @param auraType legal types: ssgod, godofdestruction, ui, ssb, ssrose, ssroseevo, ssbkk, shinka
     */
    setType(auraType: string): void;
    getType2D(): string;
    /**
     * @param type2D legal types: ssgod, godofdestruction, ui, mui, ssb, ssrose, ssroseevo, ssbkk, shinka
     */
    setType2D(type2D: string): void;
    hasColor(type: string): boolean;
    /**
     * -
     * Individual IAura Color Precedence:
     * 1. IForm's IAura object color followed by IForm's auraColor (Highest)
     * 2. Individual IAura Color
     * 3. IF overrideDBCForm &amp; in DBC Form -&gt; IAura uses DBC Form Colors [SSJ (yellow), Legendary (green), Divine (purple)]
     * 4. Individual IAura's Type Colors [GoD, UI, SSGod]
     * 5. Base Aura Color (Lowest)
     *
     * @param colorType Legal types: color1, color2, color3
     * @param color     hexadecimal color to set type to
     */
    setColor(colorType: string, color: number): void;
    /**
     * -
     * Individual IAura Color Precedence:
     * 1. Form's Aura
     * 2. Individual IAura Color
     * 3. IF overrideDBCForm &amp; in DBC Form -&gt; Use DBC Form Colors [SSJ, Shinka, Rose]
     * 4. Individual IAura's Type Colors [GoD, UI, SSGod]
     * 5. Base Aura Color
     *
     * @param colorType Legal types: color1, color2, color3
     * @return Decimal color of type
     */
    getColor(colorType: string): number;
    hasAlpha(type: string): boolean;
    getAlpha(type: string): number;
    /**
     * @param type  Legal types: aura, lightning
     * @param alpha value of alpha/transparency to set type to
     */
    setAlpha(type: string, alpha: number): void;
    /**
     * @param hasLightning True if aura is to have SSJ2 lightning effects
     */
    hasLightning(hasLightning: boolean): void;
    getHasLightning(): boolean;
    getLightningSpeed(): number;
    setLightningSpeed(lightningSpeed: number): void;
    getLightningIntensity(): number;
    setLightningIntensity(lightningIntensity: number): void;
    setLightningColor(color: number): void;
    getLightningColor(): number;
    hasSize(): boolean;
    getSize(): number;
    /**
     * @param size Value to set aura size to. Min 0.05, Max 10
     */
    setSize(size: number): void;
    hasSpeed(): boolean;
    getSpeed(): number;
    /**
     * @param speed How fast aura should go through its lifecycle in ticks. DBC Default is 40 ticks. The lower, the faster
     */
    setSpeed(speed: number): void;
    /**
     * @param textureType Legal types: texture1, texture2, texture3
     * @return resource location of texture
     */
    getTexture(textureType: string): string;
    /**
     * @param textureType     Legal types: texture1, texture2, texture3
     * @param textureLocation resource location of texture
     */
    setTexture(textureType: string, textureLocation: string): void;
    getOutlineAlwaysOn(): boolean;
    setOutlineAlwaysOn(alwaysOn: boolean): void;
    setOutline(id: number): void;
    setOutline(outline: import('../outline/IOutline').IOutline): void;
    save(): import('./IAuraDisplay').IAuraDisplay;
    doesAuraCopyDBCSuperformColors(): boolean;
    setDoesAuraCopyDBCSuperformColors(copyDBCSuperformColors: boolean): void;
}

