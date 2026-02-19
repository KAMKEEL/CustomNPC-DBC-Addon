package kamkeel.npcdbc.data.ability;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.gui.component.SubGuiSelectForm;
import kamkeel.npcdbc.constants.enums.AbilityDamageType;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.util.DBCUtils;
import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.controllers.data.ability.IAbilityFieldProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.gui.builder.FieldDef;
import noppes.npcs.client.gui.select.GuiAnimationSelection;
import noppes.npcs.controllers.AnimationController;
import noppes.npcs.controllers.data.Animation;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Injects DBC-specific tabs into ability configuration GUI:
 * - "Icon" tab for all abilities (icon texture and UV settings)
 * - "DBC" tab with Player Settings (resource costs, damage config) and Universal Settings (DBC combat stats)
 */
@SideOnly(Side.CLIENT)
public class DBCAbilityFieldProvider implements IAbilityFieldProvider {
    private static final String TAB_DBC = "DBC";
    private static final String TAB_ICON = "Icon";

    private static final String[] ATTRIBUTE_NAMES = {"STR", "DEX", "CON", "WIL", "MND", "SPI"};
    private static final String[] STAT_TYPE_NAMES = {"Melee", "Defense", "Body", "Stamina", "Ki Power", "Ki Pool"};

    @Override
    public void addFieldDefinitions(Ability ability, List<FieldDef> defs) {
        // Icon tab - skip for NPC inline abilities (only relevant for parent/preset abilities)
        if (!ability.isNpcInlineEdit()) {
            addIconFields(ability, defs);
        }

        // DBC tab - single stats instance shared across all fields
        DBCAbilityStats stats = DBCAbilityStats.fromAbility(ability);
        AbilityFormData form = AbilityFormData.fromAbility(ability);

        // Player Settings - always shown
        addPlayerFields(stats, defs);

        // Universal Settings - only for damaging abilities
        if (ability.hasDamage()) {
            addUniversalFields(stats, defs);
        }

        addFormFields(ability, defs);
    }

    private void addFormFields(Ability ability, List<FieldDef> defs) {
        AbilityFormData form = AbilityFormData.fromAbility(ability);

        defs.add(FieldDef.section("ability.form.section").tab(TAB_DBC));

        defs.add(formSubGui("ability.form.id", form::getFormID, form::setFormID).tab(TAB_DBC));

        defs.add(FieldDef.row(
            FieldDef.intField("ability.form.transformTick", form::getTransformTick, form::setTransformTick)
                .range(0, ability.getWindUpTicks()),
            FieldDef.intField("ability.form.detransformTick", form::getDetransformTick, form::setDetransformTick)
                .range(0, form.getTransformTick())
        ).tab(TAB_DBC));

        defs.add(FieldDef.row(
            FieldDef.boolField("ability.form.needsFormUnlocked", form::isNeedsFormUnlocked, form::setNeedsFormUnlocked),
            FieldDef.boolField("ability.form.keepTransformed", form::isKeepTransformed, form::setKeepTransformed)
        ).tab(TAB_DBC));

        defs.add(
            FieldDef.boolField("ability.form.activateTurbo", form::isActivateTurbo, form::setActivateTurbo)
                .tab(TAB_DBC)
        );

        defs.add(FieldDef.section("ability.form.kaioken.section").tab(TAB_DBC));

        defs.add(FieldDef.row(
            FieldDef.boolField("ability.form.kaioken", form::isKaioken, form::setKaioken),
            FieldDef.intField("ability.form.kaiokenStage", form::getKaiokenStage, form::setKaiokenStage)
                .visibleWhen(form::isKaioken)
        ).tab(TAB_DBC));

        defs.add(FieldDef.row(
            FieldDef.intField("ability.form.activateKaiokenTick", form::getActivateKaiokenTick, form::setActivateKaiokenTick)
                .range(0, ability.getWindUpTicks()).visibleWhen(form::isKaioken),
            FieldDef.intField("ability.form.deactivateKaiokenTick", form::getDeactivateKaiokenTick, form::setDeactivateKaiokenTick)
                .range(0, ability.getWindUpTicks()).visibleWhen(form::isKaioken)
        ).tab(TAB_DBC));

        defs.add(
            FieldDef.boolField("ability.form.keepKaioken", form::isKeepKaioken, form::setKeepKaioken)
                .visibleWhen(form::isKaioken).tab(TAB_DBC)
        );
    }

