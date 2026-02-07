package kamkeel.npcdbc.constants;

import kamkeel.npcs.controllers.data.ability.AbilityController;
import kamkeel.npcs.controllers.data.ability.AbilityVariant;
import kamkeel.npcs.controllers.data.ability.AnchorPoint;
import kamkeel.npcs.controllers.data.ability.LockMovementType;
import kamkeel.npcs.controllers.data.ability.type.AbilityBeam;
import kamkeel.npcs.controllers.data.ability.type.AbilityDisc;
import kamkeel.npcs.controllers.data.ability.type.AbilityLaserShot;
import kamkeel.npcs.controllers.data.ability.type.AbilityOrb;

public class DBCAbilities {
    private static final String GROUP = "DBC Addon";

    private static final String ORB = "ability.cnpc.orb";
    private static final String BEAM = "ability.cnpc.beam";
    private static final String LASER = "ability.cnpc.laser_shot";
    private static final String DISC = "ability.cnpc.disc";

    public static void register() {
        registerOrbVariants();
        registerBeamVariants();
        registerLaserVariants();
        registerDiscVariants();
    }

    private static void registerOrbVariants() {
        AbilityController ctrl = AbilityController.Instance;

        // Energy Blast
        ctrl.registerVariant(ORB, new AbilityVariant("ability.npcdbc.ki_blast", GROUP, a -> {
            AbilityOrb orb = (AbilityOrb) a;
            a.setName("Energy Blast");
            a.setWindUpTicks(15);
            a.setShowTelegraph(false);
            orb.setHoming(false);
            orb.setOuterColor(0xFFFF00);
            orb.setOrbSpeed(1.0f);
            orb.setRotationSpeed(30f);
            orb.setOuterColorAlpha(1.0f);
        }));

        // Big Bang Attack
        ctrl.registerVariant(ORB, new AbilityVariant("ability.npcdbc.bigbang_attack", GROUP, a -> {
            AbilityOrb orb = (AbilityOrb) a;
            a.setName("Big Bang Attack");
            a.setWindUpTicks(50);
            a.setShowTelegraph(false);
            orb.setHoming(false);
            orb.setOuterColor(0x48BBF6);
            orb.setOrbSpeed(1.0f);
            orb.setOrbSize(3f);
            orb.setRotationSpeed(30f);
            orb.setOuterColorAlpha(1.0f);
            orb.setOuterColorWidth(0.1f);
        }));

        // Burning Attack
        ctrl.registerVariant(ORB, new AbilityVariant("ability.npcdbc.burning_attack", GROUP, a -> {
            AbilityOrb orb = (AbilityOrb) a;
            a.setName("Burning Attack");
            a.setWindUpTicks(50);
            a.setShowTelegraph(false);
            orb.setHoming(false);
            orb.setInnerColor(0xFFEE00);
            orb.setOuterColor(0xFFEE00);
            orb.setOrbSpeed(1.0f);
            orb.setOrbSize(3.5f);
            orb.setOuterColorEnabled(false);
            orb.setRotationSpeed(30f);
        }));

        // Death Ball
        ctrl.registerVariant(ORB, new AbilityVariant("ability.npcdbc.death_ball", GROUP, a -> {
            AbilityOrb orb = (AbilityOrb) a;
            a.setName("Death Ball");
            a.setWindUpTicks(50);
            a.setShowTelegraph(false);
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
            orb.setAnchorPointEnum(AnchorPoint.ABOVE_HEAD);
        }));

        // Spirit Bomb
        ctrl.registerVariant(ORB, new AbilityVariant("ability.npcdbc.spirit_bomb", GROUP, a -> {
            AbilityOrb orb = (AbilityOrb) a;
            a.setName("Spirit Bomb");
            a.setWindUpTicks(100);
            a.setShowTelegraph(false);
            orb.setHoming(true);
            orb.setHomingStrength(0.05f);
            orb.setHomingRange(150f);
            orb.setMaxDistance(150f);
            orb.setMaxLifetime(300);
            orb.setOuterColor(0x48BBF6);
            orb.setOrbSpeed(1.0f);
            orb.setOrbSize(1.5f);
            orb.setRotationSpeed(10f);
            orb.setOuterColorAlpha(1.0f);
            orb.setOuterColorWidth(0.5f);
        }));

        // Large Spirit Bomb
        ctrl.registerVariant(ORB, new AbilityVariant("ability.npcdbc.large_spirit_bomb", GROUP, a -> {
            AbilityOrb orb = (AbilityOrb) a;
            a.setName("Large Spirit Bomb");
            a.setWindUpTicks(150);
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
            orb.setOrbSize(20f);
            orb.setRotationSpeed(7.5f);
            orb.setOuterColorAlpha(1.0f);
            orb.setOuterColorWidth(0.5f);
            orb.setAnchorPointEnum(AnchorPoint.ABOVE_HEAD);
            orb.setAnchorOffsetY(15f);
        }));

        // Super Spirit Bomb
        ctrl.registerVariant(ORB, new AbilityVariant("ability.npcdbc.super_spirit_bomb", GROUP, a -> {
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
        }));

        // Supernova
        ctrl.registerVariant(ORB, new AbilityVariant("ability.npcdbc.supernova", GROUP, a -> {
            AbilityOrb orb = (AbilityOrb) a;
            a.setName("Supernova");
            a.setWindUpTicks(100);
            a.setMaxRange(100.0f);
            a.setShowTelegraph(false);
            orb.setAnchorPointEnum(AnchorPoint.ABOVE_HEAD);
            orb.setAnchorOffsetY(10f);
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
        }));
    }

