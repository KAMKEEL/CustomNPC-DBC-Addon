package kamkeel.npcdbc.scripted;

import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.server.JGPlayerMP;
import JinRyuu.JRMCore.server.config.dbc.JGConfigDBCFormMastery;
import kamkeel.npcdbc.data.DBCUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.scripted.CustomNPCsException;
import noppes.npcs.scripted.entity.ScriptDBCPlayer;
import noppes.npcs.scripted.entity.ScriptPlayer;

import java.util.ArrayList;

// Implemented by Kam, Ported from Goatee Design
public class ScriptDBCAddon<T extends EntityPlayerMP> extends ScriptDBCPlayer<T> {
    public T player;
    public NBTTagCompound nbt;

    public ScriptDBCAddon(T player) {
        super(player);
        this.player = player;
        this.nbt = player.getEntityData().getCompoundTag("PlayerPersisted");
    }

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

    public void setFlight(boolean bo) {
        if (this.getRelease() > 0) {
            nbt.setBoolean("isFlying", bo);
        }
    }

    public int getMaxBody() {
        return DBCUtils.getMaxStat(player, 2);
    }

    public int getMaxHP() {
        return DBCUtils.getMaxStat(player, 2);
    }

    public float getBodyPerc() {
        return DBCUtils.bodyPerc(player);
    }

    public int getMaxKi() {
        int[] PlyrAttrbts = JRMCoreH.PlyrAttrbts(player);
        JGPlayerMP JG = new JGPlayerMP(player);
        JG.setNBT(nbt);
        byte pwr = JRMCoreH.getByte(player, "jrmcPwrtyp");
        byte rce = JRMCoreH.getByte(player, "jrmcRace");
        byte cls = JRMCoreH.getByte(player, "jrmcClass");
        return JG.getEnergyMax(rce, cls, pwr, PlyrAttrbts, JRMCoreH.SklLvl_KiBs(player, pwr));
    }

    public int getMaxStamina() {
        int[] PlyrAttrbts = JRMCoreH.PlyrAttrbts(player);
        JGPlayerMP JG = new JGPlayerMP(player);
        JG.setNBT(nbt);
        byte pwr = JRMCoreH.getByte(player, "jrmcPwrtyp");
        byte rce = JRMCoreH.getByte(player, "jrmcRace");
        byte cls = JRMCoreH.getByte(player, "jrmcClass");

        return JG.getStaminaMax(rce, cls, pwr, PlyrAttrbts);
    }

    public void changeDBCAnim(int i) {
        if (i == 1 || i == 2) {
            JRMCoreH.Anim(i);
        } else {
            throw new CustomNPCsException("Invalid Animation ID : " + i + "\nValid IDs are: 1 for Flight \nand 2 for Standing", new Object[0]);
        }
    }

