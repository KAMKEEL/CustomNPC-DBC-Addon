package kamkeel.npcdbc.network;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.network.packets.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;

import java.util.Hashtable;
import java.util.Map;

public final class PacketHandler {
    public static PacketHandler Instance;

    public Map<String, AbstractPacket> map = new Hashtable<>();
    public Map<String, FMLEventChannel> channels = new Hashtable<>();

    public PacketHandler() {
        map.put(PingPacket.packetName, new PingPacket());
        map.put(TransformPacket.packetName, new TransformPacket());
        map.put(AuraPacket.packetName, new AuraPacket());
        map.put(DBCSetValPacket.packetName, new DBCSetValPacket());
        map.put(DBCInfoSync.packetName, new DBCInfoSync());
        map.put(DBCSelectForm.packetName, new DBCSelectForm());
        map.put(AuraPacket.packetName, new AuraPacket());
        this.register();
    }

    public void register() {
        FMLEventChannel eventChannel;
        for (String channel : map.keySet()) {
            eventChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(channel);
            eventChannel.register(this);
            channels.put(channel, eventChannel);
        }
    }

    @SubscribeEvent
    public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent event) {
        try {
            map.get(event.packet.channel()).receiveData(event.packet.payload(), ((NetHandlerPlayServer) event.handler).playerEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent event) {
        try {
            map.get(event.packet.channel()).receiveData(event.packet.payload(), CustomNpcPlusDBC.proxy.getClientPlayer());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendToPlayer(FMLProxyPacket packet, EntityPlayerMP player) {
        if (packet != null && CustomNpcPlusDBC.side() == Side.SERVER) {
            channels.get(packet.channel()).sendTo(packet, player);
        }
    }

    public void sendToServer(FMLProxyPacket packet) {
        if (packet != null) {
            packet.setTarget(Side.SERVER);
            channels.get(packet.channel()).sendToServer(packet);
        }
    }

    public void sendAround(Entity entity, double range, FMLProxyPacket packet) {
        if (packet != null && CustomNpcPlusDBC.side() == Side.SERVER) {
            channels.get(packet.channel()).sendToAllAround(packet, new NetworkRegistry.TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, range));
        }
    }

    public void sendToAll(FMLProxyPacket packet) {
        if (packet != null && CustomNpcPlusDBC.side() == Side.SERVER) {
            channels.get(packet.channel()).sendToAll(packet);
        }
    }
}
