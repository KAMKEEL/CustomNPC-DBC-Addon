package kamkeel.npcdbc.data.form;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.api.form.IFormAdvanced;
import kamkeel.npcdbc.api.form.IFormDisplay;
import kamkeel.npcdbc.api.form.IFormMastery;
import kamkeel.npcdbc.api.form.IFormStackable;
import kamkeel.npcdbc.config.ConfigDBCGeneral;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NBTTags;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.controllers.AnimationController;
import noppes.npcs.controllers.TagController;
import noppes.npcs.scripted.NpcAPI;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class Form implements IForm {

    public int id = -1; // Only for internal usage
    public FormKey key = null;
    public String name = "";

    public String menuName = "§aNEW";
    public int timer = -1;

    private int race = DBCRace.ALL;
    public FormMastery mastery = new FormMastery(this);
    public FormDisplay display = new FormDisplay(this);
    public FormStackable stackable = new FormStackable(this);
    public FormCustomStackable customStackable = new FormCustomStackable(this);
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
    public String childKey = null, parentKey = null;
    public boolean fromParentOnly = true;

    public float strengthMulti = 1.0f;
    public float dexMulti = 1.0f;
    public float willMulti = 1.0f;

    public int mindRequirement = 0;

    public String ascendSound = "jinryuudragonbc:1610.sss", descendSound = CustomNpcPlusDBC.ID + ":transformationSounds.GodDescend";

    public HashSet<UUID> tagUUIDs = new HashSet<>();

    public boolean builtIn = false;

    public Form() {
    }

    public Form(FormKey key) {
        this.key = key;
        this.builtIn = true;
        this.name = key.name;
    }

    public Form(String formKey) {
        this(new FormKey(formKey));
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
        timer = compound.getInteger("timer");
        // Key-based links (new format)
        childKey = compound.hasKey("childKey") ? compound.getString("childKey") : null;
        parentKey = compound.hasKey("parentKey") ? compound.getString("parentKey") : null;
        // Legacy int links — kept only so FormController's post-load resolve pass can convert them
        childID = compound.hasKey("childID") ? compound.getInteger("childID") : -1;
        parentID = compound.hasKey("parentID") ? compound.getInteger("parentID") : -1;
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

        tagUUIDs = TagController.readTagUUIDs(compound, "TagUUIDs");

        race = compound.getInteger("race");
        if (compound.hasKey("FormKey")) {
            key = new FormKey(compound.getString("FormKey"));
            builtIn = true;
        }
        mastery.readFromNBT(compound);
        display.readFromNBT(compound);
        stackable.readFromNBT(compound);
        customStackable.readFromNBT(compound);
        advanced.readFromNBT(compound);
        customAttributes.readFromNBT(compound);
        magicData.readFromNBT(compound);
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("ID", id);
        compound.setString("name", name);
        compound.setString("menuName", menuName);
        compound.setInteger("timer", timer);
        if (childKey != null)
            compound.setString("childKey", childKey);
        if (parentKey != null)
            compound.setString("parentKey", parentKey);
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

        TagController.writeTagUUIDs(compound, "TagUUIDs", tagUUIDs);

        compound.setInteger("race", race);
        if (key != null)
            compound.setString("FormKey", key.toString());
        mastery.writeToNBT(compound);
        display.writeToNBT(compound);
        stackable.writeToNBT(compound);
        customStackable.writeToNBT(compound);
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

    public boolean hasKey() {
        return key != null;
    }

    public String getKeyString() {
        return key != null ? key.toString() : "";
    }

    @Override
    public void setName(String name) {
        if (name == null || name.trim().isEmpty())
            return;
        if (!builtIn && FormController.Instance != null) {
            Form existing = FormController.Instance.getFormFromName(name);
            if (existing != null && existing.id != this.id)
                return;
        }
        this.name = name;
        if (!builtIn)
            key = FormKey.custom(name);
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

    // internal usage
    public int race() {
        return race == DBCRace.ALL_SAIYANS ? 1 : race;
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
    public boolean raceEligible(int race) {
        if (this.race == DBCRace.ALL_SAIYANS)
            return DBCRace.isSaiyan(race);

        return this.race == DBCRace.ALL || this.race == race;
    }

    @Override
    public boolean raceEligible(IPlayer player) {
        return raceEligible((EntityPlayer) player.getMCEntity());
    }

    public boolean raceEligible(EntityPlayer player) {
        DBCData data = DBCData.get(player);
        if (data == null)
            return false;

        int race = data.Race;

        Race customRace = data.addonRace.getRace();
        if (customRace != null)
            race = customRace.id;

        return raceEligible(race);
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
        Form currentSelected = formData.getSelectedForm();
        if (currentSelected != null && currentSelected.id == this.id)
            formData.clearSelectedForm();

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
            childKey = form.getKeyString();
            childID = formID;
            form.parentKey = this.getKeyString();
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
            parentKey = form.getKeyString();
            parentID = formID;
            form.childKey = this.getKeyString();
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
        return FormController.Instance.getFromKey(childKey);
    }

    @Override
    public int getChildID() {
        Form child = (Form) getChild();
        return child != null ? child.id : -1;
    }

    @Override
    public boolean hasChild() {
        return childKey != null && FormController.getInstance().getFromKey(childKey) != null;
    }

    public void removeChildForm() {
        if (childKey != null) {
            Form child = (Form) getChild();
            if (child != null)
                child.parentKey = null;
        }
        childKey = null;
        childID = -1;
    }

    public IForm getParent() {
        return FormController.Instance.getFromKey(parentKey);
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
        Form parent = (Form) getParent();
        return parent != null ? parent.id : -1;
    }

    @Override
    public boolean hasParent() {
        return parentKey != null && FormController.getInstance().getFromKey(parentKey) != null;
    }

    public void removeParentForm() {
        if (parentKey != null) {
            Form parent = (Form) getParent();
            if (parent != null)
                parent.childKey = null;
        }
        parentKey = null;
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

    // TODO api class for FormOverlay

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
