package kamkeel.npcdbc.client.gui.global.overlaymodel;

import kamkeel.npcdbc.api.client.overlay.IOverlay;
import kamkeel.npcdbc.client.gui.base.AbstractGuiNpcManage;
import kamkeel.npcdbc.client.gui.dbc.EntityPreviewRenderer;
import kamkeel.npcdbc.controllers.OverlayModelController;
import kamkeel.npcdbc.controllers.base.AbstractDataController;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.data.overlay.Overlay;
import kamkeel.npcdbc.data.overlay.OverlayChain;
import kamkeel.npcdbc.data.overlay.ScriptOverlayModel;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.packets.request.overlaymodel.OverlayModelClonePacket;
import kamkeel.npcdbc.network.packets.request.overlaymodel.OverlayModelDeletePacket;
import kamkeel.npcdbc.network.packets.request.overlaymodel.OverlayModelSavePacket;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.client.gui.util.GuiNpcTextField;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import org.lwjgl.opengl.GL11;

public class GuiNpcManageOverlayModels extends AbstractGuiNpcManage<ScriptOverlayModel> {

    private final EntityPreviewRenderer previewRenderer = new EntityPreviewRenderer()
            .setDefaultZoom(2.5f)
            .setZoomBounds(1f, 5f)
            .setFollowMouse(true)
            .setAllowRotate(true);

    private static OverlayChain MODEL_CHAIN = OverlayChain.create("model");
    private static Overlay MODEL_OVERLAY = MODEL_CHAIN.add(IOverlay.Type.Custom, 0xffffff);

    private DBCDisplay display;

    public GuiNpcManageOverlayModels(EntityNPCInterface npc) {
        super(npc);
        this.npc = DBCDisplay.setupGUINPC((EntityCustomNpc) npc);
        this.npc.display.name = "overlay model man";

        display = ((INPCDisplay) this.npc.display).getDBCDisplay();
        display.hairCode = "";
        display.getOverlayChains().add(MODEL_CHAIN);
    }

    // ==================== Template Methods ====================

    @Override
    protected OverlayModelController getController() {
        return OverlayModelController.getInstance();
    }

    @Override
    protected ScriptOverlayModel createNew() { return new ScriptOverlayModel(); }

    @Override
    protected ScriptOverlayModel createNew(String name) { return new ScriptOverlayModel(name); }

    @Override
    protected AbstractPacket createSavePacket(NBTTagCompound nbt, String prevKey) {
        return new OverlayModelSavePacket(nbt, prevKey);
    }

    @Override
    protected AbstractPacket createDeletePacket(String key) {
        return new OverlayModelDeletePacket(key);
    }

    @Override
    protected AbstractPacket createClonePacket(String key) {
        return new OverlayModelClonePacket(key);
    }

    @Override
    protected void openEditGui(ScriptOverlayModel item) {
        GuiOverlayModelScript.open(this, item);
    }

    @Override
    protected void onSelectionChanged(ScriptOverlayModel item) {
        MODEL_OVERLAY.modelKey(item != null ? item.key : null);
    }

    // ==================== GUI Init ====================

    @Override
    public void initGui() {
        super.initGui();

        boolean hasSelection = selected != null && selected.hasKey();

        addButton(new GuiNpcButton(BTN_ADD, guiLeft + 368, guiTop + 8, 45, 20, "gui.add"));

        addButton(new GuiNpcButton(BTN_REMOVE, guiLeft + 368, guiTop + 32, 45, 20, "gui.remove"));
        getButton(BTN_REMOVE).enabled = hasSelection;

        addButton(new GuiNpcButton(BTN_CLONE, guiLeft + 368, guiTop + 56, 45, 20, "gui.clone"));
        getButton(BTN_CLONE).enabled = hasSelection;

        addButton(new GuiNpcButton(BTN_EDIT, guiLeft + 368, guiTop + 80, 45, 20, "gui.edit"));
        getButton(BTN_EDIT).enabled = hasSelection;

        if (scrollList == null) {
            scrollList = new GuiCustomScroll(this, 0, 0);
            scrollList.setSize(143, 185);
        }
        scrollList.guiLeft = guiLeft + 220;
        scrollList.guiTop = guiTop + 4;
        addScroll(scrollList);
        scrollList.setList(getSearchList());

        if (selectedName != null)
            scrollList.setSelected(selectedName);

        addTextField(new GuiNpcTextField(TF_SEARCH, this, fontRendererObj,
                guiLeft + 220, guiTop + 4 + 3 + 185, 143, 20, search));

        if (hasSelection) {
            addLabel(new GuiNpcLabel(12, "gui.name", guiLeft + 8, guiTop + 8));
            addTextField(new GuiNpcTextField(TF_NAME, this, fontRendererObj,
                    guiLeft + 40, guiTop + 4, 160, 20, selected.name));
        }

        initGuiExtra();
    }

    // ==================== Drawing ====================

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

    // ==================== Save Override ====================

    @Override
    public void save() {
        super.save();
        MODEL_OVERLAY.modelKey(null);
    }

    public ScriptOverlayModel getModel() {
        return selected;
    }
}
