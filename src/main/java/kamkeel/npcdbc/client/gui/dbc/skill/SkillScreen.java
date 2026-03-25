package kamkeel.npcdbc.client.gui.dbc.skill;

import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.JRMCoreGuiButtons00;
import JinRyuu.JRMCore.JRMCoreGuiButtons01;
import JinRyuu.JRMCore.JRMCoreGuiButtonsA1;
import JinRyuu.JRMCore.JRMCoreGuiButtonsA3;
import JinRyuu.JRMCore.JRMCoreGuiScreen;
import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.client.ColorMode;
import kamkeel.npcdbc.client.gui.dbc.AbstractJRMCGui;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * Addon-owned replacement for the DBC skill screen (vanilla guiID 11 / 16).
 * GuiID 19 (Arcosian color customization) is left to vanilla.
 */
public class SkillScreen extends AbstractJRMCGui {

    private static final int VANILLA_SKILL_GUI_ID = 11;
    private static final int X_SIZE = 256;
    private static final int Y_SIZE = 159;
    private static final int VISIBLE_ROWS = 10;

    private static final int BTN_LEARN_SKILLS = 9001;
    private static final int BTN_BACK_OVERVIEW = 9002;
    private static final int BTN_ARCOSIAN_INLINE = 9003;
    private static final int BTN_CONFIRM_YES = 9010;
    private static final int BTN_CONFIRM_NO = 9011;
    private static final int BTN_LEARN_PREV_PAGE = 9020;
    private static final int BTN_LEARN_NEXT_PAGE = 9021;
    private static final int BTN_UPGRADE_BASE = 9100;
    private static final int BTN_DELETE_BASE = 9300;
    private static final int BTN_LEARN_BASE = 9500;
    private static final int BTN_SCROLL_UP = 43;
    private static final int BTN_SCROLL_DOWN = 44;
    private static final int BTN_SCROLLBAR = 1000000;
    private static final int SCROLL_TRACK_WIDTH = 15;
    private static final int SCROLL_TRACK_HEIGHT = 115;
    private static final int SCROLL_MIN_THUMB_HEIGHT = 12;

    /** Texture used by vanilla for the solid-white tooltip background. */
    private static final ResourceLocation TOOLTIP_BG = new ResourceLocation("jinryuumodscore:allw.png");
    private static final int TOOLTIP_MAX_WIDTH = 200;
    private static final String TOOLTIP_TEXT_PREFIX = "§8";

    private final SkillScreenState state;
    private final SkillActionGateway gateway;
    private List<SkillRowModel> currentRows;
    private int guiLeft;
    private int guiTop;

    /**
     * First-wins tooltip text queued during label drawing.
     * Only the first hover match per frame is kept, matching vanilla behavior
     * ({@code detailList.get(0)} in JRMCoreGuiScreen).
     * Cleared at the start of each drawScreen and rendered at the very end.
     */
    private String pendingTooltip;

    /**
     * Dynamic buttons that are rebuilt every frame to track scroll position.
     * Cleared and re-added at the start of each drawScreen to prevent
     * accumulation and stale-position bugs.
     */
    private final ArrayList<GuiButton> dynamicButtons = new ArrayList<>();

    public SkillScreen() {
        super(VANILLA_SKILL_GUI_ID);
        this.state = new SkillScreenState();
        this.gateway = new SkillActionGateway(state);
    }

    // ════════════════════════════════════════════════════════════════
    // Helpers
    // ════════════════════════════════════════════════════════════════

    private String textColorPrefix() {
        return ConfigDBCClient.DarkMode ? "§7" : "";
    }

    private void addDynButton(GuiButton button) {
        dynamicButtons.add(button);
        this.buttonList.add(button);
    }

    // ════════════════════════════════════════════════════════════════
    // Lifecycle
    // ════════════════════════════════════════════════════════════════

