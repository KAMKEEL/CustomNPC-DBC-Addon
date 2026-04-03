package kamkeel.npcdbc.controllers.sync.handlers;

import kamkeel.npcdbc.controllers.OverlayModelController;
import kamkeel.npcdbc.controllers.base.AbstractDataController;
import kamkeel.npcdbc.controllers.base.AbstractDataSyncHandler;
import kamkeel.npcdbc.data.overlay.ScriptOverlayModel;

public class OverlayModelSyncHandler extends AbstractDataSyncHandler<ScriptOverlayModel> {

    @Override
    protected AbstractDataController<ScriptOverlayModel> getController() {
        return OverlayModelController.getInstance();
    }
}