    public int[] getAllStats() {
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

    public void addAllStats(int[] Stats, boolean multiplyaddedStats, double multiValue) {
        if (Stats.length == 6) {
            int[] stats = getAllStats();
            double multi = multiValue;
            if (multiValue == 0 || !multiplyaddedStats) {
                multi = 1.0;
            }

            int[] newstats = new int[stats.length];
            for (int i = 0; i < stats.length; i++) {
                newstats[i] = (int) Math.floor((double) (stats[i] + Stats[i]) * multi);
                nbt.setInteger(JRMCoreH.AttrbtNbtI[i], newstats[i]);

            }
            this.setBody(getMaxBody());
            this.setKi(getMaxKi());
            this.setStamina(getMaxStamina());
        }
    }

    public void addAllStats(int Num, boolean setStatsToNum) {

        int[] num = new int[] { Num, Num, Num, Num, Num, Num };
        if (!setStatsToNum) {
            addAllStats(num, false, 1);
        } else {
            for (int i = 0; i < num.length; i++) {
                nbt.setInteger(JRMCoreH.AttrbtNbtI[i], num[i]);
            }
            this.setBody(getMaxBody());
            this.setKi(getMaxKi());
            this.setStamina(getMaxStamina());
        }
    }

    public void addAllStats(int[] stats, boolean setStats) {
        if (stats.length == 6) {
            if (!setStats) {
                addAllStats(stats, false, 1);
            } else {
                for (int i = 0; i < stats.length; i++) {
                    nbt.setInteger(JRMCoreH.AttrbtNbtI[i], stats[i]);
                }
                this.setBody(getMaxBody());
                this.setKi(getMaxKi());
                this.setStamina(getMaxStamina());
            }
        }
    }

    // 0 for strength, 1 dex, 2 constitution, 3 willpower, 4 mind, 5 spirit
    public void multiplyStat(int statid, double multi) {
        if (multi == 0)
            multi = 1.0;

        int[] stats = getAllStats();

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

    public void multiplyAllStats(double multi) {
        int[] stats = getAllStats();
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

    // str 0, dex 1, con 2, wil 3, mnd 4, spi 5
    public int getFullStat(int statid) {
        JGPlayerMP JG = new JGPlayerMP(player);
        NBTTagCompound nbt = player.getEntityData().getCompoundTag("PlayerPersisted");
        JG.setNBT(nbt);
        return JG.getAttribute(statid);
    }

    public String getRaceName(int race) {
        if (race >= 0 && race <= 5) {
            return JRMCoreH.Races[race];
        }
        return null;
    }

    public String getRaceName() {
        if (this.getRace() >= 0 && this.getRace() <= 5) {
            return JRMCoreH.Races[this.getRace()];
        }
        return null;
    }

    public String getFormName(int race, int form) {
        CustomNPCsException c = new CustomNPCsException("Invalid \nform ID for race " + JRMCoreH.Races[race], new Object[0]);
        CustomNPCsException r = new CustomNPCsException("Invalid Race : \nValid Races are \n0 Human, 1 Saiyan\n 2 Half-Saiyan, 3 Namekian\n4 Arcosian, 5 Majin", new Object[1]);
        if (form >= 0) {
            if (race > 5 || race < 0) {
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

    public String getCurrentFormName() {
        int race = this.getRace();
        int form = (int) this.getForm();
        return getFormName(race, form);
    }

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
        }
        else {
            for (int i = 0; i < JRMCoreH.trans[race].length; i++) {
                if (JRMCoreH.trans[race][i].equalsIgnoreCase(formName)) {
                    state = i;
                    found = true;
                    JRMCoreH.changeFormMasteriesValue(player, amount, amountKK, add, race, state, state2, isKaiokenOn, isMysticOn, isUltraInstinctOn, isGoDOn, -1);

                }
            }
        }

        if (!found) {
            throw new CustomNPCsException(
                "Invalid \nform name. For non racial form names, use Kaioken, Mystic, UltraInstict and GodOfDestruction. For racial \nform names, check getFormName(int race, int form) or getCurrentFormName()",
                new Object[2]);
        }
    }

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
        }
        else {
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
            throw new CustomNPCsException(
                "Invalid \nform name. For non racial form names, use Kaioken, Mystic, UltraInstict and GodOfDestruction. For racial \nform names, check getFormName(int race, int form) or getCurrentFormName()",
                new Object[2]);
        }
        throw new CustomNPCsException("Form Mastery value is -1.0", new Object[3]);
    }

    public String getAllFormMasteries() {
        return JRMCoreH.getFormMasteryData(player);
    }

    public void addFusionFormMasteries(ScriptPlayer<T> Controller, ScriptPlayer<T> Spectator, boolean multiplyaddedStats, double multiValue, boolean addForBoth) {
        double multi = multiValue;
        if (multiValue == 0 || !multiplyaddedStats) {
            multi = 1.0;
        }

        T controller = Controller.player;
        NBTTagCompound cnbt = controller.getEntityData().getCompoundTag("PlayerPersisted");
        int crace = Controller.getDBCPlayer().getRace();
        String cracial = cnbt.getString("jrmcFormMasteryRacial_" + JRMCoreH.Races[crace]);
        String cnonracial = cnbt.getString("jrmcFormMasteryNonRacial");

        T spectator = Spectator.player;
        NBTTagCompound snbt = spectator.getEntityData().getCompoundTag("PlayerPersisted");
        int srace = Spectator.getDBCPlayer().getRace();
        String sracial = snbt.getString("jrmcFormMasteryRacial_" + JRMCoreH.Races[srace]);
        String snonracial = snbt.getString("jrmcFormMasteryNonRacial");

        boolean samerace = false;
        if (crace == srace) {
            samerace = true;
        }
        String[] cnonracialmasteries = cnonracial.split(";");
        String[] snonracialmasteries = snonracial.split(";");
        int lengthnr = cnonracialmasteries.length;
        int slengthnr = snonracialmasteries.length;

        String[] cracialmasteries = cracial.split(";");
        String[] sracialmasteries = sracial.split(";");
        int lengthr = cracialmasteries.length;
        int slengthr = sracialmasteries.length;

        String newmasteriesnr = "";
        String newmasteriesr = "";
        String snewmasteriesnr = "";
        String snewmasteriesr = "";

        boolean done = false;
        if (samerace) {
            for (int i = 0; i < lengthnr; i++) {
                String[] cmasteryvaluesnr = cnonracialmasteries[i].split(",");
                String[] smasteryvaluesnr = snonracialmasteries[i].split(",");

                // name , string( double(value1)+double(value2) ) ;
                newmasteriesnr += cmasteryvaluesnr[0] + "," + Double.toString((Double.parseDouble(cmasteryvaluesnr[1]) + Double.parseDouble(smasteryvaluesnr[1])) * multi) + ";";

            }
            for (int i = 0; i < lengthr; i++) {
                String[] cmasteryvaluesr = cracialmasteries[i].split(",");
                String[] smasteryvaluesr = sracialmasteries[i].split(",");

                newmasteriesr += cmasteryvaluesr[0] + "," + Double.toString((Double.parseDouble(cmasteryvaluesr[1]) + Double.parseDouble(smasteryvaluesr[1])) * multi) + ";";
                done = true;
            }

        } else {
            if (multiValue == 0) {
                multiValue = 2.0;
            }
            multi = multiValue;
            for (int i = 0; i < lengthnr; i++) {
                String[] cmasteryvaluesnr = cnonracialmasteries[i].split(",");
                newmasteriesnr += cmasteryvaluesnr[0] + "," + Double.toString(Double.parseDouble(cmasteryvaluesnr[1]) * multi) + ";";

            }
            for (int i = 0; i < lengthr; i++) {
                String[] cmasteryvaluesr = cracialmasteries[i].split(",");
                newmasteriesr += cmasteryvaluesr[0] + "," + Double.toString(Double.parseDouble(cmasteryvaluesr[1]) * multi) + ";";
                done = true;

            }

            for (int i = 0; i < slengthnr; i++) {
                String[] smasteryvaluesnr = snonracialmasteries[i].split(",");
                snewmasteriesnr += smasteryvaluesnr[0] + "," + Double.toString(Double.parseDouble(smasteryvaluesnr[1]) * multi) + ";";
            }
            for (int i = 0; i < slengthr; i++) {
                String[] smasteryvaluesr = snonracialmasteries[i].split(",");
                snewmasteriesr += smasteryvaluesr[0] + "," + Double.toString(Double.parseDouble(smasteryvaluesr[1]) * multi) + ";";
                done = true;
            }

        }

        newmasteriesnr.substring(0, lengthnr - 2);
        cnbt.setString("jrmcFormMasteryNonRacial", newmasteriesnr);

        newmasteriesr.substring(0, lengthr - 2);
        cnbt.setString("jrmcFormMasteryRacial_" + JRMCoreH.Races[crace], newmasteriesr);
        if (addForBoth) {
            if (samerace) {
                snbt.setString("jrmcFormMasteryNonRacial", newmasteriesnr);
                snbt.setString("jrmcFormMasteryRacial_" + JRMCoreH.Races[crace], newmasteriesr);
            } else {
                snewmasteriesnr.substring(0, snonracialmasteries.length - 2);
                snbt.setString("jrmcFormMasteryNonRacial", snewmasteriesnr);
                snewmasteriesr.substring(0, sracialmasteries.length - 2);
                snbt.setString("jrmcFormMasteryRacial_" + JRMCoreH.Races[srace], snewmasteriesr);
            }
        }

        if (!done) {
            throw new CustomNPCsException("Invalid arguments", new Object[0]);
        }
    }

    public String[] getAllFormMasteryData(int race, int formId) {
        ArrayList<String> data = new ArrayList<>();
        data.add(JGConfigDBCFormMastery.getString(race, formId, JGConfigDBCFormMastery.DATA_ID_MAX_LEVEL, 0));
        data.add(JGConfigDBCFormMastery.getString(race, formId, JGConfigDBCFormMastery.DATA_ID_INSTANT_TRANSFORM_UNLOCK, 0));
        data.add(JGConfigDBCFormMastery.getString(race, formId, JGConfigDBCFormMastery.DATA_ID_REQUIRED_MASTERIES, 0));
        data.add(JGConfigDBCFormMastery.getString(race, formId, JGConfigDBCFormMastery.DATA_ID_AUTO_LEARN_ON_LEVEL, 0));
        data.add(JGConfigDBCFormMastery.getString(race, formId, JGConfigDBCFormMastery.DATA_ID_GAIN_TO_OTHER_MASTERIES, 0));

        return data.toArray(new String[0]);
    }

    public int getAllFormsLength(int race, boolean nonRacial) {
        if (race < 0 || race > 5) {
            throw new CustomNPCsException("Races are from 0 to 5", new Object[0]);
        }
        if (nonRacial) {
            return JRMCoreH.transNonRacial.length;
        }
        return JRMCoreH.trans[race].length;
    }

    public String[] getAllForms(int race, boolean nonRacial) {
        if (race < 0 || race > 5) {
            throw new CustomNPCsException("Races are from 0 to 5", new Object[0]);
        }
        if (nonRacial) {
            return JRMCoreH.transNonRacial;

        }
        return JRMCoreH.trans[race];
    }

    public boolean isDBCFusionSpectator() {
        return JRMCoreH.isFusionSpectator(player);
    }


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
            throw new CustomNPCsException(
                "\nInvalid Skill ID :" + skillname + ". Please re-enter the skill name \nwithout any spaces in between. \ni.e: GodOfDestruction, KiProtection, \nDefensePenetration",
                new Object[0]);
        }
        if (!playerHasSkill) {
            throw new CustomNPCsException("\nPlayer doesn't have skill " + skillname + "!", new Object[1]);
        }
        return 0;
    }

    public int getMaxStat(int attribute) {
        return DBCUtils.getMaxStat(player, attribute);
    }

    public int getCurrentStat(int attribute) {
        return (int) (getMaxStat(attribute) * this.getRelease() * 100F);
    }

    public double getCurrentFormMultiplier() {
        double str = this.getStat("str");
        double maxstr = getFullStat(0);

        return (Math.max(maxstr, str)) / str;
    }

    public boolean isMUI() {
        return DBCUtils.isMUI(player);
    }
}
