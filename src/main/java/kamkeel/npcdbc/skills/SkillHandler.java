package kamkeel.npcdbc.skills;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import kamkeel.npcdbc.client.KeyHandler;
import kamkeel.npcdbc.data.SyncedData.CustomFormData;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;


public class SkillHandler {
    private static Minecraft mc = Minecraft.getMinecraft();
    EntityPlayer player;
    int id = 0;

    //WIP
    @SubscribeEvent
    public void onSkill(ClientTickEvent e) {
        player = mc.thePlayer;
        if (player == null || !e.phase.equals(TickEvent.Phase.START))
            return;
        AscendKey();
    }

    private void AscendKey() {
        if (Keyboard.isKeyDown(KeyHandler.AscendKey.getKeyCode())) {
            if (CustomFormData.getClient().getSelectedForm() != null) {
                Transform.Ascend(CustomFormData.getClient().getSelectedForm());
            } else {
                Utility.sendMessage(player, "§cYou have not selected a custom form!");
            }
        } else
            Transform.decrementRage();
    }
}
