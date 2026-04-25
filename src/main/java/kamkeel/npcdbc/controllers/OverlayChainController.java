package kamkeel.npcdbc.controllers;

import kamkeel.npcdbc.controllers.base.AbstractDataController;
import kamkeel.npcdbc.controllers.sync.DBCSyncType;
import kamkeel.npcdbc.data.overlay.OverlayChain;
import kamkeel.npcs.network.enums.SyncType;

public class OverlayChainController extends AbstractDataController<OverlayChain> {
    public static OverlayChainController Instance = new OverlayChainController();

    public OverlayChainController() {
        Instance = this;
    }

    @Override
    protected OverlayChain createNew() {
        return new OverlayChain();
    }

    @Override
    protected OverlayChain createNew(String displayName) {
        return new OverlayChain(displayName);
    }

    @Override
    protected SyncType getSyncType() {
        return DBCSyncType.OVERLAY_CHAIN;
    }

    @Override
    protected String getSaveDirectoryName() {
        return "customoverlaychains";
    }

    @Override
    protected String getLabel() {
        return "Overlay Chain";
    }

    public static OverlayChainController getInstance() {
        return Instance;
    }
}
