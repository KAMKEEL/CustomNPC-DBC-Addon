package kamkeel.npcdbc.scripted;

import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.server.JGPlayerMP;
import kamkeel.npcdbc.api.IDBCAddon;
import kamkeel.npcdbc.util.DBCUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.scripted.CustomNPCsException;
import noppes.npcs.scripted.entity.ScriptDBCPlayer;

import static JinRyuu.JRMCore.JRMCoreH.getInt;

// Implemented by Kam, Ported from Goatee Design
public class ScriptDBCAddon<T extends EntityPlayerMP> extends ScriptDBCPlayer<T> implements IDBCAddon {
    public T player;
    public NBTTagCompound nbt;

    public ScriptDBCAddon(T player) {
        super(player);
        this.player = player;
        this.nbt = player.getEntityData().getCompoundTag("PlayerPersisted");
    }

    @Override
    public int[] getAllFullStats() {
        int[] fullstats = new int[6];
        JGPlayerMP JG = new JGPlayerMP(player);
        NBTTagCompound nbt = player.getEntityData().getCompoundTag("PlayerPersisted");
        JG.setNBT(nbt);
        fullstats[0] = JG.getAttribute(0);
        fullstats[1] = JG.getAttribute(1);
        fullstats[2] = nbt.getInteger("jrmcCnsI");
        fullstats[3] = JG.getAttribute(3);
        fullstats[4] = nbt.getInteger("jrmcIntI");
        fullstats[5] = nbt.getInteger("jrmcCncI");

        return fullstats;
    }

    /**
     * @return Player's max body
     */
    @Override
    public int getMaxBody() {
        return DBCUtils.getMaxStat(player, 2);
    }

    /**
     * @return Player's max hp (body)
     */
    @Override
    public int getMaxHP() {
        return getMaxBody();
    }

    /**
     * @return Current body as a percentage of max hp (body)
     */
    @Override
    public float getBodyPercentage() {
        return DBCUtils.bodyPerc(player);
    }

    /**
     * @return Player's max ki
     */
    @Override
    public int getMaxKi() {
        int[] PlyrAttrbts = JRMCoreH.PlyrAttrbts(player);
        JGPlayerMP JG = new JGPlayerMP(player);
        JG.setNBT(nbt);
        byte pwr = JRMCoreH.getByte(player, "jrmcPwrtyp");
        byte rce = JRMCoreH.getByte(player, "jrmcRace");
        byte cls = JRMCoreH.getByte(player, "jrmcClass");
        return JG.getEnergyMax(rce, cls, pwr, PlyrAttrbts, JRMCoreH.SklLvl_KiBs(player, pwr));
    }

    /**
     * @return Player's max stamina
     */
    @Override
    public int getMaxStamina() {
        int[] PlyrAttrbts = JRMCoreH.PlyrAttrbts(player);
        JGPlayerMP JG = new JGPlayerMP(player);
        JG.setNBT(nbt);
        byte pwr = JRMCoreH.getByte(player, "jrmcPwrtyp");
        byte rce = JRMCoreH.getByte(player, "jrmcRace");
        byte cls = JRMCoreH.getByte(player, "jrmcClass");

        return JG.getStaminaMax(rce, cls, pwr, PlyrAttrbts);
    }

    /**
     * @return an array of all player attributes
     */
    @Override
    public int[] getAllAttributes() {
        int[] stats = new int[6];
        NBTTagCompound nbt = player.getEntityData().getCompoundTag("PlayerPersisted");
        stats[0] = nbt.getInteger("jrmcStrI");
        stats[1] = nbt.getInteger("jrmcDexI");
        stats[2] = nbt.getInteger("jrmcCnsI");
        stats[3] = nbt.getInteger("jrmcWilI");
        stats[4] = nbt.getInteger("jrmcIntI");
        stats[5] = nbt.getInteger("jrmcCncI");

        return stats;
    }

