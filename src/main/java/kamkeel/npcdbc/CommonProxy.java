package kamkeel.npcdbc;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.api.AbstractDBCAPI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import noppes.npcs.scripted.NpcAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommonProxy {
    public static final Logger LOGGER = LogManager.getLogger(CustomNpcPlusDBC.ID);
    public static EntityPlayer CurrentAuraPlayer = null;

    private static EntityPlayer CurrentJRMCTickPlayer = null;
    @SideOnly(Side.CLIENT)
    private static EntityPlayer CurrentJRMCTickPlayerClient;

    public static EntityPlayer getCurrentJRMCTickPlayer() {
        if(FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            return CurrentJRMCTickPlayerClient;
        } else {
            return CurrentJRMCTickPlayer;
        }
    }

    public static void setCurrentJRMCTickPlayer(EntityPlayer player) {
        if(FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            CurrentJRMCTickPlayerClient = player;
        } else {
            CurrentJRMCTickPlayer = player;
        }
    }

    public static void eventsInit() {
        ServerEventHandler handler = new ServerEventHandler();
        FMLCommonHandler.instance().bus().register(handler);
        MinecraftForge.EVENT_BUS.register(handler);
    }

    public void preInit(FMLPreInitializationEvent ev) {
        eventsInit();
    }

    public void init(FMLInitializationEvent ev) {
        NpcAPI.Instance().addGlobalObject("DBCAPI", AbstractDBCAPI.Instance());
    }

    public void postInit(FMLPostInitializationEvent ev) {
    }

    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return ctx.getServerHandler().playerEntity;
    }

    public EntityPlayer getClientPlayer() {
        return null;
    }

    public World getClientWorld() {
        return null;
    }

    public void registerItem(Item item) { }

    public int getNewRenderId() {
        return -1;
    }

    public boolean isRenderingGUI() {
        return false;
    }
}
