package kamkeel.npcdbc.client.gui.hud.abilityHotbar;

import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class HUDAbilityHotbar extends Gui {
    public AbilityHotbarSlot[] hotbarSlot = new AbilityHotbarSlot[6];

    public int selectedSlot = -1;

    public DBCData dbcData;
    public PlayerDBCInfo dbcInfo;

    public HUDAbilityHotbar() {
    }

    public void onRender(Minecraft mc) {
        for (AbilityHotbarSlot slot : hotbarSlot) {
            slot.draw(mc);
        }
    }
}
