package kamkeel.npcdbc.scripted;

import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.entity.EntityEnergyAtt;
import JinRyuu.JRMCore.server.config.dbc.JGConfigDBCFormMastery;
import JinRyuu.JRMCore.server.config.dbc.JGConfigUltraInstinct;
import kamkeel.npcdbc.api.AbstractDBCAPI;
import kamkeel.npcdbc.api.IKiAttack;
import kamkeel.npcdbc.api.aura.IAura;
import kamkeel.npcdbc.api.aura.IAuraHandler;
import kamkeel.npcdbc.api.effect.IBonusHandler;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.api.form.IFormHandler;
import kamkeel.npcdbc.api.form.IFormMastery;
import kamkeel.npcdbc.api.npc.IDBCDisplay;
import kamkeel.npcdbc.api.npc.IDBCStats;
import kamkeel.npcdbc.api.outline.IOutline;
import kamkeel.npcdbc.api.outline.IOutlineHandler;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.BonusController;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.controllers.OutlineController;
import kamkeel.npcdbc.data.KiAttack;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.data.npc.DBCStats;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import kamkeel.npcdbc.mixins.late.INPCStats;
import kamkeel.npcdbc.util.DBCUtils;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.scripted.CustomNPCsException;
import noppes.npcs.util.ValueUtil;

import java.util.ArrayList;

public class DBCAPI extends AbstractDBCAPI {
    private static AbstractDBCAPI Instance;

    private DBCAPI() {
    }

    public static AbstractDBCAPI Instance() {
        if (DBCAPI.Instance == null) {
            Instance = new DBCAPI();
        }
        return Instance;
    }

    @Override
    public IFormHandler getFormHandler() {
        return FormController.Instance;
    }

    @Override
    public IAuraHandler getAuraHandler() {
        return AuraController.Instance;
    }

    @Override
    public IOutlineHandler getOutlineHandler() {
        return OutlineController.Instance;
    }

    @Override
    public IBonusHandler getBonusHandler() {
        return BonusController.Instance;
    }

    @Override
    public IForm createForm(String name) {
        return FormController.getInstance().createForm(name);
    }

    @Override
    public IForm getOrCreateForm(String name) {
        return FormController.getInstance().createForm(name);
    }

    @Override
    public IForm getForm(String name) {
        return FormController.getInstance().get(name);
    }

    @Override
    public IAura createAura(String name) {
        return AuraController.getInstance().createAura(name);
    }

    @Override
    public IAura getAura(String name) {
        return AuraController.getInstance().get(name);
    }

    @Override
    public IOutline createOutline(String name) {
        return OutlineController.getInstance().createOutline(name);
    }

    @Override
    public IOutline getOutline(String name) {
        return OutlineController.getInstance().get(name);
    }


    @Override
    public IDBCStats abstractDBCData() {
        DBCStats dbcStats = new DBCStats(null);
        dbcStats.enabled = true;
        return dbcStats;
    }

    @Override
    public IDBCStats getDBCData(ICustomNpc npc) {
        if (npc.getMCEntity() instanceof EntityNPCInterface)
            return ((INPCStats) ((EntityNPCInterface) npc.getMCEntity()).stats).getDBCStats();
        return null;
    }

    @Override
    public IDBCDisplay getDBCDisplay(ICustomNpc npc) {
        if (npc.getMCEntity() instanceof EntityNPCInterface)
            return ((INPCDisplay) ((EntityNPCInterface) npc.getMCEntity()).display).getDBCDisplay();
        return null;
    }

    @Override
    public void doDBCDamage(IPlayer player, IDBCStats stats, int damage) {
        if (player == null || stats == null)
            return;

        if (!stats.isEnabled() || !(player.getMCEntity() instanceof EntityPlayer))
            return;

        EntityPlayer entityPlayer = (EntityPlayer) player.getMCEntity();
        int damageToHP = DBCUtils.calculateDBCStatDamage(entityPlayer, (int) damage, stats);
        DBCUtils.doDBCDamage(entityPlayer, damageToHP, stats, null);
    }

    /**
     * @param race 0 to 5
     * @return Name of race ID
     */
    @Override
    public String getRaceName(int race) {
        if (race >= 0 && race <= 5) {
            return JRMCoreH.Races[race];
        }
        return "";
    }

