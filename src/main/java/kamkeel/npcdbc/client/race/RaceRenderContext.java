package kamkeel.npcdbc.client.race;

import JinRyuu.JBRA.ModelBipedDBC;
import JinRyuu.JBRA.RenderPlayerJBRA;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.api.Color;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.race.display.BodyState;
import kamkeel.npcdbc.data.race.display.ColorLayer;
import kamkeel.npcdbc.data.race.display.ColorSlot;
import kamkeel.npcdbc.data.race.display.RaceDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RaceRenderContext {

    public final Entity entity;
    public final double x;
    public final double y;
    public final double z;
    public final float yaw;
    public final float partialTicks;

    public final RenderPlayerJBRA renderer;
    public final ModelBipedDBC model;
    public final DBCData dbcData;
    public final Race race;

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

    private BodyState activeBodyState;

    public RaceRenderContext(Entity entity, double x, double y, double z,
                             float yaw, float partialTicks,
                             RenderPlayerJBRA renderer, ModelBipedDBC model,
                             DBCData dbcData, Race race) {
        this.entity = entity;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.partialTicks = partialTicks;
        this.renderer = renderer;
        this.model = model;
        this.dbcData = dbcData;
        this.race = race;
    }

    // ── BodyState ─────────────────────────────────────────────────────────────

//    public void setActiveBodyState(BodyState bodyState) {
//        this.activeBodyState = bodyState;
//    }
//
//    public void setActiveBodyState(String stateId) {
//        RaceDisplay display = race != null ? race.display : null;
//        this.activeBodyState = display != null ? display.getBodyState(stateId) : null;
//    }
//
//    public BodyState getActiveBodyState() { return activeBodyState; }

    // ── Layer access ──────────────────────────────────────────────────────────

    /**
     * Returns a {@link LayerColorView} for the named top-level layer, respecting
     * the active {@link BodyState}. The view resolves each slot's color through
     * the BodyState override chain → raw DBC field.
     * <p>
     * Example:
     * <pre>{@code
     * int main = ctx.getLayer("body").getColor("bodycm");
     * int left = ctx.getLayer("face").getSubLayer("eyes").getColor("lefteye");
     * }</pre>
     *
     * @param layerId top-level layer id (e.g. {@link ColorLayer#BODY})
     * @return a view over the layer, or an empty no-op view if the layer is absent
     */
    public LayerColorView getLayer(String layerId) {
        RaceDisplay display = race != null ? race.display : null;
        ColorLayer layer = null;

        if (display != null) {
            // State overrides first, then parent display
            if (activeBodyState != null) {
                layer = activeBodyState.resolveLayer(layerId, display);
            } else {
                layer = display.getLayer(layerId);
            }
        }
        return new LayerColorView(layer, display);
    }

    // ── Color resolution (internal) ───────────────────────────────────────────

    /**
     * Resolves the color for a given slot id.
     * <ol>
     *   <li>Active {@link BodyState} color override, if any.</li>
     *   <li>Raw DBC field mapped to the slot.</li>
     * </ol>
     * Returns {@code 0} for unrecognised slots.
     */
    int resolveColor(String slotId) {
        if (activeBodyState != null && race != null) {
            Color override = activeBodyState.resolveColorOverride(slotId, race.display);
            if (override != null) return override.color;
        }
        return getRawColor(slotId);
    }

    /**
     * Returns the raw DBC field value for the given slot id, bypassing any
     * active {@link BodyState} overrides.
     */
    public int getRawColor(String slotId) {
        switch (slotId.toLowerCase()) {
            case ColorSlot.BODY_CM:   return bodyCM;
            case ColorSlot.BODY_C1:   return bodyC1;
            case ColorSlot.BODY_C2:   return bodyC2;
            case ColorSlot.BODY_C3:   return bodyC3;
            case ColorSlot.EYES:
            case ColorSlot.LEFT_EYE:  return eyeC1;
            case ColorSlot.RIGHT_EYE: return eyeC2;
            default:                  return 0;
        }
    }

    public boolean hasColorOverride(String slotId) {
        return activeBodyState != null && activeBodyState.hasColorOverride(slotId);
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    public void bindTexture(ResourceLocation loc) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(loc);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // LayerColorView
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * A thin view over a {@link ColorLayer} that resolves slot colors through
     * the active BodyState override chain and the raw DBC fields.
     * <p>
     * Obtain via {@link RaceRenderContext#getLayer(String)}.
     */
    public final class LayerColorView {

        private final ColorLayer  layer;
        private final RaceDisplay display;

        LayerColorView(ColorLayer layer, RaceDisplay display) {
            this.layer   = layer;
            this.display = display;
        }

        /**
         * Returns the resolved color for the given slot id in this layer.
         * Resolution: BodyState override → raw DBC field → 0.
         *
         * @param slotId one of the {@link ColorSlot} constants
         */
        public int getColor(String slotId) {
            if (layer != null && !layer.hasSlot(slotId)) return 0;
            return resolveColor(slotId);
        }

        /**
         * Returns whether this layer declares the given slot id.
         * Always {@code false} for a missing/null layer.
         */
        public boolean hasSlot(String slotId) {
            return layer != null && layer.hasSlot(slotId);
        }

        /**
         * Returns a view over a direct sub-layer of this layer.
         * Example: {@code ctx.getLayer("face").getSubLayer("eyes").getColor("lefteye")}
         *
         * @param subLayerId the child layer id
         * @return a view over the sub-layer, or an empty view if absent
         */
        public LayerColorView getSubLayer(String subLayerId) {
            ColorLayer sub = layer != null ? layer.getSubLayer(subLayerId) : null;
            return new LayerColorView(sub, display);
        }

        /** Returns the underlying {@link ColorLayer}, or {@code null} if absent. */
        public ColorLayer getColorLayer() { return layer; }
    }
}
