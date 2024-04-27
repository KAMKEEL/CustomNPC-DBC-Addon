package kamkeel.npcdbc.config;

import cpw.mods.fml.common.FMLLog;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import noppes.npcs.util.ValueUtil;
import org.apache.logging.log4j.Level;

import java.io.File;

public class ConfigDBCEffects
{
    public static Configuration config;

    public final static String REGEN = "Regen";

    public static int HealthRegenPercent = 5;
    public static int KiRegenPercent = 5;
    public static int StaminaRegenPercent = 5;


    public static void init(File configFile)
    {
        config = new Configuration(configFile);

        try
        {
            config.load();

            HealthRegenPercent = config.get(REGEN, "Health Regen", 5, "Amount of Percent to restore per Level of Regen").getInt(5);
            KiRegenPercent = config.get(REGEN, "Ki Regen", 5, "Amount of Percent to restore per Level of Regen").getInt(5);
            StaminaRegenPercent = config.get(REGEN, "Stamina Regen", 5, "Amount of Percent to restore per Level of Regen").getInt(5);
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
