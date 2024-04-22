package kamkeel.npcdbc.scripted;


import JinRyuu.JRMCore.entity.EntityEnergyAtt;
import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.util.DamageSource;
import noppes.npcs.api.IDamageSource;
import noppes.npcs.api.entity.IPlayer;
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
    }

    public static class DamagedEvent extends DBCPlayerEvent {

        public final DamageSource damageSource;
        public float damage;

        public DamagedEvent(IPlayer player, float damage, DamageSource damageSource) {
            super(player);
            this.damage = damage;
            this.damageSource = damageSource;
        }

        public float getDamage() {
            return damage;
        }

        public IDamageSource getDamageSource() {
            return NpcAPI.Instance().getIDamageSource(damageSource);
        }

        public boolean isDamageSourceKiAttack() {
            return damageSource.getDamageType().equals("EnergyAttack") && damageSource.getSourceOfDamage() instanceof EntityEnergyAtt;
        }
    }
}
