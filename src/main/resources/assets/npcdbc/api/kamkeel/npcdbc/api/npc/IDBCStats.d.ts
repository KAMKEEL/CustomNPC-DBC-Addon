/**
 * Generated from Java file for CustomNPC+ Minecraft Mod 1.7.10
 * Package: kamkeel.npcdbc.api.npc
 */

export interface IDBCStats {
    setRelease(release: number): void;
    getRelease(): number;
    setDodgeChance(dodge: number): void;
    getDodgeChance(): number;
    isEnabled(): boolean;
    setEnabled(enabled: boolean): void;
    isFriendlyFist(): boolean;
    setFriendlyFist(friendlyFist: boolean): void;
    getFriendlyFistAmount(): number;
    setFriendlyFistAmount(seconds: number): void;
    isIgnoreDex(): boolean;
    setIgnoreDex(ignoreDex: boolean): void;
    isIgnoreBlock(): boolean;
    setIgnoreBlock(ignoreBlock: boolean): void;
    isIgnoreEndurance(): boolean;
    setIgnoreEndurance(ignoreEndurance: boolean): void;
    isIgnoreKiProtection(): boolean;
    setIgnoreKiProtection(ignoreKiProtection: boolean): void;
    isIgnoreFormReduction(): boolean;
    setIgnoreFormReduction(ignoreFormReduction: boolean): void;
    hasDefensePenetration(): boolean;
    setHasDefensePenetration(hasDefensePenetration: boolean): void;
    getDefensePenetration(): number;
    setDefensePenetration(defensePenetration: number): void;
    /**
     * Checks if the NPC can be locked onto
     *
     * @return True if NPC can be locked onto.
     */
    canBeLockedOn(): boolean;
    /**
     * Changes if the NPC can be locked onto
     *
     * @param canBeLockedOn true or false
     */
    setLockOnState(canBeLockedOn: boolean): void;
}

