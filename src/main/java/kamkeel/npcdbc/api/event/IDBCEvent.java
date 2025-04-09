package kamkeel.npcdbc.api.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.IDamageSource;
import noppes.npcs.api.event.IPlayerEvent;

public interface IDBCEvent extends IPlayerEvent {

    @Cancelable
    interface CapsuleUsedEvent extends IDBCEvent {

        /**
         * 0: Misc, 1: HP, 2: Ki, 3: Stamina
         *
         * @return Type of Capsule
         */
        int getType();

        /**
         * @return Subtype / Metadata of the Capsule
         */
        int getSubType();

        /**
         * @return Capsule Name
         */
        String getCapsuleName();
    }

    @Cancelable
    interface SenzuUsedEvent extends IDBCEvent {
    }

    @Cancelable
    interface FormChangeEvent extends IDBCEvent {
        /**
         * @return The ID of the Form. If form before is vanilla DBC, returns the ID of that. i.e Base returns 0, SSGod returns 9. Else, returns ID of CNPC Custom Form
         */
        int getFormBeforeID();

        /**
         * @return The ID of the Form. If form after is vanilla DBC, returns the ID of that. i.e SSGod returns 9. Else, returns ID of CNPC Custom Form
         */
        int getFormAfterID();

        /**
         * @return If form before transformation is a vanilla DBC (SSJ/SSGod/SSBEvo) and not a CNPC Custom Form, this returns false
         */
        boolean isFormBeforeCustom();

        /**
         * @return If form after transformation is a vanilla DBC (SSJ/SSGod/SSBEvo) and not a CNPC Custom Form, this returns false
         */
        boolean isFormAfterCustom();
    }

    @Cancelable
    interface DamagedEvent extends IDBCEvent {


        /**
         * Calculated based on Player's Stats, Dex, Blocking, etc.
         *
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
         *
         * @return the stamina reduction
         */
        int getStaminaReduced();

        /**
         * @param stamina The new stamina to reduce
         */
        void setStaminaReduced(int stamina);

        boolean getKo();

        void setKo(boolean ko);

        /**
         *
         * @return the ki reduction
         */
        int getKiReduced();

        /**
         * @param ki The new ki to reduce
         */
        void setKiReduced(int ki);

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

    @Cancelable
    interface DBCReviveEvent extends IDBCEvent {
    }

    @Cancelable
    interface DBCKnockout extends IDBCEvent {
        IDamageSource getDamageSource();

    }
}
