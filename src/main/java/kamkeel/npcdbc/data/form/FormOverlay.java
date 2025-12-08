package kamkeel.npcdbc.data.form;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.common.util.Constants;

import java.util.*;

public class FormOverlay {

    private final FormDisplay parent;

    public final ArrayList<Overlay> overlays;

    public boolean hasOverlays = false;

    public FormOverlay(FormDisplay parent) {
        this.parent = parent;
        this.overlays = new ArrayList<>();
    }

    public void readFromNBT(NBTTagCompound compound) {
        hasOverlays = compound.getBoolean("hasOverlays");

        NBTTagCompound rendering = compound.getCompoundTag("overlayData");

        int i = 0;
        while (rendering.hasKey("overlay" + i)) {
            NBTTagCompound overlayCompound = rendering.getCompoundTag("overlay" + i);

            int type = overlayCompound.hasKey("type", Constants.NBT.TAG_INT) ? overlayCompound.getInteger("type") : 0;
            Overlay overlay = Type.getOverlayByType(type);

            if (overlay != null) {
                overlay.readFromNBT(overlayCompound);
                overlays.add(i, overlay);
            }
            i++;
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("hasOverlays", hasOverlays);

        NBTTagCompound rendering = new NBTTagCompound();

        for (int i = 0; i < overlays.size(); i++) {
            rendering.setTag("overlay" + i, overlays.get(i).writeToNBT());
        }

        compound.setTag("overlayData", rendering);
        return compound;
    }

    public Overlay getOverlay(int id) {
        if (id < this.overlays.size())
            return this.overlays.get(id);
        return null;
    }

    public void setOverlay(Overlay overlay) {
        if (overlay != null)
            this.overlays.add(overlay);
        save();
    }

    public boolean hasOverlay(int id) {
        return id < this.overlays.size() && this.overlays.get(id) != null;
    }

    public void deleteOverlay(int id) {
        if (id >= this.overlays.size())
            return;

        this.overlays.remove(id);
        save();
    }

    public void addOverlay() {
        addOverlay(0);
    }

    public void addOverlay(int type) {
        if (type < Type.values().length)
            this.overlays.add(Type.getOverlayByType(type));

        save();
    }

    public List<Overlay> getOverlays() {
        return this.overlays;
    }

    public FormOverlay save() {
        if (parent != null)
            parent.save();
        return this;
    }

    public static class Overlay {
        public String texture = "";
        public ColorType colorType = ColorType.Custom;
        public Type type = Type.Body;
        public int color = 0xffffff;
        public float alpha = 1;
        public boolean glow = false;
        public boolean enabled = true;

        public Overlay(){}

        public void readFromNBT(NBTTagCompound compound) {
            enabled = compound.getBoolean("enabled");

            texture = compound.getString("texture");

            colorType = ColorType.values()[compound.getInteger("colorType")];
            type = Type.values()[compound.getInteger("type")];

            if (colorType != ColorType.Custom) {
                color = 0xffffff;
            } else {
                color = compound.hasKey("color") ? compound.getInteger("color") : 0xffffff;
            }

            alpha = compound.hasKey("alpha") ? compound.getFloat("alpha") : 1;
            glow = compound.hasKey("glow") && compound.getBoolean("glow");
        }

        public NBTTagCompound writeToNBT() {
            NBTTagCompound compound = new NBTTagCompound();

            compound.setBoolean("enabled", enabled);

            compound.setInteger("colorType", colorType.ordinal());
            compound.setInteger("type", type.ordinal());

            compound.setString("texture", texture);
            compound.setInteger("color", color);
            compound.setFloat("alpha", alpha);
            compound.setBoolean("glow", glow);

            return compound;
        }

        public String getTexture() {
            return texture;
        }

        public void setTexture(String texture) {
            this.texture = texture;
        }

        public Type getType() {
            return this.type;
        }

        public void setType(int type) {
            if (type < Type.values().length)
                this.type = Type.values()[type];
        }

        public int getColorType() {
            return this.colorType.ordinal();
        }

        public void setColorType(int id) {
            if (id < ColorType.values().length)
                this.colorType = ColorType.values()[id];
        }

        public int getColor() {
            return this.color;
        }

        public boolean isGlow() {
            return glow;
        }

        public void setGlow(boolean glow) {
            this.glow = glow;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public float getAlpha() {
            return this.alpha;
        }

        public void setAlpha(float alpha) {
            this.alpha = alpha;
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class Face extends Overlay {
        public String[] faceTextures = new String[]{"", "", "", "", "", ""};
        public boolean matchPlayerFace = false;

        public boolean enabled = true;

        public Face() {
            this.type = Type.Face;
        }

        @Override
        public void readFromNBT(NBTTagCompound compound) {
            enabled = compound.getBoolean("enabled");

            matchPlayerFace = compound.getBoolean("matchPlayerFace");

            colorType = ColorType.values()[compound.getInteger("colorType")];

            if (colorType != ColorType.Custom) {
                color = 0xffffff;
            } else {
                color = compound.hasKey("color") ? compound.getInteger("color") : 0xffffff;
            }

            alpha = compound.hasKey("alpha") ? compound.getFloat("alpha") : 1;
            glow = compound.hasKey("glow") && compound.getBoolean("glow");

            if (matchPlayerFace) {
                texture = "";

                NBTTagCompound faceTypes = compound.getCompoundTag("faceTextures");

                if (!compound.hasKey("faceTextures")) {
                    Arrays.fill(faceTextures, "");
                } else {
                    for (int i = 0; i < faceTextures.length; i++) {
                        faceTextures[i] = faceTypes.getString("face" + i);
                    }
                }
            } else {
                texture = compound.getString("baseTexture");
                Arrays.fill(faceTextures, "");
            }
        }

        @Override
        public NBTTagCompound writeToNBT() {
            NBTTagCompound compound = new NBTTagCompound();

            compound.setBoolean("enabled", enabled);
            compound.setBoolean("matchPlayerFace", matchPlayerFace);

            compound.setInteger("colorType", colorType.ordinal());
            compound.setInteger("color", color);
            compound.setFloat("alpha", alpha);
            compound.setBoolean("glow", glow);

            if (matchPlayerFace) {
                NBTTagCompound faceTypes = new NBTTagCompound();

                for (int i = 0; i < faceTextures.length; i++) {
                    faceTypes.setString("face" + i, faceTextures[i]);
                }

                compound.setTag("faceTextures", faceTypes);
            } else {
                compound.setString("baseTexture", texture);
            }

            return compound;
        }

        public String getTexture(int faceType) {
            faceType = Math.max(0, Math.min(5, faceType));

            if (matchPlayerFace) {
                return faceTextures[faceType];
            }

            if (texture == null || texture.isEmpty()) {
                return faceTextures[faceType];
            }

            return texture;
        }

        public void setTexture(String texture, int faceType) {
            faceType = Math.max(0, Math.min(5, faceType));

            faceTextures[faceType] = texture;
        }

        public boolean isMatchingPlayerFace() {
            return matchPlayerFace;
        }

        public void setMatchPlayerFace(boolean matchPlayerFace) {
            this.matchPlayerFace = matchPlayerFace;
        }
    }

    public enum ColorType {
        Custom(),
        Body(),
        Eye(),
        Hair(),
        Fur()
    }

    public enum Type {
        Body(),
        Face();

        public static Overlay getOverlayByType(int type) {
            switch(type) {
                case 0:
                    return new Overlay();
                case 1:
                    return new Face();
                default:
                    return null;
            }
        }
    }
}
