package kamkeel.npcdbc.data;


import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.server.config.dbc.JGConfigRaces;
import JinRyuu.JRMCore.server.config.dbc.JGConfigUltraInstinct;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.ClientCache;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.PingPacket;
import kamkeel.npcdbc.util.DBCUtils;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.config.ConfigClient;
import noppes.npcs.util.CacheHashMap;
import noppes.npcs.util.ValueUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static JinRyuu.JRMCore.JRMCoreH.getMajinAbsorptionValueS;
import static JinRyuu.JRMCore.JRMCoreH.nbt;

public class DBCData {

    public static final CacheHashMap<String, CacheHashMap.CachedObject<DBCData>> dbcDataCache = new CacheHashMap<>((long) ConfigClient.CacheLife * 60 * 1000);

    public static String DBCPersisted = "PlayerPersisted";
    public final Side side;
    public EntityPlayer player;

    // Original DBC
    public int STR, DEX, CON, WIL, MND, SPI, TP, Body, Ki, Stamina, KOforXSeconds, Rage, Heat, AuraColor;
    public byte Class, Race, Powertype, State, State2, Release;
    public boolean Alive, isKO;
    public String Skills = "", RacialSkills = "", StatusEffects = "", Settings = "", FormMasteryRacial = "", FormMasteryNR = "", DNS = "", DNSHair = "";

    // Custom Form
    public int addonFormID, auraID;
    public float addonFormLevel;
    public HashMap<Integer, StatusEffect> activeEffects = new HashMap<>();

    public DBCData() {
        this.side = Side.SERVER;
    }

    public DBCData(EntityPlayer player) {
        this.player = player;
        this.side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;

        if (side == Side.SERVER)
            loadNBTData(true);
    }

    public static DBCData getData(EntityPlayer player) {
        synchronized (dbcDataCache) {
            if (!dbcDataCache.containsKey(player.getCommandSenderName()))
                dbcDataCache.put(player.getCommandSenderName(), new CacheHashMap.CachedObject<>(new DBCData(player)));
            return dbcDataCache.get(player.getCommandSenderName()).getObject();
        }
    }

    public static DBCData get(EntityPlayer player) {
        DBCData data;
        if (player.worldObj.isRemote) {
            data = ClientCache.getClientData(player);
        } else {
            data = getData(player);
            data.loadNBTData(false);
        }
        data.player = player;
        return data;
    }

    @SideOnly(Side.CLIENT)
    public static DBCData getClient() {
        return get(Minecraft.getMinecraft().thePlayer);
    }

    public static Form getFormClient(EntityPlayer player) {
        if (player == null)
            return null;

        DBCData dbcData = get(player);
        if (dbcData == null)//(dbcData.Release <= 0 || dbcData.Ki <= 0)
            return null;

        int form = dbcData.addonFormID;
        if (form == -1)
            return null;


        return (Form) FormController.getInstance().get(form);
    }

    public static Aura getAuraClient(EntityPlayer player) {
        if (player == null)
            return null;

        DBCData dbcData = get(player);
        if (dbcData == null)//(dbcData.Release <= 0 || dbcData.Ki <= 0)
            return null;

        int form = dbcData.auraID;
        if (form == -1)
            return null;


        return (Aura) AuraController.getInstance().get(form);
    }

    @SideOnly(Side.CLIENT)
    public static float getFormLevelClient(AbstractClientPlayer player) {
        DBCData dbcData = get(player);
        if (dbcData == null)
            return 0f;

        return dbcData.addonFormLevel;
    }

    public static Form getCurrentForm(EntityPlayer p) {
        if (Utility.isServer(p))
            return PlayerDataUtil.getDBCInfo(p) != null ? PlayerDataUtil.getDBCInfo(p).getCurrentForm() : null;
        else
            return getFormClient(p);
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

        // DBC Addon
        addonFormID = c.getInteger("addonFormID");
        addonFormLevel = c.getFloat("addonFormLevel");
        auraID = c.getInteger("auraID");

        if (c.hasKey("addonActiveEffects", 9))
        {
            NBTTagList nbttaglist = c.getTagList("addonActiveEffects", 10);
            for (int i = 0; i < nbttaglist.tagCount(); ++i)
            {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
                StatusEffect statusEffect = StatusEffect.readEffectData(nbttagcompound1);
                if (statusEffect != null)
                {
                    this.activeEffects.put(statusEffect.getId(), statusEffect);
                }
            }
        }
    }

    public void saveNBTData(boolean syncALL) {
        NBTTagCompound nbt = this.saveFromNBT(this.player.getEntityData().getCompoundTag(DBCPersisted));

        PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo(player);
        addonFormID = formData.currentForm;
        addonFormLevel = formData.getCurrentLevel();
        auraID = formData.currentAura;
        activeEffects = StatusEffectController.Instance.activeEffects.get(Utility.getUUID(player));
        nbt.setInteger("addonFormID", addonFormID);
        nbt.setInteger("auraID", auraID);
        nbt.setFloat("addonFormLevel", addonFormLevel);
        saveEffects(nbt);
        this.player.getEntityData().setTag(DBCPersisted, nbt);

        // Send to Tracking Only
        if (syncALL)
            syncAllClients();

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
            syncAllClients();
    }

    public void syncAllClients() {
        PacketHandler.Instance.sendToTrackingPlayers(new PingPacket(this).generatePacket(), player);

    }

    public NBTTagCompound getRawCompound() {
        return this.player.getEntityData().getCompoundTag(DBCPersisted);
    }

    public HashMap<Integer, StatusEffect> getActiveEffects() {
        return StatusEffectController.Instance.activeEffects.get(Utility.getUUID(player));
    }

