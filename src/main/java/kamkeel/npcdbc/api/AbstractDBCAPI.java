package kamkeel.npcdbc.api;

import cpw.mods.fml.common.Loader;
import kamkeel.npcdbc.api.aura.IAura;
import kamkeel.npcdbc.api.aura.IAuraHandler;
import kamkeel.npcdbc.api.effect.IBonusHandler;
import kamkeel.npcdbc.api.effect.IDBCEffectHandler;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.api.form.IFormHandler;
import kamkeel.npcdbc.api.npc.IDBCDisplay;
import kamkeel.npcdbc.api.npc.IDBCStats;
import kamkeel.npcdbc.api.outline.IOutline;
import kamkeel.npcdbc.api.outline.IOutlineHandler;
import kamkeel.npcdbc.api.skill.ISkillHandler;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IPlayer;

public abstract class AbstractDBCAPI {
    private static AbstractDBCAPI instance = null;

    public static boolean IsAvailable() {
        return Loader.isModLoaded("jinryuujrmcore");
    }

    public static AbstractDBCAPI Instance() {
        if (instance != null) {
            return instance;
        } else if (!IsAvailable()) {
            return null;
        } else {
            try {
                Class<?> c = Class.forName("kamkeel.npcdbc.scripted.DBCAPI");
                instance = (AbstractDBCAPI) c.getMethod("Instance").invoke(null);
            } catch (Exception ignored) {

            }
            return instance;
        }
    }

    public abstract ISkillHandler getSkillHandler();

    public abstract IFormHandler getFormHandler();

    public abstract IAuraHandler getAuraHandler();

    public abstract IOutlineHandler getOutlineHandler();

    public abstract IBonusHandler getBonusHandler();

    /**
     * You can also use the Custom Effect Handler in CNPC+
     * while using the DBC Addon Index of 1 for controlling
     * effects as well
     *
     * @return DBC Effect Handler
     */
    public abstract IDBCEffectHandler getDBCEffectHandler();

    public abstract IForm createForm(String name);

    public abstract IAura createAura(String name);

    public abstract IAura getAura(String name);

    /**
     * @param name name
     * @return new form
     */
    public abstract IForm getOrCreateForm(String name);

    public abstract IForm getForm(String name);

    public abstract IOutline createOutline(String name);

    public abstract IOutline getOutline(String name);

    /**
     * Preferably used in a DamagedEvent/AttackEvent with cancelling the event
     *
     * @param dodger   The entity dodging
     * @param attacker Attacker Entity
     */
    public abstract void forceDodge(IEntity dodger, IEntity attacker);

    /**
     * @return Fake DBC Data for Simulating Damage
     */
    public abstract IDBCStats abstractDBCData();

    /**
     * @param npc NPC
     * @return DBC Data attached to an NPC
     */
    public abstract IDBCStats getDBCData(ICustomNpc npc);

    public abstract IDBCDisplay getDBCDisplay(ICustomNpc npc);

    /**
     * Performs DBC Damage based on the DBC Stats
     * sent in
     *
     * @param player Player (Target of DBC Damage)
     * @param stats  DBC Stats / Settings to Simulate
     * @param damage Damage to simulate
     */
    public abstract void doDBCDamage(IPlayer player, IDBCStats stats, int damage);

    /**
     * @param race 0 to 5
     * @return Name of race ID
     */
    public abstract String getRaceName(int race);

    /**
     * @param race ID (0 to 5),
     * @param form ID (0 to 3 for humans/namekians, 0 to 14 for Saiyans/Half, 0 to 7 for arcosians, 0 to 4 for majins)
     * @return form name i.e "SSFullPow"
     */
    public abstract String getFormName(int race, int form);

    /**
     * @param raceid Race ID
     * @param formId Form ID
     * @return All  config data for a Form Mastery i.e Max Level, Instant Transform Unlock, Required Masteries
     */
    public abstract String[] getAllFormMasteryData(int raceid, int formId);

