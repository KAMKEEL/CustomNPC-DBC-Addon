package kamkeel.npcdbc.constants;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcs.util.Register;
import noppes.npcs.controllers.AnimationController;
import noppes.npcs.controllers.data.Animation;
import noppes.npcs.controllers.data.BuiltInAnimation;

public class DBCAnimations {
    private static final String NAMESPACE = CustomNpcPlusDBC.ID;
    public static Register.Animations ANIMATIONS = Register.Animations.create(CustomNpcPlusDBC.class, "animations", NAMESPACE);

    public static final Animation NAMEK_REGEN = ANIMATIONS.register("NamekRegen", BuiltInAnimation::new);

    public static final Animation[] FUSION = ANIMATIONS.registerBundle("Fusion", BuiltInAnimation::new,
        "Left", "Right");

    public static final Animation[] BIG_BANG_ATTACK = ANIMATIONS.registerBundle("BigBangAttack", BuiltInAnimation::new,
        "Windup", "Active");
    public static final Animation[] BURNING_ATTACK = ANIMATIONS.registerBundle("BurningAttack", BuiltInAnimation::new,
        "Windup", "Windup_Slower", "Active");
    public static final Animation[] DEATH_BALL = ANIMATIONS.registerBundle("DeathBall", BuiltInAnimation::new,
        "Windup", "Active");
    public static final Animation[] ENERGY_BLAST = ANIMATIONS.registerBundle("EnergyBlast", BuiltInAnimation::new,
        "Windup", "Windup_Looped", "Active", "Active_Looped");
    public static final Animation[] SPIRIT_BOMB = ANIMATIONS.registerBundle("SpiritBomb", BuiltInAnimation::new,
        "Windup", "Active");
    public static final Animation[] LARGE_SPIRIT_BOMB = ANIMATIONS.registerBundle("LargeSpiritBomb", BuiltInAnimation::new,
        "Windup", "Active");

    public static final Animation[] ENERGY_WAVE = ANIMATIONS.registerBundle("EnergyWave", BuiltInAnimation::new,
        "Windup", "Active");
    public static final Animation[] KAMEHAMEHA = ANIMATIONS.registerBundle("Kamehameha", BuiltInAnimation::new,
        "Windup", "Active");
    public static final Animation[] MASENKO = ANIMATIONS.registerBundle("Masenko", BuiltInAnimation::new,
        "Windup", "Active");
    public static final Animation[] GALICK_GUN = ANIMATIONS.registerBundle("GalickGun", BuiltInAnimation::new,
        "Windup", "Active");
    public static final Animation[] DOUBLE_SUNDAY = ANIMATIONS.registerBundle("DoubleSunday", BuiltInAnimation::new,
        "Windup", "Active");
    public static final Animation[] FINAL_FLASH = ANIMATIONS.registerBundle("FinalFlash", BuiltInAnimation::new,
        "Windup", "Active");

    public static final Animation[] SPECIAL_BEAM_CANNON = ANIMATIONS.registerBundle("SpecialBeamCannon", BuiltInAnimation::new,
        "Windup", "Active");
    public static final Animation[] TRIBEAM = ANIMATIONS.registerBundle("TriBeam", BuiltInAnimation::new,
        "Windup", "Active");

    public static void register() {
        AnimationController.Instance.addAnimationRegister(NAMESPACE, ANIMATIONS);
    }
}
