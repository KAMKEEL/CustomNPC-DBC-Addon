package kamkeel.npcdbc.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EntityDamageSource;

/**
 * Damage source for ability and energy hits dealt by a player.
 * <p>
 * Identical to {@link net.minecraft.util.DamageSource#causePlayerDamage} apart from the damage
 * type. JRMCore treats a source whose type is "player" and whose sourceOfDamage is the attacker
 * as a melee punch, which adds the attacker's melee stat to the hit and charges the punch
 * stamina and Ki Fist energy costs. Abilities pay their own configured costs, so they must not
 * match that test.
 */
public class AbilityDamageSource extends EntityDamageSource {

    public static final String TYPE = "npcdbc.ability";

    public AbilityDamageSource(EntityLivingBase caster) {
        super(TYPE, caster);
    }
}