    /**
     *
     * @param attri adds attri to player stats
     * @param multiplyAddedAttris if true, adds first then multiplies by multivalue
     * @param multiValue  value to multiply with
     */
    @Override
    public void modifyAllAttributes(int[] attri, boolean multiplyAddedAttris, double multiValue) {
        if (attri.length == 6) {
            int[] stats = getAllAttributes();
            double multi = multiValue;
            if (multiValue == 0 || !multiplyAddedAttris) {
                multi = 1.0;
            }

            int[] newstats = new int[stats.length];
            for (int i = 0; i < stats.length; i++) {
                newstats[i] = (int) ((double) (stats[i] + attri[i]) * multi);
                nbt.setInteger(JRMCoreH.AttrbtNbtI[i], newstats[i]);

            }
            this.setBody(getMaxBody());
            this.setKi(getMaxKi());
            this.setStamina(getMaxStamina());
        }
    }

    /**
     *
     * @param Num adds all attributes by Num
     * @param setStatsToNum sets all attributes by Num
     */
    @Override
    public void modifyAllAttributes(int Num, boolean setStatsToNum) {

        int[] num = new int[]{Num, Num, Num, Num, Num, Num};
        if (!setStatsToNum) {
            modifyAllAttributes(num, false, 1);
        } else {
            for (int i = 0; i < num.length; i++) {
                nbt.setInteger(JRMCoreH.AttrbtNbtI[i], num[i]);
            }
            this.setBody(getMaxBody());
            this.setKi(getMaxKi());
            this.setStamina(getMaxStamina());
        }
    }

    /**
     * If not setStats, then it will ADD submitted[0] to stats[0]. Submitted must be length of 6
     * @param submitted Adds all attributes by array of attributes respectively i.e atr 0 gets added to index 0 of a
     * @param setStats sets all attributes to submitted array
     */
    @Override
    public void modifyAllAttributes(int[] submitted, boolean setStats) {
        if (submitted.length == 6) {
            if (!setStats) {
                modifyAllAttributes(submitted, false, 1);
            } else {
                for (int i = 0; i < submitted.length; i++) {
                    nbt.setInteger(JRMCoreH.AttrbtNbtI[i], submitted[i]);
                }
                this.setBody(getMaxBody());
                this.setKi(getMaxKi());
                this.setStamina(getMaxStamina());
            }
        }
    }

    /**
     *  0 for strength, 1 dex, 2 constitution, 3 willpower, 4 mind, 5 spirit
     * @param statid statID to multiply
     * @param multi value to multi by
     */
    @Override
    public void multiplyAttribute(int statid, double multi) {
        if (multi == 0) multi = 1.0;

        int[] stats = getAllAttributes();

        int newstat = (int) Math.floor((double) stats[statid] * multi);
        nbt.setInteger(JRMCoreH.AttrbtNbtI[statid], newstat);

        switch (statid) {
            case 2:
                this.setBody(getMaxBody());
                this.setStamina(getMaxStamina());
                break;
            case 5:
                this.setKi(getMaxKi());
                break;
        }
    }

    /**
     *
     * @param multi multiplies all attributes by multi
     */
    @Override
    public void multiplyAllAttributes(double multi) {
        int[] stats = getAllAttributes();
        if (multi == 0) {
            multi = 1.0;
        }
        int[] newstats = new int[stats.length];
        for (int i = 0; i < stats.length; i++) {
            newstats[i] = (int) Math.floor((double) stats[i] * multi);
            nbt.setInteger(JRMCoreH.AttrbtNbtI[i], newstats[i]);
        }
        this.setBody(getMaxBody());
        this.setKi(getMaxKi());
        this.setStamina(getMaxStamina());
    }

    /**
     * A "Full" stat is a stat that has all form multipliers calculated. i.e if base Strength is 10,000, returns 10,000.
     *  if SSJ form multi is 20x and is SSJ, returns 200,000. LSSJ returns 350,000, LSSJ Kaioken x40 returns 1,000,000 and so on
     *  0 for strength, 1 dex, 2 constitution, 3 willpower, 4 mind, 5 spirit
     * @param statid ID of Stat
     * @return stat value
     */
    @Override
    public int getFullStat(int statid) {
        JGPlayerMP JG = new JGPlayerMP(player);
        NBTTagCompound nbt = player.getEntityData().getCompoundTag("PlayerPersisted");
        JG.setNBT(nbt);
        return JG.getAttribute(statid);
    }

    /**
     * @return Player's race name
     */
    @Override
    public String getRaceName() {
        if (this.getRace() >= 0 && this.getRace() <= 5) {
            return JRMCoreH.Races[this.getRace()];
        }
        return null;
    }

