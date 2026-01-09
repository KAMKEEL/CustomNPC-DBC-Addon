package kamkeel.npcdbc.scripted;

import cpw.mods.fml.common.eventhandler.Cancelable;
import kamkeel.npcdbc.api.event.IDBCEvent;
import kamkeel.npcdbc.constants.Capsule;
import kamkeel.npcdbc.constants.DBCDamageSource;
import kamkeel.npcdbc.constants.DBCScriptType;
import kamkeel.npcdbc.constants.enums.*;
import kamkeel.npcdbc.data.DBCDamageCalc;
import kamkeel.npcdbc.data.ability.Ability;
import kamkeel.npcdbc.data.ability.AddonAbility;
import kamkeel.npcdbc.util.DBCUtils;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.client.event.RenderPlayerEvent;
import noppes.npcs.api.IDamageSource;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.handler.data.IAnimation;
import noppes.npcs.controllers.AnimationController;
import noppes.npcs.scripted.NpcAPI;
import noppes.npcs.scripted.event.player.PlayerEvent;

public abstract class DBCPlayerEvent extends PlayerEvent implements IDBCEvent {

    public DBCPlayerEvent(IPlayer player) {
        super(player);
    }

    /**
     * capsuleUsed
     */
    @Cancelable
    public static class CapsuleUsedEvent extends DBCPlayerEvent implements IDBCEvent.CapsuleUsedEvent {

        private final int type;
        private final int subtype;

        public CapsuleUsedEvent(IPlayer player, int type, int subtype) {
            super(player);
            this.type = type;
            this.subtype = subtype;
        }

        @Override
        public int getType() {
            return type;
        }

        @Override
        public int getSubType() {
            return subtype;
        }

        @Override
        public String getCapsuleName() {
            String name = "UNKNOWN";
            if (subtype >= 0) {
                if (type == Capsule.MISC && subtype < EnumMiscCapsules.count()) {
                    name = EnumMiscCapsules.values()[subtype].getName();
                } else if (type == Capsule.HP && subtype < EnumHealthCapsules.count()) {
                    name = EnumHealthCapsules.values()[subtype].getName();
                } else if (type == Capsule.KI && subtype < EnumKiCapsules.count()) {
                    name = EnumKiCapsules.values()[subtype].getName();
                } else if (type == Capsule.STAMINA && subtype < EnumStaminaCapsules.count()) {
                    name = EnumStaminaCapsules.values()[subtype].getName();
                } else if (type == Capsule.REGEN && subtype < EnumRegenCapsules.count()) {
                    name = EnumRegenCapsules.values()[subtype].getName();
                }
            }

            return name;
        }

        public String getHookName() {
            return DBCScriptType.CAPSULEUSED.function;
        }
    }

    /**
     * senzuUsed
     */
    @Cancelable
    public static class SenzuUsedEvent extends DBCPlayerEvent implements IDBCEvent.SenzuUsedEvent {

        public SenzuUsedEvent(IPlayer player) {
            super(player);
        }

        public String getHookName() {
            return DBCScriptType.SENZUUSED.function;
        }
    }

    /**
     * formChange
     */
    @Cancelable
    public static class FormChangeEvent extends DBCPlayerEvent implements IDBCEvent.FormChangeEvent {

        private final int formBeforeID;
        private final boolean isBeforeCustom;

        private final int formAfterID;
        private final boolean isAfterCustom;

        public FormChangeEvent(IPlayer player, boolean isBeforeCustom, int formBeforeID, boolean isAfterCustom, int formAfterID) {
            super(player);
            this.formBeforeID = formBeforeID;
            this.isBeforeCustom = isBeforeCustom;
            this.formAfterID = formAfterID;
            this.isAfterCustom = isAfterCustom;
        }

        @Override
        public int getFormBeforeID() {
            return formBeforeID;
        }

        @Override
        public int getFormAfterID() {
            return formAfterID;
        }

        @Override
        public boolean isFormBeforeCustom() {
            return isBeforeCustom;
        }

        @Override
        public boolean isFormAfterCustom() {
            return isAfterCustom;
        }

