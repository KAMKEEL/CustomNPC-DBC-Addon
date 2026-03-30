package kamkeel.npcdbc.mixins.late.impl.dbc.race;

import JinRyuu.JRMCore.JRMCoreGuiScreen;
import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.controllers.RaceController;
import kamkeel.npcdbc.client.gui.dbc.creator.CharacterCreationGui;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.race.helper.RaceSelectorHelper;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.player.race.DBCSelectRace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.FMLCommonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Race creator GUI integration mixin — injects into JRMCoreGuiScreen
 * to add custom races from RaceController to the DBC character creator.
 * <p>
 * Uses the Hybrid strategy (Approach C):
 * <ul>
 *   <li>FIELD redirects for 11 race-indexed JRMCoreH arrays</li>
 *   <li>METHOD redirects for 4 stat preview functions (prevents config array AIOOBE)</li>
 *   <li>INJECT hooks for init seeding and finalize interception</li>
 * </ul>
 * All expanded arrays are built/cached by {@link RaceSelectorHelper}.
 * <p>
 * Finalize flow (button 13):
 * <ol>
 *   <li>HEAD inject: if RaceSlcted >= 6, capture the real custom race ID,
 *       send addon race packet immediately, then clamp RaceSlcted to 0</li>
 *   <li>Vanilla runs: setdns() encodes race "00", jrmcDataFC sends DNS,
 *       Char(0, 0) sets DBC race to Human on server, closeScreen()</li>
 * </ol>
 */
@Mixin(value = JRMCoreGuiScreen.class, remap = false)
public abstract class MixinJRMCoreGuiScreenCreator extends GuiScreen {

    @Shadow public int guiID;
    @Shadow public static int RaceSlcted;

    // ====================================================================
    // FIELD REDIRECTS
    //
    // Vanilla overrides (drawScreen, actionPerformed) require remap = true.
    // DBC-only methods (RaceSlctF, setchangerace, etc.) must stay remap = false.
    // Mixed method lists are split into separate redirects to satisfy both.
    // ====================================================================

    // --- Races ---

