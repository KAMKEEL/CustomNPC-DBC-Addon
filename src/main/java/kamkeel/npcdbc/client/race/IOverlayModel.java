package kamkeel.npcdbc.client.race;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.data.overlay.OverlayContext;

@SideOnly(Side.CLIENT)
public interface IOverlayModel {
    
    float SCALE = 0.0625f;

    void initialize(OverlayContext ctx);

    void render(OverlayContext ctx);

    default boolean appliesTo(OverlayContext ctx) {
        return true;
    }

    default boolean rendersInFirstPerson(OverlayContext ctx) {
        return false;
    }
}
