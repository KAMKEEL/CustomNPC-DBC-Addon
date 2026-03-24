package kamkeel.npcdbc.client.gui.dbc.creator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.util.List;

/**
 * Placeholder page used during Wave 1 scaffolding.
 * Draws only the page title string. Will be replaced by real page
 * implementations (AppearancePage, PowerTypePage, ConfirmPage) in Wave 2.
 */
final class StubPage extends CreatorPage {

    private final String title;
    private int guiLeft;
    private int guiTop;

    StubPage(CharacterCreationGui parent, CreatorSession session, VanillaCreatorBridge bridge, String title) {
        super(parent, session, bridge);
        this.title = title;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void initPage(List buttonList, int guiLeft, int guiTop) {
        this.guiLeft = guiLeft;
        this.guiTop = guiTop;
    }

    @Override
    public void drawPage(int mouseX, int mouseY, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        int titleWidth = mc.fontRenderer.getStringWidth(title);
        mc.fontRenderer.drawStringWithShadow(
            "\u00a77" + title + " (stub)",
            guiLeft + (256 - titleWidth) / 2,
            guiTop + 40,
            0xAAAAAA
        );
    }

    @Override
    public boolean actionPerformed(GuiButton button) {
        return false;
    }
}
