package kamkeel.npcdbc.scripted;

import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.constants.DBCScriptType;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormScript;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.PlayerDataScript;
import noppes.npcs.scripted.NpcAPI;

public class DBCEventHooks {

    public static boolean onFormChangeEvent(DBCPlayerEvent.FormChangeEvent formChangeEvent) {
        PlayerDataScript handler = ScriptController.Instance.getPlayerScripts(formChangeEvent.getPlayer());
        handler.callScript(DBCScriptType.FORMCHANGE.function, formChangeEvent);
        return NpcAPI.EVENT_BUS.post(formChangeEvent);
    }

    public static boolean onDBCDamageEvent(DBCPlayerEvent.DamagedEvent damagedEvent) {
        PlayerDataScript handler = ScriptController.Instance.getPlayerScripts(damagedEvent.getPlayer());
        handler.callScript(DBCScriptType.DAMAGED.function, damagedEvent);
        return NpcAPI.EVENT_BUS.post(damagedEvent);
    }

    public static boolean onCapsuleUsedEvent(DBCPlayerEvent.CapsuleUsedEvent capsuleUsedEvent) {
        PlayerDataScript handler = ScriptController.Instance.getPlayerScripts(capsuleUsedEvent.getPlayer());
        handler.callScript(DBCScriptType.CAPSULEUSED.function, capsuleUsedEvent);
        return NpcAPI.EVENT_BUS.post(capsuleUsedEvent);
    }

    public static boolean onSenzuUsedEvent(DBCPlayerEvent.SenzuUsedEvent senzuUsedEvent) {
        PlayerDataScript handler = ScriptController.Instance.getPlayerScripts(senzuUsedEvent.getPlayer());
        handler.callScript(DBCScriptType.SENZUUSED.function, senzuUsedEvent);
        return NpcAPI.EVENT_BUS.post(senzuUsedEvent);
    }

    public static boolean onReviveEvent(DBCPlayerEvent.ReviveEvent reviveEvent) {
        PlayerDataScript handler = ScriptController.Instance.getPlayerScripts(reviveEvent.getPlayer());
        handler.callScript(DBCScriptType.REVIVED.function, reviveEvent);
        return NpcAPI.EVENT_BUS.post(reviveEvent);
    }

    public static boolean onKnockoutEvent(DBCPlayerEvent.KnockoutEvent koEvent) {
        PlayerDataScript handler = ScriptController.Instance.getPlayerScripts(koEvent.getPlayer());
        handler.callScript(DBCScriptType.KNOCKOUT.function, koEvent);
        return NpcAPI.EVENT_BUS.post(koEvent);
    }

    public static boolean onSkillEvent(DBCPlayerEvent.SkillEvent event) {
        PlayerDataScript handler = ScriptController.Instance.getPlayerScripts(event.getPlayer());
        handler.callScript(DBCScriptType.SKILL_EVENT.function, event);
        return NpcAPI.EVENT_BUS.post(event);
    }

    public static void onFormAscend(IPlayer player, IForm form) {
        FormScript handler = ((Form) form).getScriptHandler();
        if (handler == null) return;
        DBCPlayerEvent.FormEvent.Ascend event = new DBCPlayerEvent.FormEvent.Ascend(player, form);
        handler.callScript(DBCScriptType.Form.ASCEND.function, event);
    }

    public static void onFormDescend(IPlayer player, IForm form) {
        FormScript handler = ((Form) form).getScriptHandler();
        if (handler == null) return;
        DBCPlayerEvent.FormEvent.Descend event = new DBCPlayerEvent.FormEvent.Descend(player, form);
        handler.callScript(DBCScriptType.Form.DESCEND.function, event);
    }

    public static void onFormTick(IPlayer player, IForm form) {
        FormScript handler = ((Form) form).getScriptHandler();
        if (handler == null) return;
        DBCPlayerEvent.FormEvent.Tick event = new DBCPlayerEvent.FormEvent.Tick(player, form);
        handler.callScript(DBCScriptType.Form.TICK.function, event);
    }
}
