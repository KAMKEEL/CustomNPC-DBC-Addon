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
        langName = "ability.namekregen";
        langDescription = "ability.namekregenDesc";
        id = DBCAbilities.NamekRegen;
        iconX = 48;
        iconY = 0;
        type = Type.Active;
        cooldown = 300;
    }

    @Override
    public boolean callEvent(EntityPlayer player) {
        if (!super.callEvent(player))
            return false;

        DBCDataStats stats = new DBCDataStats(DBCData.get(player));

        stats.applyNamekianRegen();

        return true;
    }

    @Override
    protected boolean canFireEvent(EntityPlayer player) {
        if (!ConfigDBCGameplay.EnableNamekianRegen || DBCData.get(player).Race != DBCRace.NAMEKIAN)
            return false;

        DBCDataStats stats = new DBCDataStats(DBCData.get(player));

        if (stats.getCurrentBodyPercentage() >= ConfigDBCGameplay.NamekianRegenMin)
            return false;

        if (DBCEffectController.getInstance().hasEffect(player, Effects.NAMEK_REGEN))
            return false;

        return true;
    }
}
