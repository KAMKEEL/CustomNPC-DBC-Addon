package kamkeel.npcdbc.constants;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcs.util.Register;
import noppes.npcs.controllers.data.Animation;
import noppes.npcs.controllers.data.BuiltInAnimation;

public class DBCAnimations {
    public static Register.Animations ANIMATIONS = Register.Animations.create(CustomNpcPlusDBC.class, "animations","npcdbc");

    public static final Animation NAMEK_REGEN = ANIMATIONS.register("Namek_Regen", BuiltInAnimation::new);
    public static final Animation[] KAMEHAMEHA = ANIMATIONS.registerBundle("Kamehameha", BuiltInAnimation::new,
        "Windup", "Charge", "Active");

    public static void register() {
        ANIMATIONS.register();
    }
}