    @Override
    public void initGui() {
        super.initGui();
        dynamicButtons.clear();
        this.guiLeft = guiWidthOffset;
        this.guiTop = guiHeightOffset;

        // MF-9: vanilla initGui syncs player data
        JRMCoreH.jrmct(1);
        JRMCoreH.jrmct(3);

        // MF-23: vanilla resets scroll on initGui
        state.setScrollOffset(0);
        state.setScrollbarPosition(0.0f);

        rebuildRows();
        // Dynamic buttons (mode + skill + scroll) are rebuilt every frame in drawScreen
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        pendingTooltip = null;

        // ── Clear and rebuild dynamic buttons every frame ──
        this.buttonList.removeAll(dynamicButtons);
        dynamicButtons.clear();

        addModeButtons();

        switch (state.getMode()) {
            case OVERVIEW:
                addOverviewButtons();
                break;
            case LEARN_SKILLS:
                addLearnSkillsButtons();
                break;
        }

        if (state.isConfirmationActive()) {
            addConfirmationButtons();
        }

        // ── Draw: background + all buttons ──
        super.drawScreen(mouseX, mouseY, partialTicks);

        // ── Draw text labels (no button creation after this point) ──
        switch (state.getMode()) {
            case OVERVIEW:
                drawOverviewLabels(mouseX, mouseY);
                break;
            case LEARN_SKILLS:
                drawLearnSkillsLabels(mouseX, mouseY);
                break;
        }

        if (state.isConfirmationActive()) {
            drawConfirmationOverlay(mouseX, mouseY);
            drawConfirmationButtonsOnTop(mouseX, mouseY);
        }

        drawPendingTooltip(mouseX, mouseY);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        // MF-22 revised: rebuild every tick so the UI reflects server-authoritative
        // data as soon as jrmct(3) / saveNBTData responses arrive on the client.
        rebuildRows();
    }

    // ════════════════════════════════════════════════════════════════
    // Overview — Button creation (runs before super.drawScreen)
    // ════════════════════════════════════════════════════════════════

    private void addOverviewButtons() {
        if (currentRows == null || currentRows.isEmpty())
            return;

        int totalRows = currentRows.size();
        int scrollOffset = state.getScrollOffset();

        int drawnCount = 0;
        for (int i = scrollOffset; i < totalRows && drawnCount < VISIBLE_ROWS; i++) {
            SkillRowModel row = currentRows.get(i);
            int skillID = drawnCount + 1;
            boolean isRacialRow = row.getType() == SkillRowModel.RowType.RACIAL
                    || row.getType() == SkillRowModel.RowType.RACIAL_Y;

            int rowY = isRacialRow
                    ? guiTop + 15 + skillID * 10
                    : guiTop + 20 + skillID * 10;

            // Vanilla line 6621: delete button for PlyrSkills rows
            if (row.canDelete()) {
                addDynButton(new JRMCoreGuiButtonsA3(
                        BTN_DELETE_BASE + i, guiLeft + 243, rowY - 2, 10, 3));
            }

            if (isRacialRow) {
                // Vanilla lines 6487-6494, 6541: gated by JRMCoreConfig.dat5711
                if (JRMCoreConfig.dat5711 && !row.isUpgradeLocked() && !row.isMaxed() && !JRMCoreH.isFused()) {
                    addDynButton(new JRMCoreGuiButtonsA3(
                            BTN_UPGRADE_BASE + i,
                            guiLeft - 10, guiTop + 13 + skillID * 10, 10, 2,
                            row.canAffordMind()));
                }
            } else {
                // Vanilla line 6629: upgrade button for owned skills
                if (!row.isTpLocked() && !row.isMaxed() && !JRMCoreH.isFused()) {
                    addDynButton(new JRMCoreGuiButtonsA3(
                            BTN_UPGRADE_BASE + i,
                            guiLeft - 10, guiTop + 18 + skillID * 10, 10, 2,
                            row.canUpgrade()));
                }
            }

            // Vanilla lines 6497-6507: Arcosian 392 button
            if (row.showArcosianButton() && !JRMCoreH.isFused()) {
                int inlineX = guiLeft + 10 + fontRendererObj.getStringWidth(
                        row.getDisplayName() + (row.getLevel() < 7 ? textLevel(row.getLevel()) : ""));
                int inlineY = guiTop + 13 + skillID * 10;
                addDynButton(new JRMCoreGuiButtonsA3(
                        BTN_ARCOSIAN_INLINE, inlineX, inlineY, 20, 1, true));
            }

            drawnCount++;
        }

        addOverviewScrollbarButtons(totalRows);
    }

