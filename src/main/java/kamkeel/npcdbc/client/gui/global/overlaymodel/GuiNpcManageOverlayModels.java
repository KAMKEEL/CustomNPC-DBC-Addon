package kamkeel.npcdbc.client.gui.global.overlaymodel;

import kamkeel.npcdbc.api.client.overlay.IOverlay;
import kamkeel.npcdbc.client.gui.dbc.EntityPreviewRenderer;
import kamkeel.npcdbc.controllers.OverlayModelController;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.data.overlay.Overlay;
import kamkeel.npcdbc.data.overlay.OverlayChain;
import kamkeel.npcdbc.data.overlay.ScriptOverlayModel;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.request.overlaymodel.OverlayModelClonePacket;
import kamkeel.npcdbc.network.packets.request.overlaymodel.OverlayModelDeletePacket;
import kamkeel.npcdbc.network.packets.request.overlaymodel.OverlayModelSavePacket;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.client.gui.util.GuiNpcTextField;
import noppes.npcs.client.gui.util.ICustomScrollListener;
import noppes.npcs.client.gui.util.IGuiData;
import noppes.npcs.client.gui.util.ITextfieldListener;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiNpcManageOverlayModels extends GuiNPCInterface2
        implements ICustomScrollListener, IGuiData, GuiYesNoCallback, ITextfieldListener {

    public GuiCustomScroll scrollModels;

    private ScriptOverlayModel model;
    public String selected = null;
    private String selectedKey = null;
    private String originalName = null;
    public String search = "";

    // ==================== Entity Renderer ====================
    private final EntityPreviewRenderer previewRenderer = new EntityPreviewRenderer()
            .setDefaultZoom(2.5f)
            .setZoomBounds(1f, 5f)
            .setFollowMouse(true)
            .setAllowRotate(true);

    // ==================== Overlay Model Data ====================
    private static OverlayChain MODEL_CHAIN = OverlayChain.create("model");
    private static Overlay MODEL_OVERLAY = MODEL_CHAIN.add(IOverlay.Type.Custom, 0xffffff);

    private DBCDisplay display;

    // ==================== Button IDs ====================
    private static final int BTN_ADD = 0;
    private static final int BTN_REMOVE = 1;
    private static final int BTN_CLONE = 2;
    private static final int BTN_EDIT = 3;

    // ==================== TextField IDs ====================
    private static final int TF_SEARCH = 55;
    private static final int TF_NAME = 13;

    public GuiNpcManageOverlayModels(EntityNPCInterface npc) {
        super(npc);
        this.npc = DBCDisplay.setupGUINPC((EntityCustomNpc) npc);
        this.npc.display.name = "overlay model man";

        display = ((INPCDisplay) this.npc.display).getDBCDisplay();
        display.hairCode = "";
        display.getOverlayChains().add(MODEL_CHAIN);
    }

    // ==================== GUI Init ====================

    @Override
    public void initGui() {
        super.initGui();

        boolean hasSelection = model != null && model.hasKey();

        addButton(new GuiNpcButton(BTN_ADD, guiLeft + 368, guiTop + 8, 45, 20, "gui.add"));

        addButton(new GuiNpcButton(BTN_REMOVE, guiLeft + 368, guiTop + 32, 45, 20, "gui.remove"));
        getButton(BTN_REMOVE).enabled = hasSelection;

        addButton(new GuiNpcButton(BTN_CLONE, guiLeft + 368, guiTop + 56, 45, 20, "gui.clone"));
        getButton(BTN_CLONE).enabled = hasSelection;

        addButton(new GuiNpcButton(BTN_EDIT, guiLeft + 368, guiTop + 80, 45, 20, "gui.edit"));
        getButton(BTN_EDIT).enabled = hasSelection;

        if (scrollModels == null) {
            scrollModels = new GuiCustomScroll(this, 0, 0);
            scrollModels.setSize(143, 185);
        }
        scrollModels.guiLeft = guiLeft + 220;
        scrollModels.guiTop = guiTop + 4;
        addScroll(scrollModels);
        scrollModels.setList(getSearchList());

        if (selected != null)
            scrollModels.setSelected(selected);

        addTextField(new GuiNpcTextField(TF_SEARCH, this, fontRendererObj,
                guiLeft + 220, guiTop + 4 + 3 + 185, 143, 20, search));

        if (hasSelection) {
            addLabel(new GuiNpcLabel(12, "gui.name", guiLeft + 8, guiTop + 8));
            addTextField(new GuiNpcTextField(TF_NAME, this, fontRendererObj,
                    guiLeft + 40, guiTop + 4, 160, 20, model.name));
        }
    }

    @Override
    public void drawScreen(int mX, int mY, float parT) {
        super.drawScreen(mX, mY, parT);
        previewRenderer.setAnchor(guiLeft + 110, guiTop + 202);
        previewRenderer.draw(npc, mX, mY, parT);
    }

    @Override
    public void drawBackground() {
        super.drawBackground();
        drawGradientRect(guiLeft + 10, guiTop + 26, guiLeft + 210, guiTop + 210, 0xc0101010, 0xd0101010);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    // ==================== Button Actions ====================

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;

        if (button.id == BTN_ADD) {
            save();
            String name = "New";
            while (hasDisplayName(name))
                name += "_";
            ScriptOverlayModel newModel = new ScriptOverlayModel(name);
            setModel(newModel);
            DBCPacketHandler.Instance.sendToServer(
                    new OverlayModelSavePacket(newModel.writeToNBT(new NBTTagCompound()), originalName));
        }

        if (button.id == BTN_REMOVE) {
            if (selectedKey != null && !selectedKey.isEmpty()) {
                GuiYesNo guiyesno = new GuiYesNo(this,
                        selected != null ? selected : selectedKey,
                        StatCollector.translateToLocal("gui.delete"), 1);
                displayGuiScreen(guiyesno);
            }
        }

        if (button.id == BTN_CLONE) {
            if (selectedKey != null && !selectedKey.isEmpty())
                DBCPacketHandler.Instance.sendToServer(new OverlayModelClonePacket(selectedKey));
        }

        if (button.id == BTN_EDIT) {
            if (model != null && model.hasKey())
                GuiOverlayModelScript.open(this, model);
        }
    }

    // ==================== IGuiData ====================
    
    @Override
    public void onDataUpdated(NBTTagCompound compound) {
        ScriptOverlayModel updated = new ScriptOverlayModel();
        updated.readFromNBT(compound);

        if (model != null && updated.key.equals(selectedKey))
            setModel(updated);

        refreshList();
    }

    @Override
    public void onDataRemoved(String key) {
        if (key != null && key.equals(selectedKey))
            setModel(null);
        refreshList();
    }

    // ==================== Scroll Listener ====================

    @Override
    public void customScrollClicked(int i, int j, int k, GuiCustomScroll guiCustomScroll) {
        if (guiCustomScroll.id != 0)
            return;

        String clickedName = scrollModels.getSelected();
        if (clickedName == null || clickedName.isEmpty() || clickedName.equals(selected))
            return;

        ScriptOverlayModel clicked = OverlayModelController.getInstance().getCustomByName(clickedName);
        if (clicked == null)
            return;

        setModel(clicked);
        initGui();
    }

    @Override
    public void customScrollDoubleClicked(String selection, GuiCustomScroll scroll) {
    }

    // ==================== Save ====================

    @Override
    public void save() {
        if (model == null || !model.hasKey() || selectedKey == null)
            return;
        DBCPacketHandler.Instance.sendToServer(
                new OverlayModelSavePacket(model.writeToNBT(new NBTTagCompound()), originalName));
        MODEL_OVERLAY.modelKey(null);
    }

    // ==================== Delete Confirm ====================

    @Override
    public void confirmClicked(boolean result, int id) {
        NoppesUtil.openGUI(player, this);
        if (!result)
            return;

        if (id == 1 && selectedKey != null && !selectedKey.isEmpty()) {
            DBCPacketHandler.Instance.sendToServer(new OverlayModelDeletePacket(selectedKey));
            setModel(null);
            if (scrollModels != null)
                scrollModels.clear();
            initGui();
        }
    }

    // ==================== Name Edit ====================

    @Override
    public void unFocused(GuiNpcTextField guiNpcTextField) {
        if (model == null || !model.hasKey())
            return;

        if (guiNpcTextField.id == TF_NAME) {
            String newName = guiNpcTextField.getText();
            if (!newName.isEmpty() && !hasDisplayName(newName, selectedKey)) {
                String oldName = model.name;
                model.setName(newName);
                selected = newName;
                if (scrollModels != null)
                    scrollModels.replace(oldName, newName);
            } else {
                guiNpcTextField.setText(model.name);
            }
        }
    }

    // ==================== Search ====================

    @Override
    public void keyTyped(char c, int i) {
        super.keyTyped(c, i);

        if (getTextField(TF_SEARCH) != null && getTextField(TF_SEARCH).isFocused()) {
            String newSearch = getTextField(TF_SEARCH).getText().toLowerCase();
            if (!search.equals(newSearch)) {
                search = newSearch;
                if (scrollModels != null) {
                    scrollModels.resetScroll();
                    scrollModels.setList(getSearchList());
                }
            }
        }
    }

    private List<String> getSearchList() {
        List<ScriptOverlayModel> sorted = OverlayModelController.getInstance().getSortedCustomModels();
        List<String> names = new ArrayList<>();
        for (ScriptOverlayModel m : sorted) {
            if (search.isEmpty() || m.name.toLowerCase().contains(search))
                names.add(m.name);
        }
        return names;
    }

    // ==================== Helpers ====================

    private void refreshList() {
        if (scrollModels == null) return;
        scrollModels.setList(getSearchList());
        if (selected != null)
            scrollModels.setSelected(selected);
        boolean hasSelection = model != null && model.hasKey();
        if (getButton(BTN_REMOVE) != null) getButton(BTN_REMOVE).enabled = hasSelection;
        if (getButton(BTN_CLONE) != null) getButton(BTN_CLONE).enabled = hasSelection;
        if (getButton(BTN_EDIT) != null) getButton(BTN_EDIT).enabled = hasSelection;
    }

    private boolean hasDisplayName(String name) {
        return OverlayModelController.getInstance().getCustomByName(name) != null;
    }

    private boolean hasDisplayName(String name, String excludeKey) {
        ScriptOverlayModel existing = OverlayModelController.getInstance().getCustomByName(name);
        if (existing == null) return false;
        return !existing.key.equals(excludeKey);
    }

    public ScriptOverlayModel getModel() {
        return model;
    }

    public void setModel(ScriptOverlayModel m) {
        this.model = m;
        this.selectedKey = m != null ? m.key : null;
        this.originalName = selectedKey;
        this.selected = m != null ? m.name : null;
        MODEL_OVERLAY.modelKey(m != null ? m.key : null);
    }
}
