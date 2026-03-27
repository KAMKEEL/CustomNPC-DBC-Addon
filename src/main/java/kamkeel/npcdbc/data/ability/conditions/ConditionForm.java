package kamkeel.npcdbc.data.ability.conditions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.enums.EnumDBCRaces;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.ability.DBCAbilityFieldProvider;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import kamkeel.npcs.controllers.data.ability.enums.UserType;
import kamkeel.npcs.controllers.data.ability.conditions.AbilityCondition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.gui.builder.FieldDef;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.List;

public class ConditionForm extends AbilityCondition {
    private String formKey = null;
    private int dbcFormID = -1;
    private EnumDBCRaces race = EnumDBCRaces.HUMAN;
    private boolean formActive = true;
    private boolean formUnlocked = true;
    private boolean isDBC = false;

    public ConditionForm() {
        this.typeId = "condition.npcdbc.form";
        this.name = "condition.npcdbc.form";
        this.userType = UserType.BOTH;
    }

    @Override
    public boolean checkEntity(EntityLivingBase entity) {
        if (!isFormValid()) return false;

        if (entity instanceof EntityNPCInterface) {
            if (isDBC) return false;
            EntityNPCInterface npc = (EntityNPCInterface) entity;

            DBCDisplay display = ((INPCDisplay) npc.display).getDBCDisplay();
            if (!display.isEnabled()) return false;

            Form npcForm = display.getForm();
            if (npcForm == null) return false;

            return formKey.equals(npcForm.getKeyString());
        }

        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;

            DBCData data = DBCData.get(player);
            boolean hasFormUnlocked;
            boolean isTransformed;

            if (isDBC) {
                hasFormUnlocked = !needsFormUnlocked() || data.isDBCFormUnlocked(dbcFormID);
                isTransformed = !isFormActive() || (data.State == dbcFormID && data.Race == getRace().ordinal());
            } else {
                PlayerDBCInfo info = data.getDBCInfo();
                hasFormUnlocked = !needsFormUnlocked() || info.hasFormUnlocked(formKey);
                isTransformed = !isFormActive() || info.isInForm(formKey);
            }

            return isTransformed && hasFormUnlocked;
        }

        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getConditionDefinitions(List<FieldDef> defs) {
        defs.add(DBCAbilityFieldProvider.formSubGui("condition.form_id", this::getFormKey, this::setFormKey));

        defs.add(FieldDef.boolField("condition.transformed", this::isFormActive, this::setFormActive));

        defs.add(FieldDef.boolField("condition.form_unlocked", this::needsFormUnlocked, this::setNeedsFormUnlocked));

//        defs.add(FieldDef.boolField("condition.is_dbc", this::isDBC, this::setDBC));

        // TODO overhaul select form menu to show all dbc forms
//        defs.add(FieldDef.enumField("condition.race_id", EnumDBCRaces.class, this::getRace, this::setRace)
//            .range(0, 5).visibleWhen(this::isDBC));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getConditionSummary() {
        String filterLabel = StatCollector.translateToLocal(getFilter().toString());
        String formName = "None";
        if (isDBC && dbcFormID > 0) {
            formName = "DBC Form " + dbcFormID;
        } else if (!isDBC && formKey != null) {
            IForm form = FormController.getInstance().getFromKey(formKey);
            formName = form != null ? form.getName() : "Key:" + formKey;
        }
        return "[" + filterLabel + "] Form: " + formName;
    }

    private boolean isFormValid() {
        if (isDBC) {
            return dbcFormID > 0 && DBCForm.getFormsMap(getRace().ordinal()).get(dbcFormID) != null;
        } else {
            return formKey != null && FormController.getInstance().getFromKey(formKey) != null;
        }
    }

    @Override
    public void writeTypeNBT(NBTTagCompound nbt) {
        nbt.setString("formKey", formKey != null ? formKey : "");
        nbt.setInteger("dbcFormID", dbcFormID);
        nbt.setInteger("race", race.ordinal());
        nbt.setBoolean("transformed", formActive);
        nbt.setBoolean("formUnlocked", formUnlocked);
        nbt.setBoolean("isDBC", isDBC);
    }

    @Override
    public void readTypeNBT(NBTTagCompound nbt) {
        isDBC = nbt.getBoolean("isDBC");
        if (nbt.hasKey("formKey")) {
            String k = nbt.getString("formKey");
            formKey = k.isEmpty() ? null : k;
        } else if (!isDBC && nbt.hasKey("formID")) {
            int legacyId = nbt.getInteger("formID");
            if (legacyId > 0) {
                Form f = (Form) FormController.getInstance().get(legacyId);
                formKey = f != null ? f.getKeyString() : null;
            }
        }
        dbcFormID = nbt.hasKey("dbcFormID") ? nbt.getInteger("dbcFormID")
                  : (isDBC && nbt.hasKey("formID") ? nbt.getInteger("formID") : -1);
        race = EnumDBCRaces.fromOrdinal(nbt.getInteger("race"));
        formActive = nbt.getBoolean("transformed");
        formUnlocked = nbt.getBoolean("formUnlocked");
    }

    @Override
    public boolean isConfigured() {
        return isDBC ? dbcFormID >= 0 : formKey != null;
    }

    public String getFormKey() {
        return formKey;
    }

    public void setFormKey(String key) {
        if (key == null || key.isEmpty()) {
            this.formKey = null;
            return;
        }
        if (FormController.getInstance().getFromKey(key) == null) return;
        this.formKey = key;
    }

    public int getDbcFormID() {
        return dbcFormID;
    }

    public void setDbcFormID(int id) {
        this.dbcFormID = id < 0 ? -1 : id;
    }

    public EnumDBCRaces getRace() {
        return race;
    }

    public void setRace(EnumDBCRaces race) {
        this.race = race;
    }

    public boolean isFormActive() {
        return formActive;
    }

    public void setFormActive(boolean formActive) {
        this.formActive = formActive;
    }

    public boolean needsFormUnlocked() {
        return formUnlocked;
    }

    public void setNeedsFormUnlocked(boolean formUnlocked) {
        this.formUnlocked = formUnlocked;
    }

    public boolean isDBC() {
        return isDBC;
    }

    public void setDBC(boolean DBC) {
        isDBC = DBC;
    }
}
