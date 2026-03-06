package kamkeel.npcdbc.data.ability.toggle;

import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.controllers.data.ability.enums.UserType;
import kamkeel.npcs.controllers.data.telegraph.TelegraphType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.data.PlayerData;

/**
 * Concrete toggle ability parameterized by {@link DBCToggle} enum.
 * Immutable built-in — each instance maps to exactly one DBC setting.
 * <p>
 * All DBC state changes go through {@link #onToggle} which delegates
 * to {@link DBCToggle#applyState}. This handles both simple (on/off) and
 * multi-state (cycling) toggles uniformly.
 */
public class DBCToggleAbility extends Ability {
    private final DBCToggle toggle;

    public DBCToggleAbility(DBCToggle toggle) {
        configureAsBuiltIn("npcdbc:" + toggle.key);
        this.name = toggle.displayName.replace(" ", "_");
        this.displayName = toggle.displayName;
        this.toggleStates = toggle.getToggleStates();
        this.telegraphType = TelegraphType.NONE;
        this.showTelegraph = false;
        this.allowedBy = UserType.PLAYER_ONLY;
        this.toggle = toggle;

        // Set state labels for multi-state toggles
        if (toggle.stateLabels != null) {
            this.setToggleStateLabels(toggle.stateLabels);
        }

        // Default icon via the base Ability layer system
        this.defaultIconWidth = toggle.width;
        this.defaultIconHeight = toggle.height;
        this.defaultIconLayers = new DefaultIconLayer[]{
            new DefaultIconLayer(toggle.iconTexture, toggle.stateIconTextures, null)
        };
    }

    public DBCToggle getToggle() {
        return toggle;
    }

    @Override
    public boolean hasDamage() {
        return false;
    }

    @Override
    public boolean isConcurrentCapable() {
        return true;
    }

    // Mutually exclusive toggles: only one can be active at a time
    private static final String[] EXCLUSIVE_GROUP = {
        "npcdbc:kaioken", "npcdbc:potential_unleashed", "npcdbc:ultra_instinct", "npcdbc:god_of_destruction"
    };

    private boolean isExclusive() {
        return toggle == DBCToggle.KAIOKEN || toggle == DBCToggle.POTENTIAL_UNLEASHED
            || toggle == DBCToggle.ULTRA_INSTINCT || toggle == DBCToggle.GOD_OF_DESTRUCTION;
    }

    // ═══════════════════════════════════════════════════════════════════
    // TOGGLE — all state changes routed through onToggle
    // ═══════════════════════════════════════════════════════════════════

    @Override
    public void onToggle(EntityLivingBase caster, int oldState, int newState) {
        if (!(caster instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) caster;

        toggle.applyState(player, newState);

        // Deactivate conflicting toggles in the ability data when activating an exclusive toggle
        if (newState > 0 && isExclusive()) {
            PlayerData playerData = PlayerData.get(player);
            if (playerData != null && playerData.abilityData != null) {
                String myKey = "npcdbc:" + toggle.key;
                for (String key : EXCLUSIVE_GROUP) {
                    if (!key.equals(myKey) && playerData.abilityData.isAbilityToggled(key)) {
                        playerData.abilityData.setToggleState(key, 0);
                    }
                }
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // EXECUTION — blocked; toggle abilities never execute
    // ═══════════════════════════════════════════════════════════════════

    @Override
    public void start(EntityLivingBase target) {
    }

    @Override
    public void onExecute(EntityLivingBase caster, EntityLivingBase target) {
    }

    @Override
    public void onActiveTick(EntityLivingBase caster, EntityLivingBase target, int tick) {
    }

    @Override
    public void writeTypeNBT(NBTTagCompound nbt) {
    }

    @Override
    public void readTypeNBT(NBTTagCompound nbt) {
    }
}
