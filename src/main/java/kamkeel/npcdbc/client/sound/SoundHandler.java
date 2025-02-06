package kamkeel.npcdbc.client.sound;

import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.StopSound;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SoundHandler {
    public static HashMap<String, ClientSound> playingSounds = new HashMap<>();

    public static void stopSounds(Entity entity, String soundContains) {
        Iterator<ClientSound> iter = playingSounds.values().iterator();
        while (iter.hasNext()) {
            ClientSound sound = iter.next();
            if (sound.entity == entity && sound.soundSource.soundDir.toLowerCase().contains(soundContains.toLowerCase())) {
                Minecraft.getMinecraft().getSoundHandler().stopSound(sound);
                DBCPacketHandler.Instance.sendToServer(new StopSound(sound.soundSource));
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

    public static void verifySounds() {
        Iterator<ClientSound> iter = playingSounds.values().iterator();
        while (iter.hasNext()) {
            ClientSound sound = iter.next();
            if (!sound.isPlaying())
                iter.remove();

        }
    }

    public static boolean isPlayingSound(Entity entity, String soundDir) {
        Iterator<Map.Entry<String, ClientSound>> iter = SoundHandler.playingSounds.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, ClientSound> entry = iter.next();
            String sound = entry.getKey();
            if (sound.contains(entity.getEntityId() + "") && sound.contains(soundDir)) {
                ClientSound found = entry.getValue();
                return found.isPlaying();
            }
        }
        return false;
    }

    public static ClientSound getPlayingSound(Entity entity, String soundDir) {
        Iterator<Map.Entry<String, ClientSound>> iter = SoundHandler.playingSounds.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, ClientSound> entry = iter.next();
            String sound = entry.getKey();
            if (sound.contains(entity.getEntityId() + "") && sound.contains(soundDir))
                return entry.getValue();

        }
        return null;
    }
}
