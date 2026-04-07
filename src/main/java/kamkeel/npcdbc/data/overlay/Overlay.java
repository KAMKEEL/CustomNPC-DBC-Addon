package kamkeel.npcdbc.data.overlay;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.api.Color;
import kamkeel.npcdbc.api.client.overlay.IOverlay;
import kamkeel.npcdbc.data.race.serial.DataCompound;
import kamkeel.npcdbc.data.race.serial.DataSerializable;
import noppes.npcs.controllers.data.JaninoScriptHandler;

import java.util.function.Function;

public class Overlay implements IOverlay, DataSerializable {
    public String key = null;
    public OverlayChain chain;
    public String texture = "";
    public ColorType colorType = ColorType.Custom;
    public Type type = Type.ALL;
    public int color = 0xffffff;
    public float alpha = 1;
    public boolean glow = false;
    public boolean enabled = true;
    public String modelKey = null;

    public TextureFunction applyTexture;
    public ColorFunction applyColor;
    public RenderFunction renderer;
    public Function<OverlayContext, Boolean> condition;

    public final JaninoScriptHandler<OverlayScript> scriptHandler = new JaninoScriptHandler(OverlayScript::new, OverlayScript.class);

    public Overlay() {
    }

    public Overlay type(Type type) {
        this.type = type;
        return this;
    }

    public Overlay modelKey(String modelKey) {
        this.modelKey = modelKey;
        return this;
    }

    public String getModelKey() {
        return modelKey;
    }

    public Overlay key(String key) {
        this.key = key;
        return this;
    }

    public String getKey() {
        return key;
    }

    public Overlay texture(TextureFunction function) {
        applyTexture = function;
        return this;
    }

    @SideOnly(Side.CLIENT)
    public String applyTexture(OverlayContext ctx) {
        if (scriptHandler.hasScript()) {
            String script = scriptHandler.getScript().call(f -> f.getTexture(ctx));
            if (script != null)
                return script;
        }

        if (applyTexture != null)
            return applyTexture.getTexture(ctx);

        return null;
    }

    public static String matchTexture(OverlayContext ctx, String text) {
        int eyeType = ctx.eyeType();
        int bodyType = ctx.bodyTypeDBC();
        int furType = ctx.furType();
        int noseType = ctx.nose();
        int mouthType = ctx.mouth();
        int gender = ctx.gender();

        return text
            .replaceAll("%b", bodyType + "")
            .replaceAll("%f", furType + "")
            .replaceAll("%e", eyeType + "")
            .replaceAll("%n", noseType + "")
            .replaceAll("%m", mouthType + "")
            .replaceAll("%g", gender == 2 ? "f" : "m");
    }

    public Overlay color(ColorFunction function) {
        applyColor = function;
        return this;
    }

