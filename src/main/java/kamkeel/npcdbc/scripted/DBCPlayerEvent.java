package kamkeel.npcdbc.scripted;


import cpw.mods.fml.common.eventhandler.Cancelable;
import noppes.npcs.api.entity.IPlayer;
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
    }
}
