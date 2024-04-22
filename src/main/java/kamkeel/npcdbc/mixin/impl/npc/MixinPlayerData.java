package kamkeel.npcdbc.mixin.impl.npc;

import kamkeel.npcdbc.data.PlayerFormData;
import kamkeel.npcdbc.mixin.IPlayerFormData;
import noppes.npcs.controllers.data.PlayerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;


@Mixin(PlayerData.class)
public abstract class MixinPlayerData implements IPlayerFormData {

    @Unique
    public PlayerFormData formData = new PlayerFormData((PlayerData)(Object)this);

    @Unique
    public boolean formUpdate = false;

    @Unique
    public PlayerFormData getPlayerFormData(){
        return formData;
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
