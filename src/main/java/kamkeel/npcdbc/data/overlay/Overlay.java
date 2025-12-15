package kamkeel.npcdbc.data.overlay;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.utils.Color;
import net.minecraft.nbt.NBTTagCompound;

import java.util.function.Function;
import java.util.function.Supplier;

public class Overlay implements JaninoScriptable {
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
    public RenderFunction renderer;
    public Function<OverlayContext, Boolean> condition;

    public OverlayScript script;

    public Overlay() {
    }

    public Overlay type(Type type) {
        this.type = type;
        return this;
    }

    public Overlay texture(TextureFunction function) {
        applyTexture = function;
        return this;
    }

    @SideOnly(Side.CLIENT)
    public String applyTexture(OverlayContext ctx) {
        if (hasScript()) {
            String script = getScript().call(f -> f.getTexture(ctx));
            if (script != null)
                return script;
        }

        if (applyTexture != null)
            return applyTexture.getTexture(ctx);

        return null;
    }

    public Overlay color(ColorFunction function) {
        applyColor = function;
        return this;
    }

    @SideOnly(Side.CLIENT)
    public Color applyColor(OverlayContext ctx) {
        if (hasScript()) {
            Color script = getScript().call(f -> f.getColor(ctx));
            if (script != null)
                return script;
        }

        if (applyColor != null)
            return applyColor.getColor(ctx);
        return null;
    }

    public Overlay renderer(RenderFunction function) {
        renderer = function;
        return this;
    }

    @SideOnly(Side.CLIENT)
    public void render(OverlayContext ctx) {
        renderer.render(ctx);
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

        script = JaninoScriptable.readFromNBT(compound, script, OverlayScript::new);
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

        JaninoScriptable.writeToNBT(compound, script);

        return compound;
    }

    public OverlayScript getScript() {
        return script;
    }

    public OverlayScript createScript() {
        if (script == null)
            script = new OverlayScript();
        return script;
    }

    public void deleteScript() {
        script = null;
    }

    public boolean hasScript() {
        return script != null;
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

        Face(),
        Eyebrows(),
        EyeWhite(),
        LeftEye(),
        RightEye(),
        Nose(),
        Mouth(),

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
            return factory.get().type(this);
        }

        public static Overlay create(int type) {
            if (type <= Type.values().length)
                return Type.values()[type].create();

            return null;
        }
    }

    public interface TextureFunction {
        String getTexture(OverlayContext ctx);
    }

    public interface ColorFunction {
        Color getColor(OverlayContext ctx);
    }

    public interface RenderFunction {
        void render(OverlayContext ctx);
    }
}
