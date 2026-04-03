package kamkeel.npcdbc.data.race.races.android;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.api.client.overlay.IOverlay;
import kamkeel.npcdbc.client.render.DBCOverlays.TexturePath;
import kamkeel.npcdbc.data.overlay.OverlayChain;

import static kamkeel.npcdbc.api.client.overlay.IOverlay.Type.ALL;
import static kamkeel.npcdbc.api.client.overlay.IOverlay.Type.Chest;
import static kamkeel.npcdbc.api.client.overlay.IOverlay.Type.EyeWhite;
import static kamkeel.npcdbc.api.client.overlay.IOverlay.Type.Eyebrows;
import static kamkeel.npcdbc.api.client.overlay.IOverlay.Type.Face;
import static kamkeel.npcdbc.api.client.overlay.IOverlay.Type.LeftEye;
import static kamkeel.npcdbc.api.client.overlay.IOverlay.Type.Mouth;
import static kamkeel.npcdbc.api.client.overlay.IOverlay.Type.RightEye;
import static kamkeel.npcdbc.client.model.ModelDBC.HDDir;
import static kamkeel.npcdbc.client.model.ModelDBC.path;
import static java.lang.String.format;

public class AndroidOverlays {

    public static final OverlayChain KI_RING = create("ki_ring");

    static {
        KI_RING.add(ALL, 0xFFFFFF, path("android/overlays/ki_ring.png"));
    }


    private static OverlayChain create(String name) {
        return OverlayChain.create(CustomNpcPlusDBC.ID + ":" + name);
    }
}
