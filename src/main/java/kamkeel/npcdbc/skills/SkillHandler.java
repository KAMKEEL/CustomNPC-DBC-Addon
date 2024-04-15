package kamkeel.npcdbc.skills;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import kamkeel.npcdbc.client.KeyHandler;
import kamkeel.npcdbc.data.SyncedData.CustomFormData;
import kamkeel.npcdbc.util.u;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;


public class SkillHandler {
    private static Minecraft mc = Minecraft.getMinecraft();
    EntityPlayer p;
    int id = 0;

    //WIP
    @SubscribeEvent
    public void onSkill(ClientTickEvent e) {

        p = mc.thePlayer;
        if (p == null || !e.phase.equals(TickEvent.Phase.START))
            return;

        AscendKey();
    }

    private void AscendKey() {
        if (Keyboard.isKeyDown(KeyHandler.Ascend.getKeyCode())) {
            if (CustomFormData.getClient().getSelectedForm() != null) {
                Transform.Ascend(CustomFormData.getClient().getSelectedForm());
            } else {
                u.sendMessage(p, "§c§lYou have not selected a custom form!");
            }

        } else
            Transform.decrementRage();
    }
}
