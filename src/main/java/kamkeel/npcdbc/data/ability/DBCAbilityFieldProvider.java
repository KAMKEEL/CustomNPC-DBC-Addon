package kamkeel.npcdbc.data.ability;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.controllers.data.ability.IAbilityFieldProvider;
import noppes.npcs.client.gui.builder.FieldDef;

import java.util.List;

/**
 * Injects a "DBC" tab into damaging abilities' configuration GUI.
 * The tab contains DBC combat stat modifiers that control how ability
 * damage is processed through the DBC damage system.
 */
@SideOnly(Side.CLIENT)
public class DBCAbilityFieldProvider implements IAbilityFieldProvider {
    private static final String TAB = "DBC";

    @Override
    public void addFieldDefinitions(Ability ability, List<FieldDef> defs) {
        if (!ability.hasDamage()) return;

        DBCAbilityStats stats = DBCAbilityStats.fromAbility(ability);

        // Master toggle
        defs.add(FieldDef.boolField("stats.dbcEnabled", stats::isEnabled, stats::setEnabled)
            .tab(TAB));

        // Friendly fist
        defs.add(FieldDef.boolField("stats.friendlyFist", stats::isFriendlyFist, stats::setFriendlyFist)
            .tab(TAB).visibleWhen(stats::isEnabled));
        defs.add(FieldDef.intField("stats.friendlyFistTime", stats::getFriendlyFistAmount, stats::setFriendlyFistAmount)
            .tab(TAB).range(1, 60)
            .visibleWhen(() -> stats.isEnabled() && stats.isFriendlyFist()));

        // Damage modifiers section
        defs.add(FieldDef.section("stats.section.damageModifiers")
            .tab(TAB).visibleWhen(stats::isEnabled));
        defs.add(FieldDef.boolField("stats.ignoreDex", stats::isIgnoreDex, stats::setIgnoreDex)
            .tab(TAB).visibleWhen(stats::isEnabled));
        defs.add(FieldDef.boolField("stats.ignoreBlock", stats::isIgnoreBlock, stats::setIgnoreBlock)
            .tab(TAB).visibleWhen(stats::isEnabled));
        defs.add(FieldDef.boolField("stats.ignoreEndurance", stats::isIgnoreEndurance, stats::setIgnoreEndurance)
            .tab(TAB).visibleWhen(stats::isEnabled));
        defs.add(FieldDef.boolField("stats.ignoreKiDamage", stats::isIgnoreKiProtection, stats::setIgnoreKiProtection)
            .tab(TAB).visibleWhen(stats::isEnabled));
        defs.add(FieldDef.boolField("stats.ignoreFormDefense", stats::isIgnoreFormReduction, stats::setIgnoreFormReduction)
            .tab(TAB).visibleWhen(stats::isEnabled));

        // Defense penetration
        defs.add(FieldDef.section("stats.section.defensePenetration")
            .tab(TAB).visibleWhen(stats::isEnabled));
        defs.add(FieldDef.boolField("stats.hasDefensePenetration", stats::hasDefensePenetration, stats::setHasDefensePenetration)
            .tab(TAB).visibleWhen(stats::isEnabled));
        defs.add(FieldDef.intField("stats.defensePenetration", stats::getDefensePenetration, stats::setDefensePenetration)
            .tab(TAB).range(0, 100)
            .visibleWhen(() -> stats.isEnabled() && stats.hasDefensePenetration()));
    }
}
