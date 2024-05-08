package kamkeel.npcdbc.network.packets;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.util.ByteBufUtils;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.Entity;
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
    private String entityID;

    public PlaySound() {
    }

    public PlaySound(String soundDir, float range, String entityID) {
        this.soundDir = soundDir;
        this.range = range;
        this.entityID = entityID;
    }

    public PlaySound(String soundDir, float range) {
        this.soundDir = soundDir;
        this.range = range;
        this.entityID = "";
    }

    public static void play(Entity entity, String soundDir, float range) {
        IEntity<?> IEntity = NpcAPI.Instance().getIEntity(entity);
        ISound sound = NpcAPI.Instance().createSound(soundDir);
        sound.setEntity(IEntity);
        sound.setVolume(1);
        sound.setPitch(1);

        if (entity instanceof EntityPlayer) {
            IPlayer<?> pl = getIPlayer((EntityPlayer) entity);

            pl.playSound(1, sound);
        }

        if (range == 0)
            return;


        IEntity<?>[] playersAround = IEntity.getSurroundingEntities((int) range);
        for (IEntity<?> otherEntity : playersAround)
            if (otherEntity instanceof IPlayer && otherEntity != entity) {
                float distance = entity.getDistanceToEntity(otherEntity.getMCEntity());
                IPlayer other = (IPlayer) otherEntity;
                float volume = 1 - distance / range;
                sound.setVolume(volume);
                other.playSound(1, sound);
            }
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        ByteBufUtils.writeUTF8String(out, soundDir);
        ByteBufUtils.writeUTF8String(out, entityID);
        out.writeFloat(range);

    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        soundDir = ByteBufUtils.readUTF8String(in);
        entityID = ByteBufUtils.readUTF8String(in);
        range = in.readFloat();

        Entity entity = entityID.isEmpty() ? player : Utility.getEntityFromID(player.worldObj, entityID);
        if (entity == null)
            return;

        play(entity, soundDir, range);


    }
}