    /**
     * @param race ID (0 to 5),
     * @param form ID (0 to 3 for humans/namekians, 0 to 14 for Saiyans/Half, 0 to 7 for arcosians, 0 to 4 for majins)
     * @return form name i.e "SSFullPow"
     */
    @Override
    public String getFormName(int race, int form) {
        CustomNPCsException c = new CustomNPCsException("Invalid \nform ID for race " + JRMCoreH.Races[race], new Object[0]);
        CustomNPCsException r = new CustomNPCsException("Invalid Race : \nValid Races are \n0 Human, 1 Saiyan\n 2 Half-Saiyan, 3 Namekian\n4 Arcosian, 5 Majin", new Object[1]);
        if (form >= 0) {
            if (race > 5) {
                throw r;
            } else {
                switch (race) {
                    case 0:
                    case 3:
                        if (form > 3) {
                            throw c;
                        }
                        break;
                    case 1:
                    case 2:
                        if (form > 20) {
                            throw c;
                        }
                        break;

                    case 4:
                        if (form > 7) {
                            throw c;
                        }
                        break;
                    case 5:
                        if (form > 4) {
                            throw c;
                        }
                        break;
                }
            }
        } else {
            throw c;
        }
        return JRMCoreH.trans[race][form];
    }

    /**
     * @param raceid Race ID
     * @param formId Form ID
     * @return All config data for a Form Mastery i.e Max Level, Instant Transform Unlock, Required Masteries
     */
    @Override
    public String[] getAllFormMasteryData(int raceid, int formId) {
        ArrayList<String> data = new ArrayList<>();
        data.add(JGConfigDBCFormMastery.getString(raceid, formId, JGConfigDBCFormMastery.DATA_ID_MAX_LEVEL, 0));
        data.add(JGConfigDBCFormMastery.getString(raceid, formId, JGConfigDBCFormMastery.DATA_ID_INSTANT_TRANSFORM_UNLOCK, 0));
        data.add(JGConfigDBCFormMastery.getString(raceid, formId, JGConfigDBCFormMastery.DATA_ID_REQUIRED_MASTERIES, 0));
        data.add(JGConfigDBCFormMastery.getString(raceid, formId, JGConfigDBCFormMastery.DATA_ID_AUTO_LEARN_ON_LEVEL, 0));
        data.add(JGConfigDBCFormMastery.getString(raceid, formId, JGConfigDBCFormMastery.DATA_ID_GAIN_TO_OTHER_MASTERIES, 0));

        return data.toArray(new String[0]);
    }

    /**
     * @param race      Race ID
     * @param nonRacial nonRacial forms are Kaioken/UI/Mystic/GOD
     * @return Number of forms a race has, i.e Saiyan has 14 racial and 4 non racial
     */
    @Override
    public int getAllFormsLength(int race, boolean nonRacial) {
        if (race < 0 || race > 5) {
            throw new CustomNPCsException("Races are from 0 to 5", new Object[0]);
        }
        if (nonRacial) {
            return JRMCoreH.transNonRacial.length;
        }
        return JRMCoreH.trans[race].length;
    }

    /**
     * @param race      Race ID
     * @param nonRacial check getAllFormsLength(int race, boolean nonRacial)
     * @return An array containing all forms the race has
     */
    @Override
    public String[] getAllForms(int race, boolean nonRacial) {
        if (race < 0 || race > 5) {
            throw new CustomNPCsException("Races are from 0 to 5", new Object[0]);
        }
        if (nonRacial) {
            return JRMCoreH.transNonRacial;

        }
        return JRMCoreH.trans[race];
    }

    /**
     * @return IKiAttack Object
     */
    @Override
    public IKiAttack createKiAttack() {
        return new KiAttack();
    }

    /**
     * @param type          Type of Ki Attack [0 - 8] "Wave", "Blast", "Disk", "Laser", "Spiral", "BigBlast", "Barrage", "Shield", "Explosion"
     * @param speed         Speed of Ki Attack [0 - 100]
     * @param damage        Damage for Ki Attack
     * @param hasEffect     True for Explosion
     * @param color         Color of Ki Attack [0 - 30] -> <br>
     *                      0: "AlignmentBased", "white", "blue", "purple", "red", "black", "green", "yellow", "orange", "pink", "magenta", <br>
     *                      11: "lightPink", "cyan", "darkCyan", "lightCyan", "darkGray", "gray", "darkBlue", "lightBlue", "darkPurple", "lightPurple", <br>
     *                      21: "darkRed", "lightRed", "darkGreen", "lime", "darkYellow", "lightYellow", "gold", "lightOrange", "darkBrown", "lightBrown" <br>
     * @param density       Density of Ki Attack > 0
     * @param hasSound      Play Impact Sound of Ki Attack
     * @param chargePercent Charge Percentage of Ki Attack [0 - 100]
     * @return IKiAttack Object with Set Values
     */
    @Override
    public IKiAttack createKiAttack(byte type, byte speed, int damage, boolean hasEffect, byte color, byte density, boolean hasSound, byte chargePercent) {
        return new KiAttack(type, speed, damage, hasEffect, color, density, hasSound, chargePercent);
    }

