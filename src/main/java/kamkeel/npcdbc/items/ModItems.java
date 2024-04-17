package kamkeel.npcdbc.items;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public class ModItems {
    public static Item KiCapsules;

    /**
     * Declare and register items. Do NOT add recipes here!
     */
    public static void init() {
        if(true){
            KiCapsules = new KiCapsule();
            GameRegistry.registerItem(KiCapsules, "kicapsule");
        }
    }
}
