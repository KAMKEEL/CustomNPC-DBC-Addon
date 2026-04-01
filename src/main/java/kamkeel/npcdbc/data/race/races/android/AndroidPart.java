package kamkeel.npcdbc.data.race.races.android;

import kamkeel.npcdbc.data.overlay.OverlayChain;

public class AndroidPart {

    private final String id;
    private final AndroidPartSlot slot;
    private OverlayChain overlays;

    private AndroidPart(Builder builder) {
        this.id = builder.id;
        this.slot = builder.slot;
        this.overlays = builder.overlays;
    }

    // ──────────────────── Getters ────────────────────

    public String getId() {
        return id;
    }

    public AndroidPartSlot getSlot() {
        return slot;
    }

    public boolean fitsSlot(AndroidPartSlot slot) {
        return this.slot.fitsSlot(slot);
    }

    public boolean hasOverlays() {
        return overlays != null;
    }

    public OverlayChain getOverlays() {
        return overlays;
    }

    // ──────────────────── Builder ────────────────────

    public static Builder create(String id, AndroidPartSlot slot) {
        return new Builder(id, slot);
    }

    public static class Builder {

        private final String id;
        private final AndroidPartSlot slot;
        private OverlayChain overlays = null;

        private Builder(String id, AndroidPartSlot slot) {
            if (id == null || id.isEmpty())
                throw new IllegalArgumentException("AndroidPart id must not be null or empty");
            if (slot == null)
                throw new IllegalArgumentException("AndroidPart must have a slot: " + id);

            this.id = id;
            this.slot = slot;
        }

        public Builder overlays(OverlayChain overlays) {
            this.overlays = overlays;
            return this;
        }

        public AndroidPart build() {
            return new AndroidPart(this);
        }
    }
}
