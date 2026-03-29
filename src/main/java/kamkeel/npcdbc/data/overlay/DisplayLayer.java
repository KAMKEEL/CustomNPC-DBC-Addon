package kamkeel.npcdbc.data.overlay;

import kamkeel.npcdbc.api.Color;
import kamkeel.npcdbc.client.race.RaceRenderContext;
import kamkeel.npcdbc.data.race.serial.DataCompound;
import kamkeel.npcdbc.data.race.serial.DataSerializable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class DisplayLayer extends Overlay implements DataSerializable {

    public String displayName = null;
    public boolean fixedColor = false;
    public boolean fixedTexture = false;
    private final List<String> textureVariants = new ArrayList<>();
    private final List<Color> colorPresets = new ArrayList<>();

    public DisplayLayer() {
    }

    // ── Fluent setters (DisplayLayer-specific) ────────────────────────────────

    public DisplayLayer displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public DisplayLayer fixedColor(boolean fixedColor) {
        this.fixedColor = fixedColor;
        return this;
    }

    public DisplayLayer fixedTexture(boolean fixedTexture) {
        this.fixedTexture = fixedTexture;
        return this;
    }

    public DisplayLayer defaultColor(int c) {
        this.color = c;
        return this;
    }

    public DisplayLayer defaultColor(Color c) {
        this.color = c.color;
        return this;
    }

    public DisplayLayer addTextureVariant(String variant) {
        textureVariants.add(variant);
        return this;
    }

    public DisplayLayer addColorPreset(int color) {
        colorPresets.add(new Color(color));
        return this;
    }

    public DisplayLayer addColorPreset(Color color) {
        colorPresets.add(color);
        return this;
    }

    public DisplayLayer truncateColorPresets(int count) {
        while (colorPresets.size() > count)
            colorPresets.remove(colorPresets.size() - 1);
        return this;
    }

    // ── Covariant overrides of Overlay fluent methods ─────────────────────────

    @Override
    public DisplayLayer texture(String texture) {
        super.texture(texture);
        return this;
    }

    @Override
    public DisplayLayer color(int color) {
        super.color(color);
        return this;
    }

    @Override
    public DisplayLayer color(int color, float alpha) {
        super.color(color, alpha);
        return this;
    }

    @Override
    public DisplayLayer colorType(ColorType type) {
        super.colorType(type);
        return this;
    }

    @Override
    public DisplayLayer glow(boolean glow) {
        super.glow(glow);
        return this;
    }

    @Override
    public DisplayLayer enabled(boolean enabled) {
        super.enabled(enabled);
        return this;
    }

    @Override
    public DisplayLayer alpha(float alpha) {
        super.alpha(alpha);
        return this;
    }

    @Override
    public DisplayLayer condition(Function<OverlayContext, Boolean> condition) {
        super.condition(condition);
        return this;
    }

    @Override
    public DisplayLayer modelKey(String modelKey) {
        super.modelKey(modelKey);
        return this;
    }

    @Override
    public DisplayLayer key(String key) {
        super.key(key);
        return this;
    }

    // ── and() — returns parent DisplayChain ───────────────────────────────────

    public DisplayChain and() {
        return (DisplayChain) chain;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public boolean isFixedColor() {
        return fixedColor;
    }

    public boolean isFixedTexture() {
        return fixedTexture;
    }

    public int getDefaultColor() {
        return this.color;
    }

    public boolean hasDefaultColor() {
        return true;
    }

    public List<String> getTextureVariants() {
        return Collections.unmodifiableList(textureVariants);
    }

    public int getTextureVariantCount() {
        return textureVariants.size();
    }

    public List<Color> getColorPresets() {
        return Collections.unmodifiableList(colorPresets);
    }

    public int getColorPresetCount() {
        return colorPresets.size();
    }

    public boolean hasColorPresets() {
        return !colorPresets.isEmpty();
    }

    public void clearColorPresets() {
        colorPresets.clear();
    }

    public void clearTextureVariants() {
        textureVariants.clear();
    }

    // ── Color / texture resolution ────────────────────────────────────────────

    public int resolveColor() {
        return this.color;
    }

    public int resolveColor(RaceRenderContext ctx) {
        return this.color;
    }

    public String resolveTexture(int variantIndex) {
        if (fixedTexture || textureVariants.isEmpty()) return this.texture;
        if (variantIndex >= 0 && variantIndex < textureVariants.size())
            return textureVariants.get(variantIndex);
        return this.texture;
    }

    @Override
    public String getTexture() {
        if (chain instanceof DisplayChain) {
            String dir = ((DisplayChain) chain).resolveTextureDir();
            if (!dir.isEmpty() && !texture.isEmpty())
                return dir + texture;
        }
        return texture;
    }

    // ── NBT serialization ─────────────────────────────────────────────────────

    @Override
    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = super.writeToNBT();

        if (displayName != null) compound.setString("displayName", displayName);
        compound.setBoolean("fixedColor", fixedColor);
        compound.setBoolean("fixedTexture", fixedTexture);

        if (!textureVariants.isEmpty()) {
            NBTTagList variantList = new NBTTagList();
            for (String v : textureVariants)
                variantList.appendTag(new NBTTagString(v));
            compound.setTag("textureVariants", variantList);
        }

        if (!colorPresets.isEmpty()) {
            int[] presetColors = new int[colorPresets.size()];
            for (int i = 0; i < colorPresets.size(); i++)
                presetColors[i] = colorPresets.get(i).color;
            compound.setIntArray("colorPresets", presetColors);
        }

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (compound.hasKey("slotId")) key = compound.getString("slotId");
        displayName = compound.hasKey("displayName") ? compound.getString("displayName") : null;
        fixedColor = compound.getBoolean("fixedColor");
        fixedTexture = compound.getBoolean("fixedTexture");

        textureVariants.clear();
        if (compound.hasKey("textureVariants")) {
            NBTTagList variantList = compound.getTagList("textureVariants", 8);
            for (int i = 0; i < variantList.tagCount(); i++)
                textureVariants.add(variantList.getStringTagAt(i));
        }

        colorPresets.clear();
        if (compound.hasKey("colorPresets")) {
            for (int c : compound.getIntArray("colorPresets"))
                colorPresets.add(new Color(c));
        }
    }

    // ── DataSerializable ──────────────────────────────────────────────────────

    @Override
    public DataCompound serialize(DataCompound data) {
        if (key != null) data.putString("key", key);
        if (displayName != null) data.putString("displayName", displayName);
        data.putString("defaultColor", Color.getColor(this.color));
        data.putBoolean("fixedColor", fixedColor);
        data.putBoolean("fixedTexture", fixedTexture);

        if (!textureVariants.isEmpty()) {
            data.putStringList("textureVariants", textureVariants);
        }

        if (!colorPresets.isEmpty()) {
            List<String> hexList = new ArrayList<>();
            for (Color c : colorPresets)
                hexList.add(Color.getColor(c.color));
            data.putStringList("colorPresets", hexList);
        }

        return data;
    }

    @Override
    public void deserialize(DataCompound data) {
        if (data.has("slotId")) key = data.getString("slotId", null);
        if (data.has("displayName")) displayName = data.getString("displayName", null);

        if (data.has("defaultColor")) {
            String hex = data.getString("defaultColor", "ffffff");
            try {
                this.color = Integer.parseInt(hex, 16);
            } catch (NumberFormatException ignored) {
                this.color = 0xffffff;
            }
        }

        if (data.has("fixedColor")) fixedColor = data.getBoolean("fixedColor", false);
        if (data.has("fixedTexture")) fixedTexture = data.getBoolean("fixedTexture", false);

        textureVariants.clear();
        if (data.has("textureVariants")) {
            textureVariants.addAll(data.getStringList("textureVariants"));
        }

        colorPresets.clear();
        if (data.has("colorPresets")) {
            for (String hex : data.getStringList("colorPresets")) {
                try {
                    colorPresets.add(new Color(Integer.parseInt(hex, 16)));
                } catch (NumberFormatException ignored) {}
            }
        }
    }
}
