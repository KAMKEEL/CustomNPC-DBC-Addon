package kamkeel.npcdbc.network.packets;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.util.ByteBufUtils;
import kamkeel.npcdbc.util.SoundHelper;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

public final class StopSound extends AbstractPacket {
    public static final String packetName = "NPC|StopSound";

    public SoundHelper.Sound sound;


    public StopSound() {
    }

    public StopSound(SoundHelper.Sound sound) {
        this.sound = sound;
    }


    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        ByteBufUtils.writeNBT(out, sound.writeToNbt());


    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        sound = SoundHelper.Sound.createFromNBT(ByteBufUtils.readNBT(in));

        if (Utility.isServer())
            stop(sound);
        else {
            if (SoundHelper.playingSounds.containsKey(sound.key)) {
                SoundHelper.playingSounds.get(sound.key).stop(false);
            }
        }


    }

    public static void stop(SoundHelper.Sound sound) {
        if (sound == null || sound.entity == null)
            return;
        PacketHandler.Instance.sendToTrackingPlayers(sound.entity, new StopSound(sound).generatePacket());
    }

}
