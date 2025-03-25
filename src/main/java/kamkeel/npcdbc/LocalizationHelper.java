package kamkeel.npcdbc;

import net.minecraft.util.StatCollector;

/**
 * A utility class that will make using the localization file more convient. This isn't quite necessary for the tutorial
 * and you can ignore it until you feel there is a need for it. (Episode 2)
 */
public class LocalizationHelper {

    /**
     * Prefix for block (tile) keys.
     */
    public static final String MOD_PREFIX = "npcdbc:"; // tile.tutorialmod:

    /**
     * Prefix for keys that don't relate to any particular block/item.
     */
    public static final String MISC_PREFIX = "misc." + MOD_PREFIX; // misc.tutorialmod:
    /**
     * Prefix for item keys.
     */
    public static final String ITEM_PREFIX = "item." + MOD_PREFIX; // item.tutorialmod:
    /**
     * Prefix for block (tile) keys.
     */
    public static final String BLOCK_PREFIX = "tile." + MOD_PREFIX; // tile.tutorialmod:

    /**
     * The main function of this class, called by all the others. I don't recommend calling this elsewhere unless you have
     * a single oddball key to use.
     *
     * @param key The key for the localization file.
     * @return The localized String, if there is one. Otherwise, the key is returned. Whitespace is trimmed.
     */
    public static String getLocalizedString(String key) {

        return StatCollector.translateToLocal(key).trim();
    }

    /**
     * Gets a localization that has the "misc" prefix.
     *
     * @param key The key for the localization. For example, "PressShift" will get "misc.tutorialmod:PressShift".
     * @return The localized String, if there is one. Otherwise, the key is returned.
     */
    public static String getMiscText(String key) {

        return getLocalizedString(MISC_PREFIX + key);
    }

    /**
     * Gets a description key for a block. The actual key will be something like "tile.tutorialmod:blockName.desc", plus a
     * number if index is greater than 0.
     *
     * @param blockName The unlocalized name of the block, preferably straight from the Names class.
     * @param index     The description index. If you want the "desc" key, use 0. For "desc1", use 1. For "desc2", use 2, etc.
     * @return The localized String, if there is one. Otherwise, the key is returned.
     */
    public static String getBlockDescription(String blockName, int index) {

        String key = BLOCK_PREFIX + blockName + ".desc" + (index > 0 ? index : "");
        return getLocalizedString(key);
    }

    /**
     * Gets a localization for a block subkey. The localization you get will be for "tile.tutorialmod:blockName.key".
     *
     * @param blockName The unlocalized name of the block, preferably straight from the Names class.
     * @param key       The subkey to get.
     * @return The localized String, if there is one. Otherwise, the key is returned.
     */
    public static String getBlockKey(String blockName, String key) {

        String newKey = BLOCK_PREFIX + blockName + "." + key;
        return getLocalizedString(newKey);
    }

    /**
     * Gets a description key for an item. The actual key will be something like "item.tutorialmod:itemName.desc", plus a
     * number if index is greater than 0.
     *
     * @param itemName The unlocalized name of the item, preferably straight from the Names class.
     * @param index    The description index. If you want the "desc" key, use 0. For "desc1", use 1. For "desc2", use 2, etc.
     * @return The localized String, if there is one. Otherwise, the key is returned.
     */
    public static String getItemDescription(String itemName, int index) {

        String key = ITEM_PREFIX + itemName + ".desc" + (index > 0 ? index : "");
        return getLocalizedString(key);
    }

    /**
     * Gets a localization for an item subkey. The localization you get will be for "item.tutorialmod:itemName.key".
     *
     * @param itemName The unlocalized name of the item, preferably straight from the Names class.
     * @param key      The subkey to get.
     * @return The localized String, if there is one. Otherwise, the key is returned.
     */
    public static String getItemKey(String itemName, String key) {

        String newKey = ITEM_PREFIX + itemName + "." + key;
        return getLocalizedString(newKey);
    }

}
