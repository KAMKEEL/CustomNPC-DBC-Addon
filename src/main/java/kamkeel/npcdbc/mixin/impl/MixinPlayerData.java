package kamkeel.npcdbc.mixin.impl;

import kamkeel.npcdbc.data.PlayerCustomFormData;
import kamkeel.npcdbc.mixin.IPlayerFormData;
import noppes.npcs.controllers.data.PlayerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;


@Mixin(PlayerData.class)
public abstract class MixinPlayerData implements IPlayerFormData {

    @Unique
    public PlayerCustomFormData customFormData = new PlayerCustomFormData();

    @Unique
    public PlayerCustomFormData getCustomFormData(){
        return customFormData;
    }
}
