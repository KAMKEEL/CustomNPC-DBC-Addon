package kamkeel.npcdbc.data.ability;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.gui.component.SubGuiSelectDBCEffect;
import kamkeel.npcdbc.client.gui.component.SubGuiSelectForm;
import kamkeel.npcdbc.client.gui.component.SubGuiSelectSkill;
import kamkeel.npcdbc.constants.DBCSkills;
import kamkeel.npcdbc.constants.DBCStatusEffects;
import kamkeel.npcdbc.constants.enums.EnumAbilityDamageType;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.controllers.SkillController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.skill.CustomSkill;
import kamkeel.npcdbc.util.DBCUtils;
import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.controllers.data.ability.gui.IAbilityFieldProvider;
import kamkeel.npcs.controllers.data.ability.type.AbilityEffect;
import kamkeel.npcs.controllers.data.ability.type.energy.AbilityBarrier;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.gui.builder.FieldDef;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Injects DBC-specific tabs into ability configuration GUI:
 * - "DBC" tab with Player Settings (resource costs, damage config) and Universal Settings (DBC combat stats)
 */
@SideOnly(Side.CLIENT)
public class DBCAbilityFieldProvider implements IAbilityFieldProvider {
    private static final String TAB_DBC = "DBC";

    private static final String[] ATTRIBUTE_NAMES = {"STR", "DEX", "CON", "WIL", "MND", "SPI"};
    private static final String[] STAT_TYPE_NAMES = {"Melee", "Defense", "Body", "Stamina", "Ki Power", "Ki Pool"};

    @Override
    public void addFieldDefinitions(Ability ability, List<FieldDef> defs) {
        // DBC tab - single stats instance shared across all fields
        DBCAbilityStats stats = DBCAbilityStats.fromAbility(ability);
        boolean isDamaging = ability.hasDamage()
            && !(ability instanceof AbilityBarrier)
            && !(ability instanceof AbilityEffect);

        // Resource costs (ki/stamina) - always shown
        addResourceCostFields(stats, defs);

        // Player damage configuration - only for damaging abilities
        if (isDamaging) {
            addPlayerDamageFields(stats, defs);
        }

        // Universal Settings (Ignore Dex, Friendly Fist, etc.) - only for damaging abilities
        if (isDamaging) {
            addUniversalFields(stats, defs);
        }

        // Barrier Health Scaling - only for barrier abilities
        if (ability instanceof AbilityBarrier) {
            addBarrierFields(stats, defs);
        }

        // DBC Healing - only for effect abilities
        if (ability instanceof AbilityEffect) {
            addHealingFields(stats, defs);
        }
    }

    private void addResourceCostFields(DBCAbilityStats stats, List<FieldDef> defs) {
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
    }

