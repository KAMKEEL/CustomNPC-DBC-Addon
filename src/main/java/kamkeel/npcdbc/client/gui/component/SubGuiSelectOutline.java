package kamkeel.npcdbc.client.gui.component;

import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.player.outline.DBCRequestOutline;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class SubGuiSelectOutline extends SubGuiInterface implements IScrollData, ICustomScrollListener, ITextfieldListener {

    private HashMap<String, Integer> data = new HashMap<>();
    private GuiCustomScroll scrollOutlines;
    private String selected = null;
    private String search = "";

    public boolean confirmed = false;
    public int selectedOutlineID = -1;

    public SubGuiSelectOutline() {
        this.closeOnEsc = true;
        this.drawDefaultBackground = true;
        xSize = 256;
        this.setBackground("menubg.png");

        PacketHandler.Instance.sendToServer(new DBCRequestOutline(-1).generatePacket());
    }

    @Override
    public void initGui() {
        super.initGui();

        if (scrollOutlines == null) {
            scrollOutlines = new GuiCustomScroll(this, 0, 0);
            scrollOutlines.setSize(143, 185);
        }
        scrollOutlines.guiLeft = guiLeft + 8;
        scrollOutlines.guiTop = guiTop + 4;
        addScroll(scrollOutlines);
        addTextField(new GuiNpcTextField(55, this, fontRendererObj, guiLeft + 8, guiTop + 192, 143, 20, search));

        addButton(new GuiNpcButton(0, guiLeft + 159, guiTop + 4, 89, 20, "gui.add"));
        addButton(new GuiNpcButton(1, guiLeft + 159, guiTop + 26, 89, 20, "gui.cancel"));
    }

    @Override
    public void actionPerformed(GuiButton button) {
        int id = button.id;

        if (id == 0 && selected != null) {
            confirmed = true;
            selectedOutlineID = data.get(selected);
            this.close();
        }
        if (id == 1) {
            this.close();
        }
    }

    @Override
    public void setData(Vector<String> list, HashMap<String, Integer> data) {
        String name = scrollOutlines.getSelected();
        this.data = data;
        scrollOutlines.setList(getSearchList());
        if (name != null)
            scrollOutlines.setSelected(name);
    }

    @Override
    public void setSelected(String selected) {
        this.selected = selected;
        scrollOutlines.setSelected(selected);
    }

    @Override
    public void customScrollClicked(int i, int i1, int i2, GuiCustomScroll guiCustomScroll) {
        selected = scrollOutlines.getSelected();
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
                scrollOutlines.resetScroll();
                scrollOutlines.setList(getSearchList());
            }
        }
    }

    private List<String> getSearchList() {
        if (search.isEmpty()) {
            return new ArrayList<String>(this.data.keySet());
        }
        List<String> list = new ArrayList<String>();
        for (String name : this.data.keySet()) {
            if (name.toLowerCase().contains(search))
                list.add(name);
        }
        return list;
    }
}
