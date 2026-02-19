package kamkeel.npcdbc.constants;

import kamkeel.npcdbc.data.ability.toggle.DBCToggle;
import kamkeel.npcdbc.data.ability.toggle.DBCToggleAbility;
import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.controllers.data.ability.AbilityVariant;
import kamkeel.npcs.controllers.data.ability.AnchorPoint;
import kamkeel.npcs.controllers.data.ability.LockMovementType;
import kamkeel.npcs.controllers.data.ability.type.energy.AbilityBeam;
import kamkeel.npcs.controllers.data.ability.type.energy.AbilityDisc;
import kamkeel.npcs.controllers.data.ability.type.energy.AbilityDome;
import kamkeel.npcs.controllers.data.ability.type.energy.AbilityLaserShot;
import kamkeel.npcs.controllers.data.ability.type.energy.AbilityOrb;
import kamkeel.npcs.util.Register;

public class DBCAbilities {

    public static Register.Abilities ABILITIES = Register.Abilities.create("npcdbc", "DBC Addon");

    // Constants
    private static final String GROUP = "DBC Addon";

    private static final String ORB = "ability.cnpc.orb";
    private static final String BEAM = "ability.cnpc.beam";
    private static final String LASER = "ability.cnpc.laser_shot";
    private static final String DISC = "ability.cnpc.disc";
    private static final String DOME = "ability.cnpc.dome";

    // Toggle Abilities
    public static final Ability FRIENDLY_FIST = ABILITIES.register("friendly_fist", () -> new DBCToggleAbility(DBCToggle.FRIENDLY_FIST));
    public static final Ability SWOOP = ABILITIES.register("swoop", () -> new DBCToggleAbility(DBCToggle.SWOOP));
    public static final Ability KAIOKEN = ABILITIES.register("kaioken", () -> new DBCToggleAbility(DBCToggle.KAIOKEN));
    public static final Ability FUSION = ABILITIES.register("fusion", () -> new DBCToggleAbility(DBCToggle.FUSION));
    public static final Ability KI_FIST = ABILITIES.register("ki_fist", () -> new DBCToggleAbility(DBCToggle.KI_FIST));
    public static final Ability KI_PROTECTION = ABILITIES.register("ki_protection", () -> new DBCToggleAbility(DBCToggle.KI_PROTECTION));
    public static final Ability KI_WEAPON = ABILITIES.register("ki_weapon", () -> new DBCToggleAbility(DBCToggle.KI_WEAPON));
    public static final Ability POTENTIAL_UNLEASHED = ABILITIES.register("potential_unleashed", () -> new DBCToggleAbility(DBCToggle.POTENTIAL_UNLEASHED));
    public static final Ability ULTRA_INSTINCT = ABILITIES.register("ultra_instinct", () -> new DBCToggleAbility(DBCToggle.ULTRA_INSTINCT));
    public static final Ability GOD_OF_DESTRUCTION = ABILITIES.register("god_of_destruction", () -> new DBCToggleAbility(DBCToggle.GOD_OF_DESTRUCTION));

    // Ability Variants

    // Orb Variants
    public static final AbilityVariant KI_BLAST = ABILITIES.registerVariant(ORB, "ability.npcdbc.ki_blast", GROUP, a -> {
        AbilityOrb orb = (AbilityOrb) a;
        a.setName("Energy Blast");
        a.setWindUpTicks(15);
        a.setShowTelegraph(false);
        a.setWindUpAnimationName("EnergyGeneric_Windup");
        a.setActiveAnimationName("EnergyGeneric_Active");
        orb.setHoming(false);
        orb.setOuterColor(0xFFFF00);
        orb.setOrbSpeed(1.0f);
        orb.setRotationSpeed(30f);
        orb.setOuterColorAlpha(1.0f);
        orb.setAnchorOffsetX(0.1f);
        orb.setAnchorOffsetY(0.25f);
    });

