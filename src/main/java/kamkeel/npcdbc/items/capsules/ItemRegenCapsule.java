package kamkeel.npcdbc.items.capsules;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.LocalizationHelper;
import kamkeel.npcdbc.config.ConfigCapsules;
import kamkeel.npcdbc.constants.Capsule;
import kamkeel.npcdbc.constants.enums.EnumKiCapsules;
import kamkeel.npcdbc.constants.enums.EnumRegenCapsules;
import kamkeel.npcdbc.controllers.CapsuleController;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import noppes.npcs.CustomItems;

import java.util.HashMap;
import java.util.List;

public class ItemRegenCapsule extends Item {

    protected IIcon[] icons;

    public ItemRegenCapsule() {
        // @TODO Change to config value later
        this.setMaxStackSize(ConfigCapsules.KiCapsuleMaxStack);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CustomItems.tabMisc);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int metadata = stack.getItemDamage();
        EnumRegenCapsules regenCapsule = EnumRegenCapsules.values()[metadata];

        return LocalizationHelper.ITEM_PREFIX + regenCapsule.getName().toLowerCase() + "_regencapsule";
    }

    @Override
    public void registerIcons(IIconRegister reg) {

        icons = new IIcon[EnumRegenCapsules.count()];
        String prefix = "npcdbc:regencapsules/";

        for (EnumRegenCapsules regenCapsule : EnumRegenCapsules.values()) {
            icons[regenCapsule.getMeta()] = reg.registerIcon(prefix + regenCapsule.getName().toLowerCase());
        }
    }

    @Override
    public IIcon getIconFromDamage(int meta) {

        if (meta >= 0 && meta < EnumRegenCapsules.count()) {
            return icons[meta];
        }
        return icons[0];
    }

    @Override
    public EnumRarity getRarity(ItemStack item) {
        return EnumRarity.values()[item.getItemDamage() % 3 + 1];
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (EnumRegenCapsules regenCapsule : EnumRegenCapsules.values()) {
            list.add(new ItemStack(item, 1, regenCapsule.getMeta()));
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        return itemStack;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.block;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 100;
    }

    @Override
    public ItemStack onEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        return par1ItemStack;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
//        int meta = itemStack.getItemDamage();
//        if (meta < 0 || meta > EnumKiCapsules.count())
//            meta = 0;
//
//        HashMap<Integer, Integer> kiStrength = CapsuleController.Instance.capsuleStrength.get(Capsule.KI);
//        HashMap<Integer, Integer> kiCooldown = CapsuleController.Instance.capsuleCooldowns.get(Capsule.KI);
//        par3List.add(StatCollector.translateToLocalFormatted("capsule.restore", kiStrength.get(meta) + "%", StatCollector.translateToLocal("capsule.ki")));
//        par3List.add(StatCollector.translateToLocalFormatted("capsule.cooldown", kiCooldown.get(meta)));
    }
}
