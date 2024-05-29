package kamkeel.npcdbc.data;

import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class SoundSource {
    public String key;
    public String soundDir;
    public Entity entity;

    public float maxVolume = 1.0f;
    public float range = 16;
    public boolean onlyOneCanExist = false;

    public boolean fadeOut = false, fadeIn = false;
    public float fadeFactor = 0.01f;

    public SoundSource(String soundDir) {
        this.soundDir = soundDir;
    }

    public SoundSource(String soundDir, Entity entity) {
        this.soundDir = soundDir;
        this.entity = entity;
        this.key = toString();
    }

    public NBTTagCompound writeToNbt() {
        NBTTagCompound c = new NBTTagCompound();

        c.setFloat("maxVolume", maxVolume);
        c.setFloat("range", range);
        c.setFloat("fadeFactor", fadeFactor);

        c.setBoolean("repeat", false); // Repeat will be managed client-side
        c.setBoolean("onlyOneCanExist", onlyOneCanExist);
        c.setBoolean("fadeOut", fadeOut);
        c.setBoolean("fadeIn", fadeIn);

        c.setString("soundDir", soundDir);
        c.setString("key", key);

        c.setInteger("dimensionID", entity.worldObj.provider.dimensionId);
        c.setString("entity", Utility.getEntityID(entity));

        return c;
    }

    public static SoundSource createFromNBT(NBTTagCompound compound) {
        String directory = compound.getString("soundDir");
        SoundSource soundSource = new SoundSource(directory);

        soundSource.maxVolume = compound.getFloat("maxVolume");
        soundSource.soundDir = directory;
        soundSource.fadeOut = compound.getBoolean("fadeOut");
        soundSource.fadeIn = compound.getBoolean("fadeIn");
        soundSource.fadeFactor = compound.getFloat("fadeFactor");

        soundSource.range = compound.getFloat("range");
        soundSource.onlyOneCanExist = compound.getBoolean("onlyOneCanExist");
        soundSource.key = compound.getString("key");

        int dimID = compound.getInteger("dimensionID");
        World world = Utility.getWorld(dimID);
        soundSource.entity = Utility.getEntityFromID(world, compound.getString("entity"));
        return soundSource;
    }

    @Override
    public String toString() {
        return entity.getEntityId() + ":" + soundDir;
    }
}
