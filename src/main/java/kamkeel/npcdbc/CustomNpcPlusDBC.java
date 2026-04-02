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
import kamkeel.npcdbc.api.event.IDBCEvent;
import kamkeel.npcdbc.controllers.sync.DBCSyncType;
import kamkeel.npcdbc.data.race.races.Android;
import kamkeel.npcdbc.data.race.races.BioAndroid;
import noppes.npcs.controllers.APIRegistry;
import kamkeel.npcdbc.config.LoadConfiguration;
import kamkeel.npcdbc.data.ability.DBCAbilities;
import kamkeel.npcdbc.constants.DBCAnimations;
import kamkeel.npcdbc.constants.DBCScriptType;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.BonusController;
import kamkeel.npcdbc.controllers.CapsuleController;
import kamkeel.npcdbc.controllers.DBCEffectController;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.controllers.OutlineController;
import kamkeel.npcdbc.controllers.RaceController;
import kamkeel.npcdbc.controllers.SkillController;
import kamkeel.npcdbc.data.DBCProfileData;
import kamkeel.npcdbc.data.ability.DBCAbilityExtender;
import kamkeel.npcdbc.data.energy.DBCEnergyExtender;
import kamkeel.npcdbc.data.ability.DBCConditions;
import kamkeel.npcdbc.data.attribute.DBCItemAttributes;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.items.ModItems;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcs.controllers.AbilityController;
import kamkeel.npcs.controllers.EnergyController;
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
    public static final String version = "1.2.1";

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

        // Register ability extender for DBC damage routing and lifecycle hooks
        AbilityController.Instance.registerExtender(new DBCAbilityExtender());

        // Register energy handler for DBC damage routing on script-created energy entities
        EnergyController.Instance.registerExtender(new DBCEnergyExtender());

        // Register DBC flight checker so abilities don't pull flying players down
        AbilityController.Instance.registerFlightChecker(player -> {
            DBCData data = DBCData.get(player);
            return data != null && data.isFlying;
        });

        // Prevent fusion spectators from activating any ability
        AbilityController.Instance.registerActivationChecker(player -> !DBCData.get(player).isFusionSpectator());

        // Register DBC player hooks so handler-based GUIs include them
        if (ScriptHookController.Instance != null) {
            ScriptContext.PLAYER.addNamespace("IDBCEvent");

            ScriptHookController.Instance.registerHook(ScriptContext.PLAYER, DBCScriptType.FORMCHANGE.function, IDBCEvent.FormChangeEvent.class);
            ScriptHookController.Instance.registerHook(ScriptContext.PLAYER, DBCScriptType.DAMAGED.function, IDBCEvent.DamagedEvent.class);
            ScriptHookController.Instance.registerHook(ScriptContext.PLAYER, DBCScriptType.CAPSULEUSED.function, IDBCEvent.CapsuleUsedEvent.class);
            ScriptHookController.Instance.registerHook(ScriptContext.PLAYER, DBCScriptType.SENZUUSED.function, IDBCEvent.SenzuUsedEvent.class);
            ScriptHookController.Instance.registerHook(ScriptContext.PLAYER, DBCScriptType.REVIVED.function, IDBCEvent.DBCReviveEvent.class);
            ScriptHookController.Instance.registerHook(ScriptContext.PLAYER, DBCScriptType.KNOCKOUT.function, IDBCEvent.DBCKnockout.class);
            ScriptHookController.Instance.registerHook(ScriptContext.PLAYER, DBCScriptType.SKILL_EVENT.function, IDBCEvent.SkillEvent.class);
            ScriptHookController.Instance.registerHook(ScriptContext.PLAYER, DBCScriptType.ANDROID_PART.function, IDBCEvent.AndroidPartEvent.class);
        }

        DBCConditions.register();
        DBCAnimations.register();
        DBCAbilities.register();

        // Force-load builtin race definitions so their static fields
        // register into RaceRegistry before we push entries to RaceController.
        BioAndroid.init();
        Android.init();

        APIRegistry.Instance.register("DBC Addon API", "https://kamkeel.github.io/CustomNPC-DBC-Addon/");
    }

    @Mod.EventHandler
    public void setAboutToStart(FMLServerAboutToStartEvent event) {
        FormController.getInstance().load();
        AuraController.getInstance().load();
        CapsuleController.getInstance().load();
        BonusController.getInstance().load();
        OutlineController.getInstance().load();
        SkillController.Instance.load();
        RaceController.getInstance().load();
        DBCSyncType.load();
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
