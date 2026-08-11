package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.JRMCoreH;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import kamkeel.npcdbc.constants.enums.EnumDBCClasses;
import kamkeel.npcdbc.controllers.RaceController;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.race.stats.ClassStats;
import kamkeel.npcdbc.data.race.stats.RaceStatCalculator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Injects custom race stat values into DBC's runtime computation pipeline.
 *
 * <h3>Injection A — {@code stat()}</h3>
 * <p>Intercepts the core stat calculation method that reads all three runtime config arrays
 * ({@code CONFIG_RACES_STATS_MULTI}, {@code CONFIG_RACES_STAT_BONUS},
 * {@code CONFIG_RACES_ATTRIBUTE_MULTI}). A single HEAD inject + cancel replaces all three
 * simultaneously via {@link RaceStatCalculator#computeStat}.
 *
 * <h3>Injection B — {@code attributeStart()}</h3>
 * <p>Intercepts character creation starting attribute lookup
 * ({@code CONFIG_RACES_ATTRIBUTE_START}).
 *
 * <p>No injection into {@code getPlayerAttribute()} is needed here: the attribute
 * calculation for custom races is handled entirely by
 * {@link kamkeel.npcdbc.mixins.late.impl.dbc.MixinJRMCoreH#onGetPlayerAttribute}
 * which delegates to {@link RaceStatCalculator#computeInnerAttribute} for the
 * inner race getter and {@link RaceStatCalculator#computePlayerAttribute} for
 * the full pipeline when no custom form is active.
 */
@Mixin(value = JRMCoreH.class, remap = false)
public class MixinJRMCoreHRaceStats {

    // ─── Injection A: stat() — core runtime stat computation ─────────────

    /**
     * Intercepts {@code stat(Entity, int, int, int, int, int, int, float)} at HEAD.
     *
     * <p>When the player belongs to a custom race (detected via addon-side PlayerDBCInfo,
     * NOT via the {@code race} parameter which is always 0/Human for carrier races), the
     * entire vanilla stat computation is replaced with {@link RaceStatCalculator#computeStat}.
     *
     * <p>This substitutes all three DBC config arrays in one shot:
     * <ul>
     *   <li>{@code CONFIG_RACES_STATS_MULTI}    → ClassStats.statAttributeMultipliers</li>
     *   <li>{@code CONFIG_RACES_STAT_BONUS}     → ClassStats.statBonuses</li>
     *   <li>{@code CONFIG_RACES_ATTRIBUTE_MULTI} → ClassStats.attributeMultipliers</li>
     * </ul>
     */
    @Inject(
        method = "stat(Lnet/minecraft/entity/Entity;IIIIIIF)I",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void npcdbc$injectCustomRaceStat(
        Entity player, int attributeID, int powerType, int stat,
        int attribute, int race, int classID, float skillBonus,
        CallbackInfoReturnable<Integer> cir
    ) {
        // Guard 1: only intercept EntityPlayer — NPCs/mobs also flow through stat()
        if (!(player instanceof EntityPlayer)) return;

        // Guard 2: only DBC Ki power type (powerType=1).
        // Other power types (MC=0, NC=2, SAO=3) use entirely different config arrays
        // (statInc[] hardcoded defaults, statIncBonusRaceDBC, statIncBonusClass) and
        // custom races do not override those.
        if (powerType != 1) return;

        // Resolve ClassStats for this player's active custom race + DBC class.
        // Returns null if the player is not a custom race — vanilla DBC handles it.
        ClassStats stats = RaceStatCalculator.resolveClassStats((EntityPlayer) player, classID);
        if (stats == null) return;

        // Replace the entire stat() computation for this custom race player.
        // The race param is 0 (Human carrier), but we bypass all config array lookups
        // and compute directly from the addon's ClassStats data model.
        cir.setReturnValue(RaceStatCalculator.computeStat((EntityPlayer) player, stats, stat, attributeID, attribute, skillBonus));
    }

    // ─── Injection B: attributeStart() — character creation ──────────────

    /**
     * Intercepts {@code attributeStart(int, int, int, int)} at HEAD.
     *
     * <p>For custom races, selected call sites pass the custom race's addon ID in the
     * {@code race} parameter. This hook resolves that ID via {@link RaceController#get(int)}
     * and returns the correct start attribute without needing a player parameter.
     */
    @Inject(
        method = "attributeStart(IIII)I",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void npcdbc$injectCustomRaceStartAttributes(
        int powerType, int attribute, int race, int classID,
        CallbackInfoReturnable<Integer> cir, @Local(ordinal = 2) LocalIntRef raceParam 
    ) {
        if (powerType != 1) return;

        Race customRace = RaceController.getInstance().get(race);
        if (customRace == null) return;

        ClassStats stats = customRace.stats.get(classID);
        if (stats == null) {
            raceParam.set(0); // IndexOutOfBounds when ClassStats is null  
            return;
        }

        cir.setReturnValue(RaceStatCalculator.getInitialAttribute(stats, attribute));
    }
}
