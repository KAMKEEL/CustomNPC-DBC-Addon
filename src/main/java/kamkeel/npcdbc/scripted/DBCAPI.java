package kamkeel.npcdbc.scripted;

import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.entity.EntityEnergyAtt;
import JinRyuu.JRMCore.server.config.dbc.JGConfigDBCFormMastery;
import kamkeel.npcdbc.api.*;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.DBCStats;
import kamkeel.npcdbc.data.KiAttack;
import kamkeel.npcdbc.mixin.INPCDisplay;
import kamkeel.npcdbc.mixin.INPCStats;
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

    public ICustomForm createCustomForm(String name) {
        return FormController.getInstance().createForm(name);
    }

    @Override
    public ICustomForm getCustomForm(String name) {
        return FormController.getInstance().get(name);
    }

    @Override
    public IDBCStats abstractDBCData() {
        DBCStats dbcStats = new DBCStats();
        dbcStats.enabled = true;
        return dbcStats;
    }

    @Override
    public IDBCStats getDBCData(ICustomNpc<EntityNPCInterface> npc) {
        if (npc.getMCEntity() instanceof EntityNPCInterface)
            return ((INPCStats) npc.getMCEntity().stats).getDBCStats();
        return null;
    }

    @Override
    public IDBCDisplay getDBCDisplay(ICustomNpc<EntityNPCInterface> npc) {
        if (npc.getMCEntity() instanceof EntityNPCInterface)
            return ((INPCDisplay) npc.getMCEntity().display).getDBCDisplay();
        return null;
    }

    @Override
    public void doDBCDamage(IPlayer player, IDBCStats stats, int damage) {
        if (player == null || stats == null)
            return;

        if (!stats.isEnabled() || !(player.getMCEntity() instanceof EntityPlayer))
            return;

        EntityPlayer entityPlayer = (EntityPlayer) player.getMCEntity();
        DBCUtils.doDBCDamage(entityPlayer, damage, stats);
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
        return null;
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
     * @param color         Color of Ki Attack [0 - 30] ->
     *                      0: "AlignmentBased", "white", "blue", "purple", "red", "black", "green", "yellow", "orange", "pink", "magenta",
     *                      11: "lightPink", "cyan", "darkCyan", "lightCyan", "darkGray", "gray", "darkBlue", "lightBlue", "darkPurple", "lightPurple",
     *                      21: "darkRed", "lightRed", "darkGreen", "lime", "darkYellow", "lightYellow", "gold", "lightOrange", "darkBrown", "lightBrown"
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
     * @param color         Color of Ki Attack [0 - 30] ->
     *                      0: "AlignmentBased", "white", "blue", "purple", "red", "black", "green", "yellow", "orange", "pink", "magenta",
     *                      11: "lightPink", "cyan", "darkCyan", "lightCyan", "darkGray", "gray", "darkBlue", "lightBlue", "darkPurple", "lightPurple",
     *                      21: "darkRed", "lightRed", "darkGreen", "lime", "darkYellow", "lightYellow", "gold", "lightOrange", "darkBrown", "lightBrown"
     * @param density       Density of Ki Attack > 0
     * @param hasSound      Play Impact Sound of Ki Attack
     * @param chargePercent Charge Percentage of Ki Attack [0 - 100]
     */
    @Override
    public void fireKiAttack(ICustomNpc<EntityNPCInterface> npc, byte type, byte speed, int damage, boolean hasEffect, byte color, byte density, boolean hasSound, byte chargePercent) {
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

                EntityNPCInterface trueNpc = npc.getMCEntity();
                npc.getMCEntity().worldObj.playSoundAtEntity(trueNpc, "jinryuudragonbc:DBC2.basicbeam_fire", 0.5F, 1.0F);
                entityEnergyAtt = new EntityEnergyAtt(trueNpc, type, speed, 50, effect, color, density, (byte) 0, (byte) 0, playSound, chargePercent, damage, 0, sts, (byte) 0);
                trueNpc.worldObj.spawnEntityInWorld(entityEnergyAtt);
            }
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    /**
     * Fires an IKiAttack with its internal params
     */
    @Override
    public void fireKiAttack(ICustomNpc<EntityNPCInterface> npc, IKiAttack kiAttack) {
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

                EntityNPCInterface trueNpc = npc.getMCEntity();
                npc.getMCEntity().worldObj.playSoundAtEntity(trueNpc, "jinryuudragonbc:DBC2.basicbeam_fire", 0.5F, 1.0F);
                entityEnergyAtt = new EntityEnergyAtt(trueNpc, type, speed, 50, effect, color, density, (byte) 0, (byte) 0, playSound, chargePercent, damage, 0, sts, (byte) 0);
                trueNpc.worldObj.spawnEntityInWorld(entityEnergyAtt);
            }
        } catch (IndexOutOfBoundsException ignored) {
        }
    }
}
