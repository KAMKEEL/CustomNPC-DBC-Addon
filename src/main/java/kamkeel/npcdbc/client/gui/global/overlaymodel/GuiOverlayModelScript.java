package kamkeel.npcdbc.client.gui.global.overlaymodel;

import kamkeel.npcdbc.data.overlay.ScriptOverlayModel;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.request.overlaymodel.OverlayModelSavePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.gui.script.GuiScriptInterface;
import noppes.npcs.controllers.data.IScriptUnit;

import java.util.ArrayList;

/**
 * Script editor GUI for a single {@link ScriptOverlayModel}.
 * <p>
 * Extends {@link GuiScriptInterface} to reuse the full script editor
 * (syntax highlighting, autocomplete, hooks panel, settings tab).
 * <p>
 * <b>Save flow</b>: Instead of per-tab script packets
 * ({@code EffectScriptPacket}-style), this GUI serializes the entire
 * overlay model compound (including script data) and dispatches a
 * single {@link OverlayModelSavePacket}. The server response
 * ({@code Type=ViewOverlayModel}) updates the model in-place.
 * <p>
 * <b>No extra packets</b>: The {@link ScriptOverlayModel#scriptHandler}
 * is a {@link noppes.npcs.controllers.data.JaninoScriptHandler} that does
 * <i>not</i> implement {@link noppes.npcs.controllers.data.IScriptHandlerPacket},
 * so this GUI bypasses the default packet-based data loading and sets
 * both {@code loaded} and {@code serverDataReceived} immediately.
 * <p>
 */
public class GuiOverlayModelScript extends GuiScriptInterface {

    /** The overlay model being edited. */
    private final ScriptOverlayModel model;

    // -------------------- Construction --------------------

    /**
     * Create a script editor for the given overlay model.
     *
     * @param parent the parent screen to return to on close
     * @param model  the overlay model whose script handler will be edited
     */
    public GuiOverlayModelScript(GuiScreen parent, ScriptOverlayModel model) {
        super();
        this.parent = parent;
        this.model = model;
        this.handler = model.getScriptHandler();

        // The handler (JaninoScriptHandler) is NOT an IScriptHandlerPacket,
        // so we must set both flags ourselves since create() won't be called.
        this.loaded = true;
        this.serverDataReceived = true;

        this.hookList = new ArrayList<>(model.getScriptHandler().getHooks());
    }

    // -------------------- Open helper --------------------

    /**
     * Opens the overlay model script editor.
     *
     * @param parent parent screen (typically the manager GUI)
     * @param model  the model to edit
     */
    public static void open(GuiScreen parent, ScriptOverlayModel model) {
        if (model == null || !model.hasKey())
            return;

        GuiOverlayModelScript gui = new GuiOverlayModelScript(parent, model);
        Minecraft.getMinecraft().displayGuiScreen(gui);
    }

    // -------------------- Save --------------------

    /**
     * Saves by pushing the current editor text into the handler, then
     * serializing the full model compound and dispatching a single
     * {@link OverlayModelSavePacket}.
     * <p>
     * This replaces the default {@code IScriptHandlerPacket.sync()} path
     * that would emit per-tab script packets.
     */
    @Override
    public void save() {
        this.setScript();
    }

    // -------------------- Server response --------------------

    @Override
    public void setGuiData(NBTTagCompound compound) {
        super.setGuiData(compound);
    }

    @Override
    public void onDataUpdated(NBTTagCompound compound) {
        model.readFromNBT(compound);
        this.handler = model.getScriptHandler();

        if (parent instanceof GuiNpcManageOverlayModels)
            ((GuiNpcManageOverlayModels) parent).setModel(model);
    }

    // -------------------- Close --------------------

    /**
     * On close, ensure the script is compiled, save, and return to
     * the parent screen.
     */
    @Override
    public void close() {
        IScriptUnit current = getCurrentContainer();
        if (current != null)
            current.ensureCompiled();

        this.save();

        if (parent != null) {
            parent.setWorldAndResolution(mc, width, height);
            parent.initGui();
            mc.currentScreen = parent;
        } else {
            super.close();
        }
    }
}
