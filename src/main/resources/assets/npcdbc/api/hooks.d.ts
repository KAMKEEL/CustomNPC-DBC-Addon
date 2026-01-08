/**
 * CustomNPC+ Event Hook Overloads
 * Auto-generated - do not edit manually.
 */

import './minecraft-raw.d.ts';
import './forge-events-raw.d.ts';

declare global {
    function capsuleUsed(IDBCEvent: IDBCEvent.CapsuleUsedEvent): void;
    function senzuUsed(IDBCEvent: IDBCEvent.SenzuUsedEvent): void;
    function formChange(IDBCEvent: IDBCEvent.FormChangeEvent): void;
    function damaged(IDBCEvent: IDBCEvent.DamagedEvent): void;
    function dBCRevive(IDBCEvent: IDBCEvent.DBCReviveEvent): void;
    function dBCKnockout(IDBCEvent: IDBCEvent.DBCKnockout): void;
}

export {};
