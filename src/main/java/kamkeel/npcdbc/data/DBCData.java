package kamkeel.npcdbc.data;


import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.server.config.dbc.JGConfigUltraInstinct;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.PingPacket;
import kamkeel.npcdbc.util.DBCUtils;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import noppes.npcs.config.ConfigClient;
import noppes.npcs.util.CacheHashMap;

public class DBCData implements IExtendedEntityProperties {

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
    public String preCustomFormDNS = "", preCustomFormDNSHair = "";
    public int preCustomAuraColor;
    public int currentCustomForm;
    public float currentCustomFormLevel;

    public DBCData() {
        this.side = Side.SERVER;
    }

    public DBCData(EntityPlayer player) {
        this.player = player;
        this.side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;
    }

    public static DBCData get(EntityPlayer player) {
        synchronized (dbcDataCache) {
            if (!dbcDataCache.containsKey(player.getCommandSenderName())) {
                dbcDataCache.put(player.getCommandSenderName(),  new CacheHashMap.CachedObject<>(new DBCData(player)));
            }
            return dbcDataCache.get(player.getCommandSenderName()).getObject();
        }
    }

    @Override
    public void init(Entity entity, World world) {
        loadNBTData(null);
    }

    public NBTTagCompound saveNBT(NBTTagCompound c){
        c.setInteger("jrmcStrI", STR);
        c.setInteger("jrmcDexI", DEX);
        c.setInteger("jrmcConI", CON);
        c.setInteger("jrmcWilI", WIL);
        c.setInteger("jrmcIntI", MND);
        c.setInteger("jrmcCncI", SPI);
        c.setInteger("jrmcEnrgy", Ki);
        c.setInteger("jrmcStamina", Stamina);
        c.setInteger("jrmcBdy", Body);
        c.setInteger("jrmcHar4va", KOforXSeconds);
        c.setInteger("jrmcSaiRg", Rage);
        c.setInteger("jrmcEf8slc", Heat);
        c.setInteger("jrmcAuraColor", AuraColor);
        c.setByte("jrmcState", State);
        c.setByte("jrmcState2", State2);
        c.setByte("jrmcRelease", Release);
        c.setByte("jrmcPwrtyp", Powertype);
        c.setByte("jrmcRace", Race);
        c.setString("jrmcStatusEff", StatusEffects);
        c.setString("jrmcSSltX", RacialSkills);
        c.setString("jrmcSSlts", Skills);
        c.setString("jrmcSettings", Settings);
        c.setString("jrmcFormMasteryRacial_" + JRMCoreH.Races[Race], FormMasteryRacial);
        c.setString("jrmcFormMasteryNonRacial", FormMasteryNR);
        c.setString("jrmcDNS", DNS);
        c.setString("jrmcDNSH", DNSHair);

        // DBC Addon
        c.setInteger("customFormID", currentCustomForm);
        c.setFloat("customFormLevel", currentCustomFormLevel);
        c.setInteger("preCustomAuraColor", preCustomAuraColor);
        c.setString("preCustomFormDNSH", preCustomFormDNSHair);
        c.setString("preCustomFormDNS", preCustomFormDNS);
        return c;
    }

    public void setNBT(NBTTagCompound c){
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
        currentCustomForm =  c.getInteger("customFormID");
        currentCustomFormLevel = c.getInteger("customFormLevel");
        preCustomAuraColor = c.getInteger("preCustomAuraColor");
        preCustomFormDNS = c.getString("preCustomFormDNS");
        preCustomFormDNSHair = c.getString("preCustomFormDNSH");
    }

    @Override
    public void saveNBTData(NBTTagCompound compound){
        NBTTagCompound nbt = this.saveNBT(this.player.getEntityData().getCompoundTag(DBCPersisted));

        PlayerCustomFormData formData = Utility.getFormData(player);
        currentCustomForm = formData.currentForm;
        currentCustomFormLevel = formData.getCurrentLevel();
        nbt.setInteger("customFormID", currentCustomForm);
        nbt.setFloat("customFormLevel", currentCustomFormLevel);
        this.player.getEntityData().setTag(DBCPersisted, nbt);

        syncAllClients();
    }

    @Override
    public void loadNBTData(NBTTagCompound compound){
        NBTTagCompound dbc = this.player.getEntityData().getCompoundTag(DBCPersisted);
        setNBT(dbc);

        // DBC Addon
        PlayerCustomFormData formData = Utility.getFormData(player);
        currentCustomForm = formData.currentForm;
        currentCustomFormLevel = formData.getCurrentLevel();
        preCustomAuraColor = dbc.getInteger("preCustomAuraColor");
        preCustomFormDNS = dbc.getString("preCustomFormDNS");
        preCustomFormDNSHair = dbc.getString("preCustomFormDNSH");
    }

    public void syncAllClients() {
        PacketHandler.Instance.sendToAll(new PingPacket(this).generatePacket());
    }

