package kamkeel.npcdbc.data.form;

import net.minecraft.nbt.NBTTagCompound;

import java.util.Arrays;

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


    public void setBodyTexture(int body, String texture) {
        getBody(body).setTexture(texture);
    }

    public void setBodyColor(int body, int color) {
        getBody(body).setColor(color);
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

    public int getFaceColor(int face) {
        return getFace(face).getColor();
    }

    public float getFaceAlpha(int face) {
        return getFace(face).getAlpha();
    }

    public String getBodyTexture(int body) {
        return getBody(body).getTexture();
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
        public int color = 0xffffff;
        public float alpha = 1;

        public boolean disableFace = false;
        public boolean matchPlayerFace = false;
        public boolean usesHairColor = false;
        public boolean usesEyeColor = false;
        public boolean usesBodyColor = false;

        public boolean enabled = true;

        public Face(Form parent) {
            this.parent = parent;
        }

        public void readFromNBT(NBTTagCompound compound) {
            enabled = compound.getBoolean("enabled");

            disableFace = compound.getBoolean("disableFace");
            matchPlayerFace = compound.getBoolean("matchPlayerFace");
            usesHairColor = compound.getBoolean("usesHairColor");
            usesEyeColor = compound.getBoolean("usesEyeColor");
            usesBodyColor = compound.getBoolean("usesBodyColor");

            if (usesHairColor || usesBodyColor) {
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
            compound.setBoolean("usesHairColor", usesHairColor);
            compound.setBoolean("usesEyeColor", usesEyeColor);
            compound.setBoolean("usesBodyColor", usesBodyColor);

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

        public boolean isUsingHairColor() {
            return this.usesHairColor;
        }

        public void setUsingHairColor(boolean uses) {
            this.usesHairColor = uses;
        }

        public boolean isUsingEyeColor() {
            return this.usesEyeColor;
        }

        public void setUsingEyeColor(boolean uses) {
            this.usesEyeColor = uses;
        }

        public boolean isUsingBodyColor() {
            return this.usesBodyColor;
        }

        public void setUsingBodyColor(boolean uses) {
            this.usesBodyColor = uses;
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
        public int color = 0xffffff;
        public float alpha = 1;

        public boolean usesFurColor = false;
        public boolean usesHairColor = false;
        public boolean usesBodyColor = false;

        public boolean enabled = true;

        public Body(Form parent) {
            this.parent = parent;
        }

        public void readFromNBT(NBTTagCompound compound) {
            enabled = compound.getBoolean("enabled");

            usesFurColor = compound.getBoolean("usesFurColor");
            usesHairColor = compound.getBoolean("usesHairColor");
            usesBodyColor = compound.getBoolean("usesBodyColor");

            texture = compound.getString("texture");

            if (usesHairColor || usesBodyColor || usesFurColor) {
                color = 0xffffff;
            } else {
                color = compound.hasKey("color") ? compound.getInteger("color") : 0xffffff;
            }

            alpha = compound.hasKey("alpha") ? compound.getFloat("alpha") : 1;
        }

        public NBTTagCompound writeToNBT() {
            NBTTagCompound compound = new NBTTagCompound();

            compound.setBoolean("enabled", enabled);

            compound.setBoolean("usesFurColor", usesFurColor);
            compound.setBoolean("usesHairColor", usesHairColor);
            compound.setBoolean("usesBodyColor", usesBodyColor);

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

        public boolean isUsingFurColor() {
            return this.usesFurColor;
        }

        public boolean isUsingHairColor() {
            return this.usesHairColor;
        }

        public boolean isUsingBodyColor() {
            return this.usesBodyColor;
        }

        public void setUsingFurColor(boolean uses) {
            this.usesFurColor = uses;
        }

        public void setUsingHairColor(boolean uses) {
            this.usesHairColor = uses;
        }

        public void setUsingBodyColor(boolean uses) {
            this.usesBodyColor = uses;
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
