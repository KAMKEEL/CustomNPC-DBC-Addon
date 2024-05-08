package kamkeel.npcdbc.items;

import kamkeel.npcdbc.LocalizationHelper;;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.data.statuseffect.PlayerEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import noppes.npcs.CustomItems;

public class ItemFruitOfMight extends ItemFood {

    public ItemFruitOfMight(int saturation, float eatTime, boolean wolfFeed){
        super(saturation, eatTime, wolfFeed);

        this.setMaxStackSize(5);
        this.setAlwaysEdible();
        this.setMaxDamage(0);
        this.setCreativeTab(CustomItems.tabMisc);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack){
        return LocalizationHelper.ITEM_PREFIX + "fruitofmight";
    }

    public EnumRarity getRarity(ItemStack p_77613_1_)
    {
        return EnumRarity.uncommon;
    }

    protected void onFoodEaten(ItemStack itemStack, World world, EntityPlayer player)
    {
        if (!world.isRemote) {
            StatusEffectController.getInstance().applyEffect(player, new PlayerEffect(Effects.FRUIT_OF_MIGHT, ConfigDBCEffects.FOM_EffectLength, (byte) 1));
        }
    }
}
