package kamkeel.npcdbc.config;

import cpw.mods.fml.common.FMLLog;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import noppes.npcs.util.ValueUtil;
import org.apache.logging.log4j.Level;

import java.io.File;

public class ConfigCapsules
{
    public static Configuration config;

    public final static String CAPSULES = "Capsules";

    public final static String KI = "Ki";
    public final static String HEALTH = "Health";
    public final static String STAMINA = "Stamina";
    public final static String MISC = "Misc";

    /**
     *  General Properties
     **/
    public static Property EnableCapsulesProperty;
    public static boolean EnableCapsules = true;

    public static Property EnableCapsuleCooldownsProperty;
    public static boolean EnableCapsuleCooldowns = true;

    public static Property EnableKiCapsuleProperty;
    public static boolean EnableKiCapsule = true;

    public static Property KiCapsuleCooldownTypeProperty;
    public static int KiCapsuleCooldownType = 0;

    public static Property KiCapsuleMaxStackProperty;
    public static int KiCapsuleMaxStack = 16;

    public static Property EnableHealthCapsuleProperty;
    public static boolean EnableHealthCapsule = true;

    public static Property HealthCapsuleCooldownTypeProperty;
    public static int HealthCapsuleCooldownType = 0;

    public static Property HealthCapsuleMaxStackProperty;
    public static int HealthCapsuleMaxStack = 16;

    public static Property EnableStaminaCapsuleProperty;
    public static boolean EnableStaminaCapsule = true;

    public static Property StaminaCapsuleCooldownTypeProperty;
    public static int StaminaCapsuleCooldownType = 0;

    public static Property StaminaCapsuleMaxStackProperty;
    public static int StaminaCapsuleMaxStack = 16;

    public static Property EnableMiscCapsuleProperty;
    public static boolean EnableMiscCapsule = true;

    public static Property MiscCapsuleMaxStackProperty;
    public static int MiscCapsuleMaxStack = 16;


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

    public static Property KiSuperiorStrengthProperty;
    public static int KiSuperiorStrength = 75;
    public static Property KiSuperiorCooldownProperty;
    public static int KiSuperiorCooldown = 10;

    public static Property KiMasterStrengthProperty;
    public static int KiMasterStrength = 100;
    public static Property KiMasterCooldownProperty;
    public static int KiMasterCooldown = 10;

    /**
     *  Health Properties
     **/
    public static Property HealthBaseStrengthProperty;
    public static int HealthBaseStrength = 5;
    public static Property HealthBaseCooldownProperty;
    public static int HealthBaseCooldown = 10;

    public static Property HealthSuperStrengthProperty;
    public static int HealthSuperStrength = 10;
    public static Property HealthSuperCooldownProperty;
    public static int HealthSuperCooldown = 10;

    public static Property HealthMegaStrengthProperty;
    public static int HealthMegaStrength = 25;
    public static Property HealthMegaCooldownProperty;
    public static int HealthMegaCooldown = 10;

    public static Property HealthGigaStrengthProperty;
    public static int HealthGigaStrength = 50;
    public static Property HealthGigaCooldownProperty;
    public static int HealthGigaCooldown = 10;

    public static Property HealthSuperiorStrengthProperty;
    public static int HealthSuperiorStrength = 75;
    public static Property HealthSuperiorCooldownProperty;
    public static int HealthSuperiorCooldown = 10;

    public static Property HealthMasterStrengthProperty;
    public static int HealthMasterStrength = 100;
    public static Property HealthMasterCooldownProperty;
    public static int HealthMasterCooldown = 10;

    /**
     *  Stamina Properties
     **/
    public static Property StaminaBaseStrengthProperty;
    public static int StaminaBaseStrength = 5;
    public static Property StaminaBaseCooldownProperty;
    public static int StaminaBaseCooldown = 10;

    public static Property StaminaSuperStrengthProperty;
    public static int StaminaSuperStrength = 10;
    public static Property StaminaSuperCooldownProperty;
    public static int StaminaSuperCooldown = 10;

