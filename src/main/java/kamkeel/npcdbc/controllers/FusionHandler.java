package kamkeel.npcdbc.controllers;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.constants.enums.EnumPotaraTypes;
import kamkeel.npcdbc.data.FuseRequest;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.dbcdata.DBCDataUniversal;
import kamkeel.npcdbc.items.ItemPotara;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class FusionHandler {

    public static HashMap<UUID, FuseRequest> fuseRequest = new HashMap<>();

    public static boolean requestFusion(EntityPlayer sender, EntityPlayer target, boolean rightSide, String hash, int tier){
        boolean senderFusion = JRMCoreH.PlyrSettingsB(sender, 4);
        boolean targetFusion = JRMCoreH.PlyrSettingsB(target, 4);

        if(!senderFusion){
            // Fusion is not on- Inform Sender
            NetworkUtility.sendServerMessage(sender, "§c", sender.getCommandSenderName(), " ", "npcdbc.fusionSkillFusion");
            return false;
        }
        if(!targetFusion){
            // Target Fusion is not on- Inform Sender
            NetworkUtility.sendServerMessage(sender, "§c", target.getCommandSenderName(), " ", "npcdbc.fusionSkillFusion");
            return false;
        }


        if(hasNofuse(sender))
            NetworkUtility.sendServerMessage(sender, "§c", sender.getCommandSenderName(), " ", "npcdbc.noFuse");

        if(hasNofuse(target))
            NetworkUtility.sendServerMessage(sender, "§c", target.getCommandSenderName(), " ", "npcdbc.noFuse");

        UUID uuidSender = Utility.getUUID(sender);
        UUID uuidTarget = Utility.getUUID(target);

        FuseRequest senderRequest = new FuseRequest(sender.getCommandSenderName(), target.getCommandSenderName(), rightSide, hash, tier);
        FuseRequest targetRequest = null;
        if(fuseRequest.containsKey(uuidTarget)){
            targetRequest = fuseRequest.get(uuidTarget);
            if(senderRequest.checkRequest(targetRequest)){
                if(sender.getHeldItem() == null || !(sender.getHeldItem().getItem() instanceof ItemPotara)){
                    NetworkUtility.sendServerMessage(sender, "§c", "npcdbc.holdPotara");
                    return false;
                }
                if(target.getHeldItem() == null || !(target.getHeldItem().getItem() instanceof ItemPotara)){
                    NetworkUtility.sendServerMessage(sender, "§c", "npcdbc.holdPotara");
                    return false;
                }
                ItemStack sendPotara = sender.getHeldItem();
                ItemStack targetPotara = target.getHeldItem();

                if(sendPotara.getItemDamage() != targetPotara.getItemDamage()) {
                    NetworkUtility.sendServerMessage(sender, "§c", "npcdbc.potaraTier");
                    return false;
                }

                NBTTagCompound sendNBT = sendPotara.getTagCompound();
                NBTTagCompound targetNBT = targetPotara.getTagCompound();
                if(sendNBT == null || targetNBT == null) {
                    NetworkUtility.sendServerMessage(sender, "§c", "npcdbc.potaraSpit");
                    return false;
                }
                if(!sendNBT.hasKey("Side") || !targetNBT.hasKey("Side")){
                    NetworkUtility.sendServerMessage(sender, "§c", "npcdbc.potaraSpit");
                    return false;
                }

                if(sendNBT.getString("Side").equals(targetNBT.getString("Side"))){
                    NetworkUtility.sendServerMessage(sender, "§c", "npcdbc.potaraSides");
                    return false;
                }

                String sendHash = sendNBT.hasKey("Hash") ? sendNBT.getString("Hash") : "";
                String targetHash = sendNBT.hasKey("Hash") ? sendNBT.getString("Hash") : "";
                if(!sendHash.equals(targetHash)) {
                    NetworkUtility.sendServerMessage(sender, "§c", "npcdbc.potaraHash");
                    return false;
                }

                fuseRequest.remove(uuidSender);
                fuseRequest.remove(uuidTarget);
                sendPotara.splitStack(1);
                targetPotara.splitStack(1);
                if(sendPotara.stackSize <= 0)
                    sender.destroyCurrentEquippedItem();

                if(targetPotara.stackSize <= 0)
                    target.destroyCurrentEquippedItem();

                NetworkUtility.sendServerMessage(sender, "§a", "npcdbc.potaraFusion", " §e", target.getCommandSenderName());
                NetworkUtility.sendServerMessage(target, "§a", "npcdbc.potaraFusion", " §e", sender.getCommandSenderName());

                EnumPotaraTypes potaraType = EnumPotaraTypes.getPotaraFromMeta(tier);

                StatusEffectController.getInstance().applyEffect(sender, Effects.POTARA, potaraType.getLength() * 60, (byte) potaraType.getMeta());
                StatusEffectController.getInstance().applyEffect(target, Effects.POTARA, potaraType.getLength() * 60, (byte) potaraType.getMeta());

                DBCData.fusePlayers(target, sender, potaraType.getLength());

                return true;
            }
        }

        FuseRequest existing = null;
        if(fuseRequest.containsKey(uuidSender))
            existing = fuseRequest.get(uuidSender);

        if(existing == null || senderRequest.newRequest(existing)){
            NetworkUtility.sendServerMessage(target, "§e", "npcdbc.potaraRequest", " §a", sender.getCommandSenderName());
            fuseRequest.put(uuidSender, senderRequest);
        }
        return false;
    }

    public static void checkNearbyPlayers(EntityPlayer player){
        ItemStack potara = player.getCurrentArmor(3);
        if(potara == null)
            return;
        if(!(potara.getItem() instanceof ItemPotara))
            return;

        if(!ItemPotara.isSplit(potara))
            return;

        if(!JRMCoreH.PlyrSettingsB(player, 4))
            return;

        if(hasNofuse(player)) {
            JRMCoreH.PlyrSettingsRem(player, 4);
            NetworkUtility.sendServerMessage(player, "§c", player.getCommandSenderName(), " ", "npcdbc.noFuse");
        }

        int tier = potara.getItemDamage();
        String hash = ItemPotara.getHash(potara);
        boolean isRight = ItemPotara.isRightSide(potara);
        double range = 8;

        List<EntityPlayer> nearbyPlayers = player.worldObj.getEntitiesWithinAABB(EntityPlayer.class, player.boundingBox.expand(range, range, range));

        for(EntityPlayer nearbyPlayer : nearbyPlayers){
            if(nearbyPlayer == player)
                continue;

            if(doesPlayerHaveEarring(nearbyPlayer, tier, !isRight, hash)){
                if(nearbyPlayer.isSneaking()){
                    NetworkUtility.sendServerMessage(nearbyPlayer, "§a", "npcdbc.potaraFusion", " §e", player.getCommandSenderName());
                    NetworkUtility.sendServerMessage(player, "§a", "npcdbc.potaraFusion", " §e", nearbyPlayer.getCommandSenderName());


                    EnumPotaraTypes potaraType = EnumPotaraTypes.getPotaraFromMeta(tier);

                    StatusEffectController.getInstance().applyEffect(player, Effects.POTARA, potaraType.getLength() * 60, (byte) potaraType.getMeta());
                    StatusEffectController.getInstance().applyEffect(nearbyPlayer, Effects.POTARA, potaraType.getLength() * 60, (byte) potaraType.getMeta());

                    DBCData.fusePlayers(player, nearbyPlayer, potaraType.getLength());

                }
                else {
                    NetworkUtility.sendServerMessage(player, "§a", "npcdbc.fusionFound");
                }
                return;
            }
        }

    }

    private static boolean hasNofuse(EntityPlayer player){
        DBCData data = DBCDataUniversal.getData(player);

        if(data == null)
            return false;

        String fusionString = data.getRawCompound().getString("jrmcFuzion");

        if (fusionString == null || fusionString.isEmpty() || fusionString.contains(","))
            return false;

        return Integer.parseInt(fusionString) > 0;
    }

    private static boolean doesPlayerHaveEarring(EntityPlayer player, int tier, boolean isRight, String hashToCheck){
        ItemStack potara = player.getCurrentArmor(3);
        if(potara == null)
            return false;
        if(!(potara.getItem() instanceof ItemPotara))
            return false;

        if(!ItemPotara.isSplit(potara))
            return false;

        if(!JRMCoreH.PlyrSettingsB(player, 4))
            return false;

        int wornTier = potara.getItemDamage();
        String wornHash = ItemPotara.getHash(potara);
        boolean wearingRight = ItemPotara.isRightSide(potara);
        return tier == wornTier && wornHash.equals(hashToCheck) && wearingRight == isRight;
    }
}
