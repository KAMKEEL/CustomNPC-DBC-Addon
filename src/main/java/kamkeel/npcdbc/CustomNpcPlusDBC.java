package kamkeel.npcdbc;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.config.LoadConfiguration;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.items.ModItems;
import kamkeel.npcdbc.network.PacketHandler;

import java.io.File;

import static noppes.npcs.CustomNpcs.configPath;

@Mod(modid = CustomNpcPlusDBC.ID, name = CustomNpcPlusDBC.name, version = CustomNpcPlusDBC.version, dependencies = "after:customnpcs;after:jinryuujrmcore;after:jinryuudragonblockc;")
public class CustomNpcPlusDBC {

    public static final String name = "CustomNPC+ DBC Addon";
    public static final String version = "1.0";
    public static final String ID = "npcdbc";
    @SidedProxy(clientSide = "kamkeel.npcdbc.client.ClientProxy", serverSide = "kamkeel.npcdbc.CommonProxy")
    public static CommonProxy proxy;
    @Mod.Instance
    public static CustomNpcPlusDBC instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent ev) {
        proxy.preInit(ev);
        LoadConfiguration.init(configPath);
        ModItems.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void setAboutToStart(FMLServerAboutToStartEvent event) {
        FormController.getInstance().load();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        PacketHandler.Instance = new PacketHandler();
        PacketHandler.Instance.register();
    }

    public static Side side() {
        return FMLCommonHandler.instance().getEffectiveSide();
    }
}
