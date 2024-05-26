package kamkeel.npcdbc.network.packets;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.CapsuleController;
import kamkeel.npcdbc.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;
import java.util.HashMap;

public final class CapsuleInfo extends AbstractPacket {
    public static final String packetName = "NPC|CapInfo";

    private InfoType infoType;

    public CapsuleInfo(){}

    public CapsuleInfo(InfoType infoType){
        this.infoType = infoType;
    }

    public enum InfoType{
        STRENGTH,
        COOLDOWN,
        EFFECT_TIME
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        byte[] data = new byte[0];

        if (this.infoType == InfoType.COOLDOWN) {
            data = CapsuleController.serializeHashMap(CapsuleController.Instance.capsuleCooldowns);
        }
        else if(this.infoType == InfoType.STRENGTH){
            data = CapsuleController.serializeHashMap(CapsuleController.Instance.capsuleStrength);
        }
        else if(this.infoType == InfoType.EFFECT_TIME){
            data = CapsuleController.serializeHashMap(CapsuleController.Instance.capsuleEffectTimes);
        }

        out.writeInt(this.infoType.ordinal());
        out.writeInt(data.length);
        out.writeBytes(data);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {

        int type = in.readInt();
        this.infoType = InfoType.values()[type];

        int length = in.readInt();
        byte[] data = new byte[length];
        in.readBytes(data);

        if (this.infoType == InfoType.COOLDOWN) {
            try {
                HashMap<Integer, HashMap<Integer, Integer>> newMap = CapsuleController.deserializeHashMap(data);
                CapsuleController.Instance.capsuleCooldowns = newMap;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        else if(this.infoType == InfoType.STRENGTH){
            try {
                HashMap<Integer, HashMap<Integer, Integer>> newMap = CapsuleController.deserializeHashMap(data);
                CapsuleController.Instance.capsuleStrength = newMap;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        else if(this.infoType == InfoType.EFFECT_TIME){
            try {
                HashMap<Integer, HashMap<Integer, Integer>> newMap = CapsuleController.deserializeHashMap(data);
                CapsuleController.Instance.capsuleEffectTimes = newMap;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
