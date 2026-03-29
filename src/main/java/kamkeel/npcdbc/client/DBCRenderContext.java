package kamkeel.npcdbc.client;

import JinRyuu.JBRA.ModelBipedDBC;
import JinRyuu.JBRA.RenderPlayerJBRA;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.entity.ModelBipedBody;
import kamkeel.npcdbc.api.Color;
import kamkeel.npcdbc.client.model.ModelDBC;
import kamkeel.npcdbc.client.utils.SimplifiedDBCData;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormDisplay;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.data.overlay.OverlayChain;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IEntityLivingBase;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.client.model.ModelMPM;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.scripted.NpcAPI;

import java.util.List;

import static kamkeel.npcdbc.constants.BodyLayer.*;

public class DBCRenderContext {
    public Entity entity; 
    
    public EntityPlayer player;
    public DBCData dbcData;
    public ModelBipedBody modelBiped;
    public ModelBipedDBC model;
    public RenderPlayerJBRA renderer;

    public boolean isNPC;
    public EntityCustomNpc npc;
    public DBCDisplay display;
    public ModelDBC modelNpc;
    
    public double renderX;
    public double renderY;
    public double renderZ;
    public float renderYaw;
    public float partialTicks;

    public boolean isFirstPersonArm;
    public int armAnimationId = -1;
    public Runnable armRenderer;

    public Form form;
    public Race race;

    // ══════════════════════════════════════════════════════════════════════════
    // Creators
    // ══════════════════════════════════════════════════════════════════════════ 

    public static <T extends DBCRenderContext> T from(DBCDisplay display) {
        T data = (T) new DBCRenderContext();
        data.isNPC = true;
        data.npc = (EntityCustomNpc) display.npc;
        data.display = display;
        return data;
    }

    public static <T extends DBCRenderContext> T from(DBCData dbcData) {
        T data = (T) new DBCRenderContext();
        data.isNPC = false;
        data.player = dbcData.player;
        data.dbcData = dbcData;
        return data;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Entity Data
    // ══════════════════════════════════════════════════════════════════════════ 

    public IEntityLivingBase getEntity() {
        return isNPC ? getNPC() : getPlayer();
    }

    public ICustomNpc getNPC() {
        return (ICustomNpc) NpcAPI.Instance().getIEntity(npc);
    }

    public IPlayer getPlayer() {
        return (IPlayer) NpcAPI.Instance().getIEntity(player);
    }

    public boolean isNPC() {
        return isNPC;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Render Data
    // ══════════════════════════════════════════════════════════════════════════

    public ModelMPM mpm() {
        return modelNpc.parent;
    }

    public DBCRenderContext setRenderVars(double renderX, double renderY, double renderZ, float renderYaw,
                                          float partialTicks) {
        this.renderX = renderX;
        this.renderY = renderY;
        this.renderZ = renderZ;
        this.renderYaw = renderYaw;
        this.partialTicks = partialTicks;
        return this;
    }

    public double getRenderX() {
        return renderX;
    }

    public double getRenderY() {
        return renderY;
    }

    public double getRenderZ() {
        return renderZ;
    }

    public float getRenderYaw() {
        return renderYaw;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // DBC Data
    // ══════════════════════════════════════════════════════════════════════════

    public SimplifiedDBCData getDBCData() {
        return dbcData.simplifiedDBCData;
    }
    
    public int gender() {
        if (isNPC)
            return display.isFemaleInternal() ? 2 : 1;
        return ModelBipedDBC.g;
    }

    public boolean female() {
        if (isNPC)
            return display.isFemaleInternal();
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

    public int race() {
        return isNPC ? display.race : dbcData.Race;
    }

    public String raceName() {
        if (race() >= 0 && race() <= 5) {
            return JRMCoreH.Races[race()];
        }
        return "";
    }

    public String raceDir() {
        return raceName().toLowerCase().replaceAll("-", "");
    }

    public int bodyTypeDBC() {
        return isNPC ? display.bodyType : JRMCoreH.dnsBodyT(dbcData.DNS);
    }
    
    public String bodyType(){
        return isNPC ? display.bodyType +"" : dbcData.getBodyType();
    }
    
    public boolean bodyType(String type){
        return type.equals(bodyType());
    }
    
    public int eyeType() {
        return isNPC ? display.eyeType : JRMCoreH.dnsEyes(dbcData.DNS);
    }

    public int nose() {
        return isNPC ? display.noseType : JRMCoreH.dnsFaceN(dbcData.DNS);
    }

    public int mouth() {
        return isNPC ? display.mouthType : JRMCoreH.dnsFaceM(dbcData.DNS);
    }

    public boolean eyebrows() {
        if (form() != null)
            return form.display.hasEyebrows;
        return isNPC ? display.hasEyebrows : true;
    }

    public int arcoState() {
        int index = 0;

        if (form() != null) {
            Form f = form();
            FormDisplay fd = f.display;

            if (!fd.bodyType.isEmpty()) {
                if (fd.bodyType.toLowerCase().contains("first"))
                    index = 0;
                else if (fd.bodyType.toLowerCase().contains("second"))
                    index = 1;
                else if (fd.bodyType.toLowerCase().contains("third"))
                    index = 2;
                else if (fd.bodyType.toLowerCase().contains("final"))
                    index = 3;
                else if (fd.bodyType.toLowerCase().contains("ultimate"))
                    index = 4;
                else if (fd.bodyType.toLowerCase().contains("golden"))
                    index = 5;
            }

            return index;
        }

        return isNPC ? display.arcoState : dbcState();
    }

    public int furType() {
        if (form() != null)
            return form.display.furType;
        return isNPC ? display.furType : 0;
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

    public boolean hairType(String type) {
        if (form() != null)
            return form.display.hairType.equalsIgnoreCase(type);
        return isNPC ? display.hairType.equalsIgnoreCase(type) : false;
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


    public void glColor(Color color) {
        if (isNPC)
            ColorMode.applyModelColor(color.color, color.alpha, ModelDBC.isHurt);
        else
            color.glColor();
    }

    public List<OverlayChain> getOverlayChains() {
        return isNPC ? display.getOverlayChains() : dbcData.getOverlayChains();
    }

    public Form form() {
        if (form == null)
            form = PlayerDataUtil.getForm(isNPC ? npc : player);
        return form;
    }

    public int dbcState() {
        return isNPC ? -1 : dbcData.State;
    }

    public Race customRace() {
        if (race == null)
            race = PlayerDataUtil.getRace(isNPC ? npc : player);
        return race;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Colors
    // ══════════════════════════════════════════════════════════════════════════

    public int color(String type) {
        return isNPC ? display.getColor(type) : dbcData.getColor(type);
    }

    public int bodyCM() {
        return color(BODY_CM);
    }

    public int bodyC1() {
        return color(BODY_C1);
    }

    public int bodyC2() {
        return color(BODY_C2);
    }

    public int bodyC3() {
        return color(BODY_C3);
    }

    public int bodyC4() {
        return color(BODY_C4);
    }

    public int eyeC1() {
        return color(EYE_LEFT);
    }

    public int eyeC2() {
        return color(EYE_RIGHT);
    }
}
