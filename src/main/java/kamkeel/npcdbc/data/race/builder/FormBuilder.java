package kamkeel.npcdbc.data.race.builder;

import kamkeel.npcdbc.AddonRegistries;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormDisplay;
import kamkeel.npcdbc.data.form.FormKey;

public class FormBuilder {
    private final int id;
    private FormKey key;
    private String menuName = "§aNEW";

    private float strengthMulti = 1.0f;
    private float dexMulti = 1.0f;
    private float willMulti = 1.0f;

    private String childKey = null;
    private String parentKey = null;

    private final FormDisplay display = new FormDisplay(null);

    private FormBuilder(int id) {
        this.id = id;
    }

    public static FormBuilder create(int id) {
        return new FormBuilder(id);
    }

    public static FormBuilder create(FormKey key) {
        FormBuilder builder = new FormBuilder(-1);
        builder.key = key;
        return builder;
    }

    public static FormBuilder create(String formKey) {
        return create(new FormKey(formKey));
    }

    public FormBuilder menuName(String name) {
        this.menuName = name;
        return this;
    }

    public FormBuilder strengthMulti(float value) {
        this.strengthMulti = value;
        return this;
    }

    public FormBuilder dexMulti(float value) {
        this.dexMulti = value;
        return this;
    }

    public FormBuilder willMulti(float value) {
        this.willMulti = value;
        return this;
    }

    public FormBuilder allMulti(float value) {
        this.strengthMulti = value;
        this.dexMulti = value;
        this.willMulti = value;
        return this;
    }

    public FormBuilder child(String childKey) {
        this.childKey = childKey;
        return this;
    }

    public FormBuilder child(Form child) {
        this.childKey = child.getKeyString();
        return this;
    }

    public FormBuilder parent(String parentKey) {
        this.parentKey = parentKey;
        return this;
    }

    public FormBuilder parent(Form parent) {
        this.parentKey = parent.getKeyString();
        return this;
    }

    public DisplayBuilder display() {
        return new DisplayBuilder(this);
    }

    public Form build() {
        return build(null);
    }

    public Form build(AddonRegistries.Forms registry) {
        Form form = key != null ? new Form(key) : new Form();
        form.id = this.id;
        form.menuName = menuName;
        form.strengthMulti = strengthMulti;
        form.dexMulti = dexMulti;
        form.willMulti = willMulti;
        form.childKey = childKey;
        form.parentKey = parentKey;
        form.childID = -1;
        form.parentID = -1;
        form.display = display;

        if(registry != null)
            registry.register(form);
        return form;
    }

    public class DisplayBuilder {
        private final FormBuilder parent;

        DisplayBuilder(FormBuilder parent) {
            this.parent = parent;
        }

        public DisplayBuilder size(float size) {
            display.formSize = size;
            return this;
        }

        public DisplayBuilder width(float width) {
            display.formWidth = width;
            return this;
        }

        public DisplayBuilder keepOriginalSize(boolean keep) {
            display.keepOriginalSize = keep;
            return this;
        }

        public DisplayBuilder hairCode(String code) {
            display.hairCode = code;
            return this;
        }

        public DisplayBuilder hairType(String type) {
            display.hairType = type;
            return this;
        }

        public DisplayBuilder bodyType(String type) {
            display.bodyType = type;
            return this;
        }

        public DisplayBuilder color(String slotId, int color) {
            display.bodyColors.setColor(slotId, color);
            return this;
        }

        public DisplayBuilder auraColor(int color) {
            display.auraColor = color;
            return this;
        }

        public DisplayBuilder kiBarColor(int color) {
            display.kiBarColor = color;
            return this;
        }

        public DisplayBuilder berserk(boolean value) {
            display.isBerserk = value;
            return this;
        }

        public DisplayBuilder pupils(boolean value) {
            display.hasPupils = value;
            return this;
        }

        public DisplayBuilder eyebrows(boolean value) {
            display.hasEyebrows = value;
            return this;
        }

        public DisplayBuilder bodyFur(boolean value) {
            display.hasBodyFur = value;
            return this;
        }

        public DisplayBuilder furType(int type) {
            display.setFurType(type);
            return this;
        }

        public DisplayBuilder arcoMask(boolean value) {
            display.hasArcoMask = value;
            return this;
        }

        public DisplayBuilder majinHair(boolean value) {
            display.effectMajinHair = value;
            return this;
        }

        public FormBuilder and() {
            return parent;
        }
    }
}