    private void addOverviewScrollbarButtons(int totalRows) {
        if (totalRows > VISIBLE_ROWS) {
            int maxScroll = Math.max(0, totalRows - VISIBLE_ROWS);
            if (state.getScrollOffset() > maxScroll) {
                state.setScrollOffset(maxScroll);
            }

            float scrollSide = maxScroll > 0 ? (float) state.getScrollOffset() / (float) maxScroll : 0.0f;
            scrollSide = Math.max(0.0f, Math.min(1.0f, scrollSide));

            if (scrollSide > 0.0f) {
                addDynButton(new JRMCoreGuiButtonsA1(
                        BTN_SCROLL_UP, guiLeft + X_SIZE / 2 + 110 + 18, guiTop + 80 - 70, "i"));
            }
            if (scrollSide < 1.0f) {
                addDynButton(new JRMCoreGuiButtonsA1(
                        BTN_SCROLL_DOWN, guiLeft + X_SIZE / 2 + 110 + 18, guiTop + 80 + 60, "v"));
            }
        }
    }

    private void drawOverviewScrollbar(int totalRows) {
        if (totalRows <= VISIBLE_ROWS) {
            return;
        }

        int trackX = guiLeft + X_SIZE / 2 + 110 + 18;
        int trackY = guiTop + 25;
        int thumbHeight = getScrollbarThumbHeight(totalRows);
        int thumbY = getScrollbarThumbY(totalRows, trackY, thumbHeight);

        this.mc.getTextureManager().bindTexture(new ResourceLocation(JRMCoreGuiScreen.button1));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.drawTexturedModalRect(trackX, trackY, 216, 128, SCROLL_TRACK_WIDTH, SCROLL_TRACK_HEIGHT);
        drawScrollbarThumb(trackX, thumbY, thumbHeight);
    }

    private void drawScrollbarThumb(int x, int y, int height) {
        this.drawTexturedModalRect(x, y, 241, 155, SCROLL_TRACK_WIDTH, 2);
        for (int offset = 2; offset < height - 2; offset += 2) {
            this.drawTexturedModalRect(x, y + offset, 241, 157, SCROLL_TRACK_WIDTH, Math.min(2, height - offset - 2));
        }
        this.drawTexturedModalRect(x, y + height - 2, 241, 157, SCROLL_TRACK_WIDTH, 2);
    }

    private int getScrollbarThumbHeight(int totalRows) {
        float visibleRatio = (float) VISIBLE_ROWS / (float) totalRows;
        int proportionalHeight = Math.round(SCROLL_TRACK_HEIGHT * visibleRatio);
        proportionalHeight = Math.max(SCROLL_MIN_THUMB_HEIGHT, proportionalHeight);
        return Math.min(SCROLL_TRACK_HEIGHT, proportionalHeight);
    }

    private int getScrollbarThumbY(int totalRows, int trackY, int thumbHeight) {
        int maxScroll = Math.max(0, totalRows - VISIBLE_ROWS);
        if (maxScroll <= 0) {
            return trackY;
        }

        float normalized = (float) state.getScrollOffset() / (float) maxScroll;
        int travel = SCROLL_TRACK_HEIGHT - thumbHeight;
        return trackY + Math.round(travel * normalized);
    }

    // ════════════════════════════════════════════════════════════════
    // Overview — Label drawing (runs after super.drawScreen)
    // ════════════════════════════════════════════════════════════════

