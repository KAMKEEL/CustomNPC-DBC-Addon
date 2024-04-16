package kamkeel.npcdbc.client.gui;

import kamkeel.npcdbc.data.PlayerCustomFormData;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.util.EnumChatFormatting;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.client.gui.util.GuiNpcTextField;

import java.util.ArrayList;
import java.util.List;

public class FormSelectionScroll extends GuiCustomScroll {
    public GuiNpcLabel status;
    public String selected;
    public String desc;
    public boolean stop = false;
    public boolean btnChangeClicked = false;
    public String search;
    GuiNpcButton btnSelectForm;
    GuiNpcTextField searchField;
    GuiDBC parent;

    PlayerCustomFormData formData;

    public FormSelectionScroll(GuiDBC parent, int id) {
        super(parent, id);
        this.parent = parent;
        guiLeft = Utility.guiX(parent, 0.16);
        guiTop = Utility.guiY(parent, 0.115);
        setSize(Utility.guiWidth(parent, 0.3d), Utility.guiHeight(parent, 0.9));
        setList(Utility.getFormDataClient().getAllForms());
        visible = true;

        btnSelectForm = new GuiNpcButton(1, guiLeft + (int) (xSize * 0.625), guiTop + ySize + 2, (int) (xSize * 0.375), 20, "Select Form");
        searchField = new GuiNpcTextField(350, this, this.fontRendererObj, guiLeft + 1, guiTop + ySize + 3, (int) (xSize * 0.6), 18, "");
        status = new GuiNpcLabel(350, "", guiLeft + (xSize / 2), guiTop + ySize + searchField.height + 9);

        System.out.println("check constructor");
        formData = Utility.getFormDataClient();

        parent.addScroll(this);
        parent.addButton(btnSelectForm);
        parent.addLabel(status);
        parent.addTextField(searchField);
    }


    public void mouseClicked(int k) {
        if (searchField.isFocused() && k == 1) { //empty search field on right click
            searchField.setText("");
            search = "";
            setList(search());
            setSelected(selected);
        }
    }

    public void customScrollClicked(int i, int j, int k) {

        if (selected != null && selected.equals(getSelected()))
            return;
        this.selected = getSelected();
        System.out.println(selected);
        btnSelectForm.setEnabled(true);
        btnSelectForm.displayString = "Select Form";
        status.enabled = false;
        btnChangeClicked = false;
    }

    public void actionPerformed(GuiNpcButton button) {
        if (button.id == 1) {
            String name = "";//KH.getKeyNames(selectedKey);
            if (!btnChangeClicked) {
                button.displayString = "§f> " + "§e" + name + " §f<";
                btnChangeClicked = true;
                // button.fakehover = true;
                status.enabled = false;
            } else {
                button.displayString = EnumChatFormatting.WHITE + name;
                btnChangeClicked = false;
                //button.fakehover = false;
            }
        }

    }

    public void keyTyped(char c, int i) {
        if (searchField.isFocused() && i == 1) //escape empties search field
            searchField.setText("");
        if (searchField.isFocused()) {
            search = searchField.getText().toLowerCase();
            setList(search());
            setSelected(selected);

        }
    }

    private List<String> search() {
        if (search.isEmpty())
            return formData.getAllForms();
        List<String> list = new ArrayList<>();
        for (String str : formData.getAllForms()) {
            if (str.toLowerCase().contains(search)) {
                if (getSelected() != null && str.toLowerCase().equals(getSelected().toLowerCase()))
                    this.selected = getSelected();
                list.add(str);
            }
        }
        return list;

    }
}
