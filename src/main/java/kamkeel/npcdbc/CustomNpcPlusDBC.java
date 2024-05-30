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
import kamkeel.npcdbc.controllers.*;
import kamkeel.npcdbc.items.ModItems;
import kamkeel.npcdbc.network.PacketHandler;
import net.minecraftforge.client.ForgeHooksClient;

import java.io.File;
import java.lang.reflect.Field;

@Mod(modid = CustomNpcPlusDBC.ID, name = CustomNpcPlusDBC.name, version = CustomNpcPlusDBC.version, dependencies = "required-after:customnpcs;required-after:jinryuujrmcore;required-after:jinryuudragonblockc;")
public class CustomNpcPlusDBC {

    public static final String name = "CustomNPC+ DBC Addon";
    public static final String version = "1.0-beta5.1";
    public static final String ID = "npcdbc";
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

        forceStencilEnable();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
        CapsuleController.getInstance().load();
        StatusEffectController.getInstance().load();
        BonusController.getInstance().load();
    }

    @Mod.EventHandler
    public void setAboutToStart(FMLServerAboutToStartEvent event) {
        FormController.getInstance().load();
        AuraController.getInstance().load();
        CapsuleController.getInstance().load();
        StatusEffectController.getInstance().load();
        BonusController.getInstance().load();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        PacketHandler.Instance = new PacketHandler();
        PacketHandler.Instance.register();
    }

    public static Side side() {
        return FMLCommonHandler.instance().getEffectiveSide();
    }

    public static void forceStencilEnable() {

        System.setProperty("forge.forceDisplayStencil", "true");

        Field field;

        try {

            field = ForgeHooksClient.class.getDeclaredField("stencilBits");

            field.setAccessible(true);

            field.setInt(ForgeHooksClient.class, 8);
        }
        catch (NoSuchFieldException | SecurityException e) {

            e.printStackTrace();
        }
        catch (IllegalArgumentException e) {

            e.printStackTrace();
        }
        catch (IllegalAccessException e) {

            e.printStackTrace();
        }
    }

    static Field field4 = null;

    public static int getStencilBits() {

        int r = -1;

        try {

            if(field4 == null) {

                field4 = ForgeHooksClient.class.getDeclaredField("stencilBits");
                field4.setAccessible(true);
                r = (int) field4.get(ForgeHooksClient.class);
            }
            else {

                r = (int) field4.get(ForgeHooksClient.class);
            }
        }
        catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {

            e.printStackTrace();
        }

        return r;
    }

}
