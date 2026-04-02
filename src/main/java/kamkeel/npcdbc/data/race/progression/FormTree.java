package kamkeel.npcdbc.data.race.progression;

import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.race.serial.DataCompound;
import kamkeel.npcdbc.data.race.serial.DataSerializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FormTree implements DataSerializable {
    private final List<Branch> branches = new ArrayList<>();
    public String raceNamespace;


    public FormTree() {}

    public static class Branch implements DataSerializable {
        private final String name;
        private final List<Form> forms = new ArrayList<>();
        private Form unlockAnchor;

        /**
         * The minimum racial skill level required to unlock this branch.
         * Resolved at build time from skill-level → form bindings.
         * <p>
         * A value of {@code 0} means the branch is always available (default for
         * backward compatibility with branches that have no explicit skill bindings).
         * A value of {@code -1} means it was never resolved (should not happen after
         * a well-formed build).
         */
        private int unlockLevel = 0;

        public Branch(String name) {
            this.name = name != null ? name : "";
        }

        public int getUnlockLevel() {
            return unlockLevel;
        }

        public void setUnlockLevel(int unlockLevel) {
            this.unlockLevel = unlockLevel;
        }

        public String getName() {
            return name;
        }

        public void addForm(Form form) {
            forms.add(form);
            if (unlockAnchor == null)
                unlockAnchor = form;
        }

        public Form getUnlockAnchor() {
            return unlockAnchor;
        }

        public void setUnlockAnchor(Form unlockAnchor) {
            this.unlockAnchor = unlockAnchor;
        }

        public List<Form> getForms() {
            return Collections.unmodifiableList(forms);
        }

        public Form getRoot() {
            return forms.isEmpty() ? null : forms.get(0);
        }

        public Form getTail() {
            return forms.isEmpty() ? null : forms.get(forms.size() - 1);
        }

        public int length() {
            return forms.size();
        }

        public boolean contains(Form form) {
            return forms.contains(form);
        }

        public boolean containsId(int formId) {
            for (Form f : forms) {
                if (f.id == formId) return true;
            }
            return false;
        }

        public int indexOf(Form form) {
            return forms.indexOf(form);
        }

        public Form get(int index) {
            if (index < 0 || index >= forms.size()) return null;
            return forms.get(index);
        }

        public Form next(Form form) {
            int idx = forms.indexOf(form);
            if (idx < 0 || idx + 1 >= forms.size()) return null;
            return forms.get(idx + 1);
        }

        public Form previous(Form form) {
            int idx = forms.indexOf(form);
            if (idx <= 0) return null;
            return forms.get(idx - 1);
        }

        public Form getFirstUnlockedForm(List<Form> unlockedForms) {
            for (Form f : forms) {
                if (unlockedForms.contains(f))
                    return f;
            }
            return null;
        }

        @Override
        public DataCompound serialize(DataCompound data) {
            data.putString("name", name);
            data.putInt("unlockLevel", unlockLevel);

            String anchorKey = (unlockAnchor != null && unlockAnchor.key != null)
                ? unlockAnchor.key.toString() : "";
            data.putString("unlockAnchorKey", anchorKey);

            List<String> formKeys = new ArrayList<>();
            for (Form f : forms) {
                if (f.key != null)
                    formKeys.add(f.key.toString());
            }
            data.putStringList("formKeys", formKeys);

            return data;
        }

        @Override
        public void deserialize(DataCompound data) {
            unlockLevel = data.getInt("unlockLevel", unlockLevel);

            forms.clear();
            unlockAnchor = null;
            for (String key : data.getStringList("formKeys")) {
                Form form = FormController.Instance.getBuiltIn(key);
                if (form != null)
                    addForm(form);
            }

            String anchorKey = data.getString("unlockAnchorKey", "");
            if (!anchorKey.isEmpty()) {
                Form anchor = FormController.Instance.getBuiltIn(anchorKey);
                if (anchor != null)
                    unlockAnchor = anchor;
            }
        }
    }

    public void addBranch(Branch branch) {
        branches.add(branch);
    }

    public List<Branch> getBranches() {
        return Collections.unmodifiableList(branches);
    }

    public Branch getBranch(String name) {
        for (Branch b : branches) {
            if (b.name.equals(name)) return b;
        }
        return null;
    }

    public Branch getBranch(int index) {
        if (index < 0 || index >= branches.size()) return null;
        return branches.get(index);
    }

    public int getScopedId(int localId) {
        return (raceNamespace.hashCode() & 0x7FFFFFFF) * 1000 + localId;
    }

    public void scopeAllIds() {
        for (Branch branch : branches) {
            scopeBranch(branch);
        }
    }

    private void scopeBranch(Branch branch) {
        List<Form> forms = branch.forms;
        for (int i = 0; i < forms.size(); i++) {
            Form form = forms.get(i);
            form.id = getScopedId(form.id);
            form.name = form.key.name;
        }
        for (int i = 0; i < forms.size(); i++) {
            Form form = forms.get(i);
            form.parentID = (i > 0) ? forms.get(i - 1).id : -1;
            form.childID = (i + 1 < forms.size()) ? forms.get(i + 1).id : -1;
            form.parentKey = (i > 0 && forms.get(i - 1).key != null) ? forms.get(i - 1).key.toString() : null;
            form.childKey = (i + 1 < forms.size() && forms.get(i + 1).key != null) ? forms.get(i + 1).key.toString() : null;
        }
    }

    public List<Form> getAllForms() {
        List<Form> all = new ArrayList<>();
        for (Branch branch : branches) {
            all.addAll(branch.forms);
        }
        return Collections.unmodifiableList(all);
    }

    public Branch findBranchContaining(Form form) {
        for (Branch branch : branches) {
            if (branch.contains(form)) return branch;
        }
        return null;
    }

    public Branch findBranchContainingId(int formId) {
        for (Branch branch : branches) {
            if (branch.containsId(formId)) return branch;
        }
        return null;
    }

    public int branchCount() {
        return branches.size();
    }

    public int size() {
        int total = 0;
        for (Branch branch : branches) {
            total += branch.length();
        }
        return total;
    }

    @Override
    public DataCompound serialize(DataCompound data) {
        if (raceNamespace != null)
            data.putString("namespace", raceNamespace);

        for (int i = 0; i < branches.size(); i++) 
            data.put("branch_" + i, branches.get(i));
        

        return data;
    }

    @Override
    public void deserialize(DataCompound data) {
        raceNamespace = data.getString("namespace", raceNamespace);

        branches.clear();
        int i = 0;
        while (data.has("branch_" + i)) {
            DataCompound branchData = data.get("branch_" + i);
            String branchName = branchData.getString("name", "");
            Branch branch = new Branch(branchName);
            branch.deserialize(branchData);
            branches.add(branch);
            i++;
        }
    }
}
