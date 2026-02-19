package kamkeel.npcdbc.data.ability;

import kamkeel.npcdbc.constants.DBCDamageSource;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.DBCDamageCalc;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.SoundSource;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.packets.player.PlaySound;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.DBCSettingsUtil;
import kamkeel.npcdbc.util.DBCUtils;
import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.controllers.data.ability.AbilityPhase;
import kamkeel.npcs.controllers.data.ability.IAbilityExtender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import noppes.npcs.NpcDamageSource;
import noppes.npcs.entity.EntityNPCInterface;

/**
 * DBC Addon ability extender. Provides:
 * - DBC damage routing for all ability damage when DBC Addon is installed
 * - Lifecycle hooks for player resource costs (ki, stamina)
 */
public class DBCAbilityExtender implements IAbilityExtender {

    @Override
    public boolean onAbilityStart(Ability ability, EntityLivingBase caster, EntityLivingBase target) {
        if (!(caster instanceof EntityPlayer))
            return true;

        DBCAbilityStats stats = DBCAbilityStats.fromAbility(ability);
        DBCData data = DBCData.get((EntityPlayer) caster);

        // Calculate actual costs (flat or percent of max pool)
        int kiCost = stats.getKiCost();
        if (kiCost > 0 && stats.isKiCostPercent())
            kiCost = (int) (kiCost / 100.0 * data.stats.getMaxKi());

        int staminaCost = stats.getStaminaCost();
        if (staminaCost > 0 && stats.isStaminaCostPercent())
            staminaCost = (int) (staminaCost / 100.0 * data.stats.getMaxStamina());

        if (kiCost <= 0 && staminaCost <= 0)
            return true;

        if (kiCost > 0 && data.Ki < kiCost)
            return false;

        if (staminaCost > 0 && data.Stamina < staminaCost)
            return false;

        if (kiCost > 0)
            data.stats.restoreKiFlat(-kiCost);

        if (staminaCost > 0)
            data.stats.restoreStaminaFlat(-staminaCost);

        return true;
    }

    @Override
    public boolean onAbilityTick(Ability ability, EntityLivingBase caster, EntityLivingBase target,
                                 AbilityPhase phase, int tick) {
        // Drain only during ACTIVE phase — cost is handled on start, drain is per-tick while executing
        if (phase != AbilityPhase.ACTIVE)
            return true;

        if (!(caster instanceof EntityPlayer))
            return true;

        if (!handleDrainProcesses(ability, caster))
            return false;

        handleFormProcesses(ability, caster, tick);

        return true;
    }

    private boolean handleDrainProcesses(Ability ability, EntityLivingBase caster) {
        DBCData data = DBCData.get((EntityPlayer) caster);
        DBCAbilityStats stats = DBCAbilityStats.fromAbility(ability);
        int kiDrain = stats.getKiDrain();
        int staminaDrain = stats.getStaminaDrain();

        if (kiDrain > 0) {
            int actual = stats.isKiDrainPercent()
                ? (int) (kiDrain / 100.0 * data.stats.getMaxKi()) : kiDrain;
            if (data.Ki < actual)
                return false; // interrupt — not enough ki
            data.stats.restoreKiFlat(-actual);
        }

        if (staminaDrain > 0) {
            int actual = stats.isStaminaDrainPercent()
                ? (int) (staminaDrain / 100.0 * data.stats.getMaxStamina()) : staminaDrain;
            if (data.Stamina < actual)
                return false; // interrupt — not enough stamina
            data.stats.restoreStaminaFlat(-actual);
        }

        return true;
    }

    private void handleFormProcesses(Ability ability, EntityLivingBase caster, int tick) {
        DBCData data = DBCData.get((EntityPlayer) caster);
        AbilityFormData formData = AbilityFormData.fromAbility(ability);
        PlayerDBCInfo info = data.getDBCInfo();
        boolean formExists = formData.getFormID() != -1 && FormController.Instance != null && FormController.Instance.has(formData.formID);
        boolean transformed = info.currentForm != -1 && info.currentForm == formData.formID;

        if (!formExists) {
            handleKaiokenProcesses(ability, caster, tick);
            return;
        }

        Form form = (Form) FormController.Instance.get(formData.getFormID());

        if (!transformed && tick == formData.getTransformTick()) {

            if (!formData.needsFormUnlocked || info.unlockedForms.contains(form.getID())) {
                info.currentForm = form.getID();

                if (formData.isActivateTurbo()) {
                    data.setTurboState(true);
                }

                info.updateClient();
                PlaySound.play(new SoundSource(form.getAscendSound(), data.player));
            }
        }

        if (transformed && formData.getDetransformTick() > 0 && tick == formData.getDetransformTick()) {
            info.currentForm = -1;
            info.updateClient();
            PlaySound.play(new SoundSource(form.getDescendSound(), data.player));
        }

        handleKaiokenProcesses(ability, caster, tick);
    }

    private void handleKaiokenProcesses(Ability ability, EntityLivingBase caster, int tick) {
        DBCData data = DBCData.get((EntityPlayer) caster);
        AbilityFormData formData = AbilityFormData.fromAbility(ability);
        PlayerDBCInfo info = data.getDBCInfo();
        boolean transformed = info.currentForm != -1 && info.currentForm == formData.formID;
        boolean isKaioken = data.isInKaioken();

        if (transformed) {
            Form form = (Form) FormController.Instance.get(formData.getFormID());

            if (!form.getStackable().isFormStackable(DBCForm.Kaioken)) {
                return;
            }
        }

        if (!isKaioken && tick == formData.getActivateKaiokenTick()) {
            data.setForm(DBCForm.Kaioken, true);
            data.getRawCompound().setByte("jrmcState2", (byte) formData.getKaiokenStage());
            DBCSettingsUtil.setKaioken((EntityPlayer) caster, true);
        }

        if (isKaioken && formData.getDeactivateKaiokenTick() > 0 && tick == formData.getDeactivateKaiokenTick()) {
            data.setForm(DBCForm.Kaioken, false);
            DBCSettingsUtil.setKaioken((EntityPlayer) caster, false);
        }
    }