    private void drawOverviewLabels(int mouseX, int mouseY) {
        String tc = textColorPrefix();

//        if (state.isConfirmationActive()) {
//            drawTPFooter(tc);
//            drawTPAndMindCostHeader();
//            drawAvailableMind(tc, mouseX, mouseY);
//            return;
//        }

        drawTPFooter(tc);
        drawTPAndMindCostHeader();
        drawAvailableMind(tc, mouseX, mouseY);

        int totalRows = currentRows != null ? currentRows.size() : 0;
        drawOverviewScrollbar(totalRows);

        if (currentRows == null || currentRows.isEmpty())
            return;

        totalRows = currentRows.size();
        int scrollOffset = state.getScrollOffset();

        int drawnCount = 0;
        for (int i = scrollOffset; i < totalRows && drawnCount < VISIBLE_ROWS; i++) {
            SkillRowModel row = currentRows.get(i);
            int skillID = drawnCount + 1;
            boolean isRacialRow = row.getType() == SkillRowModel.RowType.RACIAL
                    || row.getType() == SkillRowModel.RowType.RACIAL_Y;

            int rowY = isRacialRow
                    ? guiTop + 15 + skillID * 10
                    : guiTop + 20 + skillID * 10;

            // Build skill name text matching vanilla format per row type
            String skillName = "", levelText = "";
            int nameWidth = fontRendererObj.getStringWidth(row.getDisplayName());
            if (row.getType() == SkillRowModel.RowType.RACIAL) {
                skillName = tc + row.getDisplayName();
                levelText = row.getLevel() <= 9 ? " " + textLevel(row.getLevel()) : "";
            } else {
                skillName = tc + row.getDisplayName() + " " + "";
                levelText = textLevel(row.getLevel());
            }

            fontRendererObj.drawString(skillName, guiLeft + 5, rowY, ColorMode.textColor(), ConfigDBCClient.DarkMode);
            if (!levelText.isEmpty())
                fontRendererObj.drawString(levelText, guiLeft + 5 + fontRendererObj.getStringWidth(skillName), rowY,
                        ColorMode.textColor(), ConfigDBCClient.DarkMode);


            String desc = row.getDescriptionKey();
            if (desc != null && !desc.isEmpty()) {
                queueTooltip(desc, guiLeft + 5, rowY + 2, nameWidth, 6, mouseX, mouseY);
            }

            int costRightEdge = isRacialRow ? 250 : 240;
            String costLabel = row.getCostLabel();
            int costWidth = fontRendererObj.getStringWidth(costLabel);
            fontRendererObj.drawString(costLabel, guiLeft + costRightEdge - costWidth, rowY, ColorMode.textColor(), ConfigDBCClient.DarkMode);

            drawnCount++;
        }
    }

    private void drawTPAndMindCostHeader() {
        String header = "TP and Mind Cost:";
        int headerWidth = fontRendererObj.getStringWidth(header);
        drawStringWithBorder(fontRendererObj, header, guiLeft + 250 - headerWidth, guiTop + 5 - 2,
                16765738);
    }

    private void drawAvailableMind(String tc, int mouseX, int mouseY) {
        if (JRMCoreH.PlyrSkills == null)
            return;

        int mindLeft = JRMCoreH.skillSlot_AvailableMindLeft();
        String displayText = tc + JRMCoreH.trl("jrmc", "AvailableMind") + ": ";
        String mind = JRMCoreH.numSep(mindLeft);
        int textWidth = fontRendererObj.getStringWidth(displayText);

        fontRendererObj.drawString(displayText, guiLeft + 5, guiTop + 5, ColorMode.textColor(), ConfigDBCClient.DarkMode);
        fontRendererObj.drawString(mind, guiLeft + 5+ textWidth, guiTop + 5, ColorMode.textColor(), ConfigDBCClient.DarkMode);
        
        textWidth+=fontRendererObj.getStringWidth(mind);
        String tooltip = JRMCoreH.statMindC() >= JRMCoreGuiScreen.kqGW3Z(false)
                ? JRMCoreH.trl("jrmc", "SkillSysMax", JRMCoreH.clpr + JRMCoreH.PlyrSkills.length + JRMCoreH.cldgy)
                : JRMCoreH.trl(
                "jrmc",
                "SkillSysNext",
                JRMCoreH.cllr + JRMCoreH.attrNms(JRMCoreH.Pwrtyp, 4) + JRMCoreH.cldgy,
                JRMCoreH.clpr + JRMCoreH.PlyrSkills.length + JRMCoreH.cldgy
        );
        queueTooltip(tooltip, guiLeft + 5, guiTop + 5, textWidth, 6, mouseX, mouseY);
    }

    private void drawTPFooter(String tc) {
        String tpText = tc + JRMCoreH.numSep((long) JRMCoreH.curTP) + " " + "Training Points" + " (TP)";
        fontRendererObj.drawString(tpText, guiLeft + 10, guiTop + 150, ColorMode.textColor(), ConfigDBCClient.DarkMode);
    }