    public static final AbilityVariant KI_BLAST_VOLLEY = ABILITIES.registerVariant(ORB, "ability.npcdbc.ki_blast_volley", GROUP, a -> {
        AbilityOrb orb = (AbilityOrb) a;
        a.setName("Energy Blast Volley");
        a.setWindUpTicks(30);
        a.setShowTelegraph(false);
        a.setMaxRange(75.0f);
        a.setBurstEnabled(true);
        a.setBurstAmount(15);
        a.setBurstDelay(5);
        a.setBurstReplayAnimations(false);
        a.setBurstOverlap(true);
        a.setSyncWindupWithAnimation(false);
        a.setWindUpAnimationName("EnergyGeneric_Windup");
        a.setActiveAnimationName("EnergyGeneric_Charge");

        orb.setOrbSpeed(2.0f);
        orb.setRotationSpeed(30f);
        orb.setOuterColorAlpha(1.0f);
        orb.setOrbSize(0.5f);
        orb.setDamage(4.0f);
        orb.setKnockback(0.0f);
        orb.setKnockbackUp(0.0f);
        orb.setOuterColor(0xFFFF00);
        orb.setAnchorOffsetX(0.1f);
        orb.setAnchorOffsetY(0.25f);
    });

    public static final AbilityVariant FINISH_BREAKER = ABILITIES.registerVariant(ORB, "ability.npcdbc.finish_breaker", GROUP, a -> {
        AbilityOrb orb = (AbilityOrb) a;
        a.setName("Finish Breaker");
        a.setWindUpTicks(30);
        a.setShowTelegraph(false);
        a.setMaxRange(75.0f);
        a.setBurstEnabled(true);
        a.setBurstAmount(5);
        a.setBurstDelay(5);
        a.setBurstReplayAnimations(false);
        a.setBurstOverlap(true);
        a.setWindUpAnimationName("FinishBreaker_Windup");
        a.setActiveAnimationName("FinishBreaker_Active");

        orb.setHoming(true);
        orb.setHomingStrength(0.075f);
        orb.setProjectileCount(2);
        orb.setFireDelay(2);
        orb.setOrbSpeed(1.0f);
        orb.setRotationSpeed(30f);
        orb.setOuterColorAlpha(1.0f);
        orb.setOrbSize(1.25f);
        orb.setDamage(4.0f);
        orb.setKnockback(0.0f);
        orb.setKnockbackUp(0.0f);
        orb.setOuterColor(0xFFFF00);

        orb.setAnchorOffsetX(0, 0.1f);
        orb.setAnchorOffsetY(0, 0.25f);
        orb.setAnchorOffsetX(1, -0.1f);
        orb.setAnchorOffsetY(1, 0.25f);
    });

    public static final AbilityVariant BIG_BANG_ATTACK = ABILITIES.registerVariant(ORB, "ability.npcdbc.bigbang_attack", GROUP, a -> {
        AbilityOrb orb = (AbilityOrb) a;
        a.setName("Big Bang Attack");
        a.setWindUpTicks(50);
        a.setShowTelegraph(false);
        a.setWindUpAnimationName("BigBangAttack_Windup");
        a.setActiveAnimationName("BigBangAttack_Active");
        orb.setHoming(false);
        orb.setOuterColor(0x48BBF6);
        orb.setOrbSpeed(1.0f);
        orb.setOrbSize(3f);
        orb.setRotationSpeed(30f);
        orb.setOuterColorAlpha(1.0f);
        orb.setOuterColorWidth(0.1f);
        orb.setAnchorOffsetX(0.075f);
        orb.setAnchorOffsetY(0.25f);
        orb.setAnchorOffsetZ(1.5f);
    });

    public static final AbilityVariant BURNING_ATTACK = ABILITIES.registerVariant(ORB, "ability.npcdbc.burning_attack", GROUP, a -> {
        AbilityOrb orb = (AbilityOrb) a;
        a.setName("Burning Attack");
        a.setWindUpTicks(50);
        a.setShowTelegraph(false);
        a.setWindUpAnimationName("BurningAttack_Windup");
        a.setActiveAnimationName("BurningAttack_Active");
        orb.setAnchorPointEnum(AnchorPoint.FRONT);
        orb.setHoming(false);
        orb.setInnerColor(0xFFEE00);
        orb.setOuterColor(0xFFEE00);
        orb.setOrbSpeed(1.0f);
        orb.setOrbSize(3.5f);
        orb.setOuterColorEnabled(false);
        orb.setRotationSpeed(30f);
        orb.setAnchorOffsetX(0.05f);
        orb.setAnchorOffsetZ(1.0f);
    });