    public static FieldDef formSubGui(String label,
                                      Supplier<Integer> idGetter, Consumer<Integer> idSetter) {
        return FieldDef.subGuiField(label,
            () -> new SubGuiSelectForm(-1, false, false),
            gui -> {
                SubGuiSelectForm sel = (SubGuiSelectForm) gui;
                idSetter.accept(sel.selectedFormID);
            })
            .buttonLabel(() -> {
                int id = idGetter.get();
                if (id >= 0) {
                    Form form = FormController.Instance != null
                        ? (Form) FormController.Instance.get(id) : null;
                    String formName = form != null ? form.getName() : "";
                    return formName != null && !formName.isEmpty()
                        ? "(ID: " + id + ") " + formName : "ID: " + id;
                }
                return "gui.none";
            })
            .clearable(() -> idSetter.accept(-1));
    }

    private void addIconFields(Ability ability, List<FieldDef> defs) {
        AbilityIconData icon = AbilityIconData.fromAbility(ability);

        // Texture path (URL or resource location)
        defs.add(FieldDef.stringField("gui.texture", icon::getTexture, icon::setTexture)
            .tab(TAB_ICON));

        // UV coordinates section
        defs.add(FieldDef.section("ability.icon.section.uv")
            .tab(TAB_ICON));
        defs.add(FieldDef.intField("ability.icon.x", icon::getIconX, icon::setIconX)
            .tab(TAB_ICON).range(0, 4096));
        defs.add(FieldDef.intField("ability.icon.y", icon::getIconY, icon::setIconY)
            .tab(TAB_ICON).range(0, 4096));

        // Dimensions section
        defs.add(FieldDef.section("gui.size")
            .tab(TAB_ICON));
        defs.add(FieldDef.intField("gui.width", icon::getWidth, icon::setWidth)
            .tab(TAB_ICON).range(1, 256));
        defs.add(FieldDef.intField("gui.height", icon::getHeight, icon::setHeight)
            .tab(TAB_ICON).range(1, 256));
        defs.add(FieldDef.floatField("gui.scale", icon::getScale, icon::setScale)
            .tab(TAB_ICON).range(0.1f, 10.0f));
    }

