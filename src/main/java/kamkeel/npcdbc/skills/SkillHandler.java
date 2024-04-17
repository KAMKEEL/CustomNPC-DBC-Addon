package kamkeel.npcdbc.skills;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import kamkeel.addon.client.DBCClient;
import kamkeel.npcdbc.client.KeyHandler;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.player.inventory.*;
import noppes.npcs.constants.EnumPlayerPacket;
import org.lwjgl.input.Keyboard;


public class SkillHandler {
    private static Minecraft mc = Minecraft.getMinecraft();
    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event){
        if(KeyHandler.AscendKey.isPressed()){
            Minecraft mc = Minecraft.getMinecraft();
            if(mc.currentScreen == null){
                AscendKey();
            }
        }
    }

    private void AscendKey() {
        if (Keyboard.isKeyDown(KeyHandler.AscendKey.getKeyCode())) {
            if (Utility.getFormDataClient().getSelectedForm() != null) {
                Transform.Ascend(Utility.getFormDataClient().getSelectedForm());
            } else {
                Utility.sendMessage(mc.thePlayer, "§cYou have not selected a custom form!");
                //make this a single time text
            }
        } else
            Transform.decrementRage();
    }
}
