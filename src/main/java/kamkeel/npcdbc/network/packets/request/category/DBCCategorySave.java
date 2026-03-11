package kamkeel.npcdbc.network.packets.request.category;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.controllers.OutlineController;
import kamkeel.npcdbc.constants.DBCSyncType;
import noppes.npcs.controllers.data.Category;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketRequest;
import kamkeel.npcs.network.packets.data.large.ScrollDataPacket;
import kamkeel.npcs.util.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.constants.EnumScrollData;

import java.io.IOException;
import java.util.Map;

import static kamkeel.npcdbc.network.DBCAddonPermissions.*;

public class DBCCategorySave extends AbstractPacket {
    public static final String packetName = "NPC|CatSave";

    private int dbcType;
    private NBTTagCompound categoryNBT;

    public DBCCategorySave(int dbcType, NBTTagCompound categoryNBT) {
        this.dbcType = dbcType;
        this.categoryNBT = categoryNBT;
    }

    public DBCCategorySave() {
    }

    @Override
    public Enum getType() {
        return EnumPacketRequest.CategorySave;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.REQUEST_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(dbcType);
        ByteBufUtils.writeNBT(out, categoryNBT);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        int type = in.readInt();
        NBTTagCompound nbt = ByteBufUtils.readNBT(in);

        if (!hasPermission(player, type)) return;

        Category cat = new Category();
        cat.readNBT(nbt);

        Map<String, Integer> catScrollData;

        switch (type) {
            case DBCSyncType.FORM:
                if (cat.id <= 0) {
                    FormController.getInstance().createCategory(cat.title);
                } else {
                    FormController.getInstance().saveCategory(cat);
                }
                catScrollData = FormController.getInstance().getCategoryScrollData();
                break;
            case DBCSyncType.AURA:
                if (cat.id <= 0) {
                    AuraController.getInstance().createCategory(cat.title);
                } else {
                    AuraController.getInstance().saveCategory(cat);
                }
                catScrollData = AuraController.getInstance().getCategoryScrollData();
                break;
            case DBCSyncType.OUTLINE:
                if (cat.id <= 0) {
                    OutlineController.getInstance().createCategory(cat.title);
                } else {
                    OutlineController.getInstance().saveCategory(cat);
                }
                catScrollData = OutlineController.getInstance().getCategoryScrollData();
                break;
            default:
                return;
        }

        ScrollDataPacket.sendScrollData((EntityPlayerMP) player, catScrollData, EnumScrollData.CATEGORY_LIST);
    }

    static boolean hasPermission(EntityPlayer player, int type) {
        switch (type) {
            case DBCSyncType.FORM: return CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCFORM);
            case DBCSyncType.AURA: return CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCAURA);
            case DBCSyncType.OUTLINE: return CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCOUTLINE);
        }
        return false;
    }
}