        public String getHookName() {
            return DBCScriptType.FORMCHANGE.function;
        }
    }

    @Cancelable
    public static class DamagedEvent extends DBCPlayerEvent implements IDBCEvent.DamagedEvent {

        public final IDamageSource damageSource;
        public final int sourceType;
        public float damage;
        public int stamina;
        public int ki;
        /**
         * True if the incoming damage would knock the player out when no overrides are applied.
         */
        public boolean ko;
        /**
         * Optional override applied by scripts. If null the {@code ko} value is used.
         */
        public Boolean koOverride;

        public DamagedEvent(EntityPlayer player, float damage, DamageSource damageSource, int type) {
            super(PlayerDataUtil.getIPlayer(player));
            this.damage = damage;
            this.damageSource = NpcAPI.Instance().getIDamageSource(damageSource);
            this.sourceType = type;
            this.ko = DBCUtils.checkKnockout(player, damageSource, this.damage);
            this.koOverride = null;
        }

        public DamagedEvent(EntityPlayer player, DBCDamageCalc damageCalc, DamageSource damageSource, int type) {
            super(PlayerDataUtil.getIPlayer(player));
            this.damage = damageCalc.damage;
            this.stamina = damageCalc.stamina;
            this.ko = DBCUtils.checkKnockout(player, damageSource, this.damage);
            this.koOverride = null;
            this.ki = damageCalc.ki;
            this.damageSource = NpcAPI.Instance().getIDamageSource(damageSource);
            this.sourceType = type;
        }

        @Override
        public float getDamage() {
            return damage;
        }

        /**
         * @param damage The new damage value, this will recalculate the KO status
         */
        @Override
        public void setDamage(float damage) {
            this.damage = damage;
            if(player != null && player.getMCEntity() instanceof EntityPlayer && damageSource != null && damageSource.getMCDamageSource() != null) {
                this.ko = DBCUtils.checkKnockout((EntityPlayer) player.getMCEntity(), damageSource.getMCDamageSource(), this.damage);
            }
        }

        @Override
        public int getStaminaReduced() {
            return this.stamina;
        }

        @Override
        public void setStaminaReduced(int stamina) {
            this.stamina = stamina;
        }

        @Override
        /**
         * Base KO state calculated from the player's stats and current damage
         * without considering any overrides.
         */
        public boolean getKO() {
            return this.ko;
        }

        @Override
        public void setKo(boolean ko) {
            this.koOverride = ko;
        }

        /**
         * Returns the KO value that should be applied after taking any overrides into account.
         */
        public boolean getFinalKO() {
            return this.koOverride != null ? this.koOverride : this.ko;
        }

        @Override
        public int getKiReduced() {
            return this.ki;
        }

        @Override
        public void setKiReduced(int ki) {
            this.ki = ki;
        }

        @Override
        public IDamageSource getDamageSource() {
            return damageSource;
        }

        @Override
        public boolean isDamageSourceKiAttack() {
            return sourceType == DBCDamageSource.KIATTACK;
        }

        @Override
        public float getType() {
            return sourceType;
        }

        public String getHookName() {
            return DBCScriptType.DAMAGED.function;
        }
    }


    public static class ReviveEvent extends DBCPlayerEvent implements IDBCEvent.DBCReviveEvent {

        public ReviveEvent(IPlayer player) {
            super(player);
        }

        public String getHookName() {
            return DBCScriptType.REVIVED.function;
        }
    }

    @Cancelable
    public static class KnockoutEvent extends DBCPlayerEvent implements IDBCEvent.DBCKnockout {

        public final IDamageSource damageSource;

        public KnockoutEvent(IPlayer player, DamageSource damageSource) {
            super(player);
            this.damageSource = NpcAPI.Instance().getIDamageSource(damageSource);
        }

        @Override
        public IDamageSource getDamageSource() {
            return damageSource;
        }

        public String getHookName() {
            return DBCScriptType.KNOCKOUT.function;
        }
    }

    @Cancelable
    public static class AbilityEvent extends DBCPlayerEvent implements IDBCEvent.AbilityEvent {
        public final int id;
        public int kiCost;
        public int cooldown;
        boolean isDBC;

