package kamkeel.npcdbc.data.ability.types;

import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.constants.DBCAbilities;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.controllers.DBCEffectController;
import kamkeel.npcdbc.data.ability.AddonAbility;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import net.minecraft.entity.player.EntityPlayer;

public class NamekRegen extends AddonAbility {
    public NamekRegen() {
        super();
        name = "NamekRegen";
        langName = "effect.namekianregeneration";
        langDescription = "ability.namekregenDesc";
        id = DBCAbilities.NamekRegen;
        iconX = 0;
        iconY = 0;
        type = Type.Active;
    }

    @Override
    public void onActivate(EntityPlayer player) {
        if (!ConfigDBCGameplay.EnableNamekianRegen || DBCData.get(player).Race != DBCRace.NAMEKIAN)
            return;

        if (!DBCEffectController.getInstance().hasEffect(player, Effects.NAMEK_REGEN)) {
            DBCEffectController.getInstance().applyEffect(player, Effects.NAMEK_REGEN, -100);
        }
    }
}
