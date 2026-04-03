package kamkeel.npcdbc.controllers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.race.IOverlayModel;
import kamkeel.npcdbc.controllers.base.AbstractDataController;
import kamkeel.npcdbc.controllers.sync.DBCSyncType;
import kamkeel.npcdbc.data.overlay.OverlayModelScript;
import kamkeel.npcdbc.data.overlay.ScriptOverlayModel;
import kamkeel.npcs.network.enums.SyncType;

import java.util.HashMap;
import java.util.Map;

public class OverlayModelController extends AbstractDataController<ScriptOverlayModel> {
    public static OverlayModelController Instance = new OverlayModelController();

    @SideOnly(Side.CLIENT)
    public static Map<String, IOverlayModel> builtInModels;

    public OverlayModelController() {
        Instance = this;
    }

    // ═══════════════════════════════════════════════════════
    //  Template methods
    // ═══════════════════════════════════════════════════════

    @Override
    protected ScriptOverlayModel createNew() { return new ScriptOverlayModel(); }

    @Override
    protected ScriptOverlayModel createNew(String displayName) { return new ScriptOverlayModel(displayName); }

    @Override
    protected SyncType getSyncType() { return DBCSyncType.OVERLAY_MODEL; }

    @Override
    protected String getSaveDirectoryName() { return "customoverlaymodels"; }

    @Override
    protected String getLabel() { return "Overlay Model"; }

    // ═══════════════════════════════════════════════════════
    //  Built-in registration (client-only)
    // ═══════════════════════════════════════════════════════

    @SideOnly(Side.CLIENT)
    public void register(String key, IOverlayModel model) {
        if (key == null || model == null) return;
        if (builtInModels == null) builtInModels = new HashMap<>();
        builtInModels.put(key, model);
    }

    @SideOnly(Side.CLIENT)
    public IOverlayModel getBuiltIn(String key) {
        return key == null || builtInModels == null ? null : builtInModels.get(key);
    }

    @SideOnly(Side.CLIENT)
    public IOverlayModel resolve(String key) {
        if (key == null) return null;

        ScriptOverlayModel custom = customData.get(key);
        if (custom != null && custom.getScriptHandler().hasScript()) {
            OverlayModelScript script = custom.getScriptHandler().getScript();
            if (script != null && custom.getScriptHandler().getEnabled()) {
                IOverlayModel result = script.call(functions -> (IOverlayModel) functions);
                if (result != null) return result;
            }
        }

        return getBuiltIn(key);
    }

    public static OverlayModelController getInstance() {
        return Instance;
    }
}
