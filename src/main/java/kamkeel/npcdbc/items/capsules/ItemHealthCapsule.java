package kamkeel.npcdbc.items.capsules;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.LocalizationHelper;
import kamkeel.npcdbc.config.ConfigCapsules;
import kamkeel.npcdbc.constants.Capsule;
import kamkeel.npcdbc.constants.enums.EnumHealthCapsules;
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
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import noppes.npcs.CustomItems;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ItemHealthCapsule extends Item {

    protected IIcon[] icons;

    public ItemHealthCapsule() {
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
        return icons[0];
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
        player.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
        if (world.isRemote)
            return itemStack;

        int meta = itemStack.getItemDamage();
        if (meta < 0 || meta > EnumHealthCapsules.count())
            meta = 0;

        if (DBCEventHooks.onCapsuleUsedEvent(new DBCPlayerEvent.CapsuleUsedEvent(PlayerDataUtil.getIPlayer(player), Capsule.HP, meta)))
            return itemStack;

        EnumHealthCapsules healthCapsules = EnumHealthCapsules.values()[meta];
        UUID playerUUID = player.getUniqueID();
        long remainingTime = CapsuleController.canUseHealthCapsule(playerUUID, meta);
        if(remainingTime > 0){
            player.addChatComponentMessage(new ChatComponentText("§fCapsule is on cooldown for " + remainingTime + " seconds"));
            return itemStack;
        }

        // Percentage of Health to Restore
        int healthRestored = healthCapsules.getStrength();

        // Restore X Amount of Health
        DBCData.get(player).stats.restoreHealthPercent(healthRestored);

        // Removes 1 Item
        itemStack.splitStack(1);
        if (itemStack.stackSize <= 0)
            player.destroyCurrentEquippedItem();

        // Set Cooldown
        CapsuleController.setHealthCapsule(playerUUID, meta);
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
        int meta = itemStack.getItemDamage();
        if (meta < 0 || meta > EnumHealthCapsules.count())
            meta = 0;

        HashMap<Integer, Integer> health = CapsuleController.Instance.capsuleStrength.get(Capsule.HP);
        HashMap<Integer, Integer> healthCooldown = CapsuleController.Instance.capsuleCooldowns.get(Capsule.HP);
        par3List.add(StatCollector.translateToLocalFormatted("capsule.restore", health.get(meta) + "%", StatCollector.translateToLocal("capsule.hp")));
        par3List.add(StatCollector.translateToLocalFormatted("capsule.cooldown", healthCooldown.get(meta)));
    }
}
