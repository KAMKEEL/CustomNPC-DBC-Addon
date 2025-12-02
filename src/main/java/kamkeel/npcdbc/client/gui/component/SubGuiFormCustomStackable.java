package kamkeel.npcdbc.client.gui.component;

import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormCustomStackable;
import kamkeel.npcdbc.data.form.FormStack;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.client.gui.util.ISubGuiListener;
import noppes.npcs.client.gui.util.SubGuiInterface;

public class SubGuiFormCustomStackable extends SubGuiInterface implements ISubGuiListener {

    public Form form;
    public FormCustomStackable stackable;
    public FormStack stack1;
    public FormStack stack2;
    public FormStack stack3;
    public FormStack stack4;

    public SubGuiFormCustomStackable(Form form) {
        this.form = form;
        this.stackable = form.customStackable;
        this.stack1 = stackable.getFormStack(0) != null ? stackable.getFormStack(0) : new FormStack(this.form);
        this.stack2 = stackable.getFormStack(1) != null ? stackable.getFormStack(1) : new FormStack(this.form);
        this.stack3 = stackable.getFormStack(2) != null ? stackable.getFormStack(2) : new FormStack(this.form);
        this.stack4 = stackable.getFormStack(3) != null ? stackable.getFormStack(3) : new FormStack(this.form);

        setBackground("menubg.png");
        xSize = 300;
    }

    @Override
    public void initGui() {
        super.initGui();

        int y = guiTop + 5;

        addLabel(new GuiNpcLabel(1, "Stack 1: ", guiLeft + 8, y + 5));

        addButton(new GuiNpcButton(3, guiLeft + 145, y, 20, 20, "X"));
        addButton(new GuiNpcButton(30, guiLeft + 265, y, 20, 20, "X"));
        addButton(new GuiNpcButton(2, guiLeft + 50, y, 90, 20, "general.noForm"));
        addButton(new GuiNpcButton(20, guiLeft + 170, y, 90, 20, "general.noForm"));

        addButtonFromFormText(stack1, 2);
        addButtonToFormText(stack1, 20);

        y += 23;
        addLabel(new GuiNpcLabel(11, "Stack 2:", guiLeft + 8, y + 5));

        addButton(new GuiNpcButton(31, guiLeft + 145, y, 20, 20, "X"));
        addButton(new GuiNpcButton(32, guiLeft + 265, y, 20, 20, "X"));
        addButton(new GuiNpcButton(21, guiLeft + 50, y, 90, 20, "general.noForm"));
        addButton(new GuiNpcButton(22, guiLeft + 170, y, 90, 20, "general.noForm"));

        addButtonFromFormText(stack2, 21);
        addButtonToFormText(stack2, 22);

        y += 23;
        addLabel(new GuiNpcLabel(13, "Stack 3:", guiLeft + 8, y + 5));

        addButton(new GuiNpcButton(33, guiLeft + 145, y, 20, 20, "X"));
        addButton(new GuiNpcButton(34, guiLeft + 265, y, 20, 20, "X"));
        addButton(new GuiNpcButton(23, guiLeft + 50, y, 90, 20, "general.noForm"));
        addButton(new GuiNpcButton(24, guiLeft + 170, y, 90, 20, "general.noForm"));

        addButtonFromFormText(stack3, 23);
        addButtonToFormText(stack3, 24);

        y += 23;
        addLabel(new GuiNpcLabel(14, "Stack 4:", guiLeft + 8, y + 5));

        addButton(new GuiNpcButton(35, guiLeft + 145, y, 20, 20, "X"));
        addButton(new GuiNpcButton(36, guiLeft + 265, y, 20, 20, "X"));
        addButton(new GuiNpcButton(25, guiLeft + 50, y, 90, 20, "general.noForm"));
        addButton(new GuiNpcButton(26, guiLeft + 170, y, 90, 20, "general.noForm"));

        addButtonFromFormText(stack4, 25);
        addButtonToFormText(stack4, 26);

        y += 115;

        addButton(new GuiNpcButton(10, guiLeft + 240, y, 50, 20, "gui.close"));
    }

