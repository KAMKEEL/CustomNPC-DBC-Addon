package kamkeel.npcdbc;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.relauncher.Side;
import io.github.somehussar.janinoloader.api.IDynamicCompiler;
import io.github.somehussar.janinoloader.api.IDynamicCompilerBuilder;
import io.github.somehussar.janinoloader.api.delegates.LoadClassCondition;
import kamkeel.npcdbc.config.LoadConfiguration;
import kamkeel.npcdbc.controllers.*;
import kamkeel.npcdbc.data.DBCProfileData;
import kamkeel.npcdbc.data.attribute.DBCItemAttributes;
import kamkeel.npcdbc.items.ModItems;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcs.controllers.ProfileController;
import noppes.npcs.CustomNpcs;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Mod(
    modid = CustomNpcPlusDBC.ID,
    name = CustomNpcPlusDBC.name,
    version = CustomNpcPlusDBC.version,
    dependencies = "required-after:customnpcs;required-after:jinryuujrmcore;required-after:jinryuudragonblockc;"
)
public class CustomNpcPlusDBC {

    public static final String name = "CustomNPC+ DBC Addon";
    public static final String ID = "npcdbc";
    public static final String version = "1.1.4";

    @SidedProxy(clientSide = "kamkeel.npcdbc.client.ClientProxy", serverSide = "kamkeel.npcdbc.CommonProxy")
    public static CommonProxy proxy;

    public CustomNpcPlusDBC() {
        instance = this;
    }

    public static CustomNpcPlusDBC instance;
    public static String addonConfig;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent ev) {
        proxy.preInit(ev);
        addonConfig = ev.getModConfigurationDirectory() + File.separator + "CustomNpcPlus" + File.separator + "dbc" + File.separator;
        LoadConfiguration.init(addonConfig);
        ModItems.init();

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
        CapsuleController.getInstance().load();
        BonusController.getInstance().load();
        DBCEffectController.getInstance().load();

        new DBCItemAttributes();
    }

    @Mod.EventHandler
    public void setAboutToStart(FMLServerAboutToStartEvent event) {
        FormController.getInstance().load();
        AuraController.getInstance().load();
        CapsuleController.getInstance().load();
        BonusController.getInstance().load();
        OutlineController.getInstance().load();
    }

    @Mod.EventHandler
    public void setAboutToStart(FMLServerStartingEvent event) {
        ProfileController.registerProfileType(new DBCProfileData());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);

        DBCPacketHandler.Instance = new DBCPacketHandler();
        DBCPacketHandler.Instance.registerChannels();
    }

    public static Side side() {
        return FMLCommonHandler.instance().getEffectiveSide();
    }


    private static IDynamicCompiler jls;

    public static IDynamicCompiler getClientCompiler() {

        if (jls == null) {
            Set<String> allowedClasses = new HashSet<>();
            allowedClasses.add("kamkeel.npcdbc.client.utils.Color");
            allowedClasses.add("kamkeel.npcdbc.data.overlay.OverlayScript.OverlayFunctions");
            allowedClasses.add("java.io.Serializable");
            allowedClasses.add("java.util.Iterator");

            List<String> allowedRegexList = Arrays.asList(
                "kamkeel\\.npcdbc\\..*",
                "noppes\\.npcs\\..*"
            );

            List<String> bannedRegexList = Arrays.asList(
                ".*ClassLoader.*"
            );


            List<Pattern> allowedWildCards = allowedRegexList.stream().map(Pattern::compile).collect(Collectors.toList());
            List<Pattern> bannedWildCards = bannedRegexList.stream().map(Pattern::compile).collect(Collectors.toList());
            LoadClassCondition filter = (name)-> {
                if (bannedWildCards.stream().anyMatch(pattern -> pattern.matcher(name).matches()))
                    return false;

                if (name.startsWith("java.lang")) return true;

                if (allowedClasses.contains(name)) return true;

                return allowedWildCards.stream().anyMatch(pattern -> pattern.matcher(name).matches());

            }; //anything that's NOT java.lang is blacklisted
            jls = IDynamicCompilerBuilder.createBuilder().setClassFilter(filter).getCompiler();
        }

        return jls;
    }


}
