package kamkeel.npcdbc.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class KeyHandler {
    public static KeyBinding AscendKey = new KeyBinding("DBC Addon Ascend", Keyboard.KEY_P, "key.categories.gameplay");
    public static void registerKeys(){
        ClientRegistry.registerKeyBinding(AscendKey);
    }
}
