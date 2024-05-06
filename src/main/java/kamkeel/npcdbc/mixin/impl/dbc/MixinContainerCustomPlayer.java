package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JRMCore.i.ContainerCustomPlayer;
import JinRyuu.JRMCore.i.InventoryCustomPlayer;
import kamkeel.npcdbc.items.ItemPotara;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ContainerCustomPlayer.class)
public abstract class MixinContainerCustomPlayer extends Container {

    @Unique
    private InventoryPlayer playerInv;
    @Unique
    private InventoryCustomPlayer customInv;

    @Inject(method = "<init>", at=@At("RETURN"))
    public void onConstruct(EntityPlayer player, InventoryPlayer inventoryPlayer, InventoryCustomPlayer inventoryCustom, CallbackInfo ci){

        playerInv = inventoryPlayer;
        customInv = inventoryCustom;
    }

    @Override
    @Unique
    protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection){
        Item item = stack.getItem();
        if(!(item instanceof ItemPotara))
            return super.mergeItemStack(stack, startIndex, endIndex, reverseDirection);

        if(startIndex >= 15)
            return super.mergeItemStack(stack, startIndex, endIndex, reverseDirection);

        if(!ItemPotara.isSplit(stack))
            return false;

        if(canWearPotara(!ItemPotara.isRightSide(stack)))
            return super.mergeItemStack(stack, startIndex, endIndex, reverseDirection);
        else
            return false;
    }

    private boolean canWearPotara(boolean checkLeft){

        boolean hasPotaraOnThatSide = false;

        for(int i = 0; i < customInv.getSizeInventory(); i++){
            ItemStack itemStack = customInv.getStackInSlot(i);
            if(itemStack == null)
                continue;
            Item item = itemStack.getItem();

            if(!(item instanceof ItemPotara))
                continue;

            if(ItemPotara.isRightSide(itemStack) ^ checkLeft)
                hasPotaraOnThatSide = true;
        }

        return !hasPotaraOnThatSide;
    }
}
