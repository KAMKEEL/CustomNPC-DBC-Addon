package kamkeel.npcdbc.network.packets.effect;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.data.statuseffect.CustomEffect;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.NetworkUtility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.Server;

import java.io.IOException;

import static kamkeel.npcdbc.network.DBCAddonPermissions.GLOBAL_DBCAURA;
import static kamkeel.npcdbc.network.DBCAddonPermissions.GLOBAL_DBCEFFECT;

public class DBCSaveEffect extends AbstractPacket {
    public static final String packetName = "NPC|SaveEffect";

    private String prevName;
    private NBTTagCompound effect;

    public DBCSaveEffect(NBTTagCompound compound, String prev){
        this.effect = compound;
        this.prevName = prev;
    }

    public DBCSaveEffect() {

    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        Server.writeString(out, prevName);
        Server.writeNBT(out, effect);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if(!CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCEFFECT))
            return;

        String prevName = Server.readString(in);
        if(!prevName.isEmpty()){
            StatusEffectController.getInstance().deleteEffectFile(prevName);
        }
        CustomEffect effect = new CustomEffect();
        effect.readFromNBT(Server.readNBT(in));
        StatusEffectController.getInstance().saveEffect(effect);
        NetworkUtility.sendCustomEffectDataAll((EntityPlayerMP) player);
    }
}
