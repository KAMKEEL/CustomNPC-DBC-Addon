package kamkeel.npcdbc.data.ability.conditions;

import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import kamkeel.npcs.controllers.data.ability.conditions.AbilityCondition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.gui.builder.FieldDef;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.List;

public class ConditionForm extends AbilityCondition {
    private int formID = -1;
    private int race = DBCRace.HUMAN;
    private boolean transformed = true;
    private boolean formUnlocked = true;
    private boolean isDBC = false;

    @Override
    public boolean check(EntityLivingBase caster, EntityLivingBase target) {
        switch (getFilter()) {
            case CASTER:
                return checkEntity(caster);
            case TARGET:
                return target != null && checkEntity(target);
            case BOTH:
                return checkEntity(caster) && (target != null && checkEntity(target));
            default:
                return checkEntity(caster);
        }
    }

    public boolean checkEntity(EntityLivingBase entity) {
        if (isDBC && entity instanceof EntityNPCInterface) return false;
        if (!isFormValid(getFormID())) return false;


        if (entity instanceof EntityNPCInterface) {
            EntityNPCInterface npc = (EntityNPCInterface) entity;
            DBCDisplay display = ((INPCDisplay) npc.display).getDBCDisplay();
            Form conditionForm = (Form) FormController.getInstance().get(getFormID());

            if (display.getForm() == null) return false;

            Form npcForm = display.getForm();

            return npcForm.getID() == conditionForm.getID() && display.getRace() == conditionForm.getRace();
        }

        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            DBCData data = DBCData.get(player);

            if (isDBC) {
                return data.Race == getRace() && data.State == getFormID();
            } else {
                Form playerForm = data.getForm();
                return data.Race == playerForm.getRace() && playerForm.getID() == getFormID();
            }
        }

        return false;
    }

    @Override
    public void getConditionDefinitions(List<FieldDef> defs) {
        // put here ID edit

        defs.add(FieldDef.boolField("condition.transformed", this::isTransformed, this::setTransformed));

        defs.add(FieldDef.boolField("condition.form_unlocked", this::isFormUnlocked, this::setFormUnlocked));

        defs.add(FieldDef.boolField("condition.is_dbc", this::isDBC, this::setDBC));

        defs.add(FieldDef.intField("condition.race_id", this::getRace, this::setRace)
            .range(0, 5).visibleWhen(this::isDBC));
    }

    private boolean isFormValid(int formID) {
        if (formID <= 0) return false;

        if (isDBC) {
            return DBCForm.getFormsMap(getRace()).get(formID) != null;
        } else {
            return FormController.getInstance().has(formID);
        }
    }

    @Override
    public void writeTypeNBT(NBTTagCompound nbt) {
        nbt.setInteger("formID", formID);
        nbt.setInteger("race", race);
        nbt.setBoolean("transformed", transformed);
        nbt.setBoolean("formUnlocked", formUnlocked);
        nbt.setBoolean("isDBC", isDBC);
    }

    @Override
    public void readTypeNBT(NBTTagCompound nbt) {
        formID = nbt.getInteger("formID");
        race = nbt.getInteger("race");
        transformed = nbt.getBoolean("transformed");
        formUnlocked = nbt.getBoolean("formUnlocked");
        isDBC = nbt.getBoolean("isDBC");
    }

    public int getFormID() {
        return formID;
    }

    public void setFormID(int formID) {
        if (!isFormValid(formID)) return;
        this.formID = formID;
    }

    public int getRace() {
        return race;
    }

    public void setRace(int race) {
        if (race < 0 || race > 5) return;
        this.race = race;
    }

    public boolean isTransformed() {
        return transformed;
    }

    public void setTransformed(boolean transformed) {
        this.transformed = transformed;
    }

    public boolean isFormUnlocked() {
        return formUnlocked;
    }

    public void setFormUnlocked(boolean formUnlocked) {
        this.formUnlocked = formUnlocked;
    }

    public boolean isDBC() {
        return isDBC;
    }

    public void setDBC(boolean DBC) {
        isDBC = DBC;
    }
}
