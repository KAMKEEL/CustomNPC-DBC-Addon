package kamkeel.npcdbc.client.gui.component;

import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.player.ability.DBCRequestAbility;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumScrollData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class SubGuiSelectAbility extends SubGuiInterface implements IScrollData, ICustomScrollListener, ITextfieldListener {
    private HashMap<String, Integer> data = new HashMap<>();
    private GuiCustomScroll scrollAbilities, scrollDBCAbilities;
    private String selected = null;
    private String search = "";

    public int page;
    public boolean confirmed = false;
    public int selectedAbilityID = -1;
    public int buttonID = -1;
    public boolean isDBC;
    public boolean removeAbility = false;

    public boolean useMenuName, showDBCAbilities;

    public PlayerDBCInfo dbcInfo;

    public HashMap<Integer, String> dbcAbilities = new HashMap<>();
    private ArrayList<Integer> stateIDs = new ArrayList<>();

    public SubGuiSelectAbility(int buttonID, boolean playerAbilitiesOnly, boolean useMenuName) {
        this.buttonID = buttonID;
        this.closeOnEsc = true;
        this.drawDefaultBackground = true;
        guiLeft -= 10;
        xSize = 256 + 10;
        this.setBackground("menubg.png");

        this.useMenuName = useMenuName;
        DBCPacketHandler.Instance.sendToServer(new DBCRequestAbility(-1, playerAbilitiesOnly, this.useMenuName));
    }

    @Override
    public void initGui() {
        super.initGui();
        guiTop += 10;
        if (showDBCAbilities)
            addButton(new GuiNpcButton(3, guiLeft + 183, guiTop + 57, 79, 20, new String[]{"DBC", "Custom"}, page));

        if (scrollAbilities == null) {
            scrollAbilities = new GuiCustomScroll(this, 0, 0);
            scrollAbilities.setSize(177, 185);
        }

        scrollAbilities.guiLeft = guiLeft + 4;
        scrollAbilities.guiTop = guiTop + 4;
        addScroll(scrollAbilities);
        if (page == 0) {
            scrollAbilities.setList(getSearchList());
        } else if (dbcInfo != null) {
            dbcAbilities = DBCData.getClient().getUnlockedDBCAbilitiesMap();
            stateIDs = new ArrayList<>(dbcAbilities.keySet());
            scrollAbilities.setUnsortedList(getSearchList());
        }

        addTextField(new GuiNpcTextField(55, this, fontRendererObj, guiLeft + 4, guiTop + 192, 177, 20, search));

        addButton(new GuiNpcButton(0, guiLeft + 183, guiTop + 4, 79, 20, "gui.add"));
        addButton(new GuiNpcButton(1, guiLeft + 183, guiTop + 26, 79, 20, "gui.cancel"));
        addButton(new GuiNpcButton(2, guiLeft + 183, guiTop + 88, 79, 20, "gui.remove"));
    }

    public SubGuiSelectAbility displayDBCAbilities(PlayerDBCInfo data) {
        if (data == null) {
            showDBCAbilities = false;
            page = 0;
        } else {
            showDBCAbilities = true;
            this.dbcInfo = data;
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
                selectedAbilityID = data.get(selected);
            else
                selectedAbilityID = stateIDs.get(scrollAbilities.selected).byteValue();
            isDBC = page == 1;
            this.close();
        }
        if (id == 1) {
            this.close();
        }
        if (id == 2) {
            this.removeAbility = true;
            this.close();
        }
        if (id == 3) {
            page = bttn.getValue();
            scrollAbilities.resetScroll();
            initGui();
        }
    }

    @Override
    public void setData(Vector<String> list, HashMap<String, Integer> data, EnumScrollData dataType) {
        String name = scrollAbilities.getSelected();
        this.data = data;
        scrollAbilities.setList(getSearchList());
        if (name != null)
            scrollAbilities.setSelected(name);
    }

    @Override
    public void setSelected(String selected) {
        this.selected = selected;
        scrollAbilities.setSelected(selected);
    }

    @Override
    public void customScrollClicked(int i, int i1, int i2, GuiCustomScroll guiCustomScroll) {
        if (guiCustomScroll == scrollAbilities)
            selected = scrollAbilities.getSelected();
        else
            selected = scrollDBCAbilities.getSelected();
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
                scrollAbilities.resetScroll();
                if (page == 0)
                    scrollAbilities.setList(getSearchList());
                else
                    scrollAbilities.setUnsortedList(getSearchList());
            }
        }
    }

    private List<String> getSearchList() {
        if (search.isEmpty()) {
            return new ArrayList<String>(page == 0 ? this.data.keySet() : dbcAbilities.values());
        }

        List<String> list = new ArrayList<String>();
        for (String name : page == 0 ? this.data.keySet() : dbcAbilities.values()) {
            if (name.toLowerCase().contains(search))
                list.add(name);
        }
        return list;
    }
}
