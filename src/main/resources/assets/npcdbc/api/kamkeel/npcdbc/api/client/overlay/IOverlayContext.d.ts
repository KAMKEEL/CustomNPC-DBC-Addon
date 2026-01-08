/**
 * Generated from Java file for CustomNPC+ Minecraft Mod 1.7.10
 * Package: kamkeel.npcdbc.api.client.overlay
 */

export interface IOverlayContext {
    typeDisabled(type: import('./IOverlay/Type').IOverlay.Type): boolean;
    gender(): number;
    female(): boolean;
    age(): number;
    inverseAge(): number;
    pregnant(): number;
    genderDir(): string;
    eyeType(): number;
    eyebrows(): boolean;
    furType(): number;
    hairType(type: string): boolean;
    color(type: string): number;
    color(type: import('./IOverlay/ColorType').IOverlay.ColorType): import('../../Color').Color;
    color(type: import('./IOverlay/ColorType').IOverlay.ColorType, overlay: import('./IOverlay').IOverlay): import('../../Color').Color;
    furGT(): boolean;
    furDaima(): boolean;
    furSavior(): boolean;
    furDir(): string;
    hasFur(): boolean;
    pupils(): boolean;
    berserk(): boolean;
    isNPC(): boolean;
    getPlayer(): IEntityLivingBase;
    getNPC(): ICustomNpc;
    getEntity(): IEntity;
    getDBCData(): import('../../ISimpleDBCData').ISimpleDBCData;
    getOverlay(): import('./IOverlay').IOverlay;
    form(): import('../../form/IForm').IForm;
}