    /**
     * @return Name of form player is currently in
     */
    @Override
    public String getCurrentFormName() {
        int race = this.getRace();
        int form = (int) this.getForm();
        return DBCAPI.Instance().getFormName(race, form);
    }

    /**
     * @param formName name of form to change mastery of
     * @param amount   sets the current mastery value to amount
     * @param add      adds the amount to current mastery, instead of setting it to it
     */
    @Override
    public void changeFormMastery(String formName, double amount, boolean add) {
        JGPlayerMP JG = new JGPlayerMP(player);
        NBTTagCompound nbt = player.getEntityData().getCompoundTag("PlayerPersisted");
        JG.setNBT(nbt);
        int race = (int) JG.getRace();

        boolean found = false;
        boolean isKaiokenOn = false;
        boolean isMysticOn = false;
        boolean isUltraInstinctOn = false;
        boolean isGoDOn = false;
        int state = 0;
        int state2 = 0;
        double amountKK = 0;

        switch (formName.toLowerCase()) {
            case "kaioken":
                found = true;
                isKaiokenOn = true;
                amountKK = amount;
                break;
            case "mystic":
                found = true;
                isMysticOn = true;

            case "ultrainstict":
                found = true;
                isUltraInstinctOn = true;
                break;
            case "godofdestruction":
                found = true;
                isGoDOn = true;
                break;

        }
        if (found) {
            String nonracial = nbt.getString("jrmcFormMasteryNonRacial");
            String masteries[] = nonracial.split(";");
            int foundatindex;
            String newnonracial = "";
            for (int i = 0; i < masteries.length; i++) {
                if (masteries[i].toLowerCase().contains(formName.toLowerCase())) {
                    foundatindex = i;
                    String[] masteryvalues = masteries[i].split(",");
                    double masteryvalue = Double.parseDouble(masteryvalues[1]);
                    if (add) {
                        masteryvalue += amount;
                    } else {
                        masteryvalue = amount;
                    }
                    String newvalue = Double.toString(masteryvalue);
                    switch (foundatindex) {
                        case 0:
                            newnonracial = masteryvalues[0] + "," + newvalue + ";" + masteries[1] + ";" + masteries[2] + ";" + masteries[3];
                            break;
                        case 1:
                            newnonracial = masteries[0] + ";" + masteryvalues[0] + "," + newvalue + ";" + masteries[2] + ";" + masteries[3];
                            break;
                        case 2:
                            newnonracial = masteries[0] + ";" + masteries[1] + ";" + masteryvalues[0] + "," + newvalue + ";" + masteries[3];
                            ;
                            break;
                        case 3:
                            newnonracial = masteries[0] + ";" + masteries[1] + ";" + masteries[2] + ";" + masteryvalues[0] + "," + newvalue;
                            break;
                    }

                    nbt.setString("jrmcFormMasteryNonRacial", newnonracial);
                    break;
                }
            }
        } else {
            for (int i = 0; i < JRMCoreH.trans[race].length; i++) {
                if (JRMCoreH.trans[race][i].equalsIgnoreCase(formName)) {
                    state = i;
                    found = true;
                    JRMCoreH.changeFormMasteriesValue(player, amount, amountKK, add, race, state, state2, isKaiokenOn, isMysticOn, isUltraInstinctOn, isGoDOn, -1);

                }
            }
        }

        if (!found) {
            throw new CustomNPCsException("Invalid \nform name. For non racial form names, use Kaioken, Mystic, UltraInstict and GodOfDestruction. For racial \nform names, check getFormName(int race, int form) or getCurrentFormName()", new Object[2]);
        }
    }

