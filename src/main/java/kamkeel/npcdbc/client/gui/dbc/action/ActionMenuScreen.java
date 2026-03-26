package kamkeel.npcdbc.client.gui.dbc.action;

import JinRyuu.JRMCore.JRMCoreA;
import JinRyuu.JRMCore.JRMCoreClient;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.JRMCoreHC;
import JinRyuu.JRMCore.JRMCoreKeyHandler;
import JinRyuu.JRMCore.client.config.jrmc.JGConfigClientSettings;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

/**
 * Addon-owned replacement for the vanilla DBC action menu overlay.
 * <p>
 * This is NOT a GuiScreen — it renders as an overlay during the render tick,
 * exactly like the vanilla {@code JRMCoreGui.renderActionMenu()}.
 * <p>
 * Vanilla geometry:
 * <ul>
 *   <li>3×3 grid of 90×60 cells, centered on screen</li>
 *   <li>Center cell (id%9==4): 45×30 MORE box at +22,+15 offset within cell</li>
 *   <li>Text at +5,+5 within each cell, wrap width 80</li>
 *   <li>Instruction text at centerX-50, centerY-110, wrap 180</li>
 * </ul>
 */
public final class ActionMenuScreen {

    private static final ResourceLocation ALLW_TEXTURE = new ResourceLocation("jinryuumodscore:allw.png");

    private static final int CELL_WIDTH = 90;
    private static final int CELL_HEIGHT = 60;
    private static final int GRID_COLS = 3;
    private static final int GRID_ROWS = 3;
    private static final int CENTER_BOX_W = 45;
    private static final int CENTER_BOX_H = 30;
    private static final int CENTER_BOX_OFFSET_X = 22;
    private static final int CENTER_BOX_OFFSET_Y = 15;
    private static final int TEXT_OFFSET_X = 5;
    private static final int TEXT_OFFSET_Y = 5;
    private static final int TEXT_WRAP_WIDTH = 80;
    private static final int INSTRUCTION_WRAP_WIDTH = 180;
    private static final int INSTRUCTION_OFFSET_X = -50;
    private static final int INSTRUCTION_OFFSET_Y = -110;

    private static final float ALPHA_HOVERED = 0.75f;
    private static final float ALPHA_NON_HOVERED = 0.5f;
    private static final float ALPHA_EMPTY = 0.25f;
    private static final float ALPHA_CENTER_BASE = 0.5f;
    private static final float ALPHA_CENTER_HOVER_BOOST = 0.25f;

    private final ActionMenuState state;
    private final ActionMenuActionGateway gateway;

    public ActionMenuScreen(ActionMenuState state, ActionMenuActionGateway gateway) {
        this.state = state;
        this.gateway = gateway;
    }

    /**
     * Renders the full action menu overlay. Called from the tick handler
     * when the action menu key is held.
     * <p>
     * Mirrors {@code JRMCoreGui.renderActionMenu()} exactly.
     */
    public void render() {
        JRMCoreClient.mc.mouseHelper.mouseXYChange();

        ScaledResolution sr = new ScaledResolution(
            JRMCoreClient.mc, JRMCoreClient.mc.displayWidth, JRMCoreClient.mc.displayHeight);
        int scaledW = sr.getScaledWidth();
        int scaledH = sr.getScaledHeight();

        float posX = (float) Mouse.getX() / (float) JRMCoreClient.mc.displayWidth;
        float posY = (float) Mouse.getY() / (float) JRMCoreClient.mc.displayHeight;
        int mouseX = (int) ((float) scaledW * posX);
        int mouseY = scaledH - (int) ((float) scaledH * posY);

        JRMCoreClient.mc.entityRenderer.setupOverlayRendering();
        int centerX = scaledW / 2;
        int centerY = scaledH / 2;

        JRMCoreClient.mc.renderEngine.bindTexture(ALLW_TEXTURE);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        String keyName = GameSettings.getKeyDisplayString(JRMCoreKeyHandler.actionMenu.getKeyCode());
        JRMCoreH.txt(
            "Hover over and release " + keyName,
            JRMCoreH.cldgy, 0, true,
            centerX + INSTRUCTION_OFFSET_X,
            centerY + INSTRUCTION_OFFSET_Y,
            INSTRUCTION_WRAP_WIDTH
        );

        boolean anySlotHovered = false;
        int page = state.getPageIndex();

        for (int col = 0; col < GRID_COLS; col++) {
            for (int row = 0; row < GRID_ROWS; row++) {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                int cellX = centerX - 135 + col * CELL_WIDTH;
                int cellY = centerY - 90 + row * CELL_HEIGHT;
                int slotId = col + row * GRID_COLS + page * 9;
                boolean isCenter = ActionMenuState.isCenterSlot(slotId);
                boolean hasAction = gateway.hasAction(slotId);
                boolean hovered = isHovered(mouseX, mouseY, cellX, cellY, CELL_WIDTH, CELL_HEIGHT);

                if (hasAction) {
                    if (hovered) {
                        anySlotHovered = true;
                        state.setActionSelectID(slotId);
                        renderHoveredActionCell(cellX, cellY, slotId);
                    } else if (!isCenter) {
                        renderNonHoveredActionCell(cellX, cellY, slotId);
                    }
                } else if (!isCenter) {
                    renderEmptyCell(cellX, cellY);
                }

                if (isCenter) {
                    renderCenterCell(cellX, cellY, hovered, slotId);
                    if (hovered) {
                        anySlotHovered = true;
                        state.setActionSelectID(slotId);
                    }
                }
            }
        }

        handleAttackKeyState(anySlotHovered);
    }

