package kamkeel.npcdbc.client.gui.global.form;

import kamkeel.npcdbc.client.gui.component.SubGuiSelectForm;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.form.Form;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.select.GuiSoundSelection;
import noppes.npcs.client.gui.util.*;

public class SubGuiNpcForms extends SubGuiInterface implements ISubGuiListener, GuiSelectionListener,ITextfieldListener
{

    private final GuiNPCManageForms parent;

	public Form form;
    boolean setAscendSound = true;
    public int parentForm = -1;
    public int childForm = -1;


	public SubGuiNpcForms(GuiNPCManageForms parent, Form form)
	{
		this.parent = parent;
		this.form = form;
        this.parentForm = form.parentID;
        this.childForm = form.childID;

		setBackground("menubg.png");
		xSize = 360;
		ySize = 216;
	}

    public void initGui()
    {
        super.initGui();
        guiTop += 7;
        int y = guiTop + 3;

        addTextField(new GuiNpcTextField(1, this, this.fontRendererObj, guiLeft + 36, y, 200, 20, form.name));
        addLabel(new GuiNpcLabel(1,"gui.name", guiLeft + 4, y + 5));
        addButton(new GuiNpcButton(3, guiLeft + 260, y, 95, 20, new String[]{"All Races", "Human", "Saiyan", "Half Saiyan", "Namekian", "Arcosian", "Majin"}, form.race + 1));

        addLabel(new GuiNpcLabel(0,"ID", guiLeft + 238, y + 1));
        addLabel(new GuiNpcLabel(2,	form.id + "", guiLeft + 238, y + 11));

        y += 23;

        addTextField(new GuiNpcTextField(4, this, guiLeft + 70, y, 166, 20, form.menuName.replaceAll("§", "&")));
        getTextField(4).setMaxStringLength(20);
        addLabel(new GuiNpcLabel(4, "Menu Name", guiLeft + 4, y+5));

        addLabel(new GuiNpcLabel(19,"Strength", guiLeft + 239, y + 5));
        addTextField(new GuiNpcTextField(19, this, guiLeft + 295, y, 60, 20, String.valueOf(form.strengthMulti)));
        getTextField(19).setMaxStringLength(15);
        getTextField(19).floatsOnly = true;
        getTextField(19).setMinMaxDefaultFloat(-10000, 10000, 1);

        y += 23;
        addButton(new GuiNpcButton(6, guiLeft + 4, y, 70, 20, "DISPLAY"));
        addButton(new GuiNpcButton(7, guiLeft + 84, y, 70, 20, "MASTERY"));
        addButton(new GuiNpcButton(8, guiLeft + 166, y, 70, 20, "STACKABLE"));

        addLabel(new GuiNpcLabel(20,"Dexterity", guiLeft + 239, y + 5));
        addTextField(new GuiNpcTextField(20, this, guiLeft + 295, y, 60, 20, String.valueOf(form.dexMulti)));
        getTextField(20).setMaxStringLength(15);
        getTextField(20).floatsOnly = true;
        getTextField(20).setMinMaxDefaultFloat(-10000, 10000, 1);

        y += 23;

        addLabel(new GuiNpcLabel(21,"Willpower", guiLeft + 239, y + 5));
        addTextField(new GuiNpcTextField(21, this, guiLeft + 295, y, 60, 20, String.valueOf(form.willMulti)));
        getTextField(21).setMaxStringLength(15);
        getTextField(21).floatsOnly = true;
        getTextField(21).setMinMaxDefaultFloat(-10000, 10000, 1);

        addButton(new GuiNpcButton(10, guiLeft + 192, y, 45, 20, new String[]{"gui.no", "gui.yes"}, form.fromParentOnly ? 1 : 0));
        addLabel(new GuiNpcLabel(10, "Transform from Only Parent Form", guiLeft+8, y + 5));

        y += 23;

        addButton(new GuiNpcButton(11, guiLeft + 74, y, 140, 20, "No Parent"));
        if(parentForm != -1){
            if(FormController.getInstance().has(parentForm))
                getButton(11).setDisplayText(FormController.getInstance().get(parentForm).getName());
        }

        addButton(new GuiNpcButton(12, guiLeft + 216, y, 20, 20, "X"));
        getButton(12).enabled = parentForm != -1;
        addLabel(new GuiNpcLabel(12, "Parent Form", guiLeft + 8, y + 5));

        y += 23;

        addButton(new GuiNpcButton(13, guiLeft + 74, y, 140, 20, "No Child"));
        if(childForm != -1){
            if(FormController.getInstance().has(childForm))
                getButton(13).setDisplayText(FormController.getInstance().get(childForm).getName());
        }
        addButton(new GuiNpcButton(14, guiLeft + 216, y, 20, 20, "X"));
        getButton(14).enabled = childForm != -1;
        addLabel(new GuiNpcLabel(14, "Child Form", guiLeft + 8, y + 5));

        y += 23;

        addLabel(new GuiNpcLabel(30, "Ascend Sound", guiLeft + 7, y + 5));
        addTextField(new GuiNpcTextField(30, this, fontRendererObj, guiLeft + 90, y, 194, 20, form.ascendSound));
        addButton(new GuiNpcButton(30, guiLeft + 293, y, 60, 20, "gui.select"));

        y += 23;

        addLabel(new GuiNpcLabel(31, "Descend Sound", guiLeft + 7, y + 5));
        addTextField(new GuiNpcTextField(31, this, fontRendererObj, guiLeft + 90, y, 194, 20, form.descendSound));
        addButton(new GuiNpcButton(31, guiLeft + 293, y, 60, 20, "gui.select"));

        addButton(new GuiNpcButton(1010101, guiLeft + 200, guiTop + 192, 50, 20, "RELOAD"));
	}

