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
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

import static kamkeel.npcdbc.network.packets.request.category.DBCCategorySave.*;

public class DBCCategoryMoveItem extends AbstractPacket {
    public static final String packetName = "NPC|CatMoveItem";

    private SyncType dbcType;
    private int itemId;
    private int catId;

    public DBCCategoryMoveItem(SyncType dbcType, int itemId, int catId) {
        this.dbcType = dbcType;
        this.itemId = itemId;
        this.catId = catId;
    }

    public DBCCategoryMoveItem() {
    }

    @Override
    public Enum getType() {
        return EnumPacketRequest.CategoryItemMove;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.REQUEST_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(dbcType.ordinal());
        out.writeInt(itemId);
        out.writeInt(catId);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        SyncType type = SyncType.byOrdinal(in.readInt());
        int itemId = in.readInt();
        int catId = in.readInt();
        if (type == null) return;

        if (!hasPermission(player, type)) return;

        if (type == DBCSyncType.FORM) {
            FormController.getInstance().moveItemToCategory(itemId, catId);
        } else if (type == DBCSyncType.AURA) {
            AuraController.getInstance().moveItemToCategory(itemId, catId);
        } else if (type == DBCSyncType.OUTLINE) {
            OutlineController.getInstance().moveItemToCategory(itemId, catId);
        }
    }
}
