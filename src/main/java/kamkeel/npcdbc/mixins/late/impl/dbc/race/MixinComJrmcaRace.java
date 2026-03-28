package kamkeel.npcdbc.mixins.late.impl.dbc.race;

import JinRyuu.JRMCore.ComJrmca;
import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Makes /jrmca pass a custom race ID through the {@code race} parameter of
 * {@code JRMCoreH.attributeStart(...)} so the shared attributeStart mixin can
 * resolve the correct addon race without needing a player parameter.
 */
@Mixin(value = ComJrmca.class, remap = false)
public class MixinComJrmcaRace {

    @Unique
    private Race npcdbc$cachedCommandRace;

    @Inject(method = "processCommand", at = @At("HEAD"))
    private void npcdbc$cacheCommandRace(net.minecraft.command.ICommandSender commandSender,
                                         String[] stringArray,
                                         CallbackInfo ci) {
        npcdbc$cachedCommandRace = null;

        EntityPlayerMP targetPlayer;
        try {
            if (stringArray.length > 3) {
                targetPlayer = ((ComJrmca) (Object) this).getPlayer(commandSender, stringArray[3]);
            } else {
                targetPlayer = ((ComJrmca) (Object) this).getCommandSenderAsPlayer(commandSender);
            }
        } catch (Exception ignored) {
            return;
        }

        PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(targetPlayer);
        if (info != null && info.isCustomRace()) {
            npcdbc$cachedCommandRace = info.getRace();
        }
    }

    @Redirect(
        method = "processCommand",
        at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;attributeStart(IIII)I")
    )
    private int npcdbc$passCustomRaceId(int powerType, int attribute, int race, int classID) {
        int effectiveRace = npcdbc$cachedCommandRace != null ? npcdbc$cachedCommandRace.id : race;
        return JRMCoreH.attributeStart(powerType, attribute, effectiveRace, classID);
    }
}
