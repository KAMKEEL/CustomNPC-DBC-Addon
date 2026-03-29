package kamkeel.npcdbc.api.client.overlay;

import kamkeel.npcdbc.api.Color;
import kamkeel.npcdbc.data.overlay.Overlay;
import kamkeel.npcdbc.data.overlay.OverlayContext;

import java.util.function.Supplier;

public interface IOverlay {


    enum ColorType {
        Custom(),
        Eye(),
        Hair(),
        Fur(),
        BodyCM(),
        BodyC1(),
        BodyC2(),
        BodyC3()
    }

    enum Type {
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
        LeftLeg(),
        
        Custom;

        Supplier<Overlay> factory;

        Type() {
            this.factory = Overlay::new;
        }

        Type(Supplier<Overlay> factory) {
            this.factory = factory;
        }

        public IOverlay create() {
            return factory.get().type(this);
        }

        public static IOverlay create(int type) {
            if (type <= Type.values().length)
                return Type.values()[type].create();

            return null;
        }
    }

    interface TextureFunction {
        String getTexture(OverlayContext ctx);
    }

    interface ColorFunction {
        Color getColor(OverlayContext ctx);
    }

    interface RenderFunction {
        void render(OverlayContext ctx);
    }

    // ── API accessors ──

    /**
     * @return The overlay type (e.g. Face, Chest, ALL)
     */
    Type getType();

    /**
     * @return The color type used by this overlay
     */
    int getColorType();

    /**
     * @return The ARGB color value
     */
    int getColor();

    /**
     * @return The alpha transparency (0.0–1.0)
     */
    float getAlpha();

    /**
     * @return Whether this overlay glows
     */
    boolean isGlow();

    /**
     * @return Whether this overlay is enabled
     */
    boolean isEnabled();

    /**
     * @return The static texture path, may be empty
     */
    String getTexture();

    // ── API mutators ──

    /**
     * @param texture Static texture path
     * @return This overlay for fluent chaining
     */
    IOverlay setTexture(String texture);

    /**
     * @param color ARGB color value
     * @return This overlay for fluent chaining
     */
    IOverlay setColor(int color);

    /**
     * @param alpha Transparency (0.0–1.0)
     * @return This overlay for fluent chaining
     */
    IOverlay setAlpha(float alpha);

    /**
     * @param glow Whether this overlay should glow
     * @return This overlay for fluent chaining
     */
    IOverlay setGlow(boolean glow);

    /**
     * @param enabled Whether this overlay is enabled
     * @return This overlay for fluent chaining
     */
    IOverlay setEnabled(boolean enabled);

    /**
     * @param colorType Ordinal of the {@link ColorType} to use
     * @return This overlay for fluent chaining
     */
    IOverlay setColorType(int colorType);
}
