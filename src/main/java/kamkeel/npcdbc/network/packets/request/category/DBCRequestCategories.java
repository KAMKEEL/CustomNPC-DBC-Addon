package kamkeel.npcdbc.network.packets.request.category;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.sync.DBCSyncType;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.controllers.OutlineController;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketRequest;
import kamkeel.npcs.network.enums.SyncType;
import kamkeel.npcs.network.packets.data.large.ScrollDataPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.constants.EnumScrollData;

import java.io.IOException;
import java.util.Map;

public class DBCRequestCategories extends AbstractPacket {
    public static final String packetName = "NPC|CatList";

    private SyncType dbcType;

    public DBCRequestCategories(SyncType dbcType) {
        this.dbcType = dbcType;
    }

    public DBCRequestCategories() {
    }

    @Override
    public Enum getType() {
        return EnumPacketRequest.CategoryListRequest;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.REQUEST_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(dbcType.ordinal());
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        SyncType type = SyncType.byOrdinal(in.readInt());
        if (type == null) return;

        Map<String, Integer> catScrollData;

        if (type == DBCSyncType.FORM) {
            catScrollData = FormController.getInstance().getCategoryScrollData();
        } else if (type == DBCSyncType.AURA) {
            catScrollData = AuraController.getInstance().getCategoryScrollData();
        } else if (type == DBCSyncType.OUTLINE) {
            catScrollData = OutlineController.getInstance().getCategoryScrollData();
        } else {
            return;
        }

        ScrollDataPacket.sendScrollData((EntityPlayerMP) player, catScrollData, EnumScrollData.CATEGORY_LIST);
    }
}
