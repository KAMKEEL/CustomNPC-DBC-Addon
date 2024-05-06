package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JRMCore.i.SlotCustom;
import kamkeel.npcdbc.items.ItemPotara;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SlotCustom.class)
public abstract class MixinSlotCustom extends Slot {
    public MixinSlotCustom(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_) {
        super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
    }

    @Inject(method = "isItemValid", at=@At("HEAD"), remap = true, cancellable = true)
    public void isItemValid(ItemStack stack, CallbackInfoReturnable<Boolean> cir){
        if(getSlotIndex() < 3)
            return;

        Item item = stack.getItem();
        if(!(item instanceof ItemPotara))
            return;

        cir.cancel();

        if(ItemPotara.isSplit(stack))
            cir.setReturnValue(canWearPotara(!ItemPotara.isRightSide(stack)));
        else
            cir.setReturnValue(false);
    }

    private boolean canWearPotara(boolean checkLeft){

        boolean hasPotaraOnThatSide = false;

        for(int i = 0; i < inventory.getSizeInventory(); i++){
            ItemStack itemStack = inventory.getStackInSlot(i);
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
