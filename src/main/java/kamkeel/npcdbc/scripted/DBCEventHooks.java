package kamkeel.npcdbc.scripted;

import kamkeel.npcdbc.api.event.IDBCEvent;
import kamkeel.npcdbc.constants.DBCScriptType;
import kamkeel.npcs.controllers.AttributeController;
import kamkeel.npcs.controllers.data.attribute.tracker.PlayerAttributeTracker;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.config.ConfigMain;
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
}
