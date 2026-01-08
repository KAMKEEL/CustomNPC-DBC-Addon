/**
 * Generated from Java file for CustomNPC+ Minecraft Mod 1.7.10
 * Package: kamkeel.npcdbc.api.event
 */

export interface IDBCEvent extends IPlayerEvent {
}

export namespace IDBCEvent {
    export interface CapsuleUsedEvent extends IDBCEvent {
        /**
         * 0: Misc, 1: HP, 2: Ki, 3: Stamina
         *
         * @return Type of Capsule
         */
        getType(): number;
        /**
         * @return Subtype / Metadata of the Capsule
         */
        getSubType(): number;
        /**
         * @return Capsule Name
         */
        getCapsuleName(): string;
    }
    export interface SenzuUsedEvent extends IDBCEvent {
    }
    export interface FormChangeEvent extends IDBCEvent {
        /**
         * @return The ID of the Form. If form before is vanilla DBC, returns the ID of that. i.e Base returns 0, SSGod returns 9. Else, returns ID of CNPC Custom Form
         */
        getFormBeforeID(): number;
        /**
         * @return The ID of the Form. If form after is vanilla DBC, returns the ID of that. i.e SSGod returns 9. Else, returns ID of CNPC Custom Form
         */
        getFormAfterID(): number;
        /**
         * @return If form before transformation is a vanilla DBC (SSJ/SSGod/SSBEvo) and not a CNPC Custom Form, this returns false
         */
        isFormBeforeCustom(): boolean;
        /**
         * @return If form after transformation is a vanilla DBC (SSJ/SSGod/SSBEvo) and not a CNPC Custom Form, this returns false
         */
        isFormAfterCustom(): boolean;
    }
    export interface DamagedEvent extends IDBCEvent {
        /**
         * Calculated based on Player's Stats, Dex, Blocking, etc.
         *
         * @return Damage Dealt to the HP of the Player
         */
        getDamage(): number;
        /**
         * Allows you to change / intercept the damage to HP
         * and modify to set new HP. Note if Damage is 0 -
         * then it could be Friendly Fist protecting player. Making
         * this greater could kill the player.
         *
         * @param damage The new damage to HP
         */
        setDamage(damage: number): void;
        /**
         *
         * @return the stamina reduction
         */
        getStaminaReduced(): number;
        /**
         * @param stamina The new stamina to reduce
         */
        setStaminaReduced(stamina: number): void;
        /**
         * Returns the unmodified KO result based on the current damage and
         * attacker before any overrides are applied.
         */
        getKO(): boolean;
        /**
         *
         * Doing setDamage will automatically reset the KO calculation, so setKO modifications must be performed after setDamage.
         *
         * @return If KO will occur
         */
        setKo(ko: boolean): void;
        /**
         * @return true if a knockout will ultimately occur after applying any
         * overrides set via {@link #setKo(boolean)}
         */
        getFinalKO(): boolean;
        /**
         *
         * @return the ki reduction
         */
        getKiReduced(): number;
        /**
         * @param ki The new ki to reduce
         */
        setKiReduced(ki: number): void;
        /**
         * @return IDamageSource
         */
        getDamageSource(): IDamageSource;
        /**
         * @return Damage Type == 3
         */
        isDamageSourceKiAttack(): boolean;
        /**
         * @return 0: Unknown, 1: Player, 2: NPC, 3: Ki Attack
         */
        getType(): number;
    }
    export interface DBCReviveEvent extends IDBCEvent {
    }
    export interface DBCKnockout extends IDBCEvent {
        getDamageSource(): IDamageSource;
    }
}