    // ════════════════════════════════════════════════════════════════
    // Cell rendering
    // ════════════════════════════════════════════════════════════════

    private void renderHoveredActionCell(int cellX, int cellY, int slotId) {
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, ALPHA_HOVERED);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        JRMCoreHC.dtm((float) cellX, (float) cellY, 0, 0, 89.0F, 59.0F, -90.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();

        String borderLabel = gateway.getSlotBorderLabel(slotId);
        String textLabel = gateway.getSlotLabel(slotId);
        String textColor = gateway.getHoveredTextColor();

        int tx = cellX + TEXT_OFFSET_X;
        int ty = cellY + TEXT_OFFSET_Y;

        JRMCoreH.txt(borderLabel, JRMCoreH.clb, 0, true, tx + 1, ty, TEXT_WRAP_WIDTH);
        JRMCoreH.txt(borderLabel, JRMCoreH.clb, 0, true, tx - 1, ty, TEXT_WRAP_WIDTH);
        JRMCoreH.txt(borderLabel, JRMCoreH.clb, 0, true, tx, ty + 1, TEXT_WRAP_WIDTH);
        JRMCoreH.txt(borderLabel, JRMCoreH.clb, 0, true, tx, ty - 1, TEXT_WRAP_WIDTH);
        JRMCoreH.txt(textLabel, textColor, 0, true, tx, ty, TEXT_WRAP_WIDTH);
    }

    private void renderNonHoveredActionCell(int cellX, int cellY, int slotId) {
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, ALPHA_NON_HOVERED);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        JRMCoreHC.dtm((float) cellX, (float) cellY, 0, 0, 89.0F, 59.0F, -90.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();

        String borderLabel = gateway.getSlotBorderLabel(slotId);
        String textLabel = gateway.getSlotLabel(slotId);

        int tx = cellX + TEXT_OFFSET_X;
        int ty = cellY + TEXT_OFFSET_Y;

        JRMCoreH.txt(borderLabel, JRMCoreH.clb, 0, true, tx + 1, ty, TEXT_WRAP_WIDTH);
        JRMCoreH.txt(borderLabel, JRMCoreH.clb, 0, true, tx - 1, ty, TEXT_WRAP_WIDTH);
        JRMCoreH.txt(borderLabel, JRMCoreH.clb, 0, true, tx, ty + 1, TEXT_WRAP_WIDTH);
        JRMCoreH.txt(borderLabel, JRMCoreH.clb, 0, true, tx, ty - 1, TEXT_WRAP_WIDTH);
        JRMCoreH.txt(textLabel, JRMCoreH.clw, 0, true, tx, ty, TEXT_WRAP_WIDTH);
    }

    private void renderEmptyCell(int cellX, int cellY) {
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, ALPHA_EMPTY);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        JRMCoreHC.dtm((float) cellX, (float) cellY, 0, 0, 89.0F, 59.0F, -90.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    private void renderCenterCell(int cellX, int cellY, boolean hovered, int slotId) {
        float alpha = ALPHA_CENTER_BASE + (hovered ? ALPHA_CENTER_HOVER_BOOST : 0.0F);
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        JRMCoreHC.dtm(
            (float) (cellX + CENTER_BOX_OFFSET_X),
            (float) (cellY + CENTER_BOX_OFFSET_Y),
            0, 0, (float) CENTER_BOX_W, (float) CENTER_BOX_H, -90.0F
        );
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();

        String moreColor = hovered ? JRMCoreH.clgy : JRMCoreH.clb;
        JRMCoreH.txt("MORE", moreColor, 0, true,
            cellX + TEXT_OFFSET_X + CENTER_BOX_OFFSET_X,
            cellY + TEXT_OFFSET_Y + CENTER_BOX_OFFSET_Y,
            TEXT_WRAP_WIDTH);
    }

    // ════════════════════════════════════════════════════════════════
    // Input handling
    // ════════════════════════════════════════════════════════════════

    /**
     * Handle the attack key state exactly like vanilla.
     * <ul>
     *   <li>If attack key released: clear center debounce</li>
     *   <li>If attack key pressed on non-center slot: force-release action menu key</li>
     * </ul>
     */
    private void handleAttackKeyState(boolean anySlotHovered) {
        if (!JRMCoreClient.mc.gameSettings.keyBindAttack.getIsKeyPressed()) {
            state.setCenterDebounce(false);
        }

        if (JRMCoreClient.mc.gameSettings.keyBindAttack.getIsKeyPressed()
            && state.getActionSelectID() % 9 != 4) {
            KeyBinding.setKeyBindState(JRMCoreKeyHandler.actionMenu.getKeyCode(), false);
        }

        if (!anySlotHovered) {
            state.setActionSelectID(-1);
        }

        state.setAnySlotHovered(anySlotHovered);
    }

    private boolean isHovered(int mX, int mY, int px, int py, int w, int h) {
        return mX > px && mX < px + w && mY > py && mY < py + h;
    }
}
