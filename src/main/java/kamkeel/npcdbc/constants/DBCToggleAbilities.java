package kamkeel.npcdbc.constants;

import kamkeel.npcdbc.data.ability.toggle.*;
import kamkeel.npcs.controllers.data.ability.AbilityController;

/**
 * Registers DBC toggle abilities as built-in (non-customizable) player-only abilities.
 * These toggle various DBC settings when activated.
 */
public class DBCToggleAbilities {

    // Toggle Abilities
    public static final AbilityKiFist KI_FIST = new AbilityKiFist();
    public static final AbilitySwoop SWOOP = new AbilitySwoop();
    public static final AbilityKiProtection KI_PROTECTION = new AbilityKiProtection();
    public static final AbilityFriendlyFist FRIENDLY_FIST = new AbilityFriendlyFist();
    public static final AbilityKiWeapon KI_WEAPON = new AbilityKiWeapon();

    // Transformation Toggles
    public static final AbilityKaioken KAIOKEN = new AbilityKaioken();
    public static final AbilityPotentialUnleashed POTENTIAL_UNLEASHED = new AbilityPotentialUnleashed();
    public static final AbilityUltraInstinct ULTRA_INSTINCT = new AbilityUltraInstinct();
    public static final AbilityGodOfDestruction GOD_OF_DESTRUCTION = new AbilityGodOfDestruction();
    public static final AbilityFusion FUSION = new AbilityFusion();

    /**
     * Register all toggle abilities with the AbilityController.
     * Call this during mod initialization.
     */
    public static void register() {
        AbilityController controller = AbilityController.Instance;

        // Combat Toggles
        controller.registerAbility("Ki Fist", KI_FIST);
        controller.registerAbility("Swoop", SWOOP);
        controller.registerAbility("Ki Protection", KI_PROTECTION);
        controller.registerAbility("Friendly Fist", FRIENDLY_FIST);
        controller.registerAbility("Ki Weapon", KI_WEAPON);

        // Transformation Toggles
        controller.registerAbility("Kaioken", KAIOKEN);
        controller.registerAbility("Potential Unleashed", POTENTIAL_UNLEASHED);
        controller.registerAbility("Ultra Instinct", ULTRA_INSTINCT);
        controller.registerAbility("God of Destruction", GOD_OF_DESTRUCTION);
        controller.registerAbility("Fusion", FUSION);
    }
}
