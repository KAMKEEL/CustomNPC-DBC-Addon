package kamkeel.npcdbc.mixin.impl.npc;

import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixin.INPCDisplay;
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

@Mixin(DataDisplay.class)
public class MixinDBCDisplay implements INPCDisplay {

    @Shadow(remap = false)
    public EntityNPCInterface npc;

    @Unique
    private DBCDisplay dbcDisplay;

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    public void init(EntityNPCInterface npc, CallbackInfo ci) {
        dbcDisplay = new DBCDisplay(npc);
    }

    @Inject(method = "writeToNBT", at = @At("HEAD"), remap = false)
    public void writeToNBT(NBTTagCompound nbttagcompound, CallbackInfoReturnable<NBTTagCompound> cir) {
        if (hasDBCData())
            dbcDisplay.writeToNBT(nbttagcompound);
    }

    @Inject(method = "readToNBT", at = @At("HEAD"), remap = false)
    public void readFromNBT(NBTTagCompound nbttagcompound, CallbackInfo ci) {
        dbcDisplay.readFromNBT(nbttagcompound);
    }

    @Unique
    public DBCDisplay getDBCDisplay() {
        return dbcDisplay;
    }

    @Unique
    public boolean hasDBCData() {
        return npc instanceof EntityNPCInterface;
    }
}
