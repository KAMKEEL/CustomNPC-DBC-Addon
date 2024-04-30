package kamkeel.npcdbc.items;

import cpw.mods.fml.common.registry.GameRegistry;
import kamkeel.npcdbc.LocalizationHelper;
import kamkeel.npcdbc.config.ConfigCapsules;
import kamkeel.npcdbc.items.capsules.HealthCapsule;
import kamkeel.npcdbc.items.capsules.KiCapsule;
import kamkeel.npcdbc.items.capsules.MiscCapsule;
import kamkeel.npcdbc.items.capsules.StaminaCapsule;
import net.minecraft.item.Item;

public class ModItems {
    public static Item KiCapsules;
    public static Item HealthCapsules;
    public static Item StaminaCapsules;
    public static Item MiscCapsules;

    public static Item FruitOfMight;

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

        FruitOfMight = new FruitOfMight(5, 0.4f, false).setTextureName(LocalizationHelper.MOD_PREFIX + "fruitofmight");
        GameRegistry.registerItem(FruitOfMight, FruitOfMight.getUnlocalizedName());
    }
}
