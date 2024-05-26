package kamkeel.npcdbc.util;

import JinRyuu.DragonBC.common.DBCConfig;
import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.i.ExtendedPlayer;
import JinRyuu.JRMCore.mod_JRMCore;
import JinRyuu.JRMCore.server.config.dbc.JGConfigDBCFormMastery;
import JinRyuu.JRMCore.server.config.dbc.JGConfigUltraInstinct;
import kamkeel.npcdbc.api.npc.IDBCStats;
import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

import static JinRyuu.JRMCore.JRMCoreH.*;

// Created by Goatee
public class DBCUtils {
    public static String[][] formattedNames = new String[][]{
            {"§fBase", "§eFull Release", "§cBuffed", "§f4God"},
            {"§fBase", "§eSuper Saiyan", "§eSuper Saiyan (Grade 2)", "§eSuper Saiyan (Grade 3)", "§eMastered Super Saiyan", "§eSuper Saiyan 2", "§eSuper Saiyan 3", "§4Oozaru", "§6Golden Oozaru", "§cSuper Saiyan God", "§bSuper Saiyan Blue", "", "", "", "§4Super Saiyan 4", "§bShinka"},
            {"§fBase", "§eSuper Saiyan", "§eSuper Saiyan (Grade 2)", "§eSuper Saiyan (Grade 3)", "§eMastered Super Saiyan", "§eSuper Saiyan 2", "§eSuper Saiyan 3", "§4Oozaru", "§6Golden Oozaru", "§cSuper Saiyan God", "§bSuper Saiyan Blue", "", "", "", "§4Super Saiyan 4", "§bShinka"},
            {"§fBase", "§eFull Release", "§aGiant Form", "§4God"},
            {"§7Minimal", "§7First Form", "§7Second Form", "§7Third Form", "§fBase", "§5Fifth Form", "§6Ultimate", "§4God"},
            {"§fBase", "§4Evil", "§cFull Power", "§dPurest", "§4God"}
    };
    public static int lastSetDamage = -1;

    public static String getFormattedStateName(int race, int state) {
        String out = "";
        try {
            out = formattedNames[race][state];
        } catch (IndexOutOfBoundsException ignored) {
        }
        return out;
    }


    /**
     * A generic that checks if given st2 has white hair in the config
     */
    public static Boolean isUIWhite(boolean ui, int st2) {
        if (!ui)
            return false;
        int i = JGConfigUltraInstinct.CONFIG_UI_LEVELS < st2 ? JGConfigUltraInstinct.CONFIG_UI_LEVELS : st2;
        int ultra_instinct_level = JRMCoreH.state2UltraInstinct(false, (byte) i);
        return JGConfigUltraInstinct.CONFIG_UI_HAIR_WHITE[ultra_instinct_level];
    }

    /**
     * Last UI Level player has unlocked. True if MUI last level with white hair
     */
    public static int lastUIlvl(boolean hairWhite, EntityPlayer p) {
        int ui = JRMCoreH.SklLvl(16, p);
        int curLvl = JGConfigUltraInstinct.CONFIG_UI_LEVELS < ui ? JGConfigUltraInstinct.CONFIG_UI_LEVELS : ui;
        boolean[] haircol = JGConfigUltraInstinct.CONFIG_UI_HAIR_WHITE;
        int blacklevel = 0, whitelevel = 0;
        for (int i = 0; i < curLvl; i++)
            if (!haircol[i])
                blacklevel = i + 1;
            else
                whitelevel = i + 1;
        if (hairWhite)
            return whitelevel;
        else
            return blacklevel;

    }

    public static boolean hasMUI(EntityPlayer p) {
        return lastUIlvl(true, p) > 0;
    }

    public static boolean isMUI(EntityPlayer p) {
        int ui = JRMCoreH.SklLvl(16, p);
        int curLvl = JGConfigUltraInstinct.CONFIG_UI_LEVELS < ui ? JGConfigUltraInstinct.CONFIG_UI_LEVELS : ui;
        boolean[] haircol = JGConfigUltraInstinct.CONFIG_UI_HAIR_WHITE;
        boolean is = false;
        for (int i = 0; i < curLvl; i++) {
            if (haircol[i])
                if (i + 1 == JRMCoreH.State2)
                    is = true;
        }

        return is;
    }


