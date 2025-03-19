package kamkeel.npcdbc.mixins.late.impl.dbc.client;

import JinRyuu.DragonBC.common.Gui.DBCTalkGui;
import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DBCTalkGui.class)
public class MixinDBCTalkGui {

    // Mind fix
    @Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;statMindC()I", remap = false))
    private int drawScreenFix() {
        return JRMCoreH.statMindC() + DBCData.getClient().calculateMindBonuses();
    }
}
