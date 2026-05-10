package kamkeel.npcdbc.constants;

import kamkeel.npcdbc.api.event.IFormEvent;
import noppes.npcs.constants.ScriptContext;

public class DBCScriptContext {

    public static final ScriptContext FORM;

    static {
        FORM = register("DBCForm", "form", IFormEvent.class);
    }

    public static ScriptContext register(String id, String hookContext, Class<?>... eventClasses) {
        return ScriptContext.register(id, hookContext, eventClasses);
    }
}
