package kamkeel.npcdbc;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import kamkeel.npcdbc.controllers.FormController;

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
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void setAboutToStart(FMLServerAboutToStartEvent event) {
        new FormController();
    }
}
