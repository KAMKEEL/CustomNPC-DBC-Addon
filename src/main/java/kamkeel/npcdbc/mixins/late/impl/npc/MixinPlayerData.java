package kamkeel.npcdbc.mixins.late.impl.npc;

import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.mixins.late.IPlayerDBCInfo;
import noppes.npcs.controllers.data.PlayerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerData.class)
public abstract class MixinPlayerData implements IPlayerDBCInfo {
    @Unique
    public PlayerDBCInfo dbcInfo = new PlayerDBCInfo((PlayerData) (Object) this);

    @Unique
    public PlayerDBCInfo getPlayerDBCInfo() {
        return dbcInfo;
    }
}
