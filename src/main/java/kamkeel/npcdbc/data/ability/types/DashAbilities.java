package kamkeel.npcdbc.data.ability.types;

import kamkeel.npcdbc.constants.DBCAbilities;
import kamkeel.npcdbc.data.ability.Ability;
import kamkeel.npcdbc.data.ability.AddonAbility;
import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;

public abstract class DashAbilities extends AddonAbility {
    protected DashAbilities() {
        super();
    }

    @Override
    public boolean onActivate(EntityPlayer player) {
        if (!super.onActivate(player))
            return false;

        if (this instanceof ZVanish) {

        } else if (this instanceof Afterimage) {

        }

        return true;
    }

    @Override
    public boolean onToggle(EntityPlayer player) {
        if (!super.onToggle(player))
            return false;

        if (this instanceof Swoop) {
            DBCSettingsUtil.setSwoop(player, !DBCSettingsUtil.isSwoop(player));
        }

        return true;
    }

    public static class Swoop extends DashAbilities {
        public Swoop() {
            super();
            name = "Swoop";
            langName = "ability.swoop";
            langDescription = "ability.swoopDesc";
            id = DBCAbilities.SWOOP;
            iconX = 48;
            iconY = 0;
            type = Ability.Type.Toggle;
        }
    }

    public static class ZVanish extends DashAbilities {
        public ZVanish() {
            super();
            name = "ZVanish";
            langName = "ability.zvanish";
            langDescription = "ability.zvanishDesc";
            id = DBCAbilities.Z_VANISH;
            iconX = 96;
            iconY = 0;
            cooldown = 15;
            kiCost = 500;
            type = Ability.Type.Active;
        }
    }

    public static class Afterimage extends DashAbilities {
        public Afterimage() {
            super();
            name = "Afterimage";
            langName = "ability.afterimage";
            langDescription = "ability.afterimageDesc";
            id = DBCAbilities.AFTERIMAGE;
            iconX = 144;
            iconY = 0;
            cooldown = 30;
            kiCost = 1000;
            type = Ability.Type.Active;
        }
    }
}
