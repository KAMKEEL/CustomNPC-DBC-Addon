package kamkeel.npcdbc.scripted;

import cpw.mods.fml.common.eventhandler.Cancelable;
import kamkeel.npcdbc.api.event.IDBCEvent;
import kamkeel.npcdbc.constants.Capsule;
import kamkeel.npcdbc.constants.DBCDamageSource;
import kamkeel.npcdbc.constants.DBCScriptType;
import kamkeel.npcdbc.constants.enums.*;
import kamkeel.npcdbc.data.DBCDamageCalc;
import kamkeel.npcdbc.util.DBCUtils;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.client.event.RenderPlayerEvent;
import noppes.npcs.api.IDamageSource;
import noppes.npcs.api.entity.IPlayer;
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
    public static class SkillEvent extends DBCPlayerEvent implements IDBCEvent.SkillEvent {

        public final int type, id;
        protected SkillEvent(IPlayer player, int type, int id) {
            super(player);
            this.type = type;
            this.id = id;
        }

        @Override
        public int getEventType() {
            if (this instanceof IDBCEvent.SkillEvent.Learn)
                return 0;
            if (this instanceof IDBCEvent.SkillEvent.Unlearn)
                return 1;
            if (this instanceof IDBCEvent.SkillEvent.Upgrade)
                return 2;

            return -1;
        }

        @Override
        public int getSkillType() {
            return type;
        }

        @Override
        public int getSkillID() {
            return id;
        }

        @Cancelable
        public static class Learn extends DBCPlayerEvent.SkillEvent implements IDBCEvent.SkillEvent.Learn {
            public int cost;

            public Learn(IPlayer player, int type, int id, int cost) {
                super(player, type, id);
            }

            @Override
            public int getCost() {
                return this.cost;
            }

            @Override
            public void setCost(int cost) {
                this.cost = cost;
            }
        }
        @Cancelable
        public static class Unlearn extends DBCPlayerEvent.SkillEvent implements IDBCEvent.SkillEvent.Unlearn {
            public Unlearn(IPlayer player, int type, int id) {
                super(player, type, id);
            }
        }

        @Cancelable
        public static class Upgrade extends DBCPlayerEvent.SkillEvent implements IDBCEvent.SkillEvent.Upgrade {
            public final int level;
            public int cost;

            public Upgrade(IPlayer player, int type, int id, int cost, int level) {
                super(player, type, id);
                this.level = level;
            }

            @Override
            public int getCost() {
                return this.cost;
            }

            @Override
            public void setCost(int cost) {
                this.cost = cost;
            }

            @Override
            public int getNewLevel() {
                return this.level;
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
