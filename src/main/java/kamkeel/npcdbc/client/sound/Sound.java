package kamkeel.npcdbc.client.sound;

import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.PlaySound;
import kamkeel.npcdbc.network.packets.StopSound;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;

public class Sound extends MovingSound {
    public String key;
    public String soundDir;
    public Entity entity;

    public float maxVolume = 1.0f;
    public float range = 16;
    public boolean onlyOneCanExist = false;

    public boolean fadeOut = false, fadeIn = false;
    public float fadeFactor = 0.01f;


    public Sound(String soundDir) {
        super(new ResourceLocation(soundDir));
    }

    public Sound(String soundDir, Entity entity) {
        super(new ResourceLocation(soundDir));
        this.soundDir = soundDir;
        this.entity = entity;
        volume = 0.5f;
        key = toString();
    }

    public void update() {
        if (this.entity == null || entity.isDead || (Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER) == 0)) {
            stop(false);
            return;
        }

        this.xPosF = (float) this.entity.posX;
        this.yPosF = (float) this.entity.posY;
        this.zPosF = (float) this.entity.posZ;

        if (fadeIn && !fadeOut)
            if (volume < maxVolume)
                volume = Math.min(volume + fadeFactor, maxVolume);


        if (fadeOut) {
            volume -= fadeFactor;
            if (volume <= 0)
                stop(false);
        }
    }

    public Sound setVolume(float volume) {
        this.volume = volume;
        return this;
    }

    public Sound setRepeat(boolean repeat) {
        this.repeat = repeat;
        return this;
    }

    public Sound setRange(float repeat) {
        this.range = repeat;
        return this;
    }


    public void play(boolean forOthers) {
        if (forOthers)
            PacketHandler.Instance.sendToServer(new PlaySound(this).generatePacket());
        else {
            PlaySoundAtEntityEvent event = new PlaySoundAtEntityEvent(entity, soundDir, volume, getPitch());

            if (MinecraftForge.EVENT_BUS.post(event) || onlyOneCanExist && SoundHandler.playingSounds.containsKey(key))
                return;

            Minecraft.getMinecraft().getSoundHandler().playSound(this);
            SoundHandler.playingSounds.put(key, this);
        }

    }

    public void stop(boolean forOthers) {
        donePlaying = true; // this is all we need, let game and verifySounds handle rest

        if (forOthers)
            PacketHandler.Instance.sendToServer(new StopSound(this).generatePacket());
    }

    public boolean isPlaying() {
        return Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(this);
    }

    public NBTTagCompound writeToNbt() {
        NBTTagCompound c = new NBTTagCompound();

        c.setFloat("volume", volume);
        c.setFloat("maxVolume", maxVolume);
        c.setFloat("range", range);
        c.setFloat("fadeFactor", fadeFactor);

        c.setBoolean("repeat", repeat);
        c.setBoolean("onlyOneCanExist", onlyOneCanExist);
        c.setBoolean("fadeOut", fadeOut);
        c.setBoolean("fadeIn", fadeIn);

        c.setString("soundDir", soundDir);
        c.setString("key", key);

        c.setInteger("dimensionID", entity.worldObj.provider.dimensionId);
        c.setString("entity", Utility.getEntityID(entity));

        return c;
    }

    public String toString() {
        return entity.getEntityId() + ":" + soundDir;
    }

    public static Sound createFromNBT(NBTTagCompound compound) {
        String directory = compound.getString("soundDir");
        Sound sound = new Sound(directory);

        sound.setVolume(compound.getFloat("volume"));
        sound.maxVolume = compound.getFloat("maxVolume");
        sound.soundDir = directory;
        sound.repeat = compound.getBoolean("repeat");
        sound.fadeOut = compound.getBoolean("fadeOut");
        sound.fadeIn = compound.getBoolean("fadeIn");
        sound.fadeFactor = compound.getFloat("fadeFactor");

        sound.range = compound.getFloat("range");
        sound.onlyOneCanExist = compound.getBoolean("onlyOneCanExist");
        sound.key = compound.getString("key");

        int dimID = compound.getInteger("dimensionID");
        World world = Utility.getWorld(dimID);
        sound.entity = Utility.getEntityFromID(world, compound.getString("entity"));
        return sound;
    }
}
