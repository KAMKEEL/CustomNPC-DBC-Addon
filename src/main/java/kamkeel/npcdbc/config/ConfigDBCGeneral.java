package kamkeel.npcdbc.config;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import kamkeel.npcdbc.client.ClientCache;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;

import java.io.File;

public class ConfigDBCGeneral {
    public static Configuration config;

    public final static String NPC = "NPC";
    private static final String FORM_REMOVAL = "FORM REMOVAL";
    public final static String CHARACTER_RESET = "CHARACTER RESET";
    public final static String DISCORD_BUTTON = "DISCORD BUTTON";
    public static boolean DISPLAY_BY_DEFAULT = false;
    public static boolean STATS_BY_DEFAULT = false;
    public static boolean AURAS_CLEAR_ON_RESET = true;
    public static boolean FORMS_CLEAR_ON_RESET = true;
    public static boolean FORM_MASTERIES_CLEAR_ON_RESET = true;
    public static boolean FORM_MASTERIES_CLEAR_ON_REMOVE = true;

    private final static String defaultDiscordURL = "https://discord.com/invite/pQqRTvFeJ5";
    public static String discordURL = null;


    public static void init(File configFile) {
        config = new Configuration(configFile);

        try {
            config.load();

            DISPLAY_BY_DEFAULT = config.get(NPC, "DBC Display Enabled by Default", false).getBoolean(false);
            STATS_BY_DEFAULT = config.get(NPC, "DBC Stats Enabled by Default", false).getBoolean(false);

            AURAS_CLEAR_ON_RESET = config.get(CHARACTER_RESET, "Should Custom Auras be removed on character reset?", true).getBoolean(true);
            FORMS_CLEAR_ON_RESET = config.get(CHARACTER_RESET, "Should Custom Forms be removed on character reset?", true).getBoolean(true);
            FORM_MASTERIES_CLEAR_ON_RESET = config.get(CHARACTER_RESET, "Should CF Masteries be removed on character reset?", true).getBoolean(true);

            FORM_MASTERIES_CLEAR_ON_REMOVE = config.get(FORM_REMOVAL, "Should custom form mastery be removed on form removal unless specified otherwise in a script?", true).getBoolean(true);

            discordURL = config.getString("URL", DISCORD_BUTTON, defaultDiscordURL, "Discord URL for the button in the stat sheet. If it's empty it will show the Addon's discord");
        } catch (Exception e) {
            FMLLog.log(Level.ERROR, e, "DBC Addon has had a problem loading its general configuration");
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    public static String getDiscordURL() {
        String cachedDiscordURL = FMLCommonHandler.instance().getEffectiveSide().isClient() ? ClientCache.discordURL : ConfigDBCGeneral.discordURL;
        if (cachedDiscordURL == null || cachedDiscordURL.trim().isEmpty()) {
            return defaultDiscordURL;
        }

        return cachedDiscordURL;
    }
}
