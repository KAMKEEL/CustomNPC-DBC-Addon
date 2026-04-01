package kamkeel.npcdbc.client.gui.component;

import kamkeel.npcdbc.data.race.races.android.AndroidPartSlot;
import kamkeel.npcdbc.data.race.races.android.AndroidPartType;
import kamkeel.npcdbc.items.android.ItemAndroidPart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.SubGuiInterface;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.List;

public class SubGuiSelectAndroidPart extends SubGuiInterface {

    public final AndroidPartSlot slot;
    public boolean confirmed = false;
    public boolean removeEquip = false;
    public ItemStack selectedStack = null;

    private static final ResourceLocation INVENTORY_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/inventory.png");

    private static final int SLOT_U = 7;
    private static final int SLOT_V = 17;

    private static final int COLS = 9;
    private static final int ROWS = 4;
    private static final int SLOT_SIZE = 18;
    private static final int PADDING = 8;

    private final RenderItem renderItem = new RenderItem();

    public SubGuiSelectAndroidPart(AndroidPartSlot slot) {
        this.slot = slot;
        this.closeOnEsc = true;
        this.drawDefaultBackground = false;
        xSize = PADDING * 2 + COLS * SLOT_SIZE;
        ySize = PADDING * 2 + ROWS * SLOT_SIZE + 14 + 28;
    }

    @Override
    public void initGui() {
        super.initGui();
        int bY = guiTop + ySize - 22;
        addButton(new GuiNpcButton(200, guiLeft + PADDING, bY, 85, 20, "Remove"));
        addButton(new GuiNpcButton(201, guiLeft + xSize - PADDING - 85, bY, 85, 20, "Cancel"));
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;
        if (button.id == 200) {
            removeEquip = true;
            confirmed = true;
            close();
        } else if (button.id == 201) {
            close();
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton != 0) return;

        InventoryPlayer inv = Minecraft.getMinecraft().thePlayer.inventory;

        for (int i = 0; i < COLS * ROWS; i++) {
            if (i >= inv.mainInventory.length) break;
            if (!isValid(inv.mainInventory[i])) continue;
            int[] pos = slotPos(i);
            if (mouseX >= pos[0] && mouseX < pos[0] + SLOT_SIZE &&
                mouseY >= pos[1] && mouseY < pos[1] + SLOT_SIZE) {
                selectedStack = inv.mainInventory[i];
                confirmed = true;
                close();
                return;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawGradientRect(guiLeft, guiTop, guiLeft + xSize, guiTop + ySize, 0xf1101010, 0xf1101010);

        drawCenteredString(fontRendererObj, "§e" + slotLabel(slot), guiLeft + xSize / 2, guiTop + 4, 0xFFFFFF);

        InventoryPlayer inv = Minecraft.getMinecraft().thePlayer.inventory;

        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Minecraft.getMinecraft().getTextureManager().bindTexture(INVENTORY_TEXTURE);

        for (int i = 0; i < COLS * ROWS; i++) {
            int[] pos = slotPos(i);
            drawTexturedModalRect(pos[0], pos[1], SLOT_U, SLOT_V, SLOT_SIZE, SLOT_SIZE);
        }

        GL11.glDisable(GL11.GL_BLEND);

        ItemStack hoveredStack = null;
        int hoveredX = 0, hoveredY = 0;

        for (int i = 0; i < COLS * ROWS; i++) {
            ItemStack stack = i < inv.mainInventory.length ? inv.mainInventory[i] : null;
            if (stack == null) continue;

            int[] pos = slotPos(i);
            int x = pos[0], y = pos[1];
            boolean valid = isValid(stack);

            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);

            renderItem.renderItemAndEffectIntoGUI(fontRendererObj,
                Minecraft.getMinecraft().getTextureManager(),
                stack, x + 1, y + 1);
            renderItem.renderItemOverlayIntoGUI(fontRendererObj,
                Minecraft.getMinecraft().getTextureManager(),
                stack, x + 1, y + 1);

            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);

            if (!valid) {
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                drawRect(x + 1, y + 1, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, 0xaa000000);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }

            if (valid && mouseX >= x && mouseX < x + SLOT_SIZE && mouseY >= y && mouseY < y + SLOT_SIZE) {
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                drawRect(x + 1, y + 1, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, 0x80ffffff);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                hoveredStack = stack;
                hoveredX = mouseX;
                hoveredY = mouseY;
            }
        }

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (hoveredStack != null) {
            List<String> tooltip = hoveredStack.getTooltip(
                Minecraft.getMinecraft().thePlayer,
                Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
            drawHoveringText(tooltip, hoveredX, hoveredY, fontRendererObj);
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    // ── Helpers ──

    private int[] slotPos(int index) {
        int col = index % COLS;
        int row = index / COLS;
        int x = guiLeft + PADDING + col * SLOT_SIZE;
        int y = guiTop + 14 + PADDING + row * SLOT_SIZE;
        return new int[]{x, y};
    }

    private boolean isValid(ItemStack stack) {
        if (stack == null) return false;
        if (!(stack.getItem() instanceof ItemAndroidPart)) return false;
        AndroidPartType type = ItemAndroidPart.getPartType(stack);
        return type != null && type.fitsSlot(slot);
    }

    private String slotLabel(AndroidPartSlot slot) {
        switch (slot) {
            case CORE: return "Core";
            case BATTERY: return "Battery";
            case ARM_LEFT: return "Arm (L)";
            case ARM_RIGHT: return "Arm (R)";
            case LEG_LEFT: return "Leg (L)";
            case LEG_RIGHT: return "Leg (R)";
            default: return slot.name();
        }
    }

    @Override
    public void save() {}
}
