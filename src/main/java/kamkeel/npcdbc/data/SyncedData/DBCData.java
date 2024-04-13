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
 *
 * Example usage:
 * DBCData.get(p).setInt("jrmcStrI",100)
 * Sets the jrmcStrI int tag of entity p's PlayerPersisted compound to 100
 */
public class DBCData extends PerfectSync<DBCData> implements IExtendedEntityProperties {
    public static String dn = "PlayerPersisted";

    public int str, dex, con, wil, mnd, spi, tp, body, ki, stamina, koForXTicks;
    public byte clss, race, pwrtype, state1, state2, release;
    public boolean alive, ko;
    public String skills, racialSkill, SE, settings, FMR, FMNR;

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

        c.setInteger("jrmcStrI", str);
        c.setInteger("jrmcDexI", dex);
        c.setInteger("jrmcConI", con);
        c.setInteger("jrmcWilI", wil);
        c.setInteger("jrmcIntI", mnd);
        c.setInteger("jrmcCncI", spi);
        c.setInteger("jrmcEnrgy", ki);
        c.setInteger("jrmcStamina", stamina);
        c.setInteger("jrmcBdy", body);
        c.setInteger("jrmcHar4va", koForXTicks);

        c.setByte("jrmcState1", state1);
        c.setByte("jrmcState2", state2);
        c.setByte("jrmcRelease", release);
        c.setByte("jrmcPwrtyp", pwrtype);
        c.setByte("jrmcRace", race);
        c.setByte("jrmcClass", clss);

        c.setString("jrmcStatusEff", SE);
        c.setString("jrmcSSltX", racialSkill);
        c.setString("jrmcSSlts", skills);
        c.setString("jrmcSettings", settings);
        c.setString("jrmcFormMasteryRacial_" + JRMCoreH.Races[race], FMR);
        c.setString("jrmcFormMasteryNonRacial", FMNR);

        compound = c;
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) { // compound has all synced data,load all fields from compound
        NBTTagCompound c = u.isServer() ? compound(p, dn) : compound;

        str = c.getInteger("jrmcStrI");
        dex = c.getInteger("jrmcDexI");
        con = c.getInteger("jrmcConI");
        wil = c.getInteger("jrmcWilI");
        mnd = c.getInteger("jrmcIntI");
        spi = c.getInteger("jrmcCncI");
        ki = c.getInteger("jrmcEnrgy");
        stamina = c.getInteger("jrmcStamina");
        body = c.getInteger("jrmcBdy");
        koForXTicks = c.getInteger("jrmcHar4va");

        ko = c.getInteger("jrmcHar4va") > 0;
        state1 = c.getByte("jrmcState1");
        state2 = c.getByte("jrmcState2");
        release = c.getByte("jrmcRelease");
        pwrtype = c.getByte("jrmcPwrtyp");
        race = c.getByte("jrmcRace");
        clss = c.getByte("jrmcClass");

        SE = c.getString("jrmcStatusEff");
        racialSkill = c.getString("jrmcSSltX");
        skills = c.getString("jrmcSSlts");
        settings = c.getString("jrmcSettings");
        FMR = c.getString("jrmcFormMasteryRacial_" + JRMCoreH.Races[race]);
        FMNR = c.getString("jrmcFormMasteryNonRacial");

        cmpd = c;

    }

}
