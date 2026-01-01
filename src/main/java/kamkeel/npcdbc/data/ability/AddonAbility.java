package kamkeel.npcdbc.data.ability;

import net.minecraft.entity.player.EntityPlayer;

public class AddonAbility extends Ability {
    public String langName;
    public String langDescription;
    public int skillId = -1;

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

    public int getSkillId() {
        return skillId;
    }

    public Ability save() {
        return this;
    }
}
