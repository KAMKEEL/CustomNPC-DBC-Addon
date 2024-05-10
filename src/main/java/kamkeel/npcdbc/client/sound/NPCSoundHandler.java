package kamkeel.npcdbc.client.sound;

import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.StopSound;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NPCSoundHandler {
    public static HashMap<String, Sound> playingSounds = new HashMap<>();

    public static void stopSounds(Entity entity, String soundContains) {
        Iterator<Sound> iter = playingSounds.values().iterator();
        while (iter.hasNext()) {
            Sound sound = iter.next();
            if (sound.entity == entity && sound.soundDir.toLowerCase().contains(soundContains)) {
                Minecraft.getMinecraft().getSoundHandler().stopSound(sound);
                PacketHandler.Instance.sendToServer(new StopSound(sound).generatePacket());
                iter.remove();
            }
        }
    }

    public static boolean contains(String key) {
        Iterator<String> iter = playingSounds.keySet().iterator();
        while (iter.hasNext()) {
            String sound = iter.next();
            if (sound.contains(key))
                return true;

        }
        return false;
    }

    public static void verifySounds(){
        Iterator<Sound> iter = playingSounds.values().iterator();
        while (iter.hasNext()) {
            Sound sound = iter.next();
            sound.isPlaying();
        }
    }

    public static boolean isPlayingSound(Entity entity, String soundDir) {
        Iterator<Map.Entry<String, Sound>> iter = NPCSoundHandler.playingSounds.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Sound> entry = iter.next();
            String sound = entry.getKey();
            if (sound.contains(entity.getCommandSenderName() + entity.getEntityId()) && sound.contains(soundDir)){
                Sound found = entry.getValue();
                return found.isPlaying();
            }
        }
        return false;
    }
}
