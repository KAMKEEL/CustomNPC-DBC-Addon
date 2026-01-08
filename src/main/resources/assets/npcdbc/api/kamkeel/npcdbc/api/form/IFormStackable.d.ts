/**
 * Generated from Java file for CustomNPC+ Minecraft Mod 1.7.10
 * Package: kamkeel.npcdbc.api.form
 */

/**
 * This interface is heavily based on controlling Forms interacting with other forms
 * from DBC Vanilla forms and Custom Forms
 */
export interface IFormStackable {
    /**
     * @param DBCForm Non-racial DBC Forms, 21 Mystic, 22 UI, 23 GoD
     * @param multi   multiplier
     */
    setFormMulti(DBCForm: number, multi: number): void;
    /**
     * @param DBCForm Non-racial DBC Forms, 21 Mystic, 22 UI, 23 GoD
     * @return DBCForm's multi
     */
    getFormMulti(DBCForm: number): number;
    setFusionForm(form: import('./IForm').IForm): void;
    getFusionFormID(): number;
    getFusionForm(): import('./IForm').IForm;
    /**
     * @param form Sets the form that this form is redirected to when in Legendary status effect
     */
    setLegendaryForm(form: import('./IForm').IForm): void;
    getLegendaryFormID(): number;
    getLegendaryForm(): import('./IForm').IForm;
    /**
     * @param form Sets the form that this form is redirected to when in Divine status effect
     */
    setDivineForm(form: import('./IForm').IForm): void;
    getDivineFormID(): number;
    getDivineForm(): import('./IForm').IForm;
    /**
     * @param form Sets the form that this form is redirected to when in Majin status effect
     */
    setMajinForm(form: import('./IForm').IForm): void;
    getMajinFormID(): number;
    getMajinForm(): import('./IForm').IForm;
    /**
     * @param DBCNonRacialFormID Legal values: 25 for Legendary, 26 Divine, 27 Majin
     * @param useConfig          True to use the DBC config multis for legendary majin, DBC addon config multi for Divine since DBC config doesn't have divine multi
     */
    useConfigMulti(DBCNonRacialFormID: number, useConfig: boolean): void;
    useConfigMulti(DBCNonRacialFormID: number): boolean;
    /**
     * @param DBCNonRacialFormID Non-racial DBC Forms, 20 Kaioken, 21 Mystic, 22 UI, 23 GoD
     * @return True if can stack DBCNonRacialFormID on Custom Form
     */
    isFormStackable(DBCNonRacialFormID: number): boolean;
    isVanillaStackable(): boolean;
    /**
     * @param vanillaStackable True makes this custom form stackable onto Vanilla DBC racial forms. i.e if you go into DBC SSBlue then go into a custom form with default presets, you will maintain the
     *                         appearance and aura of DBC SSBlue. However, custom forms always have stat calculation priority. If vanilla DBC SSBlue has multi of 100x, and CF has 10x, SSBlue CF will only have the 10x of CF multi.
     */
    setVanillaStackable(vanillaStackable: boolean): void;
    /**
     * @param DBCNonRacialFormID Non-racial DBC Forms, 20 Kaioken, 21 Mystic, 22 UI, 23 GoD
     * @param stackForm          true or false
     */
    allowStackForm(DBCNonRacialFormID: number, stackForm: boolean): void;
    getKaiokenConfigs(): import('./IFormKaiokenStackables').IFormKaiokenStackables;
    setState2Factor(dbcForm: number, factor: number): void;
    /**
     * @param dbcForm Legal args: 21 UltraInstinct
     * @return how higher a stackable form's multi gets as you go up in state2
     * i.e. If UI form multi is 10x and State2Factor is 1x, very first UI form multi will be 10x, second UI form multi is 13.3x, final UI form will be 20x
     * if State2Factor is 2x, first is 10x, second is 16.6, final UI form will be 30 and so on.
     * This value scales off as a factor of the form's multiplier
     */
    getState2Factor(dbcForm: number): number;
    save(): import('./IFormStackable').IFormStackable;
}

