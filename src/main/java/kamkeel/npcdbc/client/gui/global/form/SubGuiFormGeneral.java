package kamkeel.npcdbc.client.gui.global.form;

import kamkeel.npcdbc.client.gui.component.SubGuiSelectForm;
import kamkeel.npcdbc.client.gui.component.SubGuiSetParents;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.form.Form;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.select.GuiSoundSelection;
import noppes.npcs.client.gui.util.*;

public class SubGuiFormGeneral extends SubGuiInterface implements ISubGuiListener, GuiSelectionListener,ITextfieldListener
{
    private final GuiNPCManageForms parent;
    private GuiNpcFormMenu menu;
	public Form form;
    boolean setAscendSound = true;


	public SubGuiFormGeneral(GuiNPCManageForms parent, Form form)
	{
		this.form = form;;
        this.parent = parent;

		setBackground("menubg.png");
		xSize = 360;
		ySize = 216;

        menu = new GuiNpcFormMenu(parent, this, -1, form);
	}

    public void initGui()
    {
        super.initGui();
        guiTop += 7;
        menu.initGui(guiLeft, guiTop, xSize);

        int y = guiTop + 3;

        addTextField(new GuiNpcTextField(1, this, this.fontRendererObj, guiLeft + 36, y, 200, 20, form.name));
        addLabel(new GuiNpcLabel(1,"gui.name", guiLeft + 4, y + 5));
        addButton(new GuiNpcButton(3, guiLeft + 260, y, 95, 20, new String[]{"general.allRaces", "Human", "Saiyan", "Half Saiyan", "Namekian", "Arcosian", "Majin"}, form.race + 1));

        addLabel(new GuiNpcLabel(0,"ID", guiLeft + 238, y + 1));
        addLabel(new GuiNpcLabel(2,	form.id + "", guiLeft + 238, y + 11));

        y += 23;

        addTextField(new GuiNpcTextField(4, this, guiLeft + 70, y, 166, 20, form.menuName.replaceAll("§", "&")));
        getTextField(4).setMaxStringLength(20);
        addLabel(new GuiNpcLabel(4, "general.menuName", guiLeft + 4, y+5));

        addLabel(new GuiNpcLabel(19,"general.strength", guiLeft + 239, y + 5));
        addTextField(new GuiNpcTextField(19, this, guiLeft + 295, y, 60, 20, String.valueOf(form.strengthMulti)));
        getTextField(19).setMaxStringLength(15);
        getTextField(19).floatsOnly = true;
        getTextField(19).setMinMaxDefaultFloat(-10000, 10000, 1);

        y += 23;

        addLabel(new GuiNpcLabel(20,"general.dexterity", guiLeft + 239, y + 5));
        addTextField(new GuiNpcTextField(20, this, guiLeft + 295, y, 60, 20, String.valueOf(form.dexMulti)));
        getTextField(20).setMaxStringLength(15);
        getTextField(20).floatsOnly = true;
        getTextField(20).setMinMaxDefaultFloat(-10000, 10000, 1);

        addButton(new GuiNpcButton(10, guiLeft + 192, y, 45, 20, new String[]{"gui.no", "gui.yes"}, form.fromParentOnly ? 1 : 0));
        addLabel(new GuiNpcLabel(10, "general.transformOnly", guiLeft+8, y + 5));

        y += 23;

        addLabel(new GuiNpcLabel(21,"general.willpower", guiLeft + 239, y + 5));
        addTextField(new GuiNpcTextField(21, this, guiLeft + 295, y, 60, 20, String.valueOf(form.willMulti)));
        getTextField(21).setMaxStringLength(15);
        getTextField(21).floatsOnly = true;
        getTextField(21).setMinMaxDefaultFloat(-10000, 10000, 1);

        addButton(new GuiNpcButton(50, guiLeft + 142, y, 95, 20, "general.editParents"));
        addLabel(new GuiNpcLabel(50, "general.dbcFormParents", guiLeft+8, y + 5));

        y += 23;

        addButton(new GuiNpcButton(11, guiLeft + 74, y, 140, 20, "general.noParent"));
        if(form.parentID != -1){
            if(FormController.getInstance().has(form.parentID))
                getButton(11).setDisplayText(FormController.getInstance().get(form.parentID).getName());
        }

        addButton(new GuiNpcButton(12, guiLeft + 216, y, 20, 20, "X"));
        getButton(12).enabled = form.parentID != -1;
        addLabel(new GuiNpcLabel(12, "general.parentForm", guiLeft + 8, y + 5));

        y += 23;

        addButton(new GuiNpcButton(13, guiLeft + 74, y, 140, 20, "general.noChild"));
        if(form.childID != -1){
            if(FormController.getInstance().has(form.childID))
                getButton(13).setDisplayText(FormController.getInstance().get(form.childID).getName());
        }
        addButton(new GuiNpcButton(14, guiLeft + 216, y, 20, 20, "X"));
        getButton(14).enabled = form.childID != -1;
        addLabel(new GuiNpcLabel(14, "general.childForm", guiLeft + 8, y + 5));

        y += 23;

        addLabel(new GuiNpcLabel(30, "general.ascendSound", guiLeft + 7, y + 5));
        addTextField(new GuiNpcTextField(30, this, fontRendererObj, guiLeft + 90, y, 194, 20, form.ascendSound));
        addButton(new GuiNpcButton(30, guiLeft + 293, y, 60, 20, "gui.select"));

        y += 23;

        addLabel(new GuiNpcLabel(31, "general.descendSound", guiLeft + 7, y + 5));
        addTextField(new GuiNpcTextField(31, this, fontRendererObj, guiLeft + 90, y, 194, 20, form.descendSound));
        addButton(new GuiNpcButton(31, guiLeft + 293, y, 60, 20, "gui.select"));
    }

	public void buttonEvent(GuiButton guibutton)
    {
		GuiNpcButton button = (GuiNpcButton) guibutton;
        if(button.id == 3){
            form.race = button.getValue() - 1;
        }
        if(button.id == 10){
            form.fromParentOnly = button.getValue() == 1;
        }
        if(button.id == 11){
            this.setSubGui(new SubGuiSelectForm(11));
        }
        if(button.id == 12){
            form.parentID = -1;
            initGui();
        }
        if(button.id == 13){
            this.setSubGui(new SubGuiSelectForm(13));
        }
        if(button.id == 14){
            form.childID = -1;
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
        if(button.id == 50){
            setSubGui(new SubGuiSetParents(form));
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
    public void mouseClicked(int i, int j, int k)
    {
        super.mouseClicked(i, j, k);
        if(!hasSubGui())
            menu.mouseClicked(i, j, k);
    }

	@Override
	public void subGuiClosed(SubGuiInterface subgui){
        if (subgui instanceof SubGuiSelectForm) {
            if(form != null){
                SubGuiSelectForm guiSelectForm = ((SubGuiSelectForm)subgui);
                if(guiSelectForm.confirmed){
                    if(guiSelectForm.selectedFormID == form.id)
                        return;
                    if (guiSelectForm.buttonID == 13) {
                        form.childID = guiSelectForm.selectedFormID;
                        if(form.parentID == form.childID)
                            form.parentID = -1;
                    } else if (guiSelectForm.buttonID == 11) {
                        form.parentID = guiSelectForm.selectedFormID;
                        if(form.parentID == form.childID)
                            form.parentID = -1;
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

    public void drawScreen(int i, int j, float f) {
        super.drawScreen(i, j, f);
        if(!hasSubGui())
            menu.drawElements(fontRendererObj, i, j, mc, f);
    }

	@Override
	public void selected(int id, String name) {}

	public void save(){}
}
