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

    /**
     * Renders the first-person arm for this custom race.
     * Called from {@code RenderPlayerJBRA#renderFirstPersonArm} when the
     * player has this custom race active.
     *
     * @return {@code true} if this renderer handled the arm render pass
     *         and default DBC arm rendering should be skipped.
     */
    default boolean renderArm(RaceRenderContext ctx) {
        return false;
    }
}
