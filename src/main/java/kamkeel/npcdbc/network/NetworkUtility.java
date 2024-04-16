package kamkeel.npcdbc.network;

import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.CustomForm;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.HashMap;
import java.util.Map;

import static noppes.npcs.NoppesUtilServer.sendScrollData;

public class NetworkUtility {

    public static void sendCustomFormDataAll(EntityPlayerMP player) {
        Map<String,Integer> map = new HashMap<String,Integer>();
        for(CustomForm customForm : FormController.getInstance().customForms.values()){
            map.put(customForm.name, customForm.id);
        }
        sendScrollData(player, map);
    }

}