    // ════════════════════════════════════════════════════════════════
    // Learn Skills — Button creation
    // ════════════════════════════════════════════════════════════════

    private void addLearnSkillsButtons() {
        if (currentRows == null || currentRows.isEmpty())
            return;

        int totalRows = currentRows.size();
        int startIndex = state.learnPageStart();
        int endIndex = state.learnPageEnd(totalRows);

        int baseX = guiLeft + X_SIZE / 2 - 122;
        int baseY = guiTop + (Y_SIZE + 1) / 2 - 64;

        int drawnCount = 0;
        for (int i = startIndex; i < endIndex; i++) {
            SkillRowModel row = currentRows.get(i);
            int rowY = baseY + drawnCount * 10;

            if (!row.isOwned()) {
                String learnText = "§0" + row.getDisplayName();
                int fnw = fontRendererObj.getStringWidth(learnText);

                if (!row.isUpgradeLocked() && !JRMCoreH.isFused()) {
                    boolean canAfford = row.canAffordTP() && row.canAffordMind();
                    String costText = "TP: " + JRMCoreH.numSep(row.getTpCost());
                    int onw = fontRendererObj.getStringWidth(costText);
                    int btnColor = canAfford ? 0x4040FF : 0x808080;
                    addDynButton(new JRMCoreGuiButtons01(
                            BTN_LEARN_BASE + i,
                            baseX + fnw + 2, rowY - 1, onw,
                            costText, btnColor));
                }
            }
            drawnCount++;
        }

        // Page navigation
        int totalPages = Math.max(1,
                (totalRows + SkillScreenState.LEARN_PAGE_SIZE - 1) / SkillScreenState.LEARN_PAGE_SIZE);
        int currentPage = state.getLearnPage();

        if (currentPage > 0) {
            addDynButton(new JRMCoreGuiButtons00(
                    BTN_LEARN_PREV_PAGE, guiLeft + 5, guiTop + Y_SIZE - 20, 30, 14, "<", 0));
        }
        if (currentPage < totalPages - 1) {
            addDynButton(new JRMCoreGuiButtons00(
                    BTN_LEARN_NEXT_PAGE, guiLeft + X_SIZE - 35, guiTop + Y_SIZE - 20, 30, 14, ">", 0));
        }
    }

    // ════════════════════════════════════════════════════════════════
    // Learn Skills — Label drawing
    // ════════════════════════════════════════════════════════════════

    private void drawLearnSkillsLabels(int mouseX, int mouseY) {
        // MF-12: TP at top-left like vanilla (guiLeft+10, guiTop+5)
        String tpText = JRMCoreH.numSep((long) JRMCoreH.curTP) + " " + JRMCoreH.trl("jrmc", "TrainingPoints") + " (TP)";
        fontRendererObj.drawString(tpText, guiLeft + 10, guiTop + 5, 0);

        if (currentRows == null || currentRows.isEmpty())
            return;

        int totalRows = currentRows.size();
        int startIndex = state.learnPageStart();
        int endIndex = state.learnPageEnd(totalRows);

        int baseX = guiLeft + X_SIZE / 2 - 122;
        int baseY = guiTop + (Y_SIZE + 1) / 2 - 64;

        int drawnCount = 0;
        for (int i = startIndex; i < endIndex; i++) {
            SkillRowModel row = currentRows.get(i);
            int rowY = baseY + drawnCount * 10;

            if (row.isOwned()) {
                String ownedText = "§0" + row.getDisplayName() + " §8(" + JRMCoreH.trl("jrmc", "Owned") + ")";
                fontRendererObj.drawString(ownedText, baseX, rowY, 0);
            } else {
                String learnText = "§0" + row.getDisplayName();
                int fnw = fontRendererObj.getStringWidth(learnText);
                fontRendererObj.drawString(learnText, baseX, rowY, 0);

                if (row.isUpgradeLocked()) {
                    String lockedText = JRMCoreH.trl("jrmc", "UpgradeLocked");
                    fontRendererObj.drawString("§8" + lockedText, baseX + fnw + 4, rowY, 0);
                }
            }
            drawnCount++;
        }

        // Page text
        int totalPages = Math.max(1,
                (totalRows + SkillScreenState.LEARN_PAGE_SIZE - 1) / SkillScreenState.LEARN_PAGE_SIZE);
        if (totalPages > 1) {
            int currentPage = state.getLearnPage();
            String pageText = (currentPage + 1) + "/" + totalPages;
            int pageWidth = fontRendererObj.getStringWidth(pageText);
            fontRendererObj.drawString(pageText, guiLeft + X_SIZE / 2 - pageWidth / 2, guiTop + Y_SIZE - 17, 0);
        }
    }

