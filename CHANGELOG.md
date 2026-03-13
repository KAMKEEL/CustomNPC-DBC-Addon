# DBC Addon 1.2 Changelog

## Form Stacking
- Custom forms stack with vanilla DBC forms: Kaioken, Ultra Instinct, GoD, Mystic, Legendary, Divine, Majin (with configurable multipliers)
- Custom-to-custom stacking with up to 4 stacking pairs (circular dependency prevention)

## Form Mastery
- Heat system: builds while form is active, configurable max time and per-level multiplier reduction
- Pain system: debuff on descend with heat threshold, configurable duration and multiplier

## DBC Abilities
- 21 ki-based ability variants registered under `npcdbc` namespace:
  - Orb: Ki Blast, Energy Blast Volley, Finish Breaker, Big Bang Attack, Burning Attack, Death Ball, Spirit Bomb, Large Spirit Bomb, Supernova
  - Beam: Ki Wave, Kamehameha, Masenko, Galick Gun, Double Sunday, Final Flash
  - Laser Shot: Special Beam Cannon, Tri-Beam
  - Disc: Destructo Disc, Death Saucer
  - Dome: Android Barrier
  - Effect: Namekian Regeneration
- All ship with pre-configured animations, anchors, colors, and defaults

## DBC Toggle Abilities
- 10 toggleable moves: Friendly Fist, Swoop, Kaioken, Fusion, Ki Fist, Ki Protection, Ki Weapon (Blade/Scythe), Potential Unleashed, Ultra Instinct, GoD
- Mutual exclusivity: Kaioken, Potential Unleashed, UI, GoD

## DBC Damage
- Full DBC damage calculations on abilities (replaces vanilla damage)
- Per-ability stats: Friendly Fist, Ignore Dex/Block/Endurance/Ki Protection/Form Reduction, Defense Penetration (0-100%)
- Covers all ability types: projectiles, barriers, healing, counter/dodge/guard, AOE/zones
- DBC Stats tab in ability editor, accessible via `getAbilityDBCStats()` scripting

## DBC Conditions
- 11 condition types: Ki Threshold, Stamina Threshold, Stat, Race, Class, Level, Form, Fused, Locked On, DBC Effect, Skill
- All support Caster/Target/Both checking

## Custom Skills
- Learnable skills with custom leveling, TP costs, mind costs
- Per-player progress, persistence, client sync
- Skill condition integration, `setSkillLevel()` scripting API

## Overlay System
- Complete rebuild with face + body rendering in unified system
- Overlay chains with conditional rendering, face part disabling
- Body part targeting: Face, Eyebrows, Eye Whites, Eyes, Nose, Mouth, Chest, Arms, Legs, ALL
- Dynamic color sources: Custom, Eye, Hair, Fur, Body Colors (CM/C1/C2/C3), Glow
- Built-in overlays: Pupils, SSJ3 Face, SSJ4 Face + Fur (Daima/Legend), Oozaru Fur, Savior from Heaven, Black Frieza, Namekian, Berserk Eyes
- Overlay GUI renovation with Janino script editor
- Overlay scripts: `getTexture(ctx)`, `getColor(ctx)` with full `OverlayContext`

## DBC Lock-On
- Overhauled client-server sync

## DBC Settings API
- `IDBCSettingsHandler` for programmatic control of DBC player settings

## Other Changes
- Ability info panel with damage values and DBC stats
- Animated effect icons on DBC stat sheet
- Ability ki and stamina costs with per-tick drain
- Multi-set scaling for stacking damage multipliers
- Type-safe enums for DBC Skills, Status Effects, Races, Classes
- `isTransforming()`, `getLevel()` on `IDBCPlayer`
- Instant Transmission dodge cross-dimension handling
- Flight config: turbo speed modifier, vertical dampening, enhanced movement toggle
- Arcosian tail: 3 variants with body-type color inheritance and form color overrides
- DBC model rendering: scrap armor on NPCs, female model support, age/gender overlays, first-person overlays
- Built-in animations: Namek Regen, Fusion, Mouth Laser, Supernova, plus all 21 ki ability animations
- All DBC script hooks registered in CNPC+ Script Editor with autocomplete; new Skill Event hook
- Energy projectile scripting with DBC damage via `modifyEnergyDamage()`
- Easing function API

## Bug Fixes
- Form checking order, ki drain on transform, mind 0.0 multiplication
- UI/Mystic stacking with form wheel, form speed in advanced mode, form arm crash
- SubGui save-on-ESC, form/outline/aura save-to-category
- GL resource leak, outline render artifacts, post-processing pipeline
- SSJ4/SSJ3/eyebrow rendering for female and younger models
- Aura flickering, DBC model tinting, hair animation, female model rendering
- Reflected ki attack colors, model extras, DBC turbo visuals
- Turbo knockback, turbo bounceback in liquid/lava, slab collision
- SubGui display, server-side shop, close-on-ESC for outline/tag screens
- Hover labels on scroll windows, base hair setting
- DBC effect controller init, animated icons on stat sheet
- Restore UI heat crash, `setRace` scripting API
