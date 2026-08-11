package kamkeel.npcdbc.mixins.late.impl.dbc.race;

import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.JRMCorePacHanS;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JRMCorePacHanS.class, remap = false)
public class MixinJRMCorePacHanSRace {

    /**
     * {@code JRMCore.phs} is a single shared instance and FML 1.7.10 message handlers run
     * on per-connection netty threads, so this hand-off between the HEAD inject and the
     * redirect below must not be plain instance state — two players finalizing character
     * creation at once would otherwise cross wires and hand one player the other's race ID.
     */
    @Unique
    private static final ThreadLocal<Race> npcdbc$cachedCreationRace = new ThreadLocal<>();

    @Inject(method = "handleChar", at = @At("HEAD"), remap = false)
    private void npcdbc$cacheCreationRace(byte b, int b2, EntityPlayer p, CallbackInfo ci) {
        npcdbc$cachedCreationRace.remove();

        if (p == null || p.worldObj == null || p.worldObj.isRemote) {
            return;
        }

        DBCData data = DBCData.get(p);
        if (data == null || data.currentRaceKey == null || data.currentRaceKey.isEmpty()) {
            return;
        }

        Race race = data.addonRace.getRace();
        if (race != null)
            npcdbc$cachedCreationRace.set(race);
    }

    @Inject(method = "handleChar", at = @At("RETURN"), remap = false)
    private void npcdbc$clearCreationRace(byte b, int b2, EntityPlayer p, CallbackInfo ci) {
        npcdbc$cachedCreationRace.remove();
    }

    @Redirect(
        method = "handleChar",
        at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;attributeStart(IIII)I"),
        remap = false
    )
    private int npcdbc$passCustomRaceIdToAttributeStart(int powerType, int attribute, int race, int classID) {
        Race cached = npcdbc$cachedCreationRace.get();
        int effectiveRace = cached != null ? cached.id : race;
        return JRMCoreH.attributeStart(powerType, attribute, effectiveRace, classID);
    }

    /**
     * Replaces vanilla racial skill TP-cost and max-level handling for active custom races
     * while preserving the native `jrmcSSltX` / `TRn` storage path.
     *
     * This hook runs before vanilla calls `skillTPCost_X(...)` for racial skill upgrades.
     * For a custom race player upgrading slot `b2 == 100` via `handleStats3`:
     * - reads the current `TRn` string from `DBCData.RacialSkills`
     * - computes the next level
     * - applies addon `RaceSkill` max-level and per-level TP cost
     * - writes the upgraded `TRn` string back into `jrmcSSltX`
     * - deducts TP
     * - cancels the vanilla branch so carrier-race Human/vanilla caps are not applied
     */
    @Inject(method = "handleStats3", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;skillTPCost_X(Ljava/lang/String;I[[I)I", shift = At.Shift.BEFORE), cancellable = true, remap = false)
    private void handleCustomRaceRacialSkillUpgrade(byte b, byte b2, byte b3, EntityPlayer p, CallbackInfo ci) {
        if (p.worldObj.isRemote || b != 3 || b2 != 100 || b3 != 1)
            return;

        DBCData data = DBCData.get(p);
        PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(p);
        if (data == null || info == null || !info.isCustomRace())
            return;

        Race race = info.getRace();
        if (race == null || race.skill == null)
            return;

        // substring(0, 2) below needs at least the 2-char TRn prefix.
        String currentSkill = data.RacialSkills;
        if (currentSkill == null || currentSkill.length() < 2 || currentSkill.contains("pty"))
            return;

        if (!JRMCoreConfig.dat5711) {
            ci.cancel();
            return;
        }

        int currentLevel = data.addonRace.getRacialSkillLevel();
        int nextLevel = currentLevel + 1;
        if (nextLevel > race.skill.getMaxLevel()) {
            ci.cancel();
            return;
        }

        int currentMindSpent = getSpentMindOnSkills(data) + race.skill.getTotalMindCost(currentLevel) + getSpentMindOnCustomSlot(data);
        int nextMindRequirement = race.skill.getMindCost(nextLevel);
        if (nextMindRequirement < 0 || !JRMCoreH.canAffordSkill(data.MND, currentMindSpent + nextMindRequirement)) {
            p.addChatMessage(new ChatComponentTranslation(JRMCoreH.trlai("jrmc", "nomindleft"), new Object[0]).setChatStyle(getChatStyle()));
            ci.cancel();
            return;
        }

        int tpCost = race.skill.getTPCost(nextLevel);
        if (tpCost < 0 || data.TP < tpCost) {
            ci.cancel();
            return;
        }

        // TRn format is a 2-char prefix followed by the level suffix. SklLvlX returns
        // 1 + suffix, and getRacialSkillLevel() subtracts that 1 back off, so storing
        // nextLevel verbatim round-trips to nextLevel.
        String upgradedSkill = currentSkill.substring(0, 2) + nextLevel;
        data.getRawCompound().setString("jrmcSSltX", data.RacialSkills = upgradedSkill);
        data.getRawCompound().setInteger("jrmcTpint", data.TP = data.TP - tpCost);

        int newBranchIndex = race.skill.getBranchIndexForLevel(race.formTree, nextLevel);
        if (newBranchIndex >= 0 && newBranchIndex != info.getSelectedFormBranch()) {
            info.setSelectedFormBranch(newBranchIndex);
            info.setSelectedForm(race.skill.getFirstUnlockedFormInBranch(race.formTree, nextLevel, newBranchIndex));
            info.updateClient();
        }
        
        ci.cancel();
    }

    private int getSpentMindOnSkills(DBCData data) {
        String[] skills = data.Skills == null ? new String[0] : data.Skills.split(",");
        return JRMCoreH.skillSlot_SpentMindRequirement(skills, JRMCoreH.DBCSkillsIDs, JRMCoreH.DBCSkillMindCost);
    }

    private int getSpentMindOnCustomSlot(DBCData data) {
        return JRMCoreH.skillSlot_SpentMindRequirement(data.getRawCompound().getString("jrmcSSltY"), JRMCoreH.vlblCSkls, JRMCoreH.NCRacialSkillMindCost);
    }

    private ChatStyle getChatStyle() {
        ChatStyle style = new ChatStyle();
        style.setColor(EnumChatFormatting.RED);
        return style;
    }

    @Inject(method = "handleStats3", at = @At("TAIL"), remap = false)
    private void refreshCustomRaceAfterSkillUpgrade(byte b, byte b2, byte b3, EntityPlayer p, CallbackInfo ci) {
        if (p.worldObj.isRemote || b != 3 || b2 != 100 || b3 != 1)
            return;

        PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(p);
        if (info == null || !info.isCustomRace())
            return;

        DBCData data = DBCData.get(p);
        if (data == null || !data.addonRace.isCustomRace())
            return;

        info.updateClient();
        data.saveNBTData(true);
    }
}
