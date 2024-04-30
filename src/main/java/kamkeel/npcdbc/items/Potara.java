package kamkeel.npcdbc.items;

import JinRyuu.JRMCore.JRMCoreH;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.LocalizationHelper;
import kamkeel.npcdbc.config.ConfigCapsules;
import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.constants.Capsule;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.enums.EnumMiscCapsules;
import kamkeel.npcdbc.constants.enums.EnumPotaraTypes;
import kamkeel.npcdbc.controllers.CapsuleController;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import noppes.npcs.CustomItems;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static JinRyuu.JRMCore.JRMCoreH.getInt;

public class Potara extends Item {

    protected IIcon[] icons;

    public Potara() {
        this.setMaxStackSize(16);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CustomItems.tabMisc);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {

        int metadata = stack.getItemDamage();
        EnumPotaraTypes misc = EnumPotaraTypes.values()[metadata];
        return LocalizationHelper.ITEM_PREFIX + misc.getName().toLowerCase() + "_potara";
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        icons = new IIcon[EnumPotaraTypes.count()];
        String prefix = "npcdbc:potara/";

        for (EnumPotaraTypes potaraTypes : EnumPotaraTypes.values()) {
            icons[potaraTypes.getMeta()] = reg.registerIcon(prefix + potaraTypes.getName().toLowerCase());
        }
    }

    @Override
    public IIcon getIconFromDamage(int meta) {
        if (meta >= 0 && meta < EnumMiscCapsules.count()) {
            return icons[meta];
        }
        return icons[0];
    }

    public EnumRarity getRarity(ItemStack item) {
        if (item.getTagCompound() == null || !item.getTagCompound().hasKey("Side")) {
            return EnumRarity.common;
        }
        return EnumRarity.uncommon;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (EnumPotaraTypes potara : EnumPotaraTypes.values()) {
            list.add(new ItemStack(item, 1, potara.getMeta()));
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        player.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
        if (world.isRemote)
            return itemStack;

        if(itemStack.getTagCompound() != null && itemStack.getTagCompound().hasKey("Side"))
            return itemStack;

        // Check if the player has at least 2 open slots in their inventory
        int openSlots = 0;
        for (ItemStack stack : player.inventory.mainInventory) {
            if(stack == null) {
                openSlots++;
                if (openSlots >= 2) {
                    break;
                }
            }
        }

        // If there are at least 2 open slots, proceed
        if (openSlots >= 2) {
            // Create two new ItemStacks with NBT data
            ItemStack leftPotara = new ItemStack(itemStack.getItem());
            ItemStack rightPotara = new ItemStack(itemStack.getItem());

            // Add NBT data to each ItemStack
            NBTTagCompound leftNBT = leftPotara.getTagCompound();
            NBTTagCompound rightNBT = rightPotara.getTagCompound();
            if(leftNBT == null){
                leftNBT = new NBTTagCompound();
            }
            if(rightNBT == null){
                rightNBT = new NBTTagCompound();
            }

            if(ConfigDBCGameplay.UniqueEarrings){
                String uniqueCode = generateUniqueCode();
                leftNBT.setString("Hash", uniqueCode);
                rightNBT.setString("Hash", uniqueCode);
            }
            leftNBT.setString("Side", "LEFT");
            rightNBT.setString("Side", "RIGHT");

            leftPotara.setTagCompound(leftNBT);
            rightPotara.setTagCompound(rightNBT);

            itemStack.splitStack(1);

            // Add the new ItemStacks to the player's inventory
            player.inventory.addItemStackToInventory(leftPotara);
            player.inventory.addItemStackToInventory(rightPotara);
        }
        return itemStack;
    }

    private String generateUniqueCode() {
        StringBuilder uniqueCode = new StringBuilder();
        Random random = new Random();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < 6; i++) {
            uniqueCode.append(characters.charAt(random.nextInt(characters.length())));
        }
        return uniqueCode.toString();
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        NBTTagCompound compound = itemStack.getTagCompound();
        if(compound != null){
            par3List.add(StatCollector.translateToLocalFormatted("§eSide: §6" + compound.getString("Side")));
            if(compound.hasKey("Hash")){
                par3List.add(StatCollector.translateToLocalFormatted("§7Hash: §8" + compound.getString("Hash")));
            }
        }
    }
}
