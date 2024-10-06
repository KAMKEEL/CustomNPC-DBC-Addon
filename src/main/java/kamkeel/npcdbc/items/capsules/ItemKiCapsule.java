package kamkeel.npcdbc.items.capsules;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.LocalizationHelper;
import kamkeel.npcdbc.config.ConfigCapsules;
import kamkeel.npcdbc.constants.Capsule;
import kamkeel.npcdbc.constants.enums.EnumKiCapsules;
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

public class ItemKiCapsule extends Item {

    protected IIcon[] icons;

    public ItemKiCapsule() {
        this.setMaxStackSize(ConfigCapsules.KiCapsuleMaxStack);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CustomItems.tabMisc);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {

        int metadata = stack.getItemDamage();
        EnumKiCapsules kicapsules = EnumKiCapsules.values()[metadata];
        return LocalizationHelper.ITEM_PREFIX + kicapsules.getName().toLowerCase() + "_kicapsule";
    }

    @Override
    public void registerIcons(IIconRegister reg) {

        icons = new IIcon[EnumKiCapsules.count()];
        String prefix = "npcdbc:kicapsules/";

        for (EnumKiCapsules kiCapsule : EnumKiCapsules.values()) {
            icons[kiCapsule.getMeta()] = reg.registerIcon(prefix + kiCapsule.getName().toLowerCase());
        }
    }

    @Override
    public IIcon getIconFromDamage(int meta) {

        if (meta >= 0 && meta < EnumKiCapsules.count()) {
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
        for (EnumKiCapsules kiCapsules : EnumKiCapsules.values()) {
            list.add(new ItemStack(item, 1, kiCapsules.getMeta()));
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        player.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
        if (world.isRemote)
            return itemStack;

        int meta = itemStack.getItemDamage();
        if (meta < 0 || meta >= EnumKiCapsules.count())
            meta = 0;

        EnumKiCapsules kiCapsules = EnumKiCapsules.values()[meta];
        UUID playerUUID = player.getUniqueID();
        long remainingTime = CapsuleController.canUseKiCapsule(playerUUID, meta);
        if(remainingTime > 0){
            player.addChatComponentMessage(new ChatComponentText("§fCapsule is on cooldown for " + remainingTime + " seconds"));
            return itemStack;
        }

        if (DBCEventHooks.onCapsuleUsedEvent(new DBCPlayerEvent.CapsuleUsedEvent(PlayerDataUtil.getIPlayer(player), Capsule.KI, meta)))
            return itemStack;

        // Percentage of Ki to Restore
        int kiRestored = kiCapsules.getStrength();

        // Restore X Amount of KI
        DBCData.get(player).stats.restoreKiPercent(kiRestored);

        // Removes 1 Item
        itemStack.splitStack(1);
        if (itemStack.stackSize <= 0)
            player.destroyCurrentEquippedItem();

        // Set Cooldown
        CapsuleController.setKiCapsule(playerUUID, meta);
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
        if (meta < 0 || meta >= EnumKiCapsules.count())
            meta = 0;

        HashMap<Integer, Integer> kiStrength = CapsuleController.Instance.capsuleStrength.get(Capsule.KI);
        HashMap<Integer, Integer> kiCooldown = CapsuleController.Instance.capsuleCooldowns.get(Capsule.KI);
        par3List.add(StatCollector.translateToLocalFormatted("capsule.restore", kiStrength.get(meta) + "%", StatCollector.translateToLocal("capsule.ki")));
        par3List.add(StatCollector.translateToLocalFormatted("capsule.cooldown", kiCooldown.get(meta)));
    }
}
