package kamkeel.npcdbc.data.overlay;

import JinRyuu.JBRA.ModelBipedDBC;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.entity.ModelBipedBody;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.model.ModelDBC;
import kamkeel.npcdbc.client.utils.Color;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.client.model.ModelMPM;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.List;

@SideOnly(Side.CLIENT)
public class OverlayContext {
    public Overlay overlay;
    public OverlayChain chain;

    public String finalTex; // Fully baked texture before TextureFunction is applied
    public Color finalCol; // same for ColorFunction

    public boolean isNPC;
    public EntityCustomNpc npc;
    public DBCDisplay display; //for NPCs
    public ModelDBC modelNpc;

    public EntityPlayer player;
    public DBCData dbcData; //for players
    public ModelBipedBody model;

    public Form form;

    public static OverlayContext from(DBCDisplay display) {
        OverlayContext data = new OverlayContext();
        data.isNPC = true;
        data.npc = (EntityCustomNpc) display.npc;
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

    public ModelMPM mpm() {
        return modelNpc.parent;
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

    public String genderDir() {
        return female() ? "female" : "male";
    }


    public int eyeType() {
        return isNPC ? display.eyeType : JRMCoreH.dnsEyes(dbcData.DNS);
    }

    public int furType() {
        if (form() != null)
            return form.display.furType;

        return isNPC ? display.furType : 0;
    }

    public boolean furDaima() {
        return furType() == 1;
    }

    public String furDir() {
        return furDaima() ? "ssj4d" : "ssj4";
    }

    public boolean hasFur() {
        if (form() != null)
            return form.display.hasBodyFur;

        return isNPC ? display.hasFur : false;
    }

    public boolean oozaru() {
        if (form() != null)
            return form.display.hairType.equals("oozaru");

        return isNPC ? display.hairType.equals("oozaru") : false;
    }

    public List<OverlayChain> getOverlayChains() {
        return isNPC ? display.getOverlayChains() : dbcData.getOverlayChains();
    }

    public Form form() {
        if (form == null)
            form = PlayerDataUtil.getForm(isNPC ? npc : player);

        return form;
    }
}
