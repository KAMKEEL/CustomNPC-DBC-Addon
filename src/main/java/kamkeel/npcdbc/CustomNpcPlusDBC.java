package kamkeel.npcdbc;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.config.LoadConfiguration;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.BonusController;
import kamkeel.npcdbc.controllers.CapsuleController;
import kamkeel.npcdbc.controllers.DBCEffectController;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.controllers.OutlineController;
import kamkeel.npcdbc.data.DBCProfileData;
import kamkeel.npcdbc.data.attribute.DBCItemAttributes;
import kamkeel.npcdbc.items.ModItems;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.constants.DBCScriptType;
import kamkeel.npcs.controllers.ProfileController;
import noppes.npcs.constants.ScriptContext;
import noppes.npcs.controllers.ScriptHookController;

import java.io.File;

@Mod(
    modid = CustomNpcPlusDBC.ID,
    name = CustomNpcPlusDBC.name,
    version = CustomNpcPlusDBC.version,
    dependencies = "required-after:customnpcs;required-after:jinryuujrmcore;required-after:jinryuudragonblockc;"
)
public class CustomNpcPlusDBC {

    public static final String name = "CustomNPC+ DBC Addon";
    public static final String ID = "npcdbc";
    public static final String version = "1.1.5";

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

        // Register DBC player hooks so handler-based GUIs include them
        if (ScriptHookController.Instance != null) {
            ScriptHookController.Instance.registerHooks(ScriptContext.PLAYER, DBCScriptType.FORMCHANGE.function,
                    DBCScriptType.DAMAGED.function, DBCScriptType.CAPSULEUSED.function,
                    DBCScriptType.SENZUUSED.function, DBCScriptType.REVIVED.function,
                    DBCScriptType.KNOCKOUT.function);
        }
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
