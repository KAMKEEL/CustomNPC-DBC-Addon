package kamkeel.npcdbc.controllers;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.data.FuseRequest;
import kamkeel.npcdbc.items.Potara;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.UUID;

public class FusionHandler {

    public static HashMap<UUID, FuseRequest> fuseRequest = new HashMap<>();

    public static boolean requestFusion(EntityPlayer sender, EntityPlayer target, boolean rightSide, String hash, int tier){
        boolean senderFusion = JRMCoreH.PlyrSettingsB(sender, 4);
        boolean targetFusion = JRMCoreH.PlyrSettingsB(target, 4);

        if(!senderFusion){
            // Fusion is not on- Inform Sender
            Utility.sendMessage(sender, "§cYour fusion skill is not enabled");
            return false;
        }
        if(!targetFusion){
            // Target Fusion is not on- Inform Sender
            Utility.sendMessage(sender, String.format("§c%s§c does not have their fusion skill enabled", target.getCommandSenderName()));
            return false;
        }

        UUID uuidSender = Utility.getUUID(sender);
        UUID uuidTarget = Utility.getUUID(target);

        FuseRequest senderRequest = new FuseRequest(sender.getCommandSenderName(), target.getCommandSenderName(), rightSide, hash, tier);
        FuseRequest targetRequest = null;
        if(fuseRequest.containsKey(uuidTarget)){
            targetRequest = fuseRequest.get(uuidTarget);
            if(senderRequest.checkRequest(targetRequest)){
                if(sender.getHeldItem() == null || !(sender.getHeldItem().getItem() instanceof Potara)){
                    Utility.sendMessage(sender, "§cYou are not holding a potara");
                    return false;
                }
                if(target.getHeldItem() == null || !(target.getHeldItem().getItem() instanceof Potara)){
                    Utility.sendMessage(sender, "§cThey are not holding a potara");
                    return false;
                }
                ItemStack sendPotara = sender.getHeldItem();
                ItemStack targetPotara = target.getHeldItem();

                if(sendPotara.getItemDamage() != targetPotara.getItemDamage()) {
                    Utility.sendMessage(sender, "§cThe held potaras are not the same tier");
                    return false;
                }

                NBTTagCompound sendNBT = sendPotara.getTagCompound();
                NBTTagCompound targetNBT = targetPotara.getTagCompound();
                if(sendNBT == null || targetNBT == null) {
                    Utility.sendMessage(sender, "§cThe held potaras are not split");
                    return false;
                }
                if(!sendNBT.hasKey("Side") || !targetNBT.hasKey("Side")){
                    Utility.sendMessage(sender, "§cThe held potaras are not split");
                    return false;
                }

                if(sendNBT.getString("Side").equals(targetNBT.getString("Side"))){
                    Utility.sendMessage(sender, "§cThe held potaras are the same side");
                    return false;
                }

                String sendHash = sendNBT.hasKey("Hash") ? sendNBT.getString("Hash") : "";
                String targetHash = sendNBT.hasKey("Hash") ? sendNBT.getString("Hash") : "";
                if(!sendHash.equals(targetHash)) {
                    Utility.sendMessage(sender, "§cThe held potaras are not the same pair");
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

                Utility.sendMessage(sender, String.format("§aYou fuse with §e%s§a!", target.getCommandSenderName()));
                Utility.sendMessage(target, String.format("§aYou fuse with §e%s§a!", sender.getCommandSenderName()));
                return true;
            }
        }

        FuseRequest existing = null;
        if(fuseRequest.containsKey(uuidSender))
            existing = fuseRequest.get(uuidSender);

        if(existing == null || senderRequest.newRequest(existing)){
            Utility.sendMessage(target, String.format("%s §ahas requested to Potara Fuse", sender.getCommandSenderName()));
            fuseRequest.put(uuidSender, senderRequest);
        }
        return false;
    }
}