    private static void registerBeamVariants() {
        AbilityController ctrl = AbilityController.Instance;

        // Energy Wave
        ctrl.registerVariant(BEAM, new AbilityVariant("ability.npcdbc.ki_wave", GROUP, a -> {
            AbilityBeam beam = (AbilityBeam) a;
            a.setName("Energy Wave");
            a.setWindUpTicks(30);
            a.setShowTelegraph(false);
            a.setWindUpAnimationName("Ability_Orb_Windup");
            a.setActiveAnimationName("Ability_Orb_Active");
            beam.setBeamWidth(1.0f);
            beam.setHeadSize(1.25f);
            beam.setSpeed(1.3f);
            beam.setRotationSpeed(30f);
            beam.setOuterColorAlpha(1.0f);
            beam.setOuterColor(0xFFFF00);
            beam.setHoming(false);
        }));

        // Kamehameha
        ctrl.registerVariant(BEAM, new AbilityVariant("ability.npcdbc.kamehameha", GROUP, a -> {
            AbilityBeam beam = (AbilityBeam) a;
            a.setName("Kamehameha");
            a.setWindUpTicks(60);
            a.setShowTelegraph(false);
            a.setWindUpSound("DBC4.cbeam4s");
            a.setActiveSound("DBC4.fbeam4s");
            beam.setBeamWidth(2.0f);
            beam.setHeadSize(2.5f);
            beam.setSpeed(1.5f);
            beam.setRotationSpeed(30f);
            beam.setOuterColorAlpha(1.0f);
            beam.setOuterColor(0x00FFFF);
            beam.setHoming(false);
        }));

        // Masenko
        ctrl.registerVariant(BEAM, new AbilityVariant("ability.npcdbc.masenko", GROUP, a -> {
            AbilityBeam beam = (AbilityBeam) a;
            a.setName("Masenko");
            a.setWindUpTicks(40);
            a.setShowTelegraph(false);
            beam.setBeamWidth(1.75f);
            beam.setHeadSize(2.25f);
            beam.setSpeed(1.5f);
            beam.setRotationSpeed(30f);
            beam.setOuterColorAlpha(1.0f);
            beam.setOuterColor(0xFFFF00);
            beam.setHoming(false);
        }));

        // Galick Gun
        ctrl.registerVariant(BEAM, new AbilityVariant("ability.npcdbc.galick_gun", GROUP, a -> {
            AbilityBeam beam = (AbilityBeam) a;
            a.setName("Galick Gun");
            a.setWindUpTicks(40);
            a.setShowTelegraph(false);
            beam.setBeamWidth(2.0f);
            beam.setHeadSize(2.5f);
            beam.setSpeed(1.5f);
            beam.setRotationSpeed(30f);
            beam.setInnerColor(0xDBC2EC);
            beam.setOuterColorAlpha(1.0f);
            beam.setOuterColor(0xC580E5);
            beam.setHoming(false);
        }));

        // Double Sunday (dual beam)
        ctrl.registerVariant(BEAM, new AbilityVariant("ability.npcdbc.double_sunday", GROUP, a -> {
            AbilityBeam beam = (AbilityBeam) a;
            a.setName("Double Sunday");
            a.setWindUpTicks(30);
            a.setShowTelegraph(false);
            a.setWindUpAnimationName("Ability_Orb_Windup");
            a.setActiveAnimationName("Ability_Orb_Active");
            beam.setProjectileCount(2);
            beam.setFireDelay(0);
            beam.setBeamWidth(1.0f);
            beam.setHeadSize(1.25f);
            beam.setSpeed(1.3f);
            beam.setRotationSpeed(30f);
            beam.setOuterColorAlpha(0.5f);
            beam.setOuterColor(0xFFBBFF);
            beam.setHoming(false);
        }));

        // Final Flash (dual beam)
        ctrl.registerVariant(BEAM, new AbilityVariant("ability.npcdbc.final_flash", GROUP, a -> {
            AbilityBeam beam = (AbilityBeam) a;
            a.setName("Final Flash");
            a.setWindUpTicks(80);
            a.setShowTelegraph(false);
            a.setWindUpAnimationName("Ability_BeamDual_Windup");
            a.setActiveAnimationName("Ability_BeamDual_Active");
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
        }));
    }

    private static void registerLaserVariants() {
        AbilityController ctrl = AbilityController.Instance;

        // Special Beam Cannon
        ctrl.registerVariant(LASER, new AbilityVariant("ability.npcdbc.special_beam_cannon", GROUP, a -> {
            AbilityLaserShot laser = (AbilityLaserShot) a;
            a.setName("Special Beam Cannon");
            a.setWindUpTicks(80);
            laser.setLaserWidth(0.2f);
            laser.setInnerColor(0xFFFF00);
            laser.setOuterColor(0xFF00FF);
            laser.setOuterColorAlpha(1f);
            laser.setLightningEffect(true);
            laser.setLightningDensity(1.25f);
            laser.setLightningRadius(1.25f);
        }));
    }

    private static void registerDiscVariants() {
        AbilityController ctrl = AbilityController.Instance;

        // Destructo Disc
        ctrl.registerVariant(DISC, new AbilityVariant("ability.npcdbc.destructo_disc", GROUP, a -> {
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
        }));

        // Death Saucer (dual disc)
        ctrl.registerVariant(DISC, new AbilityVariant("ability.npcdbc.death_saucer", GROUP, a -> {
            AbilityDisc disc = (AbilityDisc) a;
            a.setName("Death Saucer");
            a.setShowTelegraph(false);
            a.setLockMovement(LockMovementType.WINDUP_AND_ACTIVE);
            a.setWindUpAnimationName("Ability_DiscDual_Windup");
            a.setActiveAnimationName("Ability_DiscDual_Active");
            disc.setProjectileCount(2);
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
        }));
    }
}
