package kamkeel.npcdbc.data;


import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.i.ExtendedPlayer;
import JinRyuu.JRMCore.server.config.dbc.JGConfigRaces;
import JinRyuu.JRMCore.server.config.dbc.JGConfigUltraInstinct;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.statuseffect.PlayerEffect;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.PingPacket;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.util.ValueUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static JinRyuu.JRMCore.JRMCoreH.getMajinAbsorptionValueS;
import static JinRyuu.JRMCore.JRMCoreH.nbt;

public class DBCData extends DBCDataUniversal {

    public static String DBCPersisted = "PlayerPersisted";
    public final Side side;
    public EntityPlayer player;

    // Original DBC
    public int STR, DEX, CON, WIL, MND, SPI, TP, Body, Ki, Stamina, KOforXSeconds, Rage, Heat, AuraColor, ArcReserve;
    public byte Class, Race, Powertype, State, State2, Release;
    public boolean Alive, isKO;
    public String Skills = "", RacialSkills = "", StatusEffects = "", Settings = "", FormMasteryRacial = "", FormMasteryNR = "", DNS = "", DNSHair = "", MajinAbsorptionData = "";

    // Custom Form
    public int addonFormID, auraID;
    public float addonFormLevel;
    public HashMap<Integer, PlayerEffect> activeEffects = new HashMap<>();

    public DBCData() {
        this.side = Side.SERVER;
    }

    public DBCData(EntityPlayer player) {
        this.player = player;
        this.side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;

        if (side == Side.SERVER)
            loadNBTData(true);
    }


    public NBTTagCompound saveFromNBT(NBTTagCompound comp) {
        comp.setInteger("jrmcStrI", STR);
        comp.setInteger("jrmcDexI", DEX);
        comp.setInteger("jrmcCnsI", CON);
        comp.setInteger("jrmcWilI", WIL);
        comp.setInteger("jrmcIntI", MND);
        comp.setInteger("jrmcCncI", SPI);
        comp.setInteger("jrmcEnrgy", Ki);
        comp.setInteger("jrmcStamina", Stamina);
        comp.setInteger("jrmcBdy", Body);
        comp.setInteger("jrmcHar4va", KOforXSeconds);
        comp.setInteger("jrmcSaiRg", Rage);
        comp.setInteger("jrmcEf8slc", Heat);
        comp.setInteger("jrmcAuraColor", AuraColor);
        comp.setInteger("jrmcArcRsrv", ArcReserve);

        comp.setByte("jrmcState", State);
        comp.setByte("jrmcState2", State2);
        comp.setByte("jrmcRelease", Release);
        comp.setByte("jrmcPwrtyp", Powertype);
        comp.setByte("jrmcRace", Race);

        comp.setString("jrmcStatusEff", StatusEffects);
        comp.setString("jrmcSSltX", RacialSkills);
        comp.setString("jrmcSSlts", Skills);
        comp.setString("jrmcSettings", Settings);
        comp.setString("jrmcFormMasteryRacial_" + JRMCoreH.Races[Race], FormMasteryRacial);
        comp.setString("jrmcFormMasteryNonRacial", FormMasteryNR);
        comp.setString("jrmcDNS", DNS);
        comp.setString("jrmcDNSH", DNSHair);
        comp.setString("jrmcMajinAbsorptionData", MajinAbsorptionData);
        // DBC Addon
        comp.setInteger("addonFormID", addonFormID);
        comp.setInteger("auraID", auraID);
        comp.setFloat("addonFormLevel", addonFormLevel);
        saveEffects(comp);
        return comp;
    }

