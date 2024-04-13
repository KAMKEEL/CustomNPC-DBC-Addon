package kamkeel.npcdbc;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import kamkeel.npcdbc.api.AbstractDBCAPI;
import kamkeel.npcdbc.events.ServerEvents;
import kamkeel.npcdbc.packets.PacketRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import noppes.npcs.scripted.NpcAPI;

public class CommonProxy {

    public static void eventsInit() {
        FMLCommonHandler.instance().bus().register(new ServerEvents());
        MinecraftForge.EVENT_BUS.register(new ServerEvents());
    }

    public void preInit(FMLPreInitializationEvent ev) {
        PacketRegistry.initPackets(ev.getSide());
        eventsInit();
    }

    public void init(FMLInitializationEvent ev) {
        NpcAPI.Instance().addGlobalObject("DBCAPI", AbstractDBCAPI.Instance());
    }

    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return ctx.getServerHandler().playerEntity;
    }
}
