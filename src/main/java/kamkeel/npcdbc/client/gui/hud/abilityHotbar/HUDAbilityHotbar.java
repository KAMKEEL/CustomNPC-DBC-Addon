package kamkeel.npcdbc.client.gui.hud.abilityHotbar;

import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.player.ability.DBCRequestAbilityWheel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.util.HashMap;

public class HUDAbilityHotbar extends Gui {
    public AbilityHotbarSlot[] hotbarSlot = new AbilityHotbarSlot[6];

    public int selectedSlot = -1;

    public HashMap<Integer, String> dbcAbilities;
    public DBCData dbcData;
    public PlayerDBCInfo dbcInfo;

    public HUDAbilityHotbar() {
        DBCPacketHandler.Instance.sendToServer(new DBCRequestAbilityWheel());
    }

    public void onRender(Minecraft mc) {
        for (AbilityHotbarSlot slot : hotbarSlot) {
            slot.draw(mc);
        }
    }
}
