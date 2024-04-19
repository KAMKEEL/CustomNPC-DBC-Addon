package kamkeel.npcdbc.data.SyncedData;


import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.server.config.dbc.JGConfigUltraInstinct;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.data.PlayerCustomFormData;
import kamkeel.npcdbc.util.DBCUtils;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 * DBCData is basically all relevant DBC/JRMC tags in the PlayerPersisted compound tag. This get synced from server to client, so client always has access
 * to the latest version of the fields below
 * <p>
 * Example usage:
 * DBCData.get(p).setInt("jrmcStrI",100)
 * Sets the jrmcStrI int tag of entity p's PlayerPersisted compound to 100<
 */
public class DBCData extends PerfectSync<DBCData> {

    public static String DBCPersisted = "PlayerPersisted";

    public int STR, DEX, CON, WIL, MND, SPI, TP, Body, Ki, Stamina, KOforXTicks, Rage, Heat, AuraColor, preCustomAuraColor;
    public byte Class, Race, Powertype, State, State2, Release;
    public boolean Alive, isKO;
    public String Skills = "", RacialSkills = "", StatusEffects = "", Settings = "", FormMasteryRacial = "", FormMasteryNR = "", DNS = "", DNSHair = "", preCustomFormDNS = "", preCustomFormDNSHair = "";

    public int currentCustomForm;
    public float currentCustomFormLevel;

    public DBCData(Entity player) {
        super(player);
        this.DATA_NAME = DBCPersisted;
        this.player = (EntityPlayer) player;
    }

    public void setEyeColorLeft(int color) {
        int i = 42;
        if (DNS == null || DNS.length() < i)
            return;

        String hexCol = JRMCoreH.numToLet5(color);
        DNS = DNS.substring(0, i) + hexCol + DNS.substring(i + 5);
    }

    public void setEyeColorRight(int color) {
        int i = 47;
        if (DNS == null || DNS.length() < i)
            return;

        String hexCol = JRMCoreH.numToLet5(color);
        DNS = DNS.substring(0, i) + hexCol + DNS.substring(i + 5);
    }

    public void setHairColor(int color) {
        int i = 7;
        if (DNS == null || DNS.length() < i)
            return;

        String hexCol = JRMCoreH.numToLet5(color);
        DNS = DNS.substring(0, i) + hexCol + DNS.substring(i + 5);
    }

    //main color for humans/saiyans/majins
    public void setBodyColorMain(int color) {
        int i = 16;
        if (DNS == null || DNS.length() < i)
            return;

        String hexCol = JRMCoreH.numToLet5(color);
        DNS = DNS.substring(0, i) + hexCol + DNS.substring(i + 5);
    }

    //saiyan oozaru and arco/nameks
    public void setBodyColor1(int color) {
        int i = 21;
        if (DNS == null || DNS.length() < i)
            return;

        String hexCol = JRMCoreH.numToLet5(color);
        DNS = DNS.substring(0, i) + hexCol + DNS.substring(i + 5);
    }

    // namekian/arco
    public void setBodyColor2(int color) {
        int i = 26;
        if (DNS == null || DNS.length() < i)
            return;
        String hexCol = JRMCoreH.numToLet5(color);
        DNS = DNS.substring(0, i) + hexCol + DNS.substring(i + 5);
    }

    //only arco
    public void setBodyColor3(int color) {
        int i = 31;
        if (DNS == null || DNS.length() < i)
            return;
        String hexCol = JRMCoreH.numToLet5(color);
        DNS = DNS.substring(0, i) + hexCol + DNS.substring(i + 5);
    }

    public void setHairPreset(int preset) {
        int i = 3;
        if (DNS == null || DNS.length() < i)
            return;

        DNS = DNS.substring(0, i) + JRMCoreH.numToLet(preset) + DNS.substring(i + 2);
    }

    public void setNosePreset(int preset) {
        int i = 36;
        if (DNS == null || DNS.length() < i)
            return;

        DNS = DNS.substring(0, i) + JRMCoreH.numToLet(preset) + DNS.substring(i + 2);

    }

    // Negative Values will Drain instead
    public void restoreKiPercent(float percToRestore) {
        int maxKi = DBCUtils.getMaxKi(player);
        int toAdd = (int) (maxKi * (percToRestore / 100));

        Ki += toAdd;
        Ki = Math.min(Ki, maxKi);
        saveToNBT(true);
    }

    public void restoreHealthPercent(float percToRestore) {
        int maxBody = DBCUtils.getMaxBody(player);
        int toAdd = (int) (maxBody * (percToRestore / 100));

        Body += toAdd;
        Body = Math.min(Body, maxBody);
        saveToNBT(true);
    }

    public void restoreStaminaPercent(float percToRestore) {
        int maxSta = DBCUtils.getMaxStamina(player);
        int toAdd = (int) (maxSta * (percToRestore / 100));

        Stamina += toAdd;
        Stamina = Math.min(Stamina, maxSta);
        saveToNBT(true);
    }

