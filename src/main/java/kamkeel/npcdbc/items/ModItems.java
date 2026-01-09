package kamkeel.npcdbc.items;

import cpw.mods.fml.common.registry.GameRegistry;
import kamkeel.npcdbc.LocalizationHelper;
import kamkeel.npcdbc.config.ConfigCapsules;
import kamkeel.npcdbc.items.capsules.ItemHealthCapsule;
import kamkeel.npcdbc.items.capsules.ItemKiCapsule;
import kamkeel.npcdbc.items.capsules.ItemMiscCapsule;
import kamkeel.npcdbc.items.capsules.ItemRegenCapsule;
import kamkeel.npcdbc.items.capsules.ItemStaminaCapsule;
import net.minecraft.item.Item;

public class ModItems {
    public static Item KiCapsules;
    public static Item HealthCapsules;
    public static Item StaminaCapsules;
    public static Item MiscCapsules;
    public static Item RegenCapsules;

    public static Item FruitOfMight;

    public static Item Potaras;

    /**
     * Declare and register items. Do NOT add recipes here!
     */
    public static void init() {
        if (ConfigCapsules.EnableCapsules) {
            if (ConfigCapsules.EnableKiCapsule) {
                KiCapsules = new ItemKiCapsule();
                GameRegistry.registerItem(KiCapsules, "kicapsule");
            }
            if (ConfigCapsules.EnableHealthCapsule) {
                HealthCapsules = new ItemHealthCapsule();
                GameRegistry.registerItem(HealthCapsules, "healthcapsule");
            }
            if (ConfigCapsules.EnableStaminaCapsule) {
                StaminaCapsules = new ItemStaminaCapsule();
                GameRegistry.registerItem(StaminaCapsules, "staminacapsule");
            }
            if (ConfigCapsules.EnableMiscCapsule) {
                MiscCapsules = new ItemMiscCapsule();
                GameRegistry.registerItem(MiscCapsules, "misccapsule");
            }

            if (ConfigCapsules.EnableRegenCapsules) {
                RegenCapsules = new ItemRegenCapsule();
                GameRegistry.registerItem(RegenCapsules, "regencapsule");
            }
        }

        FruitOfMight = new ItemFruitOfMight(5, 0.4f, false).setTextureName(LocalizationHelper.MOD_PREFIX + "fruitofmight");
        GameRegistry.registerItem(FruitOfMight, FruitOfMight.getUnlocalizedName());

        Potaras = new ItemPotara();
        GameRegistry.registerItem(Potaras, "potara");
    }
}
