package kamkeel.npcdbc.mixin.impl.npc;

import kamkeel.addon.DBCAddon;
import kamkeel.npcdbc.data.PlayerCustomFormData;
import kamkeel.npcdbc.mixin.IPlayerFormData;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.data.PlayerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;


@Mixin(PlayerData.class)
public abstract class MixinPlayerData implements IPlayerFormData {

    @Unique
    public PlayerCustomFormData customFormData = new PlayerCustomFormData((PlayerData)(Object)this);

    @Unique
    public boolean formUpdate = false;

    @Unique
    public PlayerCustomFormData getCustomFormData(){
        return customFormData;
    }

    @Unique
    @Override
    public boolean getFormUpdate(){ return formUpdate;}

    @Unique
    @Override
    public void updateFormInfo(){ formUpdate = true;}

    @Unique
    @Override
    public void finishFormInfo(){ formUpdate = false;}
}
