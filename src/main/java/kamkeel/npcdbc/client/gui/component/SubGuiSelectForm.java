package kamkeel.npcdbc.client.gui.component;

import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.form.DBCRequestForm;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class SubGuiSelectForm extends SubGuiInterface implements IScrollData, ICustomScrollListener, ITextfieldListener {

    private HashMap<String, Integer> data = new HashMap<>();
    private GuiCustomScroll scrollForms, scrollDBCForms;
    private String selected = null;
    private String search = "";

    public int page;
    public boolean confirmed = false;
    public boolean selectionChild;
    public int selectedFormID = -1;
    public int buttonID = -1;
    public boolean isDBC;

    public boolean useMenuName, showDBCForms;

    public HashMap<Integer, String> dbcForms = new HashMap<>();
    private ArrayList<Integer> stateIDs = new ArrayList<>();

    public DBCData dbcData;


    public SubGuiSelectForm(int buttonID, boolean playerFormsOnly, boolean useMenuName) {
        this.selectionChild = selectionChild;
        this.buttonID = buttonID;
        this.closeOnEsc = true;
        this.drawDefaultBackground = true;
        xSize = 256;
        this.setBackground("menubg.png");

        this.useMenuName = useMenuName;
        PacketHandler.Instance.sendToServer(new DBCRequestForm(-1, playerFormsOnly, this.useMenuName).generatePacket());
    }

    @Override
    public void initGui() {
        super.initGui();
        guiTop += 10;
        if (showDBCForms)
            addButton(new GuiNpcButton(3, guiLeft + 159, guiTop + 10, 89, 20, new String[]{"DBC", "Custom"}, page));

        if (scrollForms == null) {
            scrollForms = new GuiCustomScroll(this, 0, 0);
            scrollForms.setSize(143, 185);
        }

        scrollForms.guiLeft = guiLeft + 8;
        scrollForms.guiTop = guiTop + 4;
        addScroll(scrollForms);
        if (page == 0) {
            scrollForms.setList(getSearchList());
        } else if (dbcData != null) {
            dbcForms = dbcData.getUnlockedDBCFormsMap();
            stateIDs = new ArrayList<>(dbcForms.keySet());
            scrollForms.setUnsortedList(getSearchList());
        }

        addTextField(new GuiNpcTextField(55, this, fontRendererObj, guiLeft + 8, guiTop + 192, 143, 20, search));

        addButton(new GuiNpcButton(0, guiLeft + 159, guiTop + 4 + 166, 89, 20, "gui.add"));
        addButton(new GuiNpcButton(1, guiLeft + 159, guiTop + 26 + 166, 89, 20, "gui.cancel"));
    }

    public SubGuiSelectForm displayDBCForms(DBCData data) {
        if (data == null) {
            showDBCForms = false;
            page = 0;
        } else {
            showDBCForms = true;
            this.dbcData = data;
        }
        return this;
    }

    @Override
    public void actionPerformed(GuiButton button) {
        GuiNpcButton bttn = (GuiNpcButton) button;
        int id = button.id;

        if (id == 0 && selected != null) {
            confirmed = true;
            if (page == 0)
                selectedFormID = data.get(selected);
            else
                selectedFormID = stateIDs.get(scrollForms.selected).byteValue();
            isDBC = page == 1;
            this.close();
        }
        if (id == 1) {
            this.close();
        }
        if (id == 3) {
            page = bttn.getValue();
            initGui();
        }
    }

    @Override
    public void setData(Vector<String> list, HashMap<String, Integer> data) {
        String name = scrollForms.getSelected();
        this.data = data;
        scrollForms.setList(getSearchList());
        if (name != null)
            scrollForms.setSelected(name);
    }

    @Override
    public void setSelected(String selected) {
        this.selected = selected;
        scrollForms.setSelected(selected);
    }

    @Override
    public void customScrollClicked(int i, int i1, int i2, GuiCustomScroll guiCustomScroll) {
        if (guiCustomScroll == scrollForms)
            selected = scrollForms.getSelected();
        else
            selected = scrollDBCForms.getSelected();
    }

    @Override
    public void unFocused(GuiNpcTextField guiNpcTextField) {
    }

    @Override
    public void keyTyped(char c, int i) {
        super.keyTyped(c, i);
        if (getTextField(55) != null) {
            if (getTextField(55).isFocused()) {
                if (search.equals(getTextField(55).getText()))
                    return;
                search = getTextField(55).getText().toLowerCase();
                scrollForms.resetScroll();
                if (page == 0)
                    scrollForms.setList(getSearchList());
                else
                    scrollForms.setUnsortedList(getSearchList());
            }
        }
    }

    private List<String> getSearchList() {
        if (search.isEmpty()) {
            return new ArrayList<String>(page == 0 ? this.data.keySet() : dbcForms.values());
        }
        List<String> list = new ArrayList<String>();
        for (String name : page == 0 ? this.data.keySet() : dbcForms.values()) {
            if (name.toLowerCase().contains(search))
                list.add(name);
        }
        return list;
    }

}
