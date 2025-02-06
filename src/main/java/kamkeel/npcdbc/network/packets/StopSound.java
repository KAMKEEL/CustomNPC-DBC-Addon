package kamkeel.npcdbc.network.packets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.client.sound.SoundHandler;
import kamkeel.npcdbc.data.SoundSource;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.util.ByteBufUtils;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

public final class StopSound extends AbstractPacket {
    public static final String packetName = "NPC|StopSound";

    public SoundSource sound;

    public StopSound() {
    }

    public StopSound(SoundSource sound) {
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
        SoundSource sound = SoundSource.createFromNBT(ByteBufUtils.readNBT(in));

        if (Utility.isServer())
            stop(sound);
        else {
            stopClient(sound);
        }
    }

    public static void stop(SoundSource sound) {
        if (sound == null || sound.entity == null)
            return;
        DBCPacketHandler.Instance.sendToTrackingPlayers(sound.entity, new StopSound(sound));
    }


    @SideOnly(Side.CLIENT)
    public static void stopClient(SoundSource sound) {
        if (sound == null || sound.entity == null)
            return;
        if (SoundHandler.playingSounds.containsKey(sound.key)) {
            SoundHandler.playingSounds.get(sound.key).stop(false);
        }
    }

}
