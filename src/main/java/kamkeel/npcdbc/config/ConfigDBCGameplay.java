package kamkeel.npcdbc.config;

import cpw.mods.fml.common.FMLLog;
import net.minecraftforge.common.config.Configuration;
import noppes.npcs.util.ValueUtil;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class ConfigDBCGameplay
{
    public static Configuration config;

    public final static String StatusEffects = "Status_Effects";
    public static double CheckEffectsTick = 10;

    public final static String Forms = "Forms";
    public static boolean InstantTransform = true;

    public final static String ChargingDex = "Charging_Dex";
    public static boolean EnableChargingDex = true;
    public static int MartialArtistCharge = 60;
    public static int SpiritualistCharge = 60;
    public static int WarriorCharge = 60;

    public final static String NamekianRegeneration = "Namek_Regen";
    public static boolean EnableNamekianRegen = true;
    public static int NamekianRegenMin = 20;
    public static int NamekianRegenMax = 50;

    public final static String PotaraFusion = "Potara_Fusion";
    public static boolean UniqueEarrings = true;
    public static boolean WearableEarrings = true;
    public static int PotaraOneTime = 10;
    public static int PotaraTwoTime = 15;
    public static int PotaraThreeTime = 25;

    public final static String Zenkai = "Zenkai";
    public static boolean SaiyanZenkai = true;
    public static boolean HalfSaiyanZenkai = true;

    public final static String KiCharge = "Ki_Charge";
    public static boolean RevampKiCharging = true;
    public static boolean KiPotentialUnlock = true;
    public static int KiChargeRate = 1;

    public static void init(File configFile)
    {
        config = new Configuration(configFile);

        try
        {
            config.load();

            CheckEffectsTick = config.get(StatusEffects, "Run Effects Every X Tick", 10, "This will check effects every X ticks. All registered effects must be multiple of 10. [10, 20, 30...] Max: 100").getInt(10);
            CheckEffectsTick = (CheckEffectsTick % 10 == 0) ? CheckEffectsTick : ((CheckEffectsTick / 10) + 1) * 10;
            CheckEffectsTick = ValueUtil.clamp(CheckEffectsTick, 10, 100);

            EnableChargingDex = config.get(ChargingDex, "0. Enable Charging Dex", true,
                "Charging Dex -> Percent of Total Defense Activated while Charging Ki Attacks. " +
                "\nActive Defense [Blocking], Passive [Not Blocking](Takes Percent of Active in JRMCore Configs.) " +
                "\nCharging Defense [Charging Ki Attack](Percent of Active)[0 - 100]").getBoolean(true);
            MartialArtistCharge = config.get(ChargingDex, "1. Martial Artist Percent", 60).getInt(60);
            SpiritualistCharge = config.get(ChargingDex, "2. Spiritualist Percent", 60).getInt(60);
            WarriorCharge = config.get(ChargingDex, "3. Warrior Percent", 60).getInt(60);

            MartialArtistCharge = ValueUtil.clamp(WarriorCharge, 0, 100);
            SpiritualistCharge = ValueUtil.clamp(WarriorCharge, 0, 100);
            WarriorCharge = ValueUtil.clamp(WarriorCharge, 0, 100);

            EnableNamekianRegen = config.get(NamekianRegeneration, "Enable Namekian Regeneration", true,
                "Namekian Regeneration will automatically apply the Namek Regen Effect (dbc/effects.cfg), " +
                    "\nwhen the Player falls below MIN Health and will stop continue to MAX Health.").getBoolean(true);
            NamekianRegenMin = config.get(NamekianRegeneration, "Min Namekian Regen", 20).getInt(20);
            NamekianRegenMax = config.get(NamekianRegeneration, "Max Namekian Regen", 50).getInt(50);

            SaiyanZenkai = config.get(Zenkai, "Enable Saiyan Zenkai", true, "Enables Zenkai for Saiyans after Revive").getBoolean(true);
            HalfSaiyanZenkai = config.get(Zenkai, "Enable Half Saiyan Zenkai", true, "Enables Zenkai for Half Saiyans after Revive").getBoolean(true);

            InstantTransform = config.get(Forms, "Instant Transform Bypass Parent", false,
                "Allows Instant Transform to Bypass the Parent Only Check\n" +
                    "[If the player has Instant Transform Unlocked in Mastery, they can go to the form directly]").getBoolean(false);

            config.setCategoryComment(PotaraFusion, "Potara Fusion comes with a Bonus Multi applied by the Potara Status Effect. This can be modified within the DBC Addon Effect Config");
            config.setCategoryPropertyOrder(PotaraFusion, new ArrayList<>(Arrays.asList("Unique Earrings", "Wearable Earrings", "Tier 1 Time", "Tier 2 Time", "Tier 3 Time")));
            UniqueEarrings = config.get(PotaraFusion, "Unique Earrings", true, "Enabling Unique Earrings will cause all SPLIT Potaras to be hashed to a\n unique pair. Only the unique pairs can be used in a fusion.").getBoolean(true);
            WearableEarrings = config.get(PotaraFusion, "Wearable Earrings", true, "Enabling Wearable Earrings will check a radius around the worn player.\n Disabling this will require players to right-click each-other with valid earrings to fuse.").getBoolean(true);
            PotaraOneTime = config.get(PotaraFusion, "Tier 1 Time", 10, "The amount of time the Tier One Potara Earrings Fusion lasts. [In Minutes]").getInt(10);
            PotaraTwoTime = config.get(PotaraFusion, "Tier 2 Time", 15, "The amount of time the Tier Two Potara Earrings Fusion lasts. [In Minutes]").getInt(15);
            PotaraThreeTime = config.get(PotaraFusion, "Tier 3 Time", 25, "The amount of time the Tier Three Potara Earrings Fusion lasts. [In Minutes]").getInt(25);
            PotaraOneTime = Math.max(1, PotaraOneTime);
            PotaraTwoTime = Math.max(1, PotaraTwoTime);
            PotaraThreeTime = Math.max(1, PotaraThreeTime);

            config.setCategoryComment(KiCharge,
                "Ki Charge Revamp allows many modifications to the normal vanilla DBC Ki Charge to be configured.\n" +
                    "If the Revamp Ki Charge is disabled then the Status Effect Overpower will not function.\n" +
                    "This functionality does not control Energy Attack Charging speeds. Ki Revamp will also allow\n" +
                    "Humans and Namekians to power down with the FN Key, as opposed to vanilla DBC.");

            RevampKiCharging = config.get(KiCharge, "0. Enable Ki Revamp", true, "Enabling this feature will allow huge modifications to Ki Charging as featured below.").getBoolean(true);
            KiPotentialUnlock = config.get(KiCharge, "Ki Charge Speed with Potential Unlock Level", true,
                "Enabling this feature will make Ki Charging faster for players with a higher Release Level." +
                    "\nEach level: [1: 15ticks, 2: 14, 3: 13.... 10: 5 Ticks").getBoolean(true);
            KiChargeRate = config.get(KiCharge, "Ki Charge Rate", 1,
                "Tweaking this number will allow for more precise ki charging. Default for DBC is 5.").getInt(1);
            KiChargeRate = ValueUtil.clamp(KiChargeRate, 1, 50);
        }
        catch (Exception e)
        {
            FMLLog.log(Level.ERROR, e, "DBC Addon has had a problem loading its gameplay configuration");
        }
        finally
        {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }
}
