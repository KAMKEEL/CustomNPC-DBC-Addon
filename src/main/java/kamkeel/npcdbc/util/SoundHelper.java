package kamkeel.npcdbc.util;

import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.PlaySound;
import kamkeel.npcdbc.network.packets.StopSound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import noppes.npcs.client.controllers.ScriptClientSound;


import java.util.HashMap;
import java.util.Iterator;

public class SoundHelper {
    public static HashMap<String, Sound> playingSounds = new HashMap<>();

    public static void stopSounds(Entity entity, String soundContains) {
        Iterator<Sound> iter = SoundHelper.playingSounds.values().iterator();
        while (iter.hasNext()) {
            SoundHelper.Sound sound = iter.next();
            if (sound.entity == entity && sound.soundDir.toLowerCase().contains(soundContains)) {
                Minecraft.getMinecraft().getSoundHandler().stopSound(sound);
                PacketHandler.Instance.sendToServer(new StopSound(sound).generatePacket());
                iter.remove();
            }
        }
    }


    public static class Sound extends MovingSound {
        public String key;
        public String soundDir;
        public Entity entity;

        public float range = 16;
        public boolean onlyOneCanExist = true;


        public Sound(ResourceLocation location) {
            super(location);
        }

        public Sound(String soundDir, Entity entity) {
            super(new ResourceLocation(soundDir));
            this.soundDir = soundDir;

            Utility.setPrivateField(ScriptClientSound.class, "entity", false, this, entity);
            this.entity = entity;

            key = toString();

        }

        public void update() {
            if (this.entity != null) {
                if (this.entity.isDead) {
                    this.donePlaying = true;
                } else {
                    this.xPosF = (float) this.entity.posX;
                    this.yPosF = (float) this.entity.posY;
                    this.zPosF = (float) this.entity.posZ;
                }
            }

        }

        public void setVolume(float volume) {
            this.volume = volume;

        }

        public void setRepeat(boolean repeat) {
            this.repeat = repeat;
        }


        public void play(boolean forOthers) {
            if (onlyOneCanExist && playingSounds.containsKey(key))
                return;
            playingSounds.put(key, this);
            Minecraft.getMinecraft().getSoundHandler().playSound(this);
            if (forOthers)
                PacketHandler.Instance.sendToServer(new PlaySound(this).generatePacket());

        }

        public void stop(boolean forOthers) {
            if (playingSounds.containsKey(key))
                playingSounds.remove(key);

            Minecraft.getMinecraft().getSoundHandler().stopSound(this);
            if (forOthers)
                PacketHandler.Instance.sendToServer(new StopSound(this).generatePacket());
        }

        public NBTTagCompound writeToNbt() {
            NBTTagCompound c = new NBTTagCompound();

            c.setFloat("volume", volume);
            c.setBoolean("repeat", repeat);
            c.setFloat("range", range);
            c.setBoolean("onlyOneCanExist", onlyOneCanExist);
            c.setString("soundDir", soundDir);
            c.setString("key", key);
            c.setInteger("dimensionID", entity.worldObj.provider.dimensionId);
            c.setString("entity", Utility.getEntityID(entity));

            return c;
        }

        public boolean isPlaying() {
            return Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(this);
        }

        public String toString() {
            return entity.getCommandSenderName() + entity.getEntityId() + "," + soundDir + "," + this.hashCode();
        }

        public static Sound createFromNBT(NBTTagCompound compound) {
            String directory = compound.getString("soundDir");
            Sound sound = new Sound(new ResourceLocation(directory));

            sound.setVolume(compound.getFloat("volume"));
            sound.soundDir = directory;
            sound.repeat = compound.getBoolean("repeat");
            sound.range = compound.getFloat("range");
            sound.onlyOneCanExist = compound.getBoolean("onlyOneCanExist");
            sound.key = compound.getString("key");

            int dimID = compound.getInteger("dimensionID");
            World world = Utility.getWorld(dimID);
            sound.entity = Utility.getEntityFromID(world, compound.getString("entity"));
            return sound;
        }
    }
}