    public static final AbilityVariant DEATH_BALL = ABILITIES.registerVariant(ORB, "ability.npcdbc.death_ball", GROUP, a -> {
        AbilityOrb orb = (AbilityOrb) a;
        a.setName("Death Ball");
        a.setWindUpTicks(50);
        a.setShowTelegraph(false);
        a.setWindUpAnimationName("DeathBall_Windup");
        a.setActiveAnimationName("DeathBall_Active");
        orb.setHoming(false);
        orb.setOrbSpeed(1.0f);
        orb.setOrbSize(1.0f);
        orb.setRotationSpeed(30f);
        orb.setInnerColor(0x30004B);
        orb.setOuterColor(0xE600F6);
        orb.setOuterColorAlpha(1.0f);
        orb.setOuterColorWidth(0.05f);
        orb.setLightningEffect(true);
        orb.setLightningDensity(1.5f);
        orb.setLightningRadius(2.5f);
        orb.setAnchorOffsetY(0.5f);
        orb.setAnchorOffsetX(0.1f);
    });

    public static final AbilityVariant SPIRIT_BOMB = ABILITIES.registerVariant(ORB, "ability.npcdbc.spirit_bomb", GROUP, a -> {
        AbilityOrb orb = (AbilityOrb) a;
        a.setName("Spirit Bomb");
        a.setWindUpTicks(100);
        a.setShowTelegraph(false);
        a.setWindUpAnimationName("SpiritBomb_Windup");
        a.setActiveAnimationName("SpiritBomb_Active");
        orb.setHoming(true);
        orb.setHomingStrength(0.5f);
        orb.setHomingRange(150f);
        orb.setMaxDistance(150f);
        orb.setMaxLifetime(300);
        orb.setOuterColor(0x48BBF6);
        orb.setOrbSpeed(1.0f);
        orb.setOrbSize(1.5f);
        orb.setRotationSpeed(10f);
        orb.setOuterColorAlpha(1.0f);
        orb.setOuterColorWidth(0.5f);
        orb.setAnchorOffsetY(1.0f);
        orb.setAnchorOffsetX(0.25f);
    });

    public static final AbilityVariant LARGE_SPIRIT_BOMB = ABILITIES.registerVariant(ORB, "ability.npcdbc.large_spirit_bomb", GROUP, a -> {
        AbilityOrb orb = (AbilityOrb) a;
        a.setName("Large Spirit Bomb");
        a.setWindUpTicks(150);
        a.setMaxRange(100.0f);
        a.setLockMovement(LockMovementType.WINDUP_AND_ACTIVE);
        a.setShowTelegraph(false);
        a.setWindUpAnimationName("LargeSpiritBomb_Windup");
        a.setActiveAnimationName("LargeSpiritBomb_Active");
        orb.setHoming(true);
        orb.setHomingStrength(0.05f);
        orb.setHomingRange(150f);
        orb.setMaxDistance(300f);
        orb.setMaxLifetime(300);
        orb.setOuterColor(0x48BBF6);
        orb.setOrbSpeed(1.0f);
        orb.setOrbSize(20f);
        orb.setRotationSpeed(7.5f);
        orb.setOuterColorAlpha(1.0f);
        orb.setOuterColorWidth(0.5f);
        orb.setAnchorPointEnum(AnchorPoint.ABOVE_HEAD);
        orb.setAnchorOffsetY(15f);
    });

    public static final AbilityVariant SUPER_SPIRIT_BOMB = ABILITIES.registerVariant(ORB, "ability.npcdbc.super_spirit_bomb", GROUP, a -> {
        AbilityOrb orb = (AbilityOrb) a;
        a.setName("Super Spirit Bomb");
        a.setWindUpTicks(250);
        a.setMaxRange(100.0f);
        a.setLockMovement(LockMovementType.WINDUP_AND_ACTIVE);
        a.setShowTelegraph(false);
        orb.setHoming(true);
        orb.setHomingStrength(0.05f);
        orb.setHomingRange(150f);
        orb.setMaxDistance(150f);
        orb.setMaxLifetime(300);
        orb.setOuterColor(0x48BBF6);
        orb.setOrbSpeed(1.0f);
        orb.setOrbSize(30f);
        orb.setRotationSpeed(7.5f);
        orb.setOuterColorAlpha(1.0f);
        orb.setOuterColorWidth(0.5f);
        orb.setAnchorPointEnum(AnchorPoint.ABOVE_HEAD);
        orb.setAnchorOffsetY(30f);
    });

