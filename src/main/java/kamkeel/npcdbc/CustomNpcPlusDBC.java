package kamkeel.npcdbc;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "npcdbc",
    name = "CustomNPC+ DBC Addon",
    version = "1.0",
    dependencies = "after:customnpcs;after:jinryuujrmcore;after:jinryuudragonblockc;")
public class CustomNpcPlusDBC {

    @SidedProxy(clientSide = "kamkeel.npcdbc.client.ClientProxy", serverSide = "kamkeel.npcdbc.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance
    public static CustomNpcPlusDBC instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent ev) {
        proxy.preInit(ev);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event){
        proxy.init(event);
    }
}