    //Negative value will add instead
    public void restoreUIHeat(float percToRestore) {
        if (!isForm(DBCForm.UltraInstinct))
            return;

        int maxHeat = JGConfigUltraInstinct.CONFIG_UI_HEAT_DURATION[State2];
        int toAdd = (int) (maxHeat * (percToRestore / 100));

        Heat = Math.max(Heat - toAdd, 0);
        saveToNBT(true);

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
        saveToNBT(true);
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

    // RUN FROM SERVER
    public NBTTagCompound getNBT(){
        NBTTagCompound c = new NBTTagCompound();
        PlayerCustomFormData formData = Utility.getFormData(player);
        c.setInteger("customFormID", formData.currentForm);
        c.setFloat("customFormLevel", formData.getCurrentLevel());
        c.setInteger("jrmcStrI", STR);
        c.setInteger("jrmcDexI", DEX);
        c.setInteger("jrmcConI", CON);
        c.setInteger("jrmcWilI", WIL);
        c.setInteger("jrmcIntI", MND);
        c.setInteger("jrmcCncI", SPI);
        c.setInteger("jrmcEnrgy", Ki);
        c.setInteger("jrmcStamina", Stamina);
        c.setInteger("jrmcBdy", Body);
        c.setInteger("jrmcHar4va", KOforXTicks);
        c.setInteger("jrmcSaiRg", Rage);
        c.setInteger("jrmcEf8slc", Heat);
        c.setInteger("jrmcAuraColor", AuraColor);
        c.setInteger("preCustomAuraColor", preCustomAuraColor);
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
        c.setString("preCustomFormDNS", preCustomFormDNS);
        c.setString("jrmcDNSH", DNSHair);
        c.setString("preCustomFormDNSH", preCustomFormDNSHair);
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
        KOforXTicks = c.getInteger("jrmcHar4va");
        Rage = c.getInteger("jrmcSaiRg");
        Heat = c.getInteger("jrmcEf8slc");
        isKO = c.getInteger("jrmcHar4va") > 0;
        AuraColor = c.getInteger("jrmcAuraColor");
        preCustomAuraColor = c.getInteger("preCustomAuraColor");
        currentCustomForm = c.getInteger("customFormID");
        currentCustomFormLevel = c.getFloat("customFormLevel");
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
        preCustomFormDNS = c.getString("preCustomFormDNS");
        preCustomFormDNSHair = c.getString("preCustomFormDNSH");
        nbt = c;
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) { // save all fields to compound
        NBTTagCompound c = compound(entity, DBCPersisted);
        PlayerCustomFormData formData = Utility.getFormData(player);
        c.setInteger("customFormID", formData.currentForm);
        c.setFloat("customFormLevel", formData.getCurrentLevel());
        c.setInteger("jrmcStrI", STR);
        c.setInteger("jrmcDexI", DEX);
        c.setInteger("jrmcConI", CON);
        c.setInteger("jrmcWilI", WIL);
        c.setInteger("jrmcIntI", MND);
        c.setInteger("jrmcCncI", SPI);
        c.setInteger("jrmcEnrgy", Ki);
        c.setInteger("jrmcStamina", Stamina);
        c.setInteger("jrmcBdy", Body);
        c.setInteger("jrmcHar4va", KOforXTicks);
        c.setInteger("jrmcSaiRg", Rage);
        c.setInteger("jrmcEf8slc", Heat);
        c.setInteger("jrmcAuraColor", AuraColor);
        c.setInteger("preCustomAuraColor", preCustomAuraColor);

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
        c.setString("preCustomFormDNS", preCustomFormDNS);
        c.setString("jrmcDNSH", DNSHair);
        c.setString("preCustomFormDNSH", preCustomFormDNSHair);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) { // compound has all synced data,load all fields from compound
        NBTTagCompound c = Utility.isServer() ? compound(entity, DBCPersisted) : compound;
        STR = c.getInteger("jrmcStrI");
        DEX = c.getInteger("jrmcDexI");
        CON = c.getInteger("jrmcConI");
        WIL = c.getInteger("jrmcWilI");
        MND = c.getInteger("jrmcIntI");
        SPI = c.getInteger("jrmcCncI");
        Ki = c.getInteger("jrmcEnrgy");
        Stamina = c.getInteger("jrmcStamina");
        Body = c.getInteger("jrmcBdy");
        KOforXTicks = c.getInteger("jrmcHar4va");
        Rage = c.getInteger("jrmcSaiRg");
        Heat = c.getInteger("jrmcEf8slc");
        isKO = c.getInteger("jrmcHar4va") > 0;
        AuraColor = c.getInteger("jrmcAuraColor");
        preCustomAuraColor = c.getInteger("preCustomAuraColor");

        if (Utility.isServer(world)) {
            PlayerCustomFormData formData = Utility.getFormData(player);
            currentCustomForm = formData.currentForm;
            currentCustomFormLevel = formData.getCurrentLevel();
        } else {
            currentCustomForm = c.getInteger("customFormID");
            currentCustomFormLevel = c.getFloat("customFormLevel");
        }

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

        preCustomFormDNS = c.getString("preCustomFormDNS");
        preCustomFormDNSHair = c.getString("preCustomFormDNSH");
        nbt = c;
    }


    public static boolean eligibleForDBC(Entity p) {
        if (p instanceof EntityPlayer)
            return true;
        return false;
    }

    public static DBCData get(Entity player) {
        return getCache(player, DBCPersisted);

    }

    public static DBCData get(String playerName) {
        return getCache(playerName, DBCPersisted);

    }

    public static DBCData getClient() {
        if(Minecraft.getMinecraft().thePlayer == null)
            return null;
        return getClient(DBCPersisted);

    }

    public static boolean has(Entity p) {
        return getCache(p, DBCPersisted) != null;
    }

    public static boolean has(String name) {
        return getCache(name, DBCPersisted) != null;
    }

}
