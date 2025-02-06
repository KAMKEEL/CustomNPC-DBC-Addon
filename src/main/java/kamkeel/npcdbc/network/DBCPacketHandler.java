package kamkeel.npcdbc.network;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.network.packets.*;
import kamkeel.npcdbc.network.packets.aura.*;
import kamkeel.npcdbc.network.packets.form.*;
import kamkeel.npcdbc.network.packets.outline.DBCGetOutline;
import kamkeel.npcdbc.network.packets.outline.DBCRemoveOutline;
import kamkeel.npcdbc.network.packets.outline.DBCRequestOutline;
import kamkeel.npcdbc.network.packets.outline.DBCSaveOutline;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import noppes.npcs.LogWriter;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public final class DBCPacketHandler {
    public static DBCPacketHandler Instance;

    public Map<String, AbstractPacket> map = new Hashtable<>();
    public Map<String, FMLEventChannel> channels = new Hashtable<>();

    public DBCPacketHandler() {
        map.put(PingPacket.packetName, new PingPacket());
        map.put(TransformPacket.packetName, new TransformPacket());
        map.put(DBCSetValPacket.packetName, new DBCSetValPacket());
        map.put(DBCInfoSyncPacket.packetName, new DBCInfoSyncPacket());
        map.put(DBCSelectForm.packetName, new DBCSelectForm());
        map.put(DBCSelectAura.packetName, new DBCSelectAura());
        map.put(DBCSetAura.packetName, new DBCSetAura());
        map.put(DBCRequestAura.packetName, new DBCRequestAura());
        map.put(DBCGetAura.packetName, new DBCGetAura());
        map.put(DBCRequestForm.packetName, new DBCRequestForm());
        map.put(DBCGetForm.packetName, new DBCGetForm());
        map.put(CapsuleInfo.packetName, new CapsuleInfo());
        map.put(LoginInfo.packetName, new LoginInfo());
        map.put(SendChat.packetName, new SendChat());
        map.put(PlaySound.packetName, new PlaySound());
        map.put(StopSound.packetName, new StopSound());
        map.put(DBCSaveForm.packetName, new DBCSaveForm());
        map.put(DBCRemoveForm.packetName, new DBCRemoveForm());
        map.put(DBCSetFlight.packetName, new DBCSetFlight());
        map.put(DBCRemoveAura.packetName, new DBCRemoveAura());
        map.put(DBCSaveAura.packetName, new DBCSaveAura());

        map.put(DBCRequestOutline.packetName, new DBCRequestOutline());
        map.put(DBCRemoveOutline.packetName, new DBCRemoveOutline());
        map.put(DBCGetOutline.packetName, new DBCGetOutline());
        map.put(DBCSaveOutline.packetName, new DBCSaveOutline());
        map.put(DBCUpdateLockOn.packetName, new DBCUpdateLockOn());

        map.put(DBCRequestFormWheel.packetName, new DBCRequestFormWheel());
        map.put(DBCSaveFormWheel.packetName, new DBCSaveFormWheel());
        map.put(TurboPacket.packetName, new TurboPacket());
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


    /**
     * Sends every FMLProxyPacket produced by packet.generatePackets()
     */
    private void sendAllPackets(AbstractPacket packet, SendAction action) {
        // get all generated FMLProxyPacket objects (could be 1 for normal, or many for large)
        List<FMLProxyPacket> proxyPackets = packet.generatePackets();
        if (proxyPackets.isEmpty()) {
            LogWriter.error("Warning: No packets generated for " + packet.getClass().getName());
        }

        for (FMLProxyPacket proxy : proxyPackets) {
            action.send(proxy);
        }
    }

    // ------------------------------------------------------------------------
    // Public API methods for sending

    public void sendToPlayer(AbstractPacket packet, EntityPlayerMP player) {
        FMLEventChannel eventChannel = channels.get(packet.getChannel());
        if (eventChannel == null) {
            LogWriter.error("Error: Event channel is null for packet: " + packet.getClass().getName());
            return;
        }
        sendAllPackets(packet, p -> eventChannel.sendTo(p, player));
    }

    public void sendToServer(AbstractPacket packet) {
        FMLEventChannel eventChannel = channels.get(packet.getChannel());
        if (eventChannel == null) {
            LogWriter.error("Error: Event channel is null for packet: " + packet.getClass().getName());
            return;
        }
        sendAllPackets(packet, eventChannel::sendToServer);
    }

    public void sendToAll(AbstractPacket packet) {
        FMLEventChannel eventChannel = channels.get(packet.getChannel());
        if (eventChannel == null) {
            LogWriter.error("Error: Event channel is null for packet: " + packet.getClass().getName());
            return;
        }
        sendAllPackets(packet, eventChannel::sendToAll);
    }

    public void sendToDimension(AbstractPacket packet, final int dimensionId) {
        FMLEventChannel eventChannel = channels.get(packet.getChannel());
        if (eventChannel == null) {
            LogWriter.error("Error: Event channel is null for packet: " + packet.getClass().getName());
            return;
        }
        sendAllPackets(packet, p -> eventChannel.sendToDimension(p, dimensionId));
    }

    public void sendToTrackingPlayers(final Entity entity, AbstractPacket packet) {
        FMLEventChannel eventChannel = channels.get(packet.getChannel());
        if (eventChannel == null) {
            LogWriter.error("Error: Event channel is null for packet: " + packet.getClass().getName());
            return;
        }
        final NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 60);
        sendAllPackets(packet, p -> eventChannel.sendToAllAround(p, point));
    }

    // Simple functional interface to unify the "send" action
    private interface SendAction {
        void send(FMLProxyPacket proxy);
    }
}
