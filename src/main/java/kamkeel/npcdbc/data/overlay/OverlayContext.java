package kamkeel.npcdbc.data.overlay;

import kamkeel.npcdbc.api.Color;
import kamkeel.npcdbc.api.client.overlay.IOverlay;
import kamkeel.npcdbc.api.client.overlay.IOverlayContext;
import kamkeel.npcdbc.client.DBCRenderContext;

import java.util.List;
import java.util.Set;

import static kamkeel.npcdbc.api.client.overlay.IOverlay.ColorType.Custom;

public class OverlayContext extends DBCRenderContext implements IOverlayContext {

    public Overlay overlay;
    public OverlayChain chain;
    public Set<Overlay.Type> disabledTypes;
    public OverlayChain exceptFor;
    public String texture;
    public Color color;

    @Override
    public boolean typeDisabled(IOverlay.Type type) {
        return chain != exceptFor && disabledTypes != null && disabledTypes.contains(type);
    }

    public void cacheOverlays(List<OverlayChain> overlays) {
        if (isNPC)
            display.cachedOverlays = overlays;
        else
            dbcData.cachedOverlays = overlays;
    }

    public Color color(IOverlay.ColorType type) {
        return color(type, overlay);
    }

    public Color color(IOverlay.ColorType type, IOverlay iOverlay) {
        Overlay o = (Overlay) iOverlay;
        int col = type == Custom ? o.color : color(type.name());
        return new Color(col, o.alpha);
    }

    @Override
    public IOverlay getOverlay() {
        return overlay;
    }
}
