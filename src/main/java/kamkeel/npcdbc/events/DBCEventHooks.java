package kamkeel.npcdbc.events;

import kamkeel.npcdbc.constants.DBCScriptType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.data.PlayerDataScript;
import noppes.npcs.scripted.NpcAPI;
import noppes.npcs.scripted.event.PlayerEvent;

public class DBCEventHooks {

    public static void onFormChangeEvent(PlayerDataScript handler, IPlayer player, int formBefore, int formAfter) {
        DBCPlayerEvent.FormChangeEvent event = new DBCPlayerEvent.FormChangeEvent(player, 0, 0);
        handler.callScript(DBCScriptType.FORMCHANGE.function, event);
        NpcAPI.EVENT_BUS.post(event);
    }
}
