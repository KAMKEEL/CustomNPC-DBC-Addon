package kamkeel.npcdbc.items.capsules;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.LocalizationHelper;
import kamkeel.npcdbc.config.ConfigCapsules;
import kamkeel.npcdbc.constants.Capsule;
import kamkeel.npcdbc.constants.enums.EnumRegenCapsules;
import kamkeel.npcdbc.controllers.CapsuleController;
import kamkeel.npcdbc.controllers.DBCEffectController;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcdbc.util.Utility;
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

public class ItemRegenCapsule extends Item {

    protected IIcon[] icons;

    public ItemRegenCapsule() {
        this.setMaxStackSize(ConfigCapsules.RegenCapsuleMaxStack);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CustomItems.tabMisc);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int metadata = stack.getItemDamage();
        EnumRegenCapsules regenCapsule = EnumRegenCapsules.values()[metadata];

        return LocalizationHelper.ITEM_PREFIX + regenCapsule.getName().toLowerCase() + "_regencapsule";
    }

    @Override
    public void registerIcons(IIconRegister reg) {

        icons = new IIcon[EnumRegenCapsules.count()];
        String prefix = "npcdbc:regencapsules/";

        for (EnumRegenCapsules regenCapsule : EnumRegenCapsules.values()) {
            icons[regenCapsule.getMeta()] = reg.registerIcon(prefix + regenCapsule.getName().toLowerCase());
        }
    }

    @Override
    public IIcon getIconFromDamage(int meta) {

        if (meta >= 0 && meta < EnumRegenCapsules.count()) {
            return icons[meta];
        }
        return icons[0];
    }

    @Override
    public EnumRarity getRarity(ItemStack item) {
        int rarity = (item.getItemDamage() % 3) + 1;

        if(rarity >= EnumRarity.values().length)
            return EnumRarity.epic;

        return EnumRarity.values()[rarity];
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (EnumRegenCapsules regenCapsule : EnumRegenCapsules.values()) {
            list.add(new ItemStack(item, 1, regenCapsule.getMeta()));
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        player.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
        if (world.isRemote)
            return itemStack;

        int meta = itemStack.getItemDamage();
        if (meta < 0 || meta >= EnumRegenCapsules.count())
            meta = 0;

        EnumRegenCapsules regenCapsules = EnumRegenCapsules.values()[meta];
        UUID playerUUID = Utility.getUUID(player);

        // Check cooldowns
        long remainingTime = CapsuleController.canUseRegenCapsule(playerUUID, meta);
        if(remainingTime > 0){
            player.addChatComponentMessage(new ChatComponentText("§fCapsule is on cooldown for " + remainingTime + " seconds"));
            return itemStack;
        }

        if (DBCEventHooks.onCapsuleUsedEvent(new DBCPlayerEvent.CapsuleUsedEvent(PlayerDataUtil.getIPlayer(player), Capsule.REGEN, meta)))
            return itemStack;

        // Apply status effect
        DBCEffectController.getInstance().applyEffect(player, regenCapsules.getStatusEffectId(), regenCapsules.getEffectTime(), (byte) regenCapsules.getStrength());

        // Remove 1 item
        itemStack.splitStack(1);
        if (itemStack.stackSize <= 0)
            player.destroyCurrentEquippedItem();

        // Set Cooldown
        CapsuleController.setRegenCapsule(playerUUID, meta);

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
        if (meta < 0 || meta >= EnumRegenCapsules.count())
            meta = 0;

        EnumRegenCapsules regenCapsules = EnumRegenCapsules.values()[meta];

        HashMap<Integer, Integer> regenStrength = CapsuleController.Instance.capsuleStrength.get(Capsule.REGEN);
        HashMap<Integer, Integer> regenCooldown = CapsuleController.Instance.capsuleCooldowns.get(Capsule.REGEN);
        HashMap<Integer, Integer> regenTimes = CapsuleController.Instance.capsuleEffectTimes.get(Capsule.REGEN);

        par3List.add(StatCollector.translateToLocalFormatted("capsule.effect", DBCEffectController.getInstance().get(regenCapsules.getStatusEffectId()).getName(), regenStrength.get(meta), regenTimes.get(meta)));
        par3List.add(StatCollector.translateToLocalFormatted("capsule.cooldown", regenCooldown.get(meta)));
    }
}