    public void saveEffects(NBTTagCompound nbt){
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.activeEffects.values().iterator();
        while (iterator.hasNext())
        {
            StatusEffect statusEffect = (StatusEffect)iterator.next();
            nbttaglist.appendTag(statusEffect.writeEffectData(new NBTTagCompound()));
        }
        nbt.setTag("addonActiveEffects", nbttaglist);
    }

    public void setActiveEffects(HashMap<Integer, StatusEffect> activeEffects) {
        NBTTagCompound raw = getRawCompound();
        this.activeEffects = activeEffects;
        saveEffects(raw);
    }

    public void decrementActiveEffects() {
        HashMap<Integer, StatusEffect> currentEffects = getActiveEffects();
        Iterator<Map.Entry<Integer, StatusEffect>> iterator = currentEffects.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Integer, StatusEffect> entry = iterator.next();
            StatusEffect currentEffect = entry.getValue();

            if (currentEffect.duration == -1)
                continue;

            currentEffect.duration--;

            if (currentEffect.duration == 0)
                iterator.remove(); // Remove the current entry using iterator
        }
        setActiveEffects(currentEffects);
    }

    public int[] getAllFullAttributes() {
        boolean x = player.worldObj.isRemote;
        String skillX = JRMCoreH.getString(player, "jrmcSSltX");
        int reserve = x ? JRMCoreH.getArcRsrv() : JRMCoreH.getInt(player, "jrmcArcRsrv");
        String absorption = x ? JRMCoreH.getMajinAbsorption() : JRMCoreH.getString(player, "jrmcMajinAbsorptionData");
        int[] PlyrAttrbts = JRMCoreH.PlyrAttrbts(player);
        String[] PlyrSkills = JRMCoreH.PlyrSkills(player);
        boolean mj = JRMCoreH.StusEfcts(12, StatusEffects);
        boolean c = (JRMCoreH.StusEfcts(10, StatusEffects) || JRMCoreH.StusEfcts(11, StatusEffects));
        boolean lg = JRMCoreH.StusEfcts(14, StatusEffects);
        boolean kk = JRMCoreH.StusEfcts(5, StatusEffects);
        boolean mc = JRMCoreH.StusEfcts(13, StatusEffects);
        boolean mn = JRMCoreH.StusEfcts(19, StatusEffects);
        boolean gd = JRMCoreH.StusEfcts(20, StatusEffects);
        int[] a = new int[6];
        for (int i = 0; i <= 5; i++)
            a[i] = JRMCoreH.getPlayerAttribute(player, PlyrAttrbts, i, State, State2, Race, skillX, (int) Release, reserve, lg, mj, kk, mc, mn, gd, Powertype, PlyrSkills, c, absorption);

        return a;
    }

    public int getExtraOutput(int att, int release) {
        int extraoutput = 0;
        if (att == 0) {
            int maxki = getMaxStat(5);
            extraoutput = (int) (JRMCoreH.SklLvl(12, player) * 0.0025 * maxki * release * 0.01);
        } else if (att == 1) {
            int maxki = getMaxStat(5);
            extraoutput = (int) (JRMCoreH.SklLvl(11, player) * 0.005 * maxki * release * 0.01);
        } else if (att == 5) {
            extraoutput = getMaxStat(5) - JRMCoreH.stat(player, att, Powertype, att, JRMCoreH.PlyrAttrbts(player)[5], Race, Class, 0);
        }
        return extraoutput;
    }

    public int getMaxStat(int att) { // gets max player stat, 0 dmg 1 def only, rest are

        int[] PlyrAttrbts = JRMCoreH.PlyrAttrbts(player);
        int[] PlyrAttrbtsFull = getAllFullAttributes();

        for (int i = 0; i < PlyrAttrbts.length; i++) {
            if (i == 0 || i == 1 || i == 4)
                PlyrAttrbts[i] = PlyrAttrbtsFull[i];
        }

        float f = att == 5 ? JRMCoreH.SklLvl_KiBs(player, 1) : 0f;
        int stat = JRMCoreH.stat(player, att, Powertype, att, PlyrAttrbts[att], Race, Class, f);

        if (att == 0)
            stat += getExtraOutput(att, 100);
        else if (att == 1)
            stat += getExtraOutput(att, 100);

        return stat;
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

    public float getCurrentBodyPercentage() {
        return (Body * 100) / (float) getMaxBody();

    }

    // Negative Values will Drain instead
    public void restoreKiPercent(float percToRestore) {
        int maxKi = DBCUtils.getMaxKi(player);
        int toAdd = (int) (maxKi * (percToRestore / 100));

        Ki = ValueUtil.clamp(Ki + toAdd, 0, maxKi);
        getRawCompound().setInteger("jrmcEnrgy", Ki);
    }

    public void restoreHealthPercent(float percToRestore) {
        int maxBody = DBCUtils.getMaxBody(player);
        int toAdd = (int) (maxBody * (percToRestore / 100));

        Body = ValueUtil.clamp(Body + toAdd, 0, maxBody);
        getRawCompound().setInteger("jrmcBdy", Body);
    }

    public void restoreStaminaPercent(float percToRestore) {
        int maxSta = DBCUtils.getMaxStamina(player);
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

        int reserve = nbt(player).getInteger("jrmcArcRsrv");
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
        return Utility.isServer() ? JRMCoreH.PlyrSettingsB(player, id) : JRMCoreH.PlyrSettingsB(id);
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

    public Aura getCurrentAura() {
        DBCData dbcData = DBCData.get(player);
        Form form = (Form) FormController.getInstance().get(dbcData.addonFormID);

        if (form != null && form.display.hasAura())
            return form.display.getAur();
        else if (auraID > -1)
            return (Aura) AuraController.Instance.get(auraID);

        return null;
    }


}
