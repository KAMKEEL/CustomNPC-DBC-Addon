package kamkeel.npcdbc.data.ability;

import kamkeel.npcdbc.CustomNpcPlusDBC;

public class AddonAbility extends Ability {
    public String langName;
    public String langDescription;
    public int skillId = -1;

    public AddonAbility() {
        super();
        icon = CustomNpcPlusDBC.ID + ":textures/gui/ability_icons.png";
    }

    @Override
    public int getWidth() {
        return 48;
    }

    @Override
    public int getHeight() {
        return 48;
    }

    @Override
    public float getScale() {
        return 1.5f;
    }

    public String getLangName() {
        return this.langName;
    }

    public String getLangDescription() {
        return langDescription;
    }

    public int getSkillId() {
        return skillId;
    }

    public Ability save() {
        return this;
    }

//    public boolean onAnimationEvent(AbilityData data, AnimationEvent event) {
//        return false;
//    }
}
