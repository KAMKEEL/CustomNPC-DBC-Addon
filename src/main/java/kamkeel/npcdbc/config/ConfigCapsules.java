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
    public final static String REGEN = "Regen";

    /**
     *  General Properties
     **/
    public static boolean EnableCapsules = true;
    public static boolean EnableCapsuleCooldowns = true;

    public static boolean EnableKiCapsule = true;
    public static int KiCapsuleCooldownType = 0;
    public static int KiCapsuleMaxStack = 16;

    public static boolean EnableHealthCapsule = true;
    public static int HealthCapsuleCooldownType = 0;
    public static int HealthCapsuleMaxStack = 16;

    public static boolean EnableStaminaCapsule = true;
    public static int StaminaCapsuleCooldownType = 0;
    public static int StaminaCapsuleMaxStack = 16;

    public static boolean EnableMiscCapsule = true;
    public static int MiscCapsuleMaxStack = 16;

    public static boolean EnableRegenCapsules = true;
    public static int RegenCapsuleCooldownType = 2;
    public static int RegenCapsuleMaxStack = 16;


    /**
     *  Ki Properties
     **/
    public static int KiBaseStrength = 5;
    public static int KiBaseCooldown = 10;

    public static int KiSuperStrength = 10;
    public static int KiSuperCooldown = 10;

    public static int KiMegaStrength = 25;
    public static int KiMegaCooldown = 10;

    public static int KiGigaStrength = 50;
    public static int KiGigaCooldown = 10;

    public static int KiSuperiorStrength = 75;
    public static int KiSuperiorCooldown = 10;

    public static int KiMasterStrength = 100;
    public static int KiMasterCooldown = 10;

    /**
     *  Health Properties
     **/
    public static int HealthBaseStrength = 5;
    public static int HealthBaseCooldown = 10;

    public static int HealthSuperStrength = 10;
    public static int HealthSuperCooldown = 10;

    public static int HealthMegaStrength = 25;
    public static int HealthMegaCooldown = 10;

    public static int HealthGigaStrength = 50;
    public static int HealthGigaCooldown = 10;

    public static int HealthSuperiorStrength = 75;
    public static int HealthSuperiorCooldown = 10;

    public static int HealthMasterStrength = 100;
    public static int HealthMasterCooldown = 10;

    /**
     *  Stamina Properties
     **/
    public static int StaminaBaseStrength = 5;
    public static int StaminaBaseCooldown = 10;

    public static int StaminaSuperStrength = 10;
    public static int StaminaSuperCooldown = 10;

    public static int StaminaMegaStrength = 25;
    public static int StaminaMegaCooldown = 10;

    public static int StaminaGigaStrength = 50;
    public static int StaminaGigaCooldown = 10;

    public static int StaminaSuperiorStrength = 75;
    public static int StaminaSuperiorCooldown = 10;

    public static int StaminaMasterStrength = 100;
    public static int StaminaMasterCooldown = 10;

    /**
     *  Regen properties
     */
    public static byte RegenHPBaseStrength = 1;
    public static int RegenHPBaseUseTime = 10;
    public static int RegenHPBaseCooldown = 10;

    public static byte RegenHPSuperStrength = 2;
    public static int RegenHPSuperUseTime = 10;
    public static int RegenHPSuperCooldown = 20;

    public static byte RegenHPMegaStrength = 3;
    public static int RegenHPMegaUseTime = 10;
    public static int RegenHPMegaCooldown = 30;

    public static byte RegenKiBaseStrength = 1;
    public static int RegenKiBaseUseTime = 10;
    public static int RegenKiBaseCooldown = 10;

    public static byte RegenKiSuperStrength = 2;
    public static int RegenKiSuperUseTime = 10;
    public static int RegenKiSuperCooldown = 20;

    public static byte RegenKiMegaStrength = 3;
    public static int RegenKiMegaUseTime = 10;
    public static int RegenKiMegaCooldown = 30;

    public static byte RegenStaminaBaseStrength = 1;
    public static int RegenStaminaBaseUseTime = 10;
    public static int RegenStaminaBaseCooldown = 10;

    public static byte RegenStaminaSuperStrength = 2;
    public static int RegenStaminaSuperUseTime = 10;
    public static int RegenStaminaSuperCooldown = 20;

    public static byte RegenStaminaMegaStrength = 3;
    public static int RegenStaminaMegaUseTime = 10;
    public static int RegenStaminaMegaCooldown = 30;



    /**
     *  Misc Properties
     **/
    public static int KOCooldown = 10;
    public static int ReviveCooldown = 10;
    public static int HeatCooldown = 10;
    public static int PowerPointCooldown = 10;
    public static int AbsorptionCooldown = 10;

    public static void init(File configFile)
    {
        config = new Configuration(configFile);

        try
        {
            config.load();

            // Capsules
            EnableCapsules = config.get(CAPSULES, "Enable Capsules", true).getBoolean(true);
            EnableCapsuleCooldowns = config.get(CAPSULES, "Enable Capsule Cooldowns", true, "All Cooldowns are in SECONDS").getBoolean(true);

            EnableMiscCapsule = config.get(CAPSULES, "Misc Capsules", true, "Misc Capsules: Revive, Heat, KO").getBoolean(true);
            MiscCapsuleMaxStack = config.get(CAPSULES, "Misc Capsule Max Stack Size", 16, "Max Stack Size per Misc Capsules").getInt(16);

            EnableKiCapsule = config.get(CAPSULES, "Ki Capsules", true, "Enable Ki Capsules").getBoolean(true);
            KiCapsuleCooldownType = config.get(CAPSULES, "Ki Capsule Cooldown", 0, "0 - Cooldowns are per Type [All Ki Capsules], 1 - Cooldowns are per Tier [Individual Ki Capsule Tier]").getInt(0);
            KiCapsuleMaxStack = config.get(CAPSULES, "Ki Capsule Max Stack Size", 16, "Max Stack Size per Ki Capsules").getInt(16);

            EnableHealthCapsule = config.get(CAPSULES, "Health Capsules", true, "Enable Health Capsule").getBoolean(true);
            HealthCapsuleCooldownType = config.get(CAPSULES, "Health Capsule Cooldown", 0, "0 - Cooldowns are per Type [All Health Capsules], 1 - Cooldowns are per Tier [Individual Health Capsule Tier]").getInt(0);
            HealthCapsuleMaxStack = config.get(CAPSULES, "Health Capsule Max Stack Size", 16, "Max Stack Size per Health Capsule").getInt(16);

            EnableStaminaCapsule = config.get(CAPSULES, "Stamina Capsules", true, "Enable Stamina Capsules").getBoolean(true);
            StaminaCapsuleCooldownType = config.get(CAPSULES, "Stamina Capsule Cooldown", 0, "0 - Cooldowns are per Type [All Stamina Capsules], 1 - Cooldowns are per Tier [Individual Stamina Capsule Tier]").getInt(0);
            StaminaCapsuleMaxStack = config.get(CAPSULES, "Stamina Capsule Max Stack Size", 16, "Max Stack Size per Stamina Capsule").getInt(16);

            EnableRegenCapsules = config.get(CAPSULES, "Regen Capsules", true, "Enable Regen Capsules").getBoolean(true);
            RegenCapsuleCooldownType = config.get(CAPSULES, "Regen Capsule Cooldown", 2, "0 - Cooldowns are per Type [All Regen Capsules], 1 - Cooldowns are per Tier [Individual Regen Capsule Type and Tier], 2 - Cooldowns are per Regen Type [HP, Ki and Stamina regens have separate cooldowns]").getInt(2);
            RegenCapsuleMaxStack = config.get(CAPSULES, "Regen Capsule Max Stack Size", 16, "Max Stack Size per Regen Capsule").getInt(16);

            KiCapsuleCooldownType = ValueUtil.clamp(KiCapsuleCooldownType, 0, 1);
            HealthCapsuleCooldownType = ValueUtil.clamp(HealthCapsuleCooldownType, 0, 1);
            StaminaCapsuleCooldownType = ValueUtil.clamp(StaminaCapsuleCooldownType, 0, 1);
            RegenCapsuleCooldownType = ValueUtil.clamp(RegenCapsuleCooldownType, 0, 2);

            KiCapsuleMaxStack = ValueUtil.clamp(KiCapsuleMaxStack, 1, 64);
            HealthCapsuleMaxStack = ValueUtil.clamp(HealthCapsuleMaxStack, 1, 64);
            StaminaCapsuleMaxStack = ValueUtil.clamp(StaminaCapsuleMaxStack, 1, 64);
            RegenCapsuleMaxStack = ValueUtil.clamp(RegenCapsuleMaxStack, 1, 64);

            // Ki Capsules
            KiBaseStrength = config.get(KI, "1. Base Ki Strength", 5).getInt(5);
            KiBaseCooldown = config.get(KI, "1. Base Ki Cooldown", 10).getInt(10);

            KiSuperStrength = config.get(KI, "2. Super Ki Strength", 10).getInt(10);
            KiSuperCooldown = config.get(KI, "2. Super Ki Cooldown", 10).getInt(10);

            KiMegaStrength = config.get(KI, "3. Mega Ki Strength", 25).getInt(25);
            KiMegaCooldown = config.get(KI, "3. Mega Ki Cooldown", 10).getInt(10);

            KiGigaStrength = config.get(KI, "4. Giga Ki Strength", 50).getInt(50);
            KiGigaCooldown = config.get(KI, "4. Giga Ki Cooldown", 10).getInt(10);

            KiSuperiorStrength = config.get(KI, "5. Superior Ki Strength", 75).getInt(75);
            KiSuperiorCooldown = config.get(KI, "5. Superior Ki Cooldown", 10).getInt(10);

            KiMasterStrength = config.get(KI, "6. Master Ki Strength", 100).getInt(100);
            KiMasterCooldown = config.get(KI, "6. Master Ki Cooldown", 10).getInt(10);

            // Health Capsules
            HealthBaseStrength = config.get(HEALTH, "1. Base Health Strength", 5).getInt(5);
            HealthBaseCooldown = config.get(HEALTH, "1. Base Health Cooldown", 10).getInt(10);

            HealthSuperStrength = config.get(HEALTH, "2. Super Health Strength", 10).getInt(10);
            HealthSuperCooldown = config.get(HEALTH, "2. Super Health Cooldown", 10).getInt(10);

            HealthMegaStrength = config.get(HEALTH, "3. Mega Health Strength", 25).getInt(25);
            HealthMegaCooldown = config.get(HEALTH, "3. Mega Health Cooldown", 10).getInt(10);

            HealthGigaStrength = config.get(HEALTH, "4. Giga Health Strength", 50).getInt(50);
            HealthGigaCooldown = config.get(HEALTH, "4. Giga Health Cooldown", 10).getInt(10);

            HealthSuperiorStrength = config.get(HEALTH, "5. Superior Health Strength", 75).getInt(75);
            HealthSuperiorCooldown = config.get(HEALTH, "5. Superior Health Cooldown", 10).getInt(10);

            HealthMasterStrength = config.get(HEALTH, "6. Master Health Strength", 100).getInt(100);
            HealthMasterCooldown = config.get(HEALTH, "6. Master Health Cooldown", 10).getInt(10);

            // Stamina Capsules
            StaminaBaseStrength = config.get(STAMINA, "1. Base Stamina Strength", 5).getInt(5);
            StaminaBaseCooldown = config.get(STAMINA, "1. Base Stamina Cooldown", 10).getInt(10);

            StaminaSuperStrength = config.get(STAMINA, "2. Super Stamina Strength", 10).getInt(10);
            StaminaSuperCooldown = config.get(STAMINA, "2. Super Stamina Cooldown", 10).getInt(10);

            StaminaMegaStrength = config.get(STAMINA, "3. Mega Stamina Strength", 25).getInt(25);
            StaminaMegaCooldown = config.get(STAMINA, "3. Mega Stamina Cooldown", 10).getInt(10);

            StaminaGigaStrength = config.get(STAMINA, "4. Giga Stamina Strength", 50).getInt(50);
            StaminaGigaCooldown = config.get(STAMINA, "4. Giga Stamina Cooldown", 10).getInt(10);

            StaminaSuperiorStrength = config.get(STAMINA, "5. Superior Stamina Strength", 75).getInt(75);
            StaminaSuperiorCooldown = config.get(STAMINA, "5. Superior Stamina Cooldown", 10).getInt(10);

            StaminaMasterStrength = config.get(STAMINA, "6. Master Stamina Strength", 100).getInt(100);
            StaminaMasterCooldown = config.get(STAMINA, "6. Master Stamina Cooldown", 10).getInt(10);

            // Regen capsules

            // HP Regeneration Settings
            RegenHPBaseStrength = (byte) config.getInt("1. Base HP Regen Strength", REGEN, 1, 1, Byte.MAX_VALUE, "");
            RegenHPBaseUseTime = config.getInt("1. Base HP Regen Use Time", REGEN, 10, 1, Integer.MAX_VALUE, "");
            RegenHPBaseCooldown = config.getInt("1. Base HP Regen Cooldown", REGEN, 10, 1, Integer.MAX_VALUE, "");

            RegenHPSuperStrength = (byte) config.getInt("2. Super HP Regen Strength", REGEN, 2, 1, Byte.MAX_VALUE, "");
            RegenHPSuperUseTime = config.getInt("2. Super HP Regen Use Time", REGEN, 10, 1, Integer.MAX_VALUE, "");
            RegenHPSuperCooldown = config.getInt("2. Super HP Regen Cooldown", REGEN, 20, 1, Integer.MAX_VALUE, "");

            RegenHPMegaStrength = (byte) config.getInt("3. Mega HP Regen Strength", REGEN, 3, 1, Byte.MAX_VALUE, "");
            RegenHPMegaUseTime = config.getInt("3. Mega HP Regen Use Time", REGEN, 10, 1, Integer.MAX_VALUE, "");
            RegenHPMegaCooldown = config.getInt("3. Mega HP Regen Cooldown", REGEN, 30, 1, Integer.MAX_VALUE, "");

// Ki Regeneration Settings
            RegenKiBaseStrength = (byte) config.getInt("4. Base Ki Regen Strength", REGEN, 1, 1, Byte.MAX_VALUE, "");
            RegenKiBaseUseTime = config.getInt("4. Base Ki Regen Use Time", REGEN, 10, 1, Integer.MAX_VALUE, "");
            RegenKiBaseCooldown = config.getInt("4. Base Ki Regen Cooldown", REGEN, 10, 1, Integer.MAX_VALUE, "");

            RegenKiSuperStrength = (byte) config.getInt("5. Super Ki Regen Strength", REGEN, 2, 1, Byte.MAX_VALUE, "");
            RegenKiSuperUseTime = config.getInt("5. Super Ki Regen Use Time", REGEN, 10, 1, Integer.MAX_VALUE, "");
            RegenKiSuperCooldown = config.getInt("5. Super Ki Regen Cooldown", REGEN, 20, 1, Integer.MAX_VALUE, "");

            RegenKiMegaStrength = (byte) config.getInt("6. Mega Ki Regen Strength", REGEN, 3, 1, Byte.MAX_VALUE, "");
            RegenKiMegaUseTime = config.getInt("6. Mega Ki Regen Use Time", REGEN, 10, 1, Integer.MAX_VALUE, "");
            RegenKiMegaCooldown = config.getInt("6. Mega Ki Regen Cooldown", REGEN, 30, 1, Integer.MAX_VALUE, "");

// Stamina Regeneration Settings
            RegenStaminaBaseStrength = (byte) config.getInt("7. Base Stamina Regen Strength", REGEN, 1, 1, Byte.MAX_VALUE, "");
            RegenStaminaBaseUseTime = config.getInt("7. Base Stamina Regen Use Time", REGEN, 10, 1, Integer.MAX_VALUE, "");
            RegenStaminaBaseCooldown = config.getInt("7. Base Stamina Regen Cooldown", REGEN, 10, 1, Integer.MAX_VALUE, "");

            RegenStaminaSuperStrength = (byte) config.getInt("8. Super Stamina Regen Strength", REGEN, 2, 1, Byte.MAX_VALUE, "");
            RegenStaminaSuperUseTime = config.getInt("8. Super Stamina Regen Use Time", REGEN, 10, 1, Integer.MAX_VALUE, "");
            RegenStaminaSuperCooldown = config.getInt("8. Super Stamina Regen Cooldown", REGEN, 20, 1, Integer.MAX_VALUE, "");

            RegenStaminaMegaStrength = (byte) config.getInt("9. Mega Stamina Regen Strength", REGEN, 3, 1, Byte.MAX_VALUE, "");
            RegenStaminaMegaUseTime = config.getInt("9. Mega Stamina Regen Use Time", REGEN, 10, 1, Integer.MAX_VALUE, "");
            RegenStaminaMegaCooldown = config.getInt("9. Mega Stamina Regen Cooldown", REGEN, 30, 1, Integer.MAX_VALUE, "");




            // Misc Capsules
            KOCooldown = config.get(MISC, "Ko Cooldown", 10).getInt(10);
            ReviveCooldown = config.get(MISC, "Revive Cooldown", 10).getInt(10);
            HeatCooldown = config.get(MISC, "Heat Cooldown", 10).getInt(10);
            PowerPointCooldown = config.get(MISC, "PowerPoint Cooldown", 10, "Only usable by Arcosians to restore PP Value").getInt(10);
            AbsorptionCooldown = config.get(MISC, "Absorption Cooldown", 10, "Only usable by Majins to restore absorption").getInt(10);

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
