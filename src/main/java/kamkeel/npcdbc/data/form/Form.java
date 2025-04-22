package kamkeel.npcdbc.data.form;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.api.form.*;
import kamkeel.npcdbc.config.ConfigDBCGeneral;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NBTTags;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.controllers.AnimationController;
import noppes.npcs.scripted.NpcAPI;

import java.util.HashMap;

public class Form implements IForm {

    public int id = -1; // Only for internal usage
    public String name = "";

    public String menuName = "§aNEW";
    private int race = DBCRace.ALL;
    public int timer = -1;

    public FormMastery mastery = new FormMastery(this);
    public FormDisplay display = new FormDisplay(this);
    public FormStackable stackable = new FormStackable(this);
    public FormAdvanced advanced = new FormAdvanced(this);
    public FormAttributes customAttributes = new FormAttributes(this);
    public FormMagicData magicData = new FormMagicData(this);

    /**
     * ID of parent and child forms of this
     */
    public HashMap<Integer, Byte> requiredForm = new HashMap<>();

    /**
     * ID of parent and child forms of this
     */
    public int childID = -1, parentID = -1;
    public boolean fromParentOnly = true;

    public float strengthMulti = 1.0f;
    public float dexMulti = 1.0f;
    public float willMulti = 1.0f;

    public int mindRequirement = 0;

    public String ascendSound = "jinryuudragonbc:1610.sss", descendSound = CustomNpcPlusDBC.ID + ":transformationSounds.GodDescend";

    public Form() {
    }

    public Form(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("ID"))
            id = compound.getInteger("ID");
        else if (AnimationController.Instance != null)
            id = FormController.Instance.getUnusedId();

        name = compound.getString("name");
        menuName = compound.getString("menuName");
        race = compound.getInteger("race");
        timer = compound.getInteger("timer");
        childID = compound.getInteger("childID");
        parentID = compound.getInteger("parentID");
        fromParentOnly = compound.getBoolean("fromParentOnly");
        requiredForm = NBTTags.getIntegerByteMap(compound.getTagList("requiredForm", 10));
        mindRequirement = compound.getInteger("mindRequirement");

        NBTTagCompound attributes = compound.getCompoundTag("attributes");
        strengthMulti = attributes.getFloat("strMulti");
        dexMulti = attributes.getFloat("dexMulti");
        willMulti = attributes.getFloat("willMulti");

        NBTTagCompound sounds = compound.getCompoundTag("sounds");
        ascendSound = sounds.getString("ascendSound");
        descendSound = sounds.getString("descendSound");

        mastery.readFromNBT(compound);
        display.readFromNBT(compound);
        stackable.readFromNBT(compound);
        advanced.readFromNBT(compound);
        customAttributes.readFromNBT(compound);
        magicData.readFromNBT(compound);
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("ID", id);
        compound.setString("name", name);
        compound.setString("menuName", menuName);
        compound.setInteger("race", race);
        compound.setInteger("timer", timer);
        compound.setInteger("childID", childID);
        compound.setInteger("parentID", parentID);
        compound.setBoolean("fromParentOnly", fromParentOnly);
        compound.setInteger("mindRequirement", mindRequirement);
        compound.setTag("requiredForm", NBTTags.nbtIntegerByteMap(requiredForm));

        NBTTagCompound attributes = new NBTTagCompound();
        attributes.setFloat("strMulti", strengthMulti);
        attributes.setFloat("dexMulti", dexMulti);
        attributes.setFloat("willMulti", willMulti);
        compound.setTag("attributes", attributes);

        NBTTagCompound sounds = new NBTTagCompound();
        sounds.setString("ascendSound", ascendSound);
        sounds.setString("descendSound", descendSound);
        compound.setTag("sounds", sounds);

