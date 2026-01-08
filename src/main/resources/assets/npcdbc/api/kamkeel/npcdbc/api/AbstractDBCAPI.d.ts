/**
 * Generated from Java file for CustomNPC+ Minecraft Mod 1.7.10
 * Package: kamkeel.npcdbc.api
 */

export class AbstractDBCAPI {
    IsAvailable(): boolean;
    Instance(): import('./AbstractDBCAPI').AbstractDBCAPI;
    getFormHandler(): import('./form/IFormHandler').IFormHandler;
    getAuraHandler(): import('./aura/IAuraHandler').IAuraHandler;
    getOutlineHandler(): import('./outline/IOutlineHandler').IOutlineHandler;
    getBonusHandler(): import('./effect/IBonusHandler').IBonusHandler;
    /**
     * You can also use the Custom Effect Handler in CNPC+
     * while using the DBC Addon Index of 1 for controlling
     * effects as well
     *
     * @return DBC Effect Handler
     */
    getDBCEffectHandler(): import('./effect/IDBCEffectHandler').IDBCEffectHandler;
    createForm(name: string): import('./form/IForm').IForm;
    createAura(name: string): import('./aura/IAura').IAura;
    getAura(name: string): import('./aura/IAura').IAura;
    /**
     * @param name name
     * @return new form
     */
    getOrCreateForm(name: string): import('./form/IForm').IForm;
    getForm(name: string): import('./form/IForm').IForm;
    createOutline(name: string): import('./outline/IOutline').IOutline;
    getOutline(name: string): import('./outline/IOutline').IOutline;
    /**
     * Preferably used in a DamagedEvent/AttackEvent with cancelling the event
     *
     * @param dodger   The entity dodging
     * @param attacker Attacker Entity
     */
    forceDodge(dodger: IEntity, attacker: IEntity): void;
    /**
     * @return Fake DBC Data for Simulating Damage
     */
    abstractDBCData(): import('./npc/IDBCStats').IDBCStats;
    /**
     * @param npc NPC
     * @return DBC Data attached to an NPC
     */
    getDBCData(npc: ICustomNpc): import('./npc/IDBCStats').IDBCStats;
    getDBCDisplay(npc: ICustomNpc): import('./npc/IDBCDisplay').IDBCDisplay;
    /**
     * Performs DBC Damage based on the DBC Stats
     * sent in
     *
     * @param player Player (Target of DBC Damage)
     * @param stats  DBC Stats / Settings to Simulate
     * @param damage Damage to simulate
     */
    doDBCDamage(player: IPlayer, stats: import('./npc/IDBCStats').IDBCStats, damage: number): void;
    /**
     * @param race 0 to 5
     * @return Name of race ID
     */
    getRaceName(race: number): string;
    /**
     * @param race ID (0 to 5),
     * @param form ID (0 to 3 for humans/namekians, 0 to 14 for Saiyans/Half, 0 to 7 for arcosians, 0 to 4 for majins)
     * @return form name i.e "SSFullPow"
     */
    getFormName(race: number, form: number): string;
    /**
     * @param raceid Race ID
     * @param formId Form ID
     * @return All  config data for a Form Mastery i.e Max Level, Instant Transform Unlock, Required Masteries
     */
    getAllFormMasteryData(raceid: number, formId: number): string[];
    /**
     * @param race      Race ID
     * @param nonRacial nonRacial forms are Kaioken/UI/Mystic/GOD
     * @return Number of forms a race has, i.e Saiyan has 14 racial and 4 non racial
     */
    getAllFormsLength(race: number, nonRacial: boolean): number;
    /**
     * @param race      Race ID
     * @param nonRacial check getAllFormsLength(int race, boolean nonRacial)
     * @return An array containing all forms the race has
     */
    getAllForms(race: number, nonRacial: boolean): string[];
    /**
     * @return IKiAttack Object
     */
    createKiAttack(): import('./IKiAttack').IKiAttack;
    /**
     * @param type          Type of Ki Attack [0 - 8] "Wave", "Blast", "Disk", "Laser", "Spiral", "BigBlast", "Barrage", "Shield", "Explosion"
     * @param speed         Speed of Ki Attack [0 - 100]
     * @param damage        Damage for Ki Attack
     * @param hasEffect     True for Explosion
     * @param color         Color of Ki Attack [0 - 30] -&gt; <br>
     *                      0: "AlignmentBased", "white", "blue", "purple", "red", "black", "green", "yellow", "orange", "pink", "magenta", <br>
     *                      11: "lightPink", "cyan", "darkCyan", "lightCyan", "darkGray", "gray", "darkBlue", "lightBlue", "darkPurple", "lightPurple", <br>
     *                      21: "darkRed", "lightRed", "darkGreen", "lime", "darkYellow", "lightYellow", "gold", "lightOrange", "darkBrown", "lightBrown"
     * @param density       Density of Ki Attack &gt; 0
     * @param hasSound      Play Impact Sound of Ki Attack
     * @param chargePercent Charge Percentage of Ki Attack [0 - 100]
     * @return IKiAttack Object with Set Values
     */
    createKiAttack(type: number, speed: number, damage: number, hasEffect: boolean, color: number, density: number, hasSound: boolean, chargePercent: number): import('./IKiAttack').IKiAttack;
    /**
     * Fires a Ki Attack in the Head Direction of the NPC
     *
     * @param npc           the NPC firing this attack.
     * @param type          Type of Ki Attack [0 - 8] "Wave", "Blast", "Disk", "Laser", "Spiral", "BigBlast", "Barrage", "Shield", "Explosion"
     * @param speed         Speed of Ki Attack [0 - 100]
     * @param damage        Damage for Ki Attack
     * @param hasEffect     True for Explosion
     * @param color         Color of Ki Attack [0 - 30] -&gt; <br>
     *                      0: "AlignmentBased", "white", "blue", "purple", "red", "black", "green", "yellow", "orange", "pink", "magenta", <br>
     *                      11: "lightPink", "cyan", "darkCyan", "lightCyan", "darkGray", "gray", "darkBlue", "lightBlue", "darkPurple", "lightPurple", <br>
     *                      21: "darkRed", "lightRed", "darkGreen", "lime", "darkYellow", "lightYellow", "gold", "lightOrange", "darkBrown", "lightBrown"
     * @param density       Density of Ki Attack &gt; 0
     * @param hasSound      Play Impact Sound of Ki Attack
     * @param chargePercent Charge Percentage of Ki Attack [0 - 100]
     */
    fireKiAttack(npc: ICustomNpc, type: number, speed: number, damage: number, hasEffect: boolean, color: number, density: number, hasSound: boolean, chargePercent: number): void;
    /**
     * Fires an IKiAttack with its internal params
     *
     * @param npc      NPC shooting this attack
     * @param kiAttack ki attack to shoot
     */
    fireKiAttack(npc: ICustomNpc, kiAttack: import('./IKiAttack').IKiAttack): void;
    /**
     * @param skillName Acceptable skill names:
     *                  <code>"Fusion", "Jump", "Dash", "Fly", "Endurance", <br>
     *                  "PotentialUnlock", "KiSense", "Meditation", "Kaioken", "GodForm", <br>
     *                  "OldKaiUnlock", "KiProtection", "KiFist", "KiBoost", "DefensePenetration", <br>
     *                  "KiInfuse", "UltraInstinct", "InstantTransmission", "GodOfDestruction"</code>
     * @param level     Level, starting from 1. <br> Levels can be forcefully set to exceed level 10, so it's only capped to be a minimum of 1.
     * @return The TP Cost of said skill at just that level.
     */
    getSkillTPCostSingle(skillName: string, level: number): number;
    /**
     * @param skillName Refer to {@link AbstractDBCAPI#getSkillTPCostSingle(String, int)}
     * @param level     Level, starting from 1. <br> Levels can be forcefully set to exceed level 10, so it's only capped to be a minimum of 1.
     * @return The Mind Cost of said skill at just that level.
     */
    getSkillMindCostSingle(skillName: string, level: number): number;
    /**
     * @param skillName Refer to {@link AbstractDBCAPI#getSkillTPCostSingle(String, int)}
     * @param level     Level, starting from 1. <br> Levels can be forcefully set to exceed level 10, so it's only capped to be a minimum of 1.
     * @return TheMmind cost to get this skill to given level
     */
    getSkillMindCostRecursive(skillName: string, level: number): number;
    /**
     * @param skillName Refer to {@link AbstractDBCAPI#getSkillTPCostSingle(String, int)}
     * @param level     Level, starting from 1. <br> Levels can be forcefully set to exceed level 10, so it's only capped to be a minimum of 1.
     * @return The TP cost to get this skill to given level
     */
    getSkillTPCostRecursive(skillName: string, level: number): number;
    /**
     * @param race  Race ID from 0 to 5
     * @param level Level of the Super form (uncapped).
     * @return TP cost of this single level
     */
    getSkillRacialTPCostSingle(race: number, level: number): number;
    /**
     * @param race  Race ID from 0 to 5
     * @param level Level of the Super form (uncapped).
     * @return Mind cost of this single level
     */
    getSkillRacialTPMindSingle(race: number, level: number): number;
    /**
     * @param race  Race ID from 0 to 5
     * @param level Level of the Super form (uncapped).
     * @return TP Cost to get to this level
     */
    getSkillRacialTPCostSingleRecursive(race: number, level: number): number;
    /**
     * @param race  Race ID from 0 to 5
     * @param level Level of the Super form (uncapped).
     * @return Mind Cost to get to this level
     */
    getSkillRacialTPMindSingleRecursive(race: number, level: number): number;
    /**
     * @return Max level of UI
     */
    getUltraInstinctMaxLevel(): number;
    instance: import('./AbstractDBCAPI').AbstractDBCAPI;
}

