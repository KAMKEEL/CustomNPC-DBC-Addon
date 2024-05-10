package kamkeel.npcdbc.client.gui.global.customforms;

import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.RequestForm;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.Client;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumPacketServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class SubGuiSelectForm extends SubGuiInterface implements IScrollData, ICustomScrollListener, ITextfieldListener {

    private HashMap<String, Integer> data = new HashMap<>();
    boolean selectionChild;
    private GuiCustomScroll scrollForms;
    private String selected = null;
    private String search = "";

    public SubGuiSelectForm(boolean selectionChild){
        this.selectionChild = selectionChild;
        this.closeOnEsc = true;
        this.drawDefaultBackground = true;
        xSize = 256;
        this.setBackground("menubg.png");

        PacketHandler.Instance.sendToServer(new RequestForm(-1, false).generatePacket());
    }

    @Override
    public void initGui(){
        super.initGui();

        if (scrollForms == null) {
            scrollForms = new GuiCustomScroll(this, 0, 0);
            scrollForms.setSize(143, 185);
        }
        scrollForms.guiLeft = guiLeft + 8;
        scrollForms.guiTop = guiTop + 4;
        addScroll(scrollForms);
        addTextField(new GuiNpcTextField(55, this, fontRendererObj, guiLeft + 8, guiTop + 192, 143, 20, search));

        addButton(new GuiNpcButton(0, guiLeft + 159, guiTop + 4, 89, 20, "gui.add"));
        addButton(new GuiNpcButton(1, guiLeft + 159, guiTop + 26, 89, 20, "gui.cancel"));
    }

    @Override
    public void actionPerformed(GuiButton button){
        int id = button.id;

        if(id == 0 && selected != null){
            GuiNPCManageCustomForms parent = (GuiNPCManageCustomForms) this.parent;
            if(selectionChild)
                parent.childForm = data.get(selected);
            else
                parent.parentForm = data.get(selected);
            this.close();
        }
        if(id == 1){
            this.close();
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
        if (guiCustomScroll.id == 0) {
            save();
            selected = scrollForms.getSelected();
            Client.sendData(EnumPacketServer.CustomFormGet, data.get(selected));
        }
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
                scrollForms.setList(getSearchList());
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
