package kamkeel.npcdbc.client;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import kamkeel.npcdbc.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;


public class ClientProxy extends CommonProxy {

    public static void eventsInit() {
        FMLCommonHandler.instance().bus().register(new ClientEvents());
        MinecraftForge.EVENT_BUS.register(new ClientEvents());

    }

    @Override
    public void preInit(FMLPreInitializationEvent ev) {
        super.preInit(ev);
        eventsInit();
    }

    public void init(FMLInitializationEvent ev) {
        super.init(ev);
        KeyHandler.registerKeys();
    }

    @Override
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return (ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : super.getPlayerEntity(ctx));
    }
}
