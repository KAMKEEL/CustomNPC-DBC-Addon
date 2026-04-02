package kamkeel.npcdbc.data.race.races.android;

import kamkeel.npcdbc.data.overlay.OverlayChain;
import net.minecraft.entity.player.EntityPlayer;

import java.util.function.BiConsumer;

public class AndroidPartData {

    private final String id; // namespace:name
    private final AndroidPartSlot slot;
    private final String unlocalizedName;
    private final String textureDir;
    private final OverlayChain overlays;

    private final BiConsumer<AndroidPartData, EntityPlayer> onEquip;
    private final BiConsumer<AndroidPartData, EntityPlayer> onUnequip;
    private final BiConsumer<AndroidPartData, EntityPlayer> onTick;

    private AndroidPartData(Builder builder) {
        this.id = builder.id;
        this.slot = builder.slot;
        this.unlocalizedName = builder.unlocalizedName;
        this.textureDir = builder.textureDir;
        this.overlays = builder.overlays;
        this.onEquip = builder.onEquip;
        this.onUnequip = builder.onUnequip;
        this.onTick = builder.onTick;
    }

    // ──────────────────── Getters ────────────────────

    public String getId() {
        return id;
    }

    public String getNamespace() {
        return id.split(":")[0];
    }

    public String getName() {
        return id.split(":")[1];
    }

    public String getUnlocalizedName() {
        return unlocalizedName;
    }

    public String getTextureDir() {
        return textureDir;
    }

    public String resolveTextureDir() {
        return getNamespace() + ":" + textureDir + getName();
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

    // ──────────────────── Consumers ────────────────────

    public void onEquip(EntityPlayer player) {
        if (onEquip != null) onEquip.accept(this, player);
    }

    public void onUnequip(EntityPlayer player) {
        if (onUnequip != null) onUnequip.accept(this, player);
    }

    public void onTick(EntityPlayer player) {
        if (onTick != null) onTick.accept(this, player);
    }

    // ──────────────────── Builder ────────────────────

    public static Builder create(String namespace, String name, AndroidPartSlot slot) {
        return new Builder(namespace, name, slot);
    }

    public static class Builder {

        private final String id;
        private final AndroidPartSlot slot;
        private String unlocalizedName;
        private String textureDir = "androidparts/";
        private OverlayChain overlays = null;

        private BiConsumer<AndroidPartData, EntityPlayer> onEquip = null;
        private BiConsumer<AndroidPartData, EntityPlayer> onUnequip = null;
        private BiConsumer<AndroidPartData, EntityPlayer> onTick = null;

        private Builder(String namespace, String name, AndroidPartSlot slot) {
            if (namespace == null || namespace.isEmpty())
                throw new IllegalArgumentException("AndroidPart namespace must not be null or empty");
            if (name == null || name.isEmpty())
                throw new IllegalArgumentException("AndroidPart name must not be null or empty");
            if (slot == null)
                throw new IllegalArgumentException("AndroidPart slot must not be null: " + name);

            this.id = namespace + ":" + name;
            this.slot = slot;
            this.unlocalizedName = "item.android_part." + name.toLowerCase();
        }

        public Builder unlocalizedName(String unlocalizedName) {
            this.unlocalizedName = unlocalizedName;
            return this;
        }

        public Builder textureDir(String dir) {
            this.textureDir = dir;
            return this;
        }

        public Builder overlays(OverlayChain overlays) {
            this.overlays = overlays;
            return this;
        }

        public Builder onEquip(BiConsumer<AndroidPartData, EntityPlayer> func) {
            this.onEquip = func;
            return this;
        }

        public Builder onUnequip(BiConsumer<AndroidPartData, EntityPlayer> func) {
            this.onUnequip = func;
            return this;
        }

        public Builder onTick(BiConsumer<AndroidPartData, EntityPlayer> func) {
            this.onTick = func;
            return this;
        }

        public AndroidPartData build() {
            return new AndroidPartData(this);
        }
    }
}
