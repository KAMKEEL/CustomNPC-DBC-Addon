package kamkeel.npcdbc.client.gui.global.form;

import kamkeel.npcdbc.client.gui.component.SubGuiSelectForm;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormStackable;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.*;

public class SubGuiFormStackable extends SubGuiInterface implements ISubGuiListener, GuiSelectionListener,ITextfieldListener
{
    private GuiNpcFormMenu menu;
	public Form form;
    public FormStackable stackable;
    public GuiScrollWindow scrollWindow;

	public SubGuiFormStackable(GuiNPCManageForms parent, Form form)
	{
		this.form = form;
        this.stackable = form.stackable;

		setBackground("menubg.png");
		xSize = 360;
		ySize = 216;

        menu = new GuiNpcFormMenu(parent, this, -4, form);
	}

    public void initGui()
    {
        super.initGui();
        guiTop += 7;
        menu.initGui(guiLeft, guiTop, xSize);

        int y = guiTop + 3;

        if (scrollWindow == null) {
            scrollWindow = new GuiScrollWindow(this, guiLeft + 4, y, xSize - 9, ySize - 7, 0);
        }else{
            scrollWindow.xPos = guiLeft + 4;
            scrollWindow.yPos = y;
            scrollWindow.clipWidth = xSize - 9;
            scrollWindow.clipHeight = ySize - 7;
        }

        addScrollableGui(0, scrollWindow);

        int maxScroll = -20;
        int guiLeft = 0;
        y = 2;
        scrollWindow.addLabel(new GuiNpcLabel(1, "display.vanillaStackable", 4, y + 5));
        scrollWindow.addButton(new GuiNpcButtonYesNo(1, guiLeft + 115, y, 50, 20, stackable.vanillaStackable));
        scrollWindow.getLabel(1).color = 0xffffff;

        y += 23;
        scrollWindow.addLabel(new GuiNpcLabel(2, "display.kkStackable", guiLeft + 4, y + 5));
        scrollWindow.addButton(new GuiNpcButtonYesNo(2, guiLeft + 115, y, 50, 20, stackable.kaiokenStackable));
        scrollWindow.getLabel(2).color = 0xffffff;

        if(stackable.kaiokenStackable){
            maxScroll += 23;
            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(22, "display.kkRatio", guiLeft + 4, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(22, this, guiLeft + 115, y, 50, 20, String.valueOf(stackable.kaiokenStrength)));
            scrollWindow.getTextField(22).setMaxStringLength(22);
            scrollWindow.getTextField(22).floatsOnly = true;
            scrollWindow.getTextField(22).setMinMaxDefaultFloat(-10000, 10000, 1);
            scrollWindow.getLabel(22).color = 0xffffff;

            scrollWindow.addLabel(new GuiNpcLabel(23, "display.kkLevelRatio", guiLeft + 175, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(23, this, guiLeft + 265, y, 50, 20, String.valueOf(stackable.kaiokenState2Factor)));
            scrollWindow.getTextField(23).setMaxStringLength(23);
            scrollWindow.getTextField(23).floatsOnly = true;
            scrollWindow.getTextField(23).setMinMaxDefaultFloat(-10000, 10000, 1);
            scrollWindow.getLabel(23).color = 0xffffff;
        }

        y += 23;
        scrollWindow.addLabel(new GuiNpcLabel(3, "display.uiStackable", guiLeft + 4, y + 5));
        scrollWindow.addButton(new GuiNpcButtonYesNo(3, guiLeft + 115, y, 50, 20, stackable.uiStackable));
        scrollWindow.getLabel(3).color = 0xffffff;

        if(stackable.uiStackable){
            maxScroll += 23;
            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(32, "display.uiRatio", guiLeft + 4, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(32, this, guiLeft + 115, y, 50, 20, String.valueOf(stackable.uiStrength)));
            scrollWindow.getTextField(32).setMaxStringLength(32);
            scrollWindow.getTextField(32).floatsOnly = true;
            scrollWindow.getTextField(32).setMinMaxDefaultFloat(-10000, 10000, 1);
            scrollWindow.getLabel(32).color = 0xffffff;

            scrollWindow.addLabel(new GuiNpcLabel(33, "display.uiLevelRatio", guiLeft + 175, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(33, this, guiLeft + 265, y, 50, 20, String.valueOf(stackable.uiState2Factor)));
            scrollWindow.getTextField(33).setMaxStringLength(33);
            scrollWindow.getTextField(33).floatsOnly = true;
            scrollWindow.getTextField(33).setMinMaxDefaultFloat(-10000, 10000, 1);
            scrollWindow.getLabel(33).color = 0xffffff;
        }

        y += 23;
        scrollWindow.addLabel(new GuiNpcLabel(4, "display.godStackable", guiLeft + 4, y + 5));
        scrollWindow.addButton(new GuiNpcButtonYesNo(4, guiLeft + 115, y, 50, 20, stackable.godStackable));
        scrollWindow.getLabel(4).color = 0xffffff;

        if(stackable.godStackable){
            maxScroll += 23;
            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(42, "display.godRatio", guiLeft + 4, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(42, this, guiLeft + 115, y, 50, 20, String.valueOf(stackable.godStrength)));
            scrollWindow.getTextField(42).setMaxStringLength(42);
            scrollWindow.getTextField(42).floatsOnly = true;
            scrollWindow.getTextField(42).setMinMaxDefaultFloat(-10000, 10000, 1);
            scrollWindow.getLabel(42).color = 0xffffff;
        }

        y += 23;
        scrollWindow.addLabel(new GuiNpcLabel(5, "display.mysticStackable", guiLeft + 4, y + 5));
        scrollWindow.addButton(new GuiNpcButtonYesNo(5, guiLeft + 115, y, 50, 20, stackable.mysticStackable));
        scrollWindow.getLabel(5).color = 0xffffff;

        if(stackable.mysticStackable){
            maxScroll += 23;
            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(52, "display.mysticRatio", guiLeft + 4, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(52, this, guiLeft + 115, y, 50, 20, String.valueOf(stackable.mysticStrength)));
            scrollWindow.getTextField(52).setMaxStringLength(52);
            scrollWindow.getTextField(52).floatsOnly = true;
            scrollWindow.getTextField(52).setMinMaxDefaultFloat(-10000, 10000, 1);
            scrollWindow.getLabel(52).color = 0xffffff;
        }


        y += 46;
        maxScroll += 23;

        scrollWindow.addButton(new GuiNpcButton(6, guiLeft + 75, y, 90, 20, "general.noForm"));
        scrollWindow.addButton(new GuiNpcButton(61, guiLeft + 167, y, 20, 20, "X"));
        scrollWindow.addLabel(new GuiNpcLabel(62, "Legendary", guiLeft + 4, y + 5));
        scrollWindow.getButton(61).enabled = stackable.legendaryID != -1;
        scrollWindow.getLabel(62).color = 0xffffff;
        if (stackable.legendaryID != -1) {
            if (FormController.getInstance().has(stackable.legendaryID))
                scrollWindow.getButton(6).setDisplayText(FormController.getInstance().get(stackable.legendaryID).getName());
        }
        scrollWindow.addLabel(new GuiNpcLabel(63, "Config Multi", guiLeft + 197, y + 5));
        scrollWindow.addButton(new GuiNpcButtonYesNo(65, guiLeft + 260, y, 30, 20, stackable.useLegendaryConfig));
        if (!stackable.useLegendaryConfig) {
            scrollWindow.addTextField(new GuiNpcTextField(64, this, guiLeft + 293, y, 44, 20, String.valueOf(stackable.legendaryStrength)));
            scrollWindow.getTextField(64).setMaxStringLength(22);
            scrollWindow.getTextField(64).floatsOnly = true;
            scrollWindow.getTextField(64).setMinMaxDefaultFloat(-10000, 10000, 1);
        }
        scrollWindow.getLabel(63).color = 0xffffff;


        y += 23;

        scrollWindow.addButton(new GuiNpcButton(7, guiLeft + 75, y, 90, 20, "general.noForm"));
        scrollWindow.addButton(new GuiNpcButton(71, guiLeft + 167, y, 20, 20, "X"));
        scrollWindow.addLabel(new GuiNpcLabel(72, "Divine", guiLeft + 4, y + 5));
        scrollWindow.getButton(71).enabled = stackable.divineID != -1;
        scrollWindow.getLabel(72).color = 0xffffff;

        if (stackable.divineID != -1) {
            if (FormController.getInstance().has(stackable.divineID))
                scrollWindow.getButton(7).setDisplayText(FormController.getInstance().get(stackable.divineID).getName());
        }
        scrollWindow.addLabel(new GuiNpcLabel(73, "Config Multi", guiLeft + 197, y + 5));
        scrollWindow.addButton(new GuiNpcButtonYesNo(75, guiLeft + 260, y, 30, 20, stackable.useDivineConfig));
        if (!stackable.useDivineConfig) {
            scrollWindow.addTextField(new GuiNpcTextField(74, this, guiLeft + 293, y, 44, 20, String.valueOf(stackable.divineStrength)));
            scrollWindow.getTextField(74).setMaxStringLength(22);
            scrollWindow.getTextField(74).floatsOnly = true;
            scrollWindow.getTextField(74).setMinMaxDefaultFloat(-10000, 10000, 1);
        }
        scrollWindow.getLabel(73).color = 0xffffff;


        y += 23;

        scrollWindow.addButton(new GuiNpcButton(8, guiLeft + 75, y, 90, 20, "general.noForm"));
        scrollWindow.addButton(new GuiNpcButton(81, guiLeft + 167, y, 20, 20, "X"));
        scrollWindow.addLabel(new GuiNpcLabel(82, "Majin", guiLeft + 4, y + 5));
        scrollWindow.getButton(81).enabled = stackable.majinID != -1;
        scrollWindow.getLabel(82).color = 0xffffff;

        if (stackable.majinID != -1) {
            if (FormController.getInstance().has(stackable.majinID))
                scrollWindow.getButton(8).setDisplayText(FormController.getInstance().get(stackable.majinID).getName());
        }
        scrollWindow.addLabel(new GuiNpcLabel(83, "Config Multi", guiLeft + 197, y + 5));
        scrollWindow.addButton(new GuiNpcButtonYesNo(85, guiLeft + 260, y, 30, 20, stackable.useMajinConfig));
        if (!stackable.useMajinConfig) {
            scrollWindow.addTextField(new GuiNpcTextField(84, this, guiLeft + 293, y, 44, 20, String.valueOf(stackable.majinStrength)));
            scrollWindow.getTextField(84).setMaxStringLength(22);
            scrollWindow.getTextField(84).floatsOnly = true;
            scrollWindow.getTextField(84).setMinMaxDefaultFloat(-10000, 10000, 1);
        }
        scrollWindow.getLabel(83).color = 0xffffff;

        scrollWindow.maxScrollY = maxScroll;
    }

	public void buttonEvent(GuiButton guibutton)
    {
		GuiNpcButton button = (GuiNpcButton) guibutton;
        if(button.id == 1){
            stackable.vanillaStackable = button.getValue() == 1;
        }
        if(button.id == 2){
            stackable.kaiokenStackable = button.getValue() == 1;
        }
        if(button.id == 3){
            stackable.uiStackable = button.getValue() == 1;
        }
        if(button.id == 4){
            stackable.godStackable = button.getValue() == 1;
        }
        if(button.id == 5){
            stackable.mysticStackable = button.getValue() == 1;
        }


        if (button.id == 6) {
            this.setSubGui(new SubGuiSelectForm(6,false));
        }
        if (button.id == 61) {
            stackable.legendaryID = -1;
            initGui();
        }
        if (button.id == 65) {
            stackable.useLegendaryConfig = button.getValue() == 1;
        }

        if (button.id == 7) {
            this.setSubGui(new SubGuiSelectForm(7,false));
        }
        if (button.id == 71) {
            stackable.divineID = -1;
            initGui();
        }
        if (button.id == 75) {
            stackable.useDivineConfig = button.getValue() == 1;
        }

        if (button.id == 8) {
            this.setSubGui(new SubGuiSelectForm(8,false));
        }
        if (button.id == 81) {
            stackable.majinID = -1;
            initGui();
        }
        if (button.id == 85) {
            stackable.useMajinConfig = button.getValue() == 1;
        }
        initGui();
	}


	@Override
	public void unFocused(GuiNpcTextField guiNpcTextField) {
        if (guiNpcTextField.id == 22) {
            stackable.kaiokenStrength = guiNpcTextField.getFloat();
        }
        if (guiNpcTextField.id == 23) {
            stackable.kaiokenStrength = guiNpcTextField.getFloat();
        }

        if (guiNpcTextField.id == 32) {
            stackable.uiStrength = guiNpcTextField.getFloat();
        }
        if (guiNpcTextField.id == 33) {
            stackable.uiState2Factor = guiNpcTextField.getFloat();
        }

        if (guiNpcTextField.id == 42) {
            stackable.godStrength = guiNpcTextField.getFloat();
        }

        if (guiNpcTextField.id == 52) {
            stackable.mysticStrength = guiNpcTextField.getFloat();
        }
        if (guiNpcTextField.id == 64) {
            stackable.legendaryStrength = guiNpcTextField.getFloat();
        }
        if (guiNpcTextField.id == 74) {
            stackable.divineStrength = guiNpcTextField.getFloat();
        }
        if (guiNpcTextField.id == 84) {
            stackable.majinStrength = guiNpcTextField.getFloat();
        }
	}

    @Override
    public void mouseClicked(int i, int j, int k)
    {
        super.mouseClicked(i, j, k);
        if(!hasSubGui())
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
        if (subgui instanceof SubGuiSelectForm) {
            if (form != null) {
                SubGuiSelectForm guiSelectForm = ((SubGuiSelectForm) subgui);
                if (guiSelectForm.confirmed) {
                    if (guiSelectForm.selectedFormID == form.id)
                        return;
                    if (guiSelectForm.buttonID == 6) {
                        form.stackable.legendaryID = guiSelectForm.selectedFormID;
                    } else if (guiSelectForm.buttonID == 7) {
                        form.stackable.divineID = guiSelectForm.selectedFormID;
                    } else if (guiSelectForm.buttonID == 8) {
                        form.stackable.majinID = guiSelectForm.selectedFormID;

                    }
                }
            }
            initGui();
        }
    }

    public void drawScreen(int i, int j, float f) {
        super.drawScreen(i, j, f);

        if(hasSubGui())
            return;

        menu.drawElements(fontRendererObj, i, j, mc, f);
    }

	@Override
	public void selected(int id, String name) {}

	public void save(){}
}
