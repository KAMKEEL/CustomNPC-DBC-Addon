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

        y += 23;

        addLabel(new GuiNpcLabel(1,"Max Mastery Level", guiLeft + 4, y + 5));
        addTextField(new GuiNpcTextField(1, this, guiLeft + 135, y, 50, 20, String.valueOf(form.mastery.maxLevel)));
        getTextField(1).setMaxStringLength(10);
        getTextField(1).floatsOnly = true;
        getTextField(1).setMinMaxDefaultFloat(-10000, 10000, 1);


        addLabel(new GuiNpcLabel(8,"Multi Flat", guiLeft + 190, y + 5));
        addTextField(new GuiNpcTextField(8, this, guiLeft + 300, y, 50, 20, String.valueOf(form.mastery.attributeMultiFlat)));
        getTextField(8).setMaxStringLength(10);
        getTextField(8).floatsOnly = true;
        getTextField(8).setMinMaxDefaultFloat(-10000, 10000, 1);

        y += 23;

        addLabel(new GuiNpcLabel(10,"Multi Min/Max", guiLeft + 4, y + 5));
        addTextField(new GuiNpcTextField(10, this, guiLeft + 135, y, 50, 20, String.valueOf(form.mastery.attributeMultiMinOrMax)));
        getTextField(10).setMaxStringLength(10);
        getTextField(10).floatsOnly = true;
        getTextField(10).setMinMaxDefaultFloat(-10000, 10000, 1);

        addLabel(new GuiNpcLabel(9,"Multi Per Level", guiLeft + 190, y + 5));
        addTextField(new GuiNpcTextField(9, this, guiLeft + 300, y, 50, 20, String.valueOf(form.mastery.attributeMultiPerLevel)));
        getTextField(9).setMaxStringLength(10);
        getTextField(9).floatsOnly = true;
        getTextField(9).setMinMaxDefaultFloat(-10000, 10000, 1);

        y += 23;
        y += 23;

        addLabel(new GuiNpcLabel(2,"Instant Transform Level", guiLeft + 4, y + 5));
        addTextField(new GuiNpcTextField(2, this, guiLeft + 135, y, 50, 20, String.valueOf(form.mastery.instantTransformationUnlockLevel)));
        getTextField(2).setMaxStringLength(10);
        getTextField(2).floatsOnly = true;
        getTextField(2).setMinMaxDefaultFloat(-10000, 10000, 1);

        addLabel(new GuiNpcLabel(4,"Max Heat", guiLeft + 190, y + 5));
        addTextField(new GuiNpcTextField(4, this, guiLeft + 300, y, 50, 20, String.valueOf(form.mastery.maxHeat)));
        getTextField(4).setMaxStringLength(10);
        getTextField(4).floatsOnly = true;
        getTextField(4).setMinMaxDefaultFloat(-10000, 10000, 1);

        y += 23;

        addLabel(new GuiNpcLabel(11,"Ki Drain", guiLeft + 4, y + 5));
        addTextField(new GuiNpcTextField(11, this, guiLeft + 135, y, 50, 20, String.valueOf(form.mastery.kiDrain)));
        getTextField(11).setMaxStringLength(10);
        getTextField(11).floatsOnly = true;
        getTextField(11).setMinMaxDefaultFloat(-10000, 10000, 1);

        addLabel(new GuiNpcLabel(12,"Ki Drain Ticks", guiLeft + 190, y + 5));
        addTextField(new GuiNpcTextField(12, this, guiLeft + 300, y, 50, 20, String.valueOf(form.mastery.kiDrainTimer)));
        getTextField(12).setMaxStringLength(10);
        getTextField(12).floatsOnly = true;
        getTextField(12).setMinMaxDefaultFloat(-10000, 10000, 1);

        y += 23;

        addLabel(new GuiNpcLabel(5,"Damage Negation", guiLeft + 4, y + 5));
        addTextField(new GuiNpcTextField(5, this, guiLeft + 135, y, 50, 20, String.valueOf(form.mastery.damageNegation)));
        getTextField(5).setMaxStringLength(10);
        getTextField(5).floatsOnly = true;
        getTextField(5).setMinMaxDefaultFloat(-10000, 10000, 1);

        addLabel(new GuiNpcLabel(6,"Dodge Chance", guiLeft + 190, y + 5));
        addTextField(new GuiNpcTextField(6, this, guiLeft + 300, y, 50, 20, String.valueOf(form.mastery.dodgeChance)));
        getTextField(6).setMaxStringLength(10);
        getTextField(6).floatsOnly = true;
        getTextField(6).setMinMaxDefaultFloat(-10000, 10000, 1);

        y += 23;
        addLabel(new GuiNpcLabel(7,"Pain Time", guiLeft + 4, y + 5));
        addTextField(new GuiNpcTextField(7, this, guiLeft + 135, y, 50, 20, String.valueOf(form.mastery.painTime)));
        getTextField(7).setMaxStringLength(10);
        getTextField(7).floatsOnly = true;
        getTextField(7).setMinMaxDefaultFloat(-10000, 10000, 1);

        addLabel(new GuiNpcLabel(3,"Health Requirement", guiLeft + 190, y + 5));
        addTextField(new GuiNpcTextField(3, this, guiLeft + 300, y, 50, 20, String.valueOf(form.mastery.healthRequirement)));
        getTextField(3).setMaxStringLength(10);
        getTextField(3).floatsOnly = true;
        getTextField(3).setMinMaxDefaultFloat(-10000, 10000, 1);

        y += 23;

        y += 23;

        addButton(new GuiNpcButton(1010101, guiLeft + 200, guiTop + 192, 50, 20, "RELOAD"));
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