    @Override
    public void onAbilityComplete(Ability ability, EntityLivingBase caster, EntityLivingBase target, boolean interrupted) {
        DBCData data = DBCData.get((EntityPlayer) caster);
        AbilityFormData form = AbilityFormData.fromAbility(ability);
        PlayerDBCInfo info = data.getDBCInfo();
        boolean transformed = info.currentForm != -1 && info.currentForm == form.formID;
        boolean isKaioken = data.isInKaioken();

        if (transformed && !form.isKeepTransformed()) {
            info.currentForm = -1;
            info.updateClient();
        } // and it looks like this

        if (isKaioken && !form.isKeepKaioken()) {
            data.setForm(DBCForm.Kaioken, false);
            DBCSettingsUtil.setKaioken((EntityPlayer) caster, false);
        }
    }

    @Override
    public boolean onAbilityDamage(Ability ability, EntityLivingBase caster, EntityLivingBase target,
                                   float damage, float knockback, float knockbackUp,
                                   double knockbackDirX, double knockbackDirZ) {
        DBCAbilityStats stats = DBCAbilityStats.fromAbility(ability);

        // Build the damage source based on caster type
        DamageSource source;
        if (caster instanceof EntityNPCInterface) {
            source = new NpcDamageSource("mob", (EntityNPCInterface) caster);
        } else if (caster instanceof EntityPlayer) {
            source = DamageSource.causePlayerDamage((EntityPlayer) caster);
        } else {
            source = DamageSource.causeMobDamage(caster);
        }

        // Calculate outgoing damage
        float outDamage = damage; // default: use ability's base damage
        if (caster instanceof EntityPlayer) {
            float calcDamage = DBCUtils.calculateAbilityAttackDamage((EntityPlayer) caster, stats);
            if (calcDamage > 0) {
                outDamage = calcDamage;
            }
        }

        // Route damage to target
        if (target instanceof EntityPlayer) {
            // Player target: flag-guarded attackEntityFrom for knockback/animation only
            DBCUtils.abilityDamageHandled = true;
            try {
                target.attackEntityFrom(source, 1.0f);
            } finally {
                DBCUtils.abilityDamageHandled = false;
            }

            if (stats.isEnabled()) {
                // Universal settings enabled: use ability's ignore flags for defender reduction
                applyDBCDamageToPlayer((EntityPlayer) target, outDamage, stats, source);
            } else {
                // Universal settings off: generic DBC defender reduction
                applyDBCDamageToPlayerDefault((EntityPlayer) target, outDamage, stats, source);
            }
        } else if (target instanceof EntityNPCInterface) {
            // NPC target: set npcLastSetDamage for the Mixin to pick up
            DBCUtils.npcLastSetDamage = outDamage;
            target.attackEntityFrom(source, outDamage);
        } else {
            // Other entities: direct damage
            target.attackEntityFrom(source, outDamage);
        }

        return true; // Always handled when DBC Addon is installed
    }

    /**
     * Apply damage to a player through the DBC damage system with ability's universal settings.
     * Uses calculateDBCStatDamage which respects the ability's ignore flags.
     */
    private void applyDBCDamageToPlayer(EntityPlayer player, float damage, DBCAbilityStats stats, DamageSource source) {
        DBCDamageCalc damageCalc = DBCUtils.calculateDBCStatDamage(player, (int) damage, stats, source);

        DBCPlayerEvent.DamagedEvent damagedEvent = new DBCPlayerEvent.DamagedEvent(
            player, damageCalc, source, DBCDamageSource.NPC
        );
        if (DBCEventHooks.onDBCDamageEvent(damagedEvent)) {
            return;
        }

        damageCalc.damage = damagedEvent.damage;
        damageCalc.stamina = damagedEvent.getStaminaReduced();
        damageCalc.ki = damagedEvent.getKiReduced();
        damageCalc.ko = damagedEvent.getFinalKO();

        DBCUtils.lastSetDamage = damageCalc;
        damageCalc.processExtras();

        DBCUtils.doDBCDamage(player, damageCalc.damage, stats, source);
    }

    /**
     * Apply damage to a player through the DBC damage system with default defender reduction.
     * Uses calculateDBCDamageFromSource which applies generic DEX/blocking/ki protection.
     */
    private void applyDBCDamageToPlayerDefault(EntityPlayer player, float damage, DBCAbilityStats stats, DamageSource source) {
        DBCDamageCalc damageCalc = DBCUtils.calculateDBCDamageFromSource(player, damage, source);

        DBCPlayerEvent.DamagedEvent damagedEvent = new DBCPlayerEvent.DamagedEvent(
            player, damageCalc, source, DBCDamageSource.PLAYER
        );
        if (DBCEventHooks.onDBCDamageEvent(damagedEvent)) {
            return;
        }

        damageCalc.damage = damagedEvent.damage;
        damageCalc.stamina = damagedEvent.getStaminaReduced();
        damageCalc.ki = damagedEvent.getKiReduced();
        damageCalc.ko = damagedEvent.getFinalKO();

        DBCUtils.lastSetDamage = damageCalc;
        damageCalc.processExtras();

        DBCUtils.doDBCDamage(player, damageCalc.damage, stats, source);
    }
}
