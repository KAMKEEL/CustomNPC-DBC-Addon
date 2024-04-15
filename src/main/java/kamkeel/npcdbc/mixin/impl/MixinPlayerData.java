package kamkeel.npcdbc.mixin.impl;

import kamkeel.npcdbc.data.PlayerCustomFormData;
import kamkeel.npcdbc.mixin.IPlayerFormData;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.PlayerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;


@Mixin(PlayerData.class)
public abstract class MixinPlayerData implements IPlayerFormData {
    @Shadow
    public EntityPlayer player;

    @Unique
    public PlayerCustomFormData customFormData = new PlayerCustomFormData(player);

    @Unique
    public PlayerCustomFormData getCustomFormData(){
        return customFormData;
    }
}
