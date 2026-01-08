/**
 * Generated from Java file for CustomNPC+ Minecraft Mod 1.7.10
 * Package: kamkeel.npcdbc.api.form
 */

export interface IFormAdvanced {
    getStat(id: number): import('./IAdvancedFormStat').IAdvancedFormStat;
    setStatEnabled(id: number, enabled: boolean): void;
    isStatEnabled(id: number): boolean;
    setStatBonus(id: number, bonus: number): void;
    getStatBonus(id: number): number;
    setStatMulti(id: number, multiplier: number): void;
    getStatMulti(id: number): number;
    save(): import('./IFormAdvanced').IFormAdvanced;
}

