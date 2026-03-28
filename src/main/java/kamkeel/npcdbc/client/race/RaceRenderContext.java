package kamkeel.npcdbc.client.race;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.DBCRenderContext;
import kamkeel.npcdbc.constants.BodyLayer;
import kamkeel.npcdbc.data.race.display.DisplayComponent;
import kamkeel.npcdbc.data.race.display.DisplayLayer;
import kamkeel.npcdbc.data.race.display.RaceDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RaceRenderContext extends DBCRenderContext {
    
    public int bodyCM;
    public int bodyC1;
    public int bodyC2;
    public int bodyC3;
    public int eyeC1;
    public int eyeC2;

    public int skinType;
    public int state;
    public int bodyType;
    public boolean isFirstPersonArm;
    public int armAnimationId = -1;


    // ── Component access ───────────────────────────────────────────────────────

    /**
     * Returns a {@link ComponentColorView} for the named top-level component.
     * The view resolves each layer's color through the raw DBC fields.
     * <p>
     * Example:
     * <pre>{@code
     * int main = ctx.getComponent("body").getColor("bodycm");
     * int left = ctx.getComponent("face").getSubComponent("eyes").getColor("lefteye");
     * }</pre>
     *
     * @param componentId top-level component id (e.g. {@link RaceDisplay#COMPONENT_BODY})
     * @return a view over the component, or an empty no-op view if absent
     */
    public ComponentColorView getComponent(String componentId) {
        RaceDisplay display = race != null ? race.display : null;
        DisplayComponent component = display != null ? display.getComponent(componentId) : null;
        return new ComponentColorView(component);
    }

    // ── Color resolution (internal) ───────────────────────────────────────────

    /**
     * Resolves the color for a given layer id from the raw DBC fields.
     * Returns {@code 0} for unrecognised layer ids.
     */
    int resolveColor(String layerId) {
        return getRawColor(layerId);
    }

    /**
     * Returns the raw DBC field value for the given layer id
     */
    public int getRawColor(String layerId) {
        switch (layerId.toLowerCase()) {
            case BodyLayer.BODY_CM: return bodyCM;
            case BodyLayer.BODY_C1: return bodyC1;
            case BodyLayer.BODY_C2: return bodyC2;
            case BodyLayer.BODY_C3: return bodyC3;
            case BodyLayer.EYES:
            case BodyLayer.EYE_LEFT: return eyeC1;
            case BodyLayer.EYE_RIGHT: return eyeC2;
            default: return 0;
        }
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    public void bindTexture(ResourceLocation loc) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(loc);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ComponentColorView
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * A thin view over a {@link DisplayComponent} that resolves layer colors
     * through the active BodyState override chain and the raw DBC fields.
     * <p>
     * Obtain via {@link RaceRenderContext#getComponent(String)}.
     */
    public final class ComponentColorView {

        private final DisplayComponent component;

        ComponentColorView(DisplayComponent component) {
            this.component = component;
        }

        /**
         * Returns the resolved color for the given layer id in this component.
         * Checks that the layer exists in this component before resolving.
         * Returns {@code 0} if the component is absent or the layer is not declared.
         *
         * @param layerId one of the {@link RaceDisplay} LAYER_* constants
         */
        public int getColor(String layerId) {
            if (component == null || !component.hasLayer(layerId)) return 0;
            return resolveColor(layerId);
        }

        /**
         * Returns whether this component declares the given layer id.
         * Always {@code false} for a missing/null component.
         */
        public boolean hasLayer(String layerId) {
            return component != null && component.hasLayer(layerId);
        }

        /**
         * Returns the underlying {@link DisplayLayer} for the given id,
         * or {@code null} if absent.
         */
        public DisplayLayer getLayer(String layerId) {
            return component != null ? component.getLayer(layerId) : null;
        }

        /**
         * Returns a view over the sub-component of this component.
         * Example: {@code ctx.getComponent("face").getSubComponent().getColor("lefteye")}
         *
         * @return a view over the sub-component, or an empty view if none is set
         */
        public ComponentColorView getSubComponent() {
            DisplayComponent sub = component != null ? component.getSubComponent() : null;
            return new ComponentColorView(sub);
        }

        /**
         * Returns a view over the sub-component by id.
         * Checks that the sub-component's id matches before returning it.
         * Returns an empty view if absent or id does not match.
         *
         * @param subComponentId the expected sub-component id
         */
        public ComponentColorView getSubComponent(String subComponentId) {
            if (component == null) return new ComponentColorView(null);
            DisplayComponent sub = component.getSubComponent();
            if (sub == null || !sub.id.equals(subComponentId.toLowerCase())) {
                return new ComponentColorView(null);
            }
            return new ComponentColorView(sub);
        }

        /** Returns the underlying {@link DisplayComponent}, or {@code null} if absent. */
        public DisplayComponent getDisplayComponent() { return component; }
    }
}
