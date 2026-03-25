package kamkeel.npcdbc.data.race.builder;

import kamkeel.npcdbc.api.Color;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.constants.enums.EnumDBCAttributes;
import kamkeel.npcdbc.constants.enums.EnumDBCClasses;
import kamkeel.npcdbc.constants.enums.EnumDBCStats;
import kamkeel.npcdbc.data.form.BuiltInForm;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.race.display.*;
import kamkeel.npcdbc.data.race.registry.RaceRegistry;
import kamkeel.npcdbc.data.race.progression.FormTree;
import kamkeel.npcdbc.data.race.progression.RaceSkill;
import kamkeel.npcdbc.data.race.registry.RaceRegistry;
import kamkeel.npcdbc.data.race.stats.ClassStats;
import kamkeel.npcdbc.data.race.stats.RaceStats;
import kamkeel.npcs.controllers.data.ability.Ability;
import net.minecraft.util.ResourceLocation;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RaceBuilder {
    private final int id;
    private final String name;
    private final String menuName;

    private RaceRegistry registry;
    private RaceSkill skill;
    private RaceStats stats = new RaceStats();
    private RaceDisplay display = new RaceDisplay();
    private FormTree formTree = null;

    private final String namespace;

    private RaceBuilder(int id, String name, String menuName, String namespace) {
        this.id = id;
        this.name = name;
        this.menuName = menuName;
        this.namespace = namespace;
    }

    public static RaceBuilder create(int id, String name, String menuName, String namespace) {
        return new RaceBuilder(id, name, menuName, namespace);
    }

    public FormTreeBuilder formTree() {
        return new FormTreeBuilder(this, namespace);
    }

    public SkillBuilder skill() {
        return new SkillBuilder(this);
    }

    public SkillBuilder racialSkill() {
        return skill();
    }

    public DisplayBuilder display() {
        return new DisplayBuilder(this);
    }

    public ClassStatsBuilder forClass(EnumDBCClasses raceClass) {
        return new ClassStatsBuilder(this, raceClass);
    }

    public RaceBuilder registry(RaceRegistry registry) {
        this.registry = registry;
        return this;
    }

    public Race build() {
        if (skill == null)
            throw new IllegalStateException("Race '" + name + "' is missing a racial skill.");
        return new Race(id, name, menuName, registry, display, stats, skill, formTree);
    }

    // ══════════════════════════════════════════════════════════
    // Sub-builders
    // ══════════════════════════════════════════════════════════

    public static class SkillBuilder {
        private final RaceBuilder parent;
        private int maxLevel = 5;
        private int defaultTPCost = 100;
        private int defaultMindCost = 10;
        private String displayName = "Super Form";
        private String description = "Super Form";
        private final LinkedHashMap<Integer, RaceSkill.LevelEntry> levelEntries = new LinkedHashMap<Integer, RaceSkill.LevelEntry>();

        SkillBuilder(RaceBuilder parent) { this.parent = parent; }

        public SkillBuilder maxLevel(int level) {
            this.maxLevel = level; return this;
        }

        public SkillBuilder defaultTPCost(int tpCost) {
            this.defaultTPCost = tpCost;
            return this;
        }

        public SkillBuilder defaultMindCost(int mindCost) {
            this.defaultMindCost = mindCost;
            return this;
        }

        public SkillBuilder displayName(String name) {
            this.displayName = name;
            return this;
        }

        public SkillBuilder description(String description) {
            this.description = description;
            return this;
        }

        public SkillBuilder level(int level, IForm form) {
            return level(level, form, defaultTPCost, defaultMindCost);
        }

        public SkillBuilder level(int level, IForm form, int tpCost, int mindCost) {
            if (form == null)
                throw new IllegalArgumentException("Form must not be null for level " + level);
            int formId = form.getID();
            if (formId < 0)
                throw new IllegalArgumentException("Form '" + form.getName() + "' has invalid ID " + formId);
            return level(level, formId, tpCost, mindCost);
        }

        public SkillBuilder level(int level, int formId) {
            return level(level, formId, defaultTPCost, defaultMindCost);
        }

        public SkillBuilder level(int level, int formId, int tpCost, int mindCost) {
            RaceSkill.LevelEntry entry = new RaceSkill.LevelEntry(level, formId, tpCost, mindCost);
            if (levelEntries.containsKey(level))
                throw new IllegalArgumentException("Duplicate racial skill entry for level " + level);
            levelEntries.put(level, entry);
            return this;
        }

        public RaceBuilder and() {
            RaceSkill skill = new RaceSkill(maxLevel);
            skill.setDisplayName(displayName);
            skill.setDescription(description);
            for (RaceSkill.LevelEntry entry : levelEntries.values()) {
                skill.addLevelEntry(entry);
            }
            parent.skill = skill;
            return parent;
        }
    }

    public static class ClassStatsBuilder {
        private final RaceBuilder parent;
        private final EnumDBCClasses raceClass;

        private final Map<EnumDBCAttributes, Integer> initialAttributes = new EnumMap<>(ClassStats.DEFAULT_INITIAL_ATTRIBUTES);
        private final Map<EnumDBCAttributes, Double> attributeMultipliers = new EnumMap<>(ClassStats.DEFAULT_ATTRIBUTE_MULTIPLIERS);
        private final Map<EnumDBCStats, Double> statBonuses = new EnumMap<>(ClassStats.DEFAULT_STAT_BONUSES);
        private final Map<EnumDBCStats, Double> statAttributeMultipliers = new EnumMap<>(ClassStats.DEFAULT_STAT_ATTRIBUTE_MULTIPLIERS);

        ClassStatsBuilder(RaceBuilder parent, EnumDBCClasses raceClass) {
            this.parent = parent;
            this.raceClass = raceClass;
        }

        public ClassStatsBuilder initialAttribute(EnumDBCAttributes attr, int value) {
            initialAttributes.put(attr, value);
            return this;
        }

        public ClassStatsBuilder attributeMultiplier(EnumDBCAttributes attr, double value) {
            attributeMultipliers.put(attr, value);
            return this;
        }

        public ClassStatsBuilder statBonus(EnumDBCStats stat, double value) {
            statBonuses.put(stat, value);
            return this;
        }

        public ClassStatsBuilder statAttributeMultiplier(EnumDBCStats stat, double value) {
            statAttributeMultipliers.put(stat, value);
            return this;
        }

        public ClassStatsBuilder forClass(EnumDBCClasses next) {
            and();
            return new ClassStatsBuilder(parent, next);
        }

        public RaceBuilder and() {
            parent.stats.set(raceClass, new ClassStats(
                initialAttributes,
                attributeMultipliers,
                statBonuses,
                statAttributeMultipliers
            ));
            return parent;
        }
    }

    // ══════════════════════════════════════════════════════════
    // DisplayBuilder
    // ══════════════════════════════════════════════════════════

    public static class DisplayBuilder {
        private final RaceBuilder parent;

        DisplayBuilder(RaceBuilder parent) {
            this.parent = parent;
        }

        // ── Layers ────────────────────────────────────────────

        /**
         * Opens a {@link LayerBuilder} for the named top-level layer.
         * If the layer already exists on the display (e.g. the built-in "body"
         * or "face"), the builder mutates it. Otherwise a new layer is created
         * and registered.
         *
         * <pre>{@code
         * .display()
         *     .layer(ColorLayer.BODY)
         *         .slot(ColorSlot.BODY_C1, "Body Color 1")
         *         .slot(ColorSlot.BODY_C2, "Body Color 2")
         *         .defaultColor(ColorSlot.BODY_CM, 0xFFFFFF)
         *         .and()
         *     .layer(ColorLayer.FACE)
         *         .subLayer(ColorLayer.EYES)
         *             .colorOverride(ColorSlot.LEFT_EYE, 0x0000FF)
         *             .and()
         *         .and()
         *     .and()
         * }</pre>
         */
        public LayerBuilder layer(String layerId) {
            return layer(layerId, layerId);
        }

        public LayerBuilder layer(String layerId, String layerDisplayName) {
            ColorLayer existing = parent.display.getLayer(layerId);
            if (existing != null) return new LayerBuilder(this, existing);
            ColorLayer newLayer = new ColorLayer(layerId, layerDisplayName);
            parent.display.addLayer(newLayer);
            return new LayerBuilder(this, newLayer);
        }

        // ── Body color shorthand ───────────────────────────────

        /**
         * Adds extra body color slots (1–4 total) to the built-in body layer
         * in DBC canonical order: bodycm → bodyc1 → bodyc2 → bodyc3.
         * The body layer always has bodycm — this adds the additional ones.
         * {@link RaceDisplay#syncCreatorMetadata()} derives {@code skinLimits[1]}
         * from the resulting slot count automatically.
         */
        public DisplayBuilder bodyColorSlots(int count) {
            if (count < 1 || count > 4)
                throw new IllegalArgumentException("bodyColorSlots count must be 1-4, got " + count);

            String[][] extras = {
                {},
                {ColorSlot.BODY_C1, "Body Color 1"},
                {ColorSlot.BODY_C2, "Body Color 2"},
                {ColorSlot.BODY_C3, "Body Color 3"}
            };
            ColorLayer bodyLayer = parent.display.getLayer(ColorLayer.BODY);
            for (int i = 1; i < count; i++) {
                bodyLayer.addSlot(extras[i][0], extras[i][1]);
            }
            return this;
        }

        // ── Color presets / overrides / defaults ───────────────

        public DisplayBuilder addColorPreset(ColorPreset preset) {
            parent.display.addColorPreset(preset);
            return this;
        }

        public DisplayBuilder addColorOverride(String slotId, Color color) {
            parent.display.addColorOverride(slotId, color);
            return this;
        }

        public DisplayBuilder defaultColor(String slotId, int color) {
            parent.display.setDefaultColor(slotId, color);
            return this;
        }

        // ── Textures ───────────────────────────────────────────

        public DisplayBuilder addTexture(String slotId, ResourceLocation texture) {
            TextureSlot slot = parent.display.getTextureSlot(slotId);
            if (slot != null)
                slot.add(texture);
            return this;
        }

        // ── Metadata ───────────────────────────────────────────

        public DisplayBuilder renderer(String rendererKey) {
            parent.display.rendererKey = rendererKey;
            return this;
        }

        public DisplayBuilder skinLimits(int bodyType, int colorSlots, int nose, int mouth, int eyes, int eyeColorSlots) {
            parent.display.setSkinLimits(bodyType, colorSlots, nose, mouth, eyes, eyeColorSlots);
            return this;
        }

        public DisplayBuilder genderCount(int count) {
            parent.display.setGenderCount(count);
            return this;
        }

        public DisplayBuilder hairType(String type) {
            parent.display.setHairType(type);
            return this;
        }

        public DisplayBuilder allowedPowerTypes(String types) {
            parent.display.setAllowedPowerTypes(types);
            return this;
        }

        public DisplayBuilder customSkinMode(int mode) {
            parent.display.setCustomSkinMode(mode);
            return this;
        }

        public DisplayBuilder raceAllow(String allow) {
            parent.display.setRaceAllow(allow);
            return this;
        }

        // ── Body states ────────────────────────────────────────

        /**
         * Opens a {@link BodyStateBuilder} for a new body state.
         * Call {@link BodyStateBuilder#and()} to register it and return here.
         *
         * <pre>{@code
         * .display()
         *     .bodyState("ssj", "Super Saiyan")
         *         .layer(ColorLayer.BODY)
         *             .colorOverride(ColorSlot.BODY_CM, 0xFFFFFF)
         *             .and()
         *         .textureOverride(TextureSlot.EYEBROW, 2)
         *         .and()
         *     .and()
         * }</pre>
         */
        public BodyStateBuilder bodyState(String id, String displayName) {
            return new BodyStateBuilder(this, new BodyState(id, displayName));
        }

        public RaceBuilder and() {
            return parent;
        }

        // ══════════════════════════════════════════════════════
        // LayerBuilder — mutates a ColorLayer on the display
        // ══════════════════════════════════════════════════════

        /**
         * Fluent builder that mutates a single {@link ColorLayer} on the display.
         * Obtained via {@link DisplayBuilder#layer(String)}.
         */
        public class LayerBuilder {
            private final DisplayBuilder displayParent;
            private final ColorLayer     layer;

            LayerBuilder(DisplayBuilder displayParent, ColorLayer layer) {
                this.displayParent = displayParent;
                this.layer         = layer;
            }

            /** Declares a color slot on this layer. */
            public LayerBuilder slot(String slotId, String slotDisplayName) {
                layer.addSlot(slotId, slotDisplayName);
                return this;
            }

            /**
             * Sets the default color for a slot in this layer.
             * Stored on the display's default color preset.
             */
            public LayerBuilder defaultColor(String slotId, int color) {
                parent.display.setDefaultColor(slotId, color);
                return this;
            }

            /**
             * Adds a hard color override for a slot in this layer.
             * The color is locked regardless of player choice.
             */
            public LayerBuilder colorOverride(String slotId, Color color) {
                parent.display.addColorOverride(slotId, color);
                return this;
            }

            /** Shorthand for {@link #colorOverride(String, Color)} using a raw int. */
            public LayerBuilder colorOverride(String slotId, int color) {
                return colorOverride(slotId, new Color(color));
            }

            /**
             * Opens a {@link SubLayerBuilder} for a child layer nested under this one.
             * If the sub-layer already exists, the builder mutates it; otherwise it is
             * created and registered.
             */
            public SubLayerBuilder subLayer(String subLayerId) {
                return subLayer(subLayerId, subLayerId);
            }

            public SubLayerBuilder subLayer(String subLayerId, String subLayerDisplayName) {
                ColorLayer existing = layer.getSubLayer(subLayerId);
                if (existing != null) return new SubLayerBuilder(this, existing);
                ColorLayer newSub = new ColorLayer(subLayerId, subLayerDisplayName);
                layer.addSubLayer(newSub);
                return new SubLayerBuilder(this, newSub);
            }

            /** Finalises this layer and returns the {@link DisplayBuilder}. */
            public DisplayBuilder and() {
                return displayParent;
            }
        }

        // ══════════════════════════════════════════════════════
        // SubLayerBuilder — mutates a child ColorLayer
        // ══════════════════════════════════════════════════════

        /**
         * Fluent builder for a sub-layer nested inside a {@link LayerBuilder}.
         * Obtained via {@link LayerBuilder#subLayer(String)}.
         */
        public class SubLayerBuilder {
            private final LayerBuilder layerParent;
            private final ColorLayer   subLayer;

            SubLayerBuilder(LayerBuilder layerParent, ColorLayer subLayer) {
                this.layerParent = layerParent;
                this.subLayer    = subLayer;
            }

            /** Declares a color slot on this sub-layer. */
            public SubLayerBuilder slot(String slotId, String slotDisplayName) {
                subLayer.addSlot(slotId, slotDisplayName);
                return this;
            }

            /** Sets the default color for a slot in this sub-layer. */
            public SubLayerBuilder defaultColor(String slotId, int color) {
                parent.display.setDefaultColor(slotId, color);
                return this;
            }

            /** Adds a hard color override for a slot in this sub-layer. */
            public SubLayerBuilder colorOverride(String slotId, Color color) {
                parent.display.addColorOverride(slotId, color);
                return this;
            }

            /** Shorthand for {@link #colorOverride(String, Color)} using a raw int. */
            public SubLayerBuilder colorOverride(String slotId, int color) {
                return colorOverride(slotId, new Color(color));
            }

            /** Finalises this sub-layer and returns the parent {@link LayerBuilder}. */
            public LayerBuilder and() {
                return layerParent;
            }
        }

        // ══════════════════════════════════════════════════════
        // BodyStateBuilder
        // ══════════════════════════════════════════════════════

        /**
         * Fluent builder for a single {@link BodyState}.
         * Obtained via {@link DisplayBuilder#bodyState(String, String)}.
         */
        public class BodyStateBuilder {
            private final DisplayBuilder displayParent;
            private final BodyState      state;

            BodyStateBuilder(DisplayBuilder displayParent, BodyState state) {
                this.displayParent = displayParent;
                this.state         = state;
            }

            /**
             * Opens a {@link StateLayerBuilder} for a layer declared on this state.
             * If the layer id matches a built-in (e.g. {@link ColorLayer#BODY}), the
             * state's layer will override it during rendering. If the layer does not
             * yet exist on the state, it is created.
             */
            public StateLayerBuilder layer(String layerId) {
                return layer(layerId, layerId);
            }

            public StateLayerBuilder layer(String layerId, String layerDisplayName) {
                ColorLayer existing = state.getLayer(layerId);
                if (existing != null) return new StateLayerBuilder(this, existing);
                ColorLayer newLayer = new ColorLayer(layerId, layerDisplayName);
                state.addLayer(newLayer);
                return new StateLayerBuilder(this, newLayer);
            }

            /** Adds a hard color override on the state (not tied to a specific layer). */
            public BodyStateBuilder colorOverride(String slotId, Color color) {
                state.addColorOverride(slotId, color);
                return this;
            }

            /** Shorthand for {@link #colorOverride(String, Color)} using a raw int. */
            public BodyStateBuilder colorOverride(String slotId, int color) {
                return colorOverride(slotId, new Color(color));
            }

            /** Sets the default color for a slot on this state. */
            public BodyStateBuilder defaultColor(String slotId, int color) {
                state.setDefaultColor(slotId, color);
                return this;
            }

            /**
             * Adds a texture variation to the given slot on this state.
             * If the slot is not yet declared, it is created first.
             */
            public BodyStateBuilder addTexture(String slotId, ResourceLocation texture) {
                state.addTextureVariation(slotId, texture);
                return this;
            }

            /**
             * Forces a specific texture variation index for the given slot when
             * this state is active.
             */
            public BodyStateBuilder textureOverride(String slotId, int variationIndex) {
                state.addTextureOverride(slotId, variationIndex);
                return this;
            }

            /** Registers the state on the display and returns the {@link DisplayBuilder}. */
            public DisplayBuilder and() {
                displayParent.parent.display.addBodyState(state);
                return displayParent;
            }

            // ══════════════════════════════════════════════════
            // StateLayerBuilder — mutates a ColorLayer on a BodyState
            // ══════════════════════════════════════════════════

            /**
             * Fluent builder for a {@link ColorLayer} declared on a {@link BodyState}.
             * Obtained via {@link BodyStateBuilder#layer(String)}.
             */
            public class StateLayerBuilder {
                private final BodyStateBuilder stateParent;
                private final ColorLayer       layer;

                StateLayerBuilder(BodyStateBuilder stateParent, ColorLayer layer) {
                    this.stateParent = stateParent;
                    this.layer       = layer;
                }

                /** Declares a color slot on this state layer. */
                public StateLayerBuilder slot(String slotId, String slotDisplayName) {
                    layer.addSlot(slotId, slotDisplayName);
                    return this;
                }

                /** Sets the default color for a slot in this state layer. */
                public StateLayerBuilder defaultColor(String slotId, int color) {
                    state.setDefaultColor(slotId, color);
                    return this;
                }

                /** Adds a hard color override for a slot in this state layer. */
                public StateLayerBuilder colorOverride(String slotId, Color color) {
                    state.addColorOverride(slotId, color);
                    return this;
                }

                /** Shorthand for {@link #colorOverride(String, Color)} using a raw int. */
                public StateLayerBuilder colorOverride(String slotId, int color) {
                    return colorOverride(slotId, new Color(color));
                }

                /** Finalises this layer and returns the {@link BodyStateBuilder}. */
                public BodyStateBuilder and() {
                    return stateParent;
                }
            }
        }
    }

    // ══════════════════════════════════════════════════════════
    // FormTreeBuilder
    // ══════════════════════════════════════════════════════════

    public static class FormTreeBuilder {
        private final RaceBuilder parent;
        private final FormTree formTree;

        FormTreeBuilder(RaceBuilder parent, String raceNamespace) {
            this.parent = parent;
            this.formTree = new FormTree(raceNamespace);
        }

        public LevelBuilder level(int level) {
            return new LevelBuilder(this, level);
        }

        public RaceBuilder and() {
            parent.formTree = formTree;
            return parent;
        }

        public FormTree getFormTree() {
            return formTree;
        }

        public class LevelBuilder {
            private final FormTreeBuilder parent;
            private final int level;

            LevelBuilder(FormTreeBuilder parent, int level) {
                this.parent = parent;
                this.level = level;
            }

            public LevelBuilder add(BuiltInForm form) {
                parent.formTree.add(level, form);
                return this;
            }

            public LevelBuilder level(int nextLevel) {
                return new LevelBuilder(parent, nextLevel);
            }

            public RaceBuilder and() {
                return parent.and();
            }
        }
    }
}
