package kamkeel.npcdbc.api;

import cpw.mods.fml.common.Loader;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.entity.EntityNPCInterface;

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

    public abstract IDBCStats getDBCData(ICustomNpc<EntityNPCInterface> npc);

    /**
     * @return Name of race ID
     * @param race 0 to 5
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
     * @param race Race ID
     * @param nonRacial nonRacial forms are Kaioken/UI/Mystic/GOD
     * @return Number of forms a race has, i.e Saiyan has 14 racial and 4 non racial
     */
    public abstract int getAllFormsLength(int race, boolean nonRacial);

    /**
     * @param race Race ID
     * @param nonRacial check getAllFormsLength(int race, boolean nonRacial)
     * @return An array containing all forms the race has
     */
    public abstract String[] getAllForms(int race, boolean nonRacial);

    /**
     * @return IKiAttack Object
     */
    public abstract IKiAttack createKiAttack();

    /**
     * @param type Type of Ki Attack [0 - 8] "Wave", "Blast", "Disk", "Laser", "Spiral", "BigBlast", "Barrage", "Shield", "Explosion"
     * @param speed Speed of Ki Attack [0 - 100]
     * @param damage Damage for Ki Attack
     * @param hasEffect True for Explosion
     * @param color Color of Ki Attack [0 - 30] ->
     *              0: "AlignmentBased", "white", "blue", "purple", "red", "black", "green", "yellow", "orange", "pink", "magenta",
     *              11: "lightPink", "cyan", "darkCyan", "lightCyan", "darkGray", "gray", "darkBlue", "lightBlue", "darkPurple", "lightPurple",
     *              21: "darkRed", "lightRed", "darkGreen", "lime", "darkYellow", "lightYellow", "gold", "lightOrange", "darkBrown", "lightBrown"
     * @param density Density of Ki Attack > 0
     * @param hasSound Play Impact Sound of Ki Attack
     * @param chargePercent Charge Percentage of Ki Attack [0 - 100]
     * @return IKiAttack Object with Set Values
     */
    public abstract IKiAttack createKiAttack(byte type, byte speed, int damage, boolean hasEffect, byte color, byte density, boolean hasSound, byte chargePercent);

    /**
     * Fires a Ki Attack in the Head Direction of the NPC
     * @param type Type of Ki Attack [0 - 8] "Wave", "Blast", "Disk", "Laser", "Spiral", "BigBlast", "Barrage", "Shield", "Explosion"
     * @param speed Speed of Ki Attack [0 - 100]
     * @param damage Damage for Ki Attack
     * @param hasEffect True for Explosion
     * @param color Color of Ki Attack [0 - 30] ->
     *              0: "AlignmentBased", "white", "blue", "purple", "red", "black", "green", "yellow", "orange", "pink", "magenta",
     *              11: "lightPink", "cyan", "darkCyan", "lightCyan", "darkGray", "gray", "darkBlue", "lightBlue", "darkPurple", "lightPurple",
     *              21: "darkRed", "lightRed", "darkGreen", "lime", "darkYellow", "lightYellow", "gold", "lightOrange", "darkBrown", "lightBrown"
     * @param density Density of Ki Attack > 0
     * @param hasSound Play Impact Sound of Ki Attack
     * @param chargePercent Charge Percentage of Ki Attack [0 - 100]
     */
    public abstract void fireKiAttack(ICustomNpc<EntityNPCInterface> npc, byte type, byte speed, int damage, boolean hasEffect, byte color, byte density, boolean hasSound, byte chargePercent);

    /**
     * Fires an IKiAttack with its internal params
     */
    public abstract void fireKiAttack(ICustomNpc<EntityNPCInterface> npc, IKiAttack kiAttack);
}