    public static final AbilityVariant SUPERNOVA = ABILITIES.registerVariant(ORB, "ability.npcdbc.supernova", GROUP, a -> {
        AbilityOrb orb = (AbilityOrb) a;
        a.setName("Supernova");
        a.setWindUpTicks(100);
        a.setMaxRange(100.0f);
        a.setShowTelegraph(false);
        a.setWindUpAnimationName("Supernova_Windup");
        a.setActiveAnimationName("Supernova_Active");
        orb.setAnchorPointEnum(AnchorPoint.ABOVE_HEAD);
        orb.setHoming(false);
        orb.setMaxDistance(150f);
        orb.setMaxLifetime(300);
        orb.setOrbSpeed(1.0f);
        orb.setOrbSize(20f);
        orb.setRotationSpeed(7.5f);
        orb.setInnerColor(0xFFB410);
        orb.setOuterColor(0xE86202);
        orb.setOuterColorAlpha(1.0f);
        orb.setOuterColorWidth(0.1f);
        orb.setAnchorOffsetX(0.6f);
        orb.setAnchorOffsetY(10.0f);
    });

    // Beam Variants
    public static final AbilityVariant KI_WAVE = ABILITIES.registerVariant(BEAM, "ability.npcdbc.ki_wave", GROUP, a -> {
        AbilityBeam beam = (AbilityBeam) a;
        a.setName("Energy Wave");
        a.setWindUpTicks(30);
        a.setShowTelegraph(false);
        a.setWindUpAnimationName("EnergyGeneric_Windup");
        a.setActiveAnimationName("EnergyGeneric_Charge");
        beam.setBeamWidth(1.0f);
        beam.setHeadSize(1.25f);
        beam.setSpeed(1.3f);
        beam.setRotationSpeed(30f);
        beam.setOuterColorAlpha(1.0f);
        beam.setOuterColor(0xFFFF00);
        beam.setHoming(false);
        beam.setAnchorOffsetX(0.1f);
        beam.setAnchorOffsetY(0.25f);
    });

    public static final AbilityVariant KAMEHAMEHA = ABILITIES.registerVariant(BEAM, "ability.npcdbc.kamehameha", GROUP, a -> {
        AbilityBeam beam = (AbilityBeam) a;
        a.setName("Kamehameha");
        a.setWindUpTicks(60);
        a.setShowTelegraph(false);
        a.setWindUpSound("DBC4.cbeam4s");
        a.setActiveSound("DBC4.fbeam4s");
        a.setWindUpAnimationName("Kamehameha_Windup");
        a.setActiveAnimationName("Kamehameha_Active");
        beam.setBeamWidth(2.0f);
        beam.setHeadSize(2.5f);
        beam.setSpeed(1.5f);
        beam.setRotationSpeed(30f);
        beam.setOuterColorAlpha(1.0f);
        beam.setOuterColor(0x00FFFF);
        beam.setHoming(false);
    });

    public static final AbilityVariant MASENKO = ABILITIES.registerVariant(BEAM, "ability.npcdbc.masenko", GROUP, a -> {
        AbilityBeam beam = (AbilityBeam) a;
        a.setName("Masenko");
        a.setWindUpTicks(40);
        a.setShowTelegraph(false);
        a.setWindUpAnimationName("Masenko_Windup");
        a.setActiveAnimationName("Masenko_Active");
        beam.setBeamWidth(1.5f);
        beam.setHeadSize(1.75f);
        beam.setSpeed(1.5f);
        beam.setRotationSpeed(30f);
        beam.setOuterColorAlpha(1.0f);
        beam.setOuterColor(0xFFFF00);
        beam.setHoming(false);
        beam.setAnchorOffsetX(-0.06f);
        beam.setAnchorOffsetY(-0.06f);
        beam.setAnchorOffsetZ(0.5f);
    });

