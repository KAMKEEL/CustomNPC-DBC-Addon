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

    public static Property HideInfoMessageProperty;
    public static boolean HideInfoMessage = false;


    public final static String GUI = "Gui";

    public static Property EnhancedGuiProperty;
    public static boolean EnhancedGui = true;
    public static Property DarkModeProperty;
    public static boolean DarkMode = true;

    public static Property AdvancedGuiModeProperty;
    public static boolean AdvancedGui = false;

    public final static String RENDERING = "Rendering";

    public static Property EnableHDTexturesProperty;
    public static boolean EnableHDTextures = false;

    public static Property RevampAuraProperty;
    public static boolean RevampAura = true;

    public static Property EnableOutlinesProperty;
    public static boolean EnableOutlines = true;
    public static Property EnableShadersProperty;
    public static boolean EnableShaders = true;
    public static Property EnableBloomProperty;
    public static boolean EnableBloom = true;

    public static void init(File configFile)
    {
        config = new Configuration(configFile);

        try
        {
            config.load();

            // General
            HideInfoMessageProperty = config.get(GENERAL, "Hide Info Messages", false, "Hides Change Form and other various transformation messages in game");
            HideInfoMessage = HideInfoMessageProperty.getBoolean(false);

            // GUI
            EnhancedGuiProperty = config.get(GUI, "Enable Enhanced Gui", true, "Uses DBC Addons GUI for Coloring and Manipulation");
            EnhancedGui = EnhancedGuiProperty.getBoolean(true);
            DarkModeProperty = config.get(GUI, "Dark Mode", true, "Uses Dark Mode GUI in Enhanced Menu");
            DarkMode = DarkModeProperty.getBoolean(true);
            AdvancedGuiModeProperty = config.get(GUI, "Advanced GUI", false, "Shows Advanced Status Effects and Calculations in Menu");
            AdvancedGui = AdvancedGuiModeProperty.getBoolean(false);

            // Rendering
            RevampAuraProperty = config.get(RENDERING, "Revamp Aura", true, "Renders with the new DBC Addon style of auras");
            RevampAura = RevampAuraProperty.getBoolean(true);

            EnableHDTexturesProperty = config.get(RENDERING, "Enable HD Textures", false, "Uses internal DBC Addon HD Textures");
            EnableHDTextures = EnableHDTexturesProperty.getBoolean(false);

            EnableOutlinesProperty = config.get(RENDERING, "Enable Outlines", true, "Enables outlines for players and NPCs");
            EnableOutlines = EnableOutlinesProperty.getBoolean(true);

            EnableShadersProperty = config.get(RENDERING, "Enable Shaders", true, "Enables the use of shaders when rendering");
            EnableShaders = EnableShadersProperty.getBoolean(true);

            EnableBloomProperty = config.get(RENDERING, "Enable Bloom", true, "Enables the bloom effect for player outlines and auras");
            EnableBloom = EnableBloomProperty.getBoolean(true);
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