    @SideOnly(Side.CLIENT)
    public Color applyColor(OverlayContext ctx) {
        if (scriptHandler.hasScript()) {
            Color script = scriptHandler.getScript().call(f -> f.getColor(ctx));
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
        Overlay o = ((Overlay) type.create()).chain(chain);
        chain.overlays.add(o);
        return o;
    }

    public Overlay add(Type type, String texture) {
        Overlay o = ((Overlay) type.create()).chain(chain).texture(texture);
        chain.overlays.add(o);
        return o;
    }

    public Overlay add(Type type, TextureFunction texture) {
        Overlay o = ((Overlay) type.create()).chain(chain).texture(texture);
        chain.overlays.add(o);
        return o;
    }

    public Overlay add(Type type, ColorType colorType) {
        Overlay o = ((Overlay) type.create()).chain(chain).colorType(colorType);
        chain.overlays.add(o);
        return o;
    }

    public Overlay add(Type type, int color) {
        Overlay o = ((Overlay) type.create()).chain(chain).colorType(ColorType.Custom).color(color);
        chain.overlays.add(o);
        return o;
    }

    public Overlay add(Type type, ColorType colorType, String texture) {
        Overlay o = ((Overlay) type.create()).chain(chain).texture(texture).colorType(colorType);
        chain.overlays.add(o);
        return o;
    }

    public Overlay add(Type type, ColorType colorType, TextureFunction texture) {
        Overlay o = ((Overlay) type.create()).chain(chain).texture(texture).colorType(colorType);
        chain.overlays.add(o);
        return o;
    }

    public Overlay add(Type type, int color, String texture) {
        Overlay o = ((Overlay) type.create()).chain(chain).texture(texture).colorType(ColorType.Custom).color(color);
        chain.overlays.add(o);
        return o;
    }

    public Overlay add(Type type, int color, TextureFunction texture) {
        Overlay o = ((Overlay) type.create()).chain(chain).texture(texture).colorType(ColorType.Custom).color(color);
        chain.overlays.add(o);
        return o;
    }

    public Overlay add(Type type, ColorType colorType, boolean glow, String texture) {
        Overlay o = ((Overlay) type.create()).chain(chain).texture(texture).colorType(colorType).glow(glow);
        chain.overlays.add(o);
        return o;
    }

    public Overlay add(Type type, ColorType colorType, boolean glow, TextureFunction texture) {
        Overlay o = ((Overlay) type.create()).chain(chain).texture(texture).colorType(colorType).glow(glow);
        chain.overlays.add(o);
        return o;
    }

    public Overlay add(Type type, int color, boolean glow, String texture) {
        Overlay o = ((Overlay) type.create()).chain(chain).texture(texture).colorType(ColorType.Custom).color(color).glow(glow);
        chain.overlays.add(o);
        return o.color(0xffffff);
    }

    public Overlay add(Type type, int color, boolean glow, TextureFunction texture) {
        Overlay o = ((Overlay) type.create()).chain(chain).texture(texture).colorType(ColorType.Custom).color(color).glow(glow);
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

    public Overlay copy() {
        Overlay copy = (Overlay) this.type.create();
        copy.deserialize(this.serialize(DataCompound.create()));
        return copy;
    }

    /*
           This returns the new Overlay object, which you gotta
           manually replace in the overlays list.
    */
    public Overlay convertTo(Type type) {
        if (type != this.type) {
            Overlay newO = ((Overlay) type.create());
            newO.deserialize(this.serialize(DataCompound.create()));
            newO.type = type;
            return newO;
        }
        return this;
    }

    // ── IOverlay API mutators ──

    @Override
    public IOverlay setTexture(String texture) {
        return texture(texture);
    }

    @Override
    public IOverlay setColor(int color) {
        return color(color);
    }

    @Override
    public IOverlay setAlpha(float alpha) {
        return alpha(alpha);
    }

    @Override
    public IOverlay setGlow(boolean glow) {
        return glow(glow);
    }

    @Override
    public IOverlay setEnabled(boolean enabled) {
        return enabled(enabled);
    }

    @Override
    public IOverlay setColorType(int colorType) {
        return colorType(colorType);
    }

    @Override
    public DataCompound serialize(DataCompound data) {
        data.putString("key", key);
        data.putBoolean("enabled", enabled);

        data.putInt("colorType", colorType.ordinal());
        data.putInt("type", type.ordinal());

        data.putString("texture", texture);
        data.putInt("color", color);
        data.putFloat("alpha", alpha);
        data.putBoolean("glow", glow);

        data.putString("modelKey", modelKey);

        scriptHandler.writeToNBT(data.toNbt());

        return data;
    }

    @Override
    public void deserialize(DataCompound data) {
        key = data.getString("key", key);
        enabled = data.getBoolean("enabled", enabled);

        texture = data.getString("texture", texture);

        colorType = ColorType.values()[data.getInt("colorType", colorType.ordinal())];
        type = Type.values()[data.getInt("type", type.ordinal())];

        color = data.getInt("color", color);

        alpha = data.getFloat("alpha", alpha);
        glow = data.getBoolean("glow", glow);
        modelKey = data.getString("modelKey", modelKey);

        scriptHandler.readFromNBT(data.toNbt());
    }
}
