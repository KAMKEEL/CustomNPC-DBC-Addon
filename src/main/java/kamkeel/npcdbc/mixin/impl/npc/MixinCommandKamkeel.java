package kamkeel.npcdbc.mixin.impl.npc;

import kamkeel.command.CommandKamkeel;
import kamkeel.command.CommandKamkeelBase;
import kamkeel.npcdbc.command.FormCommand;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixin.INPCDisplay;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.DataDisplay;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandKamkeel.class)
public abstract class MixinCommandKamkeel {

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    public void init(CallbackInfo ci) {
        registerCommand(new FormCommand());
    }

    @Shadow
    public void registerCommand(CommandKamkeelBase command) {}
}
