package kamkeel.npcdbc.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.controllers.ScriptClientSound;

import java.util.Map;

public class SoundHelper {


    public static ScriptClientSound getClientSound(Entity entity, String soundDir) {
        SoundManager manager = (SoundManager) Utility.getPFValue(SoundHandler.class, "sndManager", Minecraft.getMinecraft().getSoundHandler());
        Map sounds = (Map) Utility.getPFValue(SoundManager.class, "playingSounds", manager);

        for (Object soundObject : sounds.values()) {
            if (!(soundObject instanceof ScriptClientSound))
                continue;

            ScriptClientSound sound = (ScriptClientSound) soundObject;
            Entity soundOwner = (Entity) Utility.getPFValue(ScriptClientSound.class, "entity", sound);
            if (soundOwner != entity)
                continue;

            ResourceLocation loc = (ResourceLocation) Utility.getPFValue(PositionedSound.class, "field_147664_a", sound);
            String s = loc.getResourceDomain() + ":" + loc.getResourcePath();
            if (s.equalsIgnoreCase(soundDir))
                return sound;
        }
        return null;
    }


}
