package kamkeel.npcdbc.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class KeyHandler {
    public static KeyBinding FormWheelKey = new KeyBinding("Form Wheel", Keyboard.KEY_Y, "key.categories.customnpc");
    public static KeyBinding AbilityWheelKey = new KeyBinding("Ability Wheel", Keyboard.KEY_U, "key.categories.customnpc");
    public static KeyBinding AbilityCastKey = new KeyBinding("Ability Cast", Keyboard.KEY_B, "key.categories.customnpc");

    public static void registerKeys() {
        ClientRegistry.registerKeyBinding(FormWheelKey);
        ClientRegistry.registerKeyBinding(AbilityWheelKey);
    }
}
