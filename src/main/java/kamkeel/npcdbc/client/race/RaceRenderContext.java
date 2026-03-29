package kamkeel.npcdbc.client.race;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.DBCRenderContext;
import kamkeel.npcdbc.constants.BodyLayer;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.data.race.display.RaceDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.entity.EntityCustomNpc;

import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
public class RaceRenderContext extends DBCRenderContext {

    // ══════════════════════════════════════════════════════════════════════════
    // Creators
    // ══════════════════════════════════════════════════════════════════════════

    public static RaceRenderContext from(DBCDisplay display) {
        RaceRenderContext ctx = new RaceRenderContext();
        ctx.isNPC = true;
        ctx.npc = (EntityCustomNpc) display.npc;
        ctx.display = display;
        return ctx;
    }

    public static RaceRenderContext from(DBCData dbcData) {
        RaceRenderContext ctx = new RaceRenderContext();
        ctx.isNPC = false;
        ctx.player = dbcData.player;
        ctx.dbcData = dbcData;
        return ctx;
    }

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
    public ComponentColorView getComponent(String stateKey) {
        RaceDisplay display = race != null ? race.display : null;
        List<kamkeel.npcdbc.data.overlay.DisplayChain> foundChains =
            display != null ? display.getChains(stateKey) : Collections.emptyList();
        return new ComponentColorView(foundChains);
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
            case BodyLayer.BODY_CM: return bodyCM();
            case BodyLayer.BODY_C1: return bodyC1();
            case BodyLayer.BODY_C2: return bodyC2();
            case BodyLayer.BODY_C3: return bodyC3();
            case BodyLayer.EYES:
            case BodyLayer.EYE_LEFT: return eyeC1();
            case BodyLayer.EYE_RIGHT: return eyeC2();
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
     * A thin view over display chains that resolves layer colors
     * through the active BodyState override chain and the raw DBC fields.
     * <p>
     * Obtain via {@link RaceRenderContext#getComponent(String)}.
     */
    public final class ComponentColorView {

        private final List<kamkeel.npcdbc.data.overlay.DisplayChain> chains;

        ComponentColorView(List<kamkeel.npcdbc.data.overlay.DisplayChain> chains) {
            this.chains = chains != null ? chains : Collections.emptyList();
        }

        private kamkeel.npcdbc.data.overlay.DisplayLayer findLayer(String slotId) {
            String key = slotId.toLowerCase();
            for (kamkeel.npcdbc.data.overlay.DisplayChain chain : chains) {
                kamkeel.npcdbc.data.overlay.DisplayLayer dl = chain.getLayer(key);
                if (dl != null) return dl;
            }
            return null;
        }

        public int getColor(String layerId) {
            kamkeel.npcdbc.data.overlay.DisplayLayer layer = findLayer(layerId);
            if (layer == null) return 0;
            return resolveColor(layerId);
        }

        public boolean hasLayer(String layerId) {
            return findLayer(layerId) != null;
        }

        public kamkeel.npcdbc.data.overlay.DisplayLayer getLayer(String layerId) {
            return findLayer(layerId);
        }

        public ComponentColorView getSubComponent() {
            return new ComponentColorView(Collections.emptyList());
        }

        public ComponentColorView getSubComponent(String subComponentId) {
            return new ComponentColorView(Collections.emptyList());
        }
    }
}