    public static double getMaxFormMasteryLvl(int st, int race) {
        // int n = JRMCoreH.trans[JRMCoreH.Race].length - 1; // kk? n + 1 : mys? n + 2 :
        // ui? n + 3: god? n + 4 : n;
        String max = JGConfigDBCFormMastery.getString(race, st, JGConfigDBCFormMastery.DATA_ID_MAX_LEVEL, 0);
        return Double.parseDouble(max);
    }

    public static double getFormMasteryValue(EntityPlayer p, int race, String formName) {
        String fm = JRMCoreH.getFormMasteryData(p);
        String masteries[] = fm.split(";");

        for (String s : masteries)
            if (s.toLowerCase().contains(formName.toLowerCase())) {
                String[] masteryvalues = s.split(",");
                double masteryvalue = Double.parseDouble(masteryvalues[1]);
                return masteryvalue;
            }

        return -1;
    }

    public static boolean isFM(EntityPlayer p, String formName, int race, double perc) {
        int st = JRMCoreH.getFormID(formName, race, true);
        double max = getMaxFormMasteryLvl(st, race);
        double fm = getFormMasteryValue(p, race, formName);
        return fm >= Utility.percent(max, perc);
    }

    public static boolean isFMMax(EntityPlayer p, String formName, int race) {
        return isFM(p, formName, race, 100);
    }

