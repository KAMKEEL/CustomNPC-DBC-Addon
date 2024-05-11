package kamkeel.npcdbc.config;

import cpw.mods.fml.common.FMLLog;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.Level;

import java.io.File;

public class ConfigDBCClient
{
    public static Configuration config;

    public final static String GENERAL = "General";

    public static Property EnableHDTexturesProperty;
    public static boolean EnableHDTextures = false;

    public static Property HideInfoMessageProperty;
    public static boolean HideInfoMessage = false;


    public final static String GUI = "Gui";

    public static Property EnhancedGuiProperty;
    public static boolean EnhancedGui = true;
    public static Property DarkModeProperty;
    public static boolean DarkMode = true;

    public static Property AdvancedGuiModeProperty;
    public static boolean AdvancedGui = false;

    public static void init(File configFile)
    {
        config = new Configuration(configFile);

        try
        {
            config.load();

            // General
            EnableHDTexturesProperty = config.get(GENERAL, "Enable HD Textures", true, "Uses internal DBC Addon HD Textures");
            EnableHDTextures = EnableHDTexturesProperty.getBoolean(true);

            HideInfoMessageProperty = config.get(GENERAL, "Hide Info Messages", false, "Hides Change Form and other various transformation messages in game");
            HideInfoMessage = HideInfoMessageProperty.getBoolean(false);

            EnhancedGuiProperty = config.get(GUI, "Enable Enhanced Gui", true, "Uses DBC Addons GUI for Coloring and Manipulation");
            EnhancedGui = EnhancedGuiProperty.getBoolean(true);
            DarkModeProperty = config.get(GUI, "Dark Mode", true, "Uses Dark Mode GUI in Enhanced Menu");
            DarkMode = DarkModeProperty.getBoolean(true);
            AdvancedGuiModeProperty = config.get(GUI, "Advanced GUI", false, "Shows Advanced Status Effects and Calculations in Menu");
            AdvancedGui = AdvancedGuiModeProperty.getBoolean(false);
        }
        catch (Exception e)
        {
            FMLLog.log(Level.ERROR, e, "DBC Addon has had a problem loading its client configuration");
        }
        finally
        {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }
}
