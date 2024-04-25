package kamkeel.npcdbc.data;


import JinRyuu.DragonBC.common.Items.ItemSenzu;
import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.server.config.dbc.JGConfigUltraInstinct;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.ClientCache;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.PingPacket;
import kamkeel.npcdbc.util.DBCUtils;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NBTTags;
import noppes.npcs.config.ConfigClient;
import noppes.npcs.util.CacheHashMap;

import java.util.HashMap;

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
    public HashMap<Integer, Integer> activeEffects = new HashMap<>();

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

    public NBTTagCompound saveFromNBT(NBTTagCompound comp) {
        comp.setInteger("jrmcStrI", STR);
        comp.setInteger("jrmcDexI", DEX);
        comp.setInteger("jrmcConI", CON);
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
        comp.setTag("addonActiveEffects", NBTTags.nbtIntegerIntegerMap(activeEffects));
        return comp;
    }

    public void loadFromNBT(NBTTagCompound c) {
        STR = c.getInteger("jrmcStrI");
        DEX = c.getInteger("jrmcDexI");
        CON = c.getInteger("jrmcConI");
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
        activeEffects = NBTTags.getIntegerIntegerMap(c.getTagList("addonActiveEffects", 10));
    }

    public void saveNBTData(boolean syncALL) {
        NBTTagCompound nbt = this.saveFromNBT(this.player.getEntityData().getCompoundTag(DBCPersisted));

        PlayerDBCInfo formData = Utility.getData(player);
        addonFormID = formData.currentForm;
        addonFormLevel = formData.getCurrentLevel();
        auraID = formData.currentAura;
        activeEffects = StatusEffectController.Instance.activeEffects.get(Utility.getUUID(player));
        nbt.setInteger("addonFormID", addonFormID);
        nbt.setInteger("auraID", auraID);
        nbt.setFloat("addonFormLevel", addonFormLevel);
        nbt.setTag("addonActiveEffects", NBTTags.nbtIntegerIntegerMap(activeEffects));
        this.player.getEntityData().setTag(DBCPersisted, nbt);

        // Send to Tracking Only
        if (syncALL)
            syncAllClients();

    }

    public void loadNBTData(boolean syncALL) {
        NBTTagCompound dbc = this.player.getEntityData().getCompoundTag(DBCPersisted);

        // Save the DBC Addon tags to PlayerPersisted before loading it to fields
        PlayerDBCInfo formData = Utility.getData(player);
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

    public void setActiveEffects(HashMap<Integer, Integer> activeEffects) {
        NBTTagCompound raw = getRawCompound();
        this.activeEffects = activeEffects;
        raw.setTag("addonActiveEffects", NBTTags.nbtIntegerIntegerMap(activeEffects));
    }

    public void setEyeColorLeft(int color) {
        int i = 42;
        if (DNS == null || DNS.length() < i)
            return;

        String hexCol = JRMCoreH.numToLet5(color);
        DNS = DNS.substring(0, i) + hexCol + DNS.substring(i + 5);
        getRawCompound().setString("jrmcDNS", DNS);
        saveNBTData(true);
    }

    public void setEyeColorRight(int color) {
        int i = 47;
        if (DNS == null || DNS.length() < i)
            return;

        String hexCol = JRMCoreH.numToLet5(color);
        DNS = DNS.substring(0, i) + hexCol + DNS.substring(i + 5);
        getRawCompound().setString("jrmcDNS", DNS);
        saveNBTData(true);
    }

    public void setHairColor(int color) {
        int i = 7;
        if (DNS == null || DNS.length() < i)
            return;

        String hexCol = JRMCoreH.numToLet5(color);
        DNS = DNS.substring(0, i) + hexCol + DNS.substring(i + 5);
        getRawCompound().setString("jrmcDNS", DNS);
        saveNBTData(true);
    }

    //main color for humans/saiyans/majins
    public void setBodyColorMain(int color) {
        int i = 16;
        if (DNS == null || DNS.length() < i)
            return;

        String hexCol = JRMCoreH.numToLet5(color);
        DNS = DNS.substring(0, i) + hexCol + DNS.substring(i + 5);
        getRawCompound().setString("jrmcDNS", DNS);
        saveNBTData(true);
    }

    //saiyan oozaru and arco/nameks
    public void setBodyColor1(int color) {
        int i = 21;
        if (DNS == null || DNS.length() < i)
            return;

        String hexCol = JRMCoreH.numToLet5(color);
        DNS = DNS.substring(0, i) + hexCol + DNS.substring(i + 5);
        getRawCompound().setString("jrmcDNS", DNS);
        saveNBTData(true);
    }

    // namekian/arco
    public void setBodyColor2(int color) {
        int i = 26;
        if (DNS == null || DNS.length() < i)
            return;
        String hexCol = JRMCoreH.numToLet5(color);
        DNS = DNS.substring(0, i) + hexCol + DNS.substring(i + 5);
        getRawCompound().setString("jrmcDNS", DNS);
        saveNBTData(true);
    }

    //only arco
    public void setBodyColor3(int color) {
        int i = 31;
        if (DNS == null || DNS.length() < i)
            return;
        String hexCol = JRMCoreH.numToLet5(color);
        DNS = DNS.substring(0, i) + hexCol + DNS.substring(i + 5);
        getRawCompound().setString("jrmcDNS", DNS);
        saveNBTData(true);
    }

    public void setHairPreset(int preset) {
        int i = 3;
        if (DNS == null || DNS.length() < i)
            return;

        DNS = DNS.substring(0, i) + JRMCoreH.numToLet(preset) + DNS.substring(i + 2);
        getRawCompound().setString("jrmcDNS", DNS);
        saveNBTData(true);
    }

    public void setNosePreset(int preset) {
        int i = 36;
        if (DNS == null || DNS.length() < i)
            return;

        DNS = DNS.substring(0, i) + JRMCoreH.numToLet(preset) + DNS.substring(i + 2);
        getRawCompound().setString("jrmcDNS", DNS);
        saveNBTData(true);
    }

    // Negative Values will Drain instead
    public void restoreKiPercent(float percToRestore) {
        int maxKi = DBCUtils.getMaxKi(player);
        int toAdd = (int) (maxKi * (percToRestore / 100));

        Ki += toAdd;
        Ki = Math.min(Ki, maxKi);
        saveNBTData(true);
    }

    public void restoreHealthPercent(float percToRestore) {
        int maxBody = DBCUtils.getMaxBody(player);
        int toAdd = (int) (maxBody * (percToRestore / 100));

        Body += toAdd;
        Body = Math.min(Body, maxBody);
        saveNBTData(true);
    }

    public void restoreStaminaPercent(float percToRestore) {
        int maxSta = DBCUtils.getMaxStamina(player);
        int toAdd = (int) (maxSta * (percToRestore / 100));

        Stamina += toAdd;
        Stamina = Math.min(Stamina, maxSta);
        saveNBTData(true);
    }

    //Negative value will add instead
    public void restoreUIHeat(float percToRestore) {
        if (!isForm(DBCForm.UltraInstinct))
            return;

        int maxHeat = JGConfigUltraInstinct.CONFIG_UI_HEAT_DURATION[State2];
        int toAdd = (int) (maxHeat * (percToRestore / 100));

        Heat = Math.max(Heat - toAdd, 0);
        saveNBTData(true);
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
        reserve = Math.min(reserve + toAdd, maxReserve);
        setArcReserve(reserve);
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
        return JRMCoreH.StusEfctsMe(17);
    }

    public boolean containsSE(int id) {
        return JRMCoreH.StusEfcts(id, StatusEffects);
    }

    public void setSE(int id, boolean bo) {
        JRMCoreH.StusEfcts(id, StatusEffects, player, bo);
        saveNBTData(true);
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
        saveNBTData(true);
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
