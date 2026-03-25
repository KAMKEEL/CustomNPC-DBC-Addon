package kamkeel.npcdbc.data.race.builder;

import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.race.progression.FormTree;

public class FormTreeBuilder {
    private final RaceBuilder raceBuilder;
    private final FormTree formTree;

    public FormTreeBuilder(String raceNamespace) {
        this(null, raceNamespace);
    }

    FormTreeBuilder(RaceBuilder raceBuilder, String raceNamespace) {
        this.raceBuilder = raceBuilder;
        this.formTree = new FormTree(raceNamespace);
    }

    public static FormTreeBuilder create(String raceNamespace) {
        return new FormTreeBuilder(raceNamespace);
    }


    public LevelBuilder level(int level) {
        return new LevelBuilder(this, level);
    }

    public int scopedId(int localId) {
        return formTree.getScopedId(localId);
    }

    public RaceBuilder and() {
        if (raceBuilder == null)
            throw new IllegalStateException("FormTreeBuilder was not created from a RaceBuilder; use build() instead.");
        raceBuilder.setFormTree(formTree);
        return raceBuilder;
    }

    public FormTree build() {
        return formTree;
    }

    public FormTree getFormTree() {
        return formTree;
    }

    public static class LevelBuilder {
        private final FormTreeBuilder parent;
        private final int level;

        LevelBuilder(FormTreeBuilder parent, int level) {
            this.parent = parent;
            this.level = level;
        }

        public LevelBuilder add(Form form) {
            parent.formTree.add(level, form);
            return this;
        }

        public LevelBuilder level(int nextLevel) {
            return new LevelBuilder(parent, nextLevel);
        }

        public FormTreeBuilder and() {
            return parent;
        }

        public FormTree build() {
            return parent.build();
        }
    }
}
