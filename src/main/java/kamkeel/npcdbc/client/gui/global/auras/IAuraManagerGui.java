package kamkeel.npcdbc.client.gui.global.auras;

import kamkeel.npcdbc.client.sound.AuraSound;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.aura.AuraDisplay;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.HashMap;

/**
 * Interface exposing the fields that Aura SubGuis need from their parent manager GUI.
 * Implemented by both the legacy GuiNPCManageAuras and the new GuiAuraDirectory.
 */
public interface IAuraManagerGui {
    HashMap<String, Integer> getAuraData();
    GuiCustomScroll getAuraScroll();
    EntityNPCInterface getAuraNPC();
    Aura getAura();
    AuraDisplay getAuraDisplay();
    DBCDisplay getAuraVisualDisplay();
    String getAuraSelected();
    void setAuraSelected(String selected);

    // Sound management
    AuraSound getAuraSound();
    AuraSound getKaiokenSound();
    AuraSound getKettleSound();
    AuraSound getSecondarySound();
    AuraSound getSecondaryKettleSound();
    void playSound(boolean allowRepeats);
    void stopSound(AuraSound sound, boolean immediate);

    void closeSubGui(Object obj);
}
