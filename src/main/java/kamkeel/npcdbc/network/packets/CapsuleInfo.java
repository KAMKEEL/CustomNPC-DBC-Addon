package kamkeel.npcdbc.network.packets;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.controllers.CapsuleController;
import kamkeel.npcdbc.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;
import java.util.HashMap;

public final class CapsuleInfo extends AbstractPacket {
    public static final String packetName = "NPC|CapInfo";
    private boolean cooldown = false;
    private boolean effectTime = false;

    public CapsuleInfo(){}

    public CapsuleInfo(boolean cooldowns, boolean effectTime){
        if(cooldowns && effectTime)
            throw new RuntimeException("Can't send both EffectTime and Cooldowns at the same time");

        this.cooldown = cooldowns;
        this.effectTime = effectTime;
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        byte[] data;
        if (this.cooldown) {
            data = CapsuleController.serializeHashMap(CapsuleController.Instance.capsuleCooldowns);
        }else if(this.effectTime){
            data = CapsuleController.serializeHashMap(CapsuleController.Instance.capsuleEffectTimes);
        }
        else {
            data = CapsuleController.serializeHashMap(CapsuleController.Instance.capsuleStrength);
        }
        out.writeBoolean(this.cooldown);
        out.writeBoolean(this.effectTime);
        out.writeInt(data.length);
        out.writeBytes(data);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        boolean cooldown = in.readBoolean();
        boolean effectTime = in.readBoolean();
        int length = in.readInt();
        byte[] data = new byte[length];
        in.readBytes(data);

        if(cooldown){
            try {
                HashMap<Integer, HashMap<Integer, Integer>> newMap = CapsuleController.deserializeHashMap(data);
                CapsuleController.Instance.capsuleCooldowns = newMap;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else if(effectTime){
            try {
                HashMap<Integer, HashMap<Integer, Integer>> newMap = CapsuleController.deserializeHashMap(data);
                CapsuleController.Instance.capsuleEffectTimes = newMap;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                HashMap<Integer, HashMap<Integer, Integer>> newMap = CapsuleController.deserializeHashMap(data);
                CapsuleController.Instance.capsuleStrength = newMap;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
