package kamkeel.npcdbc.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.LocalizationHelper;
import kamkeel.npcdbc.client.model.Headpiece;
import kamkeel.npcdbc.client.model.ModelControlCrown;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.config.ConfigDBCGameplay;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import noppes.npcs.CustomItems;

public class ItemControlCrown extends ItemArmor implements Headpiece {

    public ItemControlCrown() {
        super(ArmorMaterial.GOLD, 0, 0);
        this.setMaxStackSize(1);
        this.setMaxDamage(ConfigDBCGameplay.ControlCrownDurability);
        this.setCreativeTab(CustomItems.tabMisc);
        this.setUnlocalizedName("controlcrown");
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return LocalizationHelper.ITEM_PREFIX + "controlcrown";
    }

    public EnumRarity getRarity(ItemStack p_77613_1_) {
        return EnumRarity.uncommon;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return "npcdbc:textures/armor/dbcvanity/controlcrown" + (ConfigDBCClient.EnableHDTextures ? "_hd.png" : ".png");
    }

    public String getArmor() {
        return "npcdbc:textures/armor/dbcvanity/controlcrown.png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int par3) {
        return ModelControlCrown.CROWN;
    }
}
