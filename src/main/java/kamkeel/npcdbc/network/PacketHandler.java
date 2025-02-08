package kamkeel.npcdbc.network;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.LogWriter;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.config.ConfigMain;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class PacketHandler {
    public static PacketHandler Instance;

//    public Map<String, AbstractPacket> map = new Hashtable<>();
//    public Map<String, FMLEventChannel> channels = new Hashtable<>();

    public Map<EnumChannelType, FMLEventChannel> channels = new Hashtable<>();

    public static final PacketChannel REQUEST_PACKETS = new PacketChannel("NPCDBC|REQUEST", EnumChannelType.REQUEST);
    public static final PacketChannel GET_PACKETS = new PacketChannel("NPCDBC|GET", EnumChannelType.GET);
    public static final PacketChannel PLAYER_PACKETS = new PacketChannel("NPCDBC|PLAYER", EnumChannelType.PLAYER);;


    public static final List<PacketChannel> packetChannels = new ArrayList<>();

    public PacketHandler() {
        packetChannels.add(REQUEST_PACKETS);
        packetChannels.add(GET_PACKETS);

        this.registerRequestPackets();
        this.registerGetPackets();
        this.registerPlayerPackets();
//        map.put(PingPacket.packetName, new PingPacket());
//        map.put(TransformPacket.packetName, new TransformPacket());
//        map.put(DBCSetValPacket.packetName, new DBCSetValPacket());
//        map.put(DBCInfoSync.packetName, new DBCInfoSync());
//        map.put(DBCSelectForm.packetName, new DBCSelectForm());
//        map.put(DBCSelectAura.packetName, new DBCSelectAura());
//        map.put(DBCSetAura.packetName, new DBCSetAura());
//        map.put(DBCRequestAura.packetName, new DBCRequestAura());
//        map.put(DBCGetAura.packetName, new DBCGetAura());
//        map.put(DBCRequestForm.packetName, new DBCRequestForm());
//        map.put(DBCGetForm.packetName, new DBCGetForm());
//        map.put(CapsuleInfo.packetName, new CapsuleInfo());
//        map.put(LoginInfo.packetName, new LoginInfo());
//        map.put(SendChat.packetName, new SendChat());
//        map.put(PlaySound.packetName, new PlaySound());
//        map.put(StopSound.packetName, new StopSound());
//        map.put(DBCSaveForm.packetName, new DBCSaveForm());
//        map.put(DBCRemoveForm.packetName, new DBCRemoveForm());
//        map.put(DBCSetFlight.packetName, new DBCSetFlight());
//        map.put(DBCRemoveAura.packetName, new DBCRemoveAura());
//        map.put(DBCSaveAura.packetName, new DBCSaveAura());
//
//        map.put(DBCRequestOutline.packetName, new DBCRequestOutline());
//        map.put(DBCRemoveOutline.packetName, new DBCRemoveOutline());
//        map.put(DBCGetOutline.packetName, new DBCGetOutline());
//        map.put(DBCSaveOutline.packetName, new DBCSaveOutline());
//        map.put(DBCUpdateLockOn.packetName, new DBCUpdateLockOn());
//
//        map.put(DBCSaveEffect.packetName, new DBCSaveEffect());
//        map.put(DBCRemoveEffect.packetName, new DBCRemoveEffect());
//        map.put(DBCGetEffect.packetName, new DBCGetEffect());
//        map.put(DBCRequestEffect.packetName, new DBCRequestEffect());
//
//        map.put(DBCRequestFormWheel.packetName, new DBCRequestFormWheel());
//        map.put(DBCSaveFormWheel.packetName, new DBCSaveFormWheel());
//        map.put(TurboPacket.packetName, new TurboPacket());
//
//        map.put(SaveFormCustomization.packetName, new SaveFormCustomization());
//        this.register();
    }

    private void registerPlayerPackets() {
    }

    private void registerGetPackets() {
    }

    private void registerRequestPackets() {
    }

    public void registerChannels() {
        for (PacketChannel channel : packetChannels) {
            FMLEventChannel eventChannel =
                NetworkRegistry.INSTANCE.newEventDrivenChannel(channel.getChannelName());
            eventChannel.register(this);
            channels.put(channel.getChannelType(), eventChannel);
        }
    }

    public PacketChannel getPacketChannel(EnumChannelType type) {
        return packetChannels.stream()
            .filter(channel -> channel.getChannelType() == type)
            .findFirst()
            .orElse(null);
    }

    public FMLEventChannel getEventChannel(AbstractPacket abstractPacket){
        PacketChannel packetChannel = getPacketChannel(abstractPacket.getChannel().getChannelType());
        if (packetChannel == null) {
            return null;
        }
        return channels.get(packetChannel.getChannelType());
    }

    @SubscribeEvent
    public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent event) {
        handlePacket(event.packet, ((NetHandlerPlayServer) event.handler).playerEntity, Side.SERVER);
    }

    @SubscribeEvent
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent event) {
        handlePacket(event.packet, CustomNpcs.proxy.getPlayer(), Side.CLIENT);
    }

    private void handlePacket(FMLProxyPacket packet, EntityPlayer player, Side side) {
        ByteBuf buf = packet.payload();
        try {
            int packetTypeOrdinal = buf.readInt();
            EnumChannelType packetType = EnumChannelType.values()[packetTypeOrdinal];

            PacketChannel packetChannel = getPacketChannel(packetType);
            if (packetChannel == null) {
                LogWriter.error("Error: Packet channel is null for packet type: " + packetType);
                return;
            }

            int packetId = buf.readInt();
            AbstractPacket abstractPacket = packetChannel.packets.get(packetId);
            if (abstractPacket == null) {
                LogWriter.error("Error: Abstract packet is null for packet ID: " + packetId);
                return;
            }

            if(side == Side.SERVER){
                if(abstractPacket.getChannel() == REQUEST_PACKETS && ConfigMain.OpsOnly && !NoppesUtilServer.isOp(player)){
                    LogWriter.error(String.format("%s tried to use CNPC+ without being an op", player.getCommandSenderName()));
                    return;
                }

                // Check if permission is allowed
                if(abstractPacket.getPermission() != null && !CustomNpcsPermissions.hasPermission(player, abstractPacket.getPermission())){
                    return;
                }

                // Check for required NPC
                EntityNPCInterface npc = NoppesUtilServer.getEditingNpc(player);
                if(abstractPacket.needsNPC() && npc == null){
                    return;
                }

                abstractPacket.setNPC(npc);
            }

            // Let the packet parse the rest
            abstractPacket.receiveData(buf, player);

        } catch (IndexOutOfBoundsException e) {
            LogWriter.error("Error: IndexOutOfBoundsException in handlePacket: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            LogWriter.error("Error: Exception in handlePacket: " + e.getMessage());
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
        FMLEventChannel eventChannel = getEventChannel(packet);
        if (eventChannel == null) {
            LogWriter.error("Error: Event channel is null for packet: " + packet.getClass().getName());
            return;
        }
        sendAllPackets(packet, p -> eventChannel.sendTo(p, player));
    }

    public void sendToServer(AbstractPacket packet) {
        FMLEventChannel eventChannel = getEventChannel(packet);
        if (eventChannel == null) {
            LogWriter.error("Error: Event channel is null for packet: " + packet.getClass().getName());
            return;
        }
        sendAllPackets(packet, eventChannel::sendToServer);
    }

    public void sendToAll(AbstractPacket packet) {
        FMLEventChannel eventChannel = getEventChannel(packet);
        if (eventChannel == null) {
            LogWriter.error("Error: Event channel is null for packet: " + packet.getClass().getName());
            return;
        }
        sendAllPackets(packet, eventChannel::sendToAll);
    }

    public void sendToDimension(AbstractPacket packet, final int dimensionId) {
        FMLEventChannel eventChannel = getEventChannel(packet);
        if (eventChannel == null) {
            LogWriter.error("Error: Event channel is null for packet: " + packet.getClass().getName());
            return;
        }
        sendAllPackets(packet, p -> eventChannel.sendToDimension(p, dimensionId));
    }

    public void sendTracking(AbstractPacket packet, final Entity entity) {
        FMLEventChannel eventChannel = getEventChannel(packet);
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
