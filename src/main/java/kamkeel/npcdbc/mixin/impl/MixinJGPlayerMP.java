package kamkeel.npcdbc.mixin.impl;

import JinRyuu.JRMCore.server.JGPlayerMP;
import kamkeel.npcdbc.data.SyncedData.DBCData;
import kamkeel.npcdbc.events.DBCEventHooks;
import kamkeel.npcdbc.events.DBCPlayerEvent;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = JGPlayerMP.class, remap = false)
public class MixinJGPlayerMP {

    @Shadow
    public EntityPlayer player;
    @Shadow
    private NBTTagCompound nbt;

    /**
     * @param value
     * @author
     * @reason
     */
    @Overwrite
    public void setState(int value) {
        if (DBCEventHooks.onFormChangeEvent(new DBCPlayerEvent.FormChangeEvent(Utility.getIPlayer(player), DBCData.get(player).State, value)))
            return;
        this.nbt.setByte("jrmcState", (byte) value);

    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void setState2(int value) {
        if (DBCEventHooks.onFormChangeEvent(new DBCPlayerEvent.FormChangeEvent(Utility.getIPlayer(player), DBCData.get(player).State, value)))
            return;
        this.nbt.setByte("jrmcState2", (byte) value);

    }
}