    public static int calculateDBCDamageFromSource(Entity Player, float dbcA, DamageSource s) {
        if (!Player.worldObj.isRemote && Player instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) Player;
            boolean dse = s != null && s.getEntity() != null && s.getEntity() instanceof EntityPlayer;
            if (!player.capabilities.isCreativeMode) {
                ExtendedPlayer props = ExtendedPlayer.get(player);
                boolean block = props.getBlocking() == 1;
                boolean isChargingKi = DBCData.get(player).stats.isChargingKiAttack();
                int[] PlyrAttrbts = PlyrAttrbts(player);
                NBTTagCompound nbt = nbt(player, "pres");
                byte state = nbt.getByte("jrmcState");
                byte state2 = nbt.getByte("jrmcState2");
                String sklx = getString(player, "jrmcSSltX");
                int t = SklLvl(4, player);
                byte race = nbt.getByte("jrmcRace");
                byte powerType = nbt.getByte("jrmcPwrtyp");
                byte classID = nbt.getByte("jrmcClass");
                byte release = getByte(player, "jrmcRelease");
                int resrv = getInt(player, "jrmcArcRsrv");
                String absorption = getString(player, "jrmcMajinAbsorptionData");
                int currStamina = getInt(player, "jrmcStamina");
                int currEnergy = getInt(player, "jrmcEnrgy");
                String ste = getString(player, "jrmcStatusEff");
                boolean mj = StusEfcts(12, ste);
                boolean lg = StusEfcts(14, ste);
                boolean mc = StusEfcts(13, ste);
                boolean kk = StusEfcts(5, ste);
                boolean mn = StusEfcts(19, ste);
                boolean gd = StusEfcts(20, ste);
                boolean lf = s != null && s == DamageSource.fall;
                int DEX = PlyrAttrbts[1];
                int CON = PlyrAttrbts[2];
                String[] ps = PlyrSkills(player);
                double per = 1.0;
                int def = 0;
                String x = getString(player, "jrmcStatusEff");
                boolean c = StusEfcts(10, x) || StusEfcts(11, x);
                if (powerType != 3 && powerType > 0) {
                    DEX = getPlayerAttribute(player, PlyrAttrbts, 1, state, state2, race, sklx, release, resrv, lg, mj, kk, mc, mn, gd, powerType, ps, c, absorption);
                }

                int kiProtection = 0;
                int kiProtectionCost = 0;
                boolean kiProtectOn = false;
                if (pwr_ki(powerType)) {
                    int maxCON = getPlayerAttribute(player, PlyrAttrbts, 2, state, state2, race, sklx, release, resrv, lg, mj, kk, mc, mn, gd, powerType, ps, c, absorption);
                    per = (double) (maxCON > CON ? maxCON : CON) / ((double) CON * 1.0);
                    def = stat(player, 1, powerType, 1, DEX, race, classID, 0.0F);
                    int SPI = PlyrAttrbts[5];
                    int energyPool = stat(player, 5, powerType, 5, SPI, race, classID, SklLvl_KiBs(ps, powerType));
                    def = (int) ((double) def * (double) release * 0.01 * (double) weightPerc(1, player));
                    kiProtectOn = !PlyrSettingsB(player, 10);
                    int kiProtectLevel = SklLvl(11, ps);
                    if (kiProtectOn) {
                        kiProtection = (int) ((double) kiProtectLevel * 0.005 * (double) energyPool * (double) release * 0.01);
                        if (kiProtection < 1) {
                            kiProtection = 1;
                        }

                        kiProtection = (int) ((double) kiProtection * DBCConfig.cnfKDd);
                        float damage = (float) dbcA / 3.0F / (float) (dbcA + "").length();
                        if (damage < 1.0F) {
                            damage = 1.0F;
                        }

                        kiProtectionCost = (int) ((double) kiProtectLevel * (double) release * 0.01 * (double) damage);
                        if (kiProtectionCost < 1) {
                            kiProtectionCost = 1;
                        }

                        kiProtectionCost = (int) ((double) kiProtectionCost * DBCConfig.cnfKDc);
                    }

                    def += kiProtection;
                } else if (pwr_cha(powerType)) {
                    int ta = SklLvl(0, 2, ps);
                    int cj = SklLvlY(2, getString(player, "jrmcSSltY"));
                    def = stat(player, 1, powerType, 1, DEX, race, classID, (float) ta * 0.04F + (float) state * 0.25F);
                    def = (int) ((double) (def * release) * 0.01);
                    if (classID == 2) {
                        String StE = nbt.getString("jrmcStatusEff");
                        if (StusEfcts(16, StE)) {
                            int WIL = PlyrAttrbts[3];
                            int statWIL = stat(player, 3, powerType, 5, WIL, race, classID, 0.0F);
                            def += (int) ((double) statWIL * 0.25 * (double) release * 0.01);
                        }
                    }
                } else if (pwr_sa(powerType)) {
                    def = 0;
                } else {
                    def = stat(player, 1, powerType, 1, DEX, race, classID, 0.0F);
                }

                int staminaCost = (int) ((float) (def - kiProtection) * 0.05F);
                if (block && currStamina >= staminaCost) {
                    int id = (int) (Math.random() * 2.0) + 1;
                    player.worldObj.playSoundAtEntity(player, "jinryuudragonbc:DBC4.block" + id, 0.5F, 0.9F / (player.worldObj.rand.nextFloat() * 0.6F + 0.9F));
                    if (!isInCreativeMode(player)) {
                        setInt(currStamina - staminaCost < 0 ? 0 : currStamina - staminaCost, player, "jrmcStamina");
                    }
                } else if (isChargingKi && ConfigDBCGameplay.EnableChargingDex) {
                    // Charging Dex
                    switch (classID) {
                        case 0:
                            def = (int) ((float) ((def - kiProtection) * ConfigDBCGameplay.MartialArtistCharge) * 0.01F) + kiProtection;
                            break;
                        case 1:
                            def = (int) ((float) ((def - kiProtection) * ConfigDBCGameplay.SpiritualistCharge) * 0.01F) + kiProtection;
                            break;
                        case 2:
                            def = (int) ((float) ((def - kiProtection) * ConfigDBCGameplay.WarriorCharge) * 0.01F) + kiProtection;
                            break;
                        default:
                            def = (int) ((float) ((def - kiProtection) * JRMCoreConfig.StatPasDef) * 0.01F) + kiProtection;
                            break;
                    }
                } else {
                    def = (int) ((float) ((def - kiProtection) * JRMCoreConfig.StatPasDef) * 0.01F) + kiProtection;
                }

                if (currEnergy >= kiProtectionCost) {
                    if (!isInCreativeMode(player)) {
                        setInt(currEnergy - kiProtectionCost < 0 ? 0 : currEnergy - kiProtectionCost, player, "jrmcEnrgy");
                    }
                } else {
                    def -= kiProtection;
                }

                if (JRMCoreConfig.DebugInfo || difp.length() > 0 && player.getCommandSenderName().equalsIgnoreCase(difp)) {
                    mod_JRMCore.logger.info(player.getCommandSenderName() + " receives Damage: Original=" + dbcA);
                }

                int defensePenetration = 0;
                if (s != null && dse) {
                    String[] ops = PlyrSkills((EntityPlayer) s.getEntity());
                    defensePenetration = SklLvl(14, 1, ops);
                } else if (s != null && s.getEntity() instanceof EntityLivingBase) {
                    defensePenetration = 10;
                }

                int defense = lf ? 0 : def;
                int defensePen2 = (int) ((float) (defense * defensePenetration) * 0.01F);
                double e = (double) (1.0F - 0.03F * (float) t);
                String ss = "A=" + defense + (defensePen2 > 0 ? "-" + defensePenetration + "%" : "") + ", SEM=" + (1.0F - 0.03F * (float) t);
                dbcA = (int) ((double) (dbcA - (defense - defensePen2)) * e);
                dbcA = dbcA < 1 ? 1 : dbcA;
                if ((double) ((float) (dbcA * defensePenetration) * 0.01F) * e > (double) dbcA) {
                    dbcA = (int) ((double) ((float) (dbcA * defensePenetration) * 0.01F) * e);
                }

                dbcA = (int) ((double) dbcA / per);

                if (JRMCoreConfig.DebugInfo || difp.length() > 0 && player.getCommandSenderName().equalsIgnoreCase(difp)) {
                    mod_JRMCore.logger.info(player.getCommandSenderName() + " DM: A=" + dbcA + ", DF Div:" + per + ", " + ss);
                }

                if (DBC()) {
                    ItemStack stackbody = ExtendedPlayer.get(player).inventory.getStackInSlot(1);
                    ItemStack stackhead = ExtendedPlayer.get(player).inventory.getStackInSlot(2);
                    if (stackbody != null) {
                        stackbody.damageItem(1, player);
                    }

                    if (stackhead != null) {
                        stackhead.damageItem(1, player);
                    }
                }

                int currentHP = getInt(player, "jrmcBdy");
                float all = currentHP - dbcA;
                int newHP = all < 0 ? 0 : (int) all;
                if (dse) {
                    boolean friendlyFist = PlyrSettingsB((EntityPlayer) s.getEntity(), 12);
                    if (friendlyFist && !s.getDamageType().equals("MajinAbsorption") && !s.getEntity().equals(Player)) {
                        int ko = getInt(player, "jrmcHar4va");
                        newHP = all < 20 ? 20 : (int) all;
                        if (ko <= 0 && newHP == 20) {
                            return 0;
                        }
                    }
                }
                return currentHP - newHP;
            }
        }

