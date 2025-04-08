package kamkeel.npcdbc.client.gui.global.form;

import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormAdvanced;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.util.ValueUtil;

public class SubGuiFormAdvanced extends SubGuiInterface implements ISubGuiListener, ITextfieldListener {

    private GuiNpcFormMenu menu;
    public Form form;
    public FormAdvanced advanced;
    public GuiScrollWindow scrollWindow;

    public SubGuiFormAdvanced(GuiNPCManageForms parent, Form form) {
        this.form = form;
        this.advanced = form.advanced;
        setBackground("menubg.png");
        xSize = 360;
        ySize = 216;
        menu = new GuiNpcFormMenu(parent, this, -6, form);
    }

    public void initGui() {
        super.initGui();
        guiTop += 7;
        menu.initGui(guiLeft, guiTop, xSize);
        // Using the master's approach:
        int startY = guiTop + 3;
        if (scrollWindow == null) {
            scrollWindow = new GuiScrollWindow(this, guiLeft + 4, startY, xSize - 9, ySize - 7, 0);
        } else {
            scrollWindow.xPos = guiLeft + 4;
            scrollWindow.yPos = startY;
            scrollWindow.clipWidth = xSize - 9;
            scrollWindow.clipHeight = ySize - 7;
        }
        addScrollableGui(0, scrollWindow);

        int currentY = 5;
        int rowHeight = 23;

        // Loop for each DBC stat (0 to 11)
        for (int i = 0; i < 12; i++){
            // Add the toggle row (stat name and enable/disable button)
            scrollWindow.addLabel(new GuiNpcLabel(10 + i, FormAdvanced.STAT_NAMES[i], 4, currentY + 5, 0xffffff));
            boolean isEnabled = advanced.getStat(i).isEnabled();
            scrollWindow.addButton(new GuiNpcButtonYesNo(100 + i, 150, currentY, 50, 20, isEnabled));
            currentY += rowHeight;

            // If the stat is enabled, add an extra row for bonus and multiplier.
            if(isEnabled) {
                scrollWindow.addLabel(new GuiNpcLabel(200 + i, "Bonus", 4, currentY + 5, 0xd9d9d9));
                scrollWindow.addTextField(new GuiNpcTextField(200 + i, this, 50, currentY, 40, 20,
                    String.valueOf(advanced.getStat(i).getBonus())));
                GuiNpcTextField bonusField = scrollWindow.getTextField(200 + i);
                if (bonusField != null) {
                    bonusField.setMaxStringLength(10);
                    bonusField.integersOnly = true;
                    bonusField.setMinMaxDefault(0, 9999999, advanced.getStat(i).getBonus());
                }

                scrollWindow.addLabel(new GuiNpcLabel(300 + i, "Multi", 110, currentY + 5, 0xd9d9d9));
                scrollWindow.addTextField(new GuiNpcTextField(300 + i, this, 150, currentY, 40, 20,
                    String.valueOf(advanced.getStat(i).getMultiplier())));
                GuiNpcTextField multiField = scrollWindow.getTextField(300 + i);
                if (multiField != null) {
                    multiField.setMaxStringLength(10);
                    multiField.floatsOnly = true;
                    multiField.setMinMaxDefaultFloat(0, 10000, advanced.getStat(i).getMultiplier());
                }
                // Increment for the bonus/multi row.
                currentY += rowHeight;
            }
        }

        scrollWindow.maxScrollY = currentY - (ySize - 7);
    }

    public void buttonEvent(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;
        int id = button.id;
        if (id >= 100 && id < 112) {
            int statId = id - 100;
            advanced.getStat(statId).setEnabled(button.getValue() == 1);
        }


        float prevY = getScrollableGui(0).scrollY;
        initGui();
        prevY = ValueUtil.clamp(prevY, 0, getScrollableGui(0).maxScrollY);
        getScrollableGui(0).nextScrollY = getScrollableGui(0).scrollY = prevY;
    }

    @Override
    public void unFocused(GuiNpcTextField textField) {
        int id = textField.id;
        if (id >= 200 && id < 212) {
            int statId = id - 200;
            try {
                int bonus = textField.getInteger();
                advanced.getStat(statId).setBonus(bonus);
            } catch (NumberFormatException e) {
                // Optionally revert to the last valid value.
            }
        }

        if (id >= 300 && id < 312) {
            int statId = id - 300;
            try {
                float multi = textField.getFloat();
                advanced.getStat(statId).setMultiplier(multi);
            } catch (NumberFormatException e) {
                // Optionally revert to the last valid value.
            }
        }
    }

    @Override
    public void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);
        if (!hasSubGui())
            menu.mouseClicked(i, j, k);
    }

    @Override
    public void keyTyped(char c, int i) {
        super.keyTyped(c, i);
        if (i == 1)
            menu.close();
    }

    @Override
    public void subGuiClosed(SubGuiInterface subgui) {
        initGui();
    }

    @Override
    public void drawScreen(int i, int j, float f) {
        super.drawScreen(i, j, f);
        if (hasSubGui())
            return;
        menu.drawElements(fontRendererObj, i, j, mc, f);
    }

    public void save() {}
}
