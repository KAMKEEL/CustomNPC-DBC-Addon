package kamkeel.npcdbc.api.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import kamkeel.npcdbc.api.form.IForm;

public interface IFormEvent extends IDBCEvent{
    IForm getForm();

    @Cancelable
    interface Ascend extends IFormEvent {}

    @Cancelable
    interface Descend extends IFormEvent {}

    interface Tick extends IFormEvent {}
}
