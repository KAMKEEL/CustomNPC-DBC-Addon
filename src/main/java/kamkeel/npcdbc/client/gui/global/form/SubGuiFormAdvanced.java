package kamkeel.npcdbc.client.gui.global.form;

import kamkeel.npcdbc.constants.DBCStatistics;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormAdvanced;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.GuiNpcButtonYesNo;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.client.gui.util.GuiNpcTextField;
import noppes.npcs.client.gui.util.GuiScrollWindow;
import noppes.npcs.client.gui.util.ISubGuiListener;
import noppes.npcs.client.gui.util.ITextfieldListener;
import noppes.npcs.client.gui.util.SubGuiInterface;
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

    @Override
    public void initGui() {
        super.initGui();
        guiTop += 7;
        menu.initGui(guiLeft, guiTop, xSize);

        if (scrollWindow == null) {
            scrollWindow = new GuiScrollWindow(this, guiLeft + 4, guiTop + 3, xSize - 9, ySize - 7, 0);
        } else {
            scrollWindow.xPos = guiLeft + 4;
            scrollWindow.yPos = guiTop + 3;
            scrollWindow.clipWidth = xSize - 9;
            scrollWindow.clipHeight = ySize - 7;
        }
        addScrollableGui(0, scrollWindow);

        int currentY = 5;
        int rowHeight = 23;

        // Loop through each stat, skipping MaxSkills (index 6)
        for (int i = 0; i < FormAdvanced.STAT_NAMES.length; i++) {
            if (i == DBCStatistics.MaxSkills) {
                continue;
            }

            // Stat label + toggle
            scrollWindow.addLabel(new GuiNpcLabel(10 + i, FormAdvanced.STAT_NAMES[i], 4, currentY + 5, 0xffffff));
            boolean isEnabled = advanced.getStat(i).isEnabled();
            scrollWindow.addButton(new GuiNpcButtonYesNo(100 + i, 150, currentY, 50, 20, isEnabled));
            currentY += rowHeight;

            // If enabled, show bonus & multiplier inputs
            if (isEnabled) {
                // Bonus
                scrollWindow.addLabel(new GuiNpcLabel(200 + i, "Bonus", 4, currentY + 5, 0xd9d9d9));
                scrollWindow.addTextField(new GuiNpcTextField(200 + i, this, 50, currentY, 40, 20,
                    String.valueOf(advanced.getStat(i).getBonus())));
                GuiNpcTextField bonusField = scrollWindow.getTextField(200 + i);
                if (bonusField != null) {
                    bonusField.setMaxStringLength(10);
                    bonusField.integersOnly = true;
                    bonusField.setMinMaxDefault(0, 9999999, advanced.getStat(i).getBonus());
                }

                // Multiplier
                scrollWindow.addLabel(new GuiNpcLabel(300 + i, "Multi", 110, currentY + 5, 0xd9d9d9));
                scrollWindow.addTextField(new GuiNpcTextField(300 + i, this, 150, currentY, 40, 20,
                    String.valueOf(advanced.getStat(i).getMultiplier())));
                GuiNpcTextField multiField = scrollWindow.getTextField(300 + i);
                if (multiField != null) {
                    multiField.setMaxStringLength(10);
                    multiField.floatsOnly = true;
                    multiField.setMinMaxDefaultFloat(0, 10000, advanced.getStat(i).getMultiplier());
                }

                currentY += rowHeight;
            }
        }

        scrollWindow.maxScrollY = currentY - (ySize - 7);
    }

    @Override
    public void buttonEvent(GuiButton guibutton) {
        int id = guibutton.id;
        // Toggle buttons are 100 + statIndex
        if (id >= 100 && id < 100 + FormAdvanced.STAT_NAMES.length) {
            int statId = id - 100;
            if (statId != DBCStatistics.MaxSkills) {
                FormAdvanced.AdvancedFormStat stat = advanced.getStat(statId);
                if (stat != null) {
                    boolean newVal = ((noppes.npcs.client.gui.util.GuiNpcButtonYesNo)guibutton).getValue() == 1;
                    advanced.setStatEnabled(statId, newVal);
                }
            }

            // preserve scroll position
            float prevY = scrollWindow.scrollY;
            initGui();
            prevY = ValueUtil.clamp(prevY, 0, scrollWindow.maxScrollY);
            scrollWindow.nextScrollY = scrollWindow.scrollY = prevY;
        }
    }

    @Override
    public void unFocused(noppes.npcs.client.gui.util.GuiNpcTextField textField) {
        int id = textField.id;
        // Bonus fields: 200 + statIndex
        if (id >= 200 && id < 200 + FormAdvanced.STAT_NAMES.length) {
            int statId = id - 200;
            if (statId != DBCStatistics.MaxSkills) {
                try {
                    int bonus = textField.getInteger();
                    advanced.setStatBonus(statId, bonus);
                } catch (NumberFormatException ignored) { }
            }
        }
        // Multiplier fields: 300 + statIndex
        if (id >= 300 && id < 300 + FormAdvanced.STAT_NAMES.length) {
            int statId = id - 300;
            if (statId != DBCStatistics.MaxSkills) {
                try {
                    float multi = textField.getFloat();
                    advanced.setStatMulti(statId, multi);
                } catch (NumberFormatException ignored) { }
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        menu.mouseClicked(mouseX, mouseY, mouseButton);
    }


    @Override
    public void subGuiClosed(SubGuiInterface subgui) {
        initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        menu.drawElements(fontRendererObj, mouseX, mouseY, mc, partialTicks);
    }

    /** no‑op save; changes are written immediately */
    public void save() {
        menu.save();
    }
}
