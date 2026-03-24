package kamkeel.npcdbc.client.gui.dbc.creator;

import JinRyuu.JRMCore.JRMCoreGuiScreen;
import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.client.gui.dbc.EntityPreviewRenderer;
import kamkeel.npcdbc.data.race.helper.RaceSelectorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;

public final class ConfirmPage extends CreatorPage {

    private int guiLeft, guiTop;
    private int lastMouseX, lastMouseY;
    private final EntityPreviewRenderer previewRenderer = new EntityPreviewRenderer()
        .setDefaultZoom(2.5f)
        .setZoomBounds(1f, 5f)
        .setFollowMouse(true)
        .setAllowRotate(true);

    ConfirmPage(CharacterCreationGui parent, CreatorSession session, VanillaCreatorBridge bridge) {
        super(parent, session, bridge);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void initPage(List buttonList, int guiLeft, int guiTop) {
        this.guiLeft = guiLeft;
        this.guiTop = guiTop;
    }

    @Override
    public void drawPage(int mouseX, int mouseY, float partialTicks) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;

        String[] races = RaceSelectorHelper.getRaces();
        String raceText = session.raceIndex < races.length
            ? JRMCoreH.trl("jrmc", races[session.raceIndex]) : "Unknown";
        String genderText = JRMCoreH.trl("jrmc", JRMCoreH.Genders[session.gender]);
        String hairText = JRMCoreH.trl("jrmc", "Hair") + " " + (session.hairBack + 1);
        String skinText = JRMCoreH.trl("jrmc", JRMCoreH.skinTyps[session.skinType]);
        String pwrText = JRMCoreH.trl("jrmc", JRMCoreH.Pwrtyps[session.powerType]);
        String[] classArr = JRMCoreH.cl(session.powerType);
        String classText = session.classType < classArr.length
            ? JRMCoreH.trl("jrmc", classArr[session.classType]) : "";
        String className = JRMCoreH.trl("jrmc", JRMCoreH.ClassNames[session.powerType]);

        int row = 0;
        font.drawString(JRMCoreH.trl("jrmc", "Race") + ": " + raceText, guiLeft + 5, guiTop + 5 + row * 10, 0);
        row++;
        font.drawString(JRMCoreH.trl("jrmc", "Gender") + ": " + genderText, guiLeft + 5, guiTop + 5 + row * 10, 0);
        row++;
        font.drawString(JRMCoreH.trl("jrmc", "Hair") + ": " + hairText, guiLeft + 5, guiTop + 5 + row * 10, 0);
        row++;
        font.drawString(JRMCoreH.trl("jrmc", "Color") + ": ", guiLeft + 5, guiTop + 5 + row * 10, 0);

        int colorSwatchX = guiLeft + 5 + font.getStringWidth(JRMCoreH.trl("jrmc", "Color") + ": ");
        drawColorSwatch(session.hairColor, colorSwatchX, guiTop + 3 + row * 10, 50, 10);

        row++;
        font.drawString(JRMCoreH.trl("jrmc", "BodyType") + ": " + skinText, guiLeft + 5, guiTop + 5 + row * 10, 0);
        row++;
        font.drawString(JRMCoreH.trl("jrmc", "PowerType") + ": " + pwrText, guiLeft + 5, guiTop + 5 + row * 10, 0);
        row++;
        row++;
        if (session.powerType != 3) {
            font.drawString(className + ": " + classText, guiLeft + 5, guiTop + 5 + (row - 1) * 10, 0);
        }

        // Player preview on the right half
        int previewX = guiLeft + 256 / 2 + 51;
        previewRenderer.setAnchor(previewX, guiTop + 152);
        previewRenderer.draw(Minecraft.getMinecraft().thePlayer, lastMouseX, lastMouseY, 0);
    }

    @Override
    public boolean actionPerformed(GuiButton button) {
        return false;
    }

    @Override
    public void onPageEnter() {
        bridge.applyPreview();
    }

    private void drawColorSwatch(int color, int x, int y, int w, int h) {
        float r = (float)(color >> 16 & 0xFF) / 255.0F;
        float g = (float)(color >> 8 & 0xFF) / 255.0F;
        float b = (float)(color & 0xFF) / 255.0F;
        GL11.glColor4f(r, g, b, 1.0F);
        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(JRMCoreGuiScreen.button1));
        parent.drawTexturedModalRect(x, y, 0, 0, w, h);
    }
}
