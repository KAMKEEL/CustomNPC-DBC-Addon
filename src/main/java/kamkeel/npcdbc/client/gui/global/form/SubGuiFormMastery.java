package kamkeel.npcdbc.client.gui.global.form;

import kamkeel.npcdbc.data.form.Form;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.*;

public class SubGuiFormMastery extends SubGuiInterface implements ISubGuiListener, GuiSelectionListener,ITextfieldListener
{
    private GuiNpcFormMenu menu;
	public Form form;

    public SubGuiFormMastery(GuiNPCManageForms parent, Form form)
	{
		this.form = form;

		setBackground("menubg.png");
		xSize = 360;
		ySize = 216;

        menu = new GuiNpcFormMenu(parent, this, -3, form);
	}

    public void initGui()
    {
        super.initGui();
        guiTop += 7;
        menu.initGui(guiLeft, guiTop, xSize);

        int y = guiTop + 3;

        GuiScrollWindow guiScrollWindow = new GuiScrollWindow(this, guiLeft +4, y, xSize-9, ySize-7, 200);
        addScrollableGui(0, guiScrollWindow);
        y = 23;


        guiScrollWindow.addLabel(new GuiNpcLabel(1,"Max Mastery Level", 4, y + 5));
        guiScrollWindow.addTextField(new GuiNpcTextField(1, this, 135, y, 50, 20, String.valueOf(form.mastery.maxLevel)));
        guiScrollWindow.getTextField(1).setMaxStringLength(10);
        guiScrollWindow.getTextField(1).floatsOnly = true;
        guiScrollWindow.getTextField(1).setMinMaxDefaultFloat(-10000, 10000, 1);
        guiScrollWindow.getLabel(1).color = 0xd3d3d3;


        guiScrollWindow.addLabel(new GuiNpcLabel(8,"Multi Flat", 190, y + 5));
        guiScrollWindow.addTextField(new GuiNpcTextField(8, this, 300, y, 50, 20, String.valueOf(form.mastery.attributeMultiFlat)));
        guiScrollWindow.getTextField(8).setMaxStringLength(10);
        guiScrollWindow.getTextField(8).floatsOnly = true;
        guiScrollWindow.getTextField(8).setMinMaxDefaultFloat(-10000, 10000, 1);
        guiScrollWindow.getLabel(8).color = 0xd3d3d3;

        y += 23;

        guiScrollWindow.addLabel(new GuiNpcLabel(10,"Multi Min/Max", 4, y + 5));
        guiScrollWindow.addTextField(new GuiNpcTextField(10, this, 135, y, 50, 20, String.valueOf(form.mastery.attributeMultiMinOrMax)));
        guiScrollWindow.getTextField(10).setMaxStringLength(10);
        guiScrollWindow.getTextField(10).floatsOnly = true;
        guiScrollWindow.getTextField(10).setMinMaxDefaultFloat(-10000, 10000, 1);
        guiScrollWindow.getLabel(10).color = 0xd3d3d3;

        guiScrollWindow.addLabel(new GuiNpcLabel(9,"Multi Per Level", 190, y + 5));
        guiScrollWindow.addTextField(new GuiNpcTextField(9, this, 300, y, 50, 20, String.valueOf(form.mastery.attributeMultiPerLevel)));
        guiScrollWindow.getTextField(9).setMaxStringLength(10);
        guiScrollWindow.getTextField(9).floatsOnly = true;
        guiScrollWindow.getTextField(9).setMinMaxDefaultFloat(-10000, 10000, 1);
        guiScrollWindow.getLabel(9).color = 0xd3d3d3;

        y += 23;
        y += 23;

        guiScrollWindow.addLabel(new GuiNpcLabel(2,"Instant Transform Level", 4, y + 5));
        guiScrollWindow.addTextField(new GuiNpcTextField(2, this, 135, y, 50, 20, String.valueOf(form.mastery.instantTransformationUnlockLevel)));
        guiScrollWindow.getTextField(2).setMaxStringLength(10);
        guiScrollWindow.getTextField(2).floatsOnly = true;
        guiScrollWindow.getTextField(2).setMinMaxDefaultFloat(-10000, 10000, 1);
        guiScrollWindow.getLabel(2).color = 0xd3d3d3;

        guiScrollWindow.addLabel(new GuiNpcLabel(4,"Max Heat", 190, y + 5));
        guiScrollWindow.addTextField(new GuiNpcTextField(4, this, 300, y, 50, 20, String.valueOf(form.mastery.maxHeat)));
        guiScrollWindow.getTextField(4).setMaxStringLength(10);
        guiScrollWindow.getTextField(4).floatsOnly = true;
        guiScrollWindow.getTextField(4).setMinMaxDefaultFloat(-10000, 10000, 1);
        guiScrollWindow.getLabel(4).color = 0xd3d3d3;

        y += 23;

        guiScrollWindow.addLabel(new GuiNpcLabel(11,"Ki Drain", 4, y + 5));
        guiScrollWindow.addTextField(new GuiNpcTextField(11, this, 135, y, 50, 20, String.valueOf(form.mastery.kiDrain)));
        guiScrollWindow.getTextField(11).setMaxStringLength(10);
        guiScrollWindow.getTextField(11).floatsOnly = true;
        guiScrollWindow.getTextField(11).setMinMaxDefaultFloat(-10000, 10000, 1);
        guiScrollWindow.getLabel(11).color = 0xd3d3d3;

        guiScrollWindow.addLabel(new GuiNpcLabel(12,"Ki Drain Ticks", 190, y + 5));
        guiScrollWindow.addTextField(new GuiNpcTextField(12, this, 300, y, 50, 20, String.valueOf(form.mastery.kiDrainTimer)));
        guiScrollWindow.getTextField(12).setMaxStringLength(10);
        guiScrollWindow.getTextField(12).floatsOnly = true;
        guiScrollWindow.getTextField(12).setMinMaxDefaultFloat(-10000, 10000, 1);
        guiScrollWindow.getLabel(12).color = 0xd3d3d3;

        y += 23;

        guiScrollWindow.addLabel(new GuiNpcLabel(5,"Damage Negation", 4, y + 5));
        guiScrollWindow.addTextField(new GuiNpcTextField(5, this, 135, y, 50, 20, String.valueOf(form.mastery.damageNegation)));
        guiScrollWindow.getTextField(5).setMaxStringLength(10);
        guiScrollWindow.getTextField(5).floatsOnly = true;
        guiScrollWindow.getTextField(5).setMinMaxDefaultFloat(-10000, 10000, 1);
        guiScrollWindow.getLabel(5).color = 0xd3d3d3;

        guiScrollWindow.addLabel(new GuiNpcLabel(6,"Dodge Chance", 190, y + 5));
        guiScrollWindow.addTextField(new GuiNpcTextField(6, this, 300, y, 50, 20, String.valueOf(form.mastery.dodgeChance)));
        guiScrollWindow.getTextField(6).setMaxStringLength(10);
        guiScrollWindow.getTextField(6).floatsOnly = true;
        guiScrollWindow.getTextField(6).setMinMaxDefaultFloat(-10000, 10000, 1);
        guiScrollWindow.getLabel(6).color = 0xd3d3d3;

        y += 23;
        guiScrollWindow.addLabel(new GuiNpcLabel(7,"Pain Time", 4, y + 5));
        guiScrollWindow.addTextField(new GuiNpcTextField(7, this, 135, y, 50, 20, String.valueOf(form.mastery.painTime)));
        guiScrollWindow.getTextField(7).setMaxStringLength(10);
        guiScrollWindow.getTextField(7).floatsOnly = true;
        guiScrollWindow.getTextField(7).setMinMaxDefaultFloat(-10000, 10000, 1);
        guiScrollWindow.getLabel(7).color = 0xd3d3d3;

        guiScrollWindow.addLabel(new GuiNpcLabel(3,"Health Requirement", 190, y + 5));
        guiScrollWindow.addTextField(new GuiNpcTextField(3, this, 300, y, 50, 20, String.valueOf(form.mastery.healthRequirement)));
        guiScrollWindow.getTextField(3).setMaxStringLength(10);
        guiScrollWindow.getTextField(3).floatsOnly = true;
        guiScrollWindow.getTextField(3).setMinMaxDefaultFloat(-10000, 10000, 1);
        guiScrollWindow.getLabel(3).color = 0xd3d3d3;

        y += 23;

        y += 23;

        guiScrollWindow.addButton(new GuiNpcButton(1010101, 200, 192, 50, 20, "RELOAD"));
	}

	public void buttonEvent(GuiButton guibutton)
    {
		GuiNpcButton button = (GuiNpcButton) guibutton;

        if(button.id == 1010101){
            initGui();
        }
	}


	@Override
	public void unFocused(GuiNpcTextField guiNpcTextField) {

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
