package kamkeel.npcdbc.data.overlay;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.utils.Color;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;

public class Overlay {
    public OverlayChain chain;
    public String texture = "";
    public ColorType colorType = ColorType.Custom;
    public Type type = Type.ALL;
    public int color = 0xffffff;
    public float alpha = 1;
    public boolean glow = false;
    public boolean enabled = true;

    public TextureFunction applyTexture;
    public ColorFunction applyColor;
    public Function<OverlayContext, Boolean> condition;

    public Overlay() {
    }

    public Overlay texture(TextureFunction function) {
        applyTexture = function;
        return this;
    }

    @SideOnly(Side.CLIENT)
    public String applyTexture(OverlayContext ctx) {
        return applyTexture.invoke(ctx);
    }

    public Overlay color(ColorFunction function) {
        applyColor = function;
        return this;
    }

    @SideOnly(Side.CLIENT)
    public Color applyColor(OverlayContext ctx) {
        return applyColor.invoke(ctx);
    }

    public Overlay condition(Function<OverlayContext, Boolean> condition) {
        this.condition = condition;
        return this;
    }

    @SideOnly(Side.CLIENT)
    public boolean checkCondition(OverlayContext ctx) {
        return condition.apply(ctx);
    }

    public Overlay add(Type type) {
        Overlay o = type.create().chain(chain);
        chain.overlays.add(o);
        return o;
    }

    public Overlay add(Type type, String texture) {
        Overlay o = type.create().chain(chain).texture(texture);
        chain.overlays.add(o);
        return o;
    }

    public Overlay add(Type type, TextureFunction texture) {
        Overlay o = type.create().chain(chain).texture(texture);
        chain.overlays.add(o);
        return o;
    }

    public Overlay add(Type type, ColorType colorType) {
        Overlay o = type.create().chain(chain).colorType(colorType);
        chain.overlays.add(o);
        return o;
    }

    public Overlay add(Type type, int color) {
        Overlay o = type.create().chain(chain).colorType(ColorType.Custom).color(color);
        chain.overlays.add(o);
        return o;
    }

    public Overlay add(Type type, ColorType colorType, String texture) {
        Overlay o = type.create().chain(chain).texture(texture).colorType(colorType);
        chain.overlays.add(o);
        return o;
    }

    public Overlay add(Type type, ColorType colorType, TextureFunction texture) {
        Overlay o = type.create().chain(chain).texture(texture).colorType(colorType);
        chain.overlays.add(o);
        return o;
    }

    public Overlay add(Type type, int color, String texture) {
        Overlay o = type.create().chain(chain).texture(texture).colorType(ColorType.Custom).color(color);
        chain.overlays.add(o);
        return o;
    }

    public Overlay add(Type type, int color, TextureFunction texture) {
        Overlay o = type.create().chain(chain).texture(texture).colorType(ColorType.Custom).color(color);
        chain.overlays.add(o);
        return o;
    }

    public Overlay add(Type type, ColorType colorType, boolean glow, String texture) {
        Overlay o = type.create().chain(chain).texture(texture).colorType(colorType).glow(glow);
        chain.overlays.add(o);
        return o;
    }

    public Overlay add(Type type, ColorType colorType, boolean glow, TextureFunction texture) {
        Overlay o = type.create().chain(chain).texture(texture).colorType(colorType).glow(glow);
        chain.overlays.add(o);
        return o;
    }

    public Overlay add(Type type, int color, boolean glow, String texture) {
        Overlay o = type.create().chain(chain).texture(texture).colorType(ColorType.Custom).color(color).glow(glow);
        chain.overlays.add(o);
        return o.color(0xffffff);
    }

    public Overlay add(Type type, int color, boolean glow, TextureFunction texture) {
        Overlay o = type.create().chain(chain).texture(texture).colorType(ColorType.Custom).color(color).glow(glow);
        chain.overlays.add(o);
        return o.color(0xffffff);
    }

    public OverlayChain getChain() {
        return chain;
    }

    public Overlay chain(OverlayChain chain) {
        this.chain = chain;
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
        Eye(),
        Hair(),
        Fur(),
        BodyCM(),
        BodyC1(),
        BodyC2(),
        BodyC3()
    }

    public enum Type {
        ALL(),
        Face(Face::new),
        Chest(),
        Arms(),
        RightArm(),
        LeftArm(),
        Legs(),
        RightLeg(),
        LeftLeg();

        Supplier<Overlay> factory;

        Type() {
            this.factory = Overlay::new;
        }

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
        String invoke(OverlayContext ctx);
    }

    public interface ColorFunction {
        Color invoke(OverlayContext ctx);
    }
}
