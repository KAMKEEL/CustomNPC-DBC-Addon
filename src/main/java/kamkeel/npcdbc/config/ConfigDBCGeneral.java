package kamkeel.npcdbc.config;

import cpw.mods.fml.common.FMLLog;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;

import java.io.File;

public class ConfigDBCGeneral
{
    public static Configuration config;

    public final static String NPC = "NPC";
    public final static String CHARACTER_RESET = "CHARACTER RESET";
    public static boolean DISPLAY_BY_DEFAULT = false;
    public static boolean STATS_BY_DEFAULT = false;
    public static boolean AURAS_CLEAR_ON_RESET = true;
    public static boolean FORMS_CLEAR_ON_RESET = true;
    public static boolean FORM_MASTERIES_CLEAR_ON_RESET = true;


    public static void init(File configFile)
    {
        config = new Configuration(configFile);

        try
        {
            config.load();

            DISPLAY_BY_DEFAULT = config.get(NPC, "DBC Display Enabled by Default", false).getBoolean(false);
            STATS_BY_DEFAULT = config.get(NPC, "DBC Stats Enabled by Default", false).getBoolean(false);

            AURAS_CLEAR_ON_RESET = config.get(CHARACTER_RESET, "Should Custom Auras be removed on character reset?", true).getBoolean(true);
            FORMS_CLEAR_ON_RESET = config.get(CHARACTER_RESET, "Should Custom Forms be removed on character reset?", true).getBoolean(true);
            FORM_MASTERIES_CLEAR_ON_RESET = config.get(CHARACTER_RESET, "Should CF Masteries be removed on character reset?", true).getBoolean(true);
        }
        catch (Exception e)
        {
            FMLLog.log(Level.ERROR, e, "DBC Addon has had a problem loading its general configuration");
        }
        finally
        {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }
}