    private void addPlayerFields(DBCAbilityStats stats, List<FieldDef> defs) {
        // Player resource costs — each paired with a percent toggle via row()
        defs.add(FieldDef.section("stats.section.playerSettings")
            .tab(TAB_DBC));

        defs.add(FieldDef.row(
            FieldDef.intField("stats.kiCost", stats::getKiCost, stats::setKiCost)
                .range(0, Integer.MAX_VALUE),
            FieldDef.boolField("stats.kiCostPercent", stats::isKiCostPercent, stats::setKiCostPercent)
        ).tab(TAB_DBC));

        defs.add(FieldDef.row(
            FieldDef.intField("stats.kiDrain", stats::getKiDrain, stats::setKiDrain)
                .range(0, Integer.MAX_VALUE).hover("stats.hover.kiDrain"),
            FieldDef.boolField("stats.kiDrainPercent", stats::isKiDrainPercent, stats::setKiDrainPercent)
        ).tab(TAB_DBC));

        defs.add(FieldDef.row(
            FieldDef.intField("stats.staminaCost", stats::getStaminaCost, stats::setStaminaCost)
                .range(0, Integer.MAX_VALUE),
            FieldDef.boolField("stats.staminaCostPercent", stats::isStaminaCostPercent, stats::setStaminaCostPercent)
        ).tab(TAB_DBC));

        defs.add(FieldDef.row(
            FieldDef.intField("stats.staminaDrain", stats::getStaminaDrain, stats::setStaminaDrain)
                .range(0, Integer.MAX_VALUE).hover("stats.hover.staminaDrain"),
            FieldDef.boolField("stats.staminaDrainPercent", stats::isStaminaDrainPercent, stats::setStaminaDrainPercent)
        ).tab(TAB_DBC));

        // Player damage configuration
        defs.add(FieldDef.section("stats.section.playerDamage")
            .tab(TAB_DBC));
        defs.add(FieldDef.enumField("stats.playerDamageType", AbilityDamageType.class,
                () -> AbilityDamageType.fromOrdinal(stats.getPlayerDamageType()),
                (val) -> stats.setPlayerDamageType(val.ordinal()))
            .tab(TAB_DBC));

        // Dynamic description of the currently selected damage type
        defs.add(FieldDef.labelField("stats.damageTypeInfo", () -> {
            switch (stats.getPlayerDamageType()) {
                case 1:
                    return StatCollector.translateToLocal("stats.damageType.desc.flat");
                case 2:
                    return StatCollector.translateToLocal("stats.damageType.desc.melee");
                case 3:
                    return StatCollector.translateToLocal("stats.damageType.desc.ki");
                case 4:
                    return StatCollector.translateToLocal("stats.damageType.desc.cnpc");
                default:
                    return StatCollector.translateToLocal("stats.damageType.desc.default");
            }
        }).tab(TAB_DBC));

        // Flat damage - visible only when FLAT selected
        defs.add(FieldDef.intField("stats.flatDamage", stats::getFlatDamage, stats::setFlatDamage)
            .tab(TAB_DBC).range(0, Integer.MAX_VALUE)
            .visibleWhen(() -> stats.getPlayerDamageType() == 1));

        // Scaling attribute - visible for MELEE and KI only (CNPC has per-set attributes)
        defs.add(FieldDef.stringEnumField("stats.scalingAttribute", ATTRIBUTE_NAMES,
                () -> ATTRIBUTE_NAMES[stats.getScalingAttribute()],
                (val) -> {
                    for (int i = 0; i < ATTRIBUTE_NAMES.length; i++) {
                        if (ATTRIBUTE_NAMES[i].equals(val)) {
                            stats.setScalingAttribute(i);
                            break;
                        }
                    }
                })
            .tab(TAB_DBC)
            .visibleWhen(() -> stats.getPlayerDamageType() == 2 || stats.getPlayerDamageType() == 3));

        // Use Player Settings - visible for MELEE and KI (controls Ki Fist/Ki Weapon/Ki Infuse toggles)
        defs.add(FieldDef.boolField("stats.usePlayerSettings", stats::getUsePlayerSettings, stats::setUsePlayerSettings)
            .tab(TAB_DBC)
            .hover("stats.hover.usePlayerSettings")
            .visibleWhen(() -> stats.getPlayerDamageType() == 2 || stats.getPlayerDamageType() == 3));

        // ═══ CNPC Multi-Set Configuration ═══
        defs.add(FieldDef.intField("stats.cnpc.setCount", stats::getScalingSetCount, stats::setScalingSetCount)
            .tab(TAB_DBC).range(1, 3)
            .visibleWhen(() -> stats.getPlayerDamageType() == 4));

        // Add fields for each CNPC set (0, 1, 2)
        for (int s = 0; s < 3; s++) {
            addCNPCSetFields(stats, defs, s);
        }

        // ═══ Damage Preview ═══
        // FLAT / MELEE / KI — single preview line
        defs.add(FieldDef.labelField("stats.damagePreview", () -> {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            if (player == null) return "";
            float damage = DBCUtils.calculateAbilityAttackDamage(player, stats);
            if (damage <= 0) return "N/A";
            int dt = stats.getPlayerDamageType();
            if (dt == 1) return String.format("FLAT = %,.0f", damage);
            if (dt == 2)
                return String.format("MELEE [%s] = %,.0f", ATTRIBUTE_NAMES[stats.getScalingAttribute()], damage);
            if (dt == 3) return String.format("KI [%s] = %,.0f", ATTRIBUTE_NAMES[stats.getScalingAttribute()], damage);
            return "";
        }).tab(TAB_DBC).visibleWhen(() -> {
            int dt = stats.getPlayerDamageType();
            return dt >= 1 && dt <= 3;
        }));

        // CNPC — per-set preview lines (one label per set)
        for (int s = 0; s < 3; s++) {
            final int set = s;
            defs.add(FieldDef.labelField("stats.cnpc.previewLine", () -> {
                EntityPlayer player = Minecraft.getMinecraft().thePlayer;
                if (player == null) return "";
                String line = DBCUtils.getCNPCSetPreviewLine(player, stats, set, ATTRIBUTE_NAMES, STAT_TYPE_NAMES);
                // Append " +" if not the last active set
                if (set < stats.getScalingSetCount() - 1) line += " +";
                return line;
            }).tab(TAB_DBC).visibleWhen(() ->
                stats.getPlayerDamageType() == 4 && stats.getScalingSetCount() > set));
        }

        // CNPC — total line
        defs.add(FieldDef.labelField("stats.cnpc.previewTotal", () -> {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            if (player == null) return "";
            float damage = DBCUtils.calculateAbilityAttackDamage(player, stats);
            return damage > 0 ? String.format("= %,.0f", damage) : "N/A";
        }).tab(TAB_DBC).visibleWhen(() -> stats.getPlayerDamageType() == 4));
    }

