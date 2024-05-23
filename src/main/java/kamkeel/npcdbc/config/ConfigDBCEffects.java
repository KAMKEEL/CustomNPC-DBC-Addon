package kamkeel.npcdbc.config;

import cpw.mods.fml.common.FMLLog;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;

import java.io.File;

public class ConfigDBCEffects
{
    public static Configuration config;

    public final static String REGEN = "Regen";

    public static int HealthRegenPercent = 5;
    public static int KiRegenPercent = 5;
    public static int StaminaRegenPercent = 5;

    public final static String NAMEK_REGEN = "Namekian Regen";
    public static int NamekRegenPercent = 3;

    public final static String FRUITOFMIGHT = "Fruit of Might";
    public static double FOM_Strength = 1.2f;
    public static double FOM_Dex = 0.5f;
    public static double FOM_Will = 0.5f;
    public static int FOM_EffectLength = 90;
    public static double FOM_KiDrain = -0.8f;


    public final static String ZENKAI = "Zenkai";
    public static double ZenkaiSaiyanStr = 1.0f;
    public static double ZenkaiSaiyanDex = 1.0f;
    public static double ZenkaiSaiyanWil = 1.0f;
    public static int ZenkaiSaiyanLength = 360;

    public static double ZenkaiHALFStr = 1.0f;
    public static double ZenkaiHALFDex = 1.0f;
    public static double ZenkaiHALFWil = 1.0f;
    public static int ZenkaiHALFLength = 180;

    public final static String Meditation = "Meditation";
    public static int MeditationSpiBoostPercent = 20;


    public final static String Potara = "Potara";
    public static double TierOneMulti = 0.5f;
    public static double TierTwoMulti = 0.7f;
    public static double TierThreeMulti = 1.0f;

    public static void init(File configFile)
    {
        config = new Configuration(configFile);

        try
        {
            config.load();

            HealthRegenPercent = config.get(REGEN, "Health Regen", 5, "Amount of Percent to restore per Level of Regen").getInt(5);
            KiRegenPercent = config.get(REGEN, "Ki Regen", 5, "Amount of Percent to restore per Level of Regen").getInt(5);
            StaminaRegenPercent = config.get(REGEN, "Stamina Regen", 5, "Amount of Percent to restore per Level of Regen").getInt(5);

            NamekRegenPercent = config.get(NAMEK_REGEN, "Namek Regen", 3, "Namekian Regen is removed once the player reaches the specified amount in Gameplay Config").getInt(3);

            FOM_Strength = config.get(FRUITOFMIGHT, "Strength Multi", 1.2, "Amount added to Strength Multi").getDouble(1.2);
            FOM_Dex = config.get(FRUITOFMIGHT, "Dex Multi", 0.5, "Amount added to Dex Multi").getDouble(0.5);
            FOM_Will = config.get(FRUITOFMIGHT, "Will Multi", 0.5, "Amount added to Will Multi").getDouble(0.5);
            FOM_EffectLength = config.get(FRUITOFMIGHT, "Effect Time", 90, "Ki Drain Percent per Second").getInt(90);
            FOM_KiDrain = config.get(FRUITOFMIGHT, "Effect Drain", -0.8, "Ki Drain Percent per Second").getDouble(-0.8);

            config.addCustomCategoryComment(ZENKAI,
                "Zenkai will occur when a Saiyan or Half Saiyan dies. This can be disabled in\n" +
                        "the DBC Gameplay Config. If the Zenaki effect is given to a none Saiyan. It will\n" +
                         "utilitze the Half Saiyan Config values.");
            ZenkaiSaiyanStr = config.get(ZENKAI, "Saiyan Strength Multi", 1.2f, "Amount added to Strength Multi").getDouble(1.0f);
            ZenkaiSaiyanDex = config.get(ZENKAI, "Saiyan Dex Multi", 1.2f, "Amount added to Dex Multi").getDouble(1.0f);
            ZenkaiSaiyanWil = config.get(ZENKAI, "Saiyan Will Multi", 1.2f, "Amount added to Will Multi").getDouble(1.0f);
            ZenkaiSaiyanLength = config.get(ZENKAI, "Saiyan Zenkai Length", 360, "Time of Zenkai in Seconds").getInt(360);

            ZenkaiHALFStr = config.get(ZENKAI, "Half Saiyan Strength Multi", 1.2f, "Amount added to Strength Multi").getDouble(1.0f);
            ZenkaiHALFDex = config.get(ZENKAI, "Half Saiyan Dex Multi", 1.2f, "Amount added to Dex Multi").getDouble(1.0f);
            ZenkaiHALFWil = config.get(ZENKAI, "Half Saiyan Will Multi", 1.2f, "Amount added to Will Multi").getDouble(1.0f);
            ZenkaiHALFLength = config.get(ZENKAI, "Half Saiyan Zenkai Length", 180, "Time of Zenkai in Seconds").getInt(180);

            config.addCustomCategoryComment(Meditation,
                "Meditation Spirit Bonus is always added AFTER all other calculations");
            MeditationSpiBoostPercent = config.get(Meditation, "Meditation Boost", 20, "Amount of percent to multiply Base Spirit Stats by").getInt(20);


            config.addCustomCategoryComment(Potara,
                "If the Potara Status Effect Multi is set to 0 or below, then the Status Effect will not be applied");
            TierOneMulti = config.get(Potara, "Tier 1 Multi", 0.5f).getDouble(0.5f);
            TierTwoMulti = config.get(Potara, "Tier 2 Multi", 0.7f).getDouble(0.7f);
            TierThreeMulti = config.get(Potara, "Tier 3 Multi", 1.0f).getDouble(1.0f);
        }
        catch (Exception e)
        {
            FMLLog.log(Level.ERROR, e, "DBC Addon has had a problem loading its status effect configuration");
        }
        finally
        {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }
}
