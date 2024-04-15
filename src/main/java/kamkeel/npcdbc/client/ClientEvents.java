package kamkeel.npcdbc.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import kamkeel.npcdbc.data.SyncedData.CustomFormData;
import kamkeel.npcdbc.network.PacketRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import org.lwjgl.input.Keyboard;

//mostly for testing stuff
public class ClientEvents {
    /**
     * For testing purposes
     */
    @SubscribeEvent
    public void onKeyPress(KeyInputEvent e) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            EntityClientPlayerMP p = Minecraft.getMinecraft().thePlayer;
            createForm(e, p);
        }

    }

    public void createForm(KeyInputEvent e, EntityClientPlayerMP p) {
        if (Keyboard.isKeyDown(50)) { //M
            PacketRegistry.tellServer("createForm");
        }
        if (Keyboard.isKeyDown(51)) { //,
            CustomFormData c = CustomFormData.get(p);
        }
    }
}
