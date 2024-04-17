package kamkeel.npcdbc.client.gui;

import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.CustomForm;
import kamkeel.npcdbc.data.PlayerCustomFormData;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.client.gui.util.GuiNpcTextField;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class FormSelectionScroll extends GuiCustomScroll {
    public GuiNpcLabel status;
    public String selectedTemp;
    public String search;
    int hovercolor;
    GuiNpcButton btnSelectForm;
    GuiNpcButton btnDeselectForm;
    GuiNpcTextField searchField;
    GuiDBC parent;

    PlayerCustomFormData formData;
    int formSelectionID = 100;

    public FormSelectionScroll(GuiDBC parent, int id) {
        super(parent, id);
        this.parent = parent;
        guiLeft = parent.guiLeft + 5;
        guiTop = parent.guiTop + 5;
        setSize(135, 150);
        visible = true;

        formData = Utility.getFormDataClient();
        if (formData.selectedForm > -1 && formData.getSelectedForm() != null)
            setSelected(formData.getColoredName(formData.getSelectedForm()));
        setList(formData.getAllForms());

        btnSelectForm = new GuiNpcButton(++formSelectionID, guiLeft, parent.guiTop + parent.ySize - 24, "Select Form");
        btnSelectForm.width = 66;

        btnDeselectForm = new GuiNpcButton(++formSelectionID, btnSelectForm.xPosition + btnSelectForm.width + 3, parent.guiTop + parent.ySize - 24, "Clear");
        btnDeselectForm.width = 66;

        searchField = new GuiNpcTextField(++formSelectionID, this, this.fontRendererObj, parent.guiLeft + 5, parent.guiTop + parent.ySize - 10, btnSelectForm.height, 65, "kjk");

        status = new GuiNpcLabel(++formSelectionID, "I want to center this", guiLeft + 67, btnSelectForm.yPosition + btnSelectForm.height + 3);
        status.center(1);


        parent.addScroll(this);
        parent.addButton(btnSelectForm);
        parent.addButton(btnDeselectForm);
        parent.addLabel(status);
        parent.addTextField(searchField);
    }


    public void mouseClicked(int k) {
        if (searchField.isFocused() && k == 1) { //empty search field on right click
            searchField.setText("");
            search = "";
            setList(search());
            setSelected(selectedTemp);
        }
    }

    public void customScrollClicked(int i, int j, int k) {

        if (selectedTemp != null) {
            if (selectedTemp.equals(getSelected())) {
                selectedTemp = "";
                selected = -1;
                return;
            }
        }

        this.selectedTemp = getSelected();
        status.label = "";


    }

    public void actionPerformed(GuiNpcButton button) {
        if (isSelectedFormValid()) {
            if (button.id == 1) {
                if (getSelectedForm() != formData.getSelectedForm()) {
                    formData.selectedForm = getSelectedForm().id;
                    status.label = "§aSelected " + formData.getColoredName(getSelectedForm()) + "§a!";
                    //add packet here
                } else {
                    formData.selectedForm = -1;
                    status.label = "§cDeselected " + formData.getColoredName(getSelectedForm()) + "§c!";
                    //add packet he/**/re
                }

            } else if (button.id == 2) {
                formData.selectedForm = -1;
                //packet here
                status.label = "§cDeselected " + formData.getColoredName(getSelectedForm()) + "§c!";
                selectedTemp = "";
                selected = -1;
            }
        }

    }

    public void keyTyped(char c, int i) {
        if (searchField.isFocused() && i == 1) //empties search field on escape
            searchField.setText("");
        if (searchField.isFocused()) {
            search = searchField.getText().toLowerCase();
            setList(search());
            setSelected(selectedTemp);

        }
    }

    private List<String> search() {
        if (search.isEmpty())
            return formData.getAllForms();
        List<String> list = new ArrayList<>();
        for (String str : formData.getAllForms()) {
            if (str.toLowerCase().contains(search)) {
                if (getSelected() != null && str.toLowerCase().equals(getSelected().toLowerCase()))
                    this.selectedTemp = getSelected();
                list.add(str);
            }
        }
        return list;

    }

    public boolean isSelectedFormValid() {
        return getSelectedForm() != null;
    }

    //the form selected in the menu
    public CustomForm getSelectedForm() {
        for (CustomForm f : FormController.Instance.customForms.values()) {
            String name = formData.getColoredName(f);
            if (name.equals(getSelected()))
                return f;
        }
        return null;
    }

    protected void drawItems() {
        for (int i = 0; i < list.size(); i++) {
            int j = 4;
            int k = (14 * i + 4) - scrollY;
            if (k >= 4 && k + 12 < ySize) {
                int xOffset = scrollHeight < ySize - 8 ? 0 : 10;
                String displayString = StatCollector.translateToLocal(list.get(i));
                String text = "";
                float maxWidth = (xSize + xOffset - 8) * 0.8f;

                if (fontRendererObj.getStringWidth(displayString) > maxWidth) {
                    for (int h = 0; h < displayString.length(); h++) {
                        char c = displayString.charAt(h);
                        text += c;
                        if (fontRendererObj.getStringWidth(text) > maxWidth)
                            break;
                    }
                    if (displayString.length() > text.length())
                        text += "...";
                } else
                    text = displayString;
                if ((multipleSelection && selectedList.contains(text)) || (!multipleSelection && selected == i)) { //if selected
                    drawVerticalLine(j - 2, k - 4, k + 10, 0xffffffff);
                    drawVerticalLine(j + xSize - 18 + xOffset, k - 4, k + 10, 0xffffffff);
                    drawHorizontalLine(j - 2, j + xSize - 18 + xOffset, k - 3, 0xffffffff);
                    drawHorizontalLine(j - 2, j + xSize - 18 + xOffset, k + 10, 0xffffffff);
                    fontRendererObj.drawString(text, j, k, 16777215);
                    setSelected(text);
                } else if (i == hover) { //if hovering over
                    GL11.glPushMatrix();
                    fontRendererObj.drawString(text, j, k, hovercolor);
                    GL11.glPopMatrix();
                } else { //everything else
                    GL11.glPushMatrix();
                    fontRendererObj.drawString(text, j, k, 16777215);
                    fontRendererObj.drawStringWithShadow(text, j, k, 16777215);
                    GL11.glPopMatrix();
                }

            }
        }

    }
}
