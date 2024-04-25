package kamkeel.npcdbc.items;

import cpw.mods.fml.common.registry.GameRegistry;
import kamkeel.npcdbc.config.ConfigCapsules;
import kamkeel.npcdbc.items.capules.HealthCapsule;
import kamkeel.npcdbc.items.capules.KiCapsule;
import kamkeel.npcdbc.items.capules.MiscCapsule;
import kamkeel.npcdbc.items.capules.StaminaCapsule;
import net.minecraft.item.Item;

public class ModItems {
    public static Item KiCapsules;
    public static Item HealthCapsules;
    public static Item StaminaCapsules;
    public static Item MiscCapsules;

    /**
     * Declare and register items. Do NOT add recipes here!
     */
    public static void init() {
        if(ConfigCapsules.EnableCapsules){
            if(ConfigCapsules.EnableKiCapsule){
                KiCapsules = new KiCapsule();
                GameRegistry.registerItem(KiCapsules, "kicapsule");
            }
            if(ConfigCapsules.EnableHealthCapsule){
                HealthCapsules = new HealthCapsule();
                GameRegistry.registerItem(HealthCapsules, "healthcapsule");
            }
            if(ConfigCapsules.EnableStaminaCapsule){
                StaminaCapsules = new StaminaCapsule();
                GameRegistry.registerItem(StaminaCapsules, "staminacapsule");
            }
            if(ConfigCapsules.EnableMiscCapsule){
                MiscCapsules = new MiscCapsule();
                GameRegistry.registerItem(MiscCapsules, "misccapsule");
            }
        }
    }
}
