/**
 * Generated from Java file for CustomNPC+ Minecraft Mod 1.7.10
 * Package: kamkeel.npcdbc.api.npc
 */

export interface IDBCDisplay {
    /**
     * @param type  Legal types: hair, eye, bodycm, bodyc1, bodyc2, bodyc3, fur
     * @param color Decimal or hexadecimal color (i.e. 0xffffff for white) color to set type as
     */
    setColor(type: string, color: number): void;
    getColor(type: string): number;
    isEnabled(): boolean;
    /**
     * @param enabled enabling this display allows everything in this interface to function
     */
    setEnabled(enabled: boolean): void;
    getHairCode(): string;
    setHairCode(hairCode: string): void;
    /**
     * @param arm 0 is right arm, 1 is left arm
     * @return Ki Weapon Data for the given arm.
     */
    getKiWeapon(arm: number): import('./IKiWeaponData').IKiWeaponData;
    getRace(): number;
    /**
     * @param race 0 is Human, 1 Saiyan, 2 Half-Saiyan, 3 Namekian, 4 Arcosian, 5 Majin
     */
    setRace(race: number): void;
    /**
     * Namekian/arcosian body types
     *
     * @param bodyType from 0 to 2.
     */
    setBodyType(bodyType: number): void;
    getBodyType(): number;
    getTailState(): number;
    /**
     * @param tail 0 for straight, 1 for wrapped, 2 for arcosian tail (if race is arco), anything else for no tail
     */
    setTailState(tail: number): void;
    /**
     * @param type Legal types: "base", "raditz", "ssj", "ssj2", "ssj3", "ssj4", "oozaru", "" for no type
     *             Only saiyans and half-saiyans are eligible for ssj4 and oozaru
     */
    setHairType(type: string): void;
    getHairType(): string;
    hasCoolerMask(): boolean;
    /**
     * @param has set
     */
    setHasCoolerMask(has: boolean): void;
    hasEyebrows(): boolean;
    setHasEyebrows(has: boolean): void;
    hasBodyFur(): boolean;
    setHasBodyFur(hasFur: boolean): void;
    hasAura(): boolean;
    getAura(): import('../aura/IAura').IAura;
    setAura(aura: import('../aura/IAura').IAura): void;
    setAura(auraID: number): void;
    isAuraToggled(): boolean;
    toggleAura(toggle: boolean): void;
    isInAura(aura: import('../aura/IAura').IAura): boolean;
    getOutline(): import('../outline/IOutline').IOutline;
    setOutline(id: number): void;
    setOutline(outline: import('../outline/IOutline').IOutline): void;
    /**
     * @param id Transforms to the form with this ID
     */
    transform(id: number): void;
    /**
     * @param form Transforms to this form
     */
    transform(form: import('../form/IForm').IForm): void;
    /**
     * if NPC is currently undergoing transformation, cancel it
     */
    cancelTransformation(): void;
    /**
     * @param id descends from current form to the form with this ID
     *           Set ID to -1 if to base
     */
    descend(id: number): void;
    /**
     * @param form descends from current form to this form
     *             set form to null for descending to base
     */
    descend(form: import('../form/IForm').IForm): void;
    setForm(id: number): void;
    setForm(form: import('../form/IForm').IForm): void;
    getCurrentForm(): import('../form/IForm').IForm;
    isInForm(form: import('../form/IForm').IForm): boolean;
    setFormLevel(amount: number): void;
    getFormLevel(formID: number): number;
}

