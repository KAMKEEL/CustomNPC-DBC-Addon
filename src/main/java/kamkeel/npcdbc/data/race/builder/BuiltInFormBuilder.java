package kamkeel.npcdbc.data.race.builder;

import kamkeel.npcdbc.data.form.BuiltInForm;
import kamkeel.npcdbc.data.form.FormDisplay;

public class BuiltInFormBuilder {
    private final int id;
    private String menuName = "§aNEW";

    private float strengthMulti = 1.0f;
    private float dexMulti = 1.0f;
    private float willMulti = 1.0f;

    private int childID = -1;
    private int parentID = -1;

    private final FormDisplay display = new FormDisplay(null);

    private BuiltInFormBuilder(int id) {
        this.id = id;
    }

    public static BuiltInFormBuilder create(int id) {
        return new BuiltInFormBuilder(id);
    }

    public BuiltInFormBuilder menuName(String name) {
        this.menuName = name;
        return this;
    }

    public BuiltInFormBuilder strengthMulti(float value) {
        this.strengthMulti = value;
        return this;
    }

    public BuiltInFormBuilder dexMulti(float value) {
        this.dexMulti = value;
        return this;
    }

    public BuiltInFormBuilder willMulti(float value) {
        this.willMulti = value;
        return this;
    }

    public BuiltInFormBuilder allMulti(float value) {
        this.strengthMulti = value;
        this.dexMulti = value;
        this.willMulti = value;
        return this;
    }

    public BuiltInFormBuilder child(int id) {
        this.childID = id;
        return this;
    }

    public BuiltInFormBuilder parent(int id) {
        this.parentID = id;
        return this;
    }

    public DisplayConfigurator display() {
        return new DisplayConfigurator(this);
    }

    public BuiltInForm build() {
        BuiltInForm form = new BuiltInForm(id);
        form.menuName = menuName;
        form.strengthMulti = strengthMulti;
        form.dexMulti = dexMulti;
        form.willMulti = willMulti;
        form.childID = childID;
        form.parentID = parentID;
        form.display = display;
        return form;
    }

    public class DisplayConfigurator {
        private final BuiltInFormBuilder parent;

        DisplayConfigurator(BuiltInFormBuilder parent) {
            this.parent = parent;
        }

        public DisplayConfigurator size(float size) {
            display.formSize = size;
            return this;
        }

        public DisplayConfigurator width(float width) {
            display.formWidth = width;
            return this;
        }

        public DisplayConfigurator keepOriginalSize(boolean keep) {
            display.keepOriginalSize = keep;
            return this;
        }

        public DisplayConfigurator hairCode(String code) {
            display.hairCode = code;
            return this;
        }

        public DisplayConfigurator hairType(String type) {
            display.hairType = type;
            return this;
        }

        public DisplayConfigurator bodyType(String type) {
            display.bodyType = type;
            return this;
        }

        public DisplayConfigurator color(String slotId, int color) {
            display.bodyColors.setColor(slotId, color);
            return this;
        }

        public DisplayConfigurator auraColor(int color) {
            display.auraColor = color;
            return this;
        }

        public DisplayConfigurator kiBarColor(int color) {
            display.kiBarColor = color;
            return this;
        }

        public DisplayConfigurator berserk(boolean value) {
            display.isBerserk = value;
            return this;
        }

        public DisplayConfigurator pupils(boolean value) {
            display.hasPupils = value;
            return this;
        }

        public DisplayConfigurator eyebrows(boolean value) {
            display.hasEyebrows = value;
            return this;
        }

        public DisplayConfigurator bodyFur(boolean value) {
            display.hasBodyFur = value;
            return this;
        }

        public DisplayConfigurator furType(int type) {
            display.setFurType(type);
            return this;
        }

        public DisplayConfigurator arcoMask(boolean value) {
            display.hasArcoMask = value;
            return this;
        }

        public DisplayConfigurator majinHair(boolean value) {
            display.effectMajinHair = value;
            return this;
        }

        public BuiltInFormBuilder and() {
            return parent;
        }
    }
}
