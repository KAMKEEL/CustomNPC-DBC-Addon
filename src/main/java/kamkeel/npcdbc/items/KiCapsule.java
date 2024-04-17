package kamkeel.npcdbc.items;

import kamkeel.npcdbc.LocalizationHelper;
import kamkeel.npcdbc.config.ConfigCapsules;
import kamkeel.npcdbc.constants.EnumKiCapsules;
import kamkeel.npcdbc.controllers.CapsuleController;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import noppes.npcs.CustomItems;

import java.util.List;
import java.util.UUID;

public class KiCapsule extends Item {

    protected IIcon[] icons;

    public KiCapsule(){
        this.setMaxStackSize(64);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CustomItems.tabMisc);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack){

        int metadata = stack.getItemDamage();
        EnumKiCapsules kicapsules = EnumKiCapsules.values()[metadata];
        return LocalizationHelper.ITEM_PREFIX + kicapsules.getName().toLowerCase() + "_kicapsule";
    }

    @Override
    public void registerIcons(IIconRegister reg){

        icons = new IIcon[EnumKiCapsules.count()];
        String prefix = "npcdbc:kicapsules/";

        for(EnumKiCapsules kiCapsule : EnumKiCapsules.values()){
            icons[kiCapsule.getMeta()] = reg.registerIcon(prefix + kiCapsule.getName().toLowerCase());
        }
    }

    @Override
    public IIcon getIconFromDamage(int meta){

        if(meta >= 0 && meta < EnumKiCapsules.count()){
            return icons[meta];
        }
        return null;
    }

    /**
     * Return an item rarity from EnumRarity
     */
    public EnumRarity getRarity(ItemStack item)
    {

        if(item.getItemDamage() == 0 || item.getItemDamage() == 1){
            return EnumRarity.uncommon;
        }
        else if(item.getItemDamage() == 2 || item.getItemDamage() == 3){
            return EnumRarity.rare;
        }

        return EnumRarity.epic;
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list){
        for (EnumKiCapsules kiCapsules: EnumKiCapsules.values()){
            list.add(new ItemStack(item, 1, kiCapsules.getMeta()));
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
    {
        if(world.isRemote)
            return itemStack;

        int meta = itemStack.getItemDamage();
        if(meta < 0 || meta > EnumKiCapsules.count())
            meta = 0;

        EnumKiCapsules kiCapsules = EnumKiCapsules.values()[meta];
        long currentTime = System.currentTimeMillis();
        long lastUsed = 0;
        UUID playerUUID = player.getUniqueID();
        if(CapsuleController.lastUsedKiCapsule.containsKey(playerUUID))
            lastUsed = CapsuleController.lastUsedKiCapsule.get(playerUUID);

        if(!ConfigCapsules.EnableCapsuleCooldowns || currentTime - lastUsed > kiCapsules.getCooldown() * 1000L){
            // Percentage of Ki to Restore
            int kiRestored = kiCapsules.getStrength();

            // Restore X Amount of KI

            // Removes 1 Item
            itemStack.splitStack(1);
            if(itemStack.stackSize <= 0)
                player.destroyCurrentEquippedItem();

            // Sets Cooldown
            CapsuleController.lastUsedKiCapsule.put(playerUUID, currentTime);
        }

        return itemStack;
    }
}
