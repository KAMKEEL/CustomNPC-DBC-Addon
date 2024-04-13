package kamkeel.npcdbc.data.SyncedData;


import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.util.u;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * DBCData is basically all relevant DBC/JRMC tags in the PlayerPersisted compound tag. This get synced from server to client, so client always has access
 * to the latest version of the fields below
 * <p>
 * Example usage:
 * DBCData.get(p).setInt("jrmcStrI",100)
 * Sets the jrmcStrI int tag of entity p's PlayerPersisted compound to 100<
 */
public class DBCData extends PerfectSync<DBCData> implements IExtendedEntityProperties {
    public static String dn = "PlayerPersisted";

    public int STR, DEX, CON, WIL, MND, SPI, TP, Body, Ki, Stamina, KOforXTicks;
    public byte Class, Race, Powertype, Form1, Form2, Release;
    public boolean Alive, isKO;
    public String Skills, RacialSkills, StatusEffects, Settings, FormMasteryRacial, FormMasteryNR;

    public DBCData(Entity player) {
        super(player);
        this.DATA_NAME = dn;

    }

    public static boolean eligibleForDBC(Entity p) {
        if (p instanceof EntityPlayer)
            return true;
        return false;
    }

    public static DBCData get(Entity player) {
        return get(player, dn);

    }

    public static boolean has(Entity p) {
        return get(p, dn) != null;
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) { // save all fields to compound
        NBTTagCompound c = compound(p, dn);

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

        c.setByte("jrmcState1", Form1);
        c.setByte("jrmcState2", Form2);
        c.setByte("jrmcRelease", Release);
        c.setByte("jrmcPwrtyp", Powertype);
        c.setByte("jrmcRace", Race);
        c.setByte("jrmcClass", Class);

        c.setString("jrmcStatusEff", StatusEffects);
        c.setString("jrmcSSltX", RacialSkills);
        c.setString("jrmcSSlts", Skills);
        c.setString("jrmcSettings", Settings);
        c.setString("jrmcFormMasteryRacial_" + JRMCoreH.Races[Race], FormMasteryRacial);
        c.setString("jrmcFormMasteryNonRacial", FormMasteryNR);

        compound = c;
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) { // compound has all synced data,load all fields from compound
        NBTTagCompound c = u.isServer() ? compound(p, dn) : compound;

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

        isKO = c.getInteger("jrmcHar4va") > 0;
        Form1 = c.getByte("jrmcState1");
        Form2 = c.getByte("jrmcState2");
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

        cmpd = c;

    }

}
