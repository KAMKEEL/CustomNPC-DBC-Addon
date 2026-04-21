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
import kamkeel.npcs.network.packets.data.large.ScrollDataPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.constants.EnumScrollData;
import noppes.npcs.controllers.TagController;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class DBCRequestCategoryItems extends AbstractPacket {
    public static final String packetName = "NPC|CatItems";

    private int dbcType;
    private int catId;

    public DBCRequestCategoryItems(int dbcType, int catId) {
        this.dbcType = dbcType;
        this.catId = catId;
    }

    public DBCRequestCategoryItems() {
    }

    @Override
    public Enum getType() {
        return EnumPacketRequest.CategoryItemsRequest;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.REQUEST_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(dbcType);
        out.writeInt(catId);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        int type = in.readInt();
        int catId = in.readInt();

        Map<String, Integer> items;
        HashMap<String, HashSet<UUID>> tagMap = null;
        switch (type) {
            case DBCSyncType.FORM:
                items = FormController.getInstance().getItemsByCategoryScrollData(catId);
                tagMap = FormController.getInstance().getItemTagMapForCategory(catId);
                break;
            case DBCSyncType.AURA:
                items = AuraController.getInstance().getItemsByCategoryScrollData(catId);
                tagMap = AuraController.getInstance().getItemTagMapForCategory(catId);
                break;
            case DBCSyncType.OUTLINE:
                items = OutlineController.getInstance().getItemsByCategoryScrollData(catId);
                tagMap = OutlineController.getInstance().getItemTagMapForCategory(catId);
                break;
            default:
                return;
        }

        ScrollDataPacket.sendScrollData((EntityPlayerMP) player, items, EnumScrollData.CATEGORY_GROUP);
        if (tagMap != null) {
            TagController.sendCategoryTagMap((EntityPlayerMP) player, tagMap);
        }
    }
}
