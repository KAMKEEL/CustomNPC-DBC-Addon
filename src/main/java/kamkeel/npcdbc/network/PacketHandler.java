package kamkeel.npcdbc.network;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.network.packets.*;
import kamkeel.npcdbc.network.packets.aura.*;
import kamkeel.npcdbc.network.packets.form.*;
import kamkeel.npcdbc.network.packets.outline.DBCGetOutline;
import kamkeel.npcdbc.network.packets.outline.DBCRemoveOutline;
import kamkeel.npcdbc.network.packets.outline.DBCRequestOutline;
import kamkeel.npcdbc.network.packets.outline.DBCSaveOutline;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.WorldServer;

import java.util.Hashtable;
import java.util.Map;

public final class PacketHandler {
    public static PacketHandler Instance;

    public Map<String, AbstractPacket> map = new Hashtable<>();
    public Map<String, FMLEventChannel> channels = new Hashtable<>();

    public PacketHandler() {
        map.put(PingPacket.packetName, new PingPacket());
        map.put(TransformPacket.packetName, new TransformPacket());
        map.put(DBCSetValPacket.packetName, new DBCSetValPacket());
        map.put(DBCInfoSync.packetName, new DBCInfoSync());
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

        map.put(SaveFormCustomization.packetName, new SaveFormCustomization());
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

    public void sendToTrackingPlayers(Entity entity, FMLProxyPacket packet) {
        if (packet != null && CustomNpcPlusDBC.side() == Side.SERVER) {
            EntityTracker tracker = ((WorldServer) entity.worldObj).getEntityTracker();
            tracker.func_151248_b(entity, packet); // Send packet to tracking players
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
