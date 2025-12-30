package kamkeel.npcdbc.data.ability;

public class AddonAbility extends Ability {
    public String langName;
    public String langDescription;

    public AddonAbility() {
        super();
    }

    @Override
    public int getWidth() {
        return 16;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public int getScale() {
        return 1;
    }

    public String getLangName() {
        return this.langName;
    }

    public String getLangDescription() {
        return langDescription;
    }

    public Ability save() {
        return this;
    }
}
