package kamkeel.npcdbc.client.gui.global.outline;

import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.data.outline.Outline;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.HashMap;

/**
 * Interface exposing the fields that Outline SubGuis need from their parent manager GUI.
 * Implemented by GuiNPCManageOutlines and GuiOutlineDirectory.
 */
public interface IOutlineManagerGui {
    HashMap<String, Integer> getOutlineData();
    GuiCustomScroll getOutlineScroll();
    EntityNPCInterface getOutlineNPC();
    Outline getOutline();
    DBCDisplay getOutlineVisualDisplay();
    String getOutlineSelected();
    void setOutlineSelected(String selected);
    void closeOutlineSubGui(Object obj);
}
