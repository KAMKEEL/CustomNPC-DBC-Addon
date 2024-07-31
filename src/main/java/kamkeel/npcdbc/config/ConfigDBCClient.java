package kamkeel.npcdbc.config;

import kamkeel.npcdbc.client.ClientProxy;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class ConfigDBCClient {
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

    public static Property FirstPerson3DAuraOpacityProperty;
    public static int FirstPerson3DAuraOpacity = 100;

    public static Property ModernWheelTextureProperty;
    public static boolean ModernWheelTexture = true;

    public static void init(File configFile) {
        config = new Configuration(configFile);

        try {
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
            ModernWheelTextureProperty = config.get(GUI, "Use Modern Wheel GUI Texture", true, "Uses modern texture for Wheel GUIs");
            ModernWheelTexture = ModernWheelTextureProperty.getBoolean(true);

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

            FirstPerson3DAuraOpacityProperty = config.get(RENDERING, "First person 3D Aura Opacity", 100, "The opacity of the first person 3D Aura." + "\nModifying this makes it so auras on other players render normally without blinding you" + "\n(Min: 0, Max: 100)");
            FirstPerson3DAuraOpacity = Math.max(Math.min(100, FirstPerson3DAuraOpacityProperty.getInt(100)), 0);
        } catch (Exception e) {
            ClientProxy.LOGGER.error("Error loading client configuration: " + e.getMessage());
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }
}
