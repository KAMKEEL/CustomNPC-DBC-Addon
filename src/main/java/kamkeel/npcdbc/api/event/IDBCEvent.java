package kamkeel.npcdbc.api.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.IDamageSource;
import noppes.npcs.api.event.IPlayerEvent;
import noppes.npcs.api.handler.data.IAnimation;

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
    interface AbilityEvent extends IDBCEvent {
        /**
         * @return The ID of the Ability.
         */
        int getID();

        /**
         * @return The type of the Ability. 0 = Active; 1 = Toggle; 2 = Animated
         */
        int getType();

        /**
         * @return The amount of ki necessary to cast the Ability.
         */
        int getKiCost();

        /**
         * Allows you to change / intercept the amount of Ki
         * taken from the player upon casting the ability
         * @param kiCost The new Ki cost
         */
        void setKiCost(int kiCost);

        /**
         * @return The amount of time in seconds it takes to re-cast the Ability
         */
        int getCooldown();

        /**
         * Allows you to change the ability's cooldown
         * @param cooldown The new cooldown in seconds
         */
        void setCooldown(int cooldown);

        /**
         * @return If ability is added by the Addon, returns true. Returns false otherwise
         */
        boolean isDBC();

        interface Casted extends AbilityEvent{

        }

        interface MultiCasted extends AbilityEvent{
            /**
             * @return The amount of times you can cast the Ability before triggering cooldown
             */
            int getMaxUses();

            /**
             * Allows you to change the max amount of uses before triggering cooldown
             * @param maxUses The new amount of uses
             */
            void setMaxUses(int maxUses);
        }

        interface Toggled extends AbilityEvent{

        }

        interface Animated extends AbilityEvent {
            /**
             * @return The animation that plays when casting the ability
             */
            IAnimation getAnimation();

            /**
             * Allows you to change the animation that plays when casting the ability
             * @param animation The new animation
             */
            void setAnimation(IAnimation animation);
        }
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

        /**
         * Returns the unmodified KO result based on the current damage and
         * attacker before any overrides are applied.
         */
        boolean getKO();

        /**
         *
         * Doing setDamage will automatically reset the KO calculation, so setKO modifications must be performed after setDamage.
         *
         * @return If KO will occur
         */
        void setKo(boolean ko);

        /**
         * @return true if a knockout will ultimately occur after applying any
         * overrides set via {@link #setKo(boolean)}
         */
        boolean getFinalKO();

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
