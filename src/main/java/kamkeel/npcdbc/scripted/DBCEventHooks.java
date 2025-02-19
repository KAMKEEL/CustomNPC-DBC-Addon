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

    public static boolean onDBCDamageEvent(DBCPlayerEvent.DamagedEvent damagedEvent) {
        PlayerDataScript handler = ScriptController.Instance.playerScripts;
        handler.callScript(DBCScriptType.DAMAGED.function, damagedEvent);
        return NpcAPI.EVENT_BUS.post(damagedEvent);
    }

    public static boolean onCapsuleUsedEvent(DBCPlayerEvent.CapsuleUsedEvent capsuleUsedEvent) {
        PlayerDataScript handler = ScriptController.Instance.playerScripts;
        handler.callScript(DBCScriptType.CAPSULEUSED.function, capsuleUsedEvent);
        return NpcAPI.EVENT_BUS.post(capsuleUsedEvent);
    }

    public static boolean onSenzuUsedEvent(DBCPlayerEvent.SenzuUsedEvent senzuUsedEvent) {
        PlayerDataScript handler = ScriptController.Instance.playerScripts;
        handler.callScript(DBCScriptType.SENZUUSED.function, senzuUsedEvent);
        return NpcAPI.EVENT_BUS.post(senzuUsedEvent);
    }

    public static boolean onReviveEvent(DBCPlayerEvent.ReviveEvent reviveEvent) {
        PlayerDataScript handler = ScriptController.Instance.playerScripts;
        handler.callScript(DBCScriptType.REVIVED.function, reviveEvent);
        return NpcAPI.EVENT_BUS.post(reviveEvent);
    }

    public static void onKnockoutEvent(DBCPlayerEvent.KnockoutEvent koEvent) {
        PlayerDataScript handler = ScriptController.Instance.playerScripts;
        handler.callScript(DBCScriptType.KNOCKOUT.function, koEvent);
        NpcAPI.EVENT_BUS.post(koEvent);
    }
}
