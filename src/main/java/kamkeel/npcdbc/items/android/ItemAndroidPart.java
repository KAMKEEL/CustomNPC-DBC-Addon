package kamkeel.npcdbc.items.android;

import kamkeel.npcdbc.LocalizationHelper;
import kamkeel.npcdbc.data.race.races.android.AndroidPartType;
import kamkeel.npcdbc.items.ModItems;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

public class ItemAndroidPart extends Item {

    protected IIcon[] icons;

    public ItemAndroidPart() {
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(ModItems.tabAndroid);
    }

    public static AndroidPartType getPartType(ItemStack stack) {
        return AndroidPartType.byOrdinal(stack.getItemDamage());
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        AndroidPartType type = getPartType(stack);
        if (type == null) return LocalizationHelper.ITEM_PREFIX + "android_part_unknown";
        return LocalizationHelper.ITEM_PREFIX + type.getName().toLowerCase();
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (AndroidPartType type : AndroidPartType.values()) {
            list.add(new ItemStack(item, 1, type.ordinal()));
        }
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        icons = new IIcon[AndroidPartType.count()];
        String prefix = "npcdbc:androidparts/";
        for (AndroidPartType type : AndroidPartType.values()) {
            icons[type.ordinal()] = reg.registerIcon(prefix + type.getName().toLowerCase());
        }
    }

    @Override
    public IIcon getIconFromDamage(int meta) {
        AndroidPartType type = AndroidPartType.byOrdinal(meta);
        if (type != null && type.ordinal() < icons.length) {
            return icons[type.ordinal()];
        }
        return null;
    }
}
