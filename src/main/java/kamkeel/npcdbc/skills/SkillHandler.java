package kamkeel.npcdbc.skills;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import kamkeel.npcdbc.client.KeyHandler;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;


public class SkillHandler {
    private static Minecraft mc = Minecraft.getMinecraft();
    EntityPlayer player;

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
            if (Utility.getFormDataClient().getSelectedForm() != null) {
                Transform.Ascend(Utility.getFormDataClient().getSelectedForm());
            } else {
                Utility.sendMessage(player, "§cYou have not selected a custom form!");
                //make this a single time text
            }
        } else
            Transform.decrementRage();
    }
}
