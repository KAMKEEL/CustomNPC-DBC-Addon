package kamkeel.npcdbc.data.npc;

import JinRyuu.DragonBC.common.Npcs.EntityAura2;
import JinRyuu.JRMCore.entity.EntityCusPar;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.api.aura.IAura;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.api.npc.IDBCDisplay;
import kamkeel.npcdbc.api.npc.IKiWeaponData;
import kamkeel.npcdbc.api.outline.IOutline;
import kamkeel.npcdbc.client.model.part.hair.DBCHair;
import kamkeel.npcdbc.config.ConfigDBCGeneral;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.controllers.OutlineController;
import kamkeel.npcdbc.controllers.TransformController;
import kamkeel.npcdbc.data.IAuraData;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormDisplay;
import kamkeel.npcdbc.data.outline.Outline;
import kamkeel.npcdbc.entity.EntityAura;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import kamkeel.npcdbc.mixins.late.INPCStats;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.player.NPCUpdateForcedColors;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.ModelData;
import noppes.npcs.entity.data.ModelPartData;
import noppes.npcs.scripted.CustomNPCsException;
import noppes.npcs.util.ValueUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class DBCDisplay implements IDBCDisplay, IAuraData {

    public static Form fakeForm;
    public final EntityNPCInterface npc;
    public boolean enabled = ConfigDBCGeneral.DISPLAY_BY_DEFAULT;
    // Hair Display //
    public String hairCode = DBCHair.GOKU_HAIR, hairType = "";
    public int hairColor = 0;
    // Race Display //
    public byte race = 1;
    public boolean useSkin = false;
    public int bodyType = 0;
    public int bodyCM = 0xffffff, bodyC1 = 0xffffff, bodyC2 = 0xffffff, bodyC3 = 0xffffff;
    public boolean hasArcoMask = false, hasEyebrows = true;
    public int furColor = -1;
    public boolean hasFur = false;
    public byte tailState;
    // Face Display //
    public int eyeColor = 0;
    public int noseType = 0, mouthType = 0, eyeType = 0, arcoState;
    // Aura Display //
    public boolean auraOn = false;
    public int auraID = -1;
    // Form Display //
    public int formID = -1, selectedForm = -1, rage;
    public float formLevel = 0;
    public boolean isTransforming, isKaioken;
    private boolean isFemale = false;
    public int breastSize = 1;


    public boolean isFemale() {
        return isFemale;
    }
    public boolean isFemaleInternal() {
        boolean isFormOozaru = false;
        Form form = getForm();
        if (form != null) {
            isFormOozaru = form.display.hairType.equals("oozaru");
        }
        isFormOozaru = isFormOozaru && DBCRace.isSaiyan(race);
        return isFemale && !isFormOozaru;
    }
    public void setFemale(boolean isFemale) {
        this.isFemale = isFemale;
    }

    // Outline
    public int outlineID;

    // Server Side Usage
    public float rageValue;
    public int tempState, stateChange, state2Change, auraTime, auraType, bendTime;

    //Rendering
    public boolean useStencilBuffer;
    public EntityAura auraEntity;
    public int activeAuraColor = -1;
    public List<EntityCusPar> particleRenderQueue = new LinkedList<>();
    public HashMap<Integer, EntityAura2> dbcAuraQueue = new HashMap<>();
    public HashMap<Integer, EntityAura2> dbcSecondaryAuraQueue = new HashMap<>();

    public KiWeaponData kiWeaponRight = new KiWeaponData();
    public KiWeaponData kiWeaponLeft = new KiWeaponData();

    public DBCDisplay(EntityNPCInterface npc) {
        this.npc = npc;
    }

    public FormDisplay.BodyColor formColor = new FormDisplay.BodyColor();

    public NBTTagCompound writeToNBT(NBTTagCompound comp) {
        comp.setBoolean("DBCDisplayEnabled", enabled);
        if (enabled) {
            NBTTagCompound dbcDisplay = new NBTTagCompound();
            dbcDisplay.setBoolean("DBCFemale", isFemale);
            dbcDisplay.setString("DBCHair", hairCode);
            dbcDisplay.setInteger("DBCHairColor", hairColor);
            dbcDisplay.setInteger("DBCEyeColor", eyeColor);

            dbcDisplay.setString("DBCHairType", hairType);
            dbcDisplay.setInteger("DBCEyeType", eyeType);
            dbcDisplay.setInteger("DBCMouthType", mouthType);
            dbcDisplay.setInteger("DBCNoseType", noseType);
            dbcDisplay.setInteger("DBCBodyType", bodyType);
            dbcDisplay.setByte("DBCTailState", tailState);

            dbcDisplay.setInteger("DBCRace", race);
            dbcDisplay.setBoolean("DBCUseSkin", useSkin);
            dbcDisplay.setInteger("DBCBodyCM", bodyCM);
            dbcDisplay.setInteger("DBCBodyC1", bodyC1);
            dbcDisplay.setInteger("DBCBodyC2", bodyC2);
            dbcDisplay.setInteger("DBCBodyC3", bodyC3);
            dbcDisplay.setInteger("DBCFurColor", furColor);

            dbcDisplay.setInteger("DBCArcoState", arcoState);
            dbcDisplay.setBoolean("DBCArcoMask", hasArcoMask);
            dbcDisplay.setBoolean("DBCFur", hasFur);
            dbcDisplay.setBoolean("DBCHasEyebrows", hasEyebrows);

            dbcDisplay.setInteger("DBCRage", rage);
            dbcDisplay.setBoolean("DBCIsTransforming", isTransforming);
            dbcDisplay.setBoolean("DBCIsKaioken", isKaioken);
            dbcDisplay.setInteger("DBCFormID", formID);
            dbcDisplay.setFloat("DBCFormLevel", formLevel);
            dbcDisplay.setInteger("DBCSelectedForm", selectedForm);

            dbcDisplay.setInteger("DBCAuraID", auraID);
            dbcDisplay.setBoolean("DBCAuraOn", auraOn);

            dbcDisplay.setInteger("DBCOutlineID", outlineID);

            dbcDisplay.setInteger("DBCFemaleBreastSize", breastSize);


            kiWeaponLeft.saveToNBT(dbcDisplay, "kiWeaponLeft");
            kiWeaponRight.saveToNBT(dbcDisplay, "kiWeaponRight");

            comp.setTag("DBCDisplay", dbcDisplay);
        } else {
            comp.removeTag("DBCDisplay");
        }
        return comp;
    }

    public void readFromNBT(NBTTagCompound comp) {
        enabled = comp.getBoolean("DBCDisplayEnabled");
        if (enabled) {
            NBTTagCompound dbcDisplay = comp.getCompoundTag("DBCDisplay");
            isFemale = dbcDisplay.getBoolean("DBCFemale");

            race = dbcDisplay.getByte("DBCRace");
            auraID = dbcDisplay.getInteger("DBCAuraID");
            auraOn = dbcDisplay.getBoolean("DBCAuraOn");

            useSkin = dbcDisplay.getBoolean("DBCUseSkin");
            hairCode = dbcDisplay.getString("DBCHair");

            hairType = dbcDisplay.getString("DBCHairType");
            eyeType = dbcDisplay.getInteger("DBCEyeType");
            mouthType = dbcDisplay.getInteger("DBCMouthType");
            noseType = dbcDisplay.getInteger("DBCNoseType");
            bodyType = dbcDisplay.getInteger("DBCBodyType");
            tailState = dbcDisplay.getByte("DBCTailState");

            hairColor = dbcDisplay.getInteger("DBCHairColor");
            eyeColor = dbcDisplay.getInteger("DBCEyeColor");
            bodyCM = dbcDisplay.getInteger("DBCBodyCM");
            bodyC1 = dbcDisplay.getInteger("DBCBodyC1");
            bodyC2 = dbcDisplay.getInteger("DBCBodyC2");
            bodyC3 = dbcDisplay.getInteger("DBCBodyC3");
            furColor = dbcDisplay.getInteger("DBCFurColor");

            arcoState = dbcDisplay.getInteger("DBCArcoState");
            hasArcoMask = dbcDisplay.getBoolean("DBCArcoMask");
            hasFur = dbcDisplay.getBoolean("DBCFur");
            hasEyebrows = !dbcDisplay.hasKey("DBCHasEyebrows") || dbcDisplay.getBoolean("DBCHasEyebrows");

            auraID = dbcDisplay.getInteger("DBCAuraID");

            outlineID = dbcDisplay.getInteger("DBCOutlineID");

            rage = dbcDisplay.getInteger("DBCRage");
            isTransforming = dbcDisplay.getBoolean("DBCIsTransforming");
            isKaioken = dbcDisplay.getBoolean("DBCIsKaioken");
            formID = dbcDisplay.getInteger("DBCFormID");
            selectedForm = dbcDisplay.getInteger("DBCSelectedForm");

            if (dbcDisplay.hasKey("DBCFemaleBreastSize"))
                breastSize = dbcDisplay.getInteger("DBCFemaleBreastSize");

            if (dbcDisplay.hasKey("kiWeaponLeft"))
                kiWeaponLeft.readFromNBT(dbcDisplay, "kiWeaponLeft");
            if (dbcDisplay.hasKey("kiWeaponRight"))
                kiWeaponRight.readFromNBT(dbcDisplay, "kiWeaponRight");
        } else {
            comp.removeTag("DBCDisplay");
        }
    }

    @Override
    public void setColor(String type, int color) {
        switch (type.toLowerCase()) {
            case "hair":
                hairColor = color;
                break;
            case "eye":
                eyeColor = color;
                break;
            case "bodycm":
                bodyCM = color;
                break;
            case "bodyc1":
                bodyC1 = color;
                break;
            case "bodyc2":
                bodyC2 = color;
                break;
            case "bodyc3":
                bodyC3 = color;
                break;
            case "fur":
                furColor = color;
                break;
            default:
                throw new CustomNPCsException("Invalid type! Legal types: aura, hair, eye, bodycm, bodyc1, bodyc2, bodyc3, fur");
        }
    }

    public boolean hasColor(String type) {
        Form form = this.getForm();
        boolean inF = form != null;
        switch (type.toLowerCase()) {
            case "hair":
                return (inF ? form.display.bodyColors.hairColor : hairColor) != -1;
            case "eye":
                return (inF ? form.display.bodyColors.eyeColor : eyeColor) != -1;
            case "bodycm":
                return (inF ? form.display.bodyColors.bodyCM : bodyCM) != -1;
            case "bodyc1":
                return (inF ? form.display.bodyColors.bodyC1 : bodyC1) != -1;
            case "bodyc2":
                return (inF ? form.display.bodyColors.bodyC2 : bodyC2) != -1;
            case "bodyc3":
                return (inF ? form.display.bodyColors.bodyC3 : bodyC3) != -1;
            case "fur":
                return (inF ? form.display.bodyColors.furColor : furColor) != -1;
        }
        throw new CustomNPCsException("Invalid type! Legal types: hair, eye, bodycm, bodyc1, bodyc2, bodyc3, fur");
    }

    @Override
    public int getColor(String type) {
        Form form = this.getForm();
        boolean inF = form != null;
        switch (type.toLowerCase()) {
            case "hair":
                return inF ? form.display.bodyColors.hairColor : hairColor;
            case "eye":
                return inF ? form.display.bodyColors.eyeColor : eyeColor;
            case "bodycm":
                return inF ? form.display.bodyColors.bodyCM : bodyCM;
            case "bodyc1":
                return inF ? form.display.bodyColors.bodyC1 : bodyC1;
            case "bodyc2":
                return inF ? form.display.bodyColors.bodyC2 : bodyC2;
            case "bodyc3":
                return inF ? form.display.bodyColors.bodyC3 : bodyC3;
            case "fur":
                return inF ? form.display.bodyColors.furColor : furColor;
        }
        throw new CustomNPCsException("Invalid type! Legal types: hair, eye, bodycm, bodyc1, bodyc2, bodyc3, fur");
    }

    public int getCurrentArcoState() {
        int state = this.arcoState;
        Form form = this.getForm();
        if (form != null) {
            switch (form.display.bodyType) {
                case "firstform":
                    state = 0;
                    break;
                case "secondform":
                    state = 2;
                    break;
                case "thirdform":
                    state = 3;
                    break;
                case "finalform":
                    state = 4;
                    break;
                case "ultimatecooler":
                    state = 5;
                    break;
                case "golden":
                    state = 6;
                    break;
            }
        }
        return state;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String getHairCode() {
        return hairCode;
    }

    @Override
    public void setHairCode(String hairCode) {
        this.hairCode = hairCode;
    }

    @Override
    public IKiWeaponData getKiWeapon(int arm) {
        if (arm == 0)
            return kiWeaponRight;
        if (arm == 1)
            return kiWeaponLeft;
        return null;
    }

    @Override
    public byte getRace() {
        return race;
    }

    @Override
    public void setRace(byte race) {
        this.race = ValueUtil.clamp(race, (byte) 0, (byte) 5);
    }

    public int getBodyType() {
        return bodyType;
    }

    @Override
    public void setBodyType(int bodyType) {
        this.bodyType = ValueUtil.clamp(bodyType, 0, 2);
    }

    @Override
    public byte getTailState() {
        return tailState;
    }

    @Override
    public void setTailState(byte tail) {
        this.tailState = ValueUtil.clamp(tail, (byte) 0, (byte) 2);
    }

    @Override
    public void setHairType(String type) {
        String s = type.toLowerCase();
        if (s.equals("base") || s.equals("ssj") || s.equals("ssj2") || s.equals("ssj3") || s.equals("ssj4") || s.equals("oozaru") || s.equals("raditz") || s.equals("")) {
            hairType = s;
        } else
            throw new CustomNPCsException("Invalid type! Legal types: base, raditz, ssj, ssj2, ssj3, ssj4, oozaru");
    }

    @Override
    public String getHairType() {
        return hairType;
    }

    @Override
    public boolean hasCoolerMask() {
        return hasArcoMask;
    }

    @Override
    public void setHasCoolerMask(boolean has) {
        hasArcoMask = has;
    }

    @Override
    public boolean hasEyebrows() {
        return hasEyebrows;
    }

    @Override
    public void setHasEyebrows(boolean has) {
        hasEyebrows = has;
    }

    @Override
    public boolean hasBodyFur() {
        return hasFur;
    }

    @Override
    public void setHasBodyFur(boolean hasFur) {
        this.hasFur = hasFur;
    }

    /////////////////////////////////////////////
    /////////////////////////////////////////////
    // Auras
    @Override
    public boolean hasAura() {
        boolean has = AuraController.getInstance().has(auraID);
        if (!has && auraID > -1)
            auraID = -1;
        return has;
    }

    @Override
    public IAura getAura() {
        return AuraController.getInstance().get(auraID);
    }

    @Override
    public void setAura(IAura aura) {
        if (aura == null)
            this.auraID = -1;
        else
            setAura(aura.getID());
    }

    @Override
    public void setAura(int auraID) {
        if (AuraController.Instance.has(auraID))
            this.auraID = auraID;
        else
            this.auraID = -1;
    }

    @Override
    public boolean isAuraToggled() {
        return auraOn;
    }

    @Override
    public void toggleAura(boolean toggle) {
        this.auraOn = toggle;
    }

    @Override
    public boolean isInAura(IAura aura) {
        return aura.getID() == auraID;
    }

    public boolean isAuraOn() {
        return auraOn || isTransforming;
    }

    @Override
    public boolean isFusionSpectator() {
        return false;
    }

    @Override
    public void useStencilBuffer(boolean use) {
        this.useStencilBuffer = use;
    }

    @Override
    public HashMap getDBCAuras(boolean secondary) {
        if (!secondary)
            return dbcAuraQueue;
        else
            return dbcSecondaryAuraQueue;
    }

    public Aura getToggledAura() {
        if (!auraOn && !isTransforming)
            return null;

        return getAur();
    }

    //internal usage
    public Aura getAur() {
        if (isInForm()) {
            Form form = getForm();
            if (form.display.hasAura())
                return form.display.getAur();
        }

        return (Aura) AuraController.getInstance().get(auraID);
    }

    /////////////////////////////////////////////
    /////////////////////////////////////////////
    // Forms

    @Override
    public void transform(int id) {
        if (FormController.Instance.has(id)) {
            isTransforming = true;
            selectedForm = id;
        } else
            throw new CustomNPCsException("Form " + id + " does not exist!");
    }

    @Override
    public void transform(IForm form) {
        transform(form.getID());
    }

    @Override
    public void cancelTransformation() {
        selectedForm = -1;
        isTransforming = false;
    }

    @Override
    public void descend(int id) {
        TransformController.npcDescend(npc, id);
    }

    @Override
    public void descend(IForm form) {
        TransformController.npcDescend(npc, form == null ? -1 : form.getID());
    }

    @Override
    public IOutline getOutline() {
        Aura aura = getToggledAura();
        OutlineController OC = OutlineController.getInstance();

        if (aura != null && OC.has(aura.display.outlineID))
            return (Outline) OC.get(aura.display.outlineID);

        Form form = getForm();

        if (form != null && OC.has(form.display.outlineID))
            return (Outline) OC.get(form.display.outlineID);

        IAura formAura = form != null ? form.display.getAura() : null;

        if (formAura != null) {
            aura = (Aura) formAura;
            if (aura.display.outlineAlwaysOn && OC.has(aura.display.outlineID)) {
                return (Outline) OC.get(aura.display.outlineID);
            }
        }

        aura = (Aura) AuraController.Instance.get(auraID);
        if (aura != null && aura.display.outlineAlwaysOn && OC.has(aura.display.outlineID)) {
            return (Outline) OC.get(aura.display.outlineID);
        }


        return OutlineController.getInstance().get(outlineID);
    }

    @Override
    public void setOutline(int id) {
        if (OutlineController.Instance.has(id))
            this.outlineID = id;
        else
            outlineID = -1;
    }

    @Override
    public void setOutline(IOutline outline) {
        int id = outline != null ? outline.getID() : -1;
        setOutline(id);
    }

    public Form getForm() {
        if (formID == -100 && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            if (fakeForm == null)
                fakeForm = new Form(-100, "EXTREME_FAKE_FORM");
            return fakeForm;
        }
        if (formID > 0)
            return (Form) FormController.Instance.get(formID);
        return null;
    }

    @Override
    public void setForm(int id) {
        Form f = (Form) FormController.Instance.get(id);
        if (f != null)
            formID = f.id;
    }

    @Override
    public void setForm(IForm form) {
        int id = form != null ? form.getID() : -1;
        setForm(id);
    }

    public boolean isInForm() {
        return formID > -1 && getForm() != null;
    }

    @Override
    public IForm getCurrentForm() {
        return getForm();
    }

    @Override
    public boolean isInForm(IForm form) {
        return formID == form.getID();
    }

    @Override
    public void setFormLevel(float amount) {
        formLevel = amount;
    }

    @Override
    public float getFormLevel(int formID) {
        if (formID != -1)
            return formLevel;
        return 0f;
    }

    public void setDefaultColors() {
        eyeColor = 0x0;

        if (race < 3) {
            bodyCM = 16297621;
            bodyC1 = 6498048;
            bodyC2 = -1;
            bodyC3 = -1;
            furColor = -1;
        } else if (race == DBCRace.NAMEKIAN) {
            hairColor = 5095183;
            bodyCM = 5095183;
            bodyC1 = 13796998;
            bodyC2 = 12854822;
            bodyC3 = -1;
        } else if (race == DBCRace.ARCOSIAN) {
            eyeColor = 0xFF0000;
            bodyCM = 15460342;
            bodyC1 = 16111595;
            bodyC2 = 8533141;
            bodyC3 = 16550015;
        } else if (race == DBCRace.MAJIN) {
            eyeColor = 0xFF0000;
            bodyCM = 16757199;
            bodyC1 = -1;
            bodyC2 = -1;
            bodyC3 = -1;
        }
    }

    public void clearHairCode(boolean forceDefaultHair) {
        hairColor = 0x0;
        boolean useDefaultHair = (forceDefaultHair || hairCode == null || hairCode.isEmpty());
        if (race < 3)
            hairCode = useDefaultHair ? DBCHair.GOKU_HAIR : "";
        else if (race < 5)
            hairCode = "";
        else if (race == DBCRace.MAJIN) {
            hairCode = useDefaultHair ? DBCHair.MAJIN_HAIR : "";
            hairColor = bodyCM;
        }

        hairType = "base";
    }

    public void setDefaultHair() {
        clearHairCode(true);
    }

    @Override
    public EntityAura getAuraEntity() {
        return this.auraEntity;
    }

    @Override
    public void setAuraEntity(EntityAura aura) {
        this.auraEntity = aura;
    }

    @Override
    public Entity getEntity() {
        return npc;
    }

    @Override
    public int getAuraColor() {
        return 11075583;
    }

    @Override
    public int getActiveAuraColor() {
        return activeAuraColor;
    }

    @Override
    public void setActiveAuraColor(int color) {
        activeAuraColor = color;
    }

    @Override
    public boolean isTransforming() {
        return isTransforming;
    }

    @Override
    public boolean isChargingKi() {
        return auraOn;
    }

    @Override
    public boolean isInKaioken() {
        return isKaioken;
    }

    @Override
    public int getFormID() {
        return formID;
    }

    @Override
    public byte getRelease() {
        return ((INPCStats) npc.stats).getDBCStats().release;
    }

    @Override
    public byte getState() {
        return 0;
    }

    @Override
    public byte getState2() {
        return 0;
    }

    @Override
    public boolean isForm(int form) {
        return false;
    }

    @Override
    public int getDBCColor() {
        return getAuraColor();
    }

    @Override
    public List<EntityCusPar> getParticles() {
        return particleRenderQueue;
    }

    public void setRacialExtras() {
        ModelData data = ((EntityCustomNpc) npc).modelData;
        data.removePart("dbcHorn");
        data.removePart("tail");
        data.removePart("dbcArms");
        data.removePart("dbcBody");
        data.removePart("dbcEars");

        if (!enabled || !useSkin)
            return;

        if (DBCRace.isSaiyan(this.race)) {
            ModelPartData tail = data.getOrCreatePart("tail");
            tail.setTexture("tail/monkey1", 8);
            tail.pattern = tailState < 2 ? tailState : 0;
        } else if (this.race == DBCRace.ARCOSIAN) {
            ModelPartData tail = data.getOrCreatePart("tail");
            tail.setTexture("tail/monkey1", 8);
            tail.pattern = 2;

            ModelPartData ears = data.getOrCreatePart("dbcEars");
            ears.setTexture("tail/monkey1", 1);

            ModelPartData horn = data.getOrCreatePart("dbcHorn");
            ModelPartData arms;

            int arcoState = getArco();
            switch (arcoState) {
                case 0:
                case 1:
                    horn.setTexture("tail/monkey1", 2);
                    break;
                case 2:
                    horn.setTexture("tail/monkey1", 3);
                    break;
                case 3:
                    horn.setTexture("tail/monkey1", 4);
                    arms = data.getOrCreatePart("dbcArms");
                    arms.setTexture("tail/monkey1", 2);
                    break;
                case 4:
                    data.removePart("dbcHorn");
                    break;
                case 5:
                    horn.setTexture("tail/monkey1", 5);
                    arms = data.getOrCreatePart("dbcArms");
                    arms.setTexture("tail/monkey1", 1);
                    ModelPartData body = data.getOrCreatePart("dbcBody");
                    body.setTexture("tail/monkey1", 1);
                    break;
                default:
                    data.removePart("dbcHorn");
                    break;
            }
        } else if (this.race == DBCRace.NAMEKIAN) {
            ModelPartData horn = data.getOrCreatePart("dbcHorn");
            horn.setTexture("tail/monkey1", 1);
        }
    }

    //Integral
    public int getArco() {
        int arcoState = this.arcoState;
        Form form = getForm();
        if (form != null) {
            if (form.display.bodyType.equals("firstform")) {
                arcoState = 0;
            } else if (form.display.bodyType.equals("secondform")) {
                arcoState = 2;
            } else if (form.display.bodyType.equals("thirdform")) {
                arcoState = 3;
            } else if (form.display.bodyType.equals("finalform")) {
                arcoState = 4;
            } else if (form.display.bodyType.equals("ultimatecooler")) {
                arcoState = 5;
            } else if (form.display.bodyType.equals("golden")) {
                arcoState = 6;
            }
        }
        return arcoState;
    }

    @Override
    public IDBCDisplay clone() {
        DBCDisplay aura = new DBCDisplay(null);
        aura.readFromNBT(writeToNBT(new NBTTagCompound()));
        return aura;
    }

    @SideOnly(Side.CLIENT)
    public static EntityCustomNpc setupGUINPC(EntityCustomNpc originalNPC) {
        DBCDisplay origDisplay = null;
        EntityCustomNpc npc = new EntityCustomNpc(Minecraft.getMinecraft().theWorld);

        if (originalNPC != null)
            origDisplay = ((INPCDisplay) originalNPC.display).getDBCDisplay();

        DBCDisplay visualDisplay = ((INPCDisplay) npc.display).getDBCDisplay();
        if (origDisplay != null && origDisplay.enabled && origDisplay.useSkin) {
            visualDisplay.readFromNBT(origDisplay.writeToNBT(new NBTTagCompound()));
            if (DBCRace.isSaiyan(visualDisplay.race))
                visualDisplay.tailState = 1;

            visualDisplay.setRacialExtras();
        } else {
            visualDisplay.enabled = true;
            visualDisplay.useSkin = true;
            visualDisplay.race = DBCData.getClient().Race;
            visualDisplay.setDefaultColors();
            boolean isSaiyan = DBCRace.isSaiyan(visualDisplay.race);
            if (isSaiyan) {
                visualDisplay.tailState = 1;
            }

            if (visualDisplay.race == DBCRace.ARCOSIAN || visualDisplay.race == DBCRace.NAMEKIAN) {
                visualDisplay.hairCode = "";
            } else if (visualDisplay.race == DBCRace.MAJIN) {
                visualDisplay.hairCode = DBCHair.MAJIN_HAIR;
                visualDisplay.hairColor = visualDisplay.bodyCM;
            }
            visualDisplay.setRacialExtras();
        }
        return npc;
    }

    public void sendCurrentFormColorData() {
        FormDisplay.BodyColor currentColors = formColor;
        NBTTagCompound dataNeededOnClient = new NBTTagCompound();

        if (currentColors != null) {
            NBTTagCompound colorCompound = new NBTTagCompound();
            currentColors.writeToNBT(colorCompound);
            dataNeededOnClient.setTag("CustomFormColors", colorCompound);
        }

        DBCPacketHandler.Instance.sendTracking(new NPCUpdateForcedColors(npc, dataNeededOnClient), npc);
    }

    @SideOnly(Side.CLIENT)
    public void loadClientSideFormColorData(NBTTagCompound c) {
        formColor = new FormDisplay.BodyColor();
        if (c.hasKey("CustomFormColors", Constants.NBT.TAG_COMPOUND)) {
            formColor.readFromNBT(c.getCompoundTag("CustomFormColors"));
        }
    }
}
