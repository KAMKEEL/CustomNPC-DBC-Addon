package kamkeel.npcdbc.network.packets.player;

import JinRyuu.JRMCore.entity.EntityEnergyAtt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.mixins.late.impl.dbc.IEntityEnergyAttAccessor;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.io.IOException;

public final class KiAttackColorSyncPacket extends AbstractPacket {

    private int entityId;
    private int color;
    private int color2;

    public KiAttackColorSyncPacket() {
    }

    public KiAttackColorSyncPacket(int entityId, int color, int color2) {
        this.entityId = entityId;
        this.color = color;
        this.color2 = color2;
    }

    @Override
    public Enum getType() {
        return EnumPacketPlayer.KiAttackColorSync;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.PLAYER_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(entityId);
        out.writeInt(color);
        out.writeInt(color2);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        int id = in.readInt();
        int col = in.readInt();
        int col2 = in.readInt();

        if (!Utility.isServer())
            applyClient(id, col, col2);
    }

    @SideOnly(Side.CLIENT)
    private static void applyClient(int entityId, int color, int color2) {
        World world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;

        Entity entity = world.getEntityByID(entityId);
        if (entity instanceof EntityEnergyAtt) {
            ((IEntityEnergyAttAccessor) entity).npcdbc$setColor(color);
            ((IEntityEnergyAttAccessor) entity).npcdbc$setColor2(color2);
        }
    }

    public static void sendToTracking(Entity kiAttack, int color, int color2) {
        if (kiAttack == null) return;
        DBCPacketHandler.Instance.sendTracking(
            new KiAttackColorSyncPacket(kiAttack.getEntityId(), color, color2),
            kiAttack
        );
    }
}
