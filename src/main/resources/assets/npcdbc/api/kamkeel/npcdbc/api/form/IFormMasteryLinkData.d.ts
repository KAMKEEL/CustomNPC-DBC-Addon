/**
 * Generated from Java file for CustomNPC+ Minecraft Mod 1.7.10
 * Package: kamkeel.npcdbc.api.form
 */

export interface IFormMasteryLinkData {
    hasLinkData(race: number): boolean;
    isCustomFormLink(race: number): boolean;
    getFormID(race: number): number;
    getForm(race: number): import('./IForm').IForm;
    setCustomFormLink(formID: number, race: number): void;
    setCustomFormLink(form: import('./IForm').IForm, race: number): void;
    setDBCFormLink(formID: number, race: number): void;
    setMasteryLink(formID: number, race: number, isCustomFormLink: boolean): void;
    removeLinkData(race: number): void;
    removeAllLinkData(): void;
}

