package kamkeel.npcdbc.data;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.api.ICustomForm;
import kamkeel.npcdbc.api.IFormMastery;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.AnimationController;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.scripted.CustomNPCsException;
import noppes.npcs.scripted.NpcAPI;

public class CustomForm implements ICustomForm {

    public int id = -1; // Only for internal usage
    public String name = "";
    //name to be displayed in DBC GUI after "Form: ", preferably short as space is narrow
    public String menuName = "§2§lCustom Form";
    public int race = DBCRace.ALL;

    public float strengthMulti = 1.0f;
    public float dexMulti = 1.0f;
    public float willMulti = 1.0f;

    public boolean kaiokenStackable = true, uiStackable = true, godStackable = true, mysticStackable = true;
    public float kaiokenStrength = 1.2f, uiStrength = 3.0f, godStrength = 3.0f, mysticStrength = 1.8f;

    public String hairCode = "", hairType = "ssj";
    public int auraColor = 1, hairColor, eyeColor, bodyCM, bodyC1, bodyC2, bodyC3, furColor = 14292268;
    public boolean hasAuraColor = true, hasHairColor = true, hasEyeColor = true, hasBodyCM = false, hasBodyC1 = false, hasBodyC2 = false, hasBodyC3 = false, hasFurColor = true;
    public boolean hasSize = true;

    public float formSize = 20.0f;

    public String ascendSound = "jinryuudragonbc:1610.sss", descendSound = CustomNpcPlusDBC.ID + ":transformationSounds.GodDescend";

    public float kaiokenState2Factor = 1.0f, uiState2Factor = 1.0f;
    public FormMastery formMastery = new FormMastery(this);

    public CustomForm() {
    }

