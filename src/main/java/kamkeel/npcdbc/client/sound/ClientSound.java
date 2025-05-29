package kamkeel.npcdbc.client.sound;

import kamkeel.npcdbc.data.SoundSource;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.player.PlaySound;
import kamkeel.npcdbc.network.packets.player.StopSound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;

public class ClientSound extends MovingSound {
    public final SoundSource soundSource;
    public final Entity entity;
    public final String key;

    public ClientSound(SoundSource soundSource) {
        super(new ResourceLocation(soundSource.soundDir));
        this.soundSource = soundSource;
        this.entity = soundSource.entity;
        this.volume = 0.5f;
        this.key = soundSource.key;
    }

    @Override
    public void update() {
        if (this.entity == null || entity.isDead || (Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER) == 0)) {
            stop(false);
            return;
        }

        this.xPosF = (float) this.entity.posX;
        this.yPosF = (float) this.entity.posY;
        this.zPosF = (float) this.entity.posZ;

        if (soundSource.fadeIn && !soundSource.fadeOut) {
            if (volume < soundSource.maxVolume)
                volume = Math.min(volume + soundSource.fadeFactor, soundSource.maxVolume);
        }

        if (soundSource.fadeOut) {
            volume -= soundSource.fadeFactor;
            if (volume <= 0)
                stop(false);
        }
    }

    public ClientSound setVolume(float volume) {
        this.volume = volume;
        return this;
    }

    public ClientSound setPitch(float pitch) {
        this.field_147663_c = pitch;
        return this;
    }

    public ClientSound setRepeat(boolean repeat) {
        this.repeat = repeat;
        return this;
    }

    public ClientSound setRange(float range) {
        this.soundSource.range = range;
        return this;
    }

    public void play(boolean forOthers) {
        if (forOthers)
            DBCPacketHandler.Instance.sendToServer(new PlaySound(this.soundSource));
        else {
            PlaySoundAtEntityEvent event = new PlaySoundAtEntityEvent(entity, soundSource.soundDir, volume, getPitch());

            if (MinecraftForge.EVENT_BUS.post(event) || soundSource.onlyOneCanExist && SoundHandler.playingSounds.containsKey(soundSource.key) && !SoundHandler.playingSounds.get(soundSource.key).soundSource.fadeOut)
                return;

            Minecraft.getMinecraft().getSoundHandler().playSound(this);
            SoundHandler.playingSounds.put(soundSource.key, this);
        }
    }

    public void stop(boolean forOthers) {
        donePlaying = true; // this is all we need, let game and verifySounds handle rest

        if (forOthers)
            DBCPacketHandler.Instance.sendToServer(new StopSound(this.soundSource));
    }

    public boolean isPlaying() {
        return Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(this);
    }
}
