package kamkeel.npcdbc.network.packets;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.util.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.handler.data.ISound;
import noppes.npcs.scripted.NpcAPI;

import java.io.IOException;

import static kamkeel.npcdbc.util.PlayerDataUtil.getIPlayer;

public final class PlaySound extends AbstractPacket {
    public static final String packetName = "NPC|PlaySound";
    private String soundDir;
    private float range;

    public PlaySound() {
    }

    public PlaySound(String soundDir, float range) {
        this.soundDir = soundDir;
        this.range = range;
    }


    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        ByteBufUtils.writeUTF8String(out, soundDir);
        out.writeFloat(range);

    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        soundDir = ByteBufUtils.readUTF8String(in);
        range = in.readFloat();

        ISound IT = NpcAPI.Instance().createSound(soundDir);
        IT.setVolume(1);
        IT.setPitch(1);

        if (player instanceof EntityPlayer) {
            IPlayer<?> pl = getIPlayer(player);
            IT.setEntity(pl);
            pl.playSound(1, IT);
        }

        if (range == 0)
            return;

        IEntity<?> playerEntity = NpcAPI.Instance().getIEntity(player);
        IEntity<?>[] playersAround = playerEntity.getSurroundingEntities((int) range);
        for (IEntity<?> otherEntity : playersAround)
            if (otherEntity instanceof IPlayer && otherEntity != player) {
                IPlayer other = (IPlayer) otherEntity;
                other.playSound(1, IT);
            }
    }
}
