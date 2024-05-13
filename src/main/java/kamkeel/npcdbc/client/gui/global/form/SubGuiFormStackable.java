package kamkeel.npcdbc.client.gui.global.form;

import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormStackable;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.*;

public class SubGuiFormStackable extends SubGuiInterface implements ISubGuiListener, GuiSelectionListener,ITextfieldListener
{
    private GuiNpcFormMenu menu;
	public Form form;
    public FormStackable stackable;

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

        addLabel(new GuiNpcLabel(1,"Vanilla Stackable", guiLeft + 4, y + 5));
        addButton(new GuiNpcButtonYesNo(1, guiLeft + 115, y, 50, 20, stackable.vanillaStackable));

        y += 23;

        addLabel(new GuiNpcLabel(2,"Kaioken Stackable", guiLeft + 4, y + 5));
        addButton(new GuiNpcButtonYesNo(2, guiLeft + 115, y, 50, 20, stackable.kaiokenStackable));

        if(stackable.kaiokenStackable){
            y += 23;

            addLabel(new GuiNpcLabel(22,"KK Strength %", guiLeft + 4, y + 5));
            addTextField(new GuiNpcTextField(22, this, guiLeft + 115, y, 50, 20, String.valueOf(stackable.kaiokenStrength)));
            getTextField(22).setMaxStringLength(22);
            getTextField(22).floatsOnly = true;
            getTextField(22).setMinMaxDefaultFloat(-10000, 10000, 1);

            addLabel(new GuiNpcLabel(23,"KK State 2 Strength %", guiLeft + 175, y + 5));
            addTextField(new GuiNpcTextField(23, this, guiLeft + 300, y, 50, 20, String.valueOf(stackable.kaiokenState2Factor)));
            getTextField(23).setMaxStringLength(23);
            getTextField(23).floatsOnly = true;
            getTextField(23).setMinMaxDefaultFloat(-10000, 10000, 1);
        }

        y += 23;

        addLabel(new GuiNpcLabel(3,"UI Stackable", guiLeft + 4, y + 5));
        addButton(new GuiNpcButtonYesNo(3, guiLeft + 115, y, 50, 20, stackable.uiStackable));

        if(stackable.uiStackable){
            y += 23;

            addLabel(new GuiNpcLabel(32,"UI Strength %", guiLeft + 4, y + 5));
            addTextField(new GuiNpcTextField(32, this, guiLeft + 115, y, 50, 20, String.valueOf(stackable.uiStrength)));
            getTextField(32).setMaxStringLength(32);
            getTextField(32).floatsOnly = true;
            getTextField(32).setMinMaxDefaultFloat(-10000, 10000, 1);

            addLabel(new GuiNpcLabel(33,"UI State 2 Strength %", guiLeft + 175, y + 5));
            addTextField(new GuiNpcTextField(33, this, guiLeft + 300, y, 50, 20, String.valueOf(stackable.uiState2Factor)));
            getTextField(33).setMaxStringLength(33);
            getTextField(33).floatsOnly = true;
            getTextField(33).setMinMaxDefaultFloat(-10000, 10000, 1);
        }

        y += 23;

        addLabel(new GuiNpcLabel(4,"GoD Stackable", guiLeft + 4, y + 5));
        addButton(new GuiNpcButtonYesNo(4, guiLeft + 115, y, 50, 20, stackable.godStackable));

        if(stackable.godStackable){
            y += 23;
            addLabel(new GuiNpcLabel(42,"GoD Strength %", guiLeft + 4, y + 5));
            addTextField(new GuiNpcTextField(42, this, guiLeft + 115, y, 50, 20, String.valueOf(stackable.godStrength)));
            getTextField(42).setMaxStringLength(42);
            getTextField(42).floatsOnly = true;
            getTextField(42).setMinMaxDefaultFloat(-10000, 10000, 1);
        }

        y += 23;

        addLabel(new GuiNpcLabel(5,"Mystic Stackable", guiLeft + 4, y + 5));
        addButton(new GuiNpcButtonYesNo(5, guiLeft + 115, y, 50, 20, stackable.mysticStackable));

        if(stackable.mysticStackable){
            y += 23;
            addLabel(new GuiNpcLabel(52,"Mystic Strength %", guiLeft + 4, y + 5));
            addTextField(new GuiNpcTextField(52, this, guiLeft + 115, y, 50, 20, String.valueOf(stackable.mysticStrength)));
            getTextField(52).setMaxStringLength(52);
            getTextField(52).floatsOnly = true;
            getTextField(52).setMinMaxDefaultFloat(-10000, 10000, 1);
        }
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
	}

    @Override
    public void mouseClicked(int i, int j, int k)
    {
        super.mouseClicked(i, j, k);
        if(!hasSubGui())
            menu.mouseClicked(i, j, k);
    }

	@Override
	public void subGuiClosed(SubGuiInterface subgui){}

    public void drawScreen(int i, int j, float f) {
        super.drawScreen(i, j, f);
        menu.drawElements(fontRendererObj, i, j, mc, f);
    }

	@Override
	public void selected(int id, String name) {}

	public void save(){}
}
