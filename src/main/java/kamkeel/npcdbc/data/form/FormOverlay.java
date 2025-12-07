package kamkeel.npcdbc.data.form;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.model.obj.Face;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class FormOverlay {

    private final FormDisplay parent;

    public final ArrayList<Face> faceOverlays;
    public final ArrayList<Body> bodyOverlays;

    public boolean hasFaceOverlays = false;
    public boolean hasBodyOverlays = false;

    public FormOverlay(FormDisplay parent) {
        this.parent = parent;
        this.faceOverlays = new ArrayList<>();
        this.bodyOverlays = new ArrayList<>();
    }

    public void readFromNBT(NBTTagCompound compound) {
        hasFaceOverlays = compound.getBoolean("hasFaceOverlays");
        hasBodyOverlays = compound.getBoolean("hasBodyOverlays");

        NBTTagCompound rendering = compound.getCompoundTag("overlayData");

        NBTTagCompound body = rendering.getCompoundTag("bodyData");
        int i = 0;
        while (body.hasKey("body" + i)) {
            NBTTagCompound bodyCompound = body.getCompoundTag("body" + i);

            bodyOverlays.add(i, new Body());
            bodyOverlays.get(i).readFromNBT(bodyCompound);
            i++;
        }



        NBTTagCompound face = rendering.getCompoundTag("faceData");
        int j = 0;
        while (face.hasKey("face" + j)) {
            NBTTagCompound faceCompound = face.getCompoundTag("face" + j);

            faceOverlays.add(j, new Face());
            faceOverlays.get(j).readFromNBT(faceCompound);
            j++;
        }

    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("hasFaceOverlays", hasFaceOverlays);
        compound.setBoolean("hasBodyOverlays", hasBodyOverlays);

        NBTTagCompound rendering = new NBTTagCompound();

        NBTTagCompound body = new NBTTagCompound();

        for (int i = 0; i < bodyOverlays.size(); i++) {
            body.setTag("body" + i, bodyOverlays.get(i).writeToNBT());
        }

        rendering.setTag("bodyData", body);


        NBTTagCompound face = new NBTTagCompound();

        for (int i = 0; i < faceOverlays.size(); i++) {
            face.setTag("face" + i, faceOverlays.get(i).writeToNBT());
        }

        rendering.setTag("faceData", face);


        compound.setTag("overlayData", rendering);
        return compound;
    }

    public Face getFace(int face) {
        return this.faceOverlays.get(Math.max(0, Math.min(this.faceOverlays.size() - 1, face)));
    }

    public Body getBody(int body) {
        return this.bodyOverlays.get(Math.max(0, Math.min(this.bodyOverlays.size() - 1, body)));
    }

    public ArrayList<Face> getFaces() {
        return this.faceOverlays;
    }

    public ArrayList<Body> getBodies() {
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
        if (faceType == -1)
            getFace(face).setTexture(texture);
        else
            getFace(face).setTexture(texture, faceType);
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

    public void setFaceAlpha(int face, float alpha) {
        getFace(face).setAlpha(alpha);
    }

    public void setFaceGlow(int face, boolean glow) {
        getFace(face).setGlow(glow);
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

    public boolean doesFaceGlow(int face) {
        return getFace(face).isGlow();
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

    public boolean doesBodyGlow(int body) {
        return getBody(body).isGlow();
    }

    public void addFaceOverlay() {
        this.faceOverlays.add(new Face());
    }

    public void addBodyOverlay() {
        this.bodyOverlays.add(new Body());
    }

    public void deleteFaceOverlay(int id) {
        if (id >= this.faceOverlays.size())
            return;

        this.faceOverlays.remove(id);
        save();
    }

    public void deleteBodyOverlay(int id) {
        if (id >= this.bodyOverlays.size())
            return;

        this.bodyOverlays.remove(id);
        save();
    }

    public FormOverlay save() {
        if (parent != null)
            parent.save();
        return this;
    }

    public static class Face  {
        public String texture = "";
        public String[] faceTextures = new String[]{"", "", "", "", "", ""};
        public ColorType colorType = ColorType.Custom;
        public int color = 0xffffff;
        public float alpha = 1;
        public boolean glow = false;

        public boolean matchPlayerFace = false;

        public boolean enabled = true;

        public Face() {
        }

        public void readFromNBT(NBTTagCompound compound) {
            enabled = compound.getBoolean("enabled");

            matchPlayerFace = compound.getBoolean("matchPlayerFace");

            colorType = ColorType.byId(compound.getInteger("colorType"));

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

        public boolean isMatchingPlayerFace() {
            return matchPlayerFace;
        }

        public void setMatchPlayerFace(boolean matchPlayerFace) {
            this.matchPlayerFace = matchPlayerFace;
        }

        public int getColorType() {
            return this.colorType.ordinal();
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

        public boolean isGlow() {
            return glow;
        }

        public void setGlow(boolean glow) {
            this.glow = glow;
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class Body {
        public String texture = "";
        public ColorType colorType = ColorType.Custom;
        public int color = 0xffffff;
        public float alpha = 1;
        public boolean glow = false;

        public boolean enabled = true;

        public Body() {
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
            glow = compound.hasKey("glow") && compound.getBoolean("glow");
        }

        public NBTTagCompound writeToNBT() {
            NBTTagCompound compound = new NBTTagCompound();

            compound.setBoolean("enabled", enabled);

            compound.setInteger("colorType", colorType.ordinal());

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

        public int getColorType() {
            return this.colorType.ordinal();
        }

        public void setColorType(int id) {
            this.colorType = ColorType.byId(id);
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

    public enum ColorType {
        Custom(),
        Body(),
        Eye(),
        Hair(),
        Fur();

        private static final ColorType[] BY_ID = Arrays.stream(values()).sorted(
                Comparator.comparing(ColorType::ordinal)
        ).toArray(ColorType[]::new);

        public static ColorType byId(int id) {
            return BY_ID[Math.floorMod(id, BY_ID.length)];
        }
    }
}
