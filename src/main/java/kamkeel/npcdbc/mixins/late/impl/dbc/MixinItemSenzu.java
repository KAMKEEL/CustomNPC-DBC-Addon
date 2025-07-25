package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.DragonBC.common.Items.ItemSenzu;
import kamkeel.npcdbc.controllers.DBCEffectController;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemSenzu.class)
public class MixinItemSenzu {

    @Inject(method = "onPlayerStoppedUsing", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/server/JGPlayerMP;getAttributes()[I", shift = At.Shift.BEFORE), cancellable = true)
    public void callSenzuEvent(ItemStack itemStack, World world, EntityPlayer entityPlayer, int par4, CallbackInfo ci) {
        if (DBCEventHooks.onSenzuUsedEvent(new DBCPlayerEvent.SenzuUsedEvent(PlayerDataUtil.getIPlayer(entityPlayer))))
            ci.cancel();

        if (!DBCEffectController.getInstance().allowSenzuConsumption(entityPlayer))
            ci.cancel();
    }
}
