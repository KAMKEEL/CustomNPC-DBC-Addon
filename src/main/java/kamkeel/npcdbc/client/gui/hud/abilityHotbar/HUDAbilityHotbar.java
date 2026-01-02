package kamkeel.npcdbc.client.gui.hud.abilityHotbar;

import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.player.ability.DBCRequestAbilityWheel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class HUDAbilityHotbar extends Gui {


    public HUDAbilityHotbar() {
        DBCPacketHandler.Instance.sendToServer(new DBCRequestAbilityWheel());
    }

    public void onRender(Minecraft mc, float partialTicks) {

    }
}
