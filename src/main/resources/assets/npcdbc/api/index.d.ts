/**
 * Centralized global declarations for CustomNPC+ scripting.
 * Auto-generated - do not edit manually.
 */

declare global {
    // ============================================================================
    // TYPE ALIASES - Make all interfaces available globally
    // ============================================================================

    type AbstractDBCAPI = import('./kamkeel/npcdbc/api/AbstractDBCAPI').AbstractDBCAPI;
    type Color = import('./kamkeel/npcdbc/api/Color').Color;
    type ConstantEasing = import('./kamkeel/npcdbc/api/easing/ConstantEasing').ConstantEasing;
    type CubicBezier = import('./kamkeel/npcdbc/api/easing/CubicBezier').CubicBezier;
    type Easing = import('./kamkeel/npcdbc/api/easing/Easing').Easing;
    type IAdvancedFormStat = import('./kamkeel/npcdbc/api/form/IAdvancedFormStat').IAdvancedFormStat;
    type IAura = import('./kamkeel/npcdbc/api/aura/IAura').IAura;
    type IAuraDisplay = import('./kamkeel/npcdbc/api/aura/IAuraDisplay').IAuraDisplay;
    type IAuraHandler = import('./kamkeel/npcdbc/api/aura/IAuraHandler').IAuraHandler;
    type IBonusHandler = import('./kamkeel/npcdbc/api/effect/IBonusHandler').IBonusHandler;
    type IDBCAddon = import('./kamkeel/npcdbc/api/IDBCAddon').IDBCAddon;
    type IDBCDisplay = import('./kamkeel/npcdbc/api/npc/IDBCDisplay').IDBCDisplay;
    type IDBCEffectHandler = import('./kamkeel/npcdbc/api/effect/IDBCEffectHandler').IDBCEffectHandler;
    type IDBCEvent = import('./kamkeel/npcdbc/api/event/IDBCEvent').IDBCEvent;
    type IDBCStats = import('./kamkeel/npcdbc/api/npc/IDBCStats').IDBCStats;
    type IForm = import('./kamkeel/npcdbc/api/form/IForm').IForm;
    type IFormAdvanced = import('./kamkeel/npcdbc/api/form/IFormAdvanced').IFormAdvanced;
    type IFormDisplay = import('./kamkeel/npcdbc/api/form/IFormDisplay').IFormDisplay;
    type IFormHandler = import('./kamkeel/npcdbc/api/form/IFormHandler').IFormHandler;
    type IFormKaiokenStackables = import('./kamkeel/npcdbc/api/form/IFormKaiokenStackables').IFormKaiokenStackables;
    type IFormMastery = import('./kamkeel/npcdbc/api/form/IFormMastery').IFormMastery;
    type IFormMasteryLinkData = import('./kamkeel/npcdbc/api/form/IFormMasteryLinkData').IFormMasteryLinkData;
    type IFormStackable = import('./kamkeel/npcdbc/api/form/IFormStackable').IFormStackable;
    type IKiAttack = import('./kamkeel/npcdbc/api/IKiAttack').IKiAttack;
    type IKiWeaponData = import('./kamkeel/npcdbc/api/npc/IKiWeaponData').IKiWeaponData;
    type IOutline = import('./kamkeel/npcdbc/api/outline/IOutline').IOutline;
    type IOutlineHandler = import('./kamkeel/npcdbc/api/outline/IOutlineHandler').IOutlineHandler;
    type IOverlay = import('./kamkeel/npcdbc/api/client/overlay/IOverlay').IOverlay;
    type IOverlayContext = import('./kamkeel/npcdbc/api/client/overlay/IOverlayContext').IOverlayContext;
    type IOverlayScript = import('./kamkeel/npcdbc/api/client/overlay/IOverlayScript').IOverlayScript;
    type IPlayerBonus = import('./kamkeel/npcdbc/api/effect/IPlayerBonus').IPlayerBonus;
    type ISimpleDBCData = import('./kamkeel/npcdbc/api/ISimpleDBCData').ISimpleDBCData;
    type Linear = import('./kamkeel/npcdbc/api/easing/Linear').Linear;
    type LinearPiecewise = import('./kamkeel/npcdbc/api/easing/LinearPiecewise').LinearPiecewise;
    type Premade = import('./kamkeel/npcdbc/api/easing/ProgressFunctions').Premade;
    type ProgressFunctions = import('./kamkeel/npcdbc/api/easing/ProgressFunctions').ProgressFunctions;

    // ============================================================================
    // NESTED INTERFACES - Allow autocomplete like INpcEvent.InitEvent
    // ============================================================================

    namespace IDBCEvent {
        interface CapsuleUsedEvent extends IDBCEvent {}
        interface DBCKnockout extends IDBCEvent {}
        interface DBCReviveEvent extends IDBCEvent {}
        interface DamagedEvent extends IDBCEvent {}
        interface FormChangeEvent extends IDBCEvent {}
        interface SenzuUsedEvent extends IDBCEvent {}
    }

    namespace IOverlay {
        interface ColorFunction extends IOverlay {}
        interface RenderFunction extends IOverlay {}
        interface TextureFunction extends IOverlay {}
    }

    namespace IOverlayScript {
        interface Functions extends IOverlayScript {}
    }

    namespace ProgressFunctions {
        interface Premade extends ProgressFunctions {}
    }

}

export {};
