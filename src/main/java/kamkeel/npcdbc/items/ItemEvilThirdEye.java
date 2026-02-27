package kamkeel.npcdbc.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.LocalizationHelper;
import kamkeel.npcdbc.client.model.Headpiece;
import kamkeel.npcdbc.client.model.ModelEvilThirdEye;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.config.ConfigDBCGameplay;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import noppes.npcs.CustomItems;

import java.util.List;

public class ItemEvilThirdEye extends ItemArmor implements Headpiece {
    public ItemEvilThirdEye() {
        super(ArmorMaterial.DIAMOND, 0, 0);
        this.setMaxStackSize(1);
        this.setCreativeTab(CustomItems.tabMisc);
        this.setUnlocalizedName("evilthirdeye");
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return LocalizationHelper.ITEM_PREFIX + "evilthirdeye";
    }

    public EnumRarity getRarity(ItemStack p_77613_1_) {
        return EnumRarity.epic;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return "npcdbc:textures/armor/dbcvanity/evilthirdeye" + (ConfigDBCClient.EnableHDTextures ? "_hd.png" : ".png"); // fahh
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World worldIn, EntityPlayer player) {
        initEyeStack(stack);
        return super.onItemRightClick(stack, worldIn, player);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        NBTTagCompound compound = itemStack.getTagCompound();
        if (compound != null && compound.hasKey("Level")) {
            par3List.add(StatCollector.translateToLocalFormatted("§cLevel: " + compound.getInteger("Level")));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int par3) {
        return ModelEvilThirdEye.EYE;
    }

    public static boolean isBlind(ItemStack itemStack) {
        return itemStack.getTagCompound() != null && itemStack.getTagCompound().hasKey("Level") && itemStack.getTagCompound().getInteger("Level") == ConfigDBCGameplay.ThirdEyeBlindLevel;
    }

    public static void increaseLevel(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof ItemEvilThirdEye))
            return;

        setLevel(stack, getLevel(stack) + 1);
    }

    public static void setLevel(ItemStack stack, int level) {
        if (stack == null || !(stack.getItem() instanceof ItemEvilThirdEye))
            return;

        NBTTagCompound compound = stack.getTagCompound();

        if (compound != null && compound.hasKey("Level")) {
            level = Math.max(1, Math.min(level, ConfigDBCGameplay.ThirdEyeBlindLevel));

            compound.setInteger("Level", level);
        }
    }

    public static int getLevel(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof ItemEvilThirdEye))
            return 0;

        NBTTagCompound compound = stack.getTagCompound();

        if (compound == null || !compound.hasKey("Level"))
            return 0;

        return compound.getInteger("Level");
    }

    public static void initEyeStack(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof ItemEvilThirdEye))
            return;

        if (stack.getTagCompound() != null && !stack.getTagCompound().hasKey("Level")) {
            stack.getTagCompound().setInteger("Level", 1);
        }
    }
}
