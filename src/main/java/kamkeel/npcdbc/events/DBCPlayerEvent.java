package kamkeel.npcdbc.events;


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

        private final int formBefore;
        private final int formAfter;

        public FormChangeEvent(IPlayer player, int formBefore, int formAfter) {
            super(player);
            this.formBefore = formBefore;
            this.formAfter = formAfter;
        }

        public int getFormBefore() {
            return formBefore;
        }

        public int getFormAfter() {
            return formAfter;
        }
    }
}
