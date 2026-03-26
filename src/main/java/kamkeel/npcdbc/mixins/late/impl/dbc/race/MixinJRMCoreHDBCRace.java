package kamkeel.npcdbc.mixins.late.impl.dbc.race;

import JinRyuu.JRMCore.JRMCoreHDBC;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.dbcdata.DBCDataRace;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.race.progression.FormTree;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.player.form.DBCSelectFormBranch;
import kamkeel.npcdbc.util.PlayerDataUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Intercepts DBC action slot 2 ({@code Action_TransformType}) for custom races.
 *
 * <p>Vanilla slot 2 semantics:
 * <ul>
 *   <li>{@code !action} — returns a label showing the current transformation type variant</li>
 *   <li>{@code action}  — cycles to the next variant type (does NOT trigger the transform itself)</li>
 * </ul>
 *
 * <p>Custom race mapping:
 * <ul>
 *   <li><b>Multi-branch races:</b> slot 2 cycles the active branch (analogous to SSJ→SSG→SSB).
 *       The server-side {@link DBCSelectFormBranch} handler auto-selects the first unlocked form
 *       in the new branch so the next ascend triggers the correct form.</li>
 *   <li><b>Single-branch / no-branch races:</b> slot 2 cycles the selected form within the
 *       available unlocked forms (analogous to how some races only have one variant line).</li>
 * </ul>
 */
@Mixin(value = JRMCoreHDBC.class, remap = false)
public class MixinJRMCoreHDBCRace {

    @Inject(method = "action", at = @At("HEAD"), cancellable = true)
    private static void npcdbc$customRaceTransformSlot(int d, boolean action, boolean black,
                                                       CallbackInfoReturnable<String> cir) {
        if (d != 2)
            return;
        if (FMLCommonHandler.instance().getEffectiveSide() != Side.CLIENT)
            return;

        DBCDataRace raceData = DBCData.getClient().addonRace;
        if (raceData == null || !raceData.isCustomRace())
            return;

        Race race = raceData.getRace();
        if (race == null || raceData.getUnlockedForms().isEmpty())
            return;

        PlayerDBCInfo formData = PlayerDataUtil.getClientDBCInfo();
        if (formData == null)
            return;

        if (!action) {
            // ── Label path: show current branch/form info ──
            String label = race.getMenuName();
            FormTree.Branch branch = race.skill.resolveActiveBranch(race.formTree, raceData.getRacialSkillLevel(),
                    formData.selectedFormBranch);

            if (branch != null && !branch.getName().isEmpty())
                label += "\n" + branch.getName();

            cir.setReturnValue(label);
        } else {
            // ── Action path: cycle to next unlocked branch ──
            // Multi-branch: cycle to next unlocked branch
            int nextIdx = race.skill.getNextUnlockedBranchIndex(race.formTree, raceData.getRacialSkillLevel(),
                    formData.selectedFormBranch);

            if (nextIdx >= 0)
                DBCPacketHandler.Instance.sendToServer(new DBCSelectFormBranch(nextIdx));
            
            cir.setReturnValue("");
        }
    }
}
