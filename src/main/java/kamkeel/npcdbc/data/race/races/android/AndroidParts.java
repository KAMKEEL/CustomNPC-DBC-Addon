package kamkeel.npcdbc.data.race.races.android;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.LocalizationHelper;
import kamkeel.npcdbc.api.client.overlay.IOverlay;
import kamkeel.npcdbc.data.overlay.OverlayChain;

public class AndroidParts {

    public static final AndroidPartData RED_RIBBON_CORE = create("rr_core", AndroidPartSlot.CORE)
        .build();

    public static final AndroidPartData KI_RING = create("ki_ring", AndroidPartSlot.BOTH_ARMS)
        .overlays(AndroidOverlays.KI_RING)
        .build();

    private static AndroidPartData.Builder create(String name, AndroidPartSlot slot) {
        return AndroidPartData.create(CustomNpcPlusDBC.ID, name, slot)
            .unlocalizedName(LocalizationHelper.ITEM_PREFIX + name);
    }
}