    public static Property StaminaMegaStrengthProperty;
    public static int StaminaMegaStrength = 25;
    public static Property StaminaMegaCooldownProperty;
    public static int StaminaMegaCooldown = 10;

    public static Property StaminaGigaStrengthProperty;
    public static int StaminaGigaStrength = 50;
    public static Property StaminaGigaCooldownProperty;
    public static int StaminaGigaCooldown = 10;

    public static Property StaminaSuperiorStrengthProperty;
    public static int StaminaSuperiorStrength = 75;
    public static Property StaminaSuperiorCooldownProperty;
    public static int StaminaSuperiorCooldown = 10;

    public static Property StaminaMasterStrengthProperty;
    public static int StaminaMasterStrength = 100;
    public static Property StaminaMasterCooldownProperty;
    public static int StaminaMasterCooldown = 10;

    /**
     *  Misc Properties
     **/
    public static Property KOCooldownProperty;
    public static int KOCooldown = 10;

    public static Property ReviveCooldownProperty;
    public static int ReviveCooldown = 10;

    public static Property HeatCooldownProperty;
    public static int HeatCooldown = 10;

    public static Property PowerPointCooldownProperty;
    public static int PowerPointCooldown = 10;

    public static Property AbsorptionCooldownProperty;
    public static int AbsorptionCooldown = 10;

