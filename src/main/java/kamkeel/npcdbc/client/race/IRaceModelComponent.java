package kamkeel.npcdbc.client.race;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.data.overlay.DisplayLayer;
import kamkeel.npcdbc.data.overlay.OverlayContext;

@SideOnly(Side.CLIENT)
public interface IRaceModelComponent {

    void initialize(OverlayContext ctx);

    void render(OverlayContext ctx, DisplayLayer layer);

    default boolean appliesTo(OverlayContext ctx) {
        return true;
    }

    default boolean rendersInFirstPerson(OverlayContext ctx) {
        return false;
    }
}
