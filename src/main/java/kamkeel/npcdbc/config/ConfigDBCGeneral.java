package kamkeel.npcdbc.config;

import cpw.mods.fml.common.FMLLog;
import net.minecraftforge.common.config.Configuration;
import noppes.npcs.util.ValueUtil;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class ConfigDBCGeneral
{
    public static Configuration config;

    public final static String NPC = "NPC";
    public static boolean DISPLAY_BY_DEFAULT = false;
    public static boolean STATS_BY_DEFAULT = false;


    public static void init(File configFile)
    {
        config = new Configuration(configFile);

        try
        {
            config.load();

            DISPLAY_BY_DEFAULT = config.get(NPC, "DBC Display Enabled by Default", false).getBoolean(false);
            STATS_BY_DEFAULT = config.get(NPC, "DBC Stats Enabled by Default", false).getBoolean(false);
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
