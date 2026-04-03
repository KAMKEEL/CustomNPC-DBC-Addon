package kamkeel.npcdbc.client.gui.base;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.controllers.base.AbstractDataController;
import kamkeel.npcdbc.controllers.base.IControllerSerializable;
import kamkeel.npcdbc.data.race.serial.DataCompound;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcTextField;
import noppes.npcs.client.gui.util.ICustomScrollListener;
import noppes.npcs.client.gui.util.IGuiData;
import noppes.npcs.client.gui.util.ITextfieldListener;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for "Manage" GUIs that display a scrollable list of
 * controller-managed data objects with Add/Remove/Clone/Edit actions.
 * <p>
 * Uses the modern {@link IGuiData} pattern exclusively.
 * Zero setGuiData, zero request packets, zero IScrollData.
 *
 * @param <T> the data type managed by this GUI
 */
@SideOnly(Side.CLIENT)
public abstract class AbstractGuiNpcManage<T extends IControllerSerializable>
        extends GuiNPCInterface2
        implements ICustomScrollListener, IGuiData, GuiYesNoCallback, ITextfieldListener {

    // ═══════════════════════════════════════════════════════
    //  State
    // ═══════════════════════════════════════════════════════

    protected GuiCustomScroll scrollList;

    /** Currently selected/editing item, or null if none. */
    protected T selected;

    /** Display name of the selected item (for scroll list tracking). */
    protected String selectedName;

    /** Key of the selected item (for tracking across renames). */
    protected String selectedKey;

    /** Original key at time of selection (for rename detection in save). */
    protected String originalKey;

    /** Current search filter text. */
    protected String search = "";

    // Button IDs
    protected static final int BTN_ADD = 0;
    protected static final int BTN_REMOVE = 1;
    protected static final int BTN_CLONE = 2;
    protected static final int BTN_EDIT = 3;

    // TextField IDs
    protected static final int TF_SEARCH = 55;
    protected static final int TF_NAME = 13;

    // ═══════════════════════════════════════════════════════
    //  TEMPLATE METHODS — Impls MUST override
    // ═══════════════════════════════════════════════════════

    /** Return the controller backing this GUI. */
    protected abstract AbstractDataController<T> getController();

    /** Create a new empty T for deserialization from sync updates. */
    protected abstract T createNew();

    /** Create a new T with a display name (for Add button). */
    protected abstract T createNew(String displayName);

    /**
     * Return the packet to send for saving an item.
     *
     * @param nbt         the serialized item data
     * @param previousKey the key before any rename (for rename handling)
     */
    protected abstract AbstractPacket createSavePacket(NBTTagCompound nbt, String previousKey);

    /** Return the packet to send for deleting an item by key. */
    protected abstract AbstractPacket createDeletePacket(String key);

    /** Return the packet to send for cloning an item by key. */
    protected abstract AbstractPacket createClonePacket(String key);

    /**
     * Called when the Edit button is clicked with a valid selection.
     * Open the appropriate sub-GUI or edit dialog.
     *
     * @param item the currently selected item
     */
    protected abstract void openEditGui(T item);

    // ═══════════════════════════════════════════════════════
    //  OPTIONAL HOOKS
    // ═══════════════════════════════════════════════════════

    /**
     * Called when the selection changes. Subclasses can update
     * preview renderers, additional labels, etc.
     * Default: no-op.
     */
    protected void onSelectionChanged(T item) {}

    /**
     * Provides additional initGui setup after the base buttons/scroll/search
     * are created. Called at the end of {@link #initGui()}.
     * Default: no-op.
     */
    protected void initGuiExtra() {}

    // ═══════════════════════════════════════════════════════
    //  Constructor
    // ═══════════════════════════════════════════════════════

    public AbstractGuiNpcManage(EntityNPCInterface npc) {
        super(npc);
    }

    // ═══════════════════════════════════════════════════════
    //  CONCRETE — IGuiData implementation
    // ═══════════════════════════════════════════════════════

    @Override
    public void onDataUpdated(NBTTagCompound compound) {
        T updated = createNew();
        updated.deserialize(DataCompound.ofNbt(compound));

        if (selected != null && updated.getKey().equals(selectedKey))
            setSelected(updated);

        refreshList();
    }

    @Override
    public void onDataRemoved(String key) {
        if (key != null && key.equals(selectedKey))
            setSelected(null);
        refreshList();
    }

    // ═══════════════════════════════════════════════════════
    //  CONCRETE — Button actions
    // ═══════════════════════════════════════════════════════

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;

        if (button.id == BTN_ADD) {
            save();
            String name = "New";
            while (getController().hasName(name)) name += "_";
            T newItem = createNew(name);
            setSelected(newItem);
            DBCPacketHandler.Instance.sendToServer(
                    createSavePacket(newItem.serialize(DataCompound.create()).toNbt(), originalKey));
        }

        if (button.id == BTN_REMOVE) {
            if (selectedKey != null && !selectedKey.isEmpty()) {
                String displayName = selected != null ? selected.getDisplayName() : selectedKey;
                GuiYesNo confirm = new GuiYesNo(this, displayName,
                        StatCollector.translateToLocal("gui.delete"), 1);
                displayGuiScreen(confirm);
            }
        }

        if (button.id == BTN_CLONE) {
            if (selectedKey != null && !selectedKey.isEmpty())
                DBCPacketHandler.Instance.sendToServer(createClonePacket(selectedKey));
        }

        if (button.id == BTN_EDIT) {
            if (selected != null && selected.hasKey())
                openEditGui(selected);
        }
    }

    // ═══════════════════════════════════════════════════════
    //  CONCRETE — Scroll list management
    // ═══════════════════════════════════════════════════════

    @Override
    public void customScrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
        if (scroll.id != 0) return;

        String clickedName = scrollList.getSelected();
        if (clickedName == null || clickedName.isEmpty() || clickedName.equals(selectedName))
            return;

        T clicked = getController().getByName(clickedName);
        if (clicked == null) return;

        setSelected(clicked);
        initGui();
    }

    @Override
    public void customScrollDoubleClicked(String selection, GuiCustomScroll scroll) {}

    /**
     * Build the filtered list of display names for the scroll.
     */
    protected List<String> getSearchList() {
        List<T> sorted = getController().getSorted();
        List<String> names = new ArrayList<>();
        for (T item : sorted) {
            if (search.isEmpty() || item.getDisplayName().toLowerCase().contains(search))
                names.add(item.getDisplayName());
        }
        return names;
    }

    protected void refreshList() {
        if (scrollList == null) return;
        scrollList.setList(getSearchList());
        if (selectedName != null)
            scrollList.setSelected(selectedName);

        boolean hasSelection = selected != null && selected.hasKey();
        if (getButton(BTN_REMOVE) != null) getButton(BTN_REMOVE).enabled = hasSelection;
        if (getButton(BTN_CLONE) != null) getButton(BTN_CLONE).enabled = hasSelection;
        if (getButton(BTN_EDIT) != null) getButton(BTN_EDIT).enabled = hasSelection;
    }

    // ═══════════════════════════════════════════════════════
    //  CONCRETE — Selection management
    // ═══════════════════════════════════════════════════════

    public void setSelected(T item) {
        this.selected = item;
        this.selectedKey = item != null ? item.getKey() : null;
        this.originalKey = selectedKey;
        this.selectedName = item != null ? item.getDisplayName() : null;
        onSelectionChanged(item);
    }

    // ═══════════════════════════════════════════════════════
    //  CONCRETE — Delete confirm
    // ═══════════════════════════════════════════════════════

    @Override
    public void confirmClicked(boolean result, int id) {
        NoppesUtil.openGUI(player, this);
        if (!result) return;

        if (id == 1 && selectedKey != null && !selectedKey.isEmpty()) {
            DBCPacketHandler.Instance.sendToServer(createDeletePacket(selectedKey));
            setSelected(null);
            if (scrollList != null) scrollList.clear();
            initGui();
        }
    }

    // ═══════════════════════════════════════════════════════
    //  CONCRETE — Save (called on gui close / add)
    // ═══════════════════════════════════════════════════════

    @Override
    public void save() {
        if (selected == null || !selected.hasKey() || selectedKey == null)
            return;
        DBCPacketHandler.Instance.sendToServer(
                createSavePacket(selected.serialize(DataCompound.create()).toNbt(), originalKey));
    }

    // ═══════════════════════════════════════════════════════
    //  CONCRETE — Search
    // ═══════════════════════════════════════════════════════

    @Override
    public void keyTyped(char c, int i) {
        super.keyTyped(c, i);

        if (getTextField(TF_SEARCH) != null && getTextField(TF_SEARCH).isFocused()) {
            String newSearch = getTextField(TF_SEARCH).getText().toLowerCase();
            if (!search.equals(newSearch)) {
                search = newSearch;
                if (scrollList != null) {
                    scrollList.resetScroll();
                    scrollList.setList(getSearchList());
                }
            }
        }
    }

    // ═══════════════════════════════════════════════════════
    //  CONCRETE — Name edit (common pattern)
    // ═══════════════════════════════════════════════════════

    @Override
    public void unFocused(GuiNpcTextField textField) {
        if (selected == null || !selected.hasKey()) return;

        if (textField.id == TF_NAME) {
            String newName = textField.getText();
            if (!newName.isEmpty() && !hasDisplayName(newName, selectedKey)) {
                String oldName = selected.getDisplayName();
                selected.setDisplayName(newName);
                selectedName = newName;
                if (scrollList != null) scrollList.replace(oldName, newName);
            } else {
                textField.setText(selected.getDisplayName());
            }
        }
    }

    protected boolean hasDisplayName(String name) {
        return getController().getByName(name) != null;
    }

    protected boolean hasDisplayName(String name, String excludeKey) {
        T existing = getController().getByName(name);
        if (existing == null) return false;
        return !existing.getKey().equals(excludeKey);
    }

    // ═══════════════════════════════════════════════════════
    //  Accessor
    // ═══════════════════════════════════════════════════════

    public T getSelected() {
        return selected;
    }
}
