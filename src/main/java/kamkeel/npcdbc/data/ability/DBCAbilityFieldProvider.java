package kamkeel.npcdbc.data.ability;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.controllers.data.ability.IAbilityFieldProvider;
import noppes.npcs.client.gui.builder.FieldDef;

import java.util.List;

/**
 * Injects DBC-specific tabs into ability configuration GUI:
 * - "Icon" tab for all abilities (icon texture and UV settings)
 * - "DBC" tab with Player Settings (ki/stamina cost & drain) and NPC Settings (DBC combat stats)
 */
@SideOnly(Side.CLIENT)
public class DBCAbilityFieldProvider implements IAbilityFieldProvider {
    private static final String TAB_DBC = "DBC";
    private static final String TAB_ICON = "Icon";

    @Override
    public void addFieldDefinitions(Ability ability, List<FieldDef> defs) {
        // Icon tab - skip for NPC inline abilities (only relevant for parent/preset abilities)
        if (!ability.isNpcInlineEdit()) {
            addIconFields(ability, defs);
        }

        // DBC tab - single stats instance shared across all fields
        DBCAbilityStats stats = DBCAbilityStats.fromAbility(ability);

        // Player Settings - always shown
        addPlayerFields(stats, defs);

        // NPC Settings - only for damaging abilities
        if (ability.hasDamage()) {
            addNPCFields(stats, defs);
        }
    }

    private void addIconFields(Ability ability, List<FieldDef> defs) {
        AbilityIconData icon = AbilityIconData.fromAbility(ability);

        // Texture path (URL or resource location)
        defs.add(FieldDef.stringField("ability.icon.texture", icon::getTexture, icon::setTexture)
            .tab(TAB_ICON));

        // UV coordinates section
        defs.add(FieldDef.section("ability.icon.section.uv")
            .tab(TAB_ICON));
        defs.add(FieldDef.intField("ability.icon.x", icon::getIconX, icon::setIconX)
            .tab(TAB_ICON).range(0, 4096));
        defs.add(FieldDef.intField("ability.icon.y", icon::getIconY, icon::setIconY)
            .tab(TAB_ICON).range(0, 4096));

        // Dimensions section
        defs.add(FieldDef.section("ability.icon.section.size")
            .tab(TAB_ICON));
        defs.add(FieldDef.intField("ability.icon.width", icon::getWidth, icon::setWidth)
            .tab(TAB_ICON).range(1, 256));
        defs.add(FieldDef.intField("ability.icon.height", icon::getHeight, icon::setHeight)
            .tab(TAB_ICON).range(1, 256));
        defs.add(FieldDef.floatField("ability.icon.scale", icon::getScale, icon::setScale)
            .tab(TAB_ICON).range(0.1f, 10.0f));
    }

    private void addPlayerFields(DBCAbilityStats stats, List<FieldDef> defs) {
        defs.add(FieldDef.section("stats.section.playerSettings")
            .tab(TAB_DBC));
        defs.add(FieldDef.intField("stats.kiCost", stats::getKiCost, stats::setKiCost)
            .tab(TAB_DBC).range(0, Integer.MAX_VALUE));
        defs.add(FieldDef.intField("stats.kiDrain", stats::getKiDrain, stats::setKiDrain)
            .tab(TAB_DBC).range(0, Integer.MAX_VALUE).hover("stats.hover.kiDrain"));
        defs.add(FieldDef.intField("stats.staminaCost", stats::getStaminaCost, stats::setStaminaCost)
            .tab(TAB_DBC).range(0, Integer.MAX_VALUE));
        defs.add(FieldDef.intField("stats.staminaDrain", stats::getStaminaDrain, stats::setStaminaDrain)
            .tab(TAB_DBC).range(0, Integer.MAX_VALUE).hover("stats.hover.staminaDrain"));
    }

    private void addNPCFields(DBCAbilityStats stats, List<FieldDef> defs) {
        // NPC Settings section header
        defs.add(FieldDef.section("stats.section.npcSettings")
            .tab(TAB_DBC));

        // Master toggle
        defs.add(FieldDef.boolField("stats.dbcEnabled", stats::isEnabled, stats::setEnabled)
            .tab(TAB_DBC));

        // Friendly fist
        defs.add(FieldDef.boolField("stats.friendlyFist", stats::isFriendlyFist, stats::setFriendlyFist)
            .tab(TAB_DBC).visibleWhen(stats::isEnabled));
        defs.add(FieldDef.intField("stats.friendlyFistTime", stats::getFriendlyFistAmount, stats::setFriendlyFistAmount)
            .tab(TAB_DBC).range(1, 60)
            .visibleWhen(() -> stats.isEnabled() && stats.isFriendlyFist()));

        // Damage modifiers section
        defs.add(FieldDef.section("stats.section.damageModifiers")
            .tab(TAB_DBC).visibleWhen(stats::isEnabled));
        defs.add(FieldDef.boolField("stats.ignoreDex", stats::isIgnoreDex, stats::setIgnoreDex)
            .tab(TAB_DBC).visibleWhen(stats::isEnabled));
        defs.add(FieldDef.boolField("stats.ignoreBlock", stats::isIgnoreBlock, stats::setIgnoreBlock)
            .tab(TAB_DBC).visibleWhen(stats::isEnabled));
        defs.add(FieldDef.boolField("stats.ignoreEndurance", stats::isIgnoreEndurance, stats::setIgnoreEndurance)
            .tab(TAB_DBC).visibleWhen(stats::isEnabled));
        defs.add(FieldDef.boolField("stats.ignoreKiDamage", stats::isIgnoreKiProtection, stats::setIgnoreKiProtection)
            .tab(TAB_DBC).visibleWhen(stats::isEnabled));
        defs.add(FieldDef.boolField("stats.ignoreFormDefense", stats::isIgnoreFormReduction, stats::setIgnoreFormReduction)
            .tab(TAB_DBC).visibleWhen(stats::isEnabled));

        // Defense penetration
        defs.add(FieldDef.section("stats.section.defensePenetration")
            .tab(TAB_DBC).visibleWhen(stats::isEnabled));
        defs.add(FieldDef.boolField("stats.hasDefensePenetration", stats::hasDefensePenetration, stats::setHasDefensePenetration)
            .tab(TAB_DBC).visibleWhen(stats::isEnabled));
        defs.add(FieldDef.intField("stats.defensePenetration", stats::getDefensePenetration, stats::setDefensePenetration)
            .tab(TAB_DBC).range(0, 100)
            .visibleWhen(() -> stats.isEnabled() && stats.hasDefensePenetration()));
    }
}
