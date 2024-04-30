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