    /**
     * Fires a Ki Attack with the Following Params
     *
     * @param type          Type of Ki Attack [0 - 8] "Wave", "Blast", "Disk", "Laser", "Spiral", "BigBlast", "Barrage", "Shield", "Explosion"
     * @param speed         Speed of Ki Attack [0 - 100]
     * @param damage        Damage for Ki Attack
     * @param hasEffect     True for Explosion
     * @param color         Color of Ki Attack [0 - 30] -> <br>
     *                      0: "AlignmentBased", "white", "blue", "purple", "red", "black", "green", "yellow", "orange", "pink", "magenta", <br>
     *                      11: "lightPink", "cyan", "darkCyan", "lightCyan", "darkGray", "gray", "darkBlue", "lightBlue", "darkPurple", "lightPurple", <br>
     *                      21: "darkRed", "lightRed", "darkGreen", "lime", "darkYellow", "lightYellow", "gold", "lightOrange", "darkBrown", "lightBrown"
     * @param density       Density of Ki Attack > 0
     * @param hasSound      Play Impact Sound of Ki Attack
     * @param chargePercent Charge Percentage of Ki Attack [0 - 100]
     */
    @Override
    public void fireKiAttack(ICustomNpc npc, byte type, byte speed, int damage, boolean hasEffect, byte color, byte density, boolean hasSound, byte chargePercent) {
        if (npc == null)
            return;

        if (npc.getMCEntity() == null)
            return;

        EntityEnergyAtt entityEnergyAtt = null;
        try {
            if (JRMCoreConfig.dat5695[type]) {
                type = ValueUtil.clamp(type, (byte) 0, (byte) 8);
                speed = ValueUtil.clamp(speed, (byte) 0, (byte) 100);
                if (damage < 0) {
                    damage = 0;
                }
                byte effect = hasEffect ? (byte) 1 : (byte) 0;
                color = ValueUtil.clamp(color, (byte) 0, (byte) (JRMCoreH.techCol.length - 1));
                if (density < 0) {
                    density = 0;
                }
                byte playSound = hasSound ? (byte) 1 : (byte) 0;
                chargePercent = ValueUtil.clamp(chargePercent, (byte) 0, (byte) 100);
                byte[] sts = JRMCoreH.techDBCstatsDefault;

                IForm npcForm = ((DBCDisplay) DBCAPI.Instance().getDBCDisplay(npc)).getForm();
                float destroyerDmgRed = -1;
                boolean enableDestroyer = false;
                if (npcForm != null) {
                    enableDestroyer = npcForm.getMastery().isDestroyerOn();
                    destroyerDmgRed = npcForm.getMastery().getDestroyerEnergyDamage();
                }

                EntityNPCInterface trueNpc = (EntityNPCInterface) npc.getMCEntity();
                npc.getMCEntity().worldObj.playSoundAtEntity(trueNpc, "jinryuudragonbc:DBC2.basicbeam_fire", 0.5F, 1.0F);
                entityEnergyAtt = new EntityEnergyAtt(trueNpc, type, speed, 50, effect, color, density, (byte) 0, (byte) 0, playSound, chargePercent, damage, 0, sts, (byte) 0);
                if (enableDestroyer) {
                    entityEnergyAtt.destroyer = true;
                    entityEnergyAtt.DAMAGE_REDUCTION = destroyerDmgRed;
                }
                trueNpc.worldObj.spawnEntityInWorld(entityEnergyAtt);
            }
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    /**
     * Fires an IKiAttack with its internal params
     */
    @Override
    public void fireKiAttack(ICustomNpc npc, IKiAttack kiAttack) {
        if (npc == null || npc.getMCEntity() == null || kiAttack == null)
            return;

        EntityEnergyAtt entityEnergyAtt = null;
        try {
            byte type = kiAttack.getType();
            byte speed = kiAttack.getSpeed();
            int damage = kiAttack.getDamage();
            boolean hasEffect = kiAttack.hasEffect();
            byte color = kiAttack.getColor();
            byte density = kiAttack.getDensity();
            boolean hasSound = kiAttack.hasSound();
            byte chargePercent = kiAttack.getChargePercent();

            if (JRMCoreConfig.dat5695[type]) {
                // Clamping and Verification
                type = ValueUtil.clamp(type, (byte) 0, (byte) 8);
                speed = ValueUtil.clamp(speed, (byte) 0, (byte) 8);
                if (damage < 0) {
                    damage = 0;
                }
                byte effect = hasEffect ? (byte) 1 : (byte) 0;
                color = ValueUtil.clamp(color, (byte) 0, (byte) (JRMCoreH.techCol.length - 1));
                if (density < 0) {
                    density = 0;
                }
                byte playSound = hasSound ? (byte) 1 : (byte) 0;
                chargePercent = ValueUtil.clamp(chargePercent, (byte) 0, (byte) 100);
                byte[] sts = JRMCoreH.techDBCstatsDefault;

                EntityNPCInterface trueNpc = (EntityNPCInterface) npc.getMCEntity();

                IForm npcForm = ((DBCDisplay) DBCAPI.Instance().getDBCDisplay(npc)).getForm();
                boolean useFormConfig = false;
                boolean enableDestroyer = false;
                float destroyerDmgRed = -1;
                if (kiAttack.isDestroyerAttack()) {
                    enableDestroyer = true;
                }
                if (npcForm != null && (enableDestroyer || kiAttack.respectFormDestoryerConfig())) {
                    IFormMastery formMasteryConfig = npcForm.getMastery();
                    if (formMasteryConfig.isDestroyerOn()) {
                        enableDestroyer = true;
                        useFormConfig = true;
                        destroyerDmgRed = formMasteryConfig.getDestroyerEnergyDamage();
                    }
                }
                npc.getMCEntity().worldObj.playSoundAtEntity(trueNpc, "jinryuudragonbc:DBC2.basicbeam_fire", 0.5F, 1.0F);
                entityEnergyAtt = new EntityEnergyAtt(trueNpc, type, speed, 50, effect, color, density, (byte) 0, (byte) 0, playSound, chargePercent, damage, 0, sts, (byte) 0);
                if (enableDestroyer) {
                    entityEnergyAtt.destroyer = true;

                    if (useFormConfig) {
                        entityEnergyAtt.DAMAGE_REDUCTION = destroyerDmgRed;
                    }
                }
                trueNpc.worldObj.spawnEntityInWorld(entityEnergyAtt);
            }
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @Override
    public int getSkillTPCostSingle(String skillName, int level) {
        int skillIndex = DBCUtils.getDBCSkillIndex(skillName);
        if (skillIndex == -1) {
            throw new CustomNPCsException("Skill name not recognized");
        }
        return DBCUtils.calculateDBCSkillTPCost(skillIndex, level);
    }

    @Override
    public int getSkillMindCostSingle(String skillName, int level) {
        int skillIndex = DBCUtils.getDBCSkillIndex(skillName);
        if (skillIndex == -1) {
            throw new CustomNPCsException("Skill name not recognized");
        }

        return DBCUtils.calculateDBCSkillMindCost(skillIndex, level);
    }

    @Override
    public int getSkillMindCostRecursive(String skillName, int level) {
        int skillIndex = DBCUtils.getDBCSkillIndex(skillName);
        if (skillIndex == -1) {
            throw new CustomNPCsException("Skill name not recognized");
        }

        return DBCUtils.calculateDBCSkillMindCostRecursively(skillIndex, level);
    }

    @Override
    public int getSkillTPCostRecursive(String skillName, int level) {
        int skillIndex = DBCUtils.getDBCSkillIndex(skillName);
        if (skillIndex == -1) {
            throw new CustomNPCsException("Skill name not recognized");
        }

        return DBCUtils.calculateDBCSkillTPCostRecursively(skillIndex, level);
    }

    @Override
    public int getSkillRacialTPCostSingle(int race, int level) {
        return DBCUtils.calculateDBCRacialSkillTPCost(race, level);
    }

    @Override
    public int getSkillRacialTPMindSingle(int race, int level) {
        return DBCUtils.calculateDBCRacialSkillMindCost(race, level);
    }

    @Override
    public int getSkillRacialTPCostSingleRecursive(int race, int level) {
        return DBCUtils.calculateDBCRacialSkillTPCostRecursively(race, level);
    }

    @Override
    public int getSkillRacialTPMindSingleRecursive(int race, int level) {
        return DBCUtils.calculateDBCRacialSkillMindCostRecursively(race, level);
    }

    @Override
    public int getUltraInstinctMaxLevel() {
        return JGConfigUltraInstinct.CONFIG_UI_LEVELS;
    }


}
