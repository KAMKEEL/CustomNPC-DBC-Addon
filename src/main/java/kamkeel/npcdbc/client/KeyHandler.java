package kamkeel.npcdbc.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class KeyHandler {
    public static KeyBinding AscendKey = new KeyBinding("Custom Form Ascend", Keyboard.KEY_P, "key.categories.customnpc");
    public static KeyBinding FormWheelKey = new KeyBinding("Form Wheel", Keyboard.KEY_LEFT, "key.categories.customnpc");

    public static void registerKeys() {
        ClientRegistry.registerKeyBinding(AscendKey);
        ClientRegistry.registerKeyBinding(FormWheelKey);
    }
}
