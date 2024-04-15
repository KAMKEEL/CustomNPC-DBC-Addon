package kamkeel.npcdbc.events;

import kamkeel.npcdbc.constants.DBCScriptType;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.PlayerDataScript;
import noppes.npcs.scripted.NpcAPI;
import noppes.npcs.scripted.event.DialogEvent;

public class DBCEventHooks {

    public static boolean onFormChangeEvent(DBCPlayerEvent.FormChangeEvent formChangeEvent) {
        PlayerDataScript handler = ScriptController.Instance.playerScripts;
        handler.callScript(DBCScriptType.FORMCHANGE.function, formChangeEvent);
        return NpcAPI.EVENT_BUS.post(formChangeEvent);
    }

    // HOW TO CANCEL AN EVENT. Must be @Cancelable
    // This will post it as well as call the script.
//    public static void something(){
//        DBCPlayerEvent.FormChangeEvent formChangeEvent = new DBCPlayerEvent.FormChangeEvent((IPlayer)NpcAPI.Instance().getIEntity((EntityPlayer)player), 0, 0);
//        if (DBCEventHooks.onFormChangeEvent(formChangeEvent)) {
//            return;
//        }
//    }
}
