/**
 * Generated from Java file for CustomNPC+ Minecraft Mod 1.7.10
 * Package: kamkeel.npcdbc.api.aura
 */

export interface IAuraHandler {
    saveAura(var1: import('./IAura').IAura): import('./IAura').IAura;
    delete(var1: string): void;
    delete(var1: number): void;
    has(var1: string): boolean;
    get(var1: string): import('./IAura').IAura;
    get(var1: number): import('./IAura').IAura;
    getAuras(): import('./IAura').IAura[];
}

