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

import static kamkeel.npcdbc.network.packets.request.category.DBCCategorySave.*;

public class DBCCategoryRemove extends AbstractPacket {
    public static final String packetName = "NPC|CatRemove";

    private int dbcType;
    private int catId;

    public DBCCategoryRemove(int dbcType, int catId) {
        this.dbcType = dbcType;
        this.catId = catId;
    }

    public DBCCategoryRemove() {
    }

    @Override
    public Enum getType() {
        return EnumPacketRequest.CategoryRemove;
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

        if (!hasPermission(player, type)) return;

        Map<String, Integer> catScrollData;

        switch (type) {
            case DBCSyncType.FORM:
                FormController.getInstance().removeCategory(catId);
                catScrollData = FormController.getInstance().getCategoryScrollData();
                break;
            case DBCSyncType.AURA:
                AuraController.getInstance().removeCategory(catId);
                catScrollData = AuraController.getInstance().getCategoryScrollData();
                break;
            case DBCSyncType.OUTLINE:
                OutlineController.getInstance().removeCategory(catId);
                catScrollData = OutlineController.getInstance().getCategoryScrollData();
                break;
            default:
                return;
        }

        ScrollDataPacket.sendScrollData((EntityPlayerMP) player, catScrollData, EnumScrollData.CATEGORY_LIST);
    }
}
