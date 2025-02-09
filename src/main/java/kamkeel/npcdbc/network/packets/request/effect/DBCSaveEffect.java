package kamkeel.npcdbc.network.packets.request.effect;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.data.statuseffect.custom.CustomEffect;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.EnumPacketRequest;
import kamkeel.npcs.util.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcsPermissions;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.PacketChannel;

import java.io.IOException;

import static kamkeel.npcdbc.network.DBCAddonPermissions.GLOBAL_DBCEFFECT;

public class DBCSaveEffect extends AbstractPacket {
    public static final String packetName = "NPC|SaveEffect";

    private String prevName;
    private NBTTagCompound effectCompound;

    public DBCSaveEffect(NBTTagCompound compound, String prev){
        this.effectCompound = compound;
        this.prevName = prev;
    }

    public DBCSaveEffect() {

    }

    @Override
    public Enum getType() {
        return EnumPacketRequest.EffectSave;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.REQUEST_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        ByteBufUtils.writeString(out, prevName);
        ByteBufUtils.writeNBT(out, effectCompound);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if(!CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCEFFECT))
            return;

        String prevName = ByteBufUtils.readString(in);

        CustomEffect effect = new CustomEffect();
        effect.readFromNBT(ByteBufUtils.readNBT(in));

        StatusEffectController.getInstance().saveEffect(effect);

        if(!prevName.isEmpty() && !prevName.equals(effect.name)){
            StatusEffectController.getInstance().deleteEffectFile(prevName);
        }

        NetworkUtility.sendCustomEffectDataAll((EntityPlayerMP) player);
    }
}