    @Redirect(method = "drawScreen", remap = true,
        at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;Races:[Ljava/lang/String;", remap = false))
    private String[] npcdbc$expandRaces_vanilla() {
        return RaceSelectorHelper.getRaces();
    }

    @Redirect(method = {"RaceSlctF", "RaceSlctB"},
        at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;Races:[Ljava/lang/String;"))
    private static String[] npcdbc$expandRaces_dbc() {
        return RaceSelectorHelper.getRaces();
    }

    // --- RaceAllow ---

    @Redirect(method = "drawScreen", remap = true,
        at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;RaceAllow:[Ljava/lang/String;", remap = false))
    private String[] npcdbc$expandRaceAllow_vanilla() {
        return RaceSelectorHelper.getRaceAllow();
    }

    @Redirect(method = {"RaceSlctF", "RaceSlctB"},
        at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;RaceAllow:[Ljava/lang/String;"))
    private static String[] npcdbc$expandRaceAllow_dbc() {
        return RaceSelectorHelper.getRaceAllow();
    }

    // --- customSknLimits ---

    @Redirect(method = {"drawScreen", "actionPerformed(Lnet/minecraft/client/gui/GuiButton;)V"}, remap = true,
        at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;customSknLimits:[[I", remap = false))
    private int[][] npcdbc$expandCustomSknLimits_vanilla() {
        return RaceSelectorHelper.getCustomSknLimits();
    }

    @Redirect(method = "setchangerace",
        at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;customSknLimits:[[I"))
    private static int[][] npcdbc$expandCustomSknLimits_dbc() {
        return RaceSelectorHelper.getCustomSknLimits();
    }

    // --- customSknLimitsBCP (actionPerformed only) ---

    @Redirect(method = "actionPerformed(Lnet/minecraft/client/gui/GuiButton;)V", remap = true,
        at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;customSknLimitsBCP:[I", remap = false))
    private int[] npcdbc$expandCustomSknLimitsBCP() {
        return RaceSelectorHelper.getCustomSknLimitsBCP();
    }

    // --- defeyecols ---

    @Redirect(method = "actionPerformed(Lnet/minecraft/client/gui/GuiButton;)V", remap = true,
        at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;defeyecols:[[I", remap = false))
    private int[][] npcdbc$expandDefEyeCols_vanilla() {
        return RaceSelectorHelper.getDefEyeCols();
    }

    @Redirect(method = "setchangeeyecol",
        at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;defeyecols:[[I"))
    private static int[][] npcdbc$expandDefEyeCols_dbc() {
        return RaceSelectorHelper.getDefEyeCols();
    }

    // --- defbodycols (DBC-only method) ---

    @Redirect(method = "setchangebodycol",
        at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;defbodycols:[[[I"))
    private static int[][][] npcdbc$expandDefBodyCols() {
        return RaceSelectorHelper.getDefBodyCols();
    }

    // --- RaceGenders ---

    @Redirect(method = "drawScreen", remap = true,
        at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;RaceGenders:[I", remap = false))
    private int[] npcdbc$expandRaceGenders_vanilla() {
        return RaceSelectorHelper.getRaceGenders();
    }

    @Redirect(method = "setchangerace",
        at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;RaceGenders:[I"))
    private static int[] npcdbc$expandRaceGenders_dbc() {
        return RaceSelectorHelper.getRaceGenders();
    }

    // --- RaceCanHaveHair (drawScreen only) ---

    @Redirect(method = "drawScreen", remap = true,
        at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;RaceCanHaveHair:[Ljava/lang/String;", remap = false))
    private String[] npcdbc$expandRaceCanHaveHair() {
        return RaceSelectorHelper.getRaceCanHaveHair();
    }

    // --- RaceCanHavePwr ---

    @Redirect(method = "drawScreen", remap = true,
        at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;RaceCanHavePwr:[Ljava/lang/String;", remap = false))
    private String[] npcdbc$expandRaceCanHavePwr_vanilla() {
        return RaceSelectorHelper.getRaceCanHavePwr();
    }

    @Redirect(method = "Slct",
        at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;RaceCanHavePwr:[Ljava/lang/String;"))
    private static String[] npcdbc$expandRaceCanHavePwr_dbc() {
        return RaceSelectorHelper.getRaceCanHavePwr();
    }

    // --- RaceCustomSkin (drawScreen only) ---

    @Redirect(method = "drawScreen", remap = true,
        at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;RaceCustomSkin:[I", remap = false))
    private int[] npcdbc$expandRaceCustomSkin() {
        return RaceSelectorHelper.getRaceCustomSkin();
    }

    // --- RaceHairColor (drawScreen only) ---

    @Redirect(method = "drawScreen", remap = true,
        at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;RaceHairColor:[I", remap = false))
    private int[] npcdbc$expandRaceHairColor() {
        return RaceSelectorHelper.getRaceHairColor();
    }

    // ====================================================================
    // METHOD REDIRECTS — clamp race for stat preview + Char(0, race) safety
    // ====================================================================

    @Redirect(method = "actionPerformed(Lnet/minecraft/client/gui/GuiButton;)V", remap = true,
        at = @At(value = "INVOKE",
            target = "LJinRyuu/JRMCore/JRMCoreH;Char(BB)V", remap = false))
    private void npcdbc$clampCharRace_action(byte slot, byte value) {
        if (slot == 0 && value >= RaceSelectorHelper.VANILLA_RACE_COUNT) {
            JRMCoreH.Char(slot, (byte) 0);
            return;
        }
        JRMCoreH.Char(slot, value);
    }

    @Redirect(method = "drawScreen", remap = true,
        at = @At(value = "INVOKE",
            target = "LJinRyuu/JRMCore/JRMCoreH;Char(BB)V", remap = false))
    private void npcdbc$clampCharRace_draw(byte slot, byte value) {
        if (slot == 0 && value >= RaceSelectorHelper.VANILLA_RACE_COUNT) {
            JRMCoreH.Char(slot, (byte) 0);
            return;
        }
        JRMCoreH.Char(slot, value);
    }

    @Redirect(method = "drawScreen", remap = true,
        at = @At(value = "INVOKE",
            target = "LJinRyuu/JRMCore/JRMCoreH;attributeStart(IIII)I", remap = false))
    private int npcdbc$redirectAttributeStart(int powerType, int attribute, int race, int classID) {
        return JRMCoreH.attributeStart(powerType, attribute, RaceSelectorHelper.clampRaceForStats(race), classID);
    }

    @Redirect(method = "drawScreen", remap = true,
        at = @At(value = "INVOKE",
            target = "LJinRyuu/JRMCore/JRMCoreH;stat(Lnet/minecraft/entity/Entity;IIIIIIF)I", remap = false))
    private int npcdbc$redirectStat(Entity player, int attributeID, int powerType,
                                           int stat, int attribute, int race, int classID, float skillBonus) {
        return JRMCoreH.stat(player, attributeID, powerType, stat, attribute,
            RaceSelectorHelper.clampRaceForStats(race), classID, skillBonus);
    }

    @Redirect(method = "drawScreen", remap = true,
        at = @At(value = "INVOKE",
            target = "LJinRyuu/JRMCore/JRMCoreH;statInc(IIIIIF)F", remap = false))
    private float npcdbc$redirectStatInc(int powerType, int stat, int attribute,
                                                int race, int classID, float skillBonus) {
        return JRMCoreH.statInc(powerType, stat, attribute,
            RaceSelectorHelper.clampRaceForStats(race), classID, skillBonus);
    }

    // ====================================================================
    // INIT — seed RaceSlcted from addon player data when creator opens
    // ====================================================================

    @Inject(method = "initGui", at = @At("RETURN"), remap = true)
    private void npcdbc$seedCustomRaceOnInit(CallbackInfo ci) {
        if (this.guiID != 0) return;

        if (ConfigDBCClient.EnhancedGui) {
            FMLCommonHandler.instance().showGuiScreen(new CharacterCreationGui());
            return;
        }

        RaceSelectorHelper.setPreviewActive(true);

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return;

        String raceKey = DBCData.getClient().currentRaceKey;
        if (raceKey == null || raceKey.isEmpty()) return;

        Race addonRace = RaceController.getInstance().getByName(raceKey);
        if (addonRace == null) return;

        int customIndex = RaceController.Instance.getIndex(addonRace.getName());
        if (customIndex >= 0) {
            RaceSlcted = RaceSelectorHelper.VANILLA_RACE_COUNT + customIndex;
            RaceSelectorHelper.setPreviewRaceIndex(RaceSlcted);
        } else {
            RaceSelectorHelper.clearPreviewRace();
        }
    }

    @Inject(method = "actionPerformed(Lnet/minecraft/client/gui/GuiButton;)V", at = @At("RETURN"), remap = true)
    private void npcdbc$updatePreviewRaceAfterButtons(GuiButton button, CallbackInfo ci) {
        if (this.guiID == 0 && CharacterCreationGui.hasPendingReturn()) {
            FMLCommonHandler.instance().showGuiScreen(new CharacterCreationGui());
            return;
        }

        if (this.guiID != 0) {
            return;
        }

        if (RaceSelectorHelper.isCustomRaceIndex(RaceSlcted)) {
            RaceSelectorHelper.setPreviewRaceIndex(RaceSlcted);
        } else {
            RaceSelectorHelper.clearPreviewRace();
        }
    }

    // ====================================================================
    // FINALIZE — intercept button 13 (creator done)
    //
    // HEAD: capture custom race ID, clamp RaceSlcted to 0 for vanilla
    // RETURN: send addon race packet with the real race ID
    // ====================================================================

    @Inject(method = "actionPerformed(Lnet/minecraft/client/gui/GuiButton;)V", at = @At("HEAD"), remap = true)
    private void npcdbc$interceptCreatorFinalize(GuiButton button, CallbackInfo ci) {
        if (button.id != 13 || ConfigDBCClient.EnhancedGui) return;

        String currentRaceKey = null;
        if (RaceSelectorHelper.isCustomRaceIndex(RaceSlcted)) {
            Race customRace = RaceSelectorHelper.getCustomRaceByIndex(RaceSlcted);
            if (customRace != null) {
                currentRaceKey = customRace.getName();
            }
            RaceSlcted = 0;
        }

        DBCPacketHandler.Instance.sendToServer(new DBCSelectRace(currentRaceKey));
        RaceSelectorHelper.clearPreviewRace();
        RaceSelectorHelper.setPreviewActive(false);
    }

    @Override
    public void onGuiClosed() {
        boolean suspendingToEnhancedCreator = CharacterCreationGui.hasPendingReturn()
            && (this.guiID == JRMCoreGuiScreen.ID_COLOR_PICKER || (this.guiID >= 20 && this.guiID <= 24));
        if (!suspendingToEnhancedCreator) {
            CharacterCreationGui.clearPendingReturn();
            RaceSelectorHelper.setPreviewActive(false);
        }
        super.onGuiClosed();
    }
}
