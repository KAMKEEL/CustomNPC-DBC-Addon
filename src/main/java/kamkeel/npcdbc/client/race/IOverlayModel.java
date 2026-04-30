package kamkeel.npcdbc.client.race;

import kamkeel.npcdbc.data.overlay.OverlayContext;

/**
 * Contract for overlay model rendering.
 * <p>
 * Not annotated {@code @SideOnly(Side.CLIENT)} so that Janino script types
 * whose {@code Functions} class implements this interface can be loaded on
 * the dedicated server without triggering a missing-class crash.  All
 * concrete implementations and call-sites remain client-only; the interface
 * itself only references the common-side {@link OverlayContext}.
 */
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

    default boolean renderDBCHair(OverlayContext ctx) {
        return true;
    }
}
