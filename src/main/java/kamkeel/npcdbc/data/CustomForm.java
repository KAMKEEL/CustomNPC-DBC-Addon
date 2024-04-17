package kamkeel.npcdbc.data;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.api.ICustomForm;
import kamkeel.npcdbc.api.IFormMastery;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.SyncedData.DBCData;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.AnimationController;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;
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

    public int auraColor = 1, hairColor, eyeColor, bodyCM, bodyC1, bodyC2, bodyC3;

    public String ascendSound = "jinryuudragonbc:1610.sss", descendSound = CustomNpcPlusDBC.ID + ":transformationSounds.GodDescend";
    public float kaiokenState2Factor = 1.0f, uiState2Factor = 1.0f;

    public FormMastery formMastery = new FormMastery(this);

    public CustomForm() {}

    public CustomForm(int id, String name) {
        this.id = id;
        this.name = name;
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
            Utility.getFormData(playerData).addForm(this);
            playerData.updateClient = true;
        }
    }

    public void assignToPlayer(String name) {
        assignToPlayer(NpcAPI.Instance().getPlayer(name).getMCEntity());
    }


    @Override
    public void removeFromPlayer(EntityPlayer p) {
        PlayerData playerData = PlayerDataController.Instance.getPlayerData(p);
        Utility.getFormData(playerData).removeForm(this);
        playerData.updateClient = true;
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

    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("ID"))
            id = compound.getInteger("ID");
        else if (AnimationController.Instance != null)
            id = FormController.Instance.getUnusedId();

        name = compound.getString("name");
        menuName = compound.getString("menuName");
        race = compound.getInteger("race");

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
        bodyCM = rendering.getInteger("bodyCM");
        bodyC1 = rendering.getInteger("bodyC1");
        bodyC2 = rendering.getInteger("bodyC2");
        bodyC3 = rendering.getInteger("bodyC3");

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
        rendering.setInteger("bodyCM", bodyCM);
        rendering.setInteger("bodyC1", bodyC1);
        rendering.setInteger("bodyC2", bodyC2);
        rendering.setInteger("bodyC3", bodyC3);

        NBTTagCompound sounds = new NBTTagCompound();
        compound.setTag("sounds", sounds);
        sounds.setString("ascendSound", ascendSound);
        sounds.setString("descendSound", descendSound);

        formMastery.writeToNBT(compound);
        return compound;
    }

    @Override
    public ICustomForm save() {
        return FormController.Instance.saveForm(this);
    }
}
