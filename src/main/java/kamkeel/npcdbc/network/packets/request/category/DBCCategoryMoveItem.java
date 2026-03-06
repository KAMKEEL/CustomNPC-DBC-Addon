package kamkeel.npcdbc.network.packets.request.category;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.constants.DBCSyncType;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.controllers.OutlineController;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketRequest;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

import static kamkeel.npcdbc.network.packets.request.category.DBCCategorySave.*;

public class DBCCategoryMoveItem extends AbstractPacket {
    public static final String packetName = "NPC|CatMoveItem";

    private int dbcType;
    private int itemId;
    private int catId;

    public DBCCategoryMoveItem(int dbcType, int itemId, int catId) {
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
        out.writeInt(dbcType);
        out.writeInt(itemId);
        out.writeInt(catId);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        int type = in.readInt();
        int itemId = in.readInt();
        int catId = in.readInt();

        if (!hasPermission(player, type)) return;

        switch (type) {
            case DBCSyncType.FORM:
                FormController.getInstance().moveItemToCategory(itemId, catId);
                break;
            case DBCSyncType.AURA:
                AuraController.getInstance().moveItemToCategory(itemId, catId);
                break;
            case DBCSyncType.OUTLINE:
                OutlineController.getInstance().moveItemToCategory(itemId, catId);
                break;
        }
    }
}
