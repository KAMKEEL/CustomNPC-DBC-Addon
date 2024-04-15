package kamkeel.npcdbc.data;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.api.ICustomForm;
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
    public String name;
    //name to be displayed in DBC GUI after "Form: ", preferably short as space is narrow
    public String menuName = "§2§lCustom Form";
    public int race = DBCRace.SAIYAN;

    public float strengthMulti = 1.0f;
    public float dexMulti = 1.0f;
    public float willMulti = 1.0f;

    public boolean kaiokenStackable = false, uiStackable = false, godStackable = false, mysticStackable = false;
    public float kaiokenMulti = 1.0f, uiMulti = 1.0f, godMulti = 1.0f, mysticMulti = 1.0f;

    public int auraColor = 1;

    public String ascendSound = "jinryuudragonbc:1610.sss", descendSound = CustomNpcPlusDBC.ID + ":transformationSounds.GodDescend";

    public CustomForm() {
    }

    public CustomForm(int id, String name) {
        this.id = id;
        this.name = name;
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
                kaiokenMulti = multi;
                break;
            case DBCForm.UltraInstinct:
                uiMulti = multi;
                break;
            case DBCForm.GodOfDestruction:
                godMulti = multi;
                break;
            case DBCForm.Mystic:
                mysticMulti = multi;
                break;
        }
        save();

    }

    public float getFormMulti(int dbcForm) {
        switch (dbcForm) {
            case DBCForm.Kaioken:
                return kaiokenMulti;
            case DBCForm.UltraInstinct:
                return uiMulti;
            case DBCForm.GodOfDestruction:
                return godMulti;
            case DBCForm.Mystic:
                return mysticMulti;
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
        if (race == DBCData.get(p).Race) {
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
        if (race == DBCData.get(p).Race) {
            PlayerData playerData = PlayerDataController.Instance.getPlayerData(p);
            Utility.getFormData(playerData).removeForm(this);
            playerData.updateClient = true;
        }
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

    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("ID"))
            id = compound.getInteger("ID");
        else if (AnimationController.Instance != null)
            id = FormController.Instance.getUnusedId();

        name = compound.getString("name");
        menuName = compound.getString("menuName");
        ascendSound = compound.getString("ascendSound");
        descendSound = compound.getString("descendSound");
        race = compound.getInteger("race");
        strengthMulti = compound.getFloat("strengthMulti");
        dexMulti = compound.getFloat("dexMulti");
        willMulti = compound.getFloat("willMulti");
        kaiokenMulti = compound.getFloat("kaiokenMulti");
        uiMulti = compound.getFloat("uiMulti");
        kaiokenStackable = compound.getBoolean("kaiokenStackable");
        uiStackable = compound.getBoolean("uiStackable");
        auraColor = compound.getInteger("auraColor");
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("ID", id);
        compound.setString("name", name);
        compound.setString("menuName", menuName);
        compound.setString("ascendSound", ascendSound);
        compound.setString("descendSound", descendSound);
        compound.setInteger("race", race);
        compound.setFloat("strengthMulti", strengthMulti);
        compound.setFloat("dexMulti", dexMulti);
        compound.setFloat("willMulti", willMulti);
        compound.setFloat("kaiokenMulti", kaiokenMulti);
        compound.setFloat("uiMulti", uiMulti);
        compound.setBoolean("kaiokenStackable", kaiokenStackable);
        compound.setBoolean("uiStackable", uiStackable);
        compound.setInteger("auraColor", auraColor);
        return compound;
    }

    @Override
    public ICustomForm save() {
        return FormController.Instance.saveForm(this);
    }
}
