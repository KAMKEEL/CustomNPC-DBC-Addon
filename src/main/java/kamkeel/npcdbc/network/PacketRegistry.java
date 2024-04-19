package kamkeel.npcdbc.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.network.packets.PacketSyncData;
import kamkeel.npcdbc.network.packets.PacketTellClient;
import kamkeel.npcdbc.network.packets.PacketTellServer;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;

public class PacketRegistry {

    public static SimpleNetworkWrapper dispatcherC;
    public static SimpleNetworkWrapper dispatcherS;

    private static int packetId = 0;

    public static void initPackets(Side side) {

        dispatcherC = NetworkRegistry.INSTANCE.newSimpleChannel("CNPCDBCtellC");
        dispatcherS = NetworkRegistry.INSTANCE.newSimpleChannel("CNPCDBCtellS");

        registerMessage(PacketTellClient.class);

        registerMessage(PacketTellServer.class);

        registerMessage(PacketSyncData.class);

    }

    private static final <T extends AbstractMessage<T> & IMessageHandler<T, IMessage>> void registerMessage(Class<T> clazz) {
        if (AbstractMessage.AbstractClientMessage.class.isAssignableFrom(clazz)) {
            dispatcherC.registerMessage(clazz, clazz, ++packetId, Side.CLIENT);
        } else if (AbstractMessage.AbstractServerMessage.class.isAssignableFrom(clazz)) {
            dispatcherS.registerMessage(clazz, clazz, ++packetId, Side.SERVER);
        } else {
            dispatcherC.registerMessage(clazz, clazz, ++packetId, Side.CLIENT);
            dispatcherS.registerMessage(clazz, clazz, ++packetId, Side.SERVER);
        }
    }


    public static void tellClient(EntityPlayer player, String id) {
        tellClient(player, new PacketTellClient(player, id));
    }

    public static void syncData(Entity e, String id, NBTTagCompound data) {
        if (Utility.isServer(e)) {
            sendToAllTracking(e, new PacketSyncData(e, id, data));
        } else
            tellServer(new PacketSyncData(e, id, data));
    }

    public static void sendToAll(IMessage message) {
        dispatcherC.sendToAll(message);
    }

    // send this packet to all players tracking this entity
    public static void sendToAllTracking(Entity e, IMessage message) {
        EntityTracker et = ((WorldServer) e.worldObj).getEntityTracker();
        et.func_151248_b(e, dispatcherC.getPacketFrom(message));
    }

    public static void tellClient(EntityPlayer player, IMessage message) {
        dispatcherC.sendTo(message, (EntityPlayerMP) player);
    }

    public static final void tellServer(IMessage message) {
        dispatcherS.sendToServer(message);
    }

    public static final void tellServer(String info) {
        dispatcherS.sendToServer(new PacketTellServer(info));
    }

}
