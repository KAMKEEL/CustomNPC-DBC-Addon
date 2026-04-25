package kamkeel.npcdbc.controllers.sync.handlers;

import kamkeel.npcdbc.controllers.OverlayChainController;
import kamkeel.npcdbc.controllers.base.AbstractDataController;
import kamkeel.npcdbc.controllers.base.AbstractDataSyncHandler;
import kamkeel.npcdbc.data.overlay.OverlayChain;

public class OverlaySyncHandler extends AbstractDataSyncHandler<OverlayChain> {

    @Override
    protected AbstractDataController<OverlayChain> getController() {
        return OverlayChainController.getInstance();
    }
}
