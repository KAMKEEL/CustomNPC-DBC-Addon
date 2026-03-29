package kamkeel.npcdbc.data.overlay;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.api.client.overlay.IOverlay;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.data.form.FacePartData;
import kamkeel.npcdbc.data.race.serial.DataCompound;
import kamkeel.npcdbc.data.race.serial.DataSerializable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class DisplayChain extends OverlayChain implements DataSerializable {

    public String stateKey = "base";
    public int presetCount = -1;
    private Supplier<String> textureDirSupplier = DEFAULT_TEXTURE_PATH;

    public DisplayChain() {
    }

    // ── Static factories ──────────────────────────────────────────────────────

    public static DisplayChain create(String stateKey) {
        DisplayChain chain = new DisplayChain();
        chain.stateKey = stateKey;
        return chain;
    }

    // ── Fluent setters ────────────────────────────────────────────────────────

    public DisplayChain stateKey(String stateKey) {
        this.stateKey = stateKey;
        return this;
    }

    public DisplayChain presetCount(int presetCount) {
        this.presetCount = presetCount;
        return this;
    }

    public DisplayChain textureDir(String textureDir) {
        this.textureDirSupplier = () -> textureDir;
        return this;
    }

    public DisplayChain textureDir(Supplier<String> supplier) {
        this.textureDirSupplier = supplier;
        return this;
    }

    public String resolveTextureDir() {
        return textureDirSupplier != null ? textureDirSupplier.get() : "";
    }

    public DisplayChain condition(Function<OverlayContext, Boolean> condition) {
        this.condition = condition;
        return this;
    }

    public DisplayChain name(String name) {
        this.name = name;
        return this;
    }

    // ── Preset normalization ──────────────────────────────────────────────────

    public void normalizeLayerPresets() {
        if (presetCount < 0) return;

        for (Overlay overlay : overlays) {
            if (!(overlay instanceof DisplayLayer)) continue;
            DisplayLayer dl = (DisplayLayer) overlay;
            List<kamkeel.npcdbc.api.Color> presets = new ArrayList<>(dl.getColorPresets());

            while (presets.size() < presetCount) {
                int fallback = dl.hasDefaultColor() ? dl.getDefaultColor() : 0xFFFFFF;
                presets.add(new kamkeel.npcdbc.api.Color(fallback));
            }
            while (presets.size() > presetCount) {
                presets.remove(presets.size() - 1);
            }

            dl.clearColorPresets();
            for (kamkeel.npcdbc.api.Color c : presets)
                dl.addColorPreset(c);
        }
    }

    // ── Override add() methods to return DisplayLayer ──────────────────────────

    @Override
    public DisplayLayer add(IOverlay.Type type) {
        DisplayLayer dl = new DisplayLayer();
        dl.type = type;
        dl.chain = this;
        this.overlays.add(dl);
        return dl;
    }

    @Override
    public DisplayLayer add(IOverlay.Type type, String texture) {
        return add(type).texture(texture);
    }

    @Override
    public DisplayLayer add(IOverlay.Type type, IOverlay.TextureFunction texture) {
        return (DisplayLayer) add(type).texture(texture);
    }

    @Override
    public DisplayLayer add(IOverlay.Type type, IOverlay.ColorType colorType) {
        return add(type).colorType(colorType);
    }

    @Override
    public DisplayLayer add(IOverlay.Type type, int color) {
        return add(type).colorType(IOverlay.ColorType.Custom).color(color);
    }

    @Override
    public DisplayLayer add(IOverlay.Type type, IOverlay.ColorType colorType, String texture) {
        return add(type).colorType(colorType).texture(texture);
    }

    @Override
    public DisplayLayer add(IOverlay.Type type, IOverlay.ColorType colorType, IOverlay.TextureFunction texture) {
        return (DisplayLayer) add(type).colorType(colorType).texture(texture);
    }

    @Override
    public DisplayLayer add(IOverlay.Type type, int color, String texture) {
        return add(type).colorType(IOverlay.ColorType.Custom).color(color).texture(texture);
    }

    @Override
    public DisplayLayer add(IOverlay.Type type, int color, IOverlay.TextureFunction texture) {
        return (DisplayLayer) add(type).colorType(IOverlay.ColorType.Custom).color(color).texture(texture);
    }

    @Override
    public DisplayLayer add(IOverlay.Type type, IOverlay.ColorType colorType, boolean glow, String texture) {
        return add(type).colorType(colorType).glow(glow).texture(texture);
    }

    @Override
    public DisplayLayer add(IOverlay.Type type, IOverlay.ColorType colorType, boolean glow, IOverlay.TextureFunction texture) {
        return (DisplayLayer) add(type).colorType(colorType).glow(glow).texture(texture);
    }

    @Override
    public DisplayLayer add(IOverlay.Type type, int color, boolean glow, String texture) {
        return add(type).colorType(IOverlay.ColorType.Custom).color(color).glow(glow).texture(texture);
    }

    @Override
    public DisplayLayer add(IOverlay.Type type, int color, boolean glow, IOverlay.TextureFunction texture) {
        return (DisplayLayer) add(type).colorType(IOverlay.ColorType.Custom).color(color).glow(glow).texture(texture);
    }

    // ── DisplayChain-specific add() overloads with key ────────────────────────

    public DisplayLayer add(IOverlay.Type type, String key, String displayName) {
        DisplayLayer dl = add(type);
        dl.key = key;
        dl.displayName = displayName;
        return dl;
    }

    public DisplayLayer add(IOverlay.Type type, IOverlay.ColorType colorType, String key, String displayName) {
        return add(type, key, displayName).colorType(colorType);
    }

    public DisplayLayer add(IOverlay.Type type, IOverlay.ColorType colorType, String key, String displayName, String texture) {
        return add(type, key, displayName).colorType(colorType).texture(texture);
    }

    // ── Layer lookup ──────────────────────────────────────────────────────────

    public DisplayLayer getLayer(String key) {
        if (key == null) return null;
        String lower = key.toLowerCase();
        for (Overlay overlay : overlays) {
            if (overlay instanceof DisplayLayer) {
                DisplayLayer dl = (DisplayLayer) overlay;
                if (dl.key != null && dl.key.toLowerCase().equals(lower))
                    return dl;
            }
        }
        return null;
    }

    // ── NBT serialization ─────────────────────────────────────────────────────

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setString("stateKey", stateKey);
        if (presetCount >= 0) compound.setInteger("presetCount", presetCount);
        String resolved = resolveTextureDir();
        if (!resolved.isEmpty()) compound.setString("textureDir", resolved);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        enabled = compound.getBoolean("hasOverlays");
        overlays.clear();
        NBTTagCompound rendering = compound.getCompoundTag("overlayData");

        int i = 0;
        while (rendering.hasKey("overlay" + i)) {
            NBTTagCompound overlayCompound = rendering.getCompoundTag("overlay" + i);

            int typeOrd = overlayCompound.hasKey("type", Constants.NBT.TAG_INT) ? overlayCompound.getInteger("type") : 0;
            DisplayLayer dl = new DisplayLayer();
            if (typeOrd < IOverlay.Type.values().length)
                dl.type = IOverlay.Type.values()[typeOrd];
            dl.chain = this;
            dl.readFromNBT(overlayCompound);
            overlays.add(dl);
            i++;
        }

        if (compound.hasKey("disabledParts")) {
            disabledParts.clear();
            FacePartData.Part[] values = FacePartData.Part.values();
            for (byte ordinal : compound.getByteArray("disabledParts")) {
                if (ordinal >= 0 && ordinal < values.length)
                    disabledParts.add(values[ordinal]);
            }
        }

        if (compound.hasKey("stateKey")) stateKey = compound.getString("stateKey");
        if (compound.hasKey("presetCount")) presetCount = compound.getInteger("presetCount");
        if (compound.hasKey("textureDir")) textureDir(compound.getString("textureDir"));
    }

    // ── DataSerializable ──────────────────────────────────────────────────────

    @Override
    public DataCompound serialize(DataCompound data) {
        data.putString("stateKey", stateKey);
        if (presetCount >= 0) data.putInt("presetCount", presetCount);
        String resolved = resolveTextureDir();
        if (!resolved.isEmpty()) data.putString("textureDir", resolved);

        for (int i = 0; i < overlays.size(); i++) {
            Overlay overlay = overlays.get(i);
            if (overlay instanceof DisplayLayer) {
                DataCompound layerData = data.child();
                ((DisplayLayer) overlay).serialize(layerData);
                data.put("layer" + i, layerData);
            }
        }

        return data;
    }

    @Override
    public void deserialize(DataCompound data) {
        if (data.has("stateKey")) stateKey = data.getString("stateKey", "base");
        if (data.has("presetCount")) presetCount = data.getInt("presetCount", -1);
        if (data.has("textureDir")) textureDir(data.getString("textureDir", ""));

        for (int i = 0; i < overlays.size(); i++) {
            Overlay overlay = overlays.get(i);
            if (overlay instanceof DisplayLayer && data.has("layer" + i)) {
                ((DisplayLayer) overlay).deserialize(data.get("layer" + i));
            }
        }
    }
    
      private static final Supplier<String> DEFAULT_TEXTURE_PATH = () ->
        CustomNpcPlusDBC.ID + ":textures/" +
        (ConfigDBCClient.EnableHDTextures ? "hd" : "sd") + "/bio_android/";
}