    private void addCNPCSetFields(DBCAbilityStats stats, List<FieldDef> defs, int set) {
        // Visible when CNPC is selected and this set is active
        java.util.function.BooleanSupplier visible = () ->
            stats.getPlayerDamageType() == 4 && stats.getScalingSetCount() > set;

        defs.add(FieldDef.section("stats.section.cnpcSet" + (set + 1))
            .tab(TAB_DBC).visibleWhen(visible));

        // Attribute selector
        defs.add(FieldDef.stringEnumField("stats.cnpc.attribute", ATTRIBUTE_NAMES,
                () -> ATTRIBUTE_NAMES[stats.getAttributeForSet(set)],
                (val) -> {
                    for (int i = 0; i < ATTRIBUTE_NAMES.length; i++) {
                        if (ATTRIBUTE_NAMES[i].equals(val)) {
                            stats.setAttributeForSet(set, i);
                            break;
                        }
                    }
                })
            .tab(TAB_DBC).visibleWhen(visible));

        // Use Stat toggle
        defs.add(FieldDef.boolField("stats.cnpc.useStat",
                () -> stats.isStatEnabledForSet(set),
                (val) -> stats.setStatEnabledForSet(set, val))
            .tab(TAB_DBC).visibleWhen(visible));

        // Stat Type selector - hidden when Use Stat is disabled
        defs.add(FieldDef.stringEnumField("stats.cnpc.statType", STAT_TYPE_NAMES,
                () -> STAT_TYPE_NAMES[stats.getStatTypeForSet(set)],
                (val) -> {
                    for (int i = 0; i < STAT_TYPE_NAMES.length; i++) {
                        if (STAT_TYPE_NAMES[i].equals(val)) {
                            stats.setStatTypeForSet(set, i);
                            break;
                        }
                    }
                })
            .tab(TAB_DBC)
            .visibleWhen(() -> stats.getPlayerDamageType() == 4
                && stats.getScalingSetCount() > set
                && stats.isStatEnabledForSet(set)));

        // Multiplier
        defs.add(FieldDef.floatField("stats.cnpc.multiplier",
                () -> stats.getMultiplierForSet(set),
                (val) -> stats.setMultiplierForSet(set, val))
            .tab(TAB_DBC).range(0.0f, 10.0f).visibleWhen(visible));

        // Ki bonus toggles
        defs.add(FieldDef.boolField("stats.cnpc.kiFist",
                () -> stats.isKiFistForSet(set),
                (val) -> stats.setKiFistForSet(set, val))
            .tab(TAB_DBC).visibleWhen(visible)
            .hover("stats.hover.cnpc.kiFist"));

        defs.add(FieldDef.boolField("stats.cnpc.kiWeapon",
                () -> stats.isKiWeaponForSet(set),
                (val) -> stats.setKiWeaponForSet(set, val))
            .tab(TAB_DBC).visibleWhen(visible)
            .hover("stats.hover.cnpc.kiWeapon"));

        defs.add(FieldDef.boolField("stats.cnpc.kiInfuse",
                () -> stats.isKiInfuseForSet(set),
                (val) -> stats.setKiInfuseForSet(set, val))
            .tab(TAB_DBC).visibleWhen(visible)
            .hover("stats.hover.cnpc.kiInfuse"));
    }

    private void addUniversalFields(DBCAbilityStats stats, List<FieldDef> defs) {
        // Universal Settings section header
        defs.add(FieldDef.section("stats.section.universalSettings")
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
