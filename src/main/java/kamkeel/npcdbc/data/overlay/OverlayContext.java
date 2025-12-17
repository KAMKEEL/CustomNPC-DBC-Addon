package kamkeel.npcdbc.data.overlay;

import JinRyuu.JBRA.ModelBipedDBC;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.entity.ModelBipedBody;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.api.IDBCAddon;
import kamkeel.npcdbc.api.client.overlay.IOverlay;
import kamkeel.npcdbc.api.client.overlay.IOverlayContext;
import kamkeel.npcdbc.client.ColorMode;
import kamkeel.npcdbc.client.model.ModelDBC;
import kamkeel.npcdbc.api.Color;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.client.model.ModelMPM;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.scripted.NpcAPI;

import java.util.List;
import java.util.Set;

import static kamkeel.npcdbc.api.client.overlay.IOverlay.ColorType.Custom;

@SideOnly(Side.CLIENT)
public class OverlayContext implements IOverlayContext {
    public Overlay overlay;
    public OverlayChain chain;

    /*
        Overlay Types disabled, these do not render.
        For now, they are only disabled by forms.

        exceptFor is the form OverlayChain that's disabling
        the types, as we do not want any of it disabled.
     */
    public Set<Overlay.Type> disabledTypes;
    public OverlayChain exceptFor;

    @Override
    public boolean typeDisabled(IOverlay.Type type) {
        return chain != exceptFor && disabledTypes != null && disabledTypes.contains(type);
    }

    /*
     The texture of the current Overlay before it gets bound.
     Can call it in TextureFunction (ctx.texture) to see
     what it is before the function is applied.
     */
    public String texture;

    /*
    The color of the current Overlay after all ColorType logic is applied.
    Can call it in ColorFunction (ctx.color) to see
    what it is before the function is applied.
    */
    public Color color; // same for ColorFunction

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

    public void cacheOverlays(List<OverlayChain> overlays) {
        if (isNPC)
            display.cachedOverlays = overlays;
        else
            dbcData.cachedOverlays = overlays;
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

    public float inverseAge() {
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

    public boolean eyebrows() {
        if (form() != null)
            return form.display.hasEyebrows;

        return isNPC ? display.hasEyebrows : true;
    }

    public int furType() {
        if (form() != null)
            return form.display.furType;

        return isNPC ? display.furType : 0;
    }

    public boolean hairType(String type) {
        if (form() != null)
            return form.display.hairType.equalsIgnoreCase(type);

        return isNPC ? display.hairType.equalsIgnoreCase(type) : false;
    }

    public int color(String type) {
        return isNPC ? display.getColor(type) : dbcData.getColor(type);
    }

    public Color color(IOverlay.ColorType type) {
        return color(type, overlay);
    }

    public Color color(IOverlay.ColorType type, IOverlay iOverlay) {
        Overlay overlay = (Overlay) iOverlay;
        int col = type == Custom ? overlay.color : color(type.name());
        return new Color(col, overlay.alpha);
    }

    public void glColor(Color color) {
        if (isNPC)
            ColorMode.applyModelColor(color.color, color.alpha, ModelDBC.isHurt);
        else
            color.glColor();
    }

    public boolean furGT() {
        return furType() == 1;
    }

    public boolean furDaima() {
        return furType() == 1;
    }

    public boolean furSavior() {
        return furType() == 2;
    }

    public String furDir() {
        return furDaima() ? "ssj4d" : "ssj4";
    }

    public boolean hasFur() {
        if (form() != null)
            return form.display.hasBodyFur;

        return isNPC ? display.hasFur : false;
    }

    public boolean pupils() {
        if (form() != null)
            return form.display.hasPupils;

        return isNPC ? display.hasPupils : false;
    }

    public boolean berserk() {
        if (form() != null)
            return form.display.isBerserk;

        return false;
    }

    public List<OverlayChain> getOverlayChains() {
        return isNPC ? display.getOverlayChains() : dbcData.getOverlayChains();
    }

    public Form form() {
        if (form == null)
            form = PlayerDataUtil.getForm(isNPC ? npc : player);

        return form;
    }

    public boolean isNPC() {
        return isNPC;
    }

    public IEntity getEntity() {
        return isNPC ? getNPC() : getPlayer();
    }

    public ICustomNpc getNPC() {
        return (ICustomNpc) NpcAPI.Instance().getIEntity(npc);
    }

    public IDBCAddon getPlayer() {
        return (IDBCAddon) ((IPlayer) NpcAPI.Instance().getIEntity(player)).getDBCPlayer();
    }

    public IOverlay getOverlay() {
        return overlay;
    }
}