    public NBTTagCompound getRawCompound() {
        return this.player.getEntityData().getCompoundTag(DBCPersisted);
    }

    public void setEyeColorLeft(int color) {
        int i = 42;
        if (DNS == null || DNS.length() < i)
            return;

        String hexCol = JRMCoreH.numToLet5(color);
        DNS = DNS.substring(0, i) + hexCol + DNS.substring(i + 5);
        getRawCompound().setString("jrmcDNS", DNS);
        saveNBTData(null);
    }

    public void setEyeColorRight(int color) {
        int i = 47;
        if (DNS == null || DNS.length() < i)
            return;

        String hexCol = JRMCoreH.numToLet5(color);
        DNS = DNS.substring(0, i) + hexCol + DNS.substring(i + 5);
        getRawCompound().setString("jrmcDNS", DNS);
        saveNBTData(null);
    }

    public void setHairColor(int color) {
        int i = 7;
        if (DNS == null || DNS.length() < i)
            return;

        String hexCol = JRMCoreH.numToLet5(color);
        DNS = DNS.substring(0, i) + hexCol + DNS.substring(i + 5);
        getRawCompound().setString("jrmcDNS", DNS);
        saveNBTData(null);
    }

    //main color for humans/saiyans/majins
    public void setBodyColorMain(int color) {
        int i = 16;
        if (DNS == null || DNS.length() < i)
            return;

        String hexCol = JRMCoreH.numToLet5(color);
        DNS = DNS.substring(0, i) + hexCol + DNS.substring(i + 5);
        getRawCompound().setString("jrmcDNS", DNS);
        saveNBTData(null);
    }

    //saiyan oozaru and arco/nameks
    public void setBodyColor1(int color) {
        int i = 21;
        if (DNS == null || DNS.length() < i)
            return;

        String hexCol = JRMCoreH.numToLet5(color);
        DNS = DNS.substring(0, i) + hexCol + DNS.substring(i + 5);
        getRawCompound().setString("jrmcDNS", DNS);
        saveNBTData(null);
    }

    // namekian/arco
    public void setBodyColor2(int color) {
        int i = 26;
        if (DNS == null || DNS.length() < i)
            return;
        String hexCol = JRMCoreH.numToLet5(color);
        DNS = DNS.substring(0, i) + hexCol + DNS.substring(i + 5);
        getRawCompound().setString("jrmcDNS", DNS);
        saveNBTData(null);
    }

    //only arco
    public void setBodyColor3(int color) {
        int i = 31;
        if (DNS == null || DNS.length() < i)
            return;
        String hexCol = JRMCoreH.numToLet5(color);
        DNS = DNS.substring(0, i) + hexCol + DNS.substring(i + 5);
        getRawCompound().setString("jrmcDNS", DNS);
        saveNBTData(null);
    }

    public void setHairPreset(int preset) {
        int i = 3;
        if (DNS == null || DNS.length() < i)
            return;

        DNS = DNS.substring(0, i) + JRMCoreH.numToLet(preset) + DNS.substring(i + 2);
        getRawCompound().setString("jrmcDNS", DNS);
        saveNBTData(null);
    }

    public void setNosePreset(int preset) {
        int i = 36;
        if (DNS == null || DNS.length() < i)
            return;

        DNS = DNS.substring(0, i) + JRMCoreH.numToLet(preset) + DNS.substring(i + 2);
        getRawCompound().setString("jrmcDNS", DNS);
        saveNBTData(null);
    }

    // Negative Values will Drain instead
    public void restoreKiPercent(float percToRestore) {
        int maxKi = DBCUtils.getMaxKi(player);
        int toAdd = (int) (maxKi * (percToRestore / 100));

        Ki += toAdd;
        Ki = Math.min(Ki, maxKi);
        saveNBTData(null);
    }

    public void restoreHealthPercent(float percToRestore) {
        int maxBody = DBCUtils.getMaxBody(player);
        int toAdd = (int) (maxBody * (percToRestore / 100));

        Body += toAdd;
        Body = Math.min(Body, maxBody);
        saveNBTData(null);
    }

    public void restoreStaminaPercent(float percToRestore) {
        int maxSta = DBCUtils.getMaxStamina(player);
        int toAdd = (int) (maxSta * (percToRestore / 100));

        Stamina += toAdd;
        Stamina = Math.min(Stamina, maxSta);
        saveNBTData(null);
    }

    //Negative value will add instead
    public void restoreUIHeat(float percToRestore) {
        if (!isForm(DBCForm.UltraInstinct))
            return;

        int maxHeat = JGConfigUltraInstinct.CONFIG_UI_HEAT_DURATION[State2];
        int toAdd = (int) (maxHeat * (percToRestore / 100));

        Heat = Math.max(Heat - toAdd, 0);
        saveNBTData(null);

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
        saveNBTData(null);
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
        saveNBTData(null);
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
}
