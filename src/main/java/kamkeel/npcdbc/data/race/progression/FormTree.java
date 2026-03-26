package kamkeel.npcdbc.data.race.progression;

import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormRace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FormTree {
    private final String raceNamespace;
    private final List<Branch> branches = new ArrayList<>();

    public FormTree(String raceNamespace) {
        this.raceNamespace = raceNamespace;
    }

    public static class Branch {
        private final String name;
        private final List<Form> forms = new ArrayList<>();

        public Branch(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void addForm(Form form) {
            forms.add(form);
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
            if (form.key != null)
                form.name = form.key.name;
            else
                form.name = "builtin_" + form.id;
            form.builtIn = true;
        }
        for (int i = 0; i < forms.size(); i++) {
            Form form = forms.get(i);
            form.parentID = (i > 0) ? forms.get(i - 1).id : -1;
            form.childID = (i + 1 < forms.size()) ? forms.get(i + 1).id : -1;
        }
    }

    public void register(FormRace race) {
        for (Branch branch : branches) {
            for (Form form : branch.forms) {
             //   form.race = race;
             //   FormController.getInstance().registerBuiltIn(form);
            }
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
}
