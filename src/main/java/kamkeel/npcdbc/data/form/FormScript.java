package kamkeel.npcdbc.data.form;

import kamkeel.npcdbc.constants.DBCScriptContext;
import kamkeel.npcdbc.network.packets.request.form.FormScriptPacket;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.constants.ScriptContext;
import noppes.npcs.controllers.data.IScriptHandlerPacket;
import noppes.npcs.controllers.data.IScriptUnit;
import noppes.npcs.controllers.data.SingleScriptHandler;
import noppes.npcs.janino.EventJaninoScript;

public class FormScript extends SingleScriptHandler implements IScriptHandlerPacket {

    private int formId = -1;

    public FormScript() {
    }

    public FormScript(int formId) {
        this.formId = formId;
    }

    @Override
    public ScriptContext getContext() {
        return DBCScriptContext.FORM;
    }

    @Override
    public IScriptUnit createJaninoScriptUnit() {
        return new EventJaninoScript(DBCScriptContext.FORM);
    }

    @Override
    public String noticeString() {
        return formId >= 0 ? "CustomForm[" + formId + "]" : "CustomForm";
    }

    @Override
    public void requestData() {
        if (formId >= 0)
            FormScriptPacket.Get(formId);
    }

    @Override
    public void sendSavePacket(int index, int totalCount, NBTTagCompound nbt) {
        if (formId >= 0)
            FormScriptPacket.Save(formId, index, totalCount, nbt);
    }
}
