package kamkeel.npcdbc.config;

import cpw.mods.fml.common.FMLLog;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.Level;

import java.io.File;

public class ConfigCapsules
{
    public static Configuration config;

    public final static String CAPSULES = "Capsules";

    public final static String KI = "Ki";
    public final static String HEALTH = "Health";
    public final static String STAMINA = "Stamina";

    /**
     *  General Properties
     **/
    public static Property EnableCapsulesProperty;
    public static boolean EnableCapsules = true;

    public static Property EnableCapsuleCooldownsProperty;
    public static boolean EnableCapsuleCooldowns = true;


    /**
     *  Ki Properties
     **/
    public static Property KiBaseStrengthProperty;
    public static int KiBaseStrength = 5;
    public static Property KiBaseCooldownProperty;
    public static int KiBaseCooldown = 10;

    public static Property KiSuperStrengthProperty;
    public static int KiSuperStrength = 10;
    public static Property KiSuperCooldownProperty;
    public static int KiSuperCooldown = 10;

    public static Property KiMegaStrengthProperty;
    public static int KiMegaStrength = 25;
    public static Property KiMegaCooldownProperty;
    public static int KiMegaCooldown = 10;

    public static Property KiGigaStrengthProperty;
    public static int KiGigaStrength = 50;
    public static Property KiGigaCooldownProperty;
    public static int KiGigaCooldown = 10;

    public static Property KiSupremeStrengthProperty;
    public static int KiSupremeStrength = 75;
    public static Property KiSupremeCooldownProperty;
    public static int KiSupremeCooldown = 10;

    public static Property KiMasterStrengthProperty;
    public static int KiMasterStrength = 100;
    public static Property KiMasterCooldownProperty;
    public static int KiMasterCooldown = 10;

    public static void init(File configFile)
    {
        config = new Configuration(configFile);

        try
        {
            config.load();

            // Capsules
            EnableCapsulesProperty = config.get(CAPSULES, "Enable Capsules", true);
            EnableCapsules = EnableCapsulesProperty.getBoolean(true);

            EnableCapsuleCooldownsProperty = config.get(CAPSULES, "Enable Capsule Cooldowns", true);
            EnableCapsuleCooldowns = EnableCapsuleCooldownsProperty.getBoolean(true);

            // Ki Capsules
            KiBaseStrengthProperty = config.get(KI, "1. Base Ki Strength", 5);
            KiBaseStrength = KiBaseStrengthProperty.getInt(5);
            KiBaseCooldownProperty = config.get(KI, "1. Base Ki Cooldown", 10);
            KiBaseCooldown = KiBaseCooldownProperty.getInt(10);

            KiSuperStrengthProperty = config.get(KI, "2. Super Ki Strength", 10);
            KiSuperStrength = KiSuperStrengthProperty.getInt(10);
            KiSuperCooldownProperty = config.get(KI, "2. Super Ki Cooldown", 10);
            KiSuperCooldown = KiSuperCooldownProperty.getInt(10);

            KiMegaStrengthProperty = config.get(KI, "3. Mega Ki Strength", 25);
            KiMegaStrength = KiMegaStrengthProperty.getInt(25);
            KiMegaCooldownProperty = config.get(KI, "3. Mega Ki Cooldown", 10);
            KiMegaCooldown = KiMegaCooldownProperty.getInt(10);

            KiGigaStrengthProperty = config.get(KI, "4. Giga Ki Strength", 50);
            KiGigaStrength = KiGigaStrengthProperty.getInt(50);
            KiGigaCooldownProperty = config.get(KI, "4. Giga Ki Cooldown", 10);
            KiGigaCooldown = KiGigaCooldownProperty.getInt(10);

            KiSupremeStrengthProperty = config.get(KI, "5. Supreme Ki Strength", 75);
            KiSupremeStrength = KiSupremeStrengthProperty.getInt(75);
            KiSupremeCooldownProperty = config.get(KI, "5. Supreme Ki Cooldown", 10);
            KiSupremeCooldown = KiSupremeCooldownProperty.getInt(10);

            KiMasterStrengthProperty = config.get(KI, "6. Master Ki Strength", 100);
            KiMasterStrength = KiMasterStrengthProperty.getInt(100);
            KiMasterCooldownProperty = config.get(KI, "6. Master Ki Cooldown", 10);
            KiMasterCooldown = KiMasterCooldownProperty.getInt(10);
        }
        catch (Exception e)
        {
            FMLLog.log(Level.ERROR, e, "DBC Addon has had a problem loading its capsule configuration");
        }
        finally
        {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }
}
