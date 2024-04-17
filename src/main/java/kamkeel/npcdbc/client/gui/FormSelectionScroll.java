package kamkeel.npcdbc.client.gui;

import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.CustomForm;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

public class FormSelectionScroll extends GuiCustomScroll {
    private final HashMap<String, String> formDisplays = new HashMap<>();
    int hoverColor;

    public FormSelectionScroll(GuiScreen parent, int id) {
        super(parent, id);
        visible = true;
        multipleSelection = false;
        for(CustomForm allForms : FormController.getInstance().customForms.values()){
            formDisplays.put(allForms.getName(), allForms.getMenuName());
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
                String menuName = formDisplays.get(list.get(i));
                if(menuName.isEmpty())
                    menuName = "Missing";
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
}
