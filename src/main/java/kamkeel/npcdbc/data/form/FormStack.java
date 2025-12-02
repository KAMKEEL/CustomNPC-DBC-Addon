package kamkeel.npcdbc.data.form;

import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.controllers.FormController;
import net.minecraft.nbt.NBTTagCompound;

public class FormStack {
    private final Form parent;
    public Form fromForm;
    public Form toForm;

    public FormStack(Form parent) {
        this.parent = parent;
        this.fromForm = new Form();
        this.toForm = new Form();
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();

        compound.setInteger("fromFormID", this.fromForm.id);
        compound.setInteger("toFormID", this.toForm.id);

        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        if (FormController.Instance.has(compound.getInteger("fromFormID")))
            this.fromForm = (Form) FormController.Instance.get(compound.getInteger("fromFormID"));

        if (FormController.Instance.has(compound.getInteger("toFormID")))
            this.toForm = (Form) FormController.Instance.get(compound.getInteger("toFormID"));
    }

    public boolean setForm(String fromID, String toID) {
        if (FormController.Instance.has(fromID) && FormController.Instance.has(toID)) {
            IForm from = FormController.Instance.get(fromID);
            IForm to = FormController.Instance.get(toID);
            return setForm(from, to);
        }

        return false;
    }

    public boolean setForm(int fromID, int toID) {
        if (FormController.Instance.has(fromID) && FormController.Instance.has(toID)) {
            IForm from = FormController.Instance.get(fromID);
            IForm to = FormController.Instance.get(toID);
            return setForm(from, to);
        }

        return false;
    }

    public boolean setForm(IForm from, IForm to) {
        if (from == null || to == null)
            return false;

        int fromID = from.getID();
        int toID = to.getID();

        if (fromID == toID)
            return false;

        if (fromID == parent.id || toID == parent.id)
            return false;

        if (isInParentLine(fromID) || isInParentLine(toID))
            return false;

        if ((this.fromForm != null && this.fromForm.getID() == fromID)
            || (this.toForm != null && this.toForm.getID() == toID))
            return false;

        if (fromID <= -1 || toID <= -1)
            return false;

        this.fromForm = (Form) from;
        this.toForm = (Form) to;

        return true;
    }

    public boolean setFromForm(IForm form) {
        if (form == null)
            return false;

        int formID = form.getID();

        if (formID == this.fromForm.getID())
            return false;

        if (formID == parent.id)
            return false;

        if (isInParentLine(formID))
            return false;

        if (this.fromForm != null && this.fromForm.getID() == formID)
            return false;

        if (formID <= -1)
            return false;

        this.fromForm = (Form) form;

        return true;
    }

    public boolean setFromForm(String fromID) {
        if (FormController.Instance.has(fromID)) {
            IForm from = FormController.Instance.get(fromID);
            return setFromForm(from);
        }

        return false;
    }

    public boolean setFromForm(int fromID) {
        if (FormController.Instance.has(fromID)) {
            IForm from = FormController.Instance.get(fromID);
            return setFromForm(from);
        }

        return false;
    }

    public IForm getFromForm() {
        return fromForm;
    }

    public int getFromID() {
        return fromForm.getID();
    }

    public void clearFromForm() {
        this.fromForm = new Form();
    }

    public boolean setToForm(IForm form) {
        if (form == null)
            return false;

        int formID = form.getID();

        if (formID == this.toForm.getID())
            return false;

        if (formID == parent.id)
            return false;

        if (isInParentLine(formID))
            return false;

        if (this.toForm != null && this.toForm.getID() == formID)
            return false;

        if (formID <= -1)
            return false;

        this.toForm = (Form) form;

        return true;
    }

    public boolean setToForm(String toID) {
        if (FormController.Instance.has(toID)) {
            IForm to = FormController.Instance.get(toID);
            return setToForm(to);
        }

        return false;
    }

    public boolean setToForm(int toID) {
        if (FormController.Instance.has(toID)) {
            IForm to = FormController.Instance.get(toID);
            return setToForm(to);
        }

        return false;
    }

    public IForm getToForm() {
        return toForm;
    }

    public int getToID() {
        return toForm.getID();
    }

    public void clearToForm() {
        this.toForm = new Form();
    }

    public IForm[] getForms() {
        if (isEmpty())
            return new IForm[0];

        return new IForm[] { fromForm, toForm };
    }

    public boolean isEmpty() {
        return fromForm.getID() == -1 || toForm.getID() == -1;
    }

    public boolean isDirectRelation(int id) {
        return id == parent.parentID || id == parent.childID;
    }

    public boolean isAncestor(int id) {
        Form f = parent;
        while (f != null && f.parentID != -1 && FormController.Instance.has(f.parentID)) {
            if (f.parentID == id)
                return true;

            f = (Form) FormController.Instance.get(f.parentID);
        }
        return false;
    }

    public boolean isDescendant(int id) {
        int current = parent.childID;
        while (current != -1 && FormController.Instance.has(current)) {
            if (current == id)
                return true;
            Form f = (Form) FormController.Instance.get(current);
            current = f.childID;
        }

        return false;
    }

    public boolean isInParentLine(int id) {
        return isDirectRelation(id) || isAncestor(id) || isDescendant(id);
    }
}
