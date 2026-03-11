package kamkeel.npcdbc.client.gui.component;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.constants.DBCStatusEffects;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcTextField;
import noppes.npcs.client.gui.util.ICustomScrollListener;
import noppes.npcs.client.gui.util.ITextfieldListener;
import noppes.npcs.client.gui.util.SubGuiInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class SubGuiSelectDBCEffect extends SubGuiInterface implements ICustomScrollListener, ITextfieldListener {

    public static final int MODE_PERMANENT = 0;
    public static final int MODE_TEMPORARY = 1;

    private static final int BTN_SELECT = 0;
    private static final int BTN_CANCEL = 1;
    private static final int BTN_TOGGLE = 2;
    private static final int TF_SEARCH = 10;

    private int viewMode;
    private String search = "";

    private final HashMap<String, Integer> displayNameToOrdinal = new HashMap<>();
    private final HashMap<String, Integer> allDisplayNameToOrdinal = new HashMap<>();

    private final int preselectedOrdinal;
    private final int preselectedMode;

    private int selectedOrdinal = -1;
    private String selectedStringId = null;
    private int selectedMode = -1;

    private GuiCustomScroll scroll;

    public SubGuiSelectDBCEffect() {
        this(-1, MODE_PERMANENT);
    }

    public SubGuiSelectDBCEffect(int preselectedOrdinal, int preselectedMode) {
        this.preselectedOrdinal = preselectedOrdinal;
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

        buildAllEffects();
        List<String> filtered = getFilteredList();
        scroll.setUnsortedList(filtered);

        if (preselectedOrdinal >= 0 && viewMode == preselectedMode && !scroll.hasSelected()) {
            for (String name : filtered) {
                Integer ord = displayNameToOrdinal.get(name);
                if (ord != null && ord == preselectedOrdinal) {
                    scroll.setSelected(name);
                    break;
                }
            }
        }

        addButton(new GuiNpcButton(BTN_SELECT, guiLeft + 5, guiTop + 188, 90, 20, "gui.select"));
        addButton(new GuiNpcButton(BTN_CANCEL, guiLeft + 105, guiTop + 188, 90, 20, "gui.cancel"));
        getButton(BTN_SELECT).setEnabled(scroll.hasSelected());
    }

    private String getToggleLabel() {
        return viewMode == MODE_PERMANENT ? "Permanent Effects" : "Temporary Effects";
    }

    private void buildAllEffects() {
        allDisplayNameToOrdinal.clear();

        int targetType = (viewMode == MODE_PERMANENT) ? 0 : 1;
        for (DBCStatusEffects effect : DBCStatusEffects.values()) {
            if (effect.getType() == targetType) {
                allDisplayNameToOrdinal.put(effect.name(), effect.ordinal());
            }
        }
    }

    private List<String> getFilteredList() {
        List<String> list = new ArrayList<>();
        displayNameToOrdinal.clear();

        for (Map.Entry<String, Integer> entry : allDisplayNameToOrdinal.entrySet()) {
            String name = entry.getKey();
            if (search.isEmpty() || name.toLowerCase().contains(search)) {
                list.add(name);
                displayNameToOrdinal.put(name, entry.getValue());
            }
        }

        list.sort(String.CASE_INSENSITIVE_ORDER);
        return list;
    }

    @Override
    public void buttonEvent(GuiButton guibutton) {
        switch (guibutton.id) {
            case BTN_SELECT:
                if (scroll.hasSelected()) confirmSelection(scroll.getSelected());
                break;

            case BTN_CANCEL:
                selectedOrdinal = -1;
                selectedStringId = null;
                selectedMode = -1;
                close();
                break;

            case BTN_TOGGLE:
                viewMode = (viewMode == MODE_PERMANENT) ? MODE_TEMPORARY : MODE_PERMANENT;
                search = "";
                scroll = null;
                initGui();
                break;
        }
    }

    private void confirmSelection(String name) {
        Integer ordinal = displayNameToOrdinal.get(name);
        if (ordinal == null) return;

        DBCStatusEffects effect = DBCStatusEffects.byOrdinal(ordinal);
        if (effect == null) return;

        selectedOrdinal = ordinal;
        selectedMode = viewMode;
        selectedStringId = effect.name();

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
    public int getSelectedOrdinal() { return selectedOrdinal; }
    public String getSelectedStringId() { return selectedStringId; }
}