    // ════════════════════════════════════════════════════════════════
    // Confirmation Modal — Button creation
    // ════════════════════════════════════════════════════════════════

    private void addConfirmationButtons() {
        int wpx = 60;
        int wpy = 50;

        SkillRowModel target = state.getConfirmationTarget();
        if (target != null) {
            addDynButton(new JRMCoreGuiButtons00(
                    BTN_CONFIRM_YES, guiLeft + 5 + wpx, guiTop + 45 + wpy, 40, 20,
                    JRMCoreH.trl("jrmc", "Yes"), 0));
        }

        addDynButton(new JRMCoreGuiButtons00(
                BTN_CONFIRM_NO, guiLeft + 95 + wpx, guiTop + 45 + wpy, 40, 20,
                JRMCoreH.trl("jrmc", "No"), 0));
    }

    // ════════════════════════════════════════════════════════════════
    // Confirmation Modal — Overlay drawing
    // ════════════════════════════════════════════════════════════════

    private void drawConfirmationOverlay(int mouseX, int mouseY) {
        int modalWidth = 140;
        int modalHeight = 71;
        int wpx = 60;
        int wpy = 50;

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new net.minecraft.util.ResourceLocation(JRMCoreGuiScreen.wish));
        this.drawTexturedModalRect(guiLeft + wpx, guiTop + wpy, 0, 159, modalWidth, modalHeight);
        drawRect(guiLeft + wpx + 4, guiTop + wpy + 4, guiLeft + wpx + modalWidth - 4, guiTop + wpy + modalHeight - 4, 0xFFE6E6E6);

