package kamkeel.npcdbc.data.ability.types;

import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.constants.DBCAbilities;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.controllers.DBCEffectController;
import kamkeel.npcdbc.data.ability.AddonAbility;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.dbcdata.DBCDataStats;
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
        cooldown = 300;
    }

    @Override
    public boolean callEvent(EntityPlayer player) {
        if (!super.callEvent(player))
            return false;

        if (!ConfigDBCGameplay.EnableNamekianRegen || DBCData.get(player).Race != DBCRace.NAMEKIAN)
            return false;

        DBCDataStats stats = new DBCDataStats(DBCData.get(player));
        stats.applyNamekianRegen();

        return true;
    }
}
