package kamkeel.npcdbc.client.gui.dbc.creator;

import JinRyuu.JRMCore.JRMCoreGuiButtons00;
import JinRyuu.JRMCore.JRMCoreGuiScreen;
import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.client.gui.dbc.AbstractJRMCGui;
import kamkeel.npcdbc.data.race.helper.RaceSelectorHelper;
import kamkeel.npcdbc.mixins.late.IDBCGuiScreen;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.input.Keyboard;

public class CharacterCreationGui extends AbstractJRMCGui {

    private static final int BUTTON_CANCEL = 900;
    private static final int BUTTON_BACK = 901;
    private static final int BUTTON_NEXT = 902;

    private static CreatorSession pendingSession;
    private static int pendingPageIndex = -1;

    private final CreatorSession session;
    private final VanillaCreatorBridge bridge;
    private final CreatorPage[] pages;
    private int currentPageIndex = 0;
    private int lastInitPageIndex = -1;

    public CharacterCreationGui() {
        super(0);
        this.addDefaultButtons = false;

        if (pendingSession != null) {
            this.session = pendingSession;
            this.currentPageIndex = Math.max(0, pendingPageIndex);
            readBackColorsFromVanillaStatics();
            clearPendingReturn();
        } else {
            this.session = CreatorSession.open();
        }

        this.bridge = new VanillaCreatorBridge(session);
        this.pages = new CreatorPage[]{
            new AppearancePage(this, session, bridge),
            new PowerTypePage(this, session, bridge),
            new ConfirmPage(this, session, bridge)
        };
    }

    @Override
    public void initGui() {
        super.initGui();

        // Button Y and positions match the vanilla JRMCoreGuiScreen creator layout
        // which uses posX (width/2) ± offset, posY (height/2) + 65.
        int posX = this.width / 2;
        int posY = this.height / 2;
        int btnY = posY + 65;

        if (currentPageIndex == 0) {
            this.buttonList.add(new JRMCoreGuiButtons00(
                BUTTON_CANCEL, posX - 150, btnY, 20, 20, "X", 0
            ));
        }

        // Back — left of centre, only enabled after page 0
        if (currentPageIndex > 0) {
            String backText = JRMCoreH.trl("jrmc", "Back");
            int backW = this.fontRendererObj.getStringWidth(backText) + 8;
            this.buttonList.add(new JRMCoreGuiButtons00(
                BUTTON_BACK, posX - 130 - backW, btnY, backW, 20, backText, 0
            ));
        }

        // Next / Accept — far right
        boolean isLastPage = currentPageIndex >= pages.length - 1;
        String nextText = JRMCoreH.trl("jrmc", isLastPage ? "Accept" : "Next");
        int nextW = this.fontRendererObj.getStringWidth(nextText) + 8;
        this.buttonList.add(new JRMCoreGuiButtons00(
            BUTTON_NEXT, posX + 130, btnY, nextW, 20, nextText, 0
        ));

        RaceSelectorHelper.setPreviewActive(true);
        bridge.applyPreview();

        pages[currentPageIndex].initPage(this.buttonList, guiWidthOffset, guiHeightOffset);

        if (lastInitPageIndex != currentPageIndex) {
            pages[currentPageIndex].onPageEnter();
            lastInitPageIndex = currentPageIndex;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        pages[currentPageIndex].drawPage(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case BUTTON_CANCEL:
                cancelAndClose();
                return;
            case BUTTON_BACK:
                if (currentPageIndex > 0) {
                    pages[currentPageIndex].onPageLeave();
                    currentPageIndex--;
                    lastInitPageIndex = -1;
                    initGui();
                }
                return;
            case BUTTON_NEXT:
                if (currentPageIndex < pages.length - 1) {
                    pages[currentPageIndex].onPageLeave();
                    currentPageIndex++;
                    lastInitPageIndex = -1;
                    initGui();
                } else {
                    bridge.commit();
                }
                return;
            default:
                break;
        }

        pages[currentPageIndex].actionPerformed(button);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (pages[currentPageIndex].keyTyped(typedChar, keyCode)) {
            return;
        }

        if (keyCode == Keyboard.KEY_ESCAPE) {
            cancelAndClose();
        }
    }

    @Override
    public void onGuiClosed() {
        bridge.cleanup();
        super.onGuiClosed();
    }

    void refreshPage() {
        lastInitPageIndex = -1;
        initGui();
    }

    /**
     * Opens the vanilla DBC color picker, saving session/page to statics so
     * the enhanced creator resumes on return via the mixin's initGui hook.
     */
    void openVanillaColorPicker(int colorType) {
        session.syncToVanillaStatics();
        pendingSession = session;
        pendingPageIndex = currentPageIndex;

        JRMCoreGuiScreen vanillaScreen = new JRMCoreGuiScreen(JRMCoreGuiScreen.ID_COLOR_PICKER);
        JRMCoreGuiScreen.colorType = colorType;
        vanillaScreen.guiIDprev = 0;
        this.mc.displayGuiScreen(vanillaScreen);
    }

    public static boolean hasPendingReturn() {
        return pendingSession != null;
    }

    public static void clearPendingReturn() {
        pendingSession = null;
        pendingPageIndex = -1;
    }

    private void readBackColorsFromVanillaStatics() {
        session.hairColor = JRMCoreGuiScreen.ColorSlcted;
        session.bodyColMain = JRMCoreGuiScreen.BodyColMainSlcted;
        session.bodyColSub1 = JRMCoreGuiScreen.BodyColSub1Slcted;
        session.bodyColSub2 = JRMCoreGuiScreen.BodyColSub2Slcted;
        session.bodyColSub3 = JRMCoreGuiScreen.BodyColSub3Slcted;
        session.eyeCol1 = JRMCoreGuiScreen.EyeCol1Slcted;
        session.eyeCol2 = JRMCoreGuiScreen.EyeCol2Slcted;
        session.kiColor = JRMCoreGuiScreen.KiColorSlcted;
        session.brightness = JRMCoreGuiScreen.BrghtSlcted;
    }

    private void cancelAndClose() {
        session.resetToSnapshot();
        session.syncToVanillaStatics();
        if (session.isCustomRace()) {
            JRMCoreGuiScreen.RaceSlcted = session.getVanillaRaceIndex();
        }
        clearPendingReturn();
        bridge.cleanup();
        if (this.mc != null && this.mc.thePlayer != null) {
            this.mc.thePlayer.closeScreen();
        }
    }
}
