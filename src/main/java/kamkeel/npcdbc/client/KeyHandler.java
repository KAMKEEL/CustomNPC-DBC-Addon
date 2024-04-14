package kamkeel.npcdbc.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;

public class KeyHandler {
    public static KeyBinding Ascend = new KeyBinding("Custom Form Ascension", 0, " Combat Mode");

    public static void registerKeys(){
        ClientRegistry.registerKeyBinding(Ascend);
    }
}
