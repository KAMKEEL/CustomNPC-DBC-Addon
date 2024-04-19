package kamkeel.npcdbc.items;

import kamkeel.npcdbc.LocalizationHelper;
import kamkeel.npcdbc.config.ConfigCapsules;
import kamkeel.npcdbc.constants.enums.EnumHealthCapsules;
import kamkeel.npcdbc.controllers.CapsuleController;
import kamkeel.npcdbc.data.DBCExtended;
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

public class HealthCapsule extends Item {

    protected IIcon[] icons;

    public HealthCapsule() {
        this.setMaxStackSize(ConfigCapsules.HealthCapsuleMaxStack);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CustomItems.tabMisc);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {

        int metadata = stack.getItemDamage();
        EnumHealthCapsules healthcapsules = EnumHealthCapsules.values()[metadata];
        return LocalizationHelper.ITEM_PREFIX + healthcapsules.getName().toLowerCase() + "_healthcapsule";
    }

    @Override
    public void registerIcons(IIconRegister reg) {

        icons = new IIcon[EnumHealthCapsules.count()];
        String prefix = "npcdbc:healthcapsules/";

        for (EnumHealthCapsules healthCapsule : EnumHealthCapsules.values()) {
            icons[healthCapsule.getMeta()] = reg.registerIcon(prefix + healthCapsule.getName().toLowerCase());
        }
    }

    @Override
    public IIcon getIconFromDamage(int meta) {

        if (meta >= 0 && meta < EnumHealthCapsules.count()) {
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
        for (EnumHealthCapsules healthCapsules : EnumHealthCapsules.values()) {
            list.add(new ItemStack(item, 1, healthCapsules.getMeta()));
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (world.isRemote)
            return itemStack;

        int meta = itemStack.getItemDamage();
        if (meta < 0 || meta > EnumHealthCapsules.count())
            meta = 0;

        EnumHealthCapsules healthCapsules = EnumHealthCapsules.values()[meta];
        UUID playerUUID = player.getUniqueID();
        if (CapsuleController.canUseHealthCapsule(playerUUID, meta)) {
            // Percentage of Health to Restore
            int healthRestored = healthCapsules.getStrength();

            // Restore X Amount of Health
            DBCExtended.get(player).restoreHealthPercent(healthRestored);

            // Removes 1 Item
            itemStack.splitStack(1);
            if (itemStack.stackSize <= 0)
                player.destroyCurrentEquippedItem();

            // Set Cooldown
            CapsuleController.setHealthCapsule(playerUUID, meta);
        }

        return itemStack;
    }
}
