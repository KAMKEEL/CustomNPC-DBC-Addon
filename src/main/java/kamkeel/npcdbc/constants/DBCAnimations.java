package kamkeel.npcdbc.constants;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcs.util.Register;
import noppes.npcs.controllers.data.Animation;

public class DBCAnimations {
    public static Register.Animations ANIMATIONS = new Register.Animations(CustomNpcPlusDBC.class, "animations","npcdbc");

    public static final Animation NAMEK_REGEN = ANIMATIONS.register("Namek_Regen", Animation::new);

    public static void register() {
        ANIMATIONS.register();
    }
}
