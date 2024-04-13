package kamkeel.npcdbc.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import kamkeel.npcdbc.packets.PacketRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import org.lwjgl.input.Keyboard;

//mostly for testing stuff
public class ClientEvents {
    @SubscribeEvent
    public void onKeyPress(KeyInputEvent e) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            EntityClientPlayerMP p = Minecraft.getMinecraft().thePlayer;
            handleGrab(e, p);
        }

    }

    public void handleGrab(KeyInputEvent e, EntityClientPlayerMP p) {
        if (Keyboard.isKeyDown(50)) {
            System.out.println("yo");
            PacketRegistry.tellServer("createForm");
        }

    }
}
