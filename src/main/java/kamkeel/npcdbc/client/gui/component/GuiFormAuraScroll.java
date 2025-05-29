package kamkeel.npcdbc.client.gui.component;

import kamkeel.npcdbc.client.gui.inventory.GuiDBC;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcs.util.TextSplitter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.List;

public class GuiFormAuraScroll extends GuiCustomScroll {
    private final HashMap<String, String> formDisplays = new HashMap<>();
    private final HashMap<String, String> auraDisplays = new HashMap<>();
    private int hoverCount = 0;
    int hoverColor;

    public GuiFormAuraScroll(GuiScreen parent, int id) {
        super(parent, id);
        visible = true;
        multipleSelection = false;
        for (Form allForms : FormController.getInstance().customForms.values()) {
            formDisplays.put(allForms.getName(), allForms.getMenuName());
        }
        for (Aura allAuras : AuraController.getInstance().customAuras.values()) {
            auraDisplays.put(allAuras.getName(), allAuras.getMenuName());
        }
    }

    @Override
    protected void drawItems() {
        for (int i = 0; i < list.size(); i++) {
            int j = 4;
            int k = (14 * i + 4) - scrollY;
            if (k >= 4 && k + 12 < ySize) {
                int xOffset = scrollHeight < ySize - 8 ? 0 : 10;
                String rawName = list.get(i);
                String menuName = "";
                if (GuiDBC.activePage == 0)
                    menuName = formDisplays.get(list.get(i));
                if (GuiDBC.activePage == 1)
                    menuName = auraDisplays.get(list.get(i));
                if (menuName == null || menuName.isEmpty())
                    menuName = "";
                String displayString = StatCollector.translateToLocal(menuName);
                String text = "";
                float maxWidth = (xSize + xOffset - 8) * 0.8f;

                if (fontRendererObj.getStringWidth(displayString) > maxWidth) {
                    for (int h = 0; h < displayString.length(); h++) {
                        char c = displayString.charAt(h);
                        text += c;
                        if (fontRendererObj.getStringWidth(text) > maxWidth)
                            break;
                    }
                    if (displayString.length() > text.length())
                        text += "...";
                } else
                    text = displayString;
                if ((multipleSelection && selectedList.contains(text)) || (!multipleSelection && selected == i)) { //if selected
                    drawVerticalLine(j - 2, k - 4, k + 10, 0xffffffff);
                    drawVerticalLine(j + xSize - 18 + xOffset, k - 4, k + 10, 0xffffffff);
                    drawHorizontalLine(j - 2, j + xSize - 18 + xOffset, k - 3, 0xffffffff);
                    drawHorizontalLine(j - 2, j + xSize - 18 + xOffset, k + 10, 0xffffffff);
                    fontRendererObj.drawString(text, j, k, 16777215);
                    setSelected(rawName);
                } else if (i == hover) { //if hovering over
                    GL11.glPushMatrix();
                    fontRendererObj.drawString(text, j, k, hoverColor);
                    GL11.glPopMatrix();
                } else { //everything else
                    GL11.glPushMatrix();
                    fontRendererObj.drawString(text, j, k, 16777215);
                    fontRendererObj.drawStringWithShadow(text, j, k, 16777215);
                    GL11.glPopMatrix();
                }

            }
        }
    }

    @Override
    public void drawHover(int i, int j) {
        if (this.visible) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float) this.guiLeft, (float) this.guiTop, 0.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            if (this.oldHover != this.hover) {
                this.oldHover = this.hover;
                this.hoverCount = 0;
            } else if (this.hoverCount < 110) {
                ++this.hoverCount;
            }

            if (this.hover != -1 && this.hoverCount > 100) {
                String menuName = "";
                if (GuiDBC.activePage == 0)
                    menuName = formDisplays.get(this.list.get(this.hover));
                if (GuiDBC.activePage == 1)
                    menuName = auraDisplays.get(this.list.get(this.hover));
                if (menuName == null || menuName.isEmpty())
                    menuName = "";
                String displayString = StatCollector.translateToLocal(menuName);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                List<String> lines = TextSplitter.splitText(displayString, 30);
                super.drawHoveringText(lines, i - this.guiLeft, j - this.guiTop, this.fontRendererObj);
                GL11.glDisable(2896);
            }

            GL11.glPopMatrix();
        }
    }
}
