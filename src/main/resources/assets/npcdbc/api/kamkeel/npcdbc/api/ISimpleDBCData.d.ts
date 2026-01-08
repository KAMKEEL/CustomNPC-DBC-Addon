/**
 * Generated from Java file for CustomNPC+ Minecraft Mod 1.7.10
 * Package: kamkeel.npcdbc.api
 */

/**
 * Primarily intended to give DBC information in client scripts,
 * as the IPlayer and subsequent interfaces do not work on the client.
 */
export interface ISimpleDBCData {
    getRace(): number;
    getRelease(): number;
    getBody(): number;
    getHP(): number;
    getStamina(): number;
    getKi(): number;
    getForm(): number;
    getForm2(): number;
    getPowerPoints(): number;
    getJRMCSE(): string;
    getDBCClass(): number;
    getPowerType(): number;
    getFusionString(): string;
    isTurboOn(): boolean;
    getMaxHP(): number;
    getMaxBody(): number;
    getBodyPercentage(): number;
    getMaxKi(): number;
    getMaxStamina(): number;
    getAllAttributes(): number[];
    getFullAttribute(id: number): number;
    getAllFullAttributes(): number[];
    getRaceName(): string;
    getCurrentDBCFormName(): string;
    isChargingKiAttack(): boolean;
    getMaxStat(id: number): number;
    getCurrentStat(id: number): number;
    getMajinAbsorptionRace(): number;
    getMajinAbsorptionPower(): number;
    isKO(): boolean;
    isUI(): boolean;
    isMUI(): boolean;
    isMystic(): boolean;
    isGOD(): boolean;
    isLegendary(): boolean;
    isDivine(): boolean;
    isMajin(): boolean;
    isKaioken(): boolean;
    isFlying(): boolean;
    isInCustomForm(): boolean;
    isInCustomForm(id: number): boolean;
    isInCustomForm(form: import('./form/IForm').IForm): boolean;
    getCurrentForm(): import('./form/IForm').IForm;
    getAura(): import('./aura/IAura').IAura;
    isInAura(): boolean;
    isInAura(aura: import('./aura/IAura').IAura): boolean;
    isInAura(auraName: string): boolean;
    isInAura(auraID: number): boolean;
    getOutline(): import('./outline/IOutline').IOutline;
    isReleasing(): boolean;
    isMeditating(): boolean;
    isSuperRegen(): boolean;
    isSwooping(): boolean;
    isInMedicalLiquid(): boolean;
}