	public void buttonEvent(GuiButton guibutton)
    {
		GuiNpcButton button = (GuiNpcButton) guibutton;
        if(button.id == 3){
            form.race = button.getValue() - 1;
        }
        if(button.id == 11){
            this.setSubGui(new SubGuiSelectForm(false));
        }
        if(button.id == 12){
            parentForm = -1;
            initGui();
        }
        if(button.id == 13){
            this.setSubGui(new SubGuiSelectForm(true));
        }
        if(button.id == 14){
            childForm = -1;
            initGui();
        }
        if(button.id == 30){
            setAscendSound = true;
            setSubGui(new GuiSoundSelection((getTextField(30).getText())));
        }
        if(button.id == 31){
            setAscendSound = false;
            setSubGui(new GuiSoundSelection((getTextField(31).getText())));
        }
        if(button.id == 1010101){
            initGui();
        }
	}


	@Override
	public void unFocused(GuiNpcTextField guiNpcTextField) {
		if(guiNpcTextField.id == 1) {
			if(form.id < 0)
				guiNpcTextField.setText("");
			else{
				String name = guiNpcTextField.getText();
				if(name.isEmpty() || this.parent.data.containsKey(name)){
					guiNpcTextField.setText(form.name);
				}
				else if(form.id >= 0){
					String old = form.name;
					this.parent.data.remove(old);
					form.name = name;
					this.parent.data.put(form.name, form.id);
					this.parent.scrollForms.replace(old, form.name);
				}
			}
		}
		if(guiNpcTextField.id == 4){
            String menuName = guiNpcTextField.getText();
            if(!menuName.isEmpty()){
                form.menuName = menuName.replaceAll("&", "§");
            }
		}
        if (guiNpcTextField.id == 19) {
            form.strengthMulti = guiNpcTextField.getFloat();
        }
        if (guiNpcTextField.id == 20) {
            form.dexMulti = guiNpcTextField.getFloat();
        }
        if (guiNpcTextField.id == 21) {
            form.willMulti = guiNpcTextField.getFloat();
        }
        if (guiNpcTextField.id == 30) {
            form.ascendSound = guiNpcTextField.getText();
        }
        if (guiNpcTextField.id == 31) {
            form.descendSound = guiNpcTextField.getText();
        }
	}

	@Override
	public void subGuiClosed(SubGuiInterface subgui){
        if (subgui instanceof SubGuiSelectForm) {
            if(form != null){
                SubGuiSelectForm guiSelectForm = ((SubGuiSelectForm)subgui);
                if(guiSelectForm.confirmed){
                    if(guiSelectForm.selectedFormID == form.id)
                        return;
                    if(guiSelectForm.selectionChild){
                        childForm = guiSelectForm.selectedFormID;
                        if(parentForm == childForm)
                            parentForm = -1;
                    }
                    else {
                        parentForm = guiSelectForm.selectedFormID;
                        if(parentForm == childForm)
                            childForm = -1;
                    }
                }
            }
            initGui();
        }
        else if (subgui instanceof GuiSoundSelection){
            GuiSoundSelection gss = (GuiSoundSelection) subgui;
            if(gss.selectedResource != null) {
                if(setAscendSound){
                    getTextField(30).setText(gss.selectedResource.toString());
                    unFocused(getTextField(30));
                }
                else {
                    getTextField(31).setText(gss.selectedResource.toString());
                    unFocused(getTextField(31));
                }
                initGui();
            }
        }
	}

	@Override
	public void selected(int id, String name) {}

	public void save(){}
}
