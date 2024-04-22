package kamkeel.npcdbc.scripted;


import JinRyuu.JRMCore.entity.EntityEnergyAtt;
import cpw.mods.fml.common.eventhandler.Cancelable;
import kamkeel.npcdbc.constants.DBCDamageSource;
import kamkeel.npcdbc.constants.DBCScriptType;
import net.minecraft.util.DamageSource;
import noppes.npcs.api.IDamageSource;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.scripted.NpcAPI;
import noppes.npcs.scripted.event.PlayerEvent;

public class DBCPlayerEvent extends PlayerEvent {

    public DBCPlayerEvent(IPlayer player) {
        super(player);
    }

    /**
     * formChange
     */
    @Cancelable
    public static class FormChangeEvent extends DBCPlayerEvent {

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

        public int getFormBeforeID() {
            return formBeforeID;
        }

        public int getFormAfterID() {
            return formAfterID;
        }

        public boolean isFormBeforeCustom() {
            return isBeforeCustom;
        }

        public boolean isFormAfterCustom() {
            return isAfterCustom;
        }

        public String getHookName() {
            return DBCScriptType.FORMCHANGE.function;
        }
    }

    public static class DamagedEvent extends DBCPlayerEvent {

        public final IDamageSource damageSource;
        public final int sourceType;
        public float damage;

        public DamagedEvent(IPlayer player, float damage, DamageSource damageSource, int type) {
            super(player);
            this.damage = damage;
            this.damageSource = NpcAPI.Instance().getIDamageSource(damageSource);
            this.sourceType = type;
        }

        public float getDamage() {
            return damage;
        }

        /**
         * @param damage The new damage value
         */
        public void setDamage(float damage){
            this.damage = damage;
        }

        public IDamageSource getDamageSource() {
            return damageSource;
        }

        public boolean isDamageSourceKiAttack() {
            return sourceType == DBCDamageSource.KIATTACK;
        }

        public float getType() {
            return sourceType;
        }

        public String getHookName() {
            return DBCScriptType.DAMAGED.function;
        }
    }
}
