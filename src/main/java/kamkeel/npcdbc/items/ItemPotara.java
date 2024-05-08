package kamkeel.npcdbc.items;

import JinRyuu.JRMCore.items.ItemVanity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.LocalizationHelper;
import kamkeel.npcdbc.client.model.ModelPotara;
import kamkeel.npcdbc.client.render.PotaraItemRenderer;
import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.constants.enums.EnumMiscCapsules;
import kamkeel.npcdbc.constants.enums.EnumPotaraTypes;
import kamkeel.npcdbc.controllers.FusionHandler;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import noppes.npcs.CustomItems;
import noppes.npcs.scripted.NpcAPI;

import java.util.List;
import java.util.Random;

public class ItemPotara extends ItemArmor {

    protected IIcon[] icons;

    public ItemPotara() {
        super(ItemArmor.ArmorMaterial.IRON, 0, 0);
        this.setMaxStackSize(16);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CustomItems.tabMisc);

        MinecraftForgeClient.registerItemRenderer(this, new PotaraItemRenderer());
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int metadata = stack.getItemDamage();
        EnumPotaraTypes misc = EnumPotaraTypes.values()[metadata];
        return LocalizationHelper.ITEM_PREFIX + misc.getName().toLowerCase() + "_potara";
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        icons = new IIcon[EnumPotaraTypes.count() * 2];
        String prefix = "npcdbc:potara/";

        for (EnumPotaraTypes potaraTypes : EnumPotaraTypes.values()) {
            icons[potaraTypes.getMeta()] = reg.registerIcon(prefix + potaraTypes.getName().toLowerCase());
        }

        for(int i = EnumPotaraTypes.count(); i < EnumPotaraTypes.count() * 2; i++){
            icons[i] = reg.registerIcon(prefix + EnumPotaraTypes.values()[i - EnumPotaraTypes.count()].getName().toLowerCase() + "_pair");
        }
    }

    @Override
    public IIcon getIconFromDamage(int meta) {
        if (meta >= 0 && meta < EnumMiscCapsules.count()) {
            return icons[meta];
        }
        if(meta >= EnumMiscCapsules.count() && meta < EnumMiscCapsules.count() * 2){
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
            leftPotara.setItemDamage(itemStack.getItemDamage());
            ItemStack rightPotara = new ItemStack(itemStack.getItem());
            rightPotara.setItemDamage(itemStack.getItemDamage());

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

    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target)
    {
        if (player.worldObj.isRemote)
            return false;

        if(!(target instanceof EntityPlayer))
            return false;

        if(stack.getTagCompound() == null || !stack.getTagCompound().hasKey("Side"))
            return false;

        NBTTagCompound potara = stack.getTagCompound();
        boolean rightSide = potara.getString("Side").equals("RIGHT");
        String hash = potara.hasKey("Hash") ? potara.getString("Hash") : "";
        int tier = stack.getItemDamage();
        FusionHandler.requestFusion(player, (EntityPlayer) target, rightSide, hash, tier);
        return true;
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

    public static boolean isRightSide(ItemStack itemStack){
        return itemStack.getTagCompound() != null && itemStack.getTagCompound().hasKey("Side") && itemStack.getTagCompound().getString("Side").equals("RIGHT");
    }

    public static boolean isSplit(ItemStack itemStack){
        return itemStack.getTagCompound() != null && itemStack.getTagCompound().hasKey("Side");
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        NBTTagCompound compound = itemStack.getTagCompound();
        if(compound != null && compound.hasKey("Side")){
            par3List.add(StatCollector.translateToLocalFormatted("§eSide: §6" + compound.getString("Side")));
            if(compound.hasKey("Hash")){
                par3List.add(StatCollector.translateToLocalFormatted("§7Hash: §8" + compound.getString("Hash")));
            }
        } else {
            par3List.add(StatCollector.translateToLocalFormatted("§eRight click to split into pairs"));
        }
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        int meta = stack.getItemDamageForDisplay();
        return "npcdbc:textures/armor/dbcvanity/potara_"+meta+".png"; //@TODO change texture based on damage
    }


    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int par3){
        if(!isSplit(itemStack))
            return ModelPotara.BOTH_EARS;

        if(isRightSide(itemStack))
            return ModelPotara.RIGHT_EAR;

        return ModelPotara.LEFT_EAR;
    }

}
