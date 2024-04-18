package kamkeel.npcdbc.items;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.LocalizationHelper;
import kamkeel.npcdbc.config.ConfigCapsules;
import kamkeel.npcdbc.constants.EnumHealthCapsules;
import kamkeel.npcdbc.constants.EnumMiscCapsules;
import kamkeel.npcdbc.controllers.CapsuleController;
import kamkeel.npcdbc.data.SyncedData.DBCData;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import noppes.npcs.CustomItems;

import java.util.List;
import java.util.UUID;

import static JinRyuu.JRMCore.JRMCoreH.getInt;

public class MiscCapsule extends Item {

    protected IIcon[] icons;

    public MiscCapsule() {
        this.setMaxStackSize(ConfigCapsules.MiscCapsuleMaxStack);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CustomItems.tabMisc);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {

        int metadata = stack.getItemDamage();
        EnumMiscCapsules misc = EnumMiscCapsules.values()[metadata];
        return LocalizationHelper.ITEM_PREFIX + misc.getName().toLowerCase() + "_misccapsule";
    }

    @Override
    public void registerIcons(IIconRegister reg) {

        icons = new IIcon[EnumMiscCapsules.count()];
        String prefix = "npcdbc:misccapsules/";

        for (EnumMiscCapsules misc : EnumMiscCapsules.values()) {
            icons[misc.getMeta()] = reg.registerIcon(prefix + misc.getName().toLowerCase());
        }
    }

    @Override
    public IIcon getIconFromDamage(int meta) {

        if (meta >= 0 && meta < EnumMiscCapsules.count()) {
            return icons[meta];
        }
        return null;
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
        for (EnumMiscCapsules miscCapsules : EnumMiscCapsules.values()) {
            list.add(new ItemStack(item, 1, miscCapsules.getMeta()));
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (world.isRemote)
            return itemStack;

        int meta = itemStack.getItemDamage();
        if (meta < 0 || meta > EnumMiscCapsules.count())
            meta = 0;

        UUID playerUUID = player.getUniqueID();
        long remainingTime = CapsuleController.canUseMiscCapsule(playerUUID, meta);
        if(remainingTime > 0){
            player.addChatComponentMessage(new ChatComponentText("§fCapsule is on cooldown for " + remainingTime + " seconds"));
            return itemStack;
        }

        if(meta == EnumMiscCapsules.KO.getMeta()){
            int currentKO = getInt(player, "jrmcHar4va");
            if (currentKO <= 0) {
                player.addChatComponentMessage(new ChatComponentText("§cYou are not ko'd"));
                return itemStack;
            }
            JRMCoreH.setInt(0, player, "jrmcHar4va");
            player.addChatComponentMessage(new ChatComponentText("§aYou are no longer ko'd!"));
        }
        else if(meta == EnumMiscCapsules.Revive.getMeta()){
            int playerTime = JRMCoreH.getInt(player, "jrmcReviveTmer");
            if (playerTime <= 0) {
                player.addChatComponentMessage(new ChatComponentText("§cYou are not dead"));
                return itemStack;
            }

            JRMCoreH.setInt(0, player, "jrmcReviveTmer");
            player.addChatComponentMessage(new ChatComponentText("§eYou are now able to revive!"));
        }
        else if(meta == EnumMiscCapsules.Heat.getMeta()){
            // Restore 100 Amount of Heat
            DBCData.get(player).restoreUIHeat(100);
            player.addChatComponentMessage(new ChatComponentText("§7UI Heat Restored"));
        }

        itemStack.splitStack(1);
        if (itemStack.stackSize <= 0)
            player.destroyCurrentEquippedItem();
        CapsuleController.setMiscCapsule(playerUUID, meta);

        return itemStack;
    }
}