    public static void init(File configFile)
    {
        config = new Configuration(configFile);

        try
        {
            config.load();

            // Capsules
            EnableCapsulesProperty = config.get(CAPSULES, "Enable Capsules", true);
            EnableCapsules = EnableCapsulesProperty.getBoolean(true);

            EnableCapsuleCooldownsProperty = config.get(CAPSULES, "Enable Capsule Cooldowns", true, "All Cooldowns are in SECONDS");
            EnableCapsuleCooldowns = EnableCapsuleCooldownsProperty.getBoolean(true);

            EnableMiscCapsuleProperty = config.get(CAPSULES, "Misc Capsules", true, "Misc Capsules: Revive, Heat, KO");
            EnableMiscCapsule = EnableMiscCapsuleProperty.getBoolean(true);

            MiscCapsuleMaxStackProperty = config.get(CAPSULES, "Misc Capsule Max Stack Size", 16, "Max Stack Size per Misc Capsules");
            MiscCapsuleMaxStack = MiscCapsuleMaxStackProperty.getInt(16);

            EnableKiCapsuleProperty = config.get(CAPSULES, "Ki Capsules", true, "Enable Ki Capsules");
            EnableKiCapsule = EnableKiCapsuleProperty.getBoolean(true);

            KiCapsuleCooldownTypeProperty = config.get(CAPSULES, "Ki Capsule Cooldown", 0, "0 - Cooldowns are per Type [All Ki Capsules], 1 - Cooldowns are per Tier [Individual Ki Capsule Tier]");
            KiCapsuleCooldownType = KiCapsuleCooldownTypeProperty.getInt(0);

            KiCapsuleMaxStackProperty = config.get(CAPSULES, "Ki Capsule Max Stack Size", 16, "Max Stack Size per Ki Capsules");
            KiCapsuleMaxStack = KiCapsuleMaxStackProperty.getInt(16);

            EnableHealthCapsuleProperty = config.get(CAPSULES, "Health Capsules", true, "Enable Health Capsule");
            EnableHealthCapsule = EnableHealthCapsuleProperty.getBoolean(true);

            HealthCapsuleCooldownTypeProperty = config.get(CAPSULES, "Health Capsule Cooldown", 0, "0 - Cooldowns are per Type [All Health Capsules], 1 - Cooldowns are per Tier [Individual Health Capsule Tier]");
            HealthCapsuleCooldownType = HealthCapsuleCooldownTypeProperty.getInt(0);

            HealthCapsuleMaxStackProperty = config.get(CAPSULES, "Health Capsule Max Stack Size", 16, "Max Stack Size per Health Capsule");
            HealthCapsuleMaxStack = HealthCapsuleMaxStackProperty.getInt(16);

            EnableStaminaCapsuleProperty = config.get(CAPSULES, "Stamina Capsules", true, "Enable Stamina Capsules");
            EnableStaminaCapsule = EnableStaminaCapsuleProperty.getBoolean(true);

            StaminaCapsuleCooldownTypeProperty = config.get(CAPSULES, "Stamina Capsule Cooldown", 0, "0 - Cooldowns are per Type [All Stamina Capsules], 1 - Cooldowns are per Tier [Individual Stamina Capsule Tier]");
            StaminaCapsuleCooldownType = StaminaCapsuleCooldownTypeProperty.getInt(0);

            StaminaCapsuleMaxStackProperty = config.get(CAPSULES, "Stamina Capsule Max Stack Size", 16, "Max Stack Size per Stamina Capsule");
            StaminaCapsuleMaxStack = StaminaCapsuleMaxStackProperty.getInt(16);

            KiCapsuleCooldownType = ValueUtil.clamp(KiCapsuleCooldownType, 0, 1);
            HealthCapsuleCooldownType = ValueUtil.clamp(HealthCapsuleCooldownType, 0, 1);
            StaminaCapsuleCooldownType = ValueUtil.clamp(StaminaCapsuleCooldownType, 0, 1);

            KiCapsuleMaxStack = ValueUtil.clamp(KiCapsuleMaxStack, 1, 64);
            HealthCapsuleMaxStack = ValueUtil.clamp(HealthCapsuleMaxStack, 1, 64);
            StaminaCapsuleMaxStack = ValueUtil.clamp(StaminaCapsuleMaxStack, 1, 64);

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

            KiSuperiorStrengthProperty = config.get(KI, "5. Superior Ki Strength", 75);
            KiSuperiorStrength = KiSuperiorStrengthProperty.getInt(75);
            KiSuperiorCooldownProperty = config.get(KI, "5. Superior Ki Cooldown", 10);
            KiSuperiorCooldown = KiSuperiorCooldownProperty.getInt(10);

            KiMasterStrengthProperty = config.get(KI, "6. Master Ki Strength", 100);
            KiMasterStrength = KiMasterStrengthProperty.getInt(100);
            KiMasterCooldownProperty = config.get(KI, "6. Master Ki Cooldown", 10);
            KiMasterCooldown = KiMasterCooldownProperty.getInt(10);

            // Health Capsules
            HealthBaseStrengthProperty = config.get(HEALTH, "1. Base Health Strength", 5);
            HealthBaseStrength = HealthBaseStrengthProperty.getInt(5);
            HealthBaseCooldownProperty = config.get(HEALTH, "1. Base Health Cooldown", 10);
            HealthBaseCooldown = HealthBaseCooldownProperty.getInt(10);

            HealthSuperStrengthProperty = config.get(HEALTH, "2. Super Health Strength", 10);
            HealthSuperStrength = HealthSuperStrengthProperty.getInt(10);
            HealthSuperCooldownProperty = config.get(HEALTH, "2. Super Health Cooldown", 10);
            HealthSuperCooldown = HealthSuperCooldownProperty.getInt(10);

            HealthMegaStrengthProperty = config.get(HEALTH, "3. Mega Health Strength", 25);
            HealthMegaStrength = HealthMegaStrengthProperty.getInt(25);
            HealthMegaCooldownProperty = config.get(HEALTH, "3. Mega Health Cooldown", 10);
            HealthMegaCooldown = HealthMegaCooldownProperty.getInt(10);

            HealthGigaStrengthProperty = config.get(HEALTH, "4. Giga Health Strength", 50);
            HealthGigaStrength = HealthGigaStrengthProperty.getInt(50);
            HealthGigaCooldownProperty = config.get(HEALTH, "4. Giga Health Cooldown", 10);
            HealthGigaCooldown = HealthGigaCooldownProperty.getInt(10);

            HealthSuperiorStrengthProperty = config.get(HEALTH, "5. Superior Health Strength", 75);
            HealthSuperiorStrength = HealthSuperiorStrengthProperty.getInt(75);
            HealthSuperiorCooldownProperty = config.get(HEALTH, "5. Superior Health Cooldown", 10);
            HealthSuperiorCooldown = HealthSuperiorCooldownProperty.getInt(10);

            HealthMasterStrengthProperty = config.get(HEALTH, "6. Master Health Strength", 100);
            HealthMasterStrength = HealthMasterStrengthProperty.getInt(100);
            HealthMasterCooldownProperty = config.get(HEALTH, "6. Master Health Cooldown", 10);
            HealthMasterCooldown = HealthMasterCooldownProperty.getInt(10);

            // Stamina Capsules
            StaminaBaseStrengthProperty = config.get(STAMINA, "1. Base Stamina Strength", 5);
            StaminaBaseStrength = StaminaBaseStrengthProperty.getInt(5);
            StaminaBaseCooldownProperty = config.get(STAMINA, "1. Base Stamina Cooldown", 10);
            StaminaBaseCooldown = StaminaBaseCooldownProperty.getInt(10);

            StaminaSuperStrengthProperty = config.get(STAMINA, "2. Super Stamina Strength", 10);
            StaminaSuperStrength = StaminaSuperStrengthProperty.getInt(10);
            StaminaSuperCooldownProperty = config.get(STAMINA, "2. Super Stamina Cooldown", 10);
            StaminaSuperCooldown = StaminaSuperCooldownProperty.getInt(10);

            StaminaMegaStrengthProperty = config.get(STAMINA, "3. Mega Stamina Strength", 25);
            StaminaMegaStrength = StaminaMegaStrengthProperty.getInt(25);
            StaminaMegaCooldownProperty = config.get(STAMINA, "3. Mega Stamina Cooldown", 10);
            StaminaMegaCooldown = StaminaMegaCooldownProperty.getInt(10);

            StaminaGigaStrengthProperty = config.get(STAMINA, "4. Giga Stamina Strength", 50);
            StaminaGigaStrength = StaminaGigaStrengthProperty.getInt(50);
            StaminaGigaCooldownProperty = config.get(STAMINA, "4. Giga Stamina Cooldown", 10);
            StaminaGigaCooldown = StaminaGigaCooldownProperty.getInt(10);

            StaminaSuperiorStrengthProperty = config.get(STAMINA, "5. Superior Stamina Strength", 75);
            StaminaSuperiorStrength = StaminaSuperiorStrengthProperty.getInt(75);
            StaminaSuperiorCooldownProperty = config.get(STAMINA, "5. Superior Stamina Cooldown", 10);
            StaminaSuperiorCooldown = StaminaSuperiorCooldownProperty.getInt(10);

            StaminaMasterStrengthProperty = config.get(STAMINA, "6. Master Stamina Strength", 100);
            StaminaMasterStrength = StaminaMasterStrengthProperty.getInt(100);
            StaminaMasterCooldownProperty = config.get(STAMINA, "6. Master Stamina Cooldown", 10);
            StaminaMasterCooldown = StaminaMasterCooldownProperty.getInt(10);

            // Misc Capsules
            KOCooldownProperty = config.get(MISC, "Ko Cooldown", 10);
            KOCooldown = KOCooldownProperty.getInt(10);

            ReviveCooldownProperty = config.get(MISC, "Revive Cooldown", 10);
            ReviveCooldown = ReviveCooldownProperty.getInt(10);

            HeatCooldownProperty = config.get(MISC, "Heat Cooldown", 10);
            HeatCooldown = HeatCooldownProperty.getInt(10);

            PowerPointCooldownProperty = config.get(MISC, "PowerPoint Cooldown", 10, "Only usable by Arcosians to restore PP Value");
            PowerPointCooldown = PowerPointCooldownProperty.getInt(10);

            AbsorptionCooldownProperty = config.get(MISC, "Absorption Cooldown", 10, "Only usable by Majins to restore absorption");
            AbsorptionCooldown = AbsorptionCooldownProperty.getInt();
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
