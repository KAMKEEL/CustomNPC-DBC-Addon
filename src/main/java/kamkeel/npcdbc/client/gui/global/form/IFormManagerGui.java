package kamkeel.npcdbc.client.gui.global.form;

import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormDisplay;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.client.gui.util.SubGuiInterface;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.HashMap;

/**
 * Interface exposing the fields that Form SubGuis need from their parent manager GUI.
 * Implemented by both the legacy GuiNPCManageForms and the new GuiFormDirectory.
 */
public interface IFormManagerGui {
    HashMap<String, Integer> getFormData();
    GuiCustomScroll getFormScroll();
    EntityNPCInterface getFormNPC();
    Form getForm();
    FormDisplay getFormDisplay();
    DBCDisplay getFormVisualDisplay();
    void setSubGui(SubGuiInterface subgui);
}