    public static final AbilityVariant GALICK_GUN = ABILITIES.registerVariant(BEAM, "ability.npcdbc.galick_gun", GROUP, a -> {
        AbilityBeam beam = (AbilityBeam) a;
        a.setName("Galick Gun");
        a.setWindUpTicks(40);
        a.setShowTelegraph(false);
        a.setWindUpAnimationName("GalickGun_Windup");
        a.setActiveAnimationName("GalickGun_Active");
        beam.setBeamWidth(2.0f);
        beam.setHeadSize(2.5f);
        beam.setSpeed(1.5f);
        beam.setRotationSpeed(30f);
        beam.setInnerColor(0xDBC2EC);
        beam.setOuterColorAlpha(1.0f);
        beam.setOuterColor(0xC580E5);
        beam.setHoming(false);
        beam.setAnchorOffsetX(-0.2f);
        beam.setAnchorOffsetZ(0.05f);
    });

    public static final AbilityVariant DOUBLE_SUNDAY = ABILITIES.registerVariant(BEAM, "ability.npcdbc.double_sunday", GROUP, a -> {
        AbilityBeam beam = (AbilityBeam) a;
        a.setName("Double Sunday");
        a.setWindUpTicks(30);
        a.setShowTelegraph(false);
        a.setWindUpAnimationName("DoubleSunday_Windup");
        a.setActiveAnimationName("DoubleSunday_Active");
        beam.setProjectileCount(2);
        beam.setFireDelay(0);
        beam.setBeamWidth(1.0f);
        beam.setHeadSize(1.25f);
        beam.setSpeed(1.3f);
        beam.setRotationSpeed(30f);
        beam.setOuterColorAlpha(0.5f);
        beam.setOuterColor(0xFFBBFF);
        beam.setHoming(false);

        beam.setAnchorOffsetX(0, 0.15f);
        beam.setAnchorOffsetY(0, 0.3f);
        beam.setAnchorOffsetZ(0, -0.1f);

        beam.setAnchorOffsetX(1, -0.15f);
        beam.setAnchorOffsetY(1, 0.3f);
        beam.setAnchorOffsetZ(1, -0.1f);
    });

    public static final AbilityVariant FINAL_FLASH = ABILITIES.registerVariant(BEAM, "ability.npcdbc.final_flash", GROUP, a -> {
        AbilityBeam beam = (AbilityBeam) a;
        a.setName("Final Flash");
        a.setWindUpTicks(80);
        a.setShowTelegraph(false);
        a.setWindUpAnimationName("FinalFlash_Windup");
        a.setActiveAnimationName("FinalFlash_Active");
        beam.setProjectileCount(2);
        beam.setFireDelay(0);
        beam.setBeamWidth(3.0f);
        beam.setHeadSize(3.25f);
        beam.setSpeed(1.5f);
        beam.setRotationSpeed(30f);
        beam.setOuterColorAlpha(1.0f);
        beam.setOuterColor(0xFFFF00);
        beam.setLightningEffect(true);
        beam.setLightningDensity(2f);
        beam.setLightningRadius(3f);
        beam.setHoming(false);

        beam.setAnchorOffsetX(0, 0.3f);
        beam.setAnchorOffsetY(0, 0.225f);

        beam.setAnchorOffsetX(1, -0.3f);
        beam.setAnchorOffsetY(1, 0.225f);
    });

    // Laser Variants
    public static final AbilityVariant SPECIAL_BEAM_CANNON = ABILITIES.registerVariant(LASER, "ability.npcdbc.special_beam_cannon", GROUP, a -> {
        AbilityLaserShot laser = (AbilityLaserShot) a;
        a.setName("Special Beam Cannon");
        a.setWindUpTicks(80);
        a.setWindUpAnimationName("SpecialBeamCannon_Windup");
        a.setActiveAnimationName("SpecialBeamCannon_Active");
        laser.setLaserWidth(0.2f);
        laser.setMaxDistance(150.0f);
        laser.setInnerColor(0xFFFF00);
        laser.setOuterColor(0xFF00FF);
        laser.setOuterColorAlpha(1f);
        laser.setLightningEffect(true);
        laser.setLightningDensity(1.25f);
        laser.setLightningRadius(1.25f);
        laser.setAnchorOffsetX(-0.1f);
        laser.setAnchorOffsetY(0.2f);
    });

