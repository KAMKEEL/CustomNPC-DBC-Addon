package kamkeel.npcdbc.data;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.entity.EntityNPCInterface;

public class RenderingData {
    public boolean isNPC;
    public EntityNPCInterface npc;
    public DBCDisplay display; //for NPCs

    public EntityPlayer player;
    public DBCData dbcData; //for players

    public int eyeType() {
        return isNPC ? display.eyeType : JRMCoreH.dnsEyes(dbcData.DNS);
    }

    public int furType() {
        Form form = form();
        if (form != null)
            return form.display.furType;

        return isNPC ? display.furType : 0;
    }

    public Form form() {
        return PlayerDataUtil.getForm(isNPC ? npc : player);
    }
}
