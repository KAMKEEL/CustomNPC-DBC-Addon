package kamkeel.npcdbc.api.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.IDamageSource;
import noppes.npcs.api.event.IPlayerEvent;

public interface IDBCEvent extends IPlayerEvent {

    @Cancelable
    interface FormChangeEvent extends IDBCEvent {

        int getFormBeforeID();

        int getFormAfterID();

        boolean isFormBeforeCustom();

        boolean isFormAfterCustom();
    }

    @Cancelable
    interface DamagedEvent extends IDBCEvent {


        /**
         * Calculated based on Player's Stats, Dex, Blocking, etc.
         * @return Damage Dealt to the HP of the Player
         */
        float getDamage();

        /**
         * Allows you to change / intercept the damage to HP
         * and modify to set new HP. Note if Damage is 0 -
         * then it could be Friendly Fist protecting player. Making
         * this greater could kill the player.
         *
         * @param damage The new damage to HP
         */
        void setDamage(float damage);

        /**
         * @return IDamageSource
         */
        IDamageSource getDamageSource();

        /**
         * @return Damage Type == 3
         */
        boolean isDamageSourceKiAttack();

        /**
         * @return 0: Unknown, 1: Player, 2: NPC, 3: Ki Attack
         */
        float getType();
    }

}
