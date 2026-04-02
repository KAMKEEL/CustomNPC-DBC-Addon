package kamkeel.npcdbc.data.race.builder;

import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.race.progression.FormTree;
import kamkeel.npcdbc.data.race.progression.FormTree.Branch;

public class FormTreeBuilder {
    private final RaceBuilder raceBuilder;
    private final FormTree formTree;

    public FormTreeBuilder(String raceNamespace) {
        this(null, raceNamespace);
    }

    FormTreeBuilder(RaceBuilder raceBuilder, String raceNamespace) {
        this.raceBuilder = raceBuilder;
        this.formTree = new FormTree();
        this.formTree.raceNamespace =raceNamespace;
    }

    public static FormTreeBuilder create(String raceNamespace) {
        return new FormTreeBuilder(raceNamespace);
    }

    public BranchBuilder branch(Form rootForm) {
        return branch(rootForm.menuName, rootForm);
    }

    public BranchBuilder branch(String name, Form rootForm) {
        Branch branch = new Branch(name);
        branch.addForm(rootForm);
        branch.setUnlockAnchor(rootForm);
        formTree.addBranch(branch);
        return new BranchBuilder(this, branch);
    }

    public int scopedId(int localId) {
        return formTree.getScopedId(localId);
    }

    public RaceBuilder and() {
        if (raceBuilder == null)
            throw new IllegalStateException("FormTreeBuilder was not created from a RaceBuilder; use build() instead.");
        formTree.scopeAllIds();
        raceBuilder.setFormTree(formTree);
        return raceBuilder;
    }

    public FormTree build() {
        formTree.scopeAllIds();
        return formTree;
    }

    public static class BranchBuilder {
        private final FormTreeBuilder treeBuilder;
        private final Branch branch;

        BranchBuilder(FormTreeBuilder treeBuilder, Branch branch) {
            this.treeBuilder = treeBuilder;
            this.branch = branch;
        }

        public BranchBuilder child(Form form) {
            branch.addForm(form);
            return this;
        }

        public BranchBuilder unlocksAt(Form form) {
            branch.setUnlockAnchor(form);
            return this;
        }

        public BranchBuilder branch(Form rootForm) {
            return treeBuilder.branch(rootForm);
        }

        public BranchBuilder branch(String name, Form rootForm) {
            return treeBuilder.branch(name, rootForm);
        }

        public FormTree build() {
            return treeBuilder.build();
        }

        public RaceBuilder and() {
            return treeBuilder.and();
        }
    }
}
