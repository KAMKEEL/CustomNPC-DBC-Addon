/**
 * Generated from Java file for CustomNPC+ Minecraft Mod 1.7.10
 * Package: kamkeel.npcdbc.api.form
 */

export interface IFormHandler {
    saveForm(var1: import('./IForm').IForm): import('./IForm').IForm;
    delete(var1: string): void;
    delete(var1: number): void;
    has(var1: string): boolean;
    get(var1: string): import('./IForm').IForm;
    get(var1: number): import('./IForm').IForm;
    getForms(): import('./IForm').IForm[];
}