    /**
     * @param formName name of form
     * @return Form mastery value
     */
    @Override
    public double getFormMasteryValue(String formName) {

        JGPlayerMP JG = new JGPlayerMP(player);
        NBTTagCompound nbt = player.getEntityData().getCompoundTag("PlayerPersisted");
        JG.setNBT(nbt);
        int race = (int) JG.getRace();
        boolean found = false;
        double valuetoreturn = -1.0;

        switch (formName.toLowerCase()) {
            case "kaioken":
                found = true;
                break;
            case "mystic":
                found = true;
            case "ultrainstict":
                found = true;
                break;
            case "godofdestruction":
                found = true;
                break;

        }
        if (found) {
            String nonracial = nbt.getString("jrmcFormMasteryNonRacial");
            String masteries[] = nonracial.split(";");
            for (String mastery : masteries) {
                if (mastery.toLowerCase().contains(formName.toLowerCase())) {
                    String[] masteryvalues = mastery.split(",");
                    valuetoreturn = Double.parseDouble(masteryvalues[1]);
                    return valuetoreturn;
                }
            }
        } else {
            String racial = nbt.getString("jrmcFormMasteryRacial_" + JRMCoreH.Races[race]);
            String masteries[] = racial.split(";");
            for (String mastery : masteries) {
                if (mastery.toLowerCase().contains(formName.toLowerCase())) {
                    String[] masteryvalues = mastery.split(",");
                    double masteryvalue = Double.parseDouble(masteryvalues[1]);
                    found = true;

                    valuetoreturn = masteryvalue;
                    return valuetoreturn;
                }
            }
        }
        if (!found) {
            throw new CustomNPCsException("Invalid \nform name. For non racial form names, use Kaioken, Mystic, UltraInstict and GodOfDestruction. For racial \nform names, check getFormName(int race, int form) or getCurrentFormName()", new Object[2]);
        }
        throw new CustomNPCsException("Form Mastery value is -1.0", new Object[3]);
    }

    /**
     * @return Entire form mastery NBT string, aka data32
     */
    @Override
    public String getAllFormMasteries() {
        return JRMCoreH.getFormMasteryData(player);
    }

    /**
     * @return True if player is fused and spectator
     */
    @Override
    public boolean isDBCFusionSpectator() {
        return JRMCoreH.isFusionSpectator(player);
    }

    /**
     * @param skillname Check JRMCoreH.DBCSkillNames
     * @return skill level from 1 to 10
     */
    @Override
    public int getSkillLevel(String skillname) {
        int i;
        String[] playerskills = nbt.getString("jrmcSSlts").split(",");
        String[] skillids = JRMCoreH.DBCSkillsIDs;
        String[] skillnames = JRMCoreH.DBCSkillNames;
        boolean skillFound = false;
        boolean playerHasSkill = false;
        for (i = 0; i < skillnames.length; i++) {
            if (skillname.equals(skillnames[i])) {
                skillFound = true;
                for (String playerskill : playerskills) {
                    if (playerskill.contains(skillids[i])) {
                        return JRMCoreH.SklLvl(i, playerskills);
                    }
                }
            }
        }
        if (!skillFound) {
            throw new CustomNPCsException("\nInvalid Skill ID :" + skillname + ". Please re-enter the skill name \nwithout any spaces in between. \ni.e: GodOfDestruction, KiProtection, \nDefensePenetration", new Object[0]);
        }
        if (!playerHasSkill) {
            throw new CustomNPCsException("\nPlayer doesn't have skill " + skillname + "!", new Object[1]);
        }
        return 0;
    }

    /**
     * @param attribute 0 for Melee Dmg, 1 for Defense, 3 for Ki Power
     * @return Player's stat, NOT attributes i.e Melee Dmg, not STR
     */
    @Override
    public int getMaxStat(int attribute) {
        return DBCUtils.getMaxStat(player, attribute);
    }

    /**
     * @param attribute check getMaxStat
     * @return Player's stat as a percentage of MaxStat through power release i.e if MaxStat is 1000 and release is 10, returns 100
     */
    @Override
    public int getCurrentStat(int attribute) {
        return (int) (getMaxStat(attribute) * this.getRelease() * 100F);
    }

    /**
     * @return if Form player is in is 10x base, returns 10
     */
    @Override
    public double getCurrentFormMultiplier() {
        double str = this.getStat("str");
        double maxstr = getFullStat(0);

        return (Math.max(maxstr, str)) / str;
    }

    /**
     * @return True if player is transformed into white MUI
     */
    @Override
    public boolean isMUI() {
        return DBCUtils.isMUI(player);
    }

    /**
     * @return True if player is currently KO
     */
    @Override
    public boolean isKO(){
        int currentKO = getInt(player, "jrmcHar4va");
        return currentKO > 0;
    }
}