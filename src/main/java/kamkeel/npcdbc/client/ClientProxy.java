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
import kamkeel.npcdbc.data.ability.DBCAbilityFieldProvider;
import kamkeel.npcdbc.data.race.registry.RaceRegistry;
import kamkeel.npcdbc.entity.EntityAura;
import kamkeel.npcdbc.items.ModItems;
import kamkeel.npcs.controllers.AbilityController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityCustomNpc;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;


public class ClientProxy extends CommonProxy {
    public static int lastRendererGUIPlayerID = -1;
    public static EntityCustomNpc currentlyDrawnNPC = null;

    public static void eventsInit() {
        FMLCommonHandler.instance().bus().register(new ClientEventHandler());
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());

        FMLCommonHandler.instance().bus().register(new RenderEventHandler());
        MinecraftForge.EVENT_BUS.register(new RenderEventHandler());
    }

    @Override
    public void preInit(FMLPreInitializationEvent ev) {
        super.preInit(ev);
        CustomNpcs.addClassesToClientClassFilter(filter -> {
            filter.addRegexes("kamkeel\\.npcdbc\\.api\\..*");
        });
    }

    public void init(FMLInitializationEvent ev) {
        super.init(ev);
        eventsInit();
        KeyHandler.registerKeys();

        // Register DBC ability field providers for GUI tab injection
        AbilityController.Instance.registerFieldProvider(new DBCAbilityFieldProvider());
        RenderingRegistry.registerEntityRenderingHandler(EntityAura.class, new AuraRenderer());
        MinecraftForgeClient.registerItemRenderer(ModItems.Potaras, new PotaraItemRenderer());
        ShaderHelper.loadShaders(false);
        ClientConstants.startTime = Instant.now();

        RaceRegistry.registerClient();
    }

    public void postInit(FMLPostInitializationEvent ev) {
        PostProcessing.init(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        //  ModernModels.loadModels();

        Collection renderManager = RenderManager.instance.entityRenderMap.values();
        for (Object o : renderManager) {
            if (o instanceof RendererLivingEntity) {
                ModelBase mainModel = ((RendererLivingEntity) o).mainModel;
                CNPCAnimationHelper.setOriginalValues(mainModel);
            }
        }
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

    public static boolean isRenderingWorld() {
        return ClientConstants.renderingWorld;
    }
}
