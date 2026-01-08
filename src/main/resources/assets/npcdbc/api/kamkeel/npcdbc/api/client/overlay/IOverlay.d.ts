/**
 * Generated from Java file for CustomNPC+ Minecraft Mod 1.7.10
 * Package: kamkeel.npcdbc.api.client.overlay
 */

export interface IOverlay {
    create(): import('./IOverlay').IOverlay;
    create(type: number): import('./IOverlay').IOverlay;
}

export namespace IOverlay {
    export interface TextureFunction {
        getTexture(ctx: OverlayContext): string;
    }
    export interface ColorFunction {
        getColor(ctx: OverlayContext): import('../../Color').Color;
    }
    export interface RenderFunction {
        render(ctx: OverlayContext): void;
    }
}

