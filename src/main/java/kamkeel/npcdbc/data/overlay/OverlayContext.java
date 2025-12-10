package kamkeel.npcdbc.data.overlay;

import JinRyuu.JBRA.ModelBipedDBC;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.entity.ModelBipedBody;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.entity.EntityNPCInterface;

@SideOnly(Side.CLIENT)
public class OverlayContext {
    public Overlay overlay;
    public OverlayChain chain;

    public boolean isNPC;
    public EntityNPCInterface npc;
    public DBCDisplay display; //for NPCs

    public EntityPlayer player;
    public DBCData dbcData; //for players
    public ModelBipedBody model;

    public Form form;

    public static OverlayContext from(DBCDisplay display) {
        OverlayContext data = new OverlayContext();
        data.isNPC = true;
        data.npc = display.npc;
        data.display = display;
        return data;
    }

    public static OverlayContext from(DBCData dbcData) {
        OverlayContext data = new OverlayContext();
        data.isNPC = false;
        data.player = dbcData.player;
        data.dbcData = dbcData;
        return data;
    }

    public int gender() {
        return ModelBipedDBC.g;
    }

    public boolean female() {
        return gender() > 1;
    }

    public float age() {
        return ModelBipedDBC.f;
    }

    public float invAge() {
        return 1.0F / age();
    }

    public int pregnant() {
        return ModelBipedDBC.p;
    }

    public String genderDirectory() {
        return female() ? "/female" : "/male";
    }


    public int eyeType() {
        return isNPC ? display.eyeType : JRMCoreH.dnsEyes(dbcData.DNS);
    }

    public int furType() {
        Form form = form();
        if (form != null)
            return form.display.furType;

        return isNPC ? display.furType : 0;
    }

    public boolean furDaima() {
        return furType() == 1;
    }

    public boolean hasFur() {
        Form form = form();
        if (form != null)
            return form.display.hasBodyFur;

        return isNPC ? display.hasFur : false;
    }

    public boolean oozaru() {
        Form form = form();
        if (form != null)
            return form.display.hairType.equals("oozaru");

        return isNPC ? display.hairType.equals("oozaru") : false;
    }

    public Form form() {
        if (form == null)
            form = PlayerDataUtil.getForm(isNPC ? npc : player);

        return form;
    }
}
