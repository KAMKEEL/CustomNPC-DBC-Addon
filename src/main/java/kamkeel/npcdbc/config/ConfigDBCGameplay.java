package kamkeel.npcdbc.config;

import cpw.mods.fml.common.FMLLog;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import noppes.npcs.util.ValueUtil;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class ConfigDBCGameplay
{
    public static Configuration config;

    public final static String StatusEffects = "StatusEffects";
    public static Property CheckEffectsTickProperty;
    public static float CheckEffectsTick = 10;

    public final static String Forms = "Forms";
    public static Property InstantTransformProperty;
    public static boolean InstantTransform = true;

    public final static String ChargingDex = "ChargingDex";
    public static Property EnableChargingDexProperty;
    public static boolean EnableChargingDex = true;
    public static Property MartialArtistChargeProperty;
    public static float MartialArtistCharge = 60;
    public static Property SpiritualistChargeProperty;
    public static float SpiritualistCharge = 60;
    public static Property WarriorChargeProperty;
    public static float WarriorCharge = 60;

    public final static String NamekianRegeneration = "Namek Regen";
    public static Property EnableNamekianRegenProperty;
    public static boolean EnableNamekianRegen = true;
    public static float NamekianRegenMin = 20;
    public static float NamekianRegenMax = 50;

    public final static String PotaraFusion = "PotaraFusion";
    public static boolean UniqueEarrings = true;
    public static boolean WearableEarrings = true;
    public static int PotaraOneTime = 10;
    public static int PotaraTwoTime = 15;
    public static int PotaraThreeTime = 25;

    public final static String Zenkai = "Zenkai";
    public static boolean SaiyanZenkai = true;
    public static boolean HalfSaiyanZenkai = true;

    public final static String KiCharge = "KiCharge";
    public static boolean PreciseKiCharging = true;
    public static boolean KiChargeReleaseLevel = true;

    public static void init(File configFile)
    {
        config = new Configuration(configFile);

        try
        {
            config.load();

            CheckEffectsTickProperty = config.get(StatusEffects, "Run Effects Every X Tick", 10, "This will check effects every X ticks. All registered effects must be multiple of 10. [10, 20, 30...] Max: 100");
            CheckEffectsTick = CheckEffectsTickProperty.getInt(10);
            CheckEffectsTick = (CheckEffectsTick % 10 == 0) ? CheckEffectsTick : ((CheckEffectsTick / 10) + 1) * 10;
            CheckEffectsTick = ValueUtil.clamp(CheckEffectsTick, 10, 100);

            EnableChargingDexProperty = config.get(ChargingDex, "0. Enable Charging Dex", true, "Charging Dex -> Percent of Total Defense Activated while Charging Ki Attacks. \nActive Defense [Blocking], Passive [Not Blocking](Takes Percent of Active in JRMCore Configs.) \nCharging Defense [Charging Ki Attack](Percent of Active)[0 - 100]");
            EnableChargingDex = EnableChargingDexProperty.getBoolean(true);
            MartialArtistChargeProperty = config.get(ChargingDex, "1. Martial Artist Percent", 60);
            MartialArtistCharge = MartialArtistChargeProperty.getInt(60);
            SpiritualistChargeProperty = config.get(ChargingDex, "2. Spiritualist Percent", 60);
            SpiritualistCharge = SpiritualistChargeProperty.getInt(60);
            WarriorChargeProperty = config.get(ChargingDex, "3. Warrior Percent", 60);
            WarriorCharge = WarriorChargeProperty.getInt(60);

            MartialArtistCharge = ValueUtil.clamp(WarriorCharge, 0, 100);
            SpiritualistCharge = ValueUtil.clamp(WarriorCharge, 0, 100);
            WarriorCharge = ValueUtil.clamp(WarriorCharge, 0, 100);

            EnableNamekianRegenProperty = config.get(NamekianRegeneration, "Enable Namekian Regeneration", true, "Namekian Regeneration will automatically apply the Namek Regen Effect (dbc_effects.cfg), \nwhen the Player falls below MIN Health and will stop continue to MAX Health.");
            EnableNamekianRegen = EnableNamekianRegenProperty.getBoolean(true);
            NamekianRegenMin = config.get(NamekianRegeneration, "Min Namekian Regen", 20).getInt(20);
            NamekianRegenMax = config.get(NamekianRegeneration, "Max Namekian Regen", 50).getInt(50);

            SaiyanZenkai = config.get(Zenkai, "Enable Saiyan Zenkai", true, "Enables Zenkai for Saiyans after Revive").getBoolean(true);
            HalfSaiyanZenkai = config.get(Zenkai, "Enable Half Saiyan Zenkai", true, "Enables Zenkai for Half Saiyans after Revive").getBoolean(true);

            InstantTransformProperty = config.get(Forms, "Instant Transform Bypass Parent", false, "Allows Instant Transform to Bypass the Parent Only Check\n [If the player has Instant Transform Unlocked in Mastery, they can go to the form directly]");
            InstantTransform = InstantTransformProperty.getBoolean(false);

            config.setCategoryComment(PotaraFusion, "Potara Fusion comes with a Bonus Multi applied by the Potara Status Effect. This can be modified within the DBC Addon Effect Config");
            config.setCategoryPropertyOrder(PotaraFusion, new ArrayList<>(Arrays.asList("Unique Earrings", "Wearable Earrings", "Tier 1 Time", "Tier 2 Time", "Tier 3 Time")));
            UniqueEarrings = config.get(PotaraFusion, "Unique Earrings", true, "Enabling Unique Earrings will cause all SPLIT Potaras to be hashed to a\n unique pair. Only the unique pairs can be used in a fusion.").getBoolean(true);
            WearableEarrings = config.get(PotaraFusion, "Wearable Earrings", true, "Enabling Wearable Earrings will check a radius around the worn player.\n Disabling this will require players to right-click each-other with valid earrings to fuse.").getBoolean(true);
            PotaraOneTime = config.get(PotaraFusion, "Tier 1 Time", 10, "The amount of time the Tier One Potara Earrings Fusion lasts").getInt(10);
            PotaraTwoTime = config.get(PotaraFusion, "Tier 2 Time", 15, "The amount of time the Tier Two Potara Earrings Fusion lasts").getInt(15);
            PotaraThreeTime = config.get(PotaraFusion, "Tier 3 Time", 25, "The amount of time the Tier Three Potara Earrings Fusion lasts").getInt(25);

            PreciseKiCharging = config.get(KiCharge, "Precise Ki Charge", true, "Enabling this feature will allow all players to Charge ki at a more direct rate. Instead of 5 percent \nit would charge every 1 percent and become more accurate / precise.").getBoolean(true);
            KiChargeReleaseLevel = config.get(KiCharge, "Ki Charge Speed with Release Level", true, "Enabling this feature will make Ki Charging faster for players with a higher Release Level.").getBoolean(true);
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
