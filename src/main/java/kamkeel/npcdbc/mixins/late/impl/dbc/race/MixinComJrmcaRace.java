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

    /** Thread-scoped for the same reason as {@code MixinJRMCorePacHanSRace}: the command
     *  instance is a shared singleton, so this hand-off must not be plain instance state. */
    @Unique
    private static final ThreadLocal<Race> npcdbc$cachedCommandRace = new ThreadLocal<>();

    @Inject(method = "processCommand", at = @At("HEAD"))
    private void npcdbc$cacheCommandRace(net.minecraft.command.ICommandSender commandSender,
                                         String[] stringArray,
                                         CallbackInfo ci) {
        npcdbc$cachedCommandRace.remove();

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
            Race race = info.getRace();
            if (race != null)
                npcdbc$cachedCommandRace.set(race);
        }
    }

    @Inject(method = "processCommand", at = @At("RETURN"))
    private void npcdbc$clearCommandRace(net.minecraft.command.ICommandSender commandSender,
                                         String[] stringArray,
                                         CallbackInfo ci) {
        npcdbc$cachedCommandRace.remove();
    }

    @Redirect(
        method = "processCommand",
        at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;attributeStart(IIII)I")
    )
    private int npcdbc$passCustomRaceId(int powerType, int attribute, int race, int classID) {
        Race cached = npcdbc$cachedCommandRace.get();
        int effectiveRace = cached != null ? cached.id : race;
        return JRMCoreH.attributeStart(powerType, attribute, effectiveRace, classID);
    }
}
