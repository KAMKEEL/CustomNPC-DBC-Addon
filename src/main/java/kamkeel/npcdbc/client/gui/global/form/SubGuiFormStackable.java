package kamkeel.npcdbc.client.gui.global.form;

import kamkeel.npcdbc.client.utils.Color;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormStackable;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.SubGuiColorSelector;
import noppes.npcs.client.gui.util.*;

public class SubGuiFormStackable extends SubGuiInterface implements ISubGuiListener, GuiSelectionListener,ITextfieldListener
{
    private GuiNpcFormMenu menu;
	public Form form;
    public FormStackable stackable;
    public int lastColorClicked = 0;
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
            scrollWindow = new GuiScrollWindow(this, guiLeft, y, xSize - 9, ySize - 7, 0);
        }
        addScrollableGui(0, scrollWindow);

        int maxScroll = -20;
        int guiLeft = 0;
        y = 2;
        scrollWindow.addLabel(new GuiNpcLabel(1, "display.vanillaStackable", 4, y + 5));
        scrollWindow.addButton(new GuiNpcButtonYesNo(1, guiLeft + 115, y, 50, 20, stackable.vanillaStackable));
        scrollWindow.getLabel(1).color = 0xffffff;

        y += 23;
        maxScroll += 23;
        scrollWindow.addLabel(new GuiNpcLabel(2, "Kaioken Stackable", guiLeft + 4, y + 5));
        scrollWindow.addButton(new GuiNpcButtonYesNo(2, guiLeft + 115, y, 50, 20, stackable.kaiokenStackable));
        scrollWindow.getLabel(2).color = 0xffffff;

        if(stackable.kaiokenStackable){
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
            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(52, "display.mysticRatio", guiLeft + 4, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(52, this, guiLeft + 115, y, 50, 20, String.valueOf(stackable.mysticStrength)));
            scrollWindow.getTextField(52).setMaxStringLength(52);
            scrollWindow.getTextField(52).floatsOnly = true;
            scrollWindow.getTextField(52).setMinMaxDefaultFloat(-10000, 10000, 1);
            scrollWindow.getLabel(52).color = 0xffffff;
        }

        y += 23;
        maxScroll += 23;
        scrollWindow.addLabel(new GuiNpcLabel(6, "display.legendaryMulti", guiLeft + 4, y + 5));
        scrollWindow.addTextField(new GuiNpcTextField(6, this, guiLeft + 115, y, 50, 20, String.valueOf(stackable.legendaryStrength)));
        scrollWindow.getTextField(6).setMaxStringLength(22);
        scrollWindow.getTextField(6).floatsOnly = true;
        scrollWindow.getTextField(6).setMinMaxDefaultFloat(-10000, 10000, 1);
        scrollWindow.getLabel(6).color = 0xffffff;

        scrollWindow.addLabel(new GuiNpcLabel(61, "display.legendaryColor", guiLeft + 175, y + 5));
        scrollWindow.addButton(new GuiNpcButton(62, guiLeft + 265, y, 50, 20, Color.getColor(form.display.legendaryColor)));
        scrollWindow.getButton(62).packedFGColour = form.display.legendaryColor;
        scrollWindow.addButton(new GuiNpcButton(621, guiLeft + 318, y, 20, 20, "X"));
        scrollWindow.getButton(621).enabled = form.display.legendaryColor != -1;
        scrollWindow.getLabel(61).color = 0xffffff;


        y += 23;
        maxScroll += 23;
        scrollWindow.addLabel(new GuiNpcLabel(7, "display.divineMulti", guiLeft + 4, y + 5));
        scrollWindow.addTextField(new GuiNpcTextField(7, this, guiLeft + 115, y, 50, 20, String.valueOf(stackable.divineStrength)));
        scrollWindow.getTextField(7).setMaxStringLength(22);
        scrollWindow.getTextField(7).floatsOnly = true;
        scrollWindow.getTextField(7).setMinMaxDefaultFloat(-10000, 10000, 1);
        scrollWindow.getLabel(7).color = 0xffffff;

        scrollWindow.addLabel(new GuiNpcLabel(71, "display.divineColor", guiLeft + 175, y + 5));
        scrollWindow.addButton(new GuiNpcButton(72, guiLeft + 265, y, 50, 20, Color.getColor(form.display.divineColor)));
        scrollWindow.getButton(72).packedFGColour = form.display.divineColor;
        scrollWindow.addButton(new GuiNpcButton(721, guiLeft + 318, y, 20, 20, "X"));
        scrollWindow.getButton(721).enabled = form.display.divineColor != -1;
        scrollWindow.getLabel(71).color = 0xffffff;

        y += 23;
        maxScroll += 23;
        scrollWindow.addLabel(new GuiNpcLabel(8, "display.majinMulti", guiLeft + 4, y + 5));
        scrollWindow.addTextField(new GuiNpcTextField(8, this, guiLeft + 115, y, 50, 20, String.valueOf(stackable.majinStrength)));
        scrollWindow.getTextField(8).setMaxStringLength(22);
        scrollWindow.getTextField(8).floatsOnly = true;
        scrollWindow.getTextField(8).setMinMaxDefaultFloat(-10000, 10000, 1);
        scrollWindow.getLabel(8).color = 0xffffff;

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
        // Legendary
        if (button.id == 62) {
            lastColorClicked = 0;
            setSubGui(new SubGuiColorSelector(form.display.legendaryColor));
        }
        // Legendary Clear
        if (button.id == 621) {
            form.display.legendaryColor = -1;
        }
        // Divine
        if (button.id == 72) {
            lastColorClicked = 1;
            setSubGui(new SubGuiColorSelector(form.display.divineColor));
        }
        // Divine Clear
        if (button.id == 721) {
            form.display.divineColor = -1;
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
        if (guiNpcTextField.id == 6) {
            stackable.legendaryStrength = guiNpcTextField.getFloat();
        }
        if (guiNpcTextField.id == 7) {
            stackable.divineStrength = guiNpcTextField.getFloat();
        }
        if (guiNpcTextField.id == 8) {
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
    public void subGuiClosed(SubGuiInterface subgui) {
        if (subgui instanceof SubGuiColorSelector) {
            int color = ((SubGuiColorSelector) subgui).color;
            if (lastColorClicked == 0) {
                form.display.legendaryColor = color;
            } else if (lastColorClicked == 1) {
                form.display.divineColor = color;
            }
            initGui();
        }
    }

    public void drawScreen(int i, int j, float f) {
        super.drawScreen(i, j, f);
        menu.drawElements(fontRendererObj, i, j, mc, f);
    }

	@Override
	public void selected(int id, String name) {}

	public void save(){}
}
