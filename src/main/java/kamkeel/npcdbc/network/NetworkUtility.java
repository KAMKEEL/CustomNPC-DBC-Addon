package kamkeel.npcdbc.network;

import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.form.Form;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.HashMap;
import java.util.Map;

import static noppes.npcs.NoppesUtilServer.sendScrollData;

public class NetworkUtility {

    public static void sendCustomFormDataAll(EntityPlayerMP player) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (Form customForm : FormController.getInstance().customForms.values()) {
            map.put(customForm.name, customForm.id);
        }
        sendScrollData(player, map);
    }

    public static void sendCustomAuraDataAll(EntityPlayerMP player) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (Aura customAura : AuraController.getInstance().customAuras.values()) {
            map.put(customAura.name, customAura.id);
        }
        sendScrollData(player, map);
    }

}
