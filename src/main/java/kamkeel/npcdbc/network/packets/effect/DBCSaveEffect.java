package kamkeel.npcdbc.network.packets.effect;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import kamkeel.npcdbc.data.statuseffect.custom.CustomEffect;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.NetworkUtility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.Server;

import java.io.IOException;

import static kamkeel.npcdbc.network.DBCAddonPermissions.GLOBAL_DBCEFFECT;

public class DBCSaveEffect extends AbstractPacket {
    public static final String packetName = "NPC|SaveEffect";

    private String prevName;
    private NBTTagCompound effect;
    private int id;

    public DBCSaveEffect(NBTTagCompound compound, int id, String prev){
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
        out.writeInt(id);
        Server.writeNBT(out, effect);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if(!CustomNpcsPermissions.hasPermission(player, GLOBAL_DBCEFFECT))
            return;

        String prevName = Server.readString(in);
        int id = in.readInt();
        if (id < 200)
            return;
        if(!prevName.isEmpty()){
            StatusEffectController.getInstance().deleteEffectFile(prevName);
        }
        CustomEffect effect = (CustomEffect) StatusEffectController.getInstance().get(id);
        if (effect == null)
            effect = new CustomEffect();
        effect.readFromNBT(Server.readNBT(in), true);
        StatusEffectController.getInstance().saveEffect(effect);
        NetworkUtility.sendCustomEffectDataAll((EntityPlayerMP) player);
    }
}
