package kamkeel.npcdbc.client;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import kamkeel.npcdbc.CommonProxy;
import kamkeel.npcdbc.client.render.AuraRenderer;
import kamkeel.npcdbc.client.render.PotaraItemRenderer;
import kamkeel.npcdbc.client.render.RenderEventHandler;
import kamkeel.npcdbc.client.shader.PostProcessing;
import kamkeel.npcdbc.client.shader.ShaderHelper;
import kamkeel.npcdbc.entity.EntityAura;
import kamkeel.npcdbc.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;


public class ClientProxy extends CommonProxy {
    public static int lastRendererGUIPlayerID = -1;

    public static void eventsInit() {
        FMLCommonHandler.instance().bus().register(new ClientEventHandler());
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());

        FMLCommonHandler.instance().bus().register(new RenderEventHandler());
        MinecraftForge.EVENT_BUS.register(new RenderEventHandler());
    }

    @Override
    public void preInit(FMLPreInitializationEvent ev) {
        super.preInit(ev);
        forceStencilEnable();
    }

    public void init(FMLInitializationEvent ev) {
        super.init(ev);
        eventsInit();
        KeyHandler.registerKeys();
        RenderingRegistry.registerEntityRenderingHandler(EntityAura.class, new AuraRenderer());
        MinecraftForgeClient.registerItemRenderer(ModItems.Potaras, new PotaraItemRenderer());
        ShaderHelper.loadShaders(false);
        ClientConstants.startTime = Instant.now();


    }

    public void postInit(FMLPostInitializationEvent ev) {
        PostProcessing.init(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
      //  ModernModels.loadModels();
    }

    public static float getTimeSinceStart() {
        return Duration.between(ClientConstants.startTime, Instant.now()).toMillis() / 1000f;
    }

    @Override
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return (ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : super.getPlayerEntity(ctx));
    }

    @Override
    public EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getMinecraft().theWorld;
    }

    @Override
    public int getNewRenderId() {
        return RenderingRegistry.getNextAvailableRenderId();
    }

    @Override
    public void registerItem(Item item) {
    }

    public boolean isRenderingGUI() {
        return ClientConstants.renderingGUI;
    }

    public static boolean isRenderingWorld(){
        return ClientConstants.renderingWorld;
    }

    private void forceStencilEnable() {
        try {
            System.setProperty("forge.forceDisplayStencil", "true");
            Field field = ForgeHooksClient.class.getDeclaredField("stencilBits");
            field.setAccessible(true);
            field.setInt(ForgeHooksClient.class, 8);
        } catch (Exception e) {
            LOGGER.error("Failed setting stencil bits to 8: " + e.getMessage());
        }
    }
}
