package kamkeel.npcdbc.items;

import kamkeel.npcdbc.LocalizationHelper;
import kamkeel.npcdbc.config.ConfigCapsules;
import kamkeel.npcdbc.constants.EnumStaminaCapsules;
import kamkeel.npcdbc.controllers.CapsuleController;
import kamkeel.npcdbc.data.SyncedData.DBCData;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import noppes.npcs.CustomItems;

import java.util.List;
import java.util.UUID;

public class StaminaCapsule extends Item {

    protected IIcon[] icons;

    public StaminaCapsule() {
        this.setMaxStackSize(ConfigCapsules.StaminaCapsuleMaxStack);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CustomItems.tabMisc);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {

        int metadata = stack.getItemDamage();
        EnumStaminaCapsules staminacapsules = EnumStaminaCapsules.values()[metadata];
        return LocalizationHelper.ITEM_PREFIX + staminacapsules.getName().toLowerCase() + "_staminacapsule";
    }

    @Override
    public void registerIcons(IIconRegister reg) {

        icons = new IIcon[EnumStaminaCapsules.count()];
        String prefix = "npcdbc:staminacapsules/";

        for (EnumStaminaCapsules staminaCapsule : EnumStaminaCapsules.values()) {
            icons[staminaCapsule.getMeta()] = reg.registerIcon(prefix + staminaCapsule.getName().toLowerCase());
        }
    }

    @Override
    public IIcon getIconFromDamage(int meta) {

        if (meta >= 0 && meta < EnumStaminaCapsules.count()) {
            return icons[meta];
        }
        return null;
    }

    /**
     * Return an item rarity from EnumRarity
     */
    public EnumRarity getRarity(ItemStack item) {

        if (item.getItemDamage() == 0 || item.getItemDamage() == 1) {
            return EnumRarity.uncommon;
        } else if (item.getItemDamage() == 2 || item.getItemDamage() == 3) {
            return EnumRarity.rare;
        }

        return EnumRarity.epic;
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (EnumStaminaCapsules staminaCapsules : EnumStaminaCapsules.values()) {
            list.add(new ItemStack(item, 1, staminaCapsules.getMeta()));
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (world.isRemote)
            return itemStack;

        int meta = itemStack.getItemDamage();
        if (meta < 0 || meta > EnumStaminaCapsules.count())
            meta = 0;

        EnumStaminaCapsules staminaCapsules = EnumStaminaCapsules.values()[meta];
        UUID playerUUID = player.getUniqueID();
        if (CapsuleController.canUseStaminaCapsule(playerUUID, meta)) {
            // Percentage of Stamina to Restore
            int staminaRestored = staminaCapsules.getStrength();

            // Restore X Amount of Stamina
            DBCData.get(player).restoreStaminaPercent(staminaRestored);

            // Removes 1 Item
            itemStack.splitStack(1);
            if (itemStack.stackSize <= 0)
                player.destroyCurrentEquippedItem();

            // Set Cooldown
            CapsuleController.setStaminaCapsule(playerUUID, meta);
        }

        return itemStack;
    }
}
