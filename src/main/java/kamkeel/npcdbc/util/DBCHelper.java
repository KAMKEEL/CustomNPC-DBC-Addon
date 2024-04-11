package kamkeel.npcdbc.util;

import JinRyuu.DragonBC.common.DBCConfig;
import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.i.ExtendedPlayer;
import kamkeel.npcdbc.data.DBCStats;
import kamkeel.npcdbc.mixin.INPCStats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.entity.EntityNPCInterface;

import static JinRyuu.JRMCore.JRMCoreH.*;

public class DBCHelper {

    public static int calculateDamage(EntityPlayer player, int damageAmount, EntityNPCInterface npc) {
        if (!player.worldObj.isRemote && npc != null && damageAmount > 0) {
            if (!player.capabilities.isCreativeMode) {
                DBCStats dbcStats = ((INPCStats) npc.stats).getDBCStats();

                ExtendedPlayer props = ExtendedPlayer.get(player);
                boolean block = props.getBlocking() == 1;

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
                if(!dbcStats.isIgnoreDex())
                    DEX = getPlayerAttribute(player, attributes, 1, state, state2, race, racialSkill, release, arcoPP, isLegendary, isMajin, isKK, isMystic, isUI, isGoD, powerType, playerSkills, isFused, absorption);

                int def = 0;
                // KI STUFF
                boolean kiProtectOn = !PlyrSettingsB((EntityPlayer)player, 10);
                int kiProtection = 0;
                int kiProtectionCost = 0;
                int maxKiPool = JRMCoreH.stat(player, 5, powerType, 5, attributes[5], race, classID, JRMCoreH.SklLvl_KiBs(playerSkills, powerType));
                int kiProtectLevel = JRMCoreH.SklLvl(11, playerSkills);;

                double formDamageReduction = 1;
                if(!dbcStats.isIgnoreFormReduction()){
                    int formDecimal = getPlayerAttribute(player, attributes, 2, state, state2, race, racialSkill, release, arcoPP, isLegendary, isMajin, isKK, isMystic, isUI, isGoD, powerType, playerSkills, isFused, absorption);
                    formDamageReduction = (double)(Math.max(formDecimal, CON)) / ((double) CON);
                }

                def = stat(player, 1, powerType, 1, DEX, race, classID, 0.0F);
                def = (int)((double)def * (double)release * 0.01 * (double)weightPerc(1, player));

                if (kiProtectOn) {
                    ////////////////////
                    //// IF KI PROTECTION IGNORE
                    // kiProtection = 0
                    if(!dbcStats.isIgnoreKiProtection()){
                        kiProtection = (int)((double)kiProtectLevel * 0.005 * maxKiPool * (double)release * 0.01);
                        if (kiProtection < 1) {
                            kiProtection = 1;
                        }

                        // Ki Protection
                        kiProtection = (int)((double)kiProtection * DBCConfig.cnfKDd);
                        float kiProtectDamage = (float)damageAmount / 3.0F / (float)(damageAmount + "").length();
                        if (kiProtectDamage < 1.0F) {
                            kiProtectDamage = 1.0F;
                        }
                        ////////////////////

                        ////////////////////
                        // Cost Ki Protection
                        kiProtectionCost = (int)((double)kiProtectLevel * (double)release * 0.01 * (double)kiProtectDamage);
                        if (kiProtectionCost < 1) {
                            kiProtectionCost = 1;
                        }
                        kiProtectionCost = (int)((double)kiProtectionCost * DBCConfig.cnfKDc);
                        ////////////////////
                    }
                }
                def += kiProtection;


                int staminaCost = (int)((float)(def - kiProtection) * 0.05F);
                ////////////////////
                ////// EFFECT STAMINA BOOL
                // Reduce Stamina
                if (block && !dbcStats.isIgnoreBlock() && currStamina >= staminaCost) {
                    if (!isInCreativeMode(player)) {
                        setInt(Math.max(currStamina - staminaCost, 0), player, "jrmcStamina");
                    }
                } else {
                    // Passive Dex
                    def = (int)((float)((def - kiProtection) * JRMCoreConfig.StatPasDef) * 0.01F) + kiProtection;
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
                if(!dbcStats.isIgnoreEndurance()){
                    int enduranceLevel = SklLvl(4, player);

                    // Calculates the Amount to Reduce the Damage with Endurance Level
                    enduranceReduction = (double)(1.0F - 0.03F * (float)enduranceLevel);
                }

                double damageBreakThrough = damageAmount;
                // Default Penetration of the NPC
                // By default all entities are 10. Players have their own
                // default penetration skill. Based on if they are in Legendary
                int npcDefensePenetration = dbcStats.getDefensePenetration();
                if(dbcStats.hasDefensePenetration()){
                    // Defense Penetrated = RawDefense * (defensePen * 0.01%)
                    // Defense Pen of 10 --> RawDefense * 0.1 -- 10% Penetrated
                    // Defense Pen of 50 --> RawDefense * 0.5 -- 50% Penetrated
                    // Defense Pen of 100 --> RawDefense * 1 -- 100% Penetrated
                    int defensePenetrated = (int)((float)(rawDefense * npcDefensePenetration) * 0.01F);

                    // The Amount of Damage that will break through based on defensePenetrated
                    damageBreakThrough = (double)(damageAmount - (rawDefense - defensePenetrated));
                }

                // Damage after Reduction
                damageAmount = (int)(damageBreakThrough * enduranceReduction);

                // Prevents Negative Damages
                damageAmount = Math.max(damageAmount, 1);

                if(dbcStats.hasDefensePenetration()){
                    // Guarantee Damage is dealt with Defense Penetration
                    if ((double)((float)(rawDamage * npcDefensePenetration) * 0.01F) * enduranceReduction > (double)damageAmount) {
                        damageAmount = (int)((double)((float)(rawDamage * npcDefensePenetration) * 0.01F) * enduranceReduction);
                    }
                }

                // Consider Stamina Cost or Con on the Damage Amount
                damageAmount = (int)((double)damageAmount / formDamageReduction);

                int playerHP = getInt(player, "jrmcBdy");
                int reducedHP = playerHP - damageAmount;
                int newHP = Math.max(reducedHP, 0);

                // FIX FRIENDLY FIST
                boolean friendlyFist = false;
                if (friendlyFist) {
                    int ko = getInt(player, "jrmcHar4va");
                    newHP = Math.max(reducedHP, 20);
                    if (ko <= 0 && newHP == 20) {
                        setInt((int)6, player, "jrmcHar4va");
                        setByte(race == 4 ? (state < 4 ? state : 4) : 0, player, "jrmcState");
                        setByte((int)0, player, "jrmcState2");
                        setByte((int)0, player, "jrmcRelease");
                        setInt((int)0, player, "jrmcStamina");
                        StusEfcts(19, statusEffects, (EntityPlayer)player, false);
                    }
                    damageAmount -= reducedHP;
                }

                if (!isInCreativeMode(player)) {
                    setInt(newHP, player, "jrmcBdy");
                }
            }
        }
        return damageAmount;
    }
}
