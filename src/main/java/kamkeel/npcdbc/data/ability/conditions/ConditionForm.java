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
    private int formID = -1;
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
        if (isDBC && entity instanceof EntityNPCInterface) return false;
        if (!isFormValid(getFormID())) return false;

        if (entity instanceof EntityNPCInterface) {
            EntityNPCInterface npc = (EntityNPCInterface) entity;

            DBCDisplay display = ((INPCDisplay) npc.display).getDBCDisplay();
            if (!display.isEnabled()) return false;

            Form npcForm = display.getForm();
            if (npcForm == null) return false;

            return npcForm.getID() == getFormID();
        }

        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;

            DBCData data = DBCData.get(player);
            boolean hasFormUnlocked;
            boolean isTransformed;

            if (isDBC) {
                hasFormUnlocked = !needsFormUnlocked() || data.isDBCFormUnlocked(getFormID());
                isTransformed = !isFormActive() || (data.State == getFormID() && data.Race == getRace().ordinal());
            } else {
                PlayerDBCInfo info = data.getDBCInfo();
                hasFormUnlocked = !needsFormUnlocked() || info.unlockedForms.contains(getFormID());
                isTransformed = !isFormActive() || info.isInForm(getFormID());
            }

            return isTransformed && hasFormUnlocked;
        }

        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getConditionDefinitions(List<FieldDef> defs) {
        defs.add(DBCAbilityFieldProvider.formSubGui("condition.form_id", this::getFormID, this::setFormID));

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
        if (formID > 0) {
            if (!isDBC) {
                IForm form = FormController.getInstance().get(formID);
                formName = form != null ? form.getName() : "ID:" + formID;
            } else {
                formName = "DBC Form " + formID;
            }
        }
        return "[" + filterLabel + "] Form: " + formName;
    }

    private boolean isFormValid(int formID) {
        if (formID <= 0) return false;

        if (isDBC) {
            return DBCForm.getFormsMap(getRace().ordinal()).get(formID) != null;
        } else {
            return FormController.getInstance().has(formID);
        }
    }

    @Override
    public void writeTypeNBT(NBTTagCompound nbt) {
        nbt.setInteger("formID", formID);
        nbt.setInteger("race", race.ordinal());
        nbt.setBoolean("transformed", formActive);
        nbt.setBoolean("formUnlocked", formUnlocked);
        nbt.setBoolean("isDBC", isDBC);
    }

    @Override
    public void readTypeNBT(NBTTagCompound nbt) {
        formID = nbt.getInteger("formID");
        race = EnumDBCRaces.fromOrdinal(nbt.getInteger("race"));
        formActive = nbt.getBoolean("transformed");
        formUnlocked = nbt.getBoolean("formUnlocked");
        isDBC = nbt.getBoolean("isDBC");
    }

    @Override
    public boolean isConfigured() {
        return formID >= 0;
    }

    public int getFormID() {
        return formID;
    }

    public void setFormID(int formID) {
        if (formID < 0) {
            this.formID = -1;
            return;
        }
        if (!isFormValid(formID)) return;
        this.formID = formID;
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
