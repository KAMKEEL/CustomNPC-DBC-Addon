package kamkeel.npcdbc.config;

import cpw.mods.fml.common.FMLLog;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import noppes.npcs.util.ValueUtil;
import org.apache.logging.log4j.Level;

import java.io.File;

public class ConfigDBCGameplay
{
    public static Configuration config;

    public final static String StatusEffects = "StatusEffects";
    public static Property CheckEffectsTickProperty;
    public static float CheckEffectsTick = 10;

    public final static String ChargingDex = "ChargingDex";
    public static Property EnableChargingDexProperty;
    public static boolean EnableChargingDex = true;
    public static Property MartialArtistChargeProperty;
    public static float MartialArtistCharge = 60;
    public static Property SpiritualistChargeProperty;
    public static float SpiritualistCharge = 60;
    public static Property WarriorChargeProperty;
    public static float WarriorCharge = 60;

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