    private void addButtonFromFormText(FormStack stack, int buttonID) {
        if (stack.fromForm.id != -1)
            if (FormController.getInstance().has(stack.fromForm.id))
                getButton(buttonID).setDisplayText(FormController.getInstance().get(stack.fromForm.id).getName());
    }

    private void addButtonToFormText(FormStack stack, int buttonID) {
        if (stack.toForm.id != -1)
            if (FormController.getInstance().has(stack.toForm.id))
                getButton(buttonID).setDisplayText(FormController.getInstance().get(stack.toForm.id).getName());
    }

    @Override
    public void actionPerformed(GuiButton guiButton) {
        GuiNpcButton button = (GuiNpcButton) guiButton;
        if (button.id == 2 || (button.id > 19 && button.id < 27)) {
            this.setSubGui(new SubGuiSelectForm(button.id, false, false));
        }

        if (button.id == 10) {
            stackable.setFormStack(0, stack1);
            stackable.setFormStack(1, stack2);
            stackable.setFormStack(2, stack3);
            stackable.setFormStack(3, stack4);
            close();
        }

        if (button.id == 3) {
            stack1.clearFromForm();
        }
        if (button.id == 30) {
            stack1.clearToForm();
        }
        if (button.id == 31) {
            stack2.clearFromForm();
        }
        if (button.id == 32) {
            stack2.clearToForm();
        }
        if (button.id == 33) {
            stack3.clearFromForm();
        }
        if (button.id == 34) {
            stack3.clearToForm();
        }
        if (button.id == 35) {
            stack4.clearFromForm();
        }
        if (button.id == 36) {
            stack4.clearToForm();
        }

        if (button.id == 3 || (button.id > 29 && button.id < 37)) {
            initGui();
        }
    }

    @Override
    public void subGuiClosed(SubGuiInterface subgui) {
        if (subgui instanceof SubGuiSelectForm) {
            if (form != null) {
                SubGuiSelectForm guiSelectForm = ((SubGuiSelectForm) subgui);
                if (guiSelectForm.confirmed) {
                    if (guiSelectForm.selectedFormID == form.id)
                        return;

                    int buttonID = guiSelectForm.buttonID;

                    if (buttonID == 2) {
                        selectFromForm(stack1, buttonID, guiSelectForm.selectedFormID);
                    } else if (buttonID == 20) {
                        selectToForm(stack1, buttonID, guiSelectForm.selectedFormID);
                    } else if (buttonID == 21) {
                        selectFromForm(stack2, buttonID, guiSelectForm.selectedFormID);
                    } else if (buttonID == 22) {
                        selectToForm(stack2, buttonID, guiSelectForm.selectedFormID);
                    } else if (buttonID == 23) {
                        selectFromForm(stack3, buttonID, guiSelectForm.selectedFormID);
                    } else if (buttonID == 24) {
                        selectToForm(stack3, buttonID, guiSelectForm.selectedFormID);
                    } else if (buttonID == 25) {
                        selectFromForm(stack4, buttonID, guiSelectForm.selectedFormID);
                    } else if (buttonID == 26) {
                        selectToForm(stack4, buttonID, guiSelectForm.selectedFormID);
                    }
                }
            }
            initGui();
        }
    }

    private void selectFromForm(FormStack stack, int buttonID, int selectedID) {
        if (buttonID != 2 && buttonID != 21 && buttonID != 23 && buttonID != 25) {
            return;
        }

        stack.setFromForm(selectedID);
    }

    private void selectToForm(FormStack stack, int buttonID, int selectedID) {
        if (buttonID != 20 && buttonID != 22 && buttonID != 24 && buttonID != 26) {
            return;
        }

        stack.setToForm(selectedID);
    }
}
