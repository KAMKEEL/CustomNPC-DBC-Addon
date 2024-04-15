package kamkeel.npcdbc.skills;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import kamkeel.npcdbc.client.KeyHandler;
import kamkeel.npcdbc.data.SyncedData.CustomFormData;
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

        // Transform.decrementDescTimer();
        //Transform.decrementRage();
        AscendKey();
    }

    private void AscendKey() {
        if (Keyboard.isKeyDown(KeyHandler.Ascend.getKeyCode()) && CustomFormData.getClient().getSelectedForm() != null) {
            Transform.Ascend(CustomFormData.getClient().getSelectedForm().getID());
            System.out.println("transforming");
        }

    }
}
