/**
 * Generated from Java file for CustomNPC+ Minecraft Mod 1.7.10
 * Package: kamkeel.npcdbc.api
 */

export class Color {
    setColor(color: number, alpha: number): void;
    setColor(red: number, green: number, blue: number, alpha: number): void;
    lerpRGBA(color1: import('./Color').Color, color2: import('./Color').Color, fraction: number): import('./Color').Color;
    Color(): import('./return new').return new;
    lerpRGB(color1: number, color2: number, fraction: number): import('./Color').Color;
    lerpRGBA(color2: import('./Color').Color, fraction: number): import('./Color').Color;
    lerpRGB(color: number, fraction: number): import('./Color').Color;
    multiply(multi: number): import('./Color').Color;
    glColor(): void;
    uniform(name: string): void;
    writeToNBT(compound: NBTTagCompound, name: string): NBTTagCompound;
    readFromNBT(compound: NBTTagCompound, name: string): void;
    getRed(): number;
    getRedF(): number;
    getGreen(): number;
    getGreenF(): number;
    getBlue(): number;
    getBlueF(): number;
    getColor(color: number): string;
    getColor(color: number, alpha: number): string;
    clone(): import('./Color').Color;
    Color(): import('./return new').return new;
    color: number;
    alpha: number;
}