        mastery.writeToNBT(compound);
        display.writeToNBT(compound);
        stackable.writeToNBT(compound);
        advanced.writeToNBT(compound);
        customAttributes.writeToNBT(compound);
        magicData.writeToNBT(compound);
        return compound;
    }

    public float[] getAllMulti() {
        return new float[]{strengthMulti, dexMulti, willMulti};
    }

    @Override
    public void setAllMulti(float allMulti) {
        this.strengthMulti = allMulti;
        this.dexMulti = allMulti;
        this.willMulti = allMulti;
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

    //internal usage
    public int race() {
        if (race == DBCRace.ALL_SAIYANS)
            return 1;
        else
            return race;
    }

    @Override
    public int getRace() {
        return race;
    }

    @Override
    public void setRace(int race) {
        if ((race > 5 || race < -1) && race != DBCRace.ALL_SAIYANS)
            return;

        this.race = race;
    }

    @Override
    public boolean raceEligible(IPlayer player) {
        return raceEligible((EntityPlayer) player.getMCEntity());
    }

    public boolean raceEligible(EntityPlayer player) {
        return raceEligible(DBCData.get(player).Race);
    }

    public boolean raceEligible(int race) {
        if (this.race == DBCRace.ALL_SAIYANS)
            return DBCRace.isSaiyan(race);

        return this.race == DBCRace.ALL || this.race == race;
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

    @Override
    public void assignToPlayer(IPlayer player) {
        if (raceEligible(player)) {
            PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo((EntityPlayer) player.getMCEntity());
            formData.addForm(this);
            formData.updateClient();
        }
    }

    @Override
    public void assignToPlayer(String playerName) {
        assignToPlayer(NpcAPI.Instance().getPlayer(playerName));
    }

    @Override
    public void removeFromPlayer(IPlayer player) {
        removeFromPlayer(player, ConfigDBCGeneral.FORM_MASTERIES_CLEAR_ON_REMOVE);
    }

    public void removeFromPlayer(String playerName) {
        removeFromPlayer(playerName, ConfigDBCGeneral.FORM_MASTERIES_CLEAR_ON_REMOVE);
    }

    @Override
    public void removeFromPlayer(IPlayer player, boolean removesMastery) {
        PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo((EntityPlayer) player.getMCEntity());
        formData.removeForm(this, removesMastery);
        if (formData.selectedForm == this.id)
            formData.selectedForm = -1;

        formData.updateClient();
    }

    @Override
    public void removeFromPlayer(String playerName, boolean removesMastery) {
        removeFromPlayer(NpcAPI.Instance().getPlayer(playerName), removesMastery);
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

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setID(int newID) {
        id = newID;
    }

    @Override
    public void linkChild(int formID) {
        if (formID == this.id)
            return;

        Form form = (Form) FormController.getInstance().get(formID);
        if (form != null) {
            childID = formID;
            form.parentID = this.id;
        }
    }

    @Override
    public void linkChild(IForm form) {
        linkChild(form.getID());
    }

    @Override
    public void linkParent(int formID) {
        if (formID == this.id)
            return;

        Form form = (Form) FormController.getInstance().get(formID);
        if (form != null) {
            parentID = formID;
            form.childID = this.id;
        }
    }

    @Override
    public void linkParent(IForm form) {
        linkParent(form.getID());
    }

    @Override
    public boolean isFromParentOnly() {
        return fromParentOnly;
    }

    @Override
    public void setFromParentOnly(boolean set) {
        fromParentOnly = set;
    }

    @Override
    public void addFormRequirement(int race, byte state) {
        if ((race > 5 || race < 0) && race != 12)
            return;

        // Add some kind of validate State Index Here
        // Goatee

        requiredForm.put(race, state);
    }

    @Override
    public void removeFormRequirement(int race) {
        if ((race > 5 || race < 0) && race != 12)
            return;
        requiredForm.remove(race);
    }

    @Override
    public int getFormRequirement(int race) {
        if (!requiredForm.containsKey(race))
            return -1;

        return requiredForm.get(race);
    }

    @Override
    public boolean isChildOf(IForm parent) {
        while (parent.getChildID() != -1) {
            if (parent.getID() == id || parent.getChildID() == id)
                return true;
            parent = parent.getChild();
        }
        return false;
    }

    @Override
    public IForm getChild() {
        return FormController.Instance.get(childID);
    }

    @Override
    public int getChildID() {
        return childID;
    }

    @Override
    public boolean hasChild() {
        return childID != -1 && FormController.getInstance().has(childID);
    }

    public void removeChildForm() {
        if (childID != -1) {
            Form child = (Form) getChild();
            if (child != null)
                child.parentID = -1;
        }
        childID = -1;
    }

    public IForm getParent() {
        return FormController.Instance.get(parentID);
    }

    @Override
    public int getTimer() {
        return timer;
    }

    @Override
    public void setTimer(int timeInTicks) {
        timer = timeInTicks;
    }

    @Override
    public boolean hasTimer() {
        return timer > 0;
    }

    @Override
    public int getParentID() {
        return parentID;
    }

    @Override
    public boolean hasParent() {
        return parentID != -1 && FormController.getInstance().has(parentID);
    }

    public void removeParentForm() {
        if (parentID != -1) {
            Form parent = (Form) getParent();
            if (parent != null)
                parent.childID = -1;
        }
        parentID = -1;
    }

    @Override
    public IFormMastery getMastery() {
        return mastery;
    }

    @Override
    public IFormDisplay getDisplay() {
        return display;
    }

    @Override
    public IFormStackable getStackable() {
        return stackable;
    }

    @Override
    public IFormAdvanced getAdvanced() {
        return advanced;
    }

    @Override
    public void setMindRequirement(int mind) {
        mindRequirement = Math.max(mind, 0);
    }

    @Override
    public int getMindRequirement() {
        return mindRequirement;
    }

    @Override
    public IForm clone() {
        Form form = new Form();
        form.readFromNBT(writeToNBT());
        form.id = FormController.Instance.getUnusedId();
        return form;
    }

    @Override
    public IForm save() {
        return FormController.Instance.saveForm(this);
    }
}
