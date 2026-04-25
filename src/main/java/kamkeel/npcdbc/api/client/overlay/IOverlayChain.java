package kamkeel.npcdbc.api.client.overlay;

import kamkeel.npcdbc.controllers.base.IControllerSerializable;
import kamkeel.npcdbc.data.form.FacePartData;
import kamkeel.npcdbc.data.overlay.OverlayContext;

import java.util.List;
import java.util.function.Function;

public interface IOverlayChain extends IControllerSerializable {

    boolean isEnabled();

    IOverlayChain setEnabled(boolean enable);

    IOverlayChain enable(boolean enable);

    IOverlayChain disable(FacePartData.Part... parts);

    IOverlayChain condition(Function<OverlayContext, Boolean> condition);

    boolean checkCondition(OverlayContext ctx);

    IOverlay getOverlay(int index);

    IOverlay get(int index);

    List<? extends IOverlay> getOverlays();

    int size();

    IOverlay addOverlay(IOverlay.Type type);

    IOverlay add(IOverlay.Type type);

    IOverlay add(IOverlay.Type type, String texture);

    IOverlay add(IOverlay.Type type, IOverlay.ColorType colorType);

    IOverlay add(IOverlay.Type type, int color);

    IOverlay add(IOverlay.Type type, IOverlay.ColorType colorType, String texture);

    IOverlay add(IOverlay.Type type, int color, String texture);

    IOverlay add(IOverlay.Type type, IOverlay.ColorType colorType, boolean glow, String texture);

    IOverlay add(IOverlay.Type type, int color, boolean glow, String texture);

    IOverlay removeOverlay(int index);

    IOverlay deleteOverlay(int index);

    boolean replaceOverlay(int index, IOverlay newOverlay);

    void replaceOverlay(IOverlay oldOverlay, IOverlay newOverlay);
}
