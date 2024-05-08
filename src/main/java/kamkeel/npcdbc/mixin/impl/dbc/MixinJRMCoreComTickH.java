package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JRMCore.JRMCoreComTickH;
import JinRyuu.JRMCore.i.InventoryCustomPlayer;
import JinRyuu.JRMCore.server.JGPlayerMP;
import kamkeel.npcdbc.CommonProxy;
import kamkeel.npcdbc.items.ItemPotara;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JRMCoreComTickH.class, remap = false)
public class MixinJRMCoreComTickH {


    @Inject(method = "updatePlayersData", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreHDBC;DBCsizeBasedOnRace(IIZ)F", shift = At.Shift.BEFORE))
    public void setCurrentTickPlayerServer(MinecraftServer server, int playerID, EntityPlayerMP player, JGPlayerMP jgPlayer, NBTTagCompound nbt, CallbackInfo ci) {
        CommonProxy.CurrentJRMCTickPlayer = player;
    }

    @Redirect(method = "updatePlayersData", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/i/InventoryCustomPlayer;setInventorySlotContents(ILnet/minecraft/item/ItemStack;)V"))
    public void preventPotaraDelete(InventoryCustomPlayer instance, int slot, ItemStack stack) {
        if(slot > 2 && stack != null && !(stack.getItem() instanceof ItemPotara)){
            instance.setInventorySlotContents(slot, (ItemStack)null);
        }
    }
}