        SkillRowModel target = state.getConfirmationTarget();
        if (target != null) {
            String skillName = target.getDisplayName();
            String confirmText = JRMCoreH.trl("jrmc", "delskillconfirm", skillName);
            JRMCoreH.txt(confirmText, JRMCoreH.cldr, 0, true,
                    guiLeft + wpx + 5, guiTop + wpy + 5, modalWidth - 10);
        }
    }

    @SuppressWarnings("unchecked")
    private void drawConfirmationButtonsOnTop(int mouseX, int mouseY) {
        for (GuiButton button : (java.util.List<GuiButton>) this.buttonList) {
            if (button.id == BTN_CONFIRM_YES || button.id == BTN_CONFIRM_NO) {
                button.drawButton(this.mc, mouseX, mouseY);
            }
        }
    }

    // ════════════════════════════════════════════════════════════════
    // Actions
    // ════════════════════════════════════════════════════════════════

    @Override
    protected void actionPerformed(GuiButton button) {
        int id = button.id;

        if (state.isConfirmationActive()) {
            if (id == BTN_CONFIRM_YES) {
                gateway.confirmDelete();
                return;
            }
            if (id == BTN_CONFIRM_NO) {
                gateway.cancelDelete();
                return;
            }
            return;
        }

        if (id == BTN_LEARN_SKILLS) {
            gateway.navigateToLearnSkills();
            return;
        }

        if (id == BTN_BACK_OVERVIEW) {
            gateway.navigateToOverview();
            return;
        }

        // MF-20: inline Arcosian button navigates to vanilla guiID 19
        if (id == BTN_ARCOSIAN_INLINE) {
            JRMCoreGuiScreen vanillaScreen = new JRMCoreGuiScreen(19);
            this.mc.displayGuiScreen(vanillaScreen);
            return;
        }

        if (id == BTN_LEARN_PREV_PAGE) {
            state.setLearnPage(state.getLearnPage() - 1);
            return;
        }
        if (id == BTN_LEARN_NEXT_PAGE) {
            state.setLearnPage(state.getLearnPage() + 1);
            return;
        }

        if (id == BTN_SCROLL_UP) {
            state.setScrollOffset(state.getScrollOffset() - 1);
            int totalRows = currentRows != null ? currentRows.size() : 0;
            state.clampScroll(totalRows);
            return;
        }
        if (id == BTN_SCROLL_DOWN) {
            state.setScrollOffset(state.getScrollOffset() + 1);
            int totalRows = currentRows != null ? currentRows.size() : 0;
            state.clampScroll(totalRows);
            return;
        }

        // Button IDs encode the actual row index (not the drawn visual offset)
        if (id >= BTN_UPGRADE_BASE && id < BTN_UPGRADE_BASE + 200) {
            int actualIndex = id - BTN_UPGRADE_BASE;
            if (currentRows != null && actualIndex < currentRows.size()) {
                gateway.upgrade(currentRows.get(actualIndex));
            }
            return;
        }

        if (id >= BTN_DELETE_BASE && id < BTN_DELETE_BASE + 200) {
            int actualIndex = id - BTN_DELETE_BASE;
            if (currentRows != null && actualIndex < currentRows.size()) {
                gateway.requestDelete(currentRows.get(actualIndex));
            }
            return;
        }

        if (id >= BTN_LEARN_BASE && id < BTN_LEARN_BASE + 200) {
            int actualIndex = id - BTN_LEARN_BASE;
            if (currentRows != null && actualIndex < currentRows.size()) {
                gateway.learnSkill(currentRows.get(actualIndex));
            }
            return;
        }

        super.actionPerformed(button);
    }

    // ════════════════════════════════════════════════════════════════
    // Mouse handling
    // ════════════════════════════════════════════════════════════════

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (state.isConfirmationActive()) {
            return;
        }

        if (state.getMode() == SkillScreenState.Mode.OVERVIEW && mouseButton == 0) {
            int scrollX = guiLeft + X_SIZE / 2 + 110 + 18;
            int scrollTopY = guiTop + 25;
            int scrollBottomY = guiTop + 25 + SCROLL_TRACK_HEIGHT;

            if (mouseX >= scrollX - 5 && mouseX <= scrollX + 15
                    && mouseY >= scrollTopY && mouseY <= scrollBottomY) {
                state.setScrollbarPressed(true);
            }
        }
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int mouseButton) {
        super.mouseMovedOrUp(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            state.setScrollbarPressed(false);
        }
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();

        if (state.isConfirmationActive()) {
            return;
        }

        if (state.getMode() == SkillScreenState.Mode.OVERVIEW) {
            int scroll = Mouse.getEventDWheel();
            if (scroll != 0) {
                int totalRows = currentRows != null ? currentRows.size() : 0;
                if (scroll > 0) {
                    state.setScrollOffset(state.getScrollOffset() - 1);
                } else {
                    state.setScrollOffset(state.getScrollOffset() + 1);
                }
                state.clampScroll(totalRows);
            }

            if (state.isScrollbarPressed() && !JRMCoreGuiButtonsA1.clicked) {
                int totalRows = currentRows != null ? currentRows.size() : 0;
                if (totalRows > VISIBLE_ROWS) {
                    int scrollTopY = guiTop + 25;
                    int scrollHeight = SCROLL_TRACK_HEIGHT;
                    int mouseY = Mouse.getEventY() * this.height / this.mc.displayHeight;
                    mouseY = this.height - mouseY;

                    int thumbHeight = getScrollbarThumbHeight(totalRows);
                    int travel = Math.max(1, scrollHeight - thumbHeight);
                    float normalized = (float) (mouseY - scrollTopY - thumbHeight / 2) / (float) travel;
                    normalized = Math.max(0.0f, Math.min(1.0f, normalized));

                    int maxScroll = Math.max(0, totalRows - VISIBLE_ROWS);
                    int newScroll = Math.round(maxScroll * normalized);
                    state.setScrollOffset(Math.min(newScroll, maxScroll));
                    state.clampScroll(totalRows);
                }
            }
        }
    }

    // ════════════════════════════════════════════════════════════════
    // Helpers
    // ════════════════════════════════════════════════════════════════

    private void rebuildRows() {
        DBCData data = DBCData.getClient();
        if (data == null)
            return;

        data.refreshSkillFieldsFromNBT();

        switch (state.getMode()) {
            case OVERVIEW:
                currentRows = SkillRowModel.buildOverviewRows(data);
                state.clampScroll(currentRows.size());
                break;
            case LEARN_SKILLS:
                currentRows = SkillRowModel.buildLearnableRows(data);
                state.clampLearnPage(currentRows.size());
                break;
        }
    }

    private void addModeButtons() {
        if (JRMCoreH.isFused())
            return;

        switch (state.getMode()) {
            case OVERVIEW:
                if (JRMCoreH.isPowerTypeChakra(JRMCoreH.Pwrtyp)) {
                    String learnText = JRMCoreH.trl("jrmc", "Learn");
                    int learnWidth = fontRendererObj.getStringWidth(learnText);
                    addDynButton(new JRMCoreGuiButtons01(
                            BTN_LEARN_SKILLS, guiLeft + 250 + 10, guiTop + 5,
                            learnWidth, learnText, JRMCoreH.techNCCol[1]));
                }
                break;

            case LEARN_SKILLS:
                addDynButton(new JRMCoreGuiButtons00(
                        BTN_BACK_OVERVIEW, guiLeft + menuImageWidth - 55, guiTop + 3,
                        50, 14, JRMCoreH.trl("jrmc", "Close"), 0));
                break;
        }
    }

    /** MF-1: vanilla format is "§8(lvl: N)" */
    private String textLevel(int level) {
        if (level <= 0)
            return "";
        return  "(lvl: " + level + ")";
    }

    public List<SkillRowModel> getCurrentRows() {
        return currentRows;
    }

    public SkillScreenState getState() {
        return state;
    }

    public SkillActionGateway getGateway() {
        return gateway;
    }

    // ════════════════════════════════════════════════════════════════
    // Tooltip rendering (ported from JRMCoreGuiScreen.drawDetails)
    // ════════════════════════════════════════════════════════════════

    private void queueTooltip(String text, int xpos, int ypos, int w, int h, int mouseX, int mouseY) {
        if (pendingTooltip != null)
            return;
        if (xpos < mouseX && xpos + w > mouseX && ypos - 3 < mouseY && ypos + h > mouseY) {
            pendingTooltip = text;
        }
    }

    private void drawPendingTooltip(int mouseX, int mouseY) {
        if (pendingTooltip == null)
            return;

        String desc = pendingTooltip;
        pendingTooltip = null;

        int descWidth = fontRendererObj.getStringWidth(desc);
        int boxWidth = Math.min(descWidth, TOOLTIP_MAX_WIDTH);

        mc.renderEngine.bindTexture(TOOLTIP_BG);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.8F);

        int lineCount = JRMCoreH.txt(desc, TOOLTIP_TEXT_PREFIX, 0, false, 0, 0, TOOLTIP_MAX_WIDTH);

        ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int screenW = sr.getScaledWidth();
        int screenH = sr.getScaledHeight();

        int xOff = 0;
        int yOff = 0;
        if (screenW < mouseX + boxWidth + 10) {
            xOff = screenW - (mouseX + boxWidth) - 10;
        }
        if (screenH < mouseY + lineCount * 10 + 10) {
            yOff = -(lineCount * 10 + 20);
        }

        this.drawTexturedModalRect(mouseX + xOff, mouseY + 10 + yOff,
                5, 5, boxWidth + 10, lineCount * 10 + 10);
        JRMCoreH.txt(desc, TOOLTIP_TEXT_PREFIX, 0, true,
                mouseX + 5 + xOff, mouseY + 5 + 10 + yOff, TOOLTIP_MAX_WIDTH);
    }

    private static void drawStringWithBorder(FontRenderer fr, String text, int x, int y, int color) {
        int border = 0;
        fr.drawString(text, x + 1, y + 2, border);
        fr.drawString(text, x - 1, y + 2, border);
        fr.drawString(text, x, y + 1 + 2, border);
        fr.drawString(text, x, y - 1 + 2, border);
        fr.drawString(text, x, y + 2, color);
    }
}
