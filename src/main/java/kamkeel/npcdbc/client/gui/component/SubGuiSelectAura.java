package kamkeel.npcdbc.client.gui.component;

import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.player.aura.DBCRequestAura;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class SubGuiSelectAura extends SubGuiInterface implements IScrollData, ICustomScrollListener, ITextfieldListener {

    private HashMap<String, Integer> data = new HashMap<>();
    private GuiCustomScroll scrollAuras;
    private String selected = null;
    private String search = "";

    public boolean confirmed = false;
    public int selectedAuraID = -1;

    public SubGuiSelectAura(){
        this.closeOnEsc = true;
        this.drawDefaultBackground = true;
        xSize = 256;
        this.setBackground("menubg.png");

        PacketHandler.Instance.sendToServer(new DBCRequestAura(-1, false));
    }

    @Override
    public void initGui(){
        super.initGui();

        if (scrollAuras == null) {
            scrollAuras = new GuiCustomScroll(this, 0, 0);
            scrollAuras.setSize(143, 185);
        }
        scrollAuras.guiLeft = guiLeft + 8;
        scrollAuras.guiTop = guiTop + 4;
        addScroll(scrollAuras);
        addTextField(new GuiNpcTextField(55, this, fontRendererObj, guiLeft + 8, guiTop + 192, 143, 20, search));

        addButton(new GuiNpcButton(0, guiLeft + 159, guiTop + 4, 89, 20, "gui.add"));
        addButton(new GuiNpcButton(1, guiLeft + 159, guiTop + 26, 89, 20, "gui.cancel"));
    }

    @Override
    public void actionPerformed(GuiButton button){
        int id = button.id;

        if(id == 0 && selected != null){
            confirmed = true;
            selectedAuraID = data.get(selected);
            this.close();
        }
        if(id == 1){
            this.close();
        }
    }

    @Override
    public void setData(Vector<String> list, HashMap<String, Integer> data) {
        String name = scrollAuras.getSelected();
        this.data = data;
        scrollAuras.setList(getSearchList());
        if (name != null)
            scrollAuras.setSelected(name);
    }

    @Override
    public void setSelected(String selected) {
        this.selected = selected;
        scrollAuras.setSelected(selected);
    }

    @Override
    public void customScrollClicked(int i, int i1, int i2, GuiCustomScroll guiCustomScroll) {
        selected = scrollAuras.getSelected();
    }

    @Override
    public void unFocused(GuiNpcTextField guiNpcTextField) {}

    @Override
    public void keyTyped(char c, int i) {
        super.keyTyped(c, i);
        if (getTextField(55) != null) {
            if (getTextField(55).isFocused()) {
                if (search.equals(getTextField(55).getText()))
                    return;
                search = getTextField(55).getText().toLowerCase();
                scrollAuras.resetScroll();
                scrollAuras.setList(getSearchList());
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
