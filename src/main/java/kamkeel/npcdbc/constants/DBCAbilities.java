package kamkeel.npcdbc.constants;

import kamkeel.npcdbc.data.ability.*;
import kamkeel.npcs.util.Register;
import kamkeel.npcs.controllers.data.ability.Ability;

public class DBCAbilities {
    public static Register.Abilities ABILITIES = Register.Abilities.create("npcdbc", "DBC Addon");

    // BLASTS
    public static final Ability KI_BLAST = ABILITIES.register("ki_blast", AbilityKiBlast::new);
    public static final Ability BIGBANG_ATTACK = ABILITIES.register("bigbang_attack", AbilityBigBangAttack::new);
    public static final Ability BURNING_ATTACK = ABILITIES.register("burning_attack", AbilityBurningAttack::new);
    public static final Ability DEATH_BALL = ABILITIES.register("death_ball", AbilityDeathBall::new);
    public static final Ability SPIRIT_BOMB = ABILITIES.register("spirit_bomb", AbilitySpiritBomb::new);
    public static final Ability LARGE_SPIRIT_BOMB = ABILITIES.register("large_spirit_bomb", AbilityLargeSpiritBomb::new);
    public static final Ability SUPER_SPIRIT_BOMB = ABILITIES.register("super_spirit_bomb", AbilitySuperSpiritBomb::new);
    public static final Ability SUPERNOVA = ABILITIES.register("supernova", AbilitySupernova::new);

    // BEAMS
    public static final Ability KI_WAVE = ABILITIES.register("ki_wave", AbilityKiWave::new);
    public static final Ability DOUBLE_SUNDAY = ABILITIES.register("double_sunday", AbilityDoubleSunday::new);
    public static final Ability KAMEHAMEHA = ABILITIES.register("kamehameha", AbilityKamehameha::new);
    public static final Ability MASENKO = ABILITIES.register("masenko", AbilityMasenko::new);
    public static final Ability GALICK_GUN = ABILITIES.register("galick_gun", AbilityGalickGun::new);
    public static final Ability FINAL_FLASH = ABILITIES.register("final_flash", AbilityFinalFlash::new);

    // LASERS
    public static final Ability SPECIAL_BEAM_CANNON = ABILITIES.register("special_beam_cannon", AbilitySpecialBeamCannon::new);

    // DISKS
    public static final Ability DESTRUCTO_DISC = ABILITIES.register("destructo_disc", AbilityDestructoDisc::new);
    public static final Ability DEATH_SAUCER = ABILITIES.register("death_saucer", AbilityDeathSaucer::new);

    public static void register() {
        ABILITIES.register();
    }
}
