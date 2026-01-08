/**
 * Generated from Java file for CustomNPC+ Minecraft Mod 1.7.10
 * Package: kamkeel.npcdbc.api.outline
 */

export interface IOutlineHandler {
    createOutline(name: string): import('./IOutline').IOutline;
    saveOutline(outline: import('./IOutline').IOutline): import('./IOutline').IOutline;
    deleteOutlineFile(name: string): void;
    hasName(newName: string): boolean;
    get(name: string): import('./IOutline').IOutline;
    get(id: number): import('./IOutline').IOutline;
    has(name: string): boolean;
    has(id: number): boolean;
    delete(id: number): void;
    delete(name: string): void;
    getNames(): string[];
    getOutlines(): import('./IOutline').IOutline[];
    getOutlineFromName(outlineName: string): import('./IOutline').IOutline;
}