    public CustomForm(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getHairCode() {
        return hairCode;
    }

    @Override
    public void setHairCode(String hairCode) {
        this.hairCode = hairCode;
        save();
    }

    @Override
    public boolean hasColor(String type) {
        switch (type.toLowerCase()) {
            case "aura":
                return hasAuraColor;
            case "hair":
                return hasHairColor;
            case "eye":
                return hasEyeColor;
            case "bodymain":
                return hasBodyCM;
            case "body1":
                return hasBodyC1;
            case "body2":
                return hasBodyC2;
            case "body3":
                return hasBodyC3;
            case "fur":
                return hasFurColor;

        }
        throw new CustomNPCsException("Invalid type! Legal types: aura, hair, eye, bodyMain, body1, body2, body3, fur");
    }

    public void setHasColor(String type, boolean has) {
        switch (type.toLowerCase()) {
            case "aura":
                hasAuraColor = has;
                break;
            case "hair":
                hasHairColor = has;
                break;
            case "eye":
                hasEyeColor = has;
                break;
            case "bodymain":
                hasBodyCM = has;
                break;
            case "body1":
                hasBodyC1 = has;
                break;
            case "body2":
                hasBodyC2 = has;
                break;
            case "body3":
                hasBodyC3 = has;
                break;
            case "fur":
                hasFurColor = has;
                break;
            default:
                throw new CustomNPCsException("Invalid type! Legal types: aura, hair, eye, bodyMain, body1, body2, body3, fur");
        }
        save();
    }

    public void setColor(String type, int color) {
        switch (type.toLowerCase()) {
            case "aura":
                auraColor = color;
                break;
            case "hair":
                hairColor = color;
                break;
            case "eye":
                eyeColor = color;
                break;

            case "bodymain":
                bodyCM = color;
                break;
            case "body1":
                bodyC1 = color;
                break;
            case "body2":
                bodyC2 = color;
                break;
            case "body3":
                bodyC3 = color;
                break;
            case "fur":
                furColor = color;
                break;
            default:
                throw new CustomNPCsException("Invalid type! Legal types: aura, hair, eye, bodyMain, body1, body2, body3, fur");
        }
        save();
    }

    @Override
    public void setHairType(String type) {
        String s = type.toLowerCase();
        if (s.equals("base") || s.equals("ssj") || s.equals("ssj2") || s.equals("ssj3") || s.equals("ssj4") || s.equals("oozaru") || s.equals("")) {
            hairType = s;

        } else {
            hairType = "";
            throw new CustomNPCsException("Invalid type!");
        }
        save();
    }

    @Override
    public String getHairType(String type) {
        String s = type.toLowerCase();
        if (s.equals("base") || s.equals("ssj") || s.equals("ssj2") || s.equals("ssj3") || s.equals("ssj4") || s.equals("oozaru") || s.equals(""))
            return hairType;
        else
            throw new CustomNPCsException("Invalid type!");
    }

    public int getColor(String type) {
        switch (type.toLowerCase()) {
            case "aura":
                return auraColor;
            case "hair":
                return hairColor;
            case "eye":
                return eyeColor;
            case "bodymain":
                return bodyCM;
            case "body1":
                return bodyC1;
            case "body2":
                return bodyC2;
            case "body3":
                return bodyC3;
            case "fur":
                return furColor;
        }
        throw new CustomNPCsException("Invalid type! Legal types: aura, hair, eye, bodyMain, body1, body2, body3, fur");
    }

    public void setState2Factor(int dbcForm, float factor) {
        switch (dbcForm) {
            case DBCForm.Kaioken:
                kaiokenState2Factor = factor;
                break;
            case DBCForm.UltraInstinct:
                uiState2Factor = factor;
                break;
        }
        save();
    }

    public float getState2Factor(int dbcForm) {
        switch (dbcForm) {
            case DBCForm.Kaioken:
                return kaiokenState2Factor;
            case DBCForm.UltraInstinct:
                return uiState2Factor;
            default:
                return 1.0f;
        }
    }

    @Override
    public float getSize() {
        return formSize;
    }

    @Override
    public void setSize(float size) {
        formSize = Math.min(size, 50);
        save();
    }

    @Override
    public boolean hasSize() {
        return hasSize;
    }

    @Override
    public void setHasSize(boolean hasSize) {
        this.hasSize = hasSize;
        save();
    }

    public float[] getAllMulti() {
        return new float[]{strengthMulti, dexMulti, willMulti};
    }

    @Override
    public void setAllMulti(float allMulti) {
        this.strengthMulti = allMulti;
        this.dexMulti = allMulti;
        this.willMulti = allMulti;
        save();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
        save();
    }

    @Override
    public String getMenuName() {
        return menuName;
    }

    @Override
    public void setMenuName(String name) {
        if (name.contains("&"))
            name = name.replace("&", "§");

        this.menuName = name;
        save();
    }


    @Override
    public int getRace() {
        return race;
    }

    @Override
    public void setRace(int race) {
        this.race = race;
        save();
    }

    @Override
    public void setAttributeMulti(int id, float multi) {
        switch (id) {
            case 0:
                strengthMulti = multi;
                break;
            case 1:
                dexMulti = multi;
                break;
            case 3:
                willMulti = multi;
                break;
        }
        save();
    }

    @Override
    public float getAttributeMulti(int id) {
        switch (id) {
            case 0:
                return strengthMulti;
            case 1:
                return dexMulti;
            case 3:
                return willMulti;

        }
        return 1.0f;
    }

    public boolean isFormStackable(int dbcForm) {
        switch (dbcForm) {
            case DBCForm.Kaioken:
                return kaiokenStackable;
            case DBCForm.UltraInstinct:
                return uiStackable;
            case DBCForm.GodOfDestruction:
                return godStackable;
            case DBCForm.Mystic:
                return mysticStackable;
            default:
                return false;
        }
    }


    public void stackForm(int dbcForm, boolean stackForm) {
        switch (dbcForm) {
            case DBCForm.Kaioken:
                kaiokenStackable = stackForm;
                break;
            case DBCForm.UltraInstinct:
                uiStackable = stackForm;
                break;
            case DBCForm.GodOfDestruction:
                godStackable = stackForm;
                break;
            case DBCForm.Mystic:
                mysticStackable = stackForm;
                break;
        }
        save();

    }

    public void setFormMulti(int dbcForm, float multi) {
        switch (dbcForm) {
            case DBCForm.Kaioken:
                kaiokenStrength = multi;
                break;
            case DBCForm.UltraInstinct:
                uiStrength = multi;
                break;
            case DBCForm.GodOfDestruction:
                godStrength = multi;
                break;
            case DBCForm.Mystic:
                mysticStrength = multi;
                break;
        }
        save();

    }

    public float getFormMulti(int dbcForm) {
        switch (dbcForm) {
            case DBCForm.Kaioken:
                return kaiokenStrength;
            case DBCForm.UltraInstinct:
                return uiStrength;
            case DBCForm.GodOfDestruction:
                return godStrength;
            case DBCForm.Mystic:
                return mysticStrength;
            default:
                return 1.0f;
        }
    }

    @Override
    public int getAuraColor() {
        return auraColor;
    }

    @Override
    public void setAuraColor(int auraColor) {
        this.auraColor = auraColor;
        save();
    }

    @Override
    public void assignToPlayer(EntityPlayer p) {
        if (race == DBCRace.ALL || race == DBCData.get(p).Race) {
            PlayerData playerData = PlayerDataController.Instance.getPlayerData(p);
            PlayerCustomFormData formData = Utility.getFormData(playerData);
            formData.addForm(this);
            formData.updateClient();
            playerData.updateClient = true;
            playerData.save();
        }
    }

    public void assignToPlayer(String name) {
        assignToPlayer(NpcAPI.Instance().getPlayer(name).getMCEntity());
    }


    @Override
    public void removeFromPlayer(EntityPlayer p) {
        PlayerData playerData = PlayerDataController.Instance.getPlayerData(p);
        PlayerCustomFormData formData = Utility.getFormData(playerData);
        formData.removeForm(this);
        if (formData.selectedForm == this.id)
            formData.selectedForm = -1;

        formData.updateClient();
        playerData.save();
    }

    public void removeFromPlayer(String name) {
        removeFromPlayer(NpcAPI.Instance().getPlayer(name).getMCEntity());
    }

    @Override
    public String getAscendSound() {
        return ascendSound;
    }

    @Override
    public void setAscendSound(String directory) {
        ascendSound = directory;
        save();
    }

    @Override
    public String getDescendSound() {
        return descendSound;
    }

    @Override
    public void setDescendSound(String directory) {
        descendSound = directory;
        save();
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setID(int newID) {
        id = newID;
        save();
    }

    @Override
    public IFormMastery getFormMastery() {
        return formMastery;
    }

    //internal usage
    public FormMastery getFM() {
        return formMastery;
    }

    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("ID"))
            id = compound.getInteger("ID");
        else if (AnimationController.Instance != null)
            id = FormController.Instance.getUnusedId();

        name = compound.getString("name");
        menuName = compound.getString("menuName");
        race = compound.getInteger("race");
        formSize = compound.getFloat("formSize");
        hasSize = compound.getBoolean("hasSize");

        NBTTagCompound attributes = compound.getCompoundTag("attributes");
        strengthMulti = attributes.getFloat("strMulti");
        dexMulti = attributes.getFloat("dexMulti");
        willMulti = attributes.getFloat("willMulti");

        NBTTagCompound stack = compound.getCompoundTag("stackableForms");
        kaiokenStrength = stack.getFloat("kaiokenStrength");
        kaiokenStackable = stack.getBoolean("kaiokenStackable");
        kaiokenState2Factor = stack.getFloat("kaiokenState2Factor");
        uiStrength = stack.getFloat("uiStrength");
        uiStackable = stack.getBoolean("uiStackable");
        uiState2Factor = stack.getFloat("uiState2Factor");
        godStrength = stack.getFloat("godStrength");
        godStackable = stack.getBoolean("godStackable");
        mysticStrength = stack.getFloat("mysticStrength");
        mysticStackable = stack.getBoolean("mysticStackable");


        NBTTagCompound rendering = compound.getCompoundTag("rendering");
        auraColor = rendering.getInteger("auraColor");
        eyeColor = rendering.getInteger("eyeColor");
        hairColor = rendering.getInteger("hairColor");
        furColor = rendering.getInteger("furColor");
        bodyCM = rendering.getInteger("bodyCM");
        bodyC1 = rendering.getInteger("bodyC1");
        bodyC2 = rendering.getInteger("bodyC2");
        bodyC3 = rendering.getInteger("bodyC3");
        hasBodyCM = rendering.getBoolean("hasBodyCM");
        hasBodyC1 = rendering.getBoolean("hasBodyC1");
        hasBodyC2 = rendering.getBoolean("hasBodyC2");
        hasBodyC3 = rendering.getBoolean("hasBodyC3");
        hairCode = rendering.getString("hairCode");
        hairType = rendering.getString("hairType");

        NBTTagCompound sounds = compound.getCompoundTag("sounds");
        ascendSound = sounds.getString("ascendSound");
        descendSound = sounds.getString("descendSound");

        formMastery.readFromNBT(compound);
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("ID", id);
        compound.setString("name", name);
        compound.setString("menuName", menuName);
        compound.setInteger("race", race);
        compound.setFloat("formSize", formSize);
        compound.setBoolean("hasSize", hasSize);

        NBTTagCompound attributes = new NBTTagCompound();
        compound.setTag("attributes", attributes);
        attributes.setFloat("strMulti", strengthMulti);
        attributes.setFloat("dexMulti", dexMulti);
        attributes.setFloat("willMulti", willMulti);

        NBTTagCompound stack = new NBTTagCompound();
        compound.setTag("stackableForms", stack);
        stack.setFloat("kaiokenStrength", kaiokenStrength);
        stack.setBoolean("kaiokenStackable", kaiokenStackable);
        stack.setFloat("kaiokenState2Factor", kaiokenState2Factor);
        stack.setFloat("uiStrength", uiStrength);
        stack.setBoolean("uiStackable", uiStackable);
        stack.setFloat("uiState2Factor", uiState2Factor);
        stack.setFloat("godStrength", godStrength);
        stack.setBoolean("godStackable", godStackable);
        stack.setFloat("mysticStrength", mysticStrength);
        stack.setBoolean("mysticStackable", mysticStackable);

        NBTTagCompound rendering = new NBTTagCompound();
        compound.setTag("rendering", rendering);
        rendering.setInteger("auraColor", auraColor);
        rendering.setInteger("eyeColor", eyeColor);
        rendering.setInteger("hairColor", hairColor);
        rendering.setString("hairCode", hairCode);
        rendering.setString("hairType", hairType);
        rendering.setInteger("furColor", furColor);
        rendering.setInteger("bodyCM", bodyCM);
        rendering.setInteger("bodyC1", bodyC1);
        rendering.setInteger("bodyC2", bodyC2);
        rendering.setInteger("bodyC3", bodyC3);
        rendering.setBoolean("hasBodyCM", hasBodyCM);
        rendering.setBoolean("hasBodyC1", hasBodyC1);
        rendering.setBoolean("hasBodyC2", hasBodyC2);
        rendering.setBoolean("hasBodyC3", hasBodyC3);

        NBTTagCompound sounds = new NBTTagCompound();
        sounds.setString("ascendSound", ascendSound);
        sounds.setString("descendSound", descendSound);
        compound.setTag("sounds", sounds);

        formMastery.writeToNBT(compound);
        return compound;
    }

    @Override
    public ICustomForm save() {
        return FormController.Instance.saveForm(this);
    }


}
