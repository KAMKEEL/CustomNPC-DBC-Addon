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

import java.util.ArrayList;
import java.util.List;

public class CustomForm implements ICustomForm {

    public int id = -1; // Only for internal usage
    public String name;
    //name to be displayed in DBC GUI after "Form: ", preferably short as space is narrow
    public String menuName = "§2§lCustom Form";
    public int race = DBCRace.SAIYAN;
    public float allMulti = 1.0f;

    public float strengthMulti = 1.0f;
    public float dexMulti = 1.0f;
    public float willMulti = 1.0f;

    public boolean kaiokenStackable = false, uiStackable = false, godStackable = false, mysticStackable = false;
    public float kaiokenMulti = 1.0f, uiMulti = 1.0f, godMulti = 1.0f, mysticMulti = 1.0f;

    public int auraColor = 1;

    public String ascendSound = "jinryuudragonbc:1610.sss", descendSound = CustomNpcPlusDBC.ID + ":transformationSounds.GodDescend";

    //players who have form unlocked in their accessibleForms NBT tag
    public List<String> playersWithForm = new ArrayList<>();


    public CustomForm() {
    }

    public CustomForm(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
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
    }

    @Override
    public int getRace() {
        return race;
    }

    @Override
    public void setRace(int race) {
        this.race = race;
    }

    @Override
    public float getAllMulti() {
        return allMulti;
    }

    @Override
    public void setAllMulti(float allMulti) {
        this.allMulti = allMulti;
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
    }

    @Override
    public void assignToPlayer(EntityPlayer p) {
        if (race == DBCData.get(p).Race) {
            PlayerData playerData = PlayerDataController.Instance.getPlayerData(p);
            Utility.getFormData(playerData).addForm(this);
            if (!playersWithForm.contains(p.getCommandSenderName())) {
                playersWithForm.add(p.getCommandSenderName());
                playerData.updateClient = true;
                save();
            }
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
            if (playersWithForm.contains(p.getCommandSenderName())) {
                playersWithForm.remove(p.getCommandSenderName());
                playerData.updateClient = true;
                save();
            }
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
    }

    @Override
    public String getDescendSound() {
        return descendSound;
    }

    @Override
    public void setDescendSound(String directory) {
        descendSound = directory;
    }

    public List<EntityPlayer> getPlayersWithForm() {
        List<EntityPlayer> plyrs = new ArrayList<>();
        for (String s : playersWithForm) {
            if (!s.isEmpty()) {
                EntityPlayer p = (EntityPlayer) NpcAPI.Instance().getPlayer("s");
                if (p != null)
                    plyrs.add(p);
            }
        }

        return plyrs;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setID(int newID) {
        id = newID;
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
        allMulti = compound.getFloat("allMulti");
        strengthMulti = compound.getFloat("strengthMulti");
        dexMulti = compound.getFloat("dexMulti");
        willMulti = compound.getFloat("willMulti");
        kaiokenMulti = compound.getFloat("kaiokenMulti");
        uiMulti = compound.getFloat("uiMulti");
        kaiokenStackable = compound.getBoolean("kaiokenStackable");
        uiStackable = compound.getBoolean("uiStackable");
        auraColor = compound.getInteger("auraColor");


        String s = compound.getString("playersWithForm");
        List<String> newPlayers = new ArrayList<>();
        if (s.contains(",")) {
            String[] forms = s.split(",");
            for (String str : forms)
                newPlayers.add(str);
        } else if (s.isEmpty())
            newPlayers.clear();
        else
            newPlayers.add(s);
        playersWithForm = newPlayers;

        // ADD REST

    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("ID", id);
        compound.setString("name", name);
        compound.setString("menuName", menuName);
        compound.setString("ascendSound", ascendSound);
        compound.setString("descendSound", descendSound);
        compound.setInteger("race", race);
        compound.setFloat("allMulti", allMulti);
        compound.setFloat("strengthMulti", strengthMulti);
        compound.setFloat("dexMulti", dexMulti);
        compound.setFloat("willMulti", willMulti);
        compound.setFloat("kaiokenMulti", kaiokenMulti);
        compound.setFloat("uiMulti", uiMulti);
        compound.setBoolean("kaiokenStackable", kaiokenStackable);
        compound.setBoolean("uiStackable", uiStackable);
        compound.setInteger("auraColor", auraColor);

        if (!playersWithForm.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String s : playersWithForm) {
                sb.append(s + ",");
            }
            compound.setString("playersWithForm", sb.toString().substring(0, sb.length() - 1)); //removes the last ,
        } else
            compound.setString("playersWithForm", "");

        // ADD REST


        return compound;
    }

    @Override
    public ICustomForm save() {
        return FormController.Instance.saveForm(this);
    }
}
