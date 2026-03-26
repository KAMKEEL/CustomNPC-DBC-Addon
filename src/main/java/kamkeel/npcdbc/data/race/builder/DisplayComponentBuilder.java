package kamkeel.npcdbc.data.race.builder;

import kamkeel.npcdbc.api.Color;
import kamkeel.npcdbc.data.race.display.DisplayComponent;
import kamkeel.npcdbc.data.race.display.DisplayLayer;

/**
 * Standalone fluent builder for a {@link DisplayComponent}.
 * <p>
 * Build components separately and add them to a race
 *
 */
public class DisplayComponentBuilder {

    private final DisplayComponent component;

    private DisplayComponentBuilder(String id, String displayName) {
        this.component = new DisplayComponent(id, displayName);
    }

    public static DisplayComponentBuilder create(String id) {
        return new DisplayComponentBuilder(id, id);
    }

    public static DisplayComponentBuilder create(String id, String displayName) {
        return new DisplayComponentBuilder(id, displayName);
    }

    // ── Layers ─────────────────────────────────────────────────────────────────

    public LayerBuilder layer(String layerId) {
        return layer(layerId, layerId);
    }

    public LayerBuilder layer(String layerId, String layerDisplayName) {
        DisplayLayer existing = component.getLayer(layerId);
        if (existing != null) return new LayerBuilder(this, existing);
        DisplayLayer newLayer = new DisplayLayer(layerId, layerDisplayName);
        component.addLayer(newLayer);
        return new LayerBuilder(this, newLayer);
    }

    // ── Sub-component ──────────────────────────────────────────────────────────

    public SubComponentBuilder subComponent(String id) {
        return subComponent(id, id);
    }

    public SubComponentBuilder subComponent(String id, String displayName) {
        DisplayComponent existing = component.getSubComponent();
        if (existing != null && existing.id.equals(id.toLowerCase()))
            return new SubComponentBuilder(this, existing);
        DisplayComponent newSub = new DisplayComponent(id, displayName);
        component.setSubComponent(newSub);
        return new SubComponentBuilder(this, newSub);
    }

    // ── Preset management ──────────────────────────────────────────────────────

    public DisplayComponentBuilder presetCount(int count) {
        component.setPresetCount(count);
        return this;
    }

    public DisplayComponentBuilder normalizePresets() {
        component.normalizeLayerPresets();
        return this;
    }

    // ── Finalise ───────────────────────────────────────────────────────────────

    public DisplayComponent build() {
        return component;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // LayerBuilder
    // ══════════════════════════════════════════════════════════════════════════

    public static class LayerBuilder {

        private final DisplayComponentBuilder componentParent;
        final DisplayLayer layer;

        LayerBuilder(DisplayComponentBuilder componentParent, DisplayLayer layer) {
            this.componentParent = componentParent;
            this.layer = layer;
        }

        // package-private for SubComponentLayerBuilder
        LayerBuilder(DisplayLayer layer) {
            this.componentParent = null;
            this.layer = layer;
        }

        public LayerBuilder textureVariant(String texturePath) {
            layer.addTextureVariant(texturePath);
            return this;
        }

        public LayerBuilder textureOverride(String texturePath) {
            layer.setTextureOverride(texturePath);
            return this;
        }

        public LayerBuilder colorOverride(int color) {
            layer.setColorOverride(color);
            return this;
        }

        public LayerBuilder colorOverride(Color color) {
            layer.setColorOverride(color);
            return this;
        }

        public LayerBuilder defaultColor(int color) {
            layer.setDefaultColor(color);
            return this;
        }

        public LayerBuilder addColorPreset(int color) {
            layer.addColorPreset(color);
            return this;
        }

        public DisplayComponentBuilder and() {
            return componentParent;
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SubComponentBuilder
    // ══════════════════════════════════════════════════════════════════════════

    public static class SubComponentBuilder {

        private final DisplayComponentBuilder componentParent;
        private final DisplayComponent subComponent;

        SubComponentBuilder(DisplayComponentBuilder componentParent, DisplayComponent subComponent) {
            this.componentParent = componentParent;
            this.subComponent = subComponent;
        }

        public SubComponentLayerBuilder layer(String layerId) {
            return layer(layerId, layerId);
        }

        public SubComponentLayerBuilder layer(String layerId, String layerDisplayName) {
            DisplayLayer existing = subComponent.getLayer(layerId);
            if (existing != null) return new SubComponentLayerBuilder(this, existing);
            DisplayLayer newLayer = new DisplayLayer(layerId, layerDisplayName);
            subComponent.addLayer(newLayer);
            return new SubComponentLayerBuilder(this, newLayer);
        }

        public DisplayComponentBuilder and() {
            return componentParent;
        }
    }

    public static class SubComponentLayerBuilder extends LayerBuilder {

        private final SubComponentBuilder subParent;

        SubComponentLayerBuilder(SubComponentBuilder subParent, DisplayLayer layer) {
            super(layer);
            this.subParent = subParent;
        }

        @Override public SubComponentLayerBuilder textureVariant(String p)  { layer.addTextureVariant(p);  return this; }
        @Override public SubComponentLayerBuilder textureOverride(String p)  { layer.setTextureOverride(p); return this; }
        @Override public SubComponentLayerBuilder colorOverride(int c)       { layer.setColorOverride(c);   return this; }
        @Override public SubComponentLayerBuilder colorOverride(Color c)     { layer.setColorOverride(c);   return this; }
        @Override public SubComponentLayerBuilder defaultColor(int c)        { layer.setDefaultColor(c);    return this; }
        @Override public SubComponentLayerBuilder addColorPreset(int c)      { layer.addColorPreset(c);     return this; }

        public SubComponentBuilder andSub() { return subParent; }
    }
}