    /**
     * @param race      Race ID
     * @param nonRacial nonRacial forms are Kaioken/UI/Mystic/GOD
     * @return Number of forms a race has, i.e Saiyan has 14 racial and 4 non racial
     */
    public abstract int getAllFormsLength(int race, boolean nonRacial);

    /**
     * @param race      Race ID
     * @param nonRacial check getAllFormsLength(int race, boolean nonRacial)
     * @return An array containing all forms the race has
     */
    public abstract String[] getAllForms(int race, boolean nonRacial);

    /**
     * @return IKiAttack Object
     */
    public abstract IKiAttack createKiAttack();

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
    public abstract IKiAttack createKiAttack(byte type, byte speed, int damage, boolean hasEffect, byte color, byte density, boolean hasSound, byte chargePercent);

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
    public abstract void fireKiAttack(ICustomNpc npc, byte type, byte speed, int damage, boolean hasEffect, byte color, byte density, boolean hasSound, byte chargePercent);

    /**
     * Fires an IKiAttack with its internal params
     *
     * @param npc      NPC shooting this attack
     * @param kiAttack ki attack to shoot
     */
    public abstract void fireKiAttack(ICustomNpc npc, IKiAttack kiAttack);


    /**
     * @param skillName Acceptable skill names:
     *                  <code>"Fusion", "Jump", "Dash", "Fly", "Endurance", <br>
     *                  "PotentialUnlock", "KiSense", "Meditation", "Kaioken", "GodForm", <br>
     *                  "OldKaiUnlock", "KiProtection", "KiFist", "KiBoost", "DefensePenetration", <br>
     *                  "KiInfuse", "UltraInstinct", "InstantTransmission", "GodOfDestruction"</code>
     * @param level     Level, starting from 1. <br> Levels can be forcefully set to exceed level 10, so it's only capped to be a minimum of 1.
     * @return The TP Cost of said skill at just that level.
     */
    public abstract int getSkillTPCostSingle(String skillName, int level);

    /**
     * @param skillName Refer to {@link AbstractDBCAPI#getSkillTPCostSingle(String, int)}
     * @param level     Level, starting from 1. <br> Levels can be forcefully set to exceed level 10, so it's only capped to be a minimum of 1.
     * @return The Mind Cost of said skill at just that level.
     */
    public abstract int getSkillMindCostSingle(String skillName, int level);

    /**
     * @param skillName Refer to {@link AbstractDBCAPI#getSkillTPCostSingle(String, int)}
     * @param level     Level, starting from 1. <br> Levels can be forcefully set to exceed level 10, so it's only capped to be a minimum of 1.
     * @return TheMmind cost to get this skill to given level
     */
    public abstract int getSkillMindCostRecursive(String skillName, int level);

    /**
     * @param skillName Refer to {@link AbstractDBCAPI#getSkillTPCostSingle(String, int)}
     * @param level     Level, starting from 1. <br> Levels can be forcefully set to exceed level 10, so it's only capped to be a minimum of 1.
     * @return The TP cost to get this skill to given level
     */
    public abstract int getSkillTPCostRecursive(String skillName, int level);

    /**
     * @param race  Race ID from 0 to 5
     * @param level Level of the Super form (uncapped).
     * @return TP cost of this single level
     */
    public abstract int getSkillRacialTPCostSingle(int race, int level);

    /**
     * @param race  Race ID from 0 to 5
     * @param level Level of the Super form (uncapped).
     * @return Mind cost of this single level
     */
    public abstract int getSkillRacialTPMindSingle(int race, int level);

    /**
     * @param race  Race ID from 0 to 5
     * @param level Level of the Super form (uncapped).
     * @return TP Cost to get to this level
     */
    public abstract int getSkillRacialTPCostSingleRecursive(int race, int level);

    /**
     * @param race  Race ID from 0 to 5
     * @param level Level of the Super form (uncapped).
     * @return Mind Cost to get to this level
     */
    public abstract int getSkillRacialTPMindSingleRecursive(int race, int level);

    /**
     * @return Max level of UI
     */
    public abstract int getUltraInstinctMaxLevel();

}