    private void addPlayerDamageFields(DBCAbilityStats stats, List<FieldDef> defs) {
        // Player damage configuration
        defs.add(FieldDef.section("stats.section.playerDamage")
            .tab(TAB_DBC));
        defs.add(FieldDef.enumField("stats.playerDamageType", EnumAbilityDamageType.class,
                () -> EnumAbilityDamageType.fromOrdinal(stats.getPlayerDamageType()),
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

        // Player DBC Stats - visible for MELEE, KI, and CNPC; greyed out when Universal DBC Stats is disabled
        defs.add(FieldDef.boolField("stats.playerDBCStats", stats::getUsePlayerSettings, stats::setUsePlayerSettings)
            .tab(TAB_DBC)
            .hover("stats.hover.playerDBCStats")
            .visibleWhen(() -> {
                int dt = stats.getPlayerDamageType();
                return dt == 2 || dt == 3 || dt == 4;
            })
            .enabledWhen(stats::isEnabled));

        // ═══ CNPC Multi-Set Configuration ═══
        BooleanSupplier cnpcVisible = () -> stats.getPlayerDamageType() == 4;
        addScalingSetConfig(stats, defs, cnpcVisible);

        // ═══ Damage Preview (FLAT / MELEE / KI only — CNPC preview is in addScalingSetConfig) ═══
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
    }

    /**
     * Adds set count + per-set config fields + per-set preview lines, gated by the given visibility.
     * Reused by Player Damage (CNPC type), Barrier Health Scaling, and DBC Healing.
     */
    private void addScalingSetConfig(DBCAbilityStats stats, List<FieldDef> defs,
                                     BooleanSupplier visibleBase) {
        defs.add(FieldDef.intField("stats.cnpc.setCount", stats::getScalingSetCount, stats::setScalingSetCount)
            .tab(TAB_DBC).range(1, 3)
            .visibleWhen(visibleBase));

        for (int s = 0; s < 3; s++) {
            addCNPCSetFields(stats, defs, s, visibleBase);
        }

        // Per-set preview lines
        for (int s = 0; s < 3; s++) {
            final int set = s;
            defs.add(FieldDef.labelField("stats.cnpc.previewLine", () -> {
                EntityPlayer player = Minecraft.getMinecraft().thePlayer;
                if (player == null) return "";
                String line = DBCUtils.getCNPCSetPreviewLine(player, stats, set, ATTRIBUTE_NAMES, STAT_TYPE_NAMES);
                if (set < stats.getScalingSetCount() - 1) line += " +";
                return line;
            }).tab(TAB_DBC).visibleWhen(() ->
                visibleBase.getAsBoolean() && stats.getScalingSetCount() > set));
        }

        // Total value preview
        defs.add(FieldDef.labelField("stats.cnpc.previewTotal", () -> {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            if (player == null) return "";
            float value = DBCUtils.calculateScalingSets(player, stats);
            return value > 0 ? String.format("= %,.0f", value) : "N/A";
        }).tab(TAB_DBC).visibleWhen(visibleBase));
    }

    private void addCNPCSetFields(DBCAbilityStats stats, List<FieldDef> defs, int set,
                                   BooleanSupplier visibleBase) {
        BooleanSupplier visible = () ->
            visibleBase.getAsBoolean() && stats.getScalingSetCount() > set;

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
            .visibleWhen(() -> visibleBase.getAsBoolean()
                && stats.getScalingSetCount() > set
                && stats.isStatEnabledForSet(set)));

        // Multiplier
        defs.add(FieldDef.floatField("stats.cnpc.multiplier",
                () -> stats.getMultiplierForSet(set),
                (val) -> stats.setMultiplierForSet(set, val))
            .tab(TAB_DBC).range(0.0f, 500.0f).visibleWhen(visible));

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

    private void addBarrierFields(DBCAbilityStats stats, List<FieldDef> defs) {
        defs.add(FieldDef.section("stats.section.barrierScaling")
            .tab(TAB_DBC));

        defs.add(FieldDef.boolField("stats.barrierScalingEnabled",
                () -> stats.barrierScalingEnabled, (val) -> { stats.barrierScalingEnabled = val; stats.save(); })
            .tab(TAB_DBC).hover("stats.hover.barrierScaling"));

        // Scaling set config (set count + per-set fields + previews)
        addScalingSetConfig(stats, defs, () -> stats.barrierScalingEnabled);
    }

    private void addHealingFields(DBCAbilityStats stats, List<FieldDef> defs) {
        defs.add(FieldDef.section("stats.section.healing")
            .tab(TAB_DBC));

        String[] healingModes = {"Flat Body HP", "Percent of Max Body"};
        defs.add(FieldDef.stringEnumField("stats.healingMode", healingModes,
                () -> healingModes[stats.getHealingMode()],
                (val) -> stats.setHealingMode(val.equals(healingModes[0]) ? 0 : 1))
            .tab(TAB_DBC).hover("stats.hover.healingMode"));

        defs.add(FieldDef.boolField("stats.healScalingEnabled",
                () -> stats.healScalingEnabled, (val) -> { stats.healScalingEnabled = val; stats.save(); })
            .tab(TAB_DBC).hover("stats.hover.healScaling"));

        // Scaling set config (set count + per-set fields + previews)
        addScalingSetConfig(stats, defs, () -> stats.healScalingEnabled);
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

    public static FieldDef formSubGui(String label,
                                      Supplier<Integer> idGetter, Consumer<Integer> idSetter) {
        return FieldDef.subGuiField(label, () -> {
                    SubGuiSelectForm gui = new SubGuiSelectForm(-1, false, false);
                    return gui;
                }, gui -> {
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

    public static FieldDef skillSubGui(String label,
                                       Supplier<Integer> idGetter,    Consumer<Integer> idSetter,
                                       Supplier<Integer> modeGetter,  Consumer<Integer> modeSetter) {
        return FieldDef.subGuiField(label, () -> {
                int currentId = idGetter.get();
                int currentMode = modeGetter != null ? modeGetter.get() : SubGuiSelectSkill.MODE_DBC;
                return new SubGuiSelectSkill(currentId, currentMode);
            }, gui -> {
                SubGuiSelectSkill sel = (SubGuiSelectSkill) gui;
                if (sel.getSelectedMode() >= 0) {
                    idSetter.accept(sel.getSelectedSkillId());
                    if (modeSetter != null)
                        modeSetter.accept(sel.getSelectedMode());
                }
            })
            .buttonLabel(() -> {
                int id = idGetter.get();
                int mode = modeGetter != null ? modeGetter.get() : SubGuiSelectSkill.MODE_DBC;
                if (id >= 0) {
                    if (mode == SubGuiSelectSkill.MODE_DBC) {
                        DBCSkills skill = DBCSkills.byIndex(id);
                        String name = skill != null ? skill.name() : "";
                        return !name.isEmpty() ? "[DBC] (ID: " + id + ") " + name : "[DBC] ID: " + id;
                    } else {
                        CustomSkill skill = SkillController.Instance != null
                            ? SkillController.Instance.customSkills.get(id) : null;
                        String name = skill != null ? skill.stringLiteralId : "";
                        return !name.isEmpty() ? "[Custom] (ID: " + id + ") " + name : "[Custom] ID: " + id;
                    }
                }
                return "gui.none";
            })
            .clearable(() -> {
                idSetter.accept(-1);
                if (modeSetter != null)
                    modeSetter.accept(SubGuiSelectSkill.MODE_DBC);
            });
    }

    public static FieldDef statusEffectSubGui(String label,
                                              Supplier<Integer> ordinalGetter, Consumer<Integer> ordinalSetter,
                                              Supplier<Integer> modeGetter,    Consumer<Integer> modeSetter) {
        return FieldDef.subGuiField(label, () -> {
                int currentOrdinal = ordinalGetter.get();
                int currentMode = modeGetter != null ? modeGetter.get() : SubGuiSelectDBCEffect.MODE_PERMANENT;
                return new SubGuiSelectDBCEffect(currentOrdinal, currentMode);
            }, gui -> {
                if (gui.getSelectedMode() >= 0) {
                    ordinalSetter.accept(gui.getSelectedOrdinal());
                    if (modeSetter != null)
                        modeSetter.accept(gui.getSelectedMode());
                }
            })
            .buttonLabel(() -> {
                int ordinal = ordinalGetter.get();
                if (ordinal >= 0) {
                    DBCStatusEffects effect = DBCStatusEffects.byOrdinal(ordinal);
                    String name = effect != null ? effect.name() : "";
                    return !name.isEmpty() ? name : "ID: " + ordinal;
                }
                return "gui.none";
            })
            .clearable(() -> {
                ordinalSetter.accept(-1);
                if (modeSetter != null)
                    modeSetter.accept(SubGuiSelectDBCEffect.MODE_PERMANENT);
            });
    }
}
