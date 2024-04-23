package kamkeel.npcdbc.mixin.impl.npc;

import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.mixin.IPlayerDBCInfo;
import noppes.npcs.controllers.data.PlayerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;


@Mixin(PlayerData.class)
public abstract class MixinPlayerData implements IPlayerDBCInfo {

    @Unique
    public PlayerDBCInfo formData = new PlayerDBCInfo((PlayerData)(Object)this);

    @Unique
    public boolean formUpdate = false;

    @Unique
    public PlayerDBCInfo getPlayerDBCInfo(){
        return formData;
    }

    @Unique
    @Override
    public boolean getDBCInfoUpdate(){ return formUpdate;}

    @Unique
    @Override
    public void updateDBCInfo(){ formUpdate = true;}

    @Unique
    @Override
    public void endDBCInfo(){ formUpdate = false;}
}