        public AbilityEvent(IPlayer player, Ability ability) {
            super(player);
            this.id = ability.id;
            this.kiCost = ability.kiCost;
            this.cooldown = ability.cooldown;
            this.isDBC = ability instanceof AddonAbility;
        }

        @Override
        public int getID() {
            return id;
        }

        @Override
        public int getType() {
            if (this instanceof IDBCEvent.AbilityEvent.Casted)
                return 0;
            if (this instanceof IDBCEvent.AbilityEvent.MultiCasted)
                return 1;
            else if (this instanceof IDBCEvent.AbilityEvent.Toggled)
                return 2;
            else if (this instanceof IDBCEvent.AbilityEvent.Animated)
                return 3;

            return -1;
        }

        @Override
        public int getKiCost() {
            return kiCost;
        }

        @Override
        public void setKiCost(int kiCost) {
            this.kiCost = kiCost;
        }

        @Override
        public int getCooldown() {
            return cooldown;
        }

        @Override
        public void setCooldown(int cooldown) {
            this.cooldown = cooldown;
        }

        @Override
        public boolean isDBC() {
            return isDBC;
        }

        public static class Casted extends DBCPlayerEvent.AbilityEvent implements IDBCEvent.AbilityEvent.Casted {
            public Casted(IPlayer player, Ability ability) {
                super(player, ability);
            }
        }

        public static class MultiCasted extends DBCPlayerEvent.AbilityEvent implements IDBCEvent.AbilityEvent.MultiCasted {
            public int maxUses;
            public MultiCasted(IPlayer player, Ability ability) {
                super(player, ability);
                this.maxUses = ability.maxUses;
            }

            @Override
            public int getMaxUses() {
                return maxUses;
            }

            @Override
            public void setMaxUses(int maxUses) {
                this.maxUses = maxUses;
            }
        }

        public static class Toggled extends DBCPlayerEvent.AbilityEvent implements IDBCEvent.AbilityEvent.Toggled {
            public Toggled(IPlayer player, Ability ability) {
                super(player, ability);
            }
        }

        public static class Animated extends DBCPlayerEvent.AbilityEvent implements IDBCEvent.AbilityEvent.Animated {
            IAnimation animation;

            public Animated(IPlayer player, Ability ability) {
                super(player, ability);
                this.animation = ability.animation;
            }

            @Override
            public IAnimation getAnimation() {
                return animation;
            }

            @Override
            public void setAnimation(IAnimation animation) {
                if (animation != null && AnimationController.getInstance().has(animation.getName()))
                    this.animation = animation;
            }
        }
    }

    public static class RenderEvent extends RenderPlayerEvent {

        public RenderEvent(EntityPlayer player, RenderPlayer renderer, float partialRenderTick) {
            super(player, renderer, partialRenderTick);
        }

        @Cancelable
        public static class Pre extends RenderPlayerEvent {
            public Pre(EntityPlayer player, RenderPlayer renderer, float tick) {
                super(player, renderer, tick);
            }
        }

        public static class Post extends RenderPlayerEvent {
            public Post(EntityPlayer player, RenderPlayer renderer, float tick) {
                super(player, renderer, tick);
            }
        }
    }

    public static class RenderArmEvent extends RenderPlayerEvent {

        public RenderArmEvent(EntityPlayer player, RenderPlayer renderer, float partialRenderTick) {
            super(player, renderer, partialRenderTick);
        }

        @Cancelable
        public static class Item extends RenderPlayerEvent {
            public Item(EntityPlayer player, RenderPlayer renderer, float partialRenderTick) {
                super(player, renderer, partialRenderTick);
            }
        }

        @Cancelable
        public static class Pre extends RenderPlayerEvent {
            public Pre(EntityPlayer player, RenderPlayer renderer, float partialRenderTick) {
                super(player, renderer, partialRenderTick);
            }
        }

        public static class Post extends RenderPlayerEvent {
            public Post(EntityPlayer player, RenderPlayer renderer, float partialRenderTick) {
                super(player, renderer, partialRenderTick);
            }
        }
    }
}