    public static final AbilityVariant TRIBEAM = ABILITIES.registerVariant(LASER, "ability.npcdbc.tribeam", GROUP, a -> {
        AbilityLaserShot laser = (AbilityLaserShot) a;
        a.setName("Tri-Beam");
        a.setWindUpTicks(50);
        a.setWindUpAnimationName("TriBeam_Windup");
        a.setActiveAnimationName("TriBeam_Active");
        laser.setLaserWidth(1.5f);
        laser.setExpansionSpeed(2.0f);
        laser.setLingerTicks(4);
        laser.setMaxDistance(150.0f);
        laser.setInnerColor(0xFCAE47);
        laser.setOuterColor(0xFF8800);
        laser.setOuterColorAlpha(1.0f);
        laser.setAnchorOffsetZ(0.5f);
    });

    // Disc Variants
    public static final AbilityVariant DESTRUCTO_DISC = ABILITIES.registerVariant(DISC, "ability.npcdbc.destructo_disc", GROUP, a -> {
        AbilityDisc disc = (AbilityDisc) a;
        a.setName("Destructo Disc");
        a.setShowTelegraph(false);
        a.setLockMovement(LockMovementType.WINDUP_AND_ACTIVE);
        disc.setSpeed(1.5f);
        disc.setOuterColor(0xFFDD00);
        disc.setInnerColor(0xFFFF00);
        disc.setRotationSpeed(30f);
        disc.setMaxDistance(35);
        disc.setBoomerang(false);
        disc.setHoming(false);
        disc.setOuterColorWidth(0.1f);
        disc.setDiscRadius(1.5f);
        disc.setAnchorOffsetY(0.25f);
    });

    public static final AbilityVariant DEATH_SAUCER = ABILITIES.registerVariant(DISC, "ability.npcdbc.death_saucer", GROUP, a -> {
        AbilityDisc disc = (AbilityDisc) a;
        a.setName("Death Saucer");
        a.setShowTelegraph(false);
        a.setLockMovement(LockMovementType.WINDUP_AND_ACTIVE);
        a.setWindUpAnimationName("Ability_DiscDual_Windup");
        a.setActiveAnimationName("Ability_DiscDual_Active");
        disc.setProjectileCount(2);
        disc.setFireDelay(5);
        disc.setSpeed(1.6f);
        disc.setDiscRadius(1.5f);
        disc.setMaxDistance(70);
        disc.setHoming(true);
        disc.setHomingRange(60f);
        disc.setHomingStrength(0.2f);
        disc.setOuterColor(0xDE5CBA);
        disc.setOuterColorAlpha(1f);
        disc.setOuterColorWidth(0.85f);
        disc.setInnerColor(0xB3000A);
        disc.setRotationSpeed(40f);
        disc.setAnchorOffsetY(0, 0.3f);
        disc.setAnchorOffsetY(1, 0.3f);
    });

    // Dome Variants
    public static final AbilityVariant ANDROID_BARRIER = ABILITIES.registerVariant(DOME, "ability.npcdbc.android_barrier", GROUP, a -> {
        AbilityDome dome = (AbilityDome) a;
        a.setName("Android Barrier");
        a.setShowTelegraph(false);
        a.setLockMovement(LockMovementType.WINDUP_AND_ACTIVE);
        a.setWindUpAnimationName("AndroidBarrier");
        a.setActiveAnimationName("AndroidBarrier");
        a.setWindUpTicks(10);
        dome.setOuterColor(0x30FFDD);
        dome.setInnerColor(0x30FFDD);
        dome.setInnerAlpha(0);
        dome.setOuterColorAlpha(0.8f);
        dome.setOuterColorWidth(0.1f);
        dome.setDomeRadius(4.0f);
    });

    public static void register() {
        ABILITIES.register();
    }
}
