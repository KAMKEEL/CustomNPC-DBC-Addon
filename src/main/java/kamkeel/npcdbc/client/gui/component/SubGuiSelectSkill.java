package kamkeel.npcdbc.client.gui.component;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.constants.DBCSkills;
import kamkeel.npcdbc.controllers.SkillController;
import kamkeel.npcdbc.data.skill.CustomSkill;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcTextField;
import noppes.npcs.client.gui.util.ICustomScrollListener;
import noppes.npcs.client.gui.util.ITextfieldListener;
import noppes.npcs.client.gui.util.SubGuiInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class SubGuiSelectSkill extends SubGuiInterface implements ICustomScrollListener, ITextfieldListener {

    public static final int MODE_DBC = 0;
    public static final int MODE_CUSTOM = 1;

    private static final int BTN_SELECT = 0;
    private static final int BTN_CANCEL = 1;
    private static final int BTN_TOGGLE = 2;
    private static final int TF_SEARCH  = 10;

    private int viewMode;
    private String search = "";

    // maps display-name → id used inside each mode
    private final HashMap<String, Integer> displayNameToId = new HashMap<>();
    private final HashMap<String, Integer> allDisplayNameToId = new HashMap<>();

    private final int preselectedId;
    private final int preselectedMode;

    private int selectedSkillId = -1;
    private String selectedStringId  = null;
    private int selectedMode = -1;

    private GuiCustomScroll scroll;


    public SubGuiSelectSkill() {
        this(-1, MODE_CUSTOM);
    }

    public SubGuiSelectSkill(int preselectedId, int preselectedMode) {
        this.preselectedId = preselectedId;
        this.preselectedMode = preselectedMode;
        this.viewMode = preselectedMode;

        setBackground("menubg.png");
        xSize = 200;
        ySize = 216;
    }

    @Override
    public void initGui() {
        super.initGui();

        addTextField(new GuiNpcTextField(TF_SEARCH, this, fontRendererObj,
            guiLeft + 5, guiTop + 5, 190, 18, search));

        addButton(new GuiNpcButton(BTN_TOGGLE, guiLeft + 5, guiTop + 26, 190, 20, getToggleLabel()));

        int scrollTop = guiTop + 49;
        int scrollHeight = 122;

        if (scroll == null) {
            scroll = new GuiCustomScroll(this, 0);
        }
        scroll.setSize(190, scrollHeight);
        scroll.guiLeft = guiLeft + 5;
        scroll.guiTop = scrollTop;
        addScroll(scroll);

        buildAllSkills();
        List<String> filtered = getFilteredList();
        scroll.setUnsortedList(filtered);

        if (preselectedId >= 0 && viewMode == preselectedMode && !scroll.hasSelected()) {
            for (String name : filtered) {
                Integer id = displayNameToId.get(name);
                if (id != null && id == preselectedId) {
                    scroll.setSelected(name);
                    break;
                }
            }
        }

        addButton(new GuiNpcButton(BTN_SELECT, guiLeft + 5,   guiTop + 188, 90, 20, "gui.select"));
        addButton(new GuiNpcButton(BTN_CANCEL, guiLeft + 105, guiTop + 188, 90, 20, "gui.cancel"));
        getButton(BTN_SELECT).setEnabled(scroll.hasSelected());
    }

    private String getToggleLabel() {
        return viewMode == MODE_CUSTOM ? "Custom Skills" : "DBC Skills";
    }

    private void buildAllSkills() {
        allDisplayNameToId.clear();

        if (viewMode == MODE_CUSTOM) {
            for (Map.Entry<Integer, CustomSkill> entry : SkillController.Instance.customSkills.entrySet()) {
                CustomSkill skill = entry.getValue();
                String displayName = (skill.stringLiteralId != null && !skill.stringLiteralId.isEmpty())
                    ? skill.stringLiteralId
                    : "Skill " + skill.id;
                allDisplayNameToId.put(displayName, skill.id);
            }
        } else {
            for (DBCSkills skill : DBCSkills.values()) {
                // Use the enum name as the display label, ordinal as id
                allDisplayNameToId.put(skill.name(), skill.index());
            }
        }
    }

    private List<String> getFilteredList() {
        List<String> list = new ArrayList<>();
        displayNameToId.clear();
        for (Map.Entry<String, Integer> entry : allDisplayNameToId.entrySet()) {
            String name = entry.getKey();
            if (search.isEmpty() || name.toLowerCase().contains(search)) {
                list.add(name);
                displayNameToId.put(name, entry.getValue());
            }
        }
        Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
        return list;
    }

    @Override
    public void buttonEvent(GuiButton guibutton) {
        switch (guibutton.id) {
            case BTN_SELECT:
                if (scroll.hasSelected()) confirmSelection(scroll.getSelected());
                break;

            case BTN_CANCEL:
                selectedSkillId = -1;
                selectedStringId = null;
                selectedMode = -1;
                close();
                break;

            case BTN_TOGGLE:
                viewMode = (viewMode == MODE_CUSTOM) ? MODE_DBC : MODE_CUSTOM;
                search = "";
                scroll = null;
                initGui();
                break;
        }
    }

    private void confirmSelection(String name) {
        Integer id = displayNameToId.get(name);
        if (id == null) return;

        selectedSkillId = id;
        selectedMode = viewMode;

        if (viewMode == MODE_CUSTOM) {
            CustomSkill skill = SkillController.Instance.customSkills.get(id);
            selectedStringId = skill != null ? skill.stringLiteralId : name;
        } else {
            DBCSkills dbc = DBCSkills.values()[id];
            selectedStringId = dbc.name();
        }

        close();
    }

    @Override
    public void customScrollClicked(int i, int j, int k, GuiCustomScroll guiCustomScroll) {
        if (guiCustomScroll.id == 0) {
            getButton(BTN_SELECT).setEnabled(scroll.hasSelected());
        }
    }

    @Override
    public void customScrollDoubleClicked(String selection, GuiCustomScroll guiCustomScroll) {
        if (guiCustomScroll.id == 0 && selection != null) {
            confirmSelection(selection);
        }
    }

    @Override
    public void keyTyped(char c, int i) {
        super.keyTyped(c, i);
        GuiNpcTextField tf = getTextField(TF_SEARCH);
        if (tf != null && tf.isFocused()) {
            String newSearch = tf.getText().toLowerCase();
            if (!search.equals(newSearch)) {
                search = newSearch;
                scroll.setUnsortedList(getFilteredList());
                scroll.resetScroll();
            }
        }
    }

    @Override
    public void unFocused(GuiNpcTextField textField) {}


    public int getSelectedMode() { return selectedMode; }

    public int getSelectedSkillId() { return selectedSkillId; }

    public String getSelectedStringId() { return selectedStringId; }
}