        return (int) dbcA;
    }


    public static int calculateDBCStatDamage(EntityPlayer player, int damageAmount, IDBCStats dbcStats) {
        if (!player.worldObj.isRemote && dbcStats != null && damageAmount > 0) {
            if (!player.capabilities.isCreativeMode) {
                ExtendedPlayer props = ExtendedPlayer.get(player);
                boolean block = props.getBlocking() == 1;
                boolean isChargingKi = DBCData.get(player).stats.isChargingKiAttack();

                int[] attributes = PlyrAttrbts(player);
                String[] playerSkills = PlyrSkills(player);

                NBTTagCompound nbt = nbt(player, "pres");
                byte state = nbt.getByte("jrmcState");
                byte state2 = nbt.getByte("jrmcState2");
                String racialSkill = getString(player, "jrmcSSltX");
                byte race = nbt.getByte("jrmcRace");
                byte powerType = nbt.getByte("jrmcPwrtyp");
                byte classID = nbt.getByte("jrmcClass");
                byte release = getByte(player, "jrmcRelease");
                int arcoPP = getInt(player, "jrmcArcRsrv");
                String absorption = getString(player, "jrmcMajinAbsorptionData");
                int currStamina = getInt(player, "jrmcStamina");
                int currEnergy = getInt(player, "jrmcEnrgy");

                String statusEffects = getString(player, "jrmcStatusEff");
                boolean isMajin = StusEfcts(12, statusEffects);
                boolean isMystic = StusEfcts(13, statusEffects);
                boolean isLegendary = StusEfcts(14, statusEffects);
                boolean isKK = StusEfcts(5, statusEffects);
                boolean isUI = StusEfcts(19, statusEffects);
                boolean isGoD = StusEfcts(20, statusEffects);
                boolean isFused = JRMCoreH.StusEfcts(10, statusEffects) || JRMCoreH.StusEfcts(11, statusEffects);

                int DEX = 0;
                int CON = attributes[2];

                // Dex Calculation
                if (!dbcStats.isIgnoreDex())
                    DEX = getPlayerAttribute(player, attributes, 1, state, state2, race, racialSkill, release, arcoPP, isLegendary, isMajin, isKK, isMystic, isUI, isGoD, powerType, playerSkills, isFused, absorption);

                int def = 0;
                // KI STUFF
                boolean kiProtectOn = !PlyrSettingsB((EntityPlayer) player, 10);
                int kiProtection = 0;
                int kiProtectionCost = 0;
                int maxKiPool = JRMCoreH.stat(player, 5, powerType, 5, attributes[5], race, classID, JRMCoreH.SklLvl_KiBs(playerSkills, powerType));
                int kiProtectLevel = JRMCoreH.SklLvl(11, playerSkills);

                double formDamageReduction = 1;
                if (!dbcStats.isIgnoreFormReduction()) {
                    int formDecimal = getPlayerAttribute(player, attributes, 2, state, state2, race, racialSkill, release, arcoPP, isLegendary, isMajin, isKK, isMystic, isUI, isGoD, powerType, playerSkills, isFused, absorption);
                    formDamageReduction = (double) (Math.max(formDecimal, CON)) / ((double) CON);
                }

                def = stat(player, 1, powerType, 1, DEX, race, classID, 0.0F);
                def = (int) ((double) def * (double) release * 0.01 * (double) weightPerc(1, player));

                if (kiProtectOn) {
                    ////////////////////
                    //// IF KI PROTECTION IGNORE
                    // kiProtection = 0
                    if (!dbcStats.isIgnoreKiProtection()) {
                        kiProtection = (int) ((double) kiProtectLevel * 0.005 * maxKiPool * (double) release * 0.01);
                        if (kiProtection < 1) {
                            kiProtection = 1;
                        }

                        // Ki Protection
                        kiProtection = (int) ((double) kiProtection * DBCConfig.cnfKDd);
                        float kiProtectDamage = (float) damageAmount / 3.0F / (float) (damageAmount + "").length();
                        if (kiProtectDamage < 1.0F) {
                            kiProtectDamage = 1.0F;
                        }
                        ////////////////////

                        ////////////////////
                        // Cost Ki Protection
                        kiProtectionCost = (int) ((double) kiProtectLevel * (double) release * 0.01 * (double) kiProtectDamage);
                        if (kiProtectionCost < 1) {
                            kiProtectionCost = 1;
                        }
                        kiProtectionCost = (int) ((double) kiProtectionCost * DBCConfig.cnfKDc);
                        ////////////////////
                    }
                }
                def += kiProtection;


                int staminaCost = (int) ((float) (def - kiProtection) * 0.05F);
                ////////////////////
                ////// EFFECT STAMINA BOOL
                // Reduce Stamina
                if (block && !dbcStats.isIgnoreBlock() && currStamina >= staminaCost) {
                    if (!isInCreativeMode(player)) {
                        setInt(Math.max(currStamina - staminaCost, 0), player, "jrmcStamina");
                    }
                } else if (isChargingKi && ConfigDBCGameplay.EnableChargingDex) {
                    // Charging Dex
                    switch (classID) {
                        case 0:
                            def = (int) ((float) ((def - kiProtection) * ConfigDBCGameplay.MartialArtistCharge) * 0.01F) + kiProtection;
                            break;
                        case 1:
                            def = (int) ((float) ((def - kiProtection) * ConfigDBCGameplay.SpiritualistCharge) * 0.01F) + kiProtection;
                            break;
                        case 2:
                            def = (int) ((float) ((def - kiProtection) * ConfigDBCGameplay.WarriorCharge) * 0.01F) + kiProtection;
                            break;
                        default:
                            def = (int) ((float) ((def - kiProtection) * JRMCoreConfig.StatPasDef) * 0.01F) + kiProtection;
                            break;
                    }
                } else {
                    // Passive Dex
                    def = (int) ((float) ((def - kiProtection) * JRMCoreConfig.StatPasDef) * 0.01F) + kiProtection;
                }
                ////////////////////

                ////////////////////
                ////// EFFECT KI BOOL
                // Reduce Energy
                if (currEnergy >= kiProtectionCost) {
                    if (!isInCreativeMode(player)) {
                        setInt(Math.max(currEnergy - kiProtectionCost, 0), player, "jrmcEnrgy");
                    }
                } else {
                    def -= kiProtection;
                }
                ////////////////////

                // Damage Amount before any calculations modify it
                int rawDamage = damageAmount;

                // Defense after Ki Protection / Dex calculations
                int rawDefense = def;

                double enduranceReduction = 1;
                if (!dbcStats.isIgnoreEndurance()) {
                    int enduranceLevel = SklLvl(4, player);

                    // Calculates the Amount to Reduce the Damage with Endurance Level
                    enduranceReduction = (double) (1.0F - 0.03F * (float) enduranceLevel);
                }

                int defensePenetrated = 0;
                // Default Penetration of the NPC
                // By default all entities are 10. Players have their own
                // default penetration skill. Based on if they are in Legendary
                int npcDefensePenetration = dbcStats.getDefensePenetration();
                if (dbcStats.hasDefensePenetration()) {
                    // Defense Penetrated = RawDefense * (defensePen * 0.01%)
                    // Defense Pen of 10 --> RawDefense * 0.1 -- 10% Penetrated
                    // Defense Pen of 50 --> RawDefense * 0.5 -- 50% Penetrated
                    // Defense Pen of 100 --> RawDefense * 1 -- 100% Penetrated
                    defensePenetrated = (int) ((float) (rawDefense * npcDefensePenetration) * 0.01F);
                }

                // Damage after Reduction
                damageAmount = (int) ((damageAmount - rawDefense - defensePenetrated) * enduranceReduction);

                // Prevents Negative Damages
                damageAmount = Math.max(damageAmount, 1);

                if (dbcStats.hasDefensePenetration()) {
                    // Guarantee Damage is dealt with Defense Penetration
                    if ((double) ((float) (rawDamage * npcDefensePenetration) * 0.01F) * enduranceReduction > (double) damageAmount) {
                        damageAmount = (int) ((double) ((float) (rawDamage * npcDefensePenetration) * 0.01F) * enduranceReduction);
                    }
                }

                // Consider Stamina Cost or Con on the Damage Amount
                damageAmount = (int) ((double) damageAmount / formDamageReduction);

                int playerHP = getInt(player, "jrmcBdy");
                int reducedHP = playerHP - damageAmount;
                int newHP = Math.max(reducedHP, 0);
                return playerHP - newHP;
            }
        }
        return damageAmount;
    }

    public static void doDBCDamage(EntityPlayer player, int damageToHP, IDBCStats dbcStats) {
        NBTTagCompound nbt = nbt(player, "pres");
        byte state = nbt.getByte("jrmcState");
        byte race = nbt.getByte("jrmcRace");
        String statusEffects = getString(player, "jrmcStatusEff");

        int playerHP = getInt(player, "jrmcBdy");
        int reducedHP = playerHP - damageToHP;
        int newHP = Math.max(reducedHP, 0);

        if (lastSetDamage != -1) {
            damageToHP = Math.max(lastSetDamage, 0);
            lastSetDamage = -1;
            reducedHP = playerHP - damageToHP;
            newHP = Math.max(reducedHP, 0);
        }

        boolean friendlyFist = dbcStats.isFriendlyFist();
        if (friendlyFist) {
            int ko = getInt(player, "jrmcHar4va");
            newHP = Math.max(reducedHP, 20);
            if (ko <= 0 && newHP == 20) {
                setInt((int) 6, player, "jrmcHar4va");
                setByte(race == 4 ? (state < 4 ? state : 4) : 0, player, "jrmcState");
                setByte((int) 0, player, "jrmcState2");
                setByte((int) 0, player, "jrmcRelease");
                setInt((int) 0, player, "jrmcStamina");
                StusEfcts(19, statusEffects, (EntityPlayer) player, false);
            }
        }

        if (!isInCreativeMode(player)) {
            setInt(newHP, player, "jrmcBdy");
        }
    }

    public static boolean noBonusEffects = false;
    public static int calculateKiDrainMight(DBCData dbcData, EntityPlayer player){
        int[] playerAttributes = JRMCoreH.PlyrAttrbts(dbcData.player); //Need to get fused attributes, major refactor of DBCData later on?

        String skillX = dbcData.RacialSkills;
        noBonusEffects = true;

        int strengthBuff = JRMCoreH.getPlayerAttribute(dbcData.player, playerAttributes, 0, dbcData.State, 0, 1, skillX, dbcData.Release, dbcData.ArcReserve,  false, false, false, false, false, false, 1, null, false, "") - playerAttributes[0];
        int dexBuff = JRMCoreH.getPlayerAttribute(dbcData.player, playerAttributes, 1, dbcData.State, 0, 1, skillX, dbcData.Release, dbcData.ArcReserve,  false, false, false, false, false, false, 1, null, false, "") - playerAttributes[1];
        int willBuff = JRMCoreH.getPlayerAttribute(dbcData.player, playerAttributes, 3, dbcData.State, 0, 1, skillX, dbcData.Release, dbcData.ArcReserve,  false, false, false, false, false, false, 1, null, false, "") - playerAttributes[3];

        noBonusEffects = false;

        float might = strengthBuff * 0.4F + dexBuff * 0.25F + willBuff * 0.35F;

        return (int) might;
    }

}