    public void loadFromNBT(NBTTagCompound c) {
        STR = c.getInteger("jrmcStrI");
        DEX = c.getInteger("jrmcDexI");
        CON = c.getInteger("jrmcCnsI");
        WIL = c.getInteger("jrmcWilI");
        MND = c.getInteger("jrmcIntI");
        SPI = c.getInteger("jrmcCncI");
        Ki = c.getInteger("jrmcEnrgy");
        Stamina = c.getInteger("jrmcStamina");
        Body = c.getInteger("jrmcBdy");
        KOforXSeconds = c.getInteger("jrmcHar4va");
        Rage = c.getInteger("jrmcSaiRg");
        Heat = c.getInteger("jrmcEf8slc");
        isKO = c.getInteger("jrmcHar4va") > 0;
        AuraColor = c.getInteger("jrmcAuraColor");
        ArcReserve = c.getInteger("jrmcArcRsrv");

        State = c.getByte("jrmcState");
        State2 = c.getByte("jrmcState2");
        Release = c.getByte("jrmcRelease");
        Powertype = c.getByte("jrmcPwrtyp");
        Race = c.getByte("jrmcRace");
        Class = c.getByte("jrmcClass");

        StatusEffects = c.getString("jrmcStatusEff");
        RacialSkills = c.getString("jrmcSSltX");
        Skills = c.getString("jrmcSSlts");
        Settings = c.getString("jrmcSettings");
        FormMasteryRacial = c.getString("jrmcFormMasteryRacial_" + JRMCoreH.Races[Race]);
        FormMasteryNR = c.getString("jrmcFormMasteryNonRacial");
        DNS = c.getString("jrmcDNS");
        DNSHair = c.getString("jrmcDNSH");
        MajinAbsorptionData = c.getString("jrmcMajinAbsorptionData");

        // DBC Addon
        addonFormID = c.getInteger("addonFormID");
        addonFormLevel = c.getFloat("addonFormLevel");
        auraID = c.getInteger("auraID");

        if (c.hasKey("addonActiveEffects", 9)) {
            NBTTagList nbttaglist = c.getTagList("addonActiveEffects", 10);
            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
                PlayerEffect playerEffect = PlayerEffect.readEffectData(nbttagcompound1);
                if (playerEffect != null) {
                    this.activeEffects.put(playerEffect.id, playerEffect);
                }
            }
        }
    }

    public void saveNBTData(boolean syncTracking) {
        NBTTagCompound nbt = this.saveFromNBT(this.player.getEntityData().getCompoundTag(DBCPersisted));

        PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo(player);
        addonFormID = formData.currentForm;
        addonFormLevel = formData.getCurrentLevel();
        auraID = formData.currentAura;
        activeEffects = StatusEffectController.Instance.playerEffects.get(Utility.getUUID(player));
        nbt.setInteger("addonFormID", addonFormID);
        nbt.setFloat("addonFormLevel", addonFormLevel);
        nbt.setInteger("auraID", auraID);
        saveEffects(nbt);
        this.player.getEntityData().setTag(DBCPersisted, nbt);

        // Send to Tracking Only
        if (syncTracking)
            syncTracking();
    }

    public void loadNBTData(boolean syncALL) {
        NBTTagCompound dbc = this.player.getEntityData().getCompoundTag(DBCPersisted);

        // Save the DBC Addon tags to PlayerPersisted before loading it to fields
        PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo(player);
        dbc.setInteger("addonFormID", formData.currentForm);
        dbc.setInteger("auraID", formData.currentAura);
        dbc.setFloat("addonFormLevel", formData.getCurrentLevel());

        loadFromNBT(dbc);
        if (syncALL)
            syncTracking();
    }

    public void syncTracking() {
        PacketHandler.Instance.sendToTrackingPlayers(new PingPacket(this).generatePacket(), player);

    }

    public NBTTagCompound getRawCompound() {
        return this.player.getEntityData().getCompoundTag(DBCPersisted);
    }

    public HashMap<Integer, PlayerEffect> getActiveEffects() {
        return StatusEffectController.Instance.playerEffects.get(Utility.getUUID(player));
    }

    public void setActiveEffects(HashMap<Integer, PlayerEffect> activeEffects) {
        NBTTagCompound raw = getRawCompound();
        this.activeEffects = activeEffects;
        saveEffects(raw);
    }

    public void saveEffects(NBTTagCompound nbt) {
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.activeEffects.values().iterator();
        while (iterator.hasNext()) {
            PlayerEffect playerEffect = (PlayerEffect) iterator.next();
            nbttaglist.appendTag(playerEffect.writeEffectData(new NBTTagCompound()));
        }
        nbt.setTag("addonActiveEffects", nbttaglist);
    }

    public void decrementActiveEffects() {
        HashMap<Integer, PlayerEffect> currentEffects = getActiveEffects();
        Iterator<Map.Entry<Integer, PlayerEffect>> iterator = currentEffects.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, PlayerEffect> entry = iterator.next();
            PlayerEffect currentEffect = entry.getValue();
            if(currentEffect == null) {
                iterator.remove();
                continue;
            }

            if (currentEffect.duration == -100)
                continue;
            else if (currentEffect.duration <= 0) {
                StatusEffect parent = StatusEffectController.Instance.get(currentEffect.id);
                if(parent != null)
                    parent.runout(player, currentEffect);
                iterator.remove();
            } else
                currentEffect.duration--;
        }
        setActiveEffects(currentEffects);
    }

    public int[] getAllAttributes() {
        return new int[]{STR, DEX, CON, WIL, MND, SPI};
    }

    public int getFullAttribute(int attri) {
        boolean majin = JRMCoreH.StusEfcts(12, StatusEffects);
        boolean fusion = (JRMCoreH.StusEfcts(10, StatusEffects) || JRMCoreH.StusEfcts(11, StatusEffects));
        boolean legendary = JRMCoreH.StusEfcts(14, StatusEffects);
        boolean kaioken = JRMCoreH.StusEfcts(5, StatusEffects);
        boolean mystic = JRMCoreH.StusEfcts(13, StatusEffects);
        boolean ui = JRMCoreH.StusEfcts(19, StatusEffects);
        boolean GoD = JRMCoreH.StusEfcts(20, StatusEffects);

        return JRMCoreH.getPlayerAttribute(player, getAllAttributes(), attri, State, State2, Race, RacialSkills, (int) Release, ArcReserve, legendary, majin, kaioken, mystic, ui, GoD, Powertype, Skills.split(","), fusion, MajinAbsorptionData);

    }

    public int[] getAllFullAttributes() {
        boolean majin = JRMCoreH.StusEfcts(12, StatusEffects);
        boolean fusion = (JRMCoreH.StusEfcts(10, StatusEffects) || JRMCoreH.StusEfcts(11, StatusEffects));
        boolean legendary = JRMCoreH.StusEfcts(14, StatusEffects);
        boolean kaioken = JRMCoreH.StusEfcts(5, StatusEffects);
        boolean mystic = JRMCoreH.StusEfcts(13, StatusEffects);
        boolean ui = JRMCoreH.StusEfcts(19, StatusEffects);
        boolean GoD = JRMCoreH.StusEfcts(20, StatusEffects);
        int[] a = new int[6];
        for (int i = 0; i <= 5; i++)
            a[i] = JRMCoreH.getPlayerAttribute(player, getAllAttributes(), i, State, State2, Race, RacialSkills, Release, ArcReserve, legendary, majin, kaioken, mystic, ui, GoD, Powertype, Skills.split(","), fusion, MajinAbsorptionData);

        return a;
    }

    public int getExtraOutput(int att, int release) {
        int extraoutput = 0;

        if (att == 0) {
            int maxki = getMaxStat(5);
            extraoutput = (int) (JRMCoreH.SklLvl(12, Skills.split(",")) * 0.0025 * maxki * release * 0.01);
        } else if (att == 1) {
            int maxki = getMaxStat(5);
            extraoutput = (int) (JRMCoreH.SklLvl(11, Skills.split(",")) * 0.005 * maxki * release * 0.01);
        } else if (att == 5)
            extraoutput = getMaxStat(5) - JRMCoreH.stat(player, att, Powertype, att, SPI, Race, Class, JRMCoreH.SklLvl_KiBs(Skills.split(","), 1));

        return extraoutput;
    }

    public int getMaxStat(int attributeID) { // gets max player stat, 0 dmg 1 def only, rest are
        int attribute = 0;

        if (attributeID == 0 || attributeID == 1 || attributeID == 4)
            attribute = getFullAttribute(attributeID);
        else
            attribute = getAllAttributes()[attributeID];

        float f = attributeID == 5 ? JRMCoreH.SklLvl_KiBs(Skills.split(","), 1) : 0f;
        int stat = JRMCoreH.stat(player, attributeID, Powertype, attributeID, attribute, Race, Class, f);

        if (attributeID == 0)
            stat += getExtraOutput(attributeID, 100);
        else if (attributeID == 1)
            stat += getExtraOutput(attributeID, 100);

        return stat;
    }

    public int getCurrentStat(int attribute) { // gets stat at current release
        return (int) (getMaxStat(attribute) * Release * 0.01D * JRMCoreH.weightPerc(0, player));
    }

    public void setEyeColorLeft(int color) {
        int i = 42;
        if (DNS == null || DNS.length() < i)
            return;

        String hexCol = JRMCoreH.numToLet5(color);
        DNS = DNS.substring(0, i) + hexCol + DNS.substring(i + 5);
        getRawCompound().setString("jrmcDNS", DNS);
    }

    public void setEyeColorRight(int color) {
        int i = 47;
        if (DNS == null || DNS.length() < i)
            return;

        String hexCol = JRMCoreH.numToLet5(color);
        DNS = DNS.substring(0, i) + hexCol + DNS.substring(i + 5);
        getRawCompound().setString("jrmcDNS", DNS);
    }

    public void setHairColor(int color) {
        int i = 7;
        if (DNS == null || DNS.length() < i)
            return;

        String hexCol = JRMCoreH.numToLet5(color);
        DNS = DNS.substring(0, i) + hexCol + DNS.substring(i + 5);
        getRawCompound().setString("jrmcDNS", DNS);
    }

    //main color for humans/saiyans/majins
    public void setBodyColorMain(int color) {
        int i = 16;
        if (DNS == null || DNS.length() < i)
            return;

        String hexCol = JRMCoreH.numToLet5(color);
        DNS = DNS.substring(0, i) + hexCol + DNS.substring(i + 5);
        getRawCompound().setString("jrmcDNS", DNS);
    }

    //saiyan oozaru and arco/nameks
    public void setBodyColor1(int color) {
        int i = 21;
        if (DNS == null || DNS.length() < i)
            return;

        String hexCol = JRMCoreH.numToLet5(color);
        DNS = DNS.substring(0, i) + hexCol + DNS.substring(i + 5);
        getRawCompound().setString("jrmcDNS", DNS);
    }

    // namekian/arco
    public void setBodyColor2(int color) {
        int i = 26;
        if (DNS == null || DNS.length() < i)
            return;
        String hexCol = JRMCoreH.numToLet5(color);
        DNS = DNS.substring(0, i) + hexCol + DNS.substring(i + 5);
        getRawCompound().setString("jrmcDNS", DNS);
    }

    //only arco
    public void setBodyColor3(int color) {
        int i = 31;
        if (DNS == null || DNS.length() < i)
            return;
        String hexCol = JRMCoreH.numToLet5(color);
        DNS = DNS.substring(0, i) + hexCol + DNS.substring(i + 5);
        getRawCompound().setString("jrmcDNS", DNS);
    }

    public void setHairPreset(int preset) {
        int i = 3;
        if (DNS == null || DNS.length() < i)
            return;

        DNS = DNS.substring(0, i) + JRMCoreH.numToLet(preset) + DNS.substring(i + 2);
        getRawCompound().setString("jrmcDNS", DNS);
    }

    public void setNosePreset(int preset) {
        int i = 36;
        if (DNS == null || DNS.length() < i)
            return;

        DNS = DNS.substring(0, i) + JRMCoreH.numToLet(preset) + DNS.substring(i + 2);
        getRawCompound().setString("jrmcDNS", DNS);
    }

    public int getMaxBody() {
        return JRMCoreH.stat(player, 2, Powertype, 2, CON, Race, Class, 0);
    }

    public int getMaxStamina() {
        return JRMCoreH.stat(player, 2, Powertype, 3, CON, Race, Class, 0);
    }

    public int getMaxKi() {
        return JRMCoreH.stat(player, 5, Powertype, 5, CON, Race, Class, 0);
    }

    public float getCurrentBodyPercentage() {
        return (Body * 100) / (float) getMaxBody();

    }

    // Negative Values will Drain instead
    public void restoreKiPercent(float percToRestore) {
        int maxKi = getMaxKi();
        int toAdd = (int) (maxKi * (percToRestore / 100));

        Ki = ValueUtil.clamp(Ki + toAdd, 0, maxKi);
        getRawCompound().setInteger("jrmcEnrgy", Ki);
    }

    public void restoreHealthPercent(float percToRestore) {
        int maxBody = getMaxBody();
        int toAdd = (int) (maxBody * (percToRestore / 100));

        Body = ValueUtil.clamp(Body + toAdd, 0, maxBody);
        getRawCompound().setInteger("jrmcBdy", Body);
    }

    public void restoreStaminaPercent(float percToRestore) {
        int maxSta = getMaxStamina();
        int toAdd = (int) (maxSta * (percToRestore / 100));


        Stamina = ValueUtil.clamp(Stamina + toAdd, 0, maxSta);
        getRawCompound().setInteger("jrmcStamina", Stamina);
    }

    //Negative value will add instead
    public void restoreUIHeat(float percToRestore) {
        if (!isForm(DBCForm.UltraInstinct))
            return;

        int maxHeat = JGConfigUltraInstinct.CONFIG_UI_HEAT_DURATION[State2];
        int toAdd = (int) (maxHeat * (percToRestore / 100));

        Heat = ValueUtil.clamp(Heat - toAdd, 0, maxHeat);
        getRawCompound().setInteger("jrmcEf8slc", Heat);
    }

    public void setArcReserve(int reserve) {
        if (Race != 4)
            return;
        nbt(player).setInteger("jrmcArcRsrv", reserve);
    }

    public void restoreArcPP(int percToRestoreFromMax) {
        if (Race != 4)
            return;

        int maxReserve = JRMCoreConfig.ArcosianPPMax[getMaxSkillX()];
        int toAdd = maxReserve * (percToRestoreFromMax / 100);

        // Arc Reserve can be less than 0, so Add from 0.
        int reserve = Math.max(nbt(player).getInteger("jrmcArcRsrv"), 0);
        reserve = ValueUtil.clamp(reserve + toAdd, 0, maxReserve);
        setArcReserve(reserve);
    }

    public void setAbsorption(int amount) {
        if (Race != DBCRace.MAJIN)
            return;

        nbt(player).setString("jrmcMajinAbsorptionData", amount + ",0,0+0");
    }

    public void restoreAbsorption(int percToRestoreFromMax) {
        if (Race != DBCRace.MAJIN)
            return;
        int maxAbsorption = JGConfigRaces.CONFIG_MAJIN_ABSORPTON_MAX_LEVEL;
        int toAdd = (int) (maxAbsorption * (percToRestoreFromMax / 100f));

        int currentAbsorption = getMajinAbsorptionValueS(nbt(player).getString("jrmcMajinAbsorptionData"));
        setAbsorption(ValueUtil.clamp(toAdd + currentAbsorption, 0, maxAbsorption));
    }

    public int getMaxSkillX() {
        String racial = nbt(player).getString("jrmcSSltX");
        if (racial == null || racial.isEmpty() || racial.contains("pty"))
            return 0;
        return Integer.parseInt(racial.substring(2));
    }

    public boolean isForm(int dbcForm) {
        switch (dbcForm) {
            case DBCForm.Kaioken:
                return State2 > 0 && JRMCoreH.StusEfcts(5, StatusEffects);
            case DBCForm.UltraInstinct:
                return State2 > 0 && JRMCoreH.StusEfcts(19, StatusEffects);
            case DBCForm.GodOfDestruction:
                return JRMCoreH.StusEfcts(20, StatusEffects);
            case DBCForm.Mystic:
                return JRMCoreH.StusEfcts(13, StatusEffects);
            //the following doesn't count as "forms" but they can be checked from this method as well
            case DBCForm.Legendary:
                return JRMCoreH.StusEfcts(14, StatusEffects);
            case DBCForm.Divine:
                return JRMCoreH.StusEfcts(17, StatusEffects);
            case DBCForm.Majin:
                return JRMCoreH.StusEfcts(12, StatusEffects);
            default:
                return false;
        }
    }

    public boolean isDivine() {
        return JRMCoreH.StusEfctsMe(17);
    }

    public boolean isLegendary() {
        return JRMCoreH.StusEfctsMe(14);
    }

    public boolean containsSE(int id) {
        return JRMCoreH.StusEfcts(id, StatusEffects);
    }

    public void setSE(int id, boolean bo) {
        JRMCoreH.StusEfcts(id, StatusEffects, player, bo);
    }

    public void setForm(int dbcForm, boolean on) {
        switch (dbcForm) {
            case DBCForm.Kaioken:
                setSE(5, on);
                if (on)
                    State2 = 1;
                break;
            case DBCForm.UltraInstinct:
                setSE(19, on);
                if (on)
                    State2 = 1;
                break;
            case DBCForm.GodOfDestruction:
                setSE(20, on);
                break;
            case DBCForm.Mystic:
                setSE(13, on);
                break;
            case DBCForm.Legendary:
                setSE(14, on);
                break;
            case DBCForm.Divine:
                setSE(17, on);
                break;
            case DBCForm.Majin:
                setSE(12, on);
                break;
        }
    }

    public boolean settingOn(int id) {
        return Utility.isServer(player) ? JRMCoreH.PlyrSettingsB(player, id) : JRMCoreH.PlyrSettingsB(id);
    }

    public boolean formSettingOn(int dbcForm) {
        switch (dbcForm) {
            case DBCForm.Kaioken:
                return settingOn(0);
            case DBCForm.UltraInstinct:
                return settingOn(11);
            case DBCForm.GodOfDestruction:
                return settingOn(16);
            case DBCForm.Mystic:
                return settingOn(6);
            default:
                return false;
        }
    }

    public int getJRMCPlayerID() {
        for (int pl = 0; pl < JRMCoreH.plyrs.length; pl++)
            if (JRMCoreH.plyrs[pl].equals(player.getCommandSenderName()))
                return pl;
        return 0;
    }

    public double getCurrentMulti() {
        return getFullAttribute(0) / STR;

    }

    public String getJRMCData(int id) {
        for (int pl = 0; pl < JRMCoreH.plyrs.length; pl++) {
            if (JRMCoreH.plyrs[pl].equals(player.getCommandSenderName())) {
                return JRMCoreH.data(id)[pl];
            }
        }
        return "";

    }

    public boolean isChargingKiAttack() {
        ExtendedPlayer jrmcExtendedPlayer = ExtendedPlayer.get(player);

        //Abusing JRMCore's animation system to see if a player is charging a ki attack.
        boolean kiAnimationTypeSelected = jrmcExtendedPlayer.getAnimKiShoot() != 0;
        boolean shouldAttemptAnimation = jrmcExtendedPlayer.getAnimKiShootOn() != 0;

        return kiAnimationTypeSelected && shouldAttemptAnimation;
    }

    public Aura getAura() {
        if (player == null)
            return null;
        DBCData dbcData = DBCData.get(player);
        Form form = (Form) FormController.getInstance().get(dbcData.addonFormID);

        if (form != null && form.display.hasAura())
            return form.display.getAur();
        else if (auraID > -1)
            return (Aura) AuraController.Instance.get(auraID);

        return null;
    }

    public Form getForm() {
        if (player == null)
            return null;

        if (addonFormID == -1)
            return null;

        return (Form) FormController.getInstance().get(addonFormID);
    }

    public void applyNamekianRegen(){
        if(player == null)
            return;

        if(Body < ConfigDBCGameplay.NamekianRegenMin){
            if(!StatusEffectController.getInstance().hasEffect(player, Effects.NAMEK_REGEN)){
                StatusEffectController.getInstance().applyEffect(player, new PlayerEffect(Effects.NAMEK_REGEN, -100, (byte) 1));
            }
        }
    }
}
