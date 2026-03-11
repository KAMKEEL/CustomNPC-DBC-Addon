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

import java.io.IOException;
import java.util.Map;

public class DBCRequestCategories extends AbstractPacket {
    public static final String packetName = "NPC|CatList";

    private int dbcType;

    public DBCRequestCategories(int dbcType) {
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
        out.writeInt(dbcType);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        int type = in.readInt();

        Map<String, Integer> catScrollData;

        switch (type) {
            case DBCSyncType.FORM:
                catScrollData = FormController.getInstance().getCategoryScrollData();
                break;
            case DBCSyncType.AURA:
                catScrollData = AuraController.getInstance().getCategoryScrollData();
                break;
            case DBCSyncType.OUTLINE:
                catScrollData = OutlineController.getInstance().getCategoryScrollData();
                break;
            default:
                return;
        }

        ScrollDataPacket.sendScrollData((EntityPlayerMP) player, catScrollData, EnumScrollData.CATEGORY_LIST);
    }
}
