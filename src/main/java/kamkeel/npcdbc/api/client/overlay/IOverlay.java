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
        LeftLeg();

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
}
