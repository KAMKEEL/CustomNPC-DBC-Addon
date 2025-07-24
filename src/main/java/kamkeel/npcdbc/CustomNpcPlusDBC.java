package kamkeel.npcdbc;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.config.LoadConfiguration;
import kamkeel.npcdbc.controllers.*;
import kamkeel.npcdbc.data.DBCProfileData;
import kamkeel.npcdbc.data.attribute.DBCItemAttributes;
import kamkeel.npcdbc.items.ModItems;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcs.controllers.ProfileController;

import java.io.File;

@Mod(
    modid = CustomNpcPlusDBC.ID,
    name = CustomNpcPlusDBC.name,
    version = CustomNpcPlusDBC.version, // If this errors out, try refreshing your project entirely
    dependencies = "required-after:customnpcs;required-after:jinryuujrmcore;required-after:jinryuudragonblockc;"
)
public class CustomNpcPlusDBC {

    public static final String name = "CustomNPC+ DBC Addon";
    public static final String ID = "npcdbc";
    public static final String version = "1.1.2-beta3";
    @SidedProxy(clientSide = "kamkeel.npcdbc.client.ClientProxy", serverSide = "kamkeel.npcdbc.CommonProxy")
    public static CommonProxy proxy;
    @Mod.Instance
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


}
