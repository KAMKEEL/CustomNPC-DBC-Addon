/**
 * Generated from Java file for CustomNPC+ Minecraft Mod 1.7.10
 * Package: kamkeel.npcdbc.api.form
 */

export interface IForm {
    getName(): string;
    /**
     * @param name Name of the form. Must be unique to each form
     */
    setName(name: string): void;
    getMenuName(): string;
    /**
     * @param name Name of form to be displayed in all form rendering, whether Form Selection GUI or DBC stat sheet or chat. Minecraft Color codes are allowed "&amp;4&amp;l"
     */
    setMenuName(name: string): void;
    getRace(): number;
    /**
     * @param race The race ID of the form. Only player's of raceID can access this form. -1 for ALL RACES
     */
    setRace(race: number): void;
    /**
     * @return An array of str,dex,will multipliers. index 0 is strengthMulti,1 dexMulti, 2 willMulti
     */
    getAllMulti(): number[];
    /**
     * @param allMulti Sets strength, dex and willpower multipliers to this value
     */
    setAllMulti(allMulti: number): void;
    raceEligible(race: number): boolean;
    raceEligible(player: IPlayer): boolean;
    /**
     * @param id    0 for Strength, 1 for Dex, 3 for Willpower
     * @param multi attribute multiplier for given stat
     */
    setAttributeMulti(id: number, multi: number): void;
    /**
     * @param id 0 for Strength, 1 for Dex, 3 for Willpower
     * @return Attribute multiplier for given stat
     */
    getAttributeMulti(id: number): number;
    /**
     * @param player Player to give this form to
     */
    assignToPlayer(player: IPlayer): void;
    removeFromPlayer(player: IPlayer): void;
    assignToPlayer(playerName: string): void;
    removeFromPlayer(playerName: string): void;
    removeFromPlayer(ScriptDBCAddon: IPlayer, removesMastery: boolean): void;
    removeFromPlayer(playerName: string, removesMastery: boolean): void;
    getAscendSound(): string;
    /**
     * @param directory Sound effect to play on form ascension
     */
    setAscendSound(directory: string): void;
    getDescendSound(): string;
    /**
     * @param directory Sound effect to play on descending
     */
    setDescendSound(directory: string): void;
    getID(): number;
    /**
     * Do not use this unless you know what you are changing. Dangerous to change.
     *
     * @param newID new ID of the form.
     */
    setID(newID: number): void;
    /**
     * @return ID of form next in chain of transformation to this i.e SSJ2 Red is child of SSJ Red
     */
    getChildID(): number;
    hasChild(): boolean;
    /**
     * @param formID form ID to of child to this form. i.e id of SSJ2 Red to link to SSJ Red
     */
    linkChild(formID: number): void;
    linkChild(form: import('./IForm').IForm): void;
    isFromParentOnly(): boolean;
    setFromParentOnly(set: boolean): void;
    addFormRequirement(race: number, state: number): void;
    removeFormRequirement(race: number): void;
    getFormRequirement(race: number): number;
    isChildOf(parent: import('./IForm').IForm): boolean;
    /**
     * @return the child of this form i.e if SSJ2 Red is child of SSJ Red(this), returns SSJ2 Red
     */
    getChild(): import('./IForm').IForm;
    /**
     * removes the child of this form
     */
    removeChildForm(): void;
    /**
     * @return ID of form's parent i.e SSJ Red is parent of SSJ2 Red
     */
    getParentID(): number;
    hasParent(): boolean;
    /**
     * @param formID ID of parent of to link to this form.
     */
    linkParent(formID: number): void;
    linkParent(form: import('./IForm').IForm): void;
    /**
     * @return the parent of this form i.e if SSJ2 Red is Parent of SSJ Red(this), returns SSJ2 Red
     */
    getParent(): import('./IForm').IForm;
    getTimer(): number;
    /**
     * @param timeInTicks Sets the form's timer. When this timer runs out, player reverts from form
     */
    setTimer(timeInTicks: number): void;
    hasTimer(): boolean;
    /**
     * removes the form's parent
     */
    removeParentForm(): void;
    /**
     * @return An interface containing getters and setters for all the form's mastery data
     */
    getMastery(): import('./IFormMastery').IFormMastery;
    /**
     * @return An interface containing getters and setters for all the form's rendering data
     */
    getDisplay(): import('./IFormDisplay').IFormDisplay;
    /**
     * @return An interface containing getters and setters for all data on the form's interactions with vanilla DBC forms
     */
    getStackable(): import('./IFormStackable').IFormStackable;
    getAdvanced(): import('./IFormAdvanced').IFormAdvanced;
    setMindRequirement(mind: number): void;
    getMindRequirement(): number;
    /**
     * @return clones this IForm object and returns a new IForm with the same exact properties
     */
    clone(): import('./IForm').IForm;
    save(): import('./IForm').IForm;
}

