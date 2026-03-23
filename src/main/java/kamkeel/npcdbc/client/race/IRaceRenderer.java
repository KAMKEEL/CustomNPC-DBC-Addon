package kamkeel.npcdbc.client.race;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IRaceRenderer {
    /**
     * @return {@code true} if this renderer handled the race render pass
     *         and default DBC rendering for that hook should be skipped.
     */
    boolean render(RaceRenderContext ctx);
}
