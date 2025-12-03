package kamkeel.npcdbc.data.form;

import net.minecraft.nbt.NBTTagCompound;

import java.util.Arrays;
import java.util.Comparator;

public class FormOverlay {

    private final Form parent;

    public final Face[] faceOverlays;
    public final Body[] bodyOverlays;

    public boolean hasFaceOverlays = false;
    public boolean hasBodyOverlays = false;

    public FormOverlay(Form parent) {
        this.parent = parent;
        this.faceOverlays = new Face[]{new Face(parent), new Face(parent)};
        this.bodyOverlays = new Body[]{new Body(parent), new Body(parent)};
    }

    public void readFromNBT(NBTTagCompound compound) {
        hasFaceOverlays = compound.getBoolean("hasFaceOverlays");
        hasBodyOverlays = compound.getBoolean("hasBodyOverlays");

        NBTTagCompound rendering = compound.getCompoundTag("overlayData");
        NBTTagCompound face = rendering.getCompoundTag("faceData");
        NBTTagCompound body = rendering.getCompoundTag("bodyData");

        for (int i = 0; i < 2; i++) {
            NBTTagCompound bodyCompound = body.getCompoundTag("body" + i);
            NBTTagCompound faceCompound = face.getCompoundTag("face" + i);

            bodyOverlays[i].readFromNBT(bodyCompound);
            faceOverlays[i].readFromNBT(faceCompound);
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("hasFaceOverlays", hasFaceOverlays);
        compound.setBoolean("hasBodyOverlays", hasBodyOverlays);

        NBTTagCompound rendering = new NBTTagCompound();
        NBTTagCompound face = new NBTTagCompound();
        NBTTagCompound body = new NBTTagCompound();

        for (int i = 0; i < 2; i++) {
            face.setTag("face" + i, faceOverlays[i].writeToNBT());
            body.setTag("body" + i, bodyOverlays[i].writeToNBT());
        }

        rendering.setTag("faceData", face);
        rendering.setTag("bodyData", body);

        compound.setTag("overlayData", rendering);
        return compound;
    }

    public Face getFace(int face) {
        return this.faceOverlays[Math.max(0, Math.min(1, face))];
    }

    public Body getBody(int body) {
        return this.bodyOverlays[Math.max(0, Math.min(1, body))];
    }

    public Face[] getFaces() {
        return this.faceOverlays;
    }

    public Body[] getBodies() {
        return this.bodyOverlays;
    }

    public boolean areFaceOverlaysEnabled() {
        return this.hasFaceOverlays;
    }

    public void setFaceOverlaysEnabled(boolean enable) {
        this.hasFaceOverlays = enable;
    }

    public boolean areBodyOverlaysEnabled() {
        return this.hasBodyOverlays;
    }

    public void setBodyOverlaysEnabled(boolean enable) {
        this.hasBodyOverlays = enable;
    }

    public void setFaceTexture(int face, String texture) {
        getFace(face).setTexture(texture);
    }

    public void setFaceTexture(int face, String texture, int faceType) {
        getFace(face).setTexture(texture, faceType);
    }

    public void setDisableFace(int face, boolean disable) {
        getFace(face).setDisableFace(disable);
    }

    public void setMatchPlayerFace(int face, boolean match) {
        getFace(face).setMatchPlayerFace(match);
    }

    public void setFaceColor(int face, int color) {
        getFace(face).setColor(color);
    }

    public void setFaceColorType(int face, int color) {
        getFace(face).setColorType(color);
    }

    public void setBodyTexture(int body, String texture) {
        getBody(body).setTexture(texture);
    }

    public void setBodyColor(int body, int color) {
        getBody(body).setColor(color);
    }

    public void setBodyColorType(int body, int color) {
        getBody(body).setColorType(color);
    }

    public String getFaceTexture(int face) {
        return getFace(face).getTexture();
    }

    public String getFaceTexture(int face, int faceType) {
        return getFace(face).getTexture(faceType);
    }

    public boolean isFaceDisabled(int face) {
        return getFace(face).isFaceDisabled();
    }

    public boolean isFaceMatchingPlayer(int face) {
        return getFace(face).isMatchingPlayerFace();
    }

    public int getFaceColorType(int face) {
        return getFace(face).getColorType();
    }

    public int getFaceColor(int face) {
        return getFace(face).getColor();
    }

    public float getFaceAlpha(int face) {
        return getFace(face).getAlpha();
    }

    public String getBodyTexture(int body) {
        return getBody(body).getTexture();
    }

    public int getBodyColorType(int body) {
        return getBody(body).getColorType();
    }

    public int getBodyColor(int body) {
        return getBody(body).getColor();
    }

    public float getBodyAlpha(int body) {
        return getBody(body).getAlpha();
    }

    public FormOverlay save() {
        if (parent != null)
            parent.save();
        return this;
    }

    public static class Face  {
        private final Form parent;

        public String texture = "";
        public String[] faceTextures = new String[]{"", "", "", "", "", ""};
        public ColorType colorType = ColorType.Custom;
        public int color = 0xffffff;
        public float alpha = 1;

        public boolean disableFace = false;
        public boolean matchPlayerFace = false;

        public boolean enabled = true;

        public Face(Form parent) {
            this.parent = parent;
        }

        public void readFromNBT(NBTTagCompound compound) {
            enabled = compound.getBoolean("enabled");

            disableFace = compound.getBoolean("disableFace");
            matchPlayerFace = compound.getBoolean("matchPlayerFace");

            colorType = ColorType.byId(compound.getInteger("colorType"));

            if (colorType != ColorType.Custom) {
                color = 0xffffff;
            } else {
                color = compound.hasKey("color") ? compound.getInteger("color") : 0xffffff;
            }

            alpha = compound.hasKey("alpha") ? compound.getFloat("alpha") : 1;

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

        public NBTTagCompound writeToNBT() {
            NBTTagCompound compound = new NBTTagCompound();

            compound.setBoolean("enabled", enabled);

            compound.setBoolean("disableFace", disableFace);
            compound.setBoolean("matchPlayerFace", matchPlayerFace);

            compound.setInteger("colorType", colorType.getId());

            compound.setInteger("color", color);
            compound.setFloat("alpha", alpha);

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

        public String getTexture() {
            return texture;
        }

        public void setTexture(String texture) {
            this.texture = texture;
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

        public boolean isFaceDisabled() {
            return disableFace;
        }

        public void setDisableFace(boolean disableFace) {
            this.disableFace = disableFace;
        }

        public boolean isMatchingPlayerFace() {
            return matchPlayerFace;
        }

        public void setMatchPlayerFace(boolean matchPlayerFace) {
            this.matchPlayerFace = matchPlayerFace;
        }

        public int getColorType() {
            return this.colorType.getId();
        }

        public void setColorType(int id) {
            this.colorType = ColorType.byId(id);
        }

        public int getColor() {
            return this.color;
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

    public static class Body {
        private final Form parent;

        public String texture = "";
        public ColorType colorType = ColorType.Custom;
        public int color = 0xffffff;
        public float alpha = 1;

        public boolean enabled = true;

        public Body(Form parent) {
            this.parent = parent;
        }

        public void readFromNBT(NBTTagCompound compound) {
            enabled = compound.getBoolean("enabled");

            texture = compound.getString("texture");

            colorType = ColorType.byId(compound.getInteger("colorType"));

            if (colorType != ColorType.Custom) {
                color = 0xffffff;
            } else {
                color = compound.hasKey("color") ? compound.getInteger("color") : 0xffffff;
            }

            alpha = compound.hasKey("alpha") ? compound.getFloat("alpha") : 1;
        }

        public NBTTagCompound writeToNBT() {
            NBTTagCompound compound = new NBTTagCompound();

            compound.setBoolean("enabled", enabled);

            compound.setInteger("colorType", colorType.getId());

            compound.setString("texture", texture);
            compound.setInteger("color", color);
            compound.setFloat("alpha", alpha);

            return compound;
        }

        public String getTexture() {
            return texture;
        }

        public void setTexture(String texture) {
            this.texture = texture;
        }

        public int getColorType() {
            return this.colorType.getId();
        }

        public void setColorType(int id) {
            this.colorType = ColorType.byId(id);
        }

        public int getColor() {
            return this.color;
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

    public enum ColorType {
        Custom(0),
        Body(1),
        Eye(2),
        Hair(3),
        Fur(4);

        private static final ColorType[] BY_ID = Arrays.stream(values()).sorted(
                Comparator.comparing(ColorType::getId)
        ).toArray(ColorType[]::new);

        private final int id;

        ColorType(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }

        public static ColorType byId(int id) {
            return BY_ID[Math.floorMod(id, BY_ID.length)];
        }
    }
}
