package kamkeel.npcdbc.scripted;

import kamkeel.npcdbc.constants.DBCScriptType;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.PlayerDataScript;
import noppes.npcs.scripted.NpcAPI;

public class DBCEventHooks {

    public static boolean onFormChangeEvent(DBCPlayerEvent.FormChangeEvent formChangeEvent) {
        PlayerDataScript handler = ScriptController.Instance.playerScripts;
        handler.callScript(DBCScriptType.FORMCHANGE.function, formChangeEvent);
        return NpcAPI.EVENT_BUS.post(formChangeEvent);
    }

    public static boolean onDamagedEvent(DBCPlayerEvent.DamagedEvent damagedEvent) {
        PlayerDataScript handler = ScriptController.Instance.playerScripts;
        handler.callScript(DBCScriptType.DAMAGED.function, damagedEvent);
        return NpcAPI.EVENT_BUS.post(damagedEvent);
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
