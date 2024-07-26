package kamkeel.npcdbc.mixins.late.impl.npc;

import kamkeel.npcdbc.data.npc.DBCStats;
import kamkeel.npcdbc.mixins.late.INPCStats;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.DataStats;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DataStats.class)
public class MixinDBCStats implements INPCStats {

    @Shadow(remap = false) public EntityNPCInterface npc;

    @Unique
    private final DBCStats customNPC_DBCAddon$dbcStats = new DBCStats();



    @Inject(method = "writeToNBT", at = @At("HEAD"), remap = false)
    public void writeToNBT(NBTTagCompound nbttagcompound, CallbackInfoReturnable<NBTTagCompound> cir) {
        if(hasDBCData())
            customNPC_DBCAddon$dbcStats.writeToNBT(nbttagcompound);
    }

    @Inject(method = "readToNBT", at = @At("HEAD"), remap = false)
    public void readFromNBT(NBTTagCompound nbttagcompound, CallbackInfo ci){
        customNPC_DBCAddon$dbcStats.readFromNBT(nbttagcompound);
    }

    @Unique
    public DBCStats getDBCStats(){
        return customNPC_DBCAddon$dbcStats;
    }

    @Unique
    public boolean hasDBCData() {
        return npc instanceof EntityNPCInterface;
    }
}
