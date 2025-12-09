package kamkeel.npcdbc.data.form;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.utils.Color;
import kamkeel.npcdbc.data.RenderingData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import java.util.*;
import java.util.function.Supplier;

public class OverlayManager {

    public final ArrayList<Overlay> overlays;
    public boolean enabled = false;

    public OverlayManager() {
        this.overlays = new ArrayList<>();
    }

    public Overlay add(Type type) {
        Overlay o = type.create().manager(this);
        this.overlays.add(o);
        return o;
    }

    public Overlay add(Type type, String texture) {
        Overlay o = type.create().manager(this).texture(texture);
        this.overlays.add(o);
        return o;
    }

    public Overlay add(Type type, String texture, ColorType colorType) {
        Overlay o = type.create().manager(this).texture(texture).colorType(colorType);
        this.overlays.add(o);
        return o;
    }

    public Overlay add(Type type, String texture, ColorType colorType, boolean glow) {
        Overlay o = type.create().manager(this).texture(texture).colorType(colorType).glow(glow);
        this.overlays.add(o);
        return o.color(0xffffff);
    }

    public Overlay get(int id) {
        if (id < this.overlays.size())
            return this.overlays.get(id);
        return null;
    }

    public Overlay deleteOverlay(int id) {
        if (id >= this.overlays.size())
            return null;

        return this.overlays.remove(id);
    }

    public void replaceOverlay(Overlay oldOverlay, Overlay newOverlay) {
        int index = overlays.indexOf(oldOverlay);
        if (index != -1)
            overlays.set(index, newOverlay);
    }

    public List<Overlay> getOverlays() {
        return this.overlays;
    }

    public void readFromNBT(NBTTagCompound compound) {
        enabled = compound.getBoolean("hasOverlays");

        NBTTagCompound rendering = compound.getCompoundTag("overlayData");

        int i = 0;
        while (rendering.hasKey("overlay" + i)) {
            NBTTagCompound overlayCompound = rendering.getCompoundTag("overlay" + i);

            int type = overlayCompound.hasKey("type", Constants.NBT.TAG_INT) ? overlayCompound.getInteger("type") : 0;
            Overlay overlay = Type.create(type);

            if (overlay != null) {
                overlay.readFromNBT(overlayCompound);
                overlays.add(i, overlay);
            }
            i++;
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("hasOverlays", enabled);

        NBTTagCompound rendering = new NBTTagCompound();

        for (int i = 0; i < overlays.size(); i++) {
            rendering.setTag("overlay" + i, overlays.get(i).writeToNBT());
        }

        compound.setTag("overlayData", rendering);
        return compound;
    }

    public static class Overlay {
        public OverlayManager manager;
        public String texture = "";
        public ColorType colorType = ColorType.Custom;
        public Type type = Type.ALL;
        public int color = 0xffffff;
        public float alpha = 1;
        public boolean glow = false;
        public boolean enabled = true;

        public TextureFunction applyTexture;
        public ColorFunction applyColor;
        public Overlay(){}

        /*
        public Overlay(String texture) {
            this.texture = texture;
        }

        public Overlay(String texture, ColorType colorType) {
            this.texture = texture;
            this.colorType = colorType;
        }

        public Overlay(String texture, ColorType colorType, int color) {
            this.texture = texture;
            this.colorType = colorType;
            this.color = color;
        }

        public Overlay(String texture, ColorType colorType, boolean glow) {
            this.texture = texture;
            this.colorType = colorType;
            this.glow = glow;
        }
        */

        public Overlay texture(TextureFunction function) {
            applyTexture = function;
            return this;
        }

        @SideOnly(Side.CLIENT)
        public String texture(String texture, RenderingData data) {
            return applyTexture.invoke(texture,data, this);
        }

        public Overlay color(ColorFunction function) {
            applyColor = function;
            return this;
        }

        @SideOnly(Side.CLIENT)
        public Color color(int color, float alpha, RenderingData data) {
            return applyColor.invoke(color, alpha, data, this);
        }

        public Overlay add(Type type) {
            Overlay o = type.create().manager(manager);
            manager.overlays.add(o);
            return o;
        }

        public Overlay add(Type type, String texture) {
            Overlay o = type.create().manager(manager).texture(texture);
            manager.overlays.add(o);
            return o;
        }

        public Overlay add(Type type, String texture, ColorType colorType) {
            Overlay o = type.create().manager(manager).texture(texture).colorType(colorType);
            manager.overlays.add(o);
            return o;
        }

        public Overlay add(Type type, String texture, ColorType colorType, boolean glow) {
            Overlay o = type.create().manager(manager).texture(texture).colorType(colorType).glow(glow);
            manager.overlays.add(o);
            return o;
        }

        public OverlayManager getManager() {
            return manager;
        }

        public Overlay manager(OverlayManager manager) {
            this.manager = manager;
            return this;
        }

        public String getTexture() {
            return texture;
        }

        public Overlay texture(String texture) {
            this.texture = texture;
            return this;
        }

        public Type getType() {
            return this.type;
        }

        public Overlay asType(Type type) {
            if (this.type != type)
                return convertTo(type);

            return this;
        }

        public Overlay asType(int type) {
            if (type < Type.values().length) {
                return asType(Type.values()[type]);
            }

            return this;
        }

        public int getColorType() {
            return this.colorType.ordinal();
        }

        public Overlay colorType(int id) {
            if (id < ColorType.values().length)
                colorType(ColorType.values()[id]);
            return this;
        }

        public Overlay colorType(ColorType type) {
            this.colorType = type;
            return this;
        }

        public int getColor() {
            return this.color;
        }

        public boolean isGlow() {
            return glow;
        }

        public Overlay glow(boolean glow) {
            this.glow = glow;
            return this;
        }

        public Overlay color(int color) {
            this.color = color;
            return this;
        }

        public Overlay color(int color, float alpha) {
            this.color = color;
            this.alpha = alpha;
            return this;
        }

        public float getAlpha() {
            return this.alpha;
        }

        public Overlay alpha(float alpha) {
            this.alpha = alpha;
            return this;
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public Overlay enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        /*
               This returns the new Overlay object, which you gotta
               manually replace in the overlays list.
        */
        public Overlay convertTo(Type type) {
            if (type != this.type) {
                Overlay newO = type.create();
                newO.readFromNBT(this.writeToNBT());
                newO.type = type;
                return newO;
            }
            return this;
        }

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
                texture = compound.getString("texture");
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
                compound.setString("texture", texture);
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

        ALL(Overlay::new),
        Face(Face::new),
        Chest(Overlay::new),
        Legs(Overlay::new),
        Arms(Overlay::new),
        RightArm(Overlay::new),
        LeftArm(Overlay::new);


        Supplier<Overlay> factory;

        Type(Supplier<Overlay> factory) {
            this.factory = factory;
        }

        public Overlay create() {
            return factory.get();
        }

        public static Overlay create(int type) {
            if (type <= Type.values().length)
                return Type.values()[type].create();

            return null;
        }
    }

    public interface TextureFunction {
        String invoke(String tex, RenderingData data, Overlay o);
    }

    public interface ColorFunction {
        Color invoke(int col, float a, RenderingData data, Overlay o);
    }
}
