package kamkeel.npcdbc.scripted;

import cpw.mods.fml.common.eventhandler.Cancelable;
import kamkeel.npcdbc.api.event.IDBCEvent;
import kamkeel.npcdbc.constants.Capsule;
import kamkeel.npcdbc.constants.DBCDamageSource;
import kamkeel.npcdbc.constants.DBCScriptType;
import kamkeel.npcdbc.constants.enums.*;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.client.event.RenderPlayerEvent;
import noppes.npcs.api.IDamageSource;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.scripted.NpcAPI;
import noppes.npcs.scripted.event.NpcEvent;
import noppes.npcs.scripted.event.PlayerEvent;

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

        public DamagedEvent(EntityPlayer player, float damage, DamageSource damageSource, int type) {
            super(PlayerDataUtil.getIPlayer(player));
            this.damage = damage;
            this.damageSource = NpcAPI.Instance().getIDamageSource(damageSource);
            this.sourceType = type;
        }

        @Override
        public float getDamage() {
            return damage;
        }

        /**
         * @param damage The new damage value
         */
        @Override
        public void setDamage(float damage) {
            this.damage = damage;
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
