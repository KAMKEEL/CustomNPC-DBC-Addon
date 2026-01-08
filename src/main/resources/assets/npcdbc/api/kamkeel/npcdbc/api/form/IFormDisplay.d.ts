/**
 * Generated from Java file for CustomNPC+ Minecraft Mod 1.7.10
 * Package: kamkeel.npcdbc.api.form
 */

/**
 * This interface is heavily based on how DBC calculates its form masteries. Please check any race's form_mastery.cfg config to
 * get a better understanding on how this interface functions
 */
export interface IFormDisplay {
    getHairCode(): string;
    /**
     * @param hairCode The hair code to set transformation's hair to, usually gotten from the JinGames Hair Salon,
     *                 "bald" for no hair
     */
    setHairCode(hairCode: string): void;
    /**
     * @param type Legal types: hud, aura, hair, eye, bodycm, bodyc1, bodyc2, bodyc3
     * @return Decimal color of type
     */
    getColor(type: string): number;
    /**
     * @param type  Legal types: hud, aura, hair, eye, bodycm, bodyc1, bodyc2, bodyc3, fur
     * @param color Decimal or hexadecimal color (i.e. 0xffffff for white) to set type as
     */
    setColor(type: string, color: number): void;
    /**
     * @param type Legal types: "base", "raditz", "ssj", "ssj2", "ssj3", "ssj4", "oozaru", "" for no type
     *             Only saiyans and half-saiyans are eligible for ssj4 and oozaru
     */
    setHairType(type: string): void;
    getHairType(): string;
    /**
     * @param type Legal types: hud, aura, hair, eye, bodycm, body1, body2, body3
     * @return True if form has the "hasTypeColor" field to true,
     * useful for Namekians and Arcosians multi body color setting
     */
    hasColor(type: string): boolean;
    isBerserk(): boolean;
    setBerserk(isBerserk: boolean): void;
    hasEyebrows(): boolean;
    hasEyebrows(has: boolean): void;
    /**
     * @return form's size, default is 1.0f of player's current size
     */
    getSize(): number;
    /**
     * @param size size to set form to. 2.0f sets the player to 2x their normal size. Min: 0.2, Max: 3.0
     */
    setSize(size: number): void;
    getKeepOriginalSize(): boolean;
    /**
     * @param keepOriginalSize True if you want CustomForm to maintain vanilla DBC size when stacking forms.
     *                         <p>
     *                         i.e if Giant form has a size of 3.0x normal minecraft steve size, enabling this
     *                         and setting formSize to 2.0 will cause the effective size to be 6.0x steve size.
     *                         Disabling it overrides vanilla DBC sizes this way a formSize of 2.0 will always be 2.0x steve size
     */
    setKeepOriginalSize(keepOriginalSize: boolean): void;
    /**
     * @return form's width, default is 1.0f of player's current size
     */
    getWidth(): number;
    /**
     * @param width size to set form to. 2.0f sets the player to 2x their normal size. Min: 0.2, Max: 3.0
     */
    setWidth(width: number): void;
    hasSize(): boolean;
    getBodyType(): string;
    /**
     * So far this is only for arcosian race.
     *
     * @param type Legal: firstform, secondform, thirdform, finalform, ultimatecooler, ultimate
     */
    setBodyType(type: string): void;
    hasArcoMask(): boolean;
    /**
     * @param hasMask True if you want the form to render with Ultimate Cooler's mask
     */
    hasArcoMask(hasMask: boolean): void;
    hasBodyFur(): boolean;
    /**
     * @param hasFur True if you want the form to render with fur on body
     */
    hasBodyFur(hasFur: boolean): void;
    effectMajinHair(): boolean;
    /**
     * @param effect True to allow majin CFs to have different hairType like SSJ,SSJ2 and SSJ3
     */
    setEffectMajinHair(effect: boolean): void;
    /**
     * @return True if form has a custom IAura object
     */
    hasAura(): boolean;
    getAura(): import('../aura/IAura').IAura;
    /**
     * @param aura Set the IAura object you want the form to use
     */
    setAura(aura: import('../aura/IAura').IAura): void;
    /**
     * @param auraID ID of IAura object
     */
    setAura(auraID: number): void;
    setOutline(id: number): void;
    setOutline(outline: import('../outline/IOutline').IOutline): void;
    /**
     * Saves CustomForm with the New Form Display Modifications
     *
     * @return IFormDisplay self object
     */
    save(): import('./IFormDisplay').IFormDisplay;
    /**
     * Allows the player to edit their custom form's appearance.
     *
     * @param customizable true or false.
     */
    setCustomizable(customizable: boolean): void;
    /**
     * @return If the form colors are customizable by players.
     */
    isCustomizable(): boolean;
}

